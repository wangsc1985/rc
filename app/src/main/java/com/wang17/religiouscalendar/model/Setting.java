package com.wang17.religiouscalendar.model;

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

    public enum KEYS{
        banner,bannerPositoin,welcome,welcome_duration,zodiac1,zodiac2,szr,lzr,gyz,latestVersionCode
    }
}
