package pyc.lopatuxin.hh.apply.domain.model;

import java.util.List;

public record ApplyStatusSnapshot(
        boolean running,
        int found,
        int filtered,
        int applied,
        int failed,
        List<ProcessedVacancy> processedVacancies
) {

    public static ApplyStatusSnapshot idle() {
        return new ApplyStatusSnapshot(false, 0, 0, 0, 0, List.of());
    }
}
