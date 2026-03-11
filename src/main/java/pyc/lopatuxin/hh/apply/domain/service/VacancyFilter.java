package pyc.lopatuxin.hh.apply.domain.service;

import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.model.WorkFormat;

import java.util.List;

public class VacancyFilter {

    public boolean matches(Vacancy vacancy, ApplyCriteria criteria) {
        if (vacancy.requiresCoverLetter()) {
            return false;
        }

        if (isCompanyExcluded(vacancy, criteria)) {
            return false;
        }

        if (!workFormatMatches(vacancy, criteria)) {
            return false;
        }

        if (!salaryMatches(vacancy, criteria)) {
            return false;
        }

        if (criteria.experience() != null
                && !criteria.experience().equals(vacancy.experience())) {
            return false;
        }

        return true;
    }

    private boolean workFormatMatches(Vacancy vacancy, ApplyCriteria criteria) {
        List<WorkFormat> wanted = criteria.workFormats();
        if (wanted == null || wanted.isEmpty()) {
            return true;
        }
        if (vacancy.workFormat() == null) {
            return true;
        }
        return wanted.contains(vacancy.workFormat());
    }

    private boolean isCompanyExcluded(Vacancy vacancy, ApplyCriteria criteria) {
        if (criteria.excludedCompanies() == null || criteria.excludedCompanies().isEmpty()) {
            return false;
        }
        if (vacancy.company() == null) {
            return false;
        }
        return criteria.excludedCompanies().stream()
                .anyMatch(excluded -> vacancy.company().toLowerCase().contains(excluded.toLowerCase()));
    }

    private boolean salaryMatches(Vacancy vacancy, ApplyCriteria criteria) {
        if (criteria.salaryFrom() <= 0 || vacancy.salary() == null) {
            return true;
        }
        Integer salaryValue = vacancy.salary().to() != null
                ? vacancy.salary().to()
                : vacancy.salary().from();
        return salaryValue == null || salaryValue >= criteria.salaryFrom();
    }

}