package pyc.lopatuxin.hh.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("hh")
@Validated
public record HhProperties(
        Api api,
        Oauth oauth,
        @NotBlank String resumeId
) {
    public record Api(
            String baseUrl,
            int rateLimitPerSecond
    ) {}

    public record Oauth(
            String clientId,
            String clientSecret,
            String accessToken,
            String refreshToken
    ) {}
}