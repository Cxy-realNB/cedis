package per.cxy.cedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import per.cxy.cedis.executor.ExecutorFactory;
import per.cxy.cedis.utils.CustomizedEnvironment;


@SpringBootApplication
@ComponentScan(basePackages={"per.cxy.cedis.controller", "per.cxy.cedis.service", "per.cxy.cedis.configuration", "per.cxy.cedis.utils", "per.cxy.cedis.handler"})
public class CedisApplication {
    private static final Logger logger = LoggerFactory.getLogger(CedisApplication.class);
    public static ConfigurableApplicationContext applicationContext;
    public static void main(String[] args) {
        applicationContext = SpringApplication.run(CedisApplication.class, args);
        CustomizedEnvironment customizedEnvironment = applicationContext.getBean(CustomizedEnvironment.class);
        ExecutorFactory.initialConnections(customizedEnvironment.getConnectionsFilePath());
    }
}
