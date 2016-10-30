package com.wang17.religiouscalendar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
public class Setting {

    private String key;
    private String value;

    public Setting(String key,String value){
        this.key=key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public enum BuddhaSelection{
        guanyin001
    }
}
