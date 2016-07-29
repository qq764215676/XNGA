package ys.oa.adapter;

import java.util.ArrayList;

import ys.oa.enity.CollectCarEnity;
import ys.oa.util.Util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ys.nlga.activity.R;

/**
 * @author wufan
 * @category 车辆ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListCollectCarAdapter extends BaseAdapter {
	private ArrayList<CollectCarEnity> enityList;
	private Context context;

	public ListCollectCarAdapter(Context context, ArrayList<CollectCarEnity> enityList) {
		this.enityList = enityList;
		this.context = context;
	}

	public long getItemId(int position) {
		return position;
	}

	public Object getItem(int position) {
		return enityList.get(position);
	}

	public int getCount() {
		if (enityList != null) {
			return enityList.size();
		} else {
			return 0;
		}
	}

	public View getView(final int position, View v, ViewGroup parent) {
		viewHolder holder;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(R.layout.collect_car_list_item, null);
			holder = new viewHolder();
			holder.iv_img = (ImageView) v.findViewById(R.id.iv_img);
			holder.tv_owner_name = (TextView) v.findViewById(R.id.tv_owner_name);
			holder.tv_owner_id = (TextView) v.findViewById(R.id.tv_owner_id);
			holder.tv_collect_time = (TextView) v.findViewById(R.id.tv_collect_time);
			holder.tv_car_id = (TextView) v.findViewById(R.id.tv_car_id);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		if (enityList.get(position).getVehType().length() > 6) {
			Util.displayImg(context, holder.iv_img, enityList.get(position).getVehType(), "png");
		}
		holder.tv_owner_name.setText(enityList.get(position).getOwnerName());
		holder.tv_owner_id.setText(enityList.get(position).getOwnerId());
		holder.tv_car_id.setText(enityList.get(position).getCarId());
		holder.tv_collect_time.setText(enityList.get(position).getTime());
		return v;
	}

	class viewHolder {
		public TextView tv_car_id;
		public TextView tv_owner_name;
		public TextView tv_owner_id;
		public TextView tv_collect_time;
		public ImageView iv_img;
	}
}
