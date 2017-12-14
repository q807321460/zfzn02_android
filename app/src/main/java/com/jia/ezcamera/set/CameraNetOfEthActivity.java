package com.jia.ezcamera.set;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import vv.ppview.PpviewClientInterface;
import vv.tool.gsonclass.item_netif;
import vv.ppview.PpviewClientInterface.OnC2dSetNetifCallback;


public class CameraNetOfEthActivity extends Activity {

    private ImageButton btn_back;
    private TextView btn_sure;
    private Context mContext = this;
    private ImageView image_dhcp;
    private LinearLayout setLyaout;
    private item_netif ethinfo;
    private MainApplication app;
    private EditText net_eth_ip,net_eth_netmask,net_eth_gateway,net_eth_dns1,net_eth_dns2;
    //listview_group_item cam_item;
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.btn_sure:
                	setNetInfo();
                    break;
                case R.id.net_eth_dhcp:
                    if(ethinfo.net_type==1){
                    	ethinfo.net_type=2;
                    	ethinfo.dns_dhcp = 0;
                    	refreshActivity();
                    }else{
                    	ethinfo.net_type=1;
                    	ethinfo.dns_dhcp = 1;
                    	refreshActivity();
                    }
                    break;
                default:
                    break;
            }

        }
    };
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cam_netofeth);
        app = (MainApplication)getApplication();
        ethinfo = (item_netif) getIntent().getExtras().getSerializable("ethinfo");
        onvif_c2s.setOnC2dSetNetifCallback(onC2dSetNetifCallback);
        init();

    }

    private void init() {
    	setLyaout = (LinearLayout)findViewById(R.id.net_set_layout);
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        btn_sure = (TextView) findViewById(R.id.btn_sure);
        image_dhcp = (ImageView)findViewById(R.id.net_eth_dhcp);
        net_eth_ip = (EditText)findViewById(R.id.net_eth_ip);
        net_eth_netmask = (EditText)findViewById(R.id.net_eth_netmask);
        net_eth_gateway = (EditText)findViewById(R.id.net_eth_gateway);
        net_eth_dns1 = (EditText)findViewById(R.id.net_eth_dns1);
        net_eth_dns2 = (EditText)findViewById(R.id.net_eth_dns2);

        btn_back.setOnClickListener(onClickListener);
        btn_sure.setOnClickListener(onClickListener);
        image_dhcp.setOnClickListener(onClickListener);
        refreshActivity();
    }
    
    private void setNetInfo(){
    	if(app.checkSetCamConnect()){
    		ProgressDialogUtil.getInstance().showDialog(mContext, getString(R.string.set_netif_seting));
    		ethinfo.ip = net_eth_ip.getText().toString().trim();
    		ethinfo.mask = net_eth_netmask.getText().toString().trim();
    		ethinfo.gate = net_eth_gateway.getText().toString().trim();
    		ethinfo.dns1 = net_eth_dns1.getText().toString().trim();
    		ethinfo.dns2 = net_eth_dns2.getText().toString().trim();
    		onvif_c2s.c2d_setNetif_fun(app.SetCamConnector, ethinfo);
    	}
    }
	private void refreshActivity() {
		if(ethinfo.net_type==1){
			image_dhcp.setImageResource(R.drawable.switch_off);
			setLyaout.setVisibility(View.VISIBLE);
			net_eth_ip.setText(ethinfo.ip);
			net_eth_ip.setEnabled(true);
			net_eth_netmask.setText(ethinfo.mask);
			net_eth_netmask.setEnabled(true);
			net_eth_gateway.setText(ethinfo.gate);
			net_eth_gateway.setEnabled(true);
			net_eth_dns1.setText(ethinfo.dns1);
			net_eth_dns1.setEnabled(true);
			net_eth_dns2.setText(ethinfo.dns2);
			net_eth_dns2.setEnabled(true);
			
		}else{
			image_dhcp.setImageResource(R.drawable.switch_on);
			setLyaout.setVisibility(View.VISIBLE);
			net_eth_ip.setText(ethinfo.ip);
			net_eth_ip.setEnabled(false);
			net_eth_netmask.setText(ethinfo.mask);
			net_eth_netmask.setEnabled(false);
			net_eth_gateway.setText(ethinfo.gate);
			net_eth_gateway.setEnabled(false);
			net_eth_dns1.setText(ethinfo.dns1);
			net_eth_dns1.setEnabled(false);
			net_eth_dns2.setText(ethinfo.dns2);
			net_eth_dns2.setEnabled(false);
			//setLyaout.setVisibility(View.GONE);
		}
	}
	
	
	OnC2dSetNetifCallback onC2dSetNetifCallback = new OnC2dSetNetifCallback() {
		
		@Override
		public void on_c2d_setNetifCallBack(final int res) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProgressDialogUtil.getInstance().cancleDialog();
					if(res==200){
						ToastUtils.show(mContext, getString(R.string.set_netif_succ));
					}else{
						ToastUtils.show(mContext, getString(R.string.set_netif_faild));
					}
				}
			});
			
		}
	};
}
