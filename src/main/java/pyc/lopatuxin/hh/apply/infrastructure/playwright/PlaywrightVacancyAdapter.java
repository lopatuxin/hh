package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Salary;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.model.WorkFormat;
import pyc.lopatuxin.hh.apply.domain.port.out.VacancyPort;
import pyc.lopatuxin.hh.config.HhProperties;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class PlaywrightVacancyAdapter implements VacancyPort {

    private static final String SEARCH_URL = "https://hh.ru/search/vacancy";
    private static final Pattern VACANCY_ID_PATTERN = Pattern.compile("/vacancy/(\\d+)");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d[\\d\\u00a0\\s]*\\d|\\d");

    private final Browser browser;
    private final HhProperties properties;

    @Override
    public List<String> collectIds(ApplyCriteria criteria, int page) {
        try (BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setStorageStatePath(Paths.get(properties.browser().authStatePath())))) {

            Page browserPage = context.newPage();
            String url = buildSearchUrl(criteria, page);
            log.info("Открываю страницу поиска #{}: {}", page + 1, url);
            browserPage.navigate(url, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                    .setTimeout(60000));
            checkCaptcha(browserPage);

            List<String> ids = new ArrayList<>();
            List<Locator> cards = browserPage.locator("[data-qa='vacancy-serp__vacancy']").all();
            boolean firstCard = true;
            for (Locator card : cards) {
                if (firstCard) {
                    log.debug("HTML карточки: {}", card.innerHTML());
                    firstCard = false;
                }
                Locator titleLink = card.locator("a[data-qa='serp-item__title']");
                if (titleLink.count() == 0) continue;
                String href = titleLink.getAttribute("href");
                String id = extractVacancyId(href);
                if (id != null) {
                    ids.add(id);
                }
            }

            log.info("Страница {}: собрано {} ID вакансий", page + 1, ids.size());
            return ids;
        }
    }

    @Override
    public List<Vacancy> fetchDetails(List<String> ids) {
        try (BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setStorageStatePath(Paths.get(properties.browser().authStatePath())))) {

            Page page = context.newPage();
            List<Vacancy> result = new ArrayList<>();
            for (String id : ids) {
                result.add(fetchVacancyDetails(page, id));
            }
            log.info("Загружено {} вакансий", result.size());
            return result;
        }
    }

    private Vacancy fetchVacancyDetails(Page page, String id) {
        String url = "https://hh.ru/vacancy/" + id;
        log.info("Загружаю детали вакансии {}", url);
        page.navigate(url, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(60000));
        checkCaptcha(page);

        Locator titleLocator = page.locator("[data-qa='vacancy-title']");
        String title = titleLocator.count() > 0 ? titleLocator.textContent().trim() : "";

        String company = getTextOrNull(page, "[data-qa='vacancy-company-name']");

        Salary salary = parseSalary(page);
        String area = getTextOrNull(page, "[data-qa='vacancy-view-location']");
        String experience = getTextOrNull(page, "[data-qa='vacancy-experience']");

        List<String> keySkills = page.locator("[data-qa='bloko-tag__text']")
                .all().stream()
                .map(l -> l.textContent().trim())
                .filter(s -> !s.isBlank())
                .toList();

        boolean requiresCoverLetter =
                page.locator("[data-qa='vacancy-response-letter-required']").count() > 0;

        WorkFormat workFormat = parseWorkFormat(page);

        log.info("Вакансия {}: '{}', компания: '{}', формат: {}, сопроводительное обязательно: {}", id, title, company, workFormat, requiresCoverLetter);
        return new Vacancy(id, title, company, salary, area, experience, keySkills, requiresCoverLetter, workFormat);
    }

    private String buildSearchUrl(ApplyCriteria criteria, int page) {
        int perPage = Math.min(50, Math.max(10, criteria.limit() * 2));
        StringBuilder url = new StringBuilder(SEARCH_URL).append("?per_page=").append(perPage);

        if (criteria.keywords() != null && !criteria.keywords().isEmpty()) {
            String text = criteria.keywords().stream()
                    .map(kw -> URLEncoder.encode(kw, StandardCharsets.UTF_8))
                    .reduce((a, b) -> a + "+" + b)
                    .orElse("");
            url.append("&text=").append(text);
        }
        if (criteria.areaId() > 0) {
            url.append("&area=").append(criteria.areaId());
        }
        if (criteria.salaryFrom() > 0) {
            url.append("&salary=").append(criteria.salaryFrom());
        }
        if (criteria.currency() != null) {
            url.append("&currency_code=").append(criteria.currency());
        }
        if (criteria.experience() != null) {
            url.append("&experience=").append(criteria.experience());
        }
        if (page > 0) {
            url.append("&page=").append(page);
        }

        return url.toString();
    }

    private void checkCaptcha(Page page) {
        if (page.url().contains("captcha")
                || page.locator("[data-qa='captcha']").count() > 0) {
            throw new CaptchaException("Обнаружена капча, страница: " + page.url());
        }
    }

    private Salary parseSalary(Page page) {
        Locator salaryLocator = page.locator("[data-qa='vacancy-salary']");
        if (salaryLocator.count() == 0) return null;

        String text = salaryLocator.textContent();
        if (text == null || text.isBlank()) return null;

        List<Integer> numbers = new ArrayList<>();
        Matcher m = NUMBER_PATTERN.matcher(text);
        while (m.find()) {
            String numStr = m.group().replaceAll("[\\u00a0\\s]", "");
            try {
                numbers.add(Integer.parseInt(numStr));
            } catch (NumberFormatException ignored) {
                // нечисловые токены пропускаем
            }
        }

        String currency = null;
        if (text.contains("руб") || text.contains("₽")) currency = "RUR";
        else if (text.contains("USD") || text.contains("$")) currency = "USD";
        else if (text.contains("EUR") || text.contains("€")) currency = "EUR";

        return switch (numbers.size()) {
            case 0 -> null;
            case 1 -> text.contains("до") ? new Salary(null, numbers.get(0), currency)
                    : new Salary(numbers.get(0), null, currency);
            default -> new Salary(numbers.get(0), numbers.get(1), currency);
        };
    }

    private WorkFormat parseWorkFormat(Page page) {
        Locator workFormatsLocator = page.locator("[data-qa='work-formats-text']");
        if (workFormatsLocator.count() > 0) {
            String text = workFormatsLocator.textContent().replace('\u00a0', ' ').toLowerCase();
            if (text.contains("удалённ") || text.contains("удаленн")) {
                return WorkFormat.REMOTE;
            }
            if (text.contains("гибрид")) {
                return WorkFormat.HYBRID;
            }
            if (text.contains("офис") || text.contains("на месте")) {
                return WorkFormat.OFFICE;
            }
        }
        if (page.locator("[data-qa='vacancy-label-work-schedule-remote']").count() > 0) {
            return WorkFormat.REMOTE;
        }
        return null;
    }

    private String getTextOrNull(Page page, String selector) {
        Locator locator = page.locator(selector);
        if (locator.count() == 0) return null;
        String text = locator.first().textContent();
        return text != null && !text.isBlank() ? text.trim() : null;
    }

    private String extractVacancyId(String href) {
        if (href == null) return null;
        Matcher m = VACANCY_ID_PATTERN.matcher(href);
        return m.find() ? m.group(1) : null;
    }
}