package com.wang17.religiouscalendar.helper;

import java.util.Calendar;

/**
 * Created by 阿弥陀佛 on 2015/6/23.
 */
public class CalendarHelper {
    /**
     * 比较两个时间是否在同一天。
     * 注：只比较年、月、日是否相同，忽略时、分、秒、毫秒、微妙。
     *
     * @param calendar1
     * @param calendar2
     * @return
     */
    public static boolean isSameDate(Calendar calendar1, Calendar calendar2) {
        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算两个时间变量之间相隔的天数，计算精确到毫秒。
     *
     * @param calendar1
     * @param calendar2
     * @return
     */
    public static long spanTotalDays(Calendar calendar1, Calendar calendar2) {
        return (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / 1000 / 60 / 60 / 24;
    }

    public static long spanTotalHours(Calendar calendar1, Calendar calendar2) {
        return (calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / 1000 / 60 / 60;
    }

    /**
     * 得到calendar的日期（时、分、秒、毫秒清零后的calendar）。
     * @param calendar
     * @return
     */
    public static Calendar getDate(Calendar calendar) {
        Calendar cal = (Calendar)calendar.clone();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
