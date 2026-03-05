package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.port.out.ApplyHistoryPort;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryApplyHistoryAdapter implements ApplyHistoryPort {

    private final ConcurrentHashMap<String, Instant> history = new ConcurrentHashMap<>();

    @Override
    public boolean isApplied(String vacancyId) {
        return history.containsKey(vacancyId);
    }

    @Override
    public void markApplied(String vacancyId) {
        history.put(vacancyId, Instant.now());
    }
}