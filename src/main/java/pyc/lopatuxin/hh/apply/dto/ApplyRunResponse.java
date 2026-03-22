package pyc.lopatuxin.hh.apply.dto;

import pyc.lopatuxin.hh.apply.model.ApplyResult;

public record ApplyRunResponse(
        int found,
        int skipped,
        int applied,
        int failed,
        long durationMs
) {

    public static ApplyRunResponse of(ApplyResult result, long durationMs) {
        return new ApplyRunResponse(result.found(), result.skipped(), result.applied(), result.failed(), durationMs);
    }
}