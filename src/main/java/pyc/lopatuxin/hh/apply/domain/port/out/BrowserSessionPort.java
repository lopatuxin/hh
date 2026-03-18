package pyc.lopatuxin.hh.apply.domain.port.out;

public interface BrowserSessionPort extends AutoCloseable {
    void open();

    @Override
    void close();
}