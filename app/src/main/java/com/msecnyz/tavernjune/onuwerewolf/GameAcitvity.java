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

import com.msecnyz.tavernjune.BaseActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.GameMsgAdapter;
import com.msecnyz.tavernjune.listitem.ImageTextItem;
import com.msecnyz.tavernjune.listitem.UWolfHeroAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private Button abChat,abSend,abCancel,audioChat,exit,pickConfirm;
    private TextView bpMsg,playername1,playername2,playername3,playername4,playername5,playername6,
            playername7,playername8,playername9,playername10,playername11,playername12,pickTime,windowGameMsg;
    private LinearLayout leftLL,rightLL,backLL,playerLLL1,playerLLL2,playerLLL3,playerLLL4,playerLLL5,
            playerLLL6,playerLLL7,playerLLL8, playerLLL9,playerLLL10,playerLLL11,playerLLL12;
    private RecyclerView msgRecyclerView;
    private EditText abcText;
    private PopupWindow pickWindow;
    private List<GameMsg> msgList = new ArrayList<>();
    private List<TextView> playerNameList = new ArrayList<>();
    private List<LinearLayout> playerLocationList = new ArrayList<>();
    private List<ImageTextItem> heroList = new ArrayList<>();
    private ArrayList<String> playerList = null;
    private ArrayList<ImageView> selectedList = new ArrayList<>();
    private ArrayList<String> heroNumber = new ArrayList<>();
    private ArrayList<String> wolfList = new ArrayList<>();
    private JSONObject gameJson = null;
    private GameMsgAdapter adapter;
    private String userName,pickHeroName;
    private String whosName = "#p$l%a&y^e$r";
    private String whichHeroMine = null;
    final private String gameStateMsg1 = "#安#排#完#毕";
    final private String gameStateMsg2 = "#选#择#完#毕";
    final private String gameStateMsg3 = "#文#字#聊#天";
    final private String gameStateMsg4 = "#离#开#游#戏";
    final private static String CANCELWINDOW = "cancel";
    private boolean beSelected = false;
    private boolean onlyWolfOr = false;
    private int countdown = 10;
    final private static int NO_NEED_PICK = 0;
    final private static int NEED_PICK = 1;
    final private static int PICKING = 2;
    final private static int PICKOVER = 3;
    private static int pickOrNot = -1;
    private int startHeroNumber = 10;
    private int startwolfNumber = 3;
    private int userNumber = -1;
    private int myPosition = -1;
    private  GridView gridView;
    private GameService.GameBinder myBinder;


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
            //可以考虑等activity创建完毕再发讯息
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
    protected void setView() {
        super.setView();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);
        setContentView(R.layout.onuwolf_game);

    }

    @Override
    protected void activityReady() {
        super.activityReady();

        SharedPreferences sharedPreferences = this.getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userId","userId");
    }

    @Override
    protected void initViews() {
        super.initViews();
        abChat = (Button)findViewById(R.id.abchat);
        audioChat = (Button)findViewById(R.id.audiochat);
        exit = (Button)findViewById(R.id.wolfgame_exit);

        player1 = (ImageView)findViewById(R.id.player1);
        player2 = (ImageView)findViewById(R.id.player2);
        player3 = (ImageView)findViewById(R.id.player3);
        player4 = (ImageView)findViewById(R.id.player4);
        player5 = (ImageView)findViewById(R.id.player5);
        player6 = (ImageView)findViewById(R.id.player6);
        player7 = (ImageView)findViewById(R.id.player7);
        player8 = (ImageView)findViewById(R.id.player8);
        player9 = (ImageView)findViewById(R.id.player9);
        player10 = (ImageView)findViewById(R.id.player10);
        player11= (ImageView)findViewById(R.id.player11);
        player12 = (ImageView)findViewById(R.id.player12);

        centercard1 = (ImageView)findViewById(R.id.centercard1);
        centercard2 = (ImageView)findViewById(R.id.centercard2);
        centercard3 = (ImageView)findViewById(R.id.centercard3);

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
        playerNameList.add(playername1);
        playername2 = (TextView)findViewById(R.id.player_name2);
        playerNameList.add(playername2);
        playername3 = (TextView)findViewById(R.id.player_name3);
        playerNameList.add(playername3);
        playername4 = (TextView)findViewById(R.id.player_name4);
        playerNameList.add(playername4);
        playername5 = (TextView)findViewById(R.id.player_name5);
        playerNameList.add(playername5);
        playername6 = (TextView)findViewById(R.id.player_name6);
        playerNameList.add(playername6);
        playername7 = (TextView)findViewById(R.id.player_name7);
        playerNameList.add(playername7);
        playername8 = (TextView)findViewById(R.id.player_name8);
        playerNameList.add(playername8);
        playername9 = (TextView)findViewById(R.id.player_name9);
        playerNameList.add(playername9);
        playername10 = (TextView)findViewById(R.id.player_name10);
        playerNameList.add(playername10);
        playername11 = (TextView)findViewById(R.id.player_name11);
        playerNameList.add(playername11);
        playername12 = (TextView)findViewById(R.id.player_name12);
        playerNameList.add(playername12);
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
                if (audioChat.getText().equals("确认")){
                    switch (whichHeroMine){
                        case "狼Alpha":
                            exchangeCard(firstSelect,centercard1);
                            break;
                        case "狼人":

                            break;
                        case "狼先知":

                            break;
                        case "幽灵":

                            break;
                        case "失眠者":

                            break;
                        case "皮匠":

                            break;
                        case "强盗":

                            break;
                        case "预言家":

                            break;
                        case "捣蛋鬼":

                            break;
                        case "女巫":

                            break;
                    }
                    abChat.setText("按住讲话");
                }else {

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
                        chatJson.put("msg2",userName);
                        //chatJson.put("msg3",myPosition);
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
        final PopupWindow gameMsgWindow = new PopupWindow(this);
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
        View view = LayoutInflater.from(this).inflate(R.layout.ulw_pickhero,null);
        pickWindow = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        if (heroList.size()==0){
            initList();
        }
        final UWolfHeroAdapter heroAdapter = new UWolfHeroAdapter(GameAcitvity.this,R.layout.ulw_heroitem,heroList);
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
        sendMsgToServer(gameStateMsg1,userName);
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
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_alphawolf);
                break;
            case "狼人":
                startHeroNumber--;
                startwolfNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.狼人));
                wolfList.remove(GameAcitvity.this.getString(R.string.狼人));
                gridView.getChildAt(1).setVisibility(View.INVISIBLE);
                gridView.getChildAt(1).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_werewolf);
                break;
            case "狼先知":
                startHeroNumber--;
                startwolfNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.狼先知));
                wolfList.remove(GameAcitvity.this.getString(R.string.狼先知));
                gridView.getChildAt(2).setVisibility(View.INVISIBLE);
                gridView.getChildAt(2).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_mysticwolf);
                break;
            case "幽灵":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.幽灵));
                gridView.getChildAt(3).setVisibility(View.INVISIBLE);
                gridView.getChildAt(3).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_doppelganger);
                break;
            case "失眠者":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.失眠者));
                gridView.getChildAt(4).setVisibility(View.INVISIBLE);
                gridView.getChildAt(4).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_insomniac);
                break;
            case "皮匠":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.皮匠));
                gridView.getChildAt(5).setVisibility(View.INVISIBLE);
                gridView.getChildAt(5).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_minion);
                break;
            case "强盗":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.强盗));
                gridView.getChildAt(6).setVisibility(View.INVISIBLE);
                gridView.getChildAt(6).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_robber);
                break;
            case "预言家":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.预言家));
                gridView.getChildAt(7).setVisibility(View.INVISIBLE);
                gridView.getChildAt(7).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_seer);
                break;
            case "捣蛋鬼":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.捣蛋鬼));
                gridView.getChildAt(8).setVisibility(View.INVISIBLE);
                gridView.getChildAt(8).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_troublemaker);
                break;
            case "女巫":
                startHeroNumber--;
                heroNumber.remove(GameAcitvity.this.getString(R.string.女巫));
                gridView.getChildAt(9).setVisibility(View.INVISIBLE);
                gridView.getChildAt(9).setEnabled(false);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_witch);
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
        //ImageTextItem option1 = new ImageTextItem(GameAcitvity.this.getString(R.string.Alpha狼),R.drawable.ulw_alphawolf);
        ImageTextItem option1 = new ImageTextItem(GameAcitvity.this.getString(R.string.Alpha狼),R.drawable.userid);
        heroList.add(option1);
        heroNumber.add(GameAcitvity.this.getString(R.string.Alpha狼));
        wolfList.add(GameAcitvity.this.getString(R.string.Alpha狼));
        //ImageTextItem option9 = new ImageTextItem(GameAcitvity.this.getString(R.string.狼人),R.drawable.ulw_werewolf);
        ImageTextItem option9 = new ImageTextItem(GameAcitvity.this.getString(R.string.狼人),R.drawable.userid);
        heroList.add(option9);
        heroNumber.add(GameAcitvity.this.getString(R.string.狼人));
        wolfList.add(GameAcitvity.this.getString(R.string.狼人));
//        ImageTextItem option5 = new ImageTextItem(GameAcitvity.this.getString(R.string.狼先知),R.drawable.ulw_mysticwolf);
        ImageTextItem option5 = new ImageTextItem(GameAcitvity.this.getString(R.string.狼先知),R.drawable.userid);
        heroList.add(option5);
        heroNumber.add(GameAcitvity.this.getString(R.string.狼先知));
        wolfList.add(GameAcitvity.this.getString(R.string.狼先知));
//        ImageTextItem option2 = new ImageTextItem(GameAcitvity.this.getString(R.string.幽灵),R.drawable.ulw_doppelganger);
        ImageTextItem option2 = new ImageTextItem(GameAcitvity.this.getString(R.string.幽灵),R.drawable.userid);
        heroList.add(option2);
        heroNumber.add(GameAcitvity.this.getString(R.string.幽灵));
//        ImageTextItem option3 = new ImageTextItem(GameAcitvity.this.getString(R.string.失眠者),R.drawable.ulw_insomniac);
        ImageTextItem option3 = new ImageTextItem(GameAcitvity.this.getString(R.string.失眠者),R.drawable.userid);
        heroList.add(option3);
        heroNumber.add(GameAcitvity.this.getString(R.string.失眠者));
//        ImageTextItem option4 = new ImageTextItem(GameAcitvity.this.getString(R.string.皮匠),R.drawable.ulw_minion);
        ImageTextItem option4 = new ImageTextItem(GameAcitvity.this.getString(R.string.皮匠),R.drawable.userid);
        heroList.add(option4);
        heroNumber.add(GameAcitvity.this.getString(R.string.皮匠));
//        ImageTextItem option6 = new ImageTextItem(GameAcitvity.this.getString(R.string.强盗),R.drawable.ulw_robber);
        ImageTextItem option6 = new ImageTextItem(GameAcitvity.this.getString(R.string.强盗),R.drawable.userid);
        heroList.add(option6);
        heroNumber.add(GameAcitvity.this.getString(R.string.强盗));
//        ImageTextItem option7 = new ImageTextItem(GameAcitvity.this.getString(R.string.预言家),R.drawable.ulw_seer);
        ImageTextItem option7 = new ImageTextItem(GameAcitvity.this.getString(R.string.预言家),R.drawable.userid);
        heroList.add(option7);
        heroNumber.add(GameAcitvity.this.getString(R.string.预言家));
//        ImageTextItem option8 = new ImageTextItem(GameAcitvity.this.getString(R.string.捣蛋鬼),R.drawable.ulw_troublemaker);
        ImageTextItem option8 = new ImageTextItem(GameAcitvity.this.getString(R.string.捣蛋鬼),R.drawable.userid);
        heroList.add(option8);
        heroNumber.add(GameAcitvity.this.getString(R.string.捣蛋鬼));
//        ImageTextItem option10 = new ImageTextItem(GameAcitvity.this.getString(R.string.女巫),R.drawable.ulw_witch);
        ImageTextItem option10 = new ImageTextItem(GameAcitvity.this.getString(R.string.女巫),R.drawable.userid);
        heroList.add(option10);
        heroNumber.add(GameAcitvity.this.getString(R.string.女巫));
    }

    private void setCardClickListener(final String heroName){
        View.OnClickListener clickMove = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstSelect == v){
                    //点同一个取消选中
                    firstSelect.setColorFilter(null);
                    firstSelect = null;
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
                    case "狼人":

                        break;
                    case "狼先知":

                        break;
                    case "幽灵":

                        break;
                    case "失眠者":

                        break;
                    case "皮匠":

                        break;
                    case "强盗":

                        break;
                    case "预言家":

                        break;
                    case "捣蛋鬼":

                        break;
                    case "女巫":

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

    private Timer pickTimer = null;
    private TimerTask pickTimerTask = new TimerTask() {
        @Override
        public void run() {
            //回到UI线程更新倒计时
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countdown--;
                    pickTime.setText(""+countdown);
                    if (countdown==0){
                        autoPickHero();
                    }
                }
            });
        }
    };

    @SuppressWarnings("unchecked")  //下面Object转ArraryList有风险
    private void startPickHero(Message msg){
        String recive = (String)msg.obj;
        pickWindow();
        pickOrNot = PICKING;
        try {
            gameJson = new JSONObject(recive);
            //playerList = (ArrayList<String>) gameJson.get("msg2");
            playerList = new ArrayList<>();
            playerList.add(gameJson.getString("msg2"));
            playerList.add(gameJson.getString("msg3"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myPosition = playerList.indexOf(userName);
        Iterator iterator = playerList.iterator();
        int j = 0;
        while (iterator.hasNext()){
            String thisName = (String) iterator.next();
            playerNameList.get(j).setText(thisName);
            j++;
        }
        for (int i=playerList.size();i<12;i++){
            playerLocationList.get(i).setVisibility(View.GONE);
        }
    }

    private void heroBeSelected(Message msg){
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
            if (whosName.equals(userName)){
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

        if (pickTimer!=null){
            countdown = 10;
            pickTimer.cancel();
            pickTimer = new Timer();
            pickTimerTask = new TimerTask() {
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
            pickTimer.schedule(pickTimerTask, 1000, 1000);
        }else {
            pickTimer = new Timer();
            pickTimer.schedule(pickTimerTask, 1000, 1000);
        }

        if(msgType.equals("overInfo")){
            pickOrNot = PICKOVER;
            pickTimer.cancel();
            Timer blink = new Timer();
            TimerTask blinkTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pickWindow.dismiss();
                        }
                    });
                }
            };
            blink.schedule(blinkTask,3000);
        }
    }

    private void myHero(Message msg){
        String myCard = "天神";
        String centerCard1 = "1";
        String centerCard2 = "2";
        String centerCard3 = "3";
        String theMsg;
        theMsg = (String)msg.obj;
        try {
            gameJson = new JSONObject(theMsg);
            centerCard1 = gameJson.getString("msg2");
            centerCard2 = gameJson.getString("msg3");
            centerCard3 = gameJson.getString("msg4");
            myCard = gameJson.getString("msg1");
            whichHeroMine = myCard;
        } catch (JSONException e) {
            e.printStackTrace();
        }

//                abChat.setText(centerCard1);
//                audioChat.setText(centerCard2);
//                exit.setText(centerCard3);

        AlertDialog.Builder dialog = new AlertDialog.Builder(GameAcitvity.this);
        ImageView heroImage = new ImageView(GameAcitvity.this);
        switch (myCard){
            case "狼Alpha":
                heroImage.setImageResource(R.drawable.ulw_alphawolf);
                myCard = "Alpha狼";
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
        dialog.setMessage(myCard);
        dialog.setView(heroImage);
        dialog.setCancelable(true);
        dialog.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
        pickOrNot = NO_NEED_PICK;
        backLL.setClickable(false);
    }

    private void nowLetsDoSTH(Message msg){
        String theMsg = (String) msg.obj;
        String msgType = null;
        String serverMsg=null;
        String thisUserName = null;
        try {
            JSONObject serverJson = new JSONObject(theMsg);
            msgType = serverJson.getString("msgType");
            serverMsg = serverJson.getString("msg1");
            thisUserName = serverJson.getString("msg2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (msgType.equals(gameStateMsg3)){

            GameMsg abchatMsg = new GameMsg(thisUserName+": "+serverMsg,GameMsg.TYPE_RECEIVE);
            msgList.add(abchatMsg);
            adapter.notifyItemInserted(msgList.size() - 1);//当有新消息时刷新显示
            msgRecyclerView.scrollToPosition(msgList.size() - 1);//翻到最后一行
        }else if (msgType.equals("英雄顺序")){
            if (serverMsg.equals(whichHeroMine)){
                backLL.setVisibility(View.VISIBLE);
                gameMsgWindow(CANCELWINDOW);
                abChat.setText("确认");
                //狼的睁眼阶段?
                doIDO(whichHeroMine);
            }else {
                backLL.setVisibility(View.INVISIBLE);
                gameMsgWindow(serverMsg+"正在进行回合");
            }
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (whosName.equals(userName)) {

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

        pickTimer.cancel();
    }

    private void doIDO(String myhero){
        /*
        * 好，让我们分析一波
        * 点击事件大体分为三种
        * 1.点击一张卡背，然后弹出这张牌的正面   2.点击一张牌，与下一张点击的牌交换。 3.前面两种的混合模式
        * 综上首先要有一个可根据传角色参数变化而变更view可点与否的方法。
        * */
        switch (myhero){
            case "狼Alpha":
         /*
        * Alpha狼的操作是中央三牌默认被选中，然后点选一位玩家卡牌、点确定进行交换，包括接下来的狼人操作
        * */
                sendSystemMsg("欢迎醒来，Alpha狼，你现在可以选择一位玩家，将其与中央狼牌交换");
                //card1是中央狼
                centercard1.setColorFilter(Color.parseColor("#59FF0000"));
                refreshClickable(true);
                setCardClickListener("狼Alpha");
                centercard1.setClickable(false);
                centercard2.setClickable(false);
                centercard3.setClickable(false);
                sendSystemMsg("请选中想要与之交换的玩家，然后选择确认");
                break;
            case "狼人":
         /*
        * 狼人只需要在睁眼回合对所有初始为狼的玩家做标记
        * */
                sendSystemMsg("欢迎醒来，狼人，你的同类已被标记");

                //此处还需要其他角色位置信息

                break;
            case "狼先知":
        /*
        * 除了狼的回合外，还要可1类型点击所有玩家牌，但不能点击中央牌堆与自己
        * */

                break;
            case "幽灵":
        /*
        * 回合开始时刻点击玩家牌变身，之后进行相应操作，不能点击自己
        * */
                break;
            case "失眠者":
        /*
        * 最后一回合不需点击，显示自己最终身份
        * */
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
                break;
            case "预言家":
        /*
        * 点击除自己外所有牌进行1操作
        * */
                sendSystemMsg("欢迎醒来，预言家，你现在可以检视一位玩家或中央牌的身份");
                refreshClickable(true);
                setCardClickListener("预言家");
                //无法选中自己
                break;
            case "捣蛋鬼":
        /*
        * 除自己外玩家牌2操作
        * */
                break;
            case "女巫":
        /*
        * 中央牌堆1操作、然后包括自己玩家牌2操作
        * */
                break;
        }
    }

    private void sendSystemMsg(String msg){
        GameMsg gameMsg = new GameMsg(msg,GameMsg.TYPE_SYSTEM);
        msgList.add(gameMsg);
        adapter.notifyItemInserted(msgList.size() - 1);//当有新消息时刷新显示
        msgRecyclerView.scrollToPosition(msgList.size() - 1);//翻到最后一行
    }

    private void refreshClickable(boolean clickable){
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
                firstJson.put("msgType","#准#备#就#绪");
                firstJson.put("msg1",userName);
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
