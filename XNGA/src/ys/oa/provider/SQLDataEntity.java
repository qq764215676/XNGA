package ys.oa.provider;

import ys.oa.enity.BaseEntity;
import android.database.Cursor;

/**
 * @author cuiru.fan
 *
 */
public class SQLDataEntity extends BaseEntity{
	
	
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 生日
	 */
	private String birthday;
	/**
	 * 身份证
	 */
	private String ID;
	
	/**
	 * 身份证正面照存储路径
	 */
	private String IDImagePath;
	
	/**
	 * 创建日期
	 */
	private String createDate;
	
	/**
	 * 车牌号
	 */
	private String carId;
	
	/**
	 * 物品名称
	 */
	private String goodsName;
	
	/**
	 * 所填时间
	 */
	private String time ;
	
	/**
	 * 人员查询结果 ：人员类型
	 */
	private String pType;
	
	/**
	 * 车辆查询结果:车辆类型
	 */
	private String vehRecord;
	
	
	public String getVehRecord() {
		return vehRecord;
	}

	public void setVehRecord(String vehRecord) {
		this.vehRecord = vehRecord;
	}

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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

	
	public void setID(String iD) {
		ID = iD;
	}

	public String getID() {
		return ID;
	}

	public String getIDImagePath() {
		return IDImagePath;
	}

	public void setIDImagePath(String iDImagePath) {
		IDImagePath = iDImagePath;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public static SQLDataEntity setEntity(int type,Cursor cursor) {
		SQLDataEntity item = new SQLDataEntity();
		item.setCheckType(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_TYPE)));
		item.setTime(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_TIME)));
		item.setName(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_NAME)));
		item.setSex(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_SEX)));
		item.setContentId(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_CONTENT_ID)));
		item.setBirthday(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_BIRTHDAY)));
		item.setID(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_ID)));
		item.setCarId(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_VEH_ID)));
		item.setGoodsName(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_GOODS_NAME)));
		if(type == 0) {
			item.setIDImagePath(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_ID_IMAGE_PATH)));
		}else if(type == 1) {
			item.setpType(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_P_TYPE)));
			item.setVehRecord(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_VEH_RECORD)));
		}
		item.setCreateDate(cursor.getString(cursor.getColumnIndex(DataProvider.ITEM_CREATE_DATE)));
    	return item;
	}

	@Override
	public String toString() {
		return "SQLDataEntity [name=" + name + ", sex=" + sex + ", birthday="
				+ birthday + ", ID=" + ID + ", IDImagePath=" + IDImagePath
				+ ", createDate=" + createDate + ", carId=" + carId
				+ ", goodsName=" + goodsName + ", time=" + time + ", pType="
				+ pType + "]";
	}

}
