package com.jia.ezcamera.add;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.play.StaticConstant;
import com.jia.ezcamera.utils.Alert;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.ezcamera.utils.wifi.lan.setview.Netif_Array;
import com.jia.ezcamera.utils.wifi.lan.wifitool.SearchType;
import com.jia.ezcamera.utils.wifi.lan.wifitool.WifiTester2;
import com.jia.ezcamera.utils.wifi.lan.wifitool.wifiInfo;
import com.jia.znjj2.CameraActivity;
import com.jia.znjj2.R;


import java.util.ArrayList;
import java.util.HashMap;

import vv.ppview.PpviewClientInterface;
import vv.tool.gsonclass.c2s_searchDev;
import vv.tool.gsonclass.item_netif;

public class SetApActivityEx extends Activity {
    private static final String TAG = SetApActivityEx.class.getSimpleName();
    private static final int SET_NETIF = 0;
    private static final int CONNECT_IP = SET_NETIF + 1;
    private static final int CONNECT_IP_OK = CONNECT_IP + 1;
    private static final int GET_WIFI_LIST = CONNECT_IP_OK+1;
    private Context mContext = null;
    private GetDevInfoBean getDevInfoBean;
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    Netif_Array na = Netif_Array.getInstance();
    ArrayList<c2s_searchDev.Dev> myList;

    private ViewFlipper mFlipper;

    private MainApplication app;

    private final static String devIp = "192.168.255.1";
    private String devUser = "admin";
    private String devPass = "123456";

    private long myConnector = 0;
    private wifiInfo myConWifiInfo = null;
    private wifiInfo mWifiInfo = null;
    private String myWifiPass="";

    private view_ap1Ex child1;
    private view_ap2Ex child2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ap);
        app = (MainApplication)getApplication();
        Intent intent = getIntent();
        if (intent != null) {
            mWifiInfo = (wifiInfo) intent.getSerializableExtra(StaticConstant.ITEM);
            myConWifiInfo = (wifiInfo) intent.getSerializableExtra(StaticConstant.CONNECTOR_WIFI_INFO);
        }
        mContext = this;
        initCallback();
        init();
        //-----获取到设备密码后,自动获取设备配置信息
        setDevicePass();
        child1.doLoginDevice();
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
//        initCallback();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        na.clearMap();
        releaseConnector();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ProgressDialogUtil.getInstance().cancleDialog();
            switch (msg.what) {
                case SET_NETIF:
                    //设置AP模式成功，返回列表界面
                    ProgressDialogUtil.getInstance().cancleDialog();
                    if(msg.arg1==200){

                        AlertDialog.Builder normalDialog = new AlertDialog.Builder(mContext)
                                .setTitle(getString(R.string.set_ok))
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //...To-do
                                                dialog.dismiss();
                                                if(app.addVRDevice(getDevInfoBean)==0){
                                                    if(ActivityAdd.myHandler!=null){
                                                        ActivityAdd.myHandler.sendEmptyMessage(ActivityAdd.FINISH);
                                                    }
                                                    if(CameraActivity.vrHandler!=null){
                                                        CameraActivity.vrHandler.sendEmptyMessage(1);
                                                    }
                                                    finish();
                                                }else{
                                                    Alert.showAlert(mContext,
                                                            getText(R.string.tips),
                                                            getText(R.string.cam_added),
                                                            getText(R.string.btn_ok));
                                                }
                                            }
                                        });
                        normalDialog.show();

                    }else if(msg.arg1==414||msg.arg1==203){
                        if(msg.arg1==414||msg.arg1==203)
                            ToastUtils.show(mContext,getString(R.string.add_info_414));
                        pageTo(0);
                    }else{
                        ToastUtils.show(mContext,getString(R.string.set_netif_faild)+msg.arg1);
                    }

                    break;
                case GET_WIFI_LIST:
                    BuilderDialog((HashMap<String, wifiInfo>) msg.obj, SearchType.WIFI);
                    break;
                case CONNECT_IP:
                    ProgressDialogUtil.getInstance().cancleDialog();
                    ToastUtils.show(mContext,R.string.content_info0);
                    finish();
                    break;
                case CONNECT_IP_OK:
                    //跳转到设置WIFI界面
                    ProgressDialogUtil.getInstance().cancleDialog();
                    pageTo(1);
                    break;

            }
            super.handleMessage(msg);
        }
    };

    private void init() {
        mFlipper = (ViewFlipper) findViewById(R.id.ap_flipper);
        child1 = new view_ap1Ex(this, mContext);
        child2 = new view_ap2Ex(this, mContext);
        mFlipper.addView(child1.getmView());
        mFlipper.addView(child2.getmView());
        if (myConWifiInfo!=null){
            child2.setWifiSsid(myConWifiInfo.ssid);
        }
    }

    private void initCallback() {
        onvif_c2s.setOnC2DCallback(cb);
        onvif_c2s.setOnC2dSetNetifCallback(onC2dSetNetifCallback);
    }

    public void doFinish() {
        this.finish();
    }

    public void pageTo(int index) {
        if (mFlipper != null && index >= 0 && index < mFlipper.getChildCount()) {
            mFlipper.setDisplayedChild(index);
        }
    }


    public String getDevIp() {
        return devIp;
    }

    public wifiInfo getmWifiInfo() {
        return myConWifiInfo;
    }

    public void setMyWifiPass(String wifiPass){
        myWifiPass=wifiPass;
    }

    private void setDevicePass() {
//        for (int i = 0; i < adh.getDevapSize(); i++) {
//            if (mWifiInfo != null) {
//                if (mWifiInfo.ssid.startsWith(adh
//                        .getDevapIndex(i))) {
//                    child1.setDevicePass("" + adh.getDevpassIndex(i));
//                    devPass = "" + adh.getDevpassIndex(i);
//                    break;
//                }
//            }
//        }
        child1.setDevicePass("88888888");
        devPass = "88888888" ;
    }




    /**
     * 搜索wifi
     */
    WifiTester2 wifiTester = null;

    private void initWifiTester(){
        if (wifiTester == null) {
            wifiTester = new WifiTester2(mContext);
            wifiTester.setWifiCallback(searchWifiListCallback);
        }
    }

    public void doSearchWifi() {
        initWifiTester();
        ProgressDialogUtil.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.search_info), false);
        wifiTester.scanWifi(SearchType.WIFI);
    }

    /**
     * 连接wifi
     */
    public void doConnectorWifi(String wifiPass) {
        initWifiTester();
        wifiTester.doConnectWifi(myConWifiInfo, wifiPass);
    }

    private Dialog dlg = null;

    /**
     * 显示wifi或者ap列表
     *
     * @param wifiMap
     * @param searchType
     */
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
                        child2.setWifiSsid(stSsid);
                        myConWifiInfo = WifiMap.get(stSsid);
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


    public void setDevPass(String devpass){
        devPass = devpass;
    }


    public void createConnector() {

        releaseConnector();
        ProgressDialogUtil.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.cam_connecting), false);

        myConnector = onvif_c2s.CreateConnectIP(devIp,devUser,devPass,"");
        Log.i(TAG,"createConnector    myConnector="+myConnector);

    }

    private void releaseConnector() {
        if (myConnector != 0) {
            onvif_c2s.releaseConnect(myConnector);
            myConnector = 0;
        }
    }

    /**
     * wifi回调
     */
    WifiTester2.SearchWifiListCallback searchWifiListCallback = new WifiTester2.SearchWifiListCallback() {
        @Override
        public void getWifiList(SearchType searchType, HashMap<String, wifiInfo> wifiMap) {
            Message message = Message.obtain();
            if (searchType == SearchType.WIFI) {
                message.what = GET_WIFI_LIST;
            }
            message.obj = wifiMap;
            mHandler.sendMessage(message);
        }

        @Override
        public void getMyConnectorWifi(wifiInfo myWifi) {

        }
    };



    PpviewClientInterface.OnC2DCallbackListener cb = new PpviewClientInterface.OnC2DCallbackListener() {

        @Override
        public void on_vv_search_dev_callback(String arg0) {

        }

        @Override
        public void on_get_devinfo_callback(final int nResuylt,final String sess,final String devinfo) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ProgressDialogUtil.getInstance().cancleDialog();
                    if(nResuylt==200){
                        Gson gson = new GsonBuilder().create();
                        getDevInfoBean = gson.fromJson(devinfo, GetDevInfoBean.class);
                        getDevInfoBean.local_name = getDevInfoBean.dev_id;
                        getDevInfoBean.devpass = devPass;
                        //连接成功，跳转到child2
                        mHandler.sendEmptyMessage(CONNECT_IP_OK);
                    }else if(nResuylt==203||nResuylt==414){
                        Alert.showAlert(mContext,
                                getText(R.string.tips),
                                getText(R.string.info_connect_wrong_pwd),
                                getText(R.string.btn_ok));
                        pageTo(0);
                    }else{
                        Alert.showAlert(mContext,
                                getText(R.string.tips),
                                getText(R.string.getdevinfo_fail),
                                getText(R.string.btn_ok));
                    }
                }
            });


        }

        @Override
        public void on_create_connect_ip(final long connectHandler, String devuser,
                                         String devpass, Object userTag) {
            Log.i(TAG, "on_create_connect_ip    connector=" + connectHandler
                    + "     myConnector=" + myConnector);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (myConnector==0&&connectHandler>0) {
                        myConnector=connectHandler;

                        ProgressDialogUtil.getInstance().showDialog(
                                mContext,
                                mContext.getResources().getString(R.string.start_getdevinfo), true);
                        onvif_c2s.c2d_get_devinfo_fun(myConnector, "admin", devPass, "getdevinfo", 0);

                    } else {
                        // 连接失败
                        releaseConnector();
                        mHandler.sendEmptyMessage(CONNECT_IP);
                    }
                }
            });

        }

        @Override
        public void on_bind_ip(int arg0, long arg1) {

        }


    };

    public void doSetDeviceNetif(item_netif netif){
        ProgressDialogUtil.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.set_netif_seting), false);

        onvif_c2s.c2d_setNetif_fun(myConnector,netif);
    }


    PpviewClientInterface.OnC2dSetNetifCallback onC2dSetNetifCallback = new PpviewClientInterface.OnC2dSetNetifCallback() {
        @Override
        /**
         * 设置设备网络的回调接口
         * @param nResult
         * -1=客户端句柄不存在
        -2=库未初始化
        -3=连接句柄不存在
        -4=json格式不正确
        -5=数据交互失败
        200=成功
        203=权限验证失败
        413=设备不支持（如设置有线时，设备没有有线功能）
        500=json中有必需要有的字段不存在
        501=设备查询网卡信息出错
         */
        public void on_c2d_setNetifCallBack(int nResult) {
            Message msg = new Message();
            msg.what = SET_NETIF;
            msg.arg1 = nResult;
            mHandler.sendMessage(msg);
        }
    };

}
