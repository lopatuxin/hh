package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.port.out.NegotiationPort;
import pyc.lopatuxin.hh.exception.AdditionalActionRequiredException;
import pyc.lopatuxin.hh.exception.ApplyButtonNotFoundException;
import pyc.lopatuxin.hh.exception.ApplyFailedException;
import pyc.lopatuxin.hh.config.HhProperties;
import pyc.lopatuxin.hh.util.HhConstants;
import pyc.lopatuxin.hh.util.PageGuards;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaywrightNegotiationAdapter implements NegotiationPort {

    private final PlaywrightContextFactory contextFactory;
    private final HhProperties properties;

    @Override
    public void apply(String vacancyId) {
        try (BrowserContext context = contextFactory.createAuthenticatedContext()) {

            Page page = context.newPage();
            String url = HhConstants.VACANCY_URL + vacancyId;
            log.info("Открываю вакансию для отклика: {}", url);
            page.navigate(url);

            PageGuards.checkCaptcha(page);
            PageGuards.checkSession(page);

            Locator applyButton = findApplyButton(page, vacancyId);
            applyButton.click();
            log.debug("Нажал кнопку отклика на вакансию {}", vacancyId);

            waitRandomDelay(page);

            verifyApplySucceeded(page, vacancyId);

            log.info("Успешно откликнулся на вакансию {}", vacancyId);
        }
    }

    private Locator findApplyButton(Page page, String vacancyId) {
        Locator button = page.locator(
                "[data-qa='vacancy-response-link-top'], [data-qa='vacancy-response-link-bottom']"
        ).first();

        if (!button.isVisible()) {
            throw new ApplyButtonNotFoundException("Кнопка отклика не найдена на вакансии " + vacancyId);
        }

        return button;
    }

    private void waitRandomDelay(Page page) {
        long delay = ThreadLocalRandom.current().nextLong(
                properties.browser().delayMinMs(),
                properties.browser().delayMaxMs()
        );
        page.waitForTimeout(delay);
    }

    private void verifyApplySucceeded(Page page, String vacancyId) {
        checkNoAdditionalActions(page, vacancyId);

        Locator responseButton = page.locator("[data-qa='vacancy-response-link-top']");
        if (responseButton.count() == 0) {
            return;
        }

        String buttonText = responseButton.first().textContent();
        if (buttonText != null && buttonText.toLowerCase().contains("откликнулись")) {
            return;
        }

        checkButtonDeactivated(responseButton, vacancyId);
    }

    private void checkNoAdditionalActions(Page page, String vacancyId) {
        if (page.locator("[data-qa='vacancy-response-popup']").count() > 0
                || page.locator("[data-qa='vacancy-response-letter-toggle']").count() > 0) {
            throw new AdditionalActionRequiredException("Вакансия " + vacancyId + " требует дополнительных действий (модальное окно)");
        }
    }

    private void checkButtonDeactivated(Locator button, String vacancyId) {
        if (!isButtonDisabled(button)) {
            throw new ApplyFailedException("Отклик на вакансию " + vacancyId + " не прошёл: кнопка всё ещё активна");
        }
    }

    private boolean isButtonDisabled(Locator button) {
        return Boolean.TRUE.toString().equals(button.first().getAttribute("aria-disabled"));
    }
}