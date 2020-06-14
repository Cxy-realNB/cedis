package per.cxy.cedis.executor.api;

import per.cxy.cedis.model.DataTableResult;
import per.cxy.cedis.model.RedisObject;

import java.util.List;
import java.util.Map;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:41
 */
public interface GetExecutable<K, V> {
    List<K> keys(int db);

    List<K> keys();

    DataTableResult keys(DataTableResult dataTableResult);

    List<K> keys(int db, String match);

    Map<String, String> getConfig(String configName);

    RedisObject get(int db, K key);

    String type(int db, K key);

    boolean exist(int db, K key);

    void select(int db);
}
