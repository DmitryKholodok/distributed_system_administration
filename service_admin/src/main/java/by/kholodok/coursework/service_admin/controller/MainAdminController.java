package by.kholodok.coursework.service_admin.controller;

import by.kholodok.coursework.service_admin.connection.ZkAdminConnector;
import by.kholodok.coursework.service_admin.entity.ServiceEntity;
import by.kholodok.coursework.service_admin.exception.NoZkConnectionException;
import by.kholodok.coursework.service_admin.service.AdminService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by dmitrykholodok on 12/10/17
 */

@Controller
@RequestMapping(value = "/")
public class MainAdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ZkAdminConnector zkAdminConnector;

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String displayServicesInformation(ModelMap model) {
        List<ServiceEntity> serviceEntityList = null;
        try {
            serviceEntityList = adminService.receiveAllServices(zkAdminConnector);
        } catch (NoZkConnectionException e) {
            return "main"; // return error page
        }
        model.addAttribute("serviceEntityList", serviceEntityList);
        return "main";
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return "Test ADMIN . . . ";
    }

}
