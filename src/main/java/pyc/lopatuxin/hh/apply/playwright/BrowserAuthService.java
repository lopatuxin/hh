package pyc.lopatuxin.hh.apply.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pyc.lopatuxin.hh.apply.repository.BrowserSessionEntity;
import pyc.lopatuxin.hh.apply.repository.BrowserSessionRepository;
import pyc.lopatuxin.hh.apply.service.VncService;
import pyc.lopatuxin.hh.util.HhConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrowserAuthService {

    private record AuthSession(Playwright playwright, Browser browser, BrowserContext context) {}

    private final VncService vncService;
    private final BrowserSessionRepository sessionRepository;
    private final AtomicReference<AuthSession> session = new AtomicReference<>();

    public void start() {
        cancel();

        log.info("Запускаю VNC и браузер для авторизации на hh.ru");
        try {
            vncService.start(this::cancel);

            Playwright playwright = Playwright.create();
            Map<String, String> env = new HashMap<>(System.getenv());
            env.put("DISPLAY", vncService.getDisplay());
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setEnv(env)
            );
            BrowserContext context = browser.newContext();
            session.set(new AuthSession(playwright, browser, context));
            context.newPage().navigate(HhConstants.LOGIN_URL);

            log.info("Браузер открыт на виртуальном дисплее, жду авторизации пользователя");
        } catch (Exception e) {
            log.error("Ошибка при запуске авторизации: {}", e.getMessage());
            closeAuthSession();
            vncService.stop();
            throw new RuntimeException("Не удалось запустить браузер авторизации", e);
        }
    }

    public void save() {
        AuthSession current = session.getAndSet(null);
        if (current == null) {
            throw new IllegalStateException("Браузер авторизации не запущен. Сначала вызовите /api/auth/start");
        }

        log.info("Сохраняю сессию авторизации в БД");
        try {
            String stateJson = current.context().storageState();

            BrowserSessionEntity entity = sessionRepository.findTopByOrderByUpdatedAtDesc()
                    .orElseGet(() -> new BrowserSessionEntity(stateJson));
            entity.setStateJson(stateJson);
            entity.setUpdatedAt(java.time.Instant.now());
            sessionRepository.save(entity);

            log.info("Сессия авторизации сохранена в БД");
        } finally {
            closeSession(current);
            vncService.stop();
        }
    }

    public void cancel() {
        AuthSession current = session.getAndSet(null);
        if (current != null) {
            log.info("Отмена авторизации — закрываю браузер без сохранения");
            closeSession(current);
        }
        vncService.stop();
    }

    @PreDestroy
    public void cleanup() {
        cancel();
    }

    private void closeSession(AuthSession authSession) {
        try {
            authSession.context().close();
        } catch (Exception e) {
            log.debug("Ошибка при закрытии контекста: {}", e.getMessage());
        }
        try {
            authSession.browser().close();
        } catch (Exception e) {
            log.debug("Ошибка при закрытии браузера: {}", e.getMessage());
        }
        try {
            authSession.playwright().close();
        } catch (Exception e) {
            log.debug("Ошибка при закрытии Playwright: {}", e.getMessage());
        }
    }

    private void closeAuthSession() {
        AuthSession current = session.getAndSet(null);
        if (current != null) {
            closeSession(current);
        }
    }
}
