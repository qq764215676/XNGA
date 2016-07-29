package ys.oa.adapter;

import java.util.ArrayList;

import ys.nlga.activity.R;
import ys.oa.enity.RegInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RegListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<RegInfo> infoList;
	public RegListAdapter (Context context,ArrayList<RegInfo> infoList) {
		this.context = context;
		this.infoList = infoList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return infoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		viewHolder holder;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(R.layout.reg_list_item, null);
			holder = new viewHolder();
			holder.tv_regDate = (TextView) v.findViewById(R.id.tv_reg_date);
			holder.tv_QRcode = (TextView) v.findViewById(R.id.tv_reg_qrcode);
			holder.tv_regAddress = (TextView) v.findViewById(R.id.tv_reg_address);
			holder.tv_LocalDate = (TextView) v.findViewById(R.id.tv_local_date);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		holder.tv_regDate.setText(infoList.get(position).getRegDate());
		holder.tv_QRcode.setText(infoList.get(position).getQRcode());
		holder.tv_regAddress.setText(infoList.get(position).getAddress());
		if(infoList.get(position).getLocalDate() != null) {
			holder.tv_LocalDate.setText(infoList.get(position).getLocalDate());
		}
		return v;
	}

	class viewHolder {
		public TextView tv_regDate;
		public TextView tv_QRcode;
		public TextView tv_regAddress;
		public TextView tv_LocalDate;
	}

}
