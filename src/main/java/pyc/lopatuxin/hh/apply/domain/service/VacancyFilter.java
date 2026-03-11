package pyc.lopatuxin.hh.apply.domain.service;

import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;

public class VacancyFilter {

    public boolean matches(Vacancy vacancy, ApplyCriteria criteria) {
        if (vacancy.requiresCoverLetter()) {
            return false;
        }

        if (isCompanyExcluded(vacancy, criteria)) {
            return false;
        }

        if (!salaryMatches(vacancy, criteria)) {
            return false;
        }

        if (criteria.experience() != null
                && !criteria.experience().equals(vacancy.experience())) {
            return false;
        }

        return keywordsMatch(vacancy, criteria);
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

    private boolean keywordsMatch(Vacancy vacancy, ApplyCriteria criteria) {
        if (criteria.keywords() == null || criteria.keywords().isEmpty()) {
            return true;
        }
        boolean matchesTitle = criteria.keywords().stream()
                .anyMatch(kw -> vacancy.title().toLowerCase().contains(kw.toLowerCase()));
        boolean matchesSkills = vacancy.keySkills() != null
                && criteria.keywords().stream()
                .anyMatch(kw -> vacancy.keySkills().stream()
                        .anyMatch(skill -> skill.equalsIgnoreCase(kw)));
        return matchesTitle || matchesSkills;
    }
}