package by.kholodok.coursework.zk;

import by.kholodok.coursework.exception.NoServiceFoundException;
import by.kholodok.coursework.observer.ZNodeObserver;
import org.apache.zookeeper.Watcher;

/**
 * Created by dmitrykholodok on 12/5/17
 */
public interface ZNodeMonitor extends Watcher {

    String createServiceZnode(String serviceName, String znodeName, byte[] znodeData);
    void addObserverToService(String serviceName, ZNodeObserver observer) throws NoServiceFoundException;
    boolean isDead();

}
