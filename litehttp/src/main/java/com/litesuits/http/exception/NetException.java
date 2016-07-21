package com.litesuits.http.exception;

public enum NetException {
    NetworkNotAvilable("Network Is Not Avilable", "未连接网络"),
    NetworkUnstable("Network Is Unstable", "网络不稳定"),
    NetworkDisabled("Current Network Is Disabled By Your Setting", "已禁用该网络类型"),
    NetworkUnreachable("Network is unreachable", "网络无法访问");

    private static final String TAG = NetException.class.getName();
    public String reason;
    public String chiReason;

    NetException(String name, String chiName) {
        this.reason = name;
        this.chiReason = chiName;
    }

    @Override
    public String toString() {
        return TAG + ": " + this.reason + " (" + this.chiReason + ")";
    }
}