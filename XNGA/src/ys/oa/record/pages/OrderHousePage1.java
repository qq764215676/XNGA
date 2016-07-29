package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 房屋信息
 */
public class OrderHousePage1 extends Page {
	public static final String HOUSE_INFO_1 = "所属社区";
	public static final String HOUSE_INFO_2 = "房屋地址";
	public static final String HOUSE_INFO_3 = "房主姓名";
	public static final String HOUSE_INFO_4 = "房主电话";
	public static final String HOUSE_INFO_5 = "管理人";
	public static final String HOUSE_INFO_6 = "管理人电话";
	public static final String HOUSE_INFO_7 = "检查时间";
	public static final String HOUSE_INFO_8 = "检查人员";

	public OrderHousePage1(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderHouseFragment1.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(HOUSE_INFO_1, mData.getString(HOUSE_INFO_1), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_INFO_2, mData.getString(HOUSE_INFO_2), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_INFO_3, mData.getString(HOUSE_INFO_3), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_INFO_4, mData.getString(HOUSE_INFO_4), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_INFO_5, mData.getString(HOUSE_INFO_5), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_INFO_6, mData.getString(HOUSE_INFO_6), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_INFO_7, mData.getString(HOUSE_INFO_7), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_INFO_8, mData.getString(HOUSE_INFO_8), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return true;
	}
}
