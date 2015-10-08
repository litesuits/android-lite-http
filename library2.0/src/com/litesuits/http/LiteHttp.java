package com.litesuits.http;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.litesuits.http.concurrent.OverloadPolicy;
import com.litesuits.http.concurrent.SchedulePolicy;
import com.litesuits.http.concurrent.SmartExecutor;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.data.StatisticsInfo;
import com.litesuits.http.exception.*;
import com.litesuits.http.impl.apache.ApacheHttpClient;
import com.litesuits.http.listener.GlobalHttpListener;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.listener.StatisticsListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.network.Network;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpRichParamModel;
import com.litesuits.http.response.InternalResponse;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

//                              _oo0oo_
//                             o8888888o
//                             88" . "88
//                             (| -_- |)
//                             0\  =  /0
//                           ___/'---'\___
//                        .' \\\|     |// '.
//                       / \\\|||  :  |||// \\
//                      / _ ||||| -:- |||||- \\
//                      | |  \\\\  -  /// |   |
//                      | \_|  ''\---/''  |_/ |
//                      \  .-\__  '-'  __/-.  /
//                    ___'. .'  /--.--\  '. .'___
//                 ."" '<  '.___\_<|>_/___.' >'  "".
//                | | : '-  \'.;'\ _ /';.'/ - ' : | |
//                \  \ '_.   \_ __\ /__ _/   .-' /  /
//            ====='-.____'.___ \_____/___.-'____.-'=====
//                              '=---='
//        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//               佛祖保佑           永无BUG         镇类之宝
//
//
//        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//        LiteHttp   1.0 Features
//        * 1. 单线程：基于当前线程高效率运作。
//        * 2. 轻量级：微小的内存开销与Jar包体积，仅约 86K 。
//        * 3. 全支持：GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS, PATCH.
//        * 4. 全自动：一行代码将请求Java Model 转化为 Http Parameter，结果Json String 转化为 Java Model 。
//        * 5. 易拓展：自定义 DataParser，将网络数据流自由转化为你想要的任意数据类型。
//        * 6. 基于接口：架构灵活，轻松替换网络连接方式的核心实现方式，以及 Json 序列化库。
//        * 7. 文件上传：支持单个、多个大文件上传。
//        * 8. 文件下载：支持文件、Bimtap下载及其进度通知。
//        * 9. 网络禁用：快速禁用一种、多种网络环境，比如禁用 2G，3G 。
//        * 10. 数据统计：链接、读取时长统计，以及流量统计。
//        * 11. 异常体系：统一的异常处理体系，简明清晰地抛出可再细分的三大类异常：客户端、网络、服务器异常。
//        * 12. GZIP压缩：Request, Response 自动 GZIP 压缩节省流量。
//        * 13. 自动重试：结合探测异常类型和当前网络状况，智能执行重试策略。
//        * 14. 自动重定向：基于 30X 状态的重试，且可设置最大次数防止过度跳转。
//        * 15. 自带简单异步执行器，方便开发者实现异步请求方案。
//
//        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//        LiteHttp   2.0 Features
//        * 1. 可配置：更多更灵活的配置选择项，多达 23+ 项。
//        * 2. 多态化：更加直观的API，输入和输出更加明确。
//        * 3. 强并发：智能高效的并发调度，有效控制核心并发与队列控制策略。
//        * 4. 注解化：信息配置约定更多样，如果你喜欢，可以注解 API、Method、ID、TAG、CacheMode 等参数。
//        * 5. 多层缓存：内存命中更高效！支持多样的缓存模式，支持设置缓存有效期。
//        * 6. 完善回调：自由设置回调当前或UI线程，自由开启上传、下载进度通知。
//        * 7. 完善构建：提供 jar 包支持，后边支持 gradle 和 maven 。


/**
 * A simple, intelligent and flexible HTTP client for Android.
 * With LiteHttp you can make HTTP request with only one line of code!
 * It supports get, post, put, delete, head, trace, options and patch request types.
 * LiteHttp could convert a java model to the parameter of http request and
 * rander the response JSON as a java model intelligently.
 * And you can extend the abstract class {@link DataParser} to parse inputstream to any you want.
 * </p>
 * <p/>
 * {@link #connectWithRetries(AbstractRequest, InternalResponse)} is an abstract method,
 * it can be implemented by developer self, the default implement use Apache HttpClient.
 * </p>
 * <p/>
 * we will need permission:
 * </br>
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 * </br>
 * <p/>
 * if set cache directory on SD card, we will need this permisson:
 * </br>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 *
 * @author MaTianyu
 *         2014-1-1下午9:53:30
 */
@TargetApi(Build.VERSION_CODES.DONUT)
public abstract class LiteHttp {

    private static final String TAG = LiteHttp.class.getSimpleName();

    protected HttpConfig config;
    protected SmartExecutor smartExecutor;
    protected final Object lock = new Object();
    protected StatisticsInfo statisticsInfo = new StatisticsInfo();
    protected AtomicLong memCachedSize = new AtomicLong();
    protected ConcurrentHashMap<String, HttpCache> memCache = new ConcurrentHashMap<String, HttpCache>();

    protected LiteHttp() {}

    public static LiteHttp newApacheHttpClient(HttpConfig config) {
        return new ApacheHttpClient(config);
    }

    /* ____________________________ may be overridden by sub class ____________________________*/
    public void initConfig(HttpConfig config) {
        this.config = config;
        this.config.setLiteHttp(this);
        Log.d(TAG, config.toString());

        // Set config and smart-executor to lite-http.
        if (smartExecutor == null) {
            smartExecutor = new SmartExecutor(config.concurrentSize, config.waitingQueueSize);
        } else {
            setConfigForSmartExecutor(config.concurrentSize, config.waitingQueueSize);
        }
        setConfigForSmartExecutor(config.schedulePolicy, config.overloadPolicy);
    }

    protected void setUserAgent(String userAgent) {}

    protected void setConfigForHttpParams(int connectTimeout, int socketTimeout, int socketBufferSize) { }

    protected void setConfigForRetryHandler(int retrySleepMillis, boolean requestSentRetryEnabled) { }

    /* ____________________________ main methods ____________________________*/
    protected final void setConfigForSmartExecutor(int concurrentSize, int waitingQueueSize) {
        smartExecutor.setCoreSize(concurrentSize);
        smartExecutor.setQueueSize(waitingQueueSize);
    }

    protected final void setConfigForSmartExecutor(SchedulePolicy schedulePolicy, OverloadPolicy overloadPolicy) {
        if (schedulePolicy != null) {
            smartExecutor.setSchedulePolicy(schedulePolicy);
        }
        if (overloadPolicy != null) {
            smartExecutor.setOverloadPolicy(overloadPolicy);
        }
    }

    public final HttpConfig getConfig() {
        return config;
    }

    public final Context getAppContext() {
        return config.context;
    }

    public final StatisticsInfo getStatisticsInfo() {
        return statisticsInfo;
    }

    protected abstract <T> void connectWithRetries(AbstractRequest<T> request, InternalResponse response)
            throws HttpClientException, HttpNetException, HttpServerException;

    public <T> Response<T> execute(AbstractRequest<T> request) {
        final InternalResponse<T> response = handleRequest(request);
        HttpException httpException = null;
        final HttpListener<T> listener = request.getHttpListener();
        final GlobalHttpListener globalListener = request.getGlobalHttpListener();
        try {
            if (HttpLog.isPrint) {
                Thread t = Thread.currentThread();
                HttpLog.i(TAG,
                        "lite http request: " + request.getFullUri()
                        + " , tag: " + request.getTag()
                        + " , method: " + request.getMethod()
                        + " , cache mode: " + request.getCacheMode()
                        + " , thread ID: " + t.getId()
                        + " , thread name: " + t.getName());
            }
            if (globalListener != null) {
                globalListener.notifyCallStart(request);
            }
            if (listener != null) {
                listener.notifyCallStart(request);
            }
            if(request.getCacheMode() == CacheMode.CacheOnly){
                tryHitCache(response);
                return response;
            }else if (request.getCacheMode() == CacheMode.CacheFirst && tryHitCache(response)) {
                return response;
            } else {
                tryToConnectNetwork(request, response);
            }
        } catch (HttpException e) {
            e.printStackTrace();
            httpException = e;
            response.setException(httpException);
        } catch (Throwable e) {
            e.printStackTrace();
            httpException = new HttpClientException(e);
            response.setException(httpException);
        } finally {
            try {
                if (HttpLog.isPrint) {
                    Thread t = Thread.currentThread();
                    HttpLog.i(TAG, "lite http response: " + request.getUri()
                                   + " , tag: " + request.getTag()
                                   + " , method: " + request.getMethod()
                                   + " , cache mode: " + request.getCacheMode()
                                   + " , thread ID: " + t.getId()
                                   + " , thread name: " + t.getName());
                }
                if (listener != null) {
                    if (request.isCancelledOrInterrupted()) {
                        listener.notifyCallCancel(response.getResult(), response);
                    } else if (httpException != null) {
                        listener.notifyCallFailure(httpException, response);
                    } else {
                        listener.notifyCallSuccess(response.getResult(), response);
                    }
                    listener.notifyCallEnd(response);
                }
                if (globalListener != null) {
                    if (request.isCancelledOrInterrupted()) {
                        globalListener.notifyCallCancel(response.getResult(), response);
                    } else if (httpException != null) {
                        globalListener.notifyCallFailure(httpException, response);
                    } else {
                        globalListener.notifyCallSuccess(response.getResult(), response);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public <T> void executeAsync(final AbstractRequest<T> request) {
        smartExecutor.execute(new Runnable() {
            @Override
            public void run() {
                execute(request);
            }
        });
    }

    public <T> Response<T> execute(HttpRichParamModel<T> model) {
        Type type = ((ParameterizedType) model.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        JsonRequest<T> request = new JsonRequest<T>(model, type);
        return execute(request);
    }

    public <T> void executeAsync(HttpRichParamModel<T> model) {
        Type type = ((ParameterizedType) model.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        JsonRequest<T> request = new JsonRequest<T>(model, type);
        executeAsync(request);
    }

    public <T> T perform(AbstractRequest<T> request) {
        return execute(request).getResult();
    }

    public <T> FutureTask<T> performAsync(final AbstractRequest<T> request) {
        FutureTask<T> futureTask = new FutureTask<T>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return execute(request).getResult();
            }
        });
        smartExecutor.execute(futureTask);
        return futureTask;
    }

    public <T> Response<T> executeOrThrow(AbstractRequest<T> request) throws HttpException {
        final Response<T> response = execute(request);
        HttpException e = response.getException();
        if (e != null) {
            throw e;
        }
        return response;
    }

    public <T> T performOrThrow(AbstractRequest<T> request) throws HttpException {
        return executeOrThrow(request).getResult();
    }

    public String get(String uri) {
        return perform(new StringRequest(uri).setMethod(HttpMethods.Get));
    }

    public <T> T get(String uri, Class<T> claxx) {
        return perform(new JsonRequest<T>(uri, claxx).setMethod(HttpMethods.Get));
    }

    public <T> T get(AbstractRequest<T> request) {
        return perform(request.setMethod(HttpMethods.Get));
    }

    public <T> T put(AbstractRequest<T> request) {
        return perform(request.setMethod(HttpMethods.Put));
    }

    public <T> T post(AbstractRequest<T> request) {
        return perform(request.setMethod(HttpMethods.Post));
    }

    public <T> T delete(AbstractRequest<T> request) {
        return perform(request.setMethod(HttpMethods.Delete));
    }

    public <T> NameValuePair[] head(AbstractRequest<T> request) {
        return execute(request.setMethod(HttpMethods.Head)).getHeaders();
    }

    /**
     * if some of request params is null or 0, set global default value to it.
     */
    protected <T> InternalResponse<T> handleRequest(AbstractRequest<T> request) {
        if (config.commonHeaders != null) {
            request.addHeader(config.commonHeaders);
        }
        if (request.getCacheMode() == null) {
            request.setCacheMode(config.defaultCacheMode);
        }
        if (request.getCacheDir() == null) {
            request.setCacheDir(config.defaultCacheDir);
        }
        if (request.getCacheExpireMillis() < 0) {
            request.setCacheExpireMillis(config.defaultCacheExpireMillis);
        }
        if (request.getCharSet() == null) {
            request.setCharSet(config.defaultCharSet);
        }
        if (request.getMethod() == null) {
            request.setMethod(config.defaultHttpMethod);
        }
        if (request.getMaxRedirectTimes() < 0) {
            request.setMaxRedirectTimes(config.defaultMaxRedirectTimes);
        }
        if (request.getMaxRetryTimes() < 0) {
            request.setMaxRetryTimes(config.defaultMaxRetryTimes);
        }
        if (request.getQueryBuilder() == null) {
            request.setQueryBuilder(config.defaultModelQueryBuilder);
        }
        if (config.globalHttpListener != null) {
            request.setGlobalHttpListener(config.globalHttpListener);
        }
        if (request.getSchemeHost() == null) {
            request.setSchemeHost(config.globalSchemeHost);
        }
        return new InternalResponse<T>(request);
    }

    /**
     * try to detect the network
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     */
    protected void tryToDetectNetwork() throws HttpClientException, HttpNetException {
        if (config.detectNetwork || config.disableNetworkFlags > 0) {
            if (config.context == null) {
                throw new HttpClientException(ClientException.ContextNeeded);
            }
            try {
                Network.NetType type = null;
                if (config.detectNetwork) {
                    type = Network.getConnectedType(config.context);
                    if (type == Network.NetType.None) {
                        throw new HttpNetException(NetException.NetworkNotAvilable);
                    }
                }
                if (config.disableNetworkFlags > 0) {
                    if (type == null) {
                        type = Network.getConnectedType(config.context);
                    }
                    if (type != null) {
                        if (config.isDisableAllNetwork() || config.isDisableNetwork(type.value)) {
                            throw new HttpNetException(NetException.NetworkDisabled);
                        }
                    } else {
                        HttpLog.e(TAG, "DisableNetwork but cant get network type !!!");
                    }
                }
            } catch (SecurityException e) {
                throw new HttpClientException(e, ClientException.PermissionDenied);
            }
        }
    }

    /**
     * connect to network
     */
    protected <T> boolean tryToConnectNetwork(AbstractRequest<T> request, InternalResponse<T> response)
            throws HttpNetException, HttpClientException, HttpServerException, IOException {
        StatisticsListener statistic = null;
        if (config.doStatistics) {
            statistic = new StatisticsListener(response, statisticsInfo);
            response.setStatistics(statistic);
        }
        try {
            if (statistic != null) {
                statistic.onStart(request);
            }
            if (!request.isCancelledOrInterrupted()) {
                if (HttpLog.isPrint) {
                    HttpLog.v(TAG, "lite http try to connect network...  url: " + request.getUri()
                                   + "  tag: " + request.getTag());
                }
                tryToDetectNetwork();
                connectWithRetries(request, response);
                tryToKeepCacheInMemory(response);
                return true;
            }
        } finally {
            if (statistic != null) {
                statistic.onEnd(response);
            }
            if (request.getCacheMode() == CacheMode.NetFirst
                && !response.isResultOk()
                && !request.isCancelledOrInterrupted()) {
                tryHitCache(response);
            }
        }
        return false;
    }

    /**
     * Multi-Level cache design.
     * <ul>
     * <li>Memory</li>
     * <li>Disk</li>
     * <li>Network</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    protected <T> boolean tryHitCache(InternalResponse<T> response) throws IOException {
        AbstractRequest<T> request = response.getRequest();
        String key = request.getCacheKey();
        long expire = request.getCacheExpireMillis();
        boolean isMemCacheSupport = request.getDataParser().isMemCacheSupport();

        // 1. try to hit memory cache
        if (isMemCacheSupport) {
            HttpCache<T> cache = (HttpCache<T>) memCache.get(key);
            if (cache != null) {
                if (expire < 0 || expire > getCurrentTimeMillis() - cache.time) {
                    // memory hit!
                    request.getDataParser().readFromMemoryCache(cache.data);
                    response.setCacheHit(true);
                    if (HttpLog.isPrint) {
                        HttpLog.i(TAG, "lite-http mem cache hit!  "
                                       + "  url:" + request.getUri()
                                       + "  tag:" + request.getTag()
                                       + "  key:" + key
                                       + "  cache time:" + HttpUtil.formatDate(cache.time)
                                       + "  expire: " + expire);
                    }
                    return true;
                }
            }
        }

        // 2. try to hit disk cache
        File file = request.getCachedFile();
        if (file.exists()) {
            if (expire < 0 || expire > getCurrentTimeMillis() - file.lastModified()) {
                // disk hit!
                request.getDataParser().readFromDiskCache(file);
                response.setCacheHit(true);
                if (HttpLog.isPrint) {
                    HttpLog.i(TAG, "lite-http disk cache hit!  "
                                   + "  url:" + request.getUri()
                                   + "  tag:" + request.getTag()
                                   + "  key:" + key
                                   + "  cache time:" + HttpUtil.formatDate(file.lastModified())
                                   + "  expire: " + expire);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * try to save data into cache
     */
    protected <T> boolean tryToKeepCacheInMemory(InternalResponse<T> response) {
        AbstractRequest<T> request = response.getRequest();
        if (request.isCachedModel()) {
            DataParser<T> dataParser = request.getDataParser();
            if (HttpLog.isPrint) {
                HttpLog.v(TAG, "lite http try to keep cache.. maximum cache len: " + config.maxMemCacheBytesSize
                               + "   now cache len: " + memCachedSize.get()
                               + "   wanna put len: " + dataParser.getReadedLength()
                               + "   url: " + request.getUri()
                               + "   tag: " + request.getTag());
            }
            if (dataParser.isMemCacheSupport()) {
                if (memCachedSize.get() + dataParser.getReadedLength() > config.maxMemCacheBytesSize) {
                    clearMemCache();
                    if (HttpLog.isPrint) {
                        HttpLog.i(TAG, "lite http cache full ______________ and clear all mem cache success");
                    }
                }
                if (dataParser.getReadedLength() < config.maxMemCacheBytesSize) {
                    HttpCache<T> cache = new HttpCache<T>();
                    cache.time = getCurrentTimeMillis();
                    cache.key = request.getCacheKey();
                    cache.charSet = response.getCharSet();
                    cache.data = dataParser.getData();
                    cache.length = dataParser.getReadedLength();
                    synchronized (lock) {
                        memCache.put(cache.key, cache);
                        memCachedSize.addAndGet(cache.length);
                        if (HttpLog.isPrint) {
                            HttpLog.v(TAG, "lite http keep mem cache success, "
                                           + "   url: " + request.getUri()
                                           + "   tag: " + request.getTag()
                                           + "   key: " + cache.key
                                           + "   len: " + cache.length);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final long cleanCacheForRequest(AbstractRequest request) {
        long len = 0;
        if (request.getCacheDir() == null) {
            request.setCacheDir(config.defaultCacheDir);
        }
        synchronized (lock) {
            if (memCache.get(request.getCacheKey()) != null) {
                HttpCache cache = memCache.remove(request.getCacheKey());
                len = cache.length;
                memCachedSize.addAndGet(-len);
            }
        }
        File file = request.getCachedFile();
        if (file != null) {
            len = file.length();
            file.delete();
        }
        return len;
    }

    public final long clearMemCache() {
        long len;
        synchronized (lock) {
            len = memCachedSize.get();
            memCache.clear();
            memCachedSize.set(0);
        }
        return len;
    }

    public final boolean deleteCachedFile(String cacehKey) {
        File file = new File(config.defaultCacheDir, cacehKey);
        return file.delete();
    }

    public final long deleteCachedFiles() {
        File file = new File(config.defaultCacheDir);
        long len = 0;
        if (file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    len += f.length();
                    f.delete();
                }
            }
        }
        return len;
    }

    protected long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    static class HttpCache<T> {
        T data;
        String key;
        long time;
        long length;
        String charSet;
    }

}
