package pyc.lopatuxin.hh.apply.infrastructure.hh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HhArea(
        String name
) {
}