package pyc.lopatuxin.hh.apply.model;

public record ApplyResult(
        int found,
        int skipped,
        int applied,
        int failed
) {
}