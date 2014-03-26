package com.litesuits.android.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.util.CharArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.litesuits.android.async.AsyncExcutor.Worker;
import com.litesuits.android.log.Log;
import com.litesuits.android.samples.model.param.RequestParams.BaiDuSearch;
import com.litesuits.android.samples.model.response.CompositeBasedModel.ApiResult;
import com.litesuits.android.samples.model.response.CompositeBasedModel.UserModel;
import com.litesuits.android.samples.model.response.ExtendBasedModel.User;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.R;
import com.litesuits.http.async.HttpAsyncExcutor;
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
import com.litesuits.http.request.param.HttpMethod;
import com.litesuits.http.request.param.HttpParam;
import com.litesuits.http.response.Response;
import com.litesuits.http.response.handler.HttpExceptionHandler;
import com.litesuits.http.response.handler.HttpModelHandler;
import com.litesuits.http.response.handler.HttpResponseHandler;

public class LiteHttpSamplesActivity extends BaseActivity {
	private LiteHttpClient client;
	private HttpAsyncExcutor asyncExcutor = new HttpAsyncExcutor();
	private String urlUser = "http://litesuits.github.io/mockdata/user";
	private String urlUserList = "http://litesuits.github.io/mockdata/user_list";

	/**
	 * 在{@link BaseActivity#onCreate(Bundle)}中设置视图
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = LiteHttpClient.getInstance(this);
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
	 * <item>1. Base Get Request</item>
	 * <item>2. Paramter Get Request</item>
	 * <item>3. Base Head Request</item>
	 * <item>4. Base Post Request</item>
	 * <item>5. Exeception Heanler</item>
	 * <item>6. Simple Get</item>
	 * <item>7. Simple Post</item>
	 * <item>8. Https Request</item>
	 * <item>9. Intelligent Json Model Maping</item>
	 * <item>10. Add Request Params By Java Model</item>
	 * <item>11. Upload File</item>
	 * <item>12. Load Bytes</item>
	 * <item>13. Load Bitmap</item>
	 * <item>14. Load File</item>
	 * <item>15. Custom DataParser</item>
	 * <item>16. Test Auto Redirect</item>
	 * <item>17. Inner AsyncExecutor:Get Response</item>
	 * <item>18. Inner AsyncExecutor:Get Model</item>
	 * <item>19. Use Thread Execute</item>
	 * <item>20. Use AsyncTask Execute</item>
	 * <item>21. Cancel HTTP Loading</item>
	 */
	@Override
	public Runnable getRunnable(final int pos) {
		// index from 1
		final int id = pos + 1;
		// Execute in UI thread: 这里将展示如何异步调用LiteHttp发送请求
		//请记住：LiteHttp全部方法都遵循一个规律：在一个线程内完成，不会跨越线程或者进程。
		switch (id) {
			case 17 :
				innerAsyncGetResponse();
				break;
			case 18 :
				innerAsyncGetModel();
				break;
			case 19 :
				useThreadExecute();
				break;
			case 20 :
				useAsyncExecute();
				break;
			case 21 :
				cancelHttpLoading();
				break;
		}
		// execute in child thread:
		return new Runnable() {
			@Override
			public void run() {
				switch (id) {
					case 1 :
						makeBaseGetRequest();
						break;
					case 2 :
						makeParamteredGetRequest();
						break;
					case 3 :
						makeBaseHeadRequest();
						break;
					case 4 :
						makePostRequest();
						break;
					case 5 :
						makeRequestWithExceptionHandler();
						break;
					case 6 :
						makeSimpleGetRequest();
						break;
					case 7 :
						makeSimplePostRequest();
						break;
					case 8 :
						makeHttpsRequest();
						break;
					case 9 :
						makeIntelligentJsonModelMapingRequest();
						break;
					case 10 :
						makeJavaModeAsParamsRequest();
						break;
					case 11 :
						makeUpLoadFileRequest();
						break;
					case 12 :
						makeLoadBytesRequest();
						break;
					case 13 :
						makeLoadBitmapRequest();
						break;
					case 14 :
						makeLoadFileRequest();
						break;
					case 15 :
						makeCustomParserRequest();
						break;
					case 16 :
						makeAutoRedirectRequest();
						break;

					default :
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
		String s = client.get(url);
		Log.d(TAG, s);
	}

	private void innerAsyncGetResponse() {
		HttpAsyncExcutor asyncExcutor = new HttpAsyncExcutor();

		asyncExcutor.execute(client, new Request(url), new HttpResponseHandler() {
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
		//		HttpAsyncExcutor asyncExcutor = new HttpAsyncExcutor();
		//		asyncExcutor.execute(client, new Request(url), new HttpResponseHandler() {
		//			@Override
		//			protected void onSuccess(Response res, HttpStatus status, NameValuePair[] headers) {
		//				// do some thing on UI thread
		//			}
		//
		//			@Override
		//			protected void onFailure(Response res, HttpException e) {
		//				// do some thing on UI thread 
		//				// and you can handle exception by HttpExceptionHandler.
		//				new HttpExceptionHandler() {
		//					
		//					@Override
		//					protected void onServerException(HttpServerException e, ServerException type, HttpStatus status, NameValuePair[] headers) {
		//						// connect success, server error
		//					}
		//					
		//					@Override
		//					protected void onNetException(HttpNetException e, NetException type) {
		//						// network error
		//					}
		//					
		//					@Override
		//					protected void onClientException(HttpClientException e, ClientException type) {
		//						//client exception
		//					}
		//				}.handleException(e);
		//			}
		//		});
	}

	private void innerAsyncGetModel() {
		asyncExcutor.execute(client, new Request(urlUser), new HttpModelHandler<String>() {
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

		asyncExcutor.execute(client, new Request(urlUser), new HttpModelHandler<User>() {
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

		asyncExcutor.execute(client, new Request(urlUserList), new HttpModelHandler<ArrayList<User>>() {
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
		FutureTask<Response> future = asyncExcutor.execute(client, new Request(url), new HttpResponseHandler() {
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
		future.cancel(true);
		// delay cancel
		asyncExcutor.execute(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				FutureTask<?> future = asyncExcutor.execute(client, new Request(url), new HttpResponseHandler() {
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
				SystemClock.sleep(400);
				future.cancel(true);
				return null;
			}
		});

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
			protected String parseData(InputStream stream, int totalLength, String charSet) throws IOException {
				Reader reader = new InputStreamReader(stream, charSet);
				CharArrayBuffer buffer = new CharArrayBuffer(totalLength);
				try {
					char[] tmp = new char[buffSize];
					int l;
					//判断线程有没有被结束，以及时停止读数据，节省流量。
					while (!Thread.currentThread().isInterrupted() && (l = reader.read(tmp)) != -1) {
						buffer.append(tmp, 0, l);
						//统计数据，不加此方法则数据统计不完整。
						if (statistics) readLength += l;
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
	private void makeUpLoadFileRequest() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File("sdcard/1.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = "http://192.168.2.108:8080/LiteHttpServer/ReceiveFile";
		Request req = new Request(url);
		req.setMethod(HttpMethod.Post).addParam("lite", new File("sdcard/lite.jpg"), "image/jpeg")
				.addParam("feiq", new File("sdcard/feiq.exe"), "application/octet-stream");
		if (fis != null) req.addParam("meinv", fis, "sm.jpg", "image/jpeg");
		Response res = client.execute(req);
		System.out.println(res);
		System.out.println("response string : " + res.getString());
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
			Toast.makeText(LiteHttpSamplesActivity.this, "Bitmap 加载完成，RowBytes：" + bitmap.getRowBytes() + ", ByteCount: " + bitmap.getByteCount(),
					Toast.LENGTH_LONG).show();
			mListview.setSelection(mListview.getAdapter().getCount());
		}
	}

	private void makeLoadBytesRequest() {
		byte[] bytes = client.execute(url, new BinaryParser(), HttpMethod.Get);
		if (bytes != null) Log.d(TAG, "bytes length is : " + bytes.length);
	}

	/**
	 * 通过String存储data对象，再转化为User模型
	 */
	private void makeIntelligentJsonModelMapingRequest() {
		//		User user = client.get("", null, User.class);
		asyncExcutor.execute(new Worker<Response>() {

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
		if (bytes != null) Log.d(TAG, new String(bytes));
	}

	private void makeBaseGetRequest() {
		Context context = this;
		// default method is get.
		LiteHttpClient client = LiteHttpClient.getInstance(context);
		Response res = client.execute(new Request("http://baidu.com"));
		String html = res.getString();
		printLog(res);
	}

	private void makeParamteredGetRequest() {
		String url = "www.baidu.com";
		Request req = new Request(url).setMethod(HttpMethod.Get).addUrlPrifix("http://").addUrlSuffix("/s").addHeader("User-Agent", "Taobao Browser 1.0")
				.addParam("wd", "你好 Lite").addParam("bs", "大家好").addParam("inputT", "0");
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
		url="http://h5.m.taobao.com/we/pc.htm";
		Request req = new Request(url).setMethod(HttpMethod.Get);
		asyncExcutor.execute(client, req, new HttpResponseHandler() {

			@Override
			public void onSuccess(Response response, HttpStatus status, NameValuePair[] headers) {
				response.getBitmap();
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
		if (response.getString() != null) Log.i(TAG, "http result lengh : " + response.getString().length());
		Log.v(TAG, "http result :\n " + response.getString());
		Log.d(TAG, response.toString());
	}

	private void toast(String s) {
		Toast.makeText(LiteHttpSamplesActivity.this, s, Toast.LENGTH_LONG).show();
	}

}
