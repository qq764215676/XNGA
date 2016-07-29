package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 业主信息
 */
public class OrderHomePage2 extends Page {
	public static final String OWNER_NAME = "业主姓名";
	public static final String OWNER_SEX = "业主性别";
	public static final String OWNER_ID = "业主身份证号";
	public static final String OWNER_PHONE = "联系电话";

	public OrderHomePage2(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderHomeFragment2.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(OWNER_NAME, mData.getString(OWNER_NAME), getKey(), -1));
		dest.add(new ReviewItem(OWNER_SEX, mData.getString(OWNER_SEX), getKey(), -1));
		dest.add(new ReviewItem(OWNER_ID, mData.getString(OWNER_ID), getKey(), -1));
		dest.add(new ReviewItem(OWNER_PHONE, mData.getString(OWNER_PHONE), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(OWNER_NAME));
	}
}
