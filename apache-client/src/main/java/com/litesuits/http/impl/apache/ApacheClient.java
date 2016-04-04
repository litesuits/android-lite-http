package com.litesuits.http.impl.apache;

import android.os.Build;
import com.litesuits.http.HttpClient;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.data.*;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpServerException;
import com.litesuits.http.exception.ServerException;
import com.litesuits.http.listener.StatisticsListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.response.InternalResponse;
import org.apache.http.*;
import org.apache.http.NameValuePair;
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
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 使用Apache HttpClient来实现
 * implemented by Apache HttpClient
 *
 * @author MaTianyu
 *         2014-1-1下午9:38:21
 */
public class ApacheClient implements HttpClient {
    private static String TAG = ApacheClient.class.getSimpleName();
    private DefaultHttpClient mHttpClient;
    public static final int DEFAULT_KEEP_LIVE = 30000;
    public static final int DEFAULT_MAX_CONN_PER_ROUT = 128;
    public static final int DEFAULT_MAX_CONN_TOTAL = 256;
    public static final int DEFAULT_TIMEOUT = 20 * 1000;
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final boolean TCP_NO_DELAY = true;
    /**
     * User-Agent
     */
    protected String userAgent = String.format("litehttp-%s (android-%s; api-%s; %s; %s)", "v3"
            , Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Build.BRAND, Build.MODEL);

    public ApacheClient() {
        mHttpClient = createApacheHttpClient(createHttpParams());
    }

    /*__________________________ initialization_methods __________________________*/

    /**
     * initialize HttpParams , initialize settings such as total connextions,timeout ...
     */
    private BasicHttpParams createHttpParams() {
        BasicHttpParams params = new BasicHttpParams();
        ConnManagerParams.setTimeout(params, DEFAULT_TIMEOUT);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(DEFAULT_MAX_CONN_PER_ROUT));
        ConnManagerParams.setMaxTotalConnections(params, DEFAULT_MAX_CONN_TOTAL);
        HttpConnectionParams.setTcpNoDelay(params, TCP_NO_DELAY);
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, DEFAULT_BUFFER_SIZE);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(params, userAgent);
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
            public URI getLocationURI(HttpResponse response, HttpContext context) {
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
            public void process(HttpRequest request, HttpContext context) {
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

    @Override
    public void config(HttpConfig config) {
        if (mHttpClient != null) {
            HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, config.getConnectTimeout());
            HttpConnectionParams.setSoTimeout(params, config.getSocketTimeout());
            HttpProtocolParams.setUserAgent(params, config.getUserAgent());
            mHttpClient.setParams(params);
        }
    }

    @Override
    public <T> void connect(AbstractRequest<T> request, InternalResponse response) throws Exception {
        //try to connect
        HttpEntity entity = null;
        int maxRedirectTimes = request.getMaxRedirectTimes();
        StatisticsListener statistic = response.getStatistics();
        try {
            // 1. create apache request
            final HttpUriRequest apacheRequest = createApacheRequest(request);

            // 2. update http header
            if (request.getHeaders() != null) {
                Set<Entry<String, String>> set = request.getHeaders().entrySet();
                for (Entry<String, String> en : set) {
                    apacheRequest.setHeader(new BasicHeader(en.getKey(), en.getValue()));
                }
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
                ArrayList<com.litesuits.http.data.NameValuePair> hs = new ArrayList<com.litesuits.http.data.NameValuePair>();
                for (Header header : headers) {
                    String name = header.getName();
                    String value = header.getValue();
                    if ("Content-Length".equalsIgnoreCase(name)) {
                        response.setContentLength(Long.parseLong(value));
                    }
                    hs.add(new com.litesuits.http.data.NameValuePair(name, value));
                }
                response.setHeaders(hs);
            }
            // 成功
            entity = ares.getEntity();

            // is cancelled ?
            if (request.isCancelledOrInterrupted()) {
                return;
            }

            // data body
            if (status.getStatusCode() <= 299 || status.getStatusCode() == 600) {
                if (entity != null) {
                    // charset
                    String charSet = getCharsetFromEntity(entity, request.getCharSet());
                    response.setCharSet(charSet);

                    if (!request.isCancelledOrInterrupted()) {
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
                    }
                }
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
                            if (request.getHttpListener() != null) {
                                request.getHttpListener()
                                       .notifyCallRedirect(request, maxRedirectTimes, response.getRedirectTimes());
                            }
                            connect(request, response);
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
        } finally {
            if (entity != null) {
                consumeEntityContent(entity);
            }
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
     *
     * @param entity HttpEntity, may be null
     */
    public void consumeEntityContent(HttpEntity entity) {
        try {
            // Close the InputStream and release the resources by "consuming the content".
            if (entity instanceof HttpEntityWrapper) {
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
            } else {
                entity.consumeContent();
            }
        } catch (Throwable t) {
            HttpLog.e(TAG, "wrappedEntity consume error. ", t);
        }
    }

}
