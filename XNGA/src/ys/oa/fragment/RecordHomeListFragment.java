package ys.oa.fragment;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.activity.RecordDetailActivity;
import ys.oa.activity.RecordListActivity;
import ys.oa.adapter.ListRecordHomeAdapter;
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
import com.xutils.entities.RecordHomeEnity;

/**
 * 入户登记表列表
 */
public class RecordHomeListFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener {
	private ListRecordHomeAdapter mAdapter;
	private ListView mListView;
	private RecordListActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;

	public static RecordHomeListFragment newInstance() {
		RecordHomeListFragment fragment = new RecordHomeListFragment();
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
				startActivity(new Intent(mActivity, RecordDetailActivity.class).putExtras(b).putExtra("Title", "入户登记表"));
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
	private List<RecordHomeEnity> enityList, listAll;
	private Integer index;

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			try {
				// DbUtils db = DbUtils.create(mActivity);
				// List<RecordHomeEnity> allEnityList =
				// db.findAll(Selector.from(RecordHomeEnity.class).orderBy("time",
				// true));
				// if (allEnityList == null) {
				// RecordHomeEnity enity;
				// for (int i = 0; i < 30; i++) {
				// enity = new RecordHomeEnity();
				// enity.setTime(""
				// + (System.currentTimeMillis() - 12L * 3600 * 1000 * i - new
				// Random().nextInt(12 * 3600) * 1000));
				// enity.setRegTime("登记时间" + i);
				// enity.setRegPoliceMan("社区民警" + i);
				// enity.setOwnerName("业主姓名" + i);
				// enity.setOwnerSex("男");
				// enity.setOwnerId("178312932891332");
				// enity.setOwnerPhone("联系电话" + i);
				// enity.setHouseAddress("房屋地址" + i);
				// enity.setHouseUse("房屋用途" + i);
				// enity.setHouseArea("房屋面积" + i);
				// enity.setPeopleNum("居住人数" + i);
				// enity.setBeginDate("出租日期" + i);
				// enity.setEndDate("注销日期" + i);
				// enity.setTenantName("承租人姓名" + i);
				// enity.setTenantSex("男");
				// enity.setTenantId("承租人身份证号" + i);
				// enity.setTenantHkAddress("户口所在地" + i);
				// db.save(enity);
				// }
				// allEnityList =
				// db.findAll(Selector.from(RecordHomeEnity.class).orderBy("time",
				// true));
				// }
				// enityList = new ArrayList<RecordHomeEnity>();
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

				if ("load".equals(params[0]) || "refresh".equals(params[0])) {
					mPage = 1;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_RECORD_HOME where username='"
							+ Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC", "", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_RECORD_HOME"), true, false));
				} else if ("upload".equals(params[0])) {
					index = EVERY_PAGE * mPage;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_RECORD_HOME where username='"
							+ Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC", "", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_RECORD_HOME"), true, false, index.toString()));
					mPage++;
				}
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					nameList = (ArrayList<String>) datasetlist.nameList;
					valueList = (ArrayList<String>) datasetlist.valueList;
					documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
					enityList = new ArrayList<RecordHomeEnity>();
					RecordHomeEnity enity = null;
					int n = 0;
					for (int i = 0; i < nameList.size(); i++) {
						if ("regTime".equals(nameList.get(i))) {
							enity = new RecordHomeEnity();
							enity.setRegTime(valueList.get(i));
						} else if ("regPoliceMan".equals(nameList.get(i))) {
							enity.setRegPoliceMan(valueList.get(i));
						} else if ("ownerName".equals(nameList.get(i))) {
							enity.setOwnerName(valueList.get(i));
						} else if ("ownerSex".equals(nameList.get(i))) {
							enity.setOwnerSex(valueList.get(i));
						} else if ("ownerId".equals(nameList.get(i))) {
							enity.setOwnerId(valueList.get(i));
						} else if ("ownerPhone".equals(nameList.get(i))) {
							enity.setOwnerPhone(valueList.get(i));
						} else if ("houseAddress".equals(nameList.get(i))) {
							enity.setHouseAddress(valueList.get(i));
						} else if ("houseUse".equals(nameList.get(i))) {
							enity.setHouseUse(valueList.get(i));
						} else if ("houseArea".equals(nameList.get(i))) {
							enity.setHouseArea(valueList.get(i));
						} else if ("peopleNum".equals(nameList.get(i))) {
							enity.setPeopleNum(valueList.get(i));
						} else if ("beginDate".equals(nameList.get(i))) {
							enity.setBeginDate(valueList.get(i));
						} else if ("endDate".equals(nameList.get(i))) {
							enity.setEndDate(valueList.get(i));
						} else if ("tenantName".equals(nameList.get(i))) {
							enity.setTenantName(valueList.get(i));
						} else if ("tenantSex".equals(nameList.get(i))) {
							enity.setTenantSex(valueList.get(i));
						} else if ("tenantId".equals(nameList.get(i))) {
							enity.setTenantId(valueList.get(i));
						} else if ("tenantHkAddress".equals(nameList.get(i))) {
							enity.setTenantHkAddress(valueList.get(i));
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
				listAll = enityList;
				mAdapter = new ListRecordHomeAdapter(mActivity, listAll);
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
