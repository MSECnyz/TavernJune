package com.msecnyz.tavernjune.mainfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.msecnyz.tavernjune.R;

import java.util.ArrayList;
import java.util.Random;

public class WorldFragment extends Fragment {
    private Toolbar mainBar;
    private BottomNavigationBar bottomNavigationBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Fragment dndFragment = new DndFragment();
    private Fragment cocFragment = new CocFragment();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_world, container, false);

        bottomNavigationBar = (BottomNavigationBar) this.getActivity().findViewById(R.id.bottom_navigation);
        mainBar = (Toolbar)v.findViewById(R.id.mainbar_world);
        mainBar.inflateMenu(R.menu.mainmenu);
        mainBar.setTitle("巨龙或是克苏鲁");
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
                                        Toast.makeText(WorldFragment.this.getActivity(),String.valueOf(fate),Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                        break;
                    case R.id.menusearch:
                        Toast.makeText(WorldFragment.this.getActivity(), "其实暂时并不能搜索任何", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
                return true;
            }
        });

        viewPager = (ViewPager) v.findViewById(R.id.rpg_viewpager);
        fragmentManager = this.getChildFragmentManager();
        if (fragments.size() == 0) {
            //保证每次执行OnCreateView时只新建第一次
            fragments.add(dndFragment);
            fragments.add(cocFragment);
        }
        FragAdapter fragAdapter = new FragAdapter(fragmentManager, fragments);
        viewPager.setAdapter(fragAdapter);
        //true:whether this layout should refresh its contents if the given ViewPager's content changes

        tabLayout = (TabLayout) v.findViewById(R.id.rpg_tab);
        //setupWithViewPager要在viewpager初始化之后
        tabLayout.setupWithViewPager(viewPager, true);
        //setPagerAdapter有removeAllTabs();方法，所以要在后面getTab
        tabLayout.getTabAt(0).setText("DND");
        tabLayout.getTabAt(1).setText("COC");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });



        return v;
    }

    public static class FragAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;

        public FragAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
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

}
