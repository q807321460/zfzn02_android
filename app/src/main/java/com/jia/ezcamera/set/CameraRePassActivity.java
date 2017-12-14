package com.jia.ezcamera.set;




import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.StringUtils;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnDevSetCallbackListener;


public class CameraRePassActivity extends Activity {

    private ImageButton btn_back;
    private TextView btn_sure;
    private EditText et_oldpass;
    private EditText et_newpass1;
    private EditText et_newpass2;
    private Context mContext = this;
    private MainApplication app = null;
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.btn_sure:
                	setNewPassword();
                    break;
                default:
                    break;
            }

        }
    };
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
	private String devid;
	private GetDevInfoBean devInfo;
	private int chl_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cam_repass);
        app = (MainApplication)getApplication();
        devid = getIntent().getStringExtra("devid");
		devInfo = app.getDevInfo(devid);
        init();
    }

    private void init() {
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        btn_sure = (TextView) findViewById(R.id.btn_sure);
        et_oldpass = (EditText) findViewById(R.id.cam_repass_password);
        et_newpass1 = (EditText) findViewById(R.id.cam_repass_newpassword1);
        et_newpass2 = (EditText) findViewById(R.id.cam_repass_newpassword2);

        btn_back.setOnClickListener(onClickListener);
        btn_sure.setOnClickListener(onClickListener);
        onvif_c2s.setOnDevSetCallback(onDevSetCallbackListener);
    }
    
    private void setNewPassword(){
    	if(app.SetCamConnector==0)
    		return;
    	String oldPass = et_oldpass.getText().toString().trim();
        String newPass = et_newpass1.getText().toString().trim();
        String newPass2 = et_newpass2.getText().toString().trim();
        
        if (oldPass == null || "".equals(oldPass)) {
            // showToast(SetDeviceActivity.this.getResources().getString(
            // R.string.old_pass_null));
            // return;
            oldPass = "";
        }
        if (newPass == null || "".equals(newPass)) {
            showToast(mContext.getResources().getString(
                    R.string.new_pass_null));
            return;
        }
        if (newPass2 == null || "".equals(newPass2)) {
            showToast(mContext.getResources().getString(
                    R.string.new_pass_null));
            return;
        }
        if (!newPass.equals(newPass2)) {
            showToast(mContext.getResources().getString(
                    R.string.pass_inconformity));
            return;
        }
        if(!StringUtils.checkDevPassword(newPass)){
            showToast(mContext.getResources().getString(
                    R.string.set_416));
            return;
        }
        
        onvif_c2s.c2d_set_pass_fun(app.SetCamConnector, "admin", oldPass,
                newPass, "");
        Log.e("DEBUG", "hconnector  "+app.SetCamConnector+"  "+"admin");
        ProgressDialogUtil.getInstance().showDialog(mContext,
                mContext.getResources().getString(
                        R.string.cam_repassing));
    }
    
    
    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    
    
    
    
    
    /**
	 * �޸��豸����ص�
	 */
	OnDevSetCallbackListener onDevSetCallbackListener = new OnDevSetCallbackListener() {

		@Override
		public void on_setpass_callback(final int res, Object obj) {
			runOnUiThread(new Runnable() {
				public void run() {
					ProgressDialogUtil.getInstance().cancleDialog();
                    switch (res){
                        case 200:
                            ToastUtils.show(mContext,R.string.set_200);
                            String newPass = et_newpass1.getText().toString().trim();
                            devInfo.devpass = newPass;
                            app.modfiyVRDevice(devInfo);
                            finish();
                            break;
                        case 203:
                            ToastUtils.show(mContext,R.string.authentication_failure);
                            break;
                        default:
                            ToastUtils.show(mContext,getString(R.string.reset_pass_faild)+res);
                            break;
                    }
					Log.e("DEBUG", " on_setpass_callback  "+res+"");
				}
			});
			
		}
	};
}
