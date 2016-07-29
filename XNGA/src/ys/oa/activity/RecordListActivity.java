package ys.oa.activity;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.fragment.RecordHomeListFragment;
import ys.oa.fragment.RecordHouseListFragment;
import ys.oa.fragment.RecordInquestListFragment;
import ys.oa.fragment.RecordListFragment;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.devspark.msg.ToastMsg;
import ys.nlga.activity.R;

public class RecordListActivity extends FragmentActivity {
	private Fragment mContentFragment;
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

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.pulldown_fade_in;
		options.headerOutAnimation = R.anim.pulldown_fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);

		mPullToRefreshAttacher.setRefreshing(false);
		if ("出警记录表".equals(title)) {
			setTitle("出警记录");
			mContentFragment = RecordListFragment.newInstance();
		} else if ("现场勘查记录表".equals(title)) {
			setTitle("现场勘查");
			mContentFragment = RecordInquestListFragment.newInstance();
		} else if ("出租屋检查表".equals(title)) {
			setTitle("出租屋检查");
			mContentFragment = RecordHouseListFragment.newInstance();
		} else if ("入户登记表".equals(title)) {
			setTitle("入户登记");
			mContentFragment = RecordHomeListFragment.newInstance();
		}
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
			if ("出警记录表".equals(title)) {
				startActivityForResult(new Intent(this, RecordActivity.class).putExtra("Title", title), 0);
			} else if ("现场勘查记录表".equals(title)) {
				startActivityForResult(new Intent(this, RecordInquestActivity.class).putExtra("Title", title), 0);
			} else if ("出租屋检查表".equals(title)) {
				startActivityForResult(new Intent(this, RecordHouseActivity.class).putExtra("Title", title), 0);
			} else if ("入户登记表".equals(title)) {
				startActivityForResult(new Intent(this, RecordHomeActivity.class).putExtra("Title", title), 0);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		ToastMsg.showMsgTop(RecordListActivity.this, "上传成功");
		switch (requestCode) {
		case 0:
			if ("出警记录表".equals(title)) {
				((RecordListFragment) mContentFragment).loadFirstPage();
			} else if ("现场勘查记录表".equals(title)) {
				((RecordInquestListFragment) mContentFragment).loadFirstPage();
			} else if ("出租屋检查表".equals(title)) {
				((RecordHouseListFragment) mContentFragment).loadFirstPage();
			} else if ("入户登记表".equals(title)) {
				((RecordHomeListFragment) mContentFragment).loadFirstPage();
			}
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
