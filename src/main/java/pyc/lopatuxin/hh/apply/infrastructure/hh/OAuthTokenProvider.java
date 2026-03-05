package pyc.lopatuxin.hh.apply.infrastructure.hh;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.infrastructure.hh.client.HhOAuthClient;
import pyc.lopatuxin.hh.apply.infrastructure.hh.dto.OAuthResponse;
import pyc.lopatuxin.hh.config.HhProperties;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuthTokenProvider {

    private final HhProperties hhProperties;
    private final HhOAuthClient oAuthClient;
    private volatile String accessToken;

    @PostConstruct
    void init() {
        accessToken = hhProperties.oauth().accessToken();
    }

    public String getToken() {
        return accessToken;
    }

    public synchronized void refreshToken() {
        OAuthResponse response = oAuthClient.refreshToken(Map.of(
                "grant_type", "refresh_token",
                "client_id", hhProperties.oauth().clientId(),
                "client_secret", hhProperties.oauth().clientSecret(),
                "refresh_token", hhProperties.oauth().refreshToken()
        ));
        accessToken = response.accessToken();
    }
}