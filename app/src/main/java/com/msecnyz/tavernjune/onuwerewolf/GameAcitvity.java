package com.msecnyz.tavernjune.onuwerewolf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.GameMsgAdapter;
import com.msecnyz.tavernjune.listitem.ImageTextItem;
import com.msecnyz.tavernjune.listitem.UWolfHeroAdapter;
import com.msecnyz.tavernjune.net.HttpOperation;
import com.msecnyz.tavernjune.net.SocketOperation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameAcitvity extends AppCompatActivity {

    private ImageView firstSelect,secondSelect,player1,player2,player3,player4,player5,player6,player7,
            player8,player9,player10,player11,player12,centercard1,centercard2,centercard3,pickHero1,
            pickHero2,pickHero3,pickHero4,pickHero5,pickHero6,pickHero7,pickHero8,pickHero9,
            pickHero10,pickHero11,pickHero12;
    private Button abChat,abSend,abCancel,audioChat,exit,pickConfirm;
    private TextView bpMsg,playername1,playername2,playername3,playername4,playername5,playername6,
            playername7,playername8,playername9,playername10,playername11,playername12,pickTime;
    private EditText abcText;
    private List<GameMsg> msgList = new ArrayList<>();
    private List<TextView> playerNameList = new ArrayList<>();
    private List<LinearLayout> playerLocationList = new ArrayList<>();
    private List<ImageTextItem> heroList = new ArrayList<>();
    private ArrayList<String> playerList = null;
    private ArrayList<ImageView> selectedList = new ArrayList<>();
    private JSONObject playerJson = null;
    private RecyclerView msgRecyclerView;
    private GameMsgAdapter adapter;
    private SocketOperation gameSocket;
    private String userName,heroName;
    private int countdown = 15;
    final private static int NO_NEED_PICK = 0;
    final private static int NEED_PICK = 1;
    final private static int PICKING = 2;
    private static int pickOrNot = -1;
    private LinearLayout leftLL,rightLL,backLL,playerLLL1,playerLLL2,playerLLL3,playerLLL4,playerLLL5,
            playerLLL6,playerLLL7,playerLLL8, playerLLL9,playerLLL10,playerLLL11,playerLLL12;
    private  GridView gridView;

    private Handler handler = new Handler(){
        //若Activity没创建完成就收到消息会报异常
        @Override
        public void handleMessage(Message msg) {
            if (pickOrNot == NEED_PICK){
                pickWindow();
                pickOrNot = PICKING;
                playerList = (ArrayList<String>) msg.obj;
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
            }else if (pickOrNot == PICKING){

                String theMsg,whosName,heroName;
                int userNumber;
                theMsg = (String)msg.obj;
                try {
                    playerJson = new JSONObject(theMsg);
                    whosName = playerJson.getString("typeK");
                    heroName = playerJson.getString("messageK");
                    userNumber = playerJson.getInt("overK");
                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa          "+userNumber);
                    if (whosName.equals(userName)){
                        pickConfirm.setEnabled(true);
                        pickConfirm.setVisibility(View.VISIBLE);
                    }
                    bpMsg.setText(whosName+"正在选择角色");

                    heroBeSelected(heroName,userNumber);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (pickTimer!=null){
                    countdown = 15;
                    pickTimer.cancel();
//                    pickTimer = null;
//                    pickTimerTask = null;
                    pickTimer = new Timer();
                    pickTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            //回到UI线程更新倒计时
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    countdown--;
                                    pickTime.setText(""+countdown);
                                    if (countdown==0){
                                        countdown = 15;
                                        pickTimer.cancel();
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
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);
        setContentView(R.layout.onuwolf_game);

        leftLL = (LinearLayout) findViewById(R.id.leftLL);
        rightLL = (LinearLayout)findViewById(R.id.rightLL);
        backLL = (LinearLayout)findViewById(R.id.backLL);

        SharedPreferences sharedPreferences = this.getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userId","userId");

        Intent intentall = getIntent();

        initView();
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pickOrNot = NEED_PICK;
                    gameSocket = new SocketOperation("192.168.199.48",10082);
                    gameSocket.setLink(handler);
                    JSONObject firstJson = new JSONObject();
                    try {
                        firstJson.put("typeK","发送用户");
                        firstJson.put("messageK",userName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    gameSocket.gameMsgToServer(firstJson.toString());
                }
            }).start();
        }else {
            leftLL.setVisibility(View.VISIBLE);
            rightLL.setVisibility(View.VISIBLE);
            backLL.setBackgroundResource(0);
            pickOrNot = NO_NEED_PICK;
        }

    }

    private void initView(){

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

        abChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatWindow();
            }
        });
        audioChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        GameAcitvity.this.finish();
                    }
                });
                dialog.show();
            }
        });
    }

    private void initMsg(){
        GameMsg msg1 = new GameMsg("Hello World.",GameMsg.TYPE_SYSTEM);
        msgList.add(msg1);
        GameMsg msg2 = new GameMsg("And?",GameMsg.TYPE_RECEIVE);
        msgList.add(msg2);
        GameMsg msg3 = new GameMsg("Come on!",GameMsg.TYPE_SENT);
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
                    GameMsg msg = new GameMsg(content,GameMsg.TYPE_SENT);
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

    private void pickWindow(){
        View view = LayoutInflater.from(this).inflate(R.layout.ulw_pickhero,null);
        PopupWindow pickWindow = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
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
                heroName = heroAdapter.changeSelectItem(position);
            }
        });
        pickConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftLL.setVisibility(View.VISIBLE);
                rightLL.setVisibility(View.VISIBLE);
                backLL.setBackgroundResource(0);

                JSONObject pickJson = new JSONObject();
                try {
                    pickJson.put("typeK","选择完毕");
                    pickJson.put("messageK",heroName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                gameSocket.gameMsgToServer(pickJson.toString());

                pickConfirm.setVisibility(View.INVISIBLE);
                pickConfirm.setEnabled(false);
            }
        });
    }

    private void heroBeSelected(String heroName,int userNumber){
        switch (heroName){
            case "Alpha狼":
                gridView.getChildAt(0).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_alphawolf);
                break;
            case "狼人":
                gridView.getChildAt(1).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_werewolf);
                break;
            case "狼先知":
                gridView.getChildAt(2).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_mysticwolf);
                break;
            case "幽灵":
                gridView.getChildAt(3).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_doppelganger);
                break;
            case "失眠者":
                gridView.getChildAt(4).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_insomniac);
                break;
            case "皮匠":
                gridView.getChildAt(5).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_minion);
                break;
            case "强盗":
                gridView.getChildAt(6).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_robber);
                break;
            case "预言家":
                gridView.getChildAt(7).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_seer);
                break;
            case "捣蛋鬼":
                gridView.getChildAt(8).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_troublemaker);
                break;
            case "女巫":
                gridView.getChildAt(9).setVisibility(View.INVISIBLE);
                selectedList.get(userNumber-1).setImageResource(R.drawable.ulw_witch);
                break;
        }
    }

    private void exchangeCard(){
        firstSelect.setColorFilter(null);
        int[] locationFirst = new  int[2] ;
        firstSelect.getLocationOnScreen(locationFirst); //获取在当前窗口内的绝对坐标
        int[] locationSecond = new  int[2] ;
        secondSelect.getLocationOnScreen(locationSecond);

        TranslateAnimation tA = new TranslateAnimation(0,locationSecond[0]-locationFirst[0],
                                                        0,locationSecond[1]-locationFirst[1]);
        tA.setDuration(390);//动画时间

        TranslateAnimation tB = new TranslateAnimation(0,locationFirst[0]-locationSecond[0],
                                                    0,locationFirst[1]-locationSecond[1]);
        tB.setDuration(390);

        firstSelect.startAnimation(tA);
        secondSelect.startAnimation(tB);

        firstSelect = null;
        secondSelect = null;
    }

    private void initList(){
        ImageTextItem option1 = new ImageTextItem("Alpha狼",R.drawable.ulw_alphawolf);
        heroList.add(option1);
        ImageTextItem option9 = new ImageTextItem("狼人",R.drawable.ulw_werewolf);
        heroList.add(option9);
        ImageTextItem option5 = new ImageTextItem("狼先知",R.drawable.ulw_mysticwolf);
        heroList.add(option5);
        ImageTextItem option2 = new ImageTextItem("幽灵",R.drawable.ulw_doppelganger);
        heroList.add(option2);
        ImageTextItem option3 = new ImageTextItem("失眠者",R.drawable.ulw_insomniac);
        heroList.add(option3);
        ImageTextItem option4 = new ImageTextItem("皮匠",R.drawable.ulw_minion);
        heroList.add(option4);
        ImageTextItem option6 = new ImageTextItem("强盗",R.drawable.ulw_robber);
        heroList.add(option6);
        ImageTextItem option7 = new ImageTextItem("预言家",R.drawable.ulw_seer);
        heroList.add(option7);
        ImageTextItem option8 = new ImageTextItem("捣蛋鬼",R.drawable.ulw_troublemaker);
        heroList.add(option8);
        ImageTextItem option10 = new ImageTextItem("女巫",R.drawable.ulw_witch);
        heroList.add(option10);
    }

    View.OnClickListener clickMove = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (firstSelect == v){
                //点同一个取消选中
                firstSelect.setColorFilter(null);
                firstSelect = null;
                return;
            }

            if (firstSelect == null){
                firstSelect = (ImageView) v;
                firstSelect.setColorFilter(Color.parseColor("#59FF0000"));
            }else {
                secondSelect = (ImageView)v;
                exchangeCard();
            }
        }
    };

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
                        countdown = 15;
                        pickTimer.cancel();
                    }
                }
            });
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){

        }
        return true;
    }
}