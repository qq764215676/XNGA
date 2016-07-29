package ys.oa.fragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ys.nlga.activity.R;
import ys.oa.activity.SearchInfoActivity;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.CollectPeopleEnity;
import ys.oa.enity.QueryPeopleResultEntity;
import ys.oa.enity.QueryVehResultEntity;
import ys.oa.util.Constants;
import ys.oa.util.T;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anim.dialog.DialogLoading;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;

/**
 * 
 * 查询结果页面；
 * 根据上一个页面传入的flagId参数进行数据的查询，并将查询到的结果显示到视图列表中；
 * 
 */
public class ResultFragment extends Fragment
{
	private SearchInfoActivity parentActivity;
	private LayoutInflater layoutInflater;

	private View rootView; // Fragment的总视图
	private AlertDialog loadingDialog; // 提示对话框
	private Timer updateDataTimer; // 用于更新数据刷新UI的Timer
	private TimerTask updateDataTask; // 用于更新数据刷新UI的TimerTask
	private DataSetList resultData; // 查询的结果集
	private ListView resultList; // Fragment中的ListView
	private ResultListAdapter resultListAdapter; // Fragment中的ListView使用的adapter

	private long searchTime; // Timer已执行的时间
	private String flagId; //当前页面用以查询数据的关键值
	private String searchType; //要查询数据的类型(人员/车辆/案件/物品...)

	private static final long SEARCH_PERIOD = 3; // Timer的执行周期
	private static final long MAX_SEARCH_TIME = 90; // Timer的最大可执行时间，超时后默认为任务失败

	public ResultFragment(String flagId, String searchType)
	{
		this.flagId = flagId;
		this.searchType = searchType;
	}

	private void initVar()
	{
		parentActivity = (SearchInfoActivity) getActivity();
		
		resultListAdapter = new ResultListAdapter();
		updateDataTimer = new Timer();
		searchTime = 0;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initVar();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if (updateDataTask != null) updateDataTask.cancel(); // 销毁Fragment时，结束Task
		if (loadingDialog != null) loadingDialog.cancel();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		layoutInflater = inflater;

		rootView = inflater.inflate(R.layout.fragment_search_result, container, false);

		resultList = (ListView) rootView.findViewById(R.id.list_vehicle_result);
		resultList.setAdapter(resultListAdapter);
		resultList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()
		{
			// 若点击了结果列表项，则进入该项的详细信息页面Fragment
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				//若当前用户的权限值为99，则不能查看详情页面
				if(AppData.getInstance().getSpUtil().getAuth().equals("99"))
				{
					T.show(parentActivity, "你没有权限查看详情", 0);
					return;
				}
				
				ResultDetailFragment resultDetailFragment = new ResultDetailFragment(searchType, resultListAdapter.resultListData.get(position));
				parentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, resultDetailFragment).addToBackStack(null).commit();
			}
		});

		// 如果任务(更新数据刷新UI)还未开始执行，则执行任务(仅在所有View加载完毕后才执行，整个生命周期中只执行一次，获取到数据刷新UI后该Task就完成了它的使命，无需重复执行)
		if (updateDataTask == null)
		{
			updateDataTask = new UpdateDataTask();
			updateDataTimer.schedule(updateDataTask, 0, SEARCH_PERIOD * 1000);
			loadingDialog = DialogLoading.getProgressDialog(parentActivity, "查询中"); // 显示提示对话框
		}

		return rootView;
	}

	/**
	 * 用于为Fragment中的ListView填充数据的Adapter
	 */
	private class ResultListAdapter extends BaseAdapter
	{
		public ArrayList<Object> resultListData; // 当前Adapter使用的数据源

		public ResultListAdapter()
		{
			resultListData = new ArrayList<Object>(); // 初始化数据源
		}
		
		@Override
		public void notifyDataSetChanged()
		{
			super.notifyDataSetChanged();
			
			//更新adapter数据源时做一些额外操作
			if(Constants.PERSON_RESEARCH.equals(searchType)) //如果数据源是人员类型，则判断人员数据中，是否存在"在逃人员"，若存在，则给出1次提示
			{
				for (int i = 0; i < resultListData.size(); i++)
				{
					Object people = resultListData.get(i);
					String record = ((QueryPeopleResultEntity) people).getRecord();
					if(record.equals("在逃人员"))
					{
						AlertDialog.Builder dialog = new AlertDialog.Builder(parentActivity);
						dialog.setCancelable(false);
						dialog.setTitle("提示");
						dialog.setMessage("发现在逃人员："+((CollectPeopleEnity) people).getName()+"\n"+((CollectPeopleEnity) people).getId());
						dialog.setNegativeButton("确定", null);
						dialog.show();
						
						/*Toast toast = Toast.makeText(parentActivity, "发现在逃人员！", Toast.LENGTH_SHORT);
						toast.show();*/
						
						break;
					}
				}
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (Constants.ONE_KEY_RESEARCH.equals(searchType))
			{
				
			}
			else if(Constants.PERSON_RESEARCH.equals(searchType))
			{
				PersonViewHolder pvh;
				if (convertView == null)
				{
					convertView = layoutInflater.inflate(R.layout.listitem_people_result, parent, false);
					pvh = new PersonViewHolder();
					pvh.name = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_name);
					pvh.sex = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_sex);
					pvh.id = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_id);
					pvh.address = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_address);
					pvh.record = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_record);
					convertView.setTag(pvh);
				}
				else
				{
					pvh = (PersonViewHolder) convertView.getTag();
				}
				Object o = resultListData.get(position);
				((CollectPeopleEnity) o).getName();
				pvh.name.setText(((CollectPeopleEnity) resultListData.get(position)).getName());
				pvh.sex.setText(((CollectPeopleEnity) resultListData.get(position)).getSex());
				pvh.id.setText(((CollectPeopleEnity) resultListData.get(position)).getId());
				pvh.address.setText(((CollectPeopleEnity) resultListData.get(position)).getAddress());
				pvh.record.setText(((QueryPeopleResultEntity) resultListData.get(position)).getRecord());
				
				//如果非普通人员，则将身份显示为警告红色
				if( !pvh.record.getText().toString().equals("普通人员")) pvh.record.setTextColor(Color.RED);
				else pvh.record.setTextColor(Color.BLACK);
			}
			else if(Constants.VEHICLE_RESEARCH.equals(searchType))
			{
				VehicleViewHolder vvh;
				if (convertView == null)
				{
					convertView = layoutInflater.inflate(R.layout.listitem_vehicle_result, parent, false);
					vvh = new VehicleViewHolder();
					vvh.carId = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_car_id);
					vvh.ownerName = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_owner_name);
					vvh.ownerId = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_owner_id);
					vvh.vehRecord = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_veh_record);
					convertView.setTag(vvh);
				}
				else
				{
					vvh = (VehicleViewHolder) convertView.getTag();
				}
				
				vvh.carId.setText(((CollectCarEnity) resultListData.get(position)).getCarId());
				vvh.ownerName.setText(((CollectCarEnity) resultListData.get(position)).getOwnerName());
				vvh.ownerId.setText(((CollectCarEnity) resultListData.get(position)).getOwnerId());
				vvh.vehRecord.setText(((QueryVehResultEntity) resultListData.get(position)).getVehRecord());
			}
			else if(Constants.GOODS_RESEARCH.equals(searchType))
			{
				
			}
			else if(Constants.CASE_RESEARCH.equals(searchType))
			{
				
			}

			return convertView;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public Object getItem(int position)
		{
			return resultListData.get(position);
		}

		@Override
		public int getCount()
		{
			return resultListData.size();
		}
		
		private class PersonViewHolder
		{
			public TextView name;
			public TextView sex;
			public TextView id;
			public TextView address;
			public TextView record;
		}
		
		private class VehicleViewHolder
		{
			public TextView carId;
			public TextView ownerName;
			public TextView ownerId;
			public TextView vehRecord;
		}
	};

	/**
	 * 该Task用于为Fragment中的ListView的Adapter使用的数据源获取数据
	 */
	private class UpdateDataTask extends TimerTask
	{
		@Override
		public void run()
		{
			searchTime += SEARCH_PERIOD;
			new AsyncQuery().execute(); // 执行AsyncTask 异步查询数据
		}

		private class AsyncQuery extends AsyncTask<String, Integer, Integer>
		{
			@Override
			protected void onPreExecute()
			{
				
			}

			protected Integer doInBackground(String... params)
			{
				try
				{
					String contentId="";
					String table="";
					String xmlStr="";
					
					//拼接xml请求字符串
					contentId = flagId;
					//contentId = "A0102320165101132489526853";
					if (Constants.PERSON_RESEARCH.equals(searchType))
					{
						table = "XNGA_PPL_RESULT";
					}
					else if (Constants.VEHICLE_RESEARCH.equals(searchType))
					{
						table = "XNGA_VEH_RESULT";
					}
					else if (Constants.ONE_KEY_RESEARCH.equals(searchType))
					{
						table = "XNGA_ONEKEY_QUERY";
					}
					else if (Constants.CASE_RESEARCH.equals(searchType))
					{
						table = "XNGA_CASE_QUERY";
					}
					else if (Constants.GOODS_RESEARCH.equals(searchType))
					{
						table = "XNGA_IOI_QUERY";
					}
					xmlStr = XmlPackage.packageSelect("from "+ table+" where FLAG_ID='" + contentId + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", table), true, false);
					
					resultData = PostHttp.PostXML(xmlStr);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return -1;
				}
				
				return -1;
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				System.out.println("Task正在执行");
				
				if (Constants.REQUESTSUCCESS.equals(resultData.SUCCESS))
				{
					resultListAdapter.resultListData = parseVehResultData(resultData, searchType);

					if (resultListAdapter.resultListData.size() != 0) // 若搜索到数据了
					{
						resultListAdapter.notifyDataSetChanged();
						updateDataTask.cancel();
						loadingDialog.cancel();
					}
					else if (searchTime > MAX_SEARCH_TIME) // 若搜索超时了
					{
						updateDataTask.cancel();
						loadingDialog.cancel();
						searchTime = 0;
						Toast.makeText(parentActivity, "没有搜索到相应结果！", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					updateDataTask.cancel();
					loadingDialog.cancel();
					searchTime = 0;
					Toast.makeText(parentActivity, "请求有误，服务器返回结果失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}

		/**
		 * 根据搜索类型(数据类型)去解析resultData，返回对应的数据类型集合
		 * @param resultData
		 * @param searchType
		 * @return
		 */
		private ArrayList<Object> parseVehResultData(DataSetList resultData, String searchType)
		{
			ArrayList<Object> resultListData = new ArrayList<Object>();
			ArrayList<String> nameList = (ArrayList<String>) resultData.nameList;
			ArrayList<String> valueList = (ArrayList<String>) resultData.valueList;

			if (Constants.ONE_KEY_RESEARCH.equals(searchType))
			{
				
			}
			else if (Constants.PERSON_RESEARCH.equals(searchType))
			{
				QueryPeopleResultEntity pplResultData = null;
				for (int i = 0; i < nameList.size(); i++)
				{
					if ("XNGA_PPL_RESULT".equals(nameList.get(i)))
					{
						if (pplResultData != null)
						{
							pplResultData = null;
						}
						pplResultData = new QueryPeopleResultEntity();
						pplResultData.setCheckType(Constants.PPL_CKS);
					}

					if ("FLAG_ID".equals(nameList.get(i)))
					{
						pplResultData.setContentId(valueList.get(i));
					}
					else if ("PPL_NAME".equals(nameList.get(i)))
					{
						String name = valueList.get(i);
						pplResultData.setName(name);
					}

					else if ("PPL_SEX".equals(nameList.get(i)))
					{
						String sex = valueList.get(i);
						pplResultData.setSex(sex);
					}
					else if ("PPL_BIRT".equals(nameList.get(i)))
					{
						String birt = valueList.get(i);
						pplResultData.setBirthday(birt);
					}
					else if ("PPL_ID".equals(nameList.get(i)))
					{
						String id = valueList.get(i);
						pplResultData.setId(id);
					}
					else if ("PPL_NATION".equals(nameList.get(i)))
					{
						String nation = valueList.get(i);
						pplResultData.setNation(nation);
					}
					else if ("PPL_ADD".equals(nameList.get(i)))
					{
						String address = valueList.get(i);
						pplResultData.setAddress(address);
					}
					else if ("PPL_TYPE".equals(nameList.get(i)))
					{
						String pType = valueList.get(i);
						pplResultData.setpType(pType);
					}
					else if ("PPL_RECORD".equals(nameList.get(i)))
					{
						String record = valueList.get(i);
						pplResultData.setRecord(record);
					}
					else if ("PPL_RECORD_CT".equals(nameList.get(i)))
					{
						String recordType = valueList.get(i);
						pplResultData.setRecordType(recordType);
					}
					else if ("REM".equals(nameList.get(i)))
					{
						String rem = valueList.get(i);
						pplResultData.setInfo(rem);
						resultListData.add(pplResultData);
					}
				}
			}
			else if (Constants.VEHICLE_RESEARCH.equals(searchType))
			{
				QueryVehResultEntity vehResultData = null;
				for (int i = 0; i < nameList.size(); i++)
				{
					if ("XNGA_VEH_RESULT".equals(nameList.get(i)))
					{
						if (vehResultData != null)
						{
							vehResultData = null;
						}
						vehResultData = new QueryVehResultEntity();
						if ("0".equals(Constants.vehicleType))
						{
							vehResultData.setCheckType(Constants.NVEH_CKS_ELECTROMOBILE);
						}
						else if ("1".equals(Constants.vehicleType))
						{
							vehResultData.setCheckType(Constants.NVEH_CKS_BICYCLE);
						}
						else if ("2".equals(Constants.vehicleType))
						{
							vehResultData.setCheckType(Constants.VEH_CKS_CAR);
						}
						else if ("3".equals(Constants.vehicleType))
						{
							vehResultData.setCheckType(Constants.VEH_CKS_MOTORCYCLE);
						}
					}

					if ("FLAG_ID".equals(nameList.get(i)))
					{
						vehResultData.setContentId(valueList.get(i));
					}
					else if ("VEH_LPN".equals(nameList.get(i)))
					{
						String vehID = valueList.get(i);
						vehResultData.setCarId(vehID);
					}
					else if ("VEH_TYPE".equals(nameList.get(i)))
					{
						String vehType = valueList.get(i);
						vehResultData.setVehType(vehType);
					}
					else if ("VEH_TN".equals(nameList.get(i)))
					{
						String vehName = valueList.get(i);
						vehResultData.setTypeName(vehName);
					}
					else if ("VEH_COLOR".equals(nameList.get(i)))
					{
						String vehColor = valueList.get(i);
						vehResultData.setVehColor(vehColor);
					}
					else if ("VEH_VIN".equals(nameList.get(i)))
					{
						String vehVIN = valueList.get(i);
						vehResultData.setVehVIN(vehVIN);
					}
					else if ("VEH_EN".equals(nameList.get(i)))
					{
						String engine = valueList.get(i);
						vehResultData.setVehEngine(engine);
					}
					else if ("VEH_RECORD".equals(nameList.get(i)))
					{
						String record = valueList.get(i);
						vehResultData.setVehRecord(record);
					}
					else if ("PPL_NAME".equals(nameList.get(i)))
					{
						String ownerName = valueList.get(i);
						vehResultData.setOwnerName(ownerName);
					}
					else if ("PPL_ID".equals(nameList.get(i)))
					{
						String ownerId = valueList.get(i);
						vehResultData.setOwnerId(ownerId);
					}
					else if ("REM".equals(nameList.get(i)))
					{
						String rem = valueList.get(i);
						vehResultData.setInfo(rem);
						resultListData.add(vehResultData);
					}
				}
			}
			else if(Constants.GOODS_RESEARCH.equals(searchType))
			{
				
			}
			else if(Constants.CASE_RESEARCH.equals(searchType))
			{
				
			}

			return resultListData;
		}
	}
}
