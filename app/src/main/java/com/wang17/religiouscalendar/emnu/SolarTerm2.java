package com.wang17.religiouscalendar.emnu;

/**
 * Created by 阿弥陀佛 on 2015/6/21.
 */
public enum SolarTerm2 {
    立春(2), 雨水(3),
    惊蛰 ( 4), 春分 ( 5),
    清明 ( 6), 谷雨 ( 7),
    立夏 ( 8), 小满 ( 9),
    芒种 ( 10), 夏至 ( 11),
    小暑 ( 12), 大暑 ( 13),
    立秋 ( 14), 处暑 ( 15),
    白露 ( 16), 秋分 ( 17),
    寒露 ( 18), 霜降 ( 19),
    立冬 ( 20), 小雪 ( 21),
    大雪 ( 22), 冬至 ( 23),
    小寒 ( 0), 大寒 ( 1);

    private int value;
    public int getValue() {
        return value;
    }

    SolarTerm2(int value) {
        this.value = value;
    }

    public static SolarTerm2 Int2SolarTerm(int solar) {
        switch (solar) {
            case 24:
                return SolarTerm2.立春;
            case 1:
                return SolarTerm2.雨水;
            case 2:
                return SolarTerm2.惊蛰;
            case 3:
                return SolarTerm2.春分;
            case 4:
                return SolarTerm2.清明;
            case 5:
                return SolarTerm2.谷雨;
            case 6:
                return SolarTerm2.立夏;
            case 7:
                return SolarTerm2.小满;
            case 8:
                return SolarTerm2.芒种;
            case 9:
                return SolarTerm2.夏至;
            case 10:
                return SolarTerm2.小暑;
            case 11:
                return SolarTerm2.大暑;
            case 12:
                return SolarTerm2.立秋;
            case 13:
                return SolarTerm2.处暑;
            case 14:
                return SolarTerm2.白露;
            case 15:
                return SolarTerm2.秋分;
            case 16:
                return SolarTerm2.寒露;
            case 17:
                return SolarTerm2.霜降;
            case 18:
                return SolarTerm2.立冬;
            case 19:
                return SolarTerm2.小雪;
            case 20:
                return SolarTerm2.大雪;
            case 21:
                return SolarTerm2.冬至;
            case 22:
                return SolarTerm2.小寒;
            case 23:
                return SolarTerm2.大寒;
        }
        return null;
    }
}
