package ys.oa.activity;

import com.refactech.driibo.AppData;

import ys.nlga.activity.R;
import ys.oa.fragment.SearchInfoFragment;
import ys.oa.util.Constants;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

/**
 * 业务查询界面，根据点击的查询类型呈现不同的界面
 * 
 * @author perng
 * 
 */
public class SearchInfoActivity extends BaseActivity
{
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	
	private SearchInfoFragment searchInfoFragment; //搜索页面Fragment

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_info);

		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);

		initActionBar();
		GetSearchFragment();

	}

	// 初始化ActionBar
	private void initActionBar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 根据SearchMainActivity中传入的Extra切换不同的Fragment界面
	 */
	private void GetSearchFragment()
	{
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		String tag = (String) getIntent().getSerializableExtra("Tag"); //"Tag"即搜索类别
		
		/** 应项目需求，暂时屏蔽部分页面功能 *//*
		if(!Constants.ONE_KEY_RESEARCH.equals(tag) && !Constants.GOODS_RESEARCH.equals(tag) && !Constants.CASE_RESEARCH.equals(tag))
		{
			//打开搜索页面
			searchInfoFragment = new SearchInfoFragment(tag); //将搜索类别信息参数传入(searchInfoFragment会根据搜索类别，展示对应的搜索页面)
			fragmentTransaction.add(R.id.content_frame, searchInfoFragment).commit();
		}*/
		
		//打开搜索页面
		searchInfoFragment = new SearchInfoFragment(tag); //将搜索类别信息参数传入(searchInfoFragment会根据搜索类别，展示对应的搜索页面)
		fragmentTransaction.add(R.id.content_frame, searchInfoFragment).commit();
	}

	// 点击ActionBar图标操作
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

}
