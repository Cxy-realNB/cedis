package per.cxy.cedis.executor.api;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:42
 */
public interface SetExecutable<K, V> {
    void renameKey(int db, K oldKey, K newKey);

    String stringSet(int db, K key, String value);

    String hashSet(int db, K key, Map<String, Object> value);

    Long listSet(int db, K key, V[] value);

    Long setSet(int db, K key, Set<V> value);

    Long zsetSet(int db, K key, LinkedHashSet<V> value);

    Boolean deleteKey(int db, K... keys);
}
