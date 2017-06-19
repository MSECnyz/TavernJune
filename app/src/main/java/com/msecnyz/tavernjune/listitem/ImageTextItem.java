package com.msecnyz.tavernjune.listitem;

public class ImageTextItem {
    private String name;
    private int imageId;
    public ImageTextItem(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
