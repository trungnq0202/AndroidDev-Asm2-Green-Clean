package com.trungngo.asm2.utilities;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class for parsing Java date string and java.util.Date object
 */
public class DateStringParser {
    /**
     * Parse DateString to Date Object with format MM/dd/YYYY
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseFromDateStringMMDDYYYY(String dateString) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String[] splitDateStr = dateString.split("/");

        int day = Integer.parseInt(splitDateStr[0]);
        int month = Integer.parseInt(splitDateStr[1]);
        int year = Integer.parseInt(splitDateStr[2]);

        return df.parse(month + "/" + day + "/" + year);
    }

    /**
     * Parse Date Object to date string with format dd/MM/YYYY
     * @param date
     * @return
     * @throws ParseException
     */
    public static String parseFromDateObjectDDMMYYYY(Date date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }
}
