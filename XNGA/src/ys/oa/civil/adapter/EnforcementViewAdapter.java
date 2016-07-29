package ys.oa.civil.adapter;

import java.util.ArrayList;

import ys.nlga.activity.R;
import ys.oa.enity.SectionEnity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author cuiru.fan
 *
 */
public class EnforcementViewAdapter extends BaseAdapter{
	private ArrayList<SectionEnity> list;
	private LayoutInflater layoutInflater;
	private Context context;
	public EnforcementViewAdapter(Context context,ArrayList<SectionEnity> list) {
		this.context = context;
		this.list = list;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.enforcement_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.itemTitle = (TextView)convertView.findViewById(R.id.enforcement_item_title);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.itemTitle.setText(list.get(position).getText().toString());
		
		return convertView;
	}
	
	public class ViewHolder {
		TextView itemTitle;
	}

}
