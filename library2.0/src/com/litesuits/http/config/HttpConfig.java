package com.litesuits.http.config;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
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

import java.util.List;

/**
 * @author MaTianyu
 * @date 2015-04-19
 */
public class HttpConfig {
    private static final String TAG = HttpConfig.class.getSimpleName();

    private static final String VERSION = "2.0";

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
    public static final int DEFAULT_TIMEOUT = 20000;
    public static final int DEFAULT_TRY_WAIT_TIME = 3000;
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
     * app context
     */
    public Context context;

    /**
     * User-Agent
     */
    public String USER_AGENT = String.format("litehttp%s (android-%s; api-%s; %s; %s)", VERSION
            , Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Build.BRAND, Build.MODEL);
    /**
     * connect time
     */
    public int connectTimeout = DEFAULT_TIMEOUT;
    /**
     * socket timeout
     */
    public int socketTimeout = DEFAULT_TIMEOUT;
    /**
     * stream buffer size
     */
    public int bufferSize = DEFAULT_BUFFER_SIZE;
    /**
     * disable some network :
     * use {@link #FLAG_NET_DISABLE_MOBILE } | {@link #FLAG_NET_DISABLE_OTHER}
     * to disable mobile and other network.
     */
    public int disableNetworkFlags;
    /**
     * time and traffic statistics
     */
    public boolean doStatistics;

    /**
     * whether to detect network
     */
    public boolean detectNetwork;
    /**
     * whether to retry when not idempotent
     */
    public boolean forceRetry;
    /**
     * duration of waiting
     */
    public int retrySleepMills = DEFAULT_TRY_WAIT_TIME;
    /**
     * concurrent threads number at the same time
     */
    public int concurrentSize = HttpUtil.getCoresNumbers();
    /**
     * waiting threads maximum number
     */
    public int waitingQueueSize = 20 * concurrentSize;
    /**
     * schedule policy when execute next task
     */
    public SchedulePolicy schedulePolicy;
    /**
     * handle policy when overload
     */
    public OverloadPolicy overloadPolicy;
    /**
     * maximum size of memory cache size, default size is
     */
    public long maxMemCacheBytesSize = 512 * 1024;
    /**
     * http cache root directory path
     * note: if context is not null, this default path will be reset by {@link #HttpConfig(android.content.Context)}.
     * if context is null, we need  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    public String cacheDirPath = Environment.getExternalStorageDirectory() + "/lite/http-cache";

    /**
     * set common headers to all request
     */
    public List<NameValuePair> commonHeaders;
    /**
     * set default charset to all request
     */
    public String defaultCharSet = Consts.DEFAULT_CHARSET;
    /**
     * set default http method to all request
     */
    public HttpMethods defaultHttpMethod = HttpMethods.Get;
    /**
     * set default cache mode to all request
     */
    public CacheMode defaultCacheMode;
    /**
     * set default cache expire time to all request
     */
    public long defaultCacheExpireMillis;
    /**
     * set default max number of retry to all request
     */
    public int defaultMaxRetryTimes = DEFAULT_MAX_RETRY_TIMES;
    /**
     * set default max number of redirect to all request
     */
    public int defaultMaxRedirectTimes = DEFAULT_MAX_REDIRECT_TIMES;
    /**
     * set default model query builder to all reqest
     */
    public ModelQueryBuilder defaultModelQueryBuilder = new JsonQueryBuilder();
    /**
     * set global http listener to all reqest
     */
    public GlobalHttpListener globalHttpListener;

    public HttpConfig(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
            cacheDirPath = context.getFilesDir() + "/lite/http-cache";
        }
        HttpLog.i(TAG, "lite http cache file dir: " + cacheDirPath);
    }





    public HttpConfig(Context context, boolean doStatistics, boolean detectNetwork) {
        this(context);
        this.doStatistics = doStatistics;
        this.detectNetwork = detectNetwork;
    }

    public boolean isDisableAllNetwork() {
        return (disableNetworkFlags & HttpConfig.FLAG_NET_DISABLE_ALL) == FLAG_NET_DISABLE_ALL;
    }

    public boolean detectNetworkNeeded() {
        return detectNetwork && context != null;
    }

    public boolean isDisableNetwork(int networkType) {
        return (disableNetworkFlags & networkType) == networkType;
    }

    public HttpConfig restoreDefault() {
        USER_AGENT = String.format("litehttp%s (android-%s; api-%s; %s; %s)", VERSION
                , Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Build.BRAND, Build.MODEL);
        connectTimeout = DEFAULT_TIMEOUT;
        socketTimeout = DEFAULT_TIMEOUT;
        bufferSize = DEFAULT_BUFFER_SIZE;
        disableNetworkFlags = FLAG_NET_DISABLE_NONE;
        doStatistics = false;
        detectNetwork = false;
        forceRetry = false;
        retrySleepMills = DEFAULT_TRY_WAIT_TIME;
        concurrentSize = HttpUtil.getCoresNumbers();
        waitingQueueSize = 20 * concurrentSize;
        schedulePolicy = SchedulePolicy.FirstInFistRun;
        overloadPolicy = OverloadPolicy.DiscardOld;
        maxMemCacheBytesSize = 512 * 1024;
        cacheDirPath = Environment.getExternalStorageDirectory() + "/lite/http-cache";

        commonHeaders = null;
        defaultCharSet = Consts.DEFAULT_CHARSET;
        defaultHttpMethod = HttpMethods.Get;
        defaultCacheMode = null;
        defaultCacheExpireMillis = 0;
        defaultMaxRetryTimes = DEFAULT_MAX_RETRY_TIMES;
        defaultMaxRedirectTimes = DEFAULT_MAX_REDIRECT_TIMES;
        defaultModelQueryBuilder = new JsonQueryBuilder();
        globalHttpListener = null;
        return this;
    }

    @Override
    public String toString() {
        return "HttpConfig{" +
               "context=" + context +
               ", USER_AGENT='" + USER_AGENT + '\'' +
               ", connectTimeout=" + connectTimeout +
               ", socketTimeout=" + socketTimeout +
               ", bufferSize=" + bufferSize +
               ", disableNetworkFlags=" + disableNetworkFlags +
               ", doStatistics=" + doStatistics +
               ", detectNetwork=" + detectNetwork +
               ", forceRetry=" + forceRetry +
               ", retrySleepMills=" + retrySleepMills +
               ", concurrentSize=" + concurrentSize +
               ", waitingQueueSize=" + waitingQueueSize +
               ", schedulePolicy=" + schedulePolicy +
               ", overloadPolicy=" + overloadPolicy +
               ", maxMemCacheBytesSize=" + maxMemCacheBytesSize +
               ", cacheDirPath='" + cacheDirPath + '\'' +
               ", commonHeaders=" + commonHeaders +
               ", defaultCharSet='" + defaultCharSet + '\'' +
               ", defaultHttpMethod=" + defaultHttpMethod +
               ", defaultCacheMode=" + defaultCacheMode +
               ", defaultCacheExpireMillis=" + defaultCacheExpireMillis +
               ", defaultMaxRetryTimes=" + defaultMaxRetryTimes +
               ", defaultMaxRedirectTimes=" + defaultMaxRedirectTimes +
               ", defaultModelQueryBuilder=" + defaultModelQueryBuilder +
               '}';
    }
}
