package ys.oa.enity;

import java.io.Serializable;

import android.content.Intent;

@SuppressWarnings("serial")
public class MenusEnity implements Serializable {

	private String menuTitle;
	private int drawableId;
	private Intent toIntent;
	private int indexInMenuList;

	public String getMenuTitle() {
		return menuTitle;
	}

	public void setMenuTitle(String menuTitle) {
		this.menuTitle = menuTitle;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

	public Intent getToIntent() {
		return toIntent;
	}

	public void setToIntent(Intent toIntent) {
		this.toIntent = toIntent;
	}

	public int getIndexInMenuList() {
		return indexInMenuList;
	}

	public void setIndexInMenuList(int indexInMenuList) {
		this.indexInMenuList = indexInMenuList;
	}

}
