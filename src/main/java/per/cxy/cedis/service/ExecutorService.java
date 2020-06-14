package per.cxy.cedis.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import per.cxy.cedis.CedisApplication;
import per.cxy.cedis.exceptions.WrongConfigException;
import per.cxy.cedis.executor.ExecutorContainer;
import per.cxy.cedis.executor.ExecutorFactory;
import per.cxy.cedis.executor.api.BaseExecutable;
import per.cxy.cedis.model.ClientParams;
import per.cxy.cedis.model.DisplayParams;
import per.cxy.cedis.model.Message;
import per.cxy.cedis.utils.CustomizedEnvironment;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 21:24
 */

@Service
public class ExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorService.class);

    public JSONObject getExecutor(String name) {
        BaseExecutable baseExecutable = ExecutorContainer.get(name);
        return baseExecutable == null ? null : baseExecutable.getDisplayParams().toJson();
    }

    public JSONArray getExecutorList() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(ExecutorContainer.getAll().stream().map(t -> t.getDisplayParams().toJson()).collect(Collectors.toList()));
        return jsonArray;
    }

    public JSONArray getClientParams() {
        JSONArray jsonArray = new JSONArray();
        Collection<BaseExecutable> a = ExecutorContainer.getAll();
        ExecutorContainer.getAll().forEach(t -> jsonArray.add(t.getDisplayParams().getClientParams().toJson()));
        return jsonArray;
    }

    public Message getState(String name) {
        Boolean bool = ExecutorContainer.isOpen(name);
        Message result;
        if (bool != null) {
            result = Message.getMessage(bool.toString());
        }else {
            result = Message.getMessage(name + "connection not found!", false);
        }
        return result;
    }

    public Message createExecutor(ClientParams clientParams) {
        Message returnMsg;
        BaseExecutable result = ExecutorFactory.createExecutor(clientParams, false);
        returnMsg = result == null ? Message.getMessage("Failed, this name is occupied, change your connection name.", false) : Message.getMessage("Success!");
        return returnMsg;
    }

    public Message createExecutors(JSONArray connections, boolean open) {
        JSONObject message = new JSONObject();
        connections.forEach(t -> {
            JSONObject jsonObject = JSONObject.fromObject(t);
            ClientParams clientParams = (ClientParams) JSONObject.toBean(jsonObject, ClientParams.class);
            if (jsonObject.getString("auth") != null) clientParams.setAuth(jsonObject.getString("auth").toCharArray());
            logger.info("{} is creating! Host: {}, Port:{}", clientParams.getName(), clientParams.getHost(), clientParams.getPort());
            clientParams.toRedisUri();
            BaseExecutable result = null;
            result = ExecutorFactory.createExecutor(clientParams, open);
            if (result == null) {
                message.put(clientParams.getName(), "Failed, this name is occupied, change your connection name.");
            }else {
                message.put(clientParams.getName(), "Success!");
            }
        });
        return Message.getMessage(message.toString());
    }

    public Message deleteExecutor(String name) {
        ExecutorContainer.remove(name);
        return Message.getSuccessfulMessage();
    }

    public Message shiftState(boolean state, String name) {
        Message result;
        if (state) ExecutorContainer.open(name);
        else ExecutorContainer.close(name);
        result = Message.getSuccessfulMessage();
        return result;
    }

    // monitor api below
    public JSONObject getMonitorByName(String name) {
        ExecutorContainer.get(name).getCurrentDisplayParams();
        DisplayParams displayParams = ExecutorContainer.get(name).getDisplayParams();
        return displayParams.toJson();
    }

    public JSONObject getMonitorAndRefreshDB(String name) {
        ExecutorContainer.get(name).getCurrentDatabases();
        DisplayParams displayParams = ExecutorContainer.get(name).getDisplayParams();
        return displayParams.toJson();
    }

    public Message persistConnection() throws WrongConfigException {
        CustomizedEnvironment customizedEnvironment = CedisApplication.applicationContext.getBean(CustomizedEnvironment.class);
        String filePath = customizedEnvironment.getConnectionsFilePath();
        if (StringUtils.isBlank(filePath)) throw new WrongConfigException("No file path found!");
        else return Message.getMessage(ExecutorFactory.persistConnection(filePath));
    }
}
