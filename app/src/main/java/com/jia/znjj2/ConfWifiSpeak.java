package com.jia.znjj2;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.smartconfig.client.SmartConfigClient;
import com.iflytek.smartconfig.listener.RecvListener;
import com.iflytek.smartconfig.message.NotifyMessage;
import com.jia.util.NetWorkUtil1;

public class ConfWifiSpeak extends Activity implements View.OnClickListener {
    private static final String TAG = "ConfWifiSpeak";

    private Button mStartSend;
    private Button mStopSend;
    private EditText mWifiPassEdit;
    private String mSSID;
    private String mPassword;
    private int mLocalIP;

    private SendThread mSendThread;

    private boolean mWifiConnected;
    private boolean mWaitRst;

    private Toast mToast;

    private int mWifiConnSuccessTime;
    private int mWifiConnFailedTime;

    private boolean appear = false;

    //wifi状态广播接收器
    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            //网络状态改变广播
            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                    mWifiConnected = true;

                    mSSID = NetWorkUtil1.getSSID(ConfWifiSpeak.this);
                    mLocalIP = NetWorkUtil1.getLocalIP(ConfWifiSpeak.this);

                    mWifiPassEdit.setHint("请输入" + mSSID +"的密码");
                } else if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {

                    mWifiConnected = false;
                    mWifiPassEdit.setHint("当前未连接wifi，请连接wifi");
                }
            }
            //WIFI状态改变广播
            else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {

                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    mWifiConnected = false;

                    new AlertDialog.Builder(ConfWifiSpeak.this)
                            .setTitle("提示")
                            .setMessage("检测到wifi未打开，前去打开？")
                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (android.os.Build.VERSION.SDK_INT > 10) {
                                        startActivity(new Intent( android.provider.Settings.ACTION_SETTINGS));
                                    } else {
                                        startActivity(new Intent( android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                    }
                                }
                            }).setNegativeButton("取消", null).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_wifi_speak);

        initUI();

        initData();

        initConfig();

        registerWifiReceiver();
    }

    private void initUI() {
        mStartSend 		= (Button) findViewById(R.id.btn_start_send);
        mStopSend 		= (Button) findViewById(R.id.btn_stop_send);
        mWifiPassEdit 	= (EditText) findViewById(R.id.wifi_pass_edit);
        mToast = Toast.makeText(ConfWifiSpeak.this, "", Toast.LENGTH_LONG);

        mStartSend.setOnClickListener(this);
        mStopSend.setOnClickListener(this);
    }

    private void initData() {
        mWifiConnected = false;
        mWaitRst	   = false;
    }

    private void initConfig() {

        SmartConfigClient.setPacketInterval(10);
        SmartConfigClient.setRecvTimeOut(60000);
    }

    public void confWifiSpeakBack(View view){
        finish();
    }
    private void registerWifiReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    private RecvListener recvListener = new RecvListener() {

        @Override
        public void onReceiveTimeOut() {
            stopSendAndListen();

            if (!appear) {
                mWifiConnFailedTime ++;
                appear = true;
            }


            Log.e(TAG,"onReceiveTimeOut");
            //showTip("接收超时，请重新发送配置");

            //test
            appear = false;
            startSendAndListen();
        }

        @Override
        public void onError(int errorCode) {
            stopSendAndListen();

            if (!appear) {
                mWifiConnFailedTime ++;
                appear = true;
            }

            showTip(SmartConfigClient.getErrorDescription(errorCode));

            //test
            appear = false;
            startSendAndListen();
        }

        @Override
        public void onReceived(NotifyMessage message) {
            Log.d(TAG, "onReceived | " + message.getMac());

            if (!appear) {
                mWifiConnSuccessTime ++;
                appear = true;
            }
            //			message.getIp();
            //			message.getHostName();
            //			message.getPort();
            showTip("设备" + message.getHostName() + "配置完成");
            stopSendAndListen();

            //test
            appear = false;
            startSendAndListen();
        }
    };

    private void startSendAndListen() {

        if (!mWaitRst) {

            //提醒: 密码长度多于32位，在传输过程中会有丢包风险
            if (mWifiConnected) {

                mPassword = getContent(mWifiPassEdit);
                Log.d(TAG, "mPassword " + mPassword);
            } else {
                showTip("未连入有效网络或wifi密码长度不在8-32位之间，请修改");
                return;
            }

            if (null == mSendThread) {
                mSendThread = new SendThread();
                mSendThread.start();
               // showTip("开始配置");
                SmartConfigClient.startListen(recvListener);

                mWaitRst = true;
            }
        } else {
            showTip("正在配置，请稍等");
            return;
        }
    }

    private void stopSendAndListen() {

        if(mWaitRst) {
            mWaitRst = false;
        } else return;

        if (mSendThread != null) {
            mSendThread.stopRun();
            mSendThread = null;
        }
        SmartConfigClient.stopListen();
    }

    //发送广播包线程
    class SendThread extends Thread {

        public SendThread(String ssid, String password) {
            mSSID = ssid;
            mPassword = password;
        }
        public SendThread(){}

        private boolean mStopRun = false;

        public void stopRun() {
            mStopRun = true;
            interrupt();
        }

        @Override
        public void run() {

            while (!mStopRun) {
                try{
                    SmartConfigClient.send(mSSID, mPassword, mLocalIP);
                } catch (Exception e) {
                    Log.e(TAG, "Exception when send smartconfig info");
                }
            }
        }
    }

    private void showTip(final String tip) {
        if (!TextUtils.isEmpty(tip)) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mToast.setText(tip);
                    mToast.show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mWifiReceiver) {
            unregisterReceiver(mWifiReceiver);
        }

        if(mSendThread != null){
            mSendThread.stopRun();
            mSendThread = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_send:

                startSendAndListen();
                break;

            case R.id.btn_stop_send:

                stopSendAndListen();
                showTip("结束配置");
                break;
        }
    }

    private int getLength(EditText edit) {
        return edit.getText().length();
    }

    private String getContent(EditText edit) {
        return edit.getText().toString();
    }
}

