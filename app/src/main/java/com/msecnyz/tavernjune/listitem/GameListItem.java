package com.msecnyz.tavernjune.listitem;


public class GameListItem {

    private String gameName;
    private String gameType;
    private String gameSynopsis;

    private int gameImage;
    private int synopsisImage;

    public GameListItem (String gameName,String gameType,String gameSynopsis,int gameImage,int synopsisImage){
        this.gameName = gameName;
        this.gameType = gameType;
        this.gameSynopsis = gameSynopsis;
        this.gameImage = gameImage;
        this.synopsisImage = synopsisImage;
    }

    public String getGameName(){
        return gameName;
    }

    public String getGameType(){
        return gameType;
    }

    public String getGameSynopsis(){
        return gameSynopsis;
    }

    public int getGameImage(){
        return gameImage;
    }

    public int getSynopsisImage(){
        return synopsisImage;
    }

}
