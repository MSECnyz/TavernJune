package com.msecnyz.tavernjune.onuwerewolf;

public class GameMsg {
    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_RECEIVE = 1;
    public static final int TYPE_SENT = 2;
    private String content;
    private int type;

    public GameMsg(String content,int type){
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
