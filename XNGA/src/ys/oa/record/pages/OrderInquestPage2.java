package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 勘验信息
 */
public class OrderInquestPage2 extends Page {
	public static final String INQUEST_TIME = "勘验时间";
	public static final String INQUEST_END_TIME = "勘验结束时间";
	public static final String INQUEST_ADDRESS = "勘验地点";
	public static final String INQUEST_LEADER = "勘验指挥人员";
	public static final String INQUEST_MEMBER = "勘验检查人员";

	public OrderInquestPage2(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderInquestFragment2.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(INQUEST_TIME, mData.getString(INQUEST_TIME), getKey(), -1));
		dest.add(new ReviewItem(INQUEST_END_TIME, mData.getString(INQUEST_END_TIME), getKey(), -1));
		dest.add(new ReviewItem(INQUEST_ADDRESS, mData.getString(INQUEST_ADDRESS), getKey(), -1));
		dest.add(new ReviewItem(INQUEST_LEADER, mData.getString(INQUEST_LEADER), getKey(), -1));
		dest.add(new ReviewItem(INQUEST_MEMBER, mData.getString(INQUEST_MEMBER), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return true;
	}
}
