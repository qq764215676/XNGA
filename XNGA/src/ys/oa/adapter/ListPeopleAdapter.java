package ys.oa.adapter;

import java.util.ArrayList;

import ys.oa.enity.PeopleEnity;
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
public class ListPeopleAdapter extends BaseAdapter {
	private ArrayList<PeopleEnity> enityList;
	private Context context;

	public ListPeopleAdapter(Context context, ArrayList<PeopleEnity> enityList) {
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
			v = inflater.inflate(R.layout.people_list_item, null);
			holder = new viewHolder();
			holder.iv_people_head = (ImageView) v.findViewById(R.id.iv_people_head);
			holder.tv_name = (TextView) v.findViewById(R.id.tv_name);
			holder.tv_sex = (TextView) v.findViewById(R.id.tv_sex);
			holder.tv_birthday = (TextView) v.findViewById(R.id.tv_birthday);
			holder.tv_id = (TextView) v.findViewById(R.id.tv_id);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		if (enityList.get(position).getDocumentId().length() > 6) {
//			BitmapUtils bitmapUtils = new BitmapUtils(context);
//			bitmapUtils.display(holder.iv_people_head, Util.getImgUrl(enityList.get(position).getDocumentId(), "png"));
			Util.displayImg(context, holder.iv_people_head, enityList.get(position).getDocumentId(), "png");
		}
		holder.tv_name.setText(enityList.get(position).getName());
		holder.tv_sex.setText(enityList.get(position).getSex());
		holder.tv_birthday.setText(enityList.get(position).getBirthday());
		holder.tv_id.setText(enityList.get(position).getId());
		return v;
	}

	class viewHolder {
		public TextView tv_name;
		public TextView tv_sex;
		public TextView tv_birthday;
		public TextView tv_id;
		public ImageView iv_people_head;
	}
}
