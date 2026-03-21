package pyc.lopatuxin.hh.apply.infrastructure.web;

public record SettingsDto(
        String resumeId,
        long delayMinMs,
        long delayMaxMs,
        int maxPerDay,
        boolean headless
) {
}
