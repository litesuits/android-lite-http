package com.litesuits.http.impl.apache;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.RequestParams.FileParam;
import com.litesuits.http.request.RequestParams.InputStreamParam;

/**
 * help us to build {@link HttpEntity}
 * 
 * @author MaTianyu
 *         2014-1-18上午1:41:41
 */
public class EntityBuilder {

	public static HttpEntity build(Request req) throws HttpClientException {
		HttpEntity entity = null;
		try {
			LinkedHashMap<String, String> paramMap = req.getBasicParams();
			LinkedHashMap<String, InputStreamParam> paramStream = req.getParamStream();
			LinkedHashMap<String, FileParam> paramFile = req.getParamFile();

			if (paramStream == null && paramFile == null) {
				LinkedList<NameValuePair> stringParmsList = new LinkedList<NameValuePair>();
				for (Entry<String, String> en : paramMap.entrySet()) {
					stringParmsList.add(new BasicNameValuePair(en.getKey(), en.getValue()));
				}
				entity = new UrlEncodedFormEntity(stringParmsList);
			} else {
				SimpleMultipartEntity smEntiry = new SimpleMultipartEntity();
				for (Entry<String, String> en : paramMap.entrySet()) {
					smEntiry.addPart(en.getKey(), en.getValue());
				}
				if (paramStream != null) {
					for (Entry<String, InputStreamParam> entry : paramStream.entrySet()) {
						InputStreamParam isp = entry.getValue();
						smEntiry.addPart(entry.getKey(), isp.name, isp.inputStream, isp.contentType);
					}
				}
				if (paramFile != null) {
					for (Entry<String, FileParam> entry : paramFile.entrySet()) {
						FileParam fparam = entry.getValue();
						smEntiry.addPart(entry.getKey(), fparam.file, fparam.contentType);
					}
				}
				entity = smEntiry;
			}
		} catch (Exception e) {
			throw new HttpClientException(e);
		}
		return entity;
	}
}
