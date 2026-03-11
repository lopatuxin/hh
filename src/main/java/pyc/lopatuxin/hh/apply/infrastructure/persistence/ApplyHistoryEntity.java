package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "applied_vacancies")
@NoArgsConstructor
@AllArgsConstructor
public class ApplyHistoryEntity {

    @Id
    @Column(name = "vacancy_id")
    private String vacancyId;

    @Column(name = "company")
    private String company;

    @Column(name = "applied_at", nullable = false)
    private Instant appliedAt;
}
