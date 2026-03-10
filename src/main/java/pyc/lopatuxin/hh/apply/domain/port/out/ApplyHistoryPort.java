package pyc.lopatuxin.hh.apply.domain.port.out;

public interface ApplyHistoryPort {
    boolean isApplied(String vacancyId);
    void markApplied(String vacancyId, String company);
}