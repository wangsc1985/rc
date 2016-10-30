package com.wang17.religiouscalendar.emnu;

/**
 * Created by 阿弥陀佛 on 2015/6/21.
 */
public enum SolarTerm {
    立春(24), 雨水(1),
    惊蛰 ( 2), 春分 ( 3),
    清明 ( 4), 谷雨 ( 5),
    立夏 ( 6), 小满 ( 7),
    芒种 ( 8), 夏至 ( 9),
    小暑 ( 10), 大暑 ( 11),
    立秋 ( 12), 处暑 ( 13),
    白露 ( 14), 秋分 ( 15),
    寒露 ( 16), 霜降 ( 17),
    立冬 ( 18), 小雪 ( 19),
    大雪 ( 20), 冬至 ( 21),
    小寒 ( 22), 大寒 ( 23);

    private int value;
    public int getValue() {
        return value;
    }

    SolarTerm(int value) {
        this.value = value;
    }

    public static SolarTerm Int2SolarTerm(int solar) {
        switch (solar) {
            case 24:
                return SolarTerm.立春;
            case 1:
                return SolarTerm.雨水;
            case 2:
                return SolarTerm.惊蛰;
            case 3:
                return SolarTerm.春分;
            case 4:
                return SolarTerm.清明;
            case 5:
                return SolarTerm.谷雨;
            case 6:
                return SolarTerm.立夏;
            case 7:
                return SolarTerm.小满;
            case 8:
                return SolarTerm.芒种;
            case 9:
                return SolarTerm.夏至;
            case 10:
                return SolarTerm.小暑;
            case 11:
                return SolarTerm.大暑;
            case 12:
                return SolarTerm.立秋;
            case 13:
                return SolarTerm.处暑;
            case 14:
                return SolarTerm.白露;
            case 15:
                return SolarTerm.秋分;
            case 16:
                return SolarTerm.寒露;
            case 17:
                return SolarTerm.霜降;
            case 18:
                return SolarTerm.立冬;
            case 19:
                return SolarTerm.小雪;
            case 20:
                return SolarTerm.大雪;
            case 21:
                return SolarTerm.冬至;
            case 22:
                return SolarTerm.小寒;
            case 23:
                return SolarTerm.大寒;
        }
        return null;
    }
}
