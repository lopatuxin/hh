package pyc.lopatuxin.hh.apply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.dto.StatsResponse;
import pyc.lopatuxin.hh.apply.service.StatsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<StatsResponse> stats(
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(statsService.calculateStats(days));
    }
}
