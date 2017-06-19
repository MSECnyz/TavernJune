package com.msecnyz.tavernjune.listitem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.onuwerewolf.GameMsg;

import java.util.List;

public class GameMsgAdapter extends RecyclerView.Adapter<GameMsgAdapter.ViewHolder> {
    private List<GameMsg> gMsgList;
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

    public GameMsgAdapter(List<GameMsg> msgList){
        gMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.item_wolfmsg,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GameMsg msg = gMsgList.get(position);
        if (msg.getType() == GameMsg.TYPE_RECEIVE){
            //收到消息则显示对应布局并隐藏另外两个
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.sysLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }else if (msg.getType()==GameMsg.TYPE_SYSTEM){
            holder.leftLayout.setVisibility(View.GONE);
            holder.sysLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.sysMsg.setText(msg.getContent());
        }else if (msg.getType() == GameMsg.TYPE_SENT){
            holder.leftLayout.setVisibility(View.GONE);
            holder.sysLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return gMsgList.size();
    }

}
