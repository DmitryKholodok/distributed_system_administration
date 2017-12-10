package by.kholodok.coursework.service_a.controller;

import by.kholodok.coursework.service_a.service.CalculateService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by dmitrykholodok on 12/3/17
 */

@Controller
@RequestMapping(value = "/")
public class CalculateController {

    CalculateService calculateService;

    @RequestMapping(value = "/calculate", method = RequestMethod.GET)
    @ResponseBody
    public String calculate(@RequestParam(value = "a") int a,
                            @RequestParam(value = "b") int b)
    {
        int one = calculateService.square(a);
        int two = calculateService.square(b);
        int result = calculateService.calculateSum(one, two);
        return Integer.toString(result);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "Test service A . . .";
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    @ResponseBody
    public String sendDataTest()
    {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://localhost:8080/service_b/calculate");
        try {
            StringEntity params = new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
            request.setEntity(params);
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
        String respString = null;
        try {
            respString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respString;
    }

}
