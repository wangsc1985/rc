package com.wang17.religiouscalendar.helper;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.exception.PermissionIsNotFullException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */

public class UpdateManager_older {
    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean isCancelUpdate = false;

    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private Handler mbUiThreadHandler;

    private static final String APP_NAME = "寿康宝鉴日历.apk";
    private static final String SOURCE = _String.concat("/apps/wp2pcs/apps/", APP_NAME);
    private static final String APK_DIR = _String.concat(Environment.getExternalStorageDirectory(), "/download");
    private static final String TARGET = _String.concat(APK_DIR, "/", APP_NAME);
    private static final String ACCESS_TOKEN = "23.49b0c9b25b4a6431ce800c7cb3839a27.2592000.1478355666.1649802760-1641135";
    private static final int PROGRESS_MAX = 100;

    static {
        File file = new File(APK_DIR);
        // 判断文件目录是否存在
        if (!file.exists()) {
            file.mkdir();
        }
    }

    //    <!--获取网络状态-->
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <!-- 访问网络权限 -->
//    <uses-permission android:name="android.permission.INTERNET" />
//    <!-- 向SD卡写入数据权限 -->
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//    <!-- 在sd卡中创建与删除文件权限 -->
//    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    public UpdateManager_older(Context context) throws PermissionIsNotFullException {
        String permission = "";
        boolean legal = true;
        if (!_Helper.havePermission(context, "android.permission.ACCESS_NETWORK_STATE")) {
            permission += "网络状态权限\n";
            legal = false;
        }
        if (!_Helper.havePermission(context, "android.permission.INTERNET")) {
            permission += "访问网络权限\n";
            legal = false;
        }
        if (!_Helper.havePermission(context, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            permission += "向SD卡写入数据权限\n";
            legal = false;
        }
//        if (!_Helper.havePermission(context, "android.permission.MOUNT_UNMOUNT_FILESYSTEMS")) {
//            permission += "在SD卡中创建与删除文件权限\n";
//            legal = false;
//        }
        if (!legal) {
            throw new PermissionIsNotFullException(permission);
        }


        this.mContext = context;
        this.mbUiThreadHandler = new Handler();
        this.isCancelUpdate = false;
    }

    private ProgressDialog dialog;
    private boolean isBackground;

    /**
     * 检测软件更新
     */
    public void checkUpdate(boolean isBackground) {

        this.isBackground = isBackground;

        //
        if (!isBackground && _Helper.isNetworkAvailable(mContext))
            dialog = ProgressDialog.show(mContext, "", "正在检查更新...", true, false);
//            dialog = new Builder(mContext).setTitle("").setMessage("正在检查更新。").setCancelable(false).show();

        //
        update();

    }

    private void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    boolean isUpdate = false;
                    if (_Helper.isNetworkAvailable(mContext)) {
                        isUpdate = isHaveNewVersion();
                    } else {
                        //
                        mbUiThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isBackground) {
                                    new Builder(mContext).setTitle("").setMessage("请先打开网络，然后再更新。").setPositiveButton("知道了", null).show();
                                }
                            }
                        });
                        return;
                    }

                    //
                    if (dialog != null)
                        dialog.dismiss();

                    if (isUpdate) {
                        // 显示升级确认对话框
                        showConfirmDialog();

                    } else if (!UpdateManager_older.this.isBackground) {
                        //
                        mbUiThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isCancelUpdate)
                                    new Builder(mContext).setTitle("").setMessage("当前已是最新版本。").setPositiveButton("知道了", null).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("wangsc", e.getMessage());
                }
            }
        }).start();
    }

    private int serviceVersionCode;
    private String appName;
    private String versionName;

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */

    private boolean isHaveNewVersion() {
        String url = "https://api.mlab.com/api/1/databases/app-manager/collections/app-info?q={\"AppName\": \"RC\"}&apiKey=7s7lwu2FGxvf7ezVUWpjuR4xMGYqSok3";
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                final String htmlStr = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(htmlStr);
                    JSONObject obj = jsonArray.getJSONObject(0);
                    serviceVersionCode = obj.getInt("VersionCode");
                    appName = obj.getString("AppName");
                    versionName = obj.getString("VersionName");
                } catch (Exception e) {
                    Log.e("wangsc", e.getMessage());
                }
            } else {
                Log.e("wangsc", "Unexpected code " + response);
            }
        } catch (IOException e) {
            Log.e("wangsc", e.getMessage());
        }

        if (serviceVersionCode > getVersionCode(mContext)) {
            return true;
        }
        return false;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showConfirmDialog() {
        mbUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {

                // 构造对话框
                Builder builder = new Builder(mContext);
                builder.setTitle("软件更新");
                builder.setMessage("检测到新版本，立即更新吗？");
                // 更新
                builder.setPositiveButton("更新", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startDownload();
                    }
                });
                // 稍后更新
                builder.setNegativeButton("稍后更新", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog noticeDialog = builder.create();
                noticeDialog.show();
            }
        });
    }

    private void startDownload() {

        if (_Helper.isNetworkAvailable(mContext)) {
            if (_Helper.isWiFiActive(mContext)) {
                showDownloadDialog();
            } else {
                //
                mbUiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new Builder(mContext).setMessage("确认要使用移动网络下载软件吗？")
                                .setPositiveButton("确定", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        showDownloadDialog();
                                    }
                                })
                                .setNegativeButton("取消", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isCancelUpdate = true;
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                });
            }
        } else {
            //
            mbUiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!isBackground)
                        new Builder(mContext).setMessage("请先打开网络，然后再更新。").setPositiveButton("知道了", null).show();
                }
            });
        }
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        mbUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // 构造软件下载对话框
                Builder builder = new Builder(mContext);
                builder.setTitle("正在升级");
                // 给下载对话框增加进度条
                final LayoutInflater inflater = LayoutInflater.from(mContext);
                View v = inflater.inflate(R.layout.softupdate_progress, null);
                mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
                builder.setView(v);
                // 取消更新
                builder.setNegativeButton("取消升级", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置取消状态
                        isCancelUpdate = true;
                    }
                });
                mDownloadDialog = builder.create();
                mDownloadDialog.show();
                // 现在文件
                downloadApkThread();
            }
        });
    }

    /**
     * 下载apk文件
     */
    private void downloadApkThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadFromBaiduPCS();
            }
        }).start();
    }

    private void showExceptionDialog(Exception e) {
        final String msg = e.getMessage();
        mbUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                new Builder(mContext).setMessage(msg).setPositiveButton("知道了", null).show();
            }
        });
    }

    private void loadFromBaiduPCS() {
        try {
            // 判断SD卡是否存在，并且是否具有读写权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 获得存储卡的路径
                String sdpath = Environment.getExternalStorageDirectory() + "/";
                mSavePath = sdpath + "download";

                mProgress.setMax(PROGRESS_MAX);

//                BaiduPCSClient api = new BaiduPCSClient();
//                api.setAccessToken(ACCESS_TOKEN); //mbOauth为使用Oauth得到的access_token
//                final BaiduPCSActionInfo.PCSSimplefiedResponse simplefiedResponse = api.downloadFileFromStream(SOURCE, TARGET, new BaiduPCSStatusListener() {
//                    @Override
//                    public void onProgress(long l, long l1) {
//                        final int value = (int) (l * PROGRESS_MAX / l1);
//
//                        if (isCancelUpdate) return;
//
//                        if (l >= l1) {
//                            installApk();
//                        }
//                        mbUiThreadHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (value <= PROGRESS_MAX)
//                                    mProgress.setProgress(value);
//                            }
//                        });
//                    }
//                });

                mProgress.setProgress(0);
            }
        } catch (Exception ex) {
            Log.e("wangsc", ex.getMessage());
            showExceptionDialog(ex);
        }
        // 取消下载对话框显示
        mDownloadDialog.dismiss();
    }

    private void loadFromNet() {
        try {
            // 判断SD卡是否存在，并且是否具有读写权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 获得存储卡的路径
                String sdpath = Environment.getExternalStorageDirectory() + "/";
                mSavePath = sdpath + "download";
                URL url = new URL(mHashMap.get("url"));
                // 创建连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                // 获取文件大小
                int length = conn.getContentLength();
                // 创建输入流
                InputStream is = conn.getInputStream();

                File file = new File(mSavePath);
                // 判断文件目录是否存在
                if (!file.exists()) {
                    file.mkdir();
                }
                File apkFile = new File(mSavePath, mHashMap.get("name"));
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                // 缓存
                byte buf[] = new byte[1024];
                // 写入到文件中
                do {
                    int numread = is.read(buf);
                    count += numread;

                    final int value = (int) (((float) count / length) * PROGRESS_MAX);
                    // 计算进度条位置
                    mbUiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(value);
                        }
                    });

                    // 更新进度
//                    mHandler.sendEmptyMessage(DOWNLOAD);
                    if (numread <= 0) {
                        // 下载完成
                        mbUiThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                installApk();
                            }
                        });
                        break;
                    }
                    // 写入文件
                    fos.write(buf, 0, numread);
                } while (!isCancelUpdate);// 点击取消就停止下载.
                fos.close();
                is.close();
            }
        } catch (Exception ex) {
            showExceptionDialog(ex);
        }
        // 取消下载对话框显示
        mDownloadDialog.dismiss();
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(TARGET);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}