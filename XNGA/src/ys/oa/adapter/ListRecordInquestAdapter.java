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
import com.xutils.entities.RecordInquestEnity;

/**
 * @author wufan
 * @category 记录表ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListRecordInquestAdapter extends BaseAdapter {
	private List<RecordInquestEnity> list;
	private Context context;

	public ListRecordInquestAdapter(Context context, List<RecordInquestEnity> list) {
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
		RecordInquestEnity inquestEnity = list.get(position);
		holder.tv_title.setText("现场勘查记录表");
		holder.tv_content.setText(inquestEnity.getCaseInfo());
		holder.tv_time.setText(inquestEnity.getTimeFormat());
		return v;
	}

	class viewHolder {
		public TextView tv_title;
		public TextView tv_content;
		public TextView tv_time;
	}
}
