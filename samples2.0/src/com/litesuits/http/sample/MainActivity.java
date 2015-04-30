package com.litesuits.http.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.concurrent.OverloadPolicy;
import com.litesuits.http.concurrent.SchedulePolicy;
import com.litesuits.http.config.HttpConfig;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.BytesRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;

import java.util.Arrays;
import java.util.concurrent.FutureTask;

public class MainActivity extends Activity {
    protected String TAG = MainActivity.class.getSimpleName();
    protected ListView mListview;
    protected BaseAdapter mAdapter;
    protected LiteHttp liteHttp;
    protected Activity activity;

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

        switch (which) {
            case 0:

                // 0. Quickly Configuration

                HttpConfig config = new HttpConfig(this);
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
                        publishProgress(result);

                        // 3.0 simple post and publish
                        result = liteHttp.post(new StringRequest(url));
                        publishProgress(result);

                        // 3.0 simple head and return
                        NameValuePair[] headers = liteHttp.head(new StringRequest(url));
                        return headers;
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(values));
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
                break;
            case 6:
                // 6. Automatic Model Conversion
                break;
            case 7:
                // 7. Custom Data Parser
                break;
            case 8:
                // 8. Replace Json Library
                break;
            case 9:
                // 9. File Upload
                break;
            case 10:
                // 10. File/Bitmap Download
                break;
            case 11:
                // 11. Disable Some Network
            case 12:

                // 12. Traffic/Time Statistics
                break;
            case 13:
                // 13. Retry/Redirect
                break;
            case 14:
                // 14. Best Practices of Exception Handling
                break;
            case 15:
                // 15. Best Practices of Cancel Request
                break;
            case 16:
                // 16. POST Multi-Form Data
                break;
            case 17:
                // 17. Concurrent and Scheduling
                break;
            case 18:
                // 18. Detail of Config
                break;
            case 19:
                // 19. The Use of Annotation
                break;
            case 20:
                // 20. Multi Cache Mechanism
                break;
            case 21:
                // 21. CallBack Mechanism
                break;
            case 22:
                // 22. Best Practices of SmartExecutor
                break;
        }
    }
}
