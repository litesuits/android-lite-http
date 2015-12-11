package com.litesuits.http.impl.apache;

import android.os.NetworkOnMainThreadException;
import com.litesuits.android.log.Log;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.data.Charsets;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpServerException;
import com.litesuits.http.exception.HttpServerException.ServerException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.StringParser;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.param.HttpMethod;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.response.InternalResponse;
import com.litesuits.http.response.Response;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;

/**
 * 使用Apache HttpClient来实现
 * implemented by Apache HttpClient
 *
 * @author MaTianyu
 *         2014-1-1下午9:38:21
 */
public class ApacheHttpClient extends LiteHttpClient {
    private static String TAG = ApacheHttpClient.class.getSimpleName();
    private DefaultHttpClient mHttpClient;
    private HttpContext       mHttpContext;
    public static final int     DEFAULT_KEEP_LIVE         = 30000;
    public static final int     DEFAULT_MAX_CONN_PER_ROUT = 128;
    public static final int     DEFAULT_MAX_CONN_TOTAL    = 512;
    public static final boolean TCP_NO_DELAY              = true;
    //private static ApacheHttpClient    instance;
    private ConnectRetryHandler retryHandler;

    private ApacheHttpClient(int retrySleep, boolean forceRetry) {
        retryHandler = new ConnectRetryHandler(retrySleep, forceRetry);
        mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
        mHttpClient = createApacheHttpClient(createHttpParams());
    }

    public synchronized static ApacheHttpClient createInstance(int retrySleep, boolean forceRetry) {
        //if (instance == null) {
        //    if (context != null) appContext = context.getApplicationContext();
        //    instance = new ApacheHttpClient(retrySleep, forceRetry);
        //}
        //return instance;
        return new ApacheHttpClient(retrySleep, forceRetry);
    }

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
        HttpProtocolParams.setUserAgent(params, USER_AGENT);
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
     * @param httpParams
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
                if (entity == null) { return; }
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
     * @param httpParams
     * @return ThreadSafeClientConnManager
     */
    private ThreadSafeClientConnManager createClientConnManager(BasicHttpParams httpParams) {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        SSLSocketFactory socketFactory = MySSLSocketFactory.getFixedSocketFactory();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), DEFAULT_HTTP_PORT));
        schemeRegistry.register(new Scheme("https", socketFactory, DEFAULT_HTTPS_PORT));
        return new ThreadSafeClientConnManager(httpParams, schemeRegistry);
    }

	/*----------------------------------  public method  ------------------------------------------*/

    @Override
    public Response executeUnsafely(Request request) throws HttpException {
        Response res = execute(request);
        if (res.getException() != null) throw res.getException();
        return res;
    }

    @Override
    public Response execute(Request request) {
        final InternalResponse innerResponse = getInternalResponse(request);
        if (request == null) return innerResponse;
        try {
            if (request.getHttpListener() != null) request.getHttpListener().onStart(request);
            if (innerResponse.getHttpInnerListener() != null) innerResponse.getHttpInnerListener().onStart(request);
            if (getCommonHeader() != null) {
                request.addHeader(getCommonHeader());
            }
            innerResponse.setRequest(request);
            innerResponse.setDataParser(request.getDataParser());
            readDataWithRetries(request, innerResponse);
        } catch (HttpClientException e) {
            // 客户端异常
            innerResponse.setException(e);
        } catch (HttpNetException e) {
            // 网络异常
            innerResponse.setException(e);
        } catch (HttpServerException e) {
            // 服务器异常
            innerResponse.setException(e);
        } catch (Exception e) {
            // 其他异常，归为客户端异常
            HttpClientException ex = new HttpClientException(e);
            if (e instanceof NetworkOnMainThreadException) {
                ex.setExceptionType(HttpClientException.ClientException.NetworkOnMainThreadException);
            }
            innerResponse.setException(ex);
        } finally {
            if (innerResponse.getHttpInnerListener() != null) innerResponse.getHttpInnerListener().onEnd(innerResponse);
            if (request.getHttpListener() != null) request.getHttpListener().onEnd(innerResponse);
            final HttpException e = innerResponse.getException();
            if (e != null) {
                Log.e(TAG, "http connect error, stack trace as fllows :");
                e.printStackTrace();
            }
        }
        return innerResponse;
    }

    @Override
    public <T> T execute(String uri, DataParser<T> parser, HttpMethod method) {
        execute(new Request(uri).setDataParser(parser).setMethod(method));
        return parser.getData();
    }

    public HttpResponse execute(HttpUriRequest req) throws IOException {
        return mHttpClient.execute(req);
    }

    @Override
    public String get(String uri) {
        return execute(uri, new StringParser(), HttpMethod.Get);
    }

    @Override
    public <T> T get(String uri, DataParser<T> parser) {
        return execute(uri, parser, HttpMethod.Get);
    }

    @Override
    public <T> T get(String uri, HttpParam model, Class<T> claxx) {
        Response res = execute(new Request(uri, model, new StringParser(), null, HttpMethod.Get));
        return res.getObject(claxx);
    }

    @Override
    public String put(String uri) {
        return execute(uri, new StringParser(), HttpMethod.Put);
    }

    @Override
    public <T> T put(String uri, DataParser<T> parser) {
        return execute(uri, parser, HttpMethod.Put);
    }

    @Override
    public <T> T put(String uri, HttpParam model, Class<T> claxx) {
        Response res = execute(new Request(uri, model, new StringParser(), null, HttpMethod.Put));
        return res.getObject(claxx);
    }

    @Override
    public String post(String uri) {
        return execute(uri, new StringParser(), HttpMethod.Post);
    }

    @Override
    public <T> T post(String uri, DataParser<T> parser) {
        return execute(uri, parser, HttpMethod.Post);
    }

    @Override
    public <T> T post(String uri, HttpParam model, Class<T> claxx) {
        return post(uri, model, null, claxx);
    }

    @Override
    public <T> T post(String uri, HttpBody body, Class<T> claxx) {
        return post(uri, null, body, claxx);
    }

    @Override
    public <T> T post(String uri, HttpParam model, HttpBody body, Class<T> claxx) {
        return execute(new Request(uri, model, new StringParser(), body, HttpMethod.Post)).getObject(claxx);
    }

    @Override
    public com.litesuits.http.data.NameValuePair[] head(String uri) {
        Response res = execute(new Request(uri).setMethod(HttpMethod.Head));
        return res.getHeaders();
    }

    @Override
    public String delete(String uri) {
        return execute(uri, new StringParser(), HttpMethod.Delete);
    }

    @Override
    public <T> T delete(String uri, DataParser<T> parser) {
        return execute(uri, parser, HttpMethod.Delete);
    }

    @Override
    public <T> T delete(String uri, HttpParam model, Class<T> claxx) {
        Response res = execute(new Request(uri, model, new StringParser(), null, HttpMethod.Delete));
        return res.getObject(claxx);
    }

	/*----------------------------------  private method  ------------------------------------------*/

    private HttpUriRequest createApacheRequest(Request request) throws HttpClientException {
        //String rawUri = request.getRawUrl();
        HttpEntityEnclosingRequestBase entityRequset = null;
        switch (request.getMethod()) {
            case Get:
                return new HttpGet(request.getUrl());
            case Head:
                return new HttpHead(request.getUrl());
            case Delete:
                return new HttpDelete(request.getUrl());
            case Trace:
                return new HttpTrace(request.getUrl());
            case Options:
                return new HttpOptions(request.getUrl());
            case Post:
                entityRequset = new HttpPost(request.getUrl());
                break;
            case Put:
                entityRequset = new HttpPut(request.getUrl());
                break;
            case Patch:
                entityRequset = new HttpPatch(request.getUrl());
                break;
            default:
                return new HttpGet(request.getUrl());
        }
        if (entityRequset != null) {
            entityRequset.setEntity(EntityBuilder.build(request));
        }
        return entityRequset;
    }

    /**
     * 连接网络读取数据
     *
     * @param request
     * @param innerResponse
     * @throws HttpNetException
     * @throws HttpClientException
     * @throws HttpServerException
     * @throws InterruptedException
     */
    private void readDataWithRetries(Request request, InternalResponse innerResponse) throws HttpException,
            InterruptedException {
        final HttpUriRequest req = createApacheRequest(request);
        // update header
        if (request.getHeaders() != null) {
            for (Entry<String, String> en : request.getHeaders().entrySet()) {
                req.setHeader(new BasicHeader(en.getKey(), en.getValue()));
            }
        }
        // set request abort
        request.setAbort(new Request.Abortable() {
            @Override
            public void abort() {
                req.abort();
            }
        });
        //try connect
        int times = 0, retryTimes = request.getRetryMaxTimes();
        HttpResponse response = null;
        IOException cause = null;
        boolean retry = true;
        HttpListener listener = request.getHttpListener();
        while (retry) {
            try {
                cause = null;
                if (!Thread.currentThread().isInterrupted()) {
                    innerResponse.setTryTimes(++times);
                    // start
                    if (Log.isPrint) Log.v(TAG, "lite http request: " + req.getURI());
                    if (listener != null) listener.onPreConnect(request);
                    if (doStatistics) innerResponse.getHttpInnerListener().onPreConnect(request);
                    response = mHttpClient.execute(req);
                    if (doStatistics) innerResponse.getHttpInnerListener().onAfterConnect(request);
                    if (listener != null) listener.onAfterConnect(request);
                    // status
                    StatusLine status = response.getStatusLine();
                    HttpStatus httpStatus = new HttpStatus(status.getStatusCode(), status.getReasonPhrase());
                    innerResponse.setHttpStatus(httpStatus);
                    // header
                    Header[] headers = response.getAllHeaders();
                    if (headers != null) {
                        com.litesuits.http.data.NameValuePair hs[] = new com.litesuits.http.data.NameValuePair[headers.length];
                        for (int i = 0; i < headers.length; i++) {
                            String name = headers[i].getName();
                            String value = headers[i].getValue();
                            if ("Content-Length".equalsIgnoreCase(name)) {
                                innerResponse.setContentLength(Long.parseLong(value));
                            }
                            hs[i] = new com.litesuits.http.data.NameValuePair(name, value);
                        }
                        innerResponse.setHeaders(hs);
                    }

                    // data body
                    if (status.getStatusCode() <= 299 || status.getStatusCode() == 600) {
                        // 成功
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            // charset
                            String charSet = getCharsetFromEntity(entity, request.getCharSet());
                            innerResponse.setCharSet(charSet);
                            // length
                            long len = innerResponse.getContentLength();
                            DataParser<?> parser = innerResponse.getDataParser();
                            parser.setRequest(request);
                            parser.setHttpReadingListener(request.getHttpListener());
                            if (!Thread.currentThread().isInterrupted()) {
                                if (listener != null) listener.onPreRead(request);
                                if (doStatistics) innerResponse.getHttpInnerListener().onPreRead(request);
                                parser.readInputStream(entity.getContent(), len, charSet);
                                if (doStatistics) innerResponse.getHttpInnerListener().onAfterRead(request);
                                innerResponse.setReadedLength(parser.getReadedLength());
                                if (listener != null) listener.onAfterRead(request);
                            } else {
                                if (Log.isPrint) Log.w(TAG, "DataParser readInputStream :currentThread isInterrupted ");
                            }
                            if (Log.isPrint) Log.v(TAG, "lite http response: " + parser.getData());
                        }
                    } else if (status.getStatusCode() <= 399) {
                        // redirect
                        if (innerResponse.getRedirectTimes() < DEFAULT_MAX_REDIRECT_TIMES) {
                            // get the location header to find out where to redirect to
                            Header locationHeader = response.getFirstHeader(Consts.REDIRECT_LOCATION);
                            if (locationHeader != null) {
                                String location = locationHeader.getValue();
                                if (location != null && location.length() > 0) {
                                    if (!location.toLowerCase().startsWith("http")) {
                                        URI uri = new URI(request.getUrl());
                                        URI redirect = new URI("http", uri.getHost(), location, null);
                                        location = redirect.toString();
                                    }
                                    innerResponse.setRedirectTimes(innerResponse.getRedirectTimes() + 1);
                                    request.setUrl(location);
                                    if (Log.isPrint) Log.i(TAG, "Redirect to : " + location);
                                    if (listener != null) listener.onRedirect(request);
                                    readDataWithRetries(request, innerResponse);
                                    return;
                                }
                            }
                            throw new HttpServerException(httpStatus);
                        } else {
                            throw new HttpServerException(ServerException.RedirectTooMany);
                        }
                    } else if (status.getStatusCode() <= 499) {
                        // 客户端被拒
                        throw new HttpServerException(httpStatus);
                    } else if (status.getStatusCode() < 599) {
                        // 服务器有误
                        throw new HttpServerException(httpStatus);
                    }
                } else {
                    if (Log.isPrint) Log.w(TAG, "While read :currentThread isInterrupted ");
                }
                retry = false;
            } catch (ClientProtocolException e) {
                throw new HttpClientException(e);
            } catch (URISyntaxException e) {
                throw new HttpClientException(e);
            } catch (IllegalStateException e) {
                // for apache http client. if url is illegal, it usually raises
                // an exception as "IllegalStateException: Scheme 'xxx' not registered."
                throw new HttpClientException(e);
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(e, times, retryTimes, mHttpContext, appContext);
            } catch (NullPointerException e) {
                // bug in HttpClient 4.0.x, see
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = new IOException("HttpClient execute NullPointerException");
                retry = retryHandler.retryRequest(cause, times, retryTimes, mHttpContext, appContext);
            }
            if (retry) {
                if (listener != null) listener.onRetry(request, retryTimes, times);
            }
        }
        if (cause != null) throw new HttpNetException(cause);
    }

    /**
     * get Charset String From HTTP Response
     *
     * @param entity
     * @param defCharset
     * @return
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
                            if (s != null && s.length() > 0) { return s; }
                        }
                    }
                }
            }
        }
        return defCharset == null ? Charsets.UTF_8 : defCharset;
    }
}
