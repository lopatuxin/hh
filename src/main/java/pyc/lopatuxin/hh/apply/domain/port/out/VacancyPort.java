package pyc.lopatuxin.hh.apply.domain.port.out;

import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;

import java.util.List;

public interface VacancyPort {
    List<Vacancy> search(ApplyCriteria criteria);
}