package pyc.lopatuxin.hh.apply.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.port.in.ApplyUseCase;
import pyc.lopatuxin.hh.config.ApplyCriteriaProperties;

@RestController
@RequestMapping("/api/apply")
public class ApplyController {

    private final ApplyUseCase applyUseCase;
    private final ApplyCriteriaProperties criteriaProperties;

    public ApplyController(ApplyUseCase applyUseCase, ApplyCriteriaProperties criteriaProperties) {
        this.applyUseCase = applyUseCase;
        this.criteriaProperties = criteriaProperties;
    }

    @PostMapping("/run")
    public ResponseEntity<ApplyRunResponse> run() {
        long start = System.currentTimeMillis();
        ApplyCriteria criteria = new ApplyCriteria(
                criteriaProperties.areaId(),
                criteriaProperties.salaryFrom(),
                criteriaProperties.currency(),
                criteriaProperties.experience(),
                criteriaProperties.keywords()
        );
        ApplyResult result = applyUseCase.run(criteria);
        long durationMs = System.currentTimeMillis() - start;
        return ResponseEntity.ok(ApplyRunResponse.of(result, durationMs));
    }

}