package com.wang17.religiouscalendar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
public class UserReligiousDays implements Serializable {
    private static final long serialVersionUID = 1L;
    public String religiousName;
    public List<Integer> religiousDays;

    public UserReligiousDays(String religiousName) {
        this.religiousName = religiousName;
        this.religiousDays = new ArrayList<Integer>();
    }

    public String getReligiousName() {
        return religiousName;
    }

    public List<Integer> getReligiousDays() {
        return religiousDays;
    }
}
