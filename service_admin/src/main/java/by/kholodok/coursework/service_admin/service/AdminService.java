package by.kholodok.coursework.service_admin.service;

import by.kholodok.coursework.zkshell.ZkShellClient;
import by.kholodok.coursework.zkshell.entity.ServiceData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dmitrykholodok on 12/11/17
 */

@Service
public class AdminService {

    private static final Logger LOGGER = LogManager.getLogger(AdminService.class);

    @Autowired
    private ZkShellClient zkShell;

    @Autowired
    private ObjectMapper jsonMapper;

    private CloseableHttpClient httpClient;

    public AdminService() {
        httpClient = HttpClientBuilder.create().build();
    }

    public List<ServiceData> receiveServiceEntities() {
        return zkShell.receiveWorkServiceInfo();
    }

    public List<String> receiveServiceLogs(ServiceData serviceData) {
        String url = "http://" + serviceData.getHost() + ":" + serviceData.getPort() + serviceData.getEndpoints().get("logs");
        HttpGet request = new HttpGet(url);
        String resp;
        try {
            HttpResponse response = httpClient.execute(request);
            resp = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
        return (resp == null) ? null : deserialize(resp);
    }

    public void shutdownService(ServiceData serviceData) {
        String url = "http://" + serviceData.getHost() + ":" + serviceData.getPort() + serviceData.getEndpoints().get("shutdown");
        HttpGet request = new HttpGet(url);
        try {
            httpClient.execute(request);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
        }
    }

    private List<String> deserialize(String jsonData) {
        String[] strArr;
        try {
            strArr = jsonMapper.readValue(jsonData, String[].class);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
        return Arrays.asList(strArr);
    }

}
