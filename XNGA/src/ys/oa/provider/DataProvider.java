package ys.oa.provider;

import java.util.ArrayList;

import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.CollectPeopleEnity;
import ys.oa.enity.QueryPeopleResultEntity;
import ys.oa.enity.QueryVehResultEntity;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author cuiru.fan 
 *
 */
public class DataProvider extends ContentProvider {
	
	public static final String AUTHORITY = "ys.nlga.activity.dataps";
	private static final String TAG = "DataProvider";
	
	private static Context context;
	protected UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static DataProvider provider;
	private static DatabaseHelper databaseHelper; 
	private static SQLiteDatabase database;
	
	private static final int MATCH_TABLE_BY_NAME = 0;
	private static final int MATCH_ENFORCEMENT_TABLE_NAME = 1;//路面稽查总表
	private static final int MATCH_PPL_CKS = 2;
	private static final int MATCH_VEH_CKS = 3;
	private static final int MATCH_GOODS_CKS = 4;
	private static final int MATCH_QUERY_RESULT_LIST = 5;
	private static final int MATCH_QUERY_PPL_RESULT = 6;
	private static final int MATCH_QUERY_VEH_RESULT = 7;
	
	/*
	 * 路面稽查本地数据库字段
	 */
	public static String ITEM_TYPE = "type";
	public static String ITEM_NAME = "name";
	public static String ITEM_SEX = "sex";
	public static String ITEM_TIME = "time";
	public static String ITEM_BIRTHDAY = "birthday";
	public static String ITEM_ID = "ID";
	public static String ITEM_ID_IMAGE_PATH = "ID_image_path";
	public static String ITEM_VEH_ID = "veh_id";
	public static String ITEM_GOODS_NAME= "goods_name";
	public static String ITEM_CREATE_DATE = "create_date";
	public static String ITEM_CONTENT_ID = "contentId";
	public static String ITEM_P_TYPE = "people_record_type";
	public static String ITEM_VEH_RECORD = "veh_record";
	
	/*
	 * 人员检查数据字段
	 */
	public static String PPL_NAME = "username";
	public static String PPL_TIME = "time";
	public static String PPL_NATION = "nation";
	public static String PPL_SEX = "sex";
	public static String PPL_BIRTH = "birthday";
	public static String PPL_ID = "id";
	public static String PPL_ADD = "address";
	public static String PPL_REM ="info";
	public static String PPL_IMA_KV = "imgKeys";
	public static String PPL_DOCID = "documentId";
	public static String PPL_CONTENT_ID= "contentId";
	public static String PPL_CADS = "collectAddress";
	
	/*
	 * 车辆检查数据字段
	 */
	public static String VEH_OWNWE_NAME = "ownerName";
	public static String VEH_OWNER_ID = "ownerID";
	public static String VEH_CAR_ID = "carId";
	public static String VEH_TIME = "time";
	public static String VEH_ADDRESS = "address";
	public static String VEH_TYPE = "vehType";//车辆类型
	public static String VEH_CONTENT_ID= "contentId";
	public static String VEH_INFO ="info";
	public static String VEH_IMA_KV = "imgKeys";
	public static String LON = "lon";
	public static String LAT = "lat";
	public static String VEH_CADS = "collectAddress";
	
	/*
	 * 物品检查字段
	 */
	public static String GOODS_OWNWE_NAME = "ownerName";
	public static String GOODS_OWNER_ID = "ownerId";
	public static String GOODS_NAME = "goodName";
	public static String GOODS_TIME = "time";
	public static String GOODS_INFO ="info";
	public static String GOODS_DOCID = "documentId";
	public static String GOODS_CONTENT_ID= "contentId";
	public static String GOODS_ADDRESS = "collectAddress";
	
	/*
	 * 人员检查结果表字段
	 */
	public static String PPL_R_CONTENT_ID= "contentId";//请求表id
	public static String PPL_R_NAME = "username";//姓名
	public static String PPL_R_CHECK_TYPE = "check_type";
	public static String PPL_R_NATION = "nation";//名族
	public static String PPL_R_SEX = "sex";//性别
	public static String PPL_R_BIRTH = "birthday";//生日
	public static String PPL_R_ID = "id";//身份证号
	public static String PPL_R_TYPE = "type";//人员类型
	public static String PPL_R_ADD = "address";//居住地址
	public static String PPL_R_RECORD = "record";//犯罪记录
	public static String PPL_R_RECORD_TYPE = "record_type";//犯罪类型
	public static String PPL_R_REM ="info";//备注
	/**
	 * 
	 * 
	 */
	public static String VEH_R_CONTENT_ID= "contentId";
	public static String VEH_R_NAME = "ownerName";
	public static String VEH_R_OWNERID = "ownerId";
	public static String VEH_R_CAR_ID = "carId";
	public static String VEH_R_CHECK_TYPE = "checkType";
	public static String VEH_R_TYPE = "vehType";
	public static String VEH_R_TYPR_NAME = "typeName";
	public static String VEH_COLOR = "vehColor";
	public static String VEH_VIN = "vehVIN";
	public static String VEH_ENGINE = "vehEngine";
	public static String VEH_R_RECORD = "veh_record";
	public static String VEH_R_INFO ="info";//备注

	public static DataProvider getInstance(Context context) {
		if(provider == null) {
			provider = new DataProvider(context);
		}
		return provider;
	}
	
	public DataProvider() {
		
	}
	
	private DataProvider (Context context) {
		DataProvider.context = context;
	}
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		context = getContext();
		databaseHelper = DatabaseHelper.getInstance(context);
		database = databaseHelper.getWritableDatabase();
		uriMatcher.addURI(AUTHORITY, "sqlite_master", MATCH_TABLE_BY_NAME);
		uriMatcher.addURI(AUTHORITY, "enforcement/*", MATCH_ENFORCEMENT_TABLE_NAME);
		uriMatcher.addURI(AUTHORITY, "ppl_cks/*", MATCH_PPL_CKS);
		uriMatcher.addURI(AUTHORITY, "veh_cks/*", MATCH_VEH_CKS);
		uriMatcher.addURI(AUTHORITY, "goods/*", MATCH_GOODS_CKS);
		uriMatcher.addURI(AUTHORITY, "query_result_list/*", MATCH_QUERY_RESULT_LIST);
		uriMatcher.addURI(AUTHORITY, "query_ppl_r/*", MATCH_QUERY_PPL_RESULT);
		uriMatcher.addURI(AUTHORITY, "query_veh_r/*", MATCH_QUERY_VEH_RESULT);
	
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		switch (uriMatcher.match(uri)) {
		case MATCH_ENFORCEMENT_TABLE_NAME:
			String tabName = uri.getLastPathSegment().toString();
			cursor = database.query(tabName,null, null, null, null, null,null);
			break;
		case MATCH_PPL_CKS:
			String tN = uri.getLastPathSegment().toString().split("&")[0];
			String contentId =  uri.getLastPathSegment().toString().split("&")[1];
			cursor = database.query(tN, null, "contentId=?", new String[] {contentId}, null, null, null);
			break;
		case MATCH_VEH_CKS:
			String t = uri.getLastPathSegment().toString().split("&")[0];
			String _contentId =  uri.getLastPathSegment().toString().split("&")[1];
			cursor = database.query(t, null, "contentId=?", new String[] {_contentId}, null, null, null);
			break;
		case MATCH_GOODS_CKS:
			break;
		case MATCH_QUERY_RESULT_LIST:
			String tab = uri.getLastPathSegment().toString();
			cursor = database.query(tab,null, null, null, null, null,null);
			break;
		case MATCH_QUERY_PPL_RESULT:
			String info = uri.getLastPathSegment().toString();
			String tb = info.split("&")[0];
			String id = info.split("&")[1];
			cursor = database.query(tb, null, "contentId=?", new String[] {id}, null, null, null);
			break;
		case MATCH_QUERY_VEH_RESULT:
			String vehInfo = uri.getLastPathSegment().toString();
			String vehTb = vehInfo.split("&")[0];
			String vehId = vehInfo.split("&")[1];
			cursor = database.query(vehTb, null, "contentId=?", new String[] {vehId}, null, null, null);
			break;
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		database = databaseHelper.getWritableDatabase();
		long id = -1;
		switch(uriMatcher.match(uri)) {
		case MATCH_ENFORCEMENT_TABLE_NAME:
			id = createEnforcementItemOrInsert(MATCH_ENFORCEMENT_TABLE_NAME,uri,values,database);
			break;
		case MATCH_PPL_CKS:
			id = createEnforcementItemOrInsert(MATCH_PPL_CKS,uri,values,database);
			break;
		case MATCH_VEH_CKS:
			id = createEnforcementItemOrInsert(MATCH_VEH_CKS,uri,values,database);
			break;
		case MATCH_GOODS_CKS:
			id = createEnforcementItemOrInsert(MATCH_GOODS_CKS,uri,values,database);
			break;
		case MATCH_QUERY_RESULT_LIST:
			id = createEnforcementItemOrInsert(MATCH_QUERY_RESULT_LIST,uri,values,database);
			break;
		case MATCH_QUERY_PPL_RESULT:
			id = createEnforcementItemOrInsert(MATCH_QUERY_PPL_RESULT, uri, values, database);
			break;
		case MATCH_QUERY_VEH_RESULT:
			id = createEnforcementItemOrInsert(MATCH_QUERY_VEH_RESULT, uri, values, database);
			break;
		}
	
		return ContentUris.withAppendedId(uri, id);
	}
	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int deleteRows = -1;
		switch(uriMatcher.match(uri)) {
		case MATCH_PPL_CKS:
			break;
		case MATCH_VEH_CKS:
			break;
		case MATCH_GOODS_CKS:
			break;
		}
		return deleteRows;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {	
		int updateRows = -1;
		switch(uriMatcher.match(uri)) {
		case MATCH_PPL_CKS:
			createEnforcementItemOrInsert(MATCH_PPL_CKS,uri,values,database);
			break;
		case MATCH_VEH_CKS:
			break;
		case MATCH_GOODS_CKS:
			break;
		}
		return updateRows;
	}
	
	
	private long createEnforcementItemOrInsert(int matchCode,Uri uri, ContentValues values, SQLiteDatabase mSqLiteDatabase) {
		databaseHelper = DatabaseHelper.getInstance(context);
		long id = -1;
		String tableName = uri.getLastPathSegment().toString();
//		String tableName = info.split("&")[0].toString();
//		String contact_id = info.split("&")[1].toString();
		mSqLiteDatabase.beginTransaction();
		try {
			Cursor cursor = databaseHelper.getWritableDatabase().query("sqlite_master",new String[]{"name"},"name = ?",new String[] {tableName},null,null,null);
			if(!cursor.moveToNext()) {
				mSqLiteDatabase.execSQL(getCreateTableStatement(matchCode,tableName));
			}
//			Cursor c = databaseHelper.getWritableDatabase().query(tableName, new String[]{"ID"}, "ID = ?", new String[]{contact_id}, null, null, null);
//			if(!c.moveToNext()) {
				id = mSqLiteDatabase.insert(tableName,null,values);
//			}
//			else {
//				id = mSqLiteDatabase.update(tableName, values, "ID = ?",new String[]{contact_id});
//			}
			mSqLiteDatabase.setTransactionSuccessful();
			}catch(Exception e) {
				
			}finally {
				mSqLiteDatabase.endTransaction();
			}
	
		return id;
	}
	
	private String getCreateTableStatement(int matchCode,String tableName) {
		StringBuffer buffer =null;
		switch(matchCode) {
		case MATCH_ENFORCEMENT_TABLE_NAME:
			buffer = new StringBuffer();
			buffer.append("CREATE TABLE IF NOT EXISTS ");
			buffer.append(tableName);
			buffer.append("(_id INTEGER PRIMARY KEY,");
			buffer.append(ITEM_CONTENT_ID + " TEXT NOT NULL,");
			buffer.append(ITEM_TYPE + " TEXT,");
			buffer.append(ITEM_NAME + " TEXT NOT NULL,");
			buffer.append(ITEM_TIME + " TEXT,");
			buffer.append(ITEM_SEX + " TEXT,");
			buffer.append(ITEM_BIRTHDAY + " TEXT,");
			buffer.append(ITEM_ID + " TEXT NOT NULL,");
			buffer.append(ITEM_ID_IMAGE_PATH + " TEXT NOT NULL,");
			buffer.append(ITEM_VEH_ID + " TEXT,");
			buffer.append(ITEM_GOODS_NAME + " TEXT,");
			buffer.append(ITEM_CREATE_DATE + " TEXT NOT NULL);");
			break;
		case MATCH_PPL_CKS:
			buffer = new StringBuffer();
			buffer.append("CREATE TABLE IF NOT EXISTS ");
			buffer.append(tableName);
			buffer.append("(_id INTEGER PRIMARY KEY,");
			buffer.append(PPL_NAME + " TEXT,");
			buffer.append(PPL_CONTENT_ID + " TEXT NOT NULL,");
			buffer.append(PPL_NATION + " TEXT,");
			buffer.append(PPL_SEX + " TEXT NOT NULL,");
			buffer.append(PPL_BIRTH + " TEXT NOT NULL,");
			buffer.append(PPL_ID + " TEXT NOT NULL,");
			buffer.append(PPL_ADD + " TEXT NOT NULL,");
			buffer.append(PPL_REM + " TEXT NOT NULL,");
			buffer.append(PPL_IMA_KV + " TEXT NOT NULL,");
			buffer.append(PPL_DOCID + " TEXT NOT NULL,");
			buffer.append(PPL_CADS + " TEXT NOT NULL);");
			break;
		case MATCH_VEH_CKS:
			buffer = new StringBuffer();
			buffer.append("CREATE TABLE IF NOT EXISTS ");
			buffer.append(tableName);
			buffer.append("(_id INTEGER PRIMARY KEY,");
			buffer.append(VEH_CONTENT_ID + " TEXT NOT NULL,");
			buffer.append(VEH_OWNWE_NAME + " TEXT,");
			buffer.append(VEH_OWNER_ID + " TEXT,");
			buffer.append(VEH_CAR_ID + " TEXT,");//非机动车的该字段为空，机动车该字段不为空
			buffer.append(VEH_TIME + " TEXT NOT NULL,");
			buffer.append(VEH_ADDRESS + " TEXT,");
			buffer.append(VEH_TYPE + " TEXT NOT NULL,");
			buffer.append(VEH_INFO + " TEXT,");
			buffer.append(VEH_IMA_KV + " TEXT NOT NULL,");
			buffer.append(LON + " TEXT NOT NULL,");
			buffer.append(LAT + " TEXT NOT NULL,");
			buffer.append(VEH_CADS + " TEXT);");
			break;
		case MATCH_GOODS_CKS:
			buffer = new StringBuffer();
			buffer.append("CREATE TABLE IF NOT EXISTS ");
			buffer.append(tableName);
			buffer.append("(_id INTEGER PRIMARY KEY,");
			buffer.append(GOODS_CONTENT_ID + " TEXT NOT NULL,");
			buffer.append(GOODS_OWNWE_NAME + " TEXT,");
			buffer.append(GOODS_OWNER_ID + " TEXT NOT NULL,");
			buffer.append(GOODS_NAME + " TEXT NOT NULL,");
			buffer.append(GOODS_TIME + " TEXT NOT NULL,");
			buffer.append(GOODS_INFO + " TEXT NOT NULL,");
			buffer.append(GOODS_DOCID + " TEXT NOT NULL,");
			buffer.append(GOODS_ADDRESS + " TEXT NOT NULL);");
			break;
		case MATCH_QUERY_RESULT_LIST:
			buffer = new StringBuffer();
			buffer.append("CREATE TABLE IF NOT EXISTS ");
			buffer.append(tableName);
			buffer.append("(_id INTEGER PRIMARY KEY,");
			buffer.append(ITEM_TYPE + " TEXT,");
			buffer.append(ITEM_CONTENT_ID + " TEXT NOT NULL,");
			buffer.append(ITEM_NAME + " TEXT NOT NULL,");
			buffer.append(ITEM_SEX + " TEXT,");
			buffer.append(ITEM_TIME + " TEXT,");
			buffer.append(ITEM_BIRTHDAY + " TEXT,");
			buffer.append(ITEM_ID + " TEXT NOT NULL,");
			buffer.append(ITEM_VEH_ID + " TEXT,");
			buffer.append(ITEM_GOODS_NAME + " TEXT,");
			buffer.append(ITEM_P_TYPE + " TEXT,");
			buffer.append(ITEM_VEH_RECORD + " TEXT,");
			buffer.append(ITEM_CREATE_DATE + " TEXT);");
			break;
		case MATCH_QUERY_PPL_RESULT:
			buffer = new StringBuffer();
			buffer.append("CREATE TABLE IF NOT EXISTS ");
			buffer.append(tableName);
			buffer.append("(_id INTEGER PRIMARY KEY,");
			buffer.append(PPL_R_NAME + " TEXT,");
			buffer.append(PPL_R_CHECK_TYPE + " TEXT,");
			buffer.append(PPL_R_CONTENT_ID + " TEXT NOT NULL,");
			buffer.append(PPL_R_SEX + " TEXT NOT NULL,");
			buffer.append(PPL_R_ID + " TEXT,");
			buffer.append(PPL_R_NATION + " TEXT,");
			buffer.append(PPL_R_BIRTH + " TEXT,");
			buffer.append(PPL_R_ADD + " TEXT,");
			buffer.append(PPL_R_TYPE + " TEXT,");
			buffer.append(PPL_R_REM + " TEXT,");
			buffer.append(PPL_R_RECORD + " TEXT,");
			buffer.append(PPL_R_RECORD_TYPE + " TEXT);");
			break;
		case MATCH_QUERY_VEH_RESULT:
			buffer = new StringBuffer();
			buffer.append("CREATE TABLE IF NOT EXISTS ");
			buffer.append(tableName);
			buffer.append("(_id INTEGER PRIMARY KEY,");
			buffer.append(VEH_R_NAME + " TEXT NOT NULL,");
			buffer.append(VEH_R_OWNERID + " TEXT NOT NULL,");
			buffer.append(VEH_R_CONTENT_ID + " TEXT NOT NULL,");
			buffer.append(VEH_R_CAR_ID + " TEXT NOT NULL,");
			buffer.append(VEH_R_CHECK_TYPE + " TEXT,");
			buffer.append(VEH_R_TYPE + " TEXT,");
			buffer.append(VEH_R_TYPR_NAME + " TEXT,");
			buffer.append(VEH_COLOR + " TEXT,");
			buffer.append(VEH_VIN + " TEXT,");
			buffer.append(VEH_ENGINE + " TEXT,");
			buffer.append(VEH_R_RECORD + " TEXT,");
			buffer.append(VEH_R_INFO + " TEXT);");
			break;
		}
		
		 return buffer.toString();
	}

	ArrayList<SQLDataEntity> getEnforcementList(String tableName) {
		 databaseHelper = DatabaseHelper.getInstance(context);
	     ArrayList<SQLDataEntity> list  = new  ArrayList<SQLDataEntity>();
	     Cursor cursor = databaseHelper.getWritableDatabase().query("sqlite_master", new String[]{"name"}, "name = ?", new String[]{tableName}, null, null, null);
	     if (null == cursor || !cursor.moveToFirst()) {
	         if (cursor != null) {
	             cursor.close();
	         }
	         cursor = null;
	         return list;
	     }
	     if (null != cursor ) {
	         Uri _uri = Uri.parse("content://" + DataProvider.AUTHORITY + "/enforcement/"+tableName);
	         Cursor _cursor = context.getContentResolver().query(_uri, null,null,null,null);
	         if(_cursor == null || !_cursor.moveToLast()) {
	             cursor.close();
	             _cursor = null;
	             return list;
	         }
	         if( null != _cursor) {
	             if(_cursor.moveToLast()) {
	                 do{
	                     SQLDataEntity entry = SQLDataEntity.setEntity(0,_cursor);
	                     list.add(entry);
	                 }while(_cursor.moveToPrevious());
	             }
	             _cursor.close();
	             _cursor = null;
	         }
	         cursor.close();
	         cursor = null;
	     }
	     return list;
	}
	
	public CollectPeopleEnity queryPPL(String tabName,String contentId) {
		 databaseHelper = DatabaseHelper.getInstance(context);
		 CollectPeopleEnity cpEntity  = new CollectPeopleEnity();
	     Cursor cursor = databaseHelper.getWritableDatabase().query("sqlite_master", new String[]{"name"}, "name = ?", new String[]{tabName}, null, null, null);
	     if (null == cursor || !cursor.moveToFirst()) {
	         if (cursor != null) {
	             cursor.close();
	         }
	         cursor = null;
	         return null;
	     }
	     if (null != cursor ) {
	         Uri _uri = Uri.parse("content://" + DataProvider.AUTHORITY + "/ppl_cks/"+tabName+"&"+contentId);
	         Cursor _cursor = context.getContentResolver().query(_uri, null,null,null,null);
	         if(_cursor == null || !_cursor.moveToLast()) {
	             cursor.close();
	             _cursor = null;
	             return null;
	         }
	         if( null != _cursor) {
	             if(_cursor.moveToLast()) {
	                 do{
	                	 cpEntity = CollectPeopleEnity.setEntity(_cursor);
	                 }while(_cursor.moveToPrevious());
	             }
	             _cursor.close();
	             _cursor = null;
	         }
	         cursor.close();
	         cursor = null;
	     }
	     return cpEntity;
	}
	
	public CollectCarEnity queryVeh(String tabName,String contentID) {
		 databaseHelper = DatabaseHelper.getInstance(context);
		 CollectCarEnity carEntity  = new CollectCarEnity();
	     Cursor cursor = databaseHelper.getWritableDatabase().query("sqlite_master", new String[]{"name"}, "name = ?", new String[]{tabName}, null, null, null);
	     if (null == cursor || !cursor.moveToFirst()) {
	         if (cursor != null) {
	             cursor.close();
	         }
	         cursor = null;
	         return null;
	     }
	     if (null != cursor ) {
	         Uri _uri = Uri.parse("content://" + DataProvider.AUTHORITY + "/veh_cks/"+tabName+"&"+contentID);
	         Cursor _cursor = context.getContentResolver().query(_uri, null,null,null,null);
	         if(_cursor == null || !_cursor.moveToLast()) {
	             cursor.close();
	             _cursor = null;
	             return null;
	         }
	         if( null != _cursor) {
	             if(_cursor.moveToLast()) {
	                 do{
	                	 carEntity = CollectCarEnity.setEntity(_cursor);
	                 }while(_cursor.moveToPrevious());
	             }
	             _cursor.close();
	             _cursor = null;
	         }
	         cursor.close();
	         cursor = null;
	     }
	     return carEntity;
	}
	
	
	
	/**
	 * 获取查询结果列表
	 * @param tableName
	 * @return
	 */
	ArrayList<SQLDataEntity> getQueryResultList(String tableName) {
		 databaseHelper = DatabaseHelper.getInstance(context);
	     ArrayList<SQLDataEntity> list  = new  ArrayList<SQLDataEntity>();
	     Cursor cursor = databaseHelper.getWritableDatabase().query("sqlite_master", new String[]{"name"}, "name = ?", new String[]{tableName}, null, null, null);
	     if (null == cursor || !cursor.moveToFirst()) {
	         if (cursor != null) {
	             cursor.close();
	         }
	         cursor = null;
	         return list;
	     }
	     if (null != cursor ) {
	         Uri _uri = Uri.parse("content://" + DataProvider.AUTHORITY + "/query_result_list/"+tableName);
	         Cursor _cursor = context.getContentResolver().query(_uri, null,null,null,null);
	         if(_cursor == null || !_cursor.moveToLast()) {
	             cursor.close();
	             _cursor = null;
	             return list;
	         }
	         if( null != _cursor) {
	             if(_cursor.moveToLast()) {
	                 do{
	                     SQLDataEntity entry = SQLDataEntity.setEntity(1,_cursor);
	                     list.add(entry);
	                 }while(_cursor.moveToPrevious());
	             }
	             _cursor.close();
	             _cursor = null;
	         }
	         cursor.close();
	         cursor = null;
	     }
	     return list;
	}
	
	/**
	 * 根据contentID 在指定的表中查询详情
	 * @param tabName
	 * @param contentId
	 * @return
	 */
	QueryPeopleResultEntity queryPeopleResultItem(String tabName,String contentId) {
		 databaseHelper = DatabaseHelper.getInstance(context);
		 QueryPeopleResultEntity cpEntity  = new QueryPeopleResultEntity();
	     Cursor cursor = databaseHelper.getWritableDatabase().query("sqlite_master", new String[]{"name"}, "name = ?", new String[]{tabName}, null, null, null);
	     if (null == cursor || !cursor.moveToFirst()) {
	         if (cursor != null) {
	             cursor.close();
	         }
	         cursor = null;
	         return null;
	     }
	     if (null != cursor ) {
	         Uri _uri = Uri.parse("content://" + DataProvider.AUTHORITY + "/query_ppl_r/"+tabName+"&"+contentId);
	         Cursor _cursor = context.getContentResolver().query(_uri, null,null,null,null);
	         if(_cursor == null || !_cursor.moveToLast()) {
	             cursor.close();
	             _cursor = null;
	             return null;
	         }
	         if( null != _cursor) {
	             if(_cursor.moveToLast()) {
	                 do{
	                	 cpEntity = QueryPeopleResultEntity.setEntity(_cursor);
	                 }while(_cursor.moveToPrevious());
	             }
	             _cursor.close();
	             _cursor = null;
	         }
	         cursor.close();
	         cursor = null;
	     }
	     return cpEntity;
	}
	
	/**
	 * 在车辆查询表中根据contentID查询 查询结果
	 * @param tabName
	 * @param contentId
	 * @return
	 */
	QueryVehResultEntity queryVehResultItem(String tabName,String contentId) {
		 databaseHelper = DatabaseHelper.getInstance(context);
		 QueryVehResultEntity qvEntity  = new QueryVehResultEntity();
	     Cursor cursor = databaseHelper.getWritableDatabase().query("sqlite_master", new String[]{"name"}, "name = ?", new String[]{tabName}, null, null, null);
	     if (null == cursor || !cursor.moveToFirst()) {
	         if (cursor != null) {
	             cursor.close();
	         }
	         cursor = null;
	         return null;
	     }
	     if (null != cursor ) {
	         Uri _uri = Uri.parse("content://" + DataProvider.AUTHORITY + "/query_veh_r/"+tabName+"&"+contentId);
	         Cursor _cursor = context.getContentResolver().query(_uri, null,null,null,null);
	         if(_cursor == null || !_cursor.moveToLast()) {
	             cursor.close();
	             _cursor = null;
	             return null;
	         }
	         if( null != _cursor) {
	             if(_cursor.moveToLast()) {
	                 do{
	                	 qvEntity = QueryVehResultEntity.setEntity(_cursor);
	                 }while(_cursor.moveToPrevious());
	             }
	             _cursor.close();
	             _cursor = null;
	         }
	         cursor.close();
	         cursor = null;
	     }
	     return qvEntity;
	}
	
	public  void cleanDate() {
		databaseHelper = DatabaseHelper.getInstance(context);
		databaseHelper.deleteDatabase(context);
	}
	
	public void release() {
		databaseHelper.release();
		provider = null;
	}
	
}
