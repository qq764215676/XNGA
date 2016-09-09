package ys.oa.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ys.nlga.activity.R;
import ys.oa.service.QueryService;
import ys.oa.util.Constants;
import ys.oa.util.L;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import ys.oa.util.qrcode.decode.DecodeThread;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.anim.dialog.DialogLoading;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gc.materialdesign.views.ButtonFlat;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.refactech.driibo.AppData;

public class PatrolRegActivity extends BaseActivity implements OnClickListener
{

	private ImageView ivRegImg;
	private TextView tvRegId, tvRegInfo, tvRegAdd, tvRegLong, tvRegLat;
	//private TextView tvLoading;
	//private ImageView ivResultImage;
	private ImageButton btnGps;
	//private ButtonFlat btnReg;
	private Button btnReg;
	//private View vFlLoading;
	//private JumpingBeans jumpingBeans;
	private LocationClient mLocationClient;
	private String addr;
	private double lat, lng;

	private boolean isShowLoading = true;

	private DataSetList datasetlist;
	private AlertDialog loadingDialog;

	// 原始照片名
	private String photoName;
	private Bitmap photo;
	private String filePath;

	// 判断onResume前是否由拍照状态传过来
	private static boolean isFromCapture = false;

	// 用来计算两点距离
	private final double EARTH_RADIUS = 6378137.0;
	private final double MAX_DISTANCE = 2000.0;

	private QueryService service;
	private SpUtil mSpUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patrol_reg);

		// 增加界面锁屏
		LockApplication.getInstance().addActivity(this);
		initView();
		initValue();
		initLocation();
		initListener();
		service = CollectPeopleListActivity.queryService;
		mSpUtil = AppData.getInstance().getSpUtil();
	}

	// 添加返回操作
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	private void initView()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		ivRegImg = (ImageView) findViewById(R.id.iv_reg_img);

		tvRegId = (TextView) findViewById(R.id.tv_reg_id);
		tvRegInfo = (TextView) findViewById(R.id.tv_reg_info);
		tvRegAdd = (TextView) findViewById(R.id.tv_reg_add);
		tvRegLong = (TextView) findViewById(R.id.tv_reg_long);
		tvRegLat = (TextView) findViewById(R.id.tv_reg_lat);

		//ivResultImage = (ImageView) findViewById(R.id.result_image);

		btnGps = (ImageButton) findViewById(R.id.btn_reg_add);

		//btnReg = (ButtonFlat) findViewById(R.id.btn_reg);
		//btnReg.setRippleSpeed(30);
		//btnReg.setRippleColor(Color.parseColor("#8dbfe7"));
		btnReg = (Button) findViewById(R.id.btn_reg);
		
		//vFlLoading = findViewById(R.id.fl_reg_loading);
		//tvLoading = (TextView) findViewById(R.id.tv_reg_loading);
		//jumpingBeans = new JumpingBeans.Builder().appendJumpingDots(tvLoading).build();

	}

	private void initValue()
	{
		initQrValue();

		photoName = Util.getDateTime() + ".jpg";
		System.out.println("initValue photoName " + photoName);

		mLocationClient = ((AppData) getApplication()).mLocationClient;
		tvRegId.setText(Constants.USER_ID);
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

	private void initListener()
	{
		btnGps.setOnClickListener(this);
		btnReg.setOnClickListener(this);
		ivRegImg.setOnClickListener(this);
	}

	// 获取二维码拍摄获取的信息，包括图像及文字
	private void initQrValue()
	{
		Bundle extras = getIntent().getExtras();
		if (null != extras)
		{
			int width = extras.getInt("width");
			int height = extras.getInt("height");

			LayoutParams lps = new LayoutParams(width, height);
			lps.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
			lps.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
			lps.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

			//ivResultImage.setLayoutParams(lps);

			String result = extras.getString("result");
			try
			{
				String utfStr = new String(result.getBytes("ISO-8859-1"), "UTF-8");
				String gbStr = new String(result.getBytes("ISO-8859-1"), "GB2312");
				System.out.println("UTF_Str " + utfStr);
				System.out.println("GB_Str " + gbStr);
				// 如果UTF与GB解码出来都是乱码且相同，则ZXing解码正确
				if (utfStr.equals(gbStr))
				{
					tvRegInfo.setText(result);
				}
				// 如果UTF与GB解码出来不同，经测试GB正确，使用该解码
				else
				{
					tvRegInfo.setText(gbStr);
				}
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// mResultText.setText(result);

			Bitmap barcode = null;
			byte[] compressedBitmap = extras.getByteArray(DecodeThread.BARCODE_BITMAP);
			if (compressedBitmap != null)
			{
				barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
				// Mutable copy:
				barcode = barcode.copy(Bitmap.Config.RGB_565, true);
			}

			// 此处无需显示二维码，故匿去
			// mResultImage.setImageBitmap(barcode);
		}
	}

	private void setLoc()
	{
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
		String loc = ((AppData) getApplication()).getLoc();
		tvRegAdd.setText(loc);
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
			lat = location.getLatitude();
			lng = location.getLongitude();

		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation)
		{

		}

	}

	private void setLongAndLat()
	{
		mLocationClient.registerLocationListener(new BDLocationListener()
		{

			@Override
			public void onReceiveLocation(BDLocation location)
			{
				// TODO Auto-generated method stub
				double longtitude = location.getLongitude();
				double latitude = location.getLatitude();
				System.out.println("longtitude " + longtitude + "\nlatitude " + latitude);
				tvRegLong.setText(longtitude + "");
				tvRegLat.setText(latitude + "");
			}

			@Override
			public void onReceivePoi(BDLocation poiLocation)
			{
				// TODO Auto-generated method stub
			}

		});

	}

	// 获取图片名称
	private String getImgName(String imgUrl)
	{
		if (imgUrl == null) { return null; }
		String[] strs = imgUrl.split("\\.");
		// String strsEnd = strs[strs.length - 1];
		System.out.println("ImgName " + strs[0]);
		return strs[0];
		// return imgUrl.substring(0, strsEnd.length()-3);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.iv_reg_img:
				// photoName = Util.getDateTime() + ".jpg";
				// 拍照并显示
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
				startActivityForResult(intent, Constants.PHOTOHRAPH);
				break;
			case R.id.btn_reg_add:
				tvRegAdd.setText(addr);
				tvRegLat.setText(lat + "");
				tvRegLong.setText(lng + "");
				// setLoc();
				// setLongAndLat();
				break;
			case R.id.btn_reg:
				if (Util.isNetworkAvailable(PatrolRegActivity.this))
				{
					// 信息填写完整正常提交签到
					if (!tvRegAdd.getText().toString().equals("") && !tvRegLong.getText().toString().equals(""))
					{
						// 首先判断人员在不在规定距离内
						new AsyncLoader().execute("judgeDistance", tvRegInfo.getText().toString());

					}
					else
					{
						T.showSnack(PatrolRegActivity.this, "请获取地点信息！");
					}

				}
				else
				{
					T.showSnack(PatrolRegActivity.this, R.string.networkerror);
				}
				break;
		}
	}

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
				if (!picture.exists())
				{
					picture.mkdir();
				}
				// startPhotoZoom(Uri.fromFile(picture));
			}
			else
			{
				T.showSnack(this, R.string.sdCardDisabled);
			}
			if (photoName.indexOf(".jpg") == -1)
			{
				photoName += ".jpg";
			}
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
					}
					catch (Exception e)
					{
					}
					Log.v("filePath", filePath);
					ivRegImg.setBackgroundColor(Color.TRANSPARENT);
					Util.displayLocalImg(PatrolRegActivity.this, ivRegImg, filePath, ".jpg");

					// 拍照采集成功后返回当前Activity，对onResume做判断
					isFromCapture = true;
				}
			}
			else
			{
				T.showSnack(this, R.string.sdCardDisabled);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	class AsyncLoader extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... params)
		{
			// TODO Auto-generated method stub
			if ("reg".equals(params[0]))
			{
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentT = format.format(new Date());

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("USER_ID", tvRegId.getText().toString());
				map.put("QRCODE_INFO", tvRegInfo.getText().toString());
				map.put("REG_ADD", tvRegAdd.getText().toString());
				map.put("REG_LONG", tvRegLong.getText().toString());
				map.put("REG_LAT", tvRegLat.getText().toString());
				map.put("REG_TIME", currentT);
				if (!TextUtils.isEmpty(filePath))
				{
					map.put("IMA_KV", getImgName(photoName));
				}

				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageForSaveOrUpdate(map, new DocInfor("", "XNGA_PATROL_REG"), false));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 1;
			}
			// 上传自拍图片
			else if ("uploadPic".equals(params[0]))
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("IMA_KEY", tvRegId.getText().toString() + "_patrol_reg");
				map.put("IMA_VAL", getImgName(photoName));
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
			else if ("judgeDistance".equals(params[0]))
			{
				try
				{
					datasetlist = PostHttp.PostXML(XmlPackage.packageSelect("from XNGA_PATROL_REG_REF where QRCODE_INFO='" + params[1] + "'", "", "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "XNGA_PATROL_REG_REF"), true, false));
				}
				catch (Exception e)
				{
					return -1;
				}
				return 3;
			}

			return -1;
		}

		protected void onPreExecute()
		{
			if (isShowLoading)
			{
				loadingDialog = DialogLoading.getProgressDialog(PatrolRegActivity.this, "正在上传");
			}
		}

		protected void onPostExecute(Integer result)
		{
			switch (result)
			{
				case -1:// 异步NullPointerException
					loadingDialog.cancel();
					T.showSnack(PatrolRegActivity.this, R.string.serverFailed);
					break;
				case 1:
					loadingDialog.cancel();
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						isShowLoading = false;
						setResult(RESULT_OK);
						T.showShort(PatrolRegActivity.this, "签到成功！");
						String mRegDate = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分").format(new Date(System.currentTimeMillis()));
						mSpUtil.putLastRegTime(mRegDate);
						service.startAlertTimer();
						Intent intent = new Intent(PatrolRegActivity.this, CollectPeopleListActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
					break;
				case 2:
					loadingDialog.cancel();
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(PatrolRegActivity.this))
						{
							isShowLoading = false;
							new AsyncLoader().execute("reg");
						}
						else
						{
							T.showSnack(PatrolRegActivity.this, R.string.networkerror);
						}
					}
					else
					{
						if (loadingDialog != null)
						{
							loadingDialog.cancel();
						}
						T.showSnack(PatrolRegActivity.this, "文件上传失败");

					}
					break;
				case 3:
					loadingDialog.cancel();
					// 找到与数据库中二维码匹配的经纬度坐标
					if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS))
					{
						if (Util.isNetworkAvailable(PatrolRegActivity.this))
						{
							isShowLoading = false;

							ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
							ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
							ArrayList<String> documentId = (ArrayList<String>) datasetlist.DOCUMENTID;

							int infoN = 0;
							int longN = 0;
							int latN = 0;
							for (int i = 0; i < nameList.size(); i++)
							{
								if ("QRCODE_INFO".equals(nameList.get(i)))
								{
									infoN = i;
								}
								if ("REG_LONG".equals(nameList.get(i)))
								{
									longN = i;
								}
								if ("REG_LAT".equals(nameList.get(i)))
								{
									latN = i;
								}
							}

							System.out.println(infoN + "  " + longN + "  " + latN);

							// 如果有查询结果
							if (nameList.size() != 0)
							{
								double latCurrent = Double.parseDouble(tvRegLat.getText().toString());
								double longCurrent = Double.parseDouble(tvRegLong.getText().toString());

								double latRef = Double.parseDouble(valueList.get(latN));
								double longRef = Double.parseDouble(valueList.get(longN));
								double distance = getDistance(latCurrent, longCurrent, latRef, longRef);
								System.out.println("distance " + distance);

								// 如果警员在最大距离范围内则进行签到
								if (distance < MAX_DISTANCE)
								{

									// 判断有无自拍图片
									if (TextUtils.isEmpty(filePath))
									{
										// 如果没有自拍图片则只上传文字信息
										new AsyncLoader().execute("reg");
									}
									else
									{
										// 有自拍图片则先上传图片再上传文字信息
										new AsyncLoader().execute("uploadPic", filePath, Util.getTypeByEnd(filePath));
									}

								}
								else
								{
									// T.showSnack(PatrolRegActivity.this,
									// "签到距离" + tvRegInfo.getText().toString() +
									// "过远");
									new AlertDialog.Builder(PatrolRegActivity.this).setTitle("警告").setMessage("签到距离" + tvRegInfo.getText().toString() + "过远，请靠近后重新获取地点后签到").setPositiveButton("确定", null).show();
									// Dialog dialog = new
									// Dialog(PatrolRegActivity.this,"警告",
									// "签到距离" + tvRegInfo.getText().toString() +
									// "过远，请靠近后重新获取地点然后签到");
									// dialog.show();
								}
							}
							else
							{
								// T.showSnack(PatrolRegActivity.this,
								// "无该二维码签到信息");

								// Updated at 2016/03/25
								// 解决巡逻签到位置参照表里无信息，但仍旧需要上传问题
								// 判断有无自拍图片
								if (TextUtils.isEmpty(filePath))
								{
									// 如果没有自拍图片则只上传文字信息
									new AsyncLoader().execute("reg");
								}
								else
								{
									// 有自拍图片则先上传图片再上传文字信息
									new AsyncLoader().execute("uploadPic", filePath, Util.getTypeByEnd(filePath));
								}

							}

						}
						else
						{
							T.showSnack(PatrolRegActivity.this, R.string.networkerror);
						}
					}
					else
					{
						if (loadingDialog != null)
						{
							loadingDialog.cancel();
						}
						T.showSnack(PatrolRegActivity.this, "数据库中暂无与二维码匹配的地址");

					}
					break;
			}
		}

	}

	/**
	 * 
	 * @param lat_a
	 *            A点纬度
	 * @param lng_a
	 *            A点经度
	 * @param lat_b
	 *            B点纬度
	 * @param lng_b
	 *            B点经度
	 * @return
	 */
	private double getDistance(double lat_a, double lng_a, double lat_b, double lng_b)
	{
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
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

	@Override
	protected void onResume()
	{
		super.onResume();
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
	protected void onDestroy()
	{
		// 退出时销毁定位
		if (mLocationClient != null)
		{
			mLocationClient.stop();
		}

		super.onDestroy();
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

	@Override
	protected void onStop()
	{
		mLocationClient.stop();
		super.onStop();
	}
}
