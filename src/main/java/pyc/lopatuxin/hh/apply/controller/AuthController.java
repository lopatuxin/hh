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

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final BrowserAuthService browserAuthService;

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
        return ResponseEntity.ok(new AuthStatusResponse(false, (Instant) null));
    }
}
