package pyc.lopatuxin.hh.apply.model;

import java.util.List;

public record ApplyCriteria(
        int areaId,
        int salaryFrom,
        Currency currency,
        String experience,
        List<String> keywords,
        List<String> excludedCompanies,
        List<WorkFormat> workFormats,
        List<List<String>> requiredTitleGroups,
        List<String> excludedTitleWords,
        int limit
) {
}