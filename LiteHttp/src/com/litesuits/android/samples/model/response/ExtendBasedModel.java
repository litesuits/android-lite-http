package com.litesuits.android.samples.model.response;

import java.util.ArrayList;

import com.litesuits.android.samples.LiteHttpSamplesActivity;

/**
 * @author MaTianyu
 * 2014-3-11下午5:05:59
 */
public class ExtendBasedModel {
	/************************************* 智能解析JSON： 继承的方式完成映射 *****************************************************/
	// UserResponse 的Json结构：
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
	// 将会解析为下面的Java对象：
	/**
	 * 用法： @see
	 * {@link LiteHttpSamplesActivity#makeIntelligentJsonModelMapingRequest()}
	 * @author MaTianyu
	 * 2014-3-11下午5:10:36
	 */
	public static class UserResult extends ApiResult {

		//全部声明public是因为使用方便，性能也好，即使private变量LiteHttp也能自动赋值，开发者可自行斟酌修饰符。
		public User data;

		public static class User {
			public String name;
			public int age;
			public ArrayList<String> girl_friends;
		}

		@Override
		public String toString() {
			return super.toString() + " User [data = " + ((data != null) ? "[name=" + data.name + ", age=" + data.age
					+ ", girl_friends=" + data.girl_friends + "]" + "]" : "null]");
		}
	}

	public static abstract class ApiResult {
		public String api;
		public String v;
		public Result result;

		public static class Result {
			public int code;
			public String message;
		}

		@Override
		public String toString() {
			return "BaseResponse [api=" + api + ", v=" + v + ", result="
					+ (result != null ? "[code=" + result.code + ", message="
							+ result.message + "]" + "]" : "null");
		}
	}

}
