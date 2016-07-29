package ys.oa.activity;

import ys.oa.civil.activity.CivilMainActivity;
import ys.oa.util.SpUtil;
import ys.oa.util.Util;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.msg.ToastMsg;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

public class WelcomeActivity extends BaseActivity implements OnClickListener {

	private ImageView mShowPicture;
	private TextView mShowText;
	private Animation mFadeInScale, mWelcomeBtn;
	private ImageButton mBtnCivil, mBtnPolice;
	private SpUtil mSpUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);

		mSpUtil = AppData.getInstance().getSpUtil();

		mShowPicture = (ImageView) findViewById(R.id.guide_picture);
		mShowText = (TextView) findViewById(R.id.guide_content);

		mFadeInScale = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
		mShowPicture.setImageDrawable(getResources().getDrawable(R.drawable.welcome));
		mShowText.setText(getResources().getString(R.string.app_name));
		mShowPicture.startAnimation(mFadeInScale);

		mFadeInScale.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
//				getLatestVersion();
			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
//				startActivity(new Intent(WelcomeActivity.this, CivilMainActivity.class));
//				overridePendingTransition(R.anim.welcome_fade_in, R.anim.welcome_fade_out);
//				onBackPressed();

				//if (mSpUtil.getIsAutoLogin() && Util.isNetworkAvailable(WelcomeActivity.this) && 
				//		mSpUtil.getBoolean("Logined", false)) {
				//	startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
				//} else {
					startActivity(new Intent(WelcomeActivity.this, LoginActivity.class).putExtra("isFromWelcome", true));
				//}
				finish();
				overridePendingTransition(R.anim.welcome_fade_in, R.anim.welcome_fade_out);
			}
		});

		// mWelcomeBtn = AnimationUtils.loadAnimation(this,
		// R.anim.welcome_fade_btn);
		// mBtnPolice = (ImageButton) findViewById(R.id.btn_police);
		// mBtnCivil = (ImageButton) findViewById(R.id.btn_civil);
		// mBtnPolice.setOnClickListener(this);
		// mBtnCivil.setOnClickListener(this);
		// mBtnPolice.startAnimation(mWelcomeBtn);
		// mBtnCivil.startAnimation(mWelcomeBtn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_police:
			//if (mSpUtil.getIsAutoLogin() && Util.isNetworkAvailable(this)) {
			//	startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
			//} else {
				startActivity(new Intent(WelcomeActivity.this, LoginActivity.class).putExtra("isFromWelcome", true));
			//}
			overridePendingTransition(R.anim.welcome_fade_in, R.anim.welcome_fade_out);
			break;
		case R.id.btn_civil:
			startActivity(new Intent(WelcomeActivity.this, CivilMainActivity.class));
			overridePendingTransition(R.anim.welcome_fade_in, R.anim.welcome_fade_out);
			break;
		}
	}

	// 点击两次返回键程序退出
	private long mExitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				// T.showShort(this, "再按一次退出程序");
				ToastMsg.showMsg(this, "再按一次退出程序");
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// splash界面不显示九宫格解锁
		LockApplication.notShowLock = true;
	}

	public void back() {
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed() {
		back();
	}

}
