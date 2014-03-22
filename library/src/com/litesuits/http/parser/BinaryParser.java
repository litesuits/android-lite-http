package com.litesuits.http.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.ByteArrayBuffer;

/**
 * parse inputstream to bytes.
 * 
 * @author MaTianyu
 * 2014-2-21下午8:56:59
 */
public class BinaryParser extends DataParser<byte[]> {

	@Override
	public byte[] parseData(InputStream stream, int len, String charSet) throws IOException {
		return streamToByteArray(stream, len);
	}

	private byte[] streamToByteArray(InputStream is, int len) throws IOException {
		final ByteArrayBuffer buffer = new ByteArrayBuffer(len);
		final byte[] tmp = new byte[buffSize];
		int l;
		while (!Thread.currentThread().isInterrupted() && (l = is.read(tmp)) != -1) {
			buffer.append(tmp, 0, l);
			if (statistics) readLength += l;
		}
		return buffer.toByteArray();
	}
}
