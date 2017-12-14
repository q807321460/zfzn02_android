package com.jia.ezcamera;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jia.ezcamera.bean.DevInfoListBean;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.play.CJpgFileTool;
import com.jia.ezcamera.play.ImageLoader_local;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.ezcamera.utils.wifi.lan.wifitool.item_wifi;
import com.jia.jdplay.JdPlayImageUtils;
import com.jia.znjj2.CameraActivity;
import com.jia.znjj2.R;
import com.judian.support.jdplay.sdk.JdPlayManager;


import java.util.ArrayList;

import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewHTTPSInterface;
import vv.tool.c2sgsonclass.HTTPSRet;
import vv.tool.c2sgsonclass.RelationUpdateBean;
import vv.tool.gsonclass.c2d_setPreConnectUUID;
import vv.tool.gsonclass.uuid_item;

/**
 * Created by Administrator on 2017/8/8.
 */
public class MainApplication extends Application{
    private static final String TAG = "MainApplication";
    private PpviewClientInterface ppviewClientInterface;
    public final static String vv_url = "http://ppview.vveye.com:3000/webapi/client";
    public final static String bound_url = "http://ppview.vveye.com:3000/webapi/device";
    public final static String app_key = "C767115F-0ED0-0001-3451-1DC0D520ECB0";
    public final static String app_pass = "9aaa8e3fea97081839f7515cb3426359";
    public final static String push_svr = "120.76.138.115";
    public final static String p2p_svr = "nat.vveye.net";
    public final static int p2p_port = 8000;
    public final static String p2p_secret = "";
    public final static String event_url = "http://ppview.vveye.com:3000/webapi/page";
    public final static String pushCer = "";
    private boolean isInit = false;
    private CJpgFileTool cft = CJpgFileTool.getInstance();
    private ImageLoader_local ilc;
    public long SetCamConnector = 0;//设置摄像头界面的统一Connect
    public long codeConnect = 0;
    public static String wifi_ssid = "";// 记录当前连接的wifi
    public static item_wifi iw = null;

    private PpviewHTTPSInterface ppviewHTTPSInterface;
    @Override
    public void onCreate() {
        super.onCreate();
        initSDK();

        //初始化HTTS与服务器交互
        ppviewHTTPSInterface = new PpviewHTTPSInterface();
        ppviewHTTPSInterface.setCer(pushCer);
        ppviewHTTPSInterface.setSvr(push_svr);
        ppviewHTTPSInterface.SetOnC2sPusherRelationUpdateCallback(onC2sPusherRelationUpdateCallback);
        //jdplay
        JdPlayImageUtils.getInstance().initialize(this);
        JdPlayManager.getInstance().initialize(this);
        Log.v(TAG, "JdPlayManager version : " + JdPlayManager.getInstance().getVersion());

    }

    PpviewHTTPSInterface.OnC2sPusherRelationUpdateCallback onC2sPusherRelationUpdateCallback = new PpviewHTTPSInterface.OnC2sPusherRelationUpdateCallback() {
        @Override
        public void on_c2s_pusher_relation_update(HTTPSRet httpsRet) {
            Log.e("DEBUG","httpsRet  code "+httpsRet.code   +"     msg "+httpsRet.msg);
        }
    };


    private void initSDK(){
        if(!isInit) {
            ppviewClientInterface = PpviewClientInterface.getInstance();
            ppviewClientInterface.SetAppInfo(vv_url, app_key, app_pass, bound_url, p2p_svr,
                    p2p_port, p2p_secret, event_url);

            readDevList();
            cft.init(getApplicationContext());
            ppviewClientInterface.SetOnC2dP2pStateCallback(onC2dP2pStateCallback);
            isInit = true;
        }
    }


    /************************VR摄像头存储相关**************************/
    private DevInfoListBean devInfoList;

    private void saveDevlist(){
        Gson gson = new GsonBuilder().create();
        String jsonstr = gson.toJson(devInfoList);
        SharedPreferences preferences=getSharedPreferences("vrdevlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("devlist", jsonstr);
        editor.commit();
    }
    private void readDevList(){
        SharedPreferences preferences=getSharedPreferences("vrdevlist", Context.MODE_PRIVATE);
        String devlistJson=preferences.getString("devlist", "");
        if(TextUtils.isEmpty(devlistJson)){
            devInfoList = new DevInfoListBean();
        }else{
            Gson gson = new GsonBuilder().create();
            devInfoList = gson.fromJson(devlistJson, DevInfoListBean.class);
        }

    }
    //添加VR设备，0成功 -1已存在
    public int addVRDevice(GetDevInfoBean info){
        if(devInfoList.devInfoList==null){
            devInfoList.devInfoList = new ArrayList<GetDevInfoBean>();
        }
        if(devInfoList.devInfoList!=null){
            for(int i=0;i<devInfoList.devInfoList.size();i++){
                if(devInfoList.devInfoList.get(i).dev_id.equals(info.dev_id)){
                    return -1;
                }
            }
            devInfoList.devInfoList.add(info);
            //从SharedPreferences中添加
            saveDevlist();
        }
        return 0;
    }

    //修改VR设备，0成功 -1已存在
    public int modfiyVRDevice(GetDevInfoBean info){
        if(devInfoList.devInfoList==null){
            devInfoList.devInfoList = new ArrayList<GetDevInfoBean>();
        }
        if(devInfoList.devInfoList!=null){
            for(int i=0;i<devInfoList.devInfoList.size();i++){
                if(devInfoList.devInfoList.get(i).dev_id.equals(info.dev_id)){
                    devInfoList.devInfoList.set(i, info);
                    saveDevlist();
                    return 0;
                }
            }
        }
        return -1;
    }

    //删除VR设备，0成功 -1失败
    public int removeVRDevice(String devid){
        if(devInfoList.devInfoList==null){
            devInfoList.devInfoList = new ArrayList<GetDevInfoBean>();
        }
        if(devInfoList.devInfoList!=null){
            for(int i=0;i<devInfoList.devInfoList.size();i++){
                if(devInfoList.devInfoList.get(i).dev_id.equals(devid)){
                    devInfoList.devInfoList.remove(i);
                    saveDevlist();

                    break;
                }
            }
        }
        return 0;
    }

    public ArrayList<GetDevInfoBean> getDevInfoList(){
        if(devInfoList.devInfoList==null){
            devInfoList.devInfoList = new ArrayList<GetDevInfoBean>();
        }
        return devInfoList.devInfoList;
    }

    public GetDevInfoBean getDevInfo(String devid){
        for(int i =0;i<getDevInfoList().size();i++){
            if(getDevInfoList().get(i).dev_id.equals(devid)){
                return getDevInfoList().get(i);
            }
        }
        return null;
    }

    public static void removeDuplicate(ArrayList<uuid_item> list) {
        for ( int i = 0 ; i < list.size() - 1 ; i ++ ) {
            for ( int j = list.size() - 1 ; j > i; j -- ) {
                if (list.get(j).uuid.equals(list.get(i).uuid)) {
                    list.remove(j);
                }
            }
        }
    }
    public void setPreConnect(){
        c2d_setPreConnectUUID uuidArrary= new c2d_setPreConnectUUID();
        uuidArrary.uuids = new ArrayList<uuid_item>();
        for(int i =0;i<getDevInfoList().size();i++){
            GetDevInfoBean item = getDevInfoList().get(i);
            uuid_item uuid = new uuid_item();
            uuid.uuid = item.dev_id;
            uuid.p2pass = "";
            uuidArrary.uuids.add(uuid);
        }
        removeDuplicate(uuidArrary.uuids);
        ppviewClientInterface.setPreConnectUUIDs(uuidArrary);


        //刷新对应关系
        ArrayList<RelationUpdateBean.Devices> devices = new ArrayList<>();
        for(int i =0;i<getDevInfoList().size();i++){
            GetDevInfoBean item = getDevInfoList().get(i);
            RelationUpdateBean.Devices device = new RelationUpdateBean.Devices();
            device.name = item.local_name;
            device.chl_id = 0;
            device.dev_id = item.dev_id;
            devices.add(device);
        }
//        if(FirebaseInstanceId.getInstance().getToken()!=null&&!TextUtils.isEmpty(FirebaseInstanceId.getInstance().getToken()))
//            ppviewHTTPSInterface.c2s_pusher_relation_update(FirebaseInstanceId.getInstance().getToken(),devices);
    }

    PpviewClientInterface.OnC2dP2pStateCallback onC2dP2pStateCallback = new PpviewClientInterface.OnC2dP2pStateCallback() {

        @Override
        public void on_c2d_p2pstate(String devid, int tag1, int tag2) {
            // TODO Auto-generated method stub
            Log.e("DEBUG","devid   p2p "+tag2);
            for(int i=0;i<getDevInfoList().size();i++){
                if(devid.equals(getDevInfoList().get(i).dev_id)){
                    if(getDevInfoList().get(i).p2pstatus!=tag2){
                        getDevInfoList().get(i).p2pstatus = tag2;
                        if(CameraActivity.vrHandler!=null){
                            CameraActivity.vrHandler.sendEmptyMessage(0);
                        }
                        break;
                    }
                }
            }
        }
    };


    public boolean checkSetCamConnect(){
        if(SetCamConnector>0){
            return true;
        }else{
            ToastUtils.show(getApplicationContext(), getString(R.string.cam_no_connecting));
            return false;
        }
    }
}
