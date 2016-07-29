package ys.oa.activity;

import java.util.ArrayList;

import ys.nlga.activity.R;
import ys.oa.civil.adapter.EnforcementViewAdapter;
import ys.oa.enity.SectionEnity;
import ys.oa.util.Util;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


/**
 * 执法规范二级页面
 * @author cuiru.fan
 *
 */
public class EnforcementView extends BaseActivity {

	private ListView listView;
	private EnforcementViewAdapter adapter;
	private ArrayList<SectionEnity> itemData;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enforcement);
		context = this;
		Util.initExce(this);
		LockApplication.getInstance().addActivity(this);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getIntent().getStringExtra("Title"));
		itemData = (ArrayList<SectionEnity>) getIntent().getSerializableExtra("list");
		initView();
	}
	
	public void initView() {
		listView = (ListView) findViewById(R.id.enforcement_listview);
		adapter = new EnforcementViewAdapter(this, itemData);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				context.startActivity(new Intent(context, itemData.get(position).getActivityClass())
				.putExtra("Title", itemData.get(position).getTitle()));
			}
			
		});
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		// 显示九宫格解锁
		LockApplication.notShowLock = false;
	}
	
	@Override
	public void onBackPressed() {
		EnforcementView.this.finish();
		//back();
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
}
