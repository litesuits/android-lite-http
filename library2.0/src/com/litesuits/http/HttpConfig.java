package com.litesuits.http;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.litesuits.http.concurrent.OverloadPolicy;
import com.litesuits.http.concurrent.SchedulePolicy;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.listener.GlobalHttpListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.network.Network;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.query.JsonQueryBuilder;
import com.litesuits.http.request.query.ModelQueryBuilder;
import com.litesuits.http.utils.HttpUtil;

import java.io.File;
import java.util.List;

/**
 * @author MaTianyu
 * @date 2015-04-19
 */
public class HttpConfig {
    protected static final String TAG = HttpConfig.class.getSimpleName();

    protected static final String VERSION = "2.0";

    public static final int FLAG_NET_DISABLE_NONE = 0;
    public static final int FLAG_NET_DISABLE_ALL = Network.NetType.None.value;
    public static final int FLAG_NET_DISABLE_MOBILE = Network.NetType.Mobile.value;
    public static final int FLAG_NET_DISABLE_WIFI = Network.NetType.Wifi.value;
    public static final int FLAG_NET_DISABLE_OTHER = Network.NetType.Other.value;

    /**
     * default retry times at most
     */
    public static final int DEFAULT_MAX_RETRY_TIMES = 3;
    /**
     * max redirect times
     */
    public static final int DEFAULT_MAX_REDIRECT_TIMES = 5;
    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_HTTPS_PORT = 443;
    /**
     * 20 second
     */
    public static final int DEFAULT_TIMEOUT = 20 * 1000;
    /**
     * 3 second
     */
    public static final int DEFAULT_TRY_WAIT_TIME = 3000;
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    protected LiteHttp liteHttp;
    /**
     * app context
     */
    protected Context context;

    /**
     * User-Agent
     */
    protected String userAgent = String.format("litehttp%s (android-%s; api-%s; %s; %s)", VERSION
            , Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Build.BRAND, Build.MODEL);
    /**
     * connect time
     */
    protected int connectTimeout = DEFAULT_TIMEOUT;
    /**
     * socket timeout
     */
    protected int socketTimeout = DEFAULT_TIMEOUT;
    /**
     * stream buffer size
     */
    protected int socketBufferSize = DEFAULT_BUFFER_SIZE;
    /**
     * disable some network :
     * use {@link #FLAG_NET_DISABLE_MOBILE } | {@link #FLAG_NET_DISABLE_OTHER}
     * to disable mobile and other network.
     */
    protected int disableNetworkFlags;
    /**
     * time and traffic statistics
     */
    protected boolean doStatistics;

    /**
     * whether to detect network
     */
    protected boolean detectNetwork;
    /**
     * true if it's OK to retry requests that have been sent
     */
    protected boolean requestSentRetryEnabled;
    /**
     * duration of waiting
     */
    protected int retrySleepMillis = DEFAULT_TRY_WAIT_TIME;
    /**
     * concurrent threads number at the same time
     */
    protected int concurrentSize = HttpUtil.getCoresNumbers();
    /**
     * waiting threads maximum number
     */
    protected int waitingQueueSize = 20 * concurrentSize;
    /**
     * schedule policy when execute next task
     */
    protected SchedulePolicy schedulePolicy;
    /**
     * handle policy when overload
     */
    protected OverloadPolicy overloadPolicy;
    /**
     * maximum size of memory cache size, default size is
     */
    protected long maxMemCacheBytesSize = 512 * 1024;
    /**
     * http cache root directory path
     * note: if context is not null, this default path will be reset by {@link #HttpConfig(android.content.Context)}.
     * if context is null, we need  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    protected String defaultCacheDir = Environment.getExternalStorageDirectory() + "/lite/http-cache";

    /**
     * set common headers to all request
     */
    protected List<NameValuePair> commonHeaders;
    /**
     * set default charset to all request
     */
    protected String defaultCharSet = Consts.DEFAULT_CHARSET;
    /**
     * set default http method to all request
     */
    protected HttpMethods defaultHttpMethod = HttpMethods.Get;
    /**
     * set default cache mode to all request
     */
    protected CacheMode defaultCacheMode;
    /**
     * set default cache expire time to all request
     */
    protected long defaultCacheExpireMillis = -1;
    /**
     * set default max number of retry to all request
     */
    protected int defaultMaxRetryTimes = DEFAULT_MAX_RETRY_TIMES;
    /**
     * set default max number of redirect to all request
     */
    protected int defaultMaxRedirectTimes = DEFAULT_MAX_REDIRECT_TIMES;
    /**
     * set default model query builder to all reqest
     */
    protected ModelQueryBuilder defaultModelQueryBuilder = new JsonQueryBuilder();
    /**
     * set global http listener to all reqest
     */
    protected GlobalHttpListener globalHttpListener;
    /**
     * set global http scheme and host for uri.
     */
    protected String globalSchemeHost;
    /**
     * set debugged status
     */
    protected boolean debugged;

    public HttpConfig(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
        setDefaultCacheDir(getDefaultCacheDir(context));
    }

    private String getDefaultCacheDir(Context context) {
        if (context != null) {
            return context.getFilesDir() + "/lite/http-cache";
        } else {
            return Environment.getExternalStorageDirectory() + "/lite/http-cache";
        }
    }

    public HttpConfig(Context context, boolean doStatistics, boolean detectNetwork) {
        this(context);
        this.doStatistics = doStatistics;
        this.detectNetwork = detectNetwork;
    }

    /* ____________________________ getter & setter ____________________________*/
    protected HttpConfig setLiteHttp(LiteHttp liteHttp) {
        this.liteHttp = liteHttp;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public HttpConfig setContext(Context context) {
        this.context = context.getApplicationContext();
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }


    public HttpConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        if (liteHttp != null) {
            liteHttp.setUserAgent(userAgent);
        }
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    public int getDisableNetworkFlags() {
        return disableNetworkFlags;
    }

    public HttpConfig setDisableNetworkFlags(int disableNetworkFlags) {
        this.disableNetworkFlags = disableNetworkFlags;
        return this;
    }

    public boolean isDoStatistics() {
        return doStatistics;
    }

    public HttpConfig setDoStatistics(boolean doStatistics) {
        this.doStatistics = doStatistics;
        return this;
    }

    public boolean isDetectNetwork() {
        return detectNetwork;
    }

    public HttpConfig setDetectNetwork(boolean detectNetwork) {
        this.detectNetwork = detectNetwork;
        return this;
    }

    public boolean isRequestSentRetryEnabled() {
        return requestSentRetryEnabled;
    }

    public int getRetrySleepMillis() {
        return retrySleepMillis;
    }

    public int getConcurrentSize() {
        return concurrentSize;
    }

    public HttpConfig setConcurrentSize(int concurrentSize) {
        this.concurrentSize = concurrentSize;
        if (liteHttp != null) {
            liteHttp.setConfigForSmartExecutor(concurrentSize, waitingQueueSize);
        }
        return this;
    }

    public int getWaitingQueueSize() {
        return waitingQueueSize;
    }

    public HttpConfig setWaitingQueueSize(int waitingQueueSize) {
        this.waitingQueueSize = waitingQueueSize;
        if (liteHttp != null) {
            liteHttp.setConfigForSmartExecutor(concurrentSize, waitingQueueSize);
        }
        return this;
    }

    public SchedulePolicy getSchedulePolicy() {
        return schedulePolicy;
    }

    public HttpConfig setSchedulePolicy(SchedulePolicy schedulePolicy) {
        this.schedulePolicy = schedulePolicy;
        if (liteHttp != null) {
            liteHttp.setConfigForSmartExecutor(schedulePolicy, overloadPolicy);
        }
        return this;
    }

    public OverloadPolicy getOverloadPolicy() {
        return overloadPolicy;
    }

    public HttpConfig setOverloadPolicy(OverloadPolicy overloadPolicy) {
        this.overloadPolicy = overloadPolicy;
        if (liteHttp != null) {
            liteHttp.setConfigForSmartExecutor(schedulePolicy, overloadPolicy);
        }
        return this;
    }

    public long getMaxMemCacheBytesSize() {
        return maxMemCacheBytesSize;
    }

    public HttpConfig setMaxMemCacheBytesSize(long maxMemCacheBytesSize) {
        this.maxMemCacheBytesSize = maxMemCacheBytesSize;
        return this;
    }

    public String getDefaultCacheDir() {
        return defaultCacheDir;
    }

    public HttpConfig setDefaultCacheDir(String defaultCacheDir) {
        this.defaultCacheDir = defaultCacheDir;
        File file = new File(defaultCacheDir);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            HttpLog.i(TAG, file.getAbsolutePath() + "  mkdirs: " + mkdirs);
        }
        HttpLog.i(TAG, "lite http cache file dir: " + defaultCacheDir);
        return this;
    }

    public List<NameValuePair> getCommonHeaders() {
        return commonHeaders;
    }

    public HttpConfig setCommonHeaders(List<NameValuePair> commonHeaders) {
        this.commonHeaders = commonHeaders;
        return this;
    }

    public String getDefaultCharSet() {
        return defaultCharSet;
    }

    public HttpConfig setDefaultCharSet(String defaultCharSet) {
        this.defaultCharSet = defaultCharSet;
        return this;
    }

    public HttpMethods getDefaultHttpMethod() {
        return defaultHttpMethod;
    }

    public HttpConfig setDefaultHttpMethod(HttpMethods defaultHttpMethod) {
        this.defaultHttpMethod = defaultHttpMethod;
        return this;
    }

    public CacheMode getDefaultCacheMode() {
        return defaultCacheMode;
    }

    public HttpConfig setDefaultCacheMode(CacheMode defaultCacheMode) {
        this.defaultCacheMode = defaultCacheMode;
        return this;
    }

    public long getDefaultCacheExpireMillis() {
        return defaultCacheExpireMillis;
    }

    public HttpConfig setDefaultCacheExpireMillis(long defaultCacheExpireMillis) {
        this.defaultCacheExpireMillis = defaultCacheExpireMillis;
        return this;
    }

    public int getDefaultMaxRetryTimes() {
        return defaultMaxRetryTimes;
    }

    public HttpConfig setDefaultMaxRetryTimes(int defaultMaxRetryTimes) {
        this.defaultMaxRetryTimes = defaultMaxRetryTimes;
        return this;
    }

    public int getDefaultMaxRedirectTimes() {
        return defaultMaxRedirectTimes;
    }

    public HttpConfig setDefaultMaxRedirectTimes(int defaultMaxRedirectTimes) {
        this.defaultMaxRedirectTimes = defaultMaxRedirectTimes;
        return this;
    }

    public ModelQueryBuilder getDefaultModelQueryBuilder() {
        return defaultModelQueryBuilder;
    }

    public HttpConfig setDefaultModelQueryBuilder(
            ModelQueryBuilder defaultModelQueryBuilder) {
        this.defaultModelQueryBuilder = defaultModelQueryBuilder;
        return this;
    }

    public GlobalHttpListener getGlobalHttpListener() {
        return globalHttpListener;
    }

    public HttpConfig setGlobalHttpListener(GlobalHttpListener globalHttpListener) {
        this.globalHttpListener = globalHttpListener;
        return this;
    }

    public String getGlobalSchemeHost() {
        return globalSchemeHost;
    }

    public HttpConfig setGlobalSchemeHost(String globalSchemeHost) {
        this.globalSchemeHost = globalSchemeHost;
        return this;
    }

    public boolean isDebugged() {
        return debugged;
    }

    /**
     * when debugged is true, the {@link Log} is opened.
     *
     * @param debugged true if debugged
     */
    public HttpConfig setDebugged(boolean debugged) {
        this.debugged = debugged;
        HttpLog.isPrint = debugged;
        return this;
    }

    /* ____________________________ enhanced methods ____________________________*/
    public boolean isDisableAllNetwork() {
        return (disableNetworkFlags & FLAG_NET_DISABLE_ALL) == FLAG_NET_DISABLE_ALL;
    }

    public boolean detectNetworkNeeded() {
        return detectNetwork && context != null;
    }

    public boolean isDisableNetwork(int networkType) {
        return (disableNetworkFlags & networkType) == networkType;
    }

    public HttpConfig restoreToDefault() {
        userAgent = String.format("litehttp%s (android-%s; api-%s; %s; %s)", VERSION
                , Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Build.BRAND, Build.MODEL);
        connectTimeout = DEFAULT_TIMEOUT;
        socketTimeout = DEFAULT_TIMEOUT;
        socketBufferSize = DEFAULT_BUFFER_SIZE;
        disableNetworkFlags = FLAG_NET_DISABLE_NONE;
        doStatistics = false;
        detectNetwork = false;
        requestSentRetryEnabled = false;
        retrySleepMillis = DEFAULT_TRY_WAIT_TIME;
        concurrentSize = HttpUtil.getCoresNumbers();
        waitingQueueSize = 20 * concurrentSize;
        schedulePolicy = SchedulePolicy.FirstInFistRun;
        overloadPolicy = OverloadPolicy.DiscardOldTaskInQueue;
        maxMemCacheBytesSize = 512 * 1024;
        defaultCacheDir = getDefaultCacheDir(context);

        commonHeaders = null;
        defaultCharSet = Consts.DEFAULT_CHARSET;
        defaultHttpMethod = HttpMethods.Get;
        defaultCacheMode = null;
        defaultCacheExpireMillis = 0;
        defaultMaxRetryTimes = DEFAULT_MAX_RETRY_TIMES;
        defaultMaxRedirectTimes = DEFAULT_MAX_REDIRECT_TIMES;
        defaultModelQueryBuilder = new JsonQueryBuilder();
        globalHttpListener = null;
        globalSchemeHost = null;
        if (liteHttp != null) {
            liteHttp.initConfig(this);
        }
        return this;
    }

    public HttpConfig setTimeOut(int connectTimeout, int socketTimeout) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        if (liteHttp != null) {
            liteHttp.setConfigForHttpParams(connectTimeout, socketTimeout, socketBufferSize);
        }
        return this;
    }

    public HttpConfig setSocketBufferSize(int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
        if (liteHttp != null) {
            liteHttp.setConfigForHttpParams(connectTimeout, socketTimeout, socketBufferSize);
        }
        return this;
    }

    public HttpConfig setForRetry(int retrySleepMillis, boolean requestSentRetryEnabled) {
        this.retrySleepMillis = retrySleepMillis;
        this.requestSentRetryEnabled = requestSentRetryEnabled;
        if (liteHttp != null) {
            liteHttp.setConfigForRetryHandler(retrySleepMillis, requestSentRetryEnabled);
        }
        return this;
    }

    @Override
    public String toString() {
        return "HttpConfig{" +
               "liteHttp=" + liteHttp +
               ", context=" + context +
               ", userAgent='" + userAgent + '\'' +
               ", connectTimeout=" + connectTimeout +
               ", socketTimeout=" + socketTimeout +
               ", socketBufferSize=" + socketBufferSize +
               ", disableNetworkFlags=" + disableNetworkFlags +
               ", doStatistics=" + doStatistics +
               ", detectNetwork=" + detectNetwork +
               ", requestSentRetryEnabled=" + requestSentRetryEnabled +
               ", retrySleepMillis=" + retrySleepMillis +
               ", concurrentSize=" + concurrentSize +
               ", waitingQueueSize=" + waitingQueueSize +
               ", schedulePolicy=" + schedulePolicy +
               ", overloadPolicy=" + overloadPolicy +
               ", maxMemCacheBytesSize=" + maxMemCacheBytesSize +
               ", defaultCacheDir='" + defaultCacheDir + '\'' +
               ", commonHeaders=" + commonHeaders +
               ", defaultCharSet='" + defaultCharSet + '\'' +
               ", defaultHttpMethod=" + defaultHttpMethod +
               ", defaultCacheMode=" + defaultCacheMode +
               ", defaultCacheExpireMillis=" + defaultCacheExpireMillis +
               ", defaultMaxRetryTimes=" + defaultMaxRetryTimes +
               ", defaultMaxRedirectTimes=" + defaultMaxRedirectTimes +
               ", defaultModelQueryBuilder=" + defaultModelQueryBuilder +
               ", globalHttpListener=" + globalHttpListener +
               ", globalSchemeHost='" + globalSchemeHost + '\'' +
               ", debugged=" + debugged +
               '}';
    }
}
