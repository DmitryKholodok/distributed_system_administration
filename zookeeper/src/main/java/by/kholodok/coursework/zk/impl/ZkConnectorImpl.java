package by.kholodok.coursework.zk.impl;

import by.kholodok.coursework.zk.ZkConnector;
import by.kholodok.coursework.zk.ZnodeMonitor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dmitrykholodok on 12/3/17
 */

public class ZkConnectorImpl implements ZkConnector, Watcher {

    private static final Logger LOGGER = LogManager.getLogger(ZkConnectorImpl.class);

    private static final int ZK_DEFAULT_SESSION_TIMEOUT = 5000;

    private ZooKeeper zk;
    private CountDownLatch connSignal = new CountDownLatch(1);
    private ZnodeMonitor znodeMonitor;

    public ZkConnectorImpl(ZnodeMonitor znodeMonitor) {
        this.znodeMonitor = znodeMonitor;
    }

    @Override
    public ZooKeeper connectToZk(String host) {
        return connectToZk(host, ZK_DEFAULT_SESSION_TIMEOUT);
    }

    @Override
    public ZooKeeper connectToZk(String host, int sessionTimeout) {
        try {
            LOGGER.log(Level.INFO, "Connecting to the " + host + " . . .");
            zk = new ZooKeeper(host, sessionTimeout, this);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
        }
        try {
            connSignal.await();
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
        }
        LOGGER.log(Level.DEBUG, "Connected to the " + host);
        return zk;
    }

    @Override
    public void close() {
        try {
            LOGGER.log(Level.INFO, "Closing zk connection");
            if (zk != null) {
                zk.close();
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected){
            connSignal.countDown(); // one, two times -> will be error or not
        } else {
            znodeMonitor.process(event);
        }

        if (znodeMonitor.isDead()) {
            LOGGER.log(Level.INFO, "The connection was lost!");
            close();
        }
    }

}
