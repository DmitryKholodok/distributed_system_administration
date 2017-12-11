package by.kholodok.coursework.service_admin.service;

import by.kholodok.coursework.service_admin.connection.ZkAdminConnector;
import by.kholodok.coursework.service_admin.entity.ServiceEntity;
import by.kholodok.coursework.service_admin.exception.NoZkConnectionException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitrykholodok on 12/11/17
 */

@Service
public class AdminService {

    private static final String[] SERVICES_NAME = {"service_a", "service_b"};

    public List<ServiceEntity> receiveAllServices(ZkAdminConnector zkAdminConnector) throws NoZkConnectionException {
        List<ServiceEntity> serviceEntityList = new ArrayList<>();
        ZooKeeper zooKeeper = zkAdminConnector.getZooKeeper();
        for(String serviceName : SERVICES_NAME) {
            receiveServiceEntityList(serviceName, zooKeeper)
                    .stream()
                    .forEach(entity -> serviceEntityList.add(entity));
        }
        return serviceEntityList;
    }

    private List<ServiceEntity> receiveServiceEntityList(String serviceName, ZooKeeper zk) throws NoZkConnectionException {
        List<ServiceEntity> serviceEntityList = new ArrayList<>();
        try {
            List<String> serviceList = zk.getChildren("/" + serviceName, false);
            for(String serviceInfo : serviceList) {
                ServiceEntity serviceEntity = new ServiceEntity();
                serviceEntity.setServiceName(serviceName);
                String serviceZNodePath = "/" + serviceName + "/" + serviceInfo;
                serviceEntity.setZNodePath(serviceZNodePath);
                byte[] zNodeData = zk.getData(serviceZNodePath, false, null);
                serviceEntity.setHostPort(new String(zNodeData));
                serviceEntityList.add(serviceEntity);
            }
        } catch (KeeperException e) {
            throw new NoZkConnectionException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serviceEntityList;
    }


}
