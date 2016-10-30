package com.wang17.religiouscalendar.emnu;

/**
 * Created by 阿弥陀佛 on 2015/12/4.
 */
public enum  Zodiac {
    无(0),鼠(1),牛(2),虎(3),兔(4),龙(5),蛇(6),马(7),羊(8),猴(9),鸡(10),狗(11),猪(12);


    private int value = 0;

    Zodiac(int value) {
        this.value = value;
    }

    /**
     * int -> Zodiac
     * @param value
     * @return
     */
    public static Zodiac fromInt(int value) {
        switch (value) {
            case 0:
                return 无;
            case 1:
                return 鼠;
            case 2:
                return 牛;
            case 3:
                return 虎;
            case 4:
                return 兔;
            case 5:
                return 龙;
            case 6:
                return 蛇;
            case 7:
                return 马;
            case 8:
                return 羊;
            case 9:
                return 猴;
            case 10:
                return 鸡;
            case 11:
                return 狗;
            case 12:
                return 猪;
            default:
                return null;
        }
    }

    /**
     * String -> Zodiac
     * @param value
     * @return
     */
    public static  Zodiac fromString(String value){
        switch (value){
            case "无":return 无;
            case "鼠":return 鼠;
            case "牛":return 牛;
            case "虎":return 虎;
            case "兔":return 兔;
            case "龙":return 龙;
            case "蛇":return 蛇;
            case "马":return 马;
            case "羊":return 羊;
            case "猴":return 猴;
            case "鸡":return 鸡;
            case "狗":return 狗;
            case "猪":return 猪;
            default:return null;
        }
    }

    /**
     * Zodiac -> int
     * @return
     */
    public int toInt() {
        return this.value;
    }

    public static int count(){
        return 13;
    }
}
