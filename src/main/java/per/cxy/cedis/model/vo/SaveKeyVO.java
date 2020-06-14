package per.cxy.cedis.model.vo;

import per.cxy.cedis.inspector.EmptyInspectable;
import per.cxy.cedis.utils.DataType;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/7 14:17
 */
public class SaveKeyVO implements EmptyInspectable {
    private String key;
    private String value;
    private DataType type;
    private String name;
    private Integer db;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

    @Override
    public boolean noEmptyField() {
        return key != null && value != null && type != null && name != null && db != null;
    }
}
