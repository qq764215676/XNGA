package ys.oa.widget;

import ys.nlga.activity.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * 标题栏
 * 
 */
public class TitleBar extends RelativeLayout
{
	
	public TitleBar(Context context)
	{
		super(context);
		
		initView(context);
	}
	
	public TitleBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		
		initView(context);
	}
	
	public TitleBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		
		initView(context);
	}
	
	private void initView(Context context)
	{
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.setPadding(5, 5, 5, 5);
		
		//标题栏的中央标题
		LinearLayout vg_titleBar_title = new LinearLayout(context);
		RelativeLayout.LayoutParams rllp1 = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		rllp1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		vg_titleBar_title.setLayoutParams(rllp1);
		//vg_titleBar_title.setId(1);
		//标题栏中央标题中的图标
		ImageView iv_titleBar_titleIcon = new ImageView(context);
		iv_titleBar_titleIcon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		//iv_titleBar_titleIcon.setImageResource(R.drawable.btn_login_normal);
		iv_titleBar_titleIcon.setBackgroundResource(R.drawable.group_2);
		//iv_titleBar_titleIcon.setBackgroundColor(Color.RED);
		vg_titleBar_title.addView(iv_titleBar_titleIcon);
		//标题栏中央标题中的文字
		/*TextView tv_titleBar_titleText = new TextView(getContext());
		tv_titleBar_titleText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		//tv_titleBar_titleText.setText("宽:"+iv_titleBar_titleIcon.getWidth()+" 高:"+iv_titleBar_titleIcon.getHeight());
		vg_titleBar_title.addView(tv_titleBar_titleText);*/
		this.addView(vg_titleBar_title);
		
		/*//标题栏的左侧按钮栏
		LinearLayout vg_titleBar_leftBtnBar = new LinearLayout(getContext());
		RelativeLayout.LayoutParams rllp2 = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		rllp2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		rllp2.addRule(RelativeLayout.LEFT_OF, 1);
		vg_titleBar_leftBtnBar.setLayoutParams(rllp2);
		addView(vg_titleBar_leftBtnBar);
		
		//标题栏的左侧按钮栏
		LinearLayout vg_titleBar_rightBtnBar = new LinearLayout(getContext());
		RelativeLayout.LayoutParams rllp3 = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		rllp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		rllp3.addRule(RelativeLayout.RIGHT_OF, 1);
		vg_titleBar_rightBtnBar.setLayoutParams(rllp3);
		addView(vg_titleBar_rightBtnBar);*/
	}
	
}
