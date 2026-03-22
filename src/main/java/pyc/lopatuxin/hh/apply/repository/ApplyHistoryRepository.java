package pyc.lopatuxin.hh.apply.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pyc.lopatuxin.hh.apply.model.ApplyStatus;

import java.time.Instant;
import java.util.List;

public interface ApplyHistoryRepository extends JpaRepository<ApplyHistoryEntity, String> {

    long countByStatus(ApplyStatus status);

    long countByAppliedAtAfter(Instant after);

    long countByStatusAndAppliedAtAfter(ApplyStatus status, Instant after);

    @Query("""
            SELECT CAST(e.appliedAt AS date) AS day, e.status, COUNT(e)
            FROM ApplyHistoryEntity e
            WHERE e.appliedAt >= :since
            GROUP BY CAST(e.appliedAt AS date), e.status
            ORDER BY day
            """)
    List<Object[]> countGroupedByDayAndStatus(@Param("since") Instant since);

    @Query("""
            SELECT e FROM ApplyHistoryEntity e
            WHERE (:status IS NULL OR e.status = :status)
            AND (:dateFrom IS NULL OR e.appliedAt >= :dateFrom)
            AND (:dateTo IS NULL OR e.appliedAt <= :dateTo)
            AND (:company IS NULL OR LOWER(e.company) LIKE LOWER(CONCAT('%', :company, '%')))
            ORDER BY e.appliedAt DESC
            """)
    Page<ApplyHistoryEntity> findFiltered(
            @Param("status") ApplyStatus status,
            @Param("dateFrom") Instant dateFrom,
            @Param("dateTo") Instant dateTo,
            @Param("company") String company,
            Pageable pageable
    );
}
