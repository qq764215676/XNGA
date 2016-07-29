package ys.oa.fragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.holoeverywhere.app.ProgressDialog;

import ys.nlga.activity.R;
import ys.oa.activity.ChangePwdActivity;
import ys.oa.activity.LockApplication;
import ys.oa.activity.LoginActivity;
import ys.oa.activity.MainActivity;
import ys.oa.activity.WelcomeActivity;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.task.UpdateAndDownloadTask;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anim.dialog.MsgSlidingDialog;
import com.anim.dialog.MsgSlidingDialog.OnMsgClickListener;
import com.gc.materialdesign.views.LayoutRipple;
import com.gc.materialdesign.views.Switch;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.nineoldandroids.view.ViewHelper;
import com.refactech.driibo.AppData;
import com.update.down.DownDB;
import com.update.down.FileDownUtil;
import com.update.down.MultiThreadDownload;

public class DrawerFragment extends Fragment implements OnClickListener {
	private LayoutRipple mLrLogin, mLrLogout, mLrIsAutologin, mChangePwd, mGesture, mLrClean, mLrUpdate;
	private ImageButton mBtnLogin;
	private TextView mTvLoginName;
	private Switch mCbIsAutologin;
	private SpUtil mSpUtil;
	private MainActivity mainActivity;
	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	private boolean isLogin;
	
	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	//make sure class name exists, is public, 
	//and has an empty constructor that is public
	public DrawerFragment(){    }
	
	private android.app.ProgressDialog dialog = null;
	
	/**
	* 静态工程方法
	* @return
	*/
	public static DrawerFragment getInstance(ImageButton mBtnLogin){
		DrawerFragment df = new DrawerFragment();
        df.mBtnLogin = mBtnLogin;
        return df;
    }
	

/*	public  DrawerFragment(boolean isLogin) {
		this.isLogin = isLogin;
	}
	*/
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			boolean isFinish = (Boolean) msg.obj;
			if(isFinish) {
				Toast.makeText(mainActivity, "清理完成！", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mainActivity, "清理失败！", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_drawer, null);
		mainActivity = (MainActivity) getActivity();
		mSpUtil = AppData.getInstance().getSpUtil();

		mTvLoginName = (TextView) v.findViewById(R.id.tv_login_name);
		mBtnLogin = (ImageButton) v.findViewById(R.id.btn_login);

		mLrLogout = (LayoutRipple) v.findViewById(R.id.lr_logout);
		mLrLogout.setRippleSpeed(40);
		mLrLogout.setOnClickListener(this);
		mLrIsAutologin = (LayoutRipple) v.findViewById(R.id.lr_is_autologin);
		mLrIsAutologin.setRippleSpeed(40);
		mLrIsAutologin.setOnClickListener(this);
		mLrUpdate = (LayoutRipple) v.findViewById(R.id.lr_update);
		mLrUpdate.setRippleSpeed(40);
		mLrUpdate.setOnClickListener(this);
		mChangePwd = (LayoutRipple) v.findViewById(R.id.lr_change_pwd);
		mChangePwd.setRippleSpeed(40);
		mChangePwd.setOnClickListener(this);
		mGesture = (LayoutRipple) v.findViewById(R.id.lr_gesture);
		mGesture.setRippleSpeed(40);
		mGesture.setOnClickListener(this);
		mLrClean = (LayoutRipple) v.findViewById(R.id.lr_clean);
		mLrClean.setRippleSpeed(40);
		mLrClean.setOnClickListener(this);
		mLrLogin = (LayoutRipple) v.findViewById(R.id.lr_login);
		setOriginRiple(mLrLogin);
		mLrLogin.setOnClickListener(this);

		mCbIsAutologin = (Switch) v.findViewById(R.id.cb_is_autologin);
		mCbIsAutologin.setChecked(mSpUtil.getIsAutoLogin());

		if (mSpUtil.getIsAutoLogin()) {
			if (Util.isNetworkAvailable(mainActivity)) {
				new AsyncLoaderLogin().execute("login", mSpUtil.getAccount(), mSpUtil.getPwd());
			} else {
				T.showSnack(mainActivity, R.string.networkerror);
			}
		}

		if (isLogin) {
			loginDrawer();
		}
		return v;
	}

	private void setOriginRiple(final LayoutRipple layoutRipple) {
		layoutRipple.post(new Runnable() {

			@Override
			public void run() {
				View v = layoutRipple.getChildAt(0);
				layoutRipple.setxRippleOrigin(ViewHelper.getX(v) + v.getWidth() / 2);
				layoutRipple.setyRippleOrigin(ViewHelper.getY(v) + v.getHeight() / 2);
				layoutRipple.setRippleColor(Color.parseColor("#212121"));
				layoutRipple.setRippleSpeed(40);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lr_login:
			if ("立即登陆".equals(mTvLoginName.getText().toString())) {
				startActivityForResult(new Intent(mainActivity, LoginActivity.class).putExtra("logout", true), 0);
				// mCbIsAutologin.setChecked(false);
			}
			break;
		case R.id.lr_logout:
			mBtnLogin.setBackgroundResource(R.drawable.drawer_head_logo);
//			mTvLoginName.setText("立即登陆");
			mainActivity.back();
			LockApplication.getInstance().finishAll();
			startActivity(new Intent(mainActivity, LoginActivity.class).putExtra("isFromWelcome", true));
			
			break;
		case R.id.lr_is_autologin:
			mCbIsAutologin.setChecked(!mCbIsAutologin.isChecked());
			mSpUtil.setIsAutoLogin(mCbIsAutologin.isChecked());
			break;
		case R.id.lr_change_pwd:
			//T.showShort(mainActivity, "暂未开通该功能");
			mainActivity.closeRightDrawer();
			startActivity(new Intent(mainActivity, ChangePwdActivity.class).putExtra("userContentId", getArguments().getString("userContentId")));
			break;
		case R.id.lr_gesture:
			//T.showShort(mainActivity, "暂未开通该功能");
			mainActivity.closeRightDrawer();
			//将进入onResume次数清零
			LockApplication.mainCount = 0;
			//清空锁屏密码
			mSpUtil.putString(mSpUtil.getString("Gesture",null) + "_Lockkey", "");
			//设置初始化，状态从登录页面进入
			mSpUtil.getBoolean("Flogin", true);
			startActivity(new Intent(mainActivity, MainActivity.class));
			break;
		case R.id.lr_clean:
			// mHelpDialog.show();
			mainActivity.closeRightDrawer();
			dialog = Util.getHoloProgressDialog(AppData.getContext());
			
			boolean isFinish = Util.cleanCache();
			Log.v("fcr","isFinish---"+isFinish);
			Message m = new Message();
			m.obj = isFinish;
			handler.sendMessage(m);
			
			break;
		case R.id.lr_update:
			// mainActivity.checkUpdate(true);
			// T.showSnack(mainActivity, "已更新到最新版本！！！");
			mainActivity.closeRightDrawer();
			if (Util.isNetworkAvailable(mainActivity)) {
//				new AsyncLoader().execute("update");
				updateTask();
			} else {
				new MsgSlidingDialog(mainActivity, MsgSlidingDialog.ERROR_TYPE).setTitleText("请检查您的网络连接!")
						.setTitleTextSize(19).show();
			}
			break;
		}
	}
	
	public void updateTask() {
		new UpdateAndDownloadTask(mainActivity,1).execute("update");
	}

	private Handler h;
	private int i = -1;
//	private MsgSlidingDialog checkUpdateDialog, isUpdateDialog, updatingDialog;

	public void loginDrawer() {
		mBtnLogin.setBackgroundResource(R.drawable.drawer_head_default);
		mTvLoginName.setText(Constants.USER_ID);
		mCbIsAutologin.setChecked(mSpUtil.getIsAutoLogin());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == 0) { // 登陆页返回
			mBtnLogin.setBackgroundResource(R.drawable.drawer_head_default);
			mTvLoginName.setText(Constants.USER_ID);
			mCbIsAutologin.setChecked(mSpUtil.getIsAutoLogin());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 版本更新
	private DataSetList datasetlist;
//	private String mApkAddress;
//	private String downUrl;
//	private String updateDetails = null;
//	private File downFile;
//	private String mCurrentVersionCode;
//	private DownDB mDownDb;
	private MultiThreadDownload mtd;

	/*class AsyncLoader extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			if ("update".equals(params[0])) {
			try {
				datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from TEST_XNGA_APP", "1", "APP_TIME DESC", "",
						"SEARCHYOUNGCONTENT", new DocInfor("", "TEST_XNGA_APP"), true, false));
				} catch (Exception e) {
					return -1;
				}
				return 1;
			}
			return 0;
		}

		protected void onPreExecute() {
			if (!checkUpdateDialog.isShowing()) {
				checkUpdateDialog.show();
			}
		}

		protected void onPostExecute(Integer result) {
			switch (result) {
			case -1:
				if (Util.isNetworkAvailable(mainActivity)) {
					T.showSnack(mainActivity, R.string.serverFailed);
				} else {
					T.showSnack(mainActivity, R.string.networkerror);
				}
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
					ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
					ArrayList<String> DOCUMENTID = (ArrayList<String>) datasetlist.DOCUMENTID;
					String mVersionCode = null;
					String mVersionName = null;
					String updateTimes = null;
					mCurrentVersionCode = Util.getVersionCode(mainActivity);
					for (int i = 0; i < nameList.size(); i++) {
						if ("APP_VC".equals(nameList.get(i))) {
							mVersionCode = valueList.get(i);
							mApkAddress = DOCUMENTID.get(0);
						} else if ("APP_VN".equals(nameList.get(i))) {
							mVersionName = valueList.get(i);
						} else if ("APP_DETAIL".equals(nameList.get(i))) {
							updateDetails = valueList.get(i);
						} else if ("APP_TIME".equals(nameList.get(i))) {
							updateTimes = valueList.get(i);
						}
					}
					
					System.out.println("mCurrentVersion "+mCurrentVersionCode);
					System.out.println("mVersionCode "+ mVersionCode);
					//add by fcr for compare versionCode 
					double _mCurrentVersionCode = Double.parseDouble(mCurrentVersionCode);
					double _mVersionCode = Double.parseDouble(mVersionCode);
					if (!TextUtils.isEmpty(mVersionCode) && (_mCurrentVersionCode < _mVersionCode) ) {
						// 如果当前版本号小于服务器返回的版本号就弹出提示对话框
						downUrl = "http://" + Constants.CONNIP + "/IDOC/service/file/" + mApkAddress + "/" + "XNGA_"
								+ mVersionName + ".apk";
						showIsUpdateDialog();// 显示更新对话框
					} else {
						checkUpdateDialog.setCancelable(true);
						checkUpdateDialog.setTitleText("已更新到最新版本!").setTitleTextSize(19)
								.changeAlertType(MsgSlidingDialog.SUCCESS_TYPE);
					}
				}
				break;
			}
			if (checkUpdateDialog.isShowing() && checkUpdateDialog.getAlerType() != MsgSlidingDialog.SUCCESS_TYPE) {
				checkUpdateDialog.cancel();
			}
		}
	}*/

	/**
	 * 弹出对话框提示下载更新
	 */
/*	public  void showIsUpdateDialog() {
		mDownDb = new DownDB(mainActivity);
		isUpdateDialog = new MsgSlidingDialog(mainActivity, MsgSlidingDialog.NORMAL_TYPE);
		isUpdateDialog.setTitleText("★有新版本发布★").setTitleTextSize(23).setContentText(updateDetails.toString())
				.setConfirmText("立即更新").setCancelText("稍后更新").setConfirmClickListener(new OnMsgClickListener() {

					@Override
					public void onClick(MsgSlidingDialog msgSlidingDialog) {
						System.out.println("url---->" + downUrl);
						// 新建应用文件夹
						File fileDir = new File(Constants.localAPKPath);
						if (!fileDir.exists())
							fileDir.mkdirs();
						downFile = new File(Constants.localAPKPath + FileDownUtil.getFileName(downUrl));
						if (downFile.exists() && FileDownUtil.isFileUsed(mainActivity, downFile)) {
							mDownDb.delete(downUrl);
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(downFile), "application/vnd.android.package-archive");
							startActivity(intent);
						} else {
							mtd = new MultiThreadDownload(mainActivity, handler, downUrl, Constants.localAPKPath, 1);
							mtd.start();
						}
					}
				});
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (!isUpdateDialog.isShowing()) {
					isUpdateDialog.show();
				}
			}
		}, 50);
	}*/

	/*private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FileDownUtil.startDownloadMeg:
				showUpdatingDialog();
				mtd.stoped = false;
				break;
			case FileDownUtil.updateDownloadMeg:
				updatingDialog.setTitleText("正在下载 " + (((int) mtd.getDownloadSize() * 100) / ((int) mtd.getFileSize()))
						+ "%");
				break;
			case FileDownUtil.stopDownloadMeg:
				// T.showShort(mainActivity, "文件已停止下载");
				updatingDialog.cancel();
				mDownDb = new DownDB(mainActivity);
				mDownDb.delete(downUrl);
				break;
			case FileDownUtil.endDownloadMeg:
				updatingDialog.setTitleText("下载完成,是否安装?").setTitleTextSize(19).setConfirmText("安装")
						.setConfirmClickListener(new OnMsgClickListener() {

							@Override
							public void onClick(MsgSlidingDialog msgSlidingDialog) {
								if (FileDownUtil.getFileType(downUrl).equals("apk")) {
									 apk安装界面跳转 
									String filename = FileDownUtil.getFileName(downUrl);
									String fileAbsoluteName = Constants.localAPKPath + filename;
									Intent intent = new Intent(Intent.ACTION_VIEW);
									intent.setDataAndType(Uri.fromFile(new File(fileAbsoluteName)),
											"application/vnd.android.package-archive");
									startActivity(intent);
								}
								msgSlidingDialog.cancel();
							}
						}).setCancelText("取消").setCancelClickListener(new OnMsgClickListener() {

							@Override
							public void onClick(MsgSlidingDialog msgSlidingDialog) {
								msgSlidingDialog.cancel();
							}
						}).changeAlertType(MsgSlidingDialog.SUCCESS_TYPE);
				break;
			case FileDownUtil.downloadTimeOut:
				T.showShort(mainActivity, "文件有误，请重新下载");
				updatingDialog.cancel();
				mDownDb = new DownDB(mainActivity);
				mDownDb.delete(downUrl);
				break;
			case FileDownUtil.networkNotAvailable:
				T.showShort(mainActivity, R.string.networkerror);
				updatingDialog.cancel();
				mDownDb = new DownDB(mainActivity);
				mDownDb.delete(downUrl);
				break;
			default:
				break;
			}
		}
	};*/

	/*private void showUpdatingDialog() {
		updatingDialog = new MsgSlidingDialog(mainActivity, MsgSlidingDialog.PROGRESS_TYPE).setTitleText("正在检查更新…")
				.setTitleTextSize(19).setConfirmText("取消").setConfirmClickListener(new OnMsgClickListener() {

					@Override
					public void onClick(MsgSlidingDialog msgSlidingDialog) {
						mtd.stoped = true;
						msgSlidingDialog.cancel();
					}
				});
		updatingDialog.setCancelable(false);
		isUpdateDialog.cancel();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (!updatingDialog.isShowing()) {
					updatingDialog.show();
				}
			}
		}, 50);
	}*/

	class AsyncLoaderLogin extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			if ("login".equals(params[0])) {
				try {
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
		}

		protected void onPostExecute(Integer result) {
			switch (result) {
			case -1:
				if (Util.isNetworkAvailable(mainActivity)) {
					Toast.makeText(mainActivity, getString(R.string.serverFailed), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mainActivity, getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					Constants.USER_ID = mSpUtil.getAccount();
					//Constants.USER_PWD = mSpUtil.getPwd();
					mBtnLogin.setBackgroundResource(R.drawable.drawer_head_default);
					mTvLoginName.setText(Constants.USER_ID);
				} else {
					if ("立即登陆".equals(mTvLoginName.getText().toString())) {
						startActivityForResult(new Intent(mainActivity, LoginActivity.class).putExtra("logout", true)
								.putExtra("isFailedAutoLogin", true), 0);
						mainActivity.onBackPressed();
					}
				}
				break;
			}
		}
	}
}
