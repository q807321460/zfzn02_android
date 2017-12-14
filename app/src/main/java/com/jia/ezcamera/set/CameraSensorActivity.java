package com.jia.ezcamera.set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;


import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.play.StaticConstant;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.SensorAdapter;
import com.jia.ezcamera.utils.SensorArray;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import java.util.ArrayList;

import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dSensorCallback;
import vv.ppview.PpviewClientInterface.OnDevConnectCallbackListener;
import vv.tool.gsonclass.item_sensorinfo;

public class CameraSensorActivity extends Activity implements OnClickListener {
    private static final String TAG = CameraSensorActivity.class.getSimpleName();

    private static final int GET_SENSOR_LIST = 3;
    private static final int COMPARE_CODE = 4;
    private static final int REMOVE_SENSOR = 7;
    public static final int UPDATE = 10;
    private Context myContext;
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();


    private MainApplication app;
    private SensorAdapter adapter = null;
    public static Handler myHandler = null;
    private boolean isCode = false;


    private String devid;
    private GetDevInfoBean devInfo;
    private int chl_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cam_afs_sensor);
        myContext = this;
        devid = getIntent().getStringExtra("devid");
        app = (MainApplication) getApplication();
        devInfo = app.getDevInfo(devid);
        init();
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                ProgressDialogUtil.getInstance().cancleDialog();
                int result = msg.arg1;
                switch (msg.what) {
                    case GET_SENSOR_LIST:
                        if (result == StaticConstant.RESULT_SUCESS) {
                            ArrayList<item_sensorinfo> list = (ArrayList<item_sensorinfo>) msg.obj;
                            if (list != null) {
                                int lSize = list.size();
                                if (lSize > 0) {
                                    SensorArray.getInstance().clearList();
                                    for (int i = 0; i < lSize; i++) {
                                        item_sensorinfo item = list.get(i);
                                        // Log.i(TAG, "handleMessage   item="
                                        // + item.sensor_id + "  status="
                                        // + item.status + "    item.type="
                                        // + item.type);
                                        SensorArray.getInstance().addItem(
                                                item.index, item.chl_id, item.name,
                                                item.type, item.sensor_id,
                                                item.preset,
                                                item.is_alarm, item.status, item.low_power, item.sub_chl_count, item.sub_chls,0);
                                    }
                                    adapter.doRefresh();
                                }
                                if(lSize==0){
                                	SensorArray.getInstance().clearList();
                                	adapter.doRefresh();
                                }
                            }
                        } else {
                        	ToastUtils.show(myContext, getString(R.string.afs_sensor_get_faild));
                        }
                        break;
                    case COMPARE_CODE:
                        if (result == StaticConstant.RESULT_SUCESS) {
                            ArrayList<item_sensorinfo> list = (ArrayList<item_sensorinfo>) msg.obj;
                            if (list != null) {
                                int lSize = list.size();
                                if (lSize > 0) {
                                    item_sensorinfo item = list.get(0);
                                    if (SensorArray.getInstance().ifContainItem(
                                            item.index, item.chl_id, item.name,
                                            item.type, item.sensor_id, item.preset, item.is_alarm,
                                            item.status, item.low_power, item.sub_chl_count, item.sub_chls,1)) {
                                        adapter.doRefresh();
                                        return;
                                    }
                                }
                            }
                            // 对码已存在
                            ToastUtils.showShort((Activity) myContext,
                                    R.string.afs_sensor_startcode_faild2);
                        } else {
                        	ToastUtils.showShort((Activity) myContext,
                                    R.string.afs_sensor_startcode_faild);
                        }
                        break;
                    case REMOVE_SENSOR:
                        if (result == StaticConstant.RESULT_SUCESS) {
                            String sId = (String) msg.obj;
                            if (!TextUtils.isEmpty(sId)) {
                                if (SensorArray.getInstance().removeItem(sId)) {
                                    adapter.doRefresh();
                                }
                            }
                        } else {
                        	ToastUtils.showShort((Activity) myContext,
                                    R.string.afs_sensor_del_faild);
                        }
                        break;
                    case UPDATE:
                        adapter.doRefresh();
                        break;
                    default:
                        break;
                }
            }

            ;
        };
    }


    private void init() {
        findViewById(R.id.btn_return).setOnClickListener(this);
        findViewById(R.id.btn_sure).setOnClickListener(this);
        adapter = new SensorAdapter(myContext);
        ListView list =  (ListView)findViewById(R.id.sensor_info_list);
        list.setAdapter(adapter);
        /*list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (position == adapter.getCount() - 1) {
                    doOpenCode();
                }
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        onvif_c2s.setOnC2dSensorCallback(sensorCallback);
        doGetList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SensorArray.getInstance().clearList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                this.finish();
                break;
            case R.id.btn_sure:// 开启对码
               // doOpenCode();
            	createCodeConnector();
                break;
            default:
                break;
        }
    }

    /**
     * 获取列表
     */
    private void doGetList() {
        if (app.checkSetCamConnect() && devInfo != null) {
            ProgressDialogUtil.getInstance().showDialog(
                    (Activity) myContext,
                    myContext.getResources().getString(
                            R.string.afs_sensor_geting), false);
            onvif_c2s.c2d_extSensorGetList_fun(app.SetCamConnector, chl_id, 0);
        }
    }

    /**
     * 开启对码
     */
    private void doOpenCode() {
        if (app.codeConnect != 0 && devInfo != null&& !isCode) {
            ProgressDialogUtil.getInstance().showDialog((Activity) myContext,
                    myContext.getResources().getString(R.string.afs_sensor_startcodeing),
                    new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							if(isCode){
								Log.e("DEBUG", "ReConnect");
								releaseCodeConnector();
								isCode = false;
							}
							
						}
					}, new OnCancelListener() {
						
						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							Log.e("DEBUG", "ReConnect");
							releaseCodeConnector();
							isCode = false;
						}
					});
            isCode = true;
            onvif_c2s.c2d_extSensorMatch_fun(app.codeConnect, chl_id);
        }
    }

    // 200=成功 203=认证错误 204=设备未就绪 205=不支持 409=超时 500=参数错误 501=内部错误

    // -----回调
    OnC2dSensorCallback sensorCallback = new OnC2dSensorCallback() {

        @Override
        public void on_c2d_extSensorMatch(int arg0,
                                          ArrayList<item_sensorinfo> arg1) {
            Log.i(TAG, "on_c2d_extSensorMatch    result=" + arg0 + "   arg1="
                    + arg1);
            Message m = Message.obtain();
            m.what = COMPARE_CODE;
            m.arg1 = arg0;
            m.obj = arg1;
            myHandler.sendMessage(m);
        }

        @Override
        public void on_c2d_extSensorGetList(int arg0,
                                            ArrayList<item_sensorinfo> arg1) {
            Log.i(TAG, "on_c2d_extSensorGetList     result=" + arg0
                    + "   arg1=" + arg1);
            Message m = Message.obtain();
            m.what = GET_SENSOR_LIST;
            m.arg1 = arg0;
            m.obj = arg1;
            myHandler.sendMessage(m);
        }

        @Override
        public void on_c2d_extSensorRemove(int result, int chlId,
                                           String sebsorId) {
            Log.i(TAG, "on_c2d_extSensorRemove    result=" + result
                    + "   chlId=" + chlId + "    sebsorId=" + sebsorId);
            Message m = Message.obtain();
            m.what = REMOVE_SENSOR;
            m.arg1 = result;
            m.arg2 = chlId;
            m.obj = sebsorId;
            myHandler.sendMessage(m);
        }

        @Override
        public void on_c2d_extSensorSetInfo(int arg0, item_sensorinfo arg1) {
            Log.i(TAG, "on_c2d_extSensorSetInfo    result=" + arg0 + "   arg1="
                    + arg1);
        }

        @Override
        public void on_c2d_extSensorRemoteControlSwitch(int arg0, int arg1,
                                                        String arg2, int arg3, int arg4) {
            // TODO Auto-generated method stub

        }

        @Override
        public void on_c2d_extSensorSetSubChlInfo(int arg0, int arg1,
                                                  String arg2, int arg3, String arg4, int arg5) {
            // TODO Auto-generated method stub

        }
    };
    
    
    
    private void createCodeConnector(){
        if (devInfo != null&&app.codeConnect==0) {
        	onvif_c2s.setOndevConnectCallback(onMessageCallback);
            ProgressDialogUtil.getInstance().showDialog(myContext,
                    myContext.getResources().getString(R.string.cam_connecting),false);
            app.codeConnect = onvif_c2s.createConnect(devInfo.dev_id, "admin",
                    devInfo.devpass);
        }else{
            releaseCodeConnector();
        }
        if(app.codeConnect<=0){
        	releaseCodeConnector();
            ToastUtils.show(myContext, getString(R.string.afs_sensor_startcode_faild));
        }
    }

    private void releaseCodeConnector(){
        if (app.codeConnect != 0) {
            onvif_c2s.releaseConnect(app.codeConnect);
            app.codeConnect=0;
        }
        onvif_c2s.removeOndevConnectCallback(onMessageCallback);
    }

    
    
 // 回调
    OnDevConnectCallbackListener onMessageCallback = new OnDevConnectCallbackListener() {

        @Override
        public void on_connect_callback(final int msgid,final long connector,final int result) {
            Log.i(TAG, "on_connect_callback    connector=" + connector
                    + "     msgid=" + msgid + "     result=" + result);
            runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (app.codeConnect == connector) {
		                if (result == 1 && msgid == 256) {
		                    doOpenCode();
		                } else {
		                    // 连接失败
		                    releaseCodeConnector();
		                    ToastUtils.show(myContext, getString(R.string.afs_sensor_startcode_faild));
		                }
		            }
				}
			});
            
        }
    };

}
