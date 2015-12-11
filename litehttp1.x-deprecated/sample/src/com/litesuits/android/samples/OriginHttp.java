package com.litesuits.android.samples;

import com.litesuits.android.samples.model.response.ExtendBasedModel.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * 传统http连接读取过程和解析json方式
 * @author MaTianyu
 * 2014-3-11下午3:28:36
 */
public class OriginHttp {


    static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    static class MyTrustManager implements X509TrustManager {


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * 关闭流顺序：先打开的后关闭；被依赖的后关闭。
     *
     * @return string info
     */
    public static String sendHttpRequstSSL(String apiUrl) {
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(apiUrl);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            int len = conn.getContentLength();
            if (len < 1) {
                len = 1024;
            }
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
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

	public static User parseJsonToUser(String json) {
		User user = new User();
		if (json != null) {
			try {
				JSONObject jsonObj = new JSONObject(json);
				JSONObject result = jsonObj.optJSONObject("result");
				JSONObject data = jsonObj.optJSONObject("data");
				JSONArray arr = null;
				if (data != null) arr = data.optJSONArray("girl_friends");

				user.result = new User.Result();
				user.data = new User.UserInfo();
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
