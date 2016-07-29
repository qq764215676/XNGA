package com.net.post;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ys.oa.util.Constants;
import android.util.Base64;
import android.util.Log;

public class XmlPackage {
	/**
	 * 
	 * @param map数据键值对
	 * @param user登录到云平台的用户
	 * @param docInfor文档信息对象
	 * @param flag是否包含文件
	 * @return 封装好的增加或更新的XML字符串
	 */
	// 封装增加或更新的XML
	public static String packageForSaveOrUpdate(HashMap<?, ?> map, DocInfor docInfor, boolean flag) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope  xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<Body><REQUEST><AUTHENTICATION><SERVERDEF><SERVERNAME>server</SERVERNAME></SERVERDEF><LOGONDATA>");
		sb.append("<USERNAME>" + Constants.USERID + "</USERNAME>");
		sb.append("<PASSWORD>" + Constants.PSWID + "</PASSWORD>");
		sb.append("</LOGONDATA></AUTHENTICATION><COMMAND>IMPORTYOUNGCONTENT</COMMAND><DATA>");
		sb.append("<CONTENTTYPENAME>" + docInfor.getContentName() + "</CONTENTTYPENAME>");
		sb.append("<CONTENTID>" + docInfor.getContentId() + "</CONTENTID>");
		sb.append("<FOLDER>" + flag + "</FOLDER><YOUNGPROPERTIES>");
		Set<?> set = map.keySet();
		Iterator<?> iterator = set.iterator();
		String key = null;
		String value = null;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			value = (String) map.get(key);
			sb.append("<YOUNGPROPERTY><NAME>" + key + "</NAME><TYPE>12</TYPE><VALUE>" + value
					+ "</VALUE></YOUNGPROPERTY>");
		}
		sb.append("</YOUNGPROPERTIES></DATA></REQUEST></Body></Envelope>");
		return sb.toString();
	}

	/**
	 * 
	 * @param map数据键值对
	 * @param docInfor文档信息对象
	 * @param flag是否包含文件
	 * @param imagePath插入的图片
	 * @return 封装好的增加或更新的XML字符串
	 */
	// 封装增加或更新带图片数据的XML
	public static String packageForInsertFileData(HashMap<?, ?> map, DocInfor docInfor, boolean flag, String filePath,
			String fileType) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope  xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<Body><REQUEST><AUTHENTICATION><SERVERDEF><SERVERNAME>server</SERVERNAME></SERVERDEF><LOGONDATA>");
		sb.append("<USERNAME>" + Constants.USERID + "</USERNAME>");// 用户名
		sb.append("<PASSWORD>" + Constants.PSWID + "</PASSWORD >");// 密码
		sb.append("</LOGONDATA></AUTHENTICATION><COMMAND>IMPORTYOUNGCONTENT</COMMAND><DATA>");
		sb.append("<CONTENTID>" + docInfor.getContentId() + "</CONTENTID>");// contentId，为null时，插入新数据，不为null时，更改该条数据属性
		sb.append("<CONTENTTYPENAME>" + docInfor.getContentName() + "</CONTENTTYPENAME>");// 表名
		sb.append("<FOLDER>" + flag + "</FOLDER>");
		sb.append("<YOUNGPROPERTIES>");
		Set<?> set = map.keySet();
		Iterator<?> iterator = set.iterator();
		String key = null;
		String value = null;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			value = (String) map.get(key);
			Log.v("fcr","key---"+key+",value----"+value);
			sb.append("<YOUNGPROPERTY><NAME>" + key + "</NAME><TYPE>12</TYPE><VALUE>" + value
					+ "</VALUE></YOUNGPROPERTY>");
		}
		sb.append("</YOUNGPROPERTIES>");

		if (filePath.length() != 0) {
			InputStream is = null;
			try {
				System.out.println("ImageStream----Path--->" + filePath);
				is = new FileInputStream(filePath);// 获取文件输入流
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			byte[] data = inputStreamToByte(is);// 将输入流转换为Byte数组自定义方法
			byte[] fileStream = Base64.encode(data, 1);// 转成64位

			sb.append("<YOUNGDOCUMENTS><YOUNGDOCUMENT>");
			sb.append("<SOURCEFILENAME>" + filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length())
					+ "</SOURCEFILENAME>");
			sb.append("<DOCUMENTTYPENAME>FILE</DOCUMENTTYPENAME>");
			sb.append("<INPUTSTREAM>" + new String(fileStream) + "</INPUTSTREAM>");
			sb.append("<SIZE>" + new File(filePath).length() + "</SIZE>");
			sb.append("<MIMETYPE>" + fileType + "&#xD;</MIMETYPE>");
			sb.append("</YOUNGDOCUMENT></YOUNGDOCUMENTS>");
		}
		sb.append("</DATA></REQUEST></Body></Envelope>");
		return sb.toString();
	}

	// 输入流转Byte
	public static byte[] inputStreamToByte(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte data[] = new byte[1024];
		int count;
		try {
			while (((count = is.read(data)) != -1)) {
				baos.write(data, 0, count);
			}
			baos.flush();
		} catch (IOException e) {
		}
		return baos.toByteArray();
	}

	/**
	 * 查询语句
	 * 
	 * @param sql
	 *            查询语句 from table;
	 * @param size
	 *            取数据的个数
	 * @param orderby
	 *            排序 xxx ASC,xxx DESC...
	 * @param columnlist
	 *            所取字段 id,name,age...
	 * @param command
	 *            执行什么操作 SEARCHYOUNGCONTENT/IMPORTYOUNGCONTENT
	 * @param docInfor
	 *            文档信息 主要是文档ID和文档名
	 * @param isSimplesearch
	 *            是否使用SimpleSearch查询方式
	 * @param noTincludedocInfo
	 *            不返回DocumentId (False 返回 /True 不返回)
	 * @return
	 */
	public static String packageSelect(String sql, String size, String orderby, String columnlist, String command,
			DocInfor docInfor, boolean isSimplesearch, boolean noTincludedocInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope  xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<Body><REQUEST><AUTHENTICATION><SERVERDEF><SERVERNAME>server</SERVERNAME></SERVERDEF><LOGONDATA>");
		sb.append("<USERNAME>" + Constants.USERID + "</USERNAME>");
		sb.append("<PASSWORD>" + Constants.PSWID + "</PASSWORD >");
		sb.append("</LOGONDATA></AUTHENTICATION>");
		sb.append("<COMMAND>" + command + "</COMMAND>");
		sb.append("<DATA><SIMPLESEARCH>" + isSimplesearch + "</SIMPLESEARCH>");
		sb.append("<CONTENTTYPENAME>" + docInfor.getContentName() + "</CONTENTTYPENAME>");
		sb.append("<QUERY>" + sql + "</QUERY>");
		sb.append("<SIZE>" + size + "</SIZE>");
		sb.append("<ORDERBY>" + orderby + "</ORDERBY>");
		sb.append("<COLUMNLIST>" + columnlist + "</COLUMNLIST>");
		sb.append("<RETENTIONDOC>true</RETENTIONDOC><CHECKDOCONLY>true</CHECKDOCONLY>");
		sb.append("<NOTINCLUDEDOCINFO>" + noTincludedocInfo + "</NOTINCLUDEDOCINFO>");
		sb.append("</DATA></REQUEST></Body></Envelope>");
		return sb.toString();
	}

	/**
	 * 分页查询语句
	 * 
	 * @param offset
	 *            开始序号
	 */
	public static String packageSelect(String sql, String size, String orderby, String columnlist, String command,
			DocInfor docInfor, boolean isSimplesearch, boolean noTincludedocInfo, String offset) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope  xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<Body><REQUEST><AUTHENTICATION><SERVERDEF><SERVERNAME>server</SERVERNAME></SERVERDEF><LOGONDATA>");
		sb.append("<USERNAME>" + Constants.USERID + "</USERNAME>");
		sb.append("<PASSWORD>" + Constants.PSWID + "</PASSWORD >");
		sb.append("</LOGONDATA></AUTHENTICATION>);");
		sb.append("<COMMAND>" + command + "</COMMAND>");
		sb.append("<DATA><SIMPLESEARCH>" + isSimplesearch + "</SIMPLESEARCH>");
		sb.append("<CONTENTTYPENAME>" + docInfor.getContentName() + "</CONTENTTYPENAME>");
		sb.append("<QUERY>" + sql + "</QUERY>");
		sb.append("<OFFSET>" + offset + "</OFFSET>");
		sb.append("<SIZE>" + size + "</SIZE>");
		sb.append("<ORDERBY>" + orderby + "</ORDERBY>");
		sb.append("<COLUMNLIST>" + columnlist + "</COLUMNLIST>");
		sb.append("<RETENTIONDOC>true</RETENTIONDOC><CHECKDOCONLY>true</CHECKDOCONLY>");
		sb.append("<NOTINCLUDEDOCINFO>" + noTincludedocInfo + "</NOTINCLUDEDOCINFO>");
		sb.append("</DATA></REQUEST></Body></Envelope>");
		return sb.toString();
	}

	/**
	 * 登录
	 * 
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static String packageForLogin(String userName, String passWord) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope  xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<Body><REQUEST><AUTHENTICATION><SERVERDEF><SERVERNAME>server</SERVERNAME></SERVERDEF><LOGONDATA>");
		sb.append("<USERNAME>" + userName + "</USERNAME>");// 用户名
		sb.append("<PASSWORD>" + passWord + "</PASSWORD >");// 密码
		sb.append("</LOGONDATA></AUTHENTICATION><COMMAND>LOGON</COMMAND></REQUEST></Body></Envelope>");
		return sb.toString();
	}

	/**
	 * 创建帐户(修改密码)
	 * 
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static String packageAccount(String userName, String passWord) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope  xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<Body><REQUEST><AUTHENTICATION><SERVERDEF><SERVERNAME>server</SERVERNAME></SERVERDEF><LOGONDATA>");
		sb.append("<USERNAME>" + Constants.USERID + "</USERNAME>"); // 用户名
		sb.append("<PASSWORD>" + Constants.PSWID + "</PASSWORD >"); // 密码
		sb.append("</LOGONDATA></AUTHENTICATION><COMMAND>IMPORTYOUNGUSER</COMMAND><DATA>");
		sb.append("<NAME>" + userName + "</NAME>");
		sb.append("<PASSWORD>" + passWord + "</PASSWORD>");
		sb.append("<YOUNGSECURITYROLE>");
		sb.append("<NAME>Administrator</NAME>");
		sb.append("</YOUNGSECURITYROLE><STATUS>0</STATUS><EXTATTR1></EXTATTR1><EXTATTR2></EXTATTR2><YOUNGUSERGROUPS></YOUNGUSERGROUPS></DATA></REQUEST></Body></Envelope>");
		return sb.toString();
	}

	/**
	 * 删除记录操作
	 * 
	 * @param conentId
	 * @return
	 */
	public static String packageDelete(String contentId) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope  xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<Body><REQUEST><AUTHENTICATION><SERVERDEF><SERVERNAME>server</SERVERNAME></SERVERDEF><LOGONDATA>");
		sb.append("<USERNAME>" + Constants.USERID + "</USERNAME>");
		sb.append("<PASSWORD>" + Constants.PSWID + "</PASSWORD>");
		sb.append("</LOGONDATA></AUTHENTICATION><COMMAND>DELETEYOUNGCONTENT</COMMAND><DATA>");
		sb.append("<CONTENTID>" + contentId + "</CONTENTID>");
		sb.append("</DATA></REQUEST></Body></Envelope>");
		return sb.toString();
	}

}