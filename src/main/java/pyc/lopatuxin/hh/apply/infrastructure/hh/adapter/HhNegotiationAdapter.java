package pyc.lopatuxin.hh.apply.infrastructure.hh.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.port.out.NegotiationPort;
import pyc.lopatuxin.hh.apply.infrastructure.hh.OAuthTokenProvider;
import pyc.lopatuxin.hh.apply.infrastructure.hh.client.HhApiClient;
import pyc.lopatuxin.hh.config.HhProperties;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class HhNegotiationAdapter implements NegotiationPort {

    private final HhApiClient apiClient;
    private final OAuthTokenProvider tokenProvider;
    private final HhProperties hhProperties;

    @Override
    public void apply(String vacancyId) {
        apiClient.apply(
                "Bearer " + tokenProvider.getToken(),
                Map.of(
                        "vacancy_id", vacancyId,
                        "resume_id", hhProperties.resumeId()
                )
        );
    }
}