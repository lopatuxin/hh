package pyc.lopatuxin.hh.apply.domain.port.out;

import pyc.lopatuxin.hh.apply.domain.model.ApplyStatus;

import java.util.Set;

public interface ApplyHistoryPort {
    void mark(String vacancyId, String company, String url, ApplyStatus status);

    Set<String> getAllIds();
}