package ys.oa.civil.adapter;

import java.util.ArrayList;

import ys.oa.enity.ComplaintEnity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ys.nlga.activity.R;

/**
 * @author wufan
 * @category 新闻资讯ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListComplaintAdapter extends BaseAdapter {
	private ArrayList<ComplaintEnity> enityList;
	private Context context;

	public ListComplaintAdapter(Context context, ArrayList<ComplaintEnity> enityList) {
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
			v = inflater.inflate(R.layout.complaint_list_item, null);
			holder = new viewHolder();
			holder.tv_lost_name = (TextView) v.findViewById(R.id.tv_lost_name);
			holder.tv_lost_info = (TextView) v.findViewById(R.id.tv_lost_info);
			holder.tv_lost_phone = (TextView) v.findViewById(R.id.tv_lost_phone);
			holder.tv_lost_time = (TextView) v.findViewById(R.id.tv_lost_time);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		holder.tv_lost_name.setText(enityList.get(position).getComplaintTitle());
		holder.tv_lost_info.setText(enityList.get(position).getComplaintSort());
		holder.tv_lost_phone.setText(enityList.get(position).getComplaintStatus());
		holder.tv_lost_time.setText(enityList.get(position).getComplaintTime());
		return v;
	}

	class viewHolder {
		public TextView tv_lost_name;
		public TextView tv_lost_info;
		public TextView tv_lost_phone;
		public TextView tv_lost_time;
	}
}
