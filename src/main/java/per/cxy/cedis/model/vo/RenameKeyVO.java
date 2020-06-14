package per.cxy.cedis.model.vo;

import per.cxy.cedis.inspector.EmptyInspectable;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/7 10:11
 */
public class RenameKeyVO implements EmptyInspectable {
    private String name;
    private Integer db;
    private String oldKey;
    private String newKey;

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

    public String getOldKey() {
        return oldKey;
    }

    public void setOldKey(String oldKey) {
        this.oldKey = oldKey;
    }

    public String getNewKey() {
        return newKey;
    }

    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }

    @Override
    public boolean noEmptyField() {
        return name != null && db != null && oldKey != null && newKey != null;
    }
}
