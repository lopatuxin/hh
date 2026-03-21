package pyc.lopatuxin.hh.apply.infrastructure.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.port.in.ApplyUseCase;
import pyc.lopatuxin.hh.apply.infrastructure.persistence.ApplyRunEntity;
import pyc.lopatuxin.hh.apply.infrastructure.persistence.ApplyRunRepository;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apply")
public class ApplyController {

    private final ApplyUseCase applyUseCase;
    private final ApplyRunRepository applyRunRepository;

    @PostMapping("/run")
    public ResponseEntity<ApplyRunResponse> run(@RequestBody ApplyCriteria criteria) {
        long start = System.currentTimeMillis();
        Instant startedAt = Instant.now();
        ApplyResult result = applyUseCase.run(criteria);
        long durationMs = System.currentTimeMillis() - start;
        Instant finishedAt = Instant.now();

        applyRunRepository.save(new ApplyRunEntity(
                startedAt, finishedAt,
                result.found(), result.skipped(), result.applied(), result.failed(),
                durationMs
        ));

        return ResponseEntity.ok(ApplyRunResponse.of(result, durationMs));
    }

    @GetMapping("/status")
    public ResponseEntity<ApplyStatusResponse> status() {
        return ResponseEntity.ok(ApplyStatusResponse.from(applyUseCase.getStatus()));
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stop() {
        applyUseCase.stop();
        return ResponseEntity.ok().build();
    }
}
