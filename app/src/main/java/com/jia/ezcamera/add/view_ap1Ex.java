package com.jia.ezcamera.add;



import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;


/**
 * Created by ls on 15/3/6.
 */
public class view_ap1Ex implements View.OnClickListener{
    private Context mContext;
    private View mView;
    private SetApActivityEx mParent;

    private EditText editDeviceUser;
    private EditText editDevicePass;
    public view_ap1Ex(SetApActivityEx setApActivity, Context context){
        this.mParent=setApActivity;
        this.mContext=context;
        mView=View.inflate(mContext, R.layout.view_setap1,null);
        init();
    }

    public View getmView(){
        return  mView;
    }

    private void init(){
        initMenu();
        mView.findViewById(R.id.btn_dev_login).setOnClickListener(this);
        editDeviceUser= (EditText) mView.findViewById(R.id.edit_dev_user);
        editDeviceUser.setText("admin");
        editDevicePass= (EditText) mView.findViewById(R.id.edit_dev_pass);
    }

    private void initMenu(){
        mView.findViewById(R.id.btn_return).setOnClickListener(this);
    }

    public void setDevicePass(String devicePass) {
        editDevicePass.setText(devicePass);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_return:
                mParent.doFinish();
                break;
            case R.id.btn_dev_login:
                doLoginDevice();
                break;
        }
    }

    public void doLoginDevice(){
        String devUser=editDeviceUser.getText().toString().trim();
        String devPass=editDevicePass.getText().toString().trim();
        if (TextUtils.isEmpty(devUser)){
            ToastUtils.show(mContext,R.string.devid_not_null);
            return;
        }
        if (TextUtils.isEmpty(devPass)){
            ToastUtils.show(mContext,R.string.devpass_not_null);
            return;
        }
        mParent.setDevPass(devPass);
        mParent.createConnector();
    }

}
