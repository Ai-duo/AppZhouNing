package com.kd.appzhouning;

public class Dmgd {
    public String wd;
    public String sd;
    public String dw;
    public Dmgd setDw(String dw) {
        this.dw =  "<font color='yellow'><b>地面温度：</b></font><font color='yellow'><b>" + dw + "℃</b></font>";

        return this;
    }

    public Dmgd setWd(String wd) {
        this.wd = "<font color='yellow'><b>气温：</b></font><font color='yellow'><b>" + wd + "℃</b></font>";
        return this;
    }

    public Dmgd setSd(String sd) {
        this.sd = "<font color='yellow'><b>相对湿度：</b></font><font color='yellow'><b>" + sd + "%</b></font>";
        return this;
    }
}
