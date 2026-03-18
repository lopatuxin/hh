package pyc.lopatuxin.hh.apply.domain.model;

import java.math.BigDecimal;

public record Salary(
        BigDecimal from,
        BigDecimal to,
        Currency currency
) {
}