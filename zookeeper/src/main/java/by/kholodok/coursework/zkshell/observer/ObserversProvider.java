package by.kholodok.coursework.zkshell.observer;

import by.kholodok.coursework.zkshell.observer.RemoteServiceObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitrykholodok on 12/12/17
 */

public class ObserversProvider {

    private Map<String, RemoteServiceObserver> serviceNameHostPortMap = new HashMap<>();

    public ObserversProvider(List<RemoteServiceObserver> remoteServiceObserverList) {
        remoteServiceObserverList
                .stream()
                .forEach(rso -> serviceNameHostPortMap.put(rso.getRemoteServiceName(), rso));
    }

    public String receiveServiceHostPort(String serviceName) {
        RemoteServiceObserver observer = serviceNameHostPortMap.get(serviceName);
        if (observer != null) {
            return observer.getServiceHostPort();
        }
        return null;
    }

    public ObserversProvider() {}

    public void setServiceNameHostPortMap(Map<String, RemoteServiceObserver> serviceNameHostPortMap) {
        this.serviceNameHostPortMap = serviceNameHostPortMap;
    }

}
