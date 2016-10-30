package com.wang17.religiouscalendar.emnu;

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
public enum MDtype {
    诞日(0),
    忌日(1);

    private int value = 0;

    private MDtype(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static MDtype fromInt(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return 诞日;
            case 1:
                return 忌日;
            default:
                return null;
        }
    }

    public static  MDtype fromString(String value){
        switch (value){
            case "诞日":return 诞日;
            case "忌日":return 忌日;
            default:return null;
        }
    }

    public int toInt() {
        return this.value;
    }

    public static int count(){
        return 2;
    }
}
