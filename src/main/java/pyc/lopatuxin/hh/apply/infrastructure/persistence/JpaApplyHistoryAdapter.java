package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.domain.port.out.ApplyHistoryPort;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaApplyHistoryAdapter implements ApplyHistoryPort {

    private final ApplyHistoryRepository repository;

    @Override
    public void mark(String vacancyId, String company, ApplyStatus status) {
        if (!repository.existsById(vacancyId)) {
            repository.save(new ApplyHistoryEntity(vacancyId, company, Instant.now(), status));
        }
    }

    @Override
    public Set<String> getAllIds() {
        return repository.findAll().stream()
                .map(ApplyHistoryEntity::getVacancyId)
                .collect(Collectors.toSet());
    }
}
