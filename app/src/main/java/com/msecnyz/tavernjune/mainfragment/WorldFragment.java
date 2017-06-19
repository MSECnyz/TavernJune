package com.msecnyz.tavernjune.mainfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msecnyz.tavernjune.R;

import java.util.ArrayList;

public class WorldFragment extends Fragment {
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
