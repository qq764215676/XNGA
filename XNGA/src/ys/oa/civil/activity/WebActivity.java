package ys.oa.civil.activity;

import java.lang.reflect.Field;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import ys.oa.util.Util;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import ys.nlga.activity.R;

/**
 * @author wufan
 * @category webview
 * 
 */
@SuppressLint("NewApi")
public class WebActivity extends SwipeBackActivity implements OnClickListener {
	private ImageButton mBtnBack;
	private TextView mTvTitle;
	private View mRlTitle;
	// private Intent intent;

	private ProgressBar mPbLoading;
	private WebView mWebView;
	private WebSettings mWebSettings;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web);
		Util.initExce(this);

		initVar();
		initWidget();
		initListener();

		initWebView();
	}

	public void initVar() {
	}

	public void initWidget() {
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		mTvTitle.setText(getIntent().getStringExtra("Title"));
		mBtnBack = (ImageButton) findViewById(R.id.ib_back);
		mRlTitle = findViewById(R.id.rl_titlebar);
		ActionBar actionBar = getActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setTitle(getIntent().getStringExtra("Title"));
		actionBar.hide();
	}

	public void initListener() {
		mBtnBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_back:
			if (mWebView.canGoBack())
				mWebView.goBack();
			else
				onBackPressed();
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				onBackPressed();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public int dpToPx(int dp) {
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return px;
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void initWebView() {
		mWebView = (WebView) findViewById(R.id.webView);
		mWebSettings = mWebView.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mPbLoading = (ProgressBar) findViewById(R.id.progressBar);
		// mPbLoading.setSmoothProgressDrawableInterpolator(new
		// AccelerateDecelerateInterpolator());
		mWebView = (WebView) findViewById(R.id.webView);
		// 防止因硬件加速引起的闪烁
		// mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mWebSettings = mWebView.getSettings();
		mWebSettings.setAllowFileAccess(true);
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebSettings.setJavaScriptEnabled(true);
		// 缩放
		mWebSettings.setSupportZoom(true);
		mWebSettings.setBuiltInZoomControls(true);
		// 大页面自动适应屏幕
		if (getIntent().getBooleanExtra("FullScreen", true))// 显示网页加载进度
		{
			mWebSettings.setUseWideViewPort(true);
			mWebSettings.setLoadWithOverviewMode(true);
		}

		// http://mobile.8684.cn/bus
		// http://mobile.8684.cn/train
		mWebView.requestFocus();// 请求焦点
		mWebView.loadUrl(getIntent().getStringExtra("url")); // 加载网址
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		// 显示网页加载进度
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					mPbLoading.setVisibility(View.GONE);
					mRlTitle.setBackgroundResource(R.drawable.header_bar);
				} else {
					mPbLoading.setVisibility(View.VISIBLE);
					mRlTitle.setBackgroundResource(0);
				}
				// mPbLoading.setProgress(newProgress);
				mPbLoading.setSecondaryProgress(newProgress);
				// if (newProgress == 100) {
				// new Handler().postDelayed(new Runnable() {
				// @Override
				// public void run() {
				// mPbLoading.progressiveStop();
				// // mPbLoading.setVisibility(View.GONE);
				// }
				// }, 900);
				// } else {
				// if (mPbLoading.getVisibility() == View.GONE)
				// mPbLoading.setVisibility(View.VISIBLE);
				// // mPbLoading.setProgress(newProgress);
				// }
			}
		});

	}

	public void setZoomControlGone(View view) {
		Class<?> classType;
		Field field;
		try {
			classType = WebView.class;
			field = classType.getDeclaredField("mZoomButtonsController");
			field.setAccessible(true);
			ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
			mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
			try {
				field.set(view, mZoomButtonsController);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();
		mWebView.pauseTimers();
	}

	@Override
	public void onResume() {
		super.onResume();
		mWebView.resumeTimers();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mWebView.pauseTimers();
		mWebView.stopLoading();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.setVisibility(View.GONE);
	}
}