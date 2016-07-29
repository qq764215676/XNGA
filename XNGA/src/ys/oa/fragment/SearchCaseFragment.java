package ys.oa.fragment;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.activity.SearchActivity;
import ys.oa.adapter.ListCaseAdapter;
import ys.oa.enity.CaseEnity;
import ys.oa.fragment.NewsFragment.AsyncLoader;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;
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
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.CardsAnimationAdapter;
import com.refactech.driibo.ListViewUtils;
import com.refactech.driibo.LoadingFooter;
import ys.nlga.activity.R;

/**
 * wf 案件搜索列表
 */
public class SearchCaseFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener {
	private ListCaseAdapter mAdapter;
	private ListView mListView;
	private SearchActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;
	private String mSearchTxt = null;

	public static SearchCaseFragment newInstance() {
		SearchCaseFragment fragment = new SearchCaseFragment();
		return fragment;
	}

	public void search(String searchTxt) {
		mSearchTxt = searchTxt;
		loadFirstPage();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_demo_list, null);
		mActivity = (SearchActivity) getActivity();
		mListView = (ListView) contentView.findViewById(R.id.listView);
		View header = new View(mActivity);
		mPullToRefreshAttacher = ((SearchActivity) mActivity).getPullToRefreshAttacher();
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

	private void loadFirstPage() {
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
	private ArrayList<CaseEnity> enityList, listAll;
	private Integer index;

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			try {
				if ("load".equals(params[0]) || "refresh".equals(params[0])) {
					mPage = 1;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect(mSearchTxt == null ? "from NLGA_CASES"
							: "from NLGA_CASES where CaseTitle like '%" + mSearchTxt + "%'", "" + EVERY_PAGE,
							"CaseTime DESC", "CaseTitle, Content,CaseAddress, CaseTime", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_CASES"), true, false));
				} else if ("upload".equals(params[0])) {
					index = EVERY_PAGE * mPage;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect(mSearchTxt == null ? "from NLGA_CASES"
							: "from NLGA_CASES where CaseTitle like '%" + mSearchTxt + "%'", "" + EVERY_PAGE,
							"CaseTime DESC", "CaseTitle, Content,CaseAddress, CaseTime", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_CASES"), true, false, index.toString()));
					mPage++;
				}
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					nameList = (ArrayList<String>) datasetlist.nameList;
					valueList = (ArrayList<String>) datasetlist.valueList;
					documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
					enityList = new ArrayList<CaseEnity>();
					CaseEnity enity = null;
					int n = 0;
					for (int i = 0; i < nameList.size(); i++) {
						if ("CaseTitle".equals(nameList.get(i))) {
							enity = new CaseEnity();
							enity.setCaseTitle(valueList.get(i));
						} else if ("Content".equals(nameList.get(i))) {
							enity.setCaseInfo(valueList.get(i));
						} else if ("CaseAddress".equals(nameList.get(i))) {
							enity.setCaseAddress(valueList.get(i));
						} else if ("CaseTime".equals(nameList.get(i))) {
							enity.setCaseTime(valueList.get(i).substring(0, 10));
							n++;
							enityList.add(enity);
						}
					}
				}
				//
				// ArrayList<CaseEnity> allEnityList = new
				// ArrayList<CaseEnity>();
				// CaseEnity enity = null;
				// for (int i = 0; i < 40; i++) {
				// if (mSearchTxt == null) {
				// enity = new CaseEnity();
				// enity.setCaseTitle("马山县城制毒案" + i);
				// enity.setCaseInfo("9月10日，禁毒支队获悉有一名吸毒人员从外地购买麻黄草运往马山县城密谋制造毒品。三天后，民警分析认为嫌疑人已制造了一批毒品，决定收网抓捕。在马山县城，民警捣毁了廉租房小区内的制毒窝点，抓获犯罪嫌疑人汪某、李某，缴获成品冰毒2707.8克以及制毒原料、配剂、工具一批。");
				// enity.setCaseAddress("马山县城廉租房小区");
				// enity.setCaseTime("2013-9-10");
				// allEnityList.add(enity);
				// } else {
				// if (("马山县城制毒案" + i).indexOf(mSearchTxt) != -1) {
				// enity = new CaseEnity();
				// enity.setCaseTitle("马山县城制毒案" + i);
				// enity.setCaseInfo("9月10日，禁毒支队获悉有一名吸毒人员从外地购买麻黄草运往马山县城密谋制造毒品。三天后，民警分析认为嫌疑人已制造了一批毒品，决定收网抓捕。在马山县城，民警捣毁了廉租房小区内的制毒窝点，抓获犯罪嫌疑人汪某、李某，缴获成品冰毒2707.8克以及制毒原料、配剂、工具一批。");
				// enity.setCaseAddress("马山县城廉租房小区");
				// enity.setCaseTime("2013-9-10");
				// allEnityList.add(enity);
				// }
				// }
				// }
				// enityList = new ArrayList<CaseEnity>();
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
				// Toast.makeText(mActivity, "已加载全部",
				// Toast.LENGTH_SHORT).show();
				mLoadingFooter.setState(LoadingFooter.State.TheEnd);
			}
			switch (result) {
			case 0:
				break;
			case -1:// Exception
				break;
			case 1:
				listAll = enityList;
				mAdapter = new ListCaseAdapter(mActivity, listAll);
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
