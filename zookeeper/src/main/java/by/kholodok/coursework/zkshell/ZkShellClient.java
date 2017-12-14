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
            LOGGER.log(Level.FATAL, "Filling the service data error!");
            throw new RuntimeException();
        }
        zkShell.createServiceZNode(serviceData);
        remoteServiceObserverList
                .parallelStream()
                .forEach(remoteServiceObserver ->
                        zkShell.addObserverToService(remoteServiceObserver));
        Runnable task = () -> {
            final int SLEEP_TIME = 3;
            LOGGER.log(Level.INFO, "Start tracking the services . . .");
            while(true) {
                remoteServiceObserverList
                        .parallelStream()
                        .filter(remoteServiceObserver -> remoteServiceObserver.getServiceHostPort() == null)
                        .forEach(rso -> zkShell.addObserverToService(rso));
                try {
                    TimeUnit.SECONDS.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(task);
    }

    public void close() {
        executorService.shutdownNow();
        zkShell.close();
    }

    private void establishConnection(String zkHostPort) {
        Callable<ZooKeeperShell> task = () -> {
            ZooKeeperShell zooKeeperShell = new ZooKeeperShellImpl();
            zooKeeperShell.connectToZk(zkHostPort);
            return zooKeeperShell;
        };
        Future<ZooKeeperShell> zkFuture = Executors.newSingleThreadExecutor().submit(task);
        try {
            zkShell = zkFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.ERROR, e);
            return;
        }
        LOGGER.log(Level.DEBUG, "Connection established . . .");
    }

}
