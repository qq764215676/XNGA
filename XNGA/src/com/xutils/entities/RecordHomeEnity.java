package com.xutils.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ys.oa.record.pages.OrderHomePage1;
import ys.oa.record.pages.OrderHomePage2;
import ys.oa.record.pages.OrderHomePage3;
import ys.oa.record.pages.OrderHomePage4;
import ys.oa.util.L;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.wizardpager.model.ReviewItem;

/**
 * 入户登记表
 */
@Table(name = "record_home")
// 建议加上注解， 混淆后表名不受影响
public class RecordHomeEnity extends EntityBase implements Serializable {

	// Transient使这个列被忽略，不存入数据库
	// 静态字段也不会存入数据库
	// @Transient
	// public String willIgnore;

	@Column(column = "time")
	private String time;

	@Column(column = "regTime")
	private String regTime;
	@Column(column = "regPoliceMan")
	private String regPoliceMan;

	@Column(column = "ownerName")
	private String ownerName;
	@Column(column = "ownerSex")
	private String ownerSex;
	@Column(column = "ownerId")
	private String ownerId;
	@Column(column = "ownerPhone")
	private String ownerPhone;

	@Column(column = "houseAddress")
	private String houseAddress;
	@Column(column = "houseUse")
	private String houseUse;
	@Column(column = "houseArea")
	private String houseArea;
	@Column(column = "peopleNum")
	private String peopleNum;
	@Column(column = "beginDate")
	private String beginDate;
	@Column(column = "endDate")
	private String endDate;

	@Column(column = "tenantName")
	private String tenantName;
	@Column(column = "tenantSex")
	private String tenantSex;
	@Column(column = "tenantId")
	private String tenantId;
	@Column(column = "tenantHkAddress")
	private String tenantHkAddress;

	public String getTimeFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(getTime())));
	}

	public ArrayList<ReviewItem> getReviewItems() {
		ArrayList<ReviewItem> list = new ArrayList<ReviewItem>();
		list.add(new ReviewItem(OrderHomePage1.REG_TIME, getRegTime(), ""));
		list.add(new ReviewItem(OrderHomePage1.REG_POLICEMAN, getRegPoliceMan(), ""));
		list.add(new ReviewItem(OrderHomePage2.OWNER_NAME, getOwnerName(), ""));
		list.add(new ReviewItem(OrderHomePage2.OWNER_SEX, getOwnerSex(), ""));
		list.add(new ReviewItem(OrderHomePage2.OWNER_ID, getOwnerId(), ""));
		list.add(new ReviewItem(OrderHomePage2.OWNER_PHONE, getOwnerPhone(), ""));
		list.add(new ReviewItem(OrderHomePage3.HOUSE_ADDRESS, getHouseAddress(), ""));
		list.add(new ReviewItem(OrderHomePage3.HOUSE_USE, getHouseUse(), ""));
		list.add(new ReviewItem(OrderHomePage3.HOUSE_AREA, getHouseArea(), ""));
		list.add(new ReviewItem(OrderHomePage3.PEOPLE_NUM, getPeopleNum(), ""));
		list.add(new ReviewItem(OrderHomePage3.BEGIN_DATE, getBeginDate(), ""));
		list.add(new ReviewItem(OrderHomePage3.END_DATE, getEndDate(), ""));
		list.add(new ReviewItem(OrderHomePage4.TENANT_NAME, getTenantName(), ""));
		list.add(new ReviewItem(OrderHomePage4.TENANT_SEX, getTenantSex(), ""));
		list.add(new ReviewItem(OrderHomePage4.TENANT_ID, getTenantId(), ""));
		list.add(new ReviewItem(OrderHomePage4.TENANT_HK_ADDRESS, getTenantHkAddress(), ""));
		return list;
	}

	public void setReviewItems(ArrayList<ReviewItem> reviewItems) {
		setTime("" + System.currentTimeMillis());
		for (int i = 0; i < reviewItems.size(); i++) {
			L.e(reviewItems.get(i).getTitle(), "" + reviewItems.get(i).getDisplayValue());
			if (OrderHomePage1.REG_TIME.equals(reviewItems.get(i).getTitle())) {
				setRegTime(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage1.REG_POLICEMAN.equals(reviewItems.get(i).getTitle())) {
				setRegPoliceMan(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage2.OWNER_NAME.equals(reviewItems.get(i).getTitle())) {
				setOwnerName(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage2.OWNER_SEX.equals(reviewItems.get(i).getTitle())) {
				setOwnerSex(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage2.OWNER_ID.equals(reviewItems.get(i).getTitle())) {
				setOwnerId(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage2.OWNER_PHONE.equals(reviewItems.get(i).getTitle())) {
				setOwnerPhone(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage3.HOUSE_ADDRESS.equals(reviewItems.get(i).getTitle())) {
				setHouseAddress(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage3.HOUSE_USE.equals(reviewItems.get(i).getTitle())) {
				setHouseUse(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage3.HOUSE_AREA.equals(reviewItems.get(i).getTitle())) {
				setHouseArea(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage3.PEOPLE_NUM.equals(reviewItems.get(i).getTitle())) {
				setPeopleNum(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage3.BEGIN_DATE.equals(reviewItems.get(i).getTitle())) {
				setBeginDate(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage3.END_DATE.equals(reviewItems.get(i).getTitle())) {
				setEndDate(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage4.TENANT_NAME.equals(reviewItems.get(i).getTitle())) {
				setTenantName(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage4.TENANT_SEX.equals(reviewItems.get(i).getTitle())) {
				setTenantSex(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage4.TENANT_ID.equals(reviewItems.get(i).getTitle())) {
				setTenantId(reviewItems.get(i).getDisplayValue());
			} else if (OrderHomePage4.TENANT_HK_ADDRESS.equals(reviewItems.get(i).getTitle())) {
				setTenantHkAddress(reviewItems.get(i).getDisplayValue());
			}
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRegTime() {
		return regTime;
	}

	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}

	public String getRegPoliceMan() {
		return regPoliceMan;
	}

	public void setRegPoliceMan(String regPoliceMan) {
		this.regPoliceMan = regPoliceMan;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerSex() {
		return ownerSex;
	}

	public void setOwnerSex(String ownerSex) {
		this.ownerSex = ownerSex;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerPhone() {
		return ownerPhone;
	}

	public void setOwnerPhone(String ownerPhone) {
		this.ownerPhone = ownerPhone;
	}

	public String getHouseAddress() {
		return houseAddress;
	}

	public void setHouseAddress(String houseAddress) {
		this.houseAddress = houseAddress;
	}

	public String getHouseUse() {
		return houseUse;
	}

	public void setHouseUse(String houseUse) {
		this.houseUse = houseUse;
	}

	public String getHouseArea() {
		return houseArea;
	}

	public void setHouseArea(String houseArea) {
		this.houseArea = houseArea;
	}

	public String getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(String peopleNum) {
		this.peopleNum = peopleNum;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantSex() {
		return tenantSex;
	}

	public void setTenantSex(String tenantSex) {
		this.tenantSex = tenantSex;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantHkAddress() {
		return tenantHkAddress;
	}

	public void setTenantHkAddress(String tenantHkAddress) {
		this.tenantHkAddress = tenantHkAddress;
	}

}
