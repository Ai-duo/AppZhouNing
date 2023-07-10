package com.kd.appzhouning;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.xixun.joey.uart.BytesData;
import com.xixun.joey.uart.IUartListener;
import com.xixun.joey.uart.IUartService;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ElementsService extends Service {
    public IUartService uart;
    boolean start1 = false,start3 = false;
    boolean dmaq = false, dmrd = false;
    StringBuffer builder = new StringBuffer();
    boolean start = false;
    boolean dmgd = false;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            uart = IUartService.Stub.asInterface(iBinder);
            Log.i("TAG_uart", "================ onServiceConnected ====================");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("TAG_uart", "================== onServiceDisconnected ====================");
            uart = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG_Service", "服务开启");
        bindCardSystemUartAidl();
        startGetUart();
        final String in2 ="DMRD WXNo1 2023-05-10 16:28 111100000000000000000000001111000000000000000000000 389 69 465 1601 17788 3203 22024 1601 *E1\n" +
                "*B8\n" +
                "T";
        final String in3 ="DMAQ WXNo1 2023-05-10 16:28 1111111111111111111111111111111110000000000000000000000000000000000000000000000000000000000000000000000000000 0 / / / / 39 / / / / 5 / / / / 101 / / / / 14 / / / / 20 / / / / 121 804 10250 *9D\n" +
                "*7B\n" +
                "T";
        getDMRD(in2);
        getDMAQ(in3);

        final String in = "DMGD WX001 2021-03-17 16:45 1111111111111011111111111111110000000000000000000000000000000000001110000000000000000000000000000001100000000000000000000001000000000000000 00000000000000000080000000000000000 98 53 102 48 100 59 1605 98 54 101 95 1606 0 218 227 1601 218 1645 * 72 69 1601 188 165 10108 10108 1617 10106 1601 10126 0 00 98 71 000000000000000000000000000000000000000000000 *5B *33 T";
        getElements(in);
    }
    public void startGetUart() {
        startDMRD();
        startDMAQ();
    }

    Thread thread,thread2;
    StringBuffer dmaqBuffer = new StringBuffer();
    StringBuffer dmrdBuffer = new StringBuffer();

    String DMAQ ="TAG_DMAQ";
    String DMRD ="TAG_DMrD";

    private void startDMAQ() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(DMAQ, "正在获取uart======================");
                } while (null == uart);
                try {
                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i(DMAQ, "========获取到串口数据===========");
                            for (byte a : data.getData()) {
                                String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                char ss = (char) a;
                                Log.i("TAG_uart", "ss:" + ss + ";s1:" + s1);
                                if (ss == 'D') {
                                    dmgd = true;
                                    start = true;
                                    builder.append(ss);
                                }  else if (start) {
                                    start = true;
                                    builder.append(ss);
                                }
                                Log.i("TAG_uart123", builder.toString());
                                if (builder.length() == 1) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder.toString().equals("D") ) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        start = false;
                                    }
                                }
                                if (builder.length() == 2) {
                                    //||!builder.toString().equals("FE")
                                    if (!builder.toString().equals("DM") ) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;

                                        start = false;
                                    }
                                }
                                if (builder.length() == 4) {
                                    if (!builder.toString().equals("DMGD") ) {
                                        builder.delete(0, builder.length());
                                        dmgd = false;
                                        start = false;
                                    }
                                }
                                if ((dmgd && 'T' == ss)  && builder.length() > 4) {
                                    dmgd = false;

                                    start = false;
                                    Log.i("TAG_uart1234", builder.toString());
                                    getElements(builder.toString());
                                   // OkHttpUploadTool.getInstance().uploadString(UrlList.sitenum, "info:" + builder.toString());
                                    builder.delete(0, builder.length());
                                    Log.i("TAG_uart1234", "END");
                                }

                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void startDMRD() {
        thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(DMRD, "正在获取uart======================");
                } while (null == uart);
                try {
                    //监听/dev/ttyMT2，获取数据/dev/s3c2410_serial3
                    uart.read(port, new IUartListener.Stub() {
                        @Override
                        public void onReceive(BytesData data) throws RemoteException {
                            Log.i(DMRD, "========获取到串口数据===========");
                            for (byte a : data.getData()) {
                                String s1 = "0x" + Integer.toHexString(a & 0xFF) + " ";
                                char ss = (char) a;
                                Log.i(DMRD, "ss:" + ss + ";s1:" + s1);
                                if (start3) {
                                    start3 = true;
                                    dmrdBuffer.append(ss);
                                } else if (ss == 'D') {
                                    dmrd = true;
                                    start3 = true;
                                    dmrdBuffer.append(ss);
                                }
                                Log.i(DMRD,  dmrdBuffer.toString());
                                if ( dmrdBuffer.length() == 1) {
                                    if (! dmrdBuffer.toString().equals("D")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                } else if ( dmrdBuffer.length() == 2) {
                                    //||!builder.toString().equals("FE")
                                    if (! dmrdBuffer.toString().equals("DM")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                }else  if ( dmrdBuffer.length() == 3) {
                                    if (! dmrdBuffer.toString().equals("DMR")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                } else if ( dmrdBuffer.length() == 4) {
                                    //||!builder.toString().equals("FE")
                                    if (! dmrdBuffer.toString().equals("DMRD")) {
                                        dmrdBuffer.delete(0,  dmrdBuffer.length());
                                        dmrd = false;
                                        start3 = false;
                                    }
                                }
                                if ( dmrdBuffer.length() > 4 && ( dmrd &&  dmrdBuffer.substring( dmrdBuffer.length() - 1).equals("T"))) {
                                    dmrd = false;
                                    start3 = false;
                                    Log.i(DMRD,  dmrdBuffer.toString());
                                    getDMRD( dmrdBuffer.toString());
                                    dmrdBuffer.delete(0,  dmrdBuffer.length());
                                }

                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();
    }

    public void bindCardSystemUartAidl() {
        Intent intent = new Intent("xixun.intent.action.UART_SERVICE");
        intent.setPackage("com.xixun.joey.cardsystem");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    ArrayList<Integer> dmaqIndex = new ArrayList<Integer>();
    ArrayList<Integer> dmrdIndex = new ArrayList<Integer>();
    public int getDMAQCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        dmaqIndex.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0 || j == 5 || j == 15 || j == 20 || j == 25 ) {
                dmaqIndex.add(count - 1);
            }
        }
        return count;
    }
    //总辐射 0 紫外线34
    public int getDMRDCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        dmrdIndex.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0||j == 26||j==51 ) {
                dmrdIndex.add(count - 1);
            }
        }
        return count;
    }
    String port = "/dev/ttysWK2";//,/dev/ttysWK2   /dev/ttyMT3
    //总辐射 0 紫外线34
    String zfs = "--",zwx = "--",gzh="--";
    public void getDMRD(String info){
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (info.startsWith("DMRD") && (info.endsWith("F") || info.endsWith("T"))) {

            String[] infoss = info.split(" ");
            for (int i = 0; i < infoss.length; i++) {
                Log.i(DMRD, i + ":" + infoss[i]);
            }
            //日期
            String date = infoss[2];
            Log.i(DMRD, "日期:" + date);
            //时间
            String time = infoss[3];
            Log.i(DMRD, "时间:" + time);
            int count = getDMRDCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            try {
                if (infoss[4].charAt(0) == '1') {
                    String swd1 = infoss[qc + dmrdIndex.get(0)];
                    Log.i(DMRD, "zfs:" + swd1);

                    if (!isNum(swd1)) {
                        //fs = "";
                    } else {
                        zfs = (int)Float.parseFloat(swd1)+ "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMRD, "解析swd时出错");
            }
            try {
                if (infoss[4].charAt(26) == '1') {
                    String syd1 = infoss[qc + dmrdIndex.get(1)];
                    Log.i(DMRD, "zwx:" + syd1);

                    if (!isNum(syd1)) {
                        //fs = "";
                    } else {
                        zwx = (int)Float.parseFloat(syd1)  + "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMRD, "解析风速时出错");
            }
            try {
                if (infoss[4].charAt(51) == '1') {
                    String gzh1 = infoss[qc + dmrdIndex.get(2)];
                    Log.i(DMRD, "zwx:" + gzh1);

                    if (!isNum(gzh1)) {
                        //fs = "";
                    } else {
                        gzh = Double.parseDouble(gzh1)/100  + "";
                    }
                }
            } catch (Exception e) {
                Log.i(DMRD, "解析风速时出错");
            }
            MainActivity.getModel().setDmrd(new Dmrd().setZf(zfs).setZw(zwx).setGzh(gzh));
        }
    }

    int so2 = -1,no2= -1 ,o3= -1,pm2= -1,pm10 = -1;
    public void getDMAQ(String info) {

        Log.i(DMAQ, "getElements:");
        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (info.startsWith("DMAQ") && info.endsWith("T")) {

            String[] infoss = info.split(" ");
            for (int i = 0; i < infoss.length; i++) {
                Log.i(DMAQ, i + ":" + infoss[i]);
            }
            //日期
            String date = infoss[2];
            Log.i(DMAQ, "日期:" + date);
            //时间
            String time = infoss[3];
            Log.i(DMAQ, "时间:" + time);
            getDMAQCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }

            try {
                if (infoss[4].charAt(0) == '1') {
                    String ii = infoss[qc + dmaqIndex.get(0)];
                    Log.i(DMAQ, "so2:" + ii);
                    if (!isNum(ii)) {
                        //fs = "";
                    } else {
                        so2 = (int)Float.parseFloat(ii)  ;
                    }
                }
            } catch (Exception e) {
                Log.i(DMAQ, "解析风速时出错");
            }

            try {
                if (infoss[4].charAt(5) == '1') {
                    String ii = infoss[qc + dmaqIndex.get(1)];
                    Log.i(DMAQ, "风速:" + ii);
                    if (!isNum(ii)) {
                        //fs = "";
                    } else {
                        no2 = (int)Float.parseFloat(ii)  ;
                    }
                }
            } catch (Exception e) {
                Log.i(DMAQ, "解析风速时出错");
            }
            try {
                if (infoss[4].charAt(15) == '1') {
                    String ii = infoss[qc + dmaqIndex.get(2)];
                    Log.i(DMAQ, "风速:" + ii);
                    if (!isNum(ii)) {
                        //fs = "";
                    } else {
                        o3 = (int)Float.parseFloat(ii)  ;
                    }
                }
            } catch (Exception e) {
                Log.i(DMAQ, "解析风速时出错");
            }
            try {
                if (infoss[4].charAt(20) == '1') {
                    String ii = infoss[qc + dmaqIndex.get(3)];
                    Log.i(DMAQ, "风速:" + ii);
                    if (!isNum(ii)) {
                        //fs = "";
                    } else {
                        pm2 = (int)Float.parseFloat(ii) ;
                    }
                }
            } catch (Exception e) {
                Log.i(DMAQ, "解析风速时出错");
            }
            try {
                if (infoss[4].charAt(25) == '1') {
                    String ii = infoss[qc + dmaqIndex.get(4)];
                    Log.i(DMAQ, "风速:" + ii);
                    if (!isNum(ii)) {
                        //fs = "";
                    } else {
                        pm10 = (int)Float.parseFloat(ii) ;
                    }
                }
            } catch (Exception e) {
                Log.i(DMAQ, "解析风速时出错");
            }
        }
        MainActivity.getModel().setDmaq(new Dmaq().setNo2(no2).setO3(o3).setPm2(pm2).setPm10(pm10).setSo2(so2).bulid());
    }
    public boolean isNum(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) str);
        boolean result = matcher.matches();
        return result;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        unbindService(conn);
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        super.onDestroy();
    }
    ArrayList<Integer> index = new ArrayList<Integer>();
    public int getCharCount(String chars) {
        if (TextUtils.isEmpty(chars)) {
            return 0;
        }
        index.clear();
        char[] chars1 = chars.toCharArray();
        int count = 0;
        for (int j = 0; j < chars1.length; j++) {
            if (chars1[j] == '1') {
                count++;
            }
            if (j == 0 || j == 1 || j == 12 || j == 14 || j == 15 || j == 17 || j == 20 || j == 21 || j == 25 ||j == 35 || j == 55) {
                index.add(count - 1);
            }
        }
        return count;
    }

    //风向
    String fx = "--";//1
    //风速
    String fs = "--";//2
    //降水
    String js = "--";//13
    //温度
    String wd = "--";
    String dw = "--";
    //日最大温度
    String max_wd = "--";
    //日最小温度
    String min_wd = "--";
    //湿度
    String sd = "--";//21
    //日最小湿度
    String min_sd = "--";
    //气压
    String qy = "--";//26
    //能见度
    String njd = "--";//26
    static String WEA;
    //判断是否重启,每十分钟判断一次sendCount与currentCount的值，如果两者相等就重起；
    int sendCount = 0, currentCount = -1;

    public void getElements(String info) {
        //builder.delete(0, builder.length());
        //开始接受PM2.5数据
        Log.i("TAG", "getElements:");
        /*if (openPm) {
            PmFlag = true;
            try {
                uart.write(port, new byte[]{0x15, 0x03, 0X00, 0X64, 0X00, 0x08, 0x06, (byte) 0xc7});
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }*/

        if (TextUtils.isEmpty(info)) {
            return;
        }
        if (info.startsWith("DMGD") && (info.endsWith("F") || info.endsWith("T"))) {

            String[] infoss = info.split(" ");
            for (int i = 0; i < infoss.length; i++) {
                Log.i("TAG_uart", i + ":" + infoss[i]);
            }
            //日期
            String date = infoss[2];
            Log.i("TAG", "日期:" + date);
            //时间
            String time = infoss[3];
            Log.i("TAG", "时间:" + time);
            int count = getCharCount(infoss[4]);
            int qc = 5;
            if (infoss[5].length() >= 4) {
                qc = 6;
            }
            try {
                if (infoss[4].charAt(0) == '1') {
                    String fx1 = infoss[qc + index.get(0)];
                    Log.i("TAG", "风向:" + fx1);
                    if (isNum(fx1)) {
                        float f = Float.valueOf(fx1);
                        if ((f >= 0 && f < 12.25) || (f > 348.76 && f <= 360)) {
                            fx = "北";
                        } else if (f > 12.26 && f < 33.75) {//22.5
                            fx = "北偏东北";
                        } else if (f > 33.76 && f < 56.25) {
                            fx = "东北";
                        } else if (f > 56.25 && f < 78.75) {
                            fx = "东偏东北";
                        } else if (f > 78.75 && f < 101.25) {
                            fx = "东";
                        } else if (f > 101.25 && f < 123.75) {
                            fx = "东偏东南";
                        } else if (f > 123.76 && f < 146.25) {
                            fx = "东南";
                        } else if (f > 146.26 && f < 168.75) {
                            fx = "南偏东南";
                        } else if (f > 168.75 && f < 191.25) {
                            fx = "南";
                        } else if (f > 191.25 && f < 213.75) {
                            fx = "南偏西南";
                        } else if (f > 213.75 && f < 236.25) {
                            fx = "西南";
                        } else if (f > 236.25 && f < 258.75) {
                            fx = "西偏西南";
                        } else if (f > 258.75 && f < 281.25) {
                            fx = "西";
                        } else if (f > 281.25 && f < 303.75) {
                            fx = "西偏西北";
                        } else if (f > 303.75 && f < 326.25) {
                            fx = "西北";
                        } else if (f > 326.25 && f < 348.75) {
                            fx = "北偏西北";
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析风向时出错");
            }
            try {
                if (infoss[4].charAt(1) == '1') {
                    String fs1 = infoss[qc + index.get(1)];
                    Log.i("TAG", "风速:" + fs1);
                    if (!isNum(fs1)) {
                        //fs = "";
                    } else {
                        fs = Float.parseFloat(infoss[qc + index.get(1)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析风速时出错");
            }

            try {
                if (infoss[4].charAt(12) == '1') {
                    String js1 = infoss[qc + index.get(2)];
                    Log.i("TAG", "降水:" + js1);
                    if (!isNum(js1)) {
                        // js = "";
                    } else {
                        js = Float.parseFloat(infoss[qc + index.get(2)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析雨量时出错");
            }

            try {
                if (infoss[4].charAt(14) == '1') {
                    String wd1 = infoss[qc + index.get(3)];
                    if (wd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (wd1.charAt(0) == '-') {
                        if (wd1.length() <= 3) {
                            wd1 = wd1.substring(1, wd1.length());
                            if (isNum(wd1) && Float.parseFloat(wd1) < 580) {
                                wd = "-" + Float.parseFloat(wd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + index.get(3)]) && Float.parseFloat(infoss[qc + index.get(3)]) < 580) {
                            wd = Float.parseFloat(infoss[qc + index.get(3)]) / 10 + "";
                        }
                    }
                    Log.i("TAG", "温度:" + wd);
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(15) == '1') {
                    String max_wd1 = infoss[qc + index.get(4)];
                    if (max_wd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (max_wd1.charAt(0) == '-') {
                        if (max_wd1.length() <= 3) {
                            max_wd1 = max_wd1.substring(1, max_wd1.length());
                            if (isNum(max_wd1) && Float.parseFloat(max_wd1) < 580) {
                                max_wd = "-" + Float.parseFloat(max_wd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + index.get(4)]) && Float.parseFloat(infoss[qc + index.get(4)]) < 580) {
                            max_wd = Float.parseFloat(infoss[qc + index.get(4)]) / 10 + "";
                        }
                    }
                    Log.i("TAG", "日最高温度:" + max_wd);
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(17) == '1') {
                    String min_wd1 = infoss[qc + index.get(5)];
                    if (min_wd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (min_wd1.charAt(0) == '-') {
                        if (min_wd1.length() <= 3) {
                            min_wd1 = min_wd1.substring(1, min_wd1.length());
                            if (isNum(min_wd1) && Float.parseFloat(min_wd1) < 580) {
                                min_wd = "-" + Float.parseFloat(min_wd1) / 10;
                            }
                        }
                    } else {
                        if (isNum(infoss[qc + index.get(5)]) && Float.parseFloat(infoss[qc + index.get(5)]) < 580) {
                            min_wd = Float.parseFloat(infoss[qc + index.get(5)]) / 10 + "";
                        }
                    }
                    Log.i("TAG", "日最低温度:" + min_wd);
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析温度时出错");
            }
            try {
                if (infoss[4].charAt(20) == '1') {
                    String sd1 = infoss[qc + index.get(6)];
                    Log.i("TAG", "湿度:" + sd1);
                    if (!isNum(sd1)) {
                        //sd = "";
                    } else {
                        sd = sd1;
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析湿度时出错");
            }
            try {
                if (infoss[4].charAt(21) == '1') {
                    String min_sd1 = infoss[qc + index.get(7)];
                    Log.i("TAG", "湿度:" + min_sd1);
                    if (!isNum(min_sd1)) {
                        //sd = "";
                    } else {
                        min_sd = min_sd1;
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析湿度时出错");
            }
            try {
                if (infoss[4].charAt(25) == '1') {
                    String qy1 = infoss[qc + index.get(8)];
                    Log.i("TAG", "气压:" + qy1);
                    if (!isNum(qy1)) {
                        //qy = "";
                    } else {
                        qy = Float.parseFloat(infoss[qc + index.get(8)]) / 10 + "";
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析气压时出错");
            }
            try {
                if (infoss[4].charAt(35) == '1') {
                    //能见度
                    String wd1 = infoss[qc + index.get(9)];
                    if (wd1.charAt(0) == '/') {
                        //wd = " ";
                    } else if (wd1.charAt(0) == '-') {
                        if (wd1.length() <= 3) {
                            wd1 = wd1.substring(1, wd1.length());
                            if (isNum(wd1) && Float.parseFloat(wd1) < 580) {
                                dw = "-" + Float.parseFloat(wd1) / 10;
                                Log.i("TAG", "dw:" + dw);
                            }
                        }
                    } else {
                        if (isNum(wd1) && Float.parseFloat(wd1) < 580) {
                            dw = Float.parseFloat(wd1) / 10 + "";
                            Log.i("TAG", "dw:" + dw);
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("TAG_", "解析能见度时出错");
            }
            MainActivity.getModel().setDmgd(new Dmgd().setSd(sd).setWd(wd).setDw(dw));
        }

    }
}