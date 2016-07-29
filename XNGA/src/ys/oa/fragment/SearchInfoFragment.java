package ys.oa.fragment;

import java.util.HashMap;

import ys.nlga.activity.R;
import ys.oa.activity.SearchInfoActivity;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonFlat;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;

/**
 * 搜索页面Fragment；
 * 根据传入的搜索类型参数，展示对应的搜索页面；
 * 
 */
public class SearchInfoFragment extends Fragment implements OnClickListener
{
	private SearchInfoActivity parentActivity;
	
	private View rootView; //Fragment的根视图
	private WidgetList widgetList; //根视图中包含的一个子组件列表(该列表中包含了根视图中所有的子组件)
	
	private DataSetList resultData;
	private String searchType;
	
	public SearchInfoFragment(String searchType)
	{
		this.searchType = searchType;
	}
	
	private void initVar()
	{
		parentActivity = (SearchInfoActivity) getActivity();
	}
	
	private void initWidget()
	{
		//根据页面的不同，动态加载相应的子组件
		if (Constants.ONE_KEY_RESEARCH.equals(searchType))
		{
			widgetList = new WidgetList.OnekeyWidgetList();
			((WidgetList.OnekeyWidgetList)widgetList).etKeyword = (EditText) rootView.findViewById(R.id.et_onekey_search_keyword);
			((WidgetList.OnekeyWidgetList)widgetList).btnSearch = (ButtonFlat) rootView.findViewById(R.id.btn_onekey_search_search);
			
			((WidgetList.OnekeyWidgetList)widgetList).btnSearch.setRippleSpeed(30);
			((WidgetList.OnekeyWidgetList)widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));
		}
		else if(Constants.PERSON_RESEARCH.equals(searchType))
		{
			widgetList = new WidgetList.PeopleWidgetList();
			((WidgetList.PeopleWidgetList)widgetList).etPplName = (EditText) rootView.findViewById(R.id.et_people_search_ppl_name);
			((WidgetList.PeopleWidgetList)widgetList).etPplId = (EditText) rootView.findViewById(R.id.et_people_search_ppl_id);
			((WidgetList.PeopleWidgetList)widgetList).etPplSex = (EditText) rootView.findViewById(R.id.et_people_search_ppl_sex);
			((WidgetList.PeopleWidgetList)widgetList).etPplBirt = (EditText) rootView.findViewById(R.id.et_people_search_ppl_birt);
			((WidgetList.PeopleWidgetList)widgetList).etPplAdd = (EditText) rootView.findViewById(R.id.et_people_search_ppl_add);
			((WidgetList.PeopleWidgetList)widgetList).btnSearch = (ButtonFlat) rootView.findViewById(R.id.btn_person_search_search);
			
			((WidgetList.PeopleWidgetList)widgetList).btnSearch.setRippleSpeed(30);
			((WidgetList.PeopleWidgetList)widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));
		}
		else if(Constants.VEHICLE_RESEARCH.equals(searchType))
		{
			widgetList = new WidgetList.VehicleWidgetList();
			((WidgetList.VehicleWidgetList)widgetList).etVehLpn = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_lpn);
			((WidgetList.VehicleWidgetList)widgetList).etVehEn = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_en);
			((WidgetList.VehicleWidgetList)widgetList).etVehVin = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_vin);
			((WidgetList.VehicleWidgetList)widgetList).etVehColor = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_color);
			((WidgetList.VehicleWidgetList)widgetList).etPplName = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_name);
			((WidgetList.VehicleWidgetList)widgetList).etPplSex = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_sex);
			((WidgetList.VehicleWidgetList)widgetList).etPplBirt = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_birt);
			((WidgetList.VehicleWidgetList)widgetList).etPplId = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_id);
			((WidgetList.VehicleWidgetList)widgetList).etPplAdd = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_add);
			((WidgetList.VehicleWidgetList)widgetList).btnSearch = (ButtonFlat) rootView.findViewById(R.id.btn_vehicle_search_search);
			
			((WidgetList.VehicleWidgetList)widgetList).btnSearch.setRippleSpeed(30);
			((WidgetList.VehicleWidgetList)widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));
		}
		else if(Constants.GOODS_RESEARCH.equals(searchType))
		{
			widgetList = new WidgetList.GoodsWidgetList();
			((WidgetList.GoodsWidgetList)widgetList).etIoiName = (EditText) rootView.findViewById(R.id.et_goods_search_ioi_name);
			((WidgetList.GoodsWidgetList)widgetList).etIoiType = (EditText) rootView.findViewById(R.id.et_goods_search_ioi_type);
			((WidgetList.GoodsWidgetList)widgetList).etPplName = (EditText) rootView.findViewById(R.id.et_goods_search_ppl_name);
			((WidgetList.GoodsWidgetList)widgetList).etPplSex = (EditText) rootView.findViewById(R.id.et_goods_search_ppl_sex);
			((WidgetList.GoodsWidgetList)widgetList).etPplBirt = (EditText) rootView.findViewById(R.id.et_goods_search_ppl_birt);
			((WidgetList.GoodsWidgetList)widgetList).etPplId = (EditText) rootView.findViewById(R.id.et_goods_search_ppl_id);
			((WidgetList.GoodsWidgetList)widgetList).etPplAdd = (EditText) rootView.findViewById(R.id.et_goods_search_ppl_add);
			((WidgetList.GoodsWidgetList)widgetList).btnSearch = (ButtonFlat) rootView.findViewById(R.id.btn_goods_search_search);
			
			((WidgetList.GoodsWidgetList)widgetList).btnSearch.setRippleSpeed(30);
			((WidgetList.GoodsWidgetList)widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));
		}
		else if(Constants.CASE_RESEARCH.equals(searchType))
		{
			widgetList = new WidgetList.CaseWidgetList();
			((WidgetList.CaseWidgetList)widgetList).etCaseNumber = (EditText) rootView.findViewById(R.id.et_case_search_case_number);
			((WidgetList.CaseWidgetList)widgetList).etCaseName = (EditText) rootView.findViewById(R.id.et_case_search_case_name);
			((WidgetList.CaseWidgetList)widgetList).etCaseType = (EditText) rootView.findViewById(R.id.et_case_search_case_type);
			((WidgetList.CaseWidgetList)widgetList).etCaseUnit = (EditText) rootView.findViewById(R.id.et_case_search_case_unit);
			((WidgetList.CaseWidgetList)widgetList).etPplName = (EditText) rootView.findViewById(R.id.et_case_search_ppl_name);
			((WidgetList.CaseWidgetList)widgetList).etPoliceNumber = (EditText) rootView.findViewById(R.id.et_case_search_police_number);
			((WidgetList.CaseWidgetList)widgetList).etSuspectPplName = (EditText) rootView.findViewById(R.id.et_case_search_suspect_ppl_name);
			((WidgetList.CaseWidgetList)widgetList).etSuspectPplSex = (EditText) rootView.findViewById(R.id.et_case_search_suspect_ppl_sex);
			((WidgetList.CaseWidgetList)widgetList).etSuspectPplBirt = (EditText) rootView.findViewById(R.id.et_case_search_suspect_ppl_birt);
			((WidgetList.CaseWidgetList)widgetList).etSuspectPplId = (EditText) rootView.findViewById(R.id.et_case_search_suspect_ppl_id);
			((WidgetList.CaseWidgetList)widgetList).etSuspectPplAdd = (EditText) rootView.findViewById(R.id.et_case_search_suspect_ppl_add);
			((WidgetList.CaseWidgetList)widgetList).etVictimPplName = (EditText) rootView.findViewById(R.id.et_case_search_victim_ppl_name);
			((WidgetList.CaseWidgetList)widgetList).etVictimPplSex = (EditText) rootView.findViewById(R.id.et_case_search_victim_ppl_sex);
			((WidgetList.CaseWidgetList)widgetList).etVictimPplBirt = (EditText) rootView.findViewById(R.id.et_case_search_victim_ppl_birt);
			((WidgetList.CaseWidgetList)widgetList).etVictimPplId = (EditText) rootView.findViewById(R.id.et_case_search_victim_ppl_id);
			((WidgetList.CaseWidgetList)widgetList).etVictimPplAdd = (EditText) rootView.findViewById(R.id.et_case_search_victim_ppl_add);
			((WidgetList.CaseWidgetList)widgetList).btnSearch = (ButtonFlat) rootView.findViewById(R.id.btn_case_search_search);
			
			((WidgetList.CaseWidgetList)widgetList).btnSearch.setRippleSpeed(30);
			((WidgetList.CaseWidgetList)widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));
		}
	}
	
	private void initListener()
	{
		if (Constants.ONE_KEY_RESEARCH.equals(searchType))
		{
			((WidgetList.OnekeyWidgetList)widgetList).btnSearch.setOnClickListener(this);
		}
		else if(Constants.PERSON_RESEARCH.equals(searchType))
		{
			((WidgetList.PeopleWidgetList)widgetList).btnSearch.setOnClickListener(this);
		}
		else if(Constants.VEHICLE_RESEARCH.equals(searchType))
		{
			((WidgetList.VehicleWidgetList)widgetList).btnSearch.setOnClickListener(this);
		}
		else if(Constants.GOODS_RESEARCH.equals(searchType))
		{
			((WidgetList.GoodsWidgetList)widgetList).btnSearch.setOnClickListener(this);
		}
		else if(Constants.CASE_RESEARCH.equals(searchType))
		{
			((WidgetList.CaseWidgetList)widgetList).btnSearch.setOnClickListener(this);
		}
	}
	
	private boolean getAllEmpty()
	{
		if (Constants.ONE_KEY_RESEARCH.equals(searchType))
		{
			return !TextUtils.isEmpty(((WidgetList.OnekeyWidgetList)widgetList).etKeyword.getText().toString());
		}
		else if(Constants.PERSON_RESEARCH.equals(searchType))
		{
			return !TextUtils.isEmpty(((WidgetList.PeopleWidgetList)widgetList).etPplName.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList)widgetList).etPplSex.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList)widgetList).etPplBirt.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList)widgetList).etPplId.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList)widgetList).etPplAdd.getText().toString());
		}
		else if(Constants.VEHICLE_RESEARCH.equals(searchType))
		{
			return !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etVehLpn.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etVehEn.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etVehVin.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etVehColor.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etPplName.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etPplSex.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etPplBirt.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etPplId.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList)widgetList).etPplAdd.getText().toString());
		}
		else if(Constants.GOODS_RESEARCH.equals(searchType))
		{
			return !TextUtils.isEmpty(((WidgetList.GoodsWidgetList)widgetList).etIoiName.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList)widgetList).etIoiType.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList)widgetList).etPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList)widgetList).etPplSex.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList)widgetList).etPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList)widgetList).etPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList)widgetList).etPplAdd.getText().toString());
		}
		else if(Constants.CASE_RESEARCH.equals(searchType))
		{
			return !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etCaseNumber.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etCaseName.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etCaseType.getText().toString()) 
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etCaseUnit.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etPoliceNumber.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etSuspectPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etSuspectPplSex.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etSuspectPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etSuspectPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etSuspectPplAdd.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etVictimPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etVictimPplSex.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etVictimPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etVictimPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList)widgetList).etVictimPplAdd.getText().toString());
		}
		
		return false;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_onekey_search_search:
			case R.id.btn_person_search_search:
			case R.id.btn_vehicle_search_search:
			case R.id.btn_goods_search_search:
			case R.id.btn_case_search_search:
			{
				if (getAllEmpty()) //如果提交搜索有内容则提交数据
				{
					if (Util.isNetworkAvailable(parentActivity))
					{
						new AsyncLoader().execute();
					}
					else
					{
						T.showSnack(parentActivity, R.string.networkerror);
					}
				}
				else //如果提交搜索无内容则提示输入检索内容
				{
					T.showSnack(parentActivity, "请输入至少一项");
				}
			}break;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		initVar();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//根据搜索类别，加载相应的搜索页面以及初始化相应的子组件
		if (Constants.ONE_KEY_RESEARCH.equals(searchType))
		{
			rootView = inflater.inflate(R.layout.fragment_onekey_search, container, false);
		}
		else if(Constants.PERSON_RESEARCH.equals(searchType))
		{
			rootView = inflater.inflate(R.layout.fragment_people_search, container, false);
		}
		else if(Constants.VEHICLE_RESEARCH.equals(searchType))
		{
			rootView = inflater.inflate(R.layout.fragment_vehicle_search, container, false);
		}
		else if(Constants.GOODS_RESEARCH.equals(searchType))
		{
			rootView = inflater.inflate(R.layout.fragment_goods_search, container, false);
		}
		else if(Constants.CASE_RESEARCH.equals(searchType))
		{
			rootView = inflater.inflate(R.layout.fragment_case_search, container, false);
		}
		
		initWidget(); //根视图加载完毕后，初始化根视图中的组件
		initListener();
		
		return rootView;
	}
	
	private class AsyncLoader extends AsyncTask<String, Integer, Integer>
	{
		@Override
		protected Integer doInBackground(String... params)
		{
			String table = null;
			HashMap<String, String> map = new HashMap<String, String>();
			String xmlStr = null;
			
			if (Constants.ONE_KEY_RESEARCH.equals(searchType))
			{
				table = "XNGA_ONEKEY_QUERY ";
				map.put("QUERY_ONEKEY", ((WidgetList.OnekeyWidgetList)widgetList).etKeyword.getText().toString());
			}
			else if(Constants.PERSON_RESEARCH.equals(searchType))
			{
				table = "XNGA_PEOPLE_QUERY ";
				map.put("PPL_NAME", ((WidgetList.PeopleWidgetList)widgetList).etPplName.getText().toString());
				map.put("PPL_SEX", ((WidgetList.PeopleWidgetList)widgetList).etPplSex.getText().toString());
				map.put("PPL_BIRT", ((WidgetList.PeopleWidgetList)widgetList).etPplBirt.getText().toString());
				map.put("PPL_ID", ((WidgetList.PeopleWidgetList)widgetList).etPplId.getText().toString());
				map.put("PPL_ADD", ((WidgetList.PeopleWidgetList)widgetList).etPplAdd.getText().toString());
			}
			else if(Constants.VEHICLE_RESEARCH.equals(searchType))
			{
				table = "XNGA_VEH_QUERY ";
				map.put("VEH_LPN", ((WidgetList.VehicleWidgetList)widgetList).etVehLpn.getText().toString().toUpperCase());
				map.put("VEH_EN", ((WidgetList.VehicleWidgetList)widgetList).etVehEn.getText().toString().toUpperCase());
				map.put("VEH_VIN", ((WidgetList.VehicleWidgetList)widgetList).etVehVin.getText().toString().toUpperCase());
				map.put("VEH_COLOR", ((WidgetList.VehicleWidgetList)widgetList).etVehColor.getText().toString());
				map.put("PPL_NAME", ((WidgetList.VehicleWidgetList)widgetList).etPplName.getText().toString());
				map.put("PPL_SEX", ((WidgetList.VehicleWidgetList)widgetList).etPplSex.getText().toString());
				map.put("PPL_BIRT", ((WidgetList.VehicleWidgetList)widgetList).etPplBirt.getText().toString());
				map.put("PPL_ID", ((WidgetList.VehicleWidgetList)widgetList).etPplId.getText().toString());
				map.put("PPL_ADD", ((WidgetList.VehicleWidgetList)widgetList).etPplAdd.getText().toString());
			}
			else if(Constants.GOODS_RESEARCH.equals(searchType))
			{
				table = "XNGA_IOI_QUERY ";
				map.put("IOI_NAME", ((WidgetList.GoodsWidgetList)widgetList).etIoiName.getText().toString());
				map.put("IOI_TYPE", ((WidgetList.GoodsWidgetList)widgetList).etIoiType.getText().toString());
				map.put("PPL_NAME", ((WidgetList.GoodsWidgetList)widgetList).etPplName.getText().toString());
				map.put("PPL_SEX", ((WidgetList.GoodsWidgetList)widgetList).etPplSex.getText().toString());
				map.put("PPL_BIRT", ((WidgetList.GoodsWidgetList)widgetList).etPplBirt.getText().toString());
				map.put("PPL_ID", ((WidgetList.GoodsWidgetList)widgetList).etPplId.getText().toString());
				map.put("PPL_ADD", ((WidgetList.GoodsWidgetList)widgetList).etPplAdd.getText().toString());
			}
			else if(Constants.CASE_RESEARCH.equals(searchType))
			{
				table = "XNGA_CASE_QUERY";
				map.put("CASE_NUMBER", ((WidgetList.CaseWidgetList)widgetList).etCaseNumber.getText().toString());
				map.put("CASE_NAME", ((WidgetList.CaseWidgetList)widgetList).etCaseName.getText().toString());
				map.put("CASE_TYPE", ((WidgetList.CaseWidgetList)widgetList).etCaseType.getText().toString());
				map.put("CASE_UNIT", ((WidgetList.CaseWidgetList)widgetList).etCaseUnit.getText().toString());
				map.put("CASE_PPL_NAME", ((WidgetList.CaseWidgetList)widgetList).etPplName.getText().toString());
				map.put("CASE_POLICE_NUMBER", ((WidgetList.CaseWidgetList)widgetList).etPoliceNumber.getText().toString());
				map.put("SUSPECT_PPL_NAME", ((WidgetList.CaseWidgetList)widgetList).etSuspectPplName.getText().toString());
				map.put("SUSPECT_PPL_SEX", ((WidgetList.CaseWidgetList)widgetList).etSuspectPplSex.getText().toString());
				map.put("SUSPECT_PPL_BIRT", ((WidgetList.CaseWidgetList)widgetList).etSuspectPplBirt.getText().toString());
				map.put("SUSPECT_PPL_ID", ((WidgetList.CaseWidgetList)widgetList).etSuspectPplId.getText().toString());
				map.put("SUSPECT_PPL_ADD", ((WidgetList.CaseWidgetList)widgetList).etSuspectPplAdd.getText().toString());
				map.put("VICTIM_PPL_NAME", ((WidgetList.CaseWidgetList)widgetList).etVictimPplName.getText().toString());
				map.put("VICTIM_PPL_SEX", ((WidgetList.CaseWidgetList)widgetList).etVictimPplSex.getText().toString());
				map.put("VICTIM_PPL_BIRT", ((WidgetList.CaseWidgetList)widgetList).etVictimPplBirt.getText().toString());
				map.put("VICTIM_PPL_ID", ((WidgetList.CaseWidgetList)widgetList).etVictimPplId.getText().toString());
				map.put("VICTIM_PPL_ADD", ((WidgetList.CaseWidgetList)widgetList).etVictimPplAdd.getText().toString());
			}
			
			xmlStr = XmlPackage.packageForSaveOrUpdate(map, new DocInfor("", table), false);
			try
			{
				resultData = PostHttp.PostXML(xmlStr);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return -1;
			}

			return 0;
		}

		protected void onPostExecute(Integer result)
		{
			if(result == -1)
			{
				T.showSnack(parentActivity, R.string.serverFailed);
			}
			else
			{
				if (Constants.REQUESTSUCCESS.equals(resultData.SUCCESS))
				{
					String contentId = resultData.CONTENTID.get(0);
					System.out.println("CONTENTID " + contentId);
					
					ResultFragment resultFragment = new ResultFragment(contentId, searchType);
					parentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, resultFragment).addToBackStack(null).commit();
				}
			}
		}
	}
	
	/**
	 * 标记接口；表示实现该接口的类是一个WidgetList类
	 */
	private interface WidgetList
	{
		public class OnekeyWidgetList implements WidgetList //一键查询页面包含的所有组件
		{
			public EditText etKeyword;
			public ButtonFlat btnSearch;
		}
		
		public class PeopleWidgetList implements WidgetList //人员查询页面包含的所有组件
		{
			//人员信息输入框
			public EditText etPplName;
			public EditText etPplId;
			public EditText etPplSex;
			public EditText etPplBirt;
			public EditText etPplAdd;
			public ButtonFlat btnSearch;
		}
		
		public class VehicleWidgetList implements WidgetList //车辆查询页面包含的所有组件
		{
			//车身信息输入框
			public EditText etVehLpn;
			public EditText etVehEn;
			public EditText etVehVin;
			public EditText etVehColor;
			
			//车主信息输入框
			public EditText etPplName;
			public EditText etPplSex;
			public EditText etPplBirt;
			public EditText etPplId;
			public EditText etPplAdd;
			
			//搜索按钮
			public ButtonFlat btnSearch;
		}
		
		public class GoodsWidgetList implements WidgetList //物品查询页面包含的所有组件
		{
			//物品信息输入框
			public EditText etIoiName;
			public EditText etIoiType;
			
			//物品持有人信息输入框
			public EditText etPplName;
			public EditText etPplSex;
			public EditText etPplBirt;
			public EditText etPplId;
			public EditText etPplAdd;
			
			public ButtonFlat btnSearch;
		}
		
		public class CaseWidgetList implements WidgetList //案件查询页面包含的所有组件
		{
			//案件信息输入框
			public EditText etCaseNumber;
			public EditText etCaseName;
			public EditText etCaseType;
			public EditText etCaseUnit;
			public EditText etPplName;
			public EditText etPoliceNumber;
			
			//嫌疑人信息输入框
			public EditText etSuspectPplName;
			public EditText etSuspectPplSex;
			public EditText etSuspectPplBirt;
			public EditText etSuspectPplId;
			public EditText etSuspectPplAdd;
			
			//被害人/报警人信息输入框
			public EditText etVictimPplName;
			public EditText etVictimPplSex;
			public EditText etVictimPplBirt;
			public EditText etVictimPplId;
			public EditText etVictimPplAdd;
			
			public ButtonFlat btnSearch;
		}
	}
	
	
}
