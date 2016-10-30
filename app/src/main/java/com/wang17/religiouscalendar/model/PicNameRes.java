package com.wang17.religiouscalendar.model;

/**
 * Created by 阿弥陀佛 on 2016/9/28.
 */
public class PicNameRes {

    private int resId;
    private String listItemString;

    public PicNameRes(int resId, String listItemString){
        this.resId = resId;
        this.listItemString = listItemString;
    }

    public int getResId() {
        return resId;
    }

    public String getListItemString() {
        return listItemString;
    }
}
