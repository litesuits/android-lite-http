package com.litesuits.http.response;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

import android.graphics.Bitmap;

import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.Json;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.parser.BinaryParser;
import com.litesuits.http.parser.BitmapParser;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.FileParser;
import com.litesuits.http.parser.InputStreamParser;
import com.litesuits.http.parser.StringParser;
import com.litesuits.http.request.Request;

/**
 * Inner Facade {@link InternalResponse } gives {@link LiteHttpClient}
 * feature-rich
 * capabiblities that set request and response info easy.
 * 
 * @author MaTianyu
 * 2014-1-1下午10:00:42
 * @param <D>
 */
public class InternalResponse implements Response {
	protected HttpStatus httpStatus;
	protected String charSet = LiteHttpClient.DEFAULT_CHARSET;
	protected int tryTimes;
	protected int redirectTimes;
	/**
	 * data real size
	 */
	protected int readedLength;
	/**
	 * http header Content-Length
	 */
	protected long contentLength;
	protected long connectTime;
	protected NameValuePair[] headers;
	protected Request request;
	protected DataParser<?> dataParser;
	protected LiteHttpClient.ExecuteListener executeListener;
	protected HttpException exception;

	@Override
	public String getString() {
		if (dataParser instanceof StringParser) {
			return ((StringParser) dataParser).getData();
		} else {
			throw new RuntimeException("get string , your Request must use StringParser");
		}
	}

	@Override
	public byte[] getBytes() {
		if (dataParser instanceof BinaryParser) {
			return ((BinaryParser) dataParser).getData();
		} else {
			throw new RuntimeException("get bytes , your Request must use BinaryParser");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<T> claxx) {
		if (dataParser instanceof BinaryParser) {
			try {
				if (claxx == String.class) return (T) new String(((BinaryParser) dataParser).getData(), getCharSet());
				return Json.get().toObject(((BinaryParser) dataParser).getData(), claxx);
			} catch (Exception e) {
				e.printStackTrace();
				if (exception == null) setException(new HttpClientException(e));
			}
		} else if (dataParser instanceof StringParser) {
			if (claxx == String.class) return (T) dataParser.getData();
			try {
				return Json.get().toObject(((StringParser) dataParser).getData(), claxx);
			} catch (Exception e) {
				e.printStackTrace();
				if (exception == null) setException(new HttpClientException(e));
			}
		} else {
			throw new RuntimeException("Json To Java Object , your Request must use BinaryParser or StringParser");
		}
		return null;
	}

	@Override
	public <T> T getObjectWithMockData(Class<T> claxx, String json) {
		try {
			return Json.get().toObject(json, claxx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Json To Java Object , your Request must use BinaryParser or StringParser");
	}

	@Override
	public File getFile() {
		if (dataParser instanceof FileParser) {
			return ((FileParser) dataParser).getData();
		} else {
			throw new RuntimeException("get file , your Request must use FileParser");
		}
	}

	@Override
	public Bitmap getBitmap() {
		if (dataParser instanceof BitmapParser) {
			return ((BitmapParser) dataParser).getData();
		} else {
			throw new RuntimeException("get bytes , your Request must use BitmapParser");
		}
	}

	/**
	 * @deprecated
	 */
	@Override
	public InputStream getInputStream() {
		if (dataParser instanceof BinaryParser) {
			return ((InputStreamParser) dataParser).getData();
		} else {
			throw new RuntimeException("get InputStream ,your Request must use InputStreamParser");
		}
	}

	@Override
	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	@Override
	public HttpException getException() {
		return exception;
	}

	public void setException(HttpException e) {
		this.exception = e;
	}

	@Override
	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		if (charSet != null) this.charSet = charSet;
	}

	@Override
	public NameValuePair[] getHeaders() {
		return headers;
	}

	public void setHeaders(NameValuePair[] headers) {
		this.headers = headers;
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	public long setContentLength(long contentLength) {
		this.contentLength = contentLength;
		return this.contentLength;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public LiteHttpClient.ExecuteListener getExecuteListener() {
		return executeListener;
	}

	public void setExecuteListener(LiteHttpClient.ExecuteListener listener) {
		this.executeListener = listener;
	}

	@Override
	public int getTryTimes() {
		return tryTimes;
	}

	public void setTryTimes(int retryTimes) {
		this.tryTimes = retryTimes;
	}

	@Override
	public int getRedirectTimes() {
		return redirectTimes;
	}

	public void setRedirectTimes(int redirectTimes) {
		this.redirectTimes = redirectTimes;
	}

	@Override
	public int getReadedLength() {
		return readedLength;
	}

	public void setReadedLength(int readedLength) {
		this.readedLength = readedLength;
	}

	@Override
	public long getConnectTime() {
		return connectTime;
	}

	public void setConnectTime(long connectTime) {
		this.connectTime = connectTime;
	}

	@Override
	public DataParser<?> getDataParser() {
		return dataParser;
	}

	public void setDataParser(DataParser<?> dataParser) {
		this.dataParser = dataParser;
	}

	@Override
	public boolean isSuccess() {
		return httpStatus != null && httpStatus.isSuccess();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("----------Http Response Info Start------------\n").append("http [Response] : httpStatus=").append(httpStatus).append(", charSet=")
				.append(charSet).append(", tryTimes=").append(tryTimes).append(", redirectTimes=").append(redirectTimes).append(", readedLength=")
				.append(readedLength).append(", contentLength=").append(contentLength).append(", connectTime=").append(connectTime)
				.append("\nhttp [headers] : ").append(Arrays.toString(headers)).append("\nhttp [request] : ").append(request).append("\nhttp [dataParser] : ")
				.append(dataParser).append("\nhttp [executeListener] : ").append(executeListener).append("\nhttp [exception] : ").append(exception)
				.append("\n----------Http Response Info End------------");
		return sb.toString();

	}

	//	@Override
	//	public String toString() {
	//		return "----------Http Response Info Start------------\n" + "http [Response] : httpStatus=" + httpStatus
	//				+ ", charSet=" + charSet + ", tryTimes=" + tryTimes + ", redirectTimes=" + redirectTimes
	//				+ ", readedLength=" + readedLength + ", contentLength=" + contentLength + ", connectTime="
	//				+ connectTime + "\nhttp [headers] : " + Arrays.toString(headers) + "\nhttp [request] : " + request
	//				+ "\nhttp [dataParser] : " + dataParser + "\nhttp [executeListener] : " + executeListener
	//				+ "\nhttp [exception] : " + exception + "\n----------Http Response Info End------------";
	//	}

}
