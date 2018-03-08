package org.android.edlo.ble_weight_scale.java_class.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by EdLo on 2018/3/5.
 */

public class RecordItem {
    private long id, user_id, dateTime;
    private String date;
    private int year, month, dayOfMonth;
    private String time;
    private double weight;

    public RecordItem(){}

    public RecordItem(
            long id, int year, int month, int dayOfMonth, int hour, int minute,
            int second, double weight, long user_id) {
        this.id = id;
        this.weight = weight;
        this.user_id = user_id;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.time = hour+":" +minute+":"+second;
        this.date = year+ "/"+month+"/"+dayOfMonth;
    }

    public RecordItem(long id, long dateTime, double weight, long user_id) {
        this.id = id;
        this.dateTime = dateTime;
        this.weight = weight;
        this.user_id = user_id;

        Date date = new Date();
        date.setTime(dateTime);
        TimeZone timeZone = TimeZone.getDefault();
        Calendar c = Calendar.getInstance(timeZone);
        c.setTime(date);

        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH) + 1;
        this.dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        this.time = hour+":" +minute+":"+second;
        this.date = year+ "/"+month+"/"+dayOfMonth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setLocalDate(String date){
        this.date = date;
    }

    public void setLocalTime(String time){
        this.time = time;
    }

    public String getLocaleDate() {
        return this.date;
    }

    public String getLocaleTime() {
        return this.time;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public double getWeight(){
        return this.weight;
    }

    public void setUserId(long id) {
        this.user_id = id;
    }

    public long getUserId() {
        return this.user_id;
    }

    @Override
    public String toString() {
        String date = this.getLocaleDate();
        String time = this.getLocaleTime();
        return "date : " + date + " ; time : " + time;
    }
}
