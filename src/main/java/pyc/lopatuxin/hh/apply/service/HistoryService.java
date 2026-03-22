package pyc.lopatuxin.hh.apply.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pyc.lopatuxin.hh.apply.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.repository.ApplyHistoryEntity;
import pyc.lopatuxin.hh.apply.repository.ApplyHistoryRepository;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final ApplyHistoryRepository repository;

    public void mark(String vacancyId, String title, String company, String url, ApplyStatus status) {
        if (!repository.existsById(vacancyId)) {
            repository.save(new ApplyHistoryEntity(vacancyId, title, company, Instant.now(), url, status));
        }
    }

    public Set<String> getAllIds() {
        return repository.findAll().stream()
                .map(ApplyHistoryEntity::getVacancyId)
                .collect(Collectors.toSet());
    }

    public Page<ApplyHistoryEntity> findFiltered(ApplyStatus status, Instant dateFrom,
                                                  Instant dateTo, String company,
                                                  int page, int size) {
        return repository.findFiltered(status, dateFrom, dateTo, company, PageRequest.of(page, size));
    }
}
