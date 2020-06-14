package per.cxy.cedis.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cxy.cedis.exceptions.WrongConfigException;
import per.cxy.cedis.model.ClientParams;
import per.cxy.cedis.model.Message;
import per.cxy.cedis.model.vo.ShiftStateVO;
import per.cxy.cedis.service.ExecutorService;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 21:05
 */

@RestController
@RequestMapping("/v1/executor")
public class ExecutorController {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorController.class);

    @Autowired
    private ExecutorService executorService;

    @GetMapping("/{name}")
    public JSONObject getExecutorByName(@PathVariable String name) {
        if (name == null) return Message.getNullValueMessage().toJson();
        return executorService.getExecutor(name);
    }

    @GetMapping("/clientParams")
    public JSONArray getClientParams() {
        return executorService.getClientParams();
    }

    @GetMapping("/list")
    public JSONArray getExecutorList() {
        return executorService.getExecutorList();
    }

    @GetMapping("/state/{name}")
    public Message getState(@PathVariable String name) {
        if (name == null) return Message.getNullValueMessage();;
        return executorService.getState(name);
    }

    @PostMapping("/")
    public Message createExecutor(@RequestBody ClientParams connection) {
        if (connection.getName() == null && connection.getHost() == null) return Message.getNullValueMessage();
        connection.toRedisUri();
        return executorService.createExecutor(connection);
    }

    @PostMapping("/list")
    public Message createExecutors(@RequestBody String json) {
        if (json == null) return Message.getNullValueMessage();
        return executorService.createExecutors(JSONArray.fromObject(json), true);
    }

    @PostMapping("/list/closed")
    public Message createExecutorsClosed(@RequestBody String json) {
        if (json == null) return Message.getNullValueMessage();
        return executorService.createExecutors(JSONArray.fromObject(json), false);
    }

    @PutMapping("/state")
    public Message shiftState(@RequestBody ShiftStateVO shiftStateVO) {
        if (!shiftStateVO.noEmptyField()) return Message.getNullValueMessage();
        return executorService.shiftState(shiftStateVO.state(), shiftStateVO.getName());
    }

    @PutMapping("/clientParams")
    public Message persistConnection() throws WrongConfigException {
        return executorService.persistConnection();
    }

    @DeleteMapping("/{name}")
    public Message deleteExecutor(@PathVariable String name) {
        if (name == null) return Message.getNullValueMessage();
        return executorService.deleteExecutor(name);
    }

}
