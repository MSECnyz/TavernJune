package com.msecnyz.tavernjune.onuwerewolf;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.msecnyz.tavernjune.BaseActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.GameMsgAdapter;
import com.msecnyz.tavernjune.listitem.ImageTextItem;
import com.msecnyz.tavernjune.listitem.UWolfHeroAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameAcitvity extends BaseActivity {

    final private String TAG = "GameActivity";

    private ImageView firstSelect,secondSelect,player1,player2,player3,player4,player5,player6,player7,
            player8,player9,player10,player11,player12,centercard1,centercard2,centercard3,pickHero1,
            pickHero2,pickHero3,pickHero4,pickHero5,pickHero6,pickHero7,pickHero8,pickHero9,
            pickHero10,pickHero11,pickHero12;

    //centerCard1是中央狼牌
    private Button abChat,abSend,abCancel,audioChat,exit,pickConfirm;

    private TextView bpMsg,playername1,playername2,playername3,playername4,playername5,playername6, playername7,
            playername8,playername9,playername10,playername11,playername12,pickTime,windowGameMsg,gameStateText;

    private LinearLayout leftLL,rightLL,backLL,playerLLL1,playerLLL2,playerLLL3,playerLLL4,playerLLL5,
            playerLLL6,playerLLL7,playerLLL8, playerLLL9,playerLLL10,playerLLL11,playerLLL12;

    private RecyclerView msgRecyclerView;
    private EditText abcText;
    private PopupWindow pickWindow;
    private  GridView gridView;

    //TODO:节约资源
    private List<GameMsg> msgList = null;
    private List<TextView> playerNameViewList = new ArrayList<>();
    ArrayList<String> playerNameList;
    private List<LinearLayout> playerLocationList = new ArrayList<>();
    private List<ImageTextItem> heroList = new ArrayList<>();
    private ArrayList<ImageView> selectedList = new ArrayList<>(); //选择阶段存放英雄image，游戏阶段被重置存放card位置
    private ArrayList<String> heroNumber = new ArrayList<>();
    private ArrayList<String> wolfList = new ArrayList<>();
    private GameMsgAdapter adapter;
    private String myUserName,pickHeroName;
    private String whosName = "#p$l%a&y^e$r";
    private String whichHeroMine = null;

    final private String gameStateMsg0 = "#准#备#就#绪";
    final private String gameStateMsg1 = "#安#排#完#毕";
    final private String gameStateMsg2 = "#选#择#完#毕";
    final private String gameStateMsg3 = "#文#字#聊#天";
    final private String gameStateMsg4 = "#本#局#结#束";
    final private String gameStateMsg5 = "#开#始#行#动";
    final private String gameStateMsg6 = "#进#行#回#合";
    final private String gameStateMsg7 = "#回#合#结#束";
    final private String gameStateMsg8 = "#开#始#发#言";
    final private String gameStateMsg9 = "#发#言#结#束";
    final private String gameStateMsg10 = "#开#始#投#票";
    final private String gameStateMsg11 = "#投#票#结#束";
    final private String gameStateMsg12 = "overInfo";

    final private static String CANCELWINDOW = "cancel";
    private boolean beSelected = false;
    private boolean onlyWolfOr = false;
    //private boolean ghostHeroFlag = false; //记录最初自己是不是幽灵
    private int countdown = 10;
    final private static int NO_NEED_PICK = 0;
    final private static int NEED_PICK = 1;
    final private static int PICKING = 2;
    final private static int PICKOVER = 3;
    private static int pickOrNot = -1;
    private int startHeroNumber = 10;
    private int startwolfNumber = 3;
    private int userNumber = -1;

    private GameService.GameBinder myBinder;
    private JSONObject userToHero;
    private PopupWindow lastMsgWindow; //用以解散上次的msgWindow


//    ######################AsyncTask?刷新动态更新的时候用吧
    private Handler handler = new Handler(){
        //若Activity没创建完成就收到消息会报异常
        @Override
        public void handleMessage(Message msg) {
            if (pickOrNot == NEED_PICK){
                startPickHero(msg);
            }else if (pickOrNot == PICKING){
                heroBeSelected(msg);
            }else if (pickOrNot == PICKOVER){
                myHero(msg);
            }else if (pickOrNot == NO_NEED_PICK) {
                nowLetsDoSTH(msg);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        leftLL = (LinearLayout) findViewById(R.id.leftLL);
        rightLL = (LinearLayout)findViewById(R.id.rightLL);
        backLL = (LinearLayout)findViewById(R.id.backLL);


        Intent intentall = getIntent();

        initMsg();
        msgRecyclerView = (RecyclerView)findViewById(R.id.game_msg_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);//设置布局方式
        msgRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, -15, 0, -15);//设置item中内容相对边框左，上，右，下距离
            }
        });
        adapter = new GameMsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        if (intentall.getStringExtra("extraData").equals("quickGame")) {

            Intent serviceIntent = new Intent(this, GameService.class);
            bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

            pickOrNot = NEED_PICK;
        }else {
            leftLL.setVisibility(View.VISIBLE);
            rightLL.setVisibility(View.VISIBLE);
            backLL.setBackgroundResource(0);
            pickOrNot = NO_NEED_PICK;
        }

    }

    @Override
    protected void setInterface() {
        super.setInterface();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);
        setContentView(R.layout.onuwolf_game);

    }

    @Override
    protected void activityReady() {
        super.activityReady();

        SharedPreferences sharedPreferences = this.getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        myUserName = sharedPreferences.getString("userId","userId");
        Log.d(TAG,"NAMENAMENAMENAME"+myUserName);
    }

    @Override
    protected void initViews() {
        super.initViews();
        abChat = (Button)findViewById(R.id.abchat);
        audioChat = (Button)findViewById(R.id.audiochat);
        exit = (Button)findViewById(R.id.wolfgame_exit);

        audioChat.setText("确认");

        player1 = (ImageView)findViewById(R.id.player1);
        Glide.with(this).load(R.drawable.temp2).into(player1);
        player2 = (ImageView)findViewById(R.id.player2);
        Glide.with(this).load(R.drawable.temp2).into(player2);
        player3 = (ImageView)findViewById(R.id.player3);
        Glide.with(this).load(R.drawable.temp2).into(player3);
        player4 = (ImageView)findViewById(R.id.player4);
        Glide.with(this).load(R.drawable.temp2).into(player4);
        player5 = (ImageView)findViewById(R.id.player5);
        Glide.with(this).load(R.drawable.temp2).into(player5);
        player6 = (ImageView)findViewById(R.id.player6);
        Glide.with(this).load(R.drawable.temp2).into(player6);
        player7 = (ImageView)findViewById(R.id.player7);
        Glide.with(this).load(R.drawable.temp2).into(player7);
        player8 = (ImageView)findViewById(R.id.player8);
        Glide.with(this).load(R.drawable.temp2).into(player8);
        player9 = (ImageView)findViewById(R.id.player9);
        Glide.with(this).load(R.drawable.temp2).into(player9);
        player10 = (ImageView)findViewById(R.id.player10);
        Glide.with(this).load(R.drawable.temp2).into(player10);
        player11= (ImageView)findViewById(R.id.player11);
        Glide.with(this).load(R.drawable.temp2).into(player11);
        player12 = (ImageView)findViewById(R.id.player12);
        Glide.with(this).load(R.drawable.temp2).into(player12);

        centercard1 = (ImageView)findViewById(R.id.centercard1);
        Glide.with(this).load(R.drawable.temp2).into(centercard1);
        centercard2 = (ImageView)findViewById(R.id.centercard2);
        Glide.with(this).load(R.drawable.temp2).into(centercard2);
        centercard3 = (ImageView)findViewById(R.id.centercard3);
        Glide.with(this).load(R.drawable.temp2).into(centercard3);

        playerLLL1 = (LinearLayout)findViewById(R.id.playerLLL1);
        playerLocationList.add(playerLLL1);
        playerLLL2 = (LinearLayout)findViewById(R.id.playerLLL2);
        playerLocationList.add(playerLLL2);
        playerLLL3 = (LinearLayout)findViewById(R.id.playerLLL3);
        playerLocationList.add(playerLLL3);
        playerLLL4 = (LinearLayout)findViewById(R.id.playerLLL4);
        playerLocationList.add(playerLLL4);
        playerLLL5 = (LinearLayout)findViewById(R.id.playerLLL5);
        playerLocationList.add(playerLLL5);
        playerLLL6 = (LinearLayout)findViewById(R.id.playerLLL6);
        playerLocationList.add(playerLLL6);
        playerLLL7 = (LinearLayout)findViewById(R.id.playerLLL7);
        playerLocationList.add(playerLLL7);
        playerLLL8 = (LinearLayout)findViewById(R.id.playerLLL8);
        playerLocationList.add(playerLLL8);
        playerLLL9 = (LinearLayout)findViewById(R.id.playerLLL9);
        playerLocationList.add(playerLLL9);
        playerLLL10 = (LinearLayout)findViewById(R.id.playerLLL10);
        playerLocationList.add(playerLLL10);
        playerLLL11 = (LinearLayout)findViewById(R.id.playerLLL11);
        playerLocationList.add(playerLLL11);
        playerLLL12 = (LinearLayout)findViewById(R.id.playerLLL12);
        playerLocationList.add(playerLLL12);

        playername1 = (TextView)findViewById(R.id.player_name1);
        playerNameViewList.add(playername1);
        playername2 = (TextView)findViewById(R.id.player_name2);
        playerNameViewList.add(playername2);
        playername3 = (TextView)findViewById(R.id.player_name3);
        playerNameViewList.add(playername3);
        playername4 = (TextView)findViewById(R.id.player_name4);
        playerNameViewList.add(playername4);
        playername5 = (TextView)findViewById(R.id.player_name5);
        playerNameViewList.add(playername5);
        playername6 = (TextView)findViewById(R.id.player_name6);
        playerNameViewList.add(playername6);
        playername7 = (TextView)findViewById(R.id.player_name7);
        playerNameViewList.add(playername7);
        playername8 = (TextView)findViewById(R.id.player_name8);
        playerNameViewList.add(playername8);
        playername9 = (TextView)findViewById(R.id.player_name9);
        playerNameViewList.add(playername9);
        playername10 = (TextView)findViewById(R.id.player_name10);
        playerNameViewList.add(playername10);
        playername11 = (TextView)findViewById(R.id.player_name11);
        playerNameViewList.add(playername11);
        playername12 = (TextView)findViewById(R.id.player_name12);
        playerNameViewList.add(playername12);

        gameStateText = (TextView)findViewById(R.id.gameStateText);
        gameStateText.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        super.setListener();

        abChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatWindow();
            }
        });
        audioChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioChat.getText().toString().equals("确认")){
                    HashMap<ImageView,String> centerCardToHero = new HashMap<>();
                    try {
                        centerCardToHero.put(centercard1,userToHero.getString("centerCard1"));
                        centerCardToHero.put(centercard2,userToHero.getString("centerCard2"));
                        centerCardToHero.put(centercard3,userToHero.getString("centerCard3"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (firstSelect!=null) {
                        switch (whichHeroMine) {
                            case "狼Alpha":
                                exchangeCard(firstSelect, centercard1);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.Alpha狼),positionToUser(firstSelect),"centerCard1");
                                    }
                                },1000);
                                break;
                            case "狼先知":
                                String hero =  positionToHero(firstSelect);
                                if (hero.contains("幽灵"))hero = "幽灵";
                                sendSystemMsg("你所查看的角色是 ***" + hero + "***");
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.狼先知));
                                    }
                                },5000);
                                break;
                            case "幽灵":
                                String hero0 = positionToHero(firstSelect);
                                sendSystemMsg("你所查看的角色是 ***" + hero0 + "***");
                                doGhostDo(hero0);
                                break;
                            case "失眠者":
                                sendMsgToServer(gameStateMsg7,getString(R.string.失眠者));
                                break;
                            case "强盗":
                                String hero1 =  positionToHero(firstSelect);
                                if (hero1.contains("幽灵"))hero1 = "幽灵";
                                sendSystemMsg("你所查看的角色是 ***" + hero1 + "***");
                                secondSelect = userToPosition(myUserName);
                                exchangeCard(firstSelect,secondSelect);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.强盗),myUserName,positionToUser(firstSelect));
                                    }
                                },5000);
                                break;
                            case "预言家":
                                String hero2;
                                if (firstSelect==centercard1||firstSelect==centercard2||firstSelect==centercard3){
                                    hero2 = centerCardToHero.get(firstSelect);
                                }else {
                                    hero2 = positionToHero(firstSelect);
                                }
                                //TODO: 不能显示幽灵XXX的名字
                                if (hero2.contains("幽灵"))hero2 = "幽灵";
                                sendSystemMsg("你所查看的角色是 ***" + hero2 + "***");
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.预言家));
                                    }
                                },5000);
                                break;
                            case "捣蛋鬼":
                                exchangeCard(firstSelect,secondSelect);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.捣蛋鬼),positionToUser(firstSelect),positionToUser(secondSelect));
                                    }
                                },1000);
                                break;
                            case "女巫":
                                String hero3 =  centerCardToHero.get(firstSelect);
                                if (hero3.contains("幽灵"))hero3 = "幽灵";
                                sendSystemMsg("你所查看的角色是 ***" + hero3 + "***");
                                whichHeroMine = "女巫2";
                                doIDo("女巫2",null);
                                break;
                            case "女巫2":
                                exchangeCard(firstSelect,secondSelect);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.女巫),positionToUser(firstSelect),positionToUser(secondSelect));
                                    }
                                },1000);
                                break;
                            case "幽灵Alpha狼":
                                exchangeCard(firstSelect, centercard1);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵Alpha狼",positionToUser(firstSelect),"centerCard1",myUserName);
                                    }
                                },1000);
                                break;
                            case "幽灵捣蛋鬼":
                                exchangeCard(firstSelect,secondSelect);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵捣蛋鬼",positionToUser(firstSelect),positionToUser(secondSelect),myUserName);
                                    }
                                },1000);
                                break;
                            case "幽灵女巫":
                                String hero4 =  centerCardToHero.get(firstSelect);
                                sendSystemMsg("你所查看的角色是 ghost***" + hero4 + "***ghost");
                                whichHeroMine = "幽灵女巫2";
                                doGhostDo(whichHeroMine);
                                break;
                            case "幽灵女巫2":
                                exchangeCard(firstSelect,secondSelect);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵女巫",positionToUser(firstSelect),positionToUser(secondSelect),myUserName);
                                    }
                                },1000);
                                break;
                            case "幽灵强盗":
                                String hero5 =  positionToHero(firstSelect);
                                sendSystemMsg("你所查看的角色是 ghost***" + hero5 + "***ghost");
                                secondSelect = userToPosition(myUserName);
                                exchangeCard(firstSelect,secondSelect);
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵强盗",myUserName,positionToUser(firstSelect),myUserName);
                                    }
                                },5000);
                                break;
                            case "幽灵预言家":
                                String hero6;
                                if (firstSelect==centercard1||firstSelect==centercard2||firstSelect==centercard3){
                                    hero6 = centerCardToHero.get(firstSelect);
                                }else {
                                    hero6 = positionToHero(firstSelect);
                                }
                                sendSystemMsg("你所查看的角色是 ghost***" + hero6 + "***ghost");
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵预言家",myUserName,myUserName,myUserName);
                                    }
                                },5000);
                                break;
                            case "幽灵狼先知":
                                String hero7 =  positionToHero(firstSelect);
                                sendSystemMsg("你所查看的角色是 ghost***" + hero7 + "***ghost");
                                backLL.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵狼先知",myUserName,myUserName,myUserName);
                                    }
                                },5000);
                                break;
                            case "finalKill":
                                sendMsgToServer(gameStateMsg11,positionToUser(firstSelect));
                                sendSystemMsg("你选择了*"+positionToUser(firstSelect)+"*作为投票结果");
                                break;
                        }
                    }else {
                        Toast.makeText(getBaseContext(),"请选择卡牌",Toast.LENGTH_SHORT).show();
                    }

                    refreshCardColor(); // 顺便重置颜色
                    //audioChat.setText("按住讲话");
                }else {
                    //audioChat.setText("确认");
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameAcitvity.this);
                dialog.setTitle("离开游戏");
                dialog.setMessage("为了大家的游戏体验请勿中途退场");
                dialog.setCancelable(false);
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject exitJson = new JSONObject();
                        try {
                            exitJson.put("msgType", gameStateMsg4);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        myBinder.sendMsgToServer(exitJson.toString());
                        myBinder.closeSocket();
                        GameAcitvity.this.finish();
                    }
                });
                dialog.show();
            }
        });
    }

    private void initMsg(){
        msgList = new ArrayList<>();
        GameMsg msg1 = new GameMsg("游戏日志: 游戏即将开始",GameMsg.TYPE_SYSTEM);
        msgList.add(msg1);
        GameMsg msg2 = new GameMsg("狼预言: And?",GameMsg.TYPE_RECEIVE);
        msgList.add(msg2);
        GameMsg msg3 = new GameMsg("我: Come on!",GameMsg.TYPE_SENT);
        msgList.add(msg3);
    }

    private void chatWindow(){
        final PopupWindow chatWindow = new PopupWindow(this);
        View view = LayoutInflater.from(this).inflate(R.layout.onuwolf_chatin,null);
        chatWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        chatWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        chatWindow.setContentView(view);
        ColorDrawable cw = new ColorDrawable(Color.parseColor("#90000000"));
        chatWindow.setBackgroundDrawable(cw);
        chatWindow.setFocusable(true);//使返回键无响应并处理点击事件
        chatWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//使布局不随键盘移动
        chatWindow.showAtLocation(backLL, Gravity.TOP,0,0);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        };
        timer.schedule(timerTask,300);
        abSend = (Button)view.findViewById(R.id.absend);
        abCancel = (Button)view.findViewById(R.id.abcancel);
        abcText = (EditText)view.findViewById(R.id.abctext);

        abSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = abcText.getText().toString();
                if (!"".equals(content)){
                    JSONObject chatJson = new JSONObject();
                    try {
                        chatJson.put("msgType",gameStateMsg3);
                        chatJson.put("msg1",content);
                        chatJson.put("msg2",myUserName);
                        
                        myBinder.sendMsgToServer(chatJson.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    GameMsg msg = new GameMsg("我: "+content,GameMsg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);//当有新消息时刷新显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);//翻到最后一行
                    abcText.setText("");
                }
            }
        });
        abCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatWindow.dismiss();
            }
        });

    }

    private void gameMsgWindow(String msg){
        if (lastMsgWindow!=null)lastMsgWindow.dismiss();
        final PopupWindow gameMsgWindow = new PopupWindow(this);
        lastMsgWindow = gameMsgWindow;
        View view = LayoutInflater.from(this).inflate(R.layout.onuwolf_gamemsgwindow,null);
        gameMsgWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        gameMsgWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        gameMsgWindow.setContentView(view);
        ColorDrawable cw = new ColorDrawable(Color.parseColor("#FA000000"));
        gameMsgWindow.setBackgroundDrawable(cw);
        gameMsgWindow.setFocusable(false);//使按外部不取消
        gameMsgWindow.showAtLocation(backLL, Gravity.CENTER,0,0);
        windowGameMsg = (TextView)view.findViewById(R.id.game_msg_window);
        if (msg.equals(CANCELWINDOW)){
            gameMsgWindow.dismiss();
        }else {
            windowGameMsg.setText(msg);
        }

    }

    private void pickWindow(){
        //TODO: 在上个玩家时选择的角色轮到自己时不是选中状态。能否判断一下只要红就被选中。
        View view = LayoutInflater.from(this).inflate(R.layout.onuwolf_pickhero,null);
        pickWindow = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        if (heroList.size()==0){
            initList();
        }
        final UWolfHeroAdapter heroAdapter = new UWolfHeroAdapter(GameAcitvity.this,R.layout.onuwolf_heroitem,heroList);
        gridView = (GridView) view.findViewById(R.id.herogrid);
        bpMsg = (TextView)view.findViewById(R.id.bp_msg);
        pickConfirm = (Button)view.findViewById(R.id.pickconfirm);

        pickHero1 = (ImageView)view.findViewById(R.id.pickhero1);
        selectedList.add(pickHero1);
        pickHero2 = (ImageView)view.findViewById(R.id.pickhero2);
        selectedList.add(pickHero2);
        pickHero3 = (ImageView)view.findViewById(R.id.pickhero3);
        selectedList.add(pickHero3);
        pickHero4 = (ImageView)view.findViewById(R.id.pickhero4);
        selectedList.add(pickHero4);
        pickHero5 = (ImageView)view.findViewById(R.id.pickhero5);
        selectedList.add(pickHero5);
        pickHero6 = (ImageView)view.findViewById(R.id.pickhero6);
        selectedList.add(pickHero6);
        pickHero7 = (ImageView)view.findViewById(R.id.pickhero7);
        selectedList.add(pickHero7);
        pickHero8 = (ImageView)view.findViewById(R.id.pickhero8);
        selectedList.add(pickHero8);
        pickHero9 = (ImageView)view.findViewById(R.id.pickhero9);
        selectedList.add(pickHero9);
        pickHero10 = (ImageView)view.findViewById(R.id.pickhero10);
        selectedList.add(pickHero10);
        pickHero11 = (ImageView)view.findViewById(R.id.pickhero11);
        selectedList.add(pickHero11);
        pickHero12 = (ImageView)view.findViewById(R.id.pickhero12);
        selectedList.add(pickHero12);

        pickTime = (TextView)view.findViewById(R.id.picktime);
        gridView.setAdapter(heroAdapter);
        ColorDrawable cw = new ColorDrawable(Color.parseColor("#DF000000"));
        pickWindow.setBackgroundDrawable(cw);
        pickWindow.setFocusable(false);
        pickWindow.showAtLocation(backLL, Gravity.TOP,0,0);

        pickConfirm.setVisibility(View.INVISIBLE);
        pickConfirm.setEnabled(false);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickHeroName = heroAdapter.changeSelectItem(position);
                beSelected = true;
            }
        });
        pickConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftLL.setVisibility(View.VISIBLE);
                rightLL.setVisibility(View.VISIBLE);
                backLL.setBackgroundResource(0);

                if (beSelected) {

                    JSONObject pickJson = new JSONObject();
                    try {
                        pickJson.put("msgType", gameStateMsg2);
                        pickJson.put("msg1",pickHeroName);
                        pickJson.put("msg2",myUserName);
                        Log.d(TAG,"aaaaaaaaaaaaaaaaa"+pickJson.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    myBinder.sendMsgToServer(pickJson.toString());

                    pickConfirm.setVisibility(View.INVISIBLE);
                    pickConfirm.setEnabled(false);
                }else {
                    autoPickHero();
                }

                if (onlyWolfOr){
                    onlyWolfOr = onlyWolf(1);
                }
            }
        });
        sendMsgToServer(gameStateMsg1,myUserName);
    }


    private void heroBeSelected(String heroName,int userNumber){
        switch (heroName){
            case "狼Alpha":
                startHeroNumber--;
                startwolfNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.Alpha狼));
                wolfList.remove(GameAcitvity.this.getString(R.string.Alpha狼));
                gridView.getChildAt(0).setVisibility(View.INVISIBLE);
                gridView.getChildAt(0).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_alphawolf).into(selectedList.get(userNumber-1));
                break;
            case "狼人":
                startHeroNumber--;
                startwolfNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.狼人));
                wolfList.remove(GameAcitvity.this.getString(R.string.狼人));
                gridView.getChildAt(1).setVisibility(View.INVISIBLE);
                gridView.getChildAt(1).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_werewolf).into(selectedList.get(userNumber-1));
                break;
            case "狼先知":
                startHeroNumber--;
                startwolfNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.狼先知));
                wolfList.remove(GameAcitvity.this.getString(R.string.狼先知));
                gridView.getChildAt(2).setVisibility(View.INVISIBLE);
                gridView.getChildAt(2).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_mysticwolf).into(selectedList.get(userNumber-1));

                break;
            case "幽灵":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.幽灵));
                gridView.getChildAt(3).setVisibility(View.INVISIBLE);
                gridView.getChildAt(3).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_doppelganger).into(selectedList.get(userNumber-1));
                break;
            case "失眠者":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.失眠者));
                gridView.getChildAt(4).setVisibility(View.INVISIBLE);
                gridView.getChildAt(4).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_insomniac).into(selectedList.get(userNumber-1));
                break;
            case "皮匠":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.皮匠));
                gridView.getChildAt(5).setVisibility(View.INVISIBLE);
                gridView.getChildAt(5).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_minion).into(selectedList.get(userNumber-1));
                break;
            case "强盗":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.强盗));
                gridView.getChildAt(6).setVisibility(View.INVISIBLE);
                gridView.getChildAt(6).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_robber).into(selectedList.get(userNumber-1));
                break;
            case "预言家":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.预言家));
                gridView.getChildAt(7).setVisibility(View.INVISIBLE);
                gridView.getChildAt(7).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_seer).into(selectedList.get(userNumber-1));
                break;
            case "捣蛋鬼":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.捣蛋鬼));
                gridView.getChildAt(8).setVisibility(View.INVISIBLE);
                gridView.getChildAt(8).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_troublemaker).into(selectedList.get(userNumber-1));
                break;
            case "女巫":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.女巫));
                gridView.getChildAt(9).setVisibility(View.INVISIBLE);
                gridView.getChildAt(9).setEnabled(false);
                Glide.with(this).load(R.drawable.ulw_witch).into(selectedList.get(userNumber-1));
                break;
        }
    }

    private boolean onlyWolf(int type){
        if (type == 0) {
            gridView.getChildAt(3).setVisibility(View.INVISIBLE);
            gridView.getChildAt(4).setVisibility(View.INVISIBLE);
            gridView.getChildAt(5).setVisibility(View.INVISIBLE);
            gridView.getChildAt(6).setVisibility(View.INVISIBLE);
            gridView.getChildAt(7).setVisibility(View.INVISIBLE);
            gridView.getChildAt(8).setVisibility(View.INVISIBLE);
            gridView.getChildAt(9).setVisibility(View.INVISIBLE);
            return true;
        }else if (type == 1){
            gridView.getChildAt(3).setVisibility(View.VISIBLE);
            gridView.getChildAt(4).setVisibility(View.VISIBLE);
            gridView.getChildAt(5).setVisibility(View.VISIBLE);
            gridView.getChildAt(6).setVisibility(View.VISIBLE);
            gridView.getChildAt(7).setVisibility(View.VISIBLE);
            gridView.getChildAt(8).setVisibility(View.VISIBLE);
            gridView.getChildAt(9).setVisibility(View.VISIBLE);
            return false;
        }
        return false;
    }

    private void exchangeCard(ImageView firstCard,ImageView secondCard){
        firstCard.setColorFilter(null);
        secondCard.setColorFilter(null);
        int[] locationFirst = new  int[2] ;
        firstCard.getLocationOnScreen(locationFirst); //获取在当前窗口内的绝对坐标
        int[] locationSecond = new  int[2] ;
        secondCard.getLocationOnScreen(locationSecond);

        TranslateAnimation tA = new TranslateAnimation(0,locationSecond[0]-locationFirst[0],
                                                        0,locationSecond[1]-locationFirst[1]);
        tA.setDuration(390);//动画时间

        TranslateAnimation tB = new TranslateAnimation(0,locationFirst[0]-locationSecond[0],
                                                    0,locationFirst[1]-locationSecond[1]);
        tB.setDuration(390);

        firstCard.startAnimation(tA);
        secondCard.startAnimation(tB);

        firstCard = null;
        secondCard = null;
    }

    private void initList(){
        ImageTextItem option1 = new ImageTextItem(GameAcitvity.this.getString(R.string.Alpha狼),R.drawable.ulw_alphawolf);
        heroList.add(option1);
        heroNumber.add(GameAcitvity.this.getString(R.string.Alpha狼));
        wolfList.add(GameAcitvity.this.getString(R.string.Alpha狼));
        ImageTextItem option9 = new ImageTextItem(GameAcitvity.this.getString(R.string.狼人),R.drawable.ulw_werewolf);
        heroList.add(option9);
        heroNumber.add(GameAcitvity.this.getString(R.string.狼人));
        wolfList.add(GameAcitvity.this.getString(R.string.狼人));
        ImageTextItem option5 = new ImageTextItem(GameAcitvity.this.getString(R.string.狼先知),R.drawable.ulw_mysticwolf);
        heroList.add(option5);
        heroNumber.add(GameAcitvity.this.getString(R.string.狼先知));
        wolfList.add(GameAcitvity.this.getString(R.string.狼先知));
        ImageTextItem option2 = new ImageTextItem(GameAcitvity.this.getString(R.string.幽灵),R.drawable.ulw_doppelganger);
        heroList.add(option2);
        heroNumber.add(GameAcitvity.this.getString(R.string.幽灵));
        ImageTextItem option3 = new ImageTextItem(GameAcitvity.this.getString(R.string.失眠者),R.drawable.ulw_insomniac);
        heroList.add(option3);
        heroNumber.add(GameAcitvity.this.getString(R.string.失眠者));
        ImageTextItem option4 = new ImageTextItem(GameAcitvity.this.getString(R.string.皮匠),R.drawable.ulw_minion);
        heroList.add(option4);
        heroNumber.add(GameAcitvity.this.getString(R.string.皮匠));
        ImageTextItem option6 = new ImageTextItem(GameAcitvity.this.getString(R.string.强盗),R.drawable.ulw_robber);
        heroList.add(option6);
        heroNumber.add(GameAcitvity.this.getString(R.string.强盗));
        ImageTextItem option7 = new ImageTextItem(GameAcitvity.this.getString(R.string.预言家),R.drawable.ulw_seer);
        heroList.add(option7);
        heroNumber.add(GameAcitvity.this.getString(R.string.预言家));
        ImageTextItem option8 = new ImageTextItem(GameAcitvity.this.getString(R.string.捣蛋鬼),R.drawable.ulw_troublemaker);
        heroList.add(option8);
        heroNumber.add(GameAcitvity.this.getString(R.string.捣蛋鬼));
        ImageTextItem option10 = new ImageTextItem(GameAcitvity.this.getString(R.string.女巫),R.drawable.ulw_witch);
        heroList.add(option10);
        heroNumber.add(GameAcitvity.this.getString(R.string.女巫));
    }

    private void setCardClickListener(final String heroName){
        audioChat.setText("确认");
        View.OnClickListener clickMove = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG,"触发卡牌点击");
                if (firstSelect == v){
                    //点同一个取消选中
                    firstSelect.setColorFilter(null);
                    firstSelect = null;
                    return;
                }
                if (secondSelect == v){
                    secondSelect.setColorFilter(null);
                    secondSelect = null;
                    return;
                }

//                if (firstSelect == null){
//                    firstSelect = (ImageView) v;
//                    firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
//                } else {
//                    secondSelect = (ImageView)v;
//                    exchangeCard();
//                }
                switch (heroName){
                    case "狼Alpha":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            firstSelect.setColorFilter(null);
                            firstSelect = (ImageView)v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "狼先知":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            firstSelect.setColorFilter(null);
                            firstSelect = (ImageView)v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "幽灵":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            firstSelect.setColorFilter(null);
                            firstSelect = (ImageView)v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "失眠者":

                        break;
                    case "皮匠":

                        break;
                    case "强盗":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            firstSelect.setColorFilter(null);
                            firstSelect = (ImageView)v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "预言家":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            firstSelect.setColorFilter(null);
                            firstSelect = (ImageView)v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "捣蛋鬼":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        } else {
                            secondSelect = (ImageView)v;
                            secondSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "女巫":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            firstSelect.setColorFilter(null);
                            firstSelect = (ImageView)v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "女巫2":
                        if (secondSelect == null){
                            secondSelect = (ImageView) v;
                            secondSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            secondSelect.setColorFilter(null);
                            secondSelect = (ImageView)v;
                            secondSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                    case "finalKill":
                        if (firstSelect == null){
                            firstSelect = (ImageView) v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }else {
                            firstSelect.setColorFilter(null);
                            firstSelect = (ImageView)v;
                            firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                        }
                        break;
                }
            }
        };

        player1.setOnClickListener(clickMove);
        player2.setOnClickListener(clickMove);
        player3.setOnClickListener(clickMove);
        player4.setOnClickListener(clickMove);
        player5.setOnClickListener(clickMove);
        player6.setOnClickListener(clickMove);
        player7.setOnClickListener(clickMove);
        player8.setOnClickListener(clickMove);
        player9.setOnClickListener(clickMove);
        player10.setOnClickListener(clickMove);
        player11.setOnClickListener(clickMove);
        player12.setOnClickListener(clickMove);
        centercard1.setOnClickListener(clickMove);
        centercard2.setOnClickListener(clickMove);
        centercard3.setOnClickListener(clickMove);
    }

    private Timer firstTimer = null;
    private TimerTask firstTimerTask = null;

    @SuppressWarnings("unchecked")  //下面Object转ArraryList有风险
    private void startPickHero(Message msg){
        JSONObject gameJson = null;
        String recive = (String)msg.obj;
        pickWindow();
        pickOrNot = PICKING;
        playerNameList = new ArrayList<>();
        try {
            gameJson = new JSONObject(recive);
            JSONArray array = gameJson.getJSONArray("msg2");
            for (int i=0;i<array.length();i++){
                playerNameList.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        Iterator iterator = playerNameList.iterator();
        int j = 0;
        while (iterator.hasNext()){
            String thisName = (String) iterator.next();
            playerNameViewList.get(j).setText(thisName);
            j++;
        }
        
        //playerNameViewList之后作为TexiView集合没用了，在此处直接存储用户名字符串
        
        for (int i=playerNameList.size();i<12;i++){
            playerLocationList.get(i).setVisibility(View.GONE);
        }

        //填满前12位，后三位用户名留给三个centerCard，用以之后用户名--卡牌位置转换
        for (int i = playerNameList.size();i<12;i++){
            playerNameList.add("noPlayer");
        }
        playerNameList.add("centerCard1");
        playerNameList.add("centerCard2");
        playerNameList.add("centerCard3");

        Log.i(TAG,"用户名集合###"+playerNameList.toString());
    }

    private void heroBeSelected(Message msg){
        JSONObject gameJson = null;
        String selHeroName,theMsg;
        String msgType = null;

        int chooseType;
        theMsg = (String)msg.obj;
        try {
            gameJson = new JSONObject(theMsg);
            msgType = gameJson.getString("msgType");
            Log.i(TAG,msgType);
            whosName = gameJson.getString("msg1");
            Log.i(TAG,whosName);
            selHeroName = gameJson.getString("msg2");
            userNumber = gameJson.getInt("msg3");
            Log.i(TAG,String.valueOf(userNumber));
            chooseType= gameJson.getInt("msg4");
            if (whosName.equals(myUserName)){
                beSelected = false;
                if (userNumber==0){
                    onlyWolfOr=onlyWolf(0);
                }else if (userNumber==1){
                    onlyWolfOr=onlyWolf(0);
                }
                pickConfirm.setEnabled(true);
                pickConfirm.setVisibility(View.VISIBLE);
            }
            if (chooseType==0) {
                String first = whosName + "正在选择角色";
                bpMsg.setText(first);
            }else if (chooseType==1){
                String second = whosName + "正在进行额外选择";
                bpMsg.setText(second);
            }
            heroBeSelected(selHeroName,userNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (firstTimer!=null){
            countdown = 10;
            firstTimer.cancel();
            firstTimer = new Timer();
            firstTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //回到UI线程更新倒计时
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countdown--;
                            pickTime.setText(String.valueOf(countdown));
                            if (countdown==0){
                                autoPickHero();
                            }
                        }
                    });
                }
            };
            firstTimer.schedule(firstTimerTask, 1000, 1000);
        }else {
            firstTimer = new Timer();
            countdown = 10;
            firstTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //回到UI线程更新倒计时
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countdown--;
                            pickTime.setText(String.valueOf(countdown));
                            if (countdown==0){
                                autoPickHero();
                            }
                        }
                    });
                }
            };
            firstTimer.schedule(firstTimerTask, 1000, 1000);
        }

        if(msgType.equals(gameStateMsg12)){
            pickOrNot = PICKOVER;
            firstTimer.cancel();
            bpMsg.setText("选择完毕，正在分配卡牌");
            bpMsg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pickWindow.dismiss();
                }
            },6000);
        }
    }

    private void myHero(Message msg){
        JSONObject gameJson;
        String theMsg;
        theMsg = (String)msg.obj;
        try {
            gameJson = new JSONObject(theMsg);
            Log.w(TAG,"*************"+gameJson.toString());
            userToHero = gameJson.getJSONObject("msg2"); //JJ说有历史遗留问题所以用msg2
            whichHeroMine = userToHero.getString(myUserName);
            Log.w(TAG,"*************"+userToHero.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pickOrNot = NO_NEED_PICK;
        Log.i(TAG,"我的英雄*********"+whichHeroMine);

        showHeroDialog(whichHeroMine);

        //将没用的英雄选择集合换为界面牌位置集合
        selectedList.clear();
        selectedList.add(player1);
        selectedList.add(player2);
        selectedList.add(player3);
        selectedList.add(player4);
        selectedList.add(player5);
        selectedList.add(player6);
        selectedList.add(player7);
        selectedList.add(player8);
        selectedList.add(player9);
        selectedList.add(player10);
        selectedList.add(player11);
        selectedList.add(player12);
        selectedList.add(centercard1);
        selectedList.add(centercard2);
        selectedList.add(centercard3);

    }

    private void showHeroDialog(String card){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(GameAcitvity.this);
        ImageView heroImage = new ImageView(GameAcitvity.this);
        switch (card){
            case "狼Alpha":
                heroImage.setImageResource(R.drawable.ulw_alphawolf);
                card = "Alpha狼";
                break;
            case "狼人":
                heroImage.setImageResource(R.drawable.ulw_werewolf);
                break;
            case "狼先知":
                heroImage.setImageResource(R.drawable.ulw_mysticwolf);
                break;
            case "幽灵":
                heroImage.setImageResource(R.drawable.ulw_doppelganger);
                break;
            case "失眠者":
                heroImage.setImageResource(R.drawable.ulw_insomniac);
                break;
            case "皮匠":
                heroImage.setImageResource(R.drawable.ulw_minion);
                break;
            case "强盗":
                heroImage.setImageResource(R.drawable.ulw_robber);
                break;
            case "预言家":
                heroImage.setImageResource(R.drawable.ulw_seer);
                break;
            case "捣蛋鬼":
                heroImage.setImageResource(R.drawable.ulw_troublemaker);
                break;
            case "女巫":
                heroImage.setImageResource(R.drawable.ulw_witch);
                break;
        }
        dialog.setTitle("你的角色是");
        dialog.setMessage(card);
        dialog.setView(heroImage);
        dialog.setCancelable(false); //点击框外不会取消
        dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMsgToServer(gameStateMsg5);
                dialog.dismiss();
            }
        });
        exit.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        },4000);
    }


    //playerNameList用户名集合的顺序和selectedList卡牌ImageView的顺序是对应的
    private String positionToHero(ImageView card){
        String playerName = playerNameList.get(selectedList.indexOf(card));
        String heroName = null;
        try {
             heroName = userToHero.getString(playerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return heroName;
    }

    private String positionToUser(ImageView card){
        //String playerName = playerNameViewList.get(selectedList.indexOf(card)).getText().toString();
        String playerName = playerNameList.get(selectedList.indexOf(card));
        return playerName;
    }

    private ImageView userToPosition(String userName){
        ImageView tempImage;
        int index = playerNameList.indexOf(userName);

        tempImage = selectedList.get(index);
        return tempImage;
    }

    //正式游戏阶段的流程函数
    private void nowLetsDoSTH(Message msg){
        String theMsg = (String) msg.obj;
        String msgType = null;
        String serverMsg=null;
        JSONObject serverJson = null;
        try {
            serverJson = new JSONObject(theMsg);
            Log.w(TAG,"gameRound***"+serverJson.toString());
            msgType = serverJson.getString("msgType");
            serverMsg = serverJson.getString("msg1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (msgType.equals(gameStateMsg3)){
            String chatUserName = null;
            try {
                chatUserName = serverJson.getString("msg2"); //聊天时为用户名
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GameMsg abchatMsg = new GameMsg(chatUserName+": "+serverMsg,GameMsg.TYPE_RECEIVE);
            msgList.add(abchatMsg);
            adapter.notifyItemInserted(msgList.size() - 1);//当有新消息时刷新显示
            msgRecyclerView.scrollToPosition(msgList.size() - 1);//翻到最后一行
        }else if (msgType.equals(gameStateMsg6)){
            try {
                userToHero = serverJson.getJSONObject("msg2");  //用户名与卡牌影射用来进行位置与名称等的相互转换
                Log.w(TAG,"用户-英雄集合***"+userToHero.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (serverMsg.equals(whichHeroMine)){
                backLL.setVisibility(View.VISIBLE);
                gameMsgWindow(CANCELWINDOW);
                //狼的睁眼阶段?
                doIDo(whichHeroMine,serverJson);
            }else if (serverMsg.equals("狼群")){
                if (whichHeroMine.contains("狼")){
                    backLL.setVisibility(View.VISIBLE);
                    gameMsgWindow(CANCELWINDOW);
                    //狼的睁眼阶段?
                    doIDo(serverMsg,serverJson);
                }else {
                    backLL.setVisibility(View.INVISIBLE);
                    gameMsgWindow("狼群密谋中。。。");
                }
            }else {
                backLL.setVisibility(View.INVISIBLE);
                gameMsgWindow(serverMsg+"正在进行回合");
            }
        }else if (msgType.equals(gameStateMsg8)){
            backLL.setVisibility(View.VISIBLE);
            gameMsgWindow(CANCELWINDOW);
            gameStateText.setVisibility(View.VISIBLE);
//            if (serverMsg.equals(whichHeroMine)) {
//
//            }
            startArgue(serverMsg);
        }else if (msgType.equals(gameStateMsg10)){
            if (firstTimer!=null)firstTimer.cancel();
            gameStateText.setVisibility(View.GONE);

            sendSystemMsg("你现在可以选择一位玩家的卡牌并点击确认，作为你的处决投票");
            setCardClickListener("finalKill"); //setListener会刷新Clickable
            centercard1.setClickable(false);
            centercard2.setClickable(false);
            centercard3.setClickable(false);

            whichHeroMine = "finalKill"; //在此处临时替换以供点击确认时发送相应的投票结果
        }else if (msgType.equals(gameStateMsg11)){
            //msg2是被投最多票的list
            Log.i(TAG,"最终字符"+serverJson.toString());
            JSONArray resultList = null;
            String chosenOne,chosenTwo;
            boolean result = false;
            try {
                whichHeroMine = userToHero.getString(myUserName);
                resultList = serverJson.getJSONArray("msg2");
                //TODO:会不会三人以上同时投票而死？
                if (resultList.length()==1){
                    chosenOne = resultList.getString(0);
                    result = startSettle(userToHero.getString(chosenOne),null);
                    sendSystemMsg(resultList.getString(0)+"被投票处决");
                }else if (resultList.length() == 2){
                    chosenOne = resultList.getString(0);
                    chosenTwo = resultList.getString(1);
                    result = startSettle(userToHero.getString(chosenOne),userToHero.getString(chosenTwo));
                    sendSystemMsg(resultList.getString(0)+"和"+resultList.getString(1)+"被投票处决");
                }

                if (result){
                    sendSystemMsg("####你赢了####");
                }else {
                    sendSystemMsg("####你输了####");
                }

                showResultImage(resultList);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void doIDo(String myhero,JSONObject json){
        /*
        * 好，让我们分析一波
        * 点击事件大体分为三种
        * 1.点击一张卡背，然后弹出这张牌的正面   2.点击一张牌，与下一张点击的牌交换。 3.前面两种的混合模式
        * 综上首先要有一个可根据传角色参数变化而变更view可点与否的方法。
        *
        * 是否要在本地存储角色与玩家的对应信息
        * */
        switch (myhero){
            case "狼Alpha":
         /*
        * Alpha狼的操作是中央三牌默认被选中，然后点选一位玩家卡牌、点确定进行交换，包括接下来的狼人操作
        * */
                sendSystemMsg("欢迎醒来，Alpha狼，你现在可以选择一位玩家，将其与中央狼牌交换");
                //card1是中央狼
                setCardClickListener("狼Alpha"); //setListener会刷新Clickable
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                centercard1.setColorFilter(Color.parseColor("#59FF0000"));
                sendSystemMsg("请选中想要与之交换的玩家，然后按下确认");
                break;
//            case "狼人":
//         /*
//        * 狼人只需要在睁眼回合对所有初始为狼的玩家做标记
//        * */
//                sendSystemMsg("欢迎醒来，狼人，你的同类已被标记");
//
//                //此处还需要其他角色位置信息
//
//                break;
            case "狼先知":
        /*
        * 除了狼的回合外，还要可1类型点击所有玩家牌，但不能点击中央牌堆与自己
        * */
                sendSystemMsg("欢迎醒来，狼先知，你现在可以查看任意一位玩家的卡牌");

                setCardClickListener("狼先知");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                sendSystemMsg("请选中想要查看身份的角色牌，然后按下确认");
                break;
            case "狼群":
                if (secondSelect==null){
                    sendSystemMsg("欢迎醒来，狼人，认识一下的的同类吧，要做个自我介绍吗？(5秒后游戏继续)");
                    refreshClickable(false);
                    refreshCardColor();
                }
                try {
                    JSONArray array = json.getJSONArray("msg3");
                    for (int i=0;i<array.length();i++){
                        String tempUser = array.getString(i);
                        Log.i(TAG,"AAA狼群集合AAA"+tempUser);
                        //  TODO 需要服务器返回包括幽灵狼的用户名
                        secondSelect =  userToPosition(tempUser);
                        secondSelect.setColorFilter(Color.parseColor("#59FF0000"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                secondSelect = null;

                centercard1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendMsgToServer(gameStateMsg7,"狼群");
                        refreshClickable(false);//重置颜色
                        refreshCardColor();
                    }
                },5000);
                break;
            case "幽灵":
        /*
        * 回合开始时刻点击玩家牌变身，之后进行相应操作，不能点击自己
        * */
                sendSystemMsg("欢迎醒来，幽灵，你现在可以选择一位玩家并变为对方的身份（选择一个玩家卡牌后点击确认）");
                setCardClickListener("幽灵");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                userToPosition(myUserName).setClickable(false); //幽灵不能变自己
                break;
            case "失眠者":
        /*
        * 最后一回合不需点击，显示自己最终身份
        * */
                String hero = null;
                try {
                    hero = userToHero.getString(myUserName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (hero.contains("幽灵"))hero = "幽灵";
                sendSystemMsg("欢迎醒来，失眠者，你的最终角色是 ***"+hero+"***");
                backLL.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendMsgToServer(gameStateMsg7,"失眠者");
                    }
                },2000);
                break;
            case "皮匠":
        /*
        * 无操作，胜利条件不同
        * */
                break;
            case "强盗":
        /*
        * 首先点击玩家牌进行1操作、之后点击另一个玩家牌进行2操作，不能点击自己
        * */

                sendSystemMsg("欢迎醒来，强盗，你现在可以查看一位玩家的卡牌，并将自己与之交换");

                setCardClickListener("强盗");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                userToPosition(myUserName).setClickable(false); //强盗不能点自己
                break;
            case "预言家":
        /*
        * 点击除自己外所有牌进行1操作
        * */
                sendSystemMsg("欢迎醒来，预言家，你现在可以检视一位玩家或中央牌的身份");
                setCardClickListener("预言家");
                //无法选中自己
                break;
            case "捣蛋鬼":
        /*
        * 除自己外玩家牌2操作
        * */
                sendSystemMsg("欢迎醒来，捣蛋鬼，你现在可以交换任意两名玩家的卡牌，选中后按确认继续");

                setCardClickListener("捣蛋鬼");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                break;
            case "女巫":
        /*
        * 中央牌堆1操作、然后包括自己玩家牌2操作
        * */
                sendSystemMsg("欢迎醒来，女巫，你现在可以检视一张中央卡牌，并将其与一名玩家交换，选中中央牌后按确认继续");
                setCardClickListener("女巫");
                //屏蔽玩家牌
                refreshClickable(false);
                refreshCardColor();
                centercard1.setClickable(true);
                centercard2.setClickable(true);
                centercard3.setClickable(true);
                break;
            case "女巫2":
                sendSystemMsg("女巫，现在可将选中的中央卡牌与一名玩家交换，选中玩家牌后按确认继续");
                setCardClickListener("女巫2");
                firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                break;
        }
    }

    private void doGhostDo(String hero0){
        switch (hero0){
            case "狼Alpha":
                sendSystemMsg("你化身为了Alpha狼，你现在可以选择一位玩家，将其与中央狼牌交换");
                setCardClickListener("狼Alpha"); //setListener会刷新Clickable
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                centercard1.setColorFilter(Color.parseColor("#59FF0000"));
                sendSystemMsg("请选中想要与之交换的玩家，然后按下确认");
                whichHeroMine = "幽灵Alpha狼";
                break;
            case "狼人":
                sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵狼人",myUserName,myUserName,myUserName);
                break;
            case "狼先知":
                sendSystemMsg("你化身为了狼先知，你现在可以查看任意一位玩家的卡牌");
                setCardClickListener("狼先知");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                sendSystemMsg("请选中想要查看身份的角色牌，然后按下确认");
                whichHeroMine = "幽灵狼先知";
                break;
            case "失眠者":
                whichHeroMine = getString(R.string.失眠者); //把自己变成失眠者以便最后阶段和失眠者一同醒来
                //TODO 失眠者回合在提示其他玩家时要加入幽灵失眠者的提示
                sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵失眠者",myUserName,myUserName,myUserName);
                //自己和自己交换，msg5是自己的ID作为服务器端的map的key值便于操作
                break;
            case "皮匠":
                sendMsgToServer(gameStateMsg7,getString(R.string.幽灵),"幽灵皮匠",myUserName,myUserName,myUserName);
                break;
            case "强盗":
                sendSystemMsg("你化身为了强盗，你现在可以查看一位玩家的卡牌，并将自己与之交换");
                setCardClickListener("强盗");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                userToPosition(myUserName).setClickable(false); //强盗不能点自己
                whichHeroMine = "幽灵强盗";
                break;
            case "预言家":
                sendSystemMsg("你化身为了预言家，你现在可以检视一位玩家或中央牌的身份");
                setCardClickListener("预言家");
                whichHeroMine = "幽灵预言家";
                break;
            case "捣蛋鬼":
                sendSystemMsg("你化身为了捣蛋鬼，你现在可以交换任意两名玩家的卡牌，选中后按确认继续");
                setCardClickListener("捣蛋鬼");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                whichHeroMine = "幽灵捣蛋鬼";
                break;
            case "女巫":
                sendSystemMsg("你化身为了女巫，你现在可以检视一张中央卡牌，并将其与一名玩家交换，选中中央牌后按确认继续");
                setCardClickListener("女巫");
                //屏蔽玩家牌
                refreshClickable(false);
                refreshCardColor();
                centercard1.setClickable(true);
                centercard2.setClickable(true);
                centercard3.setClickable(true);
                whichHeroMine = "幽灵女巫";
                break;
            case "幽灵女巫2":
                sendSystemMsg("幽灵女巫，你现在可将选中的中央卡牌与一名玩家交换，选中玩家牌后按确认继续");
                setCardClickListener("女巫2");
                firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                break;
        }
    }

    private void startArgue(final String playerName){
        if (firstTimer!=null){
            //cancel并不会等于Null
            firstTimer.cancel();
            countdown = 5;
            firstTimer = new Timer();
            firstTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //回到UI线程更新倒计时
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countdown--;
                            gameStateText.setText(playerName+"正在发言  "+String.valueOf(countdown));
                            if (countdown==0){
                                Log.i(TAG,"1   "+myUserName);
                                if (playerName.equals(myUserName))sendMsgToServer(gameStateMsg9);
                            }
                        }
                    });
                }
            };
            firstTimer.schedule(firstTimerTask, 1000, 1000);
        }else {
            firstTimer = new Timer();
            countdown = 5;
            firstTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //回到UI线程更新倒计时
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countdown--;
                            gameStateText.setText(playerName+"正在发言  "+String.valueOf(countdown));
                            if (countdown==0){
                                Log.i(TAG,"2   "+myUserName);
                                if (playerName.equals(myUserName))sendMsgToServer(gameStateMsg9);
                            }
                        }
                    });
                }
            };
            firstTimer.schedule(firstTimerTask, 1000, 1000);
        }
    }

    private boolean startSettle(String deadHeroName1,String deadHeroName2){
//        1如果至少一只狼人死了（即使有非狼人死了），那么村民党获胜。
//        2如果存在狼人并且狼人都活着，那么狼人党获胜。
//        3如果没有人是狼人，并且大家都活着，那么村民党获胜。
//        4如果没有人是狼人，并且至少一人死了，那么共同失败。

        //***目前情况下至少一人死，所以没有3情况即为只要死一个狼则村民胜，否则狼人胜

        //判断死的有没有狼，判断自己是不是狼，得出自己的胜负结论。
        if (deadHeroName2 == null){
            //只死一人
            //TODO 还要考虑幽灵狼的情况
            //TODO 幽灵皮匠结算有问题
            //TODO 幽灵XXX的图片
            if (deadHeroName1.contains(getString(R.string.皮匠))){
                if (whichHeroMine.contains(getString(R.string.皮匠))){
                    return true;
                }else{
                    return false;
                }
            }else if(deadHeroName1.contains("狼")){
                //死的是狼
                if (whichHeroMine.contains("狼")){
                    //自己是狼则输了
                    return false;
                }else {
                    return true;
                }
            }else{
                //死的不是狼
                if (whichHeroMine.contains("狼")){
                    //自己是狼则赢了
                    return true;
                }else {
                    return false;
                }
            }

        }else {
            //死两人
            if (deadHeroName1.contains(getString(R.string.皮匠))||deadHeroName2.contains(getString(R.string.皮匠))){
                if (whichHeroMine.contains(getString(R.string.皮匠))){
                    return true;
                }else{
                    return false;
                }
            }else if (deadHeroName1.contains("狼")||deadHeroName2.contains("狼")){
                //只要有一个狼死
                if (whichHeroMine.contains("狼")){
                    //自己是狼则输了
                    return false;
                }else {
                    return true;
                }
            }else {
                //两个都不是狼
                if (whichHeroMine.contains("狼")){
                    //自己是狼则赢了
                    return true;
                }else {
                    return false;
                }
            }

        }
    }

    private void showResultImage(JSONArray resultList)throws JSONException{
        //resultList得是用户名

        int playerNumber = userToHero.length();
        for (int i=0;i<playerNumber;i++){
            String heroName = userToHero.getString(playerNameList.get(i));
            if(heroName.contains("幽灵"))heroName = "幽灵";  //显示幽灵。
            loadImageByHero(heroName,selectedList.get(i));
        }

        //TODO 这里的被投死者变色和显示用户名没起作用
        if (resultList.length()==1){
            sendSystemMsg(resultList.getString(0)+"被投票处决");
            userToPosition(resultList.getString(0)).setColorFilter(Color.parseColor("#59FF0000"));
        }else if (resultList.length()==2){
            sendSystemMsg(resultList.getString(0)+"和"+resultList.getString(1)+"被投票处决");
            userToPosition(resultList.getString(0)).setColorFilter(Color.parseColor("#59FF0000"));
            userToPosition(resultList.getString(1)).setColorFilter(Color.parseColor("#59FF0000"));
        }
    }

    private void loadImageByHero(String heroName,ImageView image){
        switch (heroName){
            case "狼Alpha":
                Glide.with(this).load(R.drawable.ulw_alphawolf).into(image);
                break;
            case "狼人":
                Glide.with(this).load(R.drawable.ulw_werewolf).into(image);
                break;
            case "狼先知":
                Glide.with(this).load(R.drawable.ulw_mysticwolf).into(image);
                break;
            case "幽灵":
                Glide.with(this).load(R.drawable.ulw_doppelganger).into(image);
                break;
            case "失眠者":
                Glide.with(this).load(R.drawable.ulw_insomniac).into(image);
                break;
            case "皮匠":
                Glide.with(this).load(R.drawable.ulw_minion).into(image);
                break;
            case "强盗":
                Glide.with(this).load(R.drawable.ulw_robber).into(image);
                break;
            case "预言家":
                Glide.with(this).load(R.drawable.ulw_seer).into(image);
                break;
            case "捣蛋鬼":
                Glide.with(this).load(R.drawable.ulw_troublemaker).into(image);
                break;
            case "女巫":
                Glide.with(this).load(R.drawable.ulw_witch).into(image);
                break;
        }
    }

    private void autoPickHero(){

        countdown = 10;

        leftLL.setVisibility(View.VISIBLE);
        rightLL.setVisibility(View.VISIBLE);
        backLL.setBackgroundResource(0);

        JSONObject pickJson = new JSONObject();
        try {
            pickJson.put("msgType", "#选#择#完#毕");
            pickJson.put("msg2",myUserName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (whosName.equals(myUserName)) {

            if (onlyWolfOr){
                if (userNumber==0){
                    Random random = new Random();
                    int ranNumber = random.nextInt(startwolfNumber);
                    try {
                        pickJson.put("msg1", wolfList.get(ranNumber));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    myBinder.sendMsgToServer(pickJson.toString());
                } else if(userNumber==1){
                    Random random = new Random();
                    int ranNumber = random.nextInt(startwolfNumber);
                    try {
                        pickJson.put("msg1", wolfList.get(ranNumber));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    myBinder.sendMsgToServer(pickJson.toString());
                }

                    onlyWolfOr = onlyWolf(1);

                pickConfirm.setVisibility(View.INVISIBLE);
                pickConfirm.setEnabled(false);
            }else {
                Random random = new Random();
                int ranNumber = random.nextInt(startHeroNumber);
                try {
                    pickJson.put("msg1", heroNumber.get(ranNumber));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                myBinder.sendMsgToServer(pickJson.toString());

                pickConfirm.setVisibility(View.INVISIBLE);
                pickConfirm.setEnabled(false);
            }
        }

        firstTimer.cancel();
    }

    private void sendSystemMsg(String msg){
        GameMsg gameMsg = new GameMsg(msg,GameMsg.TYPE_SYSTEM);
        msgList.add(gameMsg);
        adapter.notifyItemInserted(msgList.size() - 1);//当有新消息时刷新显示
        msgRecyclerView.scrollToPosition(msgList.size() - 1);//翻到最后一行
    }

    private void refreshClickable(boolean clickable){
        firstSelect = null;
        secondSelect = null;

        player1.setClickable(clickable);
        player2.setClickable(clickable);
        player3.setClickable(clickable);
        player4.setClickable(clickable);
        player5.setClickable(clickable);
        player6.setClickable(clickable);
        player7.setClickable(clickable);
        player8.setClickable(clickable);
        player9.setClickable(clickable);
        player10.setClickable(clickable);
        player11.setClickable(clickable);
        player12.setClickable(clickable);
        centercard1.setClickable(clickable);
        centercard2.setClickable(clickable);
        centercard3.setClickable(clickable);
}

    private void refreshCardColor(){
        //        重置颜色
        player1.setColorFilter(null);
        player2.setColorFilter(null);
        player3.setColorFilter(null);
        player4.setColorFilter(null);
        player5.setColorFilter(null);
        player6.setColorFilter(null);
        player7.setColorFilter(null);
        player8.setColorFilter(null);
        player9.setColorFilter(null);
        player10.setColorFilter(null);
        player11.setColorFilter(null);
        player12.setColorFilter(null);
        centercard1.setColorFilter(null);
        centercard2.setColorFilter(null);
        centercard3.setColorFilter(null);
    }

    private void startCardMove(String role){

    }

    private void sendMsgToServer(final String msgType){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msgType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson.toString());
    }
    private void sendMsgToServer(final String msgType,final String msg1){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msgType);
            msgJson.put("msg1",msg1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson.toString());
    }
    private void sendMsgToServer(final String msgType,final String msg1,final String msg2){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msgType);
            msgJson.put("msg1",msg1);
            msgJson.put("msg2",msg2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson.toString());
    }
    private void sendMsgToServer(final String msgType,final String msg1,final String msg2,final String msg3){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msgType);
            msgJson.put("msg1",msg1);
            msgJson.put("msg2",msg2);
            msgJson.put("msg3",msg3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson.toString());
    }

    private void sendMsgToServer(final String msgType,final String msg1,final String msg2,final String msg3,final String msg4,final String msg5){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msgType);
            msgJson.put("msg1",msg1);
            msgJson.put("msg2",msg2);
            msgJson.put("msg3",msg3);
            msgJson.put("msg4",msg4);
            msgJson.put("msg5",msg5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson.toString());
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (GameService.GameBinder) service;
            myBinder.setHandler(handler);

            JSONObject firstJson = new JSONObject();
            try {
                firstJson.put("msgType",gameStateMsg0);
                firstJson.put("msg1",myUserName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myBinder.sendMsgToServer(firstJson.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){

        }
        return true;
    }
}
