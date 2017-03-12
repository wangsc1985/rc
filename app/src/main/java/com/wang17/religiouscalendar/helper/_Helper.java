package com.wang17.religiouscalendar.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.RunLog;

/**
 * Created by 阿弥陀佛 on 2016/10/2.
 */
public class _Helper {

    /**
     * 行房节欲期
     * @param age
     * @return
     */
    public static int getTarget(int age) {
        double day = 100;
        if (age < 18) {
            day = -1;
        }else if (age >= 18 && age < 20) {
            day = 3;
        }else if (age >= 20 && age < 30) {
            day = 4 + (age - 20) * 0.4;
        } else if (age >= 30 && age < 40) {
            day = 8 + (age - 30) * 0.8;
        } else if (age >= 40 && age < 50) {
            day = 16 + (age - 40) * 0.5;
        } else if (age >= 50 && age < 60) {
            day = 21 + (age - 50) * 0.9;
        }
        return (int)(day*24);
    }


    public static void printException(Context context, Exception e) {
        if (e.getStackTrace().length == 0)
            return;
        String msg = "";
        for (StackTraceElement ste : e.getStackTrace()) {
            if (ste.getClassName().contains(context.getPackageName())) {
                msg += "类名：\n" + ste.getClassName()
                        + "\n方法名：\n" + ste.getMethodName()
                        + "\n行号：" + ste.getLineNumber()
                        + "\n错误信息：\n" + e.getMessage() + "\n";
            }
        }
        try {
            new AlertDialog.Builder(context).setMessage(msg).setPositiveButton("知道了", null).show();
        } catch (Exception e1) {
        }
        addRunLog(context, "运行错误", msg);
        e.printStackTrace();
    }

    public static void addRunLog(Context context, String item, String message) {
        new DataContext(context).addRunLog(new RunLog(item, message));
    }

    public static void printExceptionSycn(Context context, Handler handler, Exception e) {
        try {
            if (e.getStackTrace().length == 0)
                return;

            for (StackTraceElement ste : e.getStackTrace()) {
                if (ste.getClassName().contains(context.getPackageName())) {
                    String msg = "类名：\n" + ste.getClassName()
                            + "\n方法名：\n" + ste.getMethodName()
                            + "\n行号：" + ste.getLineNumber()
                            + "\n错误信息：\n" + e.getMessage();

                    final Context finalContext = context;
                    final String message = msg;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(finalContext).setTitle("运行错误").setMessage(message).setPositiveButton("知道了", null).show();
                        }
                    });
                    break;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 判断WIFI是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWiFiActive(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info.getTypeName().equals("WIFI") && info.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 判断当前是否获得了某项权限
     *
     * @param context       例：MainActivity.this
     * @param permissionStr 例：android.permission.ACCESS_NETWORK_STATE
     * @return
     */
    public static boolean havePermission(Context context, String permissionStr) {
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionStr, context.getPackageName()));
    }
}
