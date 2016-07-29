package ys.oa.activity;

import java.util.ArrayList;
import java.util.List;

import ys.oa.adapter.GridMenuAdapter;
import ys.oa.civil.activity.WebActivity;
import ys.oa.enity.MenusEnity;
import ys.oa.enity.SectionEnity;
import ys.oa.fragment.DrawerFragment;
import ys.oa.task.UpdateAndDownloadTask;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.T;
import ys.oa.util.Util;
import ys.oa.widget.AbRotate3dAnimation;
import ys.oa.widget.AutoGridView;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import ch.fshi.btoppnet.BTDeviceListActivity;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 主页面
 * 
 */
public class MainActivity extends BaseActivity implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener {

	private AutoGridView mGvMenu;
	private SpUtil mSpUtil;
	private ArrayList<MenusEnity> mainMenuList = new ArrayList<MenusEnity>();
	private GridMenuAdapter mAdapter;
	private View mTagSub;
	private boolean isHaveTagShow = false;
	DrawerFragment drawerFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		
		initVar();
		initWidget();
		new UpdateAndDownloadTask(this,0).execute("update");
//		shouldUpdate();
		initListener();
		startService();
		Log.v("fcr","版本测试log");
	}
	

	private void initMenuDate() {
		if (Constants.menuList.size() == 0) {
			int index = 0;
			
			//业务查询Search
			MenusEnity seEnity;
			seEnity = new MenusEnity();
			seEnity.setMenuTitle("业务查询");
			seEnity.setDrawableId(R.drawable.menu_news_search);
			seEnity.setIndexInMenuList(index++);
			//enity.setToIntent(new Intent(this, EnforcementView.class).putExtra("list", listSection).putExtra("Title", "执法规范列表"));
			seEnity.setToIntent(new Intent(this, SearchMainActivity.class));
			Constants.menuList.add(seEnity);
			
			//add by fcr at 2016-01-15
			ArrayList<SectionEnity> listSection;
			listSection = new ArrayList<SectionEnity>();
			listSection.add(new SectionEnity("路面稽查", CollectPeopleListActivity.class, "路面稽查"));
			
			//Added at 2016/04/25 by Perng
			listSection.add(new SectionEnity("接处警",null,"接处警"));
			listSection.add(new SectionEnity("现场勘查",null,"现场勘查"));
			listSection.add(new SectionEnity("手机核查",null,"手机核查"));
			listSection.add(new SectionEnity("法律法规",null,"法律法规"));
			
			MenusEnity enity;
			enity = new MenusEnity();
			enity.setMenuTitle("执法规范");
			enity.setDrawableId(R.drawable.menu_car_get);
			enity.setIndexInMenuList(index++);
			//enity.setToIntent(new Intent(this, EnforcementView.class).putExtra("list", listSection).putExtra("Title", "执法规范列表"));
			enity.setToIntent(new Intent(this, SecondMenuActivity.class).putExtra("list", listSection).putExtra(
					"Title", "执法规范列表"));
			Constants.menuList.add(enity);
			
			//日常办公DailyWork
			ArrayList<SectionEnity> dwListSection;
			dwListSection = new ArrayList<SectionEnity>();
			dwListSection.add(new SectionEnity("办公审批", null, "办公审批"));
			dwListSection.add(new SectionEnity("工作提醒", null, "工作提醒"));
			
			MenusEnity dwEnity;
			dwEnity = new MenusEnity();
			dwEnity.setMenuTitle("日常办公");
			dwEnity.setDrawableId(R.drawable.menu_case_search);
			dwEnity.setIndexInMenuList(index++);
			dwEnity.setToIntent(new Intent(this, SecondMenuActivity.class).putExtra("list", dwListSection).putExtra(
					"Title", "日常办公列表"));
			Constants.menuList.add(dwEnity);
			
			//治安管理SecurityManagement
			ArrayList<SectionEnity> smListSection;
			smListSection = new ArrayList<SectionEnity>();
			smListSection.add(new SectionEnity("社区警务", null, "社区警务"));
			smListSection.add(new SectionEnity("治安工作", null, "治安工作"));
			
			MenusEnity smEnity;
			smEnity = new MenusEnity();
			smEnity.setMenuTitle("治安管理");
			smEnity.setDrawableId(R.drawable.menu_car_search);
			smEnity.setIndexInMenuList(index++);
			smEnity.setToIntent(new Intent(this, SecondMenuActivity.class).putExtra("list", dwListSection).putExtra(
					"Title", "治安管理列表"));
			Constants.menuList.add(smEnity);
			
			//服务群众Serving the masses
			MenusEnity semEnity;
			semEnity = new MenusEnity();
			semEnity.setMenuTitle("服务群众");
			semEnity.setDrawableId(R.drawable.menu_complaint_search);
			semEnity.setIndexInMenuList(index++);
			Constants.menuList.add(semEnity);
			
			//add end by fcr
			
			// 初始化所有菜单项列表
		/*	MenusEnity enity;
			enity = new MenusEnity();
			enity.setMenuTitle("警务快讯");
			enity.setDrawableId(R.drawable.menu_news);
			enity.setToIntent(new Intent(this, NewsActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);

			ArrayList<SectionEnity> listSection;
			listSection = new ArrayList<SectionEnity>();
			listSection.add(new SectionEnity("基础信息采集"));
			listSection.add(new SectionEnity("入户登记表", RecordListActivity.class, "入户登记表"));
			listSection.add(new SectionEnity("出租屋检查表", RecordListActivity.class, "出租屋检查表"));
			listSection.add(new SectionEnity("居住证办理", null, "居住证办理"));
			listSection.add(new SectionEnity("路面稽查"));
			listSection.add(new SectionEnity("人员检查", CollectPeopleListActivity.class));
			listSection.add(new SectionEnity("汽车检查", CollectCarListActivity.class, "汽车检查"));
			listSection.add(new SectionEnity("摩托车检查", CollectCarListActivity.class, "摩托车检查"));
			listSection.add(new SectionEnity("自行车检查", CollectBikeListActivity.class, "自行车检查"));
			listSection.add(new SectionEnity("电动车检查", CollectBikeListActivity.class, "电动车检查"));
			listSection.add(new SectionEnity("物品检查", CollectGoodListActivity.class));
			enity = new MenusEnity();
			enity.setMenuTitle("信息采集");
			enity.setDrawableId(R.drawable.menu_car_get);
			enity.setToIntent(new Intent(this, SecondMenuActivity.class).putExtra("list", listSection).putExtra(
					"Title", "信息采集"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);

			enity = new MenusEnity();
			enity.setMenuTitle("通讯录检查");
			enity.setDrawableId(R.drawable.menu_car_search);
			// enity.setToIntent(new Intent(this, CollectActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("MAC地址检查");
			enity.setDrawableId(R.drawable.menu_car_search);
			// enity.setToIntent(new Intent(this, CollectActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);

			enity = new MenusEnity();
			enity.setMenuTitle("出警记录");
			enity.setDrawableId(R.drawable.menu_record);
			enity.setToIntent(new Intent(this, RecordListActivity.class).putExtra("Title", "出警记录表"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("现场勘查");
			enity.setDrawableId(R.drawable.menu_case_search);
			enity.setToIntent(new Intent(this, RecordListActivity.class).putExtra("Title", "现场勘查记录表"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("社区入户访查");
			enity.setDrawableId(R.drawable.menu_house);
			enity.setToIntent(new Intent(this, RecordListActivity.class).putExtra("Title", "出租屋检查表"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("道路稽查");
			enity.setDrawableId(R.drawable.menu_road_get);
			enity.setToIntent(new Intent(this, CollectRoadActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("新闻查询");
			enity.setDrawableId(R.drawable.menu_news_search);
			enity.setToIntent(new Intent(this, SearchActivity.class).putExtra("type", "新闻"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("案件查询");
			enity.setDrawableId(R.drawable.menu_case_search);
			enity.setToIntent(new Intent(this, SearchActivity.class).putExtra("type", "案件"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("人员查询");
			enity.setDrawableId(R.drawable.menu_people_search);
			enity.setToIntent(new Intent(this, SearchActivity.class).putExtra("type", "人员"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			// enity = new MenusEnity();
			// enity.setMenuTitle("车辆查询");
			// enity.setDrawableId(R.drawable.menu_car_search);
			// enity.setToIntent(new Intent(this,
			// SearchActivity.class).putExtra("type", "车辆"));
			// enity.setIndexInMenuList(index++);
			// Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("失物查询");
			enity.setDrawableId(R.drawable.menu_goods_search);
			enity.setToIntent(new Intent(this, SearchActivity.class).putExtra("type", "失物"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("车辆号牌查询");
			enity.setDrawableId(R.drawable.menu_car_id);
			enity.setToIntent(new Intent(this, WebActivity.class).putExtra("url",
					"http://www.nngaj.gov.cn/SERVICE/WZCX/WZCX.aspx?types=WZCX").putExtra("Title", "车辆号牌查询"));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("治安管理");
			enity.setDrawableId(R.drawable.menu_order);
			enity.setToIntent(new Intent(this, CollectOrderActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("图片采集");
			enity.setDrawableId(R.drawable.menu_car_search);
			enity.setToIntent(new Intent(this, CollectActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("人员信息采集");
			enity.setDrawableId(R.drawable.menu_people_get);
			enity.setToIntent(new Intent(this, CollectPeopleActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("物品信息采集");
			enity.setDrawableId(R.drawable.menu_goods_get);
			enity.setToIntent(new Intent(this, CollectGoodActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("车辆信息采集");
			enity.setDrawableId(R.drawable.menu_car_get);
			enity.setToIntent(new Intent(this, CollectCarActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuList.add(enity);
			// enity = new MenusEnity();
			// enity.setMenuTitle("出租房信息采集");
			// enity.setDrawableId(R.drawable.menu_house);
			// enity.setToIntent(new Intent(this, CollectHouseActivity.class));
			// enity.setIndexInMenuList(index++);
			// Constants.menuList.add(enity);
			// for (int i = index; i < 20; i++) {
			// enity = new MenusEnity();
			// enity.setMenuTitle("其他" + (i - index));
			// enity.setDrawableId(R.drawable.menu_other);
			// enity.setToIntent(new Intent(this, OtherActivity.class));
			// enity.setIndexInMenuList(i);
			// Constants.menuList.add(enity);
			// }
			enity = new MenusEnity();
			enity.setMenuTitle("更多");
			enity.setDrawableId(R.drawable.menu_more);
			enity.setToIntent(new Intent(this, MenuActivity.class));
			enity.setIndexInMenuList(Constants.menuList.size());
			Constants.menuList.add(enity);*/
		}

		// 第一次创建sp时初始菜单
		if (!mSpUtil.isMenuInMain(Constants.menuList.size() - 1)) {
			for (int i = 0; i < 4; i++) {
				mSpUtil.setMenuInMain(i);
			}
			mSpUtil.setMenuInMain(Constants.menuList.size() - 1);
		}
	}

	private void updateMainMenu() {
		mainMenuList.clear();
		// 初始化主页菜单项列表
		for (int i = 0; i < Constants.menuList.size(); i++) {
			if (mSpUtil.isMenuInMain(i)) {
				mainMenuList.add(Constants.menuList.get(i));
			}
		}
		// 补menu的白格
		int blankNum = (3 - mainMenuList.size() % 3) % 3;
		for (int i = 0; i < blankNum; i++) {
			MenusEnity enity = new MenusEnity();
			enity.setMenuTitle("");
			enity.setDrawableId(R.drawable.menu_null);
			enity.setToIntent(null);
			enity.setIndexInMenuList(-1);
			mainMenuList.add(enity);
		}
	}

	private ViewGroup mVgHead = null;
	private View mllHead, mllHeadBack;

	private DrawerLayout mDrawerLayout;
	private View mDrawer;

	private void initDrawer() {
		mDrawer = findViewById(R.id.fl_drawer);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setScrimColor(Color.argb(100, 0, 0, 0));
		
		
		//获取LoginActivity传递来的用户表中的ID
		String userContentId = getIntent().getStringExtra("userContentId");
		
		if (getIntent() != null && getIntent().getBooleanExtra("isLogin", false)) {
			drawerFragment = new DrawerFragment();
			drawerFragment.setLogin(true);
		} else {
			drawerFragment = new DrawerFragment();
			drawerFragment.setLogin(false);
		}
		drawerFragment.setMainActivity(this);
		
		Bundle bundle = new Bundle();
		bundle.putString("userContentId", userContentId);
		//将userContentId传递至Fragment
		drawerFragment.setArguments(bundle);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.fl_drawer, drawerFragment).commit();
	}

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable drawerArrow;
	private boolean drawerArrowColor;

	private void initActionBar() {
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);

		drawerArrow = new DrawerArrowDrawable(this) {
			@Override
			public boolean isLayoutRtl() {
				return false;
			}
		};
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void closeRightDrawer() {
		mDrawerLayout.closeDrawer(mDrawer);
	}

	public void initVar() {
		mSpUtil = AppData.getInstance().getSpUtil();
		initMenuDate();
		updateMainMenu();
	}

	public void initWidget() {
		// 初始化翻转
		mVgHead = (ViewGroup) findViewById(R.id.fl_head);
		mVgHead.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		mllHead = findViewById(R.id.ll_head_img);
		mllHeadBack = findViewById(R.id.ll_head_img_back);

		mGvMenu = (AutoGridView) findViewById(R.id.gv_menu);
		mAdapter = new GridMenuAdapter(MainActivity.this, mainMenuList);
		mGvMenu.setAdapter(mAdapter);

		initDrawer();
		initActionBar();

		// head获得焦点使scroll滑倒顶端
		// mllHead.setFocusable(true);
		// mllHead.setFocusableInTouchMode(true);
		// mllHead.requestFocus();
	}

	public void initListener() {
		findViewById(R.id.ll_main).setOnClickListener(this);
		mGvMenu.setOnItemClickListener(this);
		mGvMenu.setOnItemLongClickListener(this);

		mllHead.setOnClickListener(this);
		// mllHeadBack.setOnClickListener(this);
	}
	
	/*private void shouldUpdate(){
		Log.v("是否可以更新？ ", isVersionLasted+"");
		if(isVersionLasted == true){
			//T.showShort(MainActivity.this, "有新版本发布，请在侧栏点击更新！");
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("版本升级提示")//提示框标题 
			.setMessage("有新版本发布，请在侧栏点击更新！")
			.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			
			}
				).create().show();
			if(drawerFragment != null) {
				drawerFragment.showIsUpdateDialog();
			}
		}
	}*/
	

	private void cancelTag() {
		mGvMenu.setAdapter(mAdapter);
		isHaveTagShow = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_main:
			if (isHaveTagShow) {
				cancelTag();
			}
			break;
		case R.id.ll_head_img:
			// applyRotation(0, 0, 90);
			// startActivity(new Intent(MainActivity.this,
			// CivilMainActivity.class));
			break;
		case R.id.ll_head_img_back:
			applyRotation(-1, 180, 90);
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawer)) {
				mDrawerLayout.closeDrawer(mDrawer);
			} else {
				mDrawerLayout.openDrawer(mDrawer);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == 0) {
			updateMainMenu();
			cancelTag();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Whether the apk is installed
	private static boolean isPresence;

	@Override
	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		if (isHaveTagShow) {
			cancelTag();
		} else {
			if (mainMenuList.get(position).getToIntent() != null) {
				startActivityForResult(mainMenuList.get(position).getToIntent(), 0);
			} else if ("通讯录检查".equals(mainMenuList.get(position).getMenuTitle())) {
				// 检查应用安装
				isPresence = false;
				PackageManager packageManager = view.getContext().getPackageManager();
				List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
				for (int i = 0; i < packageInfoList.size(); i++) {
					PackageInfo pak = (PackageInfo) packageInfoList.get(i);
					if (pak.applicationInfo.packageName.equals("com.isospat.btpbap")) {
						isPresence = true;
						break;
					}
				}
				// 将应用拷贝出来并安装
				if (!isPresence
						&& Util.copyApkFromAssets(view.getContext(), "scan.apk", Environment
								.getExternalStorageDirectory().getAbsolutePath() + "/scan.apk")) {
					android.app.AlertDialog.Builder m = new AlertDialog.Builder(view.getContext())
							.setIcon(R.drawable.ic_launcher).setMessage("需要安装蓝牙通讯录，是否安装？")
							.setIcon(R.drawable.ic_launcher)
							.setPositiveButton("是", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(Intent.ACTION_VIEW);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.setDataAndType(
											Uri.parse("file://"
													+ Environment.getExternalStorageDirectory().getAbsolutePath()
													+ "/scan.apk"), "application/vnd.android.package-archive");
									view.getContext().startActivity(intent);
								}
							});
					m.show();
				} else {
					// 安装完成，打开
					Intent intent = new Intent();
					intent = packageManager.getLaunchIntentForPackage("com.isospat.btpbap");
					intent.setAction("android.intent.category.VIEW");
					view.getContext().startActivity(intent);
				}
			} else if ("MAC地址检查".equals(mainMenuList.get(position).getMenuTitle())) {
				startActivity(new Intent(MainActivity.this, BTDeviceListActivity.class));
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		if (isHaveTagShow) {
			cancelTag();
		} else {
			if (mainMenuList.get(position).getToIntent() != null
					&& mainMenuList.get(position).getIndexInMenuList() != (Constants.menuList.size() - 1)) {
				view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake)); // 给组件播放抖动动画
				mTagSub = view.findViewById(R.id.iv_tag_sub);
				mTagSub.setVisibility(View.VISIBLE);
				isHaveTagShow = true;
				mTagSub.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mTagSub.setVisibility(View.GONE);
						mSpUtil.setMenuOutMain(mainMenuList.get(position).getIndexInMenuList());
						updateMainMenu();
						cancelTag();
					}
				});
			}
		}
		return true;
	}

	// 点击两次返回键程序退出
	private long mExitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDrawerLayout.isDrawerOpen(mDrawer)) {
				mDrawerLayout.closeDrawer(mDrawer);
			} else {
				if (isHaveTagShow) {
					cancelTag();
				} else {
					if ((System.currentTimeMillis() - mExitTime) > 2000) {
						T.showShort(this, "再按一次退出程序");
						mExitTime = System.currentTimeMillis();
					} else {
						finish();
						onBackPressed();
					}
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 要显示解锁界面
		LockApplication.notShowLock = false;
		LockApplication.mainCount++;
		// 是否已经保存了当前登陆账号的九宫格密码，有者
		if (isEmpty(mSpUtil.getString(mSpUtil.getString("Gesture",null) + "_Lockkey", ""))) {
			Intent intent = new Intent(MainActivity.this, GestureLockActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		} else {
			// 第一次进入主界面
			if (LockApplication.mainCount == 1) {
				// 判断是否从登陆界面进入主界面，是则不显示九宫格解锁界面，否则显示九宫格解锁界面
				if (!mSpUtil.getBoolean("Flogin", false)) {
					Intent intent = new Intent(MainActivity.this, GestureLockActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		}
		mSpUtil.putBoolean("Flogin", false);
	}

	public void back() {
		stopService();
	}

	@Override
	public void onBackPressed() {
		back();
		LockApplication.getInstance().exit();
	}
	
	public boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

	/**
	 * 设置一个新的三维旋转容器视图。
	 */
	private void applyRotation(int position, float start, float end) {
		// Find the center of the container
		final float centerX = mVgHead.getWidth() / 2.0f;
		final float centerY = mVgHead.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final AbRotate3dAnimation rotation = new AbRotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(position));

		mVgHead.startAnimation(rotation);
	}

	/**
	 * This class listens for the end of the first half of the animation. It
	 * then posts a new action that effectively swaps the views when the
	 * container is rotated 90 degrees and thus invisible.
	 */
	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextView(int position) {
			mPosition = position;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mVgHead.post(new SwapViews(mPosition));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * This class is responsible for swapping the views and start the second
	 * half of the animation.
	 */
	private final class SwapViews implements Runnable {
		private final int mPosition;

		public SwapViews(int position) {
			mPosition = position;
		}

		public void run() {
			final float centerX = mVgHead.getWidth() / 2.0f;
			final float centerY = mVgHead.getHeight() / 2.0f;
			AbRotate3dAnimation rotation;

			if (mPosition > -1) {
				mllHead.setVisibility(View.GONE);
				mllHeadBack.setVisibility(View.VISIBLE);

				rotation = new AbRotate3dAnimation(90, 180, centerX, centerY, 310.0f, false);
			} else {
				mllHeadBack.setVisibility(View.GONE);
				mllHead.setVisibility(View.VISIBLE);
				rotation = new AbRotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
			}

			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());

			mVgHead.startAnimation(rotation);
		}
	}
	
	private void startService(){
		Intent intent = new Intent();
		intent.setAction("ys.oa.service.query");
		intent.setPackage("ys.oa.service");
		this.startService(intent);
	}

	private void stopService(){
		Intent intent = new Intent();
		intent.setAction("ys.oa.service.query");
		intent.setPackage("ys.oa.service");
		this.stopService(intent);
	}

}
