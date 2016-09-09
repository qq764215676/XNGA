package ys.oa.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import ys.nlga.activity.R;
import ys.oa.adapter.RidersInfoAdapter;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.IDInfoEntity;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.provider.SQLDataEntity;
import ys.oa.task.QueryTask;
import ys.oa.util.Constants;
import ys.oa.util.L;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

import com.anim.dialog.DialogLoading;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * @author wf
 * @category 车辆信息采集
 * 
 */
public class CollectCarActivity extends BaseActivity implements OnClickListener
{
	private ImageButton btn_back;
	private ImageButton btn_upload;
	
	private final String TAG = "CollectCarActivity";
	private ImageView mIvCollectImg;
	private EditText mEtImgName, mEtCarId, mEtTime, mEtLocation, mEtInfo;
	private EditText mEtOwnerName;// 驾驶员姓名
	private EditText mEtOwnerId;// 驾驶员身份证
	private ImageButton mBtnGetTime, mBtnGetLocation, mBtnGetCarId, mBtnGetOwner;

	private ImageButton mBtnGetPlateInfo, mBtnGetDrivingInfo;// 点击进行车牌、驾驶证信息查询
	private RelativeLayout mRlPlateIdentify, mRlDrivingIdentify;// 身份证信息查询、驾驶证信息查询布局

	private String mPlatePhotoPath, mDrivingPhotoPath;// 车牌正面照、驾驶证正面照本地存储地址
	private ProgressBar mProgressBar, mPlateProgressBar, mDrivingProgressBar;
	OkHttpClient mOkHttpClient = new OkHttpClient();
	// add by fcr at 2016-01-18
	private EnforcementDataManager enforcementDataManager;
	private SpUtil mSpUtil;
	private AlertDialog recognizeDialog = null;
	private ArrayList<IDInfoEntity> ridersInfo;
	private RidersInfoAdapter ridersInfoAdapter;
	private ImageView mDriverImg;
	public final int FAILED = 0;
	public final int SUCCESS = 1;
	private boolean isAllSuccess = true;
	int count = 0;
	int riderCheckNum = 0;
	private StringBuilder imaKVBuilder = new StringBuilder();// 图片时间
	private StringBuilder nameBuilder = new StringBuilder();// 姓名
	private StringBuilder IDBuilder = new StringBuilder();// 身份证
	private StringBuilder imgPathBuilder = new StringBuilder();// 图片路径
	private String vehType = "2"; // 非机动车类型 “2” 表示汽车 “3” 表示摩托车
	private EditText mEtLon, mEtLat;
	private RelativeLayout ridersLayout;
	private final String NO_PICTURE_FLAG = "nopicture";
	// add end

	private int flag;// 区别更新文字属于车牌还是驾驶证
	private static final int UPDATE_PLATE_TEXT = 1;
	private static final int UPDATE_DRIVING_TEXT = 2;
	private static int CAMERA_DRIVER = 4;
	private static int CAMERA_RIDERS = 5;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case UPDATE_PLATE_TEXT:
					Map<String, String> infoMap = (Map<String, String>) msg.obj;
					// mTextView.setText(msg.obj.toString());
					if (infoMap.get("number").equals(""))
					{
						T.showSnack(CollectCarActivity.this, "车牌信息不清，请重新拍照辨识！");
					}
					else
					{
						mEtCarId.setText(infoMap.get("number"));
					}
					break;
				case UPDATE_DRIVING_TEXT:
					Map<String, String> infoMaps = (Map<String, String>) msg.obj;
					mEtOwnerName.setText(infoMaps.get("name"));
					mEtOwnerId.setText(infoMaps.get("cardno"));
					if (infoMaps.get("name").equals("") && infoMaps.get("cardno").equals(""))
					{
						T.showSnack(CollectCarActivity.this, "驾驶证资料不清，请重新拍照辨识！");
					}

					// mEtInfo.setText("有效起始日期 ： "+infoMap.get("registerDate")+"\n"+
					// "准驾车型 ： "+infoMap.get("drivingType")+"\n"+"住址 ： "+infoMap.get("address"));
					break;
				case 3:// 图片上传
					count++;
					int isSuccess = (Integer) msg.obj;
					if (isSuccess == FAILED)
					{
						isAllSuccess = false;
					}
					if (count == ridersInfo.size())
					{
						if (isAllSuccess)
						{
							if (Util.isNetworkAvailable(mActivity))
							{
								new AsyncLoader().execute("upload", filePath, Util.getTypeByEnd(filePath), vehType);
							}
							else
							{
								T.showSnack(mActivity, R.string.networkerror);
							}
						}
						else
						{
							Log.i(TAG, "有失败");
						}
					}
					break;
				case 4:
					// 车牌前照检查
					if (recognizeDialog != null)
					{
						recognizeDialog.cancel();
					}
					if (msg.obj.toString().equals("fail"))
					{
						Toast.makeText(CollectCarActivity.this, "车牌识别失败，请重新拍！", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Map<String, String> iMap1 = (Map<String, String>) msg.obj;
						if (iMap1.get("number").equals(""))
						{
							Toast.makeText(CollectCarActivity.this, "车牌识别不清晰，请重新拍！", Toast.LENGTH_SHORT).show();
						}
						else
						{
							mEtCarId.setText(iMap1.get("number"));
						}
					}
					break;
				case 5:
					// 同车人身份信息检查完成
					recognizeDialog.cancel();
					Log.i("Info33 ", msg.obj.toString());
					if (msg.obj.toString().equals("fail"))
					{
						Toast.makeText(CollectCarActivity.this, "身份证照识别失败，请重新拍！", Toast.LENGTH_SHORT).show();
					}
					else
					{
						// Map<String,String> infosMap = (Map<String, String>)
						// msg.obj;
						// String imgPath = infosMap.get("imgPath");
						// IDInfoEntity e = new
						// IDInfoEntity(imgPath,infosMap.get("name"),infosMap.get("cardno"));
						// ridersInfo.add(e);
						// for(int i=0;i<ridersInfoList.getChildCount();i++) {
						// LinearLayout layout =
						// (LinearLayout)ridersInfoList.getChildAt(i);//
						// 获得子item的layout
						// EditText etRiderName = (EditText)
						// layout.findViewById(R.id.et_riders_name);//
						// 从layout中获得控件,根据其id
						// EditText etRiderId = (EditText)
						// layout.findViewById(R.id.et_riders_id);
						// if(etRiderName.getText().equals("") ||
						// etRiderId.getText().equals("")) {
						// }else {
						// ridersInfo.get(i).setName(etRiderName.getText().toString());
						// ridersInfo.get(i).setId(etRiderId.getText().toString());
						// }
						// }
						// if(ridersInfoAdapter == null) {
						// ridersInfoAdapter = new
						// RidersInfoAdapter(RidersInfoAdapter.TYPE_VEH_CKS,CollectCarActivity.this,
						// CollectCarActivity.this,
						// ridersInfo,ridersInfoList,false);
						// ridersInfoList.setAdapter(ridersInfoAdapter);
						// }else {
						// ridersInfoAdapter.notify(ridersInfo);
						// }
						// setListViewHeight(ridersInfoList);
						Map<String, String> infosMap = (Map<String, String>) msg.obj;
						String imgPath = infosMap.get("imgPath");
						String position = infosMap.get("position");
						IDInfoEntity e = ridersInfo.get(Integer.parseInt(position));
						e.setName(infosMap.get("name"));
						e.setId(infosMap.get("cardno"));
						e.setImgPath(imgPath);
						if (ridersInfoAdapter == null)
						{
							ridersInfoAdapter = new RidersInfoAdapter(RidersInfoAdapter.TYPE_VEH_CKS, CollectCarActivity.this, mActivity, ridersInfo, ridersInfoList, false);
							ridersInfoList.setAdapter(ridersInfoAdapter);
						}
						else
						{
							ridersInfoAdapter.notify(ridersInfo);
						}
						Util.setListViewHeight(ridersInfoList);
						if (infosMap.get("name").equals("") || infosMap.get("cardno").equals(""))
						{
							Toast.makeText(CollectCarActivity.this, "身份证照识别失败，请重新拍！", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case 7:
					// 驾驶人身份信息检查完成
					recognizeDialog.cancel();
					if (msg.obj.toString().equals("fail"))
					{
						Toast.makeText(CollectCarActivity.this, "身份证照识别失败，请重新拍！", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Map<String, String> infoMap1 = (Map<String, String>) msg.obj;
						if (infoMap1.get("name").equals("") || infoMap1.get("cardno").equals(""))
						{
							Toast.makeText(CollectCarActivity.this, "身份证照不清晰，请重新拍！", Toast.LENGTH_SHORT).show();
						}
						else
						{
							mEtOwnerName.setText(infoMap1.get("name"));
							mEtOwnerId.setText(infoMap1.get("cardno"));
						}
					}
					break;
				case 6:
					riderCheckNum++;
					int isNext = (Integer) msg.obj;
					if (isNext == FAILED)
					{
						isAllSuccess = false;
					}
					if (riderCheckNum == ridersInfo.size())
					{
						if (isAllSuccess)
						{
							Log.i("fcr", "全部插入检查表完成");
							if (Util.isNetworkAvailable(mActivity))
							{
								// /执行插入机动车检查请求 参数待定
								new AsyncLoader().execute("insert_veh_req", mEtCarId.getText().toString());
							}
							else
							{
								T.showSnack(mActivity, R.string.networkerror);
							}
						}
						else
						{
							Log.i("fcr", "有失败");
						}
					}
					break;
			}
		}
	};

	private CollectCarActivity mActivity;
	private CollectCarEnity enity;

	private final int ID_FRONT = 10;
	private final int ID_BACK = 11;
	private final int DRIVING_LIC_1 = 12;
	private final int DRIVING_LIC_2 = 13;
	private int currentImgClick;
	private String urlIdFront, urlIdBack, urlDrivingLic1, urlDrivingLic2, nameIdFront, nameIdBack, nameDrivingLic1, nameDrivingLic2;
	private boolean isShowLoading = true;

	// add by xuliang 2016-03-11
	private static boolean isFromCapture = true;// 判断onResume前是否由拍照状态传过来
	private boolean isShowViwe;

	@ViewInject(R.id.riders_info_list)
	private ListView ridersInfoList;

	@ViewInject(R.id.read_riders_info)
	private Button read_riders_info;// 添加同车人信息按钮

	@ViewInject(R.id.read_driver_info)
	private Button read_driver_info;// 添加驾驶人信息按钮

	@OnClick(R.id.read_driver_info)
	public void AddDriverInfo(View v)
	{
		Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		photoName = Util.getDateTime() + ".jpg";
		it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
		startActivityForResult(it, CAMERA_DRIVER);
	}

	@OnClick(R.id.read_riders_info)
	public void AddReadRidersInfo(View v)
	{
		saveRidersInformation();
		IDInfoEntity enity = new IDInfoEntity();
		ridersInfo.add(enity);
		ridersInfoAdapter.notify(ridersInfo);
		setListViewHeight(ridersInfoList);
	}

	// add end

	@ViewInject(R.id.iv_id_front)
	private ImageView mIvIdFront;

	@OnClick(R.id.iv_id_front)
	public void IdFrontClick(View v)
	{
		currentImgClick = ID_FRONT;
		photoName = Util.getDateTime() + ".jpg";
		// 拍照并显示
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
		startActivityForResult(intent, Constants.PHOTOHRAPH);
	}

	@ViewInject(R.id.iv_id_back)
	private ImageView mIvIdBack;

	@OnClick(R.id.iv_id_back)
	public void IdBackClick(View v)
	{
		currentImgClick = ID_BACK;
		photoName = Util.getDateTime() + ".jpg";
		// 拍照并显示
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
		startActivityForResult(intent, Constants.PHOTOHRAPH);
	}

	@ViewInject(R.id.iv_driving_lic_1)
	private ImageView mIvDringLic1;

	@OnClick(R.id.iv_driving_lic_1)
	public void DringLicClick1(View v)
	{
		currentImgClick = DRIVING_LIC_1;
		photoName = Util.getDateTime() + ".jpg";
		// 拍照并显示
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
		startActivityForResult(intent, Constants.PHOTOHRAPH);
	}

	@ViewInject(R.id.iv_driving_lic_2)
	private ImageView mIvDringLic2;

	@OnClick(R.id.iv_driving_lic_2)
	public void DringLicClick2(View v)
	{
		currentImgClick = DRIVING_LIC_2;
		photoName = Util.getDateTime() + ".jpg";
		// 拍照并显示
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
		startActivityForResult(intent, Constants.PHOTOHRAPH);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_car);
		Util.initExce(this);
		ViewUtils.inject(this);
		LockApplication.getInstance().addActivity(this);

		findView();
		initVar();
		initWidget();
		initListener();
		initLocation();
		mSpUtil = AppData.getInstance().getSpUtil();
		enforcementDataManager = EnforcementDataManager.getInstance(this);
		if ("2".equals(Constants.vehicleType))
		{
			setTitle("汽车信息采集");
			vehType = "2";
		}
		else if ("3".equals(Constants.vehicleType))
		{
			setTitle("摩托车信息采集");
			vehType = "3";
		}

		if (getIntent().getSerializableExtra("enity") != null)
		{
			isShowViwe = true;
			if ("2".equals(Constants.vehicleType))
			{
				setTitle("汽车信息");
			}
			else if ("3".equals(Constants.vehicleType))
			{
				setTitle("摩托车信息");
			}

			enity = (CollectCarEnity) getIntent().getSerializableExtra("enity");
			// Util.displayImg(mActivity, mIvCollectImg, enity.getDocumentId(),
			// "png");
			mEtOwnerName.setText(enity.getOwnerName());
			mEtOwnerId.setText(enity.getOwnerId());
			mEtCarId.setText(enity.getCarId());
			mEtTime.setText(enity.getTime());
			mEtLocation.setText(enity.getAddress());
			mEtInfo.setText(enity.getInfo());
			Util.setAllFocusable(new View[] { mEtOwnerName, mEtOwnerId, mEtCarId, mEtTime, mEtLocation, mEtInfo, mIvIdFront, mIvIdBack, mIvDringLic1, mIvDringLic2, mBtnGetTime, mBtnGetLocation, mBtnGetCarId, mBtnGetOwner }, false);
			mBtnGetTime.setVisibility(View.GONE);
			mBtnGetLocation.setVisibility(View.GONE);
			mBtnGetCarId.setVisibility(View.GONE);
			mBtnGetOwner.setVisibility(View.GONE);
			String[] imgKeys = enity.getImgKeys().split(",");
			if (Util.isNetworkAvailable(mActivity))
			{
				isShowLoading = false;
				for (String imgKey : imgKeys)
				{
					// new AsyncLoader().execute("loadImg", imgKey);
					Util.displayLocalImg(mActivity, mIvIdFront, imgKeys[0], "png");
					Util.displayLocalImg(mActivity, mIvIdBack, imgKeys[1], "png");
					Util.displayLocalImg(mActivity, mIvDringLic1, imgKeys[2], "png");
					Util.displayLocalImg(mActivity, mIvDringLic2, imgKeys[3], "png");
				}

				if (enity.getRidersInfo() == null || enity.getRidersInfo().size() < 1)
				{
					ridersLayout.setVisibility(View.GONE);
				}
				else
				{
					ridersLayout.setVisibility(View.VISIBLE);
				}
				Util.displayLocalImg(mActivity, mDriverImg, enity.getIdImg(), ".jpg", "headImage");
				ridersInfoAdapter.notify(enity.getRidersInfo());
				setListViewHeight(ridersInfoList);
				read_driver_info.setVisibility(View.GONE);
				read_riders_info.setVisibility(View.GONE);
				// Util.displayLocalImg(mActivity, mIvCollectImg,
				// enity.getImgKeys(), "png");
			}
			else
			{
				T.showSnack(mActivity, R.string.networkerror);
			}
		}
		else
		{
			isShowViwe = false;
		}
	}

	public void initVar()
	{
		mActivity = CollectCarActivity.this;
		Util.hasSdcard();
	}

	private void initLocation()
	{
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

	public void initWidget()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.hide(); //因需求修改隐藏

		mIvCollectImg = (ImageView) findViewById(R.id.iv_collect_img);
		mEtImgName = (EditText) findViewById(R.id.et_img_name);
		mEtCarId = (EditText) findViewById(R.id.et_car_id);
		mEtTime = (EditText) findViewById(R.id.et_car_time);
		mEtLocation = (EditText) findViewById(R.id.et_car_location);
		mEtInfo = (EditText) findViewById(R.id.et_info);
		mEtOwnerName = (EditText) findViewById(R.id.et_car_owner_name);
		mEtOwnerId = (EditText) findViewById(R.id.et_car_owner_id);
		mBtnGetOwner = (ImageButton) findViewById(R.id.btn_get_owner);
		mBtnGetCarId = (ImageButton) findViewById(R.id.btn_get_car_id);
		mBtnGetTime = (ImageButton) findViewById(R.id.btn_get_time);
		mBtnGetLocation = (ImageButton) findViewById(R.id.btn_get_gps);
		mDriverImg = (ImageView) findViewById(R.id.owner_id_img);
		mEtLon = (EditText) findViewById(R.id.et_long);
		mEtLat = (EditText) findViewById(R.id.et_lat);
		ridersLayout = (RelativeLayout) findViewById(R.id.riders_layout);

		mBtnGetPlateInfo = (ImageButton) findViewById(R.id.btn_get_plate_info);
		mBtnGetDrivingInfo = (ImageButton) findViewById(R.id.btn_get_driving_info);
		mRlPlateIdentify = (RelativeLayout) findViewById(R.id.rl_plate_identify);
		mRlDrivingIdentify = (RelativeLayout) findViewById(R.id.rl_driving_identify);
		mPlateProgressBar = (ProgressBar) findViewById(R.id.upload_plate_progress);
		mDrivingProgressBar = (ProgressBar) findViewById(R.id.upload_driving_progress);
		// add by fcr for test
		/*
		 * mEtOwnerName.setText("222"); mEtOwnerId.setText("1111111111111");
		 * mEtCarId.setText("23456"); mEtTime.setText("1980-01-01");
		 * mEtLocation.setText("上海市"); urlIdFront =
		 * "/storage/sdcard0/XNGA/files/20000108175335594.jpg"; urlIdBack =
		 * "/storage/sdcard0/XNGA/files/20000108175335594.jpg"; urlDrivingLic1 =
		 * "/storage/sdcard0/XNGA/files/20000108175335594.jpg"; urlDrivingLic2 =
		 * "/storage/sdcard0/XNGA/files/20000108175335594.jpg";
		 */
		// add end
		ridersInfo = new ArrayList<IDInfoEntity>();
		ridersInfoAdapter = new RidersInfoAdapter(RidersInfoAdapter.TYPE_VEH_CKS, this, this, ridersInfo, ridersInfoList, isShowViwe);
		ridersInfoList.setAdapter(ridersInfoAdapter);
	}

	public void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_upload.setOnClickListener(this);
		
		mIvCollectImg.setOnClickListener(this);
		mBtnGetOwner.setOnClickListener(this);
		mBtnGetCarId.setOnClickListener(this);
		mBtnGetTime.setOnClickListener(this);
		mBtnGetLocation.setOnClickListener(this);

		mBtnGetPlateInfo.setOnClickListener(this);
		mBtnGetDrivingInfo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_qcxxcj_back:
			{
				finish();
			}break;

			case R.id.btn_qcxxcj_upload:
			{
				if (TextUtils.isEmpty(mEtCarId.getText().toString()))
				{
					T.showSnack(this, "请填写车牌号");
				}
				else if (TextUtils.isEmpty(mEtTime.getText().toString()))
				{
					T.showSnack(this, "请填写采集时间");
				}
				else if (TextUtils.isEmpty(mEtLocation.getText().toString()))
				{
					T.showSnack(this, "请填写采集地点");
				}
				else if (TextUtils.isEmpty(urlIdFront))
				{
					// T.showSnack(this, "请采集身份证正面照");
					T.showSnack(this, "请采集车辆前照");
				}
				else if (TextUtils.isEmpty(urlIdBack))
				{
					// T.showSnack(this, "请采集身份证反面照");
					T.showSnack(this, "请采集车辆后照");
				}
				// Added at 2016-04-06
				// else if (TextUtils.isEmpty(urlDrivingLic1)) {
				// T.showSnack(this, "请采集驾驶证页一");
				// } else if (TextUtils.isEmpty(urlDrivingLic2)) {
				// T.showSnack(this, "请采集驾驶证页二");
				// }
				else if (!isDriverInfoComplete())
				{
					T.showSnack(this, "驾驶人信息采集不完全");
				}
				else if (!isRidersInfoComplete())
				{
					T.showSnack(this, "同车人信息采集不完全");
				}
				else
				{
					if (Util.isNetworkAvailable(mActivity))
					{
						new AsyncLoader().execute("uploadImgIdFront", urlIdFront, Util.getTypeByEnd(urlIdFront));
					}
					else
					{
						T.showSnack(mActivity, R.string.networkerror);
					}
				}
			}break;
			
			case R.id.iv_collect_img: // 拍照并显示
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
				startActivityForResult(intent, Constants.PHOTORESULT);
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

			case R.id.btn_get_plate_info:
				// uploadAndRecognize(mBtnGetPlateInfo);
				break;
			case R.id.btn_get_driving_info:
				// uploadAndRecognize(mBtnGetDrivingInfo);
				break;
		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

		if (isFromCapture)
		{
			// 拍照成功后返回页面，不显示九宫格解锁
			LockApplication.notShowLock = true;
		}
		else
		{
			// 显示九宫格解锁
			LockApplication.notShowLock = false;
		}
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();

		Log.v("isFromCapture onRestart", isFromCapture + "");
		if (isFromCapture)
		{
			// 拍照成功后返回页面，不显示九宫格解锁
			LockApplication.notShowLock = true;
			LockApplication.isActive = false;
			isFromCapture = false;
		}
		else
		{
			// 显示九宫格解锁
			LockApplication.notShowLock = false;
		}

	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		isFromCapture = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.collect, menu);
		if (getIntent().getSerializableExtra("enity") != null)
		{
			menu.findItem(R.id.action_upload).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.action_upload:
				if (TextUtils.isEmpty(mEtCarId.getText().toString()))
				{
					T.showSnack(this, "请填写车牌号");
				}
				else if (TextUtils.isEmpty(mEtTime.getText().toString()))
				{
					T.showSnack(this, "请填写采集时间");
				}
				else if (TextUtils.isEmpty(mEtLocation.getText().toString()))
				{
					T.showSnack(this, "请填写采集地点");
				}
				else if (TextUtils.isEmpty(urlIdFront))
				{
					// T.showSnack(this, "请采集身份证正面照");
					T.showSnack(this, "请采集车辆前照");
				}
				else if (TextUtils.isEmpty(urlIdBack))
				{
					// T.showSnack(this, "请采集身份证反面照");
					T.showSnack(this, "请采集车辆后照");
				}
				// Added at 2016-04-06
				// else if (TextUtils.isEmpty(urlDrivingLic1)) {
				// T.showSnack(this, "请采集驾驶证页一");
				// } else if (TextUtils.isEmpty(urlDrivingLic2)) {
				// T.showSnack(this, "请采集驾驶证页二");
				// }
				else if (!isDriverInfoComplete())
				{
					T.showSnack(this, "驾驶人信息采集不完全");
				}
				else if (!isRidersInfoComplete())
				{
					T.showSnack(this, "同车人信息采集不完全");
				}
				else
				{
					if (Util.isNetworkAvailable(mActivity))
					{
						new AsyncLoader().execute("uploadImgIdFront", urlIdFront, Util.getTypeByEnd(urlIdFront));
					}
					else
					{
						T.showSnack(mActivity, R.string.networkerror);
					}
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void uploadAndRecognize(String photoPath)
	{
		String mPhotoPath = null;
		String mRecognizeType = null;

		// switch(v.getId()){
		// case R.id.btn_get_plate_info:
		// mPhotoPath = mPlatePhotoPath;
		// mProgressBar = mPlateProgressBar;
		// flag = UPDATE_PLATE_TEXT;
		// mRecognizeType = "plate";
		// break;
		// case R.id.btn_get_driving_info:
		// mPhotoPath = mDrivingPhotoPath;
		// mProgressBar = mDrivingProgressBar;
		// flag = UPDATE_DRIVING_TEXT;
		// mRecognizeType = "driving";
		// break;
		// }
		mPhotoPath = photoPath;
		flag = 8;
		mRecognizeType = "plate";
		if (!TextUtils.isEmpty(mPhotoPath))
		{
			File file = new File(mPhotoPath);
			// 构造上传请求，类似web表单
			RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM).addPart(Headers.of("Content-Disposition", "form-data; name=\"callbackurl\""), RequestBody.create(null, "/idcard/")).addPart(Headers.of("Content-Disposition", "form-data; name=\"action\""), RequestBody.create(null, mRecognizeType)).addPart(Headers.of("Content-Disposition", "form-data; name=\"img\"; filename=\"idcardFront_user.jpg\""), RequestBody.create(MediaType.parse("image/jpeg"), file)).build();
			// 这个是ui线程回调，可直接操作UI
			final UIProgressListener uiProgressRequestListener = new UIProgressListener()
			{
				@Override
				public void onUIProgress(long bytesWrite, long contentLength, boolean done)
				{
					Log.e("TAG", "bytesWrite:" + bytesWrite);
					Log.e("TAG", "contentLength" + contentLength);
					Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
					Log.e("TAG", "done:" + done);
					Log.e("TAG", "================================");
					// ui层回调
					mProgressBar.setProgress((int) ((100 * bytesWrite) / contentLength));
				}
			};
			// 进行包装，使其支持进度回调
			final Request request = new Request.Builder().header("Host", "ocr.ccyunmai.com").header("Origin", "http://ocr.ccyunmai.com").header("Referer", "http://ocr.ccyunmai.com/idcard/").header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2398.0 Safari/537.36").url("http://ocr.ccyunmai.com/UploadImg.action").post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener)).build();
			// 开始请求
			mOkHttpClient.newCall(request).enqueue(new Callback()
			{
				@Override
				public void onFailure(Request request, IOException e)
				{
					Log.e("TAG", "error");
					Message obtain = Message.obtain();
					obtain.what = 8;
					// obtain.obj=builder.toString();
					obtain.obj = "fail";
					mHandler.sendMessage(obtain);
				}

				@Override
				public void onResponse(Response response) throws IOException
				{
					String result = response.body().string();
					Document parse = Jsoup.parse(result);
					Elements select = parse.select("div.left fieldset");
					Log.e("TAG", select.text());
					Document parse1 = Jsoup.parse(select.text());
					StringBuilder builder = new StringBuilder();
					Map<String, String> msgMap = new HashMap<String, String>();

					// 如果为车牌照
					if (flag == 8)
					{
						String number = parse1.select("number").text();
						msgMap.put("number", number);
						String color = parse1.select("color").text();
						msgMap.put("color", color);
						String layer = parse1.select("layer").text();
						msgMap.put("layer", layer);
						Log.v("PLATE", number);
						Log.v("PLATE", color);
						Log.v("PLATE", layer);
					}
					// 如果为驾驶证照
					else
					{
						String name = parse1.select("name").text();
						msgMap.put("name", name);
						String cardno = parse1.select("cardno").text();
						msgMap.put("cardno", cardno);
						String sex = parse1.select("sex").text();
						msgMap.put("sex", sex);
						String birthday = parse1.select("birthday").text();
						msgMap.put("birthday", birthday);
						String address = parse1.select("address").text();
						msgMap.put("address", address);
						String issueDate = parse1.select("issueDate").text();
						msgMap.put("issueDate", issueDate);
						String nation = parse1.select("nation").text();
						msgMap.put("nation", nation);
						String drivingType = parse1.select("drivingType").text();
						msgMap.put("drivingType", drivingType);
						String registerDate = parse1.select("registerDate").text();
						msgMap.put("registerDate", registerDate);
						String validPeriod = parse1.select("validPeriod").text();
						msgMap.put("validPeriod", validPeriod);
						Log.v("DRIVING", name);
						Log.v("DRIVING", cardno);
						Log.v("DRIVING", sex);
						Log.v("DRIVING", birthday);
						Log.v("DRIVING", address);
						Log.v("DRIVING", issueDate);
						Log.v("DRIVING", nation);
						Log.v("DRIVING", drivingType);
						Log.v("DRIVING", registerDate);
						Log.v("DRIVING", validPeriod);

					}

					Message obtain = Message.obtain();
					obtain.what = flag;
					// obtain.obj=builder.toString();
					obtain.obj = msgMap;
					mHandler.sendMessage(obtain);
				}
			});
		}
	}

	private DataSetList datasetlist;
	private AlertDialog loadingDialog;
	private CollectCarEnity carEntity;
	private SQLDataEntity entity;

	class AsyncLoader extends AsyncTask<String, Integer, Integer>
	{

		protected Integer doInBackground(String... params)
		{
			if ("uploadDriverImg".equals(params[0]))
			{// 上传图片
				if (TextUtils.isEmpty(params[1]) || NO_PICTURE_FLAG.equals(params[1]))
				{// 如果没有拍照
					String vaule = Util.getDateTime();
					imaKVBuilder.append("," + vaule);
					return 8;
				}
				else
				{
					String vaule = Util.getDateTime();
					imaKVBuilder.append("," + vaule);
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("IMA_VAL", vaule);
					map.put("IMA_KEY", "1");
					try
					{
						datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return -1;
					}
					return 8;
				}

			}
			else if ("uploadRiderImg".equals(params[0]))
			{// 上传图片
				if (TextUtils.isEmpty(params[1]) || NO_PICTURE_FLAG.equals(params[1]))
				{
					String vaule = Util.getDateTime();
					imaKVBuilder.append("," + vaule);
					return 9;
				}
				String vaule = Util.getDateTime();
				imaKVBuilder.append("," + vaule);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", vaule);
				map.put("IMA_KEY", "1");
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 9;
			}
			else if ("loadImg".equals(params[0]))
			{
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_IMAGE where ImgKey='" + params[1] + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "NLGA_IMAGE"), true, false));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 6;
			}
			else if ("upload".equals(params[0]))
			{
				String ownerName = mEtOwnerName.getText().toString();
				String ownerId = mEtOwnerId.getText().toString();
				String carId = mEtCarId.getText().toString();
				String address = mEtLocation.getText().toString();
				String time = mEtTime.getText().toString();
				String info = mEtInfo.getText().toString();
				// String imgKey =
				// nameIdFront+","+nameIdBack+","+nameDrivingLic1+","+nameDrivingLic2;
				// String imageValue =
				// urlIdFront+","+urlIdBack+","+urlDrivingLic1+","+urlDrivingLic2;
				getUploadInfo();
				HashMap<String, String> map = new HashMap<String, String>();
				// map.put("PPL_NAME",ownerName);
				// map.put("PPL_ID", ownerId);
				map.put("PPL_NAME", nameBuilder.toString());
				map.put("PPL_ID", IDBuilder.toString());
				map.put("VEH_LPN", carId);
				map.put("CKS_ADD", address);
				map.put("CKS_TIME", time);
				map.put("REM", info);
				map.put("PPL_LONG", mEtLon.getText().toString());
				map.put("PPL_LAT", mEtLat.getText().toString());
				map.put("USER_ID", Constants.USER_ID);
				if (params[3].equals("2"))
				{
					map.put("VEH_TYPE", Constants.VEH_CKS_CAR);
				}
				else if (params[3].equals("3"))
				{
					map.put("VEH_TYPE", Constants.VEH_CKS_MOTORCYCLE);
				}
				map.put("IMA_KV", imaKVBuilder.toString());
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_VEH_CKS"), false, params[1], params[2]));
					if (carEntity != null)
					{
						carEntity = null;
					}
					if (entity != null)
					{
						entity = null;
					}
					entity = new SQLDataEntity();
					String type = "";
					if (params[3].equals("2"))
					{
						entity.setCheckType(Constants.VEH_CKS_CAR);
						type = Constants.VEH_CKS_CAR;
					}
					else if (params[3].equals("3"))
					{
						entity.setCheckType(Constants.VEH_CKS_MOTORCYCLE);
						type = Constants.VEH_CKS_MOTORCYCLE;
					}
					carEntity = new CollectCarEnity(Constants.USER_ID, nameBuilder.toString(), IDBuilder.toString(), carId, time, address, type, info, imgPathBuilder.toString());
					carEntity.setLat(mEtLat.getText().toString());
					carEntity.setLon(mEtLon.getText().toString());

					entity.setName(ownerName);
					entity.setID(ownerId);
					entity.setCarId(carId);
					entity.setIDImagePath(urlIdFront);
					entity.setTime(time);
					entity.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return -1;
				}
				return 11;
			}
			else if ("uploadImgIdFront".equals(params[0]))
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", nameIdFront);
				map.put("IMA_KEY", "1");
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 2;
			}
			else if ("uploadImgIdBack".equals(params[0]))
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", nameIdBack);
				map.put("IMA_KEY", "2");
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 3;
			}
			else if ("uploadImgDrivingLic1".equals(params[0]))
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", nameDrivingLic1);
				map.put("IMA_KEY", "3");
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 4;
			}
			else if ("uploadImgDrivingLic2".equals(params[0]))
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", nameDrivingLic2);
				map.put("IMA_KEY", "4");
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));
				}
				catch (Exception e)
				{
					return -1;
				}
				// return 5;
				return 7;// 上传驾驶人
			}
			else if ("insert_ppl_req".equals(params[0]))
			{// 插入人员检查请求表
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PPL_ID", params[1]);
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_PPL_REQ"), false, "", ""));
				}
				catch (Exception e)
				{
					return -1;
				}
				if (params[2] != null)
				{// 汽车或摩托车都执行插入机动车检查请求表
					return 12;
				}
			}
			else if ("insert_veh_req".equals(params[0]))
			{// 插入机动车检查请求表
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("VEH_LPN", params[1]);
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_VEH_REQ"), false, "", ""));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 1;// 流程结束
			}
			else if ("insert_ppl_req_riders".equals(params[0]))
			{// 同车人检查 插入人员检查请求表
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PPL_ID", params[1]);
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_PPL_REQ"), false, "", ""));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 13;
			}
			return 0;
		}

		protected void onPreExecute()
		{
			if (isShowLoading)
			{
				loadingDialog = DialogLoading.getProgressDialog(mActivity, "正在上传");
			}
		}

		protected void onPostExecute(Integer result)
		{

			switch (result)
			{
				case -1:// 异步NullPointerException
					loadingDialog.cancel();
					T.showSnack(mActivity, R.string.serverFailed);
					break;
				case 1:
					if (loadingDialog != null)
					{
						loadingDialog.cancel();
					}
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						String contentId = datasetlist.CONTENTID.get(0);
						if (Util.isNetworkAvailable(mActivity))
						{
							if (!TextUtils.isEmpty(vehType))
							{
								CollectPeopleListActivity.queryService.startTimer(QueryTask.QUERY_VEH, contentId);
							}
							if (carEntity != null)
							{
								carEntity.setContentId(contentId);
								Log.i(TAG, "insert carEntity:" + carEntity.toString());
								enforcementDataManager.insertVEHItem(carEntity);
							}
							if (entity != null)
							{
								entity.setContentId(contentId);
								enforcementDataManager.insertEnforcementItem(entity);
							}
							// T.showSnack(mActivity, "上传完成");
							setResult(RESULT_OK);
							onBackPressed();
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 2:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;
							new AsyncLoader().execute("uploadImgIdBack", urlIdBack, Util.getTypeByEnd(urlIdBack));
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 3:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;

							if (!TextUtils.isEmpty(urlDrivingLic1))
							{
								// 若行驶证照不为空，开始上传行驶证照至IMA_KV
								new AsyncLoader().execute("uploadImgDrivingLic1", urlDrivingLic1, Util.getTypeByEnd(urlDrivingLic1));
							}
							else
							{

								// 若行驶证照为空，开始判断驾驶证是否为空
								if (!TextUtils.isEmpty(urlDrivingLic2))
								{
									// 若驾驶证不为空，开始上传驾驶证照至IMA_KV
									new AsyncLoader().execute("uploadImgDrivingLic2", urlDrivingLic2, Util.getTypeByEnd(urlDrivingLic2));
								}
								else
								{
									// 若驾驶证为空，开始上传驾驶人图片
									if (mDriverImg.getTag() == null)
									{
										mDriverImg.setTag("");
									}
									new AsyncLoader().execute("uploadDriverImg", mDriverImg.getTag().toString(), Util.getTypeByEnd(mDriverImg.getTag().toString()), vehType);
								}

							}
							// new AsyncLoader().execute("uploadImgDrivingLic1",
							// urlDrivingLic1,
							// Util.getTypeByEnd(urlDrivingLic1));
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 4:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;

							// 若行驶证照为空，开始判断驾驶证是否为空
							if (!TextUtils.isEmpty(urlDrivingLic2))
							{
								// 若驾驶证不为空，开始上传驾驶证照至IMA_KV
								new AsyncLoader().execute("uploadImgDrivingLic2", urlDrivingLic2, Util.getTypeByEnd(urlDrivingLic2));
							}
							else
							{
								// 若驾驶证为空，开始上传驾驶人图片
								if (mDriverImg.getTag() == null)
								{
									mDriverImg.setTag("");
								}
								new AsyncLoader().execute("uploadDriverImg", mDriverImg.getTag().toString(), Util.getTypeByEnd(mDriverImg.getTag().toString()), vehType);
							}
							// new AsyncLoader().execute("uploadImgDrivingLic2",
							// urlDrivingLic2,
							// Util.getTypeByEnd(urlDrivingLic2));
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 5:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;
							new AsyncLoader().execute("upload", urlIdFront, Util.getTypeByEnd(urlIdFront), vehType);
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 6:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
						ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
						ArrayList<String> documentId = (ArrayList<String>) datasetlist.DOCUMENTID;
						int n = 0;
						for (int i = 0; i < nameList.size(); i++)
						{
							if ("ImgType".equals(nameList.get(i)))
							{
								if ("1".equals(valueList.get(i)))
								{
									Util.displayImg(mActivity, mIvIdFront, documentId.get(n), "png");
									n++;
								}
								else if ("2".equals(valueList.get(i)))
								{
									Util.displayImg(mActivity, mIvIdBack, documentId.get(n), "png");
									n++;
								}
								else if ("3".equals(valueList.get(i)))
								{
									Util.displayImg(mActivity, mIvDringLic1, documentId.get(n), "png");
									n++;
								}
								else if ("4".equals(valueList.get(i)))
								{
									Util.displayImg(mActivity, mIvDringLic2, documentId.get(n), "png");
									n++;
								}
							}
						}
					}
					else
					{
						T.showSnack(mActivity, "图片加载失败");
					}
					break;

				case 7:// 上传驾驶人图片
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;
							if (mDriverImg.getTag() == null)
							{
								mDriverImg.setTag("");
							}
							new AsyncLoader().execute("uploadDriverImg", mDriverImg.getTag().toString(), Util.getTypeByEnd(mDriverImg.getTag().toString()), vehType);
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 8:// 上传同车人图片
					if (datasetlist == null || Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;
							if (ridersInfo != null && ridersInfo.size() > 0)
							{
								for (int i = 0; i < ridersInfo.size(); i++)
								{
									if (ridersInfo.get(i).getImgPath() == null)
									{
										ridersInfo.get(i).setImgPath("");
									}
									String path = ridersInfo.get(i).getImgPath().toString();
									if (path != null)
									{
										new AsyncLoader().execute("uploadRiderImg", path, Util.getTypeByEnd(path), vehType);
									}
								}
							}
							else
							{
								new AsyncLoader().execute("upload", urlIdFront, Util.getTypeByEnd(urlIdFront), vehType);
							}
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 9:// 处理上传完图片
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						Log.i(TAG, "上传成功");
						// String contentId = datasetlist.CONTENTID.get(0);
						// imaKVBuilder.append(contentId+",");
						sendMsg(3, SUCCESS);
					}
					else
					{
						Log.i(TAG, "上传失败");
						sendMsg(3, FAILED);
						if (loadingDialog != null)
						{
							loadingDialog.cancel();
						}
					}
					break;
				case 10:
					if (loadingDialog != null)
					{
						loadingDialog.cancel();
					}
					T.showSnack(mActivity, "文件上传成功");
					break;
				case 11:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							// 执行插入人员检查请求表
							if (mEtOwnerId.getText().toString() != null)
							{
								new AsyncLoader().execute("insert_ppl_req", mEtOwnerId.getText().toString(), vehType);
							}
						}
						else
						{
							T.showSnack(mActivity, R.string.networkerror);
						}
					}
					else
					{
						if (loadingDialog != null)
						{
							loadingDialog.cancel();
						}
					}
					break;
				case 12:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						String contentId = datasetlist.CONTENTID.get(0);
						CollectPeopleListActivity.queryService.startTimer(QueryTask.QUERY_PEOPLE, contentId);
						if (ridersInfo != null && ridersInfo.size() > 0)
						{// 有同车人信息
							if (ridersInfo.size() > 0)
							{
								for (int i = 0; i < ridersInfo.size(); i++)
								{
									String id = ridersInfo.get(i).getId();
									new AsyncLoader().execute("insert_ppl_req_riders", id, vehType);
								}
							}
						}
						else
						{// 无同车人
							new AsyncLoader().execute("insert_veh_req", mEtCarId.getText().toString());
						}
					}
					else
					{
						if (loadingDialog != null)
						{
							loadingDialog.cancel();
						}
					}
					break;
				case 13:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						sendMsg(6, SUCCESS);
						String contentId = datasetlist.CONTENTID.get(0);
						CollectPeopleListActivity.queryService.startTimer(QueryTask.QUERY_PEOPLE, contentId);
					}
					else
					{
						sendMsg(6, FAILED);
						if (loadingDialog != null)
						{
							loadingDialog.cancel();
						}
					}
					break;
			}
		}
	}

	// 百度定位
	/*
	 * private LocationClient mLocationClient;
	 * 
	 * private String getLoc() { mLocationClient = ((AppData)
	 * getApplication()).mLocationClient; LocationClientOption option = new
	 * LocationClientOption(); option.setOpenGps(false);
	 * option.setCoorType("bd09ll");
	 * option.setServiceName("com.baidu.location.service_v2.9");
	 * option.setAddrType("all"); option.setScanSpan(1000);
	 * option.setPriority(LocationClientOption.NetWorkFirst);
	 * option.disableCache(true); mLocationClient.setLocOption(option);
	 * mLocationClient.start(); mLocationClient.stop(); return ((AppData)
	 * getApplication()).getLoc(); }
	 */

	private String photoName = Util.getDateTime() + ".jpg"; // 原始照片名
	private Bitmap photo;
	private String filePath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		isFromCapture = true;
		if (resultCode != Activity.RESULT_OK) return;
		// 拍照
		if (requestCode == Constants.PHOTOHRAPH)
		{
			// 设置文件保存路径这里放在跟目录下
			L.i("拍照photoName#########" + photoName);
			filePath = Constants.localPath + photoName;
			if (Util.hasSdcard())
			{
				File picture = new File(Constants.localPath + photoName);
				// startPhotoZoom(Uri.fromFile(picture));

				// add start xuliang
				// 获取存储再XNGA/files下的缩小过的图片
				photo = Util.getSmallBitmap(filePath);
				// 获取图片是否需要旋转，旋转的角度是多少
				int angle = Util.readPictureDegree(filePath);
				// 如果需要旋转，对图片进行旋转保存
				photo = Util.rotaingImageView(angle, photo);
				// 对图片进行分辨率压缩，并保存至文件夹
				filePath = Util.savaPhotoToLocal(data, photo);
				if (currentImgClick == ID_FRONT)
				{
					nameIdFront = Util.getDateTime();
					urlIdFront = filePath;
					mIvIdFront.setBackgroundColor(Color.TRANSPARENT);
					mPlatePhotoPath = urlIdFront;
					Util.displayLocalImg(CollectCarActivity.this, mIvIdFront, filePath, ".jpg");
					// mIvIdFront.setImageBitmap(photo);
					// mRlPlateIdentify.setVisibility(View.VISIBLE);
					recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
					try
					{
						Util.uploadAndRecognize(filePath, -1, mHandler, 4);
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						recognizeDialog.cancel();
					}
					// 拍照采集成功后返回当前Activity，对onResume做判断
					isFromCapture = true;
				}
				else if (currentImgClick == ID_BACK)
				{
					nameIdBack = Util.getDateTime();
					urlIdBack = filePath;
					mIvIdBack.setBackgroundColor(Color.TRANSPARENT);
					Util.displayLocalImg(CollectCarActivity.this, mIvIdBack, filePath, ".jpg");
					// mIvIdBack.setImageBitmap(photo);
				}
				else if (currentImgClick == DRIVING_LIC_1)
				{
					nameDrivingLic1 = Util.getDateTime();
					urlDrivingLic1 = filePath;
					mIvDringLic1.setBackgroundColor(Color.TRANSPARENT);
					mDrivingPhotoPath = urlDrivingLic1;
					Util.displayLocalImg(CollectCarActivity.this, mIvDringLic1, filePath, ".jpg");
					// mIvDringLic1.setImageBitmap(photo);
					// mRlDrivingIdentify.setVisibility(View.VISIBLE);
				}
				else if (currentImgClick == DRIVING_LIC_2)
				{
					nameDrivingLic2 = Util.getDateTime();
					urlDrivingLic2 = filePath;
					mIvDringLic2.setBackgroundColor(Color.TRANSPARENT);
					Util.displayLocalImg(CollectCarActivity.this, mIvDringLic2, filePath, ".jpg");
					// mIvDringLic2.setImageBitmap(photo);
				}
				// add end xuliang
			}
			else
			{
				T.showSnack(this, R.string.sdCardDisabled);
			}
			if (TextUtils.isEmpty(mEtImgName.getText().toString())) mEtImgName.setText(photoName);
			if (photoName.indexOf(".jpg") == -1)
			{
				photoName += ".jpg";
			}
			mEtImgName.setText(photoName);

		}
		// 相册
		if (requestCode == Constants.PHOTOZOOM)
		{
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); // 这个是获得用户选择的图片的索引值
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			photoName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
			L.i("相册photoName#########" + photoName);
			// startPhotoZoom(data.getData());
			if (TextUtils.isEmpty(mEtImgName.getText().toString())) mEtImgName.setText(photoName);
			if (photoName.indexOf(".jpg") == -1)
			{
				photoName += ".jpg";
			}
			mEtImgName.setText(photoName);
			filePath = Constants.localPath + photoName;
		}
		// add xuliang
		else if (requestCode == CAMERA_RIDERS)
		{
			if (Util.hasSdcard())
			{
				File picture = new File(Constants.localPath + photoName);
				if (photoName.indexOf(".jpg") == -1)
				{
					photoName += ".jpg";
				}
				mEtImgName.setText(photoName);
				filePath = Constants.localPath + photoName;

				// 获取存储再XNGA/files下的缩小过的图片
				photo = Util.getSmallBitmap(filePath);
				// 获取图片是否需要旋转，旋转的角度是多少
				int angle = Util.readPictureDegree(filePath);
				System.out.println("angle " + angle);
				// 如果需要旋转，对图片进行旋转保存
				photo = Util.rotaingImageView(angle, photo);
				// 对图片进行分辨率压缩，并保存至文件夹
				filePath = Util.savaPhotoToLocal(data, photo);
				recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
				if (photo != null)
				{
					try
					{
						File file = new File(filePath);
						if (!file.exists())
						{
							file.createNewFile();
						}
					}
					catch (Exception e)
					{
						recognizeDialog.cancel();
					}
					Log.v("filePath", filePath);
					Util.uploadAndRecognize(filePath, position, mHandler, 5);
					// 拍照采集成功后返回当前Activity，对onResume做判断
					isFromCapture = true;
				}
				else
				{
					recognizeDialog.cancel();
				}
			}
			else
			{
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		else if (requestCode == CAMERA_DRIVER)
		{
			if (Util.hasSdcard())
			{
				File picture = new File(Constants.localPath + photoName);
				if (photoName.indexOf(".jpg") == -1)
				{
					photoName += ".jpg";
				}
				mEtImgName.setText(photoName);
				filePath = Constants.localPath + photoName;

				// 获取存储再XNGA/files下的缩小过的图片
				photo = Util.getSmallBitmap(filePath);
				// 获取图片是否需要旋转，旋转的角度是多少
				int angle = Util.readPictureDegree(filePath);
				System.out.println("angle " + angle);
				// 如果需要旋转，对图片进行旋转保存
				photo = Util.rotaingImageView(angle, photo);
				// 对图片进行分辨率压缩，并保存至文件夹
				filePath = Util.savaPhotoToLocal(data, photo);
				recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
				if (photo != null)
				{
					try
					{
						File file = new File(filePath);
						if (!file.exists())
						{
							file.createNewFile();
						}
					}
					catch (Exception e)
					{
						recognizeDialog.cancel();
					}
					Log.v("filePath", filePath);
					mDriverImg.setBackgroundColor(Color.TRANSPARENT);
					Util.displayLocalImg(CollectCarActivity.this, mDriverImg, filePath, ".jpg");
					mDriverImg.setTag(filePath);
					try
					{
						Util.uploadAndRecognize(filePath, -1, mHandler, 7);
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 拍照采集成功后返回当前Activity，对onResume做判断
					isFromCapture = true;
				}
				else
				{
					recognizeDialog.cancel();
				}
			}
			else
			{
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		// add end xuliang

		// 图片处理结果
		if (requestCode == Constants.PHOTORESULT)
		{
			Bundle extras = data.getExtras();
			if (extras != null)
			{
				photo = extras.getParcelable("data");
				if (photoName.indexOf(".jpg") == -1)
				{
					photoName += ".jpg";
				}
				filePath = Constants.localPath + photoName;

				if (Util.hasSdcard())
				{
					if (photo != null)
					{
						try
						{
							File file = new File(filePath);
							if (!file.exists())
							{
								file.createNewFile();
							}
							if (filePath.endsWith(".jpg"))
							{
								FileOutputStream fos = new FileOutputStream(file);
								if (null != fos)
								{
									photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
									fos.flush();
									fos.close();
								}
							}
						}
						catch (Exception e)
						{
						}
						if (currentImgClick == ID_FRONT)
						{
							nameIdFront = Util.getDateTime();
							urlIdFront = filePath;
							mIvIdFront.setBackgroundColor(Color.TRANSPARENT);
							mPlatePhotoPath = urlIdFront;
							mIvIdFront.setImageBitmap(photo);
							// mRlPlateIdentify.setVisibility(View.VISIBLE);
						}
						else if (currentImgClick == ID_BACK)
						{
							nameIdBack = Util.getDateTime();
							urlIdBack = filePath;
							mIvIdBack.setBackgroundColor(Color.TRANSPARENT);
							mIvIdBack.setImageBitmap(photo);
						}
						else if (currentImgClick == DRIVING_LIC_1)
						{
							nameDrivingLic1 = Util.getDateTime();
							urlDrivingLic1 = filePath;
							mIvDringLic1.setBackgroundColor(Color.TRANSPARENT);
							mDrivingPhotoPath = urlDrivingLic1;
							mIvDringLic1.setImageBitmap(photo);
							// mRlDrivingIdentify.setVisibility(View.VISIBLE);
						}
						else if (currentImgClick == DRIVING_LIC_2)
						{
							nameDrivingLic2 = Util.getDateTime();
							urlDrivingLic2 = filePath;
							mIvDringLic2.setBackgroundColor(Color.TRANSPARENT);
							mIvDringLic2.setImageBitmap(photo);
						}
					}
				}
				else
				{
					T.showSnack(this, R.string.sdCardDisabled);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void startPhotoZoom(Uri uri)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, Constants.PHOTOTYPE);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 640 / 20);
		intent.putExtra("outputY", 480 / 20);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Constants.PHOTORESULT);
	}

	/**
	 * scrollview与listview合用会出现listview只显示一行多点。此方法是为了定死listview的高度就不会出现以上状况
	 * 算出listview的高度
	 */
	public void setListViewHeight(ListView listView)
	{
		RidersInfoAdapter listAdapter = (RidersInfoAdapter) listView.getAdapter();
		if (listAdapter == null) { return; }
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(1, 1);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
		listView.setLayoutParams(params);
	}

	// add xuliang
	private void sendMsg(int index, int flag)
	{
		Message m = Message.obtain();
		m.what = index;
		m.obj = flag;
		mHandler.sendMessage(m);
	}

	/**
	 * 检测驾驶人信息是否完整
	 * 
	 * @return
	 */
	public boolean isDriverInfoComplete()
	{
		if (TextUtils.isEmpty(mEtOwnerId.getText().toString()))
		{
			return false;
		}
		else if (TextUtils.isEmpty(mEtOwnerName.getText().toString())) { return false; }
		return true;
	}

	/**
	 * 判断同车人数据是否完整
	 * 
	 * @return
	 */
	public boolean isRidersInfoComplete()
	{
		if (ridersInfo.size() != 0)
		{
			for (int i = 0; i < ridersInfoList.getChildCount(); i++)
			{
				LinearLayout layout = (LinearLayout) ridersInfoList.getChildAt(i);// 获得子item的layout
				EditText etRiderName = (EditText) layout.findViewById(R.id.et_riders_name);// 从layout中获得控件,根据其id
				EditText etRiderId = (EditText) layout.findViewById(R.id.et_riders_id);
				if (etRiderName.getText().toString().equals("") || etRiderId.getText().toString().equals("")) { return false; }
				return true;
			}
		}
		return true;
	}

	/**
	 * 获取上传信息整理
	 */
	private void getUploadInfo()
	{
		// 添加顶部四张图片
		String topKey = nameIdFront + "," + nameIdBack + "," + nameDrivingLic1 + "," + nameDrivingLic2;
		String topValue = urlIdFront + "," + urlIdBack + "," + urlDrivingLic1 + "," + urlDrivingLic2;
		imgPathBuilder.append(topValue);
		imaKVBuilder.append(topKey);
		nameBuilder.append(mEtOwnerName.getText().toString());
		IDBuilder.append(mEtOwnerId.getText().toString());

		if (!TextUtils.isEmpty(mDriverImg.getTag().toString()))
		{
			imgPathBuilder.append("," + mDriverImg.getTag().toString());
		}
		else
		{
			imgPathBuilder.append("," + NO_PICTURE_FLAG);
		}

		for (int i = 0; i < ridersInfoList.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) ridersInfoList.getChildAt(i);// 获得子item的layout
			EditText etRiderName = (EditText) layout.findViewById(R.id.et_riders_name);// 从layout中获得控件,根据其id
			EditText etRiderId = (EditText) layout.findViewById(R.id.et_riders_id);
			if (etRiderName.getText().equals("") || etRiderId.getText().equals(""))
			{
				T.showSnack(this, "同车人信息采集不完全");
				return;
			}
			else
			{
				nameBuilder.append("," + etRiderName.getText().toString());
				ridersInfo.get(i).setName(etRiderName.getText().toString());
				IDBuilder.append("," + etRiderId.getText());
				ridersInfo.get(i).setId(etRiderId.getText().toString());
				if (!TextUtils.isEmpty(ridersInfo.get(i).getImgPath()))
				{
					imgPathBuilder.append("," + ridersInfo.get(i).getImgPath());
				}
				else
				{
					imgPathBuilder.append("," + NO_PICTURE_FLAG);
				}

			}
		}
	}

	public void back()
	{
		LockApplication.getInstance().exit();
	}

	// 百度定位
	private LocationClient mLocationClient;
	private String addr;

	public class BDLocationListenerImpl implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if (location == null) { return; }

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
			if (location.getLocType() == BDLocation.TypeGpsLocation)
			{
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			}
			else if (location.getLocType() == BDLocation.TypeNetWorkLocation)
			{
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			// Log.e("log", sb.toString());

			addr = location.getAddrStr();
			mEtLat.setText(location.getLatitude() + "");
			mEtLon.setText(location.getLongitude() + "");

		}

		@Override
		public void onReceivePoi(BDLocation poiLocation)
		{

		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mLocationClient != null)
		{
			mLocationClient.stop();
		}
		mHandler.removeCallbacks(null);
		if (photo != null)
		{
			photo.recycle();
			photo = null;
		}
	}

	public void saveRidersInformation()
	{
		for (int i = 0; i < ridersInfoList.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) ridersInfoList.getChildAt(i);// 获得子item的layout
			EditText etRiderName = (EditText) layout.findViewById(R.id.et_riders_name);// 从layout中获得控件,根据其id
			EditText etRiderId = (EditText) layout.findViewById(R.id.et_riders_id);
			if (etRiderName.getText().equals("") || etRiderId.getText().equals(""))
			{
			}
			else
			{
				ridersInfo.get(i).setName(etRiderName.getText().toString());
				ridersInfo.get(i).setId(etRiderId.getText().toString());
			}
		}
	}

	int position;

	public void takePhoto(int position)
	{
		saveRidersInformation();
		this.position = position;
		Intent itt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		photoName = Util.getDateTime() + ".jpg";
		itt.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
		startActivityForResult(itt, CollectBikeActivity.CAMERA_RIDERS);
	}
	
	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_qcxxcj_back);
		btn_upload = (ImageButton) findViewById(R.id.btn_qcxxcj_upload);
	}
}
