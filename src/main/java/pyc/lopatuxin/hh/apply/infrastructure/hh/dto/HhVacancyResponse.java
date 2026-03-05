package pyc.lopatuxin.hh.apply.infrastructure.hh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HhVacancyResponse(
        String id,
        String name,
        HhSalary salary,
        HhArea area,
        HhExperience experience,
        @JsonProperty("key_skills") List<HhKeySkill> keySkills
) {
}