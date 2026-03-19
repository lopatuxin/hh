package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import pyc.lopatuxin.hh.apply.domain.model.Currency;
import pyc.lopatuxin.hh.apply.domain.model.Salary;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.model.WorkFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
class VacancyPageParser {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d[\\d\\u00a0\\s]*\\d|\\d");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\u00a0\\s]");

    Vacancy parseVacancy(Page page, String id) {
        Locator titleLocator = page.locator("[data-qa='vacancy-title']");
        String title = titleLocator.count() > 0 ? titleLocator.textContent().trim() : "";

        String company = getTextOrNull(page, "[data-qa='vacancy-company-name']");

        Salary salary = parseSalary(page);
        String area = getTextOrNull(page, "[data-qa='vacancy-view-location']");
        String experience = getTextOrNull(page, "[data-qa='vacancy-experience']");

        List<String> keySkills = parseKeySkills(page);

        boolean requiresCoverLetter =
                page.locator("[data-qa='vacancy-response-letter-required']").count() > 0;

        WorkFormat workFormat = parseWorkFormat(page);

        log.info("Вакансия {}: '{}', компания: '{}', формат: {}, сопроводительное обязательно: {}", id, title, company, workFormat, requiresCoverLetter);
        return new Vacancy(id, title, company, salary, area, experience, keySkills, requiresCoverLetter, workFormat);
    }

    private Salary parseSalary(Page page) {
        String text = getTextOrNull(page, "[data-qa='vacancy-salary']");
        if (text == null) {
            return null;
        }

        List<BigDecimal> numbers = extractNumbers(text);
        Currency currency = Currency.fromText(text);

        return buildSalary(numbers, text, currency);
    }

    private List<BigDecimal> extractNumbers(String text) {
        List<BigDecimal> numbers = new ArrayList<>();
        Matcher matcher = NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            String numStr = WHITESPACE_PATTERN.matcher(matcher.group()).replaceAll("");
            try {
                numbers.add(new BigDecimal(numStr));
            } catch (NumberFormatException e) {
                log.debug("Не удалось распарсить число из токена '{}': {}", numStr, e.getMessage());
            }
        }
        return numbers;
    }

    private List<String> parseKeySkills(Page page) {
        return page.locator("[data-qa='bloko-tag__text']")
                .all().stream()
                .map(l -> l.textContent().trim())
                .filter(s -> !s.isBlank())
                .toList();
    }

    private Salary buildSalary(List<BigDecimal> numbers, String text, Currency currency) {
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
        if (locator.count() == 0) {
            return null;
        }
        String text = locator.first().textContent();
        return text != null && !text.isBlank() ? text.trim() : null;
    }
}