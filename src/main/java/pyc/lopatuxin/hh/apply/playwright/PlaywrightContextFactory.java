package pyc.lopatuxin.hh.apply.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.repository.BrowserSessionEntity;
import pyc.lopatuxin.hh.apply.repository.BrowserSessionRepository;

@Component
@RequiredArgsConstructor
public class PlaywrightContextFactory {

    private final Browser browser;
    private final BrowserSessionRepository sessionRepository;

    public BrowserContext createAuthenticatedContext() {
        BrowserSessionEntity session = sessionRepository.findTopByOrderByUpdatedAtDesc()
                .orElseThrow(() -> new IllegalStateException(
                        "Сессия авторизации не найдена. Необходимо авторизоваться через /api/auth/start"));

        return browser.newContext(new Browser.NewContextOptions()
                .setStorageState(session.getStateJson()));
    }
}
