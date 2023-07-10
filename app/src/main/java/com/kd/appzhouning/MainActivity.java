package com.kd.appzhouning;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kd.appzhouning.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    public static DataModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        ViewModelProvider provider  = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        model = provider.get(DataModel.class);
        init();
        start();
    }

    public static  DataModel getModel() {
        return model;
    }

    public void start(){
        Intent intent = new Intent(this,ElementsService.class);
        startService(intent);
    }

    public void end(){
        Intent intent = new Intent(this,ElementsService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        end();
        super.onDestroy();
    }

    public void init(){
        model.start();
        model.date.observe(this, new Observer<Time>() {
            @Override
            public void onChanged(Time d) {
                mainBinding.setTime(d);
            }
        });
        model.dmrd.observe(this, new Observer<Dmrd>() {
            @Override
            public void onChanged(Dmrd d) {
                mainBinding.setDmrd(d);
            }
        });

        model.dmgd.observe(this, new Observer<Dmgd>() {
            @Override
            public void onChanged(Dmgd d) {
                mainBinding.setDmgd(d);
            }
        });
    }


}