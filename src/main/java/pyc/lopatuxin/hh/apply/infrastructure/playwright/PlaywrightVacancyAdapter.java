package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.port.out.VacancyPort;
import pyc.lopatuxin.hh.config.HhProperties;
import pyc.lopatuxin.hh.util.HhConstants;
import pyc.lopatuxin.hh.util.PageGuards;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class PlaywrightVacancyAdapter implements VacancyPort {

    private static final Pattern VACANCY_ID_PATTERN = Pattern.compile("/vacancy/(\\d+)");

    private final PlaywrightSessionHolder sessionHolder;
    private final HhProperties properties;

    @Override
    public List<String> collectIds(ApplyCriteria criteria, int page) {
        try (Page browserPage = sessionHolder.getContext().newPage()) {
            String url = buildSearchUrl(criteria, page);
            log.info("Открываю страницу поиска #{}: {}", page + 1, url);
            browserPage.navigate(url, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                    .setTimeout(properties.browser().navigationTimeoutMs()));
            PageGuards.checkCaptcha(browserPage);
            PageGuards.checkSession(browserPage);

            List<String> ids = new ArrayList<>();
            List<Locator> cards = browserPage.locator("[data-qa='vacancy-serp__vacancy']").all();
            if (!cards.isEmpty()) {
                log.debug("HTML карточки: {}", cards.getFirst().innerHTML());
            }
            for (Locator card : cards) {
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
    public Optional<Vacancy> fetchDetail(String id) {
        try (Page page = sessionHolder.getContext().newPage()) {
            return Optional.of(fetchVacancyDetails(page, id));
        } catch (PlaywrightException e) {
            log.warn("Не удалось загрузить детали вакансии {}, пропускаем: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    private Vacancy fetchVacancyDetails(Page page, String id) {
        String url = HhConstants.VACANCY_URL + id;
        log.info("Загружаю детали вакансии {}", url);
        page.navigate(url, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(properties.browser().navigationTimeoutMs()));
        PageGuards.checkCaptcha(page);
        PageGuards.checkSession(page);

        return VacancyPageParser.parseVacancy(page, id);
    }

    private String buildSearchUrl(ApplyCriteria criteria, int page) {
        var builder = UriComponentsBuilder.fromUriString(HhConstants.SEARCH_URL)
                .queryParam("per_page", 50);

        if (criteria.keywords() != null && !criteria.keywords().isEmpty()) {
            builder.queryParam("text", String.join(" ", criteria.keywords()));
        }
        if (criteria.areaId() > 0) {
            builder.queryParam("area", criteria.areaId());
        }
        if (criteria.salaryFrom() > 0) {
            builder.queryParam("salary", criteria.salaryFrom());
        }
        if (criteria.currency() != null) {
            builder.queryParam("currency_code", criteria.currency());
        }
        if (criteria.experience() != null) {
            builder.queryParam("experience", criteria.experience());
        }
        if (page > 0) {
            builder.queryParam("page", page);
        }

        return builder.build().toUriString();
    }

    private String extractVacancyId(String href) {
        if (href == null) {
            return null;
        }
        Matcher m = VACANCY_ID_PATTERN.matcher(href);
        return m.find() ? m.group(1) : null;
    }
}