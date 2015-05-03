package com.litesuits.http.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.alibaba.fastjson.JSON;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.annotation.*;
import com.litesuits.http.concurrent.OverloadPolicy;
import com.litesuits.http.concurrent.SchedulePolicy;
import com.litesuits.http.concurrent.SmartExecutor;
import com.litesuits.http.config.HttpConfig;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.Json;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.*;
import com.litesuits.http.exception.handler.HttpExceptionHandler;
import com.litesuits.http.listener.GlobalHttpListener;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.MemeoryDataParser;
import com.litesuits.http.request.*;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpParamModel;
import com.litesuits.http.request.param.HttpRichParamModel;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    protected String TAG = MainActivity.class.getSimpleName();
    protected ListView mListview;
    protected BaseAdapter mAdapter;
    protected LiteHttp liteHttp;
    protected Activity activity;
    protected int count = 0;
    private boolean needRestore;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        initViews();
        activity = this;
        // keep an singleton instance of litehttp
        liteHttp = LiteHttp.newApacheHttpClient(null);
    }

    private void initViews() {
        mListview = (ListView) findViewById(R.id.listview);
        mAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.tv_item,
                                            getResources().getStringArray(R.array.http_test_list));
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickTestItem(position);
            }
        });
    }

    /**
     * <item>0. Quickly Configuration</item>
     * <item>1. Asynchronous Request</item>
     * <item>2. Synchronous Request</item>
     * <item>3. Simple Synchronous Request</item>
     * <item>4. Unsafely Request</item>
     * <item>5. HTTPS Reqeust</item>
     * <item>6. Automatic Model Conversion</item>
     * <item>7. Custom Data Parser</item>
     * <item>8. Replace Json Library</item>
     * <item>9. File Upload</item>
     * <item>10. File/Bitmap Download</item>
     * <item>11. Disable Some Network</item>
     * <item>12. Traffic/Time Statistics</item>
     * <item>13. Retry/Redirect</item>
     * <item>14. Best Practices of Exception Handling</item>
     * <item>15. Best Practices of Cancel Request</item>
     * <p/>
     * <item>16. POST Multi-Form Data</item>
     * <item>17. Concurrent and Scheduling</item>
     * <item>18. Detail of Config</item>
     * <item>19. The Use of Annotation</item>
     * <item>20. Multi Cache Mechanism</item>
     * <item>21. CallBack Mechanism</item>
     * <item>22. Best Practices of SmartExecutor</item>
     */
    private void clickTestItem(final int which) {

        final String url = "http://baidu.com";
        final String httpsUrl = "https://www.baidu.com";
        final String userGet = "http://litesuits.com/mockdata/user_get";
        final String picUrl = "http://www.88xm.com/uploads/allimg/150311/1-150311160U0.jpg";
        final String redirectUrl = "http://www.baidu.com/link?url=Lqc3GptP8u05JCRDsk0jqsAvIZh9WdtO_RkXYMYRQEm";

        // restore http config
        if (needRestore) {
            liteHttp.setNewConfig(liteHttp.getConfig().restoreDefault());
            needRestore = false;
        }

        switch (which) {
            case 0:
                // 0. Quickly Configuration

                HttpConfig config = liteHttp.getConfig();
                // set app context
                config.context = this.getApplicationContext();
                // open traffic/time statistics
                config.doStatistics = true;
                // 10 seconds timeout
                config.connectTimeout = 10000;
                // custom User-Agent
                config.USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.1.2; en-us; Nexus One Build/FRF91)";
                // set the number of concurrent tasks at the same time
                config.concurrentSize = HttpUtil.getProcessorsCount();
                // set the maximum number of waiting tasks. if request number greater than #concurrentSize then wait.
                config.waitingQueueSize = 32;
                // policy of execute next waiting task
                config.schedulePolicy = SchedulePolicy.FirstInFistRun;
                // policy of execute overload : request number greater than #waitingQueueSize + #concurrentSize
                config.overloadPolicy = OverloadPolicy.DiscardOld;
                // set max retry times
                config.defaultMaxRetryTimes = 2;
                // other configuration...

                // note : finally you have to set config to make it effective.
                liteHttp.setNewConfig(config);

                HttpUtil.showTips(activity, "LiteHttp2.0", "配置参数成功");
                break;

            case 1:
                // 1. Asynchronous Request

                // 1.0 execute async
                final StringRequest request = new StringRequest(url).setHttpListener(
                        new HttpListener<String>() {
                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", s);
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                            }
                        }
                );

                liteHttp.executeAsync(request);

                // 1.1 perform async
                FutureTask<String> task = liteHttp.performAsync(request);
                break;

            case 2:
                // 2. Synchronous Request

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 2.0 return response
                        Response response = liteHttp.execute(new BytesRequest(url));
                        response.printInfo();

                        // 2.0 return data directly
                        final byte[] data = liteHttp.perform(new BytesRequest(url));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(data));
                            }
                        });
                    }
                }).start();
                break;

            case 3:
                // 3. Simple Synchronous Request

                new AsyncTask<Void, String, NameValuePair[]>() {

                    @Override
                    protected NameValuePair[] doInBackground(Void... params) {

                        // 3.0 simple get and publish
                        String result = liteHttp.get(url);
                        publishProgress("Simple Get: \n" + result);

                        // 3.0 simple post and publish
                        result = liteHttp.post(new StringRequest(url));
                        publishProgress("Simple POST: \n" + result);

                        // 3.0 simple head and return
                        NameValuePair[] headers = liteHttp.head(new StringRequest(url));
                        return headers;
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        Toast.makeText(activity, values[0], Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onPostExecute(NameValuePair[] nameValuePairs) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(nameValuePairs));
                    }
                }.execute();
                break;

            case 4:
                // 4. Unsafely Request

                try {
                    liteHttp.executeOrThrow(new BytesRequest("haha://hehe"));
                    liteHttp.performOrThrow(new BytesRequest("baidu.com"));
                } catch (HttpException e) {
                    HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(e.getStackTrace()));
                }

                break;
            case 5:
                // 5. HTTPS Reqeust

                liteHttp.executeAsync(new StringRequest(httpsUrl).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", s);
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(e.getStackTrace()));
                    }
                }));

                break;
            case 6:
                // 6. Automatic Model Conversion

                UserRequest userRequest = new UserRequest(userGet);
                // build as http://...?id=168&key=md5
                userRequest.setParamModel(new UserParam(18, "md5"));
                userRequest.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", user.toString());
                    }

                    @Override
                    public void onFailure(HttpException e, Response<User> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                });
                liteHttp.executeAsync(userRequest);

                break;
            case 7:
                // 7. Custom Data Parser

                class JSONParser extends MemeoryDataParser<JSONObject> {

                    public JSONParser(AbstractRequest<JSONObject> request) {
                        super(request);
                    }

                    @Override
                    protected JSONObject parseNetStream(InputStream stream, long totalLength, String charSet,
                                                        String cacheDir) throws IOException {
                        return streamToJson(stream, totalLength, charSet);
                    }

                    @Override
                    protected JSONObject parseDiskCache(InputStream stream, long length) throws IOException {
                        return streamToJson(stream, length, charSet);
                    }

                    protected JSONObject streamToJson(InputStream is, long length, String charSet) throws IOException {
                        String json = streamToString(is, length, charSet);
                        try {
                            return new JSONObject(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }
                class JSONObjectRequest extends AbstractRequest<JSONObject> {
                    JSONParser jsonParser;

                    public JSONObjectRequest(String uri) {
                        super(uri);
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public DataParser<JSONObject> getDataParser() {
                        if (jsonParser == null) {
                            jsonParser = new JSONParser(this);
                        }
                        return jsonParser;
                    }
                }

                liteHttp.executeAsync(new JSONObjectRequest(userGet).setHttpListener(new HttpListener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject, Response<JSONObject> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                          "Custom JSONObject Parser:\n" + jsonObject.toString());
                    }

                    @Override
                    public void onFailure(HttpException e, Response<JSONObject> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }));
                break;

            case 8:
                // 8. Replace Json Library

                // first, builder a java class that used FastJson
                class FastJson extends Json {
                    @Override
                    public String toJson(Object src) {
                        Log.i(TAG, "FastJson parse object to json string");
                        return JSON.toJSONString(src);
                    }

                    @Override
                    public <T> T toObject(String json, Class<T> claxx) {
                        Log.i(TAG, "FastJson parse json string to Object");
                        return JSON.parseObject(json, claxx);
                    }

                    @Override
                    public <T> T toObject(byte[] bytes, Class<T> claxx) {
                        Log.i(TAG, "FastJson parse bytes to Object");
                        return JSON.parseObject(bytes, claxx);
                    }
                }

                // then, set new json framework.
                Json.set(new FastJson());

                // json model convert used #FastJson
                liteHttp.executeAsync(new JsonAbsRequest<User>(userGet) {});

                // json model convert used #FastJson
                liteHttp.performAsync(new StringRequest(userGet).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        User u = Json.get().toObject(s, User.class);
                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                          "FastJson handle this: \n" + u.toString());
                        Json.setDefault();
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                          "FastJson handle this: \n" + e.toString());
                        Json.setDefault();
                    }
                }));
                break;
            case 9:
                // 9. File Upload
                break;
            case 10:
                // 10. File/Bitmap Download

                final ProgressDialog pd = new ProgressDialog(this);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setIndeterminate(false);
                pd.show();
                // load and show bitmap
                liteHttp.executeAsync(
                        new BitmapRequest(picUrl).setHttpListener(new HttpListener<Bitmap>(true, true, false) {
                            @Override
                            public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                                pd.setMax((int) total);
                                pd.setProgress((int) len);
                            }

                            @Override
                            public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                                pd.dismiss();
                                AlertDialog.Builder b = HttpUtil.dialogBuilder(activity, "LiteHttp2.0", "");
                                ImageView iv = new ImageView(activity);
                                iv.setImageBitmap(bitmap);
                                b.setView(iv);
                                b.show();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<Bitmap> response) {
                                pd.dismiss();
                                HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                            }
                        }));

                // download a file to sdcard.
                liteHttp.executeAsync(new FileRequest(picUrl).setFileSavePath("sdcard/aaa.jpg"));
                break;
            case 11:
                // 11. Disable Some Network

                config = liteHttp.getConfig();
                // disable network need context
                config.context = activity.getApplicationContext();
                // disable mobile(2G/3G/4G..) network
                config.disableNetworkFlags = HttpConfig.FLAG_NET_DISABLE_MOBILE;
                // note : finally you have to set config to make it effective.
                liteHttp.setNewConfig(config);
                needRestore = true;

                liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", s);
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());

                        // restore network
                        liteHttp.getConfig().disableNetworkFlags = HttpConfig.FLAG_NET_DISABLE_NONE;
                        needRestore = false;
                    }
                }));
                break;

            case 12:
                // 12. Traffic/Time Statistics

                // turn on
                liteHttp.getConfig().doStatistics = true;
                // see detail
                liteHttp.executeAsync(new FileRequest(picUrl).setHttpListener(new HttpListener<File>() {
                    @Override
                    public void onSuccess(File file, Response<File> response) {
                        String msg = "This request take time:" + response.getUseTime()
                                     + ", readed length:" + response.getReadedLength();
                        msg += "  Global " + liteHttp.getStatisticsInfo();
                        HttpUtil.showTips(activity, "LiteHttp2.0", msg);

                    }

                    @Override
                    public void onFailure(HttpException e, Response<File> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }));
                break;
            case 13:
                // 13. Retry/Redirect

                // maximum retry times
                liteHttp.getConfig().defaultMaxRetryTimes = 2;
                // maximum redirect times
                liteHttp.getConfig().defaultMaxRedirectTimes = 5;
                // note : finally you have to set config to make it effective.
                liteHttp.setNewConfig(liteHttp.getConfig());
                // test it
                liteHttp.executeAsync(new StringRequest(redirectUrl).setHttpListener(new HttpListener<String>() {

                    @Override
                    public void onRedirect(AbstractRequest<String> request, int max, int times) {
                        Toast.makeText(activity, "Redirect max num: " + max + " , times: " + times
                                                 + "\n GO-TO: " + request.getUri(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRetry(AbstractRequest<String> request, int max, int times) {
                        Toast.makeText(activity, "Retry Now! max num: " + max + " , times: " + times
                                , Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", s);
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }));
                break;

            case 14:
                // 14. Best Practices of Exception Handling
                liteHttp.executeAsync(
                        new StringRequest(url).setMethod(HttpMethods.Trace).setHttpListener(new HttpListener<String>() {
                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", "Execute Success");
                            }

                            @Override
                            public void onFailure(HttpException exception, Response<String> response) {
                                new HttpExceptionHandler() {
                                    @Override
                                    protected void onClientException(HttpClientException e, ClientException type) {
                                        switch (e.getExceptionType()) {
                                            case UrlIsNull:
                                                break;
                                            case ContextNeeded:
                                                // some action need app context
                                                break;
                                            case NetworkOnMainThread:
                                                break;
                                            case PermissionDenied:
                                                break;
                                            case SomeOtherException:
                                                break;
                                        }
                                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                                          "Client Exception:\n" + e.toString());
                                    }

                                    @Override
                                    protected void onNetException(HttpNetException e, NetException type) {
                                        switch (e.getExceptionType()) {
                                            case NetworkNotAvilable:
                                                break;
                                            case NetworkUnstable:
                                                // maybe retried but fail
                                                break;
                                            case NetworkDisabled:
                                                break;
                                            default:
                                                break;
                                        }
                                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                                          "Network Exception:\n" + e.toString());
                                    }

                                    @Override
                                    protected void onServerException(HttpServerException e, ServerException type,
                                                                     HttpStatus status) {
                                        switch (e.getExceptionType()) {
                                            case ServerInnerError:
                                                // status code 5XX error
                                                break;
                                            case ServerRejectClient:
                                                // status code 4XX error
                                                break;
                                            case RedirectTooMuch:
                                                break;
                                            default:
                                                break;
                                        }
                                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                                          "Server Exception:\n" + e.toString());
                                    }
                                }.handleException(exception);
                            }
                        }));
                break;
            case 15:
                // 15. Best Practices of Cancel Request
                StringRequest stringRequest = new StringRequest(url).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onCancel(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                          "Request Canceld:" + response.getRequest().isCancelled()
                                          + "\nTask Interrupted:" + response.getRequest().isInterrupted());
                    }
                });
                FutureTask futureTask = liteHttp.performAsync(stringRequest);
                SystemClock.sleep(200);

                if (count++ % 2 == 0) {
                    // one correct way is cancel this request
                    stringRequest.cancel();
                } else {
                    // other correct way is interrupt this thread or task.
                    futureTask.cancel(true);
                }
                break;
            case 16:
                // 16. POST Multi-Form Data
                break;
            case 17:
                // 17. Concurrent and Scheduling

                HttpConfig newConfig = liteHttp.getConfig();
                // only one task can be executed at the same time
                newConfig.concurrentSize = 1;
                // at most two tasks be hold in waiting queue at the same time
                newConfig.waitingQueueSize = 2;
                // the last waiting task executed first
                newConfig.schedulePolicy = SchedulePolicy.LastInFirstRun;
                // when task more than 3(1+2), new task will be discard.
                newConfig.overloadPolicy = OverloadPolicy.DiscardNew;

                // note : finally you have to set config to make it effective.
                liteHttp.setNewConfig(newConfig);
                needRestore = true;

                // executed order: 0 -> 2 -> 1 , by [DiscardNew Policy] 3 will be discard.
                for (int i = 0; i < 4; i++) {
                    liteHttp.executeAsync(new StringRequest(url).setTag(i));
                }

                break;
            case 18:
                // 18. Detail of Config

                break;
            case 19:
                // 19. The Use of Annotation

                @HttpUri(userGet)
                @HttpMethod(HttpMethods.Get)
                @HttpTag("custom tag")
                @HttpID(7)
                @HttpCacheMode(CacheMode.CacheFirst)
                @HttpCacheExpire(10 * 60 * 1000)
                @HttpCacheKey("cache-name-by-myself")
                @HttpCharSet("UTF-8")
                @HttpMaxRetry(3)
                @HttpMaxRedirect(5)
                class UserAnnoParam implements HttpParamModel {
                    private static final long serialVersionUID = 2931033825895021716L;
                    public long id = 110;
                    private String key = "aes";
                }

                UserAnnoParam p = new UserAnnoParam();
                liteHttp.executeAsync(new JsonAbsRequest<User>(p) {}.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        HttpUtil.showTips(activity, "UserAnnoParam", user.toString());
                    }
                }));


                // rich param
                @HttpUri(userGet)
                class UserRichParam extends HttpRichParamModel<User> {
                    private static final long serialVersionUID = -785053238885177613L;

                    @Override
                    public HttpListener<User> createHttpListener() {
                        return new HttpListener<User>() {
                            @Override
                            public void onSuccess(User user, Response<User> response) {
                                HttpUtil.showTips(activity, "UserRichParam", user.toString());
                            }
                        };
                    }
                }
                liteHttp.execute(new JsonAbsRequest(new UserRichParam()) {});
                break;

            case 20:
                // 20. Multi Cache Mechanism
                JsonRequest<User> cacheRequest = new JsonRequest<User>(userGet, User.class);
                cacheRequest.setCacheMode(CacheMode.CacheFirst);
                cacheRequest.setCacheExpire(20, TimeUnit.SECONDS);
                cacheRequest.setCacheKey(userGet + "-20");
                liteHttp.executeAsync(cacheRequest);
                break;
            case 21:
                // 21. CallBack Mechanism

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.show();

                /**
                 * global http listener for all request.
                 */
                GlobalHttpListener globalHttpListener = new GlobalHttpListener() {
                    @Override
                    public void onStart(AbstractRequest<?> request) {
                        HttpLog.i(TAG, "Global, request start ...");
                    }

                    @Override
                    public void onSuccess(Object data, Response<?> response) {
                        HttpLog.i(TAG, "Global, request success ..." + data);
                    }

                    @Override
                    public void onFailure(HttpException e, Response<?> response) {
                        HttpLog.i(TAG, "Global, request failure ..." + e);
                    }

                    @Override
                    public void onCancel(Object data, Response<?> response) {
                        HttpLog.i(TAG, "Global, request cancel ..." + data);
                    }
                };

                /**
                 * http listener for current reuqest:
                 *
                 * runOnUiThread = true;
                 * readingNotify = true;
                 * uploadingNotify = true;
                 */
                HttpListener<Bitmap> httpListener = new HttpListener<Bitmap>(true, true, true) {
                    @Override
                    public void onStart(AbstractRequest<Bitmap> request) {
                        HttpLog.i(TAG, " Listener, request start ...");
                    }

                    @Override
                    public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                        HttpLog.i(TAG, " Listener, request success ...");
                        progressDialog.dismiss();
                        ImageView iv = new ImageView(activity);
                        iv.setImageBitmap(bitmap);
                        HttpUtil.dialogBuilder(activity, "LiteHttp2.0", "")
                                .setView(iv).show();
                    }

                    @Override
                    public void onFailure(HttpException e, Response<Bitmap> response) {
                        HttpLog.i(TAG, " Listener, request failure ...");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancel(Bitmap bitmap, Response<Bitmap> response) {
                        HttpLog.i(TAG, " Listener, request cancel ...");
                    }

                    @Override
                    public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                        HttpLog.i(TAG, " Listener, request loading  ...");
                        progressDialog.setMax((int) total);
                        progressDialog.setProgress((int) len);
                    }

                    @Override
                    public void onUploading(AbstractRequest<Bitmap> request, long total, long len) {
                        HttpLog.i(TAG, " Listener, request upLoading  ...");
                    }

                    @Override
                    public void onRetry(AbstractRequest<Bitmap> request, int max, int times) {
                        HttpLog.i(TAG, " Listener, request retry ...");
                    }

                    @Override
                    public void onRedirect(AbstractRequest<Bitmap> request, int max, int times) {
                        HttpLog.i(TAG, " Listener, request redirect ...");
                    }
                };

                /**
                 * new http listener for current request:
                 *
                 * runOnUiThread = false;
                 * readingNotify = false;
                 * uploadingNotify = false;
                 *
                 * actually you can set a series of http listener for one http request.
                 */
                HttpListener<Bitmap> newHttpListener = new HttpListener<Bitmap>(false, false, false) {
                    @Override
                    public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                        HttpLog.i(TAG, "New Listener, request success ...");
                    }

                    @Override
                    public void onFailure(HttpException e, Response<Bitmap> response) {
                        HttpLog.i(TAG, "New Listener, request failure ...");
                    }

                    @Override
                    public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                        HttpLog.i(TAG, "New Listener, request loading  ...");
                    }
                };

                // the correct way to set global http listener for all request.
                liteHttp.getConfig().globalHttpListener = globalHttpListener;
                // create a bitmap request.
                BitmapRequest bitmapRequest = new BitmapRequest(picUrl);
                // correct way to set the initial and only http listener
                bitmapRequest.setHttpListener(httpListener);
                // correct way to set another listener
                bitmapRequest.setLinkedHttpListener(newHttpListener);
                // correct way to set the third listener
                bitmapRequest.setLinkedHttpListener(newHttpListener);

                //load and show bitmap
                liteHttp.executeAsync(bitmapRequest);

                break;
            case 22:
                // 22. Best Practices of SmartExecutor
                SmartExecutor smartExecutor = new SmartExecutor(2, 2);
                smartExecutor.setOverloadPolicy(OverloadPolicy.DiscardOld);
                smartExecutor.setSchedulePolicy(SchedulePolicy.LastInFirstRun);
                for (int i = 0; i < 6; i++) {
                    final int j = i;
                    smartExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            HttpLog.i(TAG, j + " task running");
                            SystemClock.sleep(j * 200);
                        }
                    });
                }
                break;
        }
    }


    /**
     * will be converted to: http://...?id=168&key=md5
     */
    class UserParam implements HttpParamModel {
        // static final property will be ignored.
        private static final long serialVersionUID = 2451716801614350437L;

        public UserParam(long id, String key) {
            this.id = id;
            this.key = key;
        }

        public long id;
        private String key;
    }

    /**
     * response string will be converted to this model
     *
     * WTF! fastjson need static class...
     */
    static class User extends BaseModel {

        private int age;
        protected String name;

        @Override
        public String toString() {
            return "User{" + "age=" + age + ", name='" + name + '\'' + '}';
        }
    }

    static abstract class BaseModel {}

    static class UserRequest extends JsonAbsRequest<User> {

        public UserRequest(String url) {
            super(url);
        }

        protected UserRequest(HttpParamModel model) {
            super(model);
        }
    }
}
