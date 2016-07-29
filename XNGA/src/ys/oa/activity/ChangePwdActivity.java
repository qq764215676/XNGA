package ys.oa.activity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.anim.dialog.DialogLoading;
import com.gc.materialdesign.views.ButtonFlat;
import com.material.widget.FloatingEditText;
import com.net.post.DataSetList;
import com.net.post.DocInfor;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import ys.nlga.activity.R;
import ys.oa.util.Constants;
import ys.oa.util.T;
import ys.oa.util.Util;

public class ChangePwdActivity extends BaseActivity implements OnClickListener  {
	private FloatingEditText etPwdNew, etPwdConfirm;
	private ButtonFlat btnCheck,btnChange;
	
	private DataSetList datasetlist;
	private AlertDialog dialog;
	private boolean isShowLoading = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		etPwdNew = (FloatingEditText) findViewById(R.id.et_pwd_new);
		etPwdConfirm = (FloatingEditText) findViewById(R.id.et_pwd_new_confirm);
		btnCheck = (ButtonFlat) findViewById(R.id.btn_check);
		btnChange = (ButtonFlat) findViewById(R.id.btn_change_pwd);
		
		etPwdNew.setOnClickListener(this);
		etPwdConfirm.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
		btnChange.setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_check:
			if (!passwordFormat(etPwdNew.getText().toString())) {
				T.showShort(ChangePwdActivity.this,"密码必须以字母开头，长度在5~16之间!");
			}else{
				T.showShort(ChangePwdActivity.this, "密码符合规范");
			}
			break;
		case R.id.btn_change_pwd:
			//判断两次密码输入是否相同
			if(etPwdNew.getText().toString().equals(etPwdConfirm.getText().toString())){
				//提交新密码
				if (Util.isNetworkAvailable(getApplicationContext())) {
					isShowLoading = false;
					new AsyncLoader().execute("changePwd",Constants.USER_ID + "");
				}
			}else{
				T.showShort(ChangePwdActivity.this, "两次输入密码不同");
			}
			break;
		}
		
	}
	
	//密码必须以字母开头，长度在5~16之间，只能包含字符、数字和下划线
	private boolean passwordFormat(String password) {
	    Pattern pattern = Pattern.compile("^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{5,15}$");
	    Matcher mc = pattern.matcher(password);
	    return mc.matches();
	  }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//ChangePwdActivity.this.finish();
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		// 显示九宫格解锁
		LockApplication.notShowLock = false;
	}

	public void back() {
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed() {
		ChangePwdActivity.this.finish();
		//back();
	}
	
	class AsyncLoader extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			if ("changePwd".equals(params[0])) {
				HashMap<String, String> map = new HashMap<String, String>();
				
				//map.put("USER_ID", Constants.USER_ID + "");
				map.put("USER_PWD", Util.md5(etPwdConfirm.getText().toString()));
				try {
					//获取Fragemnt传递来的userContentId
					String userContentId = getIntent().getStringExtra("userContentId");
					datasetlist = PostHttp.PostXML(XmlPackage.packageForSaveOrUpdate(map, new DocInfor(userContentId,
							"XNGA_USER"), false));
				} catch (Exception e) {
					return -1;
				}
				return 1;
				
			}
			
			return -1;
		}
		
		protected void onPreExecute() {
			dialog = DialogLoading.getProgressDialog(ChangePwdActivity.this, "正在修改");
		}
		
		protected void onPostExecute(Integer result) {
			dialog.cancel();
			switch (result) {
			case -1:// 异步NullPointerException
				T.showSnack(ChangePwdActivity.this, R.string.serverFailed);
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					if (Util.isNetworkAvailable(ChangePwdActivity.this)) {
						setResult(RESULT_OK);
						onBackPressed();
						// T.showSnack(mActivity, "上传完成");
					} else {
						T.showSnack(ChangePwdActivity.this, R.string.networkerror);
					}
				} else {
					T.showSnack(ChangePwdActivity.this, "密码修改失败");
				}
				break;
			}
		}
		
	}


}
