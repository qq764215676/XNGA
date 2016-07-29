package com.xutils.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ys.oa.record.pages.OrderPage1;
import ys.oa.record.pages.OrderPage2;
import ys.oa.record.pages.OrderPage3;
import ys.oa.record.pages.OrderPage4;
import ys.oa.util.L;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.wizardpager.model.ReviewItem;

/**
 * 出警记录表
 */
@Table(name = "record_police")
// 建议加上注解， 混淆后表名不受影响
public class RecordEnity extends EntityBase implements Serializable {

	// Transient使这个列被忽略，不存入数据库
	// 静态字段也不会存入数据库
	// @Transient
	// public String willIgnore;

	@Column(column = "time")
	private String time;

	@Column(column = "alarmTime")
	private String alarmTime;
	@Column(column = "alarmPoliceMan")
	private String alarmPoliceMan;
	@Column(column = "alarmUnit")
	private String alarmUnit;

	@Column(column = "alarmManName")
	private String alarmManName;
	@Column(column = "alarmManAddress")
	private String alarmManAddress;
	@Column(column = "alarmManPhone")
	private String alarmManPhone;

	@Column(column = "alarmType")
	private String alarmType;
	@Column(column = "alarmHasMaterial")
	private String alarmHasMaterial;
	@Column(column = "alarmAddress")
	private String alarmAddress;
	@Column(column = "alarmInfo")
	private String alarmInfo;

	@Column(column = "alarmHandler")
	private String alarmHandler;
	@Column(column = "alarmTimeDeparture")
	private String alarmTimeDeparture;
	@Column(column = "alarmTimeReach")
	private String alarmTimeReach;
	@Column(column = "alarmHandleInfo")
	private String alarmHandleInfo;

	public String getTimeFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(getTime())));
	}

	public ArrayList<ReviewItem> getReviewItems() {
		ArrayList<ReviewItem> list = new ArrayList<ReviewItem>();
		list.add(new ReviewItem(OrderPage1.ALARM_TIME_DATA_KEY, getAlarmTime(), ""));
		list.add(new ReviewItem(OrderPage1.ALARM_POLICEMAN_DATA_KEY, getAlarmPoliceMan(), ""));
		list.add(new ReviewItem(OrderPage1.ALARM_UNIT_DATA_KEY, getAlarmUnit(), ""));
		list.add(new ReviewItem(OrderPage2.ALARM_MAN_NAME_DATA_KEY, getAlarmManName(), ""));
		list.add(new ReviewItem(OrderPage2.ALARM_MAN_ADDRESS_DATA_KEY, getAlarmManAddress(), ""));
		list.add(new ReviewItem(OrderPage2.ALARM_MAN_PHONE_DATA_KEY, getAlarmManPhone(), ""));
		list.add(new ReviewItem(OrderPage3.ALARM_TYPE_DATA_KEY, getAlarmType(), ""));
		list.add(new ReviewItem(OrderPage3.ALARM_HAS_MATERIAL_DATA_KEY, getAlarmHasMaterial(), ""));
		list.add(new ReviewItem(OrderPage3.ALARM_ADDRESS_DATA_KEY, getAlarmAddress(), ""));
		list.add(new ReviewItem(OrderPage3.ALARM_INFO_DATA_KEY, getAlarmInfo(), ""));
		list.add(new ReviewItem(OrderPage4.ALARM_HANDLER_DATA_KEY, getAlarmHandler(), ""));
		list.add(new ReviewItem(OrderPage4.ALARM_TIME_DEPARTURE_DATA_KEY, getAlarmTimeDeparture(), ""));
		list.add(new ReviewItem(OrderPage4.ALARM_TIME_REACH_DATA_KEY, getAlarmTimeReach(), ""));
		list.add(new ReviewItem(OrderPage4.ALARM_HANDLE_INFO_DATA_KEY, getAlarmHandleInfo(), ""));
		return list;
	}

	public void setReviewItems(ArrayList<ReviewItem> reviewItems) {
		setTime("" + System.currentTimeMillis());
		for (int i = 0; i < reviewItems.size(); i++) {
			L.e(reviewItems.get(i).getTitle(), "" + reviewItems.get(i).getDisplayValue());
			if (OrderPage1.ALARM_TIME_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmTime(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage1.ALARM_POLICEMAN_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmPoliceMan(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage1.ALARM_UNIT_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmUnit(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage2.ALARM_MAN_NAME_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmManName(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage2.ALARM_MAN_ADDRESS_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmManAddress(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage2.ALARM_MAN_PHONE_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmManPhone(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage3.ALARM_TYPE_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmType(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage3.ALARM_HAS_MATERIAL_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmHasMaterial(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage3.ALARM_ADDRESS_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmAddress(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage3.ALARM_INFO_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmInfo(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage4.ALARM_HANDLER_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmHandler(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage4.ALARM_TIME_DEPARTURE_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmTimeDeparture(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage4.ALARM_TIME_REACH_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmTimeReach(reviewItems.get(i).getDisplayValue());
			} else if (OrderPage4.ALARM_HANDLE_INFO_DATA_KEY.equals(reviewItems.get(i).getTitle())) {
				setAlarmHandleInfo(reviewItems.get(i).getDisplayValue());
			}
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getAlarmPoliceMan() {
		return alarmPoliceMan;
	}

	public void setAlarmPoliceMan(String alarmPoliceMan) {
		this.alarmPoliceMan = alarmPoliceMan;
	}

	public String getAlarmUnit() {
		return alarmUnit;
	}

	public void setAlarmUnit(String alarmUnit) {
		this.alarmUnit = alarmUnit;
	}

	public String getAlarmManName() {
		return alarmManName;
	}

	public void setAlarmManName(String alarmManName) {
		this.alarmManName = alarmManName;
	}

	public String getAlarmManAddress() {
		return alarmManAddress;
	}

	public void setAlarmManAddress(String alarmManAddress) {
		this.alarmManAddress = alarmManAddress;
	}

	public String getAlarmManPhone() {
		return alarmManPhone;
	}

	public void setAlarmManPhone(String alarmManPhone) {
		this.alarmManPhone = alarmManPhone;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmHasMaterial() {
		return alarmHasMaterial;
	}

	public void setAlarmHasMaterial(String alarmHasMaterial) {
		this.alarmHasMaterial = alarmHasMaterial;
	}

	public String getAlarmAddress() {
		return alarmAddress;
	}

	public void setAlarmAddress(String alarmAddress) {
		this.alarmAddress = alarmAddress;
	}

	public String getAlarmInfo() {
		return alarmInfo;
	}

	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo;
	}

	public String getAlarmHandler() {
		return alarmHandler;
	}

	public void setAlarmHandler(String alarmHandler) {
		this.alarmHandler = alarmHandler;
	}

	public String getAlarmTimeDeparture() {
		return alarmTimeDeparture;
	}

	public void setAlarmTimeDeparture(String alarmTimeDeparture) {
		this.alarmTimeDeparture = alarmTimeDeparture;
	}

	public String getAlarmTimeReach() {
		return alarmTimeReach;
	}

	public void setAlarmTimeReach(String alarmTimeReach) {
		this.alarmTimeReach = alarmTimeReach;
	}

	public String getAlarmHandleInfo() {
		return alarmHandleInfo;
	}

	public void setAlarmHandleInfo(String alarmHandleInfo) {
		this.alarmHandleInfo = alarmHandleInfo;
	}

}
