package ys.oa.enity;

import java.io.Serializable;

public class IDInfoEntity implements Serializable{
	private String imgPath;
	private String id;
	private String name;
	
	
	public IDInfoEntity() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public IDInfoEntity (String imgPath,String name,String id) {
		this.imgPath = imgPath;
		this.name = name;
		this.id = id;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "IDInfoEntity [imgPath=" + imgPath + ", id=" + id + ", name="
				+ name + "]";
	}

}
