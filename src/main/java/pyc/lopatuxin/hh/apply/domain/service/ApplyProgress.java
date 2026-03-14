package pyc.lopatuxin.hh.apply.domain.service;

import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;

class ApplyProgress {

    private int found;
    private int skipped;
    private int applied;
    private int failed;

    void recordFiltered() {
        found++;
        skipped++;
    }

    void recordApplied() {
        found++;
        applied++;
    }

    void recordFetchFailed() {
        failed++;
    }

    void recordApplyFailed() {
        found++;
        failed++;
    }

    int applied() {
        return applied;
    }

    ApplyResult toResult() {
        return new ApplyResult(found, skipped, applied, failed);
    }
}