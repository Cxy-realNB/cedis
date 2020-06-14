package per.cxy.cedis.model;

import io.lettuce.core.ScanArgs;

import java.util.List;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/5 20:18
 */
public class DataTableResult {
    private String cursor = "0";
    private String name = "";
    private int limit = 0;
    private int db = 0;
    private int iTotalDisplayRecords = 0;
    private int iTotalRecords = 0;
    private int draw = 1;
    private List<String> data;

    private DataTableResult(){

    }

    public int getDb() {
        return db;
    }

    public DataTableResult setDb(int db) {
        this.db = db;
        return this;
    }

    public String getName() {
        return name;
    }

    public DataTableResult setName(String name) {
        this.name = name;
        return this;
    }

    public String getCursor() {
        return cursor;
    }

    public DataTableResult setCursor(String cursor) {
        this.cursor = cursor;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public DataTableResult setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public DataTableResult setiTotalDisplayRecords(int iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        return this;
    }

    public int getiTotalRecords() {
        return iTotalRecords;
    }

    public DataTableResult setiTotalRecords(int iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
        return this;
    }

    public int getDraw() {
        return draw;
    }

    public DataTableResult setDraw(int draw) {
        this.draw = draw;
        return this;
    }

    public List<String> getData() {
        return data;
    }

    public DataTableResult setData(List<String> data) {
        this.data = data;
        return this;
    }

    public static class Builder {
        private Builder() {
        }

        public static DataTableResult build(List<String> data) {
            return new DataTableResult().setData(data);
        }
    }
}
