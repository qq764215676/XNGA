package ys.oa.enity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LostEnity implements Serializable {

	private String lostName;
	private String lostIntro;
	private String lostPhone;
	private String lostTime;

	public String getLostName() {
		return lostName;
	}

	public void setLostName(String lostName) {
		this.lostName = lostName;
	}

	public String getLostIntro() {
		return lostIntro;
	}

	public void setLostIntro(String lostIntro) {
		this.lostIntro = lostIntro;
	}

	public String getLostPhone() {
		return lostPhone;
	}

	public void setLostPhone(String lostPhone) {
		this.lostPhone = lostPhone;
	}

	public String getLostTime() {
		return lostTime;
	}

	public void setLostTime(String lostTime) {
		this.lostTime = lostTime;
	}

}
