package com.msecnyz.tavernjune;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.msecnyz.tavernjune.mainfragment.FriendsFragment;
import com.msecnyz.tavernjune.mainfragment.PlayFragment;
import com.msecnyz.tavernjune.mainfragment.UserFragment;
import com.msecnyz.tavernjune.mainfragment.WorldFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity{

    public static MainActivity killmyself;
    static boolean touchBack = false;

    private final  String JoinIn = "#进#入#游#戏";
    private final String exit = "#退#出#游#戏";

    private BottomNavigationBar bottomNavigationBar;
    private ViewPager viewPager;
    private Fragment friendsFragment = new FriendsFragment();
    private Fragment playFragment = new PlayFragment();
    private Fragment userFragment = new UserFragment();
    private Fragment worldFragment = new WorldFragment();
    private ArrayList<Fragment>fragments=new ArrayList<Fragment>();;
    private FragmentManager fragmentManager;

    private FirstService.MsgBinder myBinder;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        killmyself = this;

        fragmentManager = this.getSupportFragmentManager();
        fragments.add(friendsFragment);
        fragments.add(worldFragment);
        fragments.add(playFragment);
        fragments.add(userFragment);
        FragAdapter fragAdapter = new FragAdapter(fragmentManager,fragments);

        viewPager = (ViewPager)findViewById(R.id.main_viewpager);
        //viewPager.setOffscreenPageLimit(X);//缓存X个
        viewPager.setAdapter(fragAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationBar.selectTab(0);
                        break;
                    case 1:
                        bottomNavigationBar.selectTab(1);
                        if (bottomNavigationBar.isHidden()){
                            bottomNavigationBar.show(true);
                        }
                        break;
                    case 2:
                        bottomNavigationBar.selectTab(2);
                        break;
                    case 3:
                        bottomNavigationBar.selectTab(3);
                        if (bottomNavigationBar.isHidden()){
                            bottomNavigationBar.show(true);
                        }
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.friendswhite,"好友").setActiveColor("#000000"))
                                .addItem(new BottomNavigationItem(R.drawable.worldwhite,"冒险地图").setActiveColor("#1296DB"))
                                .addItem(new BottomNavigationItem(R.drawable.playwhite,"圆桌厅").setActiveColor("#F4EA2A"))
                                .addItem(new BottomNavigationItem(R.drawable.userwhite,"我").setActiveColor("#D81E06"))
                                    .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position){
                    case 0:
                        viewPager.setCurrentItem(0,false); //false取消viewpager切换动画
                        break;
                    case 1:
                        viewPager.setCurrentItem(1,false);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2,false);
                        break;
                    case 3:
                        viewPager.setCurrentItem(3,false);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onTabUnselected(int position) {}
            @Override
            public void onTabReselected(int position) {}
        });
    }

    @Override
    protected void activityReady() {
        super.activityReady();
        Intent intentall = getIntent();
        if (intentall.getStringExtra("extraData").equals("autoLogIn")) {
            if (SplashActivity.killMyself!=null)SplashActivity.killMyself.finish();
        }else if (intentall.getStringExtra("extraData").equals("default")){
            if (FirstActivity.killMyself!=null)FirstActivity.killMyself.finish();
            if (SplashActivity.killMyself!=null)SplashActivity.killMyself.finish();
        }

//        Intent startIntent = new Intent(this, FirstService.class);
//        bindService(startIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    public static class FragAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;

        public FragAdapter(FragmentManager fm,ArrayList<Fragment> fragments){
            super(fm);
            this.fragments=fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();//Fragment的个数
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);//返回第position个Fragment
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (touchBack){
                MainActivity.this.finish();
            }else {
                touchBack = true;
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            touchBack = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
//        sendMsgToServer(exit);
//        unbindService(serviceConnection);
        super.onDestroy();
    }

    private void sendMsgToServer(final String msg){
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson);
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

            sendMsgToServer(JoinIn);

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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            //结束绑定会执行此处

        }
    };


}
