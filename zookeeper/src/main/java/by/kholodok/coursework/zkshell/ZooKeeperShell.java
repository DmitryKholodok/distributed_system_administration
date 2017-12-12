package by.kholodok.coursework.zkshell;

import by.kholodok.coursework.zkshell.entity.ZNodeServiceEntity;
import by.kholodok.coursework.zkshell.observer.RemoteServiceObserver;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * Created by dmitrykholodok on 12/3/17
 */

interface ZooKeeperShell extends AutoCloseable {
    ZooKeeper connectToZk(String host);
    ZooKeeper connectToZk(String host, int port);
    String createServiceZNode(String serviceName, byte[] zNodeData);
    boolean addObserverToService(String serviceName, RemoteServiceObserver observer);
    List<ZNodeServiceEntity> receiveWorkServicesInfo(List<String> serviceNameList);
}
