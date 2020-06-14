package per.cxy.cedis.model;

import io.lettuce.core.RedisURI;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import per.cxy.cedis.CedisApplication;
import per.cxy.cedis.utils.CustomizedEnvironment;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:52
 */
public class ClientParams implements Cloneable {
    private String name;
    private String host;
    private int port;
    private char[] auth;
    private RedisURI redisURI;

    public ClientParams() {
    }

    public ClientParams(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.auth = "".toCharArray();
        this.toRedisUri();
    }

    public ClientParams(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");
        this.host = jsonObject.getString("host");
        this.port = jsonObject.getInt("port");
        this.auth = jsonObject.getString("auth").toCharArray();
        this.toRedisUri();
    }

    public ClientParams(String name, String host, String port, String auth) {
        this.name = name;
        this.host = host;
        this.port = Integer.parseInt(port);
        this.auth = "".toCharArray();
        this.toRedisUri();
    }

    public ClientParams(String name, String host, int port, String auth) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.auth = auth.toCharArray();
        this.toRedisUri();
    }

    public RedisURI getRedisURI() {
        return redisURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAuth() {
        return this.auth != null ? String.valueOf(this.auth) : "";
    }

    public void setAuth(char[] auth) {
        this.auth = auth;
    }

    @Override
    public ClientParams clone() {
        ClientParams clientParams = new ClientParams();
        clientParams.setAuth(this.auth);
        clientParams.setHost(this.host);
        clientParams.setName(this.name);
        clientParams.setPort(this.port);
        return clientParams;
    }

    public JSONObject toJson() {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[]{"redisURI"});
        return JSONObject.fromObject(this, jsonConfig);
    }

    public void toRedisUri() {
        CustomizedEnvironment env = CedisApplication.applicationContext.getBean(CustomizedEnvironment.class);
        RedisURI redisURI = new RedisURI(this.host, this.port, env.getConnectionTimeout());
        if (auth != null && auth.length != 0) redisURI.setPassword(this.auth);
        this.redisURI = redisURI;
    }
}
