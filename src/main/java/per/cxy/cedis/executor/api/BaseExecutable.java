package per.cxy.cedis.executor.api;

import per.cxy.cedis.model.DisplayParams;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/30 7:35
 */
public interface BaseExecutable<K, V> extends GetExecutable<K, V>, SetExecutable<K, V>, InfoExecutable<K, V>, LuaExecutable<K, V>, Switchable{
    DisplayParams getDisplayParams();

    void getCurrentDisplayParams();

    void getCurrentDatabases();
}
