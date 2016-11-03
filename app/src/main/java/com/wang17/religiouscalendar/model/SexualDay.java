package com.wang17.religiouscalendar.model;

import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2016/10/17.
 */

public class SexualDay {
    private UUID id;
    private DateTime dateTime;
    private String item;
    private String summary;

    public SexualDay() {
        this.id = UUID.randomUUID();
    }

    public SexualDay(UUID id){
        this.id = id;
    }

    public SexualDay(DateTime dateTime) {
        this.id = UUID.randomUUID();
        this.dateTime = dateTime;
    }

    public SexualDay(DateTime dateTime, String item, String summary) {
        this.id = UUID.randomUUID();
        this.dateTime = dateTime;
        this.item = item;
        this.summary = summary;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
