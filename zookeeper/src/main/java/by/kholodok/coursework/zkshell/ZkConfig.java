package by.kholodok.coursework.zkshell;

/**
 * Created by dmitrykholodok on 12/14/17
 */

class ZkConfig {

    private int sessionTimeout = 15000;
    private String baseServicesPath = "/services";

    public int getSessionTimeout() {
        return (sessionTimeout);
    }

    public String getBaseServicesPath() {
        return baseServicesPath;
    }
}
