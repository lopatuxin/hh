package pyc.lopatuxin.hh.apply.infrastructure.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.domain.model.DailyStats;
import pyc.lopatuxin.hh.apply.infrastructure.persistence.ApplyHistoryRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController {

    private final ApplyHistoryRepository historyRepository;

    @GetMapping
    public ResponseEntity<StatsResponse> stats(
            @RequestParam(defaultValue = "30") int days
    ) {
        Instant now = Instant.now();
        Instant todayStart = now.truncatedTo(ChronoUnit.DAYS);
        Instant weekStart = todayStart.minus(7, ChronoUnit.DAYS);
        Instant monthStart = todayStart.minus(30, ChronoUnit.DAYS);

        long today = historyRepository.countByStatusAndAppliedAtAfter(ApplyStatus.APPLIED, todayStart);
        long week = historyRepository.countByStatusAndAppliedAtAfter(ApplyStatus.APPLIED, weekStart);
        long month = historyRepository.countByStatusAndAppliedAtAfter(ApplyStatus.APPLIED, monthStart);
        long total = historyRepository.countByStatus(ApplyStatus.APPLIED);

        Instant since = todayStart.minus(days, ChronoUnit.DAYS);
        List<Object[]> raw = historyRepository.countGroupedByDayAndStatus(since);

        Map<LocalDate, long[]> grouped = new LinkedHashMap<>();
        for (Object[] row : raw) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            ApplyStatus status = (ApplyStatus) row[1];
            long count = (Long) row[2];
            long[] counts = grouped.computeIfAbsent(date, k -> new long[2]);
            if (status == ApplyStatus.APPLIED) {
                counts[0] = count;
            } else if (status == ApplyStatus.FILTERED) {
                counts[1] = count;
            }
        }

        List<DailyStats> dailyStats = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            dailyStats.add(new DailyStats(entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }

        return ResponseEntity.ok(new StatsResponse(today, week, month, total, dailyStats));
    }
}
