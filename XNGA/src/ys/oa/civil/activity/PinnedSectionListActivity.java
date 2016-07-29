package ys.oa.civil.activity;

import java.util.ArrayList;

import ys.oa.enity.SectionEnity;
import ys.oa.util.Util;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import ys.nlga.activity.R;

@SuppressLint("NewApi")
public class PinnedSectionListActivity extends ListActivity {

	private static int section = 0;
	private static int[] sectionBgs = { R.drawable.secion_1, R.drawable.secion_2, R.drawable.secion_3,
			R.drawable.secion_4 };

	static class SimpleAdapter extends ArrayAdapter<SectionEnity> implements PinnedSectionListAdapter {

		private ArrayList<SectionEnity> list;
		private Context context;

		public SimpleAdapter(Context context, int textViewResourceId, ArrayList<SectionEnity> list) {
			super(context, textViewResourceId, list);
			this.list = list;
			this.context = context;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			if (list.get(position).getType() == SectionEnity.SECTION) {
				viewHolderSection holder;
				if (v == null) {
					LayoutInflater inflater = LayoutInflater.from(context);
					v = inflater.inflate(R.layout.list_section, null);
					holder = new viewHolderSection();
					holder.tv_section = (TextView) v.findViewById(R.id.tv_section);
					v.setTag(holder);
				} else {
					holder = (viewHolderSection) v.getTag();
				}
				holder.tv_section.setText(list.get(position).getText());
				v.setBackgroundResource(sectionBgs[section % 4]);
				section++;
			} else if (list.get(position).getType() == SectionEnity.ITEM) {
				viewHolderItem holder;
				if (v == null) {
					LayoutInflater inflater = LayoutInflater.from(context);
					v = inflater.inflate(R.layout.list_section_item, null);
					holder = new viewHolderItem();
					holder.tv_item = (TextView) v.findViewById(R.id.tv_item);
					v.setTag(holder);
				} else {
					holder = (viewHolderItem) v.getTag();
				}
				holder.tv_item.setText(list.get(position).getText());
				if (list.get(position).getIconId() != 0) {
					holder.tv_item.setCompoundDrawablesRelativeWithIntrinsicBounds(list.get(position).getIconId(), 0,
							0, 0);
				} else {
					holder.tv_item.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
				}
				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						context.startActivity(new Intent(context, WebActivity.class).putExtra("url",
								list.get(position).getUrl()).putExtra("Title", list.get(position).getText()));
					}
				});
			}
			return v;
		}

		class viewHolderSection {
			public TextView tv_section;
		}

		class viewHolderItem {
			public TextView tv_item;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).getType();
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == SectionEnity.SECTION;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pinned_section_list);
		Util.initExce(this);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getIntent().getStringExtra("Title"));

		// ((PinnedSectionListView) getListView()).setShadowVisible(false);
		// getListView().setPadding(30, 0, 30, 0);
		setListAdapter(new SimpleAdapter(this, R.id.tv_section, (ArrayList<SectionEnity>) getIntent()
				.getSerializableExtra("list")));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}