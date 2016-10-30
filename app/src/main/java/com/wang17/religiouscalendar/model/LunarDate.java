package com.wang17.religiouscalendar.model;

import com.wang17.religiouscalendar.helper._String;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 阿弥陀佛 on 2015/6/24.
 * 只存储农历月和农历日的类
 */
public class LunarDate {

    public static final List<String> Months;

    static {
        Months = new ArrayList<String>();
        Months.add("正月");
        Months.add("二月");
        Months.add("三月");
        Months.add("四月");
        Months.add("五月");
        Months.add("六月");
        Months.add("七月");
        Months.add("八月");
        Months.add("九月");
        Months.add("十月");
        Months.add("十一月");
        Months.add("十二月");
    }

    private static String[] str = new String[]{"", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    public static final List<String> Days;

    static {
        Days = new ArrayList<String>();
        for (int i = 1; i <= 10; i++) {
            Days.add("初" + str[i]);
        }
        for (int i = 1; i <= 9; i++) {
            Days.add("十" + str[i]);
        }
        Days.add("二十");
        for (int i = 1; i <= 9; i++) {
            Days.add("廿" + str[i]);
        }
        Days.add("三十");
    }

    private int month;
    private int day;

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public LunarDate(String lunarMonth, String lunarDay) {
        this.month = Months.indexOf(lunarMonth) + 1;
        this.day = Days.indexOf(lunarDay) + 1;
    }

    public LunarDate(int lunarMonth, int lunarDay) {
        this.month = lunarMonth;
        this.day = lunarDay;
    }

    @Override
    public boolean equals(Object obj) {
        LunarDate md = (LunarDate) obj;
        return md.month == this.month && md.day == this.day;
    }

    @Override
    public int hashCode() {
        return _String.concat(_String.format(this.month), _String.format(this.day)).hashCode();
    }

    @Override
    public String toString() {
        return Months.get(this.month-1)+Days.get(this.day-1);
    }
}