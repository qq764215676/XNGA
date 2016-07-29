package ys.oa.civil.fragment;

import ys.oa.civil.activity.CivilLoginActivity;
import ys.oa.civil.activity.CivilMainActivity;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.LayoutRipple;
import com.gc.materialdesign.views.Switch;
import com.net.post.DataSetList;
import com.net.post.PostHttp;
import com.net.post.XmlPackage;
import com.nineoldandroids.view.ViewHelper;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

public class CivilDrawerFragment extends Fragment implements OnClickListener {
	private LayoutRipple mLrLogin, mLrLogout, mLrIsAutologin, mLrHelp, mLrUpdate;
	private ImageButton mBtnLogin;
	private TextView mTvLoginName;
	private Switch mCbIsAutologin;
	private SpUtil mSpUtil;
	private CivilMainActivity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_drawer, null);
		mainActivity = (CivilMainActivity) getActivity();
		mSpUtil = AppData.getInstance().getSpUtil();

		mTvLoginName = (TextView) v.findViewById(R.id.tv_login_name);
		mBtnLogin = (ImageButton) v.findViewById(R.id.btn_login);

		mLrLogout = (LayoutRipple) v.findViewById(R.id.lr_logout);
		mLrLogout.setRippleSpeed(40);
		mLrLogout.setOnClickListener(this);
		mLrIsAutologin = (LayoutRipple) v.findViewById(R.id.lr_is_autologin);
		mLrIsAutologin.setRippleSpeed(40);
		mLrIsAutologin.setOnClickListener(this);
		mLrUpdate = (LayoutRipple) v.findViewById(R.id.lr_update);
		mLrUpdate.setRippleSpeed(40);
		mLrUpdate.setOnClickListener(this);
		mLrHelp = (LayoutRipple) v.findViewById(R.id.lr_clean);
		mLrHelp.setRippleSpeed(40);
		mLrHelp.setOnClickListener(this);
		mLrLogin = (LayoutRipple) v.findViewById(R.id.lr_login);
		setOriginRiple(mLrLogin);
		mLrLogin.setOnClickListener(this);

		mCbIsAutologin = (Switch) v.findViewById(R.id.cb_is_autologin);
		mCbIsAutologin.setChecked(mSpUtil.getIsAutoLoginCivil());

		if (mSpUtil.getIsAutoLoginCivil()) {
			if (Util.isNetworkAvailable(mainActivity)) {
				new AsyncLoader().execute("login", mSpUtil.getAccountCivil(), mSpUtil.getPwdCivil());
			} else {
				T.showSnack(mainActivity, R.string.networkerror);
			}
		}
		return v;
	}

	private void setOriginRiple(final LayoutRipple layoutRipple) {
		layoutRipple.post(new Runnable() {

			@Override
			public void run() {
				View v = layoutRipple.getChildAt(0);
				layoutRipple.setxRippleOrigin(ViewHelper.getX(v) + v.getWidth() / 2);
				layoutRipple.setyRippleOrigin(ViewHelper.getY(v) + v.getHeight() / 2);
				layoutRipple.setRippleColor(Color.parseColor("#212121"));
				layoutRipple.setRippleSpeed(40);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lr_login:
			if ("立即登陆".equals(mTvLoginName.getText().toString())) {
				startActivityForResult(new Intent(mainActivity, CivilLoginActivity.class).putExtra("logout", true), 0);
				// mCbIsAutologin.setChecked(false);
			}
			break;
		case R.id.lr_logout:
			mBtnLogin.setBackgroundResource(R.drawable.drawer_head_logo);
			mTvLoginName.setText("立即登陆");
			break;
		case R.id.lr_is_autologin:
			mCbIsAutologin.setChecked(!mCbIsAutologin.isChecked());
			mSpUtil.setIsAutoLoginCivil(mCbIsAutologin.isChecked());
			break;
		case R.id.lr_clean:
			// mHelpDialog.show();
			break;
		case R.id.lr_update:
			// mainActivity.checkUpdate(true);
			T.showSnack(mainActivity, "已更新到最新版本！！！");
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == 0) { // 登陆页返回
			mBtnLogin.setBackgroundResource(R.drawable.drawer_head_default);
			mTvLoginName.setText(Constants.USERID);
			mCbIsAutologin.setChecked(mSpUtil.getIsAutoLoginCivil());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public DataSetList datasetlist;

	class AsyncLoader extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			if ("login".equals(params[0])) {
				try {
					datasetlist = PostHttp.PostXML(XmlPackage.packageForLogin(params[1], params[2]));
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
				return 1;
			}
			return 0;
		}

		protected void onPreExecute() {
		}

		protected void onPostExecute(Integer result) {
			switch (result) {
			case -1:
				if (Util.isNetworkAvailable(mainActivity)) {
					Toast.makeText(mainActivity, getString(R.string.serverFailed), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mainActivity, getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (Constants.REQUESTSUCCESS.equals(datasetlist.SUCCESS)) {
					Constants.USERID = mSpUtil.getAccountCivil();
					Constants.PSWID = mSpUtil.getPwdCivil();
					mBtnLogin.setBackgroundResource(R.drawable.drawer_head_default);
					mTvLoginName.setText(Constants.USERID);
				} else {
					if ("立即登陆".equals(mTvLoginName.getText().toString())) {
						startActivityForResult(
								new Intent(mainActivity, CivilLoginActivity.class).putExtra("logout", true), 0);
					}
				}
				break;
			}
		}
	}
}
