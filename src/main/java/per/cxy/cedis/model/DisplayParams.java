package per.cxy.cedis.model;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/31 9:42
 */
public class DisplayParams{

    private ClientParams clientParams;
    private boolean open = false;
    private boolean cluster = false;
    private int nodeSize = 1;
    private Map<String, String> dbSize = new HashMap<>();
    private int dbSizeSum = 0;
    private int clients = 0;
    private int databases = 0;
    private String redisVersion = "";
    private String clientInfo = "";
    private String clientConfig= "";
    private String clusterInfo = "";
    private String clusterNodes = "";

    public DisplayParams(ClientParams clientParams) {
        this.clientParams = clientParams.clone();
    }

    public JSONObject toJson() {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[]{"clientParams"});
        JSONObject jsonObject = JSONObject.fromObject(this, jsonConfig);
        jsonObject.put("clientParams", clientParams.toJson());
        return jsonObject;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public ClientParams getClientParams() {
        return clientParams;
    }

    public void setClientParams(ClientParams clientParams) {
        this.clientParams = clientParams;
    }

    public String getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(String clientConfig) {
        this.clientConfig = clientConfig;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getClusterInfo() {
        return clusterInfo;
    }

    public void setClusterInfo(String clusterInfo) {
        this.clusterInfo = clusterInfo;
    }

    public Map<String, String> getDbSize() {
        return dbSize;
    }

    public void putDbSize(String db, String size) {
        this.dbSize.put(db, size);
    }

    public int getDbSizeSum() {
        return dbSizeSum;
    }

    public void setDbSizeSum(int dbSizeSum) {
        this.dbSizeSum = dbSizeSum;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }


    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    public String getRedisVersion() {
        return redisVersion;
    }

    public void setRedisVersion(String redisVersion) {
        this.redisVersion = redisVersion;
    }

    public int getDatabases() {
        return databases;
    }

    public void setDatabases(int databases) {
        this.databases = databases;
    }
}
