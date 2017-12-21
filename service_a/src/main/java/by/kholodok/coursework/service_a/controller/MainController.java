package by.kholodok.coursework.service_a.controller;

import by.kholodok.coursework.service_a.controller.error.ErrorMsg;
import by.kholodok.coursework.service_a.exception.NoServiceFoundException;
import by.kholodok.coursework.service_a.exception.ResourceNotFoundException;
import by.kholodok.coursework.service_a.exception.ServiceConnectionException;
import by.kholodok.coursework.service_a.service.CalculateService;
import by.kholodok.coursework.service_a.service.LogsService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by dmitrykholodok on 12/3/17
 */

@Controller
public class MainController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    @Autowired
    private CalculateService calculateService;

    @Autowired
    private LogsService logService;

    @Autowired
    private ErrorMsg errorMsg;

    @RequestMapping(value = "/calculate", method = RequestMethod.GET)
    @ResponseBody
    public String calculate(@RequestParam(value = "x") String x)
    {
        LOGGER.log(Level.INFO, "Calculate request received");
        int result;
        try {
            List<Integer> digitList = calculateService.divideNumberByDigits(x);
            if (digitList == null) {
                throw new ResourceNotFoundException();
            }
            result = calculateService.calculateSum(digitList);
        } catch (ServiceConnectionException e) {
            LOGGER.log(Level.ERROR, e);
            return errorMsg.getConnectionErrorMsg();
        } catch (NoServiceFoundException e) {
            LOGGER.log(Level.ERROR, e);
            return errorMsg.getNoServiceFoundMsg();
        }
        return Integer.toString(result);
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
        final int count = 50;
        String jsonData = logService.receiveLastLogs(count); // change count
        if (jsonData == null) {
            return errorMsg.getSerializeErrorMsg();
        }
        return jsonData;
    }

}
