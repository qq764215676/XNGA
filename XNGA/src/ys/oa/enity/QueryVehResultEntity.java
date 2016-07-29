package ys.oa.enity;

import ys.oa.provider.DataProvider;
import android.database.Cursor;

/**
 * @author cuiru.fan
 * 车辆查询结果实体
 * create at 2016-01-28
 *
 */
public class QueryVehResultEntity extends CollectCarEnity {
	

	/**
	 * 用到了CollectCarEntity 中的以下字段
		private String ownerName;//车主名
		private String ownerId;//车主身份证号
		private String carId;//车牌号
		private String info;//备注
		BaseEntity 
		private String contentId;
		private String checkType;//检查类型
	 */
	private static final long serialVersionUID = 1L;
	
	private String vehType ;//机动车类型
	private String typeName;///车辆型号名称
	private String vehColor;//车辆名称
	private String vehVIN;//车架号
	private String vehEngine ;//发动机号
	private String vehRecord;
	public String getVehType() {
		return vehType;
	}
	public void setVehType(String vehType) {
		this.vehType = vehType;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getVehColor() {
		return vehColor;
	}
	public void setVehColor(String vehColor) {
		this.vehColor = vehColor;
	}
	public String getVehVIN() {
		return vehVIN;
	}
	public void setVehVIN(String vehVIN) {
		this.vehVIN = vehVIN;
	}
	public String getVehEngine() {
		return vehEngine;
	}
	public void setVehEngine(String vehEngine) {
		this.vehEngine = vehEngine;
	}
	public String getVehRecord() {
		return vehRecord;
	}
	public void setVehRecord(String vehRecord) {
		this.vehRecord = vehRecord;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "QueryVehResultEntity [vehType=" + vehType + ", typeName="
				+ typeName + ", vehColor=" + vehColor + ", vehVIN=" + vehVIN
				+ ", vehEngine=" + vehEngine + ", vehRecord=" + vehRecord + "]";
	}
	
	public static QueryVehResultEntity setEntity(Cursor cursor) {
		QueryVehResultEntity item = new QueryVehResultEntity();
		item.setOwnerName(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_NAME)));
		item.setOwnerId(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_OWNERID)));
		item.setCarId(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_CAR_ID)));
		item.setInfo(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_INFO)));
		item.setContentId(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_CONTENT_ID)));
		item.setVehRecord(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_RECORD)));
		item.setCheckType(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_CHECK_TYPE)));
		item.setVehType(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_TYPE)));
		item.setTypeName(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_R_TYPR_NAME)));
		item.setVehVIN(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_VIN)));
		item.setVehEngine(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_ENGINE)));
		item.setVehColor(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_COLOR)));
		return item;
	}
	
	

}
