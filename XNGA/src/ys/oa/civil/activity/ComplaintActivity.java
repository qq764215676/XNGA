package ys.oa.civil.activity;

import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 投诉举报
 * 
 */
public class ComplaintActivity extends FragmentActivity implements OnClickListener {

	private EditText mEtComplaintName, mEtComplaintTitle, mEtPhone, mEtLocation, mEtInfo, mEtSort;
	private ImageButton mBtnGetLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complaint);
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

		mEtSort = (EditText) findViewById(R.id.et_complaint_sort);
		mEtComplaintTitle = (EditText) findViewById(R.id.et_complaint_title);
		mEtComplaintName = (EditText) findViewById(R.id.et_complaint_name);
		mEtPhone = (EditText) findViewById(R.id.et_complaint_phone);
		mEtLocation = (EditText) findViewById(R.id.et_complaint_location);
		mEtInfo = (EditText) findViewById(R.id.et_info);
		mBtnGetLocation = (ImageButton) findViewById(R.id.btn_get_gps);
	}

	public void initListener() {
		mBtnGetLocation.setOnClickListener(this);
		mEtSort.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_get_gps:
			mEtLocation.setText(getLocDistrict());
			break;
		case R.id.et_complaint_sort:
			// 条目类型下拉
			initDropDown(mEtSort, getResources().getTextArray(R.array.complaintSort));
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.complaint, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_upload:
			if (TextUtils.isEmpty(mEtSort.getText().toString())) {
				T.showSnack(this, "请选择诉求类型");
			} else if (TextUtils.isEmpty(mEtComplaintTitle.getText().toString())) {
				T.showSnack(this, "请填写诉求标题");
			} else if (TextUtils.isEmpty(mEtComplaintName.getText().toString())) {
				T.showSnack(this, "请填写您的姓名");
			} else if (TextUtils.isEmpty(mEtPhone.getText().toString())) {
				T.showSnack(this, "请填写您的联系方式");
			} else if (TextUtils.isEmpty(mEtLocation.getText().toString())) {
				T.showSnack(this, "请填写所属地区");
			} else if (TextUtils.isEmpty(mEtInfo.getText().toString())) {
				T.showSnack(this, "请填写诉求内容");
			} else {
				onBackPressed();
				T.showSnack(this, "诉求提交完成");
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// 百度定位
	private LocationClient mLocationClient;

	private String getLocDistrict() {
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
		return ((AppData) getApplication()).getLocDistrict();
	}

	private PopupWindow popupWindow;

	public void initDropDown(final EditText editText, final CharSequence[] items) {
		View v = getLayoutInflater().inflate(R.layout.sort_list, null);
		popupWindow = new PopupWindow(v, editText.getWidth(), LayoutParams.WRAP_CONTENT, true);
		ListView listSorts = (ListView) v.findViewById(R.id.listSorts);
		listSorts.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.sort_item, R.id.tv_sort, items));
		listSorts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				editText.setText(((TextView) v.findViewById(R.id.tv_sort)).getText().toString());
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
		popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
		popupWindow.showAsDropDown(editText, 0, 1);
	}
}
