package pyc.lopatuxin.hh.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("hh")
@Validated
public record HhProperties(
        Browser browser,
        @NotBlank String resumeId
) {
    public record Browser(
            String authStatePath,
            boolean headless,
            long delayMinMs,
            long delayMaxMs,
            int maxPerDay
    ) {}
}