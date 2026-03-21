package pyc.lopatuxin.hh.apply.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRunRepository extends JpaRepository<ApplyRunEntity, Long> {

    List<ApplyRunEntity> findAllByOrderByStartedAtDesc(Pageable pageable);
}
