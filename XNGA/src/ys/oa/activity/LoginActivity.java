package ys.oa.activity;

import net.frakbot.jumpingbeans.JumpingBeans;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.CheckBox;
import com.material.widget.FloatingEditText;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 警用版登录
 */
@SuppressLint("ResourceAsColor")
public class LoginActivity extends BaseActivity implements OnClickListener, OnTouchListener {
	private FloatingEditText mLoginAccount, mLoginPsw;
	private CheckBox mIsKeepAccout, mIsAutologin;
	private ButtonFlat mLogin;
	private Drawable mPwdDrawable, mNameDrawable, mIconClear; // 清除文本内容图标
	private Context context = LoginActivity.this;
	private String TAG = "LoginActivity";

	private View mFlLoading;
	private TextView mTvLoading;
	private JumpingBeans jumpingBeans;
	
	//手机登录使用用户名、密码、设备类型、DEVICE_ID及SIM卡号
	//平板登录使用用户名、密码、设备类型
	private String deviceId; //设备唯一标识码
	private String simSerialNumber; //SIM卡号
	private String androidId; //设备首次启动时系统会随机生成的64位的数字

	/*
	 * login-wf#PSW#123账号密码，null未记住账号 ;isKeepAccout-是否记住账号密码;
	 * isAutologin-是否自动登录;isPswShow-密码是否可见
	 */
	private SpUtil mSpUtil;
	private boolean isNeedCleanFile = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		
		initVar();
		initWidget();
		initListener();
		cleanSQLite();
	}

	@Override
	protected void onStart() {
		//设置用户名可见
		if (!TextUtils.isEmpty(mSpUtil.getAccount())) {
			mLoginAccount.setText(mSpUtil.getAccount());
			//mLoginPsw.setText(mSpUtil.getPwd());
		}

		if (!getIntent().getBooleanExtra("logout", false)) {
			if (mSpUtil.getIsAutoLogin()) {
				if (Util.isNetworkAvailable(context)) {
					//Updated at 2016/03/22
					//根据客户需求，应用登陆名在前段不区分大小写，服务器中用户名如有英文字母则用大写
					//如用户输入xj001或XJ001，都以XJ001在数据库中查找
					new AsyncLoader().execute("login", mLoginAccount.getText().toString().toUpperCase(), Util.md5(mLoginPsw.getText()
							.toString()));
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
		
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		simSerialNumber = tm.getSimSerialNumber(); 
		androidId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID); 
		System.out.println("设备码 "+ deviceId + " SIM卡号 " + simSerialNumber + " Android Id " +androidId);
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
		/*mLoginAccount.setText("fcr");
		mLoginPsw.setText("123456");*/

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
				//Updated at 2016/03/22
				//根据客户需求，应用登陆名在前段不区分大小写，服务器中用户名如有英文字母则用大写
				//如用户输入xj001或XJ001，都以XJ001在数据库中查找
				new AsyncLoader().execute("login", mLoginAccount.getText().toString().toString().toUpperCase(), Util.md5(mLoginPsw.getText().toString()));
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
					//datasetlist = PostHttp.PostXML(XmlPackage.packageForLogin(params[1], params[2]));
					Log.v(TAG,"delete files");
					if(isNeedCleanFile) {
						Util.deleteDirectory(Constants.localPath);
					}
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from XNGA_USER where USER_ID='"
							+ params[1] + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "XNGA_USER"), true,
							false));
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
					ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
					ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
					ArrayList<String> documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
					ArrayList<String> contentId = (ArrayList<String>) datasetlist.CONTENTID;
					
					//判断用户名是否存在
					if(nameList==null || nameList.size()==0){
					//if("login".equals(nameList.get(0))){
						mFlLoading.setVisibility(View.GONE);
						T.showSnack(LoginActivity.this, "用户不存在");	
						
					}else{
						//获取用户在服务器上用户表中的ID
						String userContentId = contentId.get(0);
						//判断密码是否正确
						if(valueList.get(7).equals(Util.md5(mLoginPsw.getText().toString()))){
							//Constants.USERID = mLoginAccount.getText().toString();
							//Constants.PSWID = mLoginPsw.getText().toString();
							Constants.USER_ID = mLoginAccount.getText().toString().toUpperCase();
							Constants.USER_PWD = mLoginPsw.getText().toString();
							Constants.USER_AUTH = valueList.get(6);
							
							//保存用户名但不保存密码
							//每次进入系统必须输入密码
							mSpUtil.setAccount(mLoginAccount.getText().toString().toUpperCase());
							mSpUtil.setPwd(mLoginPsw.getText().toString());
							mSpUtil.setAuth(valueList.get(6));
							
							/*for (int i = 0; i < nameList.size(); i++)
							{
								Log.i("log.i", "nameList.get("+i+")="+nameList.get(i)+"="+valueList.get(i));
							}
							Log.i("log.i", "mSpUtil.setAuth(valueList.get(6))，valueList.get(6)="+valueList.get(6));
							Log.i("log.i", "AppData.getInstance().getSpUtil().getAuth()="+AppData.getInstance().getSpUtil().getAuth());*/
							
							//mSpUtil.setPwd("");
							//mSpUtil.setIsKeepAccount(true);
							
//							if (mIsKeepAccout.isChecked()) {
//								mSpUtil.setAccount(mLoginAccount.getText().toString());
//								mSpUtil.setPwd(mLoginPsw.getText().toString());
//								mSpUtil.setIsKeepAccount(true);
//							} else {
//								mSpUtil.setAccount("");
//								mSpUtil.setPwd("");
//								mSpUtil.setIsKeepAccount(false);
//							}
//							if (mIsAutologin.isChecked()) {
//								mSpUtil.setIsAutoLogin(true);
//							} else {
//								mSpUtil.setIsAutoLogin(false);
//							}
							if (getIntent() != null
									&& (getIntent().getBooleanExtra("isFromWelcome", false) || getIntent().getBooleanExtra(
											"isFailedAutoLogin", false))) {
								
								//设备类型。1为Android手机，2为iOS手机，3为Android平板，4为iOS平板
								String devType = valueList.get(4);
//								devType="3";
								//用户使用手机端
								if (devType.equals("1") || devType.equals("2")){
									//检测用户表DEVICE_ID及SIM卡号是否齐全
									//ValueList 1,设备唯一码； 3，SIM卡号
									if(valueList.get(1).equals(deviceId) && valueList.get(3).equals(simSerialNumber)){
										//startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isLogin", true));
										
										mSpUtil.getString("Gesture", "1234");
										// 已经登陆过，下次splash自动登录
										mSpUtil.putBoolean("Logined", true);
										// 从登陆界面进入主界面
										mSpUtil.putBoolean("Flogin", true);
										// 解锁时间归0
										mSpUtil.putLong(mSpUtil.getString("Gesture",null) + "_DataNum", 0);
										startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isLogin", true).putExtra("userContentId", userContentId));
										LoginActivity.this.finish();
										
										//如果还未设置锁屏密码
										//if(mSpUtil.getGestureLock().equals("")){
										//	startActivity(new Intent(LoginActivity.this, LockActivity.class));
										//	LoginActivity.this.finish();
										//}
										//如果已设置锁屏密码直接进入主界面
										//else{
										//	startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isLogin", true));
										//}
									}else{
										mFlLoading.setVisibility(View.GONE);
										T.showSnack(LoginActivity.this, "用户未在后台注册或注册信息有误！");
									}
								}
								//用户使用平板设备
								else if (devType.equals("3") || devType.equals("4")){
									mSpUtil.getString("Gesture", "1234");
									// 已经登陆过，下次splash自动登录
									mSpUtil.putBoolean("Logined", true);
									// 从登陆界面进入主界面
									mSpUtil.putBoolean("Flogin", true);
									// 解锁时间归0
									mSpUtil.putLong(mSpUtil.getString("Gesture",null) + "_DataNum", 0);
									startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isLogin", true).putExtra("userContentId", userContentId));
									LoginActivity.this.finish();
								}
								//其他类型
								else{
									T.showSnack(LoginActivity.this, "用户未在后台注册或注册信息有误！");
								}
								
//								//ValueList 1,设备唯一码； 3，SIM卡号
//								if(valueList.get(1).equals(androidId) && valueList.get(3).equals(simSerialNumber)){
//									//startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isLogin", true));
//									
//									mSpUtil.getString("Gesture", "1234");
//									// 已经登陆过，下次splash自动登录
//									mSpUtil.putBoolean("Logined", true);
//									// 从登陆界面进入主界面
//									mSpUtil.putBoolean("Flogin", true);
//									// 解锁时间归0
//									mSpUtil.putLong(mSpUtil.getString("Gesture",null) + "_DataNum", 0);
//									startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isLogin", true).putExtra("userContentId", userContentId));
//									LoginActivity.this.finish();
//									
//									//如果还未设置锁屏密码
//									//if(mSpUtil.getGestureLock().equals("")){
//									//	startActivity(new Intent(LoginActivity.this, LockActivity.class));
//									//	LoginActivity.this.finish();
//									//}
//									//如果已设置锁屏密码直接进入主界面
//									//else{
//									//	startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("isLogin", true));
//									//}
//								}else{
//									mFlLoading.setVisibility(View.GONE);
//									T.showSnack(LoginActivity.this, "用户未在后台注册！");
//								}
								
							} else {
								setResult(RESULT_OK);
							}
							//onBackPressed();
						}else{
							mFlLoading.setVisibility(View.GONE);
							T.showSnack(LoginActivity.this, "密码不正确");
						}
					}
					
				} else {
					mFlLoading.setVisibility(View.GONE);
					T.showSnack(LoginActivity.this, "系统网络异常");
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
	
	@Override
	protected void onResume() {
		super.onResume();
		// 登陆界面不显示九宫格解锁
		LockApplication.notShowLock = true;
	}

	public void back() {
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed() {
		back();
	}
	
	//add by fcr at 2016-01-29
	
	public void cleanSQLite() {
		int lastAppVersion = 0;
		String lastLoginDate = null;
		if(mSpUtil == null) {
			mSpUtil =  AppData.getInstance().getSpUtil();
		}
		try {
			lastAppVersion = mSpUtil.getAppVersion();
			lastLoginDate = mSpUtil.getLastLoginDate();
		}catch(Exception e) {
			lastAppVersion = 0;
			lastLoginDate = null;
		}
		int mCurrentVersionCode = AppData.getVersionCode(this);
		String mCurrentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		Log.v(TAG,"lastLoginDate---"+lastLoginDate+",mCurrentDate---"+mCurrentDate);
		mSpUtil.putAppVersion(mCurrentVersionCode);
		mSpUtil.putLastLoginDate(mCurrentDate);
		if(lastAppVersion==0 || lastAppVersion<mCurrentVersionCode || lastLoginDate == null || !lastLoginDate.equals(mCurrentDate)) {
			//add by fcr  清掉数据库数据
			EnforcementDataManager m = EnforcementDataManager.getInstance(this);
			m.cleanAllDate();
			isNeedCleanFile = true;
		}
	}
	
	//add end
}