package com.litesuits.http.exception;

public enum ServerException {
    //500 error
    ServerInner("Server Inner Exception", "服务器内部异常"),
    //400 error
    ServerReject("Server Reject Client Exception", "服务器拒绝或无法提供服务"),
    //redirect too many
    RedirectTooMany("Server RedirectTooMany", "重定向次数过多");

    private static final String TAG = ServerException.class.getName();
    public String reason;
    public String chiReason;

    ServerException(String name, String chiName) {
        this.reason = name;
        this.chiReason = chiName;
    }

    @Override
    public String toString() {
        return TAG +": " + this.reason + " (" + this.chiReason + ")";
    }
}