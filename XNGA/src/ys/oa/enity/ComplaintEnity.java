package ys.oa.enity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ComplaintEnity implements Serializable {

	private String complaintSort;
	private String complaintTitle;
	private String complaintStatus;
	private String complaintTime;

	public String getComplaintSort() {
		return complaintSort;
	}

	public void setComplaintSort(String complaintSort) {
		this.complaintSort = complaintSort;
	}

	public String getComplaintTitle() {
		return complaintTitle;
	}

	public void setComplaintTitle(String complaintTitle) {
		this.complaintTitle = complaintTitle;
	}

	public String getComplaintStatus() {
		return complaintStatus;
	}

	public void setComplaintStatus(String complaintStatus) {
		this.complaintStatus = complaintStatus;
	}

	public String getComplaintTime() {
		return complaintTime;
	}

	public void setComplaintTime(String complaintTime) {
		this.complaintTime = complaintTime;
	}

}
