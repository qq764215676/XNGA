package com.update.down;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ys.oa.util.L;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;

/**
 * @author liubo
 * @date 2013-07-01 10:15:30
 * @category 多线程下载
 */

public class MultiThreadDownload extends Thread {
	private final String TAG = "MultiThreadDownload";
	/***
	 * 线程数量<br>
	 * 默认为5个线程下载
	 */
	private int threadNum;
	/*** 文件大小 */
	private static int fileSize;
	/** 历史下载大小 */
	private int historyDownloadSize = 0;
	/** 总下载大小 */
	private int totalDownloadSize = 0;
	/** 文件的url */
	private String urlStr;
	private String fileName;
	/*** 保存的路径 */
	private String savePath;
	// /** 下载的 平均速度 */
	// private int downloadSpeed = 0;
	// /** 下载用的时间 */
	// private int usedTime = 0;
	// /** 当前时间 */
	// private long curTime;
	/** 是否已经下载完成 */
	public boolean stoped = false;
	private Handler handler;
	private Context context;

	private DownDB dao;
	private List<DownloadInfo> infos;// 存放下载信息类的集合

	/** 每个线程的下载完成情况 */
	private boolean[] fdtBool = { false, false, false, false, false };

	/**
	 * 下载的构造函数
	 * 
	 * @param url
	 *            请求下载的URL
	 * @param handler
	 *            UI更新使用
	 * @param savePath
	 *            保存文件的路径
	 */
	public MultiThreadDownload(Context context, Handler handler, String url, String savePath, int threadNum) {
		this.handler = handler;
		this.urlStr = url;
		this.savePath = savePath;
		this.context = context;
		this.threadNum = threadNum;
		dao = new DownDB(context);
		L.v(TAG, toString());
	}

	@Override
	public void run() {
		fileName = FileDownUtil.getFileName(urlStr);
		FileDownloadThread[] fds = new FileDownloadThread[threadNum];// 设置线程数量
		// try {

		FileDownloadThread.stoped = false;

		if (isFirst(urlStr)) {
			L.v(TAG, "isFirst");
			init();
			/** 每一个线程需要下载的大小 */
			int blockSize = fileSize / threadNum;
			infos = new ArrayList<DownloadInfo>();
			for (int i = 0; i < threadNum - 1; i++) {
				DownloadInfo info = new DownloadInfo(i, i * blockSize, (i + 1) * blockSize - 1, i * blockSize, urlStr);
				infos.add(info);
			}
			DownloadInfo info = new DownloadInfo(threadNum - 1, (threadNum - 1) * blockSize, fileSize - 1,
					(threadNum - 1) * blockSize, urlStr);
			infos.add(info);
		} else {
			// 得到数据库中已有的urlstr的下载器的具体信息
			infos = dao.getInfos(urlStr);
			L.v(TAG, "not isFirst size=" + infos.size());
			for (int i = 0; i < infos.size(); i++) {
				DownloadInfo info = infos.get(i);
				historyDownloadSize += info.getStartPos() - info.getOriginalPos();
				if (i == infos.size() - 1) {
					fileSize = info.getEndPos();
				}
			}
			L.i(TAG, "继续下载时，保存下载的大小为 : " + historyDownloadSize);
		}
		// Handler更新UI，发送消息
		sendMsg(FileDownUtil.startDownloadMeg);

		URL url;
		try {
			url = new URL(urlStr);
			File file = new File(savePath + fileName);
			for (int i = 0; i < infos.size(); i++) {
				DownloadInfo info = infos.get(i);
				if (info.getStartPos() < info.getEndPos()) {
					L.i(TAG, i + "号线程启动");
					fdtBool[i] = true;
					FileDownloadThread fdt = new FileDownloadThread(url, file, info.getStartPos(), info.getEndPos());
					fdt.setName("thread" + i);
					fdt.start();
					fds[i] = fdt;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			L.i("mag", urlStr + " 不可用");
		}

		/**
		 * 获取数据，更新UI，直到所有下载线程都下载完成。
		 */
		int temp = 0; // 计时器
		int downloadTemp = 0; // 临时缓存变量
		boolean finished = false;
		// // 开始时间，放在循环外，求解的usedTime就是总时间
		// long startTime = System.currentTimeMillis();
		while (!finished && !stoped) {
			int downloadSize = 0; // 线程下载大小
			finished = true;
			for (int i = 0; i < fds.length; i++) {
				if (fdtBool[i]) {
					downloadSize += fds[i].getDownloadSize();
					if (!fds[i].isFinished()) {
						finished = false;
					}
				}
			}
			totalDownloadSize = historyDownloadSize + downloadSize;
			if (downloadTemp != totalDownloadSize) {
				temp = 0;
			} else if (temp >= 10) {
				if (isNetworkAvailable()) {
					dao.delete(urlStr);
					sendMsg(FileDownUtil.downloadTimeOut);
					L.i(TAG, "网络超时");
				} else {
					saveDownFiletoDb(fds);
					sendMsg(FileDownUtil.networkNotAvailable);
					L.i(TAG, "网络不可用");
				}
				break;
			}
			downloadTemp = totalDownloadSize;
			L.i(TAG, "当前下载大小：" + totalDownloadSize);
			// curTime = System.currentTimeMillis();
			// usedTime = (int) ((curTime - startTime) / 1000);
			// System.out.println("curTime = " + curTime + " downloadSize = " +
			// downloadSize + " usedTime "
			// + usedTime);
			//
			// if (usedTime == 0)
			// usedTime = 1;
			// downloadSpeed = (downloadSize / usedTime) / 1024;
			try {
				sleep(1000);
				temp++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}/* 1秒钟刷新一次界面 */
			sendMsg(FileDownUtil.updateDownloadMeg);
		}
		if (finished) {
			L.e(TAG, "下载完成");
			sendMsg(FileDownUtil.endDownloadMeg);
			// 删除数据库中urlstr对应的下载器信息
			dao.delete(urlStr);
		} else if (stoped) {
			L.e(TAG, "下载停止");
			FileDownloadThread.stoped = true;
			ArrayList<DownloadInfo> downloadInfos = new ArrayList<DownloadInfo>();
			int saveDownloadSize = 0;
			for (int i = 0; i < infos.size(); i++) {
				DownloadInfo info = infos.get(i);
				if (fdtBool[i]) {
					saveDownloadSize += fds[i].getDownloadSize();
					info.setStartPos(info.getStartPos() + fds[i].getDownloadSize());
					downloadInfos.add(info);
				} else {
					info.setStartPos(info.getEndPos());
				}
			}
			L.i(TAG, "暂停时，下载的大小为：" + saveDownloadSize);
			// 保存infos中的数据到数据库
			dao.saveInfos(downloadInfos);
			sendMsg(FileDownUtil.stopDownloadMeg);
		}
		super.run();
	}

	/**
	 * 得到文件的大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * 得到已经下载的数量
	 * 
	 * @return
	 */
	public int getDownloadSize() {
		return this.totalDownloadSize;
	}

	// /**
	// * 获取下载速度
	// *
	// * @return
	// */
	// public int getDownloadSpeed() {
	// return this.downloadSpeed;
	// }

	/**
	 * 修改默认线程数
	 * 
	 * @param threadNum
	 */
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	@Override
	public String toString() {
		return "MultiThreadDownload [threadNum=" + threadNum + ", fileSize=" + fileSize + ", UrlStr=" + urlStr
				+ ", savePath=" + savePath + ", fileName = " + fileName + "]";
	}

	/**
	 * 发送消息，用户提示
	 * */
	private void sendMsg(int what) {
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}

	private boolean isFirst(String urlstr) {
		return dao.isHasInfors(urlstr);
	}

	/**
	 * 初始化
	 */
	private void init() {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			fileSize = connection.getContentLength();

			// 只创建一个文件，saveFile下载内容
			File saveFile = new File(savePath + fileName);
			L.e(TAG, "文件一共：" + fileSize + ", savePath = " + savePath + ", fileName = " + fileName);

			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			// 本地访问文件
			RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
			accessFile.setLength(fileSize);
			accessFile.close();
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public void saveDownFiletoDb(FileDownloadThread[] fds) {
		ArrayList<DownloadInfo> downloadInfos = new ArrayList<DownloadInfo>();
		int saveDownloadSize = 0;
		for (int i = 0; i < infos.size(); i++) {
			DownloadInfo info = infos.get(i);
			if (fdtBool[i]) {
				saveDownloadSize += fds[i].getDownloadSize();
				info.setStartPos(info.getStartPos() + fds[i].getDownloadSize());
				downloadInfos.add(info);
			} else {
				info.setStartPos(info.getEndPos());
			}
		}
		L.i(TAG, "暂停时，下载的大小为：" + saveDownloadSize);
		// 保存infos中的数据到数据库
		dao.saveInfos(downloadInfos);
	}
}
