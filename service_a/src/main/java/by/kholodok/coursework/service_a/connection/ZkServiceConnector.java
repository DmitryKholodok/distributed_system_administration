package by.kholodok.coursework.service_a.connection;

import by.kholodok.coursework.exception.NoServiceFoundException;
import by.kholodok.coursework.observer.ZNodeObserver;
import by.kholodok.coursework.observer.ZNodeObserverImpl;
import by.kholodok.coursework.zk.impl.ZNodeMonitorImpl;
import by.kholodok.coursework.zk.impl.ZkConnectorImpl;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by dmitrykholodok on 12/11/17
 */

@Component
@Scope(value = "singleton")
public class ZkServiceConnector {

    private static final String OBSERVER_SERVICE_NAME = "service_b";
    private static final String CURRENT_SERVICE_NAME = "service_a";
    private static final String ZK_HOST_PORT = "localhost:2181";

    private ZkConnectorImpl zkConnector;
    private ZNodeMonitorImpl zNodeMonitor;
    private ZNodeObserver zNodeObserver = new ZNodeObserverImpl();

    @PostConstruct
    public void init() {
        zkConnector = new ZkConnectorImpl();
        ZooKeeper zk = zkConnector.connectToZk(ZK_HOST_PORT);
        zNodeMonitor = new ZNodeMonitorImpl(zk);

        // HOW TO DEFINE CURRENT HOST:PORT in a real time???
        byte[] currHostPort = new String("localhost:8080").getBytes();

        zNodeMonitor.createServiceZnode(CURRENT_SERVICE_NAME, "name", currHostPort);
        try {
            zNodeMonitor.addObserverToService(OBSERVER_SERVICE_NAME, zNodeObserver);
        } catch (NoServiceFoundException e) {
            throw new RuntimeException("Service B was not found!");
        }
    }

    public ZNodeObserver getzNodeObserver() {
        return zNodeObserver;
    }
}
