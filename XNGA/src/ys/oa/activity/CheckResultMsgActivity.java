package ys.oa.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import ys.nlga.activity.R;
import ys.oa.activity.BaseActivity;
import ys.oa.activity.ChangePwdActivity;
import ys.oa.activity.LockApplication;
import ys.oa.util.Util;

public class CheckResultMsgActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_result_msg);
		
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("稽查结果查询");
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 显示九宫格解锁
		LockApplication.notShowLock = false;
	}

	public void back() {
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed() {
		CheckResultMsgActivity.this.finish();
		//back();
	}
	
}
