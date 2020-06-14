package per.cxy.cedis.model;

import net.sf.json.JSONObject;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/31 8:25
 */
public class Message {
    private String message;
    private boolean success = true;

    public static Message getMessage(String message) {
        return new Message(message);
    }

    public static Message getMessage(String message, boolean success) {
        return new Message(message, success);
    }

    private Message(String message) {
        this.message = message;
    }

    private Message(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static Message getSuccessfulMessage() {
        return new Message("Success!");
    }

    public static Message getNullValueMessage() {
        return new Message("NULL, check params please!", false);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject toJson() {
        return JSONObject.fromObject(this);
    }
}
