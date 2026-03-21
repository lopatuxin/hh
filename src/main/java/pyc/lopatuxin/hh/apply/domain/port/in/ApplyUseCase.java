package pyc.lopatuxin.hh.apply.domain.port.in;

import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;
import pyc.lopatuxin.hh.apply.domain.model.ApplyStatusSnapshot;

public interface ApplyUseCase {
    ApplyResult run(ApplyCriteria criteria);

    void stop();

    ApplyStatusSnapshot getStatus();
}
