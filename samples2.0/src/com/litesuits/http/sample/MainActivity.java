package com.litesuits.http.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.annotation.*;
import com.litesuits.http.concurrent.OverloadPolicy;
import com.litesuits.http.concurrent.SchedulePolicy;
import com.litesuits.http.concurrent.SmartExecutor;
import com.litesuits.http.custom.CustomJSONParser;
import com.litesuits.http.custom.FastJson;
import com.litesuits.http.custom.MyHttpExceptHandler;
import com.litesuits.http.data.Json;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.data.StatisticsInfo;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.GlobalHttpListener;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.model.User;
import com.litesuits.http.model.api.RichParam;
import com.litesuits.http.model.api.UserParam;
import com.litesuits.http.request.*;
import com.litesuits.http.request.content.*;
import com.litesuits.http.request.content.multi.*;
import com.litesuits.http.request.param.*;
import com.litesuits.http.request.query.JsonQueryBuilder;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    protected String TAG = MainActivity.class.getSimpleName();
    protected ListView mListview;
    protected BaseAdapter mAdapter;
    protected static LiteHttp liteHttp;
    protected Activity activity = null;
    protected int count = 0;
    private boolean needRestore;

    public static final String url = "http://baidu.com";
    public static final String httpsUrl = "https://baidu.com";
    //public static final String httpsUrl = "https://www.thanku.love";
    public static final String uploadUrl = "http://192.168.8.105:8080/upload";
    public static final String userUrl = "http://litesuits.com/mockdata/user_get";
    public static final String loginUrl = "http://litesuits.com/mockdata/user_get";
    public static final String picUrl = "http://pic.33.la/20140403sj/1638.jpg";
    public static final String redirectUrl = "http://www.baidu.com/link?url=Lqc3GptP8u05JCRDsk0jqsAvIZh9WdtO_RkXYMYRQEm";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        activity = this;
        initViews();
        initLiteHttp();
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
     * 单例 keep an singleton instance of litehttp
     */
    private void initLiteHttp() {
        if (liteHttp == null) {
            HttpConfig config = new HttpConfig(activity) // configuration quickly
                    .setDebugged(true)                   // log output when debugged
                    .setDetectNetwork(true)              // detect network before connect
                    .setDoStatistics(true)               // statistics of time and traffic
                    .setUserAgent("Mozilla/5.0 (...)")   // set custom User-Agent
                    .setTimeOut(10000, 10000);             // connect and socket timeout: 10s
            liteHttp = LiteHttp.newApacheHttpClient(config);
        } else {
            liteHttp.getConfig()                        // configuration directly
                    .setDebugged(true)                  // log output when debugged
                    .setDetectNetwork(true)             // detect network before connect
                    .setDoStatistics(true)              // statistics of time and traffic
                    .setUserAgent("Mozilla/5.0 (...)")  // set custom User-Agent
                    .setTimeOut(10000, 10000);            // connect and socket timeout: 10s
        }
    }

    /**
     * <item>0. Quickly Configuration</item>
     * <item>1. Asynchronous Request</item>
     * <item>2. Synchronous Request</item>
     * <item>3. Simple Synchronous Request</item>
     * <item>4. Exception Thrown Request</item>
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
     * <item>19. Usage of Annotation</item>
     * <item>20. Multi Cache Mechanism</item>
     * <item>21. CallBack Mechanism</item>
     * <item>22. Best Practice: SmartExecutor</item>
     * <item>23. Best Practice: Auto-Conversion of Complex Model</item>
     * <item>24. Best Practice: HTTP Rich Param Model</item>
     */


    //@HttpUri("http://baidu.com")
    //@HttpCacheMode(CacheMode.CacheFirst)
    //@HttpCacheExpire(value = 7, unit = TimeUnit.DAYS)
    //class A extends JsonRequest<User> {
    //    private String name;
    //    private String password;
    //
    //    public A(String url, Type resultType) {
    //        super(url, resultType);
    //    }
    //
    //    @Override
    //    public HttpBody getHttpBody() {
    //        if (getHttpBody() == null) {
    //            return new UrlEncodedFormBody(getQueryBuilder().buildPrimaryPairSafely(this));
    //        }
    //        return super.getHttpBody();
    //    }
    //
    //    public HttpBody buildHttpBody() {
    //        return new UrlEncodedFormBody(getQueryBuilder().buildPrimaryPairSafely(this));
    //    }
    //}
    private void clickTestItem(final int which) {

        // restore http config
        if (needRestore) {
            Json.setDefault();
            liteHttp.getConfig().restoreToDefault();
            needRestore = false;
        }

        switch (which) {
            case 0:
                initLiteHttp();
                HttpUtil.showTips(activity, "LiteHttp2.0", "Init Config Success!");
                break;

            case 1:
                // 1. Asynchronous Request

                // 1.0 init request
                final StringRequest request = new StringRequest(url).setHttpListener(
                        new HttpListener<String>() {
                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", s);
                                response.printInfo();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                            }
                        }
                );

                // 1.1 execute async, nothing returned.
                liteHttp.executeAsync(request);

                // 1.2 perform async, future task returned.
                FutureTask<String> task = liteHttp.performAsync(request);
                task.cancel(true);
                break;

            case 2:
                // 2. Synchronous Request

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 2.0 execute: return fully response
                        Response<User> response = liteHttp.execute(new RichParam(1, "a"));
                        User user = response.getResult();
                        Log.i(TAG, "User: " + user);

                        // 2.1 perform: return java model directly
                        User user2 = liteHttp.perform(new JsonAbsRequest<User>(userUrl) {});
                        Log.i(TAG, "User: " + user2);

                        // 2.2 return data directly, handle result on current thread(当前主线程处理)
                        Bitmap bitmap = liteHttp.perform(new BitmapRequest(picUrl)
                                .setHttpListener(new HttpListener<Bitmap>(false, true, true) {

                                    @Override
                                    public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                                        // down loading notification ...
                                        Log.i(TAG, "total: " + total + "  len: " + len);
                                    }

                                    @Override
                                    public void onUploading(AbstractRequest<Bitmap> request, long total, long len) {
                                        // up loading notification...
                                    }
                                }));
                        bitmap.recycle();

                        // 2.3 handle result on UI thread(主线程处理，注意HttpListener默认是在主线程回调)
                        liteHttp.execute(new StringRequest(url).setHttpListener(
                                new HttpListener<String>() {
                                    @Override
                                    public void onSuccess(String data, Response<String> response) {
                                        HttpUtil.showTips(activity, "LiteHttp2.0", data);
                                    }

                                    @Override
                                    public void onFailure(HttpException e, Response<String> response) {
                                        HttpUtil.showTips(activity, "LiteHttp2.0", e.getMessage());
                                    }
                                }
                        ));
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
                        Log.i(TAG, "get result: " + result);
                        publishProgress("Simple Get String: \n" + result);

                        // 3.1 simple post and publish
                        result = liteHttp.post(new StringRequest(httpsUrl));
                        Log.i(TAG, "post result: " + result);
                        publishProgress("Simple POST String: \n" + result);

                        // 3.2 simple post and publish
                        User user = liteHttp.get(userUrl, User.class);
                        Log.i(TAG, "user: " + user);
                        publishProgress("Simple Get User: \n" + user);

                        // 3.3 simple head and return
                        NameValuePair[] headers = liteHttp.head(new StringRequest(url));
                        return headers;
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        Toast.makeText(activity, "content length:" + values[0].length(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onPostExecute(NameValuePair[] nameValuePairs) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(nameValuePairs));
                    }
                }.execute();
                break;

            case 4:
                // 4. Exception Thrown Request

                Runnable run = new Runnable() {
                    @Override
                    public void run() {

                        // http scheme error
                        try {
                            Response response = liteHttp.executeOrThrow(new BytesRequest("haha://hehe"));
                            // do something...
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }

                        // java model translate error
                        try {
                            User user = liteHttp.performOrThrow(new JsonAbsRequest<User>("http://thanku.love") {});
                        } catch (final HttpException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpUtil.showTips(activity, "LiteHttp2.0", e.getMessage());
                                }
                            });
                        }

                    }
                };
                SmartExecutor executorOne = new SmartExecutor();
                executorOne.execute(run);

                break;
            case 5:
                // 5. HTTPS Reqeust

                liteHttp.executeAsync(new StringRequest(httpsUrl).setHttpListener(
                        new HttpListener<String>() {
                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", " Read Content Length: " + s.length());
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(e.getStackTrace()));
                            }
                        }
                ));

                break;
            case 6:
                // 6. Automatic Model Conversion
                // build as http://{userUrl}?id=168&key=md5
                liteHttp.executeAsync(new StringRequest(userUrl, new UserParam(18, "md5"))
                        .setHttpListener(new HttpListener<String>() {
                            @Override
                            public void onSuccess(String data, Response<String> response) {
                                Log.i(TAG, "USER: " + data);
                            }
                        }));

                /**
                 * Request Model : json string translate to user object
                 */
                class UserRequest extends JsonAbsRequest<User> {
                    public UserRequest(String url, HttpParamModel param) {
                        super(url, param);
                    }
                }
                // build as http://{userUrl}?id=168&key=md5
                UserRequest userRequest = new UserRequest(userUrl, new UserParam(18, "md5"));
                userRequest.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        // data has been translated to user object
                        HttpUtil.showTips(activity, "LiteHttp2.0", user.toString());
                    }
                });
                liteHttp.executeAsync(userRequest);

                break;
            case 7:
                // 7. Custom Data Parser

                JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(userUrl, JSONObject.class);
                jsonRequest.setDataParser(new CustomJSONParser());
                liteHttp.executeAsync(jsonRequest.setHttpListener(new HttpListener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject, Response<JSONObject> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                "Custom JSONObject Parser:\n" + jsonObject.toString());
                    }
                }));
                break;

            case 8:
                // 8. Replace Json Library

                // first, set new json framework instance. then, over.
                Json.set(new FastJson());
                String uj = "{\"api\":\"com.xx.get.userinfo\",\"v\":\"1.0\",\"code\":200,\"message\":\"success\",\"data\":{\"age\":18,\"name\":\"qingtianzhu\",\"girl_friends\":[\"xiaoli\",\"fengjie\",\"lucy\"]}}";
                User u11 = Json.get().toObject(uj, User.class);
                System.out.println("User:" + u11);
                // json model convert used #FastJson
                liteHttp.executeAsync(new JsonAbsRequest<User>(userUrl) {}.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        response.printInfo();
                        HttpUtil.showTips(activity, "LiteHttp2.0", "FastJson handle this: \n" + user.toString());
                        needRestore = true;
                    }
                }));

                // json model convert used #FastJson
                liteHttp.performAsync(new StringRequest(userUrl).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        User u = Json.get().toObject(s, User.class);
                        Toast.makeText(activity, u.toString(), Toast.LENGTH_LONG).show();
                        needRestore = true;
                    }

                    @Override
                    public void onEnd(Response<String> response) {
                        needRestore = true;
                    }
                }));
                break;
            case 9:
                // 9. File Upload
                final ProgressDialog upProgress = new ProgressDialog(this);
                upProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                upProgress.setIndeterminate(false);
                upProgress.show();

                StringRequest uploadRequest = new StringRequest(uploadUrl);

                uploadRequest.setMethod(HttpMethods.Post)
                             .setHttpBody(new FileBody(new File("/sdcard/aaa.jpg")))
                             .setHttpListener(new HttpListener<String>(true, false, true) {
                                 @Override
                                 public void onSuccess(String s, Response<String> response) {
                                     upProgress.dismiss();
                                     HttpUtil.showTips(activity, "Upload Success", s);
                                     response.printInfo();
                                 }

                                 @Override
                                 public void onFailure(HttpException e, Response<String> response) {
                                     upProgress.dismiss();
                                     HttpUtil.showTips(activity, "Upload Failed", e.toString());
                                 }

                                 @Override
                                 public void onUploading(AbstractRequest<String> request, long total, long len) {
                                     upProgress.setMax((int) total);
                                     upProgress.setProgress((int) len);
                                 }
                             });
                liteHttp.executeAsync(uploadRequest);
                break;
            case 10:
                // 10. File/Bitmap Download

                final ProgressDialog downProgress = new ProgressDialog(this);
                downProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                downProgress.setIndeterminate(false);
                downProgress.show();
                // load and show bitmap
                liteHttp.executeAsync(
                        new BitmapRequest(picUrl).setHttpListener(new HttpListener<Bitmap>(true, true, false) {
                            @Override
                            public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                                downProgress.setMax((int) total);
                                downProgress.setProgress((int) len);
                                HttpLog.i(TAG, total + "  total   " + len + " len");
                            }

                            @Override
                            public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                                downProgress.dismiss();
                                AlertDialog.Builder b = HttpUtil.dialogBuilder(activity, "LiteHttp2.0", "");
                                ImageView iv = new ImageView(activity);
                                iv.setImageBitmap(bitmap);
                                b.setView(iv);
                                b.show();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<Bitmap> response) {
                                downProgress.dismiss();
                                HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                            }
                        }));

                // download a file to sdcard.
                liteHttp.executeAsync(new FileRequest(picUrl, "sdcard/aaa.jpg"));
                break;
            case 11:
                // 11. Disable Some Network

                HttpConfig config = liteHttp.getConfig();
                // must set context
                config.setContext(activity);
                // disable mobile(2G/3G/4G) and wifi network
                config.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_MOBILE | HttpConfig.FLAG_NET_DISABLE_WIFI);

                liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", s);
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }));

                needRestore = true;
                break;

            case 12:
                // 12. Traffic/Time Statistics

                // turn on
                liteHttp.getConfig().setDoStatistics(true);
                // see detail
                liteHttp.executeAsync(new FileRequest(picUrl).setHttpListener(new HttpListener<File>() {
                    @Override
                    public void onSuccess(File file, Response<File> response) {
                        String msg = "This request take time:" + response.getUseTime()
                                     + ", readed length:" + response.getReadedLength();
                        msg += "  Global " + liteHttp.getStatisticsInfo();
                        HttpUtil.showTips(activity, "LiteHttp2.0", msg);

                        response.getRedirectTimes();  // 重定向的次数
                        response.getRetryTimes();     // 重试的次数
                        response.getUseTime();        // 耗时
                        response.getContentLength();  // header中的数据长度（Content-Length）
                        response.getReadedLength();   // 实际读取的数据长度

                        StatisticsInfo sta = liteHttp.getStatisticsInfo();
                        sta.getConnectTime();         // litehttp 实例化后所有请求耗时累计
                        sta.getDataLength();          // litehttp 实例化后读取数据长度累计
                    }

                    @Override
                    public void onFailure(HttpException e, Response<File> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }));
                break;
            case 13:
                // 13. Retry/Redirect

                // default retry times
                liteHttp.getConfig().setDefaultMaxRetryTimes(2);
                // default redirect times
                liteHttp.getConfig().setDefaultMaxRedirectTimes(4);
                // default retry waitting time
                liteHttp.getConfig().setForRetry(1500, false);

                // make request
                StringRequest redirect = new StringRequest(redirectUrl)
                        .setMaxRetryTimes(1) // maximum retry times
                        .setMaxRedirectTimes(5) // maximum redirect times
                        .setHttpListener(new HttpListener<String>() {

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
                                HttpUtil.showTips(activity, "LiteHttp2.0", "Content Length: " + s.length());
                            }
                        });

                liteHttp.executeAsync(redirect);
                break;

            case 14:
                // 14. Best Practices of Exception Handling

                class MyHttpListener<T> extends HttpListener<T> {
                    private Activity activity;

                    public MyHttpListener(Activity activity) {
                        this.activity = activity;
                    }

                    // disable listener when activity is null or be finished.
                    @Override
                    public boolean disableListener() {
                        return activity == null || activity.isFinishing();
                    }

                    // handle by this by call super.onFailure()
                    @Override
                    public void onFailure(HttpException e, Response response) {
                        // handle exception
                        new MyHttpExceptHandler(activity).handleException(e);
                    }
                }

                liteHttp.executeAsync(new StringRequest("httpa://invalid-url")
                        .setHttpListener(new MyHttpListener<String>(activity) {
                            @Override
                            public void onFailure(HttpException e, Response response) {
                                // handle by this by call super.onFailure()
                                super.onFailure(e, response);
                                // 通过调用父类的处理方法，来调用 MyHttpExceptHandler 来处理异常。
                            }
                        }));
                break;
            case 15:
                // 15. Best Practices of Cancel Request
                final boolean isInterrupted = count++ % 2 != 0;
                StringRequest stringRequest = new StringRequest(redirectUrl)
                        .setHttpListener(new HttpListener<String>() {
                            @Override
                            public void onCancel(String s, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0",
                                        "Request Canceld: " + response.getRequest().isCancelled()
                                        + "\nTask Interrupted: " + isInterrupted);
                            }
                        });
                FutureTask futureTask = liteHttp.performAsync(stringRequest);
                SystemClock.sleep(100);
                if (!isInterrupted) {
                    // one correct way is cancel this request
                    stringRequest.cancel();
                } else {
                    // other correct way is interrupt this thread or task.
                    futureTask.cancel(true);
                }
                break;
            case 16:
                // 16. POST Multi-Form Data

                final ProgressDialog postProgress = new ProgressDialog(this);
                postProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                postProgress.setIndeterminate(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("POST DATA TEST");
                String[] array = new String[]{
                        "字符串上传",
                        "UrlEncodedForm上传",
                        "对象自动转JSON上传",
                        "对象序列化后上传",
                        "字节上传",
                        "单文件上传",
                        "单输入流上传",
                        "多文件（表单）上传"
                };
                //String[] array = getResources().getStringArray(R.array.http_test_post);

                final StringRequest postRequest = new StringRequest(uploadUrl)
                        .setMethod(HttpMethods.Post)
                        .setHttpListener(new HttpListener<String>(true, false, true) {
                            @Override
                            public void onStart(AbstractRequest<String> request) {
                                super.onStart(request);
                                postProgress.show();
                            }

                            @Override
                            public void onUploading(AbstractRequest<String> request, long total, long len) {
                                postProgress.setMax((int) total);
                                postProgress.setProgress((int) len);
                            }

                            @Override
                            public void onEnd(Response<String> response) {
                                postProgress.dismiss();
                                if (response.isConnectSuccess()) {
                                    HttpUtil.showTips(activity, "Upload Success", response.getResult() + "");
                                } else {
                                    HttpUtil.showTips(activity, "Upload Failure", response.getException() + "");
                                }
                                response.printInfo();
                            }
                        });

                builder.setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                postRequest.setHttpBody(new StringBody("hello lite: 你好，Lite！"));
                                break;
                            case 1:
                                LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
                                pList.add(new NameValuePair("key1", "value-haha"));
                                pList.add(new NameValuePair("key2", "value-hehe"));
                                postRequest.setHttpBody(new UrlEncodedFormBody(pList));
                                break;
                            case 2:
                                postRequest.setHttpBody(new JsonBody(new UserParam(168, "haha-key")));
                                break;
                            case 3:
                                ArrayList<String> list = new ArrayList<String>();
                                list.add("a");
                                list.add("b");
                                list.add("c");
                                postRequest.setHttpBody(new SerializableBody(list));
                                break;
                            case 4:
                                postRequest.setHttpBody(new ByteArrayBody(new byte[]{1, 2, 3, 4, 5, 15, 18, 127}));
                                break;
                            case 5:
                                postRequest.setHttpBody(new FileBody(new File("/sdcard/litehttp.txt")));
                                break;
                            case 6:
                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(new File("/sdcard/aaa.jpg"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                postRequest.setHttpBody(new InputStreamBody(fis));
                                break;
                            case 7:
                                fis = null;
                                try {
                                    fis = new FileInputStream(new File("/sdcard/litehttp.txt"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                MultipartBody body = new MultipartBody();
                                body.addPart(new StringPart("key1", "hello"));
                                body.addPart(new StringPart("key2", "很高兴见到你", "utf-8", null));
                                body.addPart(new BytesPart("key3", new byte[]{1, 2, 3}));
                                body.addPart(new FilePart("pic", new File("/sdcard/aaa.jpg"), "image/jpeg"));
                                body.addPart(new InputStreamPart("litehttp", fis, "litehttp.txt", "text/plain"));
                                postRequest.setHttpBody(body);
                                break;
                        }

                        liteHttp.executeAsync(postRequest);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                break;
            case 17:
                // 17. Concurrent and Scheduling

                HttpConfig httpConfig = liteHttp.getConfig();
                // only one task can be executed at the same time
                httpConfig.setConcurrentSize(1);
                // at most two tasks be hold in waiting queue at the same time
                httpConfig.setWaitingQueueSize(2);
                // the last waiting task executed first
                httpConfig.setSchedulePolicy(SchedulePolicy.LastInFirstRun);
                // when task more than 3(current = 1, waiting = 2), new task will be discard.
                httpConfig.setOverloadPolicy(OverloadPolicy.DiscardCurrentTask);

                // note : restore config to default, next click.
                needRestore = true;

                // by [DiscardCurrentTask Policy] the last will be discard.
                for (int i = 0; i < 4; i++) {
                    liteHttp.executeAsync(new StringRequest(url).setTag(i));
                }
                // submit order : 0 -> 1 -> 2 -> 3
                // task 0 is executing, 1 and 2 is in waitting queue, 3 was discarded.
                // real executed order: 0 -> 2 -> 1

                break;
            case 18:
                // 18. Detail of Configuration

                // init common headers for all request
                List<NameValuePair> headers = new ArrayList<NameValuePair>();
                headers.add(new NameValuePair("cookies", "this is cookies"));
                headers.add(new NameValuePair("custom-key", "custom-value"));

                HttpConfig newConfig = new HttpConfig(activity);

                // app context(be used to detect network and get app files path)
                newConfig.setContext(activity);
                // the log is turn on when debugged is true
                newConfig.setDebugged(true);
                // set user-agent
                newConfig.setUserAgent("Mozilla/5.0");
                // set global http listener to all request
                newConfig.setGlobalHttpListener(null);
                // set global scheme and host to all request.
                newConfig.setGlobalSchemeHost("http://litesuits.com/");
                // common headers will be set to all request
                newConfig.setCommonHeaders(headers);
                // set default cache path to all request
                newConfig.setDefaultCacheDir(Environment.getExternalStorageDirectory() + "/a-cache");
                // set default cache expire time to all request
                newConfig.setDefaultCacheExpireMillis(30 * 60 * 1000);
                // set default cache mode to all request
                newConfig.setDefaultCacheMode(CacheMode.NetFirst);
                // set default charset to all request
                newConfig.setDefaultCharSet("utf-8");
                // set default http method to all request
                newConfig.setDefaultHttpMethod(HttpMethods.Get);
                // set default maximum redirect-times to all request
                newConfig.setDefaultMaxRedirectTimes(5);
                // set default maximum retry-times to all request
                newConfig.setDefaultMaxRetryTimes(1);
                // set defsult model query builder to all request
                newConfig.setDefaultModelQueryBuilder(new JsonQueryBuilder());
                // whether to detect network before conneting.
                newConfig.setDetectNetwork(true);
                // disable some network
                newConfig.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_NONE);
                // whether open the traffic & time statistics
                newConfig.setDoStatistics(true);
                // set connect timeout: 10s,  socket timeout: 10s
                newConfig.setTimeOut(10000, 10000);
                // socket buffer size: 4096
                newConfig.setSocketBufferSize(4096);
                // if the network is unstable, wait 3000 milliseconds then start retry.
                newConfig.setForRetry(3000, false);
                // set maximum size of memory cache space
                newConfig.setMaxMemCacheBytesSize(1024 * 300);
                // maximum number of concurrent tasks(http-request) at the same time
                newConfig.setConcurrentSize(3);
                // maximum number of waiting tasks(http-request) at the same time
                newConfig.setWaitingQueueSize(100);
                // set overload policy of thread pool executor
                newConfig.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);
                // set schedule policy of thread pool executor
                newConfig.setSchedulePolicy(SchedulePolicy.LastInFirstRun);

                // set a new config to lite-http
                liteHttp.initConfig(newConfig);
                break;
            case 19:
                // 19. Usage of Annotation

                @HttpUri(userUrl)
                @HttpMethod(HttpMethods.Get)
                @HttpID(1)
                @HttpCacheMode(CacheMode.CacheFirst)
                @HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES)
                class UserAnnoParam implements HttpParamModel {
                    public long id = 110;
                    private String key = "aes";
                }

                liteHttp.executeAsync(new JsonRequest<User>(new UserAnnoParam(), User.class) {}
                        .setHttpListener(new HttpListener<User>() {
                            @Override
                            public void onSuccess(User user, Response<User> response) {
                                HttpUtil.showTips(activity, "UserAnnoParam", user.toString());
                            }
                        }));

                break;

            case 20:
                // 20. Multi Cache Mechanism
                StringRequest cacheRequest = new StringRequest(url);

                cacheRequest.setCacheMode(CacheMode.CacheFirst);
                cacheRequest.setCacheExpire(30, TimeUnit.SECONDS);
                cacheRequest.setCacheDir("/sdcard/lite");
                cacheRequest.setCacheKey(null);

                cacheRequest.setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String html, Response<String> response) {
                        String title = response.isCacheHit() ? "Hit Cache(使用缓存)" : "No Cache(未用缓存)";
                        HttpUtil.showTips(activity, title, html);
                    }
                });
                liteHttp.executeAsync(cacheRequest);
                break;
            case 21:
                // 21. CallBack Mechanism

                // the correct way to set global http listener for all request.
                liteHttp.getConfig().setGlobalHttpListener(globalHttpListener);
                /**
                 * new http listener for current request:
                 *
                 * runOnUiThread = false;
                 * readingNotify = false;
                 * uploadingNotify = false;
                 *
                 * actually you can set a series of http listener for one http request.
                 */
                HttpListener<Bitmap> firstHttpListener = new HttpListener<Bitmap>(false, false, false) {
                    @Override
                    public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                        HttpLog.i(TAG, "first Listener, request success ...");
                    }

                    @Override
                    public void onFailure(HttpException e, Response<Bitmap> response) {
                        HttpLog.i(TAG, "first Listener, request failure ...");
                    }

                    @Override
                    public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                        HttpLog.i(TAG, "first Listener, request loading  ...");
                    }
                };
                // create a bitmap request.
                BitmapRequest bitmapRequest = new BitmapRequest(picUrl);

                // correct way to set first http listener
                bitmapRequest.setHttpListener(firstHttpListener);
                // correct way to set secondary (linked)listener
                firstHttpListener.setLinkedListener(secondaryListener);

                //load and show bitmap
                liteHttp.executeAsync(bitmapRequest);

                break;
            case 22:
                // 22. Best Practices of SmartExecutor

                //可定义等待队列进入执行状态的策略：先来先执行，后来先执行。

                //可定义等待队列满载后处理新请求的策略：
                //- 抛弃队列中最新的任务
                //- 抛弃队列中最旧的任务
                //- 抛弃当前新任务
                //- 直接执行（阻塞当前线程）
                //- 抛出异常（中断当前线程）

                // 智能并发调度控制器：设置[最大并发数]，和[等待队列]大小
                SmartExecutor smallExecutor = new SmartExecutor();

                // set this temporary parameter, just for test

                // number of concurrent threads at the same time, recommended core size is CPU count
                smallExecutor.setCoreSize(2);

                // adjust maximum number of waiting queue size by yourself or based on phone performance
                smallExecutor.setQueueSize(2);

                // 任务数量超出[最大并发数]后，自动进入[等待队列]，等待当前执行任务完成后按策略进入执行状态：后进先执行。
                smallExecutor.setSchedulePolicy(SchedulePolicy.LastInFirstRun);

                // 后续添加新任务数量超出[等待队列]大小时，执行过载策略：抛弃队列内最旧任务。
                smallExecutor.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);

                // 一次投入 4 个任务
                for (int i = 0; i < 4; i++) {
                    final int j = i;
                    smallExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            HttpLog.i(TAG, " TASK " + j + " is running now ----------->");
                            SystemClock.sleep(j * 200);
                        }
                    });
                }

                // 再投入1个需要取消的任务
                Future future = smallExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        HttpLog.i(TAG, " TASK 4 will be canceled ... ------------>");
                        SystemClock.sleep(1000);
                    }
                });
                future.cancel(false);
                break;

            case 23:
                // 23. Automatic Conversion of Complex Model

                // 实现登陆，参数为 name 和 password，成功后返回 User 对象。
                @HttpUri(loginUrl)
                class LoginParam extends HttpRichParamModel<User> {
                    private String name;
                    private String password;

                    public LoginParam(String name, String password) {
                        this.name = name;
                        this.password = password;
                    }
                }
                // 一句话调用即实现登陆
                liteHttp.executeAsync(new LoginParam("lucy", "123456").setHttpListener(
                        new HttpListener<User>() {
                            @Override
                            public void onSuccess(User user, Response<User> response) {
                                HttpUtil.showTips(activity, "对象自动转化", user.toString());
                            }

                            @Override
                            public void onFailure(HttpException e, Response<User> response) {
                                HttpUtil.showTips(activity, "对象自动转化", e.getMessage());
                            }
                        }
                ));
                break;
            case 24:
                // 24. Best Practice: HTTP Rich Param Model (It is simpler and More Useful)

                // rich param 更简单、有用！只需要定义一个RichParam，可指定URL、参数、返回响应体三个关键事物。
                @HttpUri("{url}/{path}")
                class UserRichParam extends HttpRichParamModel<User> {

                    @NonHttpParam
                    @HttpReplace("url")
                    private String url = "http://litesuits.com";

                    @NonHttpParam
                    @HttpReplace("path")
                    private String path = "mockdata/user_get";

                    public long id = 110;
                    private String key = "aes-125";
                }

                // 一句话调用即可
                liteHttp.executeAsync(new UserRichParam());


                // 其他更多注解还有：
                @HttpSchemeHost("{host}") // 定义scheme，使用host变量的值取代
                @HttpUri("{path}") // 定义uri 或者 path
                @HttpMethod(HttpMethods.Get) // 请求方式
                @HttpCharSet("UTF-8") // 请求编码
                @HttpTag("custom tag") // 打TAG
                @HttpCacheMode(CacheMode.CacheFirst) // 缓存模式
                @HttpCacheKey("custom-cache-key-name-by-myself") // 缓存文件名字
                @HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES) // 缓存时间
                @HttpID(2) // 请求ID
                @HttpMaxRetry(3) // 重试次数
                @HttpMaxRedirect(5)
                        // 重定向次数
                class TEST extends HttpRichParamModel<User> {

                    @NonHttpParam
                    @HttpReplace("host")
                    private String host = "http://litesuits.com";

                    @HttpReplace("path")
                    private String apiPath = "/mockdata/user_get";

                    // 可以复写设置headers/attachToUrl/listener/httpbody 等参数

                    /**
                     * 返回true则将将成员变量{@link #host}、{@link #apiPath}拼接到url中
                     * 返回false，则不拼接。
                     * @return
                     */
                    @Override
                    public boolean isFieldsAttachToUrl() {
                        return false;
                    }

                    @Override
                    protected LinkedHashMap<String, String> createHeaders() {
                        return super.createHeaders();
                    }

                    @Override
                    protected HttpListener<User> createHttpListener() {
                        return super.createHttpListener();
                    }

                    @Override
                    protected HttpBody createHttpBody() {
                        return super.createHttpBody();
                    }
                }
                liteHttp.executeAsync(new TEST().setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        HttpUtil.showTips(activity, "HttpRichParamModel", user.toString());
                    }
                }));
                break;
        }
    }


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
    HttpListener<Bitmap> secondaryListener = new HttpListener<Bitmap>(true, true, true) {
        ProgressDialog progressDialog = null;

        @Override
        public void onStart(AbstractRequest<Bitmap> request) {
            HttpLog.i(TAG, "second listener, request start ...");
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
            HttpLog.i(TAG, "second listener, request success ...");
            progressDialog.dismiss();
            ImageView iv = new ImageView(activity);
            iv.setImageBitmap(bitmap);
            HttpUtil.dialogBuilder(activity, "LiteHttp2.0", "")
                    .setView(iv).show();
        }

        @Override
        public void onFailure(HttpException e, Response<Bitmap> response) {
            HttpLog.i(TAG, " second listener, request failure ...");
            progressDialog.dismiss();
        }

        @Override
        public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
            HttpLog.i(TAG, " second listener, request loading  ...");
            progressDialog.setMax((int) total);
            progressDialog.setProgress((int) len);
        }

    };

}
