package per.cxy.cedis.executor.api;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:43
 */
public interface Switchable {
    boolean isOpen();

    void open();

    void close();
}
