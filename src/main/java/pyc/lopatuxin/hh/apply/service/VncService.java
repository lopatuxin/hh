package pyc.lopatuxin.hh.apply.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

@Slf4j
@Service
public class VncService {

    private static final int DISPLAY_NUM = 99;
    private static final int VNC_PORT = 5900;
    private static final int WEBSOCKIFY_PORT = 6080;
    private static final long CLEANUP_TIMEOUT_MINUTES = 10;
    private static final String DISPLAY = ":" + DISPLAY_NUM;

    private Process xvfbProcess;
    private Process x11vncProcess;
    private Process websockifyProcess;
    private ScheduledFuture<?> cleanupTask;
    private Runnable cleanupCallback;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public synchronized String getDisplay() {
        return DISPLAY;
    }

    public synchronized void start(Runnable onCleanup) throws IOException {
        stop();
        this.cleanupCallback = onCleanup;
        log.info("Запускаю VNC-стек на дисплее {}", DISPLAY);

        startXvfb();
        startX11vnc();
        startWebsockify();

        cleanupTask = scheduler.schedule(() -> {
            log.warn("Таймаут авторизации ({}мин) — автоматическая очистка", CLEANUP_TIMEOUT_MINUTES);
            if (cleanupCallback != null) {
                cleanupCallback.run();
            }
            stop();
        }, CLEANUP_TIMEOUT_MINUTES, TimeUnit.MINUTES);

        log.info("VNC-стек запущен: websockify на порту {}", WEBSOCKIFY_PORT);
    }

    public synchronized void stop() {
        if (cleanupTask != null) {
            cleanupTask.cancel(false);
            cleanupTask = null;
        }
        destroyProcess(websockifyProcess, "websockify");
        websockifyProcess = null;
        destroyProcess(x11vncProcess, "x11vnc");
        x11vncProcess = null;
        destroyProcess(xvfbProcess, "Xvfb");
        xvfbProcess = null;
        cleanupCallback = null;
    }

    public synchronized boolean isRunning() {
        return xvfbProcess != null && xvfbProcess.isAlive();
    }

    @PreDestroy
    public void cleanup() {
        stop();
        scheduler.shutdownNow();
    }

    private void startXvfb() throws IOException {
        xvfbProcess = new ProcessBuilder(
                "Xvfb", DISPLAY, "-screen", "0", "1024x768x24"
        ).redirectOutput(ProcessBuilder.Redirect.DISCARD)
         .redirectError(ProcessBuilder.Redirect.DISCARD)
         .start();

        Path lockFile = Path.of("/tmp/.X" + DISPLAY_NUM + "-lock");
        waitForCondition(() -> Files.exists(lockFile), 5000, "Xvfb");
        log.debug("Xvfb запущен на дисплее {}", DISPLAY);
    }

    private void startX11vnc() throws IOException {
        x11vncProcess = new ProcessBuilder(
                "x11vnc", "-display", DISPLAY, "-nopw", "-forever", "-quiet",
                "-ncache", "10", "-ncache_cr",
                "-wireframe", "-speeds", "lan",
                "-threads"
        ).redirectOutput(ProcessBuilder.Redirect.DISCARD)
         .redirectError(ProcessBuilder.Redirect.DISCARD)
         .start();

        waitForCondition(() -> isPortOpen(VNC_PORT), 5000, "x11vnc");
        log.debug("x11vnc запущен на порту {}", VNC_PORT);
    }

    private void startWebsockify() throws IOException {
        websockifyProcess = new ProcessBuilder(
                "websockify", "--web=/usr/share/novnc",
                String.valueOf(WEBSOCKIFY_PORT), "localhost:" + VNC_PORT
        ).redirectOutput(ProcessBuilder.Redirect.DISCARD)
         .redirectError(ProcessBuilder.Redirect.DISCARD)
         .start();

        waitForCondition(() -> isPortOpen(WEBSOCKIFY_PORT), 5000, "websockify");
        log.debug("websockify запущен на порту {}", WEBSOCKIFY_PORT);
    }

    private void destroyProcess(Process process, String name) {
        if (process != null && process.isAlive()) {
            log.debug("Останавливаю {}", name);
            process.destroy();
            try {
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    log.warn("{} не завершился за 5 сек — принудительно остановлен", name);
                }
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void waitForCondition(BooleanSupplier condition, long timeoutMs, String name) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > deadline) {
                throw new IllegalStateException("Таймаут ожидания запуска " + name);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Прервано ожидание " + name);
            }
        }
    }

    private boolean isPortOpen(int port) {
        try (var socket = new java.net.Socket("localhost", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
