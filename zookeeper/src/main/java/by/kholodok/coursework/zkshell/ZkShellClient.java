package by.kholodok.coursework.zkshell;

import by.kholodok.coursework.zkshell.entity.ServiceData;
import by.kholodok.coursework.zkshell.observer.RemoteServiceObserver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by dmitrykholodok on 12/12/17
 */

public class ZkShellClient {

    private static final Logger LOGGER = LogManager.getLogger(ZkShellClient.class);

    private ExecutorService executorService;
    private ZooKeeperShell zkShell;
    private List<RemoteServiceObserver> remoteServiceObserverList;
    private ServiceData serviceData;

    public ZkShellClient(String zkHostPort) {
        executorService = Executors.newSingleThreadExecutor();
        establishConnection(zkHostPort);
    }

    public List<RemoteServiceObserver> getRemoteServiceObserverList() {
        return remoteServiceObserverList;
    }
    public void setRemoteServiceObserverList(List<RemoteServiceObserver> remoteServiceObserverList) {
        this.remoteServiceObserverList = remoteServiceObserverList;
    }
    public void setServiceData(ServiceData serviceData) {
        this.serviceData = serviceData;
    }

    public List<ServiceData> receiveWorkServiceInfo() {
        return zkShell.receiveServicesData();
    }

    public void startTrackingServices() {
        if (serviceData == null || serviceData.isEmpty()) {
            LOGGER.log(Level.FATAL, "Skipped the service data!");
            throw new RuntimeException();
        }
        zkShell.createServiceZNode(serviceData);
        Runnable task = () -> {
            final int SLEEP_TIME = 3;
            boolean setToNull = false;
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(zkShell.isDead()) {
                    if (!setToNull) {
                        LOGGER.log(Level.INFO, "Zookeeper is dead. Setting to null all service urls ");
                        setToNull = true;
                        remoteServiceObserverList
                                .parallelStream()
                                .forEach(rso -> rso.update(null));
                    }
                } else {
                    setToNull = false;
                    remoteServiceObserverList
                            .parallelStream()
                            .filter(remoteServiceObserver -> remoteServiceObserver.getServiceHostPort() == null)
                            .forEach(rso -> zkShell.addObserverToService(rso));
                }
            }
        };
        executorService.submit(task);
    }

    public void close() {
        executorService.shutdownNow();
        zkShell.close();
    }

    private void establishConnection(String zkHostPort) {
        zkShell = new ZooKeeperShellImpl();
        zkShell.connectToZk(zkHostPort);
        LOGGER.log(Level.DEBUG, "Connection established . . .");
    }

}
