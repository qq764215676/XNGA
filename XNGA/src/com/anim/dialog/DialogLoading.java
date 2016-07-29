package com.anim.dialog;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ys.nlga.activity.R;

/**
 * @author wufan
 * @category loading对话框
 * 
 */
public class DialogLoading {

	/**
	 * loading对话框
	 */
	public static AlertDialog getProgressDialog(Activity activity, String loadText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null);
		((TextView) view.findViewById(R.id.tv_load_text)).setText(loadText + "....");
		View img1 = view.findViewById(R.id.pd_circle1);
		View img2 = view.findViewById(R.id.pd_circle2);
		View img3 = view.findViewById(R.id.pd_circle3);
		int ANIMATION_DURATION = 500;
		Animator anim1 = setRepeatableAnim(activity, img1, ANIMATION_DURATION, R.animator.growndisappear);
		Animator anim2 = setRepeatableAnim(activity, img2, ANIMATION_DURATION, R.animator.growndisappear);
		Animator anim3 = setRepeatableAnim(activity, img3, ANIMATION_DURATION, R.animator.growndisappear);
		setListeners(img1, anim1, anim2, ANIMATION_DURATION);
		setListeners(img2, anim2, anim3, ANIMATION_DURATION);
		setListeners(img3, anim3, anim1, ANIMATION_DURATION);
		anim1.start();
		builder.setView(view);
		AlertDialog ad = builder.create();
		ad.setCanceledOnTouchOutside(false);
		ad.setCancelable(false);
		ad.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		ad.show();
		ad.getWindow().setLayout(dpToPx(200, activity), dpToPx(125, activity));
		return ad;
	}

	/**
	 * Convert dp to px
	 * 
	 * @author Sachin
	 * @param i
	 * @param mContext
	 * @return
	 */
	public static int dpToPx(int i, Context mContext) {

		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		return (int) ((i * displayMetrics.density) + 0.5);

	}

	public static Animator setRepeatableAnim(Activity activity, View target, final int duration, int animRes) {
		final Animator anim = AnimatorInflater.loadAnimator(activity, animRes);
		anim.setDuration(duration);
		anim.setTarget(target);
		return anim;
	}

	public static void setListeners(final View target, Animator anim, final Animator animator, final int duration) {
		anim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animat) {
				if (target.getVisibility() == View.INVISIBLE) {
					target.setVisibility(View.VISIBLE);
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						animator.start();
					}
				}, duration - 100);
			}

			@Override
			public void onAnimationEnd(Animator animator) {

			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
	}

}
