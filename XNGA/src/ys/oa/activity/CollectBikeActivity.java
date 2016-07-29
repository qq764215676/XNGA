package ys.oa.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import ys.oa.activity.CollectPeopleActivity.BDLocationListenerImpl;
import ys.oa.adapter.RidersInfoAdapter;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.IDInfoEntity;
import ys.oa.enity.QueryVehResultEntity;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.provider.SQLDataEntity;
import ys.oa.service.QueryService;
import ys.oa.task.QueryTask;
import ys.oa.util.Constants;
import ys.oa.util.L;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.anim.dialog.DialogLoading;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author cuiru.fan
 * 电动自行车和自行车检查
 *
 */
public class CollectBikeActivity extends BaseActivity implements OnClickListener {

	//mIvCollectImg 车辆采集图片
	private Activity activity;
	private ImageView mIvCollectImg,engineLine,vinLine,colorLine,ridersLine,latLine;
	private EditText mEtImgName, mEtTime, mEtLocation, mEtInfo, mEtOwnerName, mEtOwnerId,mEtBikeId,mEtBikeEngine,mEtBikeVIN,mEtBikeColor,mEtBikeTypeName;
	private EditText mEtLon , mEtLat;
	private ImageButton mBtnGetTime, mBtnGetLocation;
	private ImageView mDriverImg;
	private ListView  ridersInfoList;
	private CollectBikeActivity mActivity;
	private LinearLayout collectImgLayout;
	private Context context;
	
	private RelativeLayout colorLayout,vinLayout,engineLayout,typeNameLayout,ridersLayout,lonLayout,latLayot,bikeIdLayout;
	private Button btnReadDriverInfo,btnReadRidersInfo;
	private EnforcementDataManager enforcementDataManager;
	private QueryService queryService ;
	private AlertDialog recognizeDialog = null;
	private String ownerName ;//驾驶人姓名
	private String ownerId ;//驾驶人身份证号
	private String address;
	private String time ;
	private String info;//备注
	private String bikeId;//车牌号
	private String engineId;//发动机号
	private String vinId;//车架号
	private String startType ;//启动类型 分为 信息采集页面和查询结果页面
	private String mLon;
	private String mLat;
	
	private RidersInfoAdapter ridersInfoAdapter;
	private ArrayList<IDInfoEntity> ridersInfo;
	private DataSetList datasetlist;
	private AlertDialog loadingDialog;
	private CollectCarEnity bikeEntity;
	private SQLDataEntity sqlEntity;
	
	private StringBuilder imaKVBuilder = new StringBuilder();
    private StringBuilder nameBuilder = new StringBuilder();
    private StringBuilder IDBuilder = new StringBuilder();
    private StringBuilder imgPathBuilder = new StringBuilder();
    
    private static String ELECTRIC_BICYCLE = "0";
    private static String BICYCLE = "1";
    private String vehType ; //非机动车类型  “0” 表示电动车  “1”表示自行车
    
    private static int CAMERA_DRIVER = 4;
    public static int CAMERA_RIDERS = 5;
    
    private boolean isShowViwe;
    private static String NO_PICTURE_FLAG = "nopicture";

    //判断onResume前是否由拍照状态传过来
    private static boolean isFromCapture = true;
    private static String TAG = "CollectBikeActivity";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_bike_new);
		context = this;
		activity = this;
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		
		initVar();
		if (getIntent().getSerializableExtra("enity") != null) {
			isShowViwe = true;
		}else {
			isShowViwe = false;
		}
		initWidget();
		initListener();
		enforcementDataManager = EnforcementDataManager.getInstance(this);
		queryService = CollectPeopleListActivity.queryService;
		
		if (ELECTRIC_BICYCLE.equals(Constants.vehicleType)) {
			Constants.vehicleType = ELECTRIC_BICYCLE;
			vehType = ELECTRIC_BICYCLE ;
			setTitle("电动车信息采集");
			
		} else if (BICYCLE.equals(Constants.vehicleType)) {
			Constants.vehicleType = BICYCLE;
			vehType = BICYCLE;
			setTitle("自行车信息采集");
		} 
		
		if (isShowViwe) {
			if (ELECTRIC_BICYCLE.equals(Constants.vehicleType)) {
				setTitle("电动车信息");
				if(startType != null) {
					if(startType.equals("1")) {//电动车查询结果
						QueryVehResultEntity enity = (QueryVehResultEntity)getIntent().getSerializableExtra("enity");
						collectImgLayout.setVisibility(View.GONE);
						colorLayout.setVisibility(View.VISIBLE);
						vinLayout.setVisibility(View.VISIBLE);
						engineLayout.setVisibility(View.VISIBLE);
						typeNameLayout.setVisibility(View.VISIBLE);
						ridersLayout.setVisibility(View.GONE);
						btnReadDriverInfo.setVisibility(View.GONE);
						mEtOwnerName.setHint("");
						mEtOwnerId.setHint("");
						mEtBikeId.setHint("");
						mEtBikeVIN.setHint("");
						mEtBikeColor.setHint("");
						mEtBikeEngine.setHint("");
						mEtBikeId.setText(enity.getCarId());
						mEtBikeEngine.setText(enity.getVehEngine());
						mEtBikeColor.setText(enity.getVehColor());
						mEtBikeVIN.setText(enity.getVehVIN());
						mEtOwnerName.setText(enity.getOwnerName());
						mEtOwnerId.setText(enity.getOwnerId());
						mDriverImg.setVisibility(View.GONE);
						mEtTime.setVisibility(View.GONE);
						mEtBikeTypeName.setText(enity.getTypeName());
						mEtLocation.setVisibility(View.GONE);
						mEtInfo.setText(enity.getVehRecord());
						engineLine.setVisibility(View.VISIBLE);
						vinLine.setVisibility(View.VISIBLE);
						colorLine.setVisibility(View.VISIBLE);
						latLine.setVisibility(View.GONE);
						lonLayout.setVisibility(View.GONE);
						latLayot.setVisibility(View.GONE);
					}
				}else {
					collectImgLayout.setVisibility(View.VISIBLE);
					colorLayout.setVisibility(View.GONE);
					vinLayout.setVisibility(View.GONE);
					engineLayout.setVisibility(View.GONE);
					typeNameLayout.setVisibility(View.GONE);
					engineLine.setVisibility(View.GONE);
					vinLine.setVisibility(View.GONE);
					colorLine.setVisibility(View.GONE);
					btnReadDriverInfo.setVisibility(View.GONE);
					btnReadRidersInfo.setVisibility(View.GONE);
					CollectCarEnity enity = (CollectCarEnity)getIntent().getSerializableExtra("enity");
					mEtOwnerName.setText(enity.getOwnerName());
					mEtOwnerId.setText(enity.getOwnerId());
					mEtBikeId.setText(enity.getCarId());
					mEtTime.setText(enity.getTime());
					mEtLocation.setText(enity.getAddress());
					mEtLon.setText(enity.getLon());
					mEtLat.setText(enity.getLat());
					mEtInfo.setText(enity.getInfo());
					if(enity.getRidersInfo() == null) {
						ridersLayout.setVisibility(View.GONE);
					}
					Util.displayLocalImg(CollectBikeActivity.this, mDriverImg, enity.getIdImg(), ".jpg");
					ridersInfoAdapter.notify(enity.getRidersInfo());
					Util.setListViewHeight(ridersInfoList);
					Util.displayLocalImg(mActivity, mIvCollectImg, enity.getImgKeys(), "png");
				}
			} else if (BICYCLE.equals(Constants.vehicleType)) {
				setTitle("自行车信息");
				collectImgLayout.setVisibility(View.VISIBLE);
				colorLayout.setVisibility(View.GONE);
				vinLayout.setVisibility(View.GONE);
				engineLayout.setVisibility(View.GONE);
				typeNameLayout.setVisibility(View.GONE);
				bikeIdLayout.setVisibility(View.GONE);
				if(startType != null) {
					if(startType.equals("1")) {//自行车查询结果
						collectImgLayout.setVisibility(View.GONE);
						btnReadDriverInfo.setVisibility(View.GONE);
						ridersLayout.setVisibility(View.GONE);
						lonLayout.setVisibility(View.GONE);
						latLayot.setVisibility(View.GONE);
						ridersLine.setVisibility(View.GONE);
						QueryVehResultEntity enity = (QueryVehResultEntity)getIntent().getSerializableExtra("enity");
						mEtOwnerName.setText(enity.getOwnerName());
						mEtOwnerId.setText(enity.getOwnerId());
						mEtTime.setText(enity.getTime());
						mEtLocation.setText(enity.getAddress());
						mEtInfo.setText(enity.getInfo());
					}
				}else {
					CollectCarEnity enity =  (CollectCarEnity)getIntent().getSerializableExtra("enity");
					btnReadDriverInfo.setVisibility(View.GONE);
					mEtOwnerName.setText(enity.getOwnerName());
					mEtOwnerId.setText(enity.getOwnerId());
					mEtTime.setText(enity.getTime());
					mEtLocation.setText(enity.getAddress());
					mEtInfo.setText(enity.getInfo());
					mEtLon.setText(enity.getLon());
					mEtLat.setText(enity.getLat());
					ridersLayout.setVisibility(View.GONE);
					Util.displayLocalImg(mActivity, mIvCollectImg, enity.getImgKeys(), "png");
					Util.displayLocalImg(CollectBikeActivity.this, mDriverImg, enity.getIdImg(), ".jpg");
				}
			}
			Util.setAllFocusable(new View[] { mEtOwnerName, mEtOwnerId, mEtTime, mEtLocation, mEtInfo, mIvCollectImg,
					mBtnGetTime, mBtnGetLocation,mEtBikeId,mEtBikeEngine,mEtBikeVIN,
					mEtBikeColor ,mEtBikeTypeName}, false);
			mBtnGetTime.setVisibility(View.GONE);
			mBtnGetLocation.setVisibility(View.GONE);
		}else {
			isShowViwe = false;
			initLocation();
			colorLayout.setVisibility(View.GONE);
			vinLayout.setVisibility(View.GONE);
			engineLayout.setVisibility(View.GONE);
			typeNameLayout.setVisibility(View.GONE);
			if(BICYCLE.equals(Constants.vehicleType)) {
				ridersLayout.setVisibility(View.GONE);
				bikeIdLayout.setVisibility(View.GONE);
			}
		}
	}

	public void initVar() {
		mActivity = CollectBikeActivity.this;
		Util.hasSdcard();
		if(getIntent().hasExtra("type")) {
			startType = getIntent().getStringExtra("type").toString();
		}
	}

	public void initWidget() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		collectImgLayout = (LinearLayout)findViewById(R.id.collect_layout);
		mIvCollectImg = (ImageView) findViewById(R.id.iv_collect_img);
		mEtImgName = (EditText) findViewById(R.id.et_img_name);
		mEtTime = (EditText) findViewById(R.id.et_car_time);
		mEtLocation = (EditText) findViewById(R.id.et_car_location);
		mEtInfo = (EditText) findViewById(R.id.et_info);
		mEtOwnerName = (EditText) findViewById(R.id.et_car_owner_name);
		mEtOwnerId = (EditText) findViewById(R.id.et_car_owner_id);
		mDriverImg = (ImageView)findViewById(R.id.owner_id_img);
		mBtnGetTime = (ImageButton) findViewById(R.id.btn_get_time);
		mBtnGetLocation = (ImageButton) findViewById(R.id.btn_get_gps);
	
		engineLine = (ImageView)findViewById(R.id.color_line);
		vinLine = (ImageView)findViewById(R.id.vin_line);
		colorLine = (ImageView)findViewById(R.id.engine_line);
		latLine = (ImageView)findViewById(R.id.lat_line);
		ridersLine = (ImageView)findViewById(R.id.riders_line);
		
		bikeIdLayout = (RelativeLayout)findViewById(R.id.bike_id_layout);
		ridersLayout = (RelativeLayout)findViewById(R.id.riders_layout);
		colorLayout = (RelativeLayout)findViewById(R.id.veh_color_layout);
		vinLayout = (RelativeLayout)findViewById(R.id.veh_vin_layout);
		engineLayout = (RelativeLayout)findViewById(R.id.veh_engine_layout);
		typeNameLayout = (RelativeLayout)findViewById(R.id.veh_typename_layout);
		lonLayout = (RelativeLayout)findViewById(R.id.lon_layout);
		latLayot = (RelativeLayout)findViewById(R.id.lat_layout);
		
		mEtBikeId = (EditText)findViewById(R.id.et_bike_id);
		mEtBikeEngine = (EditText)findViewById(R.id.et_bike_engine_id);
		mEtBikeVIN = (EditText)findViewById(R.id.et_bike_vin_id);
		mEtBikeColor = (EditText)findViewById(R.id.et_car_color);
		mEtBikeTypeName = (EditText)findViewById(R.id.et_car_typename);
		
		
		mEtLon = (EditText)findViewById(R.id.et_long);
		mEtLat = (EditText)findViewById(R.id.et_lat);
		
		
		ridersInfoList = (ListView)findViewById(R.id.riders_info_list);
		btnReadDriverInfo = (Button)findViewById(R.id.read_driver_info);
		btnReadRidersInfo = (Button)findViewById(R.id.read_riders_info);
		
		ridersInfo = new ArrayList<IDInfoEntity>();
		ridersInfoAdapter = new RidersInfoAdapter(RidersInfoAdapter.TYPE_NVEH_CKS,activity,context, ridersInfo,ridersInfoList,isShowViwe);
		ridersInfoList.setAdapter(ridersInfoAdapter);
		
		
		//test data
		/*mEtOwnerId.setText("452226197410056034");
		mEtOwnerName.setText("1515");
		filePath = "/storage/sdcard0/XNGA/files/20160315163122321.jpg";
		mIvCollectImg.setTag(filePath);
		mEtLon.setText("33.2");
		mEtLat.setText("22.2");
		mEtImgName.setText("20000103092824550");*/
	}

	public void initListener() {
		mIvCollectImg.setOnClickListener(this);
		mBtnGetTime.setOnClickListener(this);
		mBtnGetLocation.setOnClickListener(this);
		btnReadDriverInfo.setOnClickListener(this);
		btnReadRidersInfo.setOnClickListener(this);
	}
	
	private void initLocation(){
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(new BDLocationListenerImpl());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);
		option.setCoorType("bd09ll");
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");
		option.setScanSpan(1000);
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_collect_img: 
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
			startActivityForResult(intent, Constants.PHOTOHRAPH);
			break;
		case R.id.btn_get_owner:
			// mEtOwnerName.setText(getLoc());
			// mEtOwnerId.setText(getLoc());
			break;
		case R.id.btn_get_car_id:
			// mEtCarId.setText(getLoc());
			break;
		case R.id.btn_get_time:
			mEtTime.setText(Util.getNowTime("yyyy-MM-dd HH:mm:ss"));
			break;
		case R.id.btn_get_gps:
			mEtLocation.setText(addr);
			break;
		case R.id.read_driver_info:
			Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			photoName = Util.getDateTime() + ".jpg";
			it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
			startActivityForResult(it,CAMERA_DRIVER);
			break;
		case R.id.read_riders_info:
			saveRidersInformation();
			IDInfoEntity enity = new IDInfoEntity();
			ridersInfo.add(enity);
			ridersInfoAdapter.notify(ridersInfo);
			Util.setListViewHeight(ridersInfoList);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collect, menu);
		if (getIntent().getSerializableExtra("enity") != null) {
			menu.findItem(R.id.action_upload).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_upload:
			//Updated at 2016/03/25
			//增加车主、身份证、车牌号无内容提示
			if (TextUtils.isEmpty(mEtOwnerName.getText().toString())) {
				T.showSnack(this, "请填写车主姓名");
			}else if (TextUtils.isEmpty(mEtOwnerId.getText().toString())) {
				T.showSnack(this, "请填写车主身份证号");
			}else if (TextUtils.isEmpty(mEtTime.getText().toString())) {
				T.showSnack(this, "请填写采集时间");
			} else if (TextUtils.isEmpty(mEtLocation.getText().toString())) {
				T.showSnack(this, "请填写采集地点");
			} else if (TextUtils.isEmpty(mEtImgName.getText().toString()) || mIvCollectImg.getTag() == null || "".equals(mIvCollectImg.getTag().toString())) {
				T.showSnack(this, "请采集图片");
			}/* else if(TextUtils.isEmpty(mEtBikeId.getText().toString()) && TextUtils.isEmpty(mEtBikeEngine.getText().toString()) && TextUtils.isEmpty(mEtBikeVIN.getText().toString())) {
				T.showSnack(this, "请采集车牌或者发动机或者车架号中任意一种");
			}*/
			else if(!isDriverInfoComplete()) {
				T.showSnack(this, "驾驶人信息采集不完全");
			}else if(!isRidersInfoComplete()) {
				T.showSnack(this, "同车人信息采集不完全");
			}else if(TextUtils.isEmpty(mEtLon.getText().toString()) || TextUtils.isEmpty(mEtLat.getText().toString())) {
				T.showSnack(this, "请获取经度和纬度");
			}
			else {
				if(vehType.equals(ELECTRIC_BICYCLE)) {
					if(TextUtils.isEmpty(mEtBikeId.getText().toString())) {
						T.showSnack(this, "请采集车牌");
					}else {
						upload();
					}
				}else {
					upload();
				}
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void upload() {
		// 本地新增
		if (Util.hasSdcard()) {
			if (photo != null) {
				try {
					File file = new File(filePath);
					if (!file.exists()) {
						file.createNewFile();
					}
					if (filePath.endsWith(".jpg")) {
						FileOutputStream fos = new FileOutputStream(file);
						if (null != fos) {
							photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
							fos.flush();
							fos.close();
						}
					}
				} catch (Exception e) {
					}
			}
			if (Util.isNetworkAvailable(mActivity)) {
				//上传车辆图片
//				new AsyncLoader().execute("uploadImg",mIvCollectImg.getTag().toString(),Util.getTypeByEnd(mIvCollectImg.getTag().toString()),vehType);	
				//上传驾驶人身份证图片
				if(mDriverImg.getTag() != null) {
					if(mDriverImg.getTag().toString() != null && !NO_PICTURE_FLAG.equals(mDriverImg.getTag().toString())) {
						new AsyncLoader().execute("uploadImg",mDriverImg.getTag().toString(),Util.getTypeByEnd(mDriverImg.getTag().toString()),vehType);	
					}else {
						sendMsg(0,SUCCESS);
					}
				}else {
					sendMsg(0,SUCCESS);
				}
				//上传同车人身份证图片
				if(ridersInfo != null && ridersInfo.size()>0) {
					for(int i=0;i<ridersInfo.size();i++) {
						String path = ridersInfo.get(i).getImgPath();
						if(path != null) {
							if(!path.equals(NO_PICTURE_FLAG)) {
								new AsyncLoader().execute("uploadImg",path.toString(),Util.getTypeByEnd(path),vehType);
							}else {
								sendMsg(0, SUCCESS);
							}
						}else {
							sendMsg(0,SUCCESS);
						}
					}
				}
			} else {
				T.showSnack(mActivity, R.string.networkerror);
			}
		} else {
			T.showSnack(this, R.string.sdCardDisabled);
		}
	
		
	}

	class AsyncLoader extends AsyncTask<String, Integer, Integer> {
		//非机动车要分别按人员查询，机动车查询
		protected Integer doInBackground(String... params) {
			 if("uploadImg".equals(params[0])) {
					String vaule = Util.getDateTime();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("IMA_VAL", vaule);
					map.put("IMA_KEY", "1");
					try {
						datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("",
								"XNGA_IMA"), false, params[1], params[2]));
					} catch (Exception e) {
						return -1;
					}
					return 1;
				}else if ("upload".equals(params[0])) {//上传采集数据
				getuploadInfo(ridersInfo);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PPL_NAME",nameBuilder.toString() );
				map.put("PPL_ID", IDBuilder.toString());
				map.put("CKS_ADD", address);
				map.put("NVEH_LPN", bikeId);
				map.put("CKS_TIME",time);
				map.put("PPL_LONG",mLon);
				map.put("PPL_LAT", mLat);
				map.put("REM",info);
				if (BICYCLE.equals(params[3])) {
					map.put("NVEH_TYPE",Constants.NVEH_CKS_BICYCLE);
				} else if (ELECTRIC_BICYCLE.equals(params[3])) {
					map.put("NVEH_TYPE",Constants.NVEH_CKS_ELECTROMOBILE);
				}
				map.put("USER_ID", Constants.USER_ID);
				map.put("IMA_KV", imaKVBuilder.toString());
				Log.v(TAG,"imaKVBuilder----"+ imaKVBuilder.toString());
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("",
							"XNGA_NVEH_CKS "), false, params[1], params[2]));
					if(bikeEntity != null) {
						bikeEntity = null;
					}
					if(sqlEntity != null) {
						sqlEntity = null;
					}
					sqlEntity = new SQLDataEntity();
					String checkType = null;
					if (BICYCLE.equals(vehType)) {
						checkType = Constants.NVEH_CKS_BICYCLE;//自行车
						sqlEntity.setCheckType(checkType);
					} else if (ELECTRIC_BICYCLE.equals(vehType)) {
						checkType = Constants.NVEH_CKS_ELECTROMOBILE;//电动车
						sqlEntity.setCheckType(checkType);
					}
					bikeEntity = new CollectCarEnity(Constants.USER_ID,nameBuilder.toString(),IDBuilder.toString(),bikeId,time,address,checkType,info,imgPathBuilder.toString());
					bikeEntity.setLon(mLon);
					bikeEntity.setLat(mLat);
					sqlEntity.setName(ownerName);
					sqlEntity.setID(ownerId);
					sqlEntity.setCarId(bikeId);
					sqlEntity.setTime(time);
					sqlEntity.setIDImagePath(mIvCollectImg.getTag().toString());
					sqlEntity.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
				} catch (Exception e) {
					return -1;
				}
				return 2;
			}
			else if("insert_ppl_req".equals(params[0])) {//插入人员检查请求表
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PPL_ID", params[1]);
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("",
							"XNGA_PPL_REQ"), false,"",""));
				} catch (Exception e) {
					return -1;
				}
				Log.v(TAG,"vehType--"+params[2]);
				if(params[2] != null) {
					if(params[2].equals(ELECTRIC_BICYCLE)) {//如果是电动车执行插入机动车检查请求表，如果是自行车则返回人员检查结果
						return 3;
					}else {
						return 5;
					}
				}
			}else if("insert_ppl_req_riders".equals(params[0])) {//同车人检查  插入人员检查请求表
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PPL_ID", params[1]);
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("",
							"XNGA_PPL_REQ"), false,"",""));
				} catch (Exception e) {
					return -1;
				}
				return 4;
			}else if("insert_veh_req".equals(params[0])) {//插入机动车检查请求表
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("VEH_LPN", params[1]);
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("",
							"XNGA_VEH_REQ"), false,"",""));
				} catch (Exception e) {
					return -1;
				}
				return 5;
			}
			return 0;
		}

		protected void onPreExecute() {
			if(loadingDialog == null) {
				loadingDialog = DialogLoading.getProgressDialog(mActivity, "正在上传");
			}
		}

		protected void onPostExecute(Integer result) {
			switch (result) {
			case -1:// 异步NullPointerException
				T.showSnack(mActivity, R.string.serverFailed);
				break;
			case 1:
				if(Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					Log.v(TAG,"上传成功");
					String contentId = datasetlist.CONTENTID.get(0);
					imaKVBuilder.append(contentId+",");
					sendMsg(0,SUCCESS);
				}else {
					Log.v(TAG,"上传失败");
					sendMsg(0,FAILED);
					if(loadingDialog != null) {
					   loadingDialog.cancel();
					}
				}
				break;
			case 2:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					if (Util.isNetworkAvailable(mActivity)) {
						//执行插入人员检查请求表
						if(ownerId != null) {
							new AsyncLoader().execute("insert_ppl_req",ownerId,vehType);
						}
					} else {
						T.showSnack(mActivity, R.string.networkerror);
					}
				} else {
					if(loadingDialog != null) {
						loadingDialog.cancel();
					}
				}
				break;
			case 3:
				if(Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) { 
					String contentId = datasetlist.CONTENTID.get(0);
					queryService.startTimer(QueryTask.QUERY_PEOPLE, contentId);
					if(ridersInfo != null && ridersInfo.size()>0) {//有同车人信息
						if(ridersInfo.size()>0) {
							for(int i=0;i<ridersInfo.size();i++) {
								String id = ridersInfo.get(i).getId();
								new AsyncLoader().execute("insert_ppl_req_riders",id,vehType);
							}
						}
					}else {//无同车人
						new AsyncLoader().execute("insert_veh_req",bikeId);
					}
				}else {
					if(loadingDialog != null) {
						loadingDialog.cancel();
					}
				}
				break;
			case 4:
				if(Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					sendMsg(3, SUCCESS);
					String contentId = datasetlist.CONTENTID.get(0);
					queryService.startTimer(QueryTask.QUERY_PEOPLE, contentId);
				}else {
					sendMsg(3,FAILED);
					if(loadingDialog != null) {
						loadingDialog.cancel();
					}
				}
				break;
			case 5:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					String contentId = datasetlist.CONTENTID.get(0);
					if(vehType != null) {
						if(vehType.equals(ELECTRIC_BICYCLE)) {//如果是电动车启动机动车查询任务
							queryService.startTimer(QueryTask.QUERY_VEH, contentId);
						}else {//如果是自行车启动人员检查查询任务
							queryService.startTimer(QueryTask.QUERY_PEOPLE, contentId);
						}
					}
					if (Util.isNetworkAvailable(mActivity)) {
						if(bikeEntity != null) {
							bikeEntity.setContentId(contentId);
							enforcementDataManager.insertVEHItem(bikeEntity);
						}
						if(sqlEntity != null) {
							sqlEntity.setContentId(contentId);
							enforcementDataManager.insertEnforcementItem(sqlEntity);
						}
						if(loadingDialog != null) {
							loadingDialog.cancel();
						}
						setResult(RESULT_OK);
						onBackPressed();
					} else {
						T.showSnack(mActivity, R.string.networkerror);
					}
				} else {
					T.showSnack(mActivity, "文件上传失败");
					if(loadingDialog != null) {
						loadingDialog.cancel();
					}
				}
				break;
			}
		}
	}

	// 百度定位
	private LocationClient mLocationClient;
	private String addr;
	
	
    public class BDLocationListenerImpl implements BDLocationListener {  
  
        @Override  
        public void onReceiveLocation(BDLocation location) {  
            if (location == null) {  
                return;  
            }  
              
            StringBuffer sb = new StringBuffer(256);  
              sb.append("time : ");  
              sb.append(location.getTime());  
              sb.append("\nerror code : ");  
              sb.append(location.getLocType());  
              sb.append("\nlatitude : ");  
              sb.append(location.getLatitude());  
              sb.append("\nlontitude : ");  
              sb.append(location.getLongitude());  
              sb.append("\nradius : ");  
              sb.append(location.getRadius());  
              if (location.getLocType() == BDLocation.TypeGpsLocation){  
                   sb.append("\nspeed : ");  
                   sb.append(location.getSpeed());  
                   sb.append("\nsatellite : ");  
                   sb.append(location.getSatelliteNumber());  
                   } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){  
                   sb.append("\naddr : ");  
                   sb.append(location.getAddrStr());  
                }   
           
//              Log.e("log", sb.toString());  
              
              
            addr = location.getAddrStr();
            mEtLat.setText(location.getLatitude()+"");
            mEtLon.setText(location.getLongitude()+"");
            
        }
            @Override  
            public void onReceivePoi(BDLocation poiLocation) {  
                  
            }  
         
        }  

	/*private String getLoc() {
		mLocationClient = ((AppData) getApplication()).mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);
		option.setCoorType("bd09ll");
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");
		option.setScanSpan(1000);
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		mLocationClient.stop();
		return ((AppData) getApplication()).getLoc();
	}*/

	private String photoName = Util.getDateTime() + ".jpg"; // 原始照片名
	private Bitmap photo;
	private String filePath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		// 拍照
		if (Util.hasSdcard()) {
			File picture = new File(Constants.localPath + photoName);
			System.out.println(Constants.localPath + photoName);
		} else {
			T.showSnack(this, R.string.sdCardDisabled);
		}
		//if (TextUtils.isEmpty(mEtImgName.getText().toString()))
		//	mEtImgName.setText(photoName);
		if (photoName.indexOf(".jpg") == -1) {
			photoName += ".jpg";
		}
		mEtImgName.setText(photoName);
		filePath = Constants.localPath + photoName;
		
		//获取存储再XNGA/files下的缩小过的图片
		photo = Util.getSmallBitmap(filePath);
		//获取图片是否需要旋转，旋转的角度是多少
		int angle = Util.readPictureDegree(filePath);
		System.out.println("angle "+angle);
		//如果需要旋转，对图片进行旋转保存
		photo = Util.rotaingImageView(angle,photo);
		//对图片进行分辨率压缩，并保存至文件夹
		filePath = Util.savaPhotoToLocal(data,photo);
		if (requestCode == Constants.PHOTOHRAPH) {
			// 设置文件保存路径这里放在跟目录下
			L.i("拍照photoName#########" + photoName);
			if (Util.hasSdcard()) {
				if (photo != null) {
					try {
						File file = new File(filePath);
						if (!file.exists()) {
							file.createNewFile();
						}
					} catch (Exception e) {
					}
						Log.v("filePath", filePath);
						mIvCollectImg.setBackgroundColor(Color.TRANSPARENT);
						if(vehType == ELECTRIC_BICYCLE) {
							recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
							Util.uploadAndRecognize(filePath,-1, handler, 4);
						}
						Util.displayLocalImg(CollectBikeActivity.this, mIvCollectImg, filePath, ".jpg");
						mIvCollectImg.setTag(filePath);
						//拍照采集成功后返回当前Activity，对onResume做判断
						isFromCapture = true;
				}
			} else {
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		else if (requestCode == CAMERA_DRIVER) {
			// 设置文件保存路径这里放在跟目录下
			if (Util.hasSdcard()) {
				recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
				if (photo != null) {
					try {
						File file = new File(filePath);
						if (!file.exists()) {
							file.createNewFile();
						}
					} catch (Exception e) {
					}
						Log.v("filePath", filePath);
						mDriverImg.setBackgroundColor(Color.TRANSPARENT);
						Util.displayLocalImg(CollectBikeActivity.this, mDriverImg, filePath, ".jpg");
						mDriverImg.setTag(filePath);
						Util.uploadAndRecognize(filePath,-1, handler, 1);
						//拍照采集成功后返回当前Activity，对onResume做判断
						isFromCapture = true;
				}
			} else {
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		else if (requestCode == CAMERA_RIDERS) {
			Log.v(TAG,"position---"+position);
			if (Util.hasSdcard()) {
				recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
				if (photo != null) {
					try {
						File file = new File(filePath);
						if (!file.exists()) {
							file.createNewFile();
						}
					} catch (Exception e) {
					}
						Log.v("filePath", filePath);
						Util.uploadAndRecognize(filePath, position,handler, 2);
						//拍照采集成功后返回当前Activity，对onResume做判断
						isFromCapture = true;
				}
			} else {
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		
		// 相册
		if (requestCode == Constants.PHOTOZOOM) {
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); // ����ǻ���û�ѡ���ͼƬ������ֵ
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			photoName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
			L.i("相册photoName#########" + photoName);
			// startPhotoZoom(data.getData());
			if (TextUtils.isEmpty(mEtImgName.getText().toString()))
				mEtImgName.setText(photoName);
			if (photoName.indexOf(".jpg") == -1) {
				photoName += ".jpg";
			}
			mEtImgName.setText(photoName);
			filePath = Constants.localPath + photoName;
		}
		// 图片处理结果
		if (requestCode == Constants.PHOTORESULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				photo = extras.getParcelable("data");
				mIvCollectImg.setBackgroundColor(Color.TRANSPARENT);
				if (TextUtils.isEmpty(mEtImgName.getText().toString()))
					mEtImgName.setText(photoName);
				mIvCollectImg.setImageBitmap(photo);
				if (photoName.indexOf(".jpg") == -1) {
					photoName += ".jpg";
				}
				L.i("处理后photoName#########" + photoName);
				mEtImgName.setText(photoName);
				filePath = Constants.localPath + photoName;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override  
    public void onConfigurationChanged(Configuration newConfig) {  
        // TODO Auto-generated method stub  
        Log.i("CollectBikeActivity", "onConfigurationChanged");  
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {  
            Log.i("CollectBikeActivity", "横屏");  
            Configuration o = newConfig;  
            o.orientation = Configuration.ORIENTATION_PORTRAIT;  
            newConfig.setTo(o);  
        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {  
            Log.i("CollectBikeActivity", "竖屏");  
        }  
        super.onConfigurationChanged(newConfig);  
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(Constants.vehicleType != null) {
			if(Constants.vehicleType.equals(ELECTRIC_BICYCLE)) {
				vehType = ELECTRIC_BICYCLE;
			}else if(Constants.vehicleType.equals(BICYCLE)) {
				vehType = BICYCLE;
			}
		}
		
		Log.v ("isFromCapture", isFromCapture+"");
		if (isFromCapture){
			// 拍照成功后返回页面，不显示九宫格解锁
			LockApplication.notShowLock = true;
		} else {
			// 显示九宫格解锁
			LockApplication.notShowLock = false;
		}
		
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		Log.v ("isFromCapture onRestart", isFromCapture+"");
		if (isFromCapture){
			// 拍照成功后返回页面，不显示九宫格解锁
			LockApplication.notShowLock = true;
			LockApplication.isActive = false;
			isFromCapture = false;
		} else {
			// 显示九宫格解锁
			LockApplication.notShowLock = false;
		}
		
	}
	
	public void back() {
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
	
    public boolean isDriverInfoComplete() {
    	if(mEtOwnerId.getText().toString() == null && "".equals(mEtOwnerId.getText().toString())) {
    		return false;
    	}
    	else if(mEtOwnerName.getText().toString() == null && "".equals(mEtOwnerName.getText().toString())) {
    		return false;
    	}
    	/*else if(mDriverImg.getTag() == null || "".equals(mDriverImg.getTag().toString())) {
    		return false;
    	}*/
    	return true;
    }
    
    /**
     * 判断同车人数据是否完整
     * @return
     */
    public boolean isRidersInfoComplete() {
    	if(ridersInfo.size() != 0) {
			 for (int i = 0; i < ridersInfoList.getChildCount(); i++) {
			      LinearLayout layout = (LinearLayout)ridersInfoList.getChildAt(i);// 获得子item的layout
			      EditText etRiderName = (EditText) layout.findViewById(R.id.et_riders_name);// 从layout中获得控件,根据其id
			      EditText etRiderId = (EditText) layout.findViewById(R.id.et_riders_id);
			      System.out.println("the text of "+i+"'s etRiderName：----------->"+etRiderName.getText());
			      System.out.println("the text of "+i+"'s etRiderId：----------->"+etRiderId.getText());
			      if(etRiderName.getText().toString().equals("") || etRiderId.getText().toString().equals("")) {
			    	  return false;
			      }
			      return true;
			 }
    	}
    	return true;
    }
    
    /**
     * 获取要上传的信息
     * @param list 同车人列表信息
     */
    public void getuploadInfo(ArrayList<IDInfoEntity> list) {
	   	 ownerName = mEtOwnerName.getText().toString();
		 ownerId = mEtOwnerId.getText().toString();
		 address = mEtLocation.getText().toString();
		 time =  mEtTime.getText().toString();
		 info =  mEtInfo.getText().toString();
		 bikeId = mEtBikeId.getText().toString();
		 mLon = mEtLon.getText().toString();
		 mLat = mEtLat.getText().toString();
		/* engineId = mEtBikeEngine.getText().toString();
		 vinId = mEtBikeVIN.getText().toString();*/
		nameBuilder.append(ownerName);
		IDBuilder.append(ownerId);
		if(mIvCollectImg.getTag() != null) {
			imgPathBuilder.append(mIvCollectImg.getTag().toString());
		}
		if(mDriverImg.getTag() != null) {
			imgPathBuilder.append("," + mDriverImg.getTag().toString());
		}else {
			imgPathBuilder.append("," + NO_PICTURE_FLAG);
		}
		 for(int i=0;i<ridersInfoList.getChildCount();i++) {
			      LinearLayout layout = (LinearLayout)ridersInfoList.getChildAt(i);// 获得子item的layout
			      EditText etRiderName = (EditText) layout.findViewById(R.id.et_riders_name);// 从layout中获得控件,根据其id
			      EditText etRiderId = (EditText) layout.findViewById(R.id.et_riders_id);
			      if(etRiderName.getText().equals("") || etRiderId.getText().equals("")) {
			    	  T.showSnack(this, "同车人信息采集不完全");
			    	  return ;
			      }else {
			    	  nameBuilder.append(","+etRiderName.getText().toString());
			    	  ridersInfo.get(i).setName(etRiderName.getText().toString());
			    	  IDBuilder.append(","+etRiderId.getText());
			    	  ridersInfo.get(i).setId(etRiderId.getText().toString());
			    	  if(ridersInfo.get(i).getImgPath() == null) {
			    		  imgPathBuilder.append(","+ NO_PICTURE_FLAG);
			    	  }else {
			    		  imgPathBuilder.append(","+ridersInfo.get(i).getImgPath());
			    	  }
			      }
		 }
		 if(imaKVBuilder.toString().contains(",")) {
			 imaKVBuilder.substring(0,imaKVBuilder.lastIndexOf(","));
		 }
	    Log.v(TAG,"imaKVBuilder---"+imaKVBuilder.toString());
    }
    
   public static int FAILED = 0;
   public static int SUCCESS = 1;
    public void sendMsg(int index,int flag) {
    	Message m = new Message();
    	m.what = index;
    	m.obj = flag;
    	handler.sendMessage(m);
    }
    
    int count = 0;
    int riderCheckNum= 0;
    private boolean isAllSuccess = true;
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		switch(msg.what) {
			case 0://图片上传完成
				count++;
				int isSuccess =  (Integer) msg.obj;
				if(isSuccess == FAILED) {
					isAllSuccess = false;
				}
				if(count == ridersInfo.size() + 1) {
					if(isAllSuccess) {
						if (Util.isNetworkAvailable(mActivity)) {
							new AsyncLoader().execute("upload",mIvCollectImg.getTag().toString(),Util.getTypeByEnd(mIvCollectImg.getTag().toString()),vehType);
						} else {
							T.showSnack(mActivity, R.string.networkerror);
						}
					}else {
						Log.v(TAG,"有失败");
					}
				}
				break;
			case 1:
				//驾驶人身份信息检查完成
				recognizeDialog.cancel();
				Log.v("Info ",msg.obj.toString());
				if(msg.obj.toString().equals("fail")) {
					Toast.makeText(context, "身份证照识别失败，请重新拍！", Toast.LENGTH_SHORT).show();
				}
				else { Map<String,String> infoMap = (Map<String, String>) msg.obj;
	                if(infoMap.get("name").equals("") || infoMap.get("cardno").equals("")) {
	                	Toast.makeText(context, "身份证照不清晰，请重新拍！", Toast.LENGTH_SHORT).show();
	                }else {
		                mEtOwnerName.setText(infoMap.get("name"));
		                mEtOwnerId.setText(infoMap.get("cardno"));
	                }
				}
				break;
			case 2:
				//同车人身份信息检查完成
				recognizeDialog.cancel();
				Log.v("Info ",msg.obj.toString());
				if(msg.obj.toString().equals("fail")) {
					Toast.makeText(context, "身份证照识别失败，请重新拍！", Toast.LENGTH_SHORT).show();
				}else {
	                Map<String,String> infosMap = (Map<String, String>) msg.obj;
                	String imgPath = infosMap.get("imgPath");
                	String position = infosMap.get("position");
                	IDInfoEntity e = ridersInfo.get(Integer.parseInt(position));
                	e.setName(infosMap.get("name"));
                	e.setId(infosMap.get("cardno"));
                	e.setImgPath(imgPath);
	                if(ridersInfoAdapter == null) {
	    				ridersInfoAdapter = new RidersInfoAdapter(RidersInfoAdapter.TYPE_NVEH_CKS,activity,context, ridersInfo,ridersInfoList,false);
	    				ridersInfoList.setAdapter(ridersInfoAdapter);
	    			}else {
	    				ridersInfoAdapter.notify(ridersInfo);
	    			}
	    			Util.setListViewHeight(ridersInfoList);
	    			if(infosMap.get("name").equals("") || infosMap.get("cardno").equals("")) {
	                	Toast.makeText(context, "身份证照不清晰，请重新拍！", Toast.LENGTH_SHORT).show();
	                }
				}
				break;
			case 3:
				riderCheckNum++;
				int isNext =  (Integer) msg.obj;
				if(isNext == FAILED) {
					isAllSuccess = false;
				}
				if(riderCheckNum == ridersInfo.size()) {
					if(isAllSuccess) {
						Log.v(TAG,"全部插入检查表完成");
						if(Util.isNetworkAvailable(mActivity)) {
							///执行插入机动车检查请求    参数待定
							if(bikeId != null) {
								new AsyncLoader().execute("insert_veh_req",bikeId);
							}
						}else {
							T.showSnack(mActivity, R.string.networkerror);
						}
					}else {
						Log.v(TAG,"有失败");
					}
				}
				break;
			case 4:
				//车牌识别
                 //mTextView.setText(msg.obj.toString());
				recognizeDialog.cancel();
				if(msg.obj.toString().equals("fail")) {
					Toast.makeText(context, "车牌识别失败，请重新拍！", Toast.LENGTH_SHORT).show();
				}else {
	                Map<String,String> infoMap = (Map<String, String>) msg.obj;
	                Log.v("Info ",msg.obj.toString());
	                if(infoMap.get("number").equals("")){
	                 	T.showSnack(CollectBikeActivity.this, "车牌信息不清，请重新拍照辨识！");
	                 }else{
	                	 mEtBikeId.setText(infoMap.get("number"));
	                 }
				}
				break;
			}
		}
    };
    
	@Override
	protected void onDestroy() {
        if (mLocationClient != null){  
        	mLocationClient.stop();  
        }  
		super.onDestroy();
	}
	
	public void saveRidersInformation() {
		 for(int i=0;i<ridersInfoList.getChildCount();i++) {
		      LinearLayout layout = (LinearLayout)ridersInfoList.getChildAt(i);// 获得子item的layout
		      EditText etRiderName = (EditText) layout.findViewById(R.id.et_riders_name);// 从layout中获得控件,根据其id
		      EditText etRiderId = (EditText) layout.findViewById(R.id.et_riders_id);
		      if(etRiderName.getText().equals("") || etRiderId.getText().equals("")) {
		      }else {
		    	 ridersInfo.get(i).setName(etRiderName.getText().toString());
		    	 ridersInfo.get(i).setId(etRiderId.getText().toString());
		      }
           }
	}
	
	int position ;
	public void takePhoto(int position) {
		saveRidersInformation();
		this.position = position;
		Intent itt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		photoName = Util.getDateTime() + ".jpg";
		itt.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
		startActivityForResult(itt,CollectBikeActivity.CAMERA_RIDERS);
	}
}
