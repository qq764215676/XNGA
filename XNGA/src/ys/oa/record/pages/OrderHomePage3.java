package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 出租房信息
 */
public class OrderHomePage3 extends Page {
	public static final String HOUSE_ADDRESS = "房屋地址";
	public static final String HOUSE_USE = "房屋用途";
	public static final String HOUSE_AREA = "房屋面积";
	public static final String PEOPLE_NUM = "居住人数";
	public static final String BEGIN_DATE = "出租日期";
	public static final String END_DATE = "注销日期";

	public OrderHomePage3(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderHomeFragment3.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(HOUSE_ADDRESS, mData.getString(HOUSE_ADDRESS), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_USE, mData.getString(HOUSE_USE), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_AREA, mData.getString(HOUSE_AREA), getKey(), -1));
		dest.add(new ReviewItem(PEOPLE_NUM, mData.getString(PEOPLE_NUM), getKey(), -1));
		dest.add(new ReviewItem(BEGIN_DATE, mData.getString(BEGIN_DATE), getKey(), -1));
		dest.add(new ReviewItem(END_DATE, mData.getString(END_DATE), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(HOUSE_ADDRESS));
	}
}
