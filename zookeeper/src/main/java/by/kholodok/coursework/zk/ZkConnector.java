package by.kholodok.coursework.zk;

import org.apache.zookeeper.ZooKeeper;

import java.io.Serializable;

/**
 * Created by dmitrykholodok on 12/3/17
 */

public interface ZkConnector extends AutoCloseable, Serializable {

    ZooKeeper connectToZk(String host, int port); // null - if bad
    ZooKeeper connectToZk(String host);
//    void close();

}
