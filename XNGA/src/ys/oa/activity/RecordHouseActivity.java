package ys.oa.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.holoeverywhere.app.Activity;

import ys.oa.adapter.OrderHouseWizardModel;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anim.dialog.DialogLoading;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import ys.nlga.activity.R;
import com.wizardpager.model.AbstractWizardModel;
import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;
import com.wizardpager.ui.PageFragmentCallbacks;
import com.wizardpager.ui.ReviewFragment;
import com.wizardpager.ui.StepPagerStrip;
import com.xutils.entities.RecordHouseEnity;

/**
 * 社区入户访查记录表
 * 
 * @author wf
 * 
 */
public class RecordHouseActivity extends Activity implements PageFragmentCallbacks, ReviewFragment.Callbacks,
		ModelCallbacks {
	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;

	private boolean mEditingAfterReview;

	private AbstractWizardModel mWizardModel = new OrderHouseWizardModel(this);

	private boolean mConsumePageSelectedEvent;

	private Button mNextButton;
	private Button mPrevButton;

	private List<Page> mCurrentPageSequence;
	private StepPagerStrip mStepPagerStrip;

	private AlertDialog loadingDialog;
	private RecordHouseActivity mActivity;
	private RecordHouseEnity enity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		Util.initExce(this);
		mActivity = this;
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState != null) {
			mWizardModel.load(savedInstanceState.getBundle("model"));
		}

		mWizardModel.registerListener(this);

		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(10);
		mPager.setAdapter(mPagerAdapter);
		mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
		mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
			@Override
			public void onPageStripSelected(int position) {
				position = Math.min(mPagerAdapter.getCount() - 1, position);
				if (mPager.getCurrentItem() != position) {
					mPager.setCurrentItem(position);
				}
			}
		});

		mNextButton = (Button) findViewById(R.id.next_button);
		mPrevButton = (Button) findViewById(R.id.prev_button);

		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mStepPagerStrip.setCurrentPage(position);

				if (mConsumePageSelectedEvent) {
					mConsumePageSelectedEvent = false;
					return;
				}

				mEditingAfterReview = false;
				updateBottomBar();
			}
		});

		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
					// loadingDialog =
					// DialogLoading.getProgressDialog(RecordHouseActivity.this,
					// "正在上传");
					// 所有表项
					ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
					for (Page page : mCurrentPageSequence) {
						page.getReviewItems(reviewItems);
					}
					enity = new RecordHouseEnity();
					enity.setReviewItems(reviewItems);
					if (Util.isNetworkAvailable(mActivity)) {
						new AsyncLoader().execute("upload");
					} else {
						T.showSnack(mActivity, R.string.networkerror);
					}
					// try {
					// DbUtils db = DbUtils.create(RecordHouseActivity.this);
					// RecordHouseEnity enity = new RecordHouseEnity();
					// enity.setReviewItems(reviewItems);
					// db.save(enity);
					// } catch (DbException e) {
					// e.printStackTrace();
					// }
					// new Handler().postDelayed(new Runnable() {
					// @Override
					// public void run() {
					// // T.showShort(RecordHouseActivity.this, "上传成功");
					// loadingDialog.cancel();
					// setResult(RESULT_OK);
					// onBackPressed();
					// }
					// }, 3000);
				} else {
					if (mEditingAfterReview) {
						mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
					} else {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1);
					}
				}
			}
		});

		mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		});

		onPageTreeChanged();
		updateBottomBar();
	}

	private DataSetList datasetlist;

	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			if ("upload".equals(params[0])) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("houseCommunity", enity.getHouseCommunity());
				map.put("houseAddress", enity.getHouseAddress());
				map.put("ownerName", enity.getHouseOwner());
				map.put("ownerPhone", enity.getHouseOwnerPhone());
				map.put("houseKeeper", enity.getHouseKeeper());
				map.put("houseKeeperPhone", enity.getHouseKeeperPhone());
				map.put("checkTime", enity.getCheckTime());
				map.put("checker", enity.getChecker());
				map.put("houseCheck1", enity.getHouseCheck1());
				map.put("houseCheck2", enity.getHouseCheck2());
				map.put("houseCheck3", enity.getHouseCheck3());
				map.put("houseCheck4", enity.getHouseCheck4());
				map.put("houseCheck5", enity.getHouseCheck5());
				map.put("houseCheck6", enity.getHouseCheck6());
				map.put("changeOpinion", enity.getChangeOpinion());
				map.put("ownerOpinion", enity.getOwnerOpinion());
				map.put("username", Constants.USERID);
				map.put("updateTime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForSaveOrUpdate(map, new DocInfor("",
							"NLGA_RECORD_HOUSE"), false));
				} catch (Exception e) {
					return -1;
				}
				return 1;
			}
			return 0;
		}

		protected void onPreExecute() {
			loadingDialog = DialogLoading.getProgressDialog(mActivity, "正在上传");
		}

		protected void onPostExecute(Integer result) {
			loadingDialog.cancel();
			switch (result) {
			case -1:// 异步NullPointerException
				T.showSnack(mActivity, R.string.serverFailed);
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					if (Util.isNetworkAvailable(mActivity)) {
						setResult(RESULT_OK);
						onBackPressed();
						// T.showSnack(mActivity, "上传完成");
					} else {
						T.showSnack(mActivity, R.string.networkerror);
					}
				} else {
					T.showSnack(mActivity, "文件上传失败");
				}
				break;
			}
		}
	}

	@Override
	public void onPageTreeChanged() {
		mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
		recalculateCutOffPage();
		mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 =
																		// review
																		// step
		mPagerAdapter.notifyDataSetChanged();
		updateBottomBar();
	}

	private void updateBottomBar() {
		int position = mPager.getCurrentItem();
		if (position == mCurrentPageSequence.size()) {
			mNextButton.setText(R.string.finish);
			mNextButton.setBackgroundResource(R.drawable.finish_background);
			mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
		} else {
			mNextButton.setText(mEditingAfterReview ? R.string.review : R.string.next);
			mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
			TypedValue v = new TypedValue();
			getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
			mNextButton.setTextAppearance(this, v.resourceId);
			mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
		}

		mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWizardModel.unregisterListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBundle("model", mWizardModel.save());
	}

	@Override
	public AbstractWizardModel onGetModel() {
		return mWizardModel;
	}

	@Override
	public void onEditScreenAfterReview(String key) {
		for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
			if (mCurrentPageSequence.get(i).getKey().equals(key)) {
				mConsumePageSelectedEvent = true;
				mEditingAfterReview = true;
				mPager.setCurrentItem(i);
				updateBottomBar();
				break;
			}
		}
	}

	@Override
	public void onPageDataChanged(Page page) {
		if (page.isRequired()) {
			if (recalculateCutOffPage()) {
				mPagerAdapter.notifyDataSetChanged();
				updateBottomBar();
			}
		}
	}

	@Override
	public Page onGetPage(String key) {
		return mWizardModel.findByKey(key);
	}

	private boolean recalculateCutOffPage() {
		// Cut off the pager adapter at first required page that isn't completed
		int cutOffPage = mCurrentPageSequence.size() + 1;
		for (int i = 0; i < mCurrentPageSequence.size(); i++) {
			Page page = mCurrentPageSequence.get(i);
			if (page.isRequired() && !page.isCompleted()) {
				cutOffPage = i;
				break;
			}
		}

		if (mPagerAdapter.getCutOffPage() != cutOffPage) {
			mPagerAdapter.setCutOffPage(cutOffPage);
			return true;
		}

		return false;
	}

	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		private int mCutOffPage;
		private Fragment mPrimaryItem;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			if (i >= mCurrentPageSequence.size()) {
				return new ReviewFragment();
			}

			return mCurrentPageSequence.get(i).createFragment();
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO: be smarter about this
			if (object == mPrimaryItem) {
				// Re-use the current fragment (its position never changes)
				return POSITION_UNCHANGED;
			}

			return POSITION_NONE;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			mPrimaryItem = (Fragment) object;
		}

		@Override
		public int getCount() {
			if (mCurrentPageSequence == null) {
				return 0;
			}
			return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
		}

		public void setCutOffPage(int cutOffPage) {
			if (cutOffPage < 0) {
				cutOffPage = Integer.MAX_VALUE;
			}
			mCutOffPage = cutOffPage;
		}

		public int getCutOffPage() {
			return mCutOffPage;
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
}
