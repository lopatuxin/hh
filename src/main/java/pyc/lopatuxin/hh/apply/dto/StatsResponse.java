package pyc.lopatuxin.hh.apply.dto;

import pyc.lopatuxin.hh.apply.model.DailyStats;

import java.util.List;

public record StatsResponse(
        long today,
        long week,
        long month,
        long total,
        List<DailyStats> dailyStats
) {
}
