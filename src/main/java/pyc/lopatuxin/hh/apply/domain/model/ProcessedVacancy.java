package pyc.lopatuxin.hh.apply.domain.model;

public record ProcessedVacancy(
        String vacancyId,
        String title,
        String company,
        Salary salary,
        ApplyStatus status,
        String filterReason
) {
}
