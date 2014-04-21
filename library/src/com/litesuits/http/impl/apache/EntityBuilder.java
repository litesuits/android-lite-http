package com.litesuits.http.impl.apache;

import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.RequestParams;
import com.litesuits.http.request.RequestParams.FileEntity;
import com.litesuits.http.request.RequestParams.InputStreamEntity;

import org.apache.http.HttpEntity;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * help us to build {@link HttpEntity}
 *
 * @author MaTianyu
 *         2014-1-18上午1:41:41
 */
public class EntityBuilder {

    public static HttpEntity build(Request req) throws HttpClientException {
        try {
            LinkedHashMap<String, String> paramMap = req.getBasicParams();
            LinkedHashMap<String, RequestParams.InputStreamEntity> paramStream = req.getStreamEntity();
            LinkedHashMap<String, RequestParams.FileEntity> paramFile = req.getFileEntity();
            LinkedList<RequestParams.StringEntity> paramString = req.getStringEntity();
            LinkedList<RequestParams.ByteArrayEntity> paramBytes = req.getBytesEntity();
            SimpleMultipartEntity smEntiry = new SimpleMultipartEntity();
            if (paramMap != null) {
                StringBuilder sb = new StringBuilder();
                for (Entry<String, String> en : paramMap.entrySet()) {
                    if (sb.length() != 0) sb.append("&");
                    sb.append(en.getKey()).append("=").append(en.getValue());
                }
                if (sb.length() > 0) {
                    smEntiry.addPart(null, sb.toString().getBytes(), null);
                }
            }
            if (paramBytes != null) {
                for (RequestParams.ByteArrayEntity be : paramBytes) {
                    smEntiry.addPart(null, be.bytes, be.contentType);
                }
            }
            if (paramString != null) {
                for (RequestParams.StringEntity se : paramString) {
                    smEntiry.addPart(null, se.string.getBytes(se.charset), se.contentType);
                }
            }
            if (paramFile != null) {
                for (Entry<String, FileEntity> entry : paramFile.entrySet()) {
                    RequestParams.FileEntity fparam = entry.getValue();
                    smEntiry.addPart(entry.getKey(), fparam.file, fparam.contentType);
                }
            }
            if (paramStream != null) {
                for (Entry<String, InputStreamEntity> entry : paramStream.entrySet()) {
                    InputStreamEntity isp = entry.getValue();
                    smEntiry.addPart(entry.getKey(), isp.name, isp.inputStream, isp.contentType);
                }
            }
            return smEntiry;
        } catch (Exception e) {
            throw new HttpClientException(e);
        }
    }
}
