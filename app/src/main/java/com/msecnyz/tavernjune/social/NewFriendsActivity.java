package com.msecnyz.tavernjune.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.msecnyz.tavernjune.BaseActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.ImageTextItem;
import com.msecnyz.tavernjune.listitem.SearchUserAdapter;
import com.msecnyz.tavernjune.net.HttpOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by neo on 2018/2/27.
 */

public class NewFriendsActivity extends BaseActivity {

    private EditText addUserId;
    private RecyclerView recyclerView;
    private ArrayList<ImageTextItem> userList = new ArrayList<>();
    private SearchUserAdapter adapter;
    HttpOperation httpOperation = new HttpOperation();
    String httpResponse = null;
    String myUserName = null;


    private final String TAG = "NewFriendsActivity";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            final String userName = (String) msg.obj;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpOperation.setUrl(getString(R.string.addFriendsHttpURL));
                    try {
                        httpResponse = httpOperation.addNewFriends(myUserName,userName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG,httpResponse);
                }
            }).start();

            if (httpResponse.contains("失败")){
                Toast.makeText(NewFriendsActivity.this,"添加失败,二者已是好友",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(NewFriendsActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void activityReady() {
        super.activityReady();

        SharedPreferences sharedPreferences = this.getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        myUserName = sharedPreferences.getString("userId","userId");
    }

    @Override
    protected void setInterface() {
        setContentView(R.layout.activity_newfriends);
    }

    @Override
    protected void initViews() {
        addUserId = (EditText)findViewById(R.id.add_userid);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_searchuser);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void setListener() {
        addUserId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            httpOperation.setUrl(getString(R.string.searchUserHttpURL));
                            try {
                                Log.i(TAG,addUserId.getText().toString());
                                httpResponse = httpOperation.searchUser(addUserId.getText().toString());
                                showSearchResult(httpResponse);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                return false;
            }
        });
    }

    private void showSearchResult(String result) throws JSONException {
        JSONArray array;
        JSONObject object = new JSONObject(result);
        array = object.getJSONArray("msg2");

        Log.i(TAG,array.toString());

        for (int i=0;i<array.length();i++){
            userList.add(new ImageTextItem(array.getString(i),0));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new SearchUserAdapter(userList,handler);
                recyclerView.setAdapter(adapter);

            }
        });

        if (array.length()==0){
            Toast.makeText(NewFriendsActivity.this,"没有该用户",Toast.LENGTH_SHORT).show();
        }
    }
}
