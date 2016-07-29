package ys.oa.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ys.oa.activity.CollectBikeActivity.AsyncLoader;
import ys.oa.activity.CollectBikeActivity.BDLocationListenerImpl;
import ys.oa.enity.CollectCarEnity;
import ys.oa.enity.CollectGoodEnity;
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
import android.nfc.cardemulation.CardEmulation;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 物品信息采集
 * 
 */
public class CollectGoodActivity extends FragmentActivity implements OnClickListener {

	private ImageView mIvCollectImg;
	private EditText mEtImgName, mEtTime, mEtName, mEtInfo, mEtOwnerName, mEtOwnerId;
	private ImageButton mBtnGetTime;
	private Button mBtnGetOwner;
	private EditText mEtLon , mEtLat;
	private CollectGoodActivity mActivity;
	private CollectCarEnity enity;
	private static int CAMERA_OWNER = 2;
	private ImageView ownerImg;
	private Context context;
	private LocationClient mLocationClient;
	private String addr;
	private String mLon,mLat;
	  //判断onResume前是否由拍照状态传过来
    private static boolean isFromCapture = true;
	private EnforcementDataManager enforcementDataManager;
	private QueryService queryService ;

	@ViewInject(R.id.et_location)
	private EditText mEtLocation;
	
	@ViewInject(R.id.btn_get_gps)
	private ImageButton mBtnGetGps;

	@OnClick(R.id.btn_get_gps)
	public void LocationClick(View v) {
		mEtLocation.setText(addr);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_good);
		context = this;
		ViewUtils.inject(this);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
		enforcementDataManager = EnforcementDataManager.getInstance(this);
		queryService = CollectPeopleListActivity.queryService;

		if (getIntent().getSerializableExtra("enity") != null) {
			enity = (CollectCarEnity) getIntent().getSerializableExtra("enity");
			Util.displayLocalImg(mActivity, mIvCollectImg, enity.getImgKeys(), "png");
			mEtOwnerName.setText(enity.getOwnerName());
			mEtOwnerId.setText(enity.getOwnerId());
			mEtName.setText(enity.getCarId());
			mEtTime.setText(enity.getTime());
			mEtInfo.setText(enity.getInfo());
			mEtLocation.setText(enity.getAddress());
			mEtLon.setText(enity.getLon());
			mEtLat.setText(enity.getLat());
			if(enity.getIdImg() != null) {
				Util.displayLocalImg(mActivity, ownerImg, enity.getIdImg(), "png");	
			}
			Util.setAllFocusable(new View[] { mEtName, mEtOwnerName, mEtOwnerId, mEtTime, mEtInfo, mIvCollectImg,
					mBtnGetTime,mEtLocation }, false);
			mBtnGetTime.setVisibility(View.GONE);
			mBtnGetOwner.setVisibility(View.GONE);
			mBtnGetGps.setVisibility(View.GONE);
		}else {
			initLocation();
		}
	}

	public void initVar() {
		mActivity = CollectGoodActivity.this;
		Util.hasSdcard();
	}

	public void initWidget() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mIvCollectImg = (ImageView) findViewById(R.id.iv_collect_img);
		mEtImgName = (EditText) findViewById(R.id.et_img_name);
		mEtOwnerName = (EditText) findViewById(R.id.et_car_owner_name);
		mEtOwnerId = (EditText) findViewById(R.id.et_car_owner_id);
		mEtTime = (EditText) findViewById(R.id.et_good_time);
		mEtName = (EditText) findViewById(R.id.et_good_name);
		mEtInfo = (EditText) findViewById(R.id.et_info);
		mBtnGetTime = (ImageButton) findViewById(R.id.btn_get_time);
		mBtnGetOwner = (Button) findViewById(R.id.read_driver_info);
		ownerImg = (ImageView)findViewById(R.id.owner_id_img);
		
		mEtLon = (EditText)findViewById(R.id.et_long);
		mEtLat = (EditText)findViewById(R.id.et_lat);
	}

	public void initListener() {
		mIvCollectImg.setOnClickListener(this);
		mBtnGetTime.setOnClickListener(this);
		mBtnGetOwner.setOnClickListener(this);
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
		case R.id.iv_collect_img: // 拍照并显示
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
			startActivityForResult(intent, Constants.PHOTOHRAPH);
			break;
		case R.id.btn_get_time:
			mEtTime.setText(Util.getNowTime("yyyy-MM-dd HH:mm:ss"));
			break;
		// case R.id.btn_get_gps:
		// mEtLocation.setText(getLoc());
		// break;
		case R.id.read_driver_info:
			Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			photoName = Util.getDateTime() + ".jpg";
			it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
			startActivityForResult(it,CAMERA_OWNER);
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
			if (TextUtils.isEmpty(mEtName.getText().toString())) {
				T.showSnack(this, "请填写物品名称");
			} else if (TextUtils.isEmpty(mEtTime.getText().toString())) {
				T.showSnack(this, "请填写采集时间");
			} else if (TextUtils.isEmpty(mEtInfo.getText().toString())) {
				T.showSnack(this, "请填写物品描述");
			} else if (TextUtils.isEmpty(mEtImgName.getText().toString()) || filePath == null || "".equals(filePath)) {
				T.showSnack(this, "请采集图片");
			} else if(TextUtils.isEmpty(mEtLon.getText().toString()) || TextUtils.isEmpty(mEtLat.getText().toString())) {
				
			}else {
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
						if(ownerImg.getTag() != null && (ownerImg.getTag().toString() != null)){
							new AsyncLoader().execute("uploadImg", ownerImg.getTag().toString(), Util.getTypeByEnd(ownerImg.getTag().toString()));
						}else {
							new AsyncLoader().execute("upload", mIvCollectImg.getTag().toString(), Util.getTypeByEnd( mIvCollectImg.getTag().toString()));
						}
					} else {
						T.showSnack(mActivity, R.string.networkerror);
					}
				} else {
					T.showSnack(this, R.string.sdCardDisabled);
				}
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

	private DataSetList datasetlist;
	private AlertDialog loadingDialog;

	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

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
				return 2;
			}
			else if ("upload".equals(params[0])) {
				HashMap<String, String> map = new HashMap<String, String>();
				mLon = mEtLon.getText().toString();
				mLat = mEtLat.getText().toString();
				map.put("PPL_NAME", mEtOwnerName.getText().toString());
				map.put("PPL_ID", mEtOwnerId.getText().toString());
				map.put("IOI_NAME", mEtName.getText().toString());
				map.put("CKS_TIME", mEtTime.getText().toString());
				map.put("CKS_ADD", mEtLocation.getText().toString());
				map.put("REM", mEtInfo.getText().toString());
				map.put("PPL_LONG",mLon);
				map.put("PPL_LAT", mLat);
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("",
							"XNGA_IOI"), false, params[1], params[2]));
				} catch (Exception e) {
					return -1;
				}
				return 1;
			}else if("insert_ppl_req".equals(params[0])) {//插入人员检查请求表
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PPL_ID", params[1]);
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForInsertFileData(map, new DocInfor("",
							"XNGA_PPL_REQ"), false,"",""));
				} catch (Exception e) {
					return -1;
				}
				return 3;
			}
			
			return 0;
		}

		protected void onPreExecute() {
			loadingDialog = DialogLoading.getProgressDialog(mActivity, "正在上传");
		}

		protected void onPostExecute(Integer result) {
			loadingDialog.cancel();
			switch (result) {
			case -1:// 异步NullPointerException
				T.showSnack(mActivity, R.string.serverFailed);
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					String contentId = datasetlist.CONTENTID.get(0);
					insertData(contentId);
					if (Util.isNetworkAvailable(mActivity)) {
						new AsyncLoader().execute("insert_ppl_req",mEtOwnerId.getText().toString());
						
					} else {
						T.showSnack(mActivity, R.string.networkerror);
					}
				} else {
					T.showSnack(mActivity, "文件上传失败");
				}
				break;
			case 2:
				if(Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					new AsyncLoader().execute("upload", mIvCollectImg.getTag().toString(), Util.getTypeByEnd( mIvCollectImg.getTag().toString()));
				}
				break;
			case 3:
				if(Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) { 
					String contentId = datasetlist.CONTENTID.get(0);
					queryService.startTimer(QueryTask.QUERY_PEOPLE, contentId);
				}else {
					if(loadingDialog != null) {
						loadingDialog.cancel();
					}
				}
				setResult(RESULT_OK);
				onBackPressed();
				break;
			}
		}
	}

	private String photoName = Util.getDateTime() + ".jpg"; // 原始照片名
	private Bitmap photo;
	private String filePath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (Util.hasSdcard()) {
			File picture = new File(Constants.localPath + photoName);
			System.out.println(Constants.localPath + photoName);
		} else {
			T.showSnack(this, R.string.sdCardDisabled);
		}
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
						mIvCollectImg.setTag(filePath);
						Util.displayLocalImg(CollectGoodActivity.this, mIvCollectImg, filePath, ".jpg");
						//拍照采集成功后返回当前Activity，对onResume做判断
						isFromCapture = true;
				}
			} else {
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		else if (requestCode == CAMERA_OWNER) {
			// 设置文件保存路径这里放在跟目录下
			if (Util.hasSdcard()) {
//				recognizeDialog = DialogLoading.getProgressDialog(mActivity, "正在识别信息");
				if (photo != null) {
					try {
						File file = new File(filePath);
						if (!file.exists()) {
							file.createNewFile();
						}
					} catch (Exception e) {
						
					}
					Log.v("filePath", filePath);
					ownerImg.setBackgroundColor(Color.TRANSPARENT);
					ownerImg.setTag(filePath);
					Util.displayLocalImg(CollectGoodActivity.this, ownerImg, filePath, ".jpg");
					Util.uploadAndRecognize(filePath,-1, handler, 1);
					//拍照采集成功后返回当前Activity，对onResume做判断
						isFromCapture = true;
				}
			} else {
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	  private Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
			switch(msg.what) {
			
				case 1:
					//驾驶人身份信息检查完成
//					recognizeDialog.cancel();
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
			}
		}
	  };
	  
	  private CollectCarEnity bikeEntity;
	  private SQLDataEntity sqlEntity;
		
	  public void insertData(String contentId) {
		  if(bikeEntity != null) {
				bikeEntity = null;
			}
			if(sqlEntity != null) {
				sqlEntity = null;
			}
			sqlEntity = new SQLDataEntity();
			String checkType = null;
			checkType = Constants.GOODS_CKS;
			String imgPath = mIvCollectImg.getTag().toString();
			StringBuilder imgPathBuilder = new StringBuilder();
			imgPathBuilder.append(imgPath);
			if(ownerImg.getTag()!=null && ownerImg.getTag().toString() != null) {
				imgPathBuilder.append("," + ownerImg.getTag().toString());
			}
			bikeEntity = new CollectCarEnity(Constants.USER_ID, mEtOwnerName.getText().toString(),mEtOwnerId.getText().toString(),null,mEtTime.getText().toString(),mEtLocation.getText().toString(),checkType,mEtInfo.getText().toString(),imgPathBuilder.toString());
			bikeEntity.setLon(mLon);
			bikeEntity.setLat(mLat);
			bikeEntity.setCarId(mEtName.getText().toString()); //用汽车检查表中id这个字段代替物品的名称字段
			
			sqlEntity.setCheckType(checkType);
			sqlEntity.setName(mEtOwnerName.getText().toString());
			sqlEntity.setID(mEtOwnerId.getText().toString());
			sqlEntity.setCarId(null);
			sqlEntity.setTime(mEtTime.getText().toString());
			sqlEntity.setIDImagePath(imgPathBuilder.toString());
			sqlEntity.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
			sqlEntity.setGoodsName(mEtName.getText().toString());
			
			bikeEntity.setContentId(contentId);
			sqlEntity.setContentId(contentId);
			
			enforcementDataManager.insertVEHItem(bikeEntity);
			enforcementDataManager.insertEnforcementItem(sqlEntity);
			
	  }
	  
	  
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
	            addr = location.getAddrStr();
	            mEtLat.setText(location.getLatitude()+"");
	            mEtLon.setText(location.getLongitude()+"");
	            
	        }
	            @Override  
	            public void onReceivePoi(BDLocation poiLocation) {  
	                  
	            }  
	         
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
		protected void onDestroy() {
	        if (mLocationClient != null){  
	        	mLocationClient.stop();  
	        }  
			super.onDestroy();
		}
}
