package per.cxy.cedis.exceptions;

import io.lettuce.core.RedisCommandExecutionException;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/7 11:37
 */
public class CommandUnsupportedException extends RedisCommandExecutionException {
    public CommandUnsupportedException(String msg) {
        super(msg);
    }
}
