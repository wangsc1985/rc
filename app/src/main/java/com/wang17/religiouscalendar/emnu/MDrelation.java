package com.wang17.religiouscalendar.emnu;

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
public enum MDrelation {
    本人(0),
    配偶(1),
    父亲(2),
    母亲(3),
    祖先(4);

    private int value = 0;

    private MDrelation(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static MDrelation fromInt(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return 本人;
            case 1:
                return 配偶;
            case 2:
                return 父亲;
            case 3:
                return 母亲;
            case 4:
                return 祖先;
            default:
                return null;
        }
    }

    public static  MDrelation fromString(String value){
        switch (value){
            case "本人":return 本人;
            case "配偶":return 配偶;
            case "父亲":return 父亲;
            case "母亲":return 母亲;
            case "祖先":return 祖先;
            default:return null;
        }
    }

    public int toInt() {
        return this.value;
    }

    public static int count(){
        return 5;
    }
}
