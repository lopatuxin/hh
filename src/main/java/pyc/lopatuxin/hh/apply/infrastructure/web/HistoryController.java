package pyc.lopatuxin.hh.apply.infrastructure.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;
import pyc.lopatuxin.hh.apply.infrastructure.persistence.ApplyHistoryEntity;
import pyc.lopatuxin.hh.apply.infrastructure.persistence.ApplyHistoryRepository;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final ApplyHistoryRepository historyRepository;

    @GetMapping
    public ResponseEntity<HistoryPageResponse> history(
            @RequestParam(required = false) ApplyStatus status,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ApplyHistoryEntity> result = historyRepository.findFiltered(
                status, dateFrom, dateTo, company, PageRequest.of(page, size)
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
