package com.msecnyz.tavernjune.listitem;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.msecnyz.tavernjune.R;

import java.util.List;

public class UWolfHeroAdapter extends ArrayAdapter<ImageTextItem> {

    private int resourceId;
    private int mySelect = -1;
    private ImageTextItem imageTextItem;

    public UWolfHeroAdapter(Context context, int textViewResourseId, List<ImageTextItem> objects){
        super(context,textViewResourseId,objects);
        resourceId = textViewResourseId;
    }

    public String changeSelectItem(int position){
        imageTextItem = getItem(position);
        String heroName = null;
        if (position!=mySelect){
            mySelect = position;
            heroName = imageTextItem.getName();
            notifyDataSetChanged();
        }else {
            heroName = imageTextItem.getName();
        }
        return heroName;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        imageTextItem = getItem(position);
        ViewHolder viewHolder;
        View v;

        if (convertView == null){
            v = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)v.findViewById(R.id.herowho);
            viewHolder.textView = (TextView)v.findViewById(R.id.heroname);
            v.setTag(viewHolder);
        }else {
            v = convertView;
            viewHolder = (ViewHolder)v.getTag();
        }
        if (mySelect==position){
            Glide.with(parent.getContext()).load(imageTextItem.getImageId()).into(viewHolder.imageView);
            //viewHolder.imageView.setImageResource(imageTextItem.getImageId());
            viewHolder.imageView.setColorFilter(Color.parseColor("#59FF0000"));
            viewHolder.textView.setText(imageTextItem.getName());
        }else {
            Glide.with(parent.getContext()).load(imageTextItem.getImageId()).into(viewHolder.imageView);
            //viewHolder.imageView.setImageResource(imageTextItem.getImageId());
            viewHolder.imageView.setColorFilter(Color.parseColor("#00FFFFFF"));
            viewHolder.textView.setText(imageTextItem.getName());
        }
        return v;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

}
