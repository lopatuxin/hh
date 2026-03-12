package pyc.lopatuxin.hh.apply.domain.model;

import java.util.List;

public record ApplyCriteria(
        int areaId,
        int salaryFrom,
        String currency,
        String experience,
        List<String> keywords,
        List<String> excludedCompanies,
        List<WorkFormat> workFormats,
        List<List<String>> requiredTitleGroups,
        List<String> excludedTitleWords,
        int limit
) {
}