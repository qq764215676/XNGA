package ys.oa.civil.activity;

import java.util.ArrayList;

import ys.oa.adapter.GridMenuAdapter;
import ys.oa.enity.MenusEnity;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.refactech.driibo.AppData;
import ys.nlga.activity.R;

/**
 * @author wf
 * @category 民用版：所有菜单项
 * 
 */
public class CivilMenuActivity extends FragmentActivity implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener {

	private GridView mGvMenu;
	private SpUtil mSpUtil;
	private ArrayList<MenusEnity> allMenuList = new ArrayList<MenusEnity>();
	private GridMenuAdapter mAdapter;
	private View mTagPlus;
	private boolean isHaveTagShow = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		Util.initExce(this);
		initVar();
		initWidget();
		initListener();
	}

	private void updateAllMenu() {
		allMenuList.clear();
		// 初始化主页菜单项列表
		for (int i = 0; i < Constants.menuCivilList.size(); i++) {
			if (!mSpUtil.isMenuInMainCivil(i)) {
				allMenuList.add(Constants.menuCivilList.get(i));
			}
		}
		// 补menu的白格
		int blankNum = (3 - allMenuList.size() % 3) % 3;
		for (int i = 0; i < blankNum; i++) {
			MenusEnity enity = new MenusEnity();
			enity.setMenuTitle("");
			enity.setDrawableId(R.drawable.menu_null);
			enity.setToIntent(null);
			enity.setIndexInMenuList(-1);
			allMenuList.add(enity);
		}
	}

	public void initVar() {
		mSpUtil = AppData.getInstance().getSpUtil();
		updateAllMenu();
	}

	public void initWidget() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mGvMenu = (GridView) findViewById(R.id.gv_all_menu);
		mAdapter = new GridMenuAdapter(CivilMenuActivity.this, allMenuList);
		mGvMenu.setAdapter(mAdapter);
	}

	public void initListener() {
		findViewById(R.id.ll_menu).setOnClickListener(this);
		mGvMenu.setOnItemClickListener(this);
		mGvMenu.setOnItemLongClickListener(this);
	}

	private void cancelTag() {
		mGvMenu.setAdapter(mAdapter);
		isHaveTagShow = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_menu:
			if (isHaveTagShow) {
				cancelTag();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (isHaveTagShow) {
			cancelTag();
		} else {
			if (allMenuList.get(position).getToIntent() != null) {
				startActivity(allMenuList.get(position).getToIntent());
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		if (isHaveTagShow) {
			cancelTag();
		} else {
			if (allMenuList.get(position).getToIntent() != null) {
				view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake)); // 给组件播放抖动动画
				mTagPlus = view.findViewById(R.id.iv_tag_plus);
				mTagPlus.setVisibility(View.VISIBLE);
				isHaveTagShow = true;
				mTagPlus.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mTagPlus.setVisibility(View.GONE);
						mSpUtil.setMenuInMainCivil(allMenuList.get(position).getIndexInMenuList());
						updateAllMenu();
						cancelTag();
					}
				});
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(Activity.RESULT_OK);
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isHaveTagShow) {
				cancelTag();
			} else {
				setResult(Activity.RESULT_OK);
				onBackPressed();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
