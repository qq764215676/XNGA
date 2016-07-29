package ys.oa.fragment;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.activity.CollectBikeActivity;
import ys.oa.activity.CollectCarActivity;
import ys.oa.activity.CollectGoodActivity;
import ys.oa.activity.CollectPeopleActivity;
import ys.oa.activity.CollectPeopleListActivity;
import ys.oa.activity.QueryResultListActivity;
import ys.oa.activity.RegListActivity;
import ys.oa.adapter.ListCollectPeopleAdapter;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.CollectGoodEnity;
import ys.oa.enity.CollectPeopleEnity;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.provider.SQLDataEntity;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.anim.dialog.MsgSlidingDialog;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;
import com.refactech.driibo.CardsAnimationAdapter;
import com.refactech.driibo.ListViewUtils;
import com.refactech.driibo.LoadingFooter;
import ys.nlga.activity.R;

/**
 * wf 人员检查列表
 */
public class CollectPeopleFragment extends Fragment implements PullToRefreshAttacher.OnRefreshListener {
	private ListCollectPeopleAdapter mAdapter;
	private ListView mListView;
	private CollectPeopleListActivity mActivity;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;
	
	private EnforcementDataManager dataManager;
	public static Context c;
	
	private TextView regTimeTextView = null;
	private Button btnOffWork = null;
	private SpUtil mSpUtil = null;

	public static CollectPeopleFragment newInstance(Context c) {
		CollectPeopleFragment.c = c;
		CollectPeopleFragment fragment = new CollectPeopleFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dataManager = EnforcementDataManager.getInstance(c);
		mSpUtil = AppData.getInstance().getSpUtil();
	}



	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_demo_list, null);
		mActivity = (CollectPeopleListActivity) getActivity();
		mListView = (ListView) contentView.findViewById(R.id.listView);
		
		regTimeTextView = (TextView)contentView.findViewById(R.id.reg_time);
		btnOffWork = (Button)contentView.findViewById(R.id.btn_off_work);
		btnOffWork.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CollectPeopleListActivity.queryService.endAlertTimer();
			}
		});
//		View header = new View(mActivity);
		View header = LayoutInflater.from(mActivity).inflate(R.layout.enforcement_header, null);
		initHeadView(header);
		mPullToRefreshAttacher = ((CollectPeopleListActivity) mActivity).getPullToRefreshAttacher();
		mPullToRefreshAttacher.setRefreshableView(mListView, this);
		mLoadingFooter = new LoadingFooter(mActivity);
		mListView.addHeaderView(header);
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
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 序号从0开始
				// Toast.makeText(mActivity, "" + (position -
				// mListView.getHeaderViewsCount()), Toast.LENGTH_SHORT).show();
				if(position == 0) {
					return ;
				}else {
					SQLDataEntity entity = listAll.get(position-mListView.getHeaderViewsCount());
					if(entity != null) {
						String type = entity.getCheckType();
						if(type!= null) {
							Bundle b = new Bundle();
							if(Constants.PPL_CKS.equals(type)) {
								CollectPeopleEnity e = dataManager.queryPPLById(entity.getContentId()); 
								if(e!=null) {
									b.putSerializable("enity", e);
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
							}else if(Constants.NVEH_CKS_BICYCLE.equals(type)) {
								CollectCarEnity e = dataManager.queryVehById(entity.getContentId()); 
								if(e!=null) {
									b.putSerializable("enity", e);
									Constants.vehicleType="1";
									startActivity(new Intent(mActivity, CollectBikeActivity.class).putExtras(b));
								}
							}else if(Constants.NVEH_CKS_ELECTROMOBILE.equals(type)) {
								CollectCarEnity e = dataManager.queryVehById(entity.getContentId()); 
								if(e!=null) {
									Constants.vehicleType="0";
									b.putSerializable("enity", e);
									startActivity(new Intent(mActivity, CollectBikeActivity.class).putExtras(b));
								}
							}else if(Constants.GOODS_CKS.equals(type)) {
								CollectCarEnity e = dataManager.queryVehById(entity.getContentId()); 
								if(e!=null) {
									b.putSerializable("enity", e);
									startActivity(new Intent(mActivity, CollectGoodActivity.class).putExtras(b));
								}
							}
						}
					}
				}
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

//modify by fcr at 2016-01-17
	public void loadFirstPage() {
//		loadData(1);
		listAll = dataManager.queryEnforcementList();
		if(listAll == null) {
			return ;
		}
		mAdapter = new ListCollectPeopleAdapter(mActivity, listAll,"");
		mListView.setAdapter(mAdapter);
		mPullToRefreshAttacher.setRefreshing(false);
		mLoadingFooter.setState(LoadingFooter.State.TheEnd);
		setTypeCount(listAll);
		setRegTime();
	}

	public void loadFirstPageAndScrollToTop() {
		ListViewUtils.smoothScrollListViewToTop(mListView);
		loadFirstPage();
	}

	public void onRefreshStarted(View view) {
		loadFirstPage();
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(regTV != null) {
			regTV.setTextColor(Color.BLACK);
		}
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
	private ArrayList<SQLDataEntity> enityList, listAll = null;
	private Integer index;

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
				mAdapter = new ListCollectPeopleAdapter(mActivity, listAll,"");
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
	
	private TextView enfNumTV = null;
	private TextView pplNumTV = null;
	private TextView vehCarTV = null;
	private TextView vehMTV = null;
	private TextView nvehZTV = null;
	private TextView nvehDTV = null;
	private TextView goodsTV = null;
	private TextView regTV = null;
	public void initHeadView(View v) {
		enfNumTV = (TextView)v.findViewById(R.id.enf_count);
		pplNumTV = (TextView)v.findViewById(R.id.tv_ppl);
		vehCarTV = (TextView)v.findViewById(R.id.tv_veh_car);
		vehMTV = (TextView)v.findViewById(R.id.tv_veh_m);
		nvehZTV = (TextView)v.findViewById(R.id.tv_nveh_z);
		nvehDTV = (TextView)v.findViewById(R.id.tv_nveh_d);
		goodsTV = (TextView)v.findViewById(R.id.tv_goods);
		regTV = (TextView)v.findViewById(R.id.reg_text);
		regTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				startActivityForResult(new Intent(this, RegListActivity.class), 0);
				regTV.setTextColor(Color.BLUE);
				Intent i = new Intent();
				i.setClass(mActivity, RegListActivity.class);
				startActivity(i);
			}
		});
	}
	
	
	
	public void setTypeCount(ArrayList<SQLDataEntity> allList) {
		if(allList == null || allList.size() == 0) {
			return;
		}
		int ppl_num = 0;
		int veh_c_num = 0;
		int veh_m_num = 0;
		int n_veh_b_num = 0;
		int n_veh_e_num = 0;
		int good_num = 0;
		for(int i=0;i<allList.size();i++) {
			Log.v("fcr","allList.get(i)----"+allList.get(i).toString());
			String type = allList.get(i).getCheckType().toString();
			if(type != null) {
				if(type.equals(Constants.PPL_CKS)) {
					ppl_num = ppl_num + 1;
				}else if(type.equals(Constants.VEH_CKS_CAR)) {
					veh_c_num = veh_c_num + 1;
				}else if(type.equals(Constants.VEH_CKS_MOTORCYCLE)) {
					veh_m_num = veh_m_num+ 1;
				}else if(type.equals(Constants.NVEH_CKS_BICYCLE)) {
					n_veh_b_num = n_veh_b_num + 1;
				}else if(type.equals(Constants.NVEH_CKS_ELECTROMOBILE)) {
					n_veh_e_num = n_veh_e_num + 1;
				}else if(type.equals(Constants.GOODS_CKS)) {
					good_num = good_num + 1;
				}
			}
		}
		enfNumTV.setText(allList.size()+"个");
		pplNumTV.setText(ppl_num+"个");
		vehCarTV.setText(veh_c_num+"个");
		vehMTV.setText(veh_m_num+"个");
		nvehZTV.setText(n_veh_b_num+"个");
		nvehDTV.setText(n_veh_e_num+"个");
		goodsTV.setText(good_num+"个");
		
	}
	
	public void setRegTime() {
		regTimeTextView.setText("上一次签到时间：" + mSpUtil.getLastRegTime());
	}
	
}
