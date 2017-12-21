package by.kholodok.coursework.service_a.dao;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitrykholodok on 12/14/17
 */

public class LogsDAO {

    private static final Logger LOGGER = LogManager.getLogger(LogsDAO.class);

    public List<String> readAllFile(String filename)  {
        File file = new File(filename);
        List<String> lineList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(file)
        )) {
            bufferedReader.lines().forEach(line -> lineList.add(line));
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
            return null;
        }
        return lineList;
    }

}
