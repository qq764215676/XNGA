package com.devspark.msg;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.TOP;
import static com.devspark.appmsg.AppMsg.LENGTH_STICKY;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;

import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import ys.nlga.activity.R;

/**
 * 自定义Toast消息
 */
@SuppressLint("NewApi")
public class ToastMsg {
	/**
	 * 底部阴影无动画msg
	 */
	public static void showMsg(Activity activity, CharSequence msg) {
		AppMsg appMsg = AppMsg.makeText(activity, msg, new AppMsg.Style(1500, R.color.bg_msg_info));
		appMsg.setPriority(AppMsg.PRIORITY_NORMAL);
		appMsg.setLayoutGravity(BOTTOM);
		appMsg.setDuration(1500);
		appMsg.show();
	}

	/**
	 * 顶部侧滑msg
	 */
	public static void showMsgTop(Activity activity, CharSequence msg) {
		AppMsg appMsg = AppMsg.makeText(activity, msg, new AppMsg.Style(1500, R.color.bg_msg_blue));
		appMsg.setPriority(AppMsg.PRIORITY_NORMAL);
		appMsg.setLayoutGravity(TOP);
		appMsg.setAnimation(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		appMsg.show();
	}

	/**
	 * 手动关闭msg
	 */
	public static void showSticky(Activity activity, CharSequence msg) {
		Style style = new AppMsg.Style(LENGTH_STICKY, R.color.bg_msg_blue);
		AppMsg appMsg = AppMsg.makeText(activity, msg, style, R.layout.sticky);
		appMsg.getView().findViewById(R.id.remove_btn).setOnClickListener(new CancelAppMsg(appMsg));
		appMsg.setPriority(AppMsg.PRIORITY_NORMAL);
		appMsg.setLayoutGravity(TOP);
		appMsg.show();
	}

	static class CancelAppMsg implements View.OnClickListener {
		private final AppMsg mAppMsg;

		CancelAppMsg(AppMsg appMsg) {
			mAppMsg = appMsg;
		}

		@Override
		public void onClick(View v) {
			mAppMsg.cancel();
		}
	}

	public static void cancelAll(Activity activity) {
		AppMsg.cancelAll(activity);
	}

}
