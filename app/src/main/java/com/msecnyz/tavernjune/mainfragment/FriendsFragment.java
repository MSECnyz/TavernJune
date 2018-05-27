package com.msecnyz.tavernjune.mainfragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.ImageTextItem;
import com.msecnyz.tavernjune.listitem.UserListAdapter;
import com.msecnyz.tavernjune.net.HttpOperation;
import com.msecnyz.tavernjune.social.ChatActivity;
import com.msecnyz.tavernjune.social.NewFriendsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    final private String TAG = "FriendsFragment";
    private Toolbar mainBar;
    private BottomNavigationBar bottomNavigationBar;
    ListView friendsListView;
    private HttpOperation httpOperation = new HttpOperation(); //完成查询之后做一个ListView装好友（是从服务器获取吗？）然后聊天界面完成。
    private String httpResponse = null;
    private List<ImageTextItem> friendsList = new ArrayList<>();
    UserListAdapter adapter; //TODO 应该换一种性能好的重置列表的方法
    private String myUserName = null;

//    private Button play,room;
//    private AudioChat audioChat;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_friends, container, false);

        bottomNavigationBar = (BottomNavigationBar) this.getActivity().findViewById(R.id.bottom_navigation);
        mainBar = (Toolbar)v.findViewById(R.id.mainbar_friends);
        mainBar.inflateMenu(R.menu.friendsmenu);
        mainBar.setTitle(R.string.app_name);
        mainBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.addfriends:
                        startActivity(new Intent(getActivity(), NewFriendsActivity.class));
                        break;
                    case R.id.menusearch:
                        Toast.makeText(FriendsFragment.this.getActivity(), "其实暂时并不能搜索任何", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
                return true;
            }
        });

        friendsListView = (ListView) v.findViewById(R.id.friendslist);

        final SharedPreferences sharedPreferences = FriendsFragment.this.getActivity().getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);
        myUserName = sharedPreferences.getString("userId","userId");

        requestFriends();
//        play = (Button)v.findViewById(R.id.audio_re);
//        room = (Button)v.findViewById(R.id.audio_room);
//
//
//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //这里只是发一下请求就没用了。
//                audioChat = new AudioChat(FriendsFragment.this.getActivity(),true);
////                audioChat.setHandler(handler);
//
//            }
//        });
//
//        room.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                audioChat.sendRoomList();
//            }
//        });

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = friendsList.get(position).getName();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });

        return v;
    }

    private void requestFriends(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpOperation.setUrl(getString(R.string.requestFriendURL));

                try {
                    httpResponse = httpOperation.searchUser(myUserName);
                    showFriendsList(httpResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showFriendsList(String result)throws JSONException{
        final JSONArray array;
        JSONObject object = new JSONObject(result);
        array = object.getJSONArray("msg2");

        Log.i(TAG,array.toString());

        friendsList.clear();
        for (int i=0;i<array.length();i++){
            friendsList.add(new ImageTextItem(array.getString(i),R.drawable.defultface));
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new UserListAdapter(getActivity(),R.layout.item_aboutuser,friendsList);
                friendsListView.setAdapter(adapter);
            }
        });
    }

}
