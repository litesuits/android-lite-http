package com.litesuits.http.request.content;

import com.litesuits.http.data.Consts;
import com.litesuits.http.data.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class UrlEncodedFormBody extends StringBody {

    public UrlEncodedFormBody(List<NameValuePair> list) {
        super(handleString(list, Consts.DEFAULT_CHARSET), Consts.MIME_TYPE_FORM_URLENCODE, Consts.DEFAULT_CHARSET);
    }

    public UrlEncodedFormBody(List<NameValuePair> list, String charset) {
        super(handleString(list, charset), Consts.MIME_TYPE_FORM_URLENCODE, charset);
    }

    private static String handleString(List<NameValuePair> list, String charset) {
        if (list == null)
            return "";
        StringBuilder sb = new StringBuilder();
        boolean isF = true;
        for (NameValuePair p : list) {
            if (!isF)
                sb.append(Consts.AND);
            else
                isF = false;
            try {
                sb.append(URLEncoder.encode(p.getName(), charset))
                  .append(Consts.EQUALS)
                  .append(URLEncoder.encode(p.getValue(), charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    @Override
    public String toString() {
        return "StringEntity{" +
               "string='" + string + '\'' +
               ", charset='" + charset + '\'' +
               ", contentType='" + contentType + '\'' +
               '}';
    }
}
