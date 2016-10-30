package com.wang17.religiouscalendar.model;

public class AppInfo {

    private String packageName;
    private int versionCode;
    private String versionName;
    private String loadUrl;
    private String accessToken;

    public AppInfo(String packageName, int versionCode, String versionName, String loadUrl,String accessToken) {
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.loadUrl = loadUrl;
        this.accessToken = accessToken;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
