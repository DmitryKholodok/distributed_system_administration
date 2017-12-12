package by.kholodok.coursework.zkshell;

import by.kholodok.coursework.zkshell.entity.ZNodeServiceEntity;
import by.kholodok.coursework.zkshell.observer.RemoteServiceObserver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dmitrykholodok on 12/3/17
 */

class ZooKeeperShellImpl implements ZooKeeperShell, Watcher {

    private static final Logger LOGGER = LogManager.getLogger(ZooKeeperShellImpl.class);
    private static final int ZK_DEFAULT_SESSION_TIMEOUT = 15000;
    private final CountDownLatch connSignal = new CountDownLatch(1);
    private final Map<String, ObserverInfo> observerMap = new HashMap<>(); // znode path - > data
    private ZooKeeper zk;

    private class ObserverInfo {

        private String serviceName;
        private String serviceHost;
        private RemoteServiceObserver znodeObserver;

        public ObserverInfo(String serviceName, String host, RemoteServiceObserver znodeObserver) {
            this.serviceName = serviceName;
            this.serviceHost = host;
            this.znodeObserver = znodeObserver;
        }
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
        LOGGER.log(Level.DEBUG, "Got event: type - " + event.getType() + ", state - " + event.getState() + ", path - " + event.getPath());
        String zNodePath = event.getPath();
        switch (event.getType()) {  // event type -> something changed in the zookeeper . . .
            case None: {

                switch (event.getState()) { // zookeeper connection state

                    case SyncConnected: {
                        connSignal.countDown();
                    }

//                    case Expired: {  // case SyncConnected: -> describes in zk class
//                        LOGGER.log(Level.WARN, "Session expired!");
//                        break;
//                    }
//
//                    case Disconnected: {
//                        LOGGER.log(Level.WARN, "Your host is not connected to the zk!");
//                        break;
//                    }
                }
                break;
            }

            case NodeDeleted: {
                updateHostAndEstablishWatch(zNodePath);
                break;
            }
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
            LOGGER.log(Level.INFO, "Created a zNode for " + serviceName);
            return path;
        }
        return null;
    }

    @Override
    public boolean addObserverToService(String serviceName, RemoteServiceObserver zNodeObserver) {
        String serviceZNodePath = "/" + serviceName;
        String serviceHost = null;
        try {
            String terminalZNodePath = receiveTerminalZNodePath(serviceZNodePath);
            if (terminalZNodePath == null) {
                LOGGER.log(Level.WARN, "No free zNodes at " + serviceName);
                return false;
            }
            byte[] serviceHostBytes = zk.getData(terminalZNodePath, true, null); // set watch in true var
            serviceHost = new String(serviceHostBytes);
            saveObserverInfo(terminalZNodePath, serviceName, serviceHost, zNodeObserver);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
            return false;
        }
        LOGGER.log(Level.DEBUG, "Added observer for " + serviceName);
        zNodeObserver.update(serviceHost);
        return true;
    }

    @Override
    public List<ZNodeServiceEntity> receiveWorkServicesInfo(List<String> serviceNameList) {
        if (serviceNameList != null && serviceNameList.size() != 0) {
            List<ZNodeServiceEntity> serviceEntityList = new ArrayList<>();
            for(String serviceName : serviceNameList) {
                receiveServiceEntityList(serviceName, zk)
                        .stream()
                        .forEach(entity -> serviceEntityList.add(entity));
            }
            return serviceEntityList;
        }
        return null;
    }

    private String receiveTerminalZNodePath(String serviceZNodePath) throws KeeperException, InterruptedException {
        List<String> childrenPathList= zk.getChildren(serviceZNodePath, false);
        if (childrenPathList != null && childrenPathList.size() != 0) {
            int childNumberInList = generateId(childrenPathList.size());
            String zNodeName = childrenPathList.get(childNumberInList);
            return serviceZNodePath + "/" + zNodeName;
        }
        return null;
    }

    private int generateId(int upperBound) {
        return (int)Math.random() * upperBound;
    }

    private void saveObserverInfo(String terminalZNodePath, String serviceName, String serviceHost, RemoteServiceObserver znodeObserver) { // znode in not null
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
                    LOGGER.log(Level.WARN, "No free zNodes at " + observerInfo.serviceName +
                            ". Setting the service's address in observer to null");
                    observerInfo.znodeObserver.update(null);
                    observerMap.remove(deletedZNodePath);
                    return false;
                }
                byte[] serviceHostBytes = zk.getData(terminalZNodePath, true, null);
                LOGGER.log(Level.DEBUG, "A watch was set on " + terminalZNodePath);
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

    private List<ZNodeServiceEntity> receiveServiceEntityList(String serviceName, ZooKeeper zk) {
        List<ZNodeServiceEntity> serviceEntityList = new ArrayList<>();
        try {
            List<String> serviceList = zk.getChildren("/" + serviceName, false);
            for(String serviceInfo : serviceList) {
                ZNodeServiceEntity serviceEntity = new ZNodeServiceEntity();
                serviceEntity.setServiceName(serviceName);
                String serviceZNodePath = "/" + serviceName + "/" + serviceInfo;
                serviceEntity.setZNodePath(serviceZNodePath);
                byte[] zNodeData = zk.getData(serviceZNodePath, false, null);
                serviceEntity.setHostPort(new String(zNodeData));
                serviceEntityList.add(serviceEntity);
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
        return serviceEntityList;
    }

}
