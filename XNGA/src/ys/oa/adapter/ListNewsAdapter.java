package ys.oa.adapter;

import java.util.ArrayList;

import ys.oa.enity.NewsEnity;
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
 * @category 新闻资讯ListAdapter
 * 
 */
@SuppressLint("NewApi")
public class ListNewsAdapter extends BaseAdapter {
	private ArrayList<NewsEnity> newsEnityList;
	private Context context;

	public ListNewsAdapter(Context context, ArrayList<NewsEnity> newsEnityList) {
		this.newsEnityList = newsEnityList;
		this.context = context;
	}

	public long getItemId(int position) {
		return position;
	}

	public Object getItem(int position) {
		return newsEnityList.get(position);
	}

	public int getCount() {
		if (newsEnityList != null) {
			return newsEnityList.size();
		} else {
			return 0;
		}
	}

	public View getView(final int position, View v, ViewGroup parent) {
		viewHolder holder;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(R.layout.news_list_item, null);
			holder = new viewHolder();
			holder.iv_news_img = (ImageView) v.findViewById(R.id.iv_news_img);
			holder.tv_news_title = (TextView) v.findViewById(R.id.tv_news_title);
			holder.tv_news_content = (TextView) v.findViewById(R.id.tv_news_content);
			holder.tv_news_time = (TextView) v.findViewById(R.id.tv_news_time);
			v.setTag(holder);
		} else {
			holder = (viewHolder) v.getTag();
		}
		holder.tv_news_title.setText(Util.formatText(newsEnityList.get(position).getNewsTitle(), 20));
		holder.tv_news_content.setText(Util.formatText(newsEnityList.get(position).getNewsIntro(), 55));
		if (newsEnityList.get(position).getNewsSmallDocumentId().length() <= 6) {
			holder.iv_news_img.setVisibility(View.GONE);
		} else {
			holder.iv_news_img.setVisibility(View.VISIBLE);
			// BitmapUtils bitmapUtils = new BitmapUtils(context);
			// bitmapUtils.display(holder.iv_news_img,
			// Util.getImgUrl(newsEnityList.get(position).getNewsSmallDocumentId(),
			// "png"));
			Util.displayImg(context, holder.iv_news_img, newsEnityList.get(position).getNewsSmallDocumentId(), "png");
		}
		holder.tv_news_time.setText(newsEnityList.get(position).getNewsTime().substring(0, 19));
		return v;
	}

	class viewHolder {
		public TextView tv_news_title;
		public TextView tv_news_content;
		public ImageView iv_tag_new;
		public ImageView iv_news_img;
		public TextView tv_news_time;
	}
}
