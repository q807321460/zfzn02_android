package com.jia.data;

/**
 * Created by Jia on 2016/6/26.
 */
public class AppInfo {
    private String appVersion;
    private String appName;
    private String downPath;
    public AppInfo() {
        super();
    }
    public AppInfo(String appVersion, String appName, String downPath) {
        super();
        this.appVersion = appVersion;
        this.appName = appName;
        this.downPath = downPath;
    }
    public String getAppVersion() {
        return appVersion;
    }
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getDownPath() {
        return downPath;
    }
    public void setDownPath(String downPath) {
        this.downPath = downPath;
    }
    @Override
    public String toString() {
        return "AppInfo [appVersion=" + appVersion + ", appName=" + appName + ", downPath=" + downPath + "]";
    }
}
