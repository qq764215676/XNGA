package ys.oa.adapter;

import ys.oa.record.pages.OrderHomePage1;
import ys.oa.record.pages.OrderHomePage2;
import ys.oa.record.pages.OrderHomePage3;
import ys.oa.record.pages.OrderHomePage4;
import android.content.Context;

import com.wizardpager.model.AbstractWizardModel;
import com.wizardpager.model.PageList;

public class OrderHomeWizardModel extends AbstractWizardModel {
	public OrderHomeWizardModel(Context context) {
		super(context);
	}

	@Override
	protected PageList onNewRootPageList() {
		return new PageList(new OrderHomePage1(this, "民警信息").setRequired(false),
				new OrderHomePage2(this, "业主信息").setRequired(false),
				new OrderHomePage3(this, "出租屋信息").setRequired(false),
				new OrderHomePage4(this, "承租人信息").setRequired(false));
	}
}
