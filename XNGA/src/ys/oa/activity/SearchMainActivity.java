package ys.oa.activity;

import java.util.ArrayList;

import com.refactech.driibo.AppData;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ys.nlga.activity.R;
import ys.oa.adapter.GridMenuAdapter;
import ys.oa.enity.MenusEnity;
import ys.oa.util.Constants;
import ys.oa.util.SpUtil;
import ys.oa.util.Util;
import ys.oa.widget.AutoGridView;

/**
 * 业务查询主界面，呈现查询类别
 * @author perng
 *
 */
public class SearchMainActivity extends BaseActivity implements OnItemClickListener {
	private AutoGridView mGvMenu;
	private SpUtil mSpUtil;
	private ArrayList<MenusEnity> searchMainList = new ArrayList<MenusEnity>();
	private GridMenuAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_main);
		
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		mSpUtil = AppData.getInstance().getSpUtil();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mGvMenu = (AutoGridView) findViewById(R.id.gv_menu);
		mAdapter = new GridMenuAdapter(SearchMainActivity.this, searchMainList);
		mGvMenu.setAdapter(mAdapter);
		initMenuDate();
		updateMainMenu();
		mGvMenu.setOnItemClickListener(this);
	}
	
	private void initMenuDate() {
		if (Constants.searchlList.size() == 0) {
			int index = 0;
			
			//一键查询OneKey
			MenusEnity okEnity;
			okEnity = new MenusEnity();
			okEnity.setMenuTitle("一键查询");
			okEnity.setDrawableId(R.drawable.menu_news_search);
			okEnity.setIndexInMenuList(index++);
			okEnity.setToIntent(new Intent(this,SearchInfoActivity.class).putExtra("Tag", Constants.ONE_KEY_RESEARCH));
			Constants.searchlList.add(okEnity);
			
			//人员查询Person
			MenusEnity pEnity;
			pEnity = new MenusEnity();
			pEnity.setMenuTitle("人员查询");
			pEnity.setDrawableId(R.drawable.menu_people_search);
			pEnity.setIndexInMenuList(index++);
			pEnity.setToIntent(new Intent(this,SearchInfoActivity.class).putExtra("Tag", Constants.PERSON_RESEARCH));
			Constants.searchlList.add(pEnity);
			
			//车辆查询Vehicle
			MenusEnity vEnity;
			vEnity = new MenusEnity();
			vEnity.setMenuTitle("车辆查询");
			vEnity.setDrawableId(R.drawable.menu_car_id);
			vEnity.setIndexInMenuList(index++);
			vEnity.setToIntent(new Intent(this,SearchInfoActivity.class).putExtra("Tag", Constants.VEHICLE_RESEARCH));
			Constants.searchlList.add(vEnity);
			
			//物品查询Goods
			MenusEnity gEnity;
			gEnity = new MenusEnity();
			gEnity.setMenuTitle("物品查询");
			gEnity.setDrawableId(R.drawable.menu_goods_search);
			gEnity.setIndexInMenuList(index++);
			gEnity.setToIntent(new Intent(this,SearchInfoActivity.class).putExtra("Tag", Constants.GOODS_RESEARCH));
			Constants.searchlList.add(gEnity);
			
			//案件查询Case
			MenusEnity cEnity;
			cEnity = new MenusEnity();
			cEnity.setMenuTitle("案件查询");
			cEnity.setDrawableId(R.drawable.menu_record);
			cEnity.setIndexInMenuList(index++);
			cEnity.setToIntent(new Intent(this,SearchInfoActivity.class).putExtra("Tag", Constants.CASE_RESEARCH));
			Constants.searchlList.add(cEnity);
			
		}
		
		// 第一次创建sp时初始菜单
		if (!mSpUtil.isMenuInMain(Constants.searchlList.size() - 1)) {
			for (int i = 0; i < 4; i++) {
				mSpUtil.setMenuInMain(i);
			}
				mSpUtil.setMenuInMain(Constants.searchlList.size() - 1);
			}
	}
	
	private void updateMainMenu() {
		searchMainList.clear();
		// 初始化主页菜单项列表
		for (int i = 0; i < Constants.searchlList.size(); i++) {
			if (mSpUtil.isMenuInMain(i)) {
				searchMainList.add(Constants.searchlList.get(i));
			}
		}
		// 补menu的白格
		int blankNum = (3 - searchMainList.size() % 3) % 3;
		for (int i = 0; i < blankNum; i++) {
			MenusEnity enity = new MenusEnity();
			enity.setMenuTitle("");
			enity.setDrawableId(R.drawable.menu_null);
			enity.setToIntent(null);
			enity.setIndexInMenuList(-1);
			searchMainList.add(enity);
		}
	}
		
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (searchMainList.get(position).getToIntent() != null) {
			startActivityForResult(searchMainList.get(position).getToIntent(), 0);
		}
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	}

	

}
