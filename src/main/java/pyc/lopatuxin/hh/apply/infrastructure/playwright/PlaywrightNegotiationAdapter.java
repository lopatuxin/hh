package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.model.SessionExpiredException;
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
            if (page.url().contains("/login") || page.url().contains("/account/login")) {
                throw new SessionExpiredException("Сессия истекла, необходима повторная авторизация");
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

            // Ждём одно из: модалка с доп. действиями ИЛИ успешный отклик
            page.waitForTimeout(2000);

            // Проверяем модалки, требующие дополнительных действий
            if (page.locator("[data-qa='vacancy-response-popup']").count() > 0
                    || page.locator("[data-qa='vacancy-response-letter-toggle']").count() > 0) {
                throw new IllegalStateException("Вакансия " + vacancyId + " требует дополнительных действий (модальное окно)");
            }

            // Проверяем что кнопка стала "Вы откликнулись" или исчезла
            Locator responseButton = page.locator("[data-qa='vacancy-response-link-top']");
            if (responseButton.count() > 0) {
                String buttonText = responseButton.first().textContent();
                if (buttonText != null && buttonText.toLowerCase().contains("откликнулись")) {
                    log.info("Успешно откликнулся на вакансию {}", vacancyId);
                    return;
                }
                // Кнопка всё ещё активна — отклик не прошёл
                boolean isDisabled = "true".equals(responseButton.first().getAttribute("aria-disabled"));
                if (!isDisabled) {
                    throw new IllegalStateException("Отклик на вакансию " + vacancyId + " не прошёл: кнопка всё ещё активна");
                }
            }

            log.info("Успешно откликнулся на вакансию {}", vacancyId);
        }
    }
}