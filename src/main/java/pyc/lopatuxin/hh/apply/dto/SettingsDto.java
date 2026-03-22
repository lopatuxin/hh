package pyc.lopatuxin.hh.apply.dto;

public record SettingsDto(
        String resumeId,
        long delayMinMs,
        long delayMaxMs,
        int maxPerDay,
        boolean headless
) {
}
