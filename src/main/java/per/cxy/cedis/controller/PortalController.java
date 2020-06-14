package per.cxy.cedis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import per.cxy.cedis.model.Message;
import per.cxy.cedis.model.User;
import per.cxy.cedis.utils.CustomizedEnvironment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/30 7:11
 */
@Controller
public class PortalController {
    private static final Logger logger = LoggerFactory.getLogger(PortalController.class);

    @Autowired
    private CustomizedEnvironment customizedEnvironment;

    @GetMapping("/")
    public String connection() {
        return "/html/connections/connections.html";
    }

    @GetMapping("/monitor")
    public String monitor() {
        return "/html/monitor/monitor.html";
    }

    @GetMapping("/management")
    public String management(String name, HttpServletResponse response) {
        return "/html/connections/management.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/html/login/login.html";
    }

    @ResponseBody
    @PostMapping("/login")
    public Message login(@RequestBody User user, HttpServletRequest request) {
        if (customizedEnvironment.getUsername().equals(user.getUsername()) && customizedEnvironment.getPassword().equals(user.getPassword())) {
            request.getSession();
            return Message.getMessage("/", true);
        }
        return Message.getMessage("Wrong username or password!", false);
    }
}
