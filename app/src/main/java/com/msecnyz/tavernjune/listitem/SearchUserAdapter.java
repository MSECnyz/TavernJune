package com.msecnyz.tavernjune.listitem;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msecnyz.tavernjune.R;

import java.util.ArrayList;

/**
 * Created by neo on 2018/2/27.
 */

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private Context context;
    private Handler handler;
    private ArrayList<ImageTextItem> userList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        View userItem;
        Button add;
        ImageView userPortrait;
        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            userItem = itemView;
            add = (Button)itemView.findViewById(R.id.addconfirm);
            userPortrait = (ImageView)itemView.findViewById(R.id.friends_userimage);
            userName = (TextView)itemView.findViewById(R.id.friends_username);
        }
    }

    public SearchUserAdapter(ArrayList<ImageTextItem> userList,Handler handler) {
        this.userList = userList;
        this.handler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_searchuser,parent,false);

        final ViewHolder holder = new ViewHolder(view);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ImageTextItem item = userList.get(position);
                String friendName = item.getName();
                Message message = new Message();
                message.obj = friendName;
                handler.sendMessage(message);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageTextItem cardItem = userList.get(position);
        holder.userName.setText(cardItem.getName());
        if (cardItem.getImageId()!=0)Glide.with(context).load(cardItem.getImageId()).into(holder.userPortrait);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
