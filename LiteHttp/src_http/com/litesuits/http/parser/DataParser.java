package com.litesuits.http.parser;

import java.io.IOException;
import java.io.InputStream;

import com.litesuits.http.LiteHttpClient;

/**
 * 数据处理器
 * 
 * @author MaTianyu
 * 2014-2-21下午7:26:58
 * @param <D>
 */
public abstract class DataParser<D> {
	protected int buffSize = LiteHttpClient.DEFAULT_BUFFER_SIZE;
	private D data;
	protected int readLength;
	protected boolean statistics;

	public final D readInputStream(InputStream stream, int len, String charSet) throws IOException {
		if (stream != null) {
			try {
				statistics = LiteHttpClient.doStatistics;
				if (len <= 0) len = buffSize;
				else if (len > 1024000) len = 1024000;
				data = parseData(stream, len, charSet);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				stream.close();
			}
		}
		return data;
	}

	/**
	 * 自定义实现解析
	 * @param stream 数据流
	 * @param totalLength 总长度
	 * @return
	 * @throws IOException
	 */
	protected abstract D parseData(InputStream stream, int totalLength, String charSet) throws IOException;

	/**
	 * 获取读取数据的长度，以便统计流量所用
	 * @return
	 */
	public int getReadedLength() {
		return readLength;
	}

	public final D getData() {
		return data;
	}
}
