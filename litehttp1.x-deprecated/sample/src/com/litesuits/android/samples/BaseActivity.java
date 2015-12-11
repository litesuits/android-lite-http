package com.litesuits.android.samples;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.litesuits.android.log.Log;
import com.litesuits.http.R;

/**
 * 动态添加按钮和点击事件
 * 
 * @author MaTianyu
 * 2014-2-25下午2:36:30
 */
public abstract class BaseActivity extends Activity {
	protected String TAG = "BaseActivity";
	//protected TextView mTvSubTitle;
	protected TextView mTitle;
	protected ListView mListview;
	protected BaseAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_list_btn);
		TAG = this.getClass().getSimpleName();
		Log.setTag(TAG);

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(getMainTitle());
		//		mTvSubTitle = (TextView) findViewById(R.id.sub_title);
		//mTvSubTitle = new TextView(this);
		//mTvSubTitle.setPadding(20, 10, 10, 20);

		mListview = (ListView) findViewById(R.id.listview);
		//mListview.addHeaderView(mTvSubTitle);
		mAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.tv_item, getResources().getStringArray(
				R.array.http_test_list));
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Runnable run = getRunnable(position - mListview.getHeaderViewsCount());
				if (run != null) new Thread(run).start();
			}
		});
	}

	public abstract String getMainTitle();

	public abstract String[] getStringList();

	public abstract Runnable getRunnable(final int pos);

	public void setSubTitile(String st) {
		//mTvSubTitle.setText(st);
	}
}
