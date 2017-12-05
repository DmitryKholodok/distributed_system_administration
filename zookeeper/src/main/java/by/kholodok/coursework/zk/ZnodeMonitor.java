package by.kholodok.coursework.zk;

import by.kholodok.coursework.exception.NoServiceFoundException;
import by.kholodok.coursework.observer.ZnodeObserver;
import org.apache.zookeeper.Watcher;

/**
 * Created by dmitrykholodok on 12/5/17
 */
public interface ZnodeMonitor extends Watcher {

    String createServiceZnode(String serviceName, String znodeName, byte[] znodeData);
    void addObserverToService(String serviceName, ZnodeObserver observer) throws NoServiceFoundException;
    boolean isDead();

}
