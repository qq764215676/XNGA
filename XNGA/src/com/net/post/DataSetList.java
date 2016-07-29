package com.net.post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yumignxu
 * @date 2012-11-14 上午08:06:51
 * @category 解析后数据的存放数据结构
 */
public class DataSetList implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public Map<String, List<String>> map = new HashMap<String, List<String>>();
	public String type;
	public List<String> description = new ArrayList<String>();
	public List<String> nameList = new ArrayList<String>();
	public List<String> valueList = new ArrayList<String>();
	public List<String> CONTENTID = new ArrayList<String>();
	public List<String> DOCUMENTID = new ArrayList<String>();
	public List<String> COMMENTID = new ArrayList<String>();
	public List<String> NODEID = new ArrayList<String>();
	public List<String> WORKID = new ArrayList<String>();
	public List<String> WORKQUEUENAME = new ArrayList<String>();
	public List<Boolean> isExistDocumentID = new ArrayList<Boolean>();
	public String EXTATTR1;// 拓展属性1
	public String EXTATTR2;
	public String ERROR = "";
	public String SUCCESS = "";

	// MobileOffice
	public String fileName;
	public String lastChangedDate;
	public ArrayList<String> DOCUMENTTYPE = new ArrayList<String>();
	public ArrayList<String> docSize = new ArrayList<String>();
	public ArrayList<String> commSize = new ArrayList<String>();
	public ArrayList<String> size = new ArrayList<String>();
	public ArrayList<String> mimeType = new ArrayList<String>();
	public ArrayList<String> docMimeType = new ArrayList<String>();
	public ArrayList<String> commMimeType = new ArrayList<String>();
	public ArrayList<String> sharer = new ArrayList<String>();
	public ArrayList<String> creater = new ArrayList<String>();
	public ArrayList<String> lastChangedDates = new ArrayList<String>();
	public ArrayList<String> tableName = new ArrayList<String>();
}
