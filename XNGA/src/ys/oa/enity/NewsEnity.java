package ys.oa.enity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NewsEnity implements Serializable {

	private String newsTitle;
	private String newsIntro;
	private String newsDetails;
	private String newsSmallDocumentId;
	private String newsBigDocumentId;
	private String newsTime;

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getNewsIntro() {
		return newsIntro;
	}

	public void setNewsIntro(String newsIntro) {
		this.newsIntro = newsIntro;
	}

	public String getNewsDetails() {
		return newsDetails;
	}

	public void setNewsDetails(String newsDetails) {
		this.newsDetails = newsDetails;
	}

	public String getNewsSmallDocumentId() {
		return newsSmallDocumentId;
	}

	public void setNewsSmallDocumentId(String newsSmallDocumentId) {
		this.newsSmallDocumentId = newsSmallDocumentId;
	}

	public String getNewsBigDocumentId() {
		return newsBigDocumentId;
	}

	public void setNewsBigDocumentId(String newsBigDocumentId) {
		this.newsBigDocumentId = newsBigDocumentId;
	}

	public String getNewsTime() {
		return newsTime;
	}

	public void setNewsTime(String newsTime) {
		this.newsTime = newsTime;
	}

}
