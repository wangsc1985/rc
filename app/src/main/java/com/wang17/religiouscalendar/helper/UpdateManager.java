package com.wang17.religiouscalendar.helper;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.model.AppInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */

public class UpdateManager {
    private static AppInfo serverAppInfo;

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

    private String cacheDir;
    private String cacheFile;
    private String mongoApiKey;
    private final int PROGRESS_MAX = 100;
//    private String baiduAccessToken;

    public UpdateManager(Context context) {
        this.mContext = context;
        this.mbUiThreadHandler = new Handler();
        this.isCancelUpdate = false;

        cacheDir = Environment.getExternalStorageDirectory() + "/download";
        cacheFile = cacheDir + "/寿康宝鉴日历.apk";
        mongoApiKey = "7s7lwu2FGxvf7ezVUWpjuR4xMGYqSok3";

//        baiduAccessToken = "23.49b0c9b25b4a6431ce800c7cb3839a27.2592000.1478355666.1649802760-1641135";

    }

    public static boolean isUpdate() {
        return serverAppInfo != null;
    }

    /**
     * 检测软件更新
     */
    public void checkUpdate() {
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isHaveNewVersion()) {
                        // 显示升级确认对话框
                        showConfirmDialog();
                    }
                } catch (Exception e) {
                    Log.e("wangsc", e.getMessage());
                }
            }
        }).start();

    }

    private void showWarningDialog(String title, String message) {
        final String t = title;
        final String m = message;
        mbUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                new Builder(mContext).setTitle(t).setMessage(m).setPositiveButton("知道了", null).show();
            }
        });
    }

    private void showWarningDialog(String message) {
        showWarningDialog("", message);
    }

    private AppInfo getAppInfoFromMongoDB() {
        try {

//            String mongoUrl = "mongodb://wangsc:351489@ds053126.mlab.com:53126/app-manager";
//            String collctionName = "app-info";
//            //
//            String whereKey = "PackageName";
//            Object whereValue = "com.wang17.religiouscalendar";
//            String orderKey = "decade";
//            Object orderValue = 1;
//            //
//            MongoClientURI uri = new MongoClientURI(mongoUrl);
//            MongoClient client = new MongoClient(uri);
//            DB db = client.getDB(uri.getDatabase());
//            DBCollection songs = db.getCollection(collctionName);
//            //
//            BasicDBObject findQuery = new BasicDBObject(whereKey, new BasicDBObject("$gte", whereValue));
//            BasicDBObject orderBy = new BasicDBObject(orderKey, orderValue);
//
//            DBCursor docs = songs.find(findQuery).sort(orderBy);
//
//            while (docs.hasNext()) {
//                DBObject doc = docs.next();
//                return new AppInfo((String)doc.get("PackageName"), (int)doc.get("VersionCode"), (String)doc.get("VersionName"), (String)doc.get("LoadUrl"), (String)doc.get("AccessToken"));
//            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    private boolean isHaveNewVersion() {
        try {
            serverAppInfo = this.getAppInfoFromMongoDB();
            if (serverAppInfo != null && serverAppInfo.getVersionCode() > mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode) {
                return true;
            } else {
                serverAppInfo = null;
                return false;
            }
        } catch (Exception e) {
            Log.e("wangsc", e.getMessage());
            return false;
        }
    }

    /**
     * 显示确认软件更新对话框
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
                builder.show();
//                Dialog noticeDialog = builder.create();
//                noticeDialog.show();
            }
        });
    }

    public void startDownload() {

        String permission = "";
        boolean isLegal = true;
        if (!_Helper.havePermission(mContext, "android.permission.ACCESS_NETWORK_STATE")) {
            permission += "网络状态权限\n";
            isLegal = false;
        }
        if (!_Helper.havePermission(mContext, "android.permission.INTERNET")) {
            permission += "访问网络权限\n";
            isLegal = false;
        }
        if (!_Helper.havePermission(mContext, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            permission += "向SD卡写入数据权限\n";
            isLegal = false;
        }
//        if (!_Helper.havePermission(context, "android.permission.MOUNT_UNMOUNT_FILESYSTEMS")) {
//            permission += "在SD卡中创建与删除文件权限\n";
//            isLegal = false;
//        }
        if (!isLegal) {
            showWarningDialog(permission);
            return;
        }

        if (!_Helper.isNetworkAvailable(mContext)) {
            //
            showWarningDialog("请先打开网络，然后再更新。");
            return;
        }
        if (!_Helper.isWiFiActive(mContext)) {
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
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
            return;
        }

        showDownloadDialog();

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
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.i("wangsc", "Download Dialog is Cancel...");
                        isCancelUpdate = true;
                    }
                });
                // 取消更新
                builder.setNegativeButton("取消升级", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isCancelUpdate = true;
                    }
                });
                mDownloadDialog = builder.create();
                mDownloadDialog.show();
                // 下载文件
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loadFromBaiduPCS();
                        } catch (Exception e) {
                            showWarningDialog(e.getMessage());
                        }
                        // 取消下载对话框显示
                        mDownloadDialog.dismiss();
                    }
                }).start();
            }
        });
    }

    private void loadFromBaiduPCS() throws Exception {

        mProgress.setMax(PROGRESS_MAX);
        File file = new File(cacheDir);
        if (!file.exists())
            file.mkdir();

//        BaiduPCSClient api = new BaiduPCSClient();
//        api.setAccessToken(serverAppInfo.getAccessToken()); //mbOauth为使用Oauth得到的access_token
//        api.downloadFileFromStream(serverAppInfo.getLoadUrl(), cacheFile, new BaiduPCSStatusListener() {
//            @Override
//            public void onProgress(long l, long l1) {
//                final int value = (int) (l * PROGRESS_MAX / l1);
//
//                if (isCancelUpdate) return;
//
//                if (l >= l1) {
//                    installApk();
//                }
//                mbUiThreadHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (value <= PROGRESS_MAX)
//                            mProgress.setProgress(value);
//                    }
//                });
//            }
//        });

        mProgress.setProgress(0);
    }

    private void loadFromNet() throws Exception {
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
        // 取消下载对话框显示
        mDownloadDialog.dismiss();
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(cacheFile);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}

