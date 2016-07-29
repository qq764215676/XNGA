package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 承租人信息
 */
public class OrderHomePage4 extends Page {
	public static final String TENANT_NAME = "承租人姓名";
	public static final String TENANT_SEX = "承租人性别";
	public static final String TENANT_ID = "身份证号";
	public static final String TENANT_HK_ADDRESS = "户口所在地";

	public OrderHomePage4(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderHomeFragment4.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(TENANT_NAME, mData.getString(TENANT_NAME), getKey(), -1));
		dest.add(new ReviewItem(TENANT_SEX, mData.getString(TENANT_SEX), getKey(), -1));
		dest.add(new ReviewItem(TENANT_ID, mData.getString(TENANT_ID), getKey(), -1));
		dest.add(new ReviewItem(TENANT_HK_ADDRESS, mData.getString(TENANT_HK_ADDRESS), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(TENANT_NAME));
	}
}
