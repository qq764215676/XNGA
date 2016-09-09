package ys.oa.activity;

import java.util.Timer;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.nlga.activity.R;
import ys.oa.fragment.QueryResultFragment;
import ys.oa.task.QueryResultListener;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

public class QueryResultListActivity extends BaseActivity
{

	private Context context;
	private QueryResultFragment mContentFragment;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	@Override
	protected void onCreate(Bundle arg0)
	{
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_result);
		context = this;
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("查询结果列表");

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.pulldown_fade_in;
		options.headerOutAnimation = R.anim.pulldown_fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);

		mPullToRefreshAttacher.setRefreshing(false);
		mContentFragment = QueryResultFragment.newInstance(context);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
	}

	public PullToRefreshAttacher getPullToRefreshAttacher()
	{
		return mPullToRefreshAttacher;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// 显示九宫格解锁
		LockApplication.notShowLock = false;
	}

	@Override
	public void onBackPressed()
	{
		QueryResultListActivity.this.finish();
		// back();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void back()
	{
		LockApplication.getInstance().exit();
	}
}
