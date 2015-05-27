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
import com.alibaba.fastjson.JSON;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.annotation.*;
import com.litesuits.http.concurrent.OverloadPolicy;
import com.litesuits.http.concurrent.SchedulePolicy;
import com.litesuits.http.concurrent.SmartExecutor;
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
import com.litesuits.http.request.content.*;
import com.litesuits.http.request.content.multi.*;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpParamModel;
import com.litesuits.http.request.param.HttpRichParamModel;
import com.litesuits.http.request.query.JsonQueryBuilder;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
     * <item>19. Usage of Annotation</item>
     * <item>20. Multi Cache Mechanism</item>
     * <item>21. CallBack Mechanism</item>
     * <item>22. Best Practice: SmartExecutor</item>
     * <item>23. Best Practice: Auto-Conversion of Complex Model</item>
     * <item>24. Best Practice: HTTP Rich Param Model</item>
     */
    private void clickTestItem(final int which) {

        final String url = "http://baidu.com";
        final String uploadUrl = "http://192.168.8.105:8080/upload";
        final String httpsUrl = "https://www.baidu.com";
        final String userGet = "http://litesuits.com/mockdata/user_get";
        final String picUrl = "http://www.88xm.com/uploads/allimg/150311/1-150311160U0.jpg";
        final String redirectUrl = "http://www.baidu.com/link?url=Lqc3GptP8u05JCRDsk0jqsAvIZh9WdtO_RkXYMYRQEm";

        // restore http config
        if (needRestore) {
            liteHttp.getConfig().restoreToDefault();
            needRestore = false;
        }

        switch (which) {
            case 0:
                // 0. Quickly Configuration

                HttpConfig config = new HttpConfig(activity);
                // set app context
                config.setContext(activity);
                // custom User-Agent
                config.setUserAgent("Mozilla/5.0 (...)");
                // connect timeout: 10s,  socket timeout: 10s
                config.setTimeOut(1000, 1000);
                // init config
                liteHttp.initConfig(config);

                HttpUtil.showTips(activity, "LiteHttp2.0", "配置参数成功");
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

                // 1.1 execute async
                liteHttp.executeAsync(request);

                // 1.2 perform async
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
                    public <T> T toObject(String s, Type type) {
                        return JSON.parseObject(s, type);
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
                final ProgressDialog upProgress = new ProgressDialog(this);
                upProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                upProgress.setIndeterminate(false);
                upProgress.show();
                StringRequest uploadRequest = new StringRequest(uploadUrl)
                        .setMethod(HttpMethods.Post)
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
                liteHttp.executeAsync(new FileRequest(picUrl).setFileSavePath("sdcard/aaa.jpg"));
                break;
            case 11:
                // 11. Disable Some Network

                config = liteHttp.getConfig();
                // disable network need context
                config.setContext(activity);
                // disable mobile(2G/3G/4G..) network
                config.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_MOBILE);
                needRestore = true;

                liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", s);
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());

                        needRestore = false;
                    }
                }));
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
                liteHttp.getConfig().setDefaultMaxRetryTimes(2);
                // maximum redirect times
                liteHttp.getConfig().setDefaultMaxRedirectTimes(5);
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
                        new StringRequest("invalid url").setHttpListener(new HttpListener<String>() {
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
                    futureTask.cancel(isInterrupted);
                }
                break;
            case 16:
                // 16. POST Multi-Form Data
                final ProgressDialog postProgress = new ProgressDialog(this);
                postProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                postProgress.setIndeterminate(false);
                postProgress.show();
                final StringRequest postRequest = new StringRequest(uploadUrl)
                        .setMethod(HttpMethods.Post)
                        .setHttpListener(new HttpListener<String>(true, false, true) {
                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                //                                postProgress.dismiss();
                                HttpUtil.showTips(activity, "Upload Success", s);
                                response.printInfo();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                postProgress.dismiss();
                                HttpUtil.showTips(activity, "Upload Failed", e.toString());
                            }

                            @Override
                            public void onUploading(AbstractRequest<String> request, long total, long len) {
                                postProgress.setMax((int) total);
                                postProgress.setProgress((int) len);
                            }
                        });

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("POST DATA TEST");
                String[] array = getResources().getStringArray(R.array.http_test_post);
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
                                postRequest.setHttpBody(new ByteArrayBody(
                                        new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 127
                                        }));
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
                                //View v;v.setBackground();

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

                HttpConfig csConfig = liteHttp.getConfig();
                // only one task can be executed at the same time
                csConfig.setConcurrentSize(1);
                // at most two tasks be hold in waiting queue at the same time
                csConfig.setWaitingQueueSize(2);
                // the last waiting task executed first
                csConfig.setSchedulePolicy(SchedulePolicy.LastInFirstRun);
                // when task more than 3(1+2), new task will be discard.
                csConfig.setOverloadPolicy(OverloadPolicy.DiscardCurrentTask);

                // note : restore config to default, next click.
                needRestore = true;

                // executed order: 0 -> 2 -> 1 , by [DiscardNewTaskInQueue Policy] 3 will be discard.
                for (int i = 0; i < 4; i++) {
                    liteHttp.executeAsync(new StringRequest(url).setTag(i));
                }

                break;
            case 18:
                // 18. Detail of Configuration

                List<NameValuePair> headers = new ArrayList<NameValuePair>();
                headers.add(new NameValuePair("cookies", "this is cookies"));
                headers.add(new NameValuePair("custom-key", "custom-value"));

                HttpConfig newConfig = new HttpConfig(activity);

                // common headers will be set to all request
                newConfig.setCommonHeaders(headers);
                // set default cache path to all request
                newConfig.setCacheDirPath(Environment.getExternalStorageDirectory() + "/a-cache");
                // app context(be used to detect network and get app files path)
                newConfig.setContext(activity);
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
                // set global http listener to all request
                newConfig.setGlobalHttpListener(null);
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
                // set user-agent
                newConfig.setUserAgent("Mozilla/5.0");

                // set a new config to lite-http
                liteHttp.initConfig(newConfig);
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
                liteHttp.executeAsync(new UserRequest(p).setHttpListener(new HttpListener<User>() {
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
                liteHttp.executeAsync(new UserRequest(new UserRichParam()));
                break;

            case 20:
                // 20. Multi Cache Mechanism
                JsonRequest<User> cacheRequest = new JsonRequest<User>(userGet, User.class);
                cacheRequest.setCacheMode(CacheMode.CacheFirst);
                cacheRequest.setCacheExpire(20, TimeUnit.SECONDS);
                cacheRequest.setCacheKey(userGet + "-20");
                cacheRequest.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        String title = response.isCacheHit() ? "Hit Cache(使用缓存)" : "No Cache(未用缓存)";
                        HttpUtil.showTips(activity, title, user.toString());
                    }
                });
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
                liteHttp.getConfig().setGlobalHttpListener(globalHttpListener);
                // create a bitmap request.
                BitmapRequest bitmapRequest = new BitmapRequest(picUrl);
                // correct way to set the initial and only http listener
                bitmapRequest.setHttpListener(httpListener);
                // correct way to set another (linked)listener
                bitmapRequest.setLinkedHttpListener(newHttpListener);

                //load and show bitmap
                liteHttp.executeAsync(bitmapRequest);

                break;
            case 22:
                // 22. Best Practices of SmartExecutor

                /**
                 * <ul>
                 *
                 * <li>keep coreSize tasks concurrent, and put them in runningList,
                 * maximum number of running-tasks at the same time is coreSize.</li>
                 *
                 * <li>when runningList is full, put new task in waitingQueue waiting for execution,
                 * maximum of waiting-tasks number is queueSize.</li>
                 *
                 * <li>when waitingQueue is full, new task is performed by OverloadPolicy.</li>
                 *
                 * <li>when running task is completed, take it out from runningList.</li>
                 *
                 * <li>schedule next by SchedulePolicy, take next task out from waitingQueue to execute,
                 * and so on until waitingQueue is empty.</li>
                 *
                 * </ul>
                 */
                SmartExecutor smartExecutor = new SmartExecutor();

                // recommended core size is CPU count
                // set this temporary parameter, just for test
                smartExecutor.setCoreSize(2);

                //  Adjust maximum number of waiting queue size based phone performance
                // set this temporary parameter, just for test
                smartExecutor.setQueueSize(2);
                smartExecutor.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);
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

            case 23:
                // 23. Automatic Conversion of Complex Model
                break;
            case 24:
                // 24. Best Practice: HTTP Rich Param Model
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
