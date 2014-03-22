package com.litesuits.http.async;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.FutureTask;

import com.litesuits.android.async.AsyncExcutor;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;
import com.litesuits.http.response.handler.HttpModelHandler;
import com.litesuits.http.response.handler.HttpResponseHandler;

/**
 * 异步执行
 * @author MaTianyu
 */
public class HttpAsyncExcutor extends AsyncExcutor {

	/**
	 * 异步执行HTTP请求,结果回调给 UI Thread
	 * @param lite
	 * @param req
	 * @param UIHandler
	 * @return
	 */
	public FutureTask<Response> execute(final LiteHttpClient lite, final Request req, final HttpResponseHandler UIHandler) {
		Worker<Response> worker = new Worker<Response>() {

			@Override
			public Response doInBackground() {
				return lite.execute(req);
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
	 * @param hc
	 * @param req
	 * @param claxx
	 * @param UIHandler
	 * @return
	 */
	public <T> FutureTask<T> execute(final LiteHttpClient hc, final Request req, final HttpModelHandler<T> UIHandler) {
		Worker<T> worker = new Worker<T>() {
			private Response res;

			@SuppressWarnings("unchecked")
			@Override
			public T doInBackground() {
				res = hc.execute(req);
				Type type = ((ParameterizedType) UIHandler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
				if (type instanceof Class<?>) {
					return res.getObject((Class<T>) type);
				} else if (type instanceof ParameterizedType) { return res.getObject((Class<T>) ((ParameterizedType) type).getRawType()); }
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
