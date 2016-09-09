package ys.oa.adapter;

import java.util.ArrayList;

import ys.nlga.activity.R;
import ys.oa.provider.SQLDataEntity;
import ys.oa.util.Constants;
import ys.oa.util.Util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author wufan
 * @category 人员ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListCollectPeopleAdapter extends BaseAdapter
{
	private ArrayList<SQLDataEntity> entityList;
	private Context context;
	private String flag;
	private ListView listView;

	public ListCollectPeopleAdapter(Context context, ArrayList<SQLDataEntity> enityList, String flag, ListView listView)
	{
		this.entityList = enityList;
		this.context = context;
		this.flag = flag;
		this.listView = listView;
	}

	public long getItemId(int position)
	{
		return position;
	}

	public Object getItem(int position)
	{
		return entityList.get(position);
	}

	public int getCount()
	{
		if (entityList != null)
		{
			//if(listView!=null) listView.setDividerHeight(10);
			if(listView!=null) listView.setDividerHeight(0);
			return entityList.size();
		}
		else
		{
			if(listView!=null) listView.setDividerHeight(0);
			
			return 0;
		}
	}

	@Override
	public int getViewTypeCount()
	{
		// TODO Auto-generated method stub
		return 5;
	}

	private final static int HEAD_VIEW = 0;
	private final static int PPL_CKS_VIEW = 1;
	private final static int VEH_CKS_VIEW = 2;
	private final static int NVEH_CKS_VIEW = 3;
	private final static int GOODS_CKS_VIEW = 4;

	@Override
	public int getItemViewType(int position)
	{
		// TODO Auto-generated method stub
		if (entityList != null)
		{
			if (entityList.get(position).getCheckType().equals(Constants.PPL_CKS))
			{
				return PPL_CKS_VIEW;
			}
			else if (entityList.get(position).getCheckType().equals(Constants.VEH_CKS_CAR) || entityList.get(position).getCheckType().equals(Constants.VEH_CKS_MOTORCYCLE))
			{
				return VEH_CKS_VIEW;
			}
			else if (entityList.get(position).getCheckType().equals(Constants.NVEH_CKS_BICYCLE) || entityList.get(position).getCheckType().equals(Constants.NVEH_CKS_ELECTROMOBILE))
			{
				return NVEH_CKS_VIEW;
			}
			else if (entityList.get(position).getCheckType().equals(Constants.GOODS_CKS))
			{
				return GOODS_CKS_VIEW;
			}
			else
			{
				return HEAD_VIEW;
			}
		}
		return HEAD_VIEW;
	}

	public View getView(final int position, View v, ViewGroup parent)
	{
		PPLViewHolder pplHolder = null;
		VehViewHolder vehHolder = null;
		NvehViewHolder nVehHolder = null;
		GoodsViewHolder goodsHolder = null;
		int viewType = getItemViewType(position);
		LayoutInflater inflater = LayoutInflater.from(context);
		if (v == null)
		{
			Log.v("fcr", "list type--" + entityList.get(position).getCheckType() + "position---" + position);
			if (viewType == PPL_CKS_VIEW)
			{
				pplHolder = new PPLViewHolder();
				v = inflater.inflate(R.layout.collect_people_list_item, null);
				pplHolder.iv_img = (ImageView) v.findViewById(R.id.iv_people_head);
				pplHolder.tv_name = (TextView) v.findViewById(R.id.tv_name);
				pplHolder.tv_sex = (TextView) v.findViewById(R.id.tv_sex);
				pplHolder.tv_birthday = (TextView) v.findViewById(R.id.tv_birthday);
				pplHolder.tv_owner_ID = (TextView) v.findViewById(R.id.tv_id);
				pplHolder.tv_type = (TextView) v.findViewById(R.id.tv_check_type);
				pplHolder.p_type_layout = (LinearLayout) v.findViewById(R.id.people_type_layout);
				pplHolder.tv_p_type = (TextView) v.findViewById(R.id.tv_people_type);
				v.setTag(pplHolder);
			}
			else if (viewType == VEH_CKS_VIEW)
			{
				vehHolder = new VehViewHolder();
				v = inflater.inflate(R.layout.collect_car_list_item, null);
				vehHolder.iv_img = (ImageView) v.findViewById(R.id.iv_img);
				vehHolder.tv_name = (TextView) v.findViewById(R.id.tv_owner_name);
				vehHolder.tv_car_id = (TextView) v.findViewById(R.id.tv_car_id);
				vehHolder.tv_owner_ID = (TextView) v.findViewById(R.id.tv_owner_id);
				vehHolder.tv_type = (TextView) v.findViewById(R.id.tv_type);
				vehHolder.tv_time = (TextView) v.findViewById(R.id.tv_collect_time);
				vehHolder.tv_veh_record = (TextView) v.findViewById(R.id.tv_veh_record);
				vehHolder.tv_veh_record.setVisibility(View.GONE);
				v.setTag(vehHolder);
			}
			else if (viewType == NVEH_CKS_VIEW)
			{
				nVehHolder = new NvehViewHolder();
				v = inflater.inflate(R.layout.collect_car_list_item, null);
				nVehHolder.iv_img = (ImageView) v.findViewById(R.id.iv_img);
				nVehHolder.tv_name = (TextView) v.findViewById(R.id.tv_owner_name);
				nVehHolder.tv_car_id = (TextView) v.findViewById(R.id.tv_car_id);
				nVehHolder.tv_owner_ID = (TextView) v.findViewById(R.id.tv_owner_id);
				nVehHolder.tv_type = (TextView) v.findViewById(R.id.tv_type);
				nVehHolder.tv_time = (TextView) v.findViewById(R.id.tv_collect_time);
				nVehHolder.tv_veh_record = (TextView) v.findViewById(R.id.tv_veh_record);
				nVehHolder.veh_record_layout = (LinearLayout) v.findViewById(R.id.veh_record_layout);
				nVehHolder.veh_id_layout = (LinearLayout) v.findViewById(R.id.tv_veh_id_layout);
				v.setTag(nVehHolder);
			}
			else if (viewType == GOODS_CKS_VIEW)
			{
				goodsHolder = new GoodsViewHolder();
				v = inflater.inflate(R.layout.collect_good_list_item, null);
				goodsHolder.iv_img = (ImageView) v.findViewById(R.id.iv_img);
				goodsHolder.tv_name = (TextView) v.findViewById(R.id.tv_owner_name);
				goodsHolder.tv_goods_name = (TextView) v.findViewById(R.id.tv_good_name);
				goodsHolder.tv_owner_ID = (TextView) v.findViewById(R.id.tv_owner_id);
				goodsHolder.tv_type = (TextView) v.findViewById(R.id.tv_type);
				goodsHolder.tv_time = (TextView) v.findViewById(R.id.tv_collect_time);
				v.setTag(goodsHolder);
			}
			else
			{

			}
		}
		else
		{
			if (viewType == PPL_CKS_VIEW)
			{
				pplHolder = (PPLViewHolder) v.getTag();
			}
			else if (viewType == VEH_CKS_VIEW)
			{
				vehHolder = (VehViewHolder) v.getTag();
			}
			else if (viewType == NVEH_CKS_VIEW)
			{
				nVehHolder = (NvehViewHolder) v.getTag();
			}
			else if (viewType == GOODS_CKS_VIEW)
			{
				goodsHolder = (GoodsViewHolder) v.getTag();
			}
		}
		// flag 表示是查询结果list显示页面
		if (flag.equals("result"))
		{
			if (viewType == PPL_CKS_VIEW)
			{
				pplHolder.iv_img.setVisibility(View.GONE);
				String recordType = entityList.get(position).getpType().toString();
				if (recordType.equals("普通人员"))
				{
					// 2016/01/25 14:31 根据客户需求修改显示
					pplHolder.tv_p_type.setText("暂无违法记录");
					pplHolder.tv_p_type.setTextColor(0xFF6C6C6C);
				}
				else if (recordType.contains("在逃人员"))
				{
					pplHolder.tv_p_type.setText(recordType);
					pplHolder.tv_p_type.setTextColor(android.graphics.Color.RED);
				}
				else if (recordType.contains("违法人员"))
				{
					if (recordType.contains(","))
					{
						setSplitView(pplHolder.tv_p_type, recordType);
						//
					}
					else
					{
						pplHolder.tv_p_type.setText(recordType);
					}
					pplHolder.tv_p_type.setTextColor(0xFFFFCC33);
				}
				else
				{
					pplHolder.tv_p_type.setText(recordType);
				}
			}
			else if (viewType == VEH_CKS_VIEW)
			{
				vehHolder.iv_img.setVisibility(View.GONE);

			}
			else if (viewType == NVEH_CKS_VIEW)
			{
				String vehRecord = entityList.get(position).getVehRecord().toString();
				String carId = entityList.get(position).getCarId().toString();
				nVehHolder.iv_img.setVisibility(View.GONE);
				nVehHolder.tv_veh_record.setText(vehRecord);
				nVehHolder.tv_car_id.setText(carId);
			}
			else if (viewType == GOODS_CKS_VIEW)
			{
				goodsHolder.iv_img.setVisibility(View.GONE);
			}
		}
		else
		{
			if (viewType == PPL_CKS_VIEW)
			{
				pplHolder.p_type_layout.setVisibility(View.GONE);
				pplHolder.iv_img.setVisibility(View.VISIBLE);
				if (entityList.get(position).getIDImagePath() != null)
				{
					Util.displayLocalImg(context, pplHolder.iv_img, entityList.get(position).getIDImagePath(), null);
				}
			}
			else if (viewType == VEH_CKS_VIEW)
			{
				vehHolder.iv_img.setVisibility(View.VISIBLE);
				if (entityList.get(position).getIDImagePath() != null)
				{
					Util.displayLocalImg(context, vehHolder.iv_img, entityList.get(position).getIDImagePath(), null);
				}
			}
			else if (viewType == NVEH_CKS_VIEW)
			{
				nVehHolder.veh_record_layout.setVisibility(View.GONE);
				nVehHolder.iv_img.setVisibility(View.VISIBLE);
				if (entityList.get(position).getIDImagePath() != null)
				{
					Util.displayLocalImg(context, nVehHolder.iv_img, entityList.get(position).getIDImagePath(), null);
				}
			}
			else if (viewType == GOODS_CKS_VIEW)
			{
				goodsHolder.iv_img.setVisibility(View.VISIBLE);
				if (entityList.get(position).getIDImagePath() != null)
				{
					Util.displayLocalImg(context, goodsHolder.iv_img, entityList.get(position).getIDImagePath(), null);
				}
			}
		}

		if (viewType == PPL_CKS_VIEW)
		{
			pplHolder.tv_name.setText(entityList.get(position).getName());
			Log.v("fcr", "entityList.get(position).getID---" + entityList.get(position).getID());
			pplHolder.tv_owner_ID.setText(entityList.get(position).getID());
			pplHolder.tv_type.setText("人员检查");
			pplHolder.tv_sex.setText(entityList.get(position).getSex());
			pplHolder.tv_birthday.setText(entityList.get(position).getBirthday());
		}
		else if (viewType == VEH_CKS_VIEW)
		{
			vehHolder.tv_name.setText(entityList.get(position).getName());
			vehHolder.tv_owner_ID.setText(entityList.get(position).getID());
			vehHolder.tv_type.setText(entityList.get(position).getCheckType());
			vehHolder.tv_car_id.setText(entityList.get(position).getCarId());
			vehHolder.tv_time.setText(entityList.get(position).getCreateDate());
		}
		else if (viewType == NVEH_CKS_VIEW)
		{
			nVehHolder.tv_name.setText(entityList.get(position).getName());
			nVehHolder.tv_owner_ID.setText(entityList.get(position).getID());
			if (entityList.get(position).getCarId() == null || entityList.get(position).getCarId().equals(""))
			{
				nVehHolder.veh_id_layout.setVisibility(View.GONE);
			}
			else
			{
				nVehHolder.tv_car_id.setText(entityList.get(position).getCarId());
			}
			nVehHolder.tv_type.setText(entityList.get(position).getCheckType());
			nVehHolder.tv_time.setText(entityList.get(position).getCreateDate());
		}
		else if (viewType == GOODS_CKS_VIEW)
		{
			goodsHolder.tv_name.setText(entityList.get(position).getName());
			goodsHolder.tv_owner_ID.setText(entityList.get(position).getID());
			goodsHolder.tv_type.setText(entityList.get(position).getCheckType());
			goodsHolder.tv_goods_name.setText(entityList.get(position).getGoodsName());
			goodsHolder.tv_time.setText(entityList.get(position).getCreateDate());
		}
		return v;
	}

	class BaseViewHolder
	{
		public TextView tv_name;
		public ImageView iv_img;
		public TextView tv_owner_ID;
		public TextView tv_type;
	}

	class PPLViewHolder extends BaseViewHolder
	{
		public TextView tv_sex;
		public TextView tv_birthday;
		public TextView tv_p_type;
		public LinearLayout p_type_layout;
	}

	class VehViewHolder extends BaseViewHolder
	{
		public TextView tv_car_id;
		public TextView tv_time;
		public TextView tv_veh_record;
	}

	class NvehViewHolder extends BaseViewHolder
	{
		public TextView tv_car_id;
		public TextView tv_time;
		public TextView tv_veh_record;
		public LinearLayout veh_record_layout;
		public LinearLayout veh_id_layout;
	}

	class GoodsViewHolder extends BaseViewHolder
	{
		public TextView tv_goods_name;
		public TextView tv_time;
	}

	public void setSplitView(TextView tv, String text)
	{
		StringBuilder builder = new StringBuilder();
		;
		String[] splitString = text.split(",");
		for (int i = 0; i < splitString.length; i++)
		{
			builder.append(splitString[i] + "\n");
		}
		tv.setText(builder.toString());
	}
}
