package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 整改意见
 */
public class OrderHousePage3 extends Page {
	public static final String HOUSE_OPINION_1 = "整改意见";
	public static final String HOUSE_OPINION_2 = "房主或管理人意见";

	public OrderHousePage3(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderHouseFragment3.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(HOUSE_OPINION_1, mData.getString(HOUSE_OPINION_1), getKey(), -1));
		dest.add(new ReviewItem(HOUSE_OPINION_2, mData.getString(HOUSE_OPINION_2), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return true;
	}
}
