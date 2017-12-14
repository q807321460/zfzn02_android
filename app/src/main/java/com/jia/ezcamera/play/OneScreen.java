package com.jia.ezcamera.play;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

public class OneScreen extends LinearLayout {
	private LinearLayout mLinearLayout;
	private SfvView sfv;

	public OneScreen(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initLinnerlayout(context);
	}

	@SuppressLint("NewApi")
	public OneScreen(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initLinnerlayout(context);
		// TODO Auto-generated constructor stub
	}

	public OneScreen(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initLinnerlayout(context);
	}

	public void llAddView(View v) {
		// LinnerlayoutView.LayoutParams my_lp = new LinearLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// my_lp.gravity = Gravity.CENTER_VERTICAL;
		sfv = (SfvView) v;
		mLinearLayout.addView(sfv);
	}

	public void llRemoveView() {
		mLinearLayout.removeView(sfv);
	}

	private void initLinnerlayout(Context context) {
		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setGravity(Gravity.CENTER);
		LinnerlayoutView.LayoutParams my_lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		my_lp.gravity = Gravity.CENTER;

		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		this.addView(mLinearLayout, my_lp);
	}

}
