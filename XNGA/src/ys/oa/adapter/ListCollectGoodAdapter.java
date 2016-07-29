package ys.oa.adapter;

import java.util.ArrayList;

import ys.oa.enity.CollectGoodEnity;
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
 * @category 人员ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListCollectGoodAdapter extends BaseAdapter {
	private ArrayList<CollectGoodEnity> enityList;
	private Context context;

	public ListCollectGoodAdapter(Context context, ArrayList<CollectGoodEnity> enityList) {
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
			v = inflater.inflate(R.layout.collect_good_list_item, null);
			holder = new viewHolder();
			holder.iv_img = (ImageView) v.findViewById(R.id.iv_img);
			holder.tv_owner_name = (TextView) v.findViewById(R.id.tv_owner_name);
			holder.tv_owner_id = (TextView) v.findViewById(R.id.tv_owner_id);
			holder.tv_collect_time = (TextView) v.findViewById(R.id.tv_collect_time);
			holder.tv_good_name = (TextView) v.findViewById(R.id.tv_good_name);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		if (enityList.get(position).getDocumentId().length() > 6) {
			Util.displayImg(context, holder.iv_img, enityList.get(position).getDocumentId(), "png");
		}
		holder.tv_good_name.setText(enityList.get(position).getGoodName());
		holder.tv_owner_name.setText(enityList.get(position).getOwnerName());
		holder.tv_owner_id.setText(enityList.get(position).getId());
		holder.tv_collect_time.setText(enityList.get(position).getTime());
		return v;
	}

	class viewHolder {
		public TextView tv_good_name;
		public TextView tv_owner_name;
		public TextView tv_owner_id;
		public TextView tv_collect_time;
		public ImageView iv_img;
	}
}
