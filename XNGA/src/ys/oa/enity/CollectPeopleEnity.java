package ys.oa.enity;

import java.io.Serializable;

import android.database.Cursor;
import android.util.Log;

import ys.oa.provider.DataProvider;
import ys.oa.provider.SQLDataEntity;

/**
 * 人员信息
 * 
 * @author wf
 * 
 */
@SuppressWarnings("serial")
public class CollectPeopleEnity extends BaseEntity implements Serializable {

	private String username;
	private String name;
	private String nation;
	private String sex;
	private String birthday;
	private String id;
	private String address;
	private String info;
	private String documentId;
	private String imgKeys;
	private String collectAddress;
	

	public CollectPeopleEnity() {
		super();
	}

	public CollectPeopleEnity(String username, String name, String nation,
			String sex, String birthday, String id, String address,
			String info, String documentId, String imgKeys,
			String collectAddress) {
		super();
		this.username = username;
		this.name = name;
		this.nation = nation;
		this.sex = sex;
		this.birthday = birthday;
		this.id = id;
		this.address = address;
		this.info = info;
		this.documentId = documentId;
		this.imgKeys = imgKeys;
		this.collectAddress = collectAddress;
	}

	public String getCollectAddress() {
		return collectAddress;
	}

	public void setCollectAddress(String collectAddress) {
		this.collectAddress = collectAddress;
	}

	public String getImgKeys() {
		return imgKeys;
	}

	public void setImgKeys(String imgKeys) {
		this.imgKeys = imgKeys;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public static CollectPeopleEnity setEntity(Cursor cursor) {
		CollectPeopleEnity item = new CollectPeopleEnity();
		item.setName(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_NAME)));
		item.setSex(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_SEX)));
		item.setNation(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_NATION)));
		item.setId(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_ID)));
		item.setBirthday(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_BIRTH)));
		item.setDocumentId(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_DOCID)));
		item.setContentId(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_CONTENT_ID)));
		item.setInfo(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_REM)));
		item.setAddress(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_ADD)));
		item.setCollectAddress(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_CADS)));
		item.setImgKeys(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_IMA_KV)));
		return item;
	}

	@Override
	public String toString() {
		return "CollectPeopleEnity [username=" + username + ", name=" + name
				+ ", nation=" + nation + ", sex=" + sex + ", birthday="
				+ birthday + ", id=" + id + ", address=" + address + ", info="
				+ info + ", documentId=" + documentId + ", imgKeys=" + imgKeys
				+ ", collectAddress=" + collectAddress + "]";
	}
}
