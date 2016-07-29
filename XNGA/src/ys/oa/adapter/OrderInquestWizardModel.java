package ys.oa.adapter;

import ys.oa.record.pages.OrderInquestPage1;
import ys.oa.record.pages.OrderInquestPage2;
import ys.oa.record.pages.OrderInquestPage3;
import android.content.Context;

import com.wizardpager.model.AbstractWizardModel;
import com.wizardpager.model.PageList;

public class OrderInquestWizardModel extends AbstractWizardModel {
	public OrderInquestWizardModel(Context context) {
		super(context);
	}

	@Override
	protected PageList onNewRootPageList() {
		return new PageList(new OrderInquestPage1(this, "案件信息").setRequired(false),
				new OrderInquestPage2(this, "勘验信息").setRequired(false),
				new OrderInquestPage3(this, "勘验内容").setRequired(false));
	}
}
