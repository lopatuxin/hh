package pyc.lopatuxin.hh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties("hh.criteria")
@Validated
public record ApplyCriteriaProperties(
        int areaId,
        int salaryFrom,
        String currency,
        String experience,
        List<String> keywords
) {
}