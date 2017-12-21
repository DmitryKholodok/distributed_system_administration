package by.kholodok.coursework.service_b.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitrykholodok on 12/13/17
 */

public class DigitsEntity {

    public List<Integer> digitList = new ArrayList<>();

    public void addDigit(int value) {
        digitList.add(value);
    }

    public List<Integer> getDigitList() {
        return digitList;
    }

}
