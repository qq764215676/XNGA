package com.xutils.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ys.oa.record.pages.OrderInquestPage1;
import ys.oa.record.pages.OrderInquestPage2;
import ys.oa.record.pages.OrderInquestPage3;
import ys.oa.util.L;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.wizardpager.model.ReviewItem;

/**
 * 现场勘验记录表
 */
@Table(name = "record_inquest")
// 建议加上注解， 混淆后表名不受影响
public class RecordInquestEnity extends EntityBase implements Serializable {

	// Transient使这个列被忽略，不存入数据库
	// 静态字段也不会存入数据库
	// @Transient
	// public String willIgnore;

	@Column(column = "time")
	private String time;

	@Column(column = "caseTime")
	private String caseTime;
	@Column(column = "caseAddress")
	private String caseAddress;
	@Column(column = "caseInfo")
	private String caseInfo;

	@Column(column = "inquestTime")
	private String inquestTime;
	@Column(column = "inquestEndTime")
	private String inquestEndTime;
	@Column(column = "inquestAddress")
	private String inquestAddress;
	@Column(column = "inquestLeader")
	private String inquestLeader;
	@Column(column = "inquestMember")
	private String inquestMember;

	@Column(column = "sceneLoss")
	private String sceneLoss;
	@Column(column = "sceneDispose")
	private String sceneDispose;
	@Column(column = "caseProperty")
	private String caseProperty;
	@Column(column = "caseUnit")
	private String caseUnit;
	@Column(column = "caseMethod")
	private String caseMethod;
	@Column(column = "caseFeature")
	private String caseFeature;
	@Column(column = "caseTool")
	private String caseTool;
	@Column(column = "otherInfo")
	private String otherInfo;

	public String getTimeFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(getTime())));
	}

	public ArrayList<ReviewItem> getReviewItems() {
		ArrayList<ReviewItem> list = new ArrayList<ReviewItem>();
		list.add(new ReviewItem(OrderInquestPage1.CASE_TIME, getCaseTime(), ""));
		list.add(new ReviewItem(OrderInquestPage1.CASE_ADDRESS, getCaseAddress(), ""));
		list.add(new ReviewItem(OrderInquestPage1.CASE_INFO, getCaseInfo(), ""));
		list.add(new ReviewItem(OrderInquestPage2.INQUEST_TIME, getInquestTime(), ""));
		list.add(new ReviewItem(OrderInquestPage2.INQUEST_END_TIME, getInquestEndTime(), ""));
		list.add(new ReviewItem(OrderInquestPage2.INQUEST_ADDRESS, getInquestAddress(), ""));
		list.add(new ReviewItem(OrderInquestPage2.INQUEST_LEADER, getInquestLeader(), ""));
		list.add(new ReviewItem(OrderInquestPage2.INQUEST_MEMBER, getInquestMember(), ""));
		list.add(new ReviewItem(OrderInquestPage3.SCENE_LOSS, getSceneLoss(), ""));
		list.add(new ReviewItem(OrderInquestPage3.SCENE_DISPOSE, getSceneDispose(), ""));
		list.add(new ReviewItem(OrderInquestPage3.CASE_PROPERTY, getCaseProperty(), ""));
		list.add(new ReviewItem(OrderInquestPage3.CASE_UNIT, getCaseUnit(), ""));
		list.add(new ReviewItem(OrderInquestPage3.CASE_METHOD, getCaseMethod(), ""));
		list.add(new ReviewItem(OrderInquestPage3.CASE_FEATURE, getCaseFeature(), ""));
		list.add(new ReviewItem(OrderInquestPage3.CASE_TOOL, getCaseTool(), ""));
		list.add(new ReviewItem(OrderInquestPage3.OTHER_INFO, getOtherInfo(), ""));
		return list;
	}

	public void setReviewItems(ArrayList<ReviewItem> reviewItems) {
		setTime("" + System.currentTimeMillis());
		for (int i = 0; i < reviewItems.size(); i++) {
			L.e(reviewItems.get(i).getTitle(), "" + reviewItems.get(i).getDisplayValue());
			if (OrderInquestPage1.CASE_TIME.equals(reviewItems.get(i).getTitle())) {
				setCaseTime(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage1.CASE_ADDRESS.equals(reviewItems.get(i).getTitle())) {
				setCaseAddress(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage1.CASE_INFO.equals(reviewItems.get(i).getTitle())) {
				setCaseInfo(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage2.INQUEST_TIME.equals(reviewItems.get(i).getTitle())) {
				setInquestTime(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage2.INQUEST_END_TIME.equals(reviewItems.get(i).getTitle())) {
				setInquestEndTime(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage2.INQUEST_ADDRESS.equals(reviewItems.get(i).getTitle())) {
				setInquestAddress(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage2.INQUEST_LEADER.equals(reviewItems.get(i).getTitle())) {
				setInquestLeader(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage2.INQUEST_MEMBER.equals(reviewItems.get(i).getTitle())) {
				setInquestMember(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.SCENE_LOSS.equals(reviewItems.get(i).getTitle())) {
				setSceneLoss(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.SCENE_DISPOSE.equals(reviewItems.get(i).getTitle())) {
				setSceneDispose(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.CASE_PROPERTY.equals(reviewItems.get(i).getTitle())) {
				setCaseProperty(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.CASE_UNIT.equals(reviewItems.get(i).getTitle())) {
				setCaseUnit(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.CASE_METHOD.equals(reviewItems.get(i).getTitle())) {
				setCaseMethod(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.CASE_FEATURE.equals(reviewItems.get(i).getTitle())) {
				setCaseFeature(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.CASE_TOOL.equals(reviewItems.get(i).getTitle())) {
				setCaseTool(reviewItems.get(i).getDisplayValue());
			} else if (OrderInquestPage3.OTHER_INFO.equals(reviewItems.get(i).getTitle())) {
				setOtherInfo(reviewItems.get(i).getDisplayValue());
			}
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCaseTime() {
		return caseTime;
	}

	public void setCaseTime(String caseTime) {
		this.caseTime = caseTime;
	}

	public String getCaseAddress() {
		return caseAddress;
	}

	public void setCaseAddress(String caseAddress) {
		this.caseAddress = caseAddress;
	}

	public String getCaseInfo() {
		return caseInfo;
	}

	public void setCaseInfo(String caseInfo) {
		this.caseInfo = caseInfo;
	}

	public String getInquestTime() {
		return inquestTime;
	}

	public void setInquestTime(String inquestTime) {
		this.inquestTime = inquestTime;
	}

	public String getInquestEndTime() {
		return inquestEndTime;
	}

	public void setInquestEndTime(String inquestEndTime) {
		this.inquestEndTime = inquestEndTime;
	}

	public String getInquestAddress() {
		return inquestAddress;
	}

	public void setInquestAddress(String inquestAddress) {
		this.inquestAddress = inquestAddress;
	}

	public String getInquestLeader() {
		return inquestLeader;
	}

	public void setInquestLeader(String inquestLeader) {
		this.inquestLeader = inquestLeader;
	}

	public String getInquestMember() {
		return inquestMember;
	}

	public void setInquestMember(String inquestMember) {
		this.inquestMember = inquestMember;
	}

	public String getSceneLoss() {
		return sceneLoss;
	}

	public void setSceneLoss(String sceneLoss) {
		this.sceneLoss = sceneLoss;
	}

	public String getSceneDispose() {
		return sceneDispose;
	}

	public void setSceneDispose(String sceneDispose) {
		this.sceneDispose = sceneDispose;
	}

	public String getCaseProperty() {
		return caseProperty;
	}

	public void setCaseProperty(String caseProperty) {
		this.caseProperty = caseProperty;
	}

	public String getCaseUnit() {
		return caseUnit;
	}

	public void setCaseUnit(String caseUnit) {
		this.caseUnit = caseUnit;
	}

	public String getCaseMethod() {
		return caseMethod;
	}

	public void setCaseMethod(String caseMethod) {
		this.caseMethod = caseMethod;
	}

	public String getCaseFeature() {
		return caseFeature;
	}

	public void setCaseFeature(String caseFeature) {
		this.caseFeature = caseFeature;
	}

	public String getCaseTool() {
		return caseTool;
	}

	public void setCaseTool(String caseTool) {
		this.caseTool = caseTool;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

}
