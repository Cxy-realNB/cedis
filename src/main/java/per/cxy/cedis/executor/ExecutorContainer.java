package per.cxy.cedis.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import per.cxy.cedis.exceptions.CommandUnsupportedException;
import per.cxy.cedis.executor.api.BaseExecutable;
import per.cxy.cedis.model.ClientParams;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:46
 */
public class ExecutorContainer {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorContainer.class);

    private static Map<String, BaseExecutable> executorsContainer = new ConcurrentHashMap<>();

    static public boolean put(BaseExecutable baseExecutable) {
        if(!executorsContainer.containsKey(baseExecutable.getName())) {
            executorsContainer.put(baseExecutable.getName(), baseExecutable);
            return true;
        }
        return false;
    }

    static public Collection<BaseExecutable> getAll() {
        return executorsContainer.values();
    }

    static public BaseExecutable get(String name) {
        return executorsContainer.get(name);
    }

    static public boolean containsKey(String name) {
        return executorsContainer.containsKey(name);
    }

    public static void openAll() {
        executorsContainer.forEach((k, v) -> {
            logger.info("{} is opening!", k);
            try {
                v.open();
            } catch (CommandUnsupportedException e) {
                BaseExecutable baseExecutable = executorsContainer.get(k);
                baseExecutable.close();
                ClientParams clientParams = baseExecutable.getDisplayParams().getClientParams();
                clientParams.toRedisUri();
                BaseExecutable clusterNodes = ExecutorFactory.singleNodeToCluster(clientParams);
                executorsContainer.put(k, clusterNodes);
                logger.warn("Single node transferred to clusterNode.");
            }
        });
    }

    public static void open(String name) {
        if (executorsContainer.containsKey(name)) {
            try {
                executorsContainer.get(name).open();
            } catch (CommandUnsupportedException e) {
                BaseExecutable baseExecutable = executorsContainer.get(name);
                baseExecutable.close();
                ClientParams clientParams = baseExecutable.getDisplayParams().getClientParams();
                clientParams.toRedisUri();
                BaseExecutable clusterNodes = ExecutorFactory.singleNodeToCluster(clientParams);
                executorsContainer.put(name, clusterNodes);
                logger.warn("Single node transferred to clusterNode.");
            }
        }
    }

    public static Boolean isOpen(String name){
        if (executorsContainer.containsKey(name)) return executorsContainer.get(name).isOpen();
        else return null;
    }

    public static void closeAll() {
        executorsContainer.forEach((k, v) -> {
            logger.info("{} is closing!", k);
            v.close();
        });
    }

    public static void close(String name) {
        if (executorsContainer.containsKey(name)) executorsContainer.get(name).close();
    }

    public static void remove(String name){
        if (executorsContainer.containsKey(name)) {
            executorsContainer.get(name).close();
            executorsContainer.remove(name);
        }
    }
}
