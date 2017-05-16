package com.litesuits.http.request;

import android.net.Uri;
import com.litesuits.http.annotation.*;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.ClientException;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.listener.GlobalHttpListener;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.content.StringBody;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.request.content.multi.MultipartBody;
import com.litesuits.http.request.param.*;
import com.litesuits.http.request.query.JsonQueryBuilder;
import com.litesuits.http.request.query.ModelQueryBuilder;
import com.litesuits.http.utils.HexUtil;
import com.litesuits.http.utils.HttpUtil;
import com.litesuits.http.utils.MD5Util;
import com.litesuits.http.utils.UriUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.HashMap;
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
    private static final String ENCODE_PATTERN_URL = "^.+\\?(%[0-9a-fA-F]+|[=&A-Za-z0-9_#\\-\\.\\*])*$";
    /**
     * you can give an id to a requestr
     */
    private long id;
    /**
     * scheme and host for uri.
     *
     * such as https://abc.com.
     *
     * note: if {@link #uri} has be set completely and correctly(scheme + host + path), this will be ignored.
     */
    private String baseUrl;
    /**
     * uri of http request.
     *
     * if you has set {@link #baseUrl}, this can be just set a path string for uri.
     * we will concat a full uri as: scheme + host + uri(path)
     *
     * with baseUrl, uri can be just set a path: "/path/api...".
     *
     * if {@link #baseUrl} is null, you must set a complete and correct uri for a request.
     */
    private String uri;
    private String fullUri;
    /**
     * HTTP Method, such as GET, POST, PUT, DELETE, etc.
     *
     * Note: default method is GET: {@link HttpMethods#Get}, choice list is:
     *
     * {@link HttpMethods#Get} {@link HttpMethods#Post} {@link HttpMethods#Delete}
     * {@link HttpMethods#Head} {@link HttpMethods#Put} {@link HttpMethods#Trace}
     * {@link HttpMethods#Options} {@link HttpMethods#Patch}
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
    private int maxRetryTimes = -1;
    /**
     * max number of redirect..
     */
    private int maxRedirectTimes = -1;
    /**
     * connect timeout
     */
    private int connectTimeout = -1;
    /**
     * socket timeout
     */
    private int socketTimeout = -1;
    /**
     * callback of start, success, fialure, retry, redirect, loading, etc.
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
     * dir for cache data
     */
    private String cacheDir;
    /**
     * expire time of cache
     */
    private long cacheExpireMillis = -1;

    /**
     * intelligently translate java object into mapping(k=v) parameters
     */
    private HttpParamModel paramModel;
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
    //    protected DataParser<T> dataParser;
    /**
     * add custom header to request.
     */
    private Map<String, String> headers;
    /**
     * key value parameters
     */
    private Map<String, String> paramMap;
    /**
     * data parser
     */
    protected DataParser<T> dataParser;
    /**
     * global http listener for request
     */
    private GlobalHttpListener globalHttpListener;
    /**
     * parameters field
     */
    private Map<String, Field> paramFieldMap = null;

    /*________________________ constructors  ________________________*/
    //    public AbstractRequest() {}
    public AbstractRequest(String uri) {
        // init annotations
        //readParamFromAnnotations(this.getClass());
        this.uri = uri;
    }

    public AbstractRequest(HttpParamModel paramModel) {
        // init annotations
        //readParamFromAnnotations(this.getClass());
        setParamModel(paramModel);
    }

    public AbstractRequest(HttpParamModel paramModel, HttpListener<T> listener) {
        this(paramModel);
        setHttpListener(listener);
    }

    public AbstractRequest(String uri, HttpParamModel paramModel) {
        this(uri);
        setParamModel(paramModel);
    }

    /*________________________ abstract_method ________________________*/

    /**
     * create the dataparser
     */
    @SuppressWarnings("unchecked")
    public abstract DataParser<T> createDataParser();

    /*________________________ getter_setter ________________________*/

    /**
     * set a data parser
     */
    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setDataParser(DataParser<T> dataParser) {
        this.dataParser = dataParser;
        this.dataParser.setRequest(this);
        return (S) this;
    }

    /**
     * get dataparser
     */
    @SuppressWarnings("unchecked")
    public <D extends DataParser<T>> D getDataParser() {
        if (dataParser == null) {
            setDataParser(createDataParser());
        }
        return (D) dataParser;
    }

    public long getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setId(long id) {
        this.id = id;
        return (S) this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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

    public int getConnectTimeout() {
        return connectTimeout;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return (S) this;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
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
    //    public <S extends AbstractRequest<T>> S setCancel(boolean cancel) {
    //        this.cancel.set(cancel);
    //        return (S) this;
    //    }

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

    public String getCacheDir() {
        return cacheDir;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
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

    public HttpParamModel getParamModel() {
        return paramModel;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setParamModel(HttpParamModel paramModel) {
        if (paramModel != null) {
            try {
                this.paramModel = paramModel;
                readParamFromAnnotations(paramModel);
                if (paramModel instanceof HttpRichParamModel) {
                    HttpRichParamModel richModel = (HttpRichParamModel) paramModel;
                    HttpBody hb = richModel.getHttpBody();
                    HttpListener<T> hl = richModel.getHttpListener();
                    LinkedHashMap<String, String> hs = richModel.getHeaders();
                    ModelQueryBuilder mb = richModel.getModelQueryBuilder();
                    if (hb != null) {
                        setHttpBody(hb);
                    }
                    if (hl != null) {
                        setHttpListener(hl);
                    }
                    if (hs != null) {
                        setHeaders(hs);
                    }
                    if (mb != null) {
                        setQueryBuilder(mb);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        if (httpBody != null) {
            httpBody.setRequest(this);
        }
        this.httpBody = httpBody;
        return (S) this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return (S) this;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
        return (S) this;
    }

    public GlobalHttpListener getGlobalHttpListener() {
        return globalHttpListener;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setGlobalHttpListener(GlobalHttpListener globalHttpListener) {
        this.globalHttpListener = globalHttpListener;
        return (S) this;
    }

    //public boolean isFieldAttachToUrl() {
    //    return isFieldAttachToUrl;
    //}
    //
    //public <S extends AbstractRequest<T>> S setFieldAttachToUrl(boolean fieldAttachToUrl) {
    //    this.isFieldAttachToUrl = fieldAttachToUrl;
    //    return (S) this;
    //}

    /*________________________ private_methods ________________________*/

    /**
     * 融合hashmap和解析到的javamodel里的参数，即所有string 参数.
     */
    public LinkedHashMap<String, String> getBasicParams()
            throws IllegalArgumentException, UnsupportedEncodingException,
            IllegalAccessException, InvocationTargetException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        // 1. 先读取属性的key-value参数
        if (paramModel != null) {
            if (paramModel instanceof HttpRichParamModel && !((HttpRichParamModel) paramModel).isFieldsAttachToUrl()) {
                return map;
            }
            map.putAll(getQueryBuilder().buildPrimaryMap(paramModel));
        }

        // 2. 再读取Map中的key-value参数，如果有和第1步中相同的key，会覆盖之。
        if (paramMap != null) {
            map.putAll(paramMap);
        }
        return map;
    }

    /*________________________ enhenced_methods ________________________*/

    public File getCachedFile() {
        if (cacheDir == null) {
            throw new RuntimeException("lite-http cache dir for request is null !");
        }
        return new File(cacheDir, getCacheKey());
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheMode(CacheMode cacheMode, String key) {
        this.cacheMode = cacheMode;
        this.cacheKey = key;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheExpire(long expire, TimeUnit unit) {
        this.cacheExpireMillis = unit.toMillis(expire);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S setCacheMode(CacheMode cacheMode, long expire, TimeUnit unit) {
        this.cacheMode = cacheMode;
        this.cacheExpireMillis = unit.toMillis(expire);
        return (S) this;
    }

    public boolean isCancelledOrInterrupted() {
        //System.out.println("req cancel: "+cancel.get()+"  req interrupt: " +  Thread.currentThread().isInterrupted());
        return cancel.get() || Thread.currentThread().isInterrupted();
    }

    public void cancel() {
        this.cancel.set(true);
    }


    public String createFullUri() throws HttpClientException {
        if (uri == null || !uri.startsWith(Consts.SCHEME_HTTP)) {
            if (baseUrl == null) {
                throw new HttpClientException(ClientException.UrlIsNull);
            } else if (!baseUrl.startsWith(Consts.SCHEME_HTTP)) {
                throw new HttpClientException(ClientException.IllegalScheme);
            }
            uri = uri == null ? baseUrl : baseUrl + uri;
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
            if (paramMap == null && paramModel == null) {
                return sb.toString();
            }
            LinkedHashMap<String, String> map = getBasicParams();
            int size = map.size();
            if (size > 0) {
                if (!hasQes) {
                    sb.append("?");
                } else if (uri.contains("=")) {
                    sb.append("&");
                }
                int i = 0;
                for (String key : map.keySet()) {
                    // value 会导致强转String失败，需用Object，可能是泛型丢失导致，很奇怪
                    Object value = map.get(key);
                    if (value instanceof String) {
                        sb.append(URLEncoder.encode(key, charSet)).append("=")
                          .append(URLEncoder.encode((String) value, charSet));
                    } else {
                        sb.append(URLEncoder.encode(key, charSet)).append("=")
                          .append(URLEncoder.encode(value.toString(), charSet));
                    }
                    if (++i != size) {
                        sb.append("&");
                    }
                }
                // 使用Entry遍历，Entry再编译后失去泛型约束，可能导致String变为Int。
                // for (Entry<String, String> v : map.entrySet()) {
                //     String key = v.getKey();
                //     String value = v.getValue();
                //     sb.append(URLEncoder.encode(key, charSet)).append("=")
                //       .append(URLEncoder.encode(value, charSet));
                //     if (++i != size) {
                //         sb.append("&");
                //     }
                // }
            }
            //if (Log.isPrint) Log.v(TAG, "lite request uri: " + sb.toString());
            fullUri = sb.toString();
            return fullUri;
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

    @SuppressWarnings("unchecked")
    public <S extends AbstractRequest<T>> S addUrlParam(List<NameValuePair> list) {
        if (list != null) {
            if (paramMap == null) {
                paramMap = new LinkedHashMap<String, String>();
            }
            for (NameValuePair pair : list) {
                paramMap.put(pair.getName(), pair.getValue());
            }
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

    /**
     * is cache mode
     *
     * @return true if in cache mode, else fasle.
     */
    public boolean isCachedModel() {
        return cacheMode != null && cacheMode != CacheMode.NetOnly;
    }

    /**
     * @deprecated
     */
    public boolean needCached() {
        return isCachedModel();
    }

    /**
     * Override by sub-class, set id for request.
     */
    //public long buildHttpID() {
    //    return 0;
    //}

    /**
     * Override by sub-class, build tag for request.
     */
    //public Object buildHttpTag() {
    //    return null;
    //}

    /**
     * Override by sub-class, build uri for request.
     */
    //public String buildHttpUri() {
    //    return null;
    //}

    /**
     * Override by sub-class, build scheme host for request.
     */
    //public String buildSchemeHost() {
    //    return null;
    //}

    /**
     * Override by sub-class, build headers for request.
     */
    //public LinkedHashMap<String, String> buildHeaders() {return null;}

    /**
     * Override by sub-class, build http body for POST/PUT... request.
     *
     * @return such as {@link StringBody}, {@link UrlEncodedFormBody}, {@link MultipartBody}...
     */
    //public HttpBody buildHttpBody() {return null;}

    /**
     * Override by sub-class, whether http params be pinned to url
     */
    //public void initHttpParams() {
    //    Annotation annotations[] = this.getClass().getAnnotations();
    //    // 注解初始化
    //    readParamFromAnnotations(annotations);
    //    // 注解初始化
    //    if (id <= 0) {
    //        id = buildHttpID();
    //    }
    //    if (tag == null) {
    //        tag = buildHttpTag();
    //    }
    //    if (uri == null) {
    //        uri = buildHttpUri();
    //    }
    //    if (baseUrl == null) {
    //        baseUrl = buildSchemeHost();
    //    }
    //    if (headers == null) {
    //        headers = buildHeaders();
    //    }
    //    if (httpBody == null) {
    //        httpBody = buildHttpBody();
    //    }
    //}
    private void readParamFromAnnotations(HttpParamModel model) throws IllegalAccessException {
        Annotation as[] = model.getClass().getAnnotations();
        if (as != null && as.length > 0) {
            for (Annotation a : as) {
                if (a instanceof HttpID) {
                    setId(((HttpID) a).value());
                } else if (a instanceof HttpTag) {
                    setTag(handleAnnotation(model, ((HttpTag) a).value()));// may be replace{}
                } else if (a instanceof HttpBaseUrl) {
                    baseUrl = handleAnnotation(model, ((HttpUri) a).value());// may be replace{}
                } else if (a instanceof HttpUri) {
                    uri = handleAnnotation(model, ((HttpUri) a).value());// may be replace{}
                } else if (a instanceof HttpMethod) {
                    method = ((HttpMethod) a).value();
                } else if (a instanceof HttpCacheMode) {
                    cacheMode = ((HttpCacheMode) a).value();
                } else if (a instanceof HttpCacheExpire) {
                    TimeUnit unit = ((HttpCacheExpire) a).unit();
                    long time = ((HttpCacheExpire) a).value();
                    cacheExpireMillis = unit.toMillis(time);
                } else if (a instanceof HttpCacheKey) {
                    cacheKey = handleAnnotation(model, ((HttpCacheKey) a).value());// may be replace{}
                } else if (a instanceof HttpCharSet) {
                    charSet = ((HttpCharSet) a).value();
                } else if (a instanceof HttpMaxRedirect) {
                    maxRetryTimes = ((HttpMaxRedirect) a).value();
                } else if (a instanceof HttpMaxRetry) {
                    maxRetryTimes = ((HttpMaxRetry) a).value();
                }
            }
        }
    }

    private String handleAnnotation(HttpParamModel model, String value) throws IllegalAccessException {
        if (value.indexOf('{') >= 0) {
            if (paramFieldMap == null) {
                paramFieldMap = new HashMap<String, Field>();
                List<Field> fields = HttpUtil.getAllParamModelFields(model.getClass());
                if (fields != null) {
                    HttpLog.i(TAG, "handleAnnotation fields: " + fields.size());
                    for (Field f : fields) {
                        HttpReplace anno = f.getAnnotation(HttpReplace.class);
                        if (anno != null) {
                            paramFieldMap.put(anno.value(), f);
                        }
                    }
                }
            }
            if (!paramFieldMap.isEmpty()) {
                for (Entry<String, Field> entry : paramFieldMap.entrySet()) {
                    entry.getValue().setAccessible(true);
                    Object object = entry.getValue().get(model);
                    if (object != null) {
                        value = value.replace("{" + entry.getKey() + "}", object.toString());
                    }
                }
            }
            HttpLog.i(TAG, "handleAnnotation value: " + value);
        }
        return value;
    }

    /*________________________ string_methods ________________________*/
    @Override
    public String toString() {
        return reqToString();
    }

    public String reqToString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("\n________________ request-start ________________")
              .append("\n full uri         : ").append(fullUri)
              .append("\n id               : ").append(id)
              .append("\n method           : ").append(method)
              .append("\n tag              : ").append(tag)
              .append("\n class            : ").append(getClass().getSimpleName())
              .append("\n charSet          : ").append(charSet)
              .append("\n maxRetryTimes    : ").append(maxRetryTimes)
              .append("\n maxRedirectTimes : ").append(maxRedirectTimes)
              .append("\n httpListener     : ").append(httpListener)
              .append("\n cancelled        : ").append(cancel.get())
              .append("\n cacheMode        : ").append(cacheMode)
              .append("\n cacheKey         : ").append(cacheKey)
              .append("\n cacheExpireMillis: ").append(cacheExpireMillis)
              .append("\n model            : ").append(paramModel)
              .append("\n queryBuilder     : ").append(queryBuilder)
              .append("\n httpBody         : ").append(httpBody)
              .append("\n dataParser       : ").append(getDataParser())
              .append("\n header           ");
            if (headers == null) {
                sb.append(": null");
            } else {
                for (Entry<String, String> en : headers.entrySet()) {
                    sb.append("\n|    ").append(String.format("%-20s", en.getKey())).append(" = ").append(en.getValue());
                }
            }
            sb.append("\n paramMap         ");
            if (paramMap == null) {
                sb.append(": null");
            } else {
                for (Entry<String, String> en : paramMap.entrySet()) {
                    sb.append("\n|    ").append(String.format("%-20s", en.getKey())).append(" = ").append(en.getValue());
                }
            }
            sb.append("\n________________ request-end ________________");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
