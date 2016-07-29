package com.refactech.driibo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.holoeverywhere.app.Application;

import ys.oa.provider.EnforcementDataManager;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.bugtags.library.Bugtags;

/**
 * wf Application
 */
public class AppData extends Application {
	private static AppData mApplication;
	private static Context sContext;
	public static final String SP_FILE_NAME = "sp_menu";
	private SpUtil mSpUtil;

	@Override
	public void onCreate() {
		super.onCreate();
		Bugtags.start("ec7a6cb06c6a0c1a0111f7bdb505ff5e", this, Bugtags.BTGInvocationEventBubble);
		
		mApplication = this;
		sContext = getApplicationContext();
		myListener = new MyLocationListenner();
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(new MyLocationListenner());
		File file = new File(Constants.localPath);
		if(!file.exists()) {
			file.mkdirs();
		}
	}

	public static Context getContext() {
		return sContext;
	}

	public synchronized static AppData getInstance() {
		return mApplication;
	}

	public synchronized SpUtil getSpUtil() {
		if (mSpUtil == null)
			mSpUtil = new SpUtil(this, SP_FILE_NAME);
		return mSpUtil;
	}

	public LocationClient mLocationClient;
	public String loc = "", locDistrict = "";
	public MyLocationListenner myListener;

	/**
	 * 监听定位
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				loc = getSharedPreferences("skin", MODE_PRIVATE).getString("loc", "");
				locDistrict = getSharedPreferences("skin", MODE_PRIVATE).getString("locDistrict", "");
				return;
			}
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				if (location.hasAddr()) {
					loc = location.getAddrStr();
					locDistrict = location.getProvince() + location.getCity() + location.getDistrict();
				}
				SharedPreferences sp = getSharedPreferences("skin", MODE_PRIVATE);
				if (sp.getString("loc", null) == null || sp.getString("loc", null) != loc) {
					sp.edit().putString("loc", loc).commit();
				}
				if (sp.getString("locDistrict", null) == null || sp.getString("locDistrict", null) != locDistrict) {
					sp.edit().putString("locDistrict", locDistrict).commit();
				}
			}
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public String getLoc() {
		return getSharedPreferences("skin", MODE_PRIVATE).getString("loc", "");
	}

	public String getLocDistrict() {
		return getSharedPreferences("skin", MODE_PRIVATE).getString("locDistrict", "");
	}

	ArrayList<Activity> list = new ArrayList<Activity>();

	public void initExce() {
		// 设置该CrashHandler为程序的默认处理器
		UnCeHandler catchExcep = new UnCeHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(catchExcep);
	}

	/**
	 * Activity关闭时，删除Activity列表中的Activity对象
	 */
	public void removeActivity(Activity a) {
		list.remove(a);
	}

	/**
	 * 向Activity列表中添加Activity对象
	 */
	public void addActivity(Activity a) {
		list.add(a);
	}

	/**
	 * 关闭Activity列表中的所有Activity
	 */
	public void finishActivity() {
		for (Activity activity : list) {
			if (null != activity) {
				activity.finish();
			}
		}
		// 杀死该应用进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	public static int getVersionCode(Context context) {
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}
	//add by fcr
}
