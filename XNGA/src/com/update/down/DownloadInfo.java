package com.update.down;

/**
 * 3 *创建一个下载信息的实体类 4
 */
public class DownloadInfo {
	private int threadId;// 下载器id
	private int startPos;// 开始点
	private int endPos;// 结束点
	private int originalPos;// 每个线程需要下载大小
	private String url;// 下载器网络标识

	public DownloadInfo(int threadId, int startPos, int endPos, int originalPos, String url) {
		this.threadId = threadId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.originalPos = originalPos;
		this.url = url;
	}

	public DownloadInfo() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public int getOriginalPos() {
		return originalPos;
	}

	public void setOriginalPos(int originalPos) {
		this.originalPos = originalPos;
	}

	@Override
	public String toString() {
		return "DownloadInfo [threadId=" + threadId + ", startPos=" + startPos + ", endPos=" + endPos
				+ ", originalPos=" + originalPos + "]";
	}
}
