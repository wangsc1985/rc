package com.wang17.religiouscalendar.model;

import com.wang17.religiouscalendar.helper._String;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by 阿弥陀佛 on 2015/6/24.
 */
public class DateTime extends GregorianCalendar {

    public DateTime() {
    }

    public DateTime(long timeInMillis){
        this.setTimeInMillis(timeInMillis);
    }

    public DateTime(int year, int month, int day) {
        this.set(year, month, day, 0, 0, 0);
        this.set(Calendar.MILLISECOND, 0);
    }

    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        this.set(year, month, day, hour, minute, second);
        this.set(Calendar.MILLISECOND, 0);
    }

    public static DateTime getToday() {
        DateTime today = new DateTime();
        return today.getDate();
    }

    /**
     * 返回一个时、分、秒、毫秒置零的此DateTime副本。
     *
     * @return
     */
    public DateTime getDate() {
        return new DateTime(this.get(YEAR), this.get(MONTH), this.get(DAY_OF_MONTH));
    }

    public DateTime addMonths(int months) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(MONTH, months);
        return dateTime;
    }

    public DateTime addDays(int days) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(DAY_OF_MONTH, days);
        return dateTime;
    }

    public DateTime addHours(int hours) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(HOUR_OF_DAY, hours);
        return dateTime;
    }

    public int getYear() {
        return this.get(YEAR);
    }

    public int getMonth() {
        return this.get(MONTH);
    }

    public int getDay() {
        return this.get(DAY_OF_MONTH);
    }

    public int getHour() {
        return this.get(HOUR_OF_DAY);
    }

    public int getMinite() {
        return this.get(MINUTE);
    }

    public int getSecond() {
        return this.get(SECOND);
    }

    public String toShortDateString() {
        return _String.concat(this.getYear(), "年", this.getMonth() + 1, "月", this.getDay(), "日");
    }

    /**
     * 格式：****年**月**日  **:**:**
     *
     * @return
     */
    public String toLongDateString() {
        return _String.concat(toShortDateString(), "  ", toTimeString());
    }

    /**
     * 格式：**:**:**
     *
     * @return
     */
    public String toTimeString() {
        return _String.concat(this.getHourStr(), ":", this.getMiniteStr(), ":", this.getSecondStr());
    }

    public String getMonthStr() {
        int tt = this.getMonth() + 1;
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getDayStr() {
        int tt = this.getDay();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getHourStr() {
        int tt = this.getHour();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getMiniteStr() {
        int tt = this.getMinite();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getSecondStr() {
        int tt = this.getSecond();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    /**
     * 格式：*天*小时*分钟*秒
     *
     * @param timeInMillis
     * @param startTag     开始标志 1：秒；2：分；3：时；4：天
     * @param endTag       开始标志 1：秒；2：分；3：时；4：天
     * @return
     */
    public static String toSpanString(long timeInMillis, int startTag, int endTag) throws Exception {
        if (startTag < endTag)
            throw new Exception("开始标志必须大于等于结束标志");

        String resutl = "";
        int day = (int) (timeInMillis / 60000 / 60 / 24);
        int hour = (int) (timeInMillis / 60000 / 60 % 24);
        if (startTag == 3)
            hour += day * 24;
        int minite = (int) (timeInMillis / 60000 % 60);
        if (startTag == 2)
            minite += hour * 60;
        int second = (int) (timeInMillis / 1000 % 60);
        if (startTag == 1)
            second += minite * 60;
        switch (startTag) {
            case 4:
                resutl += day > 0 ? day + "天" : "";
                if (endTag == 4) {
                    if (day == 0) {
                        return day + "天";
                    }
                    break;
                }
            case 3:
                resutl += hour > 0 ? hour + "小时" : "";
                if (endTag == 3) {
                    if (day == 0 && hour == 0) {
                        return hour + "小时";
                    }
                    break;
                }
            case 2:
                resutl += minite > 0 ? minite + "分钟" : "";
                if (endTag == 2) {
                    if (day == 0 && hour == 0 && minite == 0) {
                        return minite + "分钟";
                    }
                    break;
                }
            case 1:
                resutl += second > 0 ? second + "秒" : "";

                if (day == 0 && hour == 0 && minite == 0 && second == 0) {
                    return second + "秒";
                }
        }
        return resutl;
    }
}
