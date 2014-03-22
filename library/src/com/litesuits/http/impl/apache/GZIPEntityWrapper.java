package com.litesuits.http.impl.apache;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

/**
 * Enclosing inputstream for gzip decoded data. 
 * Improve network transmission speed quite a lot.
 * @author MaTianyu
 * 2014-1-1下午7:39:45
 */
class GZIPEntityWrapper extends HttpEntityWrapper {
	public GZIPEntityWrapper(HttpEntity wrapped) {
		super(wrapped);
	}

	@Override
	public InputStream getContent() throws IOException {
		return new GZIPInputStream(wrappedEntity.getContent());
	}

	@Override
	public long getContentLength() {
		//unknown
		return -1;
	}
}
