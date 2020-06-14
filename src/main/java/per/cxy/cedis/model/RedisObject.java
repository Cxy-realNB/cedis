package per.cxy.cedis.model;

import per.cxy.cedis.utils.DataType;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/7 8:10
 */
public class RedisObject {
    private String key;
    private Object value;
    private DataType dataType;
    private Long ttl;

    public RedisObject() {

    }

    public RedisObject(String key, Object value, DataType dataType) {
        this.key = key;
        this.dataType = dataType;
        this.value = value;
        this.ttl = new Long(-1);
    }

    public RedisObject(String key, Object value, DataType dataType, Long ttl) {
        this.key = key;
        this.dataType = dataType;
        this.value = value;
        this.ttl = ttl;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
