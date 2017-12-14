package com.jia.ezcamera.add;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.jia.ezcamera.utils.wifi.lan.wifitool.wifiInfo;
import com.jia.znjj2.R;

import vv.tool.gsonclass.item_netif;

/**
 * Created by ls on 15/3/6.
 */
public class view_ap2Ex implements View.OnClickListener{
    private static final String TAG=view_ap2Ex.class.getSimpleName();
    private Context mContext;
    private View mView;
    private SetApActivityEx mParent;

    private TextView textSsid;
    private EditText editPass;

    public view_ap2Ex(SetApActivityEx setApActivity, Context context){
        this.mParent=setApActivity;
        this.mContext=context;
        mView=View.inflate(mContext, R.layout.view_setap2,null);
        init();
    }

    public View getmView(){
        return  mView;
    }

    private void init(){
        initMenu();
        mView.findViewById(R.id.wifi_btn_backup).setOnClickListener(this);
        mView.findViewById(R.id.wifi_btn_next).setOnClickListener(this);
        mView.findViewById(R.id.image_show_wifi).setOnClickListener(this);
        textSsid= (TextView) mView.findViewById(R.id.text_seclete_wifi_ssid);
        editPass= (EditText) mView.findViewById(R.id.edit_wifi_pass);
    }

    private void initMenu(){
        mView.findViewById(R.id.btn_return).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_return:
                mParent.doFinish();
                break;
            case R.id.wifi_btn_backup:
                doBackUp();
                break;
            case R.id.wifi_btn_next:
                doNext();
                break;
            case R.id.image_show_wifi:
                doSearchWifi();
                break;
        }
    }

    private void doNext(){
        mParent.setMyWifiPass(editPass.getText().toString().trim());
        mParent.doSetDeviceNetif(getItemNetIf());
    }

    private void doBackUp(){
        mParent.pageTo(0);
    }

    private void doSearchWifi(){
        mParent.doSearchWifi();
    }

    public void setWifiSsid(String ssid){
        if (textSsid!=null) {
            textSsid.setText(ssid);
        }
    }


    private item_netif getItemNetIf(){
        item_netif netif = new item_netif();
        String wifiPass = editPass.getText().toString().trim();
        wifiInfo getmWifiInfo=mParent.getmWifiInfo();
        if (getmWifiInfo!=null) {
            if (TextUtils.isEmpty(wifiPass)) {
                wifiPass="";
            }
            netif.secu_psk_pass = wifiPass;
            netif.ssid = getmWifiInfo.ssid;
            netif.net_type = 4;
        }
        return netif;
    }


}
