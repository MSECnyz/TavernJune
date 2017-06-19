package com.msecnyz.tavernjune.onuwerewolf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msecnyz.tavernjune.FirstActivity;
import com.msecnyz.tavernjune.MainActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.mainfragment.UserFragment;
import com.msecnyz.tavernjune.net.SocketOperation;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FirstwwActivity extends AppCompatActivity {

    public String returnMsg,userName;
    private TextView userId;
    private Button offLine,quickgame;
    private LinearLayout teamLayout;
    private SocketOperation quickGame;
    private ArrayList<View> views = new ArrayList<View>();
    private final static String cancelJoin = "#取#消#匹#配";
    private final static String JoinIn = "#进#入#游#戏";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);
        setContentView(R.layout.onuwolf_first);

        initView();

        SharedPreferences sharedPreferences = this.getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userId","userId");

        userId = (TextView)findViewById(R.id.gameuserid);
        userId.setText(userName);

        teamLayout = (LinearLayout)findViewById(R.id.teamreadyLL);
        teamLayout.setVisibility(View.INVISIBLE);

        quickgame = (Button)findViewById(R.id.quickgame);
        quickgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quickgame.getText().equals("快速匹配")){
                    teamLayout.setVisibility(View.VISIBLE);
                    startQuickGame();
                    quickgame.setText("取消匹配");
                }else if (quickgame.getText().equals("取消匹配")){
                    int j = 0;
                    while (j<12){
                        views.get(j).setBackgroundColor(Color.parseColor("#FFFFFF"));
                        j++;
                    }
                    teamLayout.setVisibility(View.INVISIBLE);
                    quickgame.setText("快速匹配");
                    cancelQuickGame(cancelJoin);
                }
            }
        });

        offLine = (Button)findViewById(R.id.offlinebattle);
        offLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("extraData","offLine");
                intent.setClass(FirstwwActivity.this,GameAcitvity.class);
                startActivity(intent);
            }
        });

    }

    private void initView(){
        View teamView1 = findViewById(R.id.teamView1);
        View teamView2 = findViewById(R.id.teamView2);
        View teamView3 = findViewById(R.id.teamView3);
        View teamView4 = findViewById(R.id.teamView4);
        View teamView5 = findViewById(R.id.teamView5);
        View teamView6 = findViewById(R.id.teamView6);
        View teamView7 = findViewById(R.id.teamView7);
        View teamView8 = findViewById(R.id.teamView8);
        View teamView9 = findViewById(R.id.teamView9);
        View teamView10 = findViewById(R.id.teamView10);
        View teamView11= findViewById(R.id.teamView11);
        View teamView12 = findViewById(R.id.teamView12);
        views.add(teamView1);
        views.add(teamView2);
        views.add(teamView3);
        views.add(teamView4);
        views.add(teamView5);
        views.add(teamView6);
        views.add(teamView7);
        views.add(teamView8);
        views.add(teamView9);
        views.add(teamView10);
        views.add(teamView11);
        views.add(teamView12);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            returnMsg = (String)msg.obj;
            int whitch = Integer.parseInt(returnMsg);
            int j = 0;
            while (j<12){
                //重置色块
                views.get(j).setBackgroundColor(Color.parseColor("#FFFFFF"));
                j++;
            }
            for(int i=1;i<=whitch;i++){
                views.get(whitch-i).setBackgroundColor(Color.parseColor("#1AFA29"));
            }
            if (whitch == 2){
                Toast.makeText(FirstwwActivity.this,"匹配完成！",Toast.LENGTH_SHORT).show();
                cancelQuickGame(JoinIn);
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("extraData","quickGame");
                        intent.setClass(FirstwwActivity.this,GameAcitvity.class);
                        startActivity(intent);
                    }
                };
                timer.schedule(timerTask,1500);
            }
        }
    };

    private void startQuickGame(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                quickGame = new SocketOperation("192.168.199.48",8081);
                quickGame.setLink();
                quickGame.sendPlayerToQueue(userName,handler);
            }
        }).start();
    }

    private void cancelQuickGame(final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                quickGame.sendPlayerToQueue(msg,handler);
                quickGame.closeAll();
            }
        }).start();
    }
}
