package com.jia.ezcamera.utils;




import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.znjj2.R;


public class BottomDialog extends Dialog{
	private TextView titleText;
	private TextView sureText;
	private TextView cancelText;
	private ImageView hintImage;
	private RelativeLayout etLayout;
	private EditText et;
	private CharSequence titleString,sureString,cancelString,etHintString="";
	private View.OnClickListener sureListener,calcelListener;
	private boolean isBottom = true;
	private int cancelVisibility = View.VISIBLE;
	private int etLayoutVisibility = View.GONE;
	public BottomDialog(Context context, int theme) {
	    super(context, R.style.BottomDialog);
	}

	public BottomDialog(Context context,String titleString,String sureString,String cancelString,
			View.OnClickListener sureListener,View.OnClickListener calcelListener) {
		super(context, R.style.BottomDialog);
	    this.titleString = titleString;
	    this.sureString  = sureString;
	    this.cancelString  = cancelString;
	    this.sureListener = sureListener;
	    this.calcelListener = calcelListener;
	}
	
	public BottomDialog(Context context,CharSequence titleString,CharSequence sureString,CharSequence cancelString,
			View.OnClickListener sureListener,View.OnClickListener calcelListener,boolean isBottom) {
		super(context, R.style.BottomDialog);
	    this.titleString = titleString;
	    this.sureString  = sureString;
	    this.cancelString  = cancelString;
	    this.sureListener = sureListener;
	    this.calcelListener = calcelListener;
	    this.isBottom = isBottom;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dlg_bottom);
	    titleText = (TextView)findViewById(R.id.title_text);
		hintImage = (ImageView)findViewById(R.id.dialog_image_hint);
	    sureText = (TextView)findViewById(R.id.sure_text);
	    cancelText = (TextView)findViewById(R.id.cancel_text);
	    etLayout = (RelativeLayout)findViewById(R.id.dialog_et_layout);
	    et = (EditText)findViewById(R.id.dialog_et);
	    titleText.setText(titleString);
	    sureText.setText(sureString);
	    cancelText.setText(cancelString);
	    et.setHint(etHintString);
	    sureText.setOnClickListener(sureListener);
	    cancelText.setOnClickListener(calcelListener);
	    
	    cancelText.setVisibility(cancelVisibility);
	    etLayout.setVisibility(etLayoutVisibility);
	    Window window = getWindow();  
	    if(isBottom)
	    	window.setGravity(Gravity.BOTTOM);
	    else
	    	window.setGravity(Gravity.TOP);
	    LayoutParams params = window.getAttributes();
	    params.width = LayoutParams.MATCH_PARENT;
	    window.setAttributes(params);
	    
	    
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.e("DEBUG", "DIALOG onStop");
		super.onStop();
	}



	public void setTitleText(String title){
		titleString = title;
		if(titleText!=null)
			titleText.setText(title);
	}
	public void setEditText(String hint){
		etLayoutVisibility = View.VISIBLE;
		etHintString = hint;
	}

	public void setHintImage(int imageId){
		hintImage.setVisibility(View.VISIBLE);
		hintImage.setImageResource(imageId);
	}

	public void removeHintImage(){
		hintImage.setVisibility(View.GONE);
	}
	
	public String getEditTextString(){
		return et.getText().toString().trim();
	}
	
	public void setCancelVisibility(int Visibility){
		cancelVisibility = Visibility;
	}
}
