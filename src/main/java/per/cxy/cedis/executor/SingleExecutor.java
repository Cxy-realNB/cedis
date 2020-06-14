package per.cxy.cedis.executor;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:46
 */
public class SingleExecutor<K extends String, V> implements BaseExecutable<String, Object> {

    private static final Logger logger = LoggerFactory.getLogger(SingleExecutor.class);

    private String name;
    private boolean open;
    private DisplayParams displayParams;
    private List<RedisURI> node;
    private RedisClient redisClient;
    private StatefulRedisConnection statefulRedisConnection;
    private RedisCommands<String, Object> redisCommands;


    SingleExecutor(ClientParams clientParams) {
        this(clientParams, true);
    }

    SingleExecutor(ClientParams clientParams, boolean open) {
        this.open = open;
        this.displayParams = new DisplayParams(clientParams);
        this.name = clientParams.getName();
        this.node = Collections.singletonList(clientParams.getRedisURI());
        this.redisClient = RedisClient.create(node.get(0));
        if (open) {
            this.statefulRedisConnection = this.redisClient.connect();
            this.redisCommands = this.statefulRedisConnection.sync();
            this.redisCommands.setTimeout(Constant.COMMAND_TIMEOUT);
            getCurrentDisplayParams();
        }
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
        return this.node;
    }

    @Override
    public String clusterInfo() throws CommandUnsupportedException {
        try {
            this.redisCommands.clusterInfo();
            throw new CommandUnsupportedException("This is a cluster node.");
        } catch (RedisCommandExecutionException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String clusterNodes() throws CommandUnsupportedException{
        try {
            this.redisCommands.clusterNodes();
            throw new CommandUnsupportedException("This is a cluster node.");
        }catch (RedisCommandExecutionException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public synchronized void open() throws CommandUnsupportedException {
        if (!open) {
            this.statefulRedisConnection = this.redisClient.connect();
            this.redisCommands = this.statefulRedisConnection.sync();
            this.open = true;
            this.clusterInfo();
            getCurrentDisplayParams();
        }
    }

    @Override
    public synchronized void close(){
        if (open) {
            this.statefulRedisConnection.close();
            this.open = false;
            this.displayParams.setOpen(false);
        }
    }

    @Override
    public boolean isCluster() {
        try {
            this.redisCommands.clusterNodes();
        } catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public DisplayParams getDisplayParams() {
        return this.displayParams;
    }

    @Override
    public synchronized void getCurrentDisplayParams() {
        this.displayParams.setOpen(true);
        this.displayParams.setClientInfo(this.redisCommands.info());
        this.displayParams.setClients(Integer.parseInt(this.redisCommands.info(Constant.INFO_CLIENT).split("\n", 3)[1].trim().split(":")[1]));
        this.displayParams.setRedisVersion(this.redisCommands.info(Constant.INFO_SERVER).split("\n", 3)[1].trim().split(":")[1]);
        this.displayParams.setDatabases(Integer.parseInt((String) this.redisCommands.configGet(Constant.CONFIG_DATABASES).get(Constant.CONFIG_DATABASES)));
        int sum = 0;
        if (this.displayParams.getClientConfig().equals("")) this.displayParams.setClientConfig(JSONObject.fromObject(this.redisCommands.configGet("*")).toString());
        for (int i = 0; i < this.displayParams.getDatabases(); i++) {
            this.select(i);
            int dbSize = new Long(this.redisCommands.dbsize()).intValue();
            sum += dbSize;
            this.displayParams.putDbSize(String.valueOf(i), String.valueOf(dbSize));
        }
        this.displayParams.setDbSizeSum(sum);
    }

    @Override
    public synchronized void getCurrentDatabases() {
        int sum = 0;
        for (int i = 0; i < this.displayParams.getDatabases(); i++) {
            this.select(i);
            int dbSize = new Long(this.redisCommands.dbsize()).intValue();
            sum += dbSize;
            this.displayParams.putDbSize(String.valueOf(i), String.valueOf(dbSize));
        }
        this.displayParams.setDbSizeSum(sum);
    }

    @Override
    public synchronized List<String> keys(int db) {
        return keys(db, "*");
    }

    @Override
    public synchronized List<String> keys() {
        return this.keys(0);
    }

    @Override
    public synchronized DataTableResult keys(DataTableResult dataTableResult) {
        this.select(dataTableResult.getDb());
        ScanArgs scanArgs = ScanArgs.Builder.limit(dataTableResult.getLimit());
        ScanCursor scanCursor = new ScanCursor(dataTableResult.getCursor(), false);
        KeyScanCursor keyScanCursor = redisCommands.scan(scanCursor, scanArgs);
        scanCursor.setCursor(keyScanCursor.getCursor());
        return DataTableResult.Builder.build(keyScanCursor.getKeys()).setCursor(keyScanCursor.getCursor()).setLimit(dataTableResult.getLimit()).setiTotalDisplayRecords(Integer.parseInt(displayParams.getDbSize().get(String.valueOf(dataTableResult.getDb())))).setiTotalRecords(Integer.parseInt(displayParams.getDbSize().get(String.valueOf(dataTableResult.getDb())))).setDb(dataTableResult.getDb()).setDraw(dataTableResult.getDraw());
    }

    @Override
    public synchronized List<String> keys(int db, String match) {
        this.select(db);
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
    public synchronized RedisObject get(int db, String key) {
        this.select(db);
        Object obj = null;
        DataType dataType = DataType.valueOf(this.type(db, key).toUpperCase());
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
    public synchronized String type(int db, String key) {
        this.select(db);
        return this.redisCommands.type(key);
    }

    @Override
    public synchronized boolean exist(int db, String key) {
        this.select(db);
        long result = redisCommands.exists(key);
        return result != 0;
    }

    @Override
    public synchronized void select(int db) {
        redisCommands.select(db);
    }

    @Override
    public Map<String, String> getConfig(String configName) {
        return redisCommands.configGet(configName);
    }

    @Override
    public synchronized void renameKey(int db, String oldKey, String newKey) {
        this.select(db);
        redisCommands.rename(oldKey, newKey);
    }

    @Override
    public synchronized String stringSet(int db, String key, String value) {
        this.select(db);
        return redisCommands.set(key, value);
    }

    @Override
    public synchronized String hashSet(int db, String key, Map<String, Object> value) {
        this.select(db);
        return redisCommands.hmset(key, value);
    }

    @Override
    public synchronized Long listSet(int db, String key, Object[] value) {
        this.select(db);
        return redisCommands.lpush(key, value);
    }

    @Override
    public synchronized Long setSet(int db, String key, Set<Object> value) {
        this.select(db);
        return redisCommands.sadd(key, value.toArray());
    }

    @Override
    public synchronized Long zsetSet(int db, String key, LinkedHashSet<Object> value) {
        this.select(db);
        return redisCommands.zadd(key, value.toArray());
    }

    @Override
    public synchronized Boolean deleteKey(int db, String... keys) {
        this.select(db);
        Long result = redisCommands.del(keys);
        return result > 0;
    }
}
