package ys.oa.enity;

import ys.oa.provider.DataProvider;
import android.database.Cursor;
import android.util.Log;

/**
 * 人员查询结果实体
 * @author cuiru.fan
 *
 */
public class QueryPeopleResultEntity extends CollectPeopleEnity{
	
	private String pType;//人员类型

	private String record;//人员犯罪记录
	private String recordType;//犯罪类型
	private String checkType;//检查类型   分为人员检查和非机动车检查
	
	
	public String getCheckType() {
		return checkType;
	}
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getpType() {
		return pType;
	}
	public void setpType(String pType) {
		this.pType = pType;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	
	
	public static QueryPeopleResultEntity setEntity(Cursor cursor) {
		QueryPeopleResultEntity item = new QueryPeopleResultEntity();
		item.setName(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_NAME)));
		item.setCheckType(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_CHECK_TYPE)));
		item.setSex(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_SEX)));
		item.setNation(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_NATION)));
		item.setId(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_ID)));
		item.setBirthday(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_BIRTH)));
//		item.setDocumentId(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_DOCID)));
		item.setContentId(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_CONTENT_ID)));
		item.setInfo(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_REM)));
		item.setAddress(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_ADD)));
//		item.setCollectAddress(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_CADS)));
//		item.setImgKeys(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_IMA_KV)));
		item.setRecord(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_RECORD)));
		item.setRecordType(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_RECORD_TYPE)));
		item.setpType(cursor.getString(cursor.getColumnIndex(DataProvider.PPL_R_TYPE)));
		return item;
	}
	
	@Override
	public String toString() {
		return "QueryPeopleResultEntity [type=" + pType + ", record=" + record
				+ ", recordType=" + recordType + "]";
	}
	
	
	

}
