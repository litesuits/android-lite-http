package com.litesuits.http;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

import com.litesuits.android.log.Log;
import com.litesuits.http.data.Charsets;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpNetException.NetException;
import com.litesuits.http.impl.apache.ApacheHttpClient;
import com.litesuits.http.network.Network;
import com.litesuits.http.network.Network.NetType;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.param.HttpMethod;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.response.InternalResponse;
import com.litesuits.http.response.Response;

/**
 * 可以由开发者自定义实现，目前默认实现为Apache的HttpClient实现
 * can be implemented by developer self, the default implement is Apache
 * HttpClient, now.
 * 
 * @author MaTianyu
 * 2014-1-1下午9:53:30
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

	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ENCODING_GZIP = "gzip";
	public static final String REDIRECT_LOCATION = "location";

	public static final String DEFAULT_CHARSET = Charsets.UTF_8;

	/**
	 * u cat set {@link #disableNetworkFlags } = {@link #FLAG_NET_DISABLE_MOBILE }
	 * | {@link #FLAG_NET_DISABLE_OTHER} to enable http connect only in wifi
	 * network.
	 */
	protected int disableNetworkFlags;
	/**
	 * 是否统计时间和流量
	 */
	public static boolean doStatistics;
	/**
	 * 连接前是否判断网络状态
	 */
	protected boolean detectNetwork;
	/**
	 * 非幂等请求是否强制重试
	 */
	protected boolean forceRetry;
	/**
	 * 错误描述是否使用中文
	 */
	public static boolean errorInChinese = true;
	/**
	 * 统计信息
	 */
	protected StatisticsInfo statisticsInfo;
	protected static Context appContext;
	private static LiteHttpClient instance;

	public final static LiteHttpClient getInstance(Context context) {
		return getInstance(context, false, true, false, true);
	}

	public final synchronized static LiteHttpClient getInstance(Context context, boolean detectNetwork,
			boolean doStatistics,
			boolean forceRetry, boolean errorInChinese) {
		if (instance == null) {
			instance = ApacheHttpClient.getInstance(context, 1500, false);
			instance.config(context, detectNetwork, doStatistics, forceRetry, errorInChinese);
		}
		return instance;
	}

	public abstract Response execute(Request req);

	public abstract HttpResponse execute(HttpUriRequest req) throws ClientProtocolException, IOException;

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
	 * @param context application 或者 activity context
	 * @param detectNetwork 连接前是否判断网络状态
	 * @param doStatistics 是否统计时间和流量
	 * @param forceRetry 非幂等请求是否强制重试
	 * @param errorInChinese 出现异常是否使用中文描述，建议国内开发者设置ture
	 */
	public void config(Context context, boolean detectNetwork, boolean doStatistics, boolean forceRetry,
			boolean errorInChinese) {
		if (context != null) appContext = context.getApplicationContext();
		this.detectNetwork = detectNetwork;
		LiteHttpClient.doStatistics = doStatistics;
		this.forceRetry = forceRetry;
		LiteHttpClient.errorInChinese = errorInChinese;
	}

	/**
	 * 禁用网络
	 * @param flag
	 */
	public void disableNetwork(int flag) {
		this.disableNetworkFlags = flag;
	}

	protected InternalResponse getInternalResponse() {
		final InternalResponse innerResponse = new InternalResponse();
		if (detectNetwork | doStatistics) innerResponse.setExecuteListener(new LiteHttpClient.ExecuteListener() {
			long start, connect, read;

			@Override
			public void onStart() throws HttpNetException {
				NetType type = null;
				if (detectNetwork) {
					type = Network.getConnectedType(appContext);
					if (type == NetType.None) throw new HttpNetException(NetException.NetworkError);
				}
				if ((disableNetworkFlags & FLAG_NET_DISABLE_ALL) == FLAG_NET_DISABLE_ALL) {
					throw new HttpNetException(NetException.NetworkDisabled);
				} else if (disableNetworkFlags > FLAG_NET_DISABLE_ALL) {
					if (type == null) type = Network.getConnectedType(appContext);
					if ((type.value & disableNetworkFlags) == type.value) throw new HttpNetException(
							NetException.NetworkDisabled);
				}
				if (doStatistics) {
					if (statisticsInfo == null) statisticsInfo = new StatisticsInfo();
					start = System.currentTimeMillis();
				}
			}

			@Override
			public void onEnd() {
				if (doStatistics) {
					long time = start > 0 ? System.currentTimeMillis() - start : 0;
					innerResponse.setConnectTime(time);
					statisticsInfo.addConnectTime(time);
					if (Log.isPrint) Log.d("HttpClient", "http total time: " + time + ", global total time: "
							+ statisticsInfo.getConnectTime());

					long headLen = innerResponse.getContentLength();
					long readLen = innerResponse.getReadedLength();
					long len = 0;
					if (readLen > 0) {
						len = headLen > 0 ? headLen : readLen;
					}
					statisticsInfo.addDataLength(len);
					if (Log.isPrint) Log.d("HttpClient", "http len in header: " + headLen + ", readed len: " + readLen
							+ ", global total len: " + statisticsInfo.getDataLength());
				}
			}

			@Override
			public void onPreConnect() {
				if (doStatistics) connect = System.currentTimeMillis();
			}

			@Override
			public void onAfterConnect() {
				if (doStatistics) Log.d("HttpClient", "http connect use time: "
						+ (System.currentTimeMillis() - connect));
			}

			@Override
			public void onPreRead() {
				if (doStatistics) read = System.currentTimeMillis();
			}

			@Override
			public void onAfterRead() {
				if (doStatistics) Log.d("HttpClient", "http read data time: " + (System.currentTimeMillis() - read));
			}
		});
		return innerResponse;
	}

	public static interface ExecuteListener {
		public void onStart() throws HttpNetException;

		public void onPreConnect();

		public void onAfterConnect();

		public void onPreRead();

		public void onAfterRead();

		public void onEnd();
	}

	public static class StatisticsInfo {
		private AtomicLong connectTime = new AtomicLong();
		private AtomicLong dataLength = new AtomicLong();

		public void addConnectTime(long time) {
			connectTime.addAndGet(time);
		}

		public void addDataLength(long len) {
			dataLength.addAndGet(len);
		}

		public long getConnectTime() {
			return connectTime.longValue();
		}

		public long getDataLength() {
			return dataLength.longValue();
		}

	}

}
