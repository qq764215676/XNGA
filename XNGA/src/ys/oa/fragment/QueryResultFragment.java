package ys.oa.fragment;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.nlga.activity.R;
import ys.oa.activity.CollectBikeActivity;
import ys.oa.activity.CollectCarActivity;
import ys.oa.activity.CollectPeopleActivity;
import ys.oa.activity.CollectPeopleListActivity;
import ys.oa.activity.QueryResultListActivity;
import ys.oa.adapter.ListCollectPeopleAdapter;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.CollectPeopleEnity;
import ys.oa.enity.QueryPeopleResultEntity;
import ys.oa.enity.QueryVehResultEntity;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.provider.SQLDataEntity;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;
import com.refactech.driibo.CardsAnimationAdapter;
import com.refactech.driibo.ListViewUtils;
import com.refactech.driibo.LoadingFooter;

public class QueryResultFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener{
	private ListCollectPeopleAdapter mAdapter;
	private ListView mListView;
	private QueryResultListActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;
	
	private EnforcementDataManager dataManager;
	public static Context c;

	public static QueryResultFragment newInstance(Context c) {
		QueryResultFragment.c = c;
		QueryResultFragment fragment = new QueryResultFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dataManager = EnforcementDataManager.getInstance(c);
		c.getContentResolver().registerContentObserver(Uri.parse(dataManager.getQuery_result_buffer().toString()), true,new DataObserver(handler));

	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_demo_list, null);
		mActivity = (QueryResultListActivity) getActivity();
		mListView = (ListView) contentView.findViewById(R.id.listView);
		mPullToRefreshAttacher = ((QueryResultListActivity) mActivity).getPullToRefreshAttacher();
		mPullToRefreshAttacher.setRefreshableView(mListView, this);
		mLoadingFooter = new LoadingFooter(mActivity);
		bottomLayout = (RelativeLayout)contentView.findViewById(R.id.lay_bottom);
		bottomLayout.setVisibility(View.GONE);
		mListView.addFooterView(mLoadingFooter.getView());

		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				/*if (mLoadingFooter.getState() == LoadingFooter.State.Loading
						|| mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
					return;
				}*/
				/*if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0
						&& totalItemCount != mListView.getHeaderViewsCount() + mListView.getFooterViewsCount()
						&& mAdapter.getCount() > 0) {
					loadNextPage();
				}*/
			}
		});
		
		//Updated at 2016/02/22
		Log.v("USER_AUTH", Constants.USER_AUTH);
		//当用户权限为协警时，不可点击查看检查人员详细资料
		if(!Constants.USER_AUTH.equals(Constants.USER_AUTH_XJ)){
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// 序号从0开始
					// Toast.makeText(mActivity, "" + (position -
					// mListView.getHeaderViewsCount()), Toast.LENGTH_SHORT).show();
					Log.v("fcr","position ---==="+position+"listAll---"+listAll);
						SQLDataEntity entity= listAll.get(position);
						if(entity != null) {
							String type = entity.getCheckType();
							if(type != null) {
								Bundle b = new Bundle();
								if(Constants.PPL_CKS.equals(type)) {
									Log.v("fcr","entity---"+entity.getContentId());
									QueryPeopleResultEntity e = dataManager.queryResultItem(entity.getContentId()); 
									if(e!=null) {
										b.putSerializable("enity", e);
										b.putString("type", "1");
										startActivity(new Intent(mActivity, CollectPeopleActivity.class).putExtras(b));
									}
								}
								else if(Constants.VEH_CKS_CAR.equals(type)) {
									CollectCarEnity e = dataManager.queryVehById(entity.getContentId()); 
									if(e!=null) {
										Constants.vehicleType="2";
										b.putSerializable("enity", e);
										startActivity(new Intent(mActivity, CollectCarActivity.class).putExtras(b));
									}
								}else if(Constants.VEH_CKS_MOTORCYCLE.equals(type)) {
									CollectCarEnity e = dataManager.queryVehById(entity.getContentId()); 
									if(e!=null) {
										Constants.vehicleType="3";
										b.putSerializable("enity", e);
										startActivity(new Intent(mActivity, CollectCarActivity.class).putExtras(b));
									}
								}
								else if(Constants.NVEH_CKS_BICYCLE.equals(type)) {
									QueryVehResultEntity e = dataManager.queryVehResultItem(entity.getContentId()); 
									if(e!=null) {
										b.putSerializable("enity", e);
										Constants.vehicleType="1";
										b.putString("type", "1");
										startActivity(new Intent(mActivity, CollectBikeActivity.class).putExtras(b));
									}
								}
								else if(Constants.NVEH_CKS_ELECTROMOBILE.equals(type)) {
									QueryVehResultEntity e = dataManager.queryVehResultItem(entity.getContentId());
									Log.v("fcr","entity---"+entity.getID());
									if(e!=null) {
										Constants.vehicleType="0";
										b.putSerializable("enity", e);
										b.putString("type", "1");
										startActivity(new Intent(mActivity, CollectBikeActivity.class).putExtras(b));
									}
								}
								/*else if(Constants.GOODS_CKS.equals(type)) {
									
								}*/
							}
						}
					}
			});
		}
		
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
		listAll = dataManager.queryResultList();
		if(listAll == null) {
			return ;
		}

		mAdapter = new ListCollectPeopleAdapter(mActivity, listAll,"result");
		mListView.setAdapter(mAdapter);
		mPullToRefreshAttacher.setRefreshing(false);
		mLoadingFooter.setState(LoadingFooter.State.TheEnd);
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
//				new AsyncLoader().execute("load");
			} else {
				T.showSnack(mActivity, R.string.networkerror);
				mPullToRefreshAttacher.setRefreshing(false);
			}
		} else {
			if (Util.isNetworkAvailable(mActivity)) {
//				new AsyncLoader().execute("upload");
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
	private ArrayList<SQLDataEntity> enityList, listAll;
	private Integer index;
	private RelativeLayout bottomLayout; 

	// 数据请求
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		protected Integer doInBackground(String... params) {
			try {
				if ("load".equals(params[0]) || "refresh".equals(params[0])) {
					mPage = 1;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_ROAD_PEOPLE where username='"
							+ Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC", "", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_ROAD_PEOPLE"), true, false));
				} else if ("upload".equals(params[0])) {
					index = EVERY_PAGE * mPage;
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_ROAD_PEOPLE where username='"
							+ Constants.USERID + "'", "" + EVERY_PAGE, "updateTime DESC", "", "SEARCHYOUNGCONTENT",
							new DocInfor("", "NLGA_ROAD_PEOPLE"), true, false, index.toString()));
					mPage++;
				}
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					nameList = (ArrayList<String>) datasetlist.nameList;
					valueList = (ArrayList<String>) datasetlist.valueList;
					documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
//					enityList = new ArrayList<CollectPeopleEnity>();
					CollectPeopleEnity enity = null;
					int n = 0;
					for (int i = 0; i < nameList.size(); i++) {
						if ("name".equals(nameList.get(i))) {
							enity = new CollectPeopleEnity();
							enity.setName(valueList.get(i));
						} else if ("Sex".equals(nameList.get(i))) {
							enity.setSex(valueList.get(i));
						} else if ("IdBirthday".equals(nameList.get(i))) {
							enity.setBirthday(valueList.get(i));
						} else if ("CardId".equals(nameList.get(i))) {
							enity.setId(valueList.get(i));
						} else if ("Nation".equals(nameList.get(i))) {
							enity.setNation(valueList.get(i));
						} else if ("Address".equals(nameList.get(i))) {
							enity.setAddress(valueList.get(i));
						} else if ("Info".equals(nameList.get(i))) {
							enity.setInfo(valueList.get(i));
						} else if ("ImgKeys".equals(nameList.get(i))) {
							enity.setImgKeys(valueList.get(i));
						} else if ("collectAddress".equals(nameList.get(i))) {
							enity.setCollectAddress(valueList.get(i));
							enity.setDocumentId(documentId.get(n));
							n++;
//							enityList.add(enity);
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
				mAdapter = new ListCollectPeopleAdapter(mActivity, listAll,"result");
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

	/*@Override
	public void onComplete(Timer timer) {
		// TODO Auto-generated method stub
		if(queryService != null) {
			queryService.cancelTimer(timer);
		}
		loadFirstPage();
	}*/
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			loadFirstPage();
		}
		
	};
	
	class DataObserver extends ContentObserver {
		private Handler handler;
		public DataObserver(Handler handler) {
			super(handler);
			this.handler = handler;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			handler.sendEmptyMessage(1);
		}
		
		
		
	}

}
