package pyc.lopatuxin.hh.apply.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pyc.lopatuxin.hh.apply.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.model.ApplyResult;
import pyc.lopatuxin.hh.apply.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.model.ApplyStatusSnapshot;
import pyc.lopatuxin.hh.apply.model.Vacancy;
import pyc.lopatuxin.hh.apply.playwright.PlaywrightNegotiationAdapter;
import pyc.lopatuxin.hh.apply.playwright.PlaywrightSessionHolder;
import pyc.lopatuxin.hh.apply.playwright.PlaywrightVacancyAdapter;
import pyc.lopatuxin.hh.apply.repository.ApplyRunEntity;
import pyc.lopatuxin.hh.apply.repository.ApplyRunRepository;
import pyc.lopatuxin.hh.exception.ApplyException;
import pyc.lopatuxin.hh.util.HhConstants;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final PlaywrightSessionHolder sessionHolder;
    private final PlaywrightVacancyAdapter vacancyAdapter;
    private final PlaywrightNegotiationAdapter negotiationAdapter;
    private final HistoryService historyService;
    private final VacancyFilter vacancyFilter;
    private final ApplyRunRepository runRepository;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    private volatile ApplyProgress currentProgress = new ApplyProgress();

    public ApplyResult run(ApplyCriteria criteria) {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Процесс отклика уже запущен");
        }
        stopRequested.set(false);
        currentProgress = new ApplyProgress();
        Instant startedAt = Instant.now();
        long start = System.currentTimeMillis();
        try (sessionHolder) {
            sessionHolder.open();
            Set<String> excludeIds = new HashSet<>(historyService.getAllIds());
            int page = 0;

            while (currentProgress.applied() < criteria.limit()) {
                if (stopRequested.get()) {
                    log.info("Процесс остановлен по запросу пользователя");
                    break;
                }

                List<String> rawPageIds = vacancyAdapter.collectIds(criteria, page);
                if (rawPageIds.isEmpty()) {
                    log.info("Страница {} пуста, вакансии закончились", page);
                    break;
                }

                List<String> newIds = filterNewIds(rawPageIds, excludeIds);
                log.info("Страница {}: всего {}, новых {}", page, rawPageIds.size(), newIds.size());

                for (String id : newIds) {
                    if (stopRequested.get()) {
                        log.info("Процесс остановлен по запросу пользователя");
                        break;
                    }
                    if (currentProgress.applied() >= criteria.limit()) {
                        log.info("Достигнут лимит откликов ({}), останавливаемся", criteria.limit());
                        break;
                    }
                    processVacancy(id, criteria, excludeIds, currentProgress);
                }

                page++;
            }

            ApplyResult result = currentProgress.toResult();
            long durationMs = System.currentTimeMillis() - start;
            runRepository.save(new ApplyRunEntity(
                    startedAt, Instant.now(),
                    result.found(), result.skipped(), result.applied(), result.failed(),
                    durationMs
            ));
            return result;
        } finally {
            running.set(false);
        }
    }

    public void stop() {
        if (running.get()) {
            stopRequested.set(true);
            log.info("Запрошена остановка процесса откликов");
        }
    }

    public ApplyStatusSnapshot getStatus() {
        return currentProgress.toSnapshot(running.get());
    }

    private List<String> filterNewIds(List<String> rawPageIds, Set<String> excludeIds) {
        return rawPageIds.stream()
                .filter(id -> !excludeIds.contains(id))
                .toList();
    }

    private void processVacancy(String id, ApplyCriteria criteria,
                                Set<String> excludeIds, ApplyProgress progress) {
        Optional<Vacancy> optionalVacancy = vacancyAdapter.fetchDetail(id);
        if (optionalVacancy.isEmpty()) {
            progress.recordFetchFailed();
            return;
        }
        Vacancy vacancy = optionalVacancy.get();
        String url = HhConstants.VACANCY_URL + vacancy.id();
        if (!vacancyFilter.matches(vacancy, criteria)) {
            log.debug("Вакансия {} не прошла фильтр, пропускаем", vacancy.id());
            historyService.mark(vacancy.id(), vacancy.title(), vacancy.company(), url, ApplyStatus.FILTERED);
            excludeIds.add(vacancy.id());
            progress.recordFiltered(vacancy.id(), vacancy.title(), vacancy.company(), vacancy.salary(), "Не соответствует критериям фильтра");
            return;
        }
        try {
            negotiationAdapter.apply(vacancy.id());
            historyService.mark(vacancy.id(), vacancy.title(), vacancy.company(), url, ApplyStatus.APPLIED);
            excludeIds.add(vacancy.id());
            progress.recordApplied(vacancy.id(), vacancy.title(), vacancy.company(), vacancy.salary());
        } catch (ApplyException e) {
            log.warn("Не удалось откликнуться на вакансию {}: {}", vacancy.id(), e.getMessage());
            historyService.mark(vacancy.id(), vacancy.title(), vacancy.company(), url, ApplyStatus.ACTION_REQUIRED);
            excludeIds.add(vacancy.id());
            progress.recordApplyFailed(vacancy.id(), vacancy.title(), vacancy.company(), vacancy.salary(), e.getMessage());
        }
    }
}
