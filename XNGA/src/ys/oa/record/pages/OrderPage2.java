package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 报警人信息
 */
public class OrderPage2 extends Page {
	public static final String ALARM_MAN_NAME_DATA_KEY = "报警人";
	public static final String ALARM_MAN_ADDRESS_DATA_KEY = "地址";
	public static final String ALARM_MAN_PHONE_DATA_KEY = "联系电话";

	public OrderPage2(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderFragment2.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(ALARM_MAN_NAME_DATA_KEY, mData.getString(ALARM_MAN_NAME_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_MAN_ADDRESS_DATA_KEY, mData.getString(ALARM_MAN_ADDRESS_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_MAN_PHONE_DATA_KEY, mData.getString(ALARM_MAN_PHONE_DATA_KEY), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(ALARM_MAN_NAME_DATA_KEY));
	}
}
