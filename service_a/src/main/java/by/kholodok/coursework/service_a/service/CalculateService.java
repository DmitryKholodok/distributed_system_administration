package by.kholodok.coursework.service_a.service;

import by.kholodok.coursework.zkshell.observer.ObserversProvider;
import by.kholodok.coursework.service_a.entity.DigitsEntity;
import by.kholodok.coursework.service_a.exception.NoServiceFoundException;
import by.kholodok.coursework.service_a.exception.ServiceConnectionException;
import by.kholodok.coursework.zkshell.ZkShellClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitrykholodok on 12/5/17
 */

@Service
public class CalculateService {

    private static final Logger LOGGER = LogManager.getLogger(CalculateService.class);

    @Autowired
    private ZkShellClient zkShell;

    @Autowired
    private ObserversProvider observersProvider;

    @Autowired
    private ObjectMapper jsonMapper;

    public List<Integer> divideNumberByDigits(String num) {
        List<Integer> digitList = new ArrayList<>();
        for(int i = 0; i < num.length(); i++) {
            Character digit = num.charAt(i);
            if (Character.isDigit(digit)) {
                digitList.add(Character.getNumericValue(digit));
            } else {
                return null;
            }
        }
        return digitList;
    }

    public int calculateSum(List<Integer> digitList)  throws ServiceConnectionException, NoServiceFoundException {
        final String SERVICE_NAME = "service-b";
        final DigitsEntity ENTITY = createNumEntity(digitList);
        final String ENTITY_JSON_DATA = serializeObject(ENTITY);
        String serviceBHostPort = observersProvider.receiveServiceHostPort(SERVICE_NAME);
        if (serviceBHostPort == null) {
            throw new NoServiceFoundException(SERVICE_NAME + " is not found!");
        }
        String url = "http://" + serviceBHostPort + "/add";
        HttpPost request = createPostRequest(url, ENTITY_JSON_DATA);
        String resp = sendToService(request);
        return Integer.parseInt(resp);
    }

    private String sendToService(HttpPost request) throws ServiceConnectionException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String resp;
        try {
            HttpResponse response = httpClient.execute(request);
            resp = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new ServiceConnectionException();
        }
        return resp;
    }
    
    private HttpPost createPostRequest(String url, String jsonData) {
        final String CONTENT_TYPE = "application/json";
        HttpPost request = new HttpPost( url);
        try {
            StringEntity params = new StringEntity(jsonData);
            request.setEntity(params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setHeader("Content-Type", CONTENT_TYPE);
        return request;
    }

    private DigitsEntity createNumEntity(List<Integer> digitList) {
        DigitsEntity numbersEntity = new DigitsEntity();
        digitList
                .stream()
                .forEach(digit -> numbersEntity.addDigit(digit));
        return numbersEntity;
    }

    private String serializeObject(DigitsEntity numbersEntity) {
        String entityData;
        try {
            entityData = jsonMapper.writeValueAsString(numbersEntity);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
        return entityData;
    }


}
