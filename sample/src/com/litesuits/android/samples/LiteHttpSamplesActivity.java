package com.litesuits.android.samples;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;
import com.litesuits.android.async.AsyncExecutor;
import com.litesuits.android.log.Log;
import com.litesuits.android.samples.model.param.RequestParams.BaiDuSearch;
import com.litesuits.android.samples.model.response.CompositeBasedModel.ApiResult;
import com.litesuits.android.samples.model.response.CompositeBasedModel.UserModel;
import com.litesuits.android.samples.model.response.ExtendBasedModel.User;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.R;
import com.litesuits.http.async.HttpAsyncExecutor;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpClientException;
import com.litesuits.http.exception.HttpClientException.ClientException;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.exception.HttpNetException;
import com.litesuits.http.exception.HttpNetException.NetException;
import com.litesuits.http.exception.HttpServerException;
import com.litesuits.http.exception.HttpServerException.ServerException;
import com.litesuits.http.parser.BinaryParser;
import com.litesuits.http.parser.BitmapParser;
import com.litesuits.http.parser.DataParser;
import com.litesuits.http.parser.FileParser;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.*;
import com.litesuits.http.request.content.multi.BytesPart;
import com.litesuits.http.request.content.multi.FilePart;
import com.litesuits.http.request.content.multi.InputStreamPart;
import com.litesuits.http.request.content.multi.StringPart;
import com.litesuits.http.request.param.HttpMethod;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.response.Response;
import com.litesuits.http.response.handler.HttpExceptionHandler;
import com.litesuits.http.response.handler.HttpModelHandler;
import com.litesuits.http.response.handler.HttpResponseHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class LiteHttpSamplesActivity extends BaseActivity {
    private LiteHttpClient client;
    private HttpAsyncExecutor asyncExcutor;
    private String urlUser = "http://litesuits.com/mockdata/user";
    private String urlUserList = "http://litesuits.com/mockdata/user_list";
    private String localPath = "/HttpServer/PostReceiver";
    private String localHost = "http://10.0.1.32:8080";
    //private String localPath       = "/LiteHttpServer/ReceiveFile";
    //private String localHost       = "http://192.168.1.100:8080";
    private String urlLocalRequest = localHost + localPath;
    private Context context;

    /**
     * 在{@link BaseActivity#onCreate(Bundle)}中设置视图
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        client = LiteHttpClient.newApacheHttpClient(context);
        asyncExcutor = HttpAsyncExecutor.newInstance(client);
        setSubTitile(getString(R.string.sub_title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public String getMainTitle() {
        return getString(R.string.title);
    }

    @Override
    public String[] getStringList() {
        return getResources().getStringArray(R.array.http_test_list);
    }

    /**
     * <item>1. 基础get请求</item>
     * <item>2. 带参数Get请求</item>
     * <item>3. 基础head请求</item>
     * <item>4. 基础Post请求</item>
     * <item>5. 处理异常方案</item>
     * <item>6. get简化模式</item>
     * <item>7. post简化模式</item>
     * <item>8. Https 安全协议</item>
     * <item>9. 智能Json Model转化</item>
     * <item>10. Java Model方式添加请求参数</item>
     * <item>11. 上传文件</item>
     * <item>12. 获取字节</item>
     * <item>13. 下载图片</item>
     * <item>14. 下载文件</item>
     * <item>15. 自定义数据解析</item>
     * <item>16. 自动重定向</item>
     * <item>17. 自带异步执行器:获得响应</item>
     * <item>18. 自带异步执行器:获得对象</item>
     * <item>19. Thread方式开启异步</item>
     * <item>20. AsyncTask方式开启异步</item>
     * <item>21. HTTP加载取消</item>
     * <item>22. Post发送Model Entity</item>
     * <item>23. Post 参数测试</item>
     */
    @Override
    public Runnable getRunnable(final int pos) {
        // index from 1
        final int id = pos + 1;
        // Execute in UI thread: 这里将展示如何异步调用LiteHttp发送请求
        //请记住：LiteHttp全部方法都遵循一个规律：在一个线程内完成，不会跨越线程或者进程。
        switch (id) {
            case 17:
                innerAsyncGetResponse();
                break;
            case 18:
                innerAsyncGetModel();
                break;
            case 19:
                useThreadExecute();
                break;
            case 20:
                useAsyncExecute();
                break;
            case 21:
                cancelHttpLoading();
                break;
            case 22:
                innerAsyncPostModel();
                break;
            case 23:
                innerAsyncPostParameters();
                break;
        }
        // execute in child thread:
        return new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case 1:
                        makeBaseGetRequest();
                        break;
                    case 2:
                        makeParamteredGetRequest();
                        break;
                    case 3:
                        makeBaseHeadRequest();
                        break;
                    case 4:
                        makePostRequest();
                        break;
                    case 5:
                        makeRequestWithExceptionHandler();
                        break;
                    case 6:
                        makeSimpleGetRequest();
                        break;
                    case 7:
                        makeSimplePostRequest();
                        break;
                    case 8:
                        makeHttpsRequest();
                        break;
                    case 9:
                        makeIntelligentJsonModelMapingRequest();
                        break;
                    case 10:
                        makeJavaModeAsParamsRequest();
                        break;
                    case 11:
                        makeUpLoadMultiBodyRequest();
                        break;
                    case 12:
                        makeLoadBytesRequest();
                        break;
                    case 13:
                        makeLoadBitmapRequest();
                        break;
                    case 14:
                        makeLoadFileRequest();
                        break;
                    case 15:
                        makeCustomParserRequest();
                        break;
                    case 16:
                        makeAutoRedirectRequest();
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private String url = "http://baidu.com";

    /**
     * 第一步 调用get方法
     * 第二步 没了
     * 是的 没有第二步
     */
    private void makeSimpleGetRequest() {
        LiteHttpClient client = LiteHttpClient.newApacheHttpClient(context, "Mozilla/5.0");
        String s = client.get(url);
        Log.i(TAG, s);
    }

    private void innerAsyncGetResponse() {

        HttpAsyncExecutor asyncExcutor = HttpAsyncExecutor.newInstance(client);

        asyncExcutor.execute(new Request(url), new HttpResponseHandler() {
            @Override
            protected void onSuccess(Response res, HttpStatus status, NameValuePair[] headers) {
                printLog(res);
                toast(res.getString());
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                toast("e: " + e);
            }
        });
    }

    private void innerAsyncPostParameters() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("POST BODY TEST");
        String[] array = getResources().getStringArray(R.array.http_test_post);
        builder.setItems(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String url = urlLocalRequest;

                Request req = new Request(url);
                req.setMethod(HttpMethod.Post);

                switch (which) {
                    case 0:
                        testHttpPost();
                        //req.setHttpBody(new StringBody("hello"));
                        break;
                    case 1:
                        LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
                        pList.add(new NameValuePair("key1", "value1"));
                        pList.add(new NameValuePair("key2", "value2"));
                        req.setHttpBody(new UrlEncodedFormBody(pList));
                        break;
                    case 2:
                        req.setHttpBody(new JsonBody(new BaiDuSearch()));
                        break;
                    case 3:

                        ArrayList<String> list = new ArrayList<String>();
                        list.add("a");
                        list.add("b");
                        list.add("c");
                        req.setHttpBody(new SerializableBody(list));
                        break;
                    case 4:
                        req.setHttpBody(new ByteArrayBody(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                                18, 127
                        }));
                        break;
                    case 5:
                        req.setHttpBody(new FileBody(new File("sdcard/alog.xml")));
                        break;
                    case 6:
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(new File("sdcard/alog.xml"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        req.setHttpBody(new InputStreamBody(fis));
                        break;
                    case 7:
                        asyncExcutor.execute(new AsyncExecutor.Worker<String>() {
                            @Override
                            protected String doInBackground() {
                                Response res = makeUpLoadMultiBodyRequest();
                                //printLog(res);
                                return "yes";
                            }

                            @Override
                            protected void onPostExecute(String data) {
                                toast("onSuccess : " + data);
                            }
                        });
                        break;
                }
                if (which != 0) {
                    asyncExcutor.execute(req, new HttpModelHandler<String>() {
                        @Override
                        protected void onSuccess(String data, Response res) {
                            toast("onSuccess : " + data);
                            printLog(res);
                        }

                        @Override
                        protected void onFailure(HttpException e, Response res) {
                            toast("onFailure: " + e);
                        }

                    });
                }
            }
        });
        //builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void testHttpPost() {
        asyncExcutor.execute(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    //创建连接
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(urlLocalRequest);
                    //设置参数，仿html表单提交
                    List<BasicNameValuePair> temp = new ArrayList<BasicNameValuePair>();
                    temp.add(new BasicNameValuePair("key1", "value11"));
                    temp.add(new BasicNameValuePair("key2", "value222"));
                    post.setEntity(new UrlEncodedFormEntity(temp, HTTP.UTF_8));
                    //发送HttpPost请求，并返回HttpResponse对象
                    HttpResponse httpResponse = httpClient.execute(post);
                    // 判断请求响应状态码，状态码为200表示服务端成功响应了客户端的请求
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //获取返回结果
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        Log.i(TAG, "Apache result: " + result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private void innerAsyncPostModel() {
        Request req = new Request(urlUser);
        req.setMethod(HttpMethod.Post);
        req.setHttpBody(new JsonBody(new BaiDuSearch()));
        asyncExcutor.execute(req, new HttpModelHandler<String>() {
            @Override
            protected void onSuccess(String data, Response res) {
                toast("User String: " + data);
                printLog(res);
            }

            @Override
            protected void onFailure(HttpException e, Response res) {
                toast("e: " + e);
            }

        });
    }

    private void innerAsyncGetModel() {
        asyncExcutor.execute(new Request(urlUser), new HttpModelHandler<String>() {
            @Override
            protected void onSuccess(String data, Response res) {
                toast("User String: " + data);
                printLog(res);
            }

            @Override
            protected void onFailure(HttpException e, Response res) {
                toast("e: " + e);
            }

        });

        asyncExcutor.execute(new Request(urlUser), new HttpModelHandler<User>() {
            @Override
            protected void onSuccess(User data, Response res) {
                Log.i(TAG, "User: " + data);
                toast("User: " + data);
                printLog(res);
            }

            @Override
            protected void onFailure(HttpException e, Response res) {
                toast("e: " + e);
            }

        });

        asyncExcutor.execute(new Request(urlUserList), new HttpModelHandler<ArrayList<User>>() {
            @Override
            protected void onSuccess(ArrayList<User> data, Response res) {
                Log.i(TAG, "User List: " + data);
                toast("User List: " + data);
                printLog(res);
            }

            @Override
            protected void onFailure(HttpException e, Response res) {
                toast("e: " + e);
            }

        });

    }

    private void useThreadExecute() {
        new Thread() {
            @Override
            public void run() {
                final Response res = client.execute(new Request(url));
                // must post to  UI thread by yourself
                runOnUiThread(new Runnable() {
                    public void run() {

                        new HttpResponseHandler() {
                            @Override
                            protected void onSuccess(Response res, HttpStatus status, NameValuePair[] headers) {
                                printLog(res);
                                toast("Thread Execute : " + res.getString());
                            }

                            @Override
                            protected void onFailure(Response res, HttpException e) {
                                toast("e: " + e);
                            }
                        }.handleResponse(res);

                    }
                });

            }
        }.start();
    }

    private void useAsyncExecute() {
        AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {
            @Override
            protected Response doInBackground(Void... params) {
                return client.execute(new Request(url));
            }

            @Override
            protected void onPostExecute(Response res) {
                printLog(res);
                toast("AsyncTask Execute : " + res.getString());
            }
        };
        task.execute();
    }

    private void cancelHttpLoading() {
        FutureTask<Response> future = asyncExcutor.execute(new Request(urlUser), new HttpResponseHandler() {
            @Override
            protected void onSuccess(Response res, HttpStatus status, NameValuePair[] headers) {
                printLog(res);
                toast(res.getString());
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                toast("e: " + e);
            }
        });
        SystemClock.sleep(200);
        future.cancel(true);
    }

    private void makeJavaModeAsParamsRequest() {
        BaiDuSearch model = new BaiDuSearch();
        Request req = new Request(url).addUrlSuffix("/s").setParamModel(model);
        Response res = client.execute(req);
        printLog(res);

        //		Man man = new Man("jame",18);
        //		Response resonse = client.execute(new Request("http://a.com",man));
        //build as http://a.com?name=jame&age=18
    }

    public class Man implements HttpParam {
        private String name;
        private int age;

        public Man(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    private void makeAutoRedirectRequest() {
        String url = "http://www.baidu.com/link?url=Lqc3GptP8u05JCRDsk0jqsAvIZh9WdtO_RkXYMYRQEm";
        String s = client.get(url);
        Log.d(TAG, s);
    }

    private void makeCustomParserRequest() {
        String customString = client.execute(url, new DataParser<String>() {
            @Override
            protected String parseData(InputStream stream, long totalLength, String charSet) throws IOException {
                Reader reader = new InputStreamReader(stream, charSet);
                CharArrayBuffer buffer = new CharArrayBuffer((int) totalLength);
                try {
                    char[] tmp = new char[buffSize];
                    int l;
                    //判断线程有没有被结束，以及时停止读数据，节省流量。
                    while (!Thread.currentThread().isInterrupted() && (l = reader.read(tmp)) != -1) {
                        buffer.append(tmp, 0, l);
                        //统计数据，不加此方法则数据统计不完整。
                        readLength += l;
                    }
                } finally {
                    reader.close();
                }
                return "自定义啊： 加前缀 " + buffer.toString() + "自定义哈，加后缀。";
            }
        }, HttpMethod.Get);
        Log.d(TAG, customString);
    }

    String imageUrl = "http://pic.yesky.com/imagelist/07/37/5146451_2754_1000x500.jpg";

    /**
     * 多文件上传
     */
    private Response makeUpLoadMultiBodyRequest() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("sdcard/alog.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //View v;v.setBackground();
        String url = urlLocalRequest;
        Request req = new Request(url);

        MultipartBody body = new MultipartBody();
        body.addPart(new StringPart("key1", "hello"));
        body.addPart(new StringPart("key2", "很高兴见到你", "utf-8", null));
        body.addPart(new BytesPart("key3", new byte[]{1, 2, 3}));
        body.addPart(new FilePart("pic", new File("sdcard/apic.png"), "image/jpeg"));
        body.addPart(new FilePart("song", new File("sdcard/asong.mp3"), "audio/x-mpeg"));
        body.addPart(new InputStreamPart("alog", fis, "alog.xml", "text/xml"));
        req.setMethod(HttpMethod.Post).setHttpBody(body);
        Response res = client.execute(req);
        res.printInfo();
        //System.out.println("response string : " + res.getString());
        return res;
    }

    private void makeLoadFileRequest() {
        File file = client.execute(imageUrl, new FileParser("sdcard/lite.jpg"), HttpMethod.Get);
    }

    private void makeLoadBitmapRequest() {
        final Response res = client.execute(new Request(imageUrl).setDataParser(new BitmapParser()));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new HttpResponseHandler() {
                    @Override
                    public void onSuccess(Response response, HttpStatus status, NameValuePair[] headers) {
                        addImageViewToBottom(response.getBitmap());
                    }

                    @Override
                    public void onFailure(Response response, HttpException exception) {
                        toast("加载图片失败");
                    }
                }.handleResponse(res);
            }
        });
    }

    private void addImageViewToBottom(final Bitmap bitmap) {
        if (bitmap != null) {
            ImageView imageview = new ImageView(LiteHttpSamplesActivity.this);
            imageview.setImageBitmap(bitmap);
            mListview.addFooterView(imageview);
            Toast.makeText(LiteHttpSamplesActivity.this, "Bitmap 加载完成，RowBytes：" + bitmap.getRowBytes() + ", ByteCount: " + bitmap.getRowBytes(),
                    Toast.LENGTH_LONG).show();
            mListview.setSelection(mListview.getAdapter().getCount());
        }
    }

    private void makeLoadBytesRequest() {
        byte[] bytes = client.execute(url, new BinaryParser(), HttpMethod.Get);
        if (bytes != null) {
            Log.d(TAG, "bytes length is : " + bytes.length);
        }
    }

    /**
     * 通过String存储data对象，再转化为User模型
     */
    private void makeIntelligentJsonModelMapingRequest() {
        //		User user = client.get("", null, User.class);
        asyncExcutor.execute(new AsyncExecutor.Worker<Response>() {

            @Override
            public Response doInBackground() {
                return client.execute(new Request(urlUser));
            }

            @Override
            public void onPostExecute(Response res) {
                //以组合的方式组织model并解析
                ApiResult api = res.getObject(ApiResult.class);
                if (api != null) {
                    UserModel user1 = api.getData(UserModel.class);
                    Log.i(TAG, "user1: " + user1);
                    toast("user1: " + user1);
                }
                //以继承的方式组织model并解析
                User user2 = res.getObject(User.class);
                Log.i(TAG, "user2: " + user2);
                toast("user2: " + user2);
                // user1 和 user2 是一样的对象，只是实现、组织起来的的方式不同。
                printLog(res);
            }
        });

    }

    private void makeHttpsRequest() {
        String s = client.get("https://www.alipay.com");
        Log.d(TAG, s);
    }

    private void makeSimplePostRequest() {
        byte[] bytes = client.post("https://passport.csdn.net/account/login", new BinaryParser());
        if (bytes != null) {
            Log.d(TAG, new String(bytes));
        }
    }

    private void makeBaseGetRequest() {
        //有escape方法
        //String url = "https://ibsbjstar.ccb.com.cn/app/ccbMain?REGINFO=%u9EC4%u5FD7%u52C7&MERCHANTID=105441883990003&POSID=114449357&BRANCHID=441000000&ORDERID=201502032248052848&PAYMENT=13.0&CURCODE=01&TXCODE=520100&REMARK1=&REMARK2=&TYPE=1&GATEWAY=W2Z1&CLIENTIP=&PROINFO=%u9EC4%u5FD7%u52C7%u7F34%u7EB31%u4E2A%u6708%u515A%u8D39&REFERER=&MAC=9346a5fb7fa11f3e512ffbeb5ebd63b6";

        // 重定向2次
        //String url = "http://wap.cmread.com/r/400270618/400644484/index.htm?vt=9&cm=M2040002";

        // default method is get.
        LiteHttpClient client = LiteHttpClient.newApacheHttpClient(context, "Mozilla/5.0");
        Response res = client.execute(new Request(url));
        String html = res.getString();
        System.out.println("html: " + html);
    }

    private void makeParamteredGetRequest() {
        String url = "www.baidu.com";
        Request req = new Request(url).setMethod(HttpMethod.Get).addUrlPrifix("http://").addUrlSuffix("/s").addHeader("User-Agent", "Taobao Browser 1.0")
                .addUrlParam("wd", "你好 Lite").addUrlParam("bs", "大家好").addUrlParam("inputT", "0");
        Response res = client.execute(req);
        printLog(res);
    }

    private void makeBaseHeadRequest() {
        Response res = client.execute(new Request(url).setMethod(HttpMethod.Head));
        printLog(res);
    }

    private void makePostRequest() {
        Request req = new Request("https://passport.csdn.net/account/login").setMethod(HttpMethod.Post);
        Response res = client.execute(req);
        printLog(res);
    }

    private void makeRequestWithExceptionHandler() {
        url = "http://h5.m.taobao.com/we/pc.htm";
        //			InputStream is = new

        String json = "{\"a\":1}";

        Request req = new Request(url).setMethod(HttpMethod.Trace);
        req.addHeader("Content-Type", "application/json");
        req.addUrlParam("", json);

        asyncExcutor.execute(req, new HttpResponseHandler() {

            @Override
            public void onSuccess(Response response, HttpStatus status, NameValuePair[] headers) {
                toast("成功");
            }

            @Override
            public void onFailure(Response response, HttpException e) {

                new HttpExceptionHandler() {
                    @Override
                    protected void onClientException(HttpClientException e, ClientException type) {
                        toast("开发者可更新界面提示用户，原因：客户端有异常");
                    }

                    @Override
                    protected void onNetException(HttpNetException e, NetException type) {
                        if (type == NetException.NetworkError) {
                            toast("开发者可更新界面提示用户，原因：无可用网络");
                        } else if (type == NetException.UnReachable) {
                            toast("开发者可更新界面提示用户，原因：服务器不可访问(或网络不稳定)");
                        } else if (type == NetException.NetworkDisabled) {
                            toast("原因：该网络类型已被开发者设置禁用");
                        }
                    }

                    @Override
                    protected void onServerException(HttpServerException e, ServerException type, HttpStatus status) {
                        toast("开发者可更新界面提示用户，原因：服务暂时不可用");
                    }

                }.handleException(e);

                printLog(response);
            }
        });
    }

    private void printLog(Response response) {
        if (response.getString() != null) {
            Log.i(TAG, "http result lengh : " + response.getString().length());
        }
        Log.v(TAG, "http result :\n " + response.getString());
        Log.d(TAG, response.toString());
    }

    private void toast(String s) {
        Toast.makeText(LiteHttpSamplesActivity.this, s, Toast.LENGTH_LONG).show();
    }

}
