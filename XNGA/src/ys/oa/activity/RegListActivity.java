package ys.oa.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.nlga.activity.R;
import ys.oa.adapter.RegListAdapter;
import ys.oa.enity.RegInfo;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;

public class RegListActivity extends BaseActivity implements PullToRefreshAttacher.OnRefreshListener, OnClickListener
{
	private ImageView btn_signIn;
	
	private Context context;
	private ListView regListView;
	private RegListAdapter regListAdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private ImageButton btn_back;
	private RegListActivity mActivity;
	private ArrayList<RegInfo> resultList = new ArrayList<RegInfo>();

	@Override
	protected void onCreate(Bundle arg0)
	{
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_reg_list);
		
		findView();
		initListener();
		
		Util.initExce(this);
		mActivity = this;
		context = this;
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.hide();//因需求修改而隐藏
		setTitle("巡逻签到列表");
		initWidget();
		getTime();
		getListFromServer();

	}

	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_xlqdlb_back);
		btn_signIn = (ImageView) findViewById(R.id.btn_xlqdlb_signIn);
	}
	
	private void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_signIn.setOnClickListener(this);
	}

	public void initWidget()
	{
		regListView = (ListView) findViewById(R.id.reg_list);
		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.pulldown_fade_in;
		options.headerOutAnimation = R.anim.pulldown_fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);
		mPullToRefreshAttacher.setRefreshableView(regListView, this);

		regListView.setOnScrollListener(new AbsListView.OnScrollListener()
		{

			public void onScrollStateChanged(AbsListView view, int scrollState)
			{

			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{

			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return false;
	}

	@Override
	public void onRefreshStarted(View view)
	{
		// TODO Auto-generated method stub
		getListFromServer();
	}

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			regListAdapter = new RegListAdapter(context, resultList);
			regListView.setAdapter(regListAdapter);
			regListAdapter.notifyDataSetChanged();
			mPullToRefreshAttacher.setRefreshComplete();
		};
	};

	public void getListFromServer()
	{
		new AsyncGetRegList().execute("");
	}

	private DataSetList datasetlist;

	class AsyncGetRegList extends AsyncTask<String, Integer, Integer>
	{

		protected Integer doInBackground(String... params)
		{

			if (Util.isNetworkAvailable(context))
			{
				try
				{
					datasetlist = PostHttp.PostDocXML(XmlPackage.packageSelect("from XNGA_PATROL_REG where USER_ID='" + Constants.USER_ID + "'" + " and  REG_TIME >" + "'" + time + "'" + " order by REG_TIME desc", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "XNGA_PATROL_REG"), true, false));
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}
			}
			else
			{
				return -1;
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			// TODO Auto-generated method stub
			switch (result)
			{
				case -1:// 异步NullPointerException
					T.showSnack(mActivity, R.string.serverFailed);
					break;
				case 1:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (datasetlist != null)
						{
							resultList = parseData(datasetlist);
							Message m = new Message();
							handler.sendMessage(m);
						}
					}
					else
					{
						Toast.makeText(mActivity, "查询失败！", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	}

	public ArrayList<RegInfo> parseData(DataSetList datasetlist)
	{
		ArrayList<RegInfo> infoList = new ArrayList<RegInfo>();
		ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
		ArrayList<String> s = datasetlist.lastChangedDates;
		ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;

		RegInfo info = null;
		for (int i = 0; i < nameList.size(); i++)
		{

			if ("USER_ID".equals(nameList.get(i)))
			{
				if (info != null)
				{
					info = null;
				}
				info = new RegInfo();
			}
			else if ("QRCODE_INFO".equals(nameList.get(i)))
			{
				String qrCode = valueList.get(i);
				info.setQRcode(qrCode);
			}
			else if ("REG_ADD".equals(nameList.get(i)))
			{
				String add = valueList.get(i);
				info.setAddress(add);
			}
			else if ("REG_TIME".equals(nameList.get(i)))
			{
				String time = valueList.get(i);
				info.setLocalDate(time);
				Log.v("fcr", "info--" + info.toString());
				infoList.add(info);
			}
		}
		if (infoList.size() == s.size())
		{
			for (int j = 0; j < s.size(); j++)
			{
				if (infoList.size() > 0)
				{
					infoList.get(j).setRegDate(s.get(j));
				}
			}
		}
		return infoList;
	}

	private String time;

	public void getTime()
	{
		Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH);
		int day = ca.get(Calendar.DAY_OF_MONTH);
		ca.set(year, month, day);// 月份是从0开始的，所以11表示12月
		Date now = ca.getTime();
		ca.add(Calendar.DAY_OF_MONTH, -2); // 月份减1
		Date lastMonth = ca.getTime(); // 结果
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		time = sf.format(lastMonth) + " 00:00:00";
		System.out.println(sf.format(now) + " 00:00:00");
		System.out.println(time);
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_xlqdlb_back:
			{
				finish();
			}break;
			
			case R.id.btn_xlqdlb_signIn:
			{
				startActivityForResult(new Intent(this, ScanCodeActivity.class), 0);
			}break;
		}
	}
}
