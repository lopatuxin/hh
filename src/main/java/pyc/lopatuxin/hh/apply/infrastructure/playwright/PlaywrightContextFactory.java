package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.config.HhProperties;

import java.nio.file.Paths;

/**
 * Фабрика для создания аутентифицированных контекстов Playwright.
 * Вызывающий код отвечает за закрытие контекста (try-with-resources).
 */
@Component
@RequiredArgsConstructor
public class PlaywrightContextFactory {

    private final Browser browser;
    private final HhProperties properties;

    /**
     * Создаёт {@link BrowserContext} с загруженным состоянием авторизации.
     */
    public BrowserContext createAuthenticatedContext() {
        return browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(Paths.get(properties.browser().authStatePath())));
    }
}