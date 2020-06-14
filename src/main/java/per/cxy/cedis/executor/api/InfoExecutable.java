package per.cxy.cedis.executor.api;

import per.cxy.cedis.exceptions.CommandUnsupportedException;

import java.util.Collection;

/**
 * @author Xinyuan, Chen
 * @date 2020/5/29 20:44
 */
public interface InfoExecutable<K, V> {
    boolean isCluster();

    boolean isOpen();

    String getName();

    String getInfo();

    Collection getNodes();

    String clusterInfo() throws CommandUnsupportedException;

    String clusterNodes() throws CommandUnsupportedException;
}
