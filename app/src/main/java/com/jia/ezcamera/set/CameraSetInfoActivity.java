package com.jia.ezcamera.set;



import vv.ppview.PpviewClientInterface;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

public class CameraSetInfoActivity extends Activity{
	private EditText et_cam_camname;
	private EditText et_cam_did;
	private EditText et_cam_pwd;

	private EditText et_cam_model;
	private EditText et_cam_ver;
	private Button button_save;
	
	private Activity mContext = this;
	
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
	private String devid;
	private GetDevInfoBean devInfo;
	private int chl_id = 0;
	private MainApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_cam_info);
		app = (MainApplication)getApplication();
        devid = getIntent().getStringExtra("devid");
		devInfo = app.getDevInfo(devid);
		initView();
	}
	
	
	private void initView(){
		et_cam_camname = (EditText)findViewById(R.id.et_cam_camname);
		Log.e("DEBUG", "devInfo.local_name "+devInfo.local_name);
		et_cam_camname.setText(devInfo.local_name);
		et_cam_did = (EditText)findViewById(R.id.et_cam_did);
		et_cam_did.setText(devInfo.dev_id);
		et_cam_pwd = (EditText)findViewById(R.id.et_cam_pwd);
		et_cam_pwd.setText(devInfo.devpass);
		et_cam_model = (EditText)findViewById(R.id.et_cam_model);
		et_cam_model.setText(devInfo.model);
		et_cam_ver = (EditText)findViewById(R.id.et_cam_ver);
		et_cam_ver.setText(devInfo.firmware);
		button_save = (Button)findViewById(R.id.button_save);
		
		et_cam_did.setFocusable(false);
		et_cam_model.setFocusable(false);
		et_cam_ver.setFocusable(false);
		
		button_save.setOnClickListener(onClickListener);
		findViewById(R.id.btn_return).setOnClickListener(onClickListener);
	}
	
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_return:
				finish();
				break;
			case R.id.button_save:
				saveInfo();
				
				break;
			default:
				break;
			}
		}
	};
	
	private void saveInfo(){
		String pwd = et_cam_pwd.getText().toString().trim();
		String name  = et_cam_camname.getText().toString().trim();
		
		if(TextUtils.isEmpty(pwd)){
			ToastUtils.show(mContext, getString(R.string.hint_pass_empty));
			return;
		}
		if(TextUtils.isEmpty(name)){
			ToastUtils.show(mContext, getString(R.string.devid_not_null));
			return;
		}
		devInfo.devpass = pwd;
		devInfo.local_name =name;
		app.modfiyVRDevice(devInfo);
		ToastUtils.show(mContext, getString(R.string.save_ok));
		finish();
	}
}
