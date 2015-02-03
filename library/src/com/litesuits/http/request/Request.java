package com.litesuits.http.request;

import android.net.Uri;
import com.litesuits.android.log.Log;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpClientException.ClientException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.StringParser;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.param.HttpMethod;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.request.query.AbstractQueryBuilder;
import com.litesuits.http.request.query.JsonQueryBuilder;
import com.litesuits.http.utils.UriUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Base request for {@link LiteHttpClient} method
 *
 * @author MaTianyu
 *         2014-1-1下午9:51:59
 */
public class Request {
    private static final String TAG = Request.class.getSimpleName();
    /**
     * you can give an id to a request
     */
    private long id;
    /**
     * custom tag of request
     */
    private Object tag;
    /**
     * request abort
     */
    protected Abortable abort;
    /**
     * url of http request
     */
    private String url;

    /**
     * add custom header to request.
     */
    private LinkedHashMap<String, String> headers;
    /**
     * key value parameters
     */
    private LinkedHashMap<String, String> paramMap;

    /**
     * intelligently translate java object into mapping(k=v) parameters
     */
    private HttpParam paramModel;
    /**
     * when parameter's value is complex, u can chose one buider, default mode
     * is build value into json string.
     */
    private AbstractQueryBuilder queryBuilder;

    /**
     * defaul method is get(GET).
     */
    private HttpMethod method;
    /**
     * charset of request
     */
    private String charSet = Consts.DEFAULT_CHARSET;
    /**
     * max number of retry..
     */
    private int retryMaxTimes = LiteHttpClient.DEFAULT_MAX_RETRY_TIMES;
    /**
     * http inputsream parser
     */
    private DataParser<?> dataParser;
    /**
     * body of post,put..
     */
    private HttpBody httpBody;
    /**
     * a callback of start,retry,redirect,loading,end,etc.
     */
    private HttpListener httpListener;


    public Request(String url) {
        this(url, null);
    }

    public Request(String url, HttpParam paramModel) {
        this(url, paramModel, new StringParser(), null, null);
    }

    public Request(String url, HttpParam paramModel, DataParser<?> parser, HttpBody httpBody, HttpMethod method) {
        if (url == null) throw new RuntimeException("Url Cannot be Null.");
        this.url = url;
        this.paramModel = paramModel;
        this.queryBuilder = new JsonQueryBuilder();
        setMethod(method);
        setDataParser(parser);
        setHttpBody(httpBody);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Request addHeader(List<NameValuePair> nps) {
        if (nps != null) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            for (NameValuePair np : nps) {
                headers.put(np.getName(), np.getValue());
            }
        }
        return this;
    }

    public Request addHeader(String key, String value) {
        if (value != null) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            headers.put(key, value);
        }
        return this;
    }

    /**
     * 获取消息体
     */
    public HttpBody getHttpBody() {
        return httpBody;
    }

    /**
     * 设置消息体：默认POST方式
     */
    public Request setHttpBody(HttpBody httpBody) {
        if (httpBody != null) {
            return setHttpBody(httpBody, HttpMethod.Post);
        } else {
            return this;
        }
    }

    /**
     * 设置消息体与请求方式
     */
    public Request setHttpBody(HttpBody httpBody, HttpMethod method) {
        setMethod(method);
        this.httpBody = httpBody;
        return this;
    }

    public Request addUrlParam(String key, String value) {
        if (value != null) {
            if (paramMap == null) {
                paramMap = new LinkedHashMap<String, String>();
            }
            paramMap.put(key, value);
        }
        return this;
    }

    /**
     * if you setUrl as "www.tb.cn" .
     * you must add prifix "http://" or "https://" yourself.
     *
     * @param prifix
     * @throws HttpClientException
     */
    public Request addUrlPrifix(String prifix) {
        setUrl(prifix + url);
        return this;
    }

    /**
     * if your url like this "http://tb.cn/i3.html" .
     * you can setUrl("http://tb.cn/") then addUrlSuffix("i3.html").
     *
     * @param suffix
     * @throws HttpClientException
     */
    public Request addUrlSuffix(String suffix) {
        setUrl(url + suffix);
        return this;
    }

    public String getRawUrl() {
        return url;
    }

    public String getUrl() throws HttpClientException {
        // check raw url
        if (url == null) throw new HttpClientException(ClientException.UrlIsNull);

        try {
            StringBuilder sb = new StringBuilder();
            if (url.contains("?")) {
                Uri uri = Uri.parse(url);
                Uri.Builder builder = uri.buildUpon();
                builder.query(null);
                for (String key : UriUtil.getQueryParameterNames(uri)) {
                    for (String value : UriUtil.getQueryParameters(uri, key)) {
                        builder.appendQueryParameter(key, value);
                    }
                }
                if (Log.isPrint) Log.d(TAG, "param url origin: " + uri);
                uri = builder.build();
                if (Log.isPrint) Log.d(TAG, "param url encode: " + uri);
                sb.append(uri);
            } else {
                sb.append(url);
            }
            if (paramMap == null && paramModel == null) {
                return sb.toString();
            }
            if (url.contains("?")) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            LinkedHashMap<String, String> map = getBasicParams();
            int i = 0, size = map.size();
            for (Entry<String, String> v : map.entrySet()) {
                sb.append(URLEncoder.encode(v.getKey(), charSet)).append("=").append(URLEncoder.encode(v.getValue(), charSet)).append(++i == size ? "" : "&");
            }
            //if (Log.isPrint) Log.v(TAG, "lite request url: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            throw new HttpClientException(e);
        }
    }

    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 融合hashmap和解析到的javamodel里的参数，即所有string 参数.
     */
    public LinkedHashMap<String, String> getBasicParams() throws IllegalArgumentException, UnsupportedEncodingException, IllegalAccessException,
            InvocationTargetException {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        if (paramMap != null) map.putAll(paramMap);
        LinkedHashMap<String, String> modelMap = queryBuilder.buildPrimaryMap(paramModel);
        if (modelMap != null) map.putAll(modelMap);
        return map;
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public Request setHeaders(LinkedHashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public LinkedHashMap<String, String> getParamMap() {
        return paramMap;
    }

    public Request setParamMap(LinkedHashMap<String, String> paramMap) {
        this.paramMap = paramMap;
        return this;
    }

    public HttpParam getParamModel() {
        return paramModel;
    }

    public Request setParamModel(HttpParam paramModel) {
        this.paramModel = paramModel;
        return this;
    }

    public AbstractQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public Request setQueryBuilder(AbstractQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Request setMethod(HttpMethod method) {
        if (method != null) {
            this.method = method;
        } else {
            this.method = HttpMethod.Get;
        }
        return this;
    }

    public String getCharSet() {
        return charSet;
    }

    public Request setCharSet(String charSet) {
        this.charSet = charSet;
        return this;
    }

    public int getRetryMaxTimes() {
        return retryMaxTimes;
    }

    public Request setRetryMaxTimes(int retryTimes) {
        this.retryMaxTimes = retryTimes;
        return this;
    }

    public DataParser<?> getDataParser() {
        return dataParser;
    }

    public Request setDataParser(DataParser<?> dataParser) {
        if (dataParser != null) {
            this.dataParser = dataParser;
        } else {
            this.dataParser = new StringParser();
        }
        return this;
    }

    public void setAbort(Abortable abort) {
        this.abort = abort;
    }

    public void abort() {
        if (abort != null) abort.abort();
    }

    public HttpListener getHttpListener() {
        return httpListener;
    }

    public void setHttpListener(HttpListener httpListener) {
        this.httpListener = httpListener;
        if (dataParser != null) dataParser.setHttpReadingListener(httpListener);
    }

    @Override
    public String toString() {
        return "\turl = " + url +
                "\n\tmethod = " + method +
                "\n\theaders = " + headers +
                "\n\tcharSet = " + charSet +
                "\n\tretryMaxTimes = " + retryMaxTimes +
                "\n\tparamModel = " + paramModel +
                "\n\tdataParser = " + (dataParser != null ? dataParser.getClass().getSimpleName() : "null") +
                "\n\tqueryBuilder = " + (queryBuilder != null ? queryBuilder.getClass().getSimpleName() : "null") +
                "\n\tparamMap = " + paramMap +
                "\n\thttpBody = " + httpBody;

    }

    public static interface Abortable {
        public void abort();
    }
}
