package ys.oa.civil.activity;

import net.frakbot.jumpingbeans.JumpingBeans;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.CheckBox;
import com.material.widget.FloatingEditText;
import com.net.post.DataSetList;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 民用版登录
 */
public class CivilLoginActivity extends Activity implements OnClickListener, OnTouchListener {
	private FloatingEditText mLoginAccount, mLoginPsw;
	private CheckBox mIsKeepAccout, mIsAutologin;
	private ButtonFlat mLogin;
	private Drawable mPwdDrawable, mNameDrawable, mIconClear; // 清除文本内容图标
	private Context context = CivilLoginActivity.this;
	private String TAG = "LoginActivity";

	private View mFlLoading;
	private TextView mTvLoading;
	private JumpingBeans jumpingBeans;

	/*
	 * login-wf#PSW#123账号密码，null未记住账号 ;isKeepAccout-是否记住账号密码;
	 * isAutologin-是否自动登录;isPswShow-密码是否可见
	 */
	private SpUtil mSpUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_civil);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
	}

	@Override
	protected void onStart() {
		mIsKeepAccout.setChecked(mSpUtil.getIsKeepAccountCivil());
		mIsAutologin.setChecked(mSpUtil.getIsAutoLoginCivil());
		if (!TextUtils.isEmpty(mSpUtil.getAccountCivil()) && !TextUtils.isEmpty(mSpUtil.getPwdCivil())) {
			mLoginAccount.setText(mSpUtil.getAccountCivil());
			mLoginPsw.setText(mSpUtil.getPwdCivil());
		}

		if (!getIntent().getBooleanExtra("logout", false)) {
			if (mSpUtil.getIsAutoLoginCivil()) {
				if (Util.isNetworkAvailable(context)) {
					new AsyncLoader().execute("login", mLoginAccount.getText().toString(), mLoginPsw.getText()
							.toString());
				} else {
					T.showSnack(this, R.string.networkerror);
				}
			}
		}
		super.onStart();
	}

	public void initVar() {
		// 输入框右边清除图标
		mIconClear = getResources().getDrawable(R.drawable.clear_edit);
		mNameDrawable = getResources().getDrawable(R.drawable.ic_login_username);
		mPwdDrawable = getResources().getDrawable(R.drawable.ic_login_password);
		mSpUtil = AppData.getInstance().getSpUtil();
	}

	public void initWidget() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mLoginAccount = (FloatingEditText) findViewById(R.id.et_account);
		mLoginAccount.setCompoundDrawablesWithIntrinsicBounds(mNameDrawable, null, null, null);
		mLoginPsw = (FloatingEditText) findViewById(R.id.et_pwd);
		mLoginPsw.setCompoundDrawablesWithIntrinsicBounds(mPwdDrawable, null, null, null);
		mIsKeepAccout = (CheckBox) findViewById(R.id.cb_is_keep_psw);
		mIsAutologin = (CheckBox) findViewById(R.id.cb_is_autologin);
		mLogin = (ButtonFlat) findViewById(R.id.btn_login);
		mLogin.setRippleSpeed(30);
		mLogin.setRippleColor(Color.parseColor("#8dbfe7"));

		initBtnLoading();
	}

	public void initBtnLoading() {
		mFlLoading = findViewById(R.id.fl_loading);
		mTvLoading = (TextView) findViewById(R.id.tv_loading);
		jumpingBeans = new JumpingBeans.Builder().appendJumpingDots(mTvLoading).build();
		// jumpingBeans.stopJumping();
	}

	public void initListener() {
		mLogin.setOnClickListener(this);

		mLoginAccount.addTextChangedListener(mAccountClearTextChanged);
		mLoginAccount.setOnTouchListener(this);
		mLoginPsw.addTextChangedListener(mPwdClearTextChanged);
		mLoginPsw.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if (mIsAutologin.isChecked() && !mIsKeepAccout.isChecked()) {
				T.showSnack(this, "请选择记住密码");
			} else if (Util.isNetworkAvailable(context)) {
				new AsyncLoader().execute("login", mLoginAccount.getText().toString(), mLoginPsw.getText().toString());
			} else {
				T.showSnack(this, R.string.networkerror);
			}
			break;
		}
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

	public DataSetList datasetlist;

	class AsyncLoader extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			if ("login".equals(params[0])) {
				try {
					Log.i(TAG, params[1] + ", " + params[2] + "  登录");
					datasetlist = PostHttp.PostXML(XmlPackage.packageForLogin(params[1], params[2]));
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
				return 1;
			}
			return 0;
		}

		protected void onPreExecute() {
			mFlLoading.setVisibility(View.VISIBLE);
		}

		protected void onPostExecute(Integer result) {
			switch (result) {
			case -1:
				if (Util.isNetworkAvailable(context)) {
					Toast.makeText(context, getString(R.string.serverFailed), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					Constants.USERID = mLoginAccount.getText().toString();
					Constants.PSWID = mLoginPsw.getText().toString();
					if (mIsKeepAccout.isChecked()) {
						mSpUtil.setAccountCivil(mLoginAccount.getText().toString());
						mSpUtil.setPwdCivil(mLoginPsw.getText().toString());
						mSpUtil.setIsKeepAccountCivil(true);
					} else {
						mSpUtil.setAccountCivil("");
						mSpUtil.setPwdCivil("");
						mSpUtil.setIsKeepAccountCivil(false);
					}
					if (mIsAutologin.isChecked()) {
						mSpUtil.setIsAutoLoginCivil(true);
					} else {
						mSpUtil.setIsAutoLoginCivil(false);
					}
					setResult(RESULT_OK);
					onBackPressed();
				} else {
					mFlLoading.setVisibility(View.GONE);
					T.showSnack(CivilLoginActivity.this, "用户名或密码有误");
				}
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		mFlLoading.setVisibility(View.GONE);
		jumpingBeans.stopJumping();
		super.onDestroy();
	}

	private TextWatcher mAccountClearTextChanged = new TextWatcher() {
		private boolean isNull = true;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void afterTextChanged(Editable s) {
			if (TextUtils.isEmpty(s)) {
				if (!isNull) {
					mLoginAccount.setCompoundDrawablesWithIntrinsicBounds(mNameDrawable, null, null, null);
					isNull = true;
				}
				mLoginAccount.setHint("用户名");
				mLoginAccount.setHighlightedColor(getResources().getColor(R.color.bg_color));
			} else {
				if (isNull) {
					mLoginAccount.setCompoundDrawablesWithIntrinsicBounds(mNameDrawable, null, mIconClear, null);
					isNull = false;
				}
				if (!Util.isValidUsername(s.toString())) {
					mLoginAccount.setHint(getString(R.string.username_error));
					mLoginAccount.setHighlightedColor(getResources().getColor(android.R.color.holo_red_dark));
				} else {
					mLoginAccount.setHint("用户名");
					mLoginAccount.setHighlightedColor(getResources().getColor(R.color.bg_color));
				}
			}
		}
	};

	private TextWatcher mPwdClearTextChanged = new TextWatcher() {
		private boolean isNull = true;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (TextUtils.isEmpty(s)) {
				if (!isNull) {
					mLoginPsw.setCompoundDrawablesWithIntrinsicBounds(mPwdDrawable, null, null, null);
					isNull = true;
				}
			} else {
				if (isNull) {
					mLoginPsw.setCompoundDrawablesWithIntrinsicBounds(mPwdDrawable, null, mIconClear, null);
					isNull = false;
				}
			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			int curX = (int) event.getX();

			switch (v.getId()) {
			case R.id.et_account:
				if (curX > v.getWidth() - 100 && !TextUtils.isEmpty(mLoginAccount.getText())) {
					Util.ClearEdit(mLoginAccount, mNameDrawable, event);
					return true;
				}
				break;

			case R.id.et_pwd:
				if (curX > v.getWidth() - 100 && !TextUtils.isEmpty(mLoginPsw.getText())) {
					Util.ClearEdit(mLoginPsw, mPwdDrawable, event);
					return true;
				}
				break;
			}
			break;
		}
		return false;
	}
}