package by.kholodok.coursework.service_admin.entity;

/**
 * Created by dmitrykholodok on 12/11/17
 */

public class ServiceEntity {

    private String serviceName;
    private String ZNodePath;
    private String hostPort;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getZNodePath() {
        return ZNodePath;
    }

    public void setZNodePath(String ZNodePath) {
        this.ZNodePath = ZNodePath;
    }
}
