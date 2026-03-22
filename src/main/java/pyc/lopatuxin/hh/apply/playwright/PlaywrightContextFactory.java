package pyc.lopatuxin.hh.apply.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Фабрика для создания аутентифицированных контекстов Playwright.
 * Вызывающий код отвечает за закрытие контекста (try-with-resources).
 */
@Component
@RequiredArgsConstructor
public class PlaywrightContextFactory {

    private final Browser browser;

    /**
     * Создаёт {@link BrowserContext} с загруженным состоянием авторизации.
     */
    public BrowserContext createAuthenticatedContext() {
        return browser.newContext();
    }
}