package by.kholodok.coursework.service_b.controller;

import by.kholodok.coursework.service_b.service.CalculateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dmitrykholodok on 12/10/17
 */

@Controller
@RequestMapping(value = "/")
public class MainController {

    CalculateService calculateService;

    @RequestMapping(value = "/calculate", headers = "Content-Type=application/json")
    @ResponseBody
    public String request(@RequestBody String res) {
        return res;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "Test service B . . .";
    }

}
