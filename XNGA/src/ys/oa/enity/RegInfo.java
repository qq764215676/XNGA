package ys.oa.enity;

public class RegInfo {
	private String regDate;
	private String QRcode;
	private String address;
	private String localDate;
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getQRcode() {
		return QRcode;
	}
	public void setQRcode(String qRcode) {
		QRcode = qRcode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocalDate() {
		return localDate;
	}
	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}
	@Override
	public String toString() {
		return "RegInfo [regDate=" + regDate + ", QRcode=" + QRcode
				+ ", address=" + address + ", localDate=" + localDate + "]";
	}

}
