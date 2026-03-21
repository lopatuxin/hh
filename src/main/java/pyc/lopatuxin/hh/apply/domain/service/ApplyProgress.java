package pyc.lopatuxin.hh.apply.domain.service;

import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatusSnapshot;
import pyc.lopatuxin.hh.apply.domain.model.ProcessedVacancy;
import pyc.lopatuxin.hh.apply.domain.model.Salary;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ApplyProgress {

    private int found;
    private int skipped;
    private int applied;
    private int failed;
    private final CopyOnWriteArrayList<ProcessedVacancy> processedVacancies = new CopyOnWriteArrayList<>();

    void recordFiltered(String vacancyId, String title, String company, Salary salary, String reason) {
        found++;
        skipped++;
        processedVacancies.add(new ProcessedVacancy(vacancyId, title, company, salary, ApplyStatus.FILTERED, reason));
    }

    void recordApplied(String vacancyId, String title, String company, Salary salary) {
        found++;
        applied++;
        processedVacancies.add(new ProcessedVacancy(vacancyId, title, company, salary, ApplyStatus.APPLIED, null));
    }

    void recordFetchFailed() {
        failed++;
    }

    void recordApplyFailed(String vacancyId, String title, String company, Salary salary, String reason) {
        found++;
        failed++;
        processedVacancies.add(new ProcessedVacancy(vacancyId, title, company, salary, ApplyStatus.ACTION_REQUIRED, reason));
    }

    int applied() {
        return applied;
    }

    ApplyResult toResult() {
        return new ApplyResult(found, skipped, applied, failed);
    }

    ApplyStatusSnapshot toSnapshot(boolean running) {
        return new ApplyStatusSnapshot(running, found, skipped, applied, failed, List.copyOf(processedVacancies));
    }
}
