package ys.oa.provider;


import ys.oa.util.Constants;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "efps.db";
	private static DatabaseHelper openHelper ;
	private static Context context;
	private static final String TAG = "DatabaseHelper";
	
	
	public synchronized static DatabaseHelper getInstance(Context context) {
		DatabaseHelper.context = context;
		if(openHelper == null) {
			openHelper = new DatabaseHelper(context);
		}
		return openHelper;
		}
	

	public DatabaseHelper(String dbName,Context context) {
		super(context,dbName,null,DATABASE_VERSION);
	}
	
	public DatabaseHelper(Context context) {
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		/*if(newVersion==2) {
				String tab = "enforcement_" +Constants.USER_ID;
				db.execSQL("ALTER "+tab+" time ADD TEXT default '0'");
		}*/

	}
	
	public boolean deleteDatabase(Context context) {
		Log.v("fcr","deleteDatabase");
		openHelper.close();
		return context.deleteDatabase(DATABASE_NAME);
	}
	
	public void release() {
		openHelper = null;
	}
	
	
	


}