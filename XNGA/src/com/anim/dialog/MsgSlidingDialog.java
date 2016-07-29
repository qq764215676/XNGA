package com.anim.dialog;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ys.nlga.activity.R;

/**
 * 带标志消息对话框
 */
public class MsgSlidingDialog extends Dialog implements View.OnClickListener {
	public static final int NORMAL_TYPE = 0;
	public static final int ERROR_TYPE = 1;
	public static final int SUCCESS_TYPE = 2;
	public static final int WARNING_TYPE = 3;
	public static final int CUSTOM_IMAGE_TYPE = 4;
	public static final int PROGRESS_TYPE = 5;
	private View mDialogView;
	private AnimationSet mModalInAnim;
	private AnimationSet mModalOutAnim;
	private Animation mOverlayOutAnim;
	private Animation mErrorInAnim;
	private AnimationSet mErrorXInAnim;
	private AnimationSet mSuccessLayoutAnimSet;
	private Animation mSuccessBowAnim;
	private TextView mTitleTextView;
	private float mTitleTextSize;
	private TextView mContentTextView;
	private String mTitleText;
	private String mContentText;
	private boolean mShowContent;
	private int mAlertType;
	private FrameLayout mErrorFrame;
	private FrameLayout mSuccessFrame;
	private FrameLayout mProgressFrame;
	private SuccessTickView mSuccessTick;
	private ImageView mErrorX;
	private View mSuccessLeftMask;
	private View mSuccessRightMask;
	private Drawable mCustomImgDrawable;
	private ImageView mCustomImage;
	private ProgressHelper mProgressHelper;
	private FrameLayout mWarningFrame;
	private boolean mShowCancel = false;
	private boolean mShowConfrim = false;
	private String mCancelText;
	private String mConfirmText;
	private Button mConfirmButton;
	private Button mCancelButton;
	private View mCancelButtonRight, mButtonTop;
	private OnMsgClickListener mCancelClickListener;
	private OnMsgClickListener mConfirmClickListener;

	public static interface OnMsgClickListener {
		public void onClick(MsgSlidingDialog msgSlidingDialog);
	}

	public MsgSlidingDialog(Context context, int type) {
		super(context, R.style.anim_dialog);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		mProgressHelper = new ProgressHelper(context);
		mAlertType = type;
		mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.error_frame_in);
		mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.error_x_in);
		// 2.3.x system don't support alpha-animation on layer-list drawable
		// remove it from animation set
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			List<Animation> childAnims = mErrorXInAnim.getAnimations();
			int idx = 0;
			for (; idx < childAnims.size(); idx++) {
				if (childAnims.get(idx) instanceof AlphaAnimation) {
					break;
				}
			}
			if (idx < childAnims.size()) {
				childAnims.remove(idx);
			}
		}
		mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.success_bow_roate);
		mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(),
				R.anim.success_mask_layout);
		mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.dialog_slide_in);
		mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.dialog_slide_out);
		mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mDialogView.setVisibility(View.GONE);
				mDialogView.post(new Runnable() {
					@Override
					public void run() {
						MsgSlidingDialog.super.cancel();
					}
				});
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		// dialog overlay fade out
		mOverlayOutAnim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				WindowManager.LayoutParams wlp = getWindow().getAttributes();
				wlp.alpha = 1 - interpolatedTime;
				getWindow().setAttributes(wlp);
			}
		};
		mOverlayOutAnim.setDuration(120);

		// 设置对话框坐标
		LayoutParams params = new LayoutParams();
		params.y = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight() / 2;
		getWindow().setAttributes(params);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sliding_msg);
		mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
		mCustomImage = (ImageView) findViewById(R.id.custom_image);
		mWarningFrame = (FrameLayout) findViewById(R.id.warning_frame);

		mProgressFrame = (FrameLayout) findViewById(R.id.progress_dialog);
		mProgressHelper.setProgressWheel((ProgressWheel) findViewById(R.id.progressWheel));

		mErrorFrame = (FrameLayout) findViewById(R.id.error_frame);
		mErrorX = (ImageView) mErrorFrame.findViewById(R.id.error_x);

		mSuccessFrame = (FrameLayout) findViewById(R.id.success_frame);
		mSuccessTick = (SuccessTickView) mSuccessFrame.findViewById(R.id.success_tick);
		mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
		mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);

		mTitleTextView = (TextView) findViewById(R.id.title_text);
		mContentTextView = (TextView) findViewById(R.id.content_text);
		mContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

		mConfirmButton = (Button) findViewById(R.id.confirm_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);
		mCancelButtonRight = findViewById(R.id.cancel_button_right);
		mButtonTop = findViewById(R.id.button_top);
		mConfirmButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		setCancelText(mCancelText);
		setConfirmText(mConfirmText);
		setTitleText(mTitleText);
		setTitleTextSize(mTitleTextSize);
		setContentText(mContentText);
		changeAlertType(mAlertType, true);

		if (!mShowCancel && !mShowConfrim) {
			findViewById(R.id.loading).setPadding(0, 20, 0, 20);
		} else {
			findViewById(R.id.loading).setPadding(0, 20, 0, 0);
		}
	}

	protected void onStart() {
		mDialogView.startAnimation(mModalInAnim);
		playAnimation();
	}

	private void restore() {
		mCustomImage.setVisibility(View.GONE);
		mErrorFrame.setVisibility(View.GONE);
		mSuccessFrame.setVisibility(View.GONE);
		mWarningFrame.setVisibility(View.GONE);
		mProgressFrame.setVisibility(View.GONE);
		// mConfirmButton.setVisibility(View.VISIBLE);
		// if (mConfirmButton != null) {
		// mConfirmButton.setVisibility(mShowConfrim ? View.VISIBLE :
		// View.GONE);
		// }
		// if (mButtonTop != null) {
		// mButtonTop.setVisibility(mShowConfrim ? View.VISIBLE : View.GONE);
		// }

		// mConfirmButton.setBackgroundResource(R.drawable.blue_button_background);
		mErrorFrame.clearAnimation();
		mErrorX.clearAnimation();
		mSuccessTick.clearAnimation();
		mSuccessLeftMask.clearAnimation();
		mSuccessRightMask.clearAnimation();
	}

	private void playAnimation() {
		if (mAlertType == ERROR_TYPE) {
			mErrorFrame.startAnimation(mErrorInAnim);
			mErrorX.startAnimation(mErrorXInAnim);
		} else if (mAlertType == SUCCESS_TYPE) {
			mSuccessTick.startTickAnim();
			mSuccessRightMask.startAnimation(mSuccessBowAnim);
		}
	}

	private void changeAlertType(int alertType, boolean fromCreate) {
		mAlertType = alertType;
		// call after created views
		if (mDialogView != null) {
			if (!fromCreate) {
				// restore all of views state before switching alert type
				restore();
			}
			switch (mAlertType) {
			case ERROR_TYPE:
				mErrorFrame.setVisibility(View.VISIBLE);
				break;
			case SUCCESS_TYPE:
				mSuccessFrame.setVisibility(View.VISIBLE);
				// initial rotate layout of success mask
				mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
				mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
				break;
			case WARNING_TYPE:
				// mConfirmButton.setBackgroundResource(R.drawable.red_button_background);
				mWarningFrame.setVisibility(View.VISIBLE);
				break;
			case CUSTOM_IMAGE_TYPE:
				setCustomImage(mCustomImgDrawable);
				break;
			case PROGRESS_TYPE:
				mProgressFrame.setVisibility(View.VISIBLE);
				// mConfirmButton.setVisibility(View.GONE);
				break;
			}
			if (!fromCreate) {
				playAnimation();
			}
		}
	}

	public int getAlerType() {
		return mAlertType;
	}

	public void changeAlertType(int alertType) {
		changeAlertType(alertType, false);
	}

	public MsgSlidingDialog setTitleTextSize(float size) {
		mTitleTextSize = size;
		if (mTitleTextView != null) {
			mTitleTextView.setTextSize(size);
		}
		return this;
	}

	public String getTitleText() {
		return mTitleText;
	}

	public MsgSlidingDialog setTitleText(String text) {
		mTitleText = text;
		if (mTitleTextView != null && mTitleText != null) {
			mTitleTextView.setText(mTitleText);
		}
		return this;
	}

	public MsgSlidingDialog setCustomImage(Drawable drawable) {
		mCustomImgDrawable = drawable;
		if (mCustomImage != null && mCustomImgDrawable != null) {
			mCustomImage.setVisibility(View.VISIBLE);
			mCustomImage.setImageDrawable(mCustomImgDrawable);
		}
		return this;
	}

	public MsgSlidingDialog setCustomImage(int resourceId) {
		return setCustomImage(getContext().getResources().getDrawable(resourceId));
	}

	public String getContentText() {
		return mContentText;
	}

	public MsgSlidingDialog setContentText(String text) {
		mContentText = text;
		if (mContentTextView != null && mContentText != null) {
			showContentText(true);
			mContentTextView.setText(mContentText);
		}
		return this;
	}

	@Override
	public void cancel() {
		mDialogView.startAnimation(mModalOutAnim);
	}

	public ProgressHelper getProgressHelper() {
		return mProgressHelper;
	}

	public boolean isShowCancelButton() {
		return mShowCancel;
	}

	public MsgSlidingDialog showCancelButton(boolean isShow) {
		mShowCancel = isShow;
		if (mCancelButton != null) {
			mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
			mCancelButtonRight.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
		}
		return this;
	}

	public MsgSlidingDialog showConfrimButton(boolean isShow) {
		mShowConfrim = isShow;
		if (mConfirmButton != null) {
			mConfirmButton.setVisibility(mShowConfrim ? View.VISIBLE : View.GONE);
		}
		if (mButtonTop != null) {
			mButtonTop.setVisibility(mShowConfrim ? View.VISIBLE : View.GONE);
		}
		return this;
	}

	public boolean isShowContentText() {
		return mShowContent;
	}

	public MsgSlidingDialog showContentText(boolean isShow) {
		mShowContent = isShow;
		if (mContentTextView != null) {
			mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
		}
		return this;
	}

	public String getCancelText() {
		return mCancelText;
	}

	public MsgSlidingDialog setCancelText(String text) {
		mCancelText = text;
		if (mCancelButton != null && mCancelText != null) {
			showCancelButton(true);
			mCancelButton.setText(mCancelText);
		}
		return this;
	}

	public String getConfirmText() {
		return mConfirmText;
	}

	public MsgSlidingDialog setConfirmText(String text) {
		mConfirmText = text;
		if (mConfirmButton != null && mConfirmText != null) {
			showConfrimButton(true);
			mConfirmButton.setText(mConfirmText);
		}
		return this;
	}

	public MsgSlidingDialog setCancelClickListener(OnMsgClickListener listener) {
		mCancelClickListener = listener;
		return this;
	}

	public MsgSlidingDialog setConfirmClickListener(OnMsgClickListener listener) {
		mConfirmClickListener = listener;
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cancel_button) {
			if (mCancelClickListener != null) {
				mCancelClickListener.onClick(MsgSlidingDialog.this);
			} else {
				cancel();
			}
		} else if (v.getId() == R.id.confirm_button) {
			if (mConfirmClickListener != null) {
				mConfirmClickListener.onClick(MsgSlidingDialog.this);
			} else {
				cancel();
			}
		}
	}

}