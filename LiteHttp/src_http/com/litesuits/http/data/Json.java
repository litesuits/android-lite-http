package com.litesuits.http.data;

/**
 * with this, we can change json handler easily.
 * alibaba fastjson cant handle private attribute that without getter method.
 * so we choice the google gson.
 * @author MaTianyu
 *         2014-1-14下午11:32:32
 */
public abstract class Json {
	private static Json json;

	Json() {}

	/**
	 * default json handler is alibaba fastjson.
	 * @return Json
	 */
	public static Json get() {
		if (json == null) {
			//json = new FastJson();
			json = new GsonImpl();
		}
		return json;
	}

	public abstract String toString(Object src);

	public abstract <T> T toObject(String json, Class<T> claxx);

	public abstract <T> T toObject(byte[] bytes, Class<T> claxx);
}
