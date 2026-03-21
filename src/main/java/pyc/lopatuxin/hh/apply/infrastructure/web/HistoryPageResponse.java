package pyc.lopatuxin.hh.apply.infrastructure.web;

import java.util.List;

public record HistoryPageResponse(
        List<HistoryEntryResponse> content,
        int totalPages,
        long totalElements,
        int page,
        int size
) {
}
