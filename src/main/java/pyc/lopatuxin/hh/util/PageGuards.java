package pyc.lopatuxin.hh.util;

import com.microsoft.playwright.Page;
import lombok.experimental.UtilityClass;
import pyc.lopatuxin.hh.apply.domain.model.SessionExpiredException;
import pyc.lopatuxin.hh.exception.CaptchaException;

/**
 * Общие проверки состояния страницы для Playwright-адаптеров.
 */
@UtilityClass
public class PageGuards {

    /**
     * Проверяет страницу на наличие капчи.
     *
     * @throws CaptchaException если обнаружена капча
     */
    public void checkCaptcha(Page page) {
        if (page.url().contains("captcha")
                || page.locator("[data-qa='captcha']").count() > 0) {
            throw new CaptchaException("Обнаружена капча, страница: " + page.url());
        }
    }

    /**
     * Проверяет, не истекла ли сессия (редирект на страницу логина).
     *
     * @throws SessionExpiredException если сессия истекла
     */
    public void checkSession(Page page) {
        if (page.url().contains("/login") || page.url().contains("/account/login")) {
            throw new SessionExpiredException("Сессия истекла, необходима повторная авторизация");
        }
    }
}