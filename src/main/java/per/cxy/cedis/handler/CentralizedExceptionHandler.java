package per.cxy.cedis.handler;

import io.lettuce.core.RedisCommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import per.cxy.cedis.controller.ExecutorController;
import per.cxy.cedis.exceptions.WrongConfigException;
import per.cxy.cedis.model.Message;


/**
 * @author Xinyuan, Chen
 * @date 2020/6/13 9:01
 */
@RestControllerAdvice
public class CentralizedExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorController.class);

    @ResponseBody
    @ExceptionHandler({RedisCommandExecutionException.class})
    public Message dealCommandException(RedisCommandExecutionException e) {
        logger.error(e.getMessage());
        return Message.getMessage(e.getMessage(), false);
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    public Message commonException(Exception e) {
        logger.error(e.getMessage());
        return Message.getMessage(e.getMessage(), false);
    }

    @ResponseBody
    @ExceptionHandler({WrongConfigException.class})
    public Message commonException(WrongConfigException e) {
        logger.error(e.getMessage());
        return Message.getMessage(e.getMessage(), false);
    }
}
