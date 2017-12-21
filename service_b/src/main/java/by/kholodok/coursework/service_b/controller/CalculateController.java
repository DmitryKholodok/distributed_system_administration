package by.kholodok.coursework.service_b.controller;

import by.kholodok.coursework.service_b.entity.DigitsEntity;
import by.kholodok.coursework.service_b.service.CalculateService;
import by.kholodok.coursework.service_b.service.LogsService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by dmitrykholodok on 12/10/17
 */

@Controller
@RequestMapping(value = "/")
public class CalculateController {

    private static final Logger LOGGER = LogManager.getLogger(CalculateController.class);

    @Autowired
    private CalculateService calculateService;

    @Autowired
    private LogsService logService;

    @RequestMapping(value = "/add", headers = "Content-Type=application/json")
    @ResponseBody
    public String request(@RequestBody String jsonNumberEntity) {
        LOGGER.log(Level.INFO, "Add request received");
        DigitsEntity numbersEntity = calculateService.jsonDeserialize(jsonNumberEntity);
        if (numbersEntity != null) {
            Integer result = calculateService.addition(numbersEntity);
            return String.valueOf(result);
        }
        return null;
    }

    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public void exit() {
        LOGGER.log(Level.INFO, "Exit request received");
        System.exit(0);
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    @ResponseBody
    public String returnLogs() {
        LOGGER.log(Level.INFO, "Get logs request received");
        String jsonData = logService.receiveLastLogs(50);
        return jsonData;
    }
}
