package by.kholodok.coursework.service_admin.controller;

import by.kholodok.coursework.service_admin.exception.BadRequestException;
import by.kholodok.coursework.zkshell.entity.ServiceData;
import by.kholodok.coursework.service_admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by dmitrykholodok on 12/10/17
 */

@Controller
@RequestMapping(value = "/")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String displayServicesInformation(ModelMap model, HttpServletRequest req) {
        List<ServiceData> serviceEntityList = adminService.receiveServiceEntities();
        req.getSession().setAttribute("serviceDataList", serviceEntityList);
        return "main";
    }

    @RequestMapping(value = "/logs/{id}", method = RequestMethod.GET)
    public String showServiceLogs(@PathVariable("id") int id, HttpServletRequest req, ModelMap model) {
        List<ServiceData> serviceDataList = (List<ServiceData>)req.getSession().getAttribute("serviceDataList");
        if (serviceDataList != null && id >= 0 && id < serviceDataList.size()) {
            ServiceData sd = serviceDataList.get(id);
            List<String> logList = adminService.receiveServiceLogs(sd);
            model.addAttribute("logList", logList);
            String serviceName = sd.getServiceName() + " (" + sd.getHost() + ":" + sd.getPort() + ")";
            model.addAttribute("serviceName", serviceName);
            return "logs";
        }
        throw new BadRequestException();
    }

    @RequestMapping(value = "/shutdown/{id}", method = RequestMethod.GET)
    public String shutdownService(@PathVariable("id") int id, HttpServletRequest req) {
        List<ServiceData> serviceDataList = (List<ServiceData>)req.getSession().getAttribute("serviceDataList");
        if (serviceDataList != null && id >= 0 && id < serviceDataList.size()) {
            adminService.shutdownService(serviceDataList.get(id));
            return "redirect:/show";
        }
        throw new BadRequestException();
    }



}
