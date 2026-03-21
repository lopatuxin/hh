package pyc.lopatuxin.hh.apply.infrastructure.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.config.MutableSettingsHolder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings")
public class SettingsController {

    private final MutableSettingsHolder settingsHolder;

    @GetMapping
    public ResponseEntity<SettingsDto> get() {
        return ResponseEntity.ok(settingsHolder.getSettings());
    }

    @PutMapping
    public ResponseEntity<SettingsDto> update(@RequestBody SettingsDto dto) {
        settingsHolder.updateSettings(dto);
        return ResponseEntity.ok(settingsHolder.getSettings());
    }
}
