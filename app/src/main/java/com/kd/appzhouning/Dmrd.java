package com.kd.appzhouning;

public class Dmrd {
    public String zw;
    public String zf;
    public String gzh;
    public Dmrd setZw(String zw) {
        this.zw =  "<font color='yellow'><b>紫外线辐射：</b></font><font color='yellow'><b>" + zw + "mW/㎡</b></font>";

        return this;
    }

    public Dmrd setZf(String zf) {
        this.zf = "<font color='yellow'><b>总辐射：</b></font><font color='yellow'><b>" + zf + "W/㎡</b></font>";
        return this;
    }
    public Dmrd setGzh(String gzh) {
        this.gzh = "<font color='yellow'><b>光照：</b></font><font color='yellow'><b>" + gzh + "lx</b></font>";
        return this;
    }
}
