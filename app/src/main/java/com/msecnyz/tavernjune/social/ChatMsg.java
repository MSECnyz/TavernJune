package com.msecnyz.tavernjune.social;

/**
 * Created by neo on 2018/5/26.
 */

public class ChatMsg {

    public static final int TYPE_RECEIVE = 1;
    public static final int TYPE_SENT = 2;
    private String content;
    private int type;

    public ChatMsg(String content,int type){
        this.content = content;
        this.type =type;
    }

    public String getContent(){
        return content;
    }

    public int getType(){
        return type;
    }
}
