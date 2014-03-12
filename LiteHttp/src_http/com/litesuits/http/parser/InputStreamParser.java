package com.litesuits.http.parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * get inputstream
 * 一般情况下，不建议这么做
 * @author MaTianyu
 * 2014-2-21下午8:56:59
 * @deprecated
 */
public abstract class InputStreamParser extends DataParser<InputStream> {

	@Override
	public InputStream parseData(InputStream stream, int len, String charSet) throws IOException {
		return stream;
	}

	/**
	 * 获取读取数据的长度，以便统计流量所用
	 * @return
	 */
	public abstract int getReadedLength();
}
