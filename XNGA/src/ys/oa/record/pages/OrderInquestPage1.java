package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 案件信息
 */
public class OrderInquestPage1 extends Page {
	public static final String CASE_TIME = "发案时间";
	public static final String CASE_ADDRESS = "发案地点";
	public static final String CASE_INFO = "案件发现过程";

	public OrderInquestPage1(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderInquestFragment1.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(CASE_TIME, mData.getString(CASE_TIME), getKey(), -1));
		dest.add(new ReviewItem(CASE_ADDRESS, mData.getString(CASE_ADDRESS), getKey(), -1));
		dest.add(new ReviewItem(CASE_INFO, mData.getString(CASE_INFO), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return true;
	}
}
