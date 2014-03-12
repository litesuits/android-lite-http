package com.litesuits.android.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.litesuits.android.samples.model.response.ExtendBasedModel.UserResult;

/**
 * 传统http连接读取过程和解析json方式
 * @author MaTianyu
 * 2014-3-11下午3:28:36
 */
public class OriginHttp {
	/**
	 * 关闭流顺序：先打开的后关闭；被依赖的后关闭。
	 * @return
	 */
	public static String sendHttpRequst(String u) {
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			URL url = new URL(u);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			int len = conn.getContentLength();
			if (len < 1) len = 1024;
			baos = new ByteArrayOutputStream(len);
			byte[] buffer = new byte[1024];
			len = 0;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return baos.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) baos.close();
				if (is != null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (conn != null) conn.disconnect();
		}
		return null;
	}

	public static UserResult parseJsonToUser(String json) {
		UserResult user = new UserResult();
		if (json != null) {
			try {
				JSONObject jsonObj = new JSONObject(json);
				JSONObject result = jsonObj.optJSONObject("result");
				JSONObject data = jsonObj.optJSONObject("data");
				JSONArray arr = null;
				if (data != null) arr = data.optJSONArray("girl_friends");

				user.result = new UserResult.Result();
				user.data = new UserResult.User();
				user.data.girl_friends = new ArrayList<String>();

				if (jsonObj != null) {
					user.api = jsonObj.optString("api");
					user.v = jsonObj.optString("v");
				}
				if (result != null) {
					user.result.code = result.optInt("code");
					user.result.message = result.optString("message");
				}
				if (data != null) {
					user.data.age = data.optInt("age");
					user.data.name = data.optString("name");
				}
				if (arr != null) {
					for (int i = 0, size = arr.length(); i < size; i++) {
						user.data.girl_friends.add(arr.optString(i));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return user;
	}
	//数据结构：
	//	{
	//		    "api": "com.xx.get.userinfo",
	//		    "v": "1.0",
	//		    "result": {
	//		        "code": 200,
	//		        "message": "success"
	//		    },
	//		    "data": {
	//		        "age": 18,
	//		        "name": "qingtianzhu",
	//		        "girl_friends": [
	//		            "xiaoli",
	//		            "fengjie",
	//		            "lucy"
	//		        ]
	//		    }
	//		}
}
