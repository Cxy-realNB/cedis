package per.cxy.cedis.utils;

import java.time.Duration;

/**
 * @author Xinyuan, Chen
 * @date 2020/6/1 21:52
 */
public class Constant {
    // config
    public static final String CONFIG_DATABASES = "databases";

    // info
    public static final String INFO_SERVER = "Server";
    public static final String INFO_CLIENT = "Clients";
    public static final String INFO_MEMORY = "Memory";
    public static final String INFO_PERSISTENCE = "Persistence";
    public static final String INFO_STATS = "Stats";
    public static final String INFO_REPLICATION = "Replication";
    public static final String INFO_CPU = "CPU";
    public static final String INFO_CLUSTER = "Cluster";
    public static final String INFO_KEYSPACE = "Keyspace";

    // redis command time out config
    public static final Duration COMMAND_TIMEOUT = Duration.ofSeconds(10);

}
