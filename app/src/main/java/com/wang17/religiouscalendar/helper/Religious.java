package com.wang17.religiouscalendar.helper;

import android.content.Context;
import android.util.Log;

import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.model.LunarDate;
import com.wang17.religiouscalendar.model.MemorialDay;
import com.wang17.religiouscalendar.model.Setting;
import com.wang17.religiouscalendar.emnu.SolarTerm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by 阿弥陀佛 on 2015/6/24.
 */
public class Religious {

    public HashMap<DateTime, String> getReligiousDays() {
        return religiousDays;
    }

    public HashMap<DateTime, String> getRemarks() {
        return remarks;
    }

    private HashMap<DateTime, String> religiousDays;
    private HashMap<DateTime, String> remarks;
    private HashMap<LunarDate, String> lunarReligiousDays;
    private TreeMap<DateTime, SolarTerm> solarTermTreeMap;
    private DataContext dataContext;
    private String zodiac1, zodiac2;
    private Boolean swith_szr = false;
    private List<MemorialDay> memorialDays;
    private int year, month;

    public Religious(Context context, int year, int month, TreeMap<DateTime, SolarTerm> solarTermTreeMap) throws Exception {
        this.year = year;
        this.month = month;
        this.solarTermTreeMap = solarTermTreeMap;
        this.dataContext = new DataContext(context);

        Setting setting = dataContext.getSetting("zodiac1");
        zodiac1 = setting == null ? null : setting.getValue();

        setting = dataContext.getSetting("zodiac2");
        zodiac2 = setting == null ? null : setting.getValue();

        setting = dataContext.getSetting("szr");
        if (setting == null) {
            dataContext.addSetting("szr", swith_szr + "");
        } else {
            swith_szr = Boolean.parseBoolean(setting.getValue());
        }

        memorialDays = dataContext.getMemorialDays();

        long dt1 = new DateTime().getTimeInMillis();
        this.loadLunarReligiousDays();
        long dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取农历戒期数据，用时：", (double) (dt2 - dt1) / 1000, "秒"));

        dt1 = new DateTime().getTimeInMillis();
        this.loadReligiousDays(year, month);
        dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取干支戒期数据，用时：", (double) (dt2 - dt1) / 1000, "秒"));
    }

    private Map.Entry<DateTime, SolarTerm> find(DateTime startDate, DateTime endDate, SolarTerm solarTerm) {

        Map.Entry<DateTime, SolarTerm> solar = null;
        for (Map.Entry<DateTime, SolarTerm> entry : solarTermTreeMap.entrySet()) {

            if (entry.getKey().getDate().compareTo(startDate) >= 0) {
                if (entry.getKey().getDate().compareTo(endDate) > 0)
                    break;
                if (entry.getValue() == solarTerm)
                    solar = entry;
            }
        }
        return solar;
    }

    private Map.Entry<DateTime, SolarTerm> find3Fu(int year, SolarTerm solarTerm) {
        Map.Entry<DateTime, SolarTerm> solar = null;
        for (Map.Entry<DateTime, SolarTerm> entry : solarTermTreeMap.entrySet()) {
            if (entry.getKey().getDate().getYear() == year && entry.getValue() == solarTerm) {
                solar = entry;
            }
        }
        return solar;
    }

    /// <summary>
    /// 载入给定时间当月的所有干支戒期。干支戒期，依据天干地支订立，所以每年戒期的时间都是不一样的。
    /// </summary>
    /// <param name="Calendar"></param>
    private void loadReligiousDays(int year, int month) throws Exception {
        //
        religiousDays = new HashMap<DateTime, String>();
        remarks = new HashMap<DateTime, String>();

        //
        DateTime startDate = new DateTime(year, month, 1);
        DateTime tempNextMonth = startDate.addMonths(1);
        DateTime endDate = new DateTime(tempNextMonth.getYear(), tempNextMonth.getMonth(), 1).addDays(-1);
        DateTime chufuStartDate = startDate,
                chufuEndDate = startDate,
                zhongfuStartDate = startDate,
                zhongfuEndDate = startDate,
                mofuStartDate = startDate,
                mofuEndDate = startDate;

        long dt1 = new DateTime().getTimeInMillis();

        /// ********二分日********
        /// 春分 雷将发声。犯者生子五官四肢不全。父母有灾。宜从惊蛰节禁起。戒过一月。
        /// 秋分 杀气浸盛。阳气日衰。宜从白露节禁起。戒过一月。
        /// 此二节之前三后三共七日。犯之必得危疾。尤宜切戒。
        Map.Entry<DateTime, SolarTerm> solar = find(startDate.addDays(-3), endDate.addDays(3), SolarTerm.春分);

        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-3).getDate(), "春分前三日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-2).getDate(), "春分前二日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "春分前一日。犯之必得危疾。尤宜切戒。\n四离日（春分前一日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "春分日（二分日）。1、雷将发声。犯者生子五官四肢不全。父母有灾。2、犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(1).getDate(), "春分后一日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(2).getDate(), "春分后二日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(3).getDate(), "春分后三日。犯之必得危疾。尤宜切戒。");
        }
        solar = find(startDate.addDays(-3), endDate.addDays(3), SolarTerm.秋分);
        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-3).getDate(), "秋分前三日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-2).getDate(), "秋分前二日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "秋分前一日。犯之必得危疾。尤宜切戒。\n四离日（秋分前一日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "秋分日（二分日）。1、杀气浸盛。阳气日衰。2、犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(1).getDate(), "秋分后一日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(2).getDate(), "秋分后二日。犯之必得危疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(3).getDate(), "秋分后三日。犯之必得危疾。尤宜切戒。");
        }
        long dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取二分日，用时：", (double) (dt2 - dt1) / 1000, "秒"));

        dt1 = new DateTime().getTimeInMillis();
        /// *******二至日*********
        /// 夏至阴阳相争。死生分判之时。宜从芒种节禁起。戒过一月。
        /// 冬至阴阳相争。死生分判之时。宜从大雪节禁起。戒过一月。
        /// 此二节乃阴阳绝续之交。最宜禁忌。
        /// 此二至节之前三后三共七日。犯之必得急疾。尤宜切戒。
        solar = find(startDate.addDays(-3), endDate.addDays(3), SolarTerm.夏至);
        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-3).getDate(), "夏至前三日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-2).getDate(), "夏至前二日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "夏至前一日。犯之必得急疾。尤宜切戒。\n四离日（夏至前一日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "夏至日（二至日）。1、阴阳相争。死生分判之时。2、此日乃阴阳绝续之交。最宜禁忌。3、犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(1).getDate(), "夏至后一日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(2).getDate(), "夏至后二日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(3).getDate(), "夏至后三日。犯之必得急疾。尤宜切戒。");
        }
        dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取二至日，用时：", (double) (dt2 - dt1) / 1000, "秒"));

        dt1 = new DateTime().getTimeInMillis();
        solar = find(startDate.addDays(-48), endDate.addDays(3), SolarTerm.冬至);
        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-3).getDate(), "冬至前三日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-2).getDate(), "冬至前二日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "冬至前一日。犯之必得急疾。尤宜切戒。\n四离日（冬至前一日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "冬至日（二至日）。1、阴阳相争。死生分判之时。2、此日乃阴阳绝续之交。最宜禁忌。3、犯之必得急疾。尤宜切戒。\n冬至半夜子时。犯之主在一年内亡。");
            this.AddReligiousDay(solar.getKey().addDays(1).getDate(), "冬至后一日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(2).getDate(), "冬至后二日。犯之必得急疾。尤宜切戒。");
            this.AddReligiousDay(solar.getKey().addDays(3).getDate(), "冬至后三日。犯之必得急疾。尤宜切戒。");

            /// 冬至半夜子时，后庚辛日。第三戌日。犯之皆主在一年内亡。
            {
                DateTime start = solar.getKey().getDate();
                boolean touch庚 = false, touch辛 = false, touch戌3 = false;
                int count = 1;
                while (!touch庚 || !touch辛 || !touch戌3) {
                    start = start.addDays(1);
                    GanZhi ganzhi = new GanZhi(start, this.solarTermTreeMap);
                    if (!touch庚 && ganzhi.getTianGanDay().equals("庚")) {
                        this.AddReligiousDay(start, "冬至后庚日。犯之主在一年内亡。");
                        touch庚 = true;
                    }
                    if (!touch辛 && ganzhi.getTianGanDay().equals("辛")) {
                        this.AddReligiousDay(start, "冬至后辛日。犯之主在一年内亡。");
                        touch辛 = true;
                    }
                    if (!touch戌3 && ganzhi.getDiZhiDay().equals("戌")) {
                        if (count == 3) {
                            this.AddReligiousDay(start, "冬至后第三戌日。犯之主在一年内亡。");
                            touch戌3 = true;
                        }
                        count++;
                    }
                }
            }
        }
        dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取冬至、后庚辛第三戌日，用时：", (double) (dt2 - dt1) / 1000, "秒"));


        dt1 = new DateTime().getTimeInMillis();
        /// 四立日，四绝日 犯之减寿五年。
        solar = find(startDate.addDays(-60), endDate.addDays(1), SolarTerm.立春);
        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "立春日前一日（四绝日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "立春日(四立日)。犯之减寿五年。");

            /// 立春后的第五个戊日为春社日。犯之减寿五年。社日受胎者。毛发皆白。
            {
                DateTime start = solar.getKey().getDate();
                int count = 1;
                while (true) {
                    start = start.addDays(1);
                    GanZhi ganzhi = new GanZhi(start, this.solarTermTreeMap);
                    if (ganzhi.getTianGanDay().equals("戊")) {
                        if (count == 5) {
                            this.AddReligiousDay(start, "春社日（立春后第五戊日）。犯之减寿五年。社日受胎者。毛发皆白。");
                            break;
                        }
                        count++;
                    }
                }
            }
        }

        solar = find(startDate, endDate.addDays(1), SolarTerm.立夏);
        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "立夏日前一日（四绝日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "立夏日(四立日)。犯之减寿五年。");
        }

        solar = find(startDate.addDays(-60), endDate.addDays(1), SolarTerm.立秋);
        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "立秋日前一日（四绝日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "立秋日(四立日)。犯之减寿五年。");

            /// 立秋后的第五个戊日为秋社日。犯之减寿五年。社日受胎者。毛发皆白。
            {
                DateTime start = solar.getKey().getDate();
                int count = 1;
                while (true) {
                    start = start.addDays(1);
                    GanZhi ganzhi = new GanZhi(start, this.solarTermTreeMap);
                    if (ganzhi.getTianGanDay().equals("戊")) {
                        if (count == 5) {
                            this.AddReligiousDay(start, "秋社日（立秋后第五戊日）。犯之减寿五年。社日受胎者。毛发皆白。");
                            break;
                        }
                        count++;
                    }
                }
            }
        }

        solar = find(startDate, endDate.addDays(1), SolarTerm.立冬);
        if (solar != null) {
            this.AddReligiousDay(solar.getKey().addDays(-1).getDate(), "立冬日前一日（四绝日）。犯之减寿五年。");
            this.AddReligiousDay(solar.getKey().getDate(), "立冬日(四立日)。犯之减寿五年。");
        }
        dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取四立日，用时：", (double) (dt2 - dt1) / 1000, "秒"));

        dt1 = new DateTime().getTimeInMillis();

        /// 初伏：夏至后第三个庚日起到第四个庚日前一天的一段时间叫初伏，也叫头伏。犯之减寿一年。
        {
            solar = find3Fu(year, SolarTerm.夏至);

            DateTime start = solar.getKey().getDate();
            int count = 1;
            while (true) {
                start = start.addDays(1);
                GanZhi ganzhi = new GanZhi(start, this.solarTermTreeMap);
                if (ganzhi.getTianGanDay().equals("庚")) {
                    if (count == 3) {
                        chufuStartDate = start;
                    } else if (count == 4) {
                        zhongfuStartDate = start;
                        chufuEndDate = start.addDays(-1);
                        break;
                    }
                    count++;
                }
            }
        }
        /// 中伏：夏至后第四个庚日起到立秋后第一个庚日前的一段时间叫中伏，也叫二伏。犯之减寿一年。
        /// 末伏：立秋后第一个庚日起到第二个庚日前一天的一段时间叫末伏，也叫终伏。犯之减寿一年。
        {
            solar = find3Fu(year, SolarTerm.立秋);
            DateTime start = solar.getKey().getDate();
            int count = 1;
            while (true) {
                start = start.addDays(1);
                GanZhi ganzhi = new GanZhi(start, this.solarTermTreeMap);
                if (ganzhi.getTianGanDay().equals("庚")) {
                    if (count == 1) {
                        mofuStartDate = start;
                        zhongfuEndDate = start.addDays(-1);
                    } else if (count == 2) {
                        mofuEndDate = start.addDays(-1);
                        break;
                    }
                    count++;
                }
            }
        }
        dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取三伏日，用时：", (double) (dt2 - dt1) / 1000, "秒"));

        dt1 = new DateTime().getTimeInMillis();
        DateTime day = startDate;
        while (day.compareTo(endDate) <= 0) {
            GanZhi ganzhi = new GanZhi(day, this.solarTermTreeMap);
            Lunar lunar = new Lunar(day);
            int chineseMonth = lunar.getMonth();
            int chineseDay = lunar.getDay();


            /// 犯之减寿五年。（农历正月十五、七月十五、十月十五，为上中下三元）
            if (chineseDay == 15 && chineseMonth == 1) {
                this.AddReligiousDay(day, "农历正月十五（三元日）。犯之减寿五年。");
            } else if (chineseDay == 15 && chineseMonth == 7) {
                this.AddReligiousDay(day, "农历七月十五（三元日）。犯之减寿五年。");
            } else if (chineseDay == 15 && chineseMonth == 10) {
                this.AddReligiousDay(day, "农历十月十五（三元日）。犯之减寿五年。");
            }

            /// 三伏日
            if (day.compareTo(chufuEndDate.getDate()) <= 0 && day.compareTo(chufuStartDate.getDate()) >= 0) {
                this.AddReligiousDay(day, "初伏。犯之减寿一年。");
            } else if (day.compareTo(zhongfuEndDate.getDate()) <= 0 && day.compareTo(zhongfuStartDate.getDate()) >= 0) {
                this.AddReligiousDay(day, "中伏。犯之减寿一年。");
            } else if (day.compareTo(mofuEndDate.getDate()) <= 0 && day.compareTo(mofuStartDate.getDate()) >= 0) {
                this.AddReligiousDay(day, "末伏。犯之减寿一年。");
            }


            /// 毁败日：大月十八日。小月十七日。犯之得病。
            int tChineseDay = lunar.getDay();
            if (tChineseDay == 17 || tChineseDay == 18) {
                DateTime tempday = day.addDays(1);
                Lunar tmpLunar = new Lunar(tempday);
                int maxDay = lunar.getDay();
                while (tmpLunar.getMonth() == lunar.getMonth()) {
                    maxDay = lunar.getDay();
                    tempday = tempday.addDays(1);
                    tmpLunar = new Lunar(tempday);
                }
                if (maxDay == 30) {
                    if (tChineseDay == 18) {
                        this.AddReligiousDay(day, "毁败日。犯之得病。");
                    }
                } else {
                    if (tChineseDay == 17) {
                        this.AddReligiousDay(day, "毁败日。犯之得病。");
                    }
                }
            }

            /// 上弦为初七初八，下弦为二十二二十三。犯之减寿一年。
            if (chineseDay == 7 || chineseDay == 8) {
                this.AddReligiousDay(day, "上弦日。犯之减寿一年。");
            } else if (chineseDay == 22 || chineseDay == 23) {
                this.AddReligiousDay(day, "下弦日。犯之减寿一年。");
            }

            if (new Lunar(day.addDays(1)).getDay() == 1) {
                this.AddReligiousDay(day, "本月最后一天（晦日）。犯之减寿一年。");
            }

            /// 每月三辛日，犯之减寿一年。
            if (ganzhi.getTianGanDay().equals("辛")) {
                this.AddReligiousDay(day, "每月三辛日。犯之减寿一年。");
            }
            /// 甲子日。庚申日。犯之皆减寿一年。
            else if (ganzhi.getTianGanDay().equals("甲") && ganzhi.getDiZhiDay().equals("子")) {
                this.AddReligiousDay(day, "甲子日。犯之皆减寿一年。");
            } else if (ganzhi.getTianGanDay().equals("庚") && ganzhi.getDiZhiDay().equals("申")) {
                this.AddReligiousDay(day, "庚申日。犯之皆减寿一年。");
            }
            /// 丙丁日。天地仓开日。犯之皆得病。
            else if (ganzhi.getTianGanDay().equals("丙") || ganzhi.getTianGanDay().equals("丁")) {
                this.AddReligiousDay(day, "丙丁日。天地仓开日。犯之皆得病。");
            }


            /// 十恶大败日
            /// 甲己年。三月戊戌日。七月癸亥日。十月丙申日。十一月丁亥日。
            /// 乙庚年。四月壬申日。九月乙巳日
            /// 丙辛年。三月辛巳日。九月庚辰日。十月甲辰日。
            /// 戊癸年。六月己丑日。
            /// 此皆大不吉之日。宜戒
            if (
                    ((ganzhi.getTianGanYear().equals("甲") || ganzhi.getTianGanYear().equals("己"))
                            && (chineseMonth == 3 && ganzhi.getTianGanDay().equals("戊") && ganzhi.getDiZhiDay().equals("戌")
                            || chineseMonth == 7 && ganzhi.getTianGanDay().equals("癸") && ganzhi.getDiZhiDay().equals("亥")
                            || chineseMonth == 10 && ganzhi.getTianGanDay().equals("丙") && ganzhi.getDiZhiDay().equals("申")
                            || chineseMonth == 11 && ganzhi.getTianGanDay().equals("丁") && ganzhi.getDiZhiDay().equals("亥"))) ||

                            ((ganzhi.getTianGanYear().equals("乙") || ganzhi.getTianGanYear().equals("庚"))
                                    && (chineseMonth == 4 && ganzhi.getTianGanDay().equals("壬") && ganzhi.getDiZhiDay().equals("申")
                                    || chineseMonth == 9 && ganzhi.getTianGanDay().equals("乙") && ganzhi.getDiZhiDay().equals("巳"))) ||

                            ((ganzhi.getTianGanYear().equals("丙") || ganzhi.getTianGanYear().equals("辛"))
                                    && (chineseMonth == 3 && ganzhi.getTianGanDay().equals("辛") && ganzhi.getDiZhiDay().equals("巳")
                                    || chineseMonth == 9 && ganzhi.getTianGanDay().equals("庚") && ganzhi.getDiZhiDay().equals("辰")
                                    || chineseMonth == 10 && ganzhi.getTianGanDay().equals("甲") && ganzhi.getDiZhiDay().equals("辰"))) ||

                            ((ganzhi.getTianGanYear().equals("戊") || ganzhi.getTianGanYear().equals("癸"))
                                    && (chineseMonth == 6 && ganzhi.getTianGanDay().equals("己") && ganzhi.getDiZhiDay().equals("丑")))
                    ) {
                this.AddReligiousDay(day, "十恶大败日。此皆大不吉之日。宜戒");
            }

            /// 阴错日
            /// 正月庚戌日 二月辛酉日 三月庚申日 四月丁未日
            /// 五月丙午日 六月丁巳日 七月甲辰日 八月乙卯日
            /// 九月甲寅日 十月癸丑日 十一月壬子日 十二月癸亥日
            /// 此阴不足之日。俱宜戒。
            if (chineseMonth == 1 && ganzhi.getTianGanDay().equals("庚") && ganzhi.getDiZhiDay().equals("戌")
                    || chineseMonth == 2 && ganzhi.getTianGanDay().equals("辛") && ganzhi.getDiZhiDay().equals("酉")
                    || chineseMonth == 3 && ganzhi.getTianGanDay().equals("庚") && ganzhi.getDiZhiDay().equals("申")
                    || chineseMonth == 4 && ganzhi.getTianGanDay().equals("丁") && ganzhi.getDiZhiDay().equals("未")
                    || chineseMonth == 5 && ganzhi.getTianGanDay().equals("丙") && ganzhi.getDiZhiDay().equals("午")
                    || chineseMonth == 6 && ganzhi.getTianGanDay().equals("丁") && ganzhi.getDiZhiDay().equals("巳")
                    || chineseMonth == 7 && ganzhi.getTianGanDay().equals("甲") && ganzhi.getDiZhiDay().equals("辰")
                    || chineseMonth == 8 && ganzhi.getTianGanDay().equals("乙") && ganzhi.getDiZhiDay().equals("卯")
                    || chineseMonth == 9 && ganzhi.getTianGanDay().equals("甲") && ganzhi.getDiZhiDay().equals("寅")
                    || chineseMonth == 10 && ganzhi.getTianGanDay().equals("癸") && ganzhi.getDiZhiDay().equals("丑")
                    || chineseMonth == 11 && ganzhi.getTianGanDay().equals("壬") && ganzhi.getDiZhiDay().equals("子")
                    || chineseMonth == 12 && ganzhi.getTianGanDay().equals("癸") && ganzhi.getDiZhiDay().equals("亥")) {
                this.AddReligiousDay(day, "阴错日。此阴不足之日。俱宜戒。");
            }


            /// 祖先亡忌日。父母诞日、忌日。犯之皆减寿一年。
            for (MemorialDay memorialDay : memorialDays) {
                if (chineseMonth == memorialDay.getLunarDate().getMonth() && chineseDay == memorialDay.getLunarDate().getDay())
                    this.AddReligiousDay(day, memorialDay.getRelation().toString() + memorialDay.getType() + "。犯之减寿一年。");
            }

            /// 己身夫妇本命诞日。犯之皆减寿。
            //// 太岁日。犯之皆减寿一年。
            if (!_String.IsNullOrEmpty(this.zodiac1) && this.ZodiacToDizhi(this.zodiac1).equals(ganzhi.getDiZhiDay())) {
                this.AddReligiousDay(day, "本人太岁日。犯之减寿一年。");
            }
            if (!_String.IsNullOrEmpty(this.zodiac2) && this.ZodiacToDizhi(this.zodiac2).equals(ganzhi.getDiZhiDay())) {
                this.AddReligiousDay(day, "配偶太岁日。犯之减寿一年。");
            }

//            // 自定义斋日

            /// 农历戒期
            if (lunarReligiousDays.containsKey(new LunarDate(chineseMonth, chineseDay))) {
                this.AddReligiousDay(day, lunarReligiousDays.get(new LunarDate(chineseMonth, chineseDay)));
            }

            if (swith_szr) {
                if (chineseDay == 1 || chineseDay == 8 || chineseDay == 14 || chineseDay == 15 || chineseDay == 18
                        || chineseDay == 23 || chineseDay == 24 || chineseDay == 28 || chineseDay == 29 || chineseDay == 30) {
                    this.AddReligiousDay(day, "十斋日");
                }
            }

//            // 标记当月没有戒期的日子
//            if (!religiousDays.containsKey(day)) {
//                _calendar.AddUnReligiousDay(day);
//            }

            if (chineseMonth == 5) {
                this.AddRemark(day, "注：农历五月俗称五毒月，按此月宜全戒为是。");
            }
            //
            day = day.addDays(1);
        }
        dt2 = new DateTime().getTimeInMillis();
        Log.i("wangsc-runtime", _String.concat("获取便捷的持戒日，用时：", (double) (dt2 - dt1) / 1000, "秒"));
    }

    private String ZodiacToDizhi(String zodiac) {
        switch (zodiac) {
            case "鼠":
                return "子";
            case "牛":
                return "丑";
            case "虎":
                return "寅";
            case "兔":
                return "卯";
            case "龙":
                return "辰";
            case "蛇":
                return "巳";
            case "马":
                return "午";
            case "羊":
                return "未";
            case "猴":
                return "申";
            case "鸡":
                return "酉";
            case "狗":
                return "戌";
            case "猪":
                return "亥";
        }
        return "";
    }

    /// <summary>
    /// 载入农历戒期。农历戒期，依据农历时间订立，所以每年戒期相对于农历来说，是固定不变的。
    /// </summary>
    private void loadLunarReligiousDays() {
        this.lunarReligiousDays = new HashMap<LunarDate, String>();
        lunarReligiousDays.put(new LunarDate(1, 1), "天蜡。\n月朔。犯之削禄夺纪。\n玉帝校世人神气禄命。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(1, 3), "斗降、万神都会。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(1, 5), "五虚忌。");
        lunarReligiousDays.put(new LunarDate(1, 6), "六耗忌、雷斋日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(1, 7), "上会日。犯之损寿。");
        lunarReligiousDays.put(new LunarDate(1, 8), "五殿阎罗天子诞。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(1, 9), "玉皇上帝诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(1, 13), "杨公忌。");
        lunarReligiousDays.put(new LunarDate(1, 14), "三元降。犯之减寿。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(1, 15), "三元降。犯之减寿。\n月望。犯之减寿。\n上元神会。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(1, 16), "三元降。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(1, 19), "长春真人诞。");
        lunarReligiousDays.put(new LunarDate(1, 23), "四天王巡行、三尸神奏事。");
        lunarReligiousDays.put(new LunarDate(1, 25), "月晦日。犯之减寿。\n天地仓开日。犯之损寿子带疾。");
        lunarReligiousDays.put(new LunarDate(1, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(1, 28), "人神在阴（宜先一日戒）。犯之得病。");
        lunarReligiousDays.put(new LunarDate(1, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(1, 30), "月晦、司命奏事。犯之减寿。\n四天王巡行。");


        lunarReligiousDays.put(new LunarDate(2, 1), "月朔。\n一殿秦广王诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(2, 2), "万神都会。犯之夺纪。\n福德土地正神诞。犯之得祸。");
        lunarReligiousDays.put(new LunarDate(2, 3), "斗降。\n文昌帝君诞。犯之削禄夺纪。");
        lunarReligiousDays.put(new LunarDate(2, 6), "雷斋日。犯之减寿。\n东岳帝君诞。");
        lunarReligiousDays.put(new LunarDate(2, 8), "释迦牟尼佛出日。犯之夺纪。\n宋帝王诞。\n张大帝诞。\n四天王巡行");
        lunarReligiousDays.put(new LunarDate(2, 11), "杨公忌。");
        lunarReligiousDays.put(new LunarDate(2, 14), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(2, 15), "释迦牟尼佛般涅槃。\n月望、太上老君诞。犯之削禄夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(2, 17), "东方杜将军诞。");
        lunarReligiousDays.put(new LunarDate(2, 18), "四殿五官王诞。\n至圣先师孔子讳辰。犯之削禄夺纪。");
        lunarReligiousDays.put(new LunarDate(2, 19), "观音大士诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(2, 21), "普贤菩萨诞。");
        lunarReligiousDays.put(new LunarDate(2, 23), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(2, 25), "月晦日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(2, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(2, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(2, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(2, 30), "月晦、司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");

        lunarReligiousDays.put(new LunarDate(3, 1), "月朔。\n二殿楚江王诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(3, 3), "斗降。\n玄天上帝诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(3, 6), "雷斋日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(3, 8), "六殿卞城王诞。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(3, 9), "牛鬼神出。犯之产恶胎。\n杨公忌。");
        lunarReligiousDays.put(new LunarDate(3, 12), "中央五道诞。");
        lunarReligiousDays.put(new LunarDate(3, 14), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(3, 15), "月望、玄坛诞。犯之夺纪。\n昊天上帝诞。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(3, 16), "准提菩萨诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(3, 18), "中岳大帝诞、后土娘娘诞、三茅降。");
        lunarReligiousDays.put(new LunarDate(3, 20), "天地仓开日。犯之损寿。\n子孙娘娘诞。");
        lunarReligiousDays.put(new LunarDate(3, 23), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(3, 25), "月晦日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(3, 27), "斗降。\n七殿泰山王诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(3, 28), "人神在阴。犯之得病。\n苍颉至圣先师诞。犯之削禄夺纪。");
        lunarReligiousDays.put(new LunarDate(3, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(3, 30), "月晦、司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");

        lunarReligiousDays.put(new LunarDate(4, 1), "月朔。\n八殿都市王诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(4, 3), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(4, 4), "万神善化。犯之失瘏夭胎。\n文殊菩萨诞。");
        lunarReligiousDays.put(new LunarDate(4, 6), "雷斋日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(4, 7), "南斗北斗西斗同降。犯之减寿。\n杨公忌。"); ////
        lunarReligiousDays.put(new LunarDate(4, 8), "释迦牟尼佛诞。犯之夺纪。\n万神善化。犯之失瘏夭胎。\n善恶童子降。犯之血死。\n九殿平等王诞。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(4, 14), "纯阳祖师诞。犯之减寿。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(4, 15), "月望、钟离祖师诞。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(4, 16), "天地仓开日。犯之损寿。");
        lunarReligiousDays.put(new LunarDate(4, 17), "十殿转轮王诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(4, 18), "天地仓开日。\n紫微大帝诞。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(4, 20), "眼光圣母诞。");
        lunarReligiousDays.put(new LunarDate(4, 23), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(4, 25), "月晦。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(4, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(4, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(4, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(4, 30), "月晦、司命奏事。犯之减寿。\n四天王巡行（逢月小即戒廿九）。");


        lunarReligiousDays.put(new LunarDate(5, 1), "月朔、南极长生大帝诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(5, 3), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(5, 5), "地腊。\n五帝校定人官爵。犯之削禄夺纪。\n九毒日。犯之夭亡奇祸不测。\n杨公忌。");
        lunarReligiousDays.put(new LunarDate(5, 6), "九毒日。犯之夭亡奇祸不测。\n雷斋日。");
        lunarReligiousDays.put(new LunarDate(5, 7), "九毒日。犯之夭亡奇祸不测。");
        lunarReligiousDays.put(new LunarDate(5, 8), "南方五道诞、四天王巡行。");
        lunarReligiousDays.put(new LunarDate(5, 11), "天仓开日。犯之损寿。\n天下都城隍诞。");
        lunarReligiousDays.put(new LunarDate(5, 12), "炳灵公诞。");
        lunarReligiousDays.put(new LunarDate(5, 13), "关圣降神。犯之削禄夺纪。");
        lunarReligiousDays.put(new LunarDate(5, 14), "四天王巡行。\n夜子时为天地交泰。犯之三年内夫妇俱亡。");
        lunarReligiousDays.put(new LunarDate(5, 15), "月望。犯之夺纪。\n九毒日。犯之夭亡奇祸不测。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(5, 16), "九毒日。\n天地元气造化万物之辰。\n三年内夫妇俱亡。");
        lunarReligiousDays.put(new LunarDate(5, 17), "九毒日。犯之夭亡奇祸不测。");
        lunarReligiousDays.put(new LunarDate(5, 18), "张天师诞。");
        lunarReligiousDays.put(new LunarDate(5, 22), "孝蛾神诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(5, 23), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(5, 25), "九毒日。犯之夭亡奇祸不测。\n月晦日。");
        lunarReligiousDays.put(new LunarDate(5, 26), "九毒日。犯之夭亡奇祸不测。");
        lunarReligiousDays.put(new LunarDate(5, 27), "九毒日。犯之夭亡奇祸不测。\n斗降。");
        lunarReligiousDays.put(new LunarDate(5, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(5, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(5, 30), "月晦、司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");


        lunarReligiousDays.put(new LunarDate(6, 1), "月朔。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(6, 3), "斗降。犯之夺纪。\n杨公忌。");
        lunarReligiousDays.put(new LunarDate(6, 4), "南赡部洲转大法轮。犯之损寿。");
        lunarReligiousDays.put(new LunarDate(6, 6), "天仓开日。\n雷斋日。犯之损寿。");
        lunarReligiousDays.put(new LunarDate(6, 8), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(6, 10), "金粟如来诞。");
        lunarReligiousDays.put(new LunarDate(6, 13), "井泉龙王诞。");
        lunarReligiousDays.put(new LunarDate(6, 14), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(6, 15), "月望。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(6, 19), "观音大士涅槃（成道日）。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(6, 23), "南方火神诞。犯之遭回禄。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(6, 24), "雷祖诞。\n关帝诞。犯之削禄夺纪。");
        lunarReligiousDays.put(new LunarDate(6, 25), "月晦日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(6, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(6, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(6, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(6, 30), "月晦。\n司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");


        lunarReligiousDays.put(new LunarDate(7, 1), "月晦、杨公忌。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(7, 3), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(7, 5), "中会日损寿。\n一作初七。");
        lunarReligiousDays.put(new LunarDate(7, 6), "雷斋日减寿。");
        lunarReligiousDays.put(new LunarDate(7, 7), "道德腊。\n五帝校生人善恶。\n魁星诞削禄。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(7, 8), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(7, 10), "阴毒日大忌。");
        lunarReligiousDays.put(new LunarDate(7, 12), "长真谭真人诞。");
        lunarReligiousDays.put(new LunarDate(7, 13), "大势至菩萨诞。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(7, 14), "三元降。犯之减寿。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(7, 15), "月望、三元降。\n地官校籍。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(7, 16), "三元降。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(7, 18), "西王母诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(7, 19), "太岁诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(7, 22), "增福财神诞。犯之削禄夺纪。");
        lunarReligiousDays.put(new LunarDate(7, 23), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(7, 25), "月晦。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(7, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(7, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(7, 29), "杨公忌、四天王巡行。");
        lunarReligiousDays.put(new LunarDate(7, 30), "地藏菩萨诞。犯之夺纪。\n月晦、司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");


        lunarReligiousDays.put(new LunarDate(8, 1), "月朔。犯之夺纪。\n许真君诞。");
        lunarReligiousDays.put(new LunarDate(8, 3), "斗降、北斗诞。犯之削禄夺纪。\n司命灶君诞。犯之遭回禄。");
        lunarReligiousDays.put(new LunarDate(8, 5), "雷声大帝诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(8, 6), "雷斋。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(8, 8), "四天王巡行");
        lunarReligiousDays.put(new LunarDate(8, 10), "北斗大帝诞。");
        lunarReligiousDays.put(new LunarDate(8, 12), "西方五道诞。");
        lunarReligiousDays.put(new LunarDate(8, 14), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(8, 15), "月望。\n太阴朝元（宜焚香守夜）。犯之暴亡。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(8, 16), "天曹掠刷真君降。犯之贫夭。");
        lunarReligiousDays.put(new LunarDate(8, 18), "天人兴福之辰（宜斋戒，存想吉事）。");
        lunarReligiousDays.put(new LunarDate(8, 23), "四天王巡行。\n汉桓侯张显王诞。");
        lunarReligiousDays.put(new LunarDate(8, 24), "灶君夫人诞。");
        lunarReligiousDays.put(new LunarDate(8, 25), "月晦日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(8, 27), "斗降。\n至圣先师孔子诞。犯之削禄夺纪。\n杨公忌。");
        lunarReligiousDays.put(new LunarDate(8, 28), "人神在阴。犯之得病。\n四天会事。");
        lunarReligiousDays.put(new LunarDate(8, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(8, 30), "月晦、司命奏事。犯之夺纪。\n诸神考校。犯之夺算。\n四天王巡行（月小即戒廿九）。");

        lunarReligiousDays.put(new LunarDate(9, 1), "月朔、南斗诞。犯之削禄夺纪。\n自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 2), "自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 3), "五瘟神诞。\n自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 4), "自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 5), "自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 6), "自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 7), "自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 8), "四天王巡行。\n自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 9), "斗母诞。削禄夺纪。\n酆都大帝诞、玄天上帝飞升。\n自初一至初九北斗九星降。犯之夺纪。（此九日俱宜斋戒）。");
        lunarReligiousDays.put(new LunarDate(9, 10), "斗母降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(9, 11), "宜戒。");
        lunarReligiousDays.put(new LunarDate(9, 13), "孟婆尊神诞。");
        lunarReligiousDays.put(new LunarDate(9, 14), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(9, 15), "月望。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(9, 17), "金龙四大王诞。犯之水厄。");
        lunarReligiousDays.put(new LunarDate(9, 19), "日宫月宫会合。\n观世音菩萨出家日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(9, 23), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(9, 25), "月晦日。犯之减寿。\n杨公忌。");
        lunarReligiousDays.put(new LunarDate(9, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(9, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(9, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(9, 30), "药师琉璃光佛诞危疾。\n月晦日 司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");

        lunarReligiousDays.put(new LunarDate(10, 1), "月晦、民岁腊。犯之夺纪。\n四天王降一年内死。");
        lunarReligiousDays.put(new LunarDate(10, 3), "斗降、三茅诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(10, 5), "下会日。犯之损寿。\n达摩祖师诞。");
        lunarReligiousDays.put(new LunarDate(10, 6), "天曹考察。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(10, 8), "佛涅槃日大忌色欲、四天王巡行。");
        lunarReligiousDays.put(new LunarDate(10, 10), "四天王降一年内死。");
        lunarReligiousDays.put(new LunarDate(10, 11), "宜戒。");
        lunarReligiousDays.put(new LunarDate(10, 14), "三元降减寿。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(10, 15), "月望、三元降。\n下元水府校籍。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(10, 16), "三元降。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(10, 23), "杨公忌、四天王巡行。");
        lunarReligiousDays.put(new LunarDate(10, 25), "月晦日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(10, 27), "斗降。犯之夺纪。\n北极紫薇大帝降。");
        lunarReligiousDays.put(new LunarDate(10, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(10, 29), "四天王巡行");
        lunarReligiousDays.put(new LunarDate(10, 30), "月晦日、司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");

        lunarReligiousDays.put(new LunarDate(11, 1), "月朔。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(11, 3), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(11, 4), "至圣先师孔子诞削禄。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(11, 6), "西岳大帝诞。犯之削禄夺纪。");
        lunarReligiousDays.put(new LunarDate(11, 8), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(11, 11), "天仓开日。\n太乙救苦天尊诞。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(11, 14), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(11, 15), "月望。\n四天王巡行。\n上半夜犯，男死。\n下半夜犯，女死。");
        lunarReligiousDays.put(new LunarDate(11, 17), "阿弥陀佛诞。");
        lunarReligiousDays.put(new LunarDate(11, 19), "太阳日宫诞奇祸。");
        lunarReligiousDays.put(new LunarDate(11, 21), "杨公忌。");
        lunarReligiousDays.put(new LunarDate(11, 23), "张仙诞。犯之绝嗣。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(11, 25), "掠刷大夫降。犯之大凶。\n月晦日。");
        lunarReligiousDays.put(new LunarDate(11, 26), "北方五道诞。");
        lunarReligiousDays.put(new LunarDate(11, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(11, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(11, 29), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(11, 30), "月晦、司命奏事。犯之减寿。\n四天王巡行（月小即戒廿九）。");

        lunarReligiousDays.put(new LunarDate(12, 1), "月朔。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(12, 3), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(12, 6), "天仓开日。\n雷斋日。犯之减寿。");
        lunarReligiousDays.put(new LunarDate(12, 7), "掠刷大夫将。恶疾。");/////////////////
        lunarReligiousDays.put(new LunarDate(12, 8), "王侯腊。犯之夺纪。\n释迦如来成道日。\n四天王巡行。\n初旬内戊日。");
        lunarReligiousDays.put(new LunarDate(12, 12), "太素三元君朝真。");
        lunarReligiousDays.put(new LunarDate(12, 14), "四天王巡行。");
        lunarReligiousDays.put(new LunarDate(12, 15), "月望。犯之夺纪。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(12, 16), "南岳大帝诞。");
        lunarReligiousDays.put(new LunarDate(12, 19), "杨公忌。");
        lunarReligiousDays.put(new LunarDate(12, 20), "天地交道。犯之促寿。");
        lunarReligiousDays.put(new LunarDate(12, 21), "天猷上帝诞。");
        lunarReligiousDays.put(new LunarDate(12, 23), "五岳神降。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(12, 24), "司命朝天奏人善恶。犯之大祸。");
        lunarReligiousDays.put(new LunarDate(12, 25), "三清玉帝同降考察善恶。犯之奇祸。");
        lunarReligiousDays.put(new LunarDate(12, 27), "斗降。犯之夺纪。");
        lunarReligiousDays.put(new LunarDate(12, 28), "人神在阴。犯之得病。");
        lunarReligiousDays.put(new LunarDate(12, 29), "华严菩萨诞。\n四天王巡行。");
        lunarReligiousDays.put(new LunarDate(12, 30), "诸神下降，察访善恶。犯之男女俱亡。");
    }

    private void AddReligiousDay(DateTime key, String value) {
        if (!this.religiousDays.containsKey(key))
            this.religiousDays.put(key, value);
        else
            this.religiousDays.put(key, this.religiousDays.get(key) + _String.concat("\n", value));
    }

    private void AddRemark(DateTime key, String value) {
        if (!this.remarks.containsKey(key))
            this.remarks.put(key, value);
        else
            this.remarks.put(key, this.remarks.get(key) + _String.concat("\n", value));
    }
}
