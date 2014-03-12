package com.litesuits.http.data;

//import com.alibaba.fastjson.JSON;

/**
 * alibaba fastjson, which is the fastest json handler in processing data that small than 50k.
 * 据本人多次测试，当数据较小时，fastjson处理最快，gson次之，jackson最慢，
 * 但是当数据量大时，大约大于50k，jackson优势凸显出来，快显于前两者。
 * 
 * @author MaTianyu
 * 2014-1-14下午11:44:58
 */
//public class FastJson extends Json {
//
//	@Override
//	public String toString(Object obj) {
//		return JSON.toJSONString(obj);
//	}
//
//	@Override
//	public <T> T toObject(String str, Class<T> claxx) {
//		return JSON.parseObject(str, claxx);
//	}
//
//	@Override
//	public <T> T toObject(byte[] bytes, Class<T> claxx) {
//		return JSON.parseObject(bytes, claxx);
//	}
//
//}
