package com.jia.ezcamera.play;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class LinnerlayoutView extends LinearLayout {
	private LinearLayout mLinearLayout;
	private LinearLayout ll_up;
	private SfvView rl0;

	private SfvView rl1;

	// -----------------
	private LinearLayout ll_down;
	private SfvView rl2;

	private SfvView rl3;

	public View getRl(int j) {
		int i = j % 4;
		if (i == 0) {
			return rl0;
		} else if (i == 1) {
			return rl1;
		} else if (i == 2) {
			return rl2;
		} else if (i == 3) {
			return rl3;
		}
		return null;
	}

	public LinnerlayoutView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initLinnerlayout(context);
	}

	@SuppressLint("NewApi")
	public LinnerlayoutView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initLinnerlayout(context);
		// TODO Auto-generated constructor stub
	}

	public LinnerlayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initLinnerlayout(context);
	}

	public void addRl(int j, View v) {
		RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int i = j % 4;
		if (i == 0) {
			rl0 = (SfvView) v;
			ll_up.addView(rl0, rl_lp);
		} else if (i == 1) {
			rl1 = (SfvView) v;
			ll_up.addView(rl1, rl_lp);
		} else if (i == 2) {
			rl2 = (SfvView) v;
			ll_down.addView(rl2, rl_lp);
		} else if (i == 3) {
			rl3 = (SfvView) v;
			ll_down.addView(rl3, rl_lp);
		}
	}

	public void delRl() {
		// ll_up.removeView(v);
		System.out.println("-------ll------0");
		ll_up.removeView(rl0);
		System.out.println("-------ll------1");
		ll_up.removeView(rl1);
		System.out.println("-------ll------2");
		ll_down.removeView(rl2);
		System.out.println("-------ll------3");
		ll_down.removeView(rl3);
		System.out.println("-------ll------end");
		// ll_up.removeAllViews();
		// ll_down.removeAllViews();
	}

	private void initLinnerlayout(Context context) {
		// 创建LinearLayout对象
		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setGravity(Gravity.CENTER);
		// 建立布局样式宽和高，对应xml布局中：
		// android:layout_width="fill_parent"
		// android:layout_height="fill_parent"
		LayoutParams my_lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		my_lp.gravity = Gravity.CENTER;
		// mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// android:orientation="vertical"
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);


		ll_up = new LinearLayout(context);
		ll_up.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll_up.setOrientation(LinearLayout.HORIZONTAL);
		ll_up.setGravity(Gravity.CENTER);
		ll_up.setBackgroundColor(Color.BLACK);


		ll_down = new LinearLayout(context);
		ll_down.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll_down.setOrientation(LinearLayout.HORIZONTAL);
		ll_down.setGravity(Gravity.CENTER);
		ll_down.setBackgroundColor(Color.BLACK);

		mLinearLayout.addView(ll_up);
		mLinearLayout.addView(ll_down);
		this.addView(mLinearLayout, my_lp);
	}

	public void viewToGone() {
		rl1.setVisibility(View.GONE);
		rl2.setVisibility(View.GONE);
		rl3.setVisibility(View.GONE);
	}

}
