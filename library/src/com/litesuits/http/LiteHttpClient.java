package com.litesuits.http;

import android.content.Context;
import com.litesuits.android.log.Log;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.data.StatisticsInfo;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpNetException.NetException;
import com.litesuits.http.impl.apache.ApacheHttpClient;
import com.litesuits.http.listener.HttpInnerListener;
import com.litesuits.http.network.Network;
import com.litesuits.http.network.Network.NetType;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.param.HttpMethod;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.response.InternalResponse;
import com.litesuits.http.response.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.List;

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
//
//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//
//                  佛祖保佑                 永无BUG       永不修改

/**
 * 可以由开发者自定义实现，目前默认实现为Apache的HttpClient实现
 * can be implemented by developer self, the default implement is Apache
 * HttpClient, now.
 *
 * @author MaTianyu
 *         2014-1-1下午9:53:30
 */
public abstract class LiteHttpClient {
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
    public static final int DEFAULT_MAX_REDIRECT_TIMES = 10;
    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_HTTPS_PORT = 443;
    public static final int DEFAULT_TIMEOUT = 20000;
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final String TAG = LiteHttpClient.class.getSimpleName();
    /**
     * 是否统计时间和流量
     */
    public static boolean doStatistics;
    /**
     * 错误描述是否使用中文
     */
    public static boolean errorInChinese = true;
    /**
     * User-Agent
     */
    public static String USER_AGENT = String.format("Lite %s ( http://litesuits.com )", "1.0");
    protected static Context appContext;
    //private static   LiteHttpClient instance;
    /**
     * u cat set {@link #disableNetworkFlags } = {@link #FLAG_NET_DISABLE_MOBILE }
     * | {@link #FLAG_NET_DISABLE_OTHER} to enable http connect only in wifi
     * network.
     */
    protected int disableNetworkFlags;
    /**
     * 连接前是否判断网络状态
     */
    protected boolean detectNetwork;
    /**
     * 非幂等请求是否强制重试
     */
    protected boolean forceRetry;
    /**
     * 统计信息
     */
    protected StatisticsInfo statisticsInfo;

    protected List<NameValuePair> commonHeader;

    public final static LiteHttpClient newApacheHttpClient(Context context) {
        return newApacheHttpClient(context, null);
    }


    public final static LiteHttpClient newApacheHttpClient(Context context, String UA) {
        return newApacheHttpClient(context, UA, false, true, false, true);
    }

    public final synchronized static LiteHttpClient newApacheHttpClient(Context context, String UA, boolean detectNetwork, boolean doStatistics, boolean forceRetry, boolean errorInChinese) {
        LiteHttpClient.USER_AGENT = UA;
        LiteHttpClient instance = ApacheHttpClient.createInstance(3000, false);
        instance.config(context, detectNetwork, doStatistics, forceRetry, errorInChinese);
        return instance;
    }

    public List<NameValuePair> getCommonHeader() {
        return commonHeader;
    }

    public void setCommonHeader(List<NameValuePair> commonHeader) {
        this.commonHeader = commonHeader;
    }

    public abstract Response execute(Request req);

    public abstract Response executeUnsafely(Request req) throws HttpException;

    public abstract HttpResponse execute(HttpUriRequest req) throws IOException;

    public abstract <T> T execute(String uri, DataParser<T> parser, HttpMethod method);

    public abstract String get(String uri);

    public abstract <T> T get(String uri, DataParser<T> parser);

    public abstract <T> T get(String uri, HttpParam model, Class<T> claxx);

    public abstract String put(String uri);

    public abstract <T> T put(String uri, DataParser<T> parser);

    public abstract <T> T put(String uri, HttpParam model, Class<T> claxx);

    public abstract NameValuePair[] head(String uri);

    public abstract String post(String uri);

    public abstract <T> T post(String uri, DataParser<T> parser);

    public abstract <T> T post(String uri, HttpParam model, Class<T> claxx);

    public abstract <T> T post(String uri, HttpBody body, Class<T> claxx);

    public abstract <T> T post(String uri, HttpParam model, HttpBody body, Class<T> claxx);

    public abstract String delete(String uri);

    public abstract <T> T delete(String uri, DataParser<T> parser);

    public abstract <T> T delete(String uri, HttpParam model, Class<T> claxx);

    public StatisticsInfo getStatisticsInfo() {
        return statisticsInfo;
    }

    public Context getAppContext() {
        return appContext;
    }

    /**
     * 配置{@link LiteHttpClient}
     *
     * @param context        application 或者 activity context
     * @param detectNetwork  连接前是否判断网络状态
     * @param doStatistics   是否统计时间和流量
     * @param forceRetry     非幂等请求是否强制重试
     * @param errorInChinese 出现异常是否使用中文描述，建议国内开发者设置ture
     */
    public void config(Context context, boolean detectNetwork, boolean doStatistics, boolean forceRetry, boolean errorInChinese) {
        if (context != null) appContext = context.getApplicationContext();
        this.detectNetwork = detectNetwork;
        LiteHttpClient.doStatistics = doStatistics;
        this.forceRetry = forceRetry;
        LiteHttpClient.errorInChinese = errorInChinese;
    }

    /**
     * 禁用网络
     *
     * @param flag
     */
    public void disableNetwork(int flag) {
        this.disableNetworkFlags = flag;
    }

    protected InternalResponse getInternalResponse(Request request) {
        final InternalResponse innerResponse = new InternalResponse();
        innerResponse.setRequest(request);
        if (detectNetwork | doStatistics) innerResponse.setHttpInnerListener(new HttpInnerListener() {
            private long start
                    ,
                    connect
                    ,
                    read;

            @Override
            public void onStart(Request request) throws HttpNetException {
                NetType type = null;

                if (detectNetwork) {
                    type = Network.getConnectedType(appContext);
                    if (type == NetType.None) throw new HttpNetException(NetException.NetworkError);
                }
                if ((disableNetworkFlags & FLAG_NET_DISABLE_ALL) == FLAG_NET_DISABLE_ALL) {
                    throw new HttpNetException(NetException.NetworkDisabled);
                } else if (disableNetworkFlags > FLAG_NET_DISABLE_ALL) {
                    if (type == null) type = Network.getConnectedType(appContext);
                    if ((type.value & disableNetworkFlags) == type.value)
                        throw new HttpNetException(NetException.NetworkDisabled);
                }
                if (doStatistics) start = System.currentTimeMillis();
            }

            @Override
            public void onEnd(Response res) {
                if (doStatistics) {
                    long time = start > 0 ? System.currentTimeMillis() - start : 0;
                    innerResponse.setUseTime(time);
                    if (statisticsInfo == null) statisticsInfo = new StatisticsInfo();
                    statisticsInfo.addConnectTime(time);
                    if (Log.isPrint)
                        Log.d(TAG, "http statistics : connect " + connect + "ms, read " + read + "ms, total " + time + "ms, global total time " + statisticsInfo.getConnectTime() + "ms");

                    long headLen = innerResponse.getContentLength();
                    long readLen = innerResponse.getReadedLength();
                    long len = 0;
                    if (readLen > 0) {
                        len = headLen > 0 ? headLen : readLen;
                    }
                    statisticsInfo.addDataLength(len);
                    if (Log.isPrint)
                        Log.d(TAG, "http statistics : len in header " + headLen + " B, len of readed " + readLen + " B, global total len " + statisticsInfo.getDataLength() + " B");
                }
            }

            @Override
            public void onRetry(Request req, int max, int now) {

            }

            @Override
            public void onRedirect(Request req) {

            }

            @Override
            public void onPreConnect(Request request) {
                if (doStatistics) connect = System.currentTimeMillis();
            }

            @Override
            public void onAfterConnect(Request request) {
                if (doStatistics) connect += System.currentTimeMillis() - connect;
            }

            @Override
            public void onPreRead(Request request) {
                if (doStatistics) read = System.currentTimeMillis();
            }

            @Override
            public void onAfterRead(Request request) {
                if (doStatistics) read += System.currentTimeMillis() - read;
            }
        });
        return innerResponse;
    }


    //public static interface ExecuteListener {
    //    public void onStart() throws HttpNetException;
    //
    //    public void onPreConnect();
    //
    //    public void onAfterConnect();
    //
    //    public void onPreRead();
    //
    //    public void onAfterRead();
    //
    //    public void onEnd();
    //}

    //public static class StatisticsInfo {
    //    private AtomicLong connectTime = new AtomicLong();
    //    private AtomicLong dataLength  = new AtomicLong();
    //
    //    public void addConnectTime(long time) {
    //        connectTime.addAndGet(time);
    //    }
    //
    //    public void addDataLength(long len) {
    //        dataLength.addAndGet(len);
    //    }
    //
    //    public long getConnectTime() {
    //        return connectTime.longValue();
    //    }
    //
    //    public long getDataLength() {
    //        return dataLength.longValue();
    //    }
    //
    //}

}
