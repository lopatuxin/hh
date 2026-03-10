package pyc.lopatuxin.hh.apply.domain.model;

import java.util.List;

public record Vacancy(
        String id,
        String title,
        Salary salary,
        String area,
        String experience,
        List<String> keySkills,
        boolean requiresCoverLetter
) {
}