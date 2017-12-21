package by.kholodok.coursework.zkshell;

import by.kholodok.coursework.zkshell.entity.ServiceData;
import by.kholodok.coursework.zkshell.observer.RemoteServiceObserver;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * Created by dmitrykholodok on 12/3/17
 */

interface ZooKeeperShell {
    ZooKeeper connectToZk(String host);
    ZooKeeper connectToZk(String host, int port);
    String createServiceZNode(ServiceData serviceData);
    boolean addObserverToService(RemoteServiceObserver rso);
    List<ServiceData> receiveServicesData();
    boolean isDead();
    void close();
}
