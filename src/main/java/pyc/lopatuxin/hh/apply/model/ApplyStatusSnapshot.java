package pyc.lopatuxin.hh.apply.model;

import java.util.List;

public record ApplyStatusSnapshot(
        boolean running,
        int found,
        int filtered,
        int applied,
        int failed,
        List<ProcessedVacancy> processedVacancies
) {
}
