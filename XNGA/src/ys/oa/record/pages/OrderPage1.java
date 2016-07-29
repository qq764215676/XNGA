package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 接警信息
 */
public class OrderPage1 extends Page {
	public static final String ALARM_TIME_DATA_KEY = "接警时间";
	public static final String ALARM_POLICEMAN_DATA_KEY = "接警民警";
	public static final String ALARM_UNIT_DATA_KEY = "接警单位";

	public OrderPage1(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderFragment1.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(ALARM_TIME_DATA_KEY, mData.getString(ALARM_TIME_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_POLICEMAN_DATA_KEY, mData.getString(ALARM_POLICEMAN_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_UNIT_DATA_KEY, mData.getString(ALARM_UNIT_DATA_KEY), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(ALARM_POLICEMAN_DATA_KEY));
	}
}
