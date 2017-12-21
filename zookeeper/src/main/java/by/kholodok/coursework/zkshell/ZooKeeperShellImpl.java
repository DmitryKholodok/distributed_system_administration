package by.kholodok.coursework.zkshell;

import by.kholodok.coursework.zkshell.entity.ServiceData;
import by.kholodok.coursework.zkshell.observer.RemoteServiceObserver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dmitrykholodok on 12/3/17
 */

class ZooKeeperShellImpl implements ZooKeeperShell, Watcher {

    private static final Logger LOGGER = LogManager.getLogger(ZooKeeperShellImpl.class);

    private final CountDownLatch connSignal = new CountDownLatch(1);
    private final Map<String, RemoteServiceObserver> observerMap = new HashMap<>();
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private ZkConfig zkConfig = new ZkConfig();
    private ZooKeeper zk;
    private AtomicReference<Boolean> dead = new AtomicReference<>();

    @Override
    public ZooKeeper connectToZk(String host) {
        return connectToZk(host, zkConfig.getSessionTimeout());
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
        LOGGER.log(Level.INFO, "Got event: type - " + event.getType() + ", state - " + event.getState() + ", path - " + event.getPath());
        String zNodePath = event.getPath();
        switch (event.getType()) {  // event type -> something changed in the zookeeper . . .
            case None: {

                switch (event.getState()) { // zookeeper connection state

                    case SyncConnected: {
                        dead.set(false);
                        connSignal.countDown();
                        break;
                    }

                    case Disconnected:
                    case Expired:
                        dead.set(true);

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
    public String createServiceZNode(ServiceData serviceData) {
        if (zk != null) {
            final String serviceName = serviceData.getServiceName();
            String path = null;
            try {
                String zNodePath = zkConfig.getBaseServicesPath() + "/" + serviceName + "/n_";
                byte[] serviceDataBytes = new byte[0];
                path = zk.create(zNodePath, serviceDataBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                serviceData.setZNodePath(path);
                serviceDataBytes = serializeServiceData(serviceData);
                zk.setData(path, serviceDataBytes, 0);
            } catch (KeeperException | InterruptedException e) {
                LOGGER.log(Level.ERROR, "Exception in the create process. " + e);
                return path;
            }
            LOGGER.log(Level.INFO, "Created a zNode for " + serviceName);
            return path;
        }
        return null;
    }

    @Override
    public boolean addObserverToService(RemoteServiceObserver zNodeObserver) {
        final String serviceName = zNodeObserver.getRemoteServiceName();
        String serviceZNodePath = zkConfig.getBaseServicesPath() + "/" + serviceName;
        try {
            String terminalZNodePath = receiveAnyTerminalZNodePath(serviceZNodePath);
            if (terminalZNodePath == null) {
                LOGGER.log(Level.WARN, "No free zNodes at " + serviceName);
                return false;
            }
            byte[] zNodeData = zk.getData(terminalZNodePath, true, null);
            ServiceData serviceData = deserializeServiceData(zNodeData);
            if (serviceData == null) {
                return false;
            }
            zNodeObserver.update(serviceData.getHost() + ":" + serviceData.getPort());
            saveObserverData(terminalZNodePath, zNodeObserver);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
            return false;
        }
        LOGGER.log(Level.INFO, "Added observer for " + serviceName);
        return true;
    }

    @Override
    public List<ServiceData> receiveServicesData() {
        List<String> terminalZNodePathList;
        try {
            terminalZNodePathList = receiveAllTerminalZNodePaths();
        } catch (KeeperException | InterruptedException e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
        final List<ServiceData> serviceDataList = new ArrayList<>();
        terminalZNodePathList
                .parallelStream()
                .forEach(service -> {
                    try {
                        byte[] zNodeData = zk.getData(service, false, null);
                        ServiceData serviceData = deserializeServiceData(zNodeData);
                        if (serviceData != null) {
                            serviceDataList.add(serviceData);
                        }
                    } catch (KeeperException | InterruptedException e) {
                        LOGGER.log(Level.ERROR, e);
                    }
        });
        if (serviceDataList.isEmpty()) {
            return null;
        }
        return serviceDataList;
    }

    @Override
    public boolean isDead() {
        return dead.get();
    }

    private List<String> receiveAllTerminalZNodePaths() throws KeeperException, InterruptedException {
        final String baseServicePath = zkConfig.getBaseServicesPath();
        final List<String> terminalZNodePathList = new ArrayList<>();
        for(String serviceName : zk.getChildren(baseServicePath, false)) {
            zk.getChildren(baseServicePath + "/" + serviceName, false)
                    .parallelStream()
                    .forEach(path -> terminalZNodePathList.add(baseServicePath + "/" + serviceName + "/" + path));
        }
        return terminalZNodePathList;
    }

    private String receiveAnyTerminalZNodePath(String serviceZNodePath) throws KeeperException, InterruptedException {
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

    private void saveObserverData(String terminalZNodePath, RemoteServiceObserver zNodeObserver) {
        if (observerMap.get(terminalZNodePath) == null) {
            observerMap.put(terminalZNodePath, zNodeObserver);
        }
    }

    private boolean updateHostAndEstablishWatch(String deletedZNodePath) {
        RemoteServiceObserver rso = observerMap.get(deletedZNodePath);
        if (rso != null) {
            String serviceZNodePath = zkConfig.getBaseServicesPath() + "/" + rso.getRemoteServiceName();
            String terminalZNodePath;
            try {
                terminalZNodePath = receiveAnyTerminalZNodePath(serviceZNodePath);
                if (terminalZNodePath == null) {
                    LOGGER.log(Level.WARN, "No free zNodes at " + rso.getRemoteServiceName() +
                            ". Setting the service's address in observer to null");
                    rso.update(null);
                    observerMap.remove(deletedZNodePath);
                    return false;
                }
                byte[] zNodeData = zk.getData(terminalZNodePath, true, null);
                ServiceData serviceData = deserializeServiceData(zNodeData);
                if (serviceData == null) {
                    return false;
                }
                LOGGER.log(Level.DEBUG, "A watch was set on " + terminalZNodePath);
                rso.update(terminalZNodePath);
            } catch (KeeperException | InterruptedException e ) {
                LOGGER.log(Level.ERROR, e);
                return false;
            }
            observerMap.remove(deletedZNodePath);
            observerMap.put(terminalZNodePath, rso);
            return true;
        }
        return false;
    }

    private byte[] serializeServiceData(ServiceData serviceData) {
        byte[] serviceDataBytes;
        try {
            serviceDataBytes = jsonMapper.writeValueAsBytes(serviceData);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.ERROR, "Exception in the serialize process. " + e);
            return null;
        }
        return serviceDataBytes;
    }

    private ServiceData deserializeServiceData(byte[] serviceDataBytes) {
        ServiceData serviceData;
        try {
            serviceData = jsonMapper.readValue(serviceDataBytes, ServiceData.class);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Can not deserialize a zNodeData. " + e);
            return null;
        }
        return serviceData;
    }

}
