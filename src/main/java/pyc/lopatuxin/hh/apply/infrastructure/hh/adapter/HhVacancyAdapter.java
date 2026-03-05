package pyc.lopatuxin.hh.apply.infrastructure.hh.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Salary;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.port.out.VacancyPort;
import pyc.lopatuxin.hh.apply.infrastructure.hh.OAuthTokenProvider;
import pyc.lopatuxin.hh.apply.infrastructure.hh.client.HhApiClient;
import pyc.lopatuxin.hh.apply.infrastructure.hh.dto.HhKeySkill;
import pyc.lopatuxin.hh.apply.infrastructure.hh.dto.HhVacanciesPage;
import pyc.lopatuxin.hh.apply.infrastructure.hh.dto.HhVacancyResponse;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HhVacancyAdapter implements VacancyPort {

    private final HhApiClient apiClient;
    private final OAuthTokenProvider tokenProvider;

    @Override
    public List<Vacancy> search(ApplyCriteria criteria) {
        List<Vacancy> result = new ArrayList<>();
        String authorization = "Bearer " + tokenProvider.getToken();
        String text = criteria.keywords() != null ? String.join(" ", criteria.keywords()) : "";

        int page = 0;
        int totalPages;
        do {
            HhVacanciesPage hhPage = apiClient.searchVacancies(
                    authorization,
                    text,
                    criteria.areaId(),
                    criteria.salaryFrom(),
                    criteria.currency(),
                    criteria.experience(),
                    100,
                    page
            );
            totalPages = hhPage.pages();
            hhPage.items().stream()
                    .map(this::toVacancy)
                    .forEach(result::add);
            page++;
        } while (page < totalPages);

        return result;
    }

    private Vacancy toVacancy(HhVacancyResponse r) {
        Salary salary = r.salary() != null
                ? new Salary(r.salary().from(), r.salary().to(), r.salary().currency())
                : null;
        String area = r.area() != null ? r.area().name() : null;
        String experience = r.experience() != null ? r.experience().id() : null;
        List<String> keySkills = r.keySkills() != null
                ? r.keySkills().stream().map(HhKeySkill::name).toList()
                : List.of();
        return new Vacancy(r.id(), r.name(), salary, area, experience, keySkills);
    }
}