package com.msecnyz.tavernjune.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.msecnyz.tavernjune.BaseActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.mainfragment.FriendsFragment;
import com.msecnyz.tavernjune.net.HttpOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 2018/5/26.
 */

public class ChatActivity extends BaseActivity {

    final private String TAG = "ChatActivity";
    private Toolbar mainBar;
    private String oppositeUserName;
    private String myUserName;

    final String msg1 = "聊天列表";
    final String msg2 = "没有聊天";

    private List<ChatMsg> msgList = new ArrayList<>();
    private EditText editText;
    private Button send;
    private RecyclerView recyclerView;
    private ChatMsgAdapter adapter;

    @Override
    protected void activityReady() {
        super.activityReady();
        oppositeUserName = getIntent().getStringExtra("userName");
        final SharedPreferences sharedPreferences = this.getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        myUserName = sharedPreferences.getString("userId","userId");
    }

    @Override
    protected void setInterface() {
        super.setInterface();
        setContentView(R.layout.activity_chat);
        mainBar = (Toolbar)findViewById(R.id.mainbar_play);
        mainBar.inflateMenu(R.menu.mainmenu);
        mainBar.setTitle(oppositeUserName);
    }

    @Override
    protected void initViews() {
        super.initViews();

        editText = (EditText)findViewById(R.id.sendmsgtext);
        send = (Button)findViewById(R.id.send);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMsgAdapter(msgList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        super.setListener();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    JSONObject chatJson = new JSONObject();
                    try {
                        chatJson.put("accepter", oppositeUserName);
                        chatJson.put("informer", myUserName);
                        chatJson.put("title","chat");
                        chatJson.put("message",editText.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendChatMsg(chatJson.toString());
                    showMsg(editText.getText().toString(),ChatMsg.TYPE_SENT);
                    editText.setText("");
                }
            }
        });

        requestChatMsg();
    }


    private String requestChatMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpOperation operation = new HttpOperation();
                operation.setUrl(getString(R.string.ReciveChatMsgURL));
                try {
                    while (true){
                        String response = operation.requestChatMsg(myUserName,oppositeUserName);
                        JSONArray array;
                        JSONObject object = new JSONObject(response);
                        Log.i(TAG,object.getString("title"));
                        if (object.getString("title").equals(msg1)) {
                            array = object.getJSONArray("infos");
                            for (int i=0;i<array.length();i++){
                                JSONObject msgObject = array.getJSONObject(i);
                                String msg = msgObject.getString("message");
                                Log.i(TAG,msg);
                                showMsg(msg,ChatMsg.TYPE_RECEIVE);
                            }
                        }
                        Log.i(TAG,"********once");
                        Thread.sleep(3000);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (InterruptedException e){

                }
            }
        }).start();

        return "wait";
    }

    private void showMsg(final String msg, final int type){

        final ChatMsg msgwhere = new ChatMsg(msg,type);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgList.add(msgwhere);
                adapter.notifyItemInserted(msgList.size()-1);
                recyclerView.scrollToPosition(msgList.size()-1);
            }
        });
    }

    private void sendChatMsg(final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response;
                final HttpOperation operation = new HttpOperation();
                operation.setUrl(getString(R.string.sendChatMsgURL));
                try {
                   response = operation.sendChatMsg(msg);
                    Log.i(TAG,response);
                    if (response.equals("false")){
                        Toast.makeText(ChatActivity.this,"消息发送失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
