package pyc.lopatuxin.hh.apply.infrastructure.hh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HhVacanciesPage(
        List<HhVacancyResponse> items,
        int found,
        int pages,
        @JsonProperty("per_page") int perPage
) {
}