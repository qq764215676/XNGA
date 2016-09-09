package ys.oa.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ys.nlga.activity.R;
import ys.oa.enity.CollectPeopleEnity;
import ys.oa.provider.EnforcementDataManager;
import ys.oa.provider.SQLDataEntity;
import ys.oa.service.QueryService;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.anim.dialog.DialogLoading;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gc.materialdesign.widgets.Dialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;

/**
 * @author wf
 * @category 人员信息采集
 * 
 */
public class CollectPeopleActivity extends BaseActivity implements OnClickListener
{
	private ImageButton btn_back;
	private ImageButton btn_upload;
	
	private ScrollView mScrollView;

	private ImageView mIvCollectImg;
	private EditText mEtImgName, mEtName, mEtSex, mEtBirthday, mEtNation, mEtAddress, mEtId, mEtInfo;
	private CollectPeopleActivity mActivity;
	private CollectPeopleEnity enity;

	private ImageButton mBtnGetPeopleId;
	private ImageButton mBtnGetPeopleInfo;// 点击进行身份证信息查询

	private RelativeLayout mRlIdIdentify;// 身份证信息查询布局

	private AlertDialog recognizeDialog;

	private String mPhotoPath;// 身份证正面照本地存储地址
	private ProgressBar mProgressBar;
	// OkHttpClient mOkHttpClient=new OkHttpClient();

	private ImageButton mBtnSearchId;// 点击搜索该人员是否有犯罪记录

	private final int ID_FRONT = 10;
	private final int ID_BACK = 11;
	private final int PHOTO = 12;
	private int currentImgClick;
	private String urlIdFront, urlIdBack, urlPhoto, nameIdFront, nameIdBack, namePhoto;
	private boolean isShowLoading = true;

	// 百度定位
	private LocationClient mLocationClient;
	private String addr;

	// 判断onResume前是否由拍照状态传过来
	private static boolean isFromCapture = false;

	// add by fcr at 2016-01-17
	private EnforcementDataManager enforcementDataManager;
	private SpUtil mSpUtil;
	private QueryService queryService;
	String name;
	String sex;
	String birth;
	String ID;
	String nation;
	String address;
	String info;
	String cksAddress;
	private String startType;
	// add end

	private static final int UPDATE_TEXTVIEW = 1;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case UPDATE_TEXTVIEW:
					recognizeDialog.cancel();
					// mTextView.setText(msg.obj.toString());
					Log.v("Info ", msg.obj.toString());
					Map<String, String> infoMap = (Map<String, String>) msg.obj;
					mEtName.setText(infoMap.get("name"));
					mEtId.setText(infoMap.get("cardno"));
					mEtSex.setText(infoMap.get("sex"));
					mEtNation.setText(infoMap.get("nation"));
					mEtBirthday.setText(infoMap.get("birthday"));
					mEtAddress.setText(infoMap.get("address"));
					break;
			}
		}
	};

	@ViewInject(R.id.et_location)
	private EditText mEtLocation;

	@ViewInject(R.id.btn_get_gps)
	private ImageButton mBtnGetGps;

	@OnClick(R.id.btn_get_gps)
	public void LocationClick(View v)
	{
		mEtLocation.setText(addr);
	}

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

	@ViewInject(R.id.iv_photo)
	private ImageView mIvPhoto;

	@OnClick(R.id.iv_photo)
	public void PhotoClick(View v)
	{
		currentImgClick = PHOTO;
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
		setContentView(R.layout.activity_collect_people);
		ViewUtils.inject(this);
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		initVar();
		if (getIntent().hasExtra("type"))
		{
			startType = getIntent().getStringExtra("type").toString();
		}
		findView();
		initWidget();
		initLocation();
		initListener();
		mSpUtil = AppData.getInstance().getSpUtil();
		enforcementDataManager = EnforcementDataManager.getInstance(this);
		queryService = CollectPeopleListActivity.queryService;
		if (getIntent().getSerializableExtra("enity") != null)
		{
			enity = (CollectPeopleEnity) getIntent().getSerializableExtra("enity");
			// Util.displayImg(mActivity, mIvCollectImg, enity.getDocumentId(),
			// "png");
			// Util.displayImg(mActivity, mIvIdFront, enity.getDocIdIdFront(),
			// "png");
			// Util.displayImg(mActivity, mIvIdBack, enity.getDocIdIdBack(),
			// "png");
			// Util.displayImg(mActivity, mIvPhoto, enity.getDocIdPhoto(),
			// "png");
			mEtName.setHint("");
			mEtSex.setHint("");
			mEtNation.setHint("");
			mEtBirthday.setHint("");
			mEtAddress.setHint("");
			mEtId.setHint("");
			mEtInfo.setHint("");
			mEtLocation.setHint("");

			mEtName.setText(enity.getName());
			mEtSex.setText(enity.getSex());
			mEtNation.setText(enity.getNation());
			mEtBirthday.setText(enity.getBirthday());
			mEtAddress.setText(enity.getAddress());
			mEtId.setText(enity.getId());
			mEtInfo.setText(enity.getInfo());
			mEtLocation.setText(enity.getCollectAddress());
			Util.setAllFocusable(new View[] { mEtName, mEtSex, mEtNation, mEtBirthday, mEtAddress, mEtId, mEtInfo, mIvIdFront, mIvIdBack, mIvPhoto, mEtLocation }, false);
			mBtnGetPeopleId.setVisibility(View.GONE);
			mBtnGetGps.setVisibility(View.GONE);
			if (enity.getImgKeys() != null)
			{
				String[] imgKeys = enity.getImgKeys().split(",");
				if (Util.isNetworkAvailable(mActivity))
				{
					isShowLoading = false;
					for (String imgKey : imgKeys)
					{
						// new AsyncLoader().execute("loadImg", imgKey);
						// modify by fcr for load local image
						Util.displayLocalImg(mActivity, mIvIdFront, imgKeys[0], "png");
						Util.displayLocalImg(mActivity, mIvIdBack, imgKeys[1], "png");
						Util.displayLocalImg(mActivity, mIvPhoto, imgKeys[2], "png");
						// add end
					}
				}
				else
				{
					T.showSnack(mActivity, R.string.networkerror);
				}
			}
			else
			{
			}
		}
	}

	public void initVar()
	{
		mActivity = CollectPeopleActivity.this;
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

	private LinearLayout collectView;

	public void initWidget()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.hide(); //因需求修改隐藏

		mScrollView = (ScrollView) findViewById(R.id.sv_collect);

		collectView = (LinearLayout) findViewById(R.id.img_pp);
		mIvCollectImg = (ImageView) findViewById(R.id.iv_collect_img);
		mEtImgName = (EditText) findViewById(R.id.et_img_name);
		mEtName = (EditText) findViewById(R.id.et_people_name);
		mEtSex = (EditText) findViewById(R.id.et_people_sex);
		mEtBirthday = (EditText) findViewById(R.id.et_people_birthday);
		mEtNation = (EditText) findViewById(R.id.et_people_nation);
		mEtAddress = (EditText) findViewById(R.id.et_people_address);
		mEtId = (EditText) findViewById(R.id.et_people_id);
		mEtInfo = (EditText) findViewById(R.id.et_info);
		mBtnGetPeopleId = (ImageButton) findViewById(R.id.btn_get_people_id);
		mBtnGetPeopleInfo = (ImageButton) findViewById(R.id.btn_get_people_info);

		mRlIdIdentify = (RelativeLayout) findViewById(R.id.rl_id_identify);
		mProgressBar = (ProgressBar) findViewById(R.id.upload_progress);

		mBtnSearchId = (ImageButton) findViewById(R.id.btn_search_people_id);
		// add by fcr for test
		/*
		 * mEtName.setText("张某"); mEtBirthday.setText("1980-01-01");
		 * mEtAddress.setText("上海市"); mEtId.setText("452226197410056034");
		 * mEtSex.setText("男"); mEtNation.setText("汉");
		 * ///storage/sdcard0/XNGA/files/1453698303903.jpg // urlIdFront =
		 * "/storage/sdcard0/XNGA/files/20000108175335594.jpg"; urlIdFront =
		 * "/storage/sdcard0/XNGA/files/20000103092824550.jpg"; urlIdBack =
		 * "/storage/sdcard0/XNGA/files/20000103092824550.jpg"; urlPhoto =
		 * "/storage/sdcard0/XNGA/files/20000103092824550.jpg";
		 */
		if (startType != null)
		{
			if (startType.equals("1"))
			{
				/*
				 * mIvPhoto.setVisibility(View.GONE);
				 * mIvIdBack.setVisibility(View.GONE);
				 * mIvIdFront.setVisibility(View.GONE);
				 */
				collectView.setVisibility(View.GONE);
			}
			else
			{
				/*
				 * mIvPhoto.setVisibility(View.VISIBLE);
				 * mIvIdBack.setVisibility(View.VISIBLE);
				 * mIvIdFront.setVisibility(View.VISIBLE);
				 */
				collectView.setVisibility(View.VISIBLE);
			}
		}
		// add end
	}
	
	private void findView()
	{
		btn_back = (ImageButton) findViewById(R.id.btn_ryxxcj_back);
		btn_upload = (ImageButton) findViewById(R.id.btn_ryxxcj_upload);
	}
	
	public void initListener()
	{
		btn_back.setOnClickListener(this);
		btn_upload.setOnClickListener(this);
		mIvCollectImg.setOnClickListener(this);
		mBtnGetPeopleInfo.setOnClickListener(this);
		mBtnSearchId.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_ryxxcj_back:
			{
				finish();
			}break;
			
			case R.id.btn_ryxxcj_upload:
			{
				// Updated at 2016/03/25
				// 增加采集地点无内容提示
				if (TextUtils.isEmpty(mEtName.getText().toString()))
				{
					T.showSnack(this, "请填写姓名");
				}
				else if (TextUtils.isEmpty(mEtSex.getText().toString()))
				{
					T.showSnack(this, "请填写性别");
				}
				else if (TextUtils.isEmpty(mEtNation.getText().toString()))
				{
					T.showSnack(this, "请填写民族");
				}
				else if (TextUtils.isEmpty(mEtBirthday.getText().toString()))
				{
					T.showSnack(this, "请填写出生日期");
				}
				else if (TextUtils.isEmpty(mEtAddress.getText().toString()))
				{
					T.showSnack(this, "请填写地址");
				}
				else if (TextUtils.isEmpty(mEtId.getText().toString()))
				{
					T.showSnack(this, "请填写身份证号");
				}
				else if (TextUtils.isEmpty(mEtLocation.getText().toString()))
				{
					T.showSnack(this, "请采集地点信息");
				}
				else if (TextUtils.isEmpty(urlIdFront))
				{
					T.showSnack(this, "请采集身份证正面照");
				}
				else if (TextUtils.isEmpty(urlIdBack))
				{
					T.showSnack(this, "请采集身份证反面照");
				}
				// else if (TextUtils.isEmpty(urlPhoto)) {
				// T.showSnack(this, "请采集真人正面照");
				// }
				else if (mEtId.getText().toString().endsWith("(wrong number)"))
				{
					T.showSnack(this, "身份证号有误，请核验修改或重新识别");
				}
				else
				{
					name = mEtName.getText().toString();
					sex = mEtSex.getText().toString();
					birth = mEtBirthday.getText().toString();
					ID = mEtId.getText().toString();
					nation = mEtNation.getText().toString();
					address = mEtAddress.getText().toString();
					info = mEtInfo.getText().toString();
					cksAddress = mEtLocation.getText().toString();
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
				startActivityForResult(intent, Constants.PHOTOHRAPH);

				break;
			// case R.id.btn_get_time:
			// mEtTime.setText(Util.getNowTime("yyyy-MM-dd HH:mm:ss"));
			// break;
			// case R.id.btn_get_gps:
			// mEtLocation.setText(getLoc());
			// break;
			case R.id.btn_get_people_info:
				// uploadAndRecognize();
				recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
				recognizeDialog.setCancelable(true);
				Util.uploadAndRecognize(mPhotoPath, -1, mHandler, UPDATE_TEXTVIEW);
				// 上传照片识别文字显示身份证后可以点击查询身份证号是否在犯罪分子信息库中
				/**
				 * Deprecated. 查找人员是否有犯罪记录在提交后再进行分析匹对
				 */
				// mBtnSearchId.setVisibility(View.VISIBLE);
				// 如果身份证证明照片不为默认图片，即已经拍照获取
				// if
				// (!mIvIdFront.getDrawable().equals(getResources().getDrawable(R.drawable.add_img_bg))){
				// T.showSnack(this, "已经拍照获取");
				// }else{
				// //Toast.makeText(getApplicationContext(), "请拍照身份证正面照片",
				// Toast.LENGTH_SHORT).show();
				// T.showSnack(this, "请拍照身份证正面照片");
				// }
				break;
			case R.id.btn_search_people_id:
				if (Util.isNetworkAvailable(mActivity))
				{
					new AsyncLoader().execute("criminalsCheck", mEtId.getText().toString());
				}
				else
				{
					T.showSnack(mActivity, R.string.networkerror);
				}
				break;

		}
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

				// Updated at 2016/03/25
				// 增加采集地点无内容提示
				if (TextUtils.isEmpty(mEtName.getText().toString()))
				{
					T.showSnack(this, "请填写姓名");
				}
				else if (TextUtils.isEmpty(mEtSex.getText().toString()))
				{
					T.showSnack(this, "请填写性别");
				}
				else if (TextUtils.isEmpty(mEtNation.getText().toString()))
				{
					T.showSnack(this, "请填写民族");
				}
				else if (TextUtils.isEmpty(mEtBirthday.getText().toString()))
				{
					T.showSnack(this, "请填写出生日期");
				}
				else if (TextUtils.isEmpty(mEtAddress.getText().toString()))
				{
					T.showSnack(this, "请填写地址");
				}
				else if (TextUtils.isEmpty(mEtId.getText().toString()))
				{
					T.showSnack(this, "请填写身份证号");
				}
				else if (TextUtils.isEmpty(mEtLocation.getText().toString()))
				{
					T.showSnack(this, "请采集地点信息");
				}
				else if (TextUtils.isEmpty(urlIdFront))
				{
					T.showSnack(this, "请采集身份证正面照");
				}
				else if (TextUtils.isEmpty(urlIdBack))
				{
					T.showSnack(this, "请采集身份证反面照");
				}
				// else if (TextUtils.isEmpty(urlPhoto)) {
				// T.showSnack(this, "请采集真人正面照");
				// }
				else if (mEtId.getText().toString().endsWith("(wrong number)"))
				{
					T.showSnack(this, "身份证号有误，请核验修改或重新识别");
				}
				else
				{
					name = mEtName.getText().toString();
					sex = mEtSex.getText().toString();
					birth = mEtBirthday.getText().toString();
					ID = mEtId.getText().toString();
					nation = mEtNation.getText().toString();
					address = mEtAddress.getText().toString();
					info = mEtInfo.getText().toString();
					cksAddress = mEtLocation.getText().toString();
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

	/**
	 * 定位接口，需要实现两个方法
	 * 
	 */
	public class BDLocationListenerImpl implements BDLocationListener
	{

		/**
		 * 接收异步返回的定位结果，参数是BDLocation类型参数
		 */
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

			Log.e("log", sb.toString());

			addr = location.getAddrStr();

		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation)
		{

		}

	}

	// private void uploadAndRecognize() {
	// if (!TextUtils.isEmpty(mPhotoPath)){
	// File file=new File(mPhotoPath);
	// //构造上传请求，类似web表单
	// RequestBody requestBody = new
	// MultipartBuilder().type(MultipartBuilder.FORM)
	// .addPart(Headers.of("Content-Disposition",
	// "form-data; name=\"callbackurl\""), RequestBody.create(null, "/idcard/"))
	// .addPart(Headers.of("Content-Disposition", "form-data; name=\"action\""),
	// RequestBody.create(null, "idcard"))
	// .addPart(Headers.of("Content-Disposition",
	// "form-data; name=\"img\"; filename=\"idcardFront_user.jpg\""),
	// RequestBody.create(MediaType.parse("image/jpeg"), file))
	// .build();
	// //这个是ui线程回调，可直接操作UI
	// final UIProgressListener uiProgressRequestListener = new
	// UIProgressListener() {
	// @Override
	// public void onUIProgress(long bytesWrite, long contentLength, boolean
	// done) {
	// Log.e("TAG", "bytesWrite:" + bytesWrite);
	// Log.e("TAG", "contentLength" + contentLength);
	// Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
	// Log.e("TAG", "done:" + done);
	// Log.e("TAG", "================================");
	// //ui层回调
	// mProgressBar.setProgress((int) ((100 * bytesWrite) / contentLength));
	// }
	// };
	// //进行包装，使其支持进度回调
	// final Request request = new Request.Builder()
	// .header("Host", "ocr.ccyunmai.com")
	// .header("Origin", "http://ocr.ccyunmai.com")
	// .header("Referer", "http://ocr.ccyunmai.com/idcard/")
	// .header("User-Agent",
	// "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2398.0 Safari/537.36")
	// .url("http://ocr.ccyunmai.com/UploadImg.action")
	// .post(ProgressHelper.addProgressRequestListener(requestBody,
	// uiProgressRequestListener))
	// .build();
	// //开始请求
	// mOkHttpClient.newCall(request).enqueue(new Callback() {
	// @Override
	// public void onFailure(Request request, IOException e) {
	// Log.e("TAG", "error");
	// }
	// @Override
	// public void onResponse(Response response) throws IOException {
	// String result=response.body().string();
	// Document parse = Jsoup.parse(result);
	// Elements select = parse.select("div.left fieldset");
	// Log.e("TAG",select.text());
	// Document parse1 = Jsoup.parse(select.text());
	// StringBuilder builder=new StringBuilder();
	// Map<String, String> msgMap = new HashMap<String, String>();
	//
	// String name=parse1.select("name").text();
	// msgMap.put("name", name);
	// //mEtName.setText(name);
	//
	// String cardno=parse1.select("cardno").text();
	// msgMap.put("cardno", cardno);
	// //mEtId.setText(cardno);
	//
	// String sex=parse1.select("sex").text();
	// msgMap.put("sex", sex);
	// //mEtSex.setText(sex);
	//
	// String folk=parse1.select("folk").text();
	// msgMap.put("nation", folk);
	// //mEtNation.setText(folk);
	//
	// String birthday=parse1.select("birthday").text();
	// msgMap.put("birthday", birthday);
	// //mEtBirthday.setText(birthday);
	//
	// String address=parse1.select("address").text();
	// msgMap.put("address", address);
	// //mEtAddress.setText(address);
	//
	// String issue_authority=parse1.select("issue_authority").text();
	// String valid_period=parse1.select("valid_period").text();
	// builder.append("name:"+name)
	// .append("\n")
	// .append("cardno:" + cardno)
	// .append("\n")
	// .append("sex:" + sex)
	// .append("\n")
	// .append("folk:" + folk)
	// .append("\n")
	// .append("birthday:" + birthday)
	// .append("\n")
	// .append("address:" + address)
	// .append("\n")
	// .append("issue_authority:" + issue_authority)
	// .append("\n")
	// .append("valid_period:" + valid_period)
	// .append("\n");
	// Log.e("TAG", "name:" + name);
	// Log.e("TAG","cardno:"+cardno);
	// Log.e("TAG","sex:"+sex);
	// Log.e("TAG","folk:"+folk);
	// Log.e("TAG","birthday:"+birthday);
	// Log.e("TAG","address:"+address);
	// Log.e("TAG","issue_authority:"+issue_authority);
	// Log.e("TAG","valid_period:"+valid_period);
	// Message obtain = Message.obtain();
	// obtain.what=UPDATE_TEXTVIEW;
	// //obtain.obj=builder.toString();
	// obtain.obj = msgMap;
	// mHandler.sendMessage(obtain);
	// }
	// });
	// }
	// }

	private DataSetList datasetlist;
	private AlertDialog loadingDialog;
	private SQLDataEntity entity;
	private CollectPeopleEnity cpEntity;

	class AsyncLoader extends AsyncTask<String, Integer, Integer>
	{

		protected Integer doInBackground(String... params)
		{
			if ("loadImg".equals(params[0]))
			{
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_IMAGE where ImgKey='" + params[1] + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "NLGA_IMAGE"), true, false));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 5;
			}
			else if ("upload".equals(params[0]))
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PPL_NAME", name);
				map.put("PPL_SEX", sex);
				map.put("PPL_BIRT", birth);
				map.put("PPL_ID", ID);
				map.put("PPL_NATION", nation);
				map.put("PPL_ADD", address);
				map.put("REM", info);
				map.put("CKS_ADD", cksAddress);
				map.put("USER_ID", Constants.USER_ID);
				String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
				map.put("CKS_TIME", time);
				map.put("IMA_KV", nameIdFront + "," + nameIdBack + "," + namePhoto);
				try
				{
					// modify by fcr at 2016-01-17
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_PPL_CKS"), false, params[1], params[2]));
					if (cpEntity != null)
					{
						cpEntity = null;
					}
					String imagesPath = urlIdFront + "," + urlIdBack + "," + urlPhoto;
					cpEntity = new CollectPeopleEnity(Constants.USER_ID, name, nation, sex, birth, ID, address, info, "XNGA_PPL_CKS", imagesPath, cksAddress);
					if (entity != null)
					{
						entity = null;
					}
					entity = new SQLDataEntity();
					entity.setCheckType(Constants.PPL_CKS);
					entity.setName(mEtName.getText().toString());
					entity.setSex(mEtSex.getText().toString());
					entity.setBirthday(mEtBirthday.getText().toString());
					entity.setID(mEtId.getText().toString());
					entity.setIDImagePath(urlIdFront);
					// entity.setTime(time);
					entity.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 7;
			}
			else if ("uploadImgIdFront".equals(params[0]))
			{
				nameIdFront = Util.getDateTime();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", nameIdFront);
				map.put("IMA_KEY", "1");
				try
				{
					/*
					 * datasetlist =
					 * PostHttp.PostXML(XmlPackage.packageForInsertFileData(map,
					 * new DocInfor("", "NLGA_IMAGE"), false, params[1],
					 * params[2]));
					 */
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
				nameIdBack = Util.getDateTime();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", nameIdBack);
				map.put("IMA_KEY", "2");
				try
				{
					/*
					 * datasetlist =
					 * PostHttp.PostXML(XmlPackage.packageForInsertFileData(map,
					 * new DocInfor("", "NLGA_IMAGE"), false, params[1],
					 * params[2]));
					 */
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 3;
			}
			else if ("uploadImgPhoto".equals(params[0]))
			{
				namePhoto = Util.getDateTime();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_VAL", namePhoto);
				map.put("IMA_KEY", "3");
				try
				{
					/*
					 * datasetlist =
					 * PostHttp.PostXML(XmlPackage.packageForInsertFileData(map,
					 * new DocInfor("", "NLGA_IMAGE"), false, params[1],
					 * params[2]));
					 */
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("", "XNGA_IMA"), false, params[1], params[2]));

				}
				catch (Exception e)
				{
					return -1;
				}
				return 4;
			}
			else if ("criminalsCheck".equals(params[0]))
			{
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from NLGA_CRIMINALS where CardId ='" + params[1] + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "NLGA_CRIMINALS"), true, false));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 6;
			}
			else if ("insert_ppl_req".equals(params[0]))
			{
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
				return 1;
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
					loadingDialog.cancel();
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						// add by fcr
						String contentId = datasetlist.CONTENTID.get(0);
						// A0101220161211420383566924
						queryService.startTimer(QueryTask.QUERY_PEOPLE, contentId);
						if (cpEntity != null)
						{
							cpEntity.setContentId(contentId);
							enforcementDataManager.insertPPLItem(cpEntity);
						}
						if (entity != null)
						{
							entity.setContentId(contentId);
							enforcementDataManager.insertEnforcementItem(entity);
						}
						setResult(RESULT_OK);
						onBackPressed();
						// T.showSnack(mActivity, "上传完成");
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
						if (loadingDialog != null)
						{
							loadingDialog.cancel();
						}
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 3:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;

							if (!TextUtils.isEmpty(urlPhoto))
							{
								// 若真人正面照不为空，开始上传真人正面照至IMA_KV
								new AsyncLoader().execute("uploadImgPhoto", urlPhoto, Util.getTypeByEnd(urlPhoto));
							}
							else
							{
								// 若真人正面照为空，开始上传信息至XNGA_PPL_CKS
								new AsyncLoader().execute("upload", urlIdFront, Util.getTypeByEnd(urlIdFront));
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
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 4:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;
							new AsyncLoader().execute("upload", urlIdFront, Util.getTypeByEnd(urlIdFront));
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
						T.showSnack(mActivity, "文件上传失败");
					}
					break;
				case 5:
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
									Util.displayImg(mActivity, mIvPhoto, documentId.get(n), "png");
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
				case 6:
					loadingDialog.cancel();
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
						ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
						ArrayList<String> documentId = (ArrayList<String>) datasetlist.DOCUMENTID;

						if (valueList.size() > 0)
						{
							T.showSnack(CollectPeopleActivity.this, "该核查人员为犯罪嫌疑人！");

							String message = "犯罪嫌疑人： " + valueList.get(1) + " \n身份证号：\n " + valueList.get(3) + "\n" + valueList.get(5) + "\n" + valueList.get(6);
							final Dialog dialog = new Dialog(CollectPeopleActivity.this, "嫌疑人信息", message);
							dialog.setOnAcceptButtonClickListener(new View.OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									dialog.dismiss();
									// CollectPeopleActivity.this.finish();
								}
							});

							dialog.show();

						}
						else
						{
							T.showSnack(CollectPeopleActivity.this, "该核查人员情况正常！");
						}
					}
					break;
				case 7:
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(mActivity))
						{
							isShowLoading = false;
							// 执行插入人员检查表
							if (ID != null)
							{
								new AsyncLoader().execute("insert_ppl_req", ID);
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
			}
		}
	}

	// private String getLoc() {
	// mLocationClient = ((AppData) getApplication()).mLocationClient;
	// LocationClientOption option = new LocationClientOption();
	// option.setOpenGps(false);
	// option.setCoorType("bd09ll");
	// option.setServiceName("com.baidu.location.service_v2.9");
	// option.setAddrType("all");
	// option.setScanSpan(1000);
	// option.setPriority(LocationClientOption.NetWorkFirst);
	// option.disableCache(true);
	// mLocationClient.setLocOption(option);
	// mLocationClient.start();
	// mLocationClient.stop();
	// return ((AppData) getApplication()).getLoc();
	// }

	private String photoName = Util.getDateTime() + ".jpg"; // 原始照片名
	private Bitmap photo;
	private String filePath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != Activity.RESULT_OK) return;
		// 拍照
		if (requestCode == Constants.PHOTOHRAPH)
		{
			// 设置文件保存路径这里放在跟目录下
			L.i("拍照photoName#########" + photoName);
			if (Util.hasSdcard())
			{
				File picture = new File(Constants.localPath + photoName);
				// startPhotoZoom(Uri.fromFile(picture));
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
						// if (filePath.endsWith(".jpg")) {
						// FileOutputStream fos = new FileOutputStream(file);
						// if (null != fos) {
						// photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
						// fos.flush();
						// fos.close();
						// }
						// }
					}
					catch (Exception e)
					{
					}
					if (currentImgClick == ID_FRONT)
					{
						nameIdFront = Util.getDateTime();
						urlIdFront = filePath;
						// NGA/files/20000104095129687.jpg
						mIvIdFront.setBackgroundColor(Color.TRANSPARENT);
						mPhotoPath = urlIdFront;
						Util.displayLocalImg(mActivity, mIvIdFront, filePath, ".jpg");
						// mIvIdFront.setImageBitmap(photo);
						// 设置身份证信息查询布局可见
						mRlIdIdentify.setVisibility(View.VISIBLE);
					}
					else if (currentImgClick == ID_BACK)
					{
						nameIdBack = Util.getDateTime();
						urlIdBack = filePath;
						// /storage/sdcard0/XNGA/files/20000104095239013.jpg
						mIvIdBack.setBackgroundColor(Color.TRANSPARENT);
						Util.displayLocalImg(mActivity, mIvIdBack, filePath, ".jpg");
						// mIvIdBack.setImageBitmap(photo);
					}
					else if (currentImgClick == PHOTO)
					{
						namePhoto = Util.getDateTime();
						urlPhoto = filePath;
						// /storage/sdcard0/XNGA/files/20000104095342705.jpg
						mIvPhoto.setBackgroundColor(Color.TRANSPARENT);
						Util.displayLocalImg(mActivity, mIvPhoto, filePath, ".jpg");
						// mIvPhoto.setImageBitmap(photo);
					}

					// 拍照采集成功后返回当前Activity，对onResume做判断
					isFromCapture = true;
				}
			}
			else
			{
				T.showSnack(this, R.string.sdCardDisabled);
			}
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
		// 图片处理结果
		if (requestCode == Constants.PHOTORESULT)
		{
			Bundle extras = data.getExtras();
			if (extras != null)
			{
				photo = extras.getParcelable("data");
				// mIvCollectImg.setBackgroundColor(Color.TRANSPARENT);
				// if (TextUtils.isEmpty(mEtImgName.getText().toString()))
				// mEtImgName.setText(photoName);
				// mIvCollectImg.setImageBitmap(photo);
				if (photoName.indexOf(".jpg") == -1)
				{
					photoName += ".jpg";
				}
				// L.i("处理后photoName#########" + photoName);
				// mEtImgName.setText(photoName);
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
							// NGA/files/20000104095129687.jpg
							mIvIdFront.setBackgroundColor(Color.TRANSPARENT);
							mPhotoPath = urlIdFront;
							Util.displayLocalImg(this, mIvIdFront, filePath, ".jpg");
							// mIvIdFront.setImageBitmap(photo);
							// 设置身份证信息查询布局可见
							mRlIdIdentify.setVisibility(View.VISIBLE);
						}
						else if (currentImgClick == ID_BACK)
						{
							nameIdBack = Util.getDateTime();
							urlIdBack = filePath;
							// /storage/sdcard0/XNGA/files/20000104095239013.jpg
							mIvIdBack.setBackgroundColor(Color.TRANSPARENT);
							Util.displayLocalImg(this, mIvIdBack, filePath, ".jpg");
							// mIvIdBack.setImageBitmap(photo);
						}
						else if (currentImgClick == PHOTO)
						{
							namePhoto = Util.getDateTime();
							urlPhoto = filePath;
							// /storage/sdcard0/XNGA/files/20000104095342705.jpg
							mIvPhoto.setBackgroundColor(Color.TRANSPARENT);
							Util.displayLocalImg(this, mIvPhoto, filePath, ".jpg");
							// mIvPhoto.setImageBitmap(photo);
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

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO Auto-generated method stub
		Log.i("UserInfoActivity", "onConfigurationChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			Log.i("UserInfoActivity", "横屏");
			Configuration o = newConfig;
			o.orientation = Configuration.ORIENTATION_PORTRAIT;
			newConfig.setTo(o);
		}
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			Log.i("UserInfoActivity", "竖屏");
		}
		super.onConfigurationChanged(newConfig);
	}

	// public void startPhotoZoom(Uri uri) {
	// Intent intent = new Intent("com.android.camera.action.CROP");
	// intent.setDataAndType(uri, Constants.PHOTOTYPE);
	// intent.putExtra("crop", "true");
	// // aspectX aspectY 是宽高的比例
	// intent.putExtra("aspectX", 4);
	// intent.putExtra("aspectY", 3);
	// // outputX outputY 是裁剪图片宽高
	// intent.putExtra("outputX", 640/2);
	// intent.putExtra("outputY", 480/2);
	// intent.putExtra("return-data", true);
	// Intent intent = new Intent();
	// startActivityForResult(intent, Constants.PHOTORESULT);
	// }

	private void scrollToTop()
	{
		// 顶级父View获取焦点
		mScrollView.setFocusable(true);
		mScrollView.setFocusableInTouchMode(true);
		mScrollView.requestFocus();

		// 顶级父View滑动至顶部
		mScrollView.smoothScrollTo(0, 0);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		scrollToTop();

		Log.v("isFromCapture", isFromCapture + "");
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

		scrollToTop();

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
	protected void onDestroy()
	{
		// 退出时销毁定位
		if (mLocationClient != null)
		{
			mLocationClient.stop();
		}

		super.onDestroy();
	}

	public void back()
	{
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed()
	{
		this.finish();
		// back();
	}
	
}
