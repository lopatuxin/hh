package pyc.lopatuxin.hh.apply.dto;

import java.util.List;

public record HistoryPageResponse(
        List<HistoryEntryResponse> content,
        int totalPages,
        long totalElements,
        int page,
        int size
) {
}
