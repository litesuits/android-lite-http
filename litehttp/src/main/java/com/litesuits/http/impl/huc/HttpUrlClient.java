package com.litesuits.http.impl.huc;

import android.util.Log;
import com.litesuits.http.HttpClient;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.data.Charsets;
import com.litesuits.http.data.Consts;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.*;
import com.litesuits.http.listener.StatisticsListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.InternalResponse;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 氢一 @http://def.so
 * @date 2016-04-03
 */
public class HttpUrlClient implements HttpClient {
    private static final String TAG = "HttpUrlClient";
    private SSLSocketFactory sslSocketFactory;
    private HostnameVerifier hostnameVerifier;

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HttpUrlClient setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public HttpUrlClient setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public void config(HttpConfig config) {
        // do nothing
    }

    @Override
    public <T> void connect(AbstractRequest<T> request, InternalResponse response)
            throws HttpClientException, IOException, URISyntaxException, HttpNetException, HttpServerException {
        InputStream inputStream = null;
        int maxRedirectTimes = request.getMaxRedirectTimes();
        StatisticsListener statistic = response.getStatistics();
        try {
            // 0. build URL
            URL url = new URL(request.createFullUri());

            // 1. open connection and set SSL factory and hostname verifier.
            HttpURLConnection connection;
            if (Consts.SCHEME_HTTPS.equals(url.getProtocol())) {
                HttpsURLConnection httpsConn;
                if (sslSocketFactory == null) {
                    trustAllCertificate();
                    httpsConn = (HttpsURLConnection) url.openConnection();
                } else {
                    httpsConn = (HttpsURLConnection) url.openConnection();
                    httpsConn.setSSLSocketFactory(sslSocketFactory);
                }
                if (hostnameVerifier != null) {
                    httpsConn.setHostnameVerifier(hostnameVerifier);
                } else {
                    httpsConn.setHostnameVerifier(trustAllVerifier());
                }
                connection = httpsConn;
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            // 3. set connection parameters.
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(request.getSocketTimeout());
            connection.setConnectTimeout(request.getConnectTimeout());


            // 3. update http header
            if (request.getHeaders() != null) {
                Set<Map.Entry<String, String>> set = request.getHeaders().entrySet();
                for (Map.Entry<String, String> en : set) {
                    connection.addRequestProperty(en.getKey(), en.getValue());
                }
            }

            // 4. set method and user-agent
            connection.setRequestMethod(request.getMethod().getMethodName());
            connection.setRequestProperty(Consts.USER_AGENT, HttpConfig.userAgent);


            // 5. write data and get input stream
            if (statistic != null) {
                statistic.onPreConnect(request);
            }
            try {
                writeDataIfNecessary(connection, request);
                inputStream = connection.getInputStream();
            } catch (SocketTimeoutException e) {
                throw e;
            } catch (InterruptedIOException e) {
                Log.w(TAG, TAG + " InterruptedIOException ", e);
                // when thread interrupted, get an InterruptedIOException.
                // but sometimes Thread.interrupted() is false, so we cancel it now.
                request.cancel();
            } catch (IOException e) {
                Log.w(TAG, TAG + " IOException ", e);
                inputStream = connection.getErrorStream();
            } finally {
                if (statistic != null) {
                    statistic.onAfterConnect(request);
                }
            }
            // if input stream is null
            if (inputStream == null) {
                throw new HttpNetException(NetException.NetworkUnreachable);
            }

            // 6. handle http status code
            int statusCode = connection.getResponseCode();
            HttpStatus httpStatus = new HttpStatus(statusCode, connection.getResponseMessage());
            response.setHttpStatus(httpStatus);

            // 7. handle headers and content info
            ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
            for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                if (header.getKey() != null) {
                    List<String> values = header.getValue();
                    if (values != null) {
                        for (String value : values) {
                            headers.add(new NameValuePair(header.getKey(), value));
                            //if (Consts.CONTENT_LEN.equalsIgnoreCase(header.getKey())) {
                            //    response.setContentLength(Long.parseLong(value));
                            //} else if (Consts.CONTENT_TYPE.equalsIgnoreCase(header.getKey())) {
                            //    response.setContentType(value);
                            //} else if (Consts.CONTENT_ENCODING.equalsIgnoreCase(header.getKey())) {
                            //    response.setContentEncoding(value);
                            //}
                        }
                    }
                }
            }
            response.setHeaders(headers);
            response.setContentLength(connection.getContentLength());
            response.setContentEncoding(connection.getContentEncoding());
            response.setContentType(connection.getContentType());

            // 8. is cancelled ?
            if (request.isCancelledOrInterrupted()) {
                return;
            }

            // 9. handle data body by status code
            if (statusCode <= 299 || statusCode == 600) {
                // 9.1 get charset and length
                String charSet = getCharsetByContentType(response.getContentType(), request.getCharSet());
                response.setCharSet(charSet);
                long len = response.getContentLength();

                // 9.2 read and parse stream
                if (statistic != null) {
                    statistic.onPreRead(request);
                }
                DataParser<T> parser = request.getDataParser();
                parser.readFromNetStream(inputStream, len, charSet);
                if (statistic != null) {
                    statistic.onAfterRead(request);
                }

                // 9.3 data parser has closed input stream
                inputStream = null;

                // 9.4 set readed length
                response.setReadedLength(parser.getReadedLength());
            } else if (statusCode <= 399) {
                // 10. handle redirect
                if (response.getRedirectTimes() < maxRedirectTimes) {
                    // get the location header to find out where to redirect to
                    String location = connection.getHeaderField(Consts.REDIRECT_LOCATION);
                    if (location != null && location.length() > 0) {
                        if (!location.toLowerCase().startsWith("http")) {
                            URI uri = new URI(request.createFullUri());
                            URI redirect = new URI(uri.getScheme(), uri.getHost(), location, null);
                            location = redirect.toString();
                        }
                        response.setRedirectTimes(response.getRedirectTimes() + 1);
                        request.setUri(location);
                        if (HttpLog.isPrint) {
                            HttpLog.i(TAG, "Redirect to : " + location);
                        }
                        if (request.getHttpListener() != null) {
                            request.getHttpListener().notifyCallRedirect(
                                    request, maxRedirectTimes, response.getRedirectTimes());
                        }
                        connect(request, response);
                        return;
                    }
                    throw new HttpServerException(httpStatus);
                } else {
                    throw new HttpServerException(ServerException.RedirectTooMuch);
                }
            } else if (statusCode <= 499) {
                // 客户端被拒
                throw new HttpServerException(httpStatus);
            } else if (statusCode < 599) {
                // 服务器有误
                throw new HttpServerException(httpStatus);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private String getCharsetByContentType(String contentType, String defCharset) {
        if (contentType != null) {
            String[] values = contentType.split(";"); // values.length should be 2
            for (String value : values) {
                value = value.trim();
                if (value.toLowerCase().startsWith("charset=")) {
                    return value.substring("charset=".length());
                }
            }
        }
        return defCharset == null ? Charsets.UTF_8 : defCharset;
    }

    private void writeDataIfNecessary(HttpURLConnection connection, AbstractRequest<?> request) throws IOException {
        HttpMethods method = request.getMethod();
        if (method == HttpMethods.Post || method == HttpMethods.Put || method == HttpMethods.Patch) {
            HttpBody body = request.getHttpBody();
            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty(Consts.CONTENT_TYPE, body.getContentType());
                OutputStream outStream = connection.getOutputStream();
                body.writeTo(outStream);
                outStream.close();
            }
        }
    }

    public static void trustAllCertificate() {
        // Install the all-trusting trust manager
        try {
            // Create a trust manager that does not validate certificate chains
            // Android use X509 cert
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private HostnameVerifier trustAllVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

}
