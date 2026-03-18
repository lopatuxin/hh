package pyc.lopatuxin.hh.apply.domain.model;

import java.util.List;

public enum Currency {

    RUR(List.of("руб", "₽")),
    USD(List.of("USD", "$")),
    EUR(List.of("EUR", "€"));

    private final List<String> markers;

    Currency(List<String> markers) {
        this.markers = markers;
    }

    public static Currency fromText(String text) {
        for (Currency currency : values()) {
            for (String marker : currency.markers) {
                if (text.contains(marker)) {
                    return currency;
                }
            }
        }
        return null;
    }
}