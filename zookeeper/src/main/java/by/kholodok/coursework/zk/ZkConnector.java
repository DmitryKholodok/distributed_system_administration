package by.kholodok.coursework.zk;

import org.apache.zookeeper.ZooKeeper;

/**
 * Created by dmitrykholodok on 12/3/17
 */

public interface ZkConnector extends AutoCloseable {

    ZooKeeper connectToZk(String host, int port); // null - if bad
    ZooKeeper connectToZk(String host);
//    void close();

}
