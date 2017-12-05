package by.kholodok.coursework.zk.impl;

import by.kholodok.coursework.exception.NoServiceFoundException;
import by.kholodok.coursework.observer.ZnodeObserver;
import by.kholodok.coursework.zk.ZnodeMonitor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitrykholodok on 12/4/17
 */

public class ZnodeMonitorImpl implements ZnodeMonitor {

    private static final Logger LOGGER = LogManager.getLogger(ZkConnectorImpl.class);

    private ZooKeeper zk;

    private boolean dead;
    private Map<String, ObserverInfo> observerMap = new HashMap<>(); // znode path - > data

    public ZnodeMonitorImpl(ZooKeeper zk) {
        this.zk = zk;
    }

    private class ObserverInfo {

        private String serviceName;
        private String serviceHost;
        private ZnodeObserver znodeObserver;

        public ObserverInfo(String serviceName, String host, ZnodeObserver znodeObserver) {
            this.serviceName = serviceName;
            this.serviceHost = host;
            this.znodeObserver = znodeObserver;
        }
    }

    @Override
    public void process(WatchedEvent event) {
        String znodePath = event.getPath();
        // event type -> something changed in the zk . . .
        switch (event.getType()) {

            case None: {
                // zk state
                switch (event.getState()) {

                    case Expired: {  // case SyncConnected: -> describes in zk class
                        LOGGER.log(Level.WARN, "Got expired event type!");
                        dead = true;
                        break;
                    }

                    case Disconnected: {
                        LOGGER.log(Level.WARN, "Your host is not connected to the zk!");
                        break;
                    }
                }
                break;
            }

            case NodeDeleted: {
                LOGGER.log(Level.INFO, "The watched node was deleted!");
                updateHostAndEstablishWatch(znodePath);
                break;
            }
        }
    }

    @Override
    public String createServiceZnode(String serviceName, String znodeName, byte[] znodeData) {

        if (zk == null) {
            return null;
        }

        LOGGER.log(Level.INFO, "Creating a znode for " + serviceName + "service");

        String path = null;
        try {
            String znodePath = "/" + serviceName + "/" + znodeName + "_";
            path = zk.create(znodePath, znodeData, null, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.log(Level.ERROR, "Exception in the create process. " + e);
        }
        return path;
    }

    @Override
    public void addObserverToService(String serviceName, ZnodeObserver znodeObserver) throws NoServiceFoundException {
        String serviceZnodePath = "/" + serviceName;
        String serviceHost = null;
        try {
            String terminalZnodePath = receiveTerminalZnodePath(serviceZnodePath);
            byte[] serviceHostBytes = zk.getData(terminalZnodePath, true, null); // set watch in true var
            serviceHost = new String(serviceHostBytes);
            addObserver(terminalZnodePath, serviceName, serviceHost, znodeObserver);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
        }
        znodeObserver.update(serviceHost);
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    private String receiveTerminalZnodePath(String serviceZnodePath) throws NoServiceFoundException, KeeperException, InterruptedException {
        List<String> childrenPathList= zk.getChildren(serviceZnodePath, false);
        if (childrenPathList == null) {
            throw new NoServiceFoundException();
        }
        int childNumberInList = generateId(childrenPathList.size());
        return childrenPathList.get(childNumberInList);
    }

    private int generateId(int upperBound) {
        return (int)Math.random() * upperBound;
    }

    private void addObserver(String terminalZnodePath, String serviceName, String serviceHost, ZnodeObserver znodeObserver) { // znode in not null
        if (observerMap.get(terminalZnodePath) == null) {
            ObserverInfo observerInfo = new ObserverInfo(serviceName, serviceHost, znodeObserver);
            observerMap.put(terminalZnodePath, observerInfo);
        }
    }

    private void updateHostAndEstablishWatch(String deletedZnodePath) {
        ObserverInfo observerInfo = observerMap.get(deletedZnodePath);
        String serviceZnodePath = "/" + observerInfo.serviceName;
        String terminalZnodePath = null;
        try {
            terminalZnodePath = receiveTerminalZnodePath(serviceZnodePath);
            byte[] serviceHostBytes = zk.getData(terminalZnodePath, true, null); // set watch in true var
            observerInfo.serviceHost = new String(serviceHostBytes);
        } catch (KeeperException | InterruptedException | NoServiceFoundException e ) {
            LOGGER.log(Level.ERROR, e);
        }
        observerMap.remove(deletedZnodePath);
        observerMap.put(terminalZnodePath, observerInfo);
    }

}
