package com.revature.registrar.util;

import java.io.BufferedReader;
import java.util.Calendar;

/**
 * Helper class that takes User input and returns an appropriate Calendar object
 */
public class CalendarBuilder {

    private BufferedReader consoleReader;

    public CalendarBuilder(BufferedReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    /**
     * Helper class which takes in user input and creates a Calendar Object
     * @return
     * @throws Exception
     */
    public long build() throws Exception {
        System.out.print("Date (MM/DD/YYYY):\n> ");
        String response = consoleReader.readLine();

        String[] vals = response.split("/");
        int month = 0;
        int day = 0;
        int year = 0;
        try {
            month = Integer.parseInt(vals[0]);
            day = Integer.parseInt(vals[1]);
            year = Integer.parseInt(vals[2]);
        } catch (Exception e) {
            return -1;
        }

        if(month <= 0 || month > 12) return -1;
        if(day <= 0 || day > 31) return -1;
        if(year < 2021) return -1;

        System.out.print("Time (HH:MM):\n> ");

        response = consoleReader.readLine();

        String[] vals2 = response.split(":");
        int hour;
        int minute;
        try {
            hour = Integer.parseInt(vals2[0]);
            minute = Integer.parseInt(vals2[1]);
        } catch (Exception e) {
            return -1;
        }

        if(hour <= 0 || hour >= 24) return -1;
        if(minute < 0 || minute >= 60) return -1;


        Calendar date = new Calendar.Builder()
                .setCalendarType("iso8601")
                .setDate(year, month - 1, day)
                .setTimeOfDay(hour, minute,0)
                .build();

        return date.getTimeInMillis();
    }
}
