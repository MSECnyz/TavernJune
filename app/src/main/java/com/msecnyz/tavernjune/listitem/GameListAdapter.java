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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.mainfragment.PlayFragment;
import com.msecnyz.tavernjune.onuwerewolf.FirstwwActivity;

import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private Context mContext;
    private List<GameListItem> gameList;

    private String gameName;
    private String gameType;
    private String gameSynopsis;

    private int gameImage;
    private int synopsisImage;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View gameCard;
        CardView cardView;
        ImageView gameImageView,synopsisImageView;
        TextView gameNameText,gameTypeText,gameSynopsisText;

        public ViewHolder(View view){
            super(view);
            gameCard = view;
            cardView = (CardView)view;
            gameImageView = (ImageView)view.findViewById(R.id.gamelist_gameImage);
            synopsisImageView = (ImageView)view.findViewById(R.id.gamelist_synopsisImage);
            gameNameText = (TextView)view.findViewById(R.id.gamelist_gamename);
            gameTypeText = (TextView)view.findViewById(R.id.gamelist_gametype);
            gameSynopsisText = (TextView)view.findViewById(R.id.gamelist_gamesynopsis);
        }
    }

    public GameListAdapter(List<GameListItem> gameList){
        this.gameList = gameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gamecard,parent,false);

        final ViewHolder holder = new ViewHolder(view);
        holder.gameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                GameListItem item = gameList.get(position);
                if (item.getGameName().equals("一夜终极狼人")){
                    Intent intent = new Intent();
                    intent.setClass(mContext, FirstwwActivity.class);
                    mContext.startActivity(intent);
                }else {
                    Toast.makeText(mContext,"假的，都是假的",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GameListItem cardItem = gameList.get(position);
        holder.gameNameText.setText(cardItem.getGameName());
        holder.gameTypeText.setText(cardItem.getGameType());
        holder.gameSynopsisText.setText(cardItem.getGameSynopsis());
        Glide.with(mContext).load(cardItem.getGameImage()).into(holder.gameImageView);
        Glide.with(mContext).load(cardItem.getSynopsisImage()).into(holder.synopsisImageView);
        //Glide可压缩图片防止内存溢出
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }
}