package per.cxy.cedis.model.vo;

import per.cxy.cedis.inspector.EmptyInspectable;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/7 10:12
 */
public class ShiftStateVO implements EmptyInspectable {
    private String name;
    private Boolean state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean state() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public boolean noEmptyField() {
        return name != null && state != null;
    }
}
