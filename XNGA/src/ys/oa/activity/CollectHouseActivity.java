package ys.oa.activity;

import java.io.File;
import java.io.FileOutputStream;

import ys.oa.util.Constants;
import ys.oa.util.L;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 出租房信息采集
 * 
 */
public class CollectHouseActivity extends FragmentActivity implements OnClickListener {

	private ImageView mIvCollectImg;
	private EditText mEtImgName, mEtName, mEtType, mEtLocation, mEtInfo;
	private ImageButton mBtnGetTime, mBtnGetLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_house);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
	}

	public void initVar() {
		Util.hasSdcard();
	}

	public void initWidget() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mIvCollectImg = (ImageView) findViewById(R.id.iv_collect_img);
		mEtImgName = (EditText) findViewById(R.id.et_img_name);
		mEtName = (EditText) findViewById(R.id.et_house_name);
		mEtType = (EditText) findViewById(R.id.et_house_type);
		mEtLocation = (EditText) findViewById(R.id.et_house_location);
		mEtInfo = (EditText) findViewById(R.id.et_info);
		// mBtnGetTime = (ImageButton) findViewById(R.id.btn_get_time);
		mBtnGetLocation = (ImageButton) findViewById(R.id.btn_get_gps);
	}

	public void initListener() {
		mIvCollectImg.setOnClickListener(this);
		// mBtnGetTime.setOnClickListener(this);
		mBtnGetLocation.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_collect_img: // 拍照并显示
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.localPath, photoName)));
			startActivityForResult(intent, Constants.PHOTOHRAPH);
			break;
		// case R.id.btn_get_time:
		// mEtTime.setText(Util.getNowTime("yyyy-MM-dd HH:mm:ss"));
		// break;
		case R.id.btn_get_gps:
			mEtLocation.setText(getLoc());
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collect, menu);
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
				T.showSnack(this, "请填写房屋名称");
			} else if (TextUtils.isEmpty(mEtType.getText().toString())) {
				T.showSnack(this, "请填写房屋类型");
			} else if (TextUtils.isEmpty(mEtLocation.getText().toString())) {
				T.showSnack(this, "请填写房屋地址");
			} else if (TextUtils.isEmpty(mEtImgName.getText().toString()) || filePath == null || "".equals(filePath)) {
				T.showSnack(this, "请采集图片");
			} else {
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
					// setResult(RESULT_OK);
					onBackPressed();
					T.showSnack(this, "上传完成");
				} else {
					T.showSnack(this, R.string.sdCardDisabled);
				}
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// 百度定位
	private LocationClient mLocationClient;

	private String getLoc() {
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
	}

	private String photoName = Util.getDateTime() + ".jpg"; // 原始照片名
	private Bitmap photo;
	private String filePath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		// 拍照
		if (requestCode == Constants.PHOTOHRAPH) {
			// 设置文件保存路径这里放在跟目录下
			L.i("拍照photoName#########" + photoName);
			if (Util.hasSdcard()) {
				File picture = new File(Constants.localPath + photoName);
				startPhotoZoom(Uri.fromFile(picture));
			} else {
				T.showSnack(this, R.string.sdCardDisabled);
			}
			if (TextUtils.isEmpty(mEtImgName.getText().toString()))
				mEtImgName.setText(photoName);
			if (photoName.indexOf(".jpg") == -1) {
				photoName += ".jpg";
			}
			mEtImgName.setText(photoName);
			filePath = Constants.localPath + photoName;
		}
		// 相册
		if (requestCode == Constants.PHOTOZOOM) {
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); // 这个是获得用户选择的图片的索引值
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

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, Constants.PHOTOTYPE);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 480);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Constants.PHOTORESULT);
	}
}
