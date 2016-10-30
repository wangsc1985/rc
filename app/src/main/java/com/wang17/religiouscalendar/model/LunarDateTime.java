package com.wang17.religiouscalendar.model;

/**
 * Created by 阿弥陀佛 on 2015/6/19.
 * 只用于存储农历日期，不存在阳历农历转换功能。
 */
public class LunarDateTime {
    private int year;
    private int month;
    private int day;

    public LunarDateTime(int lunnarYear, int lunnarMonth, int lunnarDay) {
        this.year = lunnarYear;
        this.month = lunnarMonth;
        this.day = lunnarDay;
    }

    public int getYear(){
        return this.year;
    }
    public int getMonth(){
        return this.month;
    }
    public int getDay(){
        return this.day;
    }
    public String getYearStr() {
        StringBuilder res = new StringBuilder();
        String str = String.valueOf(year);
        for (int i = 0; i < str.length(); i++) {
            res.append(toString(str.charAt(i)));
        }
        return res.toString();
    }

    public String getMonthStr() {
        switch (month) {
            case 1:
                return "一月";
            case 2:
                return "二月";
            case 3:
                return "三月";
            case 4:
                return "四月";
            case 5:
                return "五月";
            case 6:
                return "六月";
            case 7:
                return "七月";
            case 8:
                return "八月";
            case 9:
                return "九月";
            case 10:
                return "十月";
            case 11:
                return "十一月";
            case 12:
                return "十二月";
        }
        return "";
    }

    public String getDayStr() {
        StringBuilder res = new StringBuilder();
        String str = String.valueOf(day);
        if (str.length() == 1) {
            return "初"+toString(str.charAt(0));
        } else {
            switch (str.charAt(0)) {
                case '1':
                    res.append("十");
                    if(str.charAt(1)=='0'){
                        return "初十";
                    }
                    if (str.charAt(1) != '0') {
                        res.append(toString(str.charAt(1)));
                    }
                    return res.toString();
                case '2':
                    res.append("廿");
                    if(str.charAt(1)=='0'){
                        return "二十";
                    }else{
                        res.append(toString(str.charAt(1)));
                    }
                    return res.toString();
                case '3':
                    return "三十";
            }
        }
//        switch (str.charAt())

        return "";
    }

    private String toString(char number) {
        switch (number) {
            case '0':
                return "零";
            case '1':
                return "一";
            case '2':
                return "二";
            case '3':
                return "三";
            case '4':
                return "四";
            case '5':
                return "五";
            case '6':
                return "六";
            case '7':
                return "七";
            case '8':
                return "八";
            case '9':
                return "九";
        }
        return "";
    }
}
