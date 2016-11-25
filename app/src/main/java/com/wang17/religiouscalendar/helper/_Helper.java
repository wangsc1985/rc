package com.wang17.religiouscalendar.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

/**
 * Created by 阿弥陀佛 on 2016/10/2.
 */
public class _Helper {

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
