package ys.oa.adapter;

import java.util.ArrayList;
import ys.nlga.activity.R;
import ys.oa.activity.CollectBikeActivity;
import ys.oa.activity.CollectCarActivity;
import ys.oa.enity.IDInfoEntity;
import ys.oa.util.Constants;
import ys.oa.util.Util;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class RidersInfoAdapter extends BaseAdapter
{

	private Context context;
	private ArrayList<IDInfoEntity> infoList;
	private LayoutInflater inflater;
	private ListView listView;
	private boolean tag;
	private int type;
	Activity activity;

	public final static int TYPE_VEH_CKS = 0;
	public final static int TYPE_NVEH_CKS = 1;

	/**
	 * @param type
	 *            包括机动车和非机动车
	 * @param activity
	 * @param context
	 * @param arrayList
	 * @param listView
	 * @param tag
	 */
	public RidersInfoAdapter(int type, Activity activity, Context context, ArrayList<IDInfoEntity> arrayList, ListView listView, boolean tag)
	{
		this.context = context;
		this.activity = activity;
		this.infoList = arrayList;
		this.listView = listView;
		this.tag = tag;
		this.type = type;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (infoList != null)
		{
			return infoList.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return infoList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parents)
	{
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (v == null)
		{
			holder = new ViewHolder();
			v = inflater.inflate(R.layout.collect_add_riders_item_layout, null);
			holder.img = (ImageView) v.findViewById(R.id.riders_img);
			holder.nameText = (EditText) v.findViewById(R.id.et_riders_name);
			holder.idText = (EditText) v.findViewById(R.id.et_riders_id);
			holder.delete = (ImageView) v.findViewById(R.id.delete);
			if (tag)
			{// tag 为1表示是显示页面，不是录入界面
				holder.delete.setVisibility(View.GONE);
			}
			v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) v.getTag();
		}

		holder.nameText.setText(infoList.get(position).getName());
		holder.idText.setText(infoList.get(position).getId());
		if (infoList.get(position).getImgPath() != null && !infoList.get(position).getImgPath().equals("") && !infoList.get(position).getImgPath().equals("nopicture"))
		{
			Util.displayLocalImg(context, holder.img, infoList.get(position).getImgPath(), ".jpg");
		}
		else
		{
			holder.img.setImageResource(R.drawable.l_xxcj_idcard);
		}
		holder.img.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				switch (type)
				{
					case TYPE_VEH_CKS:
						((CollectCarActivity) activity).takePhoto(position);
						break;
					case TYPE_NVEH_CKS:
						((CollectBikeActivity) activity).takePhoto(position);
						break;
				}
			}
		});

		holder.delete.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				infoList.remove(position);
				notifyDataSetChanged();
				Util.setListViewHeight(listView);
			}
		});
		return v;
	}

	private class ViewHolder
	{
		private ImageView img;
		private EditText nameText;
		private EditText idText;
		private ImageView delete;
	}

	public void notify(ArrayList<IDInfoEntity> list)
	{
		this.infoList = list;
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged()
	{
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

}
