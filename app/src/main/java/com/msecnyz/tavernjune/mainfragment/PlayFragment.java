package com.msecnyz.tavernjune.mainfragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.msecnyz.tavernjune.MainActivity;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.GameListAdapter;
import com.msecnyz.tavernjune.listitem.GameListItem;
import com.msecnyz.tavernjune.onuwerewolf.FirstwwActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayFragment extends Fragment {

    private Toolbar mainBar;
    private RecyclerView gameRecycler;
    private List<GameListItem> gameList = new ArrayList<>();
    private GameListAdapter gameListAdapter;
    private BottomNavigationBar bottomNavigationBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // If false, root is only used to create the correct subclass of LayoutParams for the root view in the XML.
        final View v = inflater.inflate(R.layout.fragment_play, container, false);
        bottomNavigationBar = (BottomNavigationBar) this.getActivity().findViewById(R.id.bottom_navigation);

        mainBar = (Toolbar)v.findViewById(R.id.mainbar_play);
        mainBar.inflateMenu(R.menu.mainmenu);
        mainBar.setTitle("来局牌如何？");
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
                                        Toast.makeText(PlayFragment.this.getActivity(),String.valueOf(fate),Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                        break;
                    case R.id.menusearch:
                        Toast.makeText(PlayFragment.this.getActivity(), "其实暂时并不能搜索任何", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
                return true;
            }
        });

        initGames();
        gameRecycler = (RecyclerView)v.findViewById(R.id.gamelist_recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(),1);
        gameRecycler.setLayoutManager(layoutManager);
        gameListAdapter = new GameListAdapter(gameList);
        gameRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
//                if(parent.getChildLayoutPosition(view) != 0) {
                    outRect.set(0, 30, 0, 10);//设置item中内容相对边框左，上，右，下距离
//                }
            }
        });
        gameRecycler.setAdapter(gameListAdapter);

        gameRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>16){
                    if (!bottomNavigationBar.isHidden()){
                        bottomNavigationBar.hide(true);
                    }
                }else if (dy<-16){
                    if (bottomNavigationBar.isHidden()){
                        bottomNavigationBar.show(true);
                    }
                }
            }
        });

        return v;
    }

    private void initGames(){
        gameList.clear();
        gameList.add(new GameListItem("一夜终极狼人","卡牌/推理",this.getActivity().getString(R.string.ulwtips),R.drawable.ulwicon,R.drawable.ulwshow));
        gameList.add(new GameListItem("2222222","卡牌/推理",this.getActivity().getString(R.string.ulwtips),R.drawable.ulwicon,R.drawable.ulwshow));
        gameList.add(new GameListItem("3333333","卡牌/推理",this.getActivity().getString(R.string.ulwtips),R.drawable.ulwicon,R.drawable.ulwshow));
        gameList.add(new GameListItem("4444444","卡牌/推理",this.getActivity().getString(R.string.ulwtips),R.drawable.ulwicon,R.drawable.ulwshow));
    }

}
