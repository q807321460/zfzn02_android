package com.jia.ezcamera.set;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.BottomDialog;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dImageMirrorCallback;
import vv.ppview.PpviewClientInterface.OnC2sSetCamPrivateStatusCallback;


public class CameraSystemActivity extends Activity {

    private ImageButton btn_back;
    private Context mContext = this;
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    private ImageView sleepButton;
    private ImageView imageButton;
    private RelativeLayout rl_modify_pass,rl_reboot;
    private MainApplication app = null;
    private int imageMirrorMode = 0;

	private String devid;
	private GetDevInfoBean devInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        app = (MainApplication)getApplication();
        setContentView(R.layout.activity_set_cam_system);
        devid = getIntent().getStringExtra("devid");
		app = (MainApplication) getApplication();
		devInfo = app.getDevInfo(devid);
        init();
        if(app.checkSetCamConnect()){
        	onvif_c2s.c2d_getImageMirror_fun(app.SetCamConnector, 0);
        }

    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

    private void init() {
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        sleepButton = (ImageView)findViewById(R.id.image_sleepstatus);
        imageButton = (ImageView)findViewById(R.id.image_imagestatus);
        rl_modify_pass = (RelativeLayout)findViewById(R.id.rl_modify_pass);
        rl_reboot = (RelativeLayout)findViewById(R.id.rl_reboot);
        btn_back.setOnClickListener(onClickListener);
        sleepButton.setOnClickListener(onClickListener);
        imageButton.setOnClickListener(onClickListener);
        rl_modify_pass.setOnClickListener(onClickListener);
        rl_reboot.setOnClickListener(onClickListener);
        
//        if(cam_item.privateStatus==1){
//        	sleepButton.setImageResource(R.drawable.switch_on);
//        }
        onvif_c2s.setOnC2dImageMirrorCallback(onC2dImageMirrorCallback);
       // onvif_c2s.setOnC2sSetCamPrivateStatusCallback(onC2sSetCamPrivateStatusCallback);
    }
    
    
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
        	Intent intent = new Intent();
        	Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.image_sleepstatus:
                	//setPrivateStatus();
                    break;
                case R.id.image_imagestatus:
                	setImageStatus();
                    break;
                case R.id.rl_modify_pass:
                	if(checkConnect()){
	                	intent.setClass(mContext, CameraRePassActivity.class);
	                	intent.putExtra("devid", devid);
	                	mContext.startActivity(intent);
                	}
                    break;
                case R.id.rl_reboot:
                	if(checkConnect()){
                		reboot();
                	}
                    break;

                default:
                    break;
            }

        }
    };
    
    
    private boolean checkConnect() {
		if (app.SetCamConnector != 0) {
			return true;
		}
		else{
			ToastUtils.show(mContext, R.string.cam_no_connecting);
		}
		return false;
	}
    
    /**
	 * ����
	 */
    private BottomDialog rebootDialog;
	private void reboot() {
		rebootDialog = new BottomDialog(mContext, getString(R.string.cam_reboot_hint), 
				getString(R.string.cam_reboot), getString(R.string.cancel), 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						restartCam();
						rebootDialog.dismiss();
					}
				}, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						rebootDialog.dismiss();
					}
				});
		rebootDialog.show();
	}
	
	
	/**
     * �����豸
     */
    private void restartCam(){
        if (checkConnect()) {
            onvif_c2s.reboot(app.SetCamConnector);
        }
    }
    
    
   
    
    
    
    /**
     * ���õ�װ״̬
     */
    private void setImageStatus(){
        if (devInfo!=null){
            int status=imageMirrorMode;
            if (status==3){
                status=0;
            }else{
                status=3;
            }
            if(app.checkSetCamConnect()){
            	ProgressDialogUtil.getInstance().showDialog(mContext, getString(R.string.doing));
            	onvif_c2s.c2d_setImageMirror_fun(app.SetCamConnector, 0, status);
            }
        }
    }
    
    
	
	
	
	/**
     * ��ȡ����ģʽ�ص�
     */
	OnC2dImageMirrorCallback onC2dImageMirrorCallback = new OnC2dImageMirrorCallback(){

		@Override
		public void on_c2d_getImageMirrorCallBack(final int res,final int mode) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(res == 200){
						imageMirrorMode = mode;
						if(mode ==3){
							imageButton.setImageResource(R.drawable.switch_on);
						}else{
							imageButton.setImageResource(R.drawable.switch_off);
						}
						
					}else{
						ToastUtils.show(mContext, getString(R.string.get_imagemirror_faild)+res);
					}
				}
			});
		}

		@Override
		public void on_c2d_setImageMirrorCallBack(final int res, final int mode) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProgressDialogUtil.getInstance().cancleDialog();
					if(res == 200){
						imageMirrorMode = mode;
						if(mode ==3){
							imageButton.setImageResource(R.drawable.switch_on);
						}else{
							imageButton.setImageResource(R.drawable.switch_off);
						}
						
					}else{
						ToastUtils.show(mContext, getString(R.string.set_imagemirror_faild)+res);
					}
				}
			});
		}
		
	};
}
