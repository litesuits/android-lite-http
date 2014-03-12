package com.litesuits.http.data;

import com.google.gson.Gson;

/**
 * google gson
 * @author MaTianyu
 *         2014-2-26下午11:13:39
 */
public class GsonImpl extends Json {
	private Gson gson = new Gson();

	@Override
	public String toString(Object src) {
		return gson.toJson(src);
	}

	@Override
	public <T> T toObject(String json, Class<T> claxx) {
		return gson.fromJson(json, claxx);
	}

	@Override
	public <T> T toObject(byte[] bytes, Class<T> claxx) {
		return gson.fromJson(new String(bytes), claxx);
	}

}
