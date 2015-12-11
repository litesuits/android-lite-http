package com.litesuits.android.samples.model.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.litesuits.http.request.param.CustomHttpParam;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.request.param.NonHttpParam;

/**
 * Java Object
 * 将java model 转化为http param
 */
public class RequestParams {
	/**
	 * 必须实现{@link HttpParam},才能被自动转化为参数
	 * 参数在你父类的父类那也没关系，LiteHttp也能找出来传递过去。
	 * <ul>这些将被忽略：
	 * <li>1. 被static final修饰</li>
	 * <li>2.标注{@link @NonHttpParam} </li>
	 * <li>3. 值为null的属性</li>
	 * </ul>
	 * @author MaTianyu
	 */
	public static abstract class Base implements HttpParam {
		/**
		 * 对象组。就是呆在你爷爷那也没关系，LiteHttp也能揪出来。
		 * strings
		 */
		public String[] strings = new String[]{"s1", "s2", "s3"};
	}

	/**
	 * 基类.
	 * 属性在父类也没关系，LiteHttp也能把你揪出来。
	 * @author MaTianyu
	 */
	public static class BaseSearch extends Base {
		/**
		 * 真实的场景也可能会延迟初始化这个参数。
		 * 这里的实用性在于开发者可以自已组织String，
		 * 我们将自动调用{@link CustomHttpParam#buildValue} 来获取参数值
		 * 比如假设每个接口都有的sign签名或其他自定义参数，在这里调用返回sign字符串。
		 */
		public CustomHttpParam custom = new CustomHttpParam() {
			@Override
			public CharSequence buildValue() {
				return sign();
			}
		};
		/**
		 * 这里static只是便于模拟自定义数据的场景。
		 */
		public static ArrayList<String> list = new ArrayList<String>();
		/**
		 * map
		 */
		public static LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();

		static {
			list.add("va");
			list.add("vb");
			list.add("vc");
			map.put("one", 1);
			map.put("two", 6);
			map.put("three", 8);
		}

		private static String sign() {
			return "sign";
		}
	}

	/**
	 * 
	 * {@link BaiDuSearch} 将会被解析为：
	 * http://baidu.com/s?custom=sign&bs=大家好！&wd=你好Lite&inputT=0&list=["va","vb"
	 * ,"vc"]&map={"one":1,"two":6,"three":8}&strings=["s1","s2","s3"]
	 * 当然，里面的汉字和特殊字符将会被urlencode
	 * @author MaTianyu
	 */
	public static class BaiDuSearch extends BaseSearch {
		/**
		 * static final 一起修饰的属性不会被解析入http参数
		 */
		public static final int CONTANT = 12354;
		/**
		 * 空值不会被解析入http参数
		 */
		public String haha = null;
		/**
		 * 标记{@link @NonHttpParam}不会被解析入http参数
		 */
		@NonHttpParam
		public String noparam = "this will be ignore";

		public String wd = "你好Lite";
		/**
		 * 你是私有变量也没关系，也能把你揪出来。
		 */
		private String bs = "大家好！";
		protected int inputT = 0;
	}
}
