package com.ryanschafer.authorgenie.ui.utils;

import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static String formatDate(Date date){
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    public static String formatDate(long date){
        return formatDate(new Date(date));
    }

    public static String formatTime(Date date){
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    public static String formatTime(long date){
        return formatTime(new Date(date));
    }

    public static String formatDate(int year, int month, int day) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return dateFormat.format(calendar.getTime());
    }

    public static long getLong(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }
}
