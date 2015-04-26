package com.litesuits.http.request;

import android.net.Uri;
import com.litesuits.http.annotation.*;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.ClientException;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.RequestModel;
import com.litesuits.http.request.param.RequestRichModel;
import com.litesuits.http.request.query.JsonQueryBuilder;
import com.litesuits.http.request.query.ModelQueryBuilder;
import com.litesuits.http.utils.HexUtil;
import com.litesuits.http.utils.MD5Util;
import com.litesuits.http.utils.UriUtil;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base request for {@link com.litesuits.http.LiteHttp} method
 *
 * @author MaTianyu
 *         2014-1-1下午9:51:59
 */
public abstract class AbstractRequest<T> {
    private static final String TAG = AbstractRequest.class.getSimpleName();
    private static final String ENCODE_PATTERN_URL = "^.+\\?(%[0-9a-fA-F]+|[=&A-Za-z0-9_#\\-\\.\\*])+$";
    /**
     * you can give an id to a request
     */
    private long id;
    /**
     * uri of http request
     */
    private String uri;
    /**
     * HTTP Method, such as GET, POST, PUT, DELETE, etc.
     *
     * Note: default method is GET: {@link com.litesuits.http.request.param.HttpMethods#Get}, choice list is:
     *
     * {@link com.litesuits.http.request.param.HttpMethods#Get} {@link com.litesuits.http.request.param.HttpMethods#Post} {@link com.litesuits.http.request.param.HttpMethods#Delete}
     * {@link com.litesuits.http.request.param.HttpMethods#Head} {@link com.litesuits.http.request.param.HttpMethods#Put} {@link com.litesuits.http.request.param.HttpMethods#Trace}
     * {@link com.litesuits.http.request.param.HttpMethods#Options} {@link com.litesuits.http.request.param.HttpMethods#Patch}
     */
    private HttpMethods method;
    /**
     * custom tag of request
     */
    private Object tag;
    /**
     * charset of request, default charset is UTF-8
     */
    private String charSet;
    /**
     * max number of retry..
     */
    private int maxRetryTimes;
    /**
     * max number of redirect..
     */
    private int maxRedirectTimes;
    /**
     * callback of start, success, fialure, retry, redirect, reading, etc.
     */
    private HttpListener<T> httpListener;
    /**
     * cancel this request
     */
    private AtomicBoolean cancel = new AtomicBoolean();
    /**
     * request cache mode
     *
     * default cache mode is NO-CACHE:
     * {@link CacheMode#NetOnly}
     *
     * other choice:
     * {@link CacheMode#NetFirst}
     * {@link CacheMode#CacheFirst}
     */
    private CacheMode cacheMode;
    /**
     * key for cache data
     */
    private String cacheKey;
    /**
     * expire time of cache
     */
    private long cacheExpireMillis;

    /**
     * intelligently translate java object into mapping(k=v) parameters
     */
    private RequestModel model;
    /**
     * when parameter's value is complex, u can chose one buider, default mode
     * is build value into json string.
     *
     * Note : default model query builder is {@link JsonQueryBuilder}
     */
    private ModelQueryBuilder queryBuilder;
    /**
     * body of post,put..
     */
    private HttpBody httpBody;
    /**
     * custom http data parser.
     *
     * such as:
     * {@link com.litesuits.http.parser.impl.BytesParser}
     * {@link com.litesuits.http.parser.impl.StringParser}
     * {@link com.litesuits.http.parser.impl.FileParser}
     * {@link com.litesuits.http.parser.impl.BitmapParser}
     * {@link com.litesuits.http.parser.impl.JsonParser}
     */
    protected DataParser<T> dataParser;
    /**
     * add custom header to request.
     */
    private LinkedHashMap<String, String> headers;
    /**
     * key value parameters
     */
    private LinkedHashMap<String, String> paramMap;

    /*________________________ constructors  ________________________*/
    public AbstractRequest() {
    }

    public AbstractRequest(String uri) {
        this.uri = uri;
    }

    public AbstractRequest(RequestModel model) {
        setModel(model);
    }

    /*________________________ abstract_method ________________________*/

    /**
     * create a dataparser
     */
    protected abstract DataParser<T> createDataParser();


    /*________________________ getter_setter ________________________*/
    public long getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setId(long id) {
        this.id = id;
        return (S) this;
    }

    public String getUri() {
        return uri;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setUri(String uri) {
        this.uri = uri;
        return (S) this;
    }

    public HttpMethods getMethod() {
        return method;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setMethod(HttpMethods method) {
        this.method = method;
        return (S) this;
    }

    public Object getTag() {
        return tag;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setTag(Object tag) {
        this.tag = tag;
        return (S) this;
    }

    public String getCharSet() {
        return charSet;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCharSet(String charSet) {
        this.charSet = charSet;
        return (S) this;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
        return (S) this;
    }

    public int getMaxRedirectTimes() {
        return maxRedirectTimes;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setMaxRedirectTimes(int maxRedirectTimes) {
        this.maxRedirectTimes = maxRedirectTimes;
        return (S) this;
    }

    public HttpListener<T> getHttpListener() {
        return httpListener;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setHttpListener(HttpListener<T> httpListener) {
        this.httpListener = httpListener;
        return (S) this;
    }

    public boolean isCancelled() {
        return cancel.get();
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCancel(boolean cancel) {
        this.cancel.set(cancel);
        return (S) this;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (S) this;
    }

    public String getCacheKey() {
        if (cacheKey == null) {
            cacheKey = HexUtil.encodeHexStr(MD5Util.md5(getUri()));
            if (HttpLog.isPrint) {
                HttpLog.v(TAG, "generate cache key: " + cacheKey);
            }
        }
        return cacheKey;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (S) this;
    }

    public long getCacheExpireMillis() {
        return cacheExpireMillis;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheExpireMillis(long cacheExpireMillis) {
        this.cacheExpireMillis = cacheExpireMillis;
        return (S) this;
    }

    public RequestModel getModel() {
        return model;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setModel(RequestModel model) {
        if (model != null) {
            this.model = model;
            if (model instanceof RequestRichModel) {
                RequestRichModel richModel = (RequestRichModel) model;
                addHeader(richModel.createHeaders());
                if (httpBody == null) {
                    setHttpBody(richModel.createHttpBody());
                }
                if (httpListener == null) {
                    setHttpListener(richModel.createHttpListener());
                }
                if (queryBuilder == null) {
                    setQueryBuilder(richModel.createQueryBuilder());
                }
            }
            Annotation as[] = model.getClass().getAnnotations();
            if (as != null && as.length > 0) {
                for (Annotation a : as) {
                    if (a instanceof HttpID) {
                        if (id == 0) {
                            setId(((HttpID) a).value());
                        }
                    } else if (a instanceof HttpTag) {
                        if (tag == null) {
                            setTag(((HttpTag) a).value());
                        }
                    } else if (a instanceof HttpUri) {
                        if (uri == null) {
                            uri = ((HttpUri) a).value();
                        }
                    } else if (a instanceof HttpMethod) {
                        if (method == null) {
                            method = ((HttpMethod) a).value();
                        }
                    } else if (a instanceof HttpCacheMode) {
                        if (cacheMode == null) {
                            cacheMode = ((HttpCacheMode) a).value();
                        }
                    } else if (a instanceof HttpCacheExpire) {
                        if (cacheExpireMillis == 0) {
                            cacheExpireMillis = ((HttpCacheExpire) a).value();
                        }
                    } else if (a instanceof HttpCacheKey) {
                        if (cacheKey == null) {
                            cacheKey = ((HttpCacheKey) a).value();
                        }
                    } else if (a instanceof HttpCharSet) {
                        if (charSet == null) {
                            charSet = ((HttpCharSet) a).value();
                        }
                    } else if (a instanceof HttpMaxRedirect) {
                        if (maxRedirectTimes == 0) {
                            maxRetryTimes = ((HttpMaxRedirect) a).value();
                        }
                    } else if (a instanceof HttpMaxRetry) {
                        if (maxRetryTimes == 0) {
                            maxRetryTimes = ((HttpMaxRetry) a).value();
                        }
                    }
                }
            }
        }
        return (S) this;
    }

    public ModelQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setQueryBuilder(ModelQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return (S) this;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setHttpBody(HttpBody httpBody) {
        httpBody.setRequest(this);
        this.httpBody = httpBody;
        return (S) this;
    }

    public DataParser<T> getDataParser() {
        if (dataParser == null) {
            dataParser = createDataParser();
            dataParser.setRequest(this);
        }
        return dataParser;
    }

    //@SuppressWarnings("unchecked")
    //public <S extends AbstractRequest<T>> S setDataParser(DataParser<T> dataParser) {
    //    this.dataParser = dataParser;
    //    return (S) this;
    //}

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setHeaders(LinkedHashMap<String, String> headers) {
        this.headers = headers;
        return (S) this;
    }

    public LinkedHashMap<String, String> getParamMap() {
        return paramMap;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setParamMap(LinkedHashMap<String, String> paramMap) {
        this.paramMap = paramMap;
        return (S) this;
    }

    /*________________________ private_methods ________________________*/

    /**
     * 融合hashmap和解析到的javamodel里的参数，即所有string 参数.
     */
    public LinkedHashMap<String, String> getBasicParams()
            throws IllegalArgumentException, UnsupportedEncodingException, IllegalAccessException,
            InvocationTargetException {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        if (paramMap != null) {
            map.putAll(paramMap);
        }
        LinkedHashMap<String, String> modelMap = getQueryBuilder().buildPrimaryMap(model);
        if (modelMap != null) {
            map.putAll(modelMap);
        }
        return map;
    }

    /*________________________ enhenced_methods ________________________*/

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheMode(CacheMode cacheMode, String key) {
        this.cacheMode = cacheMode;
        this.cacheKey = key;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheMode(CacheMode cacheMode, long expire, TimeUnit unit) {
        this.cacheMode = cacheMode;
        this.cacheExpireMillis = unit.toMillis(expire);
        return (S) this;
    }

    public boolean isCancelledOrInterrupted() {
        return isCancelled() || Thread.currentThread().isInterrupted();
    }

    public void cancel() {
        this.cancel.set(true);
    }


    public String getFullUri() throws HttpClientException {
        if (uri == null) {
            throw new HttpClientException(ClientException.UrlIsNull);
        }
        try {
            StringBuilder sb = new StringBuilder();
            boolean hasQes = uri.contains("?");
            if (hasQes && !uri.matches(ENCODE_PATTERN_URL)) {
                Uri uri = Uri.parse(this.uri);
                Uri.Builder builder = uri.buildUpon();
                builder.query(null);
                for (String key : UriUtil.getQueryParameterNames(uri)) {
                    for (String value : UriUtil.getQueryParameters(uri, key)) {
                        builder.appendQueryParameter(key, value);
                    }
                }
                if (HttpLog.isPrint) {
                    HttpLog.d(TAG, "param uri origin: " + uri);
                }
                uri = builder.build();
                if (HttpLog.isPrint) {
                    HttpLog.d(TAG, "param uri encode: " + uri);
                }
                sb.append(uri);
            } else {
                sb.append(uri);
            }
            if (paramMap == null && model == null) {
                return sb.toString();
            }
            if (hasQes) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            LinkedHashMap<String, String> map = getBasicParams();
            int i = 0, size = map.size();
            for (Entry<String, String> v : map.entrySet()) {
                sb.append(URLEncoder.encode(v.getKey(), charSet)).append("=")
                  .append(URLEncoder.encode(v.getValue(), charSet)).append(++i == size ? "" : "&");
            }
            //if (Log.isPrint) Log.v(TAG, "lite request uri: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            throw new HttpClientException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S addUrlParam(String key, String value) {
        if (value != null) {
            if (paramMap == null) {
                paramMap = new LinkedHashMap<String, String>();
            }
            paramMap.put(key, value);
        }
        return (S) this;
    }

    /**
     * if you setUri as "www.tb.cn" .
     * you must add prifix "http://" or "https://" yourself.
     */
    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S addUrlPrifix(String prifix) {
        setUri(prifix + uri);
        return (S) this;
    }

    /**
     * if your uri like this "http://tb.cn/i3.html" .
     * you can setUri("http://tb.cn/") then addUrlSuffix("i3.html").
     */
    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S addUrlSuffix(String suffix) {
        setUri(uri + suffix);
        return (S) this;
    }

    /**
     * 设置消息体与请求方式
     */
    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setHttpBody(HttpBody httpBody, HttpMethods method) {
        setMethod(method);
        setHttpBody(httpBody);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S addHeader(List<NameValuePair> nps) {
        if (nps != null) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            for (NameValuePair np : nps) {
                headers.put(np.getName(), np.getValue());
            }
        }
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S addHeader(String key, String value) {
        if (value != null) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            headers.put(key, value);
        }
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S addHeader(Map<String, String> map) {
        if (map != null) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            headers.putAll(map);
        }
        return (S) this;
    }

    public boolean needCache() {
        return cacheMode != null && cacheMode != CacheMode.NetOnly;
    }

    public boolean doNotCache() {
        return cacheMode == null || cacheMode == CacheMode.NetOnly;
    }

    /*________________________ string_methods ________________________*/
    @Override
    public String toString() {
        return reqToString();
    }

    public String reqToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n_____________________ lite http request start _____________________")
          .append("\n class            : ").append(getClass().getSimpleName())
          .append("\n id               : ").append(id)
          .append("\n uri              : ").append(uri)
          .append("\n method           : ").append(method)
          .append("\n tag              : ").append(tag)
          .append("\n charSet          : ").append(charSet)
          .append("\n maxRetryTimes    : ").append(maxRetryTimes)
          .append("\n maxRedirectTimes : ").append(maxRedirectTimes)
          .append("\n httpListener     : ").append(httpListener)
          .append("\n cancelled        : ").append(cancel.get())
          .append("\n cacheMode        : ").append(cacheMode)
          .append("\n cacheKey         : ").append(cacheKey)
          .append("\n cacheExpireMillis: ").append(cacheExpireMillis)
          .append("\n model       : ").append(model)
          .append("\n queryBuilder     : ").append(queryBuilder)
          .append("\n httpBody         : ").append(httpBody)
          .append("\n dataParser       : ").append(dataParser)
          .append("\n header           ");
        if (headers == null) {
            sb.append(": null");
        } else {
            for (Entry<String, String> en : headers.entrySet()) {
                sb.append("\n|    ").append(String.format("%-20s", en.getKey())).append(" = ").append(en.getValue());
            }
        }
        sb.append("\n paramMap         ");
        if (headers == null) {
            sb.append(": null");
        } else {
            for (Entry<String, String> en : headers.entrySet()) {
                sb.append("\n|    ").append(String.format("%-20s", en.getKey())).append(" = ").append(en.getValue());
            }
        }
        sb.append("\n_____________________ lite http request end ______________________");
        return sb.toString();

    }
}
