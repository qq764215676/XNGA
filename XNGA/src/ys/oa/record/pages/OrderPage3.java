package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 报警信息
 */
public class OrderPage3 extends Page {
	public static final String ALARM_TYPE_DATA_KEY = "报警形式";
	public static final String ALARM_HAS_MATERIAL_DATA_KEY = "有无材料";
	public static final String ALARM_ADDRESS_DATA_KEY = "报警地点";
	public static final String ALARM_INFO_DATA_KEY = "简要报警内容";

	public OrderPage3(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderFragment3.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(ALARM_TYPE_DATA_KEY, mData.getString(ALARM_TYPE_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_HAS_MATERIAL_DATA_KEY, mData.getString(ALARM_HAS_MATERIAL_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_ADDRESS_DATA_KEY, mData.getString(ALARM_ADDRESS_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem(ALARM_INFO_DATA_KEY, mData.getString(ALARM_INFO_DATA_KEY), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(ALARM_INFO_DATA_KEY));
	}
}
