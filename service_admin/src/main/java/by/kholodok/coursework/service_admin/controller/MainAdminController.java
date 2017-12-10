package by.kholodok.coursework.service_admin.controller;

import by.kholodok.coursework.service_admin.service.AdminService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by dmitrykholodok on 12/10/17
 */

@Controller
@RequestMapping(value = "/")
public class MainAdminController {

    private AdminService adminService;

    public String displayServicesInformation(ModelMap model) {
        //
        return "main";
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return "Test ADMIN . . .";
    }

}
