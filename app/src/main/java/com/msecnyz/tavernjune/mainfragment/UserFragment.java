package com.msecnyz.tavernjune.mainfragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.msecnyz.tavernjune.FirstActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.UserListAdapter;
import com.msecnyz.tavernjune.listitem.ImageTextItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserFragment extends Fragment {

    private Toolbar mainBar;
    private BottomNavigationBar bottomNavigationBar;
    private List<ImageTextItem> optionList = new ArrayList<>();
    private Button userExit;
    private TextView setUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        bottomNavigationBar = (BottomNavigationBar) this.getActivity().findViewById(R.id.bottom_navigation);
        mainBar = (Toolbar)v.findViewById(R.id.mainbar_user);
        mainBar.inflateMenu(R.menu.mainmenu);
        mainBar.setTitle("更多");
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
                                        Toast.makeText(UserFragment.this.getActivity(),String.valueOf(fate),Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                        break;
                    case R.id.menusearch:
                        Toast.makeText(UserFragment.this.getActivity(), "其实暂时并不能搜索任何", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
                return true;
            }
        });

        if (optionList.size()==0){
            initList();
        }
        UserListAdapter adapter = new UserListAdapter(this.getActivity(),R.layout.item_useroption,optionList);
        ListView listView = (ListView)v.findViewById(R.id.userlist);
        listView.setAdapter(adapter);

        final SharedPreferences sharedPreferences = UserFragment.this.getActivity().getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);

        setUserId = (TextView)v.findViewById(R.id.setuserid);
        setUserId.setText(sharedPreferences.getString("userId","userId"));


        userExit = (Button)v.findViewById(R.id.userexit);
        userExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId","我的ID");
                editor.putString("password","");
                editor.putString("autoLogIn","false");
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(UserFragment.this.getActivity(), FirstActivity.class);
                intent.putExtra("extraData","userExit");
                UserFragment.this.getActivity().startActivity(intent);
            }
        });

        return v;
    }

    private void initList(){
        ImageTextItem optionA = new ImageTextItem("这是第一个暂时没用的选项",R.drawable.userlistitem1);
        optionList.add(optionA);
        ImageTextItem optionB = new ImageTextItem("这是第二个暂时没用的选项",R.drawable.userlistitem2);
        optionList.add(optionB);
        ImageTextItem optionC = new ImageTextItem("下面是第四个暂时没用的选项",R.drawable.userlistitem3);
        optionList.add(optionC);
        ImageTextItem optionD = new ImageTextItem("上面是第三个暂时没用的选项",R.drawable.userlistitem4);
        optionList.add(optionD);
    }

}
