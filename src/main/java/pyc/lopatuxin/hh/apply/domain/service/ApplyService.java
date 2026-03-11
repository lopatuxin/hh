package pyc.lopatuxin.hh.apply.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.model.SessionExpiredException;
import pyc.lopatuxin.hh.apply.domain.port.in.ApplyUseCase;
import pyc.lopatuxin.hh.apply.domain.port.out.ApplyHistoryPort;
import pyc.lopatuxin.hh.apply.domain.port.out.NegotiationPort;
import pyc.lopatuxin.hh.apply.domain.port.out.VacancyPort;

import java.util.List;
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
            Set<String> excludeIds = historyPort.getAllIds();
            List<String> allIds = vacancyPort.collectIds(criteria).stream()
                    .filter(id -> !excludeIds.contains(id))
                    .toList();

            int batchSize = criteria.limit() > 0 ? criteria.limit() : allIds.size();
            int found = 0;
            int skipped = 0;
            int applied = 0;
            int failed = 0;
            int offset = 0;

            while (applied < criteria.limit() && offset < allIds.size()) {
                List<String> batch = allIds.subList(offset, Math.min(offset + batchSize, allIds.size()));
                offset += batchSize;

                List<Vacancy> vacancies = vacancyPort.fetchDetails(batch);
                found += vacancies.size();

                for (Vacancy vacancy : vacancies) {
                    if (criteria.limit() > 0 && applied >= criteria.limit()) {
                        log.info("Достигнут лимит откликов ({}), останавливаемся", criteria.limit());
                        break;
                    }
                    if (!vacancyFilter.matches(vacancy, criteria)) {
                        log.debug("Вакансия {} не прошла фильтр, пропускаем", vacancy.id());
                        skipped++;
                    } else {
                        try {
                            negotiationPort.apply(vacancy.id());
                            historyPort.markApplied(vacancy.id(), vacancy.company());
                            applied++;
                        } catch (SessionExpiredException e) {
                            log.error("Сессия истекла, прерываю прогон. Необходима повторная авторизация через /api/auth/start");
                            throw e;
                        } catch (Exception e) {
                            log.warn("Не удалось откликнуться на вакансию {}: {}", vacancy.id(), e.getMessage());
                            failed++;
                        }
                    }
                }
            }

            return new ApplyResult(found, skipped, applied, failed);
        } finally {
            running.set(false);
        }
    }
}