package com.xutils.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ys.oa.adapter.OrderHouseWizardModel;
import ys.oa.record.pages.OrderHousePage1;
import ys.oa.record.pages.OrderHousePage3;
import ys.oa.util.L;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.wizardpager.model.ReviewItem;

/**
 * 租房记录表
 */
@Table(name = "record_house")
// 建议加上注解， 混淆后表名不受影响
public class RecordHouseEnity extends EntityBase implements Serializable {

	// Transient使这个列被忽略，不存入数据库
	// 静态字段也不会存入数据库
	// @Transient
	// public String willIgnore;

	@Column(column = "time")
	private String time;

	@Column(column = "houseCommunity")
	private String houseCommunity;
	@Column(column = "houseAddress")
	private String houseAddress;
	@Column(column = "houseOwner")
	private String houseOwner;
	@Column(column = "houseOwnerPhone")
	private String houseOwnerPhone;
	@Column(column = "houseKeeper")
	private String houseKeeper;
	@Column(column = "houseKeeperPhone")
	private String houseKeeperPhone;
	@Column(column = "checkTime")
	private String checkTime;
	@Column(column = "checker")
	private String checker;

	@Column(column = "houseCheck1")
	private String houseCheck1;
	@Column(column = "houseCheck2")
	private String houseCheck2;
	@Column(column = "houseCheck3")
	private String houseCheck3;
	@Column(column = "houseCheck4")
	private String houseCheck4;
	@Column(column = "houseCheck5")
	private String houseCheck5;
	@Column(column = "houseCheck6")
	private String houseCheck6;

	@Column(column = "changeOpinion")
	private String changeOpinion;
	@Column(column = "ownerOpinion")
	private String ownerOpinion;

	public String getTimeFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(getTime())));
	}

	public ArrayList<ReviewItem> getReviewItems() {
		ArrayList<ReviewItem> list = new ArrayList<ReviewItem>();
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_1, getHouseCommunity(), ""));
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_2, getHouseAddress(), ""));
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_3, getHouseOwner(), ""));
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_4, getHouseOwnerPhone(), ""));
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_5, getHouseKeeper(), ""));
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_6, getHouseKeeperPhone(), ""));
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_7, getCheckTime(), ""));
		list.add(new ReviewItem(OrderHousePage1.HOUSE_INFO_8, getChecker(), ""));
		list.add(new ReviewItem(OrderHouseWizardModel.HOUSE_CHECK_1, getHouseCheck1(), ""));
		list.add(new ReviewItem(OrderHouseWizardModel.HOUSE_CHECK_2, getHouseCheck2(), ""));
		list.add(new ReviewItem(OrderHouseWizardModel.HOUSE_CHECK_3, getHouseCheck3(), ""));
		list.add(new ReviewItem(OrderHouseWizardModel.HOUSE_CHECK_4, getHouseCheck4(), ""));
		list.add(new ReviewItem(OrderHouseWizardModel.HOUSE_CHECK_5, getHouseCheck5(), ""));
		list.add(new ReviewItem(OrderHouseWizardModel.HOUSE_CHECK_6, getHouseCheck6(), ""));
		list.add(new ReviewItem(OrderHousePage3.HOUSE_OPINION_1, getChangeOpinion(), ""));
		list.add(new ReviewItem(OrderHousePage3.HOUSE_OPINION_2, getOwnerOpinion(), ""));
		return list;
	}

	public void setReviewItems(ArrayList<ReviewItem> reviewItems) {
		setTime("" + System.currentTimeMillis());
		for (int i = 0; i < reviewItems.size(); i++) {
			L.e(reviewItems.get(i).getTitle(), "" + reviewItems.get(i).getDisplayValue());
			if (OrderHousePage1.HOUSE_INFO_1.equals(reviewItems.get(i).getTitle())) {
				setHouseCommunity(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage1.HOUSE_INFO_2.equals(reviewItems.get(i).getTitle())) {
				setHouseAddress(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage1.HOUSE_INFO_3.equals(reviewItems.get(i).getTitle())) {
				setHouseOwner(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage1.HOUSE_INFO_4.equals(reviewItems.get(i).getTitle())) {
				setHouseOwnerPhone(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage1.HOUSE_INFO_5.equals(reviewItems.get(i).getTitle())) {
				setHouseKeeper(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage1.HOUSE_INFO_6.equals(reviewItems.get(i).getTitle())) {
				setHouseKeeperPhone(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage1.HOUSE_INFO_7.equals(reviewItems.get(i).getTitle())) {
				setCheckTime(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage1.HOUSE_INFO_8.equals(reviewItems.get(i).getTitle())) {
				setChecker(reviewItems.get(i).getDisplayValue());
			} else if (OrderHouseWizardModel.HOUSE_CHECK_1.equals(reviewItems.get(i).getTitle())) {
				setHouseCheck1(reviewItems.get(i).getDisplayValue());
			} else if (OrderHouseWizardModel.HOUSE_CHECK_2.equals(reviewItems.get(i).getTitle())) {
				setHouseCheck2(reviewItems.get(i).getDisplayValue());
			} else if (OrderHouseWizardModel.HOUSE_CHECK_3.equals(reviewItems.get(i).getTitle())) {
				setHouseCheck3(reviewItems.get(i).getDisplayValue());
			} else if (OrderHouseWizardModel.HOUSE_CHECK_4.equals(reviewItems.get(i).getTitle())) {
				setHouseCheck4(reviewItems.get(i).getDisplayValue());
			} else if (OrderHouseWizardModel.HOUSE_CHECK_5.equals(reviewItems.get(i).getTitle())) {
				setHouseCheck5(reviewItems.get(i).getDisplayValue());
			} else if (OrderHouseWizardModel.HOUSE_CHECK_6.equals(reviewItems.get(i).getTitle())) {
				setHouseCheck6(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage3.HOUSE_OPINION_1.equals(reviewItems.get(i).getTitle())) {
				setChangeOpinion(reviewItems.get(i).getDisplayValue());
			} else if (OrderHousePage3.HOUSE_OPINION_2.equals(reviewItems.get(i).getTitle())) {
				setOwnerOpinion(reviewItems.get(i).getDisplayValue());
			}
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHouseCommunity() {
		return houseCommunity;
	}

	public void setHouseCommunity(String houseCommunity) {
		this.houseCommunity = houseCommunity;
	}

	public String getHouseAddress() {
		return houseAddress;
	}

	public void setHouseAddress(String houseAddress) {
		this.houseAddress = houseAddress;
	}

	public String getHouseOwner() {
		return houseOwner;
	}

	public void setHouseOwner(String houseOwner) {
		this.houseOwner = houseOwner;
	}

	public String getHouseOwnerPhone() {
		return houseOwnerPhone;
	}

	public void setHouseOwnerPhone(String houseOwnerPhone) {
		this.houseOwnerPhone = houseOwnerPhone;
	}

	public String getHouseKeeper() {
		return houseKeeper;
	}

	public void setHouseKeeper(String houseKeeper) {
		this.houseKeeper = houseKeeper;
	}

	public String getHouseKeeperPhone() {
		return houseKeeperPhone;
	}

	public void setHouseKeeperPhone(String houseKeeperPhone) {
		this.houseKeeperPhone = houseKeeperPhone;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getHouseCheck1() {
		return houseCheck1;
	}

	public void setHouseCheck1(String houseCheck1) {
		this.houseCheck1 = houseCheck1;
	}

	public String getHouseCheck2() {
		return houseCheck2;
	}

	public void setHouseCheck2(String houseCheck2) {
		this.houseCheck2 = houseCheck2;
	}

	public String getHouseCheck3() {
		return houseCheck3;
	}

	public void setHouseCheck3(String houseCheck3) {
		this.houseCheck3 = houseCheck3;
	}

	public String getHouseCheck4() {
		return houseCheck4;
	}

	public void setHouseCheck4(String houseCheck4) {
		this.houseCheck4 = houseCheck4;
	}

	public String getHouseCheck5() {
		return houseCheck5;
	}

	public void setHouseCheck5(String houseCheck5) {
		this.houseCheck5 = houseCheck5;
	}

	public String getHouseCheck6() {
		return houseCheck6;
	}

	public void setHouseCheck6(String houseCheck6) {
		this.houseCheck6 = houseCheck6;
	}

	public String getChangeOpinion() {
		return changeOpinion;
	}

	public void setChangeOpinion(String changeOpinion) {
		this.changeOpinion = changeOpinion;
	}

	public String getOwnerOpinion() {
		return ownerOpinion;
	}

	public void setOwnerOpinion(String ownerOpinion) {
		this.ownerOpinion = ownerOpinion;
	}

}
