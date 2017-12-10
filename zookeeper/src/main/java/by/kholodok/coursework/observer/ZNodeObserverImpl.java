package by.kholodok.coursework.observer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dmitrykholodok on 12/11/17
 */

public class ZNodeObserverImpl implements ZNodeObserver {

    private Lock lock = new ReentrantLock();

    private String hostPort = "";

    @Override
    public void update(Object obj) {
        lock.lock();
        try {
            hostPort = (String)obj;
        } finally {
            lock.unlock();
        }
    }

    public String getHostPort() {
        lock.lock();
        try {
            return hostPort;
        } finally {
            lock.unlock();
        }
    }
}