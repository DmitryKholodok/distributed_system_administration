package by.kholodok.coursework.service_a.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by dmitrykholodok on 12/5/17
 */

@Service
public class CalculateService {

    @Autowired
    private ZkConnector zkConnector;

    public int square(int num) {
        return num * num;
    }

    public int calculateSum(int a, int b)  {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://localhost:8080/service_b/calculate");
        try {
            StringEntity params = new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setHeader("Content-Type", "application/json");
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getStatusLine().getStatusCode();
    }

}
