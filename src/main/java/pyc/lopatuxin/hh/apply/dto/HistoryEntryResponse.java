package pyc.lopatuxin.hh.apply.dto;

import pyc.lopatuxin.hh.apply.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.repository.ApplyHistoryEntity;

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
