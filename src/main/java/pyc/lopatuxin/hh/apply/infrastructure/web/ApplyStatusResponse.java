package pyc.lopatuxin.hh.apply.infrastructure.web;

import pyc.lopatuxin.hh.apply.domain.model.ApplyStatusSnapshot;
import pyc.lopatuxin.hh.apply.domain.model.ProcessedVacancy;

import java.util.List;

public record ApplyStatusResponse(
        boolean running,
        int found,
        int filtered,
        int applied,
        int failed,
        List<ProcessedVacancy> processedVacancies
) {

    public static ApplyStatusResponse from(ApplyStatusSnapshot snapshot) {
        return new ApplyStatusResponse(
                snapshot.running(),
                snapshot.found(),
                snapshot.filtered(),
                snapshot.applied(),
                snapshot.failed(),
                snapshot.processedVacancies()
        );
    }
}
