package com.msecnyz.tavernjune.listitem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msecnyz.tavernjune.R;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<ImageTextItem> {

    private int resourceId;

    public UserListAdapter(Context context, int textViewResourseId, List<ImageTextItem> objects){
        super(context,textViewResourseId,objects);
        resourceId = textViewResourseId;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageTextItem imageTextItem = getItem(position); //获取当前项的实例
        ViewHolder viewHolder;
        View v;
        //convertView用于缓存之前加载好的，重复利用不用重新加载
        //尽管暂时好像根本没几个选项。。
        if (convertView == null){
            v = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);//false不为当前View添加父布局
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)v.findViewById(R.id.useroption_image);
            viewHolder.textView = (TextView)v.findViewById(R.id.useroption_name);
            v.setTag(viewHolder);//缓存实例，不用每次都findViewById了
        }else {
            v = convertView;
            viewHolder = (ViewHolder)v.getTag();
        }
        viewHolder.imageView.setImageResource(imageTextItem.getImageId());
        viewHolder.textView.setText(imageTextItem.getName());

        return v;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

}
