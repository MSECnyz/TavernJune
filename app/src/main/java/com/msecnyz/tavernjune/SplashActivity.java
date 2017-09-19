package com.msecnyz.tavernjune;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.msecnyz.tavernjune.net.HttpOperation;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    public static SplashActivity killMyself;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        killMyself = this;

        SharedPreferences sharedPreferences = getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("userId","我的ID");
        final String password = sharedPreferences.getString("password","-1");
        final String autoLogIn = sharedPreferences.getString("autoLogIn","false");

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                final Intent intentS = new Intent();
                intentS.setClass(SplashActivity.this,MainActivity.class);
                intentS.putExtra("extraData","autoLogIn");
                final Intent intentF = new Intent();
                intentF.setClass(SplashActivity.this,FirstActivity.class);
                intentF.putExtra("extraData","default");
                //是否自动登陆
                if (autoLogIn.equals("true")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpOperation httpOperation = new HttpOperation();
                            try {
                                String response = httpOperation.sendUserInformation(userId,password,"Nothing","2");
                                if (response.equals("登录成功")){
                                    SplashActivity.this.startActivity(intentS);
                                }else {
                                    SplashActivity.this.startActivity(intentF);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                SplashActivity.this.startActivity(intentF);
                            }
                        }
                    }).start();
                }else {
                    SplashActivity.this.startActivity(intentF);
                }
            }
        };
        timer.schedule(timerTask,1000);
    }
}
