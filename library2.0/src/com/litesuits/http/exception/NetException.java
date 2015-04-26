package com.litesuits.http.exception;

public enum NetException {
    NetworkError("Network Is Not Avilable", "网络不可用"),
    NetworkDisabled("Current Network Is Disabled By Your Setting", "你已设置禁用该网络类型"),
    UnReachable("Service UnreachNetwork Is Unstable", "无法访问或网络不稳定");

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