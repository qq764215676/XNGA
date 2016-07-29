package ys.oa.util;

import java.util.ArrayList;

import com.refactech.driibo.AppData;

import ys.oa.enity.MenusEnity;
import android.os.Environment;

public class Constants {
	//登录系统
	public static String USERID = "docadmin";
	public static String PSWID = "passw0rd";
	
	//用户名密码
	public static String USER_ID = AppData.getInstance().getSpUtil().getAccount();
	public static String USER_PWD = AppData.getInstance().getSpUtil().getPwd();
	public static String USER_AUTH = AppData.getInstance().getSpUtil().getAuth();
	
	//协警权限为99
	public static String USER_AUTH_XJ = "99";
	
	//升级测试
	public static String UPDATE_TEST_NOTIFY = "开发人员测试，请勿点击更新！";
	
	// public static final String CONNIP = "222.73.156.26:9080"; // 测试服务器地址
	//public static final String CONNIP = "101.227.249.15:9080"; // 南宁服务器地址
	public static final String CONNIP = "219.159.71.168:9080"; // 南宁服务器地址2016/01/06
	public static final String PATH = "/IDOC/WebService";// 服务器路径

	public static final String IMGPATH = "/IDOC/service/file/";
	public static final String imageCachePath = "/XNGA/ImageCache/"; // 图片缓存路径
	public static final String fileCachePath = "/XNGA/FileCache/"; // 文件缓存路径

	public static final String WEB_IP = "http://222.73.156.26:9080/weixinPublicPlatform/";

	// 下载文件的本地路径
	public static String localPath = Environment.getExternalStorageDirectory().getPath() + "/XNGA/files/";
	// 下载APK文件的本地路径
	public static String localAPKPath = Environment.getExternalStorageDirectory().getPath() + "/XNGA/";

	public static String REQUESTSUCCESS = "SUCCESS"; // 请求返回成功码

	public static ArrayList<MenusEnity> menuList = new ArrayList<MenusEnity>();
	public static ArrayList<MenusEnity> menuCivilList = new ArrayList<MenusEnity>();
	public static ArrayList<MenusEnity> searchlList = new ArrayList<MenusEnity>();

	public static int PHOTOHRAPH = 1;// 拍照
	public static int PHOTOZOOM = 2; // 缩放
	public static int PHOTORESULT = 3;// 结果
	public static String PHOTOTYPE = "image/*";

	public static int mWinheight = 0;

	// 保存表单中的时间
	public static String mDateAndTime = null;

	// 车辆类型
	public static String vehicleType = null;
	
	//add by fcr 
	/**
	 * 路面稽查的六种类型定义
	 */
	public static String PPL_CKS = "人员检查";
	public static String VEH_CKS_CAR = "汽车检查";
	public static String VEH_CKS_MOTORCYCLE = "摩托车检查";
	public static String NVEH_CKS_BICYCLE = "自行车检查";
	public static String NVEH_CKS_ELECTROMOBILE= "电动车检查";
	public static String GOODS_CKS = "物品检查";
	
	/**
	 * 业务查询类型
	 */
	public static String ONE_KEY_RESEARCH = "一键查询";
	public static String PERSON_RESEARCH = "人员查询";
	public static String VEHICLE_RESEARCH = "车辆查询";
	public static String GOODS_RESEARCH = "物品查询";
	public static String CASE_RESEARCH = "案件查询";
	
}
