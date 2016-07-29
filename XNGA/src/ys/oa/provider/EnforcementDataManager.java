package ys.oa.provider;

import java.util.ArrayList;

import ys.oa.activity.CollectCarActivity;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.CollectGoodEnity;
import ys.oa.enity.CollectPeopleEnity;
import ys.oa.enity.QueryPeopleResultEntity;
import ys.oa.enity.QueryVehResultEntity;
import ys.oa.util.Constants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author cuiru.fan
 *
 */
public class EnforcementDataManager {
	
	public static EnforcementDataManager enforcementDataManager;
	private Context context;
	private String name;
	private String tabName;
	private String pplTabName;
	private String vehTabName;
	private String goodsTabName;
	private String resultTabName;
	private String pplResultTabName = null;
	private String vehResultTabName = null;
	
	private String uriTemp = "content://" + DataProvider.AUTHORITY;
	private Uri dataUrl;
	StringBuffer enforcement_buffer = new StringBuffer();
	StringBuffer ppl_buffer = null;
	StringBuffer veh_buffer = null;//机动车非机动车数据表
	StringBuffer goods_buffer = null;
	StringBuffer query_result_buffer = null;
	public StringBuffer getQuery_result_buffer() {
		return query_result_buffer;
	}

	StringBuffer query_ppl_result_buffer = null;
	StringBuffer query_veh_result_buffer = null;
	
	private static DataProvider dataProvider;
	public EnforcementDataManager(Context context) {
		this.context = context;
		enforcement_buffer = new StringBuffer();
		ppl_buffer = new StringBuffer();
		veh_buffer = new StringBuffer();
		goods_buffer = new StringBuffer();
		query_result_buffer = new StringBuffer();
		query_ppl_result_buffer = new StringBuffer();
		query_veh_result_buffer = new StringBuffer();
		setName();
        dataProvider = DataProvider.getInstance(context);
	}
	
	public static EnforcementDataManager getInstance(Context context) {
		if(enforcementDataManager == null) {
			enforcementDataManager = new EnforcementDataManager(context);
		}
		return enforcementDataManager; 
	}
	
	
	public void setName() {
		this.name = Constants.USER_ID;
		tabName =  "enforcement_"+name;
		pplTabName = "ppl_"+name;
		vehTabName = "veh_"+name;
		goodsTabName = "goods_"+name;
		resultTabName = "R_"+name;
		pplResultTabName = "ppl_" + name+"_R";
		vehResultTabName = "veh_"+name+"_R";
		
        enforcement_buffer.append(uriTemp +"/enforcement/"+tabName);
        ppl_buffer.append(uriTemp + "/ppl_cks/"+pplTabName);
        veh_buffer.append(uriTemp+"/veh_cks/"+ vehTabName);
        goods_buffer.append(uriTemp+"/goods_cks/"+ goodsTabName);
        query_result_buffer.append(uriTemp+"/query_result_list/"+resultTabName);
        query_ppl_result_buffer.append(uriTemp + "/query_ppl_r/"+pplResultTabName);
        query_veh_result_buffer.append(uriTemp + "/query_veh_r/"+vehResultTabName);
        
	}
	
	/**
	 * 插入 路面稽查表
	 * @param entity
	 */
	public void insertEnforcementItem(SQLDataEntity entity) {
//			dataUrl = Uri.parse(enforcement_buffer.toString() + "&" + entity.getID().toString());
			dataUrl = Uri.parse(enforcement_buffer.toString());
		  	ContentResolver resolver = context.getContentResolver();
	        ContentValues values = new ContentValues();
	        values.put(DataProvider.ITEM_TYPE,entity.getCheckType());
	        values.put(DataProvider.ITEM_NAME,entity.getName());
	        values.put(DataProvider.ITEM_TIME,entity.getTime());
	        values.put(DataProvider.ITEM_CONTENT_ID,entity.getContentId());
	        values.put(DataProvider.ITEM_SEX,entity.getSex());
	        values.put(DataProvider.ITEM_ID,entity.getID());
	        values.put(DataProvider.ITEM_VEH_ID,entity.getCarId());
	        values.put(DataProvider.ITEM_GOODS_NAME,entity.getGoodsName());
	        values.put(DataProvider.ITEM_ID_IMAGE_PATH,entity.getIDImagePath());
	        values.put(DataProvider.ITEM_BIRTHDAY,entity.getBirthday());
	        values.put(DataProvider.ITEM_CREATE_DATE,entity.getCreateDate());
	        resolver.insert(dataUrl, values);
	}
	
	
	private Uri pplInsertUrl ;
	/**
	 * 插入人员检查表
	 * @param entity
	 */
	public void insertPPLItem(CollectPeopleEnity entity) {
//		 Uri pplInsertUrl = Uri.parse(ppl_buffer.toString() + "&" + entity.getId().toString());
		Uri pplInsertUrl = Uri.parse(ppl_buffer.toString());
	  	ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DataProvider.PPL_NAME,entity.getName());
        values.put(DataProvider.PPL_SEX,entity.getSex());
        values.put(DataProvider.PPL_NATION,entity.getNation());
        values.put(DataProvider.PPL_BIRTH,entity.getBirthday());
        values.put(DataProvider.PPL_ADD,entity.getAddress());
        values.put(DataProvider.PPL_ID,entity.getId());
        values.put(DataProvider.PPL_IMA_KV,entity.getImgKeys());
        values.put(DataProvider.PPL_DOCID,entity.getDocumentId());
        values.put(DataProvider.PPL_CONTENT_ID,entity.getContentId());
        values.put(DataProvider.PPL_REM,entity.getInfo());
        values.put(DataProvider.PPL_CADS, entity.getCollectAddress());
        resolver.insert(pplInsertUrl, values);
	}
	
	/**
	 * 插入车辆检查表
	 * @param entity
	 */
	public void insertVEHItem(CollectCarEnity entity) {
//		Uri vehInsertUrl = Uri.parse(veh_buffer + "&" + entity.getCarId().toString());
		Uri vehInsertUrl = Uri.parse(veh_buffer.toString());
	  	ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DataProvider.VEH_OWNWE_NAME,entity.getOwnerName());
        values.put(DataProvider.VEH_CONTENT_ID,entity.getContentId());
        values.put(DataProvider.VEH_OWNER_ID,entity.getOwnerId());
        values.put(DataProvider.VEH_CAR_ID,entity.getCarId());
        values.put(DataProvider.VEH_ADDRESS,entity.getAddress());
        values.put(DataProvider.VEH_INFO,entity.getInfo());
        values.put(DataProvider.VEH_TIME,entity.getTime());
        values.put(DataProvider.VEH_IMA_KV,entity.getImgKeys());
        values.put(DataProvider.VEH_TYPE,entity.getVehType());
        values.put(DataProvider.LON,entity.getLon());
        values.put(DataProvider.LAT,entity.getLat());
        resolver.insert(vehInsertUrl, values);
	}
	
	/**
	 * 插入物品检查表
	 * @param entity
	 */
	public void insertGoodsItem(CollectGoodEnity entity) {
//		Uri goodsInsertUrl = Uri.parse(goods_buffer + "&" + entity.getId().toString());
		Uri goodsInsertUrl = Uri.parse(goods_buffer.toString());
	  	ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DataProvider.GOODS_OWNWE_NAME,entity.getOwnerName());
        values.put(DataProvider.GOODS_CONTENT_ID,entity.getContentId());
        values.put(DataProvider.GOODS_NAME,entity.getGoodName());
        values.put(DataProvider.GOODS_OWNER_ID,entity.getId());
        values.put(DataProvider.GOODS_ADDRESS,entity.getCollectAddress());
        values.put(DataProvider.GOODS_DOCID,entity.getDocumentId());
        values.put(DataProvider.GOODS_INFO,entity.getInfo());
        values.put(DataProvider.GOODS_TIME,entity.getTime());
        resolver.insert(goodsInsertUrl, values);
	}
	
	private Uri queryResultUrl = null;
	/**
	 * @param entity
	 */
	public void insertQueryResultListItem(SQLDataEntity entity) {
//		 Uri pplInsertUrl = Uri.parse(ppl_buffer.toString() + "&" + entity.getId().toString());
		queryResultUrl = Uri.parse(query_result_buffer.toString());
	  	ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DataProvider.ITEM_TYPE,entity.getCheckType());
        values.put(DataProvider.ITEM_NAME,entity.getName());
        values.put(DataProvider.ITEM_TIME,entity.getTime());
        values.put(DataProvider.ITEM_CONTENT_ID,entity.getContentId());
        values.put(DataProvider.ITEM_SEX,entity.getSex());
        values.put(DataProvider.ITEM_ID,entity.getID());
        values.put(DataProvider.ITEM_VEH_ID,entity.getCarId());
        values.put(DataProvider.ITEM_GOODS_NAME,entity.getGoodsName());
        values.put(DataProvider.ITEM_BIRTHDAY,entity.getBirthday());
        values.put(DataProvider.ITEM_P_TYPE,entity.getpType());
        values.put(DataProvider.ITEM_VEH_RECORD,entity.getVehRecord());
        values.put(DataProvider.ITEM_CREATE_DATE,entity.getCreateDate());
        resolver.insert(queryResultUrl, values);
        context.getContentResolver().notifyChange(queryResultUrl, null);
	}
	
	/**
	 * 插入人员检查结果到人员检查结果表中
	 * @param entity
	 */
	public void insertQueryPPLResultItem(QueryPeopleResultEntity entity) {
//		 Uri pplInsertUrl = Uri.parse(ppl_buffer.toString() + "&" + entity.getId().toString());
		Uri pplInsertUrl = Uri.parse(query_ppl_result_buffer.toString());
	  	ContentResolver resolver = context.getContentResolver();
       ContentValues values = new ContentValues();
       values.put(DataProvider.PPL_R_NAME,entity.getName());
       values.put(DataProvider.PPL_R_CHECK_TYPE,entity.getCheckType());
       values.put(DataProvider.PPL_R_SEX,entity.getSex());
       values.put(DataProvider.PPL_R_NATION,entity.getNation());
       values.put(DataProvider.PPL_R_BIRTH,entity.getBirthday());
       values.put(DataProvider.PPL_R_ADD,entity.getAddress());
       values.put(DataProvider.PPL_R_ID,entity.getId());
       values.put(DataProvider.PPL_R_CONTENT_ID,entity.getContentId());
       values.put(DataProvider.PPL_R_REM,entity.getInfo());
       values.put(DataProvider.PPL_R_RECORD, entity.getRecord());
       values.put(DataProvider.PPL_R_TYPE, entity.getpType());
       values.put(DataProvider.PPL_R_RECORD_TYPE, entity.getRecordType());
       resolver.insert(pplInsertUrl, values);
	}
	
	public void insertQueryVehResultItem(QueryVehResultEntity entity) {
	  Uri pplInsertUrl = Uri.parse(query_veh_result_buffer.toString());
	  ContentResolver resolver = context.getContentResolver();
      ContentValues values = new ContentValues();
      values.put(DataProvider.VEH_R_NAME,entity.getOwnerName());
      values.put(DataProvider.VEH_R_CHECK_TYPE,entity.getCheckType());
      values.put(DataProvider.VEH_R_CAR_ID,entity.getCarId());
      values.put(DataProvider.VEH_R_OWNERID,entity.getOwnerId());
      values.put(DataProvider.VEH_R_TYPE,entity.getVehType());
      values.put(DataProvider.VEH_R_TYPR_NAME,entity.getTypeName());
      values.put(DataProvider.VEH_COLOR,entity.getVehColor());
      values.put(DataProvider.VEH_R_CONTENT_ID,entity.getContentId());
      values.put(DataProvider.VEH_ENGINE,entity.getVehEngine());
      values.put(DataProvider.VEH_R_RECORD, entity.getVehRecord());
      values.put(DataProvider.VEH_VIN, entity.getVehVIN());
      values.put(DataProvider.VEH_R_INFO, entity.getInfo());
      resolver.insert(pplInsertUrl, values);
	}
	
	/**
	 * 通过uuid查询该条人员检查详情
	 * @param ID
	 * @return
	 */
	public CollectPeopleEnity queryPPLById(String uuid) {
		return dataProvider.queryPPL(pplTabName, uuid);
	}
	
	/**
	 * 通过uuid查询车辆检查详情
	 * @param uuid
	 * @return
	 */
	public CollectCarEnity queryVehById(String uuid) {
		return dataProvider.queryVeh(vehTabName, uuid);
	}
	
	/**
	 * 查询本地所有路面稽查列表
	 * @return
	 */
	public ArrayList<SQLDataEntity> queryEnforcementList() {
		ArrayList<SQLDataEntity> dataList = dataProvider.getEnforcementList(tabName);
		return dataList;
		
	}
	
	/**
	 * 查询结果列表
	 * @return
	 */
	public ArrayList<SQLDataEntity> queryResultList() {
		ArrayList<SQLDataEntity> dataList = dataProvider.getQueryResultList(resultTabName);
		return dataList;
	}
	
	public QueryPeopleResultEntity queryResultItem(String contentID) {
		
		QueryPeopleResultEntity dataList = dataProvider.queryPeopleResultItem(pplResultTabName,contentID);
		return dataList;
	}
	

	public QueryVehResultEntity queryVehResultItem(String contentID) {
		
		QueryVehResultEntity dataList = dataProvider.queryVehResultItem(vehResultTabName,contentID);
		return dataList;
	}
	
	
	public void cleanAllDate () {
		dataProvider = DataProvider.getInstance(context);
		dataProvider.cleanDate();
		release();
	}
	
	public void release() {
		dataProvider.release();
		enforcementDataManager = null;
	}

	

}
