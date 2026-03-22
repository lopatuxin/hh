package pyc.lopatuxin.hh.apply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.model.ApplyResult;
import pyc.lopatuxin.hh.apply.service.ApplyService;
import pyc.lopatuxin.hh.apply.dto.ApplyRunResponse;
import pyc.lopatuxin.hh.apply.dto.ApplyStatusResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apply")
public class ApplyController {

    private final ApplyService applyService;

    @PostMapping("/run")
    public ResponseEntity<ApplyRunResponse> run(@RequestBody ApplyCriteria criteria) {
        long start = System.currentTimeMillis();
        ApplyResult result = applyService.run(criteria);
        long durationMs = System.currentTimeMillis() - start;
        return ResponseEntity.ok(ApplyRunResponse.of(result, durationMs));
    }

    @GetMapping("/status")
    public ResponseEntity<ApplyStatusResponse> status() {
        return ResponseEntity.ok(ApplyStatusResponse.from(applyService.getStatus()));
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stop() {
        applyService.stop();
        return ResponseEntity.ok().build();
    }
}
