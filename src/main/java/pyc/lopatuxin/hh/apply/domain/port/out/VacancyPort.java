package pyc.lopatuxin.hh.apply.domain.port.out;

import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;

import java.util.List;
import java.util.Optional;

public interface VacancyPort {
    List<String> collectIds(ApplyCriteria criteria, int page);
    Optional<Vacancy> fetchDetail(String id);
}