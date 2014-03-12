package com.litesuits.http.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.util.CharArrayBuffer;

/**
 * parse inputstream to string.
 * 
 * @author MaTianyu
 * 2014-2-21下午8:56:59
 */
public class StringParser extends DataParser<String> {

	@Override
	public String parseData(InputStream stream, int len, String charSet) throws IOException {
		return streamToString(stream, len, charSet);
	}

	private String streamToString(InputStream is, int len, String charSet) throws IOException {
		Reader reader = new InputStreamReader(is, charSet);
		CharArrayBuffer buffer = new CharArrayBuffer(len);
		try {
			char[] tmp = new char[buffSize];
			int l;
			while (!Thread.currentThread().isInterrupted() && (l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
				if (statistics) readLength += l;
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}
}
