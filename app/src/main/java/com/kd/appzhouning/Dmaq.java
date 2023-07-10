package com.kd.appzhouning;

public class Dmaq {
    public String so2="--",no2 = "--",o3 = "--",pm2="--",pm10 = "--";
    public  String wr = "",aq;
    public String aqcount;int index;
    public Dmaq setSo2(int so2) {

        if(index<=so2){
            index = so2;
            wr = "SO2";
        }
        if(so2<0)
            this.so2 = "<font color='yellow'><b>SO2浓度：</b></font><font color='yellow'><b>--ug/m³</b></font>";
        this.so2 = "<font color='yellow'><b>SO2浓度：</b></font><font color='green'><b>" + so2 + "ug/m³</b></font>";
        return this;
    }

    public Dmaq setNo2(int no2) {

        if(index<=no2){
            index = no2;
            wr = "NO2";
        }
        if(no2<0)this.pm2 ="<font color='yellow'><b>PM2.5浓度：</b></font><font color='yellow'><b>--ug/m³</b></font>";
        this.no2 = "<font color='yellow'><b>NO2浓度：</b></font><font color='yellow'><b>" + no2 + "ug/m³</b></font>";

        return this;
    }

    public Dmaq setO3(int o3) {


        if(index<=o3){
            index = o3;
            wr = "O3";
        }
        if(o3<0)this.pm2 ="<font color='yellow'><b>PM2.5浓度：</b></font><font color='green'><b>--ug/m³</b></font>";
        this.o3 = "<font color='yellow'><b>O3浓度：</b></font><font color='green'><b>" + o3 + "ug/m³</b></font>";
        return this;
    }

    public Dmaq setPm2(int pm2) {


        if(index<=pm2){
            index = pm2;
            wr = "PM2.5";
        }
        if(pm2<0)this.pm2 ="<font color='yellow'><b>PM2.5浓度：</b></font><font color='green'><b>--ug/m³</b></font>";
        this.pm2 ="<font color='yellow'><b>PM2.5浓度：</b></font><font color='green'><b>" + pm2 + "ug/m³</b></font>";
        return this;
    }

    public Dmaq setPm10(int pm10) {

        if(index<=pm10){
            index = pm10;
            wr = "PM10";
        }
        if(pm10<0)
            this.pm10 =  "<font color='yellow'><b>PM10浓度：</b></font><font color='green'><b>--ug/m³</b></font>";
        this.pm10 =  "<font color='yellow'><b>PM10浓度：</b></font><font color='green'><b>" + pm10 + "ug/m³</b></font>";
        return this;
    }
    public Dmaq bulid(){
        wr = "<font color='yellow'><b>首要污染物：</b></font><font color='red'><b>" + wr + "</b></font>";
        if(index< 51){
            aq = "优（一级）";
        }else if(index< 100){
            aq = "良（二级）";
        }else if(index< 150){
            aq = "轻度污染";
        }else if(index<200){
            aq = "中度污染";
        }else if(index< 300){
            aq = "重度污染";
        }else if(index< 500){
            aq = "严重污染";
        }
        aq="<font color='yellow'><b>空气质量：</b></font><font color='green'><b>" + aq + "</b></font>";

        aqcount = "<font color='yellow'><b>空气质量指数：</b></font><font color='green'><b>" + index + "</b></font>";
        return this;
    }
}
