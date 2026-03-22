package pyc.lopatuxin.hh.apply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.repository.ApplyRunRepository;
import pyc.lopatuxin.hh.apply.dto.RunEntryResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/runs")
public class RunsController {

    private final ApplyRunRepository runRepository;

    @GetMapping
    public ResponseEntity<List<RunEntryResponse>> runs(
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<RunEntryResponse> runs = runRepository
                .findAllByOrderByStartedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(RunEntryResponse::from)
                .toList();
        return ResponseEntity.ok(runs);
    }
}
