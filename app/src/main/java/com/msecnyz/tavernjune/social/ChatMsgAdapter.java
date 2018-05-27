package com.msecnyz.tavernjune.social;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msecnyz.tavernjune.R;

import java.util.List;

/**
 * Created by neo on 2018/5/26.
 */

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder>{
    private List<ChatMsg> gMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout sysLayout;
        LinearLayout rightLayout;
        TextView leftMsg,sysMsg,rightMsg;

        public ViewHolder(View view){
            super(view);
            leftLayout = (LinearLayout)view.findViewById(R.id.left_wolflayout);
            sysLayout = (LinearLayout)view.findViewById(R.id.sys_wolflayout);
            rightLayout = (LinearLayout)view.findViewById(R.id.right_wolflayout);
            leftMsg = (TextView)view.findViewById(R.id.left_wolfmsg);
            sysMsg = (TextView)view.findViewById(R.id.sys_wolfmsg);
            rightMsg = (TextView)view.findViewById(R.id.right_wolfmsg);
        }
    }

    public ChatMsgAdapter(List<ChatMsg> msgList){
        gMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.item_chat,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMsg msg = gMsgList.get(position);
        if (msg.getType() == ChatMsg.TYPE_RECEIVE){
            //收到消息则显示对应布局并隐藏另外两个
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }else if (msg.getType() == ChatMsg.TYPE_SENT){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return gMsgList.size();
    }
}
