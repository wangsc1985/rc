package com.wang17.religiouscalendar.model;

import com.wang17.religiouscalendar.emnu.MDrelation;
import com.wang17.religiouscalendar.emnu.MDtype;

import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
public class MemorialDay{

    public UUID id;
    public MDtype type;
    public MDrelation relation;
    public LunarDate lunarDate;

    public MemorialDay() {
        this.id=UUID.randomUUID();
    }

    public MDtype getType() {
        return type;
    }
    public LunarDate getLunarDate() {
        return lunarDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setType(MDtype type) {
        this.type = type;
    }

    public void setLunarDate(LunarDate lunarDate) {
        this.lunarDate = lunarDate;
    }

    public MDrelation getRelation() {
        return relation;
    }

    public void setRelation(MDrelation relation) {
        this.relation = relation;
    }

}
