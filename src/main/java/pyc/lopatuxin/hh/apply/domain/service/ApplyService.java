package pyc.lopatuxin.hh.apply.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.model.SessionExpiredException;
import pyc.lopatuxin.hh.apply.domain.port.in.ApplyUseCase;
import pyc.lopatuxin.hh.apply.domain.port.out.ApplyHistoryPort;
import pyc.lopatuxin.hh.apply.domain.port.out.NegotiationPort;
import pyc.lopatuxin.hh.apply.domain.port.out.VacancyPort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService implements ApplyUseCase {

    private final VacancyPort vacancyPort;
    private final NegotiationPort negotiationPort;
    private final ApplyHistoryPort historyPort;
    private final VacancyFilter vacancyFilter;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public ApplyResult run(ApplyCriteria criteria) {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Процесс отклика уже запущен");
        }
        try {
            Set<String> excludeIds = new HashSet<>(historyPort.getAllIds());
            ApplyProgress progress = new ApplyProgress();
            int page = 0;

            while (progress.applied() < criteria.limit()) {
                List<String> rawPageIds = vacancyPort.collectIds(criteria, page);
                if (rawPageIds.isEmpty()) {
                    log.info("Страница {} пуста, вакансии закончились", page);
                    break;
                }

                List<String> newIds = filterNewIds(rawPageIds, excludeIds);
                log.info("Страница {}: всего {}, новых {}", page, rawPageIds.size(), newIds.size());

                for (String id : newIds) {
                    if (progress.applied() >= criteria.limit()) {
                        log.info("Достигнут лимит откликов ({}), останавливаемся", criteria.limit());
                        break;
                    }
                    processVacancy(id, criteria, excludeIds, progress);
                }

                page++;
            }

            return progress.toResult();
        } finally {
            running.set(false);
        }
    }

    private List<String> filterNewIds(List<String> rawPageIds, Set<String> excludeIds) {
        return rawPageIds.stream()
                .filter(id -> !excludeIds.contains(id))
                .toList();
    }

    private void processVacancy(String id, ApplyCriteria criteria,
                                Set<String> excludeIds, ApplyProgress progress) {
        Optional<Vacancy> optionalVacancy = vacancyPort.fetchDetail(id);
        if (optionalVacancy.isEmpty()) {
            progress.recordFetchFailed();
            return;
        }
        Vacancy vacancy = optionalVacancy.get();
        if (!vacancyFilter.matches(vacancy, criteria)) {
            log.debug("Вакансия {} не прошла фильтр, пропускаем", vacancy.id());
            historyPort.mark(vacancy.id(), vacancy.company(), ApplyStatus.FILTERED);
            excludeIds.add(vacancy.id());
            progress.recordFiltered();
            return;
        }
        try {
            negotiationPort.apply(vacancy.id());
            historyPort.mark(vacancy.id(), vacancy.company(), ApplyStatus.APPLIED);
            excludeIds.add(vacancy.id());
            progress.recordApplied();
        } catch (IllegalStateException e) {
            log.warn("Не удалось откликнуться на вакансию {}: {}", vacancy.id(), e.getMessage());
            progress.recordApplyFailed();
        }
    }
}