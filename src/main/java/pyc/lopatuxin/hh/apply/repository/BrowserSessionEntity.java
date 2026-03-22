package pyc.lopatuxin.hh.apply.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "browser_sessions")
@Getter
@Setter
@NoArgsConstructor
public class BrowserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state_json", nullable = false, columnDefinition = "TEXT")
    private String stateJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public BrowserSessionEntity(String stateJson) {
        this.stateJson = stateJson;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
