package pyc.lopatuxin.hh.apply.domain.port.out;

import java.util.Set;

public interface ApplyHistoryPort {
    boolean isApplied(String vacancyId);
    void markApplied(String vacancyId, String company);
    Set<String> getAllIds();
}