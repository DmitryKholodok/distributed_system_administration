package by.kholodok.coursework.observer;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dmitrykholodok on 12/11/17
 */

public class ZNodeObserverImpl implements ZNodeObserver {

    private AtomicReference<String> value = new AtomicReference<>();

    @Override
    public void update(String newServiceHostPort) {
        value.set(newServiceHostPort);
    }

    public String getHostPort() {
        return value.get();
    }
}
