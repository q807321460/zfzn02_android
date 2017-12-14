package com.jia.ezcamera.add;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;


import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.ezcamera.utils.wifi.lan.wifitool.SearchType;
import com.jia.ezcamera.utils.wifi.lan.wifitool.WifiHostBiz;
import com.jia.ezcamera.utils.wifi.lan.wifitool.WifiTester2;
import com.jia.ezcamera.utils.wifi.lan.wifitool.wifiInfo;
import com.jia.znjj2.R;

import java.util.HashMap;

import vv.ppview.PpviewClientInterface;

/**
 * Created by Administrator on 2016/5/17.
 */
public class SetupQrcordActivity extends Activity{
    private Context mContext = this;
    private TextView textSsid;
    private EditText editPass;
    private PpviewClientInterface onvif_c2s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_qrcode);
        onvif_c2s = PpviewClientInterface.getInstance();
        initView();

    }

    private void initView(){
        findViewById(R.id.btn_return).setOnClickListener(onClickListener);
        findViewById(R.id.btn_sure).setOnClickListener(onClickListener);
        findViewById(R.id.btn_skip).setOnClickListener(onClickListener);
        findViewById(R.id.image_show_wifi).setOnClickListener(onClickListener);

        textSsid = (TextView) findViewById(R.id.text_seclete_wifi_ssid);
        WifiHostBiz wifiHostBiz = new WifiHostBiz(mContext);
        if(wifiHostBiz.isWifiApEnabled()){
            WifiConfiguration wifiConf = wifiHostBiz.getWifiApConfiguration();
            textSsid.setText(wifiConf.SSID);
        }else {
            textSsid.setText(getWifiSSid());
        }
        editPass = (EditText)findViewById(R.id.edit_wifi_pass);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.btn_sure:
                case R.id.btn_skip:
                    String qrcodeString = onvif_c2s.getSetupQrcodeString(textSsid.getText().toString(),editPass.getText().toString().trim());
                    Intent qrcodeIntent = new Intent();
                    qrcodeIntent.setClass(mContext,SetupQrcordActivity2.class);
                    qrcodeIntent.putExtra("QRCODESTRING",qrcodeString);
                    mContext.startActivity(qrcodeIntent);
                    finish();
                    break;
               
                case R.id.image_show_wifi:
                    doSearchWifi();
                    break;
            }
        }
    };


    WifiTester2 wifiTester = null;

    public void setWifiSsid(String ssid){
        if (textSsid!=null) {
            textSsid.setText(ssid);
        }
    }

    WifiTester2.SearchWifiListCallback searchWifiListCallback = new WifiTester2.SearchWifiListCallback() {
        @Override
        public void getWifiList(SearchType searchType, final HashMap<String, wifiInfo> wifiMap) {
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressDialogUtil.getInstance().cancleDialog();
                    BuilderDialog(wifiMap, SearchType.WIFI);
                }
            });

        }

        @Override
        public void getMyConnectorWifi(wifiInfo myWifi) {

        }
    };



    private Dialog dlg = null;

    private void BuilderDialog(HashMap<String, wifiInfo> wifiMap, SearchType searchType) {
        if (wifiMap == null) {
            return;
        }
        if (wifiMap.size() < 1) {
            ToastUtils.show(mContext, R.string.no_devap);
            return;
        }
        final HashMap<String, wifiInfo> WifiMap = wifiMap;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (searchType == SearchType.AP) {
            //builder.setTitle(mContext.getString(R.string.select_ap));
        } else if (searchType == SearchType.WIFI) {
            builder.setTitle(mContext.getString(R.string.select_wifi));
        }
        final ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(
                mContext, R.layout.item_wifi);
        for (String mStr : wifiMap.keySet()) {
            array_adapter.add(mStr);
        }
        builder.setAdapter(array_adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String stSsid = array_adapter.getItem(which);
                        cancleDialog();
                        setWifiSsid(stSsid);
                    }
                });
        dlg = builder.create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }

    private void cancleDialog() {
        if (dlg != null) {
            dlg.cancel();
            dlg = null;
        }
    }

    private void initWifiTester(){
        if (wifiTester == null) {
            wifiTester = new WifiTester2(mContext);
            wifiTester.setWifiCallback(searchWifiListCallback);
        }
    }

    private  void doSearchWifi() {
        initWifiTester();
        ProgressDialogUtil.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.search_info), false);
        wifiTester.scanWifi(SearchType.WIFI);
    }

    private String getWifiSSid(){
        initWifiTester();
        return wifiTester.getMyWifiSsid();
    }

}
