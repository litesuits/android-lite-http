package com.litesuits.http.data;

import java.io.Serializable;

/**
 * kv pair
 * 
 * @author MaTianyu
 *         2014-1-18上午3:19:12
 */
public class NameValuePair implements Serializable {
	private static final long serialVersionUID = -1339856642868580559L;
	private final String name;
	private final String value;

	public NameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		if (this.value == null) { return name; }
		final int len = this.name.length() + 1 + this.value.length();
		final StringBuilder buffer = new StringBuilder(len);
		buffer.append(this.name);
		buffer.append("=");
		buffer.append(this.value);
		buffer.append("\n");
		return buffer.toString();
	}

}
