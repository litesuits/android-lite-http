package com.litesuits.android.samples.model.response;

import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.litesuits.android.samples.LiteHttpSamplesActivity;
import com.litesuits.http.data.Json;

/**
 * 
 * @author MaTianyu
 * 2014-3-11下午5:06:06
 */
public class CompositeBasedModel {
	/************************************* 智能解析JSON： 组合的方式完成映射 *****************************************************/
	/**
	 * 用法： @see
	 * {@link LiteHttpSamplesActivity#makeIntelligentJsonModelMapingRequest()}
	 */
	public static class UserModel {
		private String name;
		private int age;
		public ArrayList<String> girl_friends;

		@Override
		public String toString() {
			return "User [name=" + name + ", age=" + age + ", girl_friends=" + girl_friends + "]";
		}
	}

	/**
	 * 用法： @see
	 * {@link LiteHttpSamplesActivity#makeIntelligentJsonModelMapingRequest()}
	 * <p>使用google_gson#JsonObject完成data映射
	 */
	public static class ApiResult {
		public String api;
		private String v;
		public Result result;
		/**
		 * 组合: 这里使用Google Gson的JsonObject做中转类型
		 * data可以根据json结构转化为其相应类的实例
		 */
		public JsonElement data;

		public <T> T getData(Class<T> claxx) {
			System.out.println(data.toString());
			return Json.get().toObject(data.toString(), claxx);
		}

		public static class Result {
			public int code;
			public String message;

			@Override
			public String toString() {
				return "Result [code=" + code + ", message=" + message + "]";
			}

		}

		@Override
		public String toString() {
			return "BaseResponse [api=" + api + ", v=" + v + ", result="
					+ (result != null ? "[code=" + result.code + ", message="
							+ result.message + "]" + "]" : "null");
		}
	}

	// APIResult 的Json结构：
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
