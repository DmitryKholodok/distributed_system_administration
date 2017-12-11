package by.kholodok.coursework.zk.impl;

import by.kholodok.coursework.exception.NoServiceFoundException;
import by.kholodok.coursework.observer.ZNodeObserver;
import by.kholodok.coursework.zk.ZNodeMonitor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Created by dmitrykholodok on 12/4/17
 */

public class ZNodeMonitorImpl implements ZNodeMonitor {

    private static final Logger LOGGER = LogManager.getLogger(ZkConnectorImpl.class);
    private final Map<String, ObserverInfo> observerMap = new HashMap<>(); // znode path - > data
    private final ZooKeeper zk;

    public ZNodeMonitorImpl(ZooKeeper zk) {
        this.zk = zk;
    }

    private class ObserverInfo {

        private String serviceName;
        private String serviceHost;
        private ZNodeObserver znodeObserver;

        public ObserverInfo(String serviceName, String host, ZNodeObserver znodeObserver) {
            this.serviceName = serviceName;
            this.serviceHost = host;
            this.znodeObserver = znodeObserver;
        }
    }

    @Override
    public void process(WatchedEvent event) {
        String zNodePath = event.getPath();
        switch (event.getType()) {  // event type -> something changed in the zookeeper . . .
            case None: {

                switch (event.getState()) { // zookeeper connection state

                    case Expired: {  // case SyncConnected: -> describes in zk class
                        LOGGER.log(Level.WARN, "Session expired!");
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
                updateHostAndEstablishWatch(zNodePath);
                break;
            }
        }
    }

    @Override
    public String createServiceZNode(String serviceName, byte[] zNodeData) {
        if (zk != null) {
            String path = null;
            try {
                String zNodePath = "/" + serviceName + "/n_";
                path = zk.create(zNodePath, zNodeData, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            } catch (KeeperException | InterruptedException e) {
                LOGGER.log(Level.ERROR, "Exception in the create process. " + e);
                return null;
            }
            LOGGER.log(Level.INFO, "Created a ZNode for " + serviceName);
            return path;
        }
        return null;
    }

    @Override
    public boolean addObserverToService(String serviceName, ZNodeObserver zNodeObserver) throws NoServiceFoundException {
        String serviceZNodePath = "/" + serviceName;
        String serviceHost = null;
        try {
            String terminalZNodePath = receiveTerminalZNodePath(serviceZNodePath);
            if (terminalZNodePath == null) {
                throw new NoServiceFoundException();
            }
            byte[] serviceHostBytes = zk.getData(terminalZNodePath, true, null); // set watch in true var
            serviceHost = new String(serviceHostBytes);
            saveObserverInfo(terminalZNodePath, serviceName, serviceHost, zNodeObserver);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
            return false;
        }
        zNodeObserver.update(serviceHost);
        return true;
    }

    private String receiveTerminalZNodePath(String serviceZNodePath) throws KeeperException, InterruptedException {
        List<String> childrenPathList= zk.getChildren(serviceZNodePath, false);
        if (childrenPathList != null) {
            int childNumberInList = generateId(childrenPathList.size());
            String zNodeName = childrenPathList.get(childNumberInList);
            return serviceZNodePath + "/" + zNodeName;
        }
        return null;
    }

    private int generateId(int upperBound) {
        return (int)Math.random() * upperBound;
    }

    private void saveObserverInfo(String terminalZNodePath, String serviceName, String serviceHost, ZNodeObserver znodeObserver) { // znode in not null
        if (observerMap.get(terminalZNodePath) == null) {
            ObserverInfo observerInfo = new ObserverInfo(serviceName, serviceHost, znodeObserver);
            observerMap.put(terminalZNodePath, observerInfo);
        }
    }

    private boolean updateHostAndEstablishWatch(String deletedZNodePath) {
        ObserverInfo observerInfo = observerMap.get(deletedZNodePath);
        if (observerInfo != null) {
            String serviceZNodePath = "/" + observerInfo.serviceName;
            String terminalZNodePath = null;
            try {
                terminalZNodePath = receiveTerminalZNodePath(serviceZNodePath);
                if (terminalZNodePath == null) {
                    observerInfo.znodeObserver.update(null);
                    observerMap.remove(deletedZNodePath);
                    return false;
                }
                byte[] serviceHostBytes = zk.getData(terminalZNodePath, true, null);
                observerInfo.serviceHost = new String(serviceHostBytes);
            } catch (KeeperException | InterruptedException e ) {
                LOGGER.log(Level.ERROR, e);
                return false;
            }
            observerMap.remove(deletedZNodePath);
            observerMap.put(terminalZNodePath, observerInfo);
            return true;
        }
        return false;
    }

}
