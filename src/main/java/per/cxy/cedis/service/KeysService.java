package per.cxy.cedis.service;

import io.lettuce.core.RedisCommandExecutionException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import per.cxy.cedis.executor.ExecutorContainer;
import per.cxy.cedis.executor.api.BaseExecutable;
import per.cxy.cedis.model.DataTableResult;
import per.cxy.cedis.model.Message;
import per.cxy.cedis.model.RedisObject;
import per.cxy.cedis.model.vo.DeleteKeysVO;
import per.cxy.cedis.model.vo.RenameKeyVO;
import per.cxy.cedis.model.vo.SaveKeyVO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/1 21:33
 */
@Service
public class KeysService {
    private static final Logger logger = LoggerFactory.getLogger(KeysService.class);

    public List<Object> getAll(String connectionName, int db) {
        BaseExecutable executor = ExecutorContainer.get(connectionName);
        return executor.keys(db);
    }

    public List<Object> getAll(String connectionName, int db, String match) {
        BaseExecutable executor = ExecutorContainer.get(connectionName);
        return executor.keys(db, match);
    }

    public DataTableResult getAll(DataTableResult dataTableResult) {
        BaseExecutable executor = ExecutorContainer.get(dataTableResult.getName());
        if (dataTableResult.getLimit() == -1) {
            List<String> keys = executor.keys(dataTableResult.getDb());
            return DataTableResult.Builder.build(keys).setCursor("0").setLimit(dataTableResult.getLimit()).setiTotalDisplayRecords(keys.size()).setiTotalRecords(keys.size()).setDb(dataTableResult.getDb()).setDraw(dataTableResult.getDraw());
        }else {
            return executor.keys(dataTableResult);
        }
    }

    public RedisObject getValue(int db, String key, String name) {
        BaseExecutable executor = ExecutorContainer.get(name);
        return executor.get(db, key);
    }

    public Message renameKey(RenameKeyVO renameKeyVO) {
        Message result = null;
        BaseExecutable executor = ExecutorContainer.get(renameKeyVO.getName());
        boolean oldKeyExist = executor.exist(renameKeyVO.getDb(), renameKeyVO.getOldKey()), newKeyExist = executor.exist(renameKeyVO.getDb(), renameKeyVO.getNewKey());
        if (oldKeyExist && !newKeyExist) {
            executor.renameKey(renameKeyVO.getDb(), renameKeyVO.getOldKey(), renameKeyVO.getNewKey());
            result = Message.getSuccessfulMessage();
        }else {
            if (newKeyExist == true) result = Message.getMessage("New key is existed!", false);
            if (oldKeyExist == false) result = Message.getMessage("Old key is not existed!", false);
        }
        return result;
    }

    public Message addKey(SaveKeyVO saveKeyVO) {
        Message result = null;
        BaseExecutable executor = ExecutorContainer.get(saveKeyVO.getName());
        int db = saveKeyVO.getDb();
        String key = saveKeyVO.getKey(), value = saveKeyVO.getValue();
        try {
            switch (saveKeyVO.getType()) {
                case STRING:
                    result = Message.getMessage(executor.stringSet(db, key, value), true);
                    break;
                case HASH:
                    JSONObject hashValues = JSONObject.fromObject(value);
                    result = Message.getMessage(executor.hashSet(db, key, hashValues), true);
                    break;
                case LIST:
                    Object[] listValues = value.split(",");
                    result = Message.getMessage(String.valueOf(executor.listSet(db, key, listValues)), true);
                    break;
                case SET:
                    Object[] setValues = value.split(",");
                    Set<Object> set = new HashSet<>();
                    set.addAll(Arrays.asList(setValues));
                    result = Message.getMessage(String.valueOf(executor.setSet(db, key, set)), true);
                    break;
                case ZSET:
                    String[] strs = value.split(",");
                    Object[] zsetValues = new Object[strs.length];
                    if (zsetValues.length % 2 != 0) {
                        result = Message.getMessage("Value must be a multiple of 2 and contain a sequence of score1, value1, score2, value2, scoreN, valueN", false);
                        break;
                    }
                    for (int i = 0; i < zsetValues.length; i++) {
                        if (i % 2 == 0) {
                            zsetValues[i] = new Double(strs[i]);
                        }else {
                            zsetValues[i] = strs[i];
                        }
                    }
                    LinkedHashSet<Object> zset = new LinkedHashSet<>();
                    zset.addAll(Arrays.asList(zsetValues));
                    result = Message.getMessage(String.valueOf(executor.zsetSet(db, key, zset)), true);
                    break;
            }
        } catch (RedisCommandExecutionException e) {
            logger.error(e.getMessage());
            result = Message.getMessage(e.getMessage(), false);
        }
        return result;
    }

    public Message deleteKeys(DeleteKeysVO deleteKeysVO) {
        BaseExecutable executor = ExecutorContainer.get(deleteKeysVO.getName());
        boolean result = executor.deleteKey(deleteKeysVO.getDb(), deleteKeysVO.getKeys());
        return result ? Message.getSuccessfulMessage() : Message.getMessage("No keys found!", false);
    }

    public Message updateRedisObject(SaveKeyVO saveKeyVO) {
        Message msg = deleteKeys(new DeleteKeysVO(saveKeyVO.getName(), saveKeyVO.getDb(), new String[]{saveKeyVO.getKey()}));
        if (msg.isSuccess()) return addKey(saveKeyVO);
        return msg;
    }
}
