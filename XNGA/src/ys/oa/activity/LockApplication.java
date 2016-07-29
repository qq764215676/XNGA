package ys.oa.activity;

import java.util.ArrayList;
import java.util.List;

import com.refactech.driibo.AppData;

import android.app.Activity;
import android.app.Application;
import ys.oa.listener.ScreenListener;

public class LockApplication extends AppData {

	public static LockApplication instance = null;
	/** 程序活动列表 */
	private List<Activity> activityList = new ArrayList<Activity>();

	public static LockApplication getInstance() {
		if (instance == null) {
			instance = new LockApplication();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	/** 程序在前台 */
	public static boolean isActive = false;
	/** 程序从后台到前台 */
	public static boolean isBtoF = false;
	/** 正在显示lock界面 */
	public static boolean isShowLock = false;
	/** 不显示显示lock界面【splash界面和login界面】 */
	public static boolean notShowLock = false;
	/** 显示亮屏的次数 */
	public static int ligthCount = 0;
	/** main界面resume次数 */
	public static int mainCount = 0;
	/** 屏幕锁屏监听 */
	public static ScreenListener sl;

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		sl.unregisterListener();
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	public void finishAll() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
	}

	@SuppressWarnings("rawtypes")
	public void finishOthers(Class clazz) {
		for (int i = 0; i < activityList.size(); i++) {
			if (!clazz.getName().equals(activityList.get(i).getClass().getName())) {
				activityList.get(i).finish();
				activityList.remove(i);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void toFinish(Class clazz) {
		for (int i = 0; i < activityList.size(); i++) {
			if (clazz.getName().equals(activityList.get(i).getClass().getName())) {
				activityList.get(i).finish();
				activityList.remove(i);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean hasClazz(Class clazz) {
		boolean flag = false;
		for (int i = 0; i < activityList.size(); i++) {
			if (clazz.getName().equals(activityList.get(i).getClass().getName())) {
				flag = true;
			}
		}
		return flag;
	}

}
