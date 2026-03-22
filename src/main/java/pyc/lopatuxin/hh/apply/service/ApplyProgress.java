package pyc.lopatuxin.hh.apply.service;

import pyc.lopatuxin.hh.apply.model.ApplyResult;
import pyc.lopatuxin.hh.apply.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.model.ApplyStatusSnapshot;
import pyc.lopatuxin.hh.apply.model.ProcessedVacancy;
import pyc.lopatuxin.hh.apply.model.Salary;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class ApplyProgress {

    private final AtomicInteger found = new AtomicInteger();
    private final AtomicInteger skipped = new AtomicInteger();
    private final AtomicInteger applied = new AtomicInteger();
    private final AtomicInteger failed = new AtomicInteger();
    private final CopyOnWriteArrayList<ProcessedVacancy> processedVacancies = new CopyOnWriteArrayList<>();

    void recordFiltered(String vacancyId, String title, String company, Salary salary, String reason) {
        found.incrementAndGet();
        skipped.incrementAndGet();
        processedVacancies.add(new ProcessedVacancy(vacancyId, title, company, salary, ApplyStatus.FILTERED, reason));
    }

    void recordApplied(String vacancyId, String title, String company, Salary salary) {
        found.incrementAndGet();
        applied.incrementAndGet();
        processedVacancies.add(new ProcessedVacancy(vacancyId, title, company, salary, ApplyStatus.APPLIED, null));
    }

    void recordFetchFailed() {
        failed.incrementAndGet();
    }

    void recordApplyFailed(String vacancyId, String title, String company, Salary salary, String reason) {
        found.incrementAndGet();
        failed.incrementAndGet();
        processedVacancies.add(new ProcessedVacancy(vacancyId, title, company, salary, ApplyStatus.ACTION_REQUIRED, reason));
    }

    int applied() {
        return applied.get();
    }

    ApplyResult toResult() {
        return new ApplyResult(found.get(), skipped.get(), applied.get(), failed.get());
    }

    ApplyStatusSnapshot toSnapshot(boolean running) {
        return new ApplyStatusSnapshot(running, found.get(), skipped.get(), applied.get(), failed.get(), List.copyOf(processedVacancies));
    }
}
