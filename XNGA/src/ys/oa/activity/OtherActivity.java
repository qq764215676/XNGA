package ys.oa.activity;

import ys.oa.util.Util;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import ys.nlga.activity.R;

/**
 * @author wf
 * @category 空白模块
 * 
 */
public class OtherActivity extends FragmentActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
	}

	public void initVar() {
	}

	public void initWidget() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		findViewById(R.id.gv_all_menu).setVisibility(View.GONE);
	}

	public void initListener() {
		// findViewById(R.id.ll_menu).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.ll_menu:
		// break;
		// }
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
