package com.trungngo.asm2.utilities;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateStringParser {
    public static Date parseFromDateStringMMDDYYYY(String dateString) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String[] splitDateStr = dateString.split("/");

        int day = Integer.parseInt(splitDateStr[0]);
        int month = Integer.parseInt(splitDateStr[1]);
        int year = Integer.parseInt(splitDateStr[2]);

        return df.parse(month + "/" + day + "/" + year);
    }

    public static String parseFromDateObjectDDMMYYYY(Date date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }
}
