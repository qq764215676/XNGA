package ys.oa.enity;

/**
 * @author cuiru.fan
 *
 */
public class BaseEntity {
	
	public String contentId;
	public String checkType;//检查类型

	
	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	@Override
	public String toString() {
		return "BaseEntity [contentId=" + contentId + ", checkType="
				+ checkType + "]";
	}
	
}
