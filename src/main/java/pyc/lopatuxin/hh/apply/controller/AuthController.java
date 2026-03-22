package pyc.lopatuxin.hh.apply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pyc.lopatuxin.hh.apply.dto.AuthResponse;
import pyc.lopatuxin.hh.apply.dto.AuthStatusResponse;
import pyc.lopatuxin.hh.apply.playwright.BrowserAuthService;
import pyc.lopatuxin.hh.apply.repository.BrowserSessionRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final BrowserAuthService browserAuthService;
    private final BrowserSessionRepository sessionRepository;

    @PostMapping("/start")
    public ResponseEntity<AuthResponse> start() {
        browserAuthService.start();
        return ResponseEntity.ok(AuthResponse.opened());
    }

    @PostMapping("/save")
    public ResponseEntity<AuthResponse> save() {
        browserAuthService.save();
        return ResponseEntity.ok(AuthResponse.saved());
    }

    @PostMapping("/cancel")
    public ResponseEntity<AuthResponse> cancel() {
        browserAuthService.cancel();
        return ResponseEntity.ok(AuthResponse.cancelled());
    }

    @GetMapping("/status")
    public ResponseEntity<AuthStatusResponse> status() {
        return sessionRepository.findTopByOrderByUpdatedAtDesc()
                .map(entity -> ResponseEntity.ok(
                        new AuthStatusResponse(true, entity.getUpdatedAt())))
                .orElse(ResponseEntity.ok(
                        new AuthStatusResponse(false, null)));
    }
}
