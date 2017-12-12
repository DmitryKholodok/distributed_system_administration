package by.kholodok.coursework.zkshell;

import by.kholodok.coursework.zkshell.observer.RemoteServiceObserver;
import by.kholodok.coursework.zkshell.entity.ZNodeServiceEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dmitrykholodok on 12/12/17
 */

public class ZkShellClient {

    private static final Logger LOGGER = LogManager.getLogger(ZkShellClient.class);
    private ZooKeeperShell zkShell;
    private List<RemoteServiceObserver> remoteServiceObserverList;
    private String currServiceName;

    public ZkShellClient(String zkHostPort) {
        establishConnection(zkHostPort);
    }

    public void setCurrServiceName(String currServiceName) {
        this.currServiceName = currServiceName;
    }

    public void setRemoteServiceObserverList(List<RemoteServiceObserver> remoteServiceObserverList) {
        this.remoteServiceObserverList = remoteServiceObserverList;
    }

    public List<ZNodeServiceEntity> receiveWorkServiceInfo(List<String> serviceNameList) {
        return zkShell.receiveWorkServicesInfo(serviceNameList);
    }

    public void startTrackingServices() {
        if (currServiceName == null) {
            LOGGER.log(Level.FATAL, "No service name found!");
            throw new RuntimeException();
        }
        byte[] currHostPort = new String("localhost:8080").getBytes();
        zkShell.createServiceZNode(currServiceName, currHostPort);
        remoteServiceObserverList
                .stream()
                .forEach(remoteServiceObserver ->
                        zkShell.addObserverToService(remoteServiceObserver.receiveServiceName(), remoteServiceObserver));
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
    }

}
