package com.wang17.religiouscalendar.model;

import com.wang17.religiouscalendar.helper.Lunar;

/**
 * Created by 阿弥陀佛 on 2015/6/19.
 */
public class CalendarItem {
    private DateTime yangLi;
    private LunarDateTime nongLi;
    private String religious;
    private String remarks;

    public void setReligious(String religious) {
        this.religious = religious;
    }

    public CalendarItem(DateTime yangLi){
        this.yangLi = yangLi;
        Lunar lunar = new Lunar(yangLi);
        this.nongLi = new LunarDateTime(lunar.getYear(), lunar.getMonth(), lunar.getDay());
    }

    public LunarDateTime getNongLi() {
        return nongLi;
    }

    public DateTime getYangLi() {
        return yangLi;
    }

    public String getReligious() {
        return religious;
    }
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
