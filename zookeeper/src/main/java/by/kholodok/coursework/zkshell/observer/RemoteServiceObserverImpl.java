package by.kholodok.coursework.zkshell.observer;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dmitrykholodok on 12/11/17
 */

public class RemoteServiceObserverImpl implements RemoteServiceObserver {

    private AtomicReference<String> value = new AtomicReference<>();
    private String remoteServiceName;

    public RemoteServiceObserverImpl(String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    @Override
    public void update(String newServiceHostPort) {
        value.set(newServiceHostPort);
    }

    @Override
    public String receiveHostPort() {
        return value.get();
    }

    @Override
    public String receiveServiceName() {
        return remoteServiceName;
    }
}
