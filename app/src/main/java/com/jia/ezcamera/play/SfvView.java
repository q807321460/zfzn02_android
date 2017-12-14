package com.jia.ezcamera.play;





import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.znjj2.R;

@SuppressLint("NewApi")
public class SfvView extends LinearLayout {


	private RelativeLayout rl;
	private GLSurfaceView sfv;
	private ProgressBar pb;
	private Button btv;
	private TextView fishOSDText;
	private LinearLayout ll;

	public RelativeLayout getRl() {
		
		return rl;
	}

	public GLSurfaceView getSfv() {
		return sfv;
	}

	public void setSfv(GLSurfaceView sfv) {
		this.sfv = sfv;
	}

	public void llRemoveSfv(GLSurfaceView glsfv) {
		ll.removeView(glsfv);
	}

	public void llAddSfv(GLSurfaceView glsfv) {
		LayoutParams sfv0_lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll.addView(glsfv, sfv0_lp);
	}

	public ProgressBar getPb() {
		return pb;
	}

	public RelativeLayout getmLinearLayout() {
		return rl;
	}

	public SfvView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initLinnerlayout(context);
	}

	public SfvView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initLinnerlayout(context);
	}

	public SfvView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initLinnerlayout(context);
	}

	public Button getBtn() {
		return btv;
	}

	public TextView getFishOSD(){
		return fishOSDText;
	}

	private void initLinnerlayout(Context context) {

		// 000000000
		rl = new RelativeLayout(context);
		RelativeLayout.LayoutParams rl0_lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.setPadding(1, 1, 1, 1);
		rl.setBackgroundResource(R.color.white);
		rl0_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		// int rl0_id = 2110;
		// rl.setId(rl0_id);

		ll = new LinearLayout(context);
		RelativeLayout.LayoutParams ll_lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		ll.setBackgroundColor(Color.BLACK);
		ll.setGravity(Gravity.CENTER);

		sfv = new GLSurfaceView(context);
		// int sfv0_id = 2000;
		// sfv.setId(sfv0_id);
		LayoutParams sfv0_lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// sfv0_lp.addRule(RelativeLayout.CENTER_IN_PARENT);//
		ll.addView(sfv, sfv0_lp);
		rl.addView(ll, ll_lp);

		btv = new Button(context);
		RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(64,
				64);
		btv.setBackgroundResource(R.drawable.png_again);
		btv.setFocusable(false);
		btv.setVisibility(View.GONE);
		tv_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rl.addView(btv, tv_lp);

		fishOSDText = new TextView(context);
		RelativeLayout.LayoutParams fishOSDText_lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		fishOSDText.setPadding(10,0,10,0);
		fishOSDText.setBackgroundResource(R.color.transparent_background);
		fishOSDText.setTextSize(14.5f);
		fishOSDText.setTextColor(Color.WHITE);
		fishOSDText.setVisibility(View.GONE);

		rl.addView(fishOSDText, fishOSDText_lp);


		pb = new ProgressBar(context);
		// int pb0_id = 2010;
		// pb.setId(pb0_id);
		pb.setVisibility(View.GONE);
		RelativeLayout.LayoutParams pb0_lp = new RelativeLayout.LayoutParams(
				64, 64);
		pb0_lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		rl.addView(pb, pb0_lp);

		// mLinearLayout.addView(rl, rl0_lp);
		this.addView(rl, rl0_lp);
	}

}
