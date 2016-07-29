package ys.oa.activity;

import ys.nlga.activity.R;

import org.holoeverywhere.app.AlertDialog;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.refactech.driibo.AppData;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.SyncStateContract.Constants;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.widget.GestureLockView;
import ys.oa.widget.GestureLockView.OnGestureFinishListener;

public class GestureLockActivity extends BaseActivity {
	

	@ViewInject(R.id.gesture_tv_tips)
	private TextView gesture_tv_tips;
	@ViewInject(R.id.gestureLockView)
	private GestureLockView gestureLockView;
	@ViewInject(R.id.gesture_tv_forget)
	private TextView gesture_tv_forget;
	@ViewInject(R.id.gesture_btn_reset)
	private TextView gesture_btn_reset;

	private Animation animation;
	private boolean flag = false;
	private String key;
	private String keystore;
	private int count = 0;
	private MyCount counttime;
	private SpUtil mSpUtil;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_gesture_lock);
		LockApplication.getInstance().addActivity(this);
		ViewUtils.inject(this);
		LockApplication.getInstance().addActivity(this);
		mSpUtil = AppData.getInstance().getSpUtil();

		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		gestureLockView.resetErrorTime();
		stopCountTime();
		LockApplication.isShowLock = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		LockApplication.isShowLock = true;
	}

	private void init() {
		animation = new TranslateAnimation(-20, 20, 0, 0);
		animation.setDuration(50);
		animation.setRepeatCount(2);
		animation.setRepeatMode(Animation.REVERSE);

		key = mSpUtil.getString(mSpUtil.getString("Gesture",null) + "_Lockkey", "");
		if (!isEmpty(key)) {
			flag = true;
			// 设置密码
			gestureLockView.setKey(key);
			startCountTime(countTimeBollean());
		} else {
			gesture_tv_tips.setTextColor(Color.parseColor("#FFFFFF"));
			gesture_tv_tips.setVisibility(View.VISIBLE);
			gesture_tv_tips.setText("请先设置应用解锁密码\n[至少4位]");
			gesture_tv_forget.setVisibility(View.GONE);
			gestureLockView.setLockMode(true);
		}
		// 手势完成后回调
		gestureLockView.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void OnGestureFinish(boolean success, String key) {
				if (flag) {
					if (success) {
						LockApplication.getInstance().toFinish(GestureLockActivity.class);
						finish();
					} else {
						gestureLockView.minusErrorTime();
						if (gestureLockView.getLockErrorTime() > 0) {
							gesture_tv_tips.setTextColor(Color.parseColor("#FF2525"));
							gesture_tv_tips.setVisibility(View.VISIBLE);
							gesture_tv_tips.setText("密码错误\n还可以再解锁" + gestureLockView.getLockErrorTime() + "次");
							gesture_tv_tips.startAnimation(animation);
						} else {
							setLockTime(System.currentTimeMillis());
							startCountTime((long) 3600000);
						}
					}
				} else {
					if (key.length() > 3) {
						count++;
						if (count == 1) {
							keystore = key;
							gesture_tv_tips.setTextColor(Color.parseColor("#FFFFFF"));
							gesture_tv_tips.setText("请再次设置解锁密码");
							gesture_btn_reset.setText(Html.fromHtml("<u>" + "重新设置" + "</u>"));
							gesture_btn_reset.setVisibility(View.VISIBLE);

						} else if (count == 2) {
							if (keystore.equals(key)) {
								showToast("解锁密码设置成功");
								// 此处密码请自行加密
								mSpUtil.putString(mSpUtil.getString("Gesture",null) + "_Lockkey", keystore);
								LockApplication.getInstance().toFinish(GestureLockActivity.class);
								finish();
							} else {
								count = 0;
								gesture_tv_tips.setText("请先设置应用解锁密码\n[至少4位]");
								gesture_tv_tips.startAnimation(animation);
								showToast("两次密码设置不一致，请重新设置");
								gesture_btn_reset.setVisibility(View.GONE);
							}
						}
					} else {
						if (count == 1) {
							count = 0;
							gesture_tv_tips.setText("请先设置应用解锁密码\n[至少4位]");
							gesture_tv_tips.startAnimation(animation);
							showToast("两次密码设置不一致，请重新设置");
							gesture_btn_reset.setVisibility(View.GONE);
						} else {
							count = 0;
							gesture_tv_tips.setText("请先设置应用解锁密码\n[至少4位]");
							gesture_tv_tips.startAnimation(animation);
							showToast("解锁密码太短，请重新设置");
						}
					}
				}
			}
		});
		gesture_tv_forget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				
				LayoutInflater inflater = LayoutInflater.from(GestureLockActivity.this);
                final View dialogView = inflater.inflate(R.layout.dialog_change_pwd, null);  
                //final EditText edit=(EditText)dialogView.findViewById(R.id.et_pwd);
                
                new AlertDialog.Builder(GestureLockActivity.this)  
                .setTitle("登录密码确认")//提示框标题  
                .setView(dialogView)  
                .setCancelable(false)//禁对话框以外点击及返回键
                .setPositiveButton("确定",//提示框的两个按钮  
                        new android.content.DialogInterface.OnClickListener() {
                            @Override  
                            public void onClick(DialogInterface dialog,  
                                    int which) {
                            	EditText et=(EditText)dialogView.findViewById(R.id.et_pwd);
                            	System.out.println(" "+et.getText().toString());
                            	System.out.println("pwd "+ys.oa.util.Constants.USER_PWD);
                            	//密码不正确则进行错误提示
                            	if(!et.getText().toString().equals(ys.oa.util.Constants.USER_PWD)){
                            		T.showShort(GestureLockActivity.this, "密码不正确！");
                            	}else{
                            		T.showShort(GestureLockActivity.this, "请重置手势密码！");
                            		//将进入onResume次数清零
                        			LockApplication.mainCount = 0;
                        			//清空锁屏密码
                        			mSpUtil.putString(mSpUtil.getString("Gesture",null) + "_Lockkey", "");
                        			//设置初始化，状态从登录页面进入
                        			mSpUtil.getBoolean("Flogin", true);
                        			startActivity(new Intent(GestureLockActivity.this, MainActivity.class));
                        			GestureLockActivity.this.finish();
                            	}
                            }  
                        }).setNegativeButton("取消", null).create().show();
				
				//Intent intent = new Intent(GestureLockActivity.this, LoginActivity.class);
				//startActivity(intent);
				//finish();
			}
		});
		gesture_btn_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				keystore = null;
				count = 0;
				gesture_tv_tips.setText("请先设置应用解锁密码\n[至少4位]");
				gesture_tv_tips.startAnimation(animation);
				gesture_btn_reset.setVisibility(View.GONE);
			}
		});
	}
	
	 public boolean isEmpty(CharSequence str) {
	        return (str == null || str.length() == 0);
	    }

	/**
	 * 开始计时工作
	 */
	private void startCountTime(Long count) {
		if (count != 0) {
			gestureLockView.setUnlock(false);
			gesture_tv_tips.setTextColor(Color.parseColor("#ED6C00"));
			gesture_tv_tips.setVisibility(View.VISIBLE);
			counttime = new MyCount(count, 1000);
			counttime.start();
		}
	}

	/**
	 * 停止计时
	 */
	private void stopCountTime() {
		if (counttime != null) {
			counttime.cancel();
			counttime = null;
		}
	}

	/**
	 * 
	 * @Title: countTimeBollean
	 * @Description: TODO(计算解锁时间)
	 * @param @return 设定文件
	 * @return long 返回类型
	 * @throws
	 */
	private long countTimeBollean() {
		long cnt = mSpUtil.getLong(mSpUtil.getString("UserUid",null) + "_DataNum", 0);
		if (cnt == 0) {
			return 0;
		} else {
			long spass = System.currentTimeMillis() - cnt;
			if (spass < 0 || spass > 3600000) {
				return 0;
			} else {
				return (3600000 - spass);
			}
		}
	}

	private class MyCount extends CountDownTimer {

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			gestureLockView.setUnlock(true);
			gestureLockView.resetErrorTime();
			gesture_tv_tips.setTextColor(Color.parseColor("#FFFFFF"));
			gesture_tv_tips.setText("请解锁应用");
			setLockTime(0);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long minutes = millisUntilFinished / (60 * 1000);
			long seconds = (millisUntilFinished % (60 * 1000)) / 1000;
			if (minutes > 0) {
				if (seconds > 0) {
					gesture_tv_tips.setText("请" + minutes + "分" + seconds + "秒后尝试解锁");
				} else {
					gesture_tv_tips.setText("请" + minutes + "分后尝试解锁");
				}
			} else {
				gesture_tv_tips.setText("请" + seconds + "秒后尝试解锁");
			}
		}
	}

	private void setLockTime(long time) {
		mSpUtil.putLong(mSpUtil.getString("Gesture",null) + "_DataNum", time);
	}

	private void back() {
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed() {
		back();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
