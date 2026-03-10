package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.port.out.ApplyHistoryPort;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JpaApplyHistoryAdapter implements ApplyHistoryPort {

    private final ApplyHistoryRepository repository;

    @Override
    public boolean isApplied(String vacancyId) {
        return repository.existsById(vacancyId);
    }

    @Override
    public void markApplied(String vacancyId, String company) {
        if (!repository.existsById(vacancyId)) {
            repository.save(new ApplyHistoryEntity(vacancyId, company, Instant.now()));
        }
    }
}
