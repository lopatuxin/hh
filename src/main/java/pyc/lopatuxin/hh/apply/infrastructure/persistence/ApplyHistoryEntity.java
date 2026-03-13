package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;

import java.time.Instant;

@Getter
@Entity
@Table(name = "applied_vacancies")
@NoArgsConstructor
@AllArgsConstructor
public class ApplyHistoryEntity {

    @Id
    private String vacancyId;

    private String company;

    @Column(nullable = false)
    private Instant appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplyStatus status;
}
