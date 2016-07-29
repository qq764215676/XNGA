package ys.oa.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;
import ys.oa.enity.QueryPeopleResultEntity;
import ys.oa.enity.QueryVehResultEntity;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.provider.SQLDataEntity;
import ys.oa.util.Constants;
import ys.oa.util.Util;

/**
 * @author cuiru.fan
 * create at 2016-01-21
 *
 */
public class QueryTask extends TimerTask {
	
		private static String TAG = "QueryTask";
		private Activity mActivity;
		private String contentId;
		private QueryResultListener queryResultListener;
		private EnforcementDataManager dataManager;
		private int queryType;
		public static int  QUERY_PEOPLE = 0;//人员查询
		public static int QUERY_VEH = 1;//机动车
		private Timer timer;
		
		/**
		 * 根据queryType 启动不同的查询任务
		 * @param mActivity
		 * @param type
		 * @param contentId
		 * @param queryResultListener
		 */
		public QueryTask(Activity mActivity,int queryType,String contentId,QueryResultListener queryResultListener,Timer timer) {
			this.mActivity = mActivity;
			this.contentId = contentId;
			this.queryType = queryType;
			this.queryResultListener = queryResultListener;
			this.timer = timer;
			dataManager = EnforcementDataManager.getInstance(mActivity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Util.isNetworkAvailable(AppData.getContext())) {
				if(queryType == QUERY_PEOPLE) {
					new AsyncQuery(timer).execute("queryPPL",contentId);
				}else if(queryType == QUERY_VEH) {
					new AsyncQuery(timer).execute("queryVEH",contentId);
				}
			}
		}
		
		private DataSetList datasetlist;
		class AsyncQuery extends AsyncTask<String, Integer, Integer> {
			Timer timer;
			public AsyncQuery(Timer timer) {
				this.timer = timer;
			}

			protected Integer doInBackground(String... params) {
				if ("queryPPL".equals(params[0])) {
					try {
						datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from XNGA_PPL_RESULT where FLAG_ID='"
								+ params[1] + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "XNGA_PPL_RESULT"), true,
								false));
					} catch (Exception e) {
						return -1;
					}
					return 1;
				}
				else if("queryVEH".equals(params[0])) {
					try {
						datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from XNGA_VEH_RESULT where FLAG_ID='"
								+ params[1] + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "XNGA_VEH_RESULT"), true,
								false));
					} catch (Exception e) {
						return -1;
					}
					return 2;
				}
				return -1;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				switch (result) {
				case -1:// 异步NullPointerException
//					T.showSnack(mActivity, R.string.serverFailed);
					break;
				case 1:
					//人员检查结果
					if(Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
						if(datasetlist != null) {
							ArrayList<QueryPeopleResultEntity> resultList = parsePPLResultAndInsertData(datasetlist);
							queryResultListener.onComplete(timer);
						}
					}else {
						queryResultListener.onComplete(timer);
						Toast.makeText(mActivity, "查询失败！", Toast.LENGTH_SHORT).show();
					}
					break;
				case 2:
					//机动车检查结果
					if(Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
						if(datasetlist != null) {
							ArrayList<QueryVehResultEntity> resultList = parseVehResultAndInsertData(datasetlist);
							queryResultListener.onComplete(timer);
						}
					}else {
						queryResultListener.onComplete(timer);
						Toast.makeText(mActivity, "查询失败！", Toast.LENGTH_SHORT).show();
					}
					
					break;
				case 3:
					break;
				}
			}
			
		}
		
		/**
		 * 解析人员查询结果数据
		 * @param dataSetList
		 * @return
		 */
		public ArrayList<QueryPeopleResultEntity> parsePPLResultAndInsertData(DataSetList dataSetList) {
			ArrayList<QueryPeopleResultEntity> resultList = new ArrayList<QueryPeopleResultEntity>();
			ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
			Log.v(TAG,"nameList-----"+nameList.toString());
			ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
			Log.v(TAG,"valueList-----"+valueList.toString());
			ArrayList<String> documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
			QueryPeopleResultEntity qp = null;
			SQLDataEntity se = null;
			Log.v(TAG,"documentId-----"+valueList.toString());
			for (int i = 0; i < nameList.size(); i++) {
				if("XNGA_PPL_RESULT".equals(nameList.get(i))) {
					if(qp != null) {
						qp = null;
					}
					if(se != null) {
						se = null;
					}
					qp = new QueryPeopleResultEntity();
					se = new SQLDataEntity();
					se.setCheckType(Constants.PPL_CKS);
					if(queryType == QUERY_PEOPLE) {
						qp.setCheckType(Constants.PPL_CKS);
					}
					/*else if(queryType == QUERY_N_VEH) {
						if("0".equals(Constants.vehicleType)) {
//							se.setType(Constants.NVEH_CKS_ELECTROMOBILE);
							qp.setCheckType(Constants.NVEH_CKS_ELECTROMOBILE);
						}else if("1".equals(Constants.vehicleType)) {
//							se.setType(Constants.NVEH_CKS_BICYCLE);
							qp.setCheckType(Constants.NVEH_CKS_BICYCLE);
						}
					}*/
				}
				if ("FLAG_ID".equals(nameList.get(i))) {
					qp.setContentId(valueList.get(i));
					se.setContentId(valueList.get(i));
				}
				else if ("PPL_NAME".equals(nameList.get(i))) {
					String name = valueList.get(i);
					qp.setName(name);
					se.setName(name);
				}else if ("PPL_SEX".equals(nameList.get(i))) {
					String sex = valueList.get(i);
					qp.setSex(sex);
					se.setSex(sex);
				}else if ("PPL_BIRT".equals(nameList.get(i))) {
					String birth = valueList.get(i);
					qp.setBirthday(birth);
					se.setBirthday(birth);
				}
				else if ("PPL_ID".equals(nameList.get(i))) {
					String ID = valueList.get(i);
					qp.setId(ID);
					se.setID(ID);
				}
				else if ("PPL_NATION".equals(nameList.get(i))) {
					String nation = valueList.get(i);
					qp.setNation(nation);
				}
				else if ("PPL_ADD".equals(nameList.get(i))) {
					String address = valueList.get(i);
					qp.setAddress(address);
				}else if ("PPL_TYPE".equals(nameList.get(i))) {
					String Ptype = valueList.get(i);//人员类型
					qp.setpType(Ptype);
				}else if ("PPL_RECORD".equals(nameList.get(i))) {
					String record = valueList.get(i);
					qp.setRecord(record);
					se.setpType(record);
				}else if ("REM".equals(nameList.get(i))) {
					String rem = valueList.get(i);
					qp.setInfo(rem);
				}else if ("PPL_RECORD_CT".equals(nameList.get(i))) {
					qp.setRecordType(valueList.get(i));
				}
			}
			se.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
			Log.v("fcr","qp----"+qp.toString());
			Log.v("fcr","se--"+se.toString());
			dataManager.insertQueryResultListItem(se);
			dataManager.insertQueryPPLResultItem(qp);
			resultList.add(qp);
			
			return resultList;
		}
		
		/**
		 * 解析车辆查询结果
		 * @param dataSetList
		 * @return
		 */
		public ArrayList<QueryVehResultEntity> parseVehResultAndInsertData(DataSetList dataSetList) {
			ArrayList<QueryVehResultEntity> resultList = new ArrayList<QueryVehResultEntity>();
			//nameList-----[XNGA_VEH_RESULT, FLAG_ID, VEH_LPN, VEH_TYPE, VEH_TN, VEH_COLOR, VEH_VIN, VEH_EN, VEH_RECORD, PPL_NAME, PPL_ID, REM]
			ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
			Log.v(TAG,"nameList-----"+nameList.toString());
			//valueList-----[, A0101420161281425560660599, A康0012, 电单车, JL50QZC-2B, , , JL1P44FMB2003003600, 未被盗, 陈建清, 450111581119001, ]
			ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
			Log.v(TAG,"valueList-----"+valueList.toString());
			//documentId-----[, A0101420161281425560660599, A康0012, 电单车, JL50QZC-2B, , , JL1P44FMB2003003600, 未被盗, 陈建清, 450111581119001, ]
			ArrayList<String> documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
			Log.v(TAG,"documentId-----"+valueList.toString());
			QueryVehResultEntity qVehResult = null;
			SQLDataEntity resultListSe = null;
			for (int i = 0; i < nameList.size(); i++) {
				if("XNGA_VEH_RESULT".equals(nameList.get(i))) {
					if(qVehResult != null) {
						qVehResult = null;
					}
					if(resultListSe != null) {
						resultListSe = null;
					}
					qVehResult = new QueryVehResultEntity();
					resultListSe = new SQLDataEntity();
					Log.v(TAG,"Constants.vehicleType ---" + Constants.vehicleType);
					if("0".equals(Constants.vehicleType)) {
						qVehResult.setCheckType(Constants.NVEH_CKS_ELECTROMOBILE);
						resultListSe.setCheckType(Constants.NVEH_CKS_ELECTROMOBILE);
					}
					else if("1".equals(Constants.vehicleType)) {
						resultListSe.setCheckType(Constants.NVEH_CKS_BICYCLE);
						qVehResult.setCheckType(Constants.NVEH_CKS_BICYCLE);
					}else if("2".equals(Constants.vehicleType)) {
						resultListSe.setCheckType(Constants.VEH_CKS_CAR);
						qVehResult.setCheckType(Constants.VEH_CKS_CAR);
					}else if("3".equals(Constants.vehicleType)) {
						resultListSe.setCheckType(Constants.VEH_CKS_MOTORCYCLE);
						qVehResult.setCheckType(Constants.VEH_CKS_MOTORCYCLE);
					}
				}
				if ("FLAG_ID".equals(nameList.get(i))) {
					qVehResult.setContentId(valueList.get(i));
					resultListSe.setContentId(valueList.get(i));
				}
				else if ("VEH_LPN".equals(nameList.get(i))) {
					String vehID = valueList.get(i);
					qVehResult.setCarId(vehID);
					resultListSe.setCarId(vehID);
				}else if ("VEH_TYPE".equals(nameList.get(i))) {
					String vehType = valueList.get(i);
					qVehResult.setVehType(vehType);
				}else if ("VEH_TN".equals(nameList.get(i))) {
					String vehName = valueList.get(i);
					qVehResult.setTypeName(vehName);
				}
				else if ("VEH_COLOR".equals(nameList.get(i))) {
					String vehColor = valueList.get(i);
					qVehResult.setVehColor(vehColor);
				}
				else if ("VEH_VIN".equals(nameList.get(i))) {
					String vehVIN = valueList.get(i);
					qVehResult.setVehVIN(vehVIN);
				}
				else if ("VEH_EN".equals(nameList.get(i))) {
					String engine = valueList.get(i);
					qVehResult.setVehEngine(engine);
				}else if ("VEH_RECORD".equals(nameList.get(i))) {
					String record = valueList.get(i);
					qVehResult.setVehRecord(record);
					resultListSe.setVehRecord(record);
				}else if ("PPL_NAME".equals(nameList.get(i))) {
					String ownerName = valueList.get(i);
					qVehResult.setOwnerName(ownerName);
					resultListSe.setName(ownerName);
				}else if ("PPL_ID".equals(nameList.get(i))) {
					String ownerId = valueList.get(i);
					qVehResult.setOwnerId(ownerId);
					resultListSe.setID(ownerId);
				}else if ("REM".equals(nameList.get(i))) {
					qVehResult.setInfo(valueList.get(i));
				}
			}
			resultListSe.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
			Log.v("fcr","qVehResult----"+qVehResult.toString());
			Log.v("fcr","resultListSe--"+resultListSe.toString());
			dataManager.insertQueryResultListItem(resultListSe);
			dataManager.insertQueryVehResultItem(qVehResult);
			resultList.add(qVehResult);
			
			return resultList;
		}

	}


