package pyc.lopatuxin.hh.apply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.playwright.BrowserAuthService;
import pyc.lopatuxin.hh.apply.dto.AuthResponse;
import pyc.lopatuxin.hh.apply.dto.AuthStatusResponse;
import pyc.lopatuxin.hh.config.HhProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final BrowserAuthService browserAuthService;
    private final HhProperties properties;

    @PostMapping("/start")
    public ResponseEntity<AuthResponse> start() {
        browserAuthService.start();
        return ResponseEntity.ok(AuthResponse.opened());
    }

    @PostMapping("/save")
    public ResponseEntity<AuthResponse> save() {
        String path = browserAuthService.save();
        return ResponseEntity.ok(AuthResponse.saved(path));
    }

    @GetMapping("/status")
    public ResponseEntity<AuthStatusResponse> status() {
        Path authPath = Paths.get(properties.browser().authStatePath());
        boolean exists = Files.exists(authPath);
        Instant lastModified = null;
        if (exists) {
            try {
                lastModified = Files.getLastModifiedTime(authPath).toInstant();
            } catch (IOException e) {
                // Файл существует, но не удалось прочитать время модификации
            }
        }
        return ResponseEntity.ok(new AuthStatusResponse(exists, lastModified));
    }
}
