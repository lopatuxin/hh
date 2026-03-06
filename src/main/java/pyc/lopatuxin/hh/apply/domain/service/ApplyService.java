package pyc.lopatuxin.hh.apply.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.port.in.ApplyUseCase;
import pyc.lopatuxin.hh.apply.domain.port.out.ApplyHistoryPort;
import pyc.lopatuxin.hh.apply.domain.port.out.NegotiationPort;
import pyc.lopatuxin.hh.apply.domain.port.out.VacancyPort;

import java.util.List;
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
            List<Vacancy> vacancies = vacancyPort.search(criteria);
            int found = vacancies.size();
            int skipped = 0;
            int applied = 0;
            int failed = 0;

            for (Vacancy vacancy : vacancies) {
                if (historyPort.isApplied(vacancy.id())) {
                    log.debug("Вакансия {} уже в истории, пропускаем", vacancy.id());
                    skipped++;
                } else if (!vacancyFilter.matches(vacancy, criteria)) {
                    log.debug("Вакансия {} не прошла фильтр, пропускаем", vacancy.id());
                    skipped++;
                } else {
                    try {
                        negotiationPort.apply(vacancy.id());
                        historyPort.markApplied(vacancy.id());
                        applied++;
                    } catch (Exception e) {
                        log.warn("Не удалось откликнуться на вакансию {}: {}", vacancy.id(), e.getMessage());
                        failed++;
                    }
                }
            }

            return new ApplyResult(found, skipped, applied, failed);
        } finally {
            running.set(false);
        }
    }
}