package com.litesuits.http.impl.apache;

import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.*;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

/**
 * help us to build {@link org.apache.http.HttpEntity}
 *
 * @author MaTianyu
 *         2014-1-18上午1:41:41
 */
public class EntityBuilder {

    public static HttpEntity build(Request req) {
        try {

            AbstractBody body = req.getHttpBody();
            if (body != null) {
                if (body instanceof StringBody) {
                    // StringBody JsonBody UrlEncodedFormBody
                    StringBody b = (StringBody) body;
                    return new StringEntity(b.string, b.charset);
                } else if (body instanceof ByteArrayBody) {
                    // ByteArrayBody SerializableBody
                    ByteArrayBody b = (ByteArrayBody) body;
                    return new ByteArrayEntity(b.bytes);
                } else if (body instanceof InputStreamBody) {
                    InputStreamBody b = (InputStreamBody) body;
                    return new InputStreamEntity(b.inputStream, b.inputStream.available());
                } else if (body instanceof FileBody) {
                    FileBody b = (FileBody) body;
                    return new FileEntity(b.file, b.contentType);
                } else if (body instanceof MultipartBody) {
                    return new MultipartEntity((MultipartBody) body);
                } else {
                    throw new RuntimeException("Unpredictable Entity Body(非法实体)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
