package ys.oa.adapter;

import ys.oa.record.pages.OrderPage1;
import ys.oa.record.pages.OrderPage2;
import ys.oa.record.pages.OrderPage3;
import ys.oa.record.pages.OrderPage4;
import android.content.Context;

import com.wizardpager.model.AbstractWizardModel;
import com.wizardpager.model.PageList;

public class OrderWizardModel extends AbstractWizardModel {
	public OrderWizardModel(Context context) {
		super(context);
	}

	@Override
	protected PageList onNewRootPageList() {
		return new PageList(new OrderPage1(this, "接警信息").setRequired(false),
				new OrderPage2(this, "报警人信息").setRequired(false), 
				new OrderPage3(this, "报警信息").setRequired(false),
				new OrderPage4(this, "出警信息").setRequired(false));
	}
}
