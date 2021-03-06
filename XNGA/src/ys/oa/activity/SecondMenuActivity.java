package ys.oa.activity;

import java.util.ArrayList;

import ys.nlga.activity.R;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;

@SuppressLint("NewApi")
public class SecondMenuActivity extends ListActivity implements OnClickListener
{
	private ImageButton btn_back;
	
	private static int section = 0;
	private static int[] sectionBgs = { R.drawable.secion_1, R.drawable.secion_2, R.drawable.secion_3, R.drawable.secion_4 };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pinned_section_list);
		Util.initExce(this);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getIntent().getStringExtra("Title"));
		actionBar.hide(); //目前需求修改后，不再使用ActionBar；故用此方法隐藏以前使用的ActionBar
		
		findView();
		initListener();
		
		// ((PinnedSectionListView) getListView()).setShadowVisible(false);
		// getListView().setPadding(30, 0, 30, 0);
		setListAdapter(new SimpleAdapter(this, R.id.tv_section, (ArrayList<SectionEnity>) getIntent().getSerializableExtra("list")));
	}
	
	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_zfgflb_back);
	}
	
	private void initListener()
	{
		btn_back.setOnClickListener(this);
	}
	
	static class SimpleAdapter extends ArrayAdapter<SectionEnity> implements PinnedSectionListAdapter
	{
		private ArrayList<SectionEnity> list;
		private Context context;

		public SimpleAdapter(Context context, int textViewResourceId, ArrayList<SectionEnity> list)
		{
			super(context, textViewResourceId, list);
			this.list = list;
			this.context = context;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent)
		{
			if (list.get(position).getType() == SectionEnity.SECTION)
			{
				viewHolderSection holder;
				if (v == null)
				{
					LayoutInflater inflater = LayoutInflater.from(context);
					v = inflater.inflate(R.layout.list_section, null);
					holder = new viewHolderSection();
					holder.tv_section = (TextView) v.findViewById(R.id.tv_section);
					v.setTag(holder);
				}
				else
				{
					holder = (viewHolderSection) v.getTag();
				}
				holder.tv_section.setText(list.get(position).getText());
				v.setBackgroundResource(sectionBgs[section % 4]);
				section++;
			}
			else if (list.get(position).getType() == SectionEnity.ITEM)
			{
				viewHolderItem holder;
				if (v == null)
				{
					LayoutInflater inflater = LayoutInflater.from(context);
					v = inflater.inflate(R.layout.list_section_item, null);
					holder = new viewHolderItem();
					//holder.iv_icon = (ImageView) v.findViewById(R.id.iv_zfgf_listItemIcon);
					holder.tv_item = (TextView) v.findViewById(R.id.tv_item);
					v.setTag(holder);
				}
				else
				{
					holder = (viewHolderItem) v.getTag();
				}
				//holder.iv_icon.setImageResource(list.get(position).getIconId());
				holder.tv_item.setText(list.get(position).getText());
				if (list.get(position).getIconId() != 0)
				{
					holder.tv_item.setCompoundDrawablesRelativeWithIntrinsicBounds(list.get(position).getIconId(), 0, 0, 0);
				}
				else
				{
					holder.tv_item.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
				}
				v.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View paramView)
					{
						if (list.get(position).getActivityClass() != null)
						{
							if (list.get(position).getTitle() != null)
							{
								context.startActivity(new Intent(context, list.get(position).getActivityClass()).putExtra("Title", list.get(position).getTitle()));
							}
							else
							{
								context.startActivity(new Intent(context, list.get(position).getActivityClass()));
							}
						}
					}
				});
			}
			return v;
		}

		class viewHolderSection
		{
			public TextView tv_section;
		}

		class viewHolderItem
		{
			public ImageView iv_icon;
			public TextView tv_item;
		}

		@Override
		public int getViewTypeCount()
		{
			return 2;
		}

		@Override
		public int getItemViewType(int position)
		{
			return getItem(position).getType();
		}

		@Override
		public boolean isItemViewTypePinned(int viewType)
		{
			return viewType == SectionEnity.SECTION;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_zfgflb_back:
			{
				finish();
			}break;
		}
	}
}