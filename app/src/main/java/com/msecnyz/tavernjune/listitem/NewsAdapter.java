package com.msecnyz.tavernjune.listitem;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msecnyz.tavernjune.R;

import java.util.List;

/**
 * Created by hasee on 2018/5/27.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;
    private List<NewsItem> newsList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView newsImage;
        TextView title,content;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            newsImage = (ImageView)view.findViewById(R.id.news_image);
            title = (TextView)view.findViewById(R.id.news_title);
            content = (TextView)view.findViewById(R.id.news_content);
        }
    }

    public NewsAdapter(List<NewsItem> newsList){
        this.newsList = newsList;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_news,parent,false);

        final NewsAdapter.ViewHolder holder = new NewsAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                NewsItem item = newsList.get(position);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        NewsItem newsItem = newsList.get(position);
        holder.title.setText(newsItem.getTitle());
        holder.content.setText(newsItem.getContent());
        Glide.with(mContext).load(newsItem.getImageLocal()).into(holder.newsImage);

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
