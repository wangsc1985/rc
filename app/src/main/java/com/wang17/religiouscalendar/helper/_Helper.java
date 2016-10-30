package com.wang17.religiouscalendar.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

/**
 * Created by 阿弥陀佛 on 2016/10/2.
 */
public class _Helper {
    public static void exceptionSnackbar(Context context, String method, String message) {
        String activity = context.toString();
        activity = activity.substring(activity.lastIndexOf(".") + 1, activity.indexOf("@"));
        new AlertDialog.Builder(context).setMessage(_String.concat("来源：", _String.concat(activity, " -> ", method), "\n运行异常：", message)).setPositiveButton("知道了", null).show();
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
