package per.cxy.cedis.controller;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cxy.cedis.model.Message;
import per.cxy.cedis.service.ExecutorService;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/3 21:53
 */
@RestController
@RequestMapping("/v1/monitor")
public class MonitorController {
    private static final Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private ExecutorService executorService;

    @GetMapping("/{name}")
    public JSONObject getMonitorByName(@PathVariable String name) {
        if (name == null) return Message.getNullValueMessage().toJson();
        return executorService.getMonitorByName(name);
    }

    @GetMapping("/refreshDB/{name}")
    public JSONObject getMonitorAndRefreshDB(@PathVariable String name) {
        if (name == null) return Message.getNullValueMessage().toJson();
        return executorService.getMonitorAndRefreshDB(name);
    }
}
