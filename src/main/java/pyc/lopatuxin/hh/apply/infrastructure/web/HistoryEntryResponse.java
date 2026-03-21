package pyc.lopatuxin.hh.apply.infrastructure.web;

import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.infrastructure.persistence.ApplyHistoryEntity;

import java.time.Instant;

public record HistoryEntryResponse(
        String vacancyId,
        String title,
        String company,
        ApplyStatus status,
        Instant appliedAt,
        String url
) {

    public static HistoryEntryResponse from(ApplyHistoryEntity entity) {
        return new HistoryEntryResponse(
                entity.getVacancyId(),
                entity.getTitle(),
                entity.getCompany(),
                entity.getStatus(),
                entity.getAppliedAt(),
                entity.getUrl()
        );
    }
}
