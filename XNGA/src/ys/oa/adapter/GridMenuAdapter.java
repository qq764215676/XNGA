package ys.oa.adapter;

import java.util.ArrayList;

import ys.oa.enity.MenusEnity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ys.nlga.activity.R;

public class GridMenuAdapter extends BaseAdapter {
	private ArrayList<MenusEnity> list;
	private Context context;

	public GridMenuAdapter(Context context, ArrayList<MenusEnity> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		viewHolder holder;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(R.layout.main_grid_item, null);
			holder = new viewHolder();
			holder.image = (ImageView) v.findViewById(R.id.iv_item_img);
			holder.text = (TextView) v.findViewById(R.id.iv_item_txt);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		// if (btns.get(position).get("menuImg").toString().equals("0")) {
		// holder.image.setVisibility(View.GONE);
		// } else {
		// holder.image.setVisibility(View.VISIBLE);
		// }
		holder.image.setBackgroundResource(list.get(position).getDrawableId());
		holder.text.setText(list.get(position).getMenuTitle());
		return v;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		} else {
			return 0;
		}
	}

	class viewHolder {
		public TextView text;
		public ImageView image;
	}
}