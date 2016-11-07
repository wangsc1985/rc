package com.wang17.religiouscalendar.helper;

import android.support.v7.app.AppCompatActivity;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.model.PicNameRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 阿弥陀佛 on 2016/9/28.
 */
public class _Session extends AppCompatActivity {
    public static List<PicNameRes> welcomes;
    public static List<PicNameRes> banners;
//    public static AppInfo newVersionAppInfo;

    static {
        welcomes = new ArrayList<PicNameRes>();
        welcomes.add(new PicNameRes(R.drawable.welcome01, "观世音菩萨一"));
        welcomes.add(new PicNameRes(R.drawable.welcome02, "观世音菩萨二"));
        welcomes.add(new PicNameRes(R.drawable.welcome03, "观世音菩萨三"));
        welcomes.add(new PicNameRes(R.drawable.welcome04, "观世音菩萨四"));
        welcomes.add(new PicNameRes(R.drawable.welcome05, "观世音菩萨五"));

        banners = new ArrayList<PicNameRes>();
        banners.add(new PicNameRes(R.mipmap.banner01, "释迦牟尼佛"));
        banners.add(new PicNameRes(R.mipmap.banner02, "弥勒菩萨 - 插秧偈"));
        banners.add(new PicNameRes(R.mipmap.banner03, "大智度论 - 佛号功德"));
        banners.add(new PicNameRes(R.mipmap.banner04, "善导大师 - 念佛法要"));
        banners.add(new PicNameRes(R.mipmap.banner05, "龙门大佛"));
        banners.add(new PicNameRes(R.mipmap.banner05, "相约春季"));
    }
}
