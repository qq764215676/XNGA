package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 民警信息
 */
public class OrderHomePage1 extends Page {
	public static final String REG_TIME = "登记时间";
	public static final String REG_POLICEMAN = "社区民警";

	public OrderHomePage1(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderHomeFragment1.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(REG_TIME, mData.getString(REG_TIME), getKey(), -1));
		dest.add(new ReviewItem(REG_POLICEMAN, mData.getString(REG_POLICEMAN), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(REG_TIME));
	}
}
