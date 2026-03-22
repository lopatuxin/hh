package pyc.lopatuxin.hh.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.dto.SettingsDto;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class MutableSettingsHolder {

    private final AtomicReference<SettingsDto> override = new AtomicReference<>();
    private final HhProperties defaults;

    public MutableSettingsHolder(HhProperties properties) {
        this.defaults = properties;
    }

    public SettingsDto getSettings() {
        SettingsDto current = override.get();
        if (current != null) {
            return current;
        }
        return new SettingsDto(
                defaults.resumeId(),
                defaults.browser().delayMinMs(),
                defaults.browser().delayMaxMs(),
                defaults.browser().maxPerDay()
        );
    }

    public void updateSettings(SettingsDto dto) {
        log.info("Обновлены настройки: resumeId={}, delayMin={}, delayMax={}, maxPerDay={}",
                dto.resumeId(), dto.delayMinMs(), dto.delayMaxMs(), dto.maxPerDay());
        override.set(dto);
    }
}
