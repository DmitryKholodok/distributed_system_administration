package by.kholodok.coursework.zkshell.observer;

/**
 * Created by dmitrykholodok on 12/5/17
 */

public interface RemoteServiceObserver {
    void update(String obj);
    String receiveHostPort();
    String receiveServiceName();
}
