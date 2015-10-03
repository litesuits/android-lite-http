package com.litesuits.http.impl.apache;

import android.os.Build;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.data.Charsets;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.exception.*;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.listener.StatisticsListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.response.InternalResponse;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 使用Apache HttpClient来实现
 * implemented by Apache HttpClient
 *
 * @author MaTianyu
 *         2014-1-1下午9:38:21
 */
public class ApacheHttpClient extends LiteHttp {
    private static String TAG = ApacheHttpClient.class.getSimpleName();
    private DefaultHttpClient mHttpClient;
    private HttpContext mHttpContext;
    public static final int DEFAULT_KEEP_LIVE = 30000;
    public static final int DEFAULT_MAX_CONN_PER_ROUT = 128;
    public static final int DEFAULT_MAX_CONN_TOTAL = 256;
    public static final boolean TCP_NO_DELAY = true;
    private HttpRetryHandler retryHandler;

    public ApacheHttpClient(HttpConfig config) {
        super();
        initConfig(config);
        mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
        mHttpClient = createApacheHttpClient(createHttpParams());
    }

    @Override
    public void initConfig(HttpConfig config) {
        if (config == null) {
            config = new HttpConfig(null);
        }
        super.initConfig(config);
        retryHandler = new HttpRetryHandler(config.getRetrySleepMillis(), config.isRequestSentRetryEnabled());
        if (mHttpClient != null) {
            HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, config.getConnectTimeout());
            HttpConnectionParams.setSoTimeout(params, config.getSocketTimeout());
            HttpConnectionParams.setSocketBufferSize(params, config.getSocketBufferSize());
            HttpProtocolParams.setUserAgent(params, config.getUserAgent());
            mHttpClient.setParams(params);
        }
    }

    @Override
    protected void setConfigForRetryHandler(int retrySleepMillis, boolean requestSentRetryEnabled) {
        super.setConfigForRetryHandler(retrySleepMillis, requestSentRetryEnabled);
        retryHandler = new HttpRetryHandler(config.getRetrySleepMillis(), config.isRequestSentRetryEnabled());
        HttpLog.i(TAG, "lite-http set retrySleepMillis" + retrySleepMillis
                       + " , requestSentRetryEnabled: " + requestSentRetryEnabled);
    }

    @Override
    protected void setConfigForHttpParams(int connectTimeout, int socketTimeout, int socketBufferSize) {
        super.setConfigForHttpParams(connectTimeout, socketTimeout, socketBufferSize);
        if (mHttpClient != null) {
            HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, config.getConnectTimeout());
            HttpConnectionParams.setSoTimeout(params, config.getSocketTimeout());
            HttpConnectionParams.setSocketBufferSize(params, config.getSocketBufferSize());
            mHttpClient.setParams(params);
            HttpLog.i(TAG, "lite-http set connectTimeout" + connectTimeout
                           + " , socketTimeout: " + socketTimeout
                           + " , socketBufferSize: " + socketBufferSize);
        }
    }

    @Override
    protected void setUserAgent(String userAgent) {
        super.setUserAgent(userAgent);
        if (mHttpClient != null) {
            HttpParams params = mHttpClient.getParams();
            HttpProtocolParams.setUserAgent(params, config.getUserAgent());
            mHttpClient.setParams(params);
            HttpLog.i(TAG, "lite-http set User-Agent: " + userAgent);
        }
    }

    /*__________________________ initialization_methods __________________________*/

    /**
     * initialize HttpParams , initialize settings such as total connextions,timeout ...
     */
    private BasicHttpParams createHttpParams() {
        BasicHttpParams params = new BasicHttpParams();
        ConnManagerParams.setTimeout(params, config.getConnectTimeout());
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(DEFAULT_MAX_CONN_PER_ROUT));
        ConnManagerParams.setMaxTotalConnections(params, DEFAULT_MAX_CONN_TOTAL);
        HttpConnectionParams.setTcpNoDelay(params, TCP_NO_DELAY);
        HttpConnectionParams.setConnectionTimeout(params, config.getConnectTimeout());
        HttpConnectionParams.setSoTimeout(params, config.getSocketTimeout());
        HttpConnectionParams.setSocketBufferSize(params, config.getSocketBufferSize());
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(params, config.getUserAgent());
        // settingOthers(params);
        return params;
    }

    private void settingOthers(BasicHttpParams params) {
        HttpConnectionParams.setLinger(params, DEFAULT_KEEP_LIVE);
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpProtocolParams.setUseExpectContinue(params, false);
    }

    /**
     * new {@link DefaultHttpClient}, and set strategy.
     *
     * @return DefaultHttpClient
     */
    private DefaultHttpClient createApacheHttpClient(BasicHttpParams httpParams) {
        DefaultHttpClient httpClient = new DefaultHttpClient(createClientConnManager(httpParams), httpParams);
        // disable apache default redirect handler
        httpClient.setRedirectHandler(new RedirectHandler() {

            @Override
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                return false;
            }

            @Override
            public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
                return null;
            }
        });
        // disable apache default retry handler
        httpClient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                return false;
            }
        });
        // enable gzip supporting in request
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(org.apache.http.HttpRequest request, HttpContext context) {
                if (!request.containsHeader(Consts.HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(Consts.HEADER_ACCEPT_ENCODING, Consts.ENCODING_GZIP);
                }
            }
        });
        // enable gzip supporting in response
        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(Consts.ENCODING_GZIP)) {
                            response.setEntity(new GZIPEntityWrapper(entity));
                            break;
                        }
                    }
                }
            }
        });
        // setKeepAlive(httpClient);
        return httpClient;
    }

    private void setKeepAlive(DefaultHttpClient httpClient) {
        // 10 seconds keepalive
        httpClient.setReuseStrategy(new ConnectionReuseStrategy() {
            @Override
            public boolean keepAlive(HttpResponse arg0, HttpContext arg1) {
                return true;
            }
        });
        httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse arg0, HttpContext arg1) {
                return DEFAULT_KEEP_LIVE;
            }
        });
    }

    /**
     * register http and https scheme, and got ThreadSafeClientConnManager
     *
     * @return ThreadSafeClientConnManager
     */
    private ThreadSafeClientConnManager createClientConnManager(BasicHttpParams httpParams) {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        SSLSocketFactory socketFactory = MySSLSocketFactory.getFixedSocketFactory();
        schemeRegistry.register(new Scheme(Consts.SCHEME_HTTP,
                PlainSocketFactory.getSocketFactory(),
                HttpConfig.DEFAULT_HTTP_PORT));
        schemeRegistry.register(new Scheme(Consts.SCHEME_HTTPS, socketFactory, HttpConfig.DEFAULT_HTTPS_PORT));
        return new ThreadSafeClientConnManager(httpParams, schemeRegistry);
    }

    /*__________________________ implemention_methods __________________________*/

    /**
     * 连接网络读取数据
     */
    @Override
    protected <T> void connectWithRetries(AbstractRequest<T> request, InternalResponse response)
            throws HttpClientException, HttpNetException, HttpServerException {

        //if(true) {
        //    throw new HttpNetException(NetException.NetworkDisabled);
        //}

        // 1. create apache request
        final HttpUriRequest apacheRequest = createApacheRequest(request);

        // 2. update http header
        if (request.getHeaders() != null) {
            Set<Entry<String, String>> set = request.getHeaders().entrySet();
            for (Entry<String, String> en : set) {
                apacheRequest.setHeader(new BasicHeader(en.getKey(), en.getValue()));
            }
        }

        // 3. try to connect
        HttpListener<T> listener = request.getHttpListener();
        StatisticsListener statistic = response.getStatistics();
        int times = 0, maxRetryTimes = request.getMaxRetryTimes(), maxRedirectTimes = request.getMaxRedirectTimes();
        boolean retry = true;
        IOException cause = null;
        while (retry) {
            try {
                cause = null;
                retry = false;
                if (request.isCancelledOrInterrupted()) {
                    return;
                }
                if (statistic != null) {
                    statistic.onPreConnect(request);
                }
                HttpResponse ares = mHttpClient.execute(apacheRequest);
                if (statistic != null) {
                    statistic.onAfterConnect(request);
                }
                // status
                StatusLine status = ares.getStatusLine();
                HttpStatus httpStatus = new HttpStatus(status.getStatusCode(), status.getReasonPhrase());
                response.setHttpStatus(httpStatus);
                // header
                Header[] headers = ares.getAllHeaders();
                if (headers != null) {
                    com.litesuits.http.data.NameValuePair hs[] = new com.litesuits.http.data.NameValuePair[headers.length];
                    for (int i = 0; i < headers.length; i++) {
                        String name = headers[i].getName();
                        String value = headers[i].getValue();
                        if ("Content-Length".equalsIgnoreCase(name)) {
                            response.setContentLength(Long.parseLong(value));
                        }
                        hs[i] = new com.litesuits.http.data.NameValuePair(name, value);
                    }
                    response.setHeaders(hs);
                }

                // data body
                if (status.getStatusCode() <= 299 || status.getStatusCode() == 600) {
                    // 成功
                    HttpEntity entity = ares.getEntity();
                    if (entity != null) {
                        // charset
                        String charSet = getCharsetFromEntity(entity, request.getCharSet());
                        response.setCharSet(charSet);
                        // is cancelled ?
                        if (request.isCancelledOrInterrupted()) {
                            return;
                        }
                        // length
                        long len = response.getContentLength();
                        DataParser<T> parser = request.getDataParser();
                        if (statistic != null) {
                            statistic.onPreRead(request);
                        }
                        parser.readFromNetStream(entity.getContent(), len, charSet);
                        if (statistic != null) {
                            statistic.onAfterRead(request);
                        }
                        response.setReadedLength(parser.getReadedLength());
                        endEntityViaReflection(entity);
                    }
                    return;
                } else if (status.getStatusCode() <= 399) {
                    // redirect
                    if (response.getRedirectTimes() < maxRedirectTimes) {
                        // get the location header to find out where to redirect to
                        Header locationHeader = ares.getFirstHeader(Consts.REDIRECT_LOCATION);
                        if (locationHeader != null) {
                            String location = locationHeader.getValue();
                            if (location != null && location.length() > 0) {
                                if (!location.toLowerCase().startsWith("http")) {
                                    URI uri = new URI(request.getFullUri());
                                    URI redirect = new URI(uri.getScheme(), uri.getHost(), location, null);
                                    location = redirect.toString();
                                }
                                response.setRedirectTimes(response.getRedirectTimes() + 1);
                                request.setUri(location);
                                if (HttpLog.isPrint) {
                                    HttpLog.i(TAG, "Redirect to : " + location);
                                }
                                if (listener != null) {
                                    listener.notifyCallRedirect(request, maxRedirectTimes, response.getRedirectTimes());
                                }
                                connectWithRetries(request, response);
                                return;
                            }
                        }
                        throw new HttpServerException(httpStatus);
                    } else {
                        throw new HttpServerException(ServerException.RedirectTooMuch);
                    }
                } else if (status.getStatusCode() <= 499) {
                    // 客户端被拒
                    throw new HttpServerException(httpStatus);
                } else if (status.getStatusCode() < 599) {
                    // 服务器有误
                    throw new HttpServerException(httpStatus);
                }
            } catch (IOException e) {
                cause = e;
            } catch (NullPointerException e) {
                // bug in HttpClient 4.0.x, see http://code.google.com/p/android/issues/detail?id=5255
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                    cause = new IOException(e.getMessage());
                } else {
                    cause = new IOException(e);
                }
            } catch (URISyntaxException e) {
                throw new HttpClientException(e);
            } catch (IllegalStateException e) {
                // for apache http client. if url is illegal, it usually raises an exception as "IllegalStateException:
                // Scheme 'xxx' not registered."
                throw new HttpClientException(e);
            } catch (SecurityException e) {
                throw new HttpClientException(e, ClientException.PermissionDenied);
            } catch (RuntimeException e) {
                throw new HttpClientException(e);
            }
            if (cause != null) {
                try {
                    if (request.isCancelledOrInterrupted()) {
                        return;
                    }
                    times++;
                    retry = retryHandler.retryRequest(cause, times, maxRetryTimes, mHttpContext, config.getContext());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                if (retry) {
                    response.setRetryTimes(times);
                    if (HttpLog.isPrint) {
                        HttpLog.i(TAG, "LiteHttp retry request: " + request.getUri());
                    }
                    if (listener != null) {
                        listener.notifyCallRetry(request, maxRetryTimes, times);
                    }
                }
            }
        }
        if (cause != null) {
            throw new HttpNetException(cause);
        }
    }

    /**
     * create an apache request
     */
    private HttpUriRequest createApacheRequest(AbstractRequest request) throws HttpClientException {
        HttpEntityEnclosingRequestBase entityRequset = null;
        switch (request.getMethod()) {
            case Get:
                return new HttpGet(request.getFullUri());
            case Head:
                return new HttpHead(request.getFullUri());
            case Delete:
                return new HttpDelete(request.getFullUri());
            case Trace:
                return new HttpTrace(request.getFullUri());
            case Options:
                return new HttpOptions(request.getFullUri());
            case Post:
                entityRequset = new HttpPost(request.getFullUri());
                break;
            case Put:
                entityRequset = new HttpPut(request.getFullUri());
                break;
            case Patch:
                entityRequset = new HttpPatch(request.getFullUri());
                break;
            default:
                return new HttpGet(request.getFullUri());
        }
        entityRequset.setEntity(EntityBuilder.build(request));
        return entityRequset;
    }

    /**
     * get Charset String From HTTP Response
     */
    private String getCharsetFromEntity(HttpEntity entity, String defCharset) {
        final Header header = entity.getContentType();
        if (header != null) {
            final HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
                HeaderElement helem = elements[0];
                // final String mimeType = helem.getName();
                final NameValuePair[] params = helem.getParameters();
                if (params != null) {
                    for (final NameValuePair param : params) {
                        if (param.getName().equalsIgnoreCase("charset")) {
                            String s = param.getValue();
                            if (s != null && s.length() > 0) {
                                return s;
                            }
                        }
                    }
                }
            }
        }
        return defCharset == null ? Charsets.UTF_8 : defCharset;
    }

    /**
     * This horrible hack is required on Android, due to implementation of BasicManagedEntity, which
     * doesn't chain call consumeContent on underlying wrapped HttpEntity
     * see more at open source project 'android-async-http'
     *
     * @param entity HttpEntity, may be null
     */
    public void endEntityViaReflection(HttpEntity entity) {
        if (entity instanceof HttpEntityWrapper) {
            try {
                Field f = null;
                Field[] fields = HttpEntityWrapper.class.getDeclaredFields();
                for (Field ff : fields) {
                    if (ff.getName().equals("wrappedEntity")) {
                        f = ff;
                        break;
                    }
                }
                if (f != null) {
                    f.setAccessible(true);
                    HttpEntity wrapped = (HttpEntity) f.get(entity);
                    if (wrapped != null) {
                        wrapped.consumeContent();
                        if (HttpLog.isPrint) {
                            HttpLog.d(TAG, "HttpEntity wrappedEntity reflection consumeContent");
                        }
                    }
                }
            } catch (Throwable t) {
                HttpLog.e(TAG, "wrappedEntity consume error. ", t);
            }
        }
    }
}
