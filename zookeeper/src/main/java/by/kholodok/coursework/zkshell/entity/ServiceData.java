package by.kholodok.coursework.zkshell.entity;

import java.beans.Transient;
import java.util.Map;

/**
 * Created by dmitrykholodok on 12/14/17
 */

public class ServiceData {

    private String serviceName;
    private String ZNodePath;
    private String host;
    private String port;
    private Map<String, String> endpoints;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getZNodePath() {
        return ZNodePath;
    }

    public void setZNodePath(String ZNodePath) {
        this.ZNodePath = ZNodePath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Map<String, String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, String> endpoints) {
        this.endpoints = endpoints;
    }

    @Transient
    public boolean isEmpty() {
        return host == null || port == null || serviceName == null;
    }
}
