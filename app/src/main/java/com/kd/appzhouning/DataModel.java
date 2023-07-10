package com.kd.appzhouning;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DataModel extends ViewModel {
    MutableLiveData<Time> date = new MutableLiveData();
    MutableLiveData<Dmrd> dmrd = new MutableLiveData<>();
    MutableLiveData<Dmaq> dmaq = new MutableLiveData<>();
    MutableLiveData<Dmgd> dmgd = new MutableLiveData<>();
    public void setDate(Time d) {
        date.postValue(d);
    }

    public void setDmgd(Dmgd d) {
       dmgd.postValue(d);
    }

    public void setDmrd(Dmrd d) {
        dmrd.postValue(d);
    }

    public void setDmaq(Dmaq d) {
       dmaq.postValue(d);
    }
    Timer timer;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm", Locale.CHINA);
    public void start(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                date.postValue(new Time(sdf.format(new Date())));
            }
        }, 0, 60 * 1000);
    }
    @Override
    protected void onCleared() {

    }
}
