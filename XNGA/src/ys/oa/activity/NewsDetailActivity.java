package ys.oa.activity;

import java.util.ArrayList;

import ys.oa.enity.NewsEnity;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anim.dialog.DialogLoading;
import com.byaku.gallery.TouchImageActivity;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import ys.nlga.activity.R;

public class NewsDetailActivity extends Activity {

	// 载入中...
	private View mLoading, mLlLoading;

	private TextView mTvTitle, mTvTime, mTvContent;
	private ImageView mIvBanner;
	private Context context;
	private String TAG = "NewsDetailActivity";
	private NewsEnity newsEnity;
	private String mTabName = "NLGA_NEWS_DETAIL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
		initLoading();
		if (Util.isNetworkAvailable(this)) {
			new AsyncLoader().execute();
		} else {
			T.showSnack(this, R.string.networkerror);
		}
	}

	public void initVar() {
		newsEnity = (NewsEnity) getIntent().getSerializableExtra("newsEnity");
		mTabName = getIntent().getStringExtra("TabName") + "_DETAIL";
		if ("NLGA_NEWS_CIVIL_DETAIL".equals(mTabName)) {
			mTabName = "NLGA_NEWS_DETAIL_CIVIL";
		}
		context = this;
	}

	public void initWidget() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mTvTitle = (TextView) findViewById(R.id.tvNewsTitle);
		mTvTime = (TextView) findViewById(R.id.tvTime);
		mTvContent = (TextView) findViewById(R.id.tvNewsContent);
		mIvBanner = (ImageView) findViewById(R.id.ivNewsBanner);
	}

	public void initListener() {
		mIvBanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String imgUrl = Util.saveImageView(mIvBanner, "" + System.currentTimeMillis());
				if (imgUrl != null) {
					startActivity(new Intent(NewsDetailActivity.this, TouchImageActivity.class).putExtra("imgUrl",
							imgUrl));
				}
			}
		});
	}

	private Animator anim1, anim2, anim3;

	private void initLoading() {
		mLoading = findViewById(R.id.fl_loading);
		mLlLoading = findViewById(R.id.ll_loading);
		View img1 = findViewById(R.id.pd_circle1);
		View img2 = findViewById(R.id.pd_circle2);
		View img3 = findViewById(R.id.pd_circle3);
		int ANIMATION_DURATION = 400;
		anim1 = DialogLoading.setRepeatableAnim(this, img1, ANIMATION_DURATION, R.animator.growndisappear);
		anim2 = DialogLoading.setRepeatableAnim(this, img2, ANIMATION_DURATION, R.animator.growndisappear);
		anim3 = DialogLoading.setRepeatableAnim(this, img3, ANIMATION_DURATION, R.animator.growndisappear);
		DialogLoading.setListeners(img1, anim1, anim2, ANIMATION_DURATION);
		DialogLoading.setListeners(img2, anim2, anim3, ANIMATION_DURATION);
		DialogLoading.setListeners(img3, anim3, anim1, ANIMATION_DURATION);
	}

	private void startLoading() {
		anim1.start();
		mLoading.setVisibility(View.VISIBLE);
	}

	private void endLoading() {
		Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_out);
		fadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation paramAnimation) {
			}

			@Override
			public void onAnimationRepeat(Animation paramAnimation) {
			}

			@Override
			public void onAnimationEnd(Animation paramAnimation) {
				mLoading.setVisibility(View.GONE);
				anim1.end();
				anim2.end();
				anim3.end();
			}
		});
		mLlLoading.startAnimation(fadeOut);
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

	private DataSetList datasetlist;
	private ArrayList<String> nameList;
	private ArrayList<String> valueList;
	private ArrayList<String> documentId;

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				Log.i(TAG, newsEnity.getNewsTitle() + " 的新闻详情");
				datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from " + mTabName + " where NewsTitle = '"
						+ newsEnity.getNewsTitle() + "'", "", "", "ContentDetailed", "SEARCHYOUNGCONTENT",
						new DocInfor("", mTabName), true, false));
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			return 1;
		}

		protected void onPreExecute() {
			startLoading();
		}

		protected void onPostExecute(Integer result) {
			switch (result) {
			case -1:// Exception
				if (Util.isNetworkAvailable(context)) {
					Toast.makeText(context, getString(R.string.serverFailed), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					nameList = (ArrayList<String>) datasetlist.nameList;
					valueList = (ArrayList<String>) datasetlist.valueList;
					documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
					int n = 0;
					for (int i = 0; i < nameList.size(); i++) {
						if ("ContentDetailed".equals(nameList.get(i))) {
							newsEnity.setNewsDetails(valueList.get(i));
							newsEnity.setNewsBigDocumentId(documentId.get(n));
							n++;
						}
					}
					mTvTitle.setText(newsEnity.getNewsTitle());
					mTvTime.setText(newsEnity.getNewsTime().substring(0, 19));
					mTvContent.setText(newsEnity.getNewsDetails());
					if (newsEnity.getNewsBigDocumentId().length() <= 6) {
						mIvBanner.setVisibility(View.GONE);
					} else {
						mIvBanner.setVisibility(View.VISIBLE);
						// BitmapUtils bitmapUtils = new BitmapUtils(context);
						// bitmapUtils.display(mIvBanner,
						// Util.getImgUrl(newsEnity.getNewsBigDocumentId(),
						// "png"));
						Util.displayImg(context, mIvBanner, newsEnity.getNewsBigDocumentId(), "png");
					}
				}
				break;
			}
			endLoading();
		}
	}
}
