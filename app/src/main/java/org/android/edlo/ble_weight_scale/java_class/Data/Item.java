package org.android.edlo.ble_weight_scale.java_class.Data;

import java.util.Date;
import java.util.Locale;

/**
 * Created by EdLo on 2018/2/2.
 */

public class Item {

    private long id, user_id;
    private long dateTime;
    private double weight;

    public Item() {}

    public Item(long id, long dateTime, double weight, long user_id) {
        this.id = id;
        this.dateTime = dateTime;
        this.weight = weight;
        this.user_id = user_id;
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

    // 裝置區域的日期時間
    public String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date(dateTime));
    }

    // 裝置區域的日期
    public String getLocaleDate() {
        return String.format(Locale.getDefault(), "%tF", new Date(dateTime));
    }

    // 裝置區域的時間
    public String getLocaleTime() {
        return String.format(Locale.getDefault(), "%tR", new Date(dateTime));
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
