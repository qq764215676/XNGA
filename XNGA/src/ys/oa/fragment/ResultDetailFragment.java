package ys.oa.fragment;

import ys.nlga.activity.R;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.CollectPeopleEnity;
import ys.oa.enity.QueryVehResultEntity;
import ys.oa.util.Constants;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResultDetailFragment extends Fragment
{
	private View rootView;
	private String searchType;
	private Object resultData;
	
	public ResultDetailFragment(String searchType, Object resultData)
	{
		this.searchType = searchType;
		this.resultData = resultData;
	}
	
	private void initVar()
	{
		
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
		//根据数据类型的不同加载相应的视图
		if (Constants.ONE_KEY_RESEARCH.equals(searchType))
		{
			
		}
		else if (Constants.PERSON_RESEARCH.equals(searchType))
		{
			PersonViewHolder pvh;
			if(rootView == null)
			{
				rootView = inflater.inflate(R.layout.fragment_people_result_detail, container, false);
				pvh = new PersonViewHolder();
				pvh.name = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_name);
				pvh.sex = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_sex);
				pvh.nation = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_nation);
				pvh.birt = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_birt);
				pvh.address = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_address);
				pvh.id = (TextView) rootView.findViewById(R.id.tv_people_result_detail_ppl_id);
				pvh.info = (TextView) rootView.findViewById(R.id.tv_people_result_detial_info);
				rootView.setTag(pvh);
			}
			else
			{
				pvh = (PersonViewHolder) rootView.getTag();
			}
			
			pvh.name.setText(((CollectPeopleEnity) resultData).getName());
			pvh.sex.setText(((CollectPeopleEnity) resultData).getSex());
			pvh.nation.setText(((CollectPeopleEnity) resultData).getNation());
			pvh.birt.setText(((CollectPeopleEnity) resultData).getBirthday());
			pvh.address.setText(((CollectPeopleEnity) resultData).getAddress());
			pvh.id.setText(((CollectPeopleEnity) resultData).getId());
			pvh.info.setText(((CollectPeopleEnity) resultData).getInfo());
		}
		else if (Constants.VEHICLE_RESEARCH.equals(searchType))
		{
			VehicleViewHolder vvh;
			if(rootView == null)
			{
				rootView = inflater.inflate(R.layout.fragment_vehicle_result_detail, container, false);
				vvh = new VehicleViewHolder();
				vvh.ownerName = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_owner_name);
				vvh.ownerId = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_owner_id);
				vvh.carId = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_car_id);
				vvh.vehEngine = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_engine);
				vvh.vehVIN = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_vin);
				vvh.vehColor = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_color);
				vvh.typeName = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_typename);
				vvh.time = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_time);
				vvh.address = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_address);
				vvh.info = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_info);
				rootView.setTag(vvh);
			}
			else
			{
				vvh = (VehicleViewHolder) rootView.getTag();
			}
			
			vvh.ownerName.setText(((CollectCarEnity) resultData).getOwnerName());
			vvh.ownerId.setText(((CollectCarEnity) resultData).getOwnerId());
			vvh.carId.setText(((CollectCarEnity) resultData).getCarId());
			vvh.vehEngine.setText(((QueryVehResultEntity) resultData).getVehEngine());
			vvh.vehVIN.setText(((QueryVehResultEntity) resultData).getVehVIN());
			vvh.vehColor.setText(((QueryVehResultEntity) resultData).getVehColor());
			vvh.typeName.setText(((QueryVehResultEntity) resultData).getTypeName());
			vvh.time.setText(((CollectCarEnity) resultData).getTime());
			vvh.address.setText(((CollectCarEnity) resultData).getAddress());
			vvh.info.setText(((CollectCarEnity) resultData).getInfo());
		}
		else if(Constants.GOODS_RESEARCH.equals(searchType))
		{
			
		}
		else if(Constants.CASE_RESEARCH.equals(searchType))
		{
			
		}
		
		return rootView;
	}
	
	private class PersonViewHolder //人员信息ViewHolder
	{
		public TextView name; //姓名
		public TextView sex; //性别
		public TextView nation; //民族
		public TextView birt; //出生日期
		public TextView address; //居住地
		public TextView id; //身份证
		public TextView info; //备注信息
	}
	
	private class VehicleViewHolder //车辆信息ViewHolder
	{
		public TextView ownerName; //车主姓名
		public TextView ownerId; //车主身份证
		public TextView carId; //车牌号
		public TextView vehEngine; //发动机号
		public TextView vehVIN; //车架号
		public TextView vehColor; //车身颜色
		public TextView typeName; //车辆类型名称
		public TextView time;
		public TextView address;
		public TextView info; //备注信息
	}
}
