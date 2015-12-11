package com.litesuits.http.async;

import com.litesuits.android.async.AsyncExecutor;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;
import com.litesuits.http.response.handler.HttpModelHandler;
import com.litesuits.http.response.handler.HttpResponseHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

/**
 * 异步执行
 *
 * @author MaTianyu
 */
public class HttpAsyncExecutor extends AsyncExecutor {
    private LiteHttpClient client;

    private HttpAsyncExecutor(LiteHttpClient client, ExecutorService threadPool) {
        super(threadPool);
        this.client = client;
    }

    public final static HttpAsyncExecutor newInstance(LiteHttpClient client) {
        return new HttpAsyncExecutor(client, null);
    }

    public final static HttpAsyncExecutor newInstance(LiteHttpClient client, ExecutorService threadPool) {
        return new HttpAsyncExecutor(client, threadPool);
    }

    /**
     * 异步执行HTTP请求,结果回调给 UI Thread
     *
     * @param req
     * @param UIHandler
     * @return
     */
    public FutureTask<Response> execute(final Request req, final HttpResponseHandler UIHandler) {
        Worker<Response> worker = new Worker<Response>() {

            @Override
            public Response doInBackground() {
                return client.execute(req);
            }

            @Override
            public void onPostExecute(Response res) {
                UIHandler.handleResponse(res);
            }

            @Override
            public void abort() {
                if (req != null) req.abort();
            }

        };
        return execute(worker);
    }

    /**
     * 异步加载某一类数据，结果回调给 UI Thread
     *
     * @param req
     * @param UIHandler
     * @return
     */
    public <T> FutureTask<T> execute(final Request req, final HttpModelHandler<T> UIHandler) {
        Worker<T> worker = new Worker<T>() {
            private Response res;

            @SuppressWarnings("unchecked")
            @Override
            public T doInBackground() {
                res = client.execute(req);
                res.printInfo();
                Type type = ((ParameterizedType) UIHandler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                if (type instanceof Class<?>) {
                    return res.getObject((Class<T>) type);
                } else if (type instanceof ParameterizedType) {
                    return res.getObject((Class<T>) ((ParameterizedType) type).getRawType());
                }
                return null;
            }

            @Override
            public void onPostExecute(T data) {
                UIHandler.handleModelData(data, res);
            }

            @Override
            public void abort() {
                if (req != null) req.abort();
            }

        };
        return execute(worker);
    }
}
