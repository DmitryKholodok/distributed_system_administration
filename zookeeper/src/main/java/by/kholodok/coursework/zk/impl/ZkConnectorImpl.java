package by.kholodok.coursework.zk.impl;

import by.kholodok.coursework.zk.ZNodeMonitor;
import by.kholodok.coursework.zk.ZkConnector;
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
    private final CountDownLatch connSignal = new CountDownLatch(1);

    private ZooKeeper zk;
    private ZNodeMonitor zNodeMonitor;

    public ZkConnectorImpl() {
    }

    public void setZNodeMonitor(ZNodeMonitor zNodeMonitor) {
        this.zNodeMonitor = zNodeMonitor;
    }

    @Override
    public ZooKeeper connectToZk(String host) {
        return connectToZk(host, ZK_DEFAULT_SESSION_TIMEOUT);
    }

    @Override
    public ZooKeeper connectToZk(String host, int sessionTimeout) {
        LOGGER.log(Level.INFO, "Connecting to the " + host + " . . .");
        try {
            zk = new ZooKeeper(host, sessionTimeout, this);
            connSignal.await();
        } catch (InterruptedException | IOException e) {
            LOGGER.log(Level.ERROR, e);
        }
        LOGGER.log(Level.DEBUG, "Connected to the " + host);
        return zk;
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected){
            connSignal.countDown(); // one, two times -> will be error or not
        } else {
            if (zNodeMonitor != null)
                zNodeMonitor.process(event);
        }
    }

    @Override
    public void close() {
        LOGGER.log(Level.INFO, "Closing zookeeper connection");
        try {
            if (zk != null) {
                zk.close();
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
        }
    }

}
