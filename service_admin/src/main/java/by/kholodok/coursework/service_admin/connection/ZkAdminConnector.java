package by.kholodok.coursework.service_admin.connection;

import by.kholodok.coursework.zk.ZkConnector;
import by.kholodok.coursework.zk.impl.ZNodeMonitorImpl;
import by.kholodok.coursework.zk.impl.ZkConnectorImpl;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dmitrykholodok on 12/11/17
 */

@Component
public class ZkAdminConnector {

    private static final String ZK_HOST_PORT = "localhost:2181";

    private ZkConnector zkConnector;
    private ZNodeMonitorImpl zNodeMonitor;
    private ZooKeeper zooKeeper;

    @PostConstruct
    public void init() {
        zkConnector = new ZkConnectorImpl();
        zooKeeper= zkConnector.connectToZk(ZK_HOST_PORT);
        zNodeMonitor = new ZNodeMonitorImpl(zooKeeper);
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

}
