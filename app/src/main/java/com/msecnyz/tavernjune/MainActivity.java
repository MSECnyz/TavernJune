package com.msecnyz.tavernjune;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.msecnyz.tavernjune.mainfragment.FriendsFragment;
import com.msecnyz.tavernjune.mainfragment.PlayFragment;
import com.msecnyz.tavernjune.mainfragment.UserFragment;
import com.msecnyz.tavernjune.mainfragment.WorldFragment;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity{

    public static MainActivity killmyself;
    static boolean touchBack = false;
    private Toolbar mainBar;
    private BottomNavigationBar bottomNavigationBar;
    private ViewPager viewPager;
    private Fragment friendsFragment = new FriendsFragment();
    private Fragment playFragment = new PlayFragment();
    private Fragment userFragment = new UserFragment();
    private Fragment worldFragment = new WorldFragment();
    private ArrayList<Fragment>fragments=new ArrayList<Fragment>();;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        killmyself = this;

        Intent intentall = getIntent();
        if (intentall.getStringExtra("extraData").equals("autoLogIn")) {
            SplashActivity.killMyself.finish();
        }else {
            FirstActivity.killMyself.finish();
            SplashActivity.killMyself.finish();
        }

//        mainBar = (Toolbar)findViewById(R.id.mainbar);
//        mainBar.inflateMenu(R.menu.mainmenu);
//        mainBar.setTitle(R.string.app_name);
//        mainBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.roll20:
//                        Random random = new Random();
//                        int fate = random.nextInt(20)+1;//0（包括）-20（不包括）之前取伪随机
//                        Snackbar.make(coordinatorLayout,String.valueOf(fate),Snackbar.LENGTH_SHORT) //getWindow().getDecorView()获得最最外层的View
//                                .setAction("ReRoll", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Random random = new Random();
//                                        int fate = random.nextInt(21);
//                                        Toast.makeText(MainActivity.this,String.valueOf(fate),Toast.LENGTH_SHORT).show();
//                                    }
//                                }).show();
//                        break;
//                    case R.id.menusearch:
//                        Toast.makeText(MainActivity.this, "其实暂时并不能搜索任何", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:break;
//                }
//                return true;
//            }
//        });

        fragmentManager = this.getSupportFragmentManager();
        fragments.add(friendsFragment);
        fragments.add(worldFragment);
        fragments.add(playFragment);
        fragments.add(userFragment);
        FragAdapter fragAdapter = new FragAdapter(fragmentManager,fragments);

        viewPager = (ViewPager)findViewById(R.id.main_viewpager);
        //viewPager.setOffscreenPageLimit(2);//缓存两个
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
                        break;
                    case 2:
                        bottomNavigationBar.selectTab(2);
                        break;
                    case 3:
                        bottomNavigationBar.selectTab(3);
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
}
