package com.jia.ezcamera.set;



import java.util.Timer;
import java.util.TimerTask;

import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnDevConnectCallbackListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.znjj2.R;


public class CameraSetActivity extends Activity {
	
	private ImageButton btn_back;
	private RelativeLayout camDevinfolayout;
	private RelativeLayout camSyslayout;
	private RelativeLayout camNetlayout;
	private RelativeLayout camRecordlayout;
	private RelativeLayout camSecuritylayout;
	private RelativeLayout camPiclayout;
	private Context mContext = this;
	private MainApplication app= null;
	private String devid;
	private GetDevInfoBean devInfo;
	private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_cam_set);
		devid = getIntent().getStringExtra("devid");
		app = (MainApplication) getApplication();
		devInfo = app.getDevInfo(devid);
		init();
		createConnector(false);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseConnector();
	}
	
	private void init(){
		btn_back = (ImageButton)findViewById(R.id.btn_return);
		camDevinfolayout = (RelativeLayout)findViewById(R.id.cam_devinfolayout);
		camSyslayout = (RelativeLayout)findViewById(R.id.cam_syslayout);
		camNetlayout = (RelativeLayout)findViewById(R.id.cam_netlayout);
		camRecordlayout = (RelativeLayout)findViewById(R.id.cam_reclayout);
		camSecuritylayout = (RelativeLayout)findViewById(R.id.cam_seclayout);
		camPiclayout = (RelativeLayout)findViewById(R.id.cam_pic);
		btn_back.setOnClickListener(onClickListener);
		camDevinfolayout.setOnClickListener(onClickListener);
		camSyslayout.setOnClickListener(onClickListener);
		camNetlayout.setOnClickListener(onClickListener);
		camRecordlayout.setOnClickListener(onClickListener);
		camSecuritylayout.setOnClickListener(onClickListener);
		camPiclayout.setOnClickListener(onClickListener);
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_return:
				finish();
				break;
			case R.id.cam_devinfolayout:
				intent.setClass(mContext, CameraSetInfoActivity.class);
				intent.putExtra("devid", devid);
				mContext.startActivity(intent);
				break;
			case R.id.cam_syslayout:
				if(app.checkSetCamConnect()){
					intent.setClass(mContext, CameraSystemActivity.class);
					intent.putExtra("devid", devid);
					mContext.startActivity(intent);
				}
				break;
			case R.id.cam_netlayout:
				if(app.checkSetCamConnect()){
//					intent.setClass(mContext, CameraNetSetActivity.class);
//					intent.putExtra("devid", devid);
//					mContext.startActivity(intent);
					
					intent.setClass(mContext, CameraNetOfWifiActivity.class);
                	mContext.startActivity(intent);
				}
				break;
			case R.id.cam_reclayout:
				if(app.checkSetCamConnect()){
					intent.setClass(mContext, CameraRecordActivity.class);
					intent.putExtra("devid", devid);
					mContext.startActivity(intent);
				}
				break;
			case R.id.cam_seclayout:
				if(app.checkSetCamConnect()){
					intent.setClass(mContext, CameraAfsActivity.class);
					intent.putExtra("devid", devid);
					mContext.startActivity(intent);
				}
				break;
			case R.id.cam_pic:
				intent.setClass(mContext, FileListActivity.class);
				mContext.startActivity(intent);
				break;
			default:
				break;
			}
			
		}
	};
	
	
	
	private void createConnector(boolean isdialog){
    	onvif_c2s.setOndevConnectCallback(onMessageCallback);
        if (devInfo != null&&app.SetCamConnector==0) {
        	if(isdialog){
	        	ProgressDialogUtil.getInstance().showDialog(mContext,
	                    mContext.getResources().getString(
	                            R.string.cam_connecting));
        	}
        	
        	app.SetCamConnector = onvif_c2s.createConnect(devInfo.dev_id, "admin",
        			devInfo.devpass);
        	Log.e("DEBUG", "app.SetCamConnector  "+app.SetCamConnector);
        }else{
            releaseConnector();
        }
    }
    
    private void releaseConnector(){
    	
        if (app.SetCamConnector != 0) {
            onvif_c2s.releaseConnect(app.SetCamConnector);
            app.SetCamConnector=0;
        }
        onvif_c2s.removeOndevConnectCallback(onMessageCallback);
    }
    
    
    Timer reConnectTimer = null;
    OnDevConnectCallbackListener onMessageCallback = new OnDevConnectCallbackListener() {

        @Override
        public void on_connect_callback(final int msgid, final long connector, final int result) {
            // TODO Auto-generated method stub
            Log.i("info", "SetCameraActivity    onmessagecallback   result="
                    + result + "     msgid=" + msgid + "    connect="
                    + connector+"     "+app.SetCamConnector);
            runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProgressDialogUtil.getInstance().cancleDialog();
		            if (app.SetCamConnector == connector) {
		            	if (result == 1 && msgid == 256) {
		            		
		            	}else {
							// ����ʧ��
		            		//ToastUtils.show(SetCameraActivity.this, getString(R.string.cam_connecting_faild));
							releaseConnector();
							if(reConnectTimer!=null){
								reConnectTimer.cancel();
							}
							reConnectTimer = new Timer();
							reConnectTimer.schedule(new TimerTask() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									createConnector(false);
									reConnectTimer.cancel();
									reConnectTimer = null;
								}
							}, 2000);
							
		            	}
		            }
				}
			});
            
        }
    };
}
