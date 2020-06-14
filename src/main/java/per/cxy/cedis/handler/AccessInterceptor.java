package per.cxy.cedis.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/13 10:01
 */
@Service
public class AccessInterceptor  extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            request.changeSessionId();
            return true;
        }else {
            response.sendRedirect("/login");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private String getCookieValue(HttpServletRequest request, HttpServletResponse response, String cookieName) {

        return null;
    }
}