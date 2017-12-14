package by.kholodok.coursework.zkshell.observer;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dmitrykholodok on 12/11/17
 */

public class RemoteServiceObserver {

    private AtomicReference<String> remoteServiceHostPort = new AtomicReference<>();

    private String remoteServiceName;

    public RemoteServiceObserver(String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    public void update(String value) {
        remoteServiceHostPort.set(value);
    }

    public String getServiceHostPort() {
        return remoteServiceHostPort.get();
    }

    public String getRemoteServiceName() {
        return remoteServiceName;
    }


}
