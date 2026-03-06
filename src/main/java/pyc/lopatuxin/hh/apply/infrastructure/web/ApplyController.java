package pyc.lopatuxin.hh.apply.infrastructure.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.port.in.ApplyUseCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apply")
public class ApplyController {

    private final ApplyUseCase applyUseCase;

    @PostMapping("/run")
    public ResponseEntity<ApplyRunResponse> run(@RequestBody ApplyCriteria criteria) {
        long start = System.currentTimeMillis();
        ApplyResult result = applyUseCase.run(criteria);
        long durationMs = System.currentTimeMillis() - start;
        return ResponseEntity.ok(ApplyRunResponse.of(result, durationMs));
    }
}