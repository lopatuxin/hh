package pyc.lopatuxin.hh.apply.domain.model;

import java.time.LocalDate;

public record DailyStats(
        LocalDate date,
        long applied,
        long filtered
) {
}
