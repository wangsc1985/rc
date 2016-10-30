package com.wang17.religiouscalendar.helper;

import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.emnu.SolarTerm;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by 阿弥陀佛 on 2015/6/19.
 */
public class GanZhi {

    private static final com.wang17.religiouscalendar.model.DateTime MINIMAL_DATE = new DateTime(1999, 1, 1, 1, 0, 0);
    private static com.wang17.religiouscalendar.model.DateTime MAXIMAL_DATE = new DateTime(2050, 12, 31, 22, 59, 59);

    private Map<com.wang17.religiouscalendar.model.DateTime, SolarTerm> solarTermMap;
    private com.wang17.religiouscalendar.model.DateTime DateTime;
    private final String[] tianGan = {"癸", "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private final String[] diZhi = {"亥", "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private final String[] monthDiZhi = {"丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑"};

    private String tianGanYear;
    private String tianGanMonth;
    private String tianGanDay;
    private String tianGanHour;
    private String diZhiYear;
    private String diZhiMonth;
    private String diZhiDay;
    private String diZhiHour;

    public static com.wang17.religiouscalendar.model.DateTime getMaximalDate() {
        return MAXIMAL_DATE;
    }

    public static com.wang17.religiouscalendar.model.DateTime getMinimalDate() {
        return MINIMAL_DATE;
    }


    public String getTianGanYear() {
        return tianGanYear;
    }

    public String getTianGanMonth() {
        return tianGanMonth;
    }

    public String getTianGanDay() {
        return tianGanDay;
    }

    public String getTianGanHour() {
        return tianGanHour;
    }

    public String getDiZhiYear() {
        return diZhiYear;
    }

    public String getDiZhiMonth() {
        return diZhiMonth;
    }

    public String getDiZhiDay() {
        return diZhiDay;
    }

    public String getDiZhiHour() {
        return diZhiHour;
    }

    /**
     * 根据给定的公历时间得到对应的八字。
     *
     * @param dateTime
     * @param solarTermTreeMap 必须是“按Key（也就是时间）升序排列的”TreeMap
     * @throws StackOverflowError 时间超出范围时会抛出的异常。
     */
    public GanZhi(com.wang17.religiouscalendar.model.DateTime dateTime, TreeMap<com.wang17.religiouscalendar.model.DateTime, SolarTerm> solarTermTreeMap) throws Exception {
        this.DateTime = dateTime;
        this.solarTermMap = solarTermTreeMap;
        this.convertToGanZhi(dateTime);
    }

    /// <summary>
    /// 根据公历日期得到四柱天干、农历月、农历日
    /// </summary>
    /// <param name="dateTime"></param>
    private void convertToGanZhi(com.wang17.religiouscalendar.model.DateTime dateTime) throws Exception {
        if (dateTime.compareTo(MINIMAL_DATE) == -1 || dateTime.compareTo(MAXIMAL_DATE) == 1) {
            throw new Exception(_String.concat("当前时间：【", dateTime.toShortDateString(), "】超出时间允许范围【",
                    MINIMAL_DATE.getYear() , "年", MINIMAL_DATE.getMonth() , "月", MINIMAL_DATE.getDay() , "日",
                    " - ",
                    MAXIMAL_DATE.getYear() , "年", MAXIMAL_DATE.getMonth() , "月", MAXIMAL_DATE.getDay() , "日", "】！"));
        }
        // 年干支

        com.wang17.religiouscalendar.model.DateTime cal =new DateTime(2015,1,1);
        com.wang17.religiouscalendar.model.DateTime today = dateTime.getDate();
        int years = today.getYear() - 1955;

        for (Map.Entry<com.wang17.religiouscalendar.model.DateTime, SolarTerm> entry : this.solarTermMap.entrySet()) {
            if (entry.getKey().getYear()==today.getYear()&&entry.getValue()==SolarTerm.立春) {
                cal.set(entry.getKey().getYear(),entry.getKey().getMonth(),entry.getKey().getDay(),0,0,0);
                break;
            }
        }
        if (today.compareTo(cal)==-1 )
        {
            years --;
        }
        this.tianGanYear = tianGan[((int) (years % 10) + 2) % 10];
        this.diZhiYear = diZhi[((int) (years % 12) + 8) % 12];

        // 月干支
        Map.Entry<com.wang17.religiouscalendar.model.DateTime, SolarTerm> nextSolarTerm = null;
        for (Map.Entry<com.wang17.religiouscalendar.model.DateTime, SolarTerm> entry : this.solarTermMap.entrySet()) {
            cal.set(entry.getKey().getYear(), entry.getKey().getMonth(), entry.getKey().getDay(), 0, 0, 0);
            if (cal.compareTo(today) == 1) {
                nextSolarTerm = entry;
                break;
            }
        }

        int chineseMonth = 0;
        if (nextSolarTerm != null) {
            chineseMonth = ((int) nextSolarTerm.getValue().getValue() - 1) / 2 + 1;
        } else {
            chineseMonth = 11;
        }
        this.tianGanMonth = GetTianGanMonth(tianGanYear, chineseMonth);
        this.diZhiMonth = monthDiZhi[chineseMonth];

        // 日干支  1800年1月1日00:00  庚寅日 丙子时
        com.wang17.religiouscalendar.model.DateTime normDateTime = new DateTime(1800, 0, 1, 0, 0, 0).addHours(-1);
        long totalDays = CalendarHelper.spanTotalDays(dateTime, normDateTime);
        this.tianGanDay = tianGan[((int) (totalDays % 10) + 7) % 10];
        this.diZhiDay = diZhi[((int) (totalDays % 12) + 3) % 12];

        // 时干支
        long totalHours = CalendarHelper.spanTotalHours(dateTime, normDateTime);
        this.tianGanHour = tianGan[((int) (totalHours / 2 % 10) + 3) % 10];
        this.diZhiHour = diZhi[((int) (totalHours / 2 % 12) + 1) % 12];
    }

    private String GetTianGanMonth(String tianGanYear, int chineseMonth) {
        // 甲己之年丙作首
        if (tianGanYear.equals("甲") || tianGanYear.equals("己")) {
            return tianGan[(2 + chineseMonth) % 10];
        }
        //乙庚之岁戊为头
        else if (tianGanYear.equals("乙") || tianGanYear.equals("庚")) {
            return tianGan[(4 + chineseMonth) % 10];
        }
        //丙辛必定寻庚起
        else if (tianGanYear.equals("丙") || tianGanYear.equals("辛")) {
            return tianGan[(6 + chineseMonth) % 10];
        }
        //丁壬壬位顺行流
        else if (tianGanYear.equals("丁") || tianGanYear.equals("壬")) {
            return tianGan[(8 + chineseMonth) % 10];
        }
        //若问戊癸何方发 甲寅之上好追求
        else if (tianGanYear.equals("戊") || tianGanYear.equals("癸")) {
            return tianGan[(chineseMonth) % 10];
        }
        return null;
    }
}
