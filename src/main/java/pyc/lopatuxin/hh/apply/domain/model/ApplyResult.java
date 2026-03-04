package pyc.lopatuxin.hh.apply.domain.model;

public record ApplyResult(
        int found,
        int skipped,
        int applied,
        int failed
) {
}