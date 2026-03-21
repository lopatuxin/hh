package pyc.lopatuxin.hh.apply.infrastructure.web;

import pyc.lopatuxin.hh.apply.infrastructure.persistence.ApplyRunEntity;

import java.time.Instant;

public record RunEntryResponse(
        Long id,
        Instant startedAt,
        Instant finishedAt,
        int found,
        int filtered,
        int applied,
        int failed,
        long durationMs
) {

    public static RunEntryResponse from(ApplyRunEntity entity) {
        return new RunEntryResponse(
                entity.getId(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getFound(),
                entity.getFiltered(),
                entity.getApplied(),
                entity.getFailed(),
                entity.getDurationMs()
        );
    }
}
