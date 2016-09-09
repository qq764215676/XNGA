package ys.oa.activity;

import ys.nlga.activity.R;
import ys.oa.fragment.ScanCodeFragment;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanCodeActivity extends BaseActivity implements View.OnClickListener
{

	private Context context;
	private FragmentTransaction mFragTransaction;
	public static ScanCodeFragment scanCodeFragment;

	private View mQrCodeView;
	private ImageView ivTitleBtnLeft;
	private ImageView ivTitleBtnRight;
	private TextView mTitleText;
	private long mExitTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scancode_activity);
		context = this;
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		initTab();
		initWidget();
	}

	public void initTab()
	{
		scanCodeFragment = ScanCodeFragment.getInstance();
		mFragTransaction = getSupportFragmentManager().beginTransaction();
		mFragTransaction.add(R.id.fl_scancode, scanCodeFragment).commit();
	}

	public void initWidget()
	{
		mQrCodeView = findViewById(R.id.fl_scancode);
	}

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

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		/*
		 * case R.id.ivTitleBtnRight: if (popupwindow != null &&
		 * popupwindow.isShowing()) { popupwindow.dismiss(); popupwindow = null;
		 * return; } else { initmPopupWindowView(); if (Constant.screenHeight >
		 * 1100) { popupwindow.showAsDropDown(v, 0, 25); } else {
		 * popupwindow.showAsDropDown(v, 0, 20); }
		 * 
		 * } break; case R.id.pop_exit: System.exit(0); if (popupwindow != null
		 * && popupwindow.isShowing()) { popupwindow.dismiss(); popupwindow =
		 * null; return; } break;
		 */
		}
	}

	/*
	 * public boolean onKeyDown(int keyCode, KeyEvent event) { if (keyCode ==
	 * KeyEvent.KEYCODE_BACK) { if ((System.currentTimeMillis() - mExitTime) >
	 * 2000) { Toast.makeText(this, "R.string", Toast.LENGTH_SHORT).show();
	 * mExitTime = System.currentTimeMillis(); } else { System.exit(0); } return
	 * true; } return super.onKeyDown(keyCode, event); }
	 */

}
