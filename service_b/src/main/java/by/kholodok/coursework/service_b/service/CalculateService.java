package by.kholodok.coursework.service_b.service;

import by.kholodok.coursework.service_b.entity.DigitsEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by dmitrykholodok on 12/10/17
 */

@Service
public class CalculateService {

    private int result;

    public int addition(DigitsEntity numbersEntity) {
        result = 0;
        numbersEntity.digitList
                .stream()
                .forEach(num -> result += num);
        return result;
    }

    public DigitsEntity jsonDeserialize(String jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        DigitsEntity resultEntity;
        try {
            resultEntity = mapper.readValue(jsonData, DigitsEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return resultEntity;
    }

}
