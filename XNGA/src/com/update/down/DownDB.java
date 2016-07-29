package com.update.down;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DownDB {
	private DownDBHelper mDbHelper;
	private SQLiteDatabase db;

	public DownDB(Context context) {
		this.mDbHelper = new DownDBHelper(context);
	}

	/**
	 * 查看数据库中是否有数据
	 * 
	 * @param urlstr
	 * @return
	 */
	public boolean isHasInfors(String urlstr) {
		db = mDbHelper.getReadableDatabase();
		String sql = "select count(*) from download_info where url = ?";
		Cursor c = db.rawQuery(sql, new String[] { urlstr });
		c.moveToFirst();
		int count = c.getInt(0);
		c.close();
		return count == 0;
	}

	/**
	 * 保存 下载的具体信息
	 * 
	 * @param infos
	 */
	public void saveInfos(List<DownloadInfo> infos) {
		db = mDbHelper.getWritableDatabase();
		for (DownloadInfo info : infos) {
			String sql = "insert into download_info(thread_id,start_pos, end_pos,compelete_size,url) values (?,?,?,?,?)";
			Object[] bindArgs = { info.getThreadId(), info.getStartPos(), info.getEndPos(), info.getOriginalPos(),
					info.getUrl() };
			db.execSQL(sql, bindArgs);
		}
	}

	/**
	 * 得到下载具体信息
	 * @param urlstr
	 * @return
	 */
	
	public List<DownloadInfo> getInfos(String urlstr) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		db = mDbHelper.getReadableDatabase();
		String sql = "select thread_id, start_pos, end_pos,compelete_size,url from download_info where url=?";
		Cursor c = db.rawQuery(sql, new String[] { urlstr });
		while (c.moveToNext()) {
			DownloadInfo info = new DownloadInfo(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getString(4));
			list.add(info);
		}
		c.close();
		return list;
	}
	
	/**
	 * 更新数据库中的下载信息
	 * @param threadId
	 * @param compeleteSize
	 * @param urlstr
	 */

	public void updateInfos(int threadId, int compeleteSize, String urlstr) {
		db = mDbHelper.getReadableDatabase();
		String sql = "update download_info set compelete_size=? where thread_id=? and url=?";
		Object[] bindArgs = { compeleteSize, threadId, urlstr };
		db.execSQL(sql, bindArgs);
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		mDbHelper.close();
	}
	
	/**
	 * 下载完成后删除数据库中的数据
	 * @param url
	 */
	public void delete(String url) {
		db = mDbHelper.getReadableDatabase();
		db.delete("download_info", "url=?", new String[] { url });
		db.close();
	}
	
	/**
	 * 清除数据库所有数据
	 */
	public void deleteAll() {
		db = mDbHelper.getReadableDatabase();
		db.delete("download_info", null, null);
		db.close();
	}
}
