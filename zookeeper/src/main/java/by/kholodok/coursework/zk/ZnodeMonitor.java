package by.kholodok.coursework.zk;

import by.kholodok.coursework.exception.NoServiceFoundException;
import by.kholodok.coursework.observer.ZNodeObserver;
import org.apache.zookeeper.Watcher;

import java.io.Serializable;

/**
 * Created by dmitrykholodok on 12/5/17
 */

public interface ZNodeMonitor extends Watcher {

    String createServiceZNode(String serviceName, byte[] zNodeData);
    boolean addObserverToService(String serviceName, ZNodeObserver observer) throws NoServiceFoundException;

}
