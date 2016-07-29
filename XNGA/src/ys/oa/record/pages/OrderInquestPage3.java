package ys.oa.record.pages;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.wizardpager.model.ModelCallbacks;
import com.wizardpager.model.Page;
import com.wizardpager.model.ReviewItem;

/**
 * 勘察内容
 */
public class OrderInquestPage3 extends Page {
	public static final String SCENE_LOSS = "现场损失情况";
	public static final String SCENE_DISPOSE = "现场处置意见";
	public static final String CASE_PROPERTY = "案件性质描述";
	public static final String CASE_UNIT = "涉案单位描述";
	public static final String CASE_METHOD = "作案手段描述";
	public static final String CASE_FEATURE = "作案特点描述";
	public static final String CASE_TOOL = "作案工具描述";
	public static final String OTHER_INFO = "备注";

	public OrderInquestPage3(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return OrderInquestFragment3.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(SCENE_LOSS, mData.getString(SCENE_LOSS), getKey(), -1));
		dest.add(new ReviewItem(SCENE_DISPOSE, mData.getString(SCENE_DISPOSE), getKey(), -1));
		dest.add(new ReviewItem(CASE_PROPERTY, mData.getString(CASE_PROPERTY), getKey(), -1));
		dest.add(new ReviewItem(CASE_UNIT, mData.getString(CASE_UNIT), getKey(), -1));
		dest.add(new ReviewItem(CASE_METHOD, mData.getString(CASE_METHOD), getKey(), -1));
		dest.add(new ReviewItem(CASE_FEATURE, mData.getString(CASE_FEATURE), getKey(), -1));
		dest.add(new ReviewItem(CASE_TOOL, mData.getString(CASE_TOOL), getKey(), -1));
		dest.add(new ReviewItem(OTHER_INFO, mData.getString(OTHER_INFO), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		return true;
	}
}
