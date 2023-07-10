package com.kd.appzhouning;

import android.text.TextUtils;

public class Time {
    public String date;
    public String week;
    public String time;
    public void setAll(String all){
        if(TextUtils.isEmpty(all))return;
        String[] ss = all.split(" ");
        date = ss[0];
        week = ss[1];
        time = ss[2];
    }
    public Time(String s){
        setAll(s);
    }
}
