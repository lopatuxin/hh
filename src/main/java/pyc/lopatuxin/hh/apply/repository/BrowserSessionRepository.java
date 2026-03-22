package pyc.lopatuxin.hh.apply.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrowserSessionRepository extends JpaRepository<BrowserSessionEntity, Long> {
    Optional<BrowserSessionEntity> findTopByOrderByUpdatedAtDesc();
}
