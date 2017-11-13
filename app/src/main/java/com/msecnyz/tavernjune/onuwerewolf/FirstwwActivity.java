package com.msecnyz.tavernjune.onuwerewolf;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msecnyz.tavernjune.BaseActivity;
import com.msecnyz.tavernjune.FirstService;
import com.msecnyz.tavernjune.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FirstwwActivity extends BaseActivity {

    public String userName;
    private JSONObject returnMsg;
    private TextView userId;
    private Button offLine,quickgame;
    private LinearLayout teamLayout;
    private ArrayList<View> views = new ArrayList<View>();
    private final  String cancelJoin = "#取#消#匹#配";
    private final  String JoinIn = "#进#入#游#戏";
    private final String exit = "#退#出#游#戏";
    private boolean intoGame;
    private static int playerNumber = 2;
    private FirstService.MsgBinder myBinder;
    private GameService.GameBinder gameBinder;
    //private FirstService myService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId.setText(userName);
        teamLayout.setVisibility(View.INVISIBLE);

        intoGame = false;

    }

    @Override
    protected void setView() {
        super.setView();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);
        setContentView(R.layout.onuwolf_first);
    }

    @Override
    protected void activityReady() {
        super.activityReady();
        Intent startIntent = new Intent(this, FirstService.class);
        bindService(startIntent, serviceConnection, BIND_AUTO_CREATE);

        Intent gameServiceIntent = new Intent(this, GameService.class);
        startService(gameServiceIntent);
        bindService(gameServiceIntent, gameConnection, BIND_AUTO_CREATE);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userId","userId");
    }

    @Override
    protected void onDestroy() {
        if (!intoGame){
            sendMsgToServer(exit);
        }
        super.onDestroy();
        unbindService(serviceConnection);
        unbindService(gameConnection);
    }

    @Override
    protected void initViews() {
        super.initViews();
        userId = (TextView)findViewById(R.id.gameuserid);
        teamLayout = (LinearLayout)findViewById(R.id.teamreadyLL);
        quickgame = (Button)findViewById(R.id.quickgame);
        offLine = (Button)findViewById(R.id.offlinebattle);

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

    @Override
    protected void setListener() {
        super.setListener();
        quickgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quickgame.getText().equals("快速匹配")) {
                    gameBinder.startSocket(handler);
                    teamLayout.setVisibility(View.VISIBLE);
                    quickgame.setText("取消匹配");
                    //startQuickGame();
                } else if (quickgame.getText().equals("取消匹配")) {
                    cancelQuickGame();
                    int j = 0;
                    while (j < 12) {
                        views.get(j).setBackgroundColor(Color.parseColor("#FFFFFF"));
                        j++;
                    }
                    teamLayout.setVisibility(View.INVISIBLE);
                    quickgame.setText("快速匹配");
                    gameBinder.closeSocket();
                }
            }
        });


        offLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("extraData", "offLine");
                intent.setClass(FirstwwActivity.this, GameAcitvity.class);
                startActivity(intent);
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String recive = (String)msg.obj;
            int whitch = 0;
            try {
                returnMsg = new JSONObject(recive);
                whitch = (int)returnMsg.get("msg1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int j = 0;
            while (j<12){
                //重置色块
                views.get(j).setBackgroundColor(Color.parseColor("#FFFFFF"));
                j++;
            }
            for(int i=1;i<=whitch;i++){
                views.get(whitch-i).setBackgroundColor(Color.parseColor("#1AFA29"));
            }
            if (whitch == playerNumber){
                Toast.makeText(FirstwwActivity.this,"匹配完成！",Toast.LENGTH_SHORT).show();

                int aaa = 0;
                while (aaa<12){
                    views.get(aaa).setBackgroundColor(Color.parseColor("#FFFFFF"));
                    aaa++;
                }
                teamLayout.setVisibility(View.INVISIBLE);
                quickgame.setText("快速匹配");

                intoGame = true;

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
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType","#游#戏#匹#配");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gameBinder.sendMsgToServer(msgJson.toString());
    }
    private void cancelQuickGame(){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",cancelJoin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gameBinder.sendMsgToServer(msgJson.toString());;
    }

    private void sendMsgToServer(final String msg){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson.toString());
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        //我认为此处的myBinder调用一个函数相当于传递到了service自己new出来的那个binder来调用其内部类的方法，而作为内部类Binder又可以调用service的方法，从而达到service跑自己的函数的效果
        //同理反向通信并不能简单的传个Activity对象过去，相当于new一个对象对其操作，从而没有意义，需要用回调函数，使得调用的方法是Activity本身调用的
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定成功会执行这里
            myBinder = (FirstService.MsgBinder) service; //向下转型得到实例
            //Activity需要主动通信的时候，只需用binder调用方法来调用service内部的方法就好了。目前是这样的
            myBinder.setHandler(handler);

//            myService = myBinder.getService();
//            myService.setServiceListener(new FirstServiceListener() {
//                @Override
//                public void msg(String msg) {
//                    //此处就可以通过回调来实现让service通知Activity进行Activity想要的操作。
//                    //service需要主动通信的时候，只需在其class内需要的地方调用msg方法就好了。而重写的msg又是Act的，想必也是在该线程执行了
//                    //而接口也恰恰为两者之间的通信定下了标准（比如这个可以让我调用的函数就叫msg一类的，我写好了，你也照着写）
//
//                    //<用以被回调的方法>myResponse.setText(msg);
//                }
//            });

            sendMsgToServer(JoinIn); //通知服务器进入游戏了

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            //结束绑定会执行此处

        }
    };

    private ServiceConnection gameConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            gameBinder = (GameService.GameBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
