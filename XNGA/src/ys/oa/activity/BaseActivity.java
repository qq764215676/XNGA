package ys.oa.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.anim.dialog.MsgSlidingDialog;
import com.bugtags.library.Bugtags;
import com.lidroid.xutils.util.LogUtils;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.update.down.DownDB;
import com.update.down.MultiThreadDownload;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import ys.nlga.activity.R;
import ys.oa.listener.ScreenListener;
import ys.oa.listener.ScreenListener.ScreenStateListener;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;

public class BaseActivity extends FragmentActivity {

	private final static int TOASTTIME = 1000;
	private Intent intent = null;
	private String latestVersion;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Bugtags.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Bugtags.onResume(this);
		
		if (!LockApplication.isActive) {
			// app 从后台唤醒，进入前台
			LockApplication.isActive = true;
			if (LockApplication.isBtoF) {
				LockApplication.isBtoF = false;
				if (!LockApplication.isShowLock && !LockApplication.notShowLock) {
					intent = new Intent(getApplicationContext(), GestureLockActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		}
		if (LockApplication.sl == null) {
			LockApplication.sl = new ScreenListener(this);
			LockApplication.sl.begin(new ScreenStateListener() {

				@Override
				public void onUserPresent() {
					LogUtils.d("onUserPresent");
				}

				@Override
				public void onScreenOn() {
					LogUtils.d("onScreenOn");
					/** 排除第一次监听 显示亮屏 */
					LockApplication.ligthCount++;
					if (LockApplication.isActive && !LockApplication.isShowLock && (LockApplication.ligthCount > 1) && !LockApplication.notShowLock) {
						intent = new Intent(getApplicationContext(), GestureLockActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					}
				}

				@Override
				public void onScreenOff() {
					LogUtils.d("onScreenOff");
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!isAppOnForeground()) {
			// app 进入后台
			LockApplication.isActive = false;
			LockApplication.isBtoF = true;
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		Bugtags.onDispatchTouchEvent(this, ev);
		return super.dispatchTouchEvent(ev);
	}

	public void showToast(String msg) {
		T.showShort(getApplicationContext(), msg);
		Toast.makeText(getApplicationContext(), msg, TOASTTIME).show();
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}
	
	//获取版本更新信息
	public void getLatestVersion(){
//		new AsyncLoader().execute("update");
//		new UpdateAndDownloadTask(this,0).execute("update");
	}
	
		// 版本更新
		private DataSetList datasetlist;
		private String mApkAddress;
		private String downUrl;
		private String updateDetails = null;
		private String mCurrentVersionCode;
		//当前版本是否需要更新
		public static boolean isVersionLasted;

		class AsyncLoader extends AsyncTask<String, Integer, Integer> {
			@Override
			protected Integer doInBackground(String... params) {
				if ("update".equals(params[0])) {
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from XNGA_APP", "1", "APP_TIME DESC", "",
							"SEARCHYOUNGCONTENT", new DocInfor("", "XNGA_APP"), true, false));
					} catch (Exception e) {
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
					if (Util.isNetworkAvailable(BaseActivity.this)) {
						T.showSnack(BaseActivity.this, R.string.serverFailed);
					} else {
						T.showSnack(BaseActivity.this, R.string.networkerror);
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
						mCurrentVersionCode = Util.getVersionCode(BaseActivity.this);
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
						//add by fcr 
						double _mCurrentVersionCode = Double.parseDouble(mCurrentVersionCode);
						double _mVersionCode = Double.parseDouble(mVersionCode);
						if (!TextUtils.isEmpty(mVersionCode) && (_mCurrentVersionCode < _mVersionCode )) {
							//开发人员测试时，版本详情中有UPDATE_TEST_NOTIFY时不提示版本自动更新
//							if(updateDetails.equals(Constants.UPDATE_TEST_NOTIFY)){
//								isVersionLasted = false;
//							}else{
								isVersionLasted = true;
//							}
							
						} 
					}
					break;
				}
			}
		}

}
