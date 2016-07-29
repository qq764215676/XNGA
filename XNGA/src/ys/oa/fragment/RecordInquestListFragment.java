package ys.oa.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.activity.RecordDetailActivity;
import ys.oa.activity.RecordListActivity;
import ys.oa.adapter.ListRecordInquestAdapter;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.refactech.driibo.CardsAnimationAdapter;
import com.refactech.driibo.ListViewUtils;
import com.refactech.driibo.LoadingFooter;
import ys.nlga.activity.R;
import com.xutils.entities.RecordInquestEnity;

/**
 * 记录表列表
 */
public class RecordInquestListFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener {
	private ListRecordInquestAdapter mAdapter;
	private ListView mListView;
	private RecordListActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;

	public static RecordInquestListFragment newInstance() {
		RecordInquestListFragment fragment = new RecordInquestListFragment();
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_demo_list, null);
		ViewUtils.inject(this, contentView);
		mActivity = (RecordListActivity) getActivity();
		mListView = (ListView) contentView.findViewById(R.id.listView);
		View header = new View(mActivity);
		mPullToRefreshAttacher = ((RecordListActivity) mActivity).getPullToRefreshAttacher();
		mPullToRefreshAttacher.setRefreshableView(mListView, this);
		mLoadingFooter = new LoadingFooter(mActivity);
		mListView.addHeaderView(header);
		mListView.addFooterView(mLoadingFooter.getView());

		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (mLoadingFooter.getState() == LoadingFooter.State.Loading
						|| mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
					return;
				}
				if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0
						&& totalItemCount != mListView.getHeaderViewsCount() + mListView.getFooterViewsCount()
						&& mAdapter.getCount() > 0) {
					loadNextPage();
				}
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 序号从0开始
				// Toast.makeText(mActivity, "" + (position -
				// mListView.getHeaderViewsCount()), Toast.LENGTH_SHORT).show();
				Bundle b = new Bundle();
				b.putSerializable("enity", allList.get(position - mListView.getHeaderViewsCount()));
				startActivity(new Intent(mActivity, RecordDetailActivity.class).putExtras(b).putExtra("Title",
						"现场勘查记录表"));
			}
		});

		loadFirstPage();
		return contentView;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mPullToRefreshAttacher.getHeaderTransformer().onConfigurationChanged(mActivity);
	}

	private void loadNextPage() {
		mLoadingFooter.setState(LoadingFooter.State.Loading);
		loadData(mPage + 1);
	}

	public void loadFirstPage() {
		loadData(1);
	}

	public void loadFirstPageAndScrollToTop() {
		ListViewUtils.smoothScrollListViewToTop(mListView);
		loadFirstPage();
	}

	public void onRefreshStarted(View view) {
		loadFirstPage();
	}

	private void loadData(final int page) {
		isRefreshFromTop = page == 1;
		if (!mPullToRefreshAttacher.isRefreshing() && isRefreshFromTop) {
			mPullToRefreshAttacher.setRefreshing(true);
		}
		if (isRefreshFromTop) {
			mLoadingFooter.setState(LoadingFooter.State.Idle);
			if (Util.isNetworkAvailable(mActivity)) {
				new AsyncLoader().execute("load");
			} else {
				T.showSnack(mActivity, R.string.networkerror);
				mPullToRefreshAttacher.setRefreshing(false);
			}
		} else {
			if (Util.isNetworkAvailable(mActivity)) {
				new AsyncLoader().execute("upload");
			} else {
				T.showSnack(mActivity, R.string.networkerror);
				mPullToRefreshAttacher.setRefreshing(false);
			}
		}
	}

	public static final int EVERY_PAGE = 7;
	private int mPage = 1;
	private boolean isRefreshFromTop;
	private List<RecordInquestEnity> enityList, allList;
	private Integer index;

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			try {
				DbUtils db = DbUtils.create(mActivity);
				List<RecordInquestEnity> allEnityList = db.findAll(Selector.from(RecordInquestEnity.class).orderBy("time", true));
				if (allEnityList == null) {
					RecordInquestEnity enity;
					for (int i = 0; i < 30; i++) {
						enity = new RecordInquestEnity();
						enity.setTime(""
								+ (System.currentTimeMillis() - 12L * 3600 * 1000 * i - new Random().nextInt(12 * 3600) * 1000));
						enity.setCaseTime("案发时间" + i);
						enity.setCaseAddress("发案地点" + i);
						enity.setCaseInfo("发案过程" + i);
						enity.setInquestTime("勘验时间" + i);
						enity.setInquestEndTime("勘验结束时间" + i);
						enity.setInquestAddress("勘验地点" + i);
						enity.setInquestLeader("勘验指挥人员" + i);
						enity.setInquestMember("勘验检查人员" + i);
						enity.setSceneLoss("现场损失情况" + i);
						enity.setSceneDispose("现场处置意见" + i);
						enity.setCaseProperty("案件性质描述" + i);
						enity.setCaseUnit("涉案单位描述" + i);
						enity.setCaseMethod("作案手段描述" + i);
						enity.setCaseFeature("作案特点描述" + i);
						enity.setCaseTool("作案工具描述" + i);
						enity.setOtherInfo("备注" + i);
						db.save(enity);
					}
					allEnityList = db.findAll(Selector.from(RecordInquestEnity.class).orderBy("time", true));
				}

				enityList = new ArrayList<RecordInquestEnity>();
				if ("load".equals(params[0])) {
					mPage = 1;
					if (allEnityList.size() <= EVERY_PAGE) {
						enityList.addAll(allEnityList.subList(0, allEnityList.size()));
					} else {
						enityList.addAll(allEnityList.subList(0, EVERY_PAGE));
					}
				} else if ("upload".equals(params[0])) {
					index = EVERY_PAGE * mPage;
					if (allEnityList.size() <= index + EVERY_PAGE) {
						enityList.addAll(allEnityList.subList(index, allEnityList.size()));
					} else {
						enityList.addAll(allEnityList.subList(index, index + EVERY_PAGE));
					}
					mPage++;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			if ("load".equals(params[0])) {
				return 1;
			} else if ("upload".equals(params[0])) {
				return 2;
			} else {
				return 0;
			}
		}

		protected void onPreExecute() {
		}

		protected void onPostExecute(Integer result) {
			if (isRefreshFromTop) {
				mPullToRefreshAttacher.setRefreshComplete();
			} else {
				mPullToRefreshAttacher.setRefreshComplete();
				mLoadingFooter.setState(LoadingFooter.State.Idle);
			}
			if (enityList.size() < EVERY_PAGE) {
				mLoadingFooter.setState(LoadingFooter.State.TheEnd);
			}
			switch (result) {
			case 0:
				break;
			case -1:// Exception
				break;
			case 1:
				allList = enityList;
				mAdapter = new ListRecordInquestAdapter(mActivity, allList);
				AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
				animationAdapter.setListView(mListView);
				mListView.setAdapter(animationAdapter);
				break;
			case 2:
				allList.addAll(enityList);
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	}
}
