package ys.oa.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.activity.RecordDetailActivity;
import ys.oa.activity.RecordListActivity;
import ys.oa.adapter.ListRecordAdapter;
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
import com.xutils.entities.RecordEnity;

/**
 * 记录表列表
 */
public class RecordListFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener {
	private ListRecordAdapter mAdapter;
	private ListView mListView;
	private RecordListActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;

	public static RecordListFragment newInstance() {
		RecordListFragment fragment = new RecordListFragment();
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
				startActivity(new Intent(mActivity, RecordDetailActivity.class).putExtras(b).putExtra("Title", "出警记录表"));
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
	private List<RecordEnity> enityList, allList;
	private Integer index;

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			try {
				DbUtils db = DbUtils.create(mActivity);
				List<RecordEnity> allEnityList = db.findAll(Selector.from(RecordEnity.class).orderBy("time", true));
				if (allEnityList == null) {
					RecordEnity enity;
					for (int i = 0; i < 30; i++) {
						enity = new RecordEnity();
						enity.setTime(""
								+ (System.currentTimeMillis() - 12L * 3600 * 1000 * i - new Random().nextInt(12 * 3600) * 1000));
						enity.setAlarmTime("接警时间" + i);
						enity.setAlarmPoliceMan("接警民警" + i);
						enity.setAlarmUnit("接警单位" + i);
						enity.setAlarmManName("报警人" + i);
						enity.setAlarmManAddress("地址" + i);
						enity.setAlarmManPhone("联系电话" + i);
						enity.setAlarmType("报警形式" + i);
						enity.setAlarmHasMaterial("有无材料" + i);
						enity.setAlarmAddress("报警地点" + i);
						enity.setAlarmInfo("简要报警内容" + i);
						enity.setAlarmHandler("出警民警" + i);
						enity.setAlarmTimeDeparture("出警时间" + i);
						enity.setAlarmTimeReach("到达时间" + i);
						enity.setAlarmHandleInfo("处警经过及结果" + i);
						db.save(enity);
					}
					allEnityList = db.findAll(Selector.from(RecordEnity.class).orderBy("time", true));
				}
				enityList = new ArrayList<RecordEnity>();
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
				// if (mPage > 2) {
				// T.showSnack(mActivity, "已加载全部");
				// }
				mLoadingFooter.setState(LoadingFooter.State.TheEnd);
			}
			switch (result) {
			case 0:
				break;
			case -1:// Exception
				break;
			case 1:
				allList = enityList;
				mAdapter = new ListRecordAdapter(mActivity, allList);
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
