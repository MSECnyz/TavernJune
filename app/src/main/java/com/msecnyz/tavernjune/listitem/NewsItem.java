package com.msecnyz.tavernjune.listitem;

/**
 * Created by neo on 2018/5/27.
 */

public class NewsItem {

    //private String imageUrl; TODO:解析网络图片
    private int imageLocal;
    private String title;
    private String content;

    public NewsItem(String title,String content,int imageLocal){
        this.title = title;
        this.content = content;
        this.imageLocal = imageLocal;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public int getImageLocal(){
        return imageLocal;
    }
}
