package com.msecnyz.tavernjune.mainfragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.listitem.GameListAdapter;
import com.msecnyz.tavernjune.listitem.GameListItem;
import com.msecnyz.tavernjune.listitem.NewsAdapter;
import com.msecnyz.tavernjune.listitem.NewsItem;
import com.msecnyz.tavernjune.net.HttpOperation;
import com.msecnyz.tavernjune.social.ChatMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CocFragment extends Fragment {

    final private String TAG = "CocFragment";
    private RecyclerView newsRecycler;
    private NewsAdapter newsAdapter;
    private HttpOperation httpOperation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_coc,container,false);

        newsRecycler = (RecyclerView)v.findViewById(R.id.recycler_news);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(),1);
        newsRecycler.setLayoutManager(layoutManager);
        newsRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
//                if(parent.getChildLayoutPosition(view) != 0) {
                outRect.set(0, 30, 0, 10);//设置item中内容相对边框左，上，右，下距离
//                }
            }
        });

        Log.i(TAG,"############开始加载");
        searchNews();

        return v;
    }

    //TODO 添加刷新功能
    private void searchNews(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewsItem> newsList = new ArrayList<>();

                httpOperation = new HttpOperation();
                httpOperation.setUrl(getString(R.string.newsUrl));
                try {
                    String response = httpOperation.searchNews();
                    Log.i(TAG,response);
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("newsList");
                    for (int i=0;i<array.length();i++){
                        JSONObject msgObject = array.getJSONObject(i);
                        String title = msgObject.getString("title");
                        String content = msgObject.getString("description");
                        //String picUrl = msgObject.getString("pic");

                        Log.i(TAG,title+content);

                        newsList.add(new NewsItem(title,content,R.drawable.defultface));
                    }

                    showNews(newsList);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showNews(final List<NewsItem> list){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsAdapter = new NewsAdapter(list);
                newsRecycler.setAdapter(newsAdapter);
            }
        });
    }

}
