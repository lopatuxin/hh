package pyc.lopatuxin.hh.apply.domain.port.in;

import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.ApplyResult;

public interface ApplyUseCase {
    ApplyResult run(ApplyCriteria criteria);
}