package ys.oa.enity;

import java.io.Serializable;
import java.util.ArrayList;

import ys.oa.provider.DataProvider;
import ys.oa.util.Constants;
import android.database.Cursor;

/**
 * 车辆信息
 * 
 * @author wf
 * 
 */
@SuppressWarnings("serial")
public class CollectCarEnity extends BaseEntity implements Serializable {

	private String username;
	private String ownerName;//车主名
	private String ownerId;//车主身份证号
	private String carId;//车牌号
	private String time;//时间
	private String address;//地点
	private String vehType;//车辆类型
	private String info;//备注
	private String imgKeys;
	private String idImg;
	private ArrayList<IDInfoEntity> ridersInfo;
	private String lon;//经度
	private String lat;//纬度

	public ArrayList<IDInfoEntity> getRidersInfo() {
		return ridersInfo;
	}

	public void setRidersInfo(ArrayList<IDInfoEntity> ridersInfo) {
		this.ridersInfo = ridersInfo;
	}

	public CollectCarEnity() {
		super();
	}

	public CollectCarEnity(String username, String ownerName, String ownerId,
			String carId, String time, String address, String vehType,
			String info, String imgKeys) {
		super();
		this.username = username;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.carId = carId;
		this.time = time;
		this.address = address;
		this.vehType = vehType;
		this.info = info;
		this.imgKeys = imgKeys;
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

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getVehType() {
		return vehType;
	}

	public void setVehType(String vehType) {
		this.vehType = vehType;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getIdImg() {
		return idImg;
	}

	public void setIdImg(String idImg) {
		this.idImg = idImg;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public static CollectCarEnity setEntity(Cursor cursor) {
		CollectCarEnity item = new CollectCarEnity();
		ArrayList<IDInfoEntity> riders = new ArrayList<IDInfoEntity>();
//		item.setUsername(Constants.USER_ID);
		String carType = cursor.getString(cursor.getColumnIndex(DataProvider.VEH_TYPE));
		item.setVehType(carType);
		String nameInfo = cursor.getString(cursor.getColumnIndex(DataProvider.VEH_OWNWE_NAME));
		String idInfo = cursor.getString(cursor.getColumnIndex(DataProvider.VEH_OWNER_ID));
		String imgInfos = cursor.getString(cursor.getColumnIndex(DataProvider.VEH_IMA_KV));
		if(carType.equals(Constants.NVEH_CKS_ELECTROMOBILE)) {
			if(nameInfo != null && nameInfo.contains(",")) {
				String[] names = nameInfo.split(",");
				String[] ids = idInfo.split(",");
				String[] imgs = imgInfos.split(",");
				item.setOwnerName(nameInfo.split(",")[0]);
				item.setOwnerId(ids[0]);
				item.setImgKeys(imgs[0]);
				item.setIdImg(imgs[1]);
				for(int i=1;i<names.length;i++) {
					IDInfoEntity entity = new IDInfoEntity();
					entity.setName(names[i]);
					entity.setId(ids[i]);
					entity.setImgPath(imgs[i+1]);
					riders.add(entity);
				}
				item.setRidersInfo(riders);
				
			}else {
				String[] imgs = imgInfos.split(",");
				item.setOwnerName(nameInfo);
				item.setOwnerId(idInfo);
				item.setImgKeys(imgs[0]);
				item.setIdImg(imgs[1]);
			}
		}else if(carType.equals(Constants.NVEH_CKS_BICYCLE)) {
				String[] imgs = imgInfos.split(",");
				item.setOwnerName(nameInfo);
				item.setOwnerId(idInfo);
				item.setImgKeys(imgs[0]);
				item.setIdImg(imgs[1]);
		}else if(carType.equals(Constants.VEH_CKS_CAR) || carType.equals(Constants.VEH_CKS_MOTORCYCLE)){
			if(nameInfo != null && nameInfo.contains(",")) {
				String[] names = nameInfo.split(",");
				String[] ids = idInfo.split(",");
				String[] imgs = imgInfos.split(",");
				item.setOwnerName(nameInfo.split(",")[0]);
				item.setOwnerId(ids[0]);
				item.setImgKeys(imgInfos);
				item.setIdImg(imgs[4]);
				for(int i=1;i<names.length;i++) {
					IDInfoEntity entity = new IDInfoEntity();
					entity.setName(names[i]);
					entity.setId(ids[i]);
					entity.setImgPath(imgs[i+4]);
					riders.add(entity);
				}
				item.setRidersInfo(riders);
				
			}else {
				String[] imgs = imgInfos.split(",");
				item.setOwnerName(nameInfo);
				item.setOwnerId(idInfo);
				item.setImgKeys(imgInfos);
				item.setIdImg(imgs[4]);
			}
		}else if(carType.equals(Constants.GOODS_CKS)) {
			if(imgInfos.contains(",")) {
				String[] imgs = imgInfos.split(",");
				item.setImgKeys(imgs[0]);
				item.setIdImg(imgs[1]);
			}else {
				item.setOwnerName(nameInfo);
				item.setOwnerId(idInfo);
				item.setImgKeys(imgInfos);
				item.setIdImg(null);
			}
		}
		item.setTime(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_TIME)));
		item.setCarId(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_CAR_ID)));
		item.setTime(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_TIME)));
		item.setAddress(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_ADDRESS)));
		item.setContentId(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_CONTENT_ID)));
		item.setLon(cursor.getString(cursor.getColumnIndex(DataProvider.LON)));
		item.setLat(cursor.getString(cursor.getColumnIndex(DataProvider.LAT)));
		item.setInfo(cursor.getString(cursor.getColumnIndex(DataProvider.VEH_INFO)));
		return item;
	}

	@Override
	public String toString() {
		return "CollectCarEnity [username=" + username + ", ownerName="
				+ ownerName + ", ownerId=" + ownerId + ", carId=" + carId
				+ ", time=" + time + ", address=" + address + ", vehType="
				+ vehType + ", info=" + info + ", imgKeys=" + imgKeys
				+ ", idImg=" + idImg + ", ridersInfo=" + ridersInfo + ", lon="
				+ lon + ", lat=" + lat + "]";
	}

}
