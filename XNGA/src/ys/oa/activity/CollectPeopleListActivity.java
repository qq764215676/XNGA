package ys.oa.activity;

import java.util.Timer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import ys.oa.fragment.CollectPeopleFragment;
import ys.oa.service.QueryService;
import ys.oa.task.QueryResultListener;
import ys.oa.util.Constants;
import ys.oa.util.Util;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import com.devspark.msg.ToastMsg;
import ys.nlga.activity.R;

public class CollectPeopleListActivity extends BaseActivity implements OnClickListener,QueryResultListener {
	private CollectPeopleFragment mContentFragment;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	public static QueryService queryService;
	private Context context;
	private Activity mActivity;
	// private String title;
	
	// 下拉菜单
	private PopupWindow popWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_news);
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		context = this;
		mActivity = this;
		queryService = QueryService.getInstance(this, mActivity,this);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		// title = getIntent().getStringExtra("Title");

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.headerInAnimation = R.anim.pulldown_fade_in;
		options.headerOutAnimation = R.anim.pulldown_fade_out;
		options.refreshScrollDistance = 0.3f;
		options.headerLayout = R.layout.pulldown_header;
		mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);

		mPullToRefreshAttacher.setRefreshing(false);
		setTitle("路面稽查列表");
		mContentFragment = CollectPeopleFragment.newInstance(context);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
		
		// ToastMsg.showSticky(RecordListActivity.this, "有新的版本,快更新吧!");
	}

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	private View v;
	private int result = 0;
	private MenuItem msgNormal;
	private MenuItem msgComplete;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.title_msg, menu);
		getMenuInflater().inflate(R.menu.add, menu);
		msgNormal = (MenuItem)menu.findItem(R.id.action_msg_1);
		msgComplete = (MenuItem)menu.findItem(R.id.action_msg_2);
		if(result == 0) {
			msgNormal.setVisible(true);
			msgComplete.setVisible(false);
		}else if(result == 1) {
			msgNormal.setVisible(false);
			msgComplete.setVisible(true);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_add:
			 v = findViewById(R.id.action_add);
//			startActivityForResult(new Intent(this, CollectPeopleActivity.class), 0);
			showPopUp(v);
			return true;
		case R.id.action_msg_2:
		case R.id.action_msg_1:
			msgNormal.setVisible(true);
			msgComplete.setVisible(false);
			Intent i = new Intent();
			i.setClass(this, QueryResultListActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
			ToastMsg.showMsgTop(CollectPeopleListActivity.this, "上传成功");
		
		switch (requestCode) {
		case 0:
			mContentFragment.loadFirstPage();
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	//add by fcr at 2016-01-15
	private PopupWindow popupWindow;
	@SuppressLint("ResourceAsColor")
	private void showPopUp(View v) {
		//获取一个填充器
		LayoutInflater inflater = LayoutInflater.from(this);
		//填充自定义下拉菜单布局
		View view = inflater.inflate(R.layout.dialog_pop, null);
		//得到当前屏幕的显示器对象
		Display display = getWindowManager().getDefaultDisplay();
		//创建一个Point点对象用来接收屏幕尺寸信息
		Point size = new Point();
		//Point点对象接收当前设备屏幕尺寸信息
		display.getSize(size);
		//从Point点对象中获取屏幕的宽度(单位像素)
		int width = size.x;
		//从Point点对象中获取屏幕的高度(单位像素)
		int height = size.y;
		//width=480,height=854可知手机的像素是480x854的
		Log.v("zxy", "width="+width+",height="+height);
		//创建一个PopupWindow对象，第二个参数是设置宽度的，用刚刚获取到的屏幕宽度乘以1/3，取该屏幕的1/3宽度，从而在任何设备中都可以适配，高度则包裹内容即可，最后一个参数是设置得到焦点
		popWindow = new PopupWindow(view, width/3, LayoutParams.WRAP_CONTENT, true);
		//设置PopupWindow的背景为一个空的Drawable对象，如果不设置这个，那么PopupWindow弹出后就无法退出了
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		//设置是否点击PopupWindow外退出PopupWindow
		popWindow.setOutsideTouchable(true);
		
		//创建当前界面的一个参数对象
		WindowManager.LayoutParams params = getWindow().getAttributes();
		//设置参数的透明度为0.8，透明度取值为0~1，1为完全不透明，0为完全透明，因为android中默认的屏幕颜色都是纯黑色的，所以如果设置为1，那么背景将都是黑色，设置为0，背景显示我们的当前界面
		params.alpha = 0.8f;
		//把该参数对象设置进当前界面中
		getWindow().setAttributes(params);
		//设置PopupWindow退出监听器
		popWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				//如果PopupWindow消失了，即退出了，那么触发该事件，然后把当前界面的透明度设置为不透明
				WindowManager.LayoutParams params = getWindow().getAttributes();
				//设置为不透明，即恢复原来的界面
				params.alpha = 1.0f;
				getWindow().setAttributes(params);
			}
		});
		//第一个参数为父View对象，即PopupWindow所在的父控件对象，第二个参数为它的重心，后面两个分别为x轴和y轴的偏移量
		popWindow.showAsDropDown(v); 
		popWindow.showAtLocation(inflater.inflate(R.layout.activity_news, null), Gravity.CENTER, 0, 0);
		
		LinearLayout llPopPpl = (LinearLayout) view.findViewById(R.id.ll_pop_people);
		LinearLayout llPopCar = (LinearLayout) view.findViewById(R.id.ll_pop_car);
		LinearLayout llPopMotor = (LinearLayout) view.findViewById(R.id.ll_pop_motor);
		LinearLayout llPopBike = (LinearLayout) view.findViewById(R.id.ll_pop_bike);
		LinearLayout llPopElect = (LinearLayout) view.findViewById(R.id.ll_pop_elect);
		LinearLayout llPopGoods = (LinearLayout) view.findViewById(R.id.ll_pop_goods);
		LinearLayout llPopReg = (LinearLayout) view.findViewById(R.id.ll_pop_reg);
		
		ImageView ivDl1 = (ImageView) view.findViewById(R.id.iv_dl1);
		ImageView ivDl2 = (ImageView) view.findViewById(R.id.iv_dl2);
		ImageView ivDl3 = (ImageView) view.findViewById(R.id.iv_dl3);
		ImageView ivDl4 = (ImageView) view.findViewById(R.id.iv_dl4);
		ImageView ivDl5 = (ImageView) view.findViewById(R.id.iv_dl5);
		ImageView ivDl6 = (ImageView) view.findViewById(R.id.iv_dl6);
		
		//Added at 2016/02/02
		//设置点击权限，权限为99（协警）人员目前只有巡逻签到功能权限
		String user = Constants.USER_ID;
		String auth = Constants.USER_AUTH;
		System.out.println(user + "权限为" + auth);
		//当用户为协警时
		//Updated at 2016/02/22
		//协警路面稽查列表同正常警员
//		if (auth.equals(Constants.USER_AUTH_XJ)){
//			//其他模块不予显示
//			llPopPpl.setVisibility(View.GONE);
//			llPopCar.setVisibility(View.GONE);
//			llPopMotor.setVisibility(View.GONE);
//			llPopBike.setVisibility(View.GONE);
//			llPopElect.setVisibility(View.GONE);
//			llPopGoods.setVisibility(View.GONE);
//			
//			//间隔线也不予显示
//			ivDl1.setVisibility(View.GONE);
//			ivDl2.setVisibility(View.GONE);
//			ivDl3.setVisibility(View.GONE);
//			ivDl4.setVisibility(View.GONE);
//			ivDl5.setVisibility(View.GONE);
//			ivDl6.setVisibility(View.GONE);
//		}
		
		llPopPpl.setOnClickListener(this);
		llPopCar.setOnClickListener(this);
		llPopMotor.setOnClickListener(this);
		llPopBike.setOnClickListener(this);
		llPopElect.setOnClickListener(this);
		llPopGoods.setOnClickListener(this);
		llPopReg.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.ll_pop_people:
			startActivityForResult(new Intent(this, CollectPeopleActivity.class), 0);
			popWindow.dismiss();
			break;
		case R.id.ll_pop_car:
			popWindow.dismiss();
			Constants.vehicleType="2";
			startActivityForResult(new Intent(this, CollectCarActivity.class), 0);
			break;
		case R.id.ll_pop_motor:
			popWindow.dismiss();
			Constants.vehicleType="3";
			startActivityForResult(new Intent(this, CollectCarActivity.class), 0);
			break;
		case R.id.ll_pop_bike:
			popWindow.dismiss();
			Constants.vehicleType="1";
			startActivityForResult(new Intent(this, CollectBikeActivity.class), 0);
			break;
		case R.id.ll_pop_elect:
			popWindow.dismiss();
			Constants.vehicleType="0";
			startActivityForResult(new Intent(this, CollectBikeActivity.class), 0);
			break;
		case R.id.ll_pop_goods:
			popWindow.dismiss();
			startActivityForResult(new Intent(this, CollectGoodActivity.class), 0);
			break;
		case R.id.ll_pop_reg:
//			startActivityForResult(new Intent(this, CaptureActivity.class), 0);
			startActivityForResult(new Intent(this, ScanCodeActivity.class), 0);
			popWindow.dismiss();
			break;
		}
	} 
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(popupWindow !=null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
		LockApplication.notShowLock = false;
		if(mContentFragment!= null) {
			mContentFragment.loadFirstPage();
		}
		super.onResume();
	}
	//add end

	@Override
	public void onComplete(Timer timer) {
		// TODO Auto-generated method stub
		if(queryService != null) {
			queryService.cancelTimer(timer);
		}
		Message msg = new Message();
		handler.sendMessage(msg);
	}
	
	public void back() {
		LockApplication.getInstance().exit();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		//back();
	}
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			result = 1;
			msgNormal.setVisible(false);
			msgComplete.setVisible(true);
		}
	};
	
	
}
