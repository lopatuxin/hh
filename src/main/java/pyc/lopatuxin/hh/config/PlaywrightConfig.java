package pyc.lopatuxin.hh.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Playwright для браузерной автоматизации.
 * <p>
 * Создаёт управляемые Spring-контейнером бины {@link Playwright} и {@link Browser},
 * которые автоматически закрываются при остановке приложения ({@code destroyMethod = "close"}).
 * Режим запуска браузера (headless/headed) определяется настройкой {@code hh.browser.headless}.
 */
@Configuration
@RequiredArgsConstructor
public class PlaywrightConfig {

    private final HhProperties properties;

    /**
     * Создаёт экземпляр {@link Playwright} — точку входа для управления браузерами.
     *
     * @return экземпляр Playwright
     */
    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        return Playwright.create();
    }

    /**
     * Запускает браузер Chromium с настройками из {@link HhProperties}.
     *
     * @param playwright экземпляр Playwright для запуска браузера
     * @return экземпляр браузера Chromium
     */
    @Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {
        return playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(properties.browser().headless())
        );
    }
}