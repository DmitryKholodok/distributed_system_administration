package by.kholodok.coursework.service_a.service;


import by.kholodok.coursework.service_a.dao.LogsDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * Created by dmitrykholodok on 12/14/17
 */

@PropertySource("classpath:config.properties")
public class LogsService {

    @Autowired
    private LogsDAO logsDAO;

    @Value("${logs.path}")
    private String logsPath;

    @Autowired
    private ObjectMapper jsonMapper;

    public String receiveLastLogs(int count) {
        List<String> logLines = logsDAO.readAllFile(logsPath);
        if (count <= logLines.size()) {
            logLines = logLines.subList(logLines.size()- count, logLines.size());
        }
        try {
            return jsonMapper.writeValueAsString(logLines.toArray());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
