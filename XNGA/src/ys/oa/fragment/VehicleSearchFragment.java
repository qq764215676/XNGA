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

@Deprecated
public class VehicleSearchFragment extends Fragment implements OnClickListener
{
	private SearchInfoActivity parentActivity;
	
	private View rootView; // Fragment View
	private EditText etVehLpn, etVehEn, etVehVin, etVehColor; // 车身信息输入框
	private EditText etPplName, etPplSex, etPplBirt, etPplId, etPplAdd; // 车主信息输入框
	
	private ButtonFlat btnSearch; // 搜索按钮
	private OnSearchClick onSearchClick; // 搜索点击事件
	private DataSetList datasetlist; // 通讯字段

	public static VehicleSearchFragment newInstance()
	{
		VehicleSearchFragment fragment = new VehicleSearchFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.fragment_vehicle_search, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		parentActivity = (SearchInfoActivity) getActivity();
		initWidget();
		initListener();

	}

	private void initWidget()
	{
		// 初始化车身信息输入框
		etVehLpn = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_lpn);
		etVehEn = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_en);
		etVehVin = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_vin);
		etVehColor = (EditText) rootView.findViewById(R.id.et_vehicle_search_ven_color);

		// 初始化车主信息输入框
		etPplName = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_name);
		etPplSex = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_sex);
		etPplBirt = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_birt);
		etPplId = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_id);
		etPplAdd = (EditText) rootView.findViewById(R.id.et_vehicle_search_ppl_add);

		// 初始化搜索按钮
		btnSearch = (ButtonFlat) rootView.findViewById(R.id.btn_vehicle_search_search);
		btnSearch.setRippleSpeed(30);
		btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));
	}

	private void initListener()
	{
		btnSearch.setOnClickListener(this);
	}

	/**
	 * 检查所有输入框状态，全为空返回假
	 * 
	 * @return
	 */
	private boolean getAllEmpty()
	{
		return !TextUtils.isEmpty(etVehLpn.getText().toString()) || !TextUtils.isEmpty(etVehEn.getText().toString()) || !TextUtils.isEmpty(etVehVin.getText().toString()) || !TextUtils.isEmpty(etVehColor.getText().toString()) || !TextUtils.isEmpty(etPplName.getText().toString()) || !TextUtils.isEmpty(etPplSex.getText().toString()) || !TextUtils.isEmpty(etPplBirt.getText().toString()) || !TextUtils.isEmpty(etPplId.getText().toString()) || !TextUtils.isEmpty(etPplAdd.getText().toString());
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_vehicle_search_search:
				// 如果提交搜索有内容则提交数据
				if (getAllEmpty())
				{
					if (Util.isNetworkAvailable(parentActivity))
					{
						new AsyncLoader().execute("search");
					}
					else
					{
						T.showSnack(parentActivity, R.string.networkerror);
					}
				}
				// 如果提交搜索无内容则提示输入检索内容
				else
				{
					T.showSnack(parentActivity, "请输入至少一项");
				}
				break;
		}
	}

	class AsyncLoader extends AsyncTask<String, Integer, Integer>
	{
		@Override
		protected Integer doInBackground(String... params)
		{
			if ("search".equals(params[0]))
			{
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("VEH_LPN", etVehLpn.getText().toString());
				map.put("VEH_EN", etVehEn.getText().toString());
				map.put("VEH_VIN", etVehVin.getText().toString());
				map.put("VEH_COLOR", etVehColor.getText().toString());

				map.put("PPL_NAME", etPplName.getText().toString());
				map.put("PPL_SEX", etPplSex.getText().toString());
				map.put("PPL_BIRT", etPplBirt.getText().toString());
				map.put("PPL_ID", etPplId.getText().toString());
				map.put("PPL_ADD", etPplAdd.getText().toString());

				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForSaveOrUpdate(map, new DocInfor("", "XNGA_VEH_QUERY"), false));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 1;
			}
			return 0;

		}

		protected void onPostExecute(Integer result)
		{
			switch (result)
			{
				case -1:// 异步NullPointerException
					T.showSnack(parentActivity, R.string.serverFailed);
					break;
				case 1:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						String contentId = datasetlist.CONTENTID.get(0);
						System.out.println("CONTENTID " + contentId);

						//回调OnSearchClick.onClick方法
						Bundle args = new Bundle();
						args.putString("FLAG_ID", contentId);
						args.putString("SEARCH_TYPE", Constants.VEHICLE_RESEARCH);
						onSearchClick.onClick(btnSearch, args);
					}
					break;
			}
		}
	}

	// 车辆搜索点击接口
	public interface OnSearchClick
	{
		public void onClick(View view, Bundle args);
	}

	// 获取车辆搜索事件点击实例
	public OnSearchClick getOnButtonClick()
	{
		return onSearchClick;
	}

	// 车辆搜索点击方法
	public void setOnSearchClick(OnSearchClick onSearchClick)
	{
		this.onSearchClick = onSearchClick;
	}

}
