package ys.oa.activity;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.fragment.CollectBikeFragment;
import ys.oa.util.Constants;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.devspark.msg.ToastMsg;
import ys.nlga.activity.R;

public class CollectBikeListActivity extends FragmentActivity {
	private CollectBikeFragment mContentFragment;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_news);
		Util.initExce(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		title = getIntent().getStringExtra("Title");
		if ("自行车检查".equals(title)) {
			Constants.vehicleType = "1";
		} else if ("电动车检查".equals(title)) {
			Constants.vehicleType = "0";
		}

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.pulldown_fade_in;
		options.headerOutAnimation = R.anim.pulldown_fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);

		mPullToRefreshAttacher.setRefreshing(false);
		setTitle(title);
		mContentFragment = CollectBikeFragment.newInstance();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();

		// ToastMsg.showSticky(RecordListActivity.this, "有新的版本,快更新吧!");
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_add:
			startActivityForResult(new Intent(this, CollectBikeActivity.class), 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		ToastMsg.showMsgTop(CollectBikeListActivity.this, "上传成功");
		switch (requestCode) {
		case 0:
			mContentFragment.loadFirstPage();
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
