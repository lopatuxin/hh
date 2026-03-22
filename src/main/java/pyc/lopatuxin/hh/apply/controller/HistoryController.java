package pyc.lopatuxin.hh.apply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.repository.ApplyHistoryEntity;
import pyc.lopatuxin.hh.apply.service.HistoryService;
import pyc.lopatuxin.hh.apply.dto.HistoryEntryResponse;
import pyc.lopatuxin.hh.apply.dto.HistoryPageResponse;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<HistoryPageResponse> history(
            @RequestParam(required = false) ApplyStatus status,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ApplyHistoryEntity> result = historyService.findFiltered(
                status, dateFrom, dateTo, company, page, size
        );

        List<HistoryEntryResponse> content = result.getContent().stream()
                .map(HistoryEntryResponse::from)
                .toList();

        return ResponseEntity.ok(new HistoryPageResponse(
                content,
                result.getTotalPages(),
                result.getTotalElements(),
                result.getNumber(),
                result.getSize()
        ));
    }
}
