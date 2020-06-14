package per.cxy.cedis.model.vo;

import per.cxy.cedis.inspector.EmptyInspectable;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/7 14:17
 */
public class DeleteKeysVO implements EmptyInspectable {
    private String name;
    private Integer db;
    private String[] keys;

    public DeleteKeysVO(){

    }

    public DeleteKeysVO(String name, Integer db, String[] keys){
        this.name = name;
        this.db = db;
        this.keys = keys;
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

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    @Override
    public boolean noEmptyField() {
        return name != null && db != null && keys != null;
    }
}
