package com.wang17.religiouscalendar.helper;

import android.content.Context;

import com.wang17.religiouscalendar.model.Setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by 阿弥陀佛 on 2015/7/4.
 */
public class SettingContext {

    public static Setting loadSetting(Context context) throws IOException, ClassNotFoundException {
        File file = new File(context.getFilesDir()+"/setting.dat");
        Setting setting = null;

        if (file.exists()) {
            ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
            setting = (Setting) oin.readObject();
            oin.close();
        }
        return setting;
    }

    public static void saveSetting(Context context,Setting setting) throws IOException, ClassNotFoundException {
        File file = new File(context.getFilesDir()+"/setting.dat");
        if (!file.exists()) {
            file.createNewFile();
        }
        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
        oout.writeObject(setting);
        oout.close();
    }
}
