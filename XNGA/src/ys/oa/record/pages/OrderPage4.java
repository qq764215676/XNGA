package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 出警信息
 */
public class OrderPage4 extends Page {
	public static final String ALARM_HANDLER_DATA_KEY = "出警民警";
	public static final String ALARM_TIME_DEPARTURE_DATA_KEY = "出警时间";
	public static final String ALARM_TIME_REACH_DATA_KEY = "到达时间";
	public static final String ALARM_HANDLE_INFO_DATA_KEY = "处警经过及结果";

	public OrderPage4(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderFragment4.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(ALARM_HANDLER_DATA_KEY, mData.getString(ALARM_HANDLER_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_TIME_DEPARTURE_DATA_KEY, mData.getString(ALARM_TIME_DEPARTURE_DATA_KEY),
				getKey(), -1));
		dest.add(new ReviewItem(ALARM_TIME_REACH_DATA_KEY, mData.getString(ALARM_TIME_REACH_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_HANDLE_INFO_DATA_KEY, mData.getString(ALARM_HANDLE_INFO_DATA_KEY), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(ALARM_HANDLER_DATA_KEY));
	}
}
