package ys.oa.activity;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.fragment.NewsFragment;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.Window;

import ys.nlga.activity.R;

public class NewsActivity extends FragmentActivity {
	private NewsFragment mContentFragment;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_news);
		Util.initExce(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		if (getIntent() != null && getIntent().getStringExtra("title") != null) {
			actionBar.setTitle(getIntent().getStringExtra("title"));
			if ("安全防范".equals(getIntent().getStringExtra("title"))) {
				mContentFragment = NewsFragment.newInstance("NLGA_SAFES");
			} else if ("线索征询".equals(getIntent().getStringExtra("title"))) {
				mContentFragment = NewsFragment.newInstance("NLGA_CLUES");
			} else if ("案情报道".equals(getIntent().getStringExtra("title"))) {
				mContentFragment = NewsFragment.newInstance("NLGA_NEWS_CIVIL");
			} else {
				mContentFragment = NewsFragment.newInstance();
			}
		} else {
			mContentFragment = NewsFragment.newInstance();
		}

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.pulldown_fade_in;
		options.headerOutAnimation = R.anim.pulldown_fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);

		mPullToRefreshAttacher.setRefreshing(false);
		// mContentFragment = NewsFragment.newInstance();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
