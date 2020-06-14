package per.cxy.cedis.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import per.cxy.cedis.handler.AccessInterceptor;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/13 10:00
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String[] EXCLUDE_PATH = new String[]{"/login", "/assets/**", "/js/**", "/html/**", "/css/**"};

    @Autowired
    private AccessInterceptor interceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATH);
    }
}