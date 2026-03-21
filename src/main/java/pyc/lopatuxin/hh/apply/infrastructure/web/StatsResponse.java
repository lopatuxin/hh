package pyc.lopatuxin.hh.apply.infrastructure.web;

import pyc.lopatuxin.hh.apply.domain.model.DailyStats;

import java.util.List;

public record StatsResponse(
        long today,
        long week,
        long month,
        long total,
        List<DailyStats> dailyStats
) {
}
