package com.update.down;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ys.oa.util.L;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

/**
 * @author liubo
 * @date 2013-07-01 10:15:30
 * @category 文件下载工具类
 */
public class FileDownUtil {
	/**
	 * 开始消息提示常量
	 * */
	public static final int startDownloadMeg = 1;

	/**
	 * 更新消息提示常量
	 * */
	public static final int updateDownloadMeg = 2;

	/**
	 * 完成消息提示常量
	 * */
	public static final int endDownloadMeg = 3;

	/**
	 * 停止消息提示常量
	 * */
	public static final int stopDownloadMeg = 4;

	/**
	 * 下载文件超时
	 */
	public static final int downloadTimeOut = 5;

	/**
	 * 网络无连接
	 */
	public static final int networkNotAvailable = 6;

	/**
	 * 检验SDcard状态
	 * 
	 * @return boolean
	 */
	public static boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存文件文件到目录
	 * 
	 * @param context
	 * @return 文件保存的目录
	 */
	public static String setMkdir(Context context) {
		String filePath;
		if (checkSDCard()) {
			filePath = Environment.getExternalStorageDirectory() + File.separator + "myfile";
		} else {
			filePath = context.getCacheDir().getAbsolutePath() + File.separator + "myfile";
		}
		File file = new File(filePath);
		if (!file.exists()) {
			boolean b = file.mkdirs();
			L.e("file", "文件不存在  创建文件    " + b);
		} else {
			L.e("file", "文件存在");
		}
		return filePath;
	}

	/**
	 * 得到文件的名称
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getFileName(String url) {
		String name = null;
		try {
			name = url.substring(url.lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 得到文件的名称
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getFileType(String url) {
		String name = null;
		try {
			name = url.substring(url.lastIndexOf(".") + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 关闭输入流>/br>
	 * 
	 * @param InputStream
	 */
	public static void closeInputStream(InputStream input) {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				L.e("InputStream", "InputStream IOException " + e.getMessage());
			}
		}
	}

	/**
	 * 关闭输出流</br>
	 * 
	 * @param output
	 */
	public static void closeOutputStream(OutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				L.e("OutputStream", "OutputStream IOException " + e.getMessage());
			}
		}
	}

	/**
	 * 删除文件
	 */
	public static void deleteFile(File file) {

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

	public static boolean isFileUsed(Context context, File file) {
		boolean result = false;
		try {
			PackageManager pm = context.getPackageManager();
			L.e("archiveFilePath", file.getAbsolutePath());
			PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
			if (info != null) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
			L.e("FileDownUtil", "*****  解析未安装的 apk 出现异常 *****" + e.getMessage());
		}
		return result;
	}
}
