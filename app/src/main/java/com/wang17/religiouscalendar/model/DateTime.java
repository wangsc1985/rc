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

    public String toShortDateString() {
        return _String.concat(this.getYear() , "年", this.getMonth() + 1, "月", this.getDay() ,"日");
    }
}
