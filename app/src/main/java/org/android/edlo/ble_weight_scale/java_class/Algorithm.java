package org.android.edlo.ble_weight_scale.java_class;

/**
 * Created by EdLo on 2018/2/22.
 */

public class Algorithm {
    public static String imperialToMetric(String ft, String in){
        int temp_ft = Integer.parseInt(ft);
        int temp_in;
        if(in != null){
            temp_in = Integer.parseInt(in);
        }else {
            temp_in = 0;
        }
        return ""+(Math.round((3048 * temp_ft + 254 * temp_in)/100));
    }

    public static String[] metricToImperial(String cm){
        int tmp_cm = Integer.parseInt(cm);
        int ft = tmp_cm*100/3048;
        long in = Math.round(((tmp_cm * 100 - ft * 3048) * 10/254 + 5)/10);
        return new String[]{""+ft, ""+in};
    }

    public static double[] year_Data(){
        return new double[]{};
    }

    public static double[] month_Data(){
        return new double[]{};
    }

    public static double[] week_Data(){
        return new double[]{};
    }

    public static double[] day_Data(){
        return new double[]{};
    }
}
