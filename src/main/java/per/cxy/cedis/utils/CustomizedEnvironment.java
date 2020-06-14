package per.cxy.cedis.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/30 7:46
 */
@Component
public class CustomizedEnvironment {

    @Value("${redis.client.connection.timeout}")
    private int connectionTimeout;

    @Value("${server.username}")
    private String username;

    @Value("${server.password}")
    private String password;

    @Value("${initial.connections.filePath}")
    private String connectionsFilePath;

    public String getConnectionsFilePath() {
        return connectionsFilePath;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Duration getConnectionTimeout() {
        return Duration.ofSeconds(connectionTimeout);
    }
}
