package ys.oa.activity;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import ys.nlga.activity.R;
import com.wizardpager.model.ReviewItem;
import com.xutils.entities.RecordEnity;
import com.xutils.entities.RecordHomeEnity;
import com.xutils.entities.RecordHouseEnity;
import com.xutils.entities.RecordInquestEnity;

public class RecordDetailActivity extends SwipeBackActivity {

	static class SimpleAdapter extends BaseAdapter {

		private ArrayList<ReviewItem> list;
		private Context context;

		public SimpleAdapter(Context context, ArrayList<ReviewItem> list) {
			this.list = list;
			this.context = context;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			viewHolder holder;
			if (v == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				v = inflater.inflate(R.layout.list_item_review, null);
				holder = new viewHolder();
				holder.text1 = (TextView) v.findViewById(android.R.id.text1);
				holder.text2 = (TextView) v.findViewById(android.R.id.text2);
				v.setTag(holder);
			} else {
				holder = (viewHolder) v.getTag();
			}
			ReviewItem reviewItem = list.get(position);
			String value = reviewItem.getDisplayValue();
			if (TextUtils.isEmpty(value) || "null".equals(value)) {
				value = context.getResources().getString(R.string.review_none);
			}
			holder.text1.setText(reviewItem.getTitle());
			holder.text2.setText(value);
			return v;
		}

		class viewHolder {
			public TextView text1;
			public TextView text2;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int index) {
			return list.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

	}

	@ViewInject(android.R.id.title)
	private TextView titleView;

	@ViewInject(android.R.id.list)
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_page);
		Util.initExce(this);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		ViewUtils.inject(this);

		setTitle(getIntent().getStringExtra("Title"));
		titleView.setText(getTitle());
		titleView.setTextColor(getResources().getColor(R.color.review_green));

		ArrayList<ReviewItem> list = new ArrayList<ReviewItem>();

		if ("出警记录表".equals(getTitle())) {
			list = ((RecordEnity) getIntent().getSerializableExtra("enity")).getReviewItems();
		} else if ("现场勘查记录表".equals(getTitle())) {
			list = ((RecordInquestEnity) getIntent().getSerializableExtra("enity")).getReviewItems();
		} else if ("出租屋检查表".equals(getTitle())) {
			list = ((RecordHouseEnity) getIntent().getSerializableExtra("enity")).getReviewItems();
		} else if ("入户登记表".equals(getTitle())) {
			list = ((RecordHomeEnity) getIntent().getSerializableExtra("enity")).getReviewItems();
		}

		listView.setAdapter(new SimpleAdapter(this, list));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			// scrollToFinishActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}