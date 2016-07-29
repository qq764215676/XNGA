package ys.oa.activity;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.civil.fragment.SearchComplaintFragment;
import ys.oa.fragment.SearchCaseFragment;
import ys.oa.fragment.SearchFragment;
import ys.oa.fragment.SearchLostFragment;
import ys.oa.fragment.SearchNewsFragment;
import ys.oa.fragment.SearchPeopleFragment;
import ys.oa.util.Util;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import ys.nlga.activity.R;

/**
 * @author wf
 * @category 人、案、车、物搜索页面
 * 
 */
public class SearchActivity extends FragmentActivity implements OnClickListener {

	private ImageButton mBtnBack, mBtnClear, mBtnSearch;
	private EditText mEtSearch;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private View mContentFrame;
	private String searchHint = "请输入关键字", type = "";

	private Fragment mContentFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_search);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	public void initVar() {
		type = getIntent().getStringExtra("type");
		searchHint = type + "查询";

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.pulldown_fade_in;
		options.headerOutAnimation = R.anim.pulldown_fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header_null;
		mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);

		mPullToRefreshAttacher.setRefreshing(false);
		mPullToRefreshAttacher.setEnabled(false);

		if ("新闻".equals(type)) {
			mContentFragment = SearchNewsFragment.newInstance();
		} else if ("案件".equals(type)) {
			mContentFragment = SearchCaseFragment.newInstance();
		} else if ("人员".equals(type)) {
			mContentFragment = SearchPeopleFragment.newInstance();
		} else if ("车辆".equals(type)) {
			mContentFragment = SearchFragment.newInstance();
		} else if ("失物".equals(type)) {
			mContentFragment = SearchLostFragment.newInstance();
		} else if ("诉求".equals(type)) {
			mContentFragment = SearchComplaintFragment.newInstance();
		} else {
			mContentFragment = SearchFragment.newInstance();
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
	}

	public void initWidget() {
		getActionBar().hide();
		mContentFrame = findViewById(R.id.content_frame);
		mContentFrame.setVisibility(View.VISIBLE);
		mBtnBack = (ImageButton) findViewById(R.id.btn_back);
		mBtnClear = (ImageButton) findViewById(R.id.btn_clear_search);
		mBtnSearch = (ImageButton) findViewById(R.id.btn_search);
		mEtSearch = (EditText) findViewById(R.id.et_search_txt);
		mEtSearch.setHint(searchHint);
	}

	public void initListener() {
		mBtnBack.setOnClickListener(this);
		mBtnSearch.setOnClickListener(this);
		mEtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					mBtnClear.setVisibility(View.GONE);
				} else {
					mBtnClear.setVisibility(View.VISIBLE);
				}
			}
		});

		mEtSearch.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int curX = (int) event.getX();
				if (curX > v.getWidth() - 100 && !TextUtils.isEmpty(mEtSearch.getText())) {
					mEtSearch.setText("");
					// mContentFrame.setVisibility(View.GONE);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			onBackPressed();
			break;
		case R.id.btn_search:
			if (!TextUtils.isEmpty(mEtSearch.getText())) {
				// T.showSnack(this, mEtSearch.getText().toString());
				// mContentFrame.setVisibility(View.VISIBLE);
				closeKeyboard();
				if ("新闻".equals(type)) {
					((SearchNewsFragment) mContentFragment).search(mEtSearch.getText().toString());
				} else if ("案件".equals(type)) {
					((SearchCaseFragment) mContentFragment).search(mEtSearch.getText().toString());
				} else if ("人员".equals(type)) {
					((SearchPeopleFragment) mContentFragment).search(mEtSearch.getText().toString());
				} else if ("车辆".equals(type)) {
					((SearchFragment) mContentFragment).search(mEtSearch.getText().toString());
				} else if ("失物".equals(type)) {
					((SearchLostFragment) mContentFragment).search(mEtSearch.getText().toString());
				} else if ("诉求".equals(type)) {
					((SearchComplaintFragment) mContentFragment).search(mEtSearch.getText().toString());
				} else {
					((SearchFragment) mContentFragment).search(mEtSearch.getText().toString());
				}
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

	private void closeKeyboard() {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (isHaveTagShow) {
	// cancelTag();
	// } else {
	// setResult(Activity.RESULT_OK);
	// onBackPressed();
	// }
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
}
