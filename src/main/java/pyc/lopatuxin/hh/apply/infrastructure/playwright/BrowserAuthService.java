package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pyc.lopatuxin.hh.config.HhProperties;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrowserAuthService {

    private record AuthSession(Browser browser, BrowserContext context) {}

    private final Playwright playwright;
    private final HhProperties properties;
    private final AtomicReference<AuthSession> session = new AtomicReference<>();

    public void start() {
        AuthSession old = session.getAndSet(null);
        if (old != null) {
            log.info("Закрываю предыдущую сессию авторизации");
            old.context().close();
            old.browser().close();
        }

        log.info("Открываю браузер для авторизации на hh.ru");
        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        try {
            BrowserContext context = browser.newContext();
            session.set(new AuthSession(browser, context));
            context.newPage().navigate("https://hh.ru/login");
            log.info("Браузер открыт, жду авторизации пользователя");
        } catch (Exception e) {
            log.error("Ошибка при открытии браузера авторизации: {}", e.getMessage());
            browser.close();
            throw e;
        }
    }

    @PreDestroy
    public void cleanup() {
        AuthSession current = session.getAndSet(null);
        if (current != null) {
            log.info("Закрываю незавершённую сессию авторизации при остановке приложения");
            current.context().close();
            current.browser().close();
        }
    }

    public String save() {
        AuthSession current = session.getAndSet(null);
        if (current == null) {
            throw new IllegalStateException("Браузер авторизации не запущен. Сначала вызовите /api/auth/start");
        }

        String path = properties.browser().authStatePath();
        log.info("Сохраняю состояние браузера в {}", path);

        current.context().storageState(
                new BrowserContext.StorageStateOptions().setPath(Paths.get(path))
        );
        current.context().close();
        current.browser().close();

        log.info("Авторизация сохранена в {}", path);
        return path;
    }
}