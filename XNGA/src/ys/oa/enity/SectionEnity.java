package ys.oa.enity;

import java.io.Serializable;

import ys.oa.util.Constants;

@SuppressWarnings("serial")
public class SectionEnity implements Serializable {

	public static final int ITEM = 0;
	public static final int SECTION = 1;

	private int type;
	private String text;
	private String url;
	private int iconId;
	private Class<?> activityClass;
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Class<?> getActivityClass() {
		return activityClass;
	}

	public void setActivityClass(Class<?> activityClass) {
		this.activityClass = activityClass;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public SectionEnity(String text, String url) {
		this.type = ITEM;
		this.text = text;
		this.url = Constants.WEB_IP + url;
	}

	public SectionEnity(String text, String url, int drawableId) {
		this.type = ITEM;
		this.text = text;
		this.url = Constants.WEB_IP + url;
		iconId = drawableId;
	}

	public SectionEnity(String text, Class<?> activityClass, String title) {
		this.type = ITEM;
		this.text = text;
		this.activityClass = activityClass;
		this.title = title;
	}
	
	public SectionEnity(String text, Class<?> activityClass, String title, int iconId) {
		this.type = ITEM;
		this.text = text;
		this.activityClass = activityClass;
		this.title = title;
		this.iconId = iconId;
	}

	public SectionEnity(String text, Class<?> activityClass) {
		this.type = ITEM;
		this.text = text;
		this.activityClass = activityClass;
	}

	public SectionEnity(String text, Class<?> activityClass, int drawableId) {
		this.type = ITEM;
		this.text = text;
		this.activityClass = activityClass;
		iconId = drawableId;
	}

	public SectionEnity(String text) {
		this.type = SECTION;
		this.text = text;
	}

}
