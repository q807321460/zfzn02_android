package com.jia.ezcamera.set;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import vv.ppview.PpviewClientInterface;
import vv.tool.gsonclass.c2d_netif_info;
import vv.ppview.PpviewClientInterface.OnC2dGetNetifCallback;


public class CameraNetSetActivity extends Activity {

    private ImageButton btn_back;
    private Context mContext = this;
    private c2d_netif_info netInfo;
    private MainApplication app;
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
	private String devid;
	private GetDevInfoBean devInfo;

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
        	Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.net_eth_set:
                	intent.setClass(mContext, CameraNetOfEthActivity.class);
                	for(int i=0;i<netInfo.netifs.size();i++){
						if(netInfo.netifs.get(i).net_type==1||netInfo.netifs.get(i).net_type==2){
							intent.putExtra("ethinfo", netInfo.netifs.get(i));
							break;
						}
					}
                	mContext.startActivity(intent);
                    break;
                case R.id.net_wifi_set:
                	intent.setClass(mContext, CameraNetOfWifiActivity.class);
                	mContext.startActivity(intent);
                    break;
                case R.id.net_easy_set:
                	//startSetWifiActivity();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cam_net);
        app = (MainApplication)getApplication();
        devid = getIntent().getStringExtra("devid");
		app = (MainApplication) getApplication();
		devInfo = app.getDevInfo(devid);
        init();
        onvif_c2s.setOnC2dGetNetifCallback(onC2dGetNetifCallback);
    }

    private void init() {
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        btn_back.setOnClickListener(onClickListener);
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	if(app.checkSetCamConnect()){
        	ProgressDialogUtil.getInstance().showDialog(mContext, getString(R.string.get_netif_geting));
        	onvif_c2s.c2d_getNetif_fun(app.SetCamConnector);
        }
    }
    
   
	 
	 
	 OnC2dGetNetifCallback onC2dGetNetifCallback = new OnC2dGetNetifCallback() {
		
		@Override
		public void on_c2d_getNetifCallBack(final int nResult, final c2d_netif_info info) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProgressDialogUtil.getInstance().cancleDialog();
					findViewById(R.id.net_wifi_set).setOnClickListener(null);
			        findViewById(R.id.net_easy_set).setOnClickListener(null);
			        findViewById(R.id.net_eth_set).setOnClickListener(null);
					if(nResult==200&&info!=null){
						netInfo = info;
						if(netInfo.netifs==null){
							return;
						}
						for(int i=0;i<netInfo.netifs.size();i++){
							if(netInfo.netifs.get(i).net_type==3||netInfo.netifs.get(i).net_type==4){
								findViewById(R.id.net_wifi_set).setOnClickListener(onClickListener);
						        findViewById(R.id.net_easy_set).setOnClickListener(onClickListener);
								break;
							}
						}
						for(int i=0;i<netInfo.netifs.size();i++){
							if(netInfo.netifs.get(i).net_type==1||netInfo.netifs.get(i).net_type==2){
								findViewById(R.id.net_eth_set).setOnClickListener(onClickListener);
								break;
							}
						}
						
					}else{
						ToastUtils.show(mContext, getString(R.string.get_netif_faild)+nResult);
						
					}
				}
			});
			
		}
	};
	
	
}
