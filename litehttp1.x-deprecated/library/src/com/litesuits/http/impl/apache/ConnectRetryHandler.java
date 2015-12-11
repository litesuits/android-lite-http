package com.litesuits.http.impl.apache;

import android.content.Context;
import com.litesuits.android.log.Log;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpNetException.NetException;
import com.litesuits.http.network.Network;
import org.apache.http.NoHttpResponseException;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

/**
 * determine whether to send request try again.
 * 试验发现，当url非法时，{@link HttpURLConnection}报MalformedURLException异常，
 * 而apache httpclient会报IllegalStateException异常。
 * 当url非法，和ssl错误时，不用重试。 当有可用网络但连接不稳定时，一般会报IO异常，此种情况尝试重试，以提高成功率。
 * 继承StandardHttpRequestRetryHandler因用到其判断请求方式是否幂等和连接是否取消等方法。
 * @author MaTianyu
 *         2014-1-1下午5:03:46
 */
public class ConnectRetryHandler extends StandardHttpRequestRetryHandler {
	public static final String TAG = ConnectRetryHandler.class.getSimpleName();
	private HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();
	private HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();

	private final int retrySleepTimeMS;

	/**
	 * @param retrySleepTimeMS 遇到网络不稳地，暂停retrySleepTimeMS毫秒
	 * @param requestSentRetryEnabled 非幂等请求（如POST）是否重试
	 */
	public ConnectRetryHandler(int retrySleepTimeMS, boolean requestSentRetryEnabled) {
		super(0, requestSentRetryEnabled);
		this.retrySleepTimeMS = retrySleepTimeMS;
		exceptionWhitelist.add(UnknownHostException.class);
		exceptionWhitelist.add(SocketException.class);
		exceptionWhitelist.add(NoHttpResponseException.class);

		exceptionBlacklist.add(FileNotFoundException.class);
		exceptionBlacklist.add(InterruptedIOException.class);
		exceptionBlacklist.add(SSLException.class);
		exceptionBlacklist.add(ConnectException.class);
	}

	public boolean retryRequest(IOException exception, int retryCount, int maxRetries, HttpContext context,
			Context appContext) throws HttpNetException, InterruptedException {
		boolean retry = true;
		if (retryCount > maxRetries) {
			if (Log.isPrint) Log.w(TAG, "retry count > max retry times..");
			throw new HttpNetException(exception);
		} else if (isInList(exceptionBlacklist, exception)) {
			if (Log.isPrint) Log.w(TAG, "exception in blacklist..");
			retry = false;
		} else if (isInList(exceptionWhitelist, exception)) {
			if (Log.isPrint) Log.w(TAG, "exception in whitelist..");
			retry = true;
		}
		if (retry) {
			// 判断连接是否取消，非否幂等请求是否重试
			retry = retryRequest(context);
		}
		if (retry) {
			if (appContext != null) {
				if (Log.isPrint) Log.v(TAG, "has app context..");
				if (Network.isConnected(appContext)) {
					Log.d(TAG, "Network isConnected, retry now");
				} else if (Network.isConnectedOrConnecting(appContext)) {
					if (Log.isPrint) Log.v(TAG, "Network is Connected Or Connecting, wait for retey : "
							+ retrySleepTimeMS + " ms");
					Thread.sleep(retrySleepTimeMS);
				} else {
					Log.d(TAG, "Without any Network , immediately cancel retry");
					throw new HttpNetException(NetException.NetworkError);
				}
			} else {
				Log.v(TAG, "app context is null..");
				if (exception instanceof UnknownHostException) {
					Log.d(TAG, "UnknownHostException. Without app context, immediately cancel retry");
					throw new HttpNetException(NetException.NetworkError);
				} else {
					if (Log.isPrint) Log.v(TAG, "wait for retry : " + retrySleepTimeMS + " ms");
					Thread.sleep(retrySleepTimeMS);
				}
			}
		}
		if (Log.isPrint) Log.i(TAG, "retry: " + retry + " , retryCount: " + retryCount + " , exception: " + exception);
		return retry;
	}

	protected boolean isInList(HashSet<Class<?>> list, Throwable error) {
		for (Class<?> aList : list) {
			if (aList.isInstance(error)) { return true; }
		}
		return false;
	}
}
