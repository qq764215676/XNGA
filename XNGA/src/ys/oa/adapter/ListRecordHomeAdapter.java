package ys.oa.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ys.nlga.activity.R;
import com.xutils.entities.RecordHomeEnity;

/**
 * @author wufan
 * @category 记录表ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListRecordHomeAdapter extends BaseAdapter {
	private List<RecordHomeEnity> list;
	private Context context;

	public ListRecordHomeAdapter(Context context, List<RecordHomeEnity> list) {
		this.list = list;
		this.context = context;
	}

	public long getItemId(int position) {
		return position;
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public int getCount() {
		if (list != null) {
			return list.size();
		} else {
			return 0;
		}
	}

	public View getView(final int position, View v, ViewGroup parent) {
		viewHolder holder;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(R.layout.record_list_item, null);
			holder = new viewHolder();
			holder.tv_title = (TextView) v.findViewById(R.id.tv_title);
			holder.tv_content = (TextView) v.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) v.findViewById(R.id.tv_time);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		RecordHomeEnity homeEnity = list.get(position);
		holder.tv_title.setText("入户登记表");
		holder.tv_content.setText("null".equals(homeEnity.getHouseAddress()) ? "" : homeEnity.getHouseAddress());
		holder.tv_time.setText(homeEnity.getTime().subSequence(0, 19));
		return v;
	}

	class viewHolder {
		public TextView tv_title;
		public TextView tv_content;
		public TextView tv_time;
	}
}
