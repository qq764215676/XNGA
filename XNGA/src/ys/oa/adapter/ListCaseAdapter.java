package ys.oa.adapter;

import java.util.ArrayList;

import ys.oa.enity.CaseEnity;
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
 * @category 案件ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListCaseAdapter extends BaseAdapter {
	private ArrayList<CaseEnity> enityList;
	private Context context;

	public ListCaseAdapter(Context context, ArrayList<CaseEnity> enityList) {
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
			v = inflater.inflate(R.layout.case_list_item, null);
			holder = new viewHolder();
			holder.tv_case_title = (TextView) v.findViewById(R.id.tv_case_title);
			holder.tv_case_info = (TextView) v.findViewById(R.id.tv_case_info);
			holder.tv_case_address = (TextView) v.findViewById(R.id.tv_case_address);
			holder.tv_case_time = (TextView) v.findViewById(R.id.tv_case_time);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		holder.tv_case_title.setText(enityList.get(position).getCaseTitle());
		holder.tv_case_info.setText(enityList.get(position).getCaseInfo());
		holder.tv_case_address.setText(enityList.get(position).getCaseAddress());
		holder.tv_case_time.setText(enityList.get(position).getCaseTime());
		return v;
	}

	class viewHolder {
		public TextView tv_case_title;
		public TextView tv_case_info;
		public TextView tv_case_address;
		public TextView tv_case_time;
	}
}
