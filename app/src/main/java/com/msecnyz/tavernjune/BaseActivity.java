package com.msecnyz.tavernjune;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by neo on 2017/9/19.
 */

public class BaseActivity extends AppCompatActivity {
    //final public static boolean Debug = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityReady();
        setInterface();
        initViews();
        setListener();
    }

    protected void activityReady(){

    }

    protected void setInterface(){
        //设定初始界面
    }

    protected void initViews(){
        //初始化控件
    }

    protected void setListener(){

    }


}
