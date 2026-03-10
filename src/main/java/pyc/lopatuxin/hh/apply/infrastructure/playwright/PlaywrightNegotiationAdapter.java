package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.port.out.NegotiationPort;
import pyc.lopatuxin.hh.config.HhProperties;

import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaywrightNegotiationAdapter implements NegotiationPort {

    private final Browser browser;
    private final HhProperties properties;

    @Override
    public void apply(String vacancyId) {
        try (BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setStorageStatePath(Paths.get(properties.browser().authStatePath())))) {

            Page page = context.newPage();
            String url = "https://hh.ru/vacancy/" + vacancyId;
            log.info("Открываю вакансию для отклика: {}", url);
            page.navigate(url);

            if (page.url().contains("captcha") || page.locator("[data-qa='captcha']").count() > 0) {
                throw new CaptchaException("Обнаружена капча на вакансии " + vacancyId);
            }

            Locator applyButton = page.locator("[data-qa='vacancy-response-link-top']");
            if (applyButton.count() == 0) {
                applyButton = page.locator("[data-qa='vacancy-response-link-bottom']");
            }
            if (applyButton.count() == 0) {
                throw new IllegalStateException("Кнопка отклика не найдена на вакансии " + vacancyId);
            }

            applyButton.first().click();
            log.debug("Нажал кнопку отклика на вакансию {}", vacancyId);

            Locator submitButton = page.locator("[data-qa='vacancy-response-letter-submit']");
            if (submitButton.count() > 0 && submitButton.isVisible()) {
                submitButton.click();
                log.debug("Подтвердил отклик на вакансию {}", vacancyId);
            }

            log.info("Успешно откликнулся на вакансию {}", vacancyId);
        }
    }
}