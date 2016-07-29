package ys.oa.civil.activity;

import java.util.ArrayList;

import ys.oa.activity.NewsActivity;
import ys.oa.activity.SearchActivity;
import ys.oa.adapter.GridMenuAdapter;
import ys.oa.civil.fragment.CivilDrawerFragment;
import ys.oa.enity.MenusEnity;
import ys.oa.enity.SectionEnity;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.Util;
import ys.oa.widget.AbRotate3dAnimation;
import ys.oa.widget.AutoGridView;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 民用版：主页面
 * 
 */
public class CivilMainActivity extends FragmentActivity implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener {

	private AutoGridView mGvMenu;
	private SpUtil mSpUtil;
	private ArrayList<MenusEnity> mainMenuList = new ArrayList<MenusEnity>();;
	private GridMenuAdapter mAdapter;
	private View mTagSub;
	private boolean isHaveTagShow = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_civil);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
	}

	private void initMenuDate() {
		if (Constants.menuCivilList.size() == 0) {
			int index = 0;
			ArrayList<SectionEnity> listSection;
			// 初始化所有菜单项列表
			listSection = new ArrayList<SectionEnity>();
			listSection.add(new SectionEnity("户政业务办理指南"));
			listSection.add(new SectionEnity("身份证事务", "ywzn/hzywzn/sfzsw.html"));
			listSection.add(new SectionEnity("户籍事务", "/ywzn/hzywzn/hjsw.html"));
			listSection.add(new SectionEnity("流动人口事务", "ywzn/hzywzn/ldrksw.html"));
			listSection.add(new SectionEnity("出入境业务办理指南"));
			listSection.add(new SectionEnity("赴港澳、台湾通行证办理", "ywzn/crjywzn/fgattxzbl.html"));
			listSection.add(new SectionEnity("出国（普通护照办理）", "ywzn/crjywzn/cg.html"));
			listSection.add(new SectionEnity("台胞及通行证办理", "ywzn/crjywzn/tbjtxzbl.html"));
			listSection.add(new SectionEnity("外国人签证", "ywzn/crjywzn/wgrqz.html"));
			listSection.add(new SectionEnity("其他", "ywzn/crjywzn/other.html"));
			listSection.add(new SectionEnity("交管业务办理指南"));
			listSection.add(new SectionEnity("机动车事务", "ywzn/jgywzn/jdcsw.html"));
			listSection.add(new SectionEnity("驾驶员事务", "ywzn/jgywzn/jsysw.html"));
			listSection.add(new SectionEnity("道路运输及铁路秩序事务", "ywzn/jgywzn/dlysyjtl.html"));
			MenusEnity enity;
			enity = new MenusEnity();
			enity.setMenuTitle("办事大厅");
			enity.setDrawableId(R.drawable.menu_internet_law);
			enity.setToIntent(new Intent(this, PinnedSectionListActivity.class).putExtra("list", listSection).putExtra(
					"Title", "办事大厅"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);

			listSection = new ArrayList<SectionEnity>();
			listSection.add(new SectionEnity("出入境证件办理进度查询", "ywzn/crjyw/crjyw2.html", R.drawable.ic_crj));
			listSection.add(new SectionEnity("机动车违章查询", "ywzn/jgyw/jdcwf.html", R.drawable.ic_jdc));
			listSection.add(new SectionEnity("驾驶员违章查询", "ywzn/jgyw/jsywf.html", R.drawable.ic_jsy));
			listSection.add(new SectionEnity("本市同名查询", "ywzn/hzyw/tmtxcx3.html", R.drawable.ic_bstm));
			listSection.add(new SectionEnity("阳光执法", "/ywzn/ygzf/ygzf.html", R.drawable.ic_ygzf));
			enity = new MenusEnity();
			enity.setMenuTitle("便民查询");
			enity.setDrawableId(R.drawable.menu_complaint_search);
			enity.setToIntent(new Intent(this, PinnedSectionListActivity.class).putExtra("list", listSection).putExtra(
					"Title", "便民查询"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);

			listSection = new ArrayList<SectionEnity>();
			listSection.add(new SectionEnity("警方通缉", "jfzxShow.htm?forum=JFTJ", R.drawable.ic_jmtj));
			listSection.add(new SectionEnity("线索征集", "jfzxShow.htm?forum=XSZJ", R.drawable.ic_xszx));
			listSection.add(new SectionEnity("网上寻人", "jfzxShow.htm?forum=WSXR", R.drawable.ic_wsxr));
			listSection.add(new SectionEnity("无名尸体", "jfzxShow.htm?forum=WMST", R.drawable.ic_wmst));
			enity = new MenusEnity();
			enity.setMenuTitle("警民互助");
			enity.setDrawableId(R.drawable.menu_police_office);
			enity.setToIntent(new Intent(this, PinnedSectionListActivity.class).putExtra("list", listSection).putExtra(
					"Title", "警民互助"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);

			listSection = new ArrayList<SectionEnity>();
			listSection.add(new SectionEnity("安全防范", "jftgShow.htm?forum=GAY_JFTS", R.drawable.ic_aqff));
			listSection.add(new SectionEnity("警务报道", "jftgShow.htm?forum=GAY_ZABB", R.drawable.ic_jwbd));
			listSection.add(new SectionEnity("媒体报道", "jftgShow.htm?forum=GAY_MTBD", R.drawable.ic_mtbd));
			enity = new MenusEnity();
			enity.setMenuTitle("警务资讯");
			enity.setDrawableId(R.drawable.menu_case_info);
			enity.setToIntent(new Intent(this, PinnedSectionListActivity.class).putExtra("list", listSection).putExtra(
					"Title", "警务资讯"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);

			listSection = new ArrayList<SectionEnity>();
			listSection.add(new SectionEnity("诉求提交", "ywzn/suqiu/sqbl.jsp", R.drawable.ic_sqts));
			listSection.add(new SectionEnity("诉求结果查询", "/ywzn/suqiu/suqiuSearch.jsp", R.drawable.ic_sqjg));
			enity = new MenusEnity();
			enity.setMenuTitle("咨询投诉");
			enity.setDrawableId(R.drawable.menu_complaint);
			enity.setToIntent(new Intent(this, PinnedSectionListActivity.class).putExtra("list", listSection).putExtra(
					"Title", "咨询投诉"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			// web项
			// addWebMenuItem("身份证事务", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/hzywzn/sfzsw.html", index++);
			// addWebMenuItem("户籍事务", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/hzywzn/hjsw.html", index++);
			// addWebMenuItem("流动人口事务", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/hzywzn/ldrksw.html",
			// index++);
			// addWebMenuItem("赴港澳、台湾通行证办理", R.drawable.menu_internet_law,
			// Constants.WEB_IP
			// + "ywzn/crjywzn/fgattxzbl.html", index++);
			// addWebMenuItem("出国（普通护照办理）", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/crjywzn/cg.html",
			// index++);
			// addWebMenuItem("台胞及通行证办理", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/crjywzn/tbjtxzbl.html",
			// index++);
			// addWebMenuItem("外国人签证", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/crjywzn/wgrqz.html", index++);
			// addWebMenuItem("其他出入境业务", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/crjywzn/other.html",
			// index++);
			// addWebMenuItem("机动车事务", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/jgywzn/jdcsw.html", index++);
			// addWebMenuItem("驾驶员事务", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/jgywzn/jsysw.html", index++);
			// addWebMenuItem("道路运输及铁路秩序事务", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/jgywzn/dlysyjtl.html",
			// index++);
			// addWebMenuItem("出入境证件办理进度查询", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/crjyw/crjyw2.html",
			// index++);
			// addWebMenuItem("机动车违章查询", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/jgyw/jdcwf.html", index++);
			// addWebMenuItem("驾驶员违章查询", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/jgyw/jsywf.html", index++);
			// addWebMenuItem("本市同名查询", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/hzyw/tmtxcx3.html", index++);
			// addWebMenuItem("阳光执法", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/ygzf/ygzf.html", index++);
			// addWebMenuItem("警方通缉", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "jfzxShow.htm?forum=JFTJ", index++);
			// addWebMenuItem("线索征集", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "jfzxShow.htm?forum=XSZJ", index++);
			// addWebMenuItem("网上寻人", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "jfzxShow.htm?forum=WSXR", index++);
			// addWebMenuItem("无名尸体", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "jfzxShow.htm?forum=WMST", index++);
			// addWebMenuItem("安全防范", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "jftgShow.htm?forum=GAY_JFTS",
			// index++);
			// addWebMenuItem("警务报道", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "jftgShow.htm?forum=GAY_ZABB",
			// index++);
			// addWebMenuItem("媒体报道", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "jftgShow.htm?forum=GAY_MTBD",
			// index++);
			addWebMenuItem("警务知识", R.drawable.menu_news_police, Constants.WEB_IP + "ywzn/jwzx/wenda.jsp", index++);
			// addWebMenuItem("诉求提交", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/suqiu/sqbl.jsp", index++);
			// addWebMenuItem("诉求结果查询", R.drawable.menu_internet_law,
			// Constants.WEB_IP + "ywzn/suqiu/suqiuSearch.jsp",
			// index++);
			// 其他项
			enity = new MenusEnity();
			enity.setMenuTitle("警务报道");
			enity.setDrawableId(R.drawable.menu_news_police);
			enity.setToIntent(new Intent(this, NewsActivity.class).putExtra("title", "警务报道"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("案情报道");
			enity.setDrawableId(R.drawable.menu_case_info);
			enity.setToIntent(new Intent(this, NewsActivity.class).putExtra("title", "案情报道"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("安全防范");
			enity.setDrawableId(R.drawable.menu_news_safe);
			enity.setToIntent(new Intent(this, NewsActivity.class).putExtra("title", "安全防范"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("线索征询");
			enity.setDrawableId(R.drawable.menu_news_clue);
			enity.setToIntent(new Intent(this, NewsActivity.class).putExtra("title", "线索征询"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("投诉举报");
			enity.setDrawableId(R.drawable.menu_complaint);
			enity.setToIntent(new Intent(this, ComplaintActivity.class));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("诉求查询");
			enity.setDrawableId(R.drawable.menu_complaint_search);
			enity.setToIntent(new Intent(this, SearchActivity.class).putExtra("type", "诉求"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			enity = new MenusEnity();
			enity.setMenuTitle("失物招领");
			enity.setDrawableId(R.drawable.menu_lost_civil);
			enity.setToIntent(new Intent(this, SearchActivity.class).putExtra("type", "失物"));
			enity.setIndexInMenuList(index++);
			Constants.menuCivilList.add(enity);
			addWebMenuItem("警务室", R.drawable.menu_police_office, "http://www.nngaj.gov.cn/MapList.aspx", index++);
			addWebMenuItem("网上办事", R.drawable.menu_internet_law, "http://www.nngaj.gov.cn/SERVICE/list.aspx", index++);
			// for (int i = index; i < 20; i++) {
			// enity = new MenusEnity();
			// enity.setMenuTitle("其他" + (i - index));
			// enity.setDrawableId(R.drawable.menu_other);
			// enity.setToIntent(new Intent(this, OtherActivity.class));
			// enity.setIndexInMenuList(i);
			// Constants.menuCivilList.add(enity);
			// }
			enity = new MenusEnity();
			enity.setMenuTitle("更多");
			enity.setDrawableId(R.drawable.menu_more);
			enity.setToIntent(new Intent(this, CivilMenuActivity.class));
			enity.setIndexInMenuList(Constants.menuCivilList.size());
			Constants.menuCivilList.add(enity);
		}

		// 第一次创建sp时初始菜单
		if (!mSpUtil.isMenuInMainCivil(Constants.menuCivilList.size() - 1)) {
			for (int i = 0; i < 6; i++) {
				mSpUtil.setMenuInMainCivil(i);
			}
			mSpUtil.setMenuInMainCivil(Constants.menuCivilList.size() - 1);
		}
	}

	private void addWebMenuItem(String title, int iconDrawableId, String url, int menuListIndex) {
		MenusEnity enityWeb = new MenusEnity();
		enityWeb.setMenuTitle(title);
		enityWeb.setDrawableId(iconDrawableId);
		enityWeb.setToIntent(new Intent(this, WebActivity.class).putExtra("url", url).putExtra("Title", title));
		enityWeb.setIndexInMenuList(menuListIndex);
		Constants.menuCivilList.add(enityWeb);
	}

	private void updateMainMenu() {
		mainMenuList.clear();
		// 初始化主页菜单项列表
		for (int i = 0; i < Constants.menuCivilList.size(); i++) {
			if (mSpUtil.isMenuInMainCivil(i)) {
				mainMenuList.add(Constants.menuCivilList.get(i));
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
		CivilDrawerFragment civilDrawerFragment = new CivilDrawerFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.fl_drawer, civilDrawerFragment).commit();
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
		mAdapter = new GridMenuAdapter(CivilMainActivity.this, mainMenuList);
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

		// mllHead.setOnClickListener(this);
		// mllHeadBack.setOnClickListener(this);
	}

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
			applyRotation(0, 0, 90);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (isHaveTagShow) {
			cancelTag();
		} else {
			if (mainMenuList.get(position).getToIntent() != null) {
				startActivityForResult(mainMenuList.get(position).getToIntent(), 0);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		if (isHaveTagShow) {
			cancelTag();
		} else {
			if (mainMenuList.get(position).getToIntent() != null
					&& mainMenuList.get(position).getIndexInMenuList() != (Constants.menuCivilList.size() - 1)) {
				view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake)); // 给组件播放抖动动画
				mTagSub = view.findViewById(R.id.iv_tag_sub);
				mTagSub.setVisibility(View.VISIBLE);
				isHaveTagShow = true;
				mTagSub.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mTagSub.setVisibility(View.GONE);
						mSpUtil.setMenuOutMainCivil(mainMenuList.get(position).getIndexInMenuList());
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
					// if ((System.currentTimeMillis() - mExitTime) > 2000) {
					// T.showShort(this, "再按一次退出程序");
					// mExitTime = System.currentTimeMillis();
					// } else {
					// finish();
					// }
					onBackPressed();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

}
