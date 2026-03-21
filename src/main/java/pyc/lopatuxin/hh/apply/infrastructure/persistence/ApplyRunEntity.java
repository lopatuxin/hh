package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "apply_runs")
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant finishedAt;

    @Column(nullable = false)
    private int found;

    @Column(nullable = false)
    private int filtered;

    @Column(nullable = false)
    private int applied;

    @Column(nullable = false)
    private int failed;

    @Column(nullable = false)
    private long durationMs;

    public ApplyRunEntity(Instant startedAt, Instant finishedAt, int found, int filtered,
                          int applied, int failed, long durationMs) {
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.found = found;
        this.filtered = filtered;
        this.applied = applied;
        this.failed = failed;
        this.durationMs = durationMs;
    }
}
