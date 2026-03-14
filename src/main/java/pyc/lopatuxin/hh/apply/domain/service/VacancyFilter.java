package pyc.lopatuxin.hh.apply.domain.service;

import pyc.lopatuxin.hh.apply.domain.model.ApplyCriteria;
import pyc.lopatuxin.hh.apply.domain.model.Vacancy;
import pyc.lopatuxin.hh.apply.domain.model.WorkFormat;

import java.util.List;

public class VacancyFilter {

    public boolean matches(Vacancy vacancy, ApplyCriteria criteria) {
        if (!titleMatchesGroups(vacancy, criteria)) {
            return false;
        }

        if (titleContainsExcludedWords(vacancy, criteria)) {
            return false;
        }

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

        return criteria.experience() == null
                || criteria.experience().equals(vacancy.experience());
    }

    private boolean titleMatchesGroups(Vacancy vacancy, ApplyCriteria criteria) {
        List<List<String>> groups = criteria.requiredTitleGroups();
        if (groups == null || groups.isEmpty()) {
            return true;
        }
        if (vacancy.title() == null || vacancy.title().isBlank()) {
            return false;
        }
        String titleLower = vacancy.title().toLowerCase();
        return groups.stream()
                .allMatch(group -> group.stream()
                        .anyMatch(word -> titleLower.contains(word.toLowerCase())));
    }

    private boolean titleContainsExcludedWords(Vacancy vacancy, ApplyCriteria criteria) {
        List<String> excluded = criteria.excludedTitleWords();
        if (excluded == null || excluded.isEmpty()) {
            return false;
        }
        if (vacancy.title() == null || vacancy.title().isBlank()) {
            return false;
        }
        String titleLower = vacancy.title().toLowerCase();
        return excluded.stream()
                .anyMatch(word -> titleLower.contains(word.toLowerCase()));
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