package per.cxy.cedis.executor;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import per.cxy.cedis.exceptions.CommandUnsupportedException;
import per.cxy.cedis.executor.api.BaseExecutable;
import per.cxy.cedis.model.ClientParams;
import per.cxy.cedis.model.DataTableResult;
import per.cxy.cedis.model.DisplayParams;
import per.cxy.cedis.model.RedisObject;
import per.cxy.cedis.utils.Constant;
import per.cxy.cedis.utils.DataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:46
 */
public class ClusterExecutor<K extends String, V> implements BaseExecutable<String, Object> {

    private static final Logger logger = LoggerFactory.getLogger(ClusterExecutor.class);

    private String name;
    private boolean open = true;
    private DisplayParams displayParams;
    private List<RedisURI> nodes;
    private RedisClusterClient redisClient;
    private StatefulRedisClusterConnection statefulRedisConnection;
    private RedisAdvancedClusterCommands redisCommands;

    ClusterExecutor(ClientParams clientParams) {
        this.displayParams = new DisplayParams(clientParams);
        this.name = clientParams.getName();
        RedisURI redisURI = clientParams.getRedisURI();
        RedisClient rc = RedisClient.create(redisURI);
        StatefulRedisConnection src = rc.connect();
        RedisCommands rcs = src.sync();
        rcs.setTimeout(Constant.COMMAND_TIMEOUT);
        createCluster(clientParams, rcs);
        getCurrentDisplayParams();
    }

    private void createCluster(ClientParams clientParams, RedisCommands rcs) {
        this.nodes = new LinkedList<>();
        Stream.of(rcs.clusterNodes().split("\n"))
                .map(s -> s.split(" ")[1].split("@")[0])
                .map(s -> s.split(":"))
                .forEach(t -> this.nodes.add(new ClientParams(clientParams.getName(), t[0], Integer.parseInt(t[1]), clientParams.getAuth()).getRedisURI()));
        this.redisClient = RedisClusterClient.create(this.nodes);
        this.statefulRedisConnection = this.redisClient.connect();
        this.redisCommands = this.statefulRedisConnection.sync();
        this.redisCommands.setTimeout(Constant.COMMAND_TIMEOUT);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getInfo() {
        return this.redisCommands.info();
    }

    @Override
    public Collection getNodes() {
        return this.nodes;
    }

    @Override
    public String clusterInfo() throws CommandUnsupportedException {
        return redisCommands.clusterInfo();
    }

    @Override
    public String clusterNodes() throws CommandUnsupportedException {
        return redisCommands.clusterNodes();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public synchronized void open() {
        if (!open) {
            this.statefulRedisConnection = this.redisClient.connect();
            this.redisCommands = this.statefulRedisConnection.sync();
            this.open = true;
            getCurrentDisplayParams();
        }
    }

    @Override
    public synchronized void close() {
        if (open) {
            this.statefulRedisConnection.close();
            this.open = false;
            this.displayParams.setOpen(false);
        }
    }
    @Override
    public boolean isCluster() {
        return true;
    }

    @Override
    public DisplayParams getDisplayParams() {
        return this.displayParams;
    }

    @Override
    public synchronized void getCurrentDisplayParams() {
        this.displayParams.setOpen(true);
        this.displayParams.setClusterNodes(this.redisCommands.clusterNodes());
        this.displayParams.setNodeSize(this.redisCommands.clusterNodes().split("\n").length);
        this.displayParams.setCluster(true);
        this.displayParams.setClusterInfo(this.redisCommands.clusterInfo());
        this.displayParams.setClientInfo(this.redisCommands.info());
        this.displayParams.setClients(Integer.parseInt(this.redisCommands.info(Constant.INFO_CLIENT).split("\n", 3)[1].trim().split(":")[1]));
        this.displayParams.setRedisVersion(this.redisCommands.info(Constant.INFO_SERVER).split("\n", 3)[1].trim().split(":")[1]);
        this.displayParams.setDatabases(Integer.parseInt((String) this.redisCommands.configGet(Constant.CONFIG_DATABASES).get(Constant.CONFIG_DATABASES)));
        if (this.displayParams.getClientConfig().equals("")) this.displayParams.setClientConfig(JSONObject.fromObject(this.redisCommands.configGet("*")).toString());
        int dbSize = new Long(this.redisCommands.dbsize()).intValue();
        this.displayParams.putDbSize("0", String.valueOf(dbSize));
        this.displayParams.setDbSizeSum(dbSize);
    }

    @Override
    public synchronized void getCurrentDatabases() {
        int dbSize = new Long(this.redisCommands.dbsize()).intValue();
        this.displayParams.putDbSize("0", String.valueOf(dbSize));
        this.displayParams.setDbSizeSum(dbSize);
    }

    @Override
    public List<String> keys(int db) {
        if (db != 0) return null;
        return keys(db, "*");
    }

    @Override
    public List<String> keys() {
        return this.keys(0);
    }

    @Override
    public DataTableResult keys(DataTableResult dataTableResult) {
        if (dataTableResult.getDb() != 0) return null;
        ScanArgs scanArgs = ScanArgs.Builder.limit(dataTableResult.getLimit());
        ScanCursor scanCursor = new ScanCursor(dataTableResult.getCursor(), false);
        KeyScanCursor keyScanCursor = redisCommands.scan(scanCursor, scanArgs);
        scanCursor.setCursor(keyScanCursor.getCursor());
        return DataTableResult.Builder.build(keyScanCursor.getKeys()).setCursor(keyScanCursor.getCursor()).setLimit(dataTableResult.getLimit()).setiTotalDisplayRecords(Integer.parseInt(displayParams.getDbSize().get(String.valueOf(dataTableResult.getDb())))).setiTotalRecords(Integer.parseInt(displayParams.getDbSize().get(String.valueOf(dataTableResult.getDb())))).setDb(dataTableResult.getDb()).setDraw(dataTableResult.getDraw());
    }

    @Override
    public List<String> keys(int db, String match) {
        if (db != 0) return null;
        List<String> result = new ArrayList<>();
        ScanArgs scanArgs = ScanArgs.Builder.limit(10000);
        scanArgs.match(match);
        ScanCursor cursor = new ScanCursor("0", false);
        do {
            KeyScanCursor keyScanCursor = redisCommands.scan(cursor, scanArgs);
            cursor.setCursor(keyScanCursor.getCursor());
            result.addAll(keyScanCursor.getKeys());
        }while (!cursor.getCursor().equals("0") && !cursor.isFinished());
        return result;
    }

    @Override
    public RedisObject get(int db, String key) {
        if (db != 0) return null;
        Object obj = null;
        DataType dataType = DataType.valueOf(this.type(0, key).toUpperCase());
        switch (dataType) {
            case STRING:
                obj = this.redisCommands.get(key);
                break;
            case HASH:
                obj = this.redisCommands.hgetall(key);
                break;
            case SET:
                obj = this.redisCommands.smembers(key);
                break;
            case LIST:
                obj = this.redisCommands.lrange(key, 0, -1);
                break;
            case ZSET:
                obj = this.redisCommands.zrange(key, 0, -1);
                break;
        }
        return new RedisObject(key, obj, dataType, redisCommands.ttl(key));
    }

    @Override
    public boolean exist(int db, String key) {
        if (db != 0) return false;
        long result = redisCommands.exists(key);
        return result != 0;
    }

    @Override
    public void select(int db) throws CommandUnsupportedException{
        throw new CommandUnsupportedException("This is a cluster node! Can not use select command!");
    }

    @Override
    public String type(int db, String key) {
        if (db != 0) return "none";
        return this.redisCommands.type(key);
    }

    @Override
    public Map<String, String> getConfig(String configName) {
        return redisCommands.configGet(configName);
    }

    @Override
    public synchronized void renameKey(int db, String oldKey, String newKey) {
        if (db != 0) return;
        redisCommands.rename(oldKey, newKey);
    }

    @Override
    public synchronized String stringSet(int db, String key, String value) {
        if (db != 0) return null;
        return redisCommands.set(key, value);
    }

    @Override
    public synchronized String hashSet(int db, String key, Map<String, Object> value) {
        if (db != 0) return null;
        return redisCommands.hmset(key, value);
    }

    @Override
    public synchronized Long listSet(int db, String key, Object[] value) {
        if (db != 0) return null;
        return redisCommands.lpush(key, value);
    }

    @Override
    public synchronized Long setSet(int db, String key, Set<Object> value) {
        if (db != 0) return null;
        return redisCommands.sadd(key, value.toArray());
    }

    @Override
    public synchronized Long zsetSet(int db, String key, LinkedHashSet<Object> value) {
        if (db != 0) return null;
        return redisCommands.zadd(key, value.toArray());
    }

    @Override
    public synchronized Boolean deleteKey(int db, String... keys) {
        if (db != 0) return null;
        Long result = redisCommands.del(keys);
        return result > 0;
    }
}
