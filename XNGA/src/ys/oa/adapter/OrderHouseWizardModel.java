package ys.oa.adapter;

import ys.oa.record.pages.OrderHousePage1;
import ys.oa.record.pages.OrderHousePage3;
import android.content.Context;

import com.wizardpager.model.AbstractWizardModel;
import com.wizardpager.model.PageList;
import com.wizardpager.model.SingleFixedChoicePage;

public class OrderHouseWizardModel extends AbstractWizardModel {
	public OrderHouseWizardModel(Context context) {
		super(context);
	}

	@Override
	protected PageList onNewRootPageList() {
		return new PageList(new OrderHousePage1(this, "房屋信息").setRequired(false), new SingleFixedChoicePage(this,
				HOUSE_CHECK_1).setChoices("未落实", "已落实").setRequired(false), new SingleFixedChoicePage(this,
				HOUSE_CHECK_2).setChoices("未落实", "已落实").setRequired(false), new SingleFixedChoicePage(this,
				HOUSE_CHECK_3).setChoices("未落实", "已落实").setRequired(false), new SingleFixedChoicePage(this,
				HOUSE_CHECK_4).setChoices("未落实", "已落实").setRequired(false), new SingleFixedChoicePage(this,
				HOUSE_CHECK_5).setChoices("未落实", "已落实").setRequired(false), new SingleFixedChoicePage(this,
				HOUSE_CHECK_6).setChoices("未落实", "已落实").setRequired(false),
				new OrderHousePage3(this, "整改意见").setRequired(false));
	}

	public static final String HOUSE_CHECK_1 = "1、对居住人的姓名、性别、年龄、常住户口所在地、职业或者主要经济来源，服务处所等具体情况进行登记后向公安派出所备案。";
	public static final String HOUSE_CHECK_2 = "2、发现居住人有违法犯罪活动或者有违法犯罪嫌疑的，应当及时报告公安机关。";
	public static final String HOUSE_CHECK_3 = "3、对居住的房屋经常进行安全检查，及时发现和排除不安全隐患，保障居住人的居住安全。";
	public static final String HOUSE_CHECK_4 = "4、将居住房屋转租或转借他人的，应当向当地公安派出所申报备案。";
	public static final String HOUSE_CHECK_5 = "5、居住的房屋不准用于生产、储存、经营易燃易爆和剧毒等危险物品。";
	public static final String HOUSE_CHECK_6 = "6、居住的房屋不准用于\"邪教\"组织或非法团体做为活动据点。";
}
