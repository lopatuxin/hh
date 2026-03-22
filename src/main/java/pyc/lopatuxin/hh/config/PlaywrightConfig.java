package pyc.lopatuxin.hh.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Playwright для браузерной автоматизации.
 * <p>
 * Создаёт управляемые Spring-контейнером бины {@link Playwright} и {@link Browser},
 * которые автоматически закрываются при остановке приложения ({@code destroyMethod = "close"}).
 * Браузер всегда запускается в headless-режиме.
 */
@Configuration
public class PlaywrightConfig {

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
     * Запускает браузер Chromium в headless-режиме.
     *
     * @param playwright экземпляр Playwright для запуска браузера
     * @return экземпляр браузера Chromium
     */
    @Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {
        return playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }
}