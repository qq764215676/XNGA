package ys.oa.fragment;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.activity.RecordDetailActivity;
import ys.oa.activity.RecordListActivity;
import ys.oa.adapter.ListRecordHouseAdapter;
import ys.oa.util.Constants;
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
import com.lidroid.xutils.ViewUtils;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.CardsAnimationAdapter;
import com.refactech.driibo.ListViewUtils;
import com.refactech.driibo.LoadingFooter;
import ys.nlga.activity.R;
import com.xutils.entities.RecordHouseEnity;

/**
 * 记录表列表
 */
public class RecordHouseListFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener {
	private ListRecordHouseAdapter mAdapter;
	private ListView mListView;
	private RecordListActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;

	public static RecordHouseListFragment newInstance() {
		RecordHouseListFragment fragment = new RecordHouseListFragment();
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
				b.putSerializable("enity", listAll.get(position - mListView.getHeaderViewsCount()));
				startActivity(new Intent(mActivity, RecordDetailActivity.class).putExtras(b)
						.putExtra("Title", "出租屋检查表"));
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
	private DataSetList datasetlist;
	private ArrayList<String> nameList;
	private ArrayList<String> valueList;
	private ArrayList<String> documentId;
	private List<RecordHouseEnity> enityList, listAll;
	private Integer index;

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			try {
				// DbUtils db = DbUtils.create(mActivity);
				// List<RecordHouseEnity> allEnityList =
				// db.findAll(Selector.from(RecordHouseEnity.class).orderBy("time",
				// true));
				// if (allEnityList == null) {
				// RecordHouseEnity enity;
				// for (int i = 0; i < 30; i++) {
				// enity = new RecordHouseEnity();
				// enity.setTime(""
				// + (System.currentTimeMillis() - 12L * 3600 * 1000 * i - new
				// Random().nextInt(12 * 3600) * 1000));
				// enity.setHouseCommunity("所属社区" + i);
				// enity.setHouseAddress("房屋地址" + i);
				// enity.setHouseOwner("房主姓名" + i);
				// enity.setHouseOwnerPhone("房主电话" + i);
				// enity.setHouseKeeper("管理人" + i);
				// enity.setHouseKeeperPhone("管理人电话" + i);
				// enity.setCheckTime("检查时间" + i);
				// enity.setChecker("检查人员" + i);
				// enity.setHouseCheck1("未落实");
				// enity.setHouseCheck2("已落实");
				// enity.setHouseCheck3("未落实");
				// enity.setHouseCheck4("已落实");
				// enity.setHouseCheck5("未落实");
				// enity.setHouseCheck6("未落实");
				// enity.setChangeOpinion("整改意见" + i);
				// enity.setOwnerOpinion("房主意见" + i);
				// db.save(enity);
				// }
				// allEnityList =
				// db.findAll(Selector.from(RecordHouseEnity.class).orderBy("time",
				// true));
				// }
				//
				// enityList = new ArrayList<RecordHouseEnity>();
				// if ("load".equals(params[0])) {
				// mPage = 1;
				// if (allEnityList.size() <= EVERY_PAGE) {
				// enityList.addAll(allEnityList.subList(0,
				// allEnityList.size()));
				// } else {
				// enityList.addAll(allEnityList.subList(0, EVERY_PAGE));
				// }
				// } else if ("upload".equals(params[0])) {
				// index = EVERY_PAGE * mPage;
				// if (allEnityList.size() <= index + EVERY_PAGE) {
				// enityList.addAll(allEnityList.subList(index,
				// allEnityList.size()));
				// } else {
				// enityList.addAll(allEnityList.subList(index, index +
				// EVERY_PAGE));
				// }
				// mPage++;
				// }
				//
				// if ("load".equals(params[0]) || "refresh".equals(params[0]))
				// {
				// mPage = 1;
				// datasetlist =
				// PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_RECORD_HOUSE where username='"
				// + Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC",
				// "", "SEARCHYOUNGCONTENT",
				// new DocInfor("", "NLGA_RECORD_HOUSE"), true, false));
				// } else if ("upload".equals(params[0])) {
				// index = EVERY_PAGE * mPage;
				// datasetlist =
				// PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_RECORD_HOUSE where username='"
				// + Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC",
				// "", "SEARCHYOUNGCONTENT",
				// new DocInfor("", "NLGA_RECORD_HOUSE"), true, false,
				// index.toString()));
				// mPage++;
				// }

				if ("load".equals(params[0]) || "refresh".equals(params[0])) {
					mPage = 1;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_RECORD_HOUSE where username='"
							+ Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC", "", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_RECORD_HOUSE"), true, false));
				} else if ("upload".equals(params[0])) {
					index = EVERY_PAGE * mPage;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_RECORD_HOUSE where username='"
							+ Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC", "", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_RECORD_HOUSE"), true, false, index.toString()));
					mPage++;
				}
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					nameList = (ArrayList<String>) datasetlist.nameList;
					valueList = (ArrayList<String>) datasetlist.valueList;
					documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
					enityList = new ArrayList<RecordHouseEnity>();
					RecordHouseEnity enity = null;
					int n = 0;
					for (int i = 0; i < nameList.size(); i++) {
						if ("houseCommunity".equals(nameList.get(i))) {
							enity = new RecordHouseEnity();
							enity.setHouseCommunity(valueList.get(i));
						} else if ("houseAddress".equals(nameList.get(i))) {
							enity.setHouseAddress(valueList.get(i));
						} else if ("ownerName".equals(nameList.get(i))) {
							enity.setHouseOwner(valueList.get(i));
						} else if ("ownerPhone".equals(nameList.get(i))) {
							enity.setHouseOwnerPhone(valueList.get(i));
						} else if ("houseKeeper".equals(nameList.get(i))) {
							enity.setHouseKeeper(valueList.get(i));
						} else if ("houseKeeperPhone".equals(nameList.get(i))) {
							enity.setHouseKeeperPhone(valueList.get(i));
						} else if ("checkTime".equals(nameList.get(i))) {
							enity.setCheckTime(valueList.get(i));
						} else if ("checker".equals(nameList.get(i))) {
							enity.setChecker(valueList.get(i));
						} else if ("houseCheck1".equals(nameList.get(i))) {
							enity.setHouseCheck1(valueList.get(i));
						} else if ("houseCheck2".equals(nameList.get(i))) {
							enity.setHouseCheck2(valueList.get(i));
						} else if ("houseCheck3".equals(nameList.get(i))) {
							enity.setHouseCheck3(valueList.get(i));
						} else if ("houseCheck4".equals(nameList.get(i))) {
							enity.setHouseCheck4(valueList.get(i));
						} else if ("houseCheck5".equals(nameList.get(i))) {
							enity.setHouseCheck5(valueList.get(i));
						} else if ("houseCheck6".equals(nameList.get(i))) {
							enity.setHouseCheck6(valueList.get(i));
						} else if ("changeOpinion".equals(nameList.get(i))) {
							enity.setChangeOpinion(valueList.get(i));
						} else if ("ownerOpinion".equals(nameList.get(i))) {
							enity.setOwnerOpinion(valueList.get(i));
						} else if ("updateTime".equals(nameList.get(i))) {
							enity.setTime(valueList.get(i));
							n++;
							enityList.add(enity);
						}
					}
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
				listAll = enityList;
				mAdapter = new ListRecordHouseAdapter(mActivity, listAll);
				AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
				animationAdapter.setListView(mListView);
				mListView.setAdapter(animationAdapter);
				break;
			case 2:
				listAll.addAll(enityList);
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	}
}
