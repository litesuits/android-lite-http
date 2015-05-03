package com.litesuits.http.exception;

public enum ClientException {
    UrlIsNull("Url is Null!", "Url 为空!"),
    ContextNeeded("(Detect or Disable Network, etc) Need Context.", "（探测和禁用网络等）需要 Context"),
    PermissionDenied("Missing NETWORK-ACCESS Permission in Manifest?", "Manifest 中是否声明 INTERNET-ACCESS 权限？"),
    SomeOtherException("Client Exception", "Client 端异常");

    private static final String TAG = ClientException.class.getName();
    public String reason;
    public String chiReason;

    ClientException(String name, String chiName) {
        this.reason = name;
        this.chiReason = chiName;
    }

    @Override
    public String toString() {
        return TAG + ": " + this.reason + " (" + this.chiReason + ")";
    }
}