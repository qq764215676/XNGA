package ys.oa.fragment;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.activity.NewsActivity;
import ys.oa.activity.NewsDetailActivity;
import ys.oa.adapter.ListNewsAdapter;
import ys.oa.enity.NewsEnity;
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
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.CardsAnimationAdapter;
import com.refactech.driibo.ListViewUtils;
import com.refactech.driibo.LoadingFooter;
import ys.nlga.activity.R;

/**
 * 新闻列表
 */
public class NewsFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener {
	private ListNewsAdapter mAdapter;
	private ListView mListView;
	private NewsActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;
	private static String mOrderby = "LastChangedTime DESC";
	private static String mTabName = "NLGA_NEWS";

	// private boolean isLoadAll = false;
	// private View mTvLoadAll;

	public static NewsFragment newInstance() {
		NewsFragment fragment = new NewsFragment();
		mTabName = "NLGA_NEWS";
		return fragment;
	}

	public static NewsFragment newInstance(String tabName) {
		NewsFragment fragment = new NewsFragment();
		mTabName = tabName;
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_demo_list, null);
		mActivity = (NewsActivity) getActivity();
		mListView = (ListView) contentView.findViewById(R.id.listView);
		// 使用ListView等容器展示图片时可通过PauseOnScrollListener控制滑动和快速滑动过程中时候暂停加载图片
		// mListView.setOnScrollListener(new PauseOnScrollListener(new
		// BitmapUtils(mActivity), false, true));
		View header = new View(mActivity);
		mPullToRefreshAttacher = ((NewsActivity) mActivity).getPullToRefreshAttacher();
		mPullToRefreshAttacher.setRefreshableView(mListView, this);
		mLoadingFooter = new LoadingFooter(mActivity);
		mListView.addHeaderView(header);
		mListView.addFooterView(mLoadingFooter.getView());
		// mTvLoadAll = contentView.findViewById(R.id.tv_load_all);

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
				b.putSerializable("newsEnity", newsListAll.get(position - mListView.getHeaderViewsCount()));
				b.putString("TabName", mTabName);
				startActivity(new Intent(mActivity, NewsDetailActivity.class).putExtras(b));
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
		// isLoadAll = false;
	}

	public static final int EVERY_PAGE = 7;
	private int mPage = 1;
	private boolean isRefreshFromTop;
	private DataSetList datasetlist;
	private ArrayList<String> nameList;
	private ArrayList<String> valueList;
	private ArrayList<String> documentId;
	private ArrayList<NewsEnity> newsEnityList, newsListAll;
	private Integer index;

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			try {
				if ("load".equals(params[0]) || "refresh".equals(params[0])) {
					mPage = 1;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from " + mTabName, "" + EVERY_PAGE,
							mOrderby, "NewsTitle, Content, LastChangedTime", "SEARCHYOUNGCONTENT", new DocInfor("",
									mTabName), true, false));
				} else if ("upload".equals(params[0])) {
					index = EVERY_PAGE * mPage;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from " + mTabName, "" + EVERY_PAGE,
							mOrderby, "NewsTitle, Content, LastChangedTime", "SEARCHYOUNGCONTENT", new DocInfor("",
									mTabName), true, false, index.toString()));
					mPage++;
				}
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					nameList = (ArrayList<String>) datasetlist.nameList;
					valueList = (ArrayList<String>) datasetlist.valueList;
					documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
					newsEnityList = new ArrayList<NewsEnity>();
					NewsEnity newsEnity = null;
					int n = 0;
					for (int i = 0; i < nameList.size(); i++) {
						if ("NewsTitle".equals(nameList.get(i))) {
							newsEnity = new NewsEnity();
							newsEnity.setNewsTitle(valueList.get(i));
						} else if ("Content".equals(nameList.get(i))) {
							newsEnity.setNewsIntro(valueList.get(i));
						} else if ("LastChangedTime".equals(nameList.get(i))) {
							newsEnity.setNewsTime(valueList.get(i));
							newsEnity.setNewsSmallDocumentId(documentId.get(n));
							n++;
							newsEnityList.add(newsEnity);
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
			if (newsEnityList.size() < EVERY_PAGE) {
				// isLoadAll = true;
				if (mPage > 2) {
					T.showSnack(mActivity, "已加载全部");
				}
				mLoadingFooter.setState(LoadingFooter.State.TheEnd);
			}
			switch (result) {
			case 0:
				break;
			case -1:// Exception
				break;
			case 1:
				newsListAll = newsEnityList;
				mAdapter = new ListNewsAdapter(mActivity, newsListAll);
				AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
				animationAdapter.setListView(mListView);
				mListView.setAdapter(animationAdapter);
				break;
			case 2:
				newsListAll.addAll(newsEnityList);
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	}
}
