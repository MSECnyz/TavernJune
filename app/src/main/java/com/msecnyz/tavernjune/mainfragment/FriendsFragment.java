package com.msecnyz.tavernjune.mainfragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.msecnyz.tavernjune.MainActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.legionsupport.AudioChat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class FriendsFragment extends Fragment {

    private Toolbar mainBar;
    private BottomNavigationBar bottomNavigationBar;

//    private Button play,room;
//    private AudioChat audioChat;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_friends, container, false);

        bottomNavigationBar = (BottomNavigationBar) this.getActivity().findViewById(R.id.bottom_navigation);
        mainBar = (Toolbar)v.findViewById(R.id.mainbar_friends);
        mainBar.inflateMenu(R.menu.mainmenu);
        mainBar.setTitle(R.string.app_name);
        mainBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.roll20:
                        Random random = new Random();
                        int fate = random.nextInt(20)+1;//0（包括）-20（不包括）之前取伪随机
                        Snackbar.make(bottomNavigationBar,String.valueOf(fate),Snackbar.LENGTH_SHORT) //getWindow().getDecorView()获得最最外层的View
                                .setAction("ReRoll", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Random random = new Random();
                                        int fate = random.nextInt(21);
                                        Toast.makeText(FriendsFragment.this.getActivity(),String.valueOf(fate),Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                        break;
                    case R.id.menusearch:
                        Toast.makeText(FriendsFragment.this.getActivity(), "其实暂时并不能搜索任何", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
                return true;
            }
        });

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

        return v;
    }
}
