package pyc.lopatuxin.hh.apply.infrastructure.playwright;

import com.microsoft.playwright.BrowserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.port.out.BrowserSessionPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaywrightSessionHolder implements BrowserSessionPort {

    private final PlaywrightContextFactory contextFactory;
    private BrowserContext context;

    @Override
    public void open() {
        if (context != null) {
            throw new IllegalStateException("Сессия браузера уже открыта");
        }
        context = contextFactory.createAuthenticatedContext();
        log.info("Открыта сессия браузера");
    }

    @Override
    public void close() {
        if (context != null) {
            context.close();
            context = null;
            log.info("Сессия браузера закрыта");
        }
    }

    public BrowserContext getContext() {
        if (context == null) {
            throw new IllegalStateException("Сессия браузера не открыта, вызовите open() перед использованием");
        }
        return context;
    }
}