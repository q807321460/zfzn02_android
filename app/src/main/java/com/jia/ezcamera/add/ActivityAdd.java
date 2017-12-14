package com.jia.ezcamera.add;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.play.StaticConstant;
import com.jia.ezcamera.utils.Alert;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.ezcamera.utils.wifi.lan.wifitool.SearchType;
import com.jia.ezcamera.utils.wifi.lan.wifitool.WifiTester2;
import com.jia.ezcamera.utils.wifi.lan.wifitool.wifiInfo;
import com.jia.znjj2.CameraActivity;
import com.jia.znjj2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2DCallbackListener;
import vv.tool.gsonclass.c2s_searchDev;

public class ActivityAdd extends Activity {
   
    private EditText cam_name;
    private EditText dev_id1;
    private EditText view_pwd;
    private Button btn_advance;
    private MainApplication app;
    public static final int GET_WIFI_LIST = 1;
    public static final int GET_AP_LIST = GET_WIFI_LIST + 1;
    public static final int CHEK_WIFI_CONNECTOR = GET_AP_LIST + 1;
    public static final int TIME_UP=CHEK_WIFI_CONNECTOR+1;
    public static final int SET_AP=TIME_UP+1;
    public static final int FINISH=SET_AP+1;
    public static final int ADD_DEVICE=FINISH+1;    private Button btn_search;
	private Button btn_cancel;
	private Button btn_save;
	private Button btn_scan_qrcode;

	PpviewClientInterface ppviewClient = PpviewClientInterface.getInstance();
	private Activity mContext = this;
	private String devId,devPass,camName;

	public static final int SEARCH_ADD= ADD_DEVICE +1;



    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
	    	setContentView(R.layout.setting);
	    	initView();
	    	app = (MainApplication)getApplication();
	    	myHandler = new Handler() {
	    		@Override
	    		public void handleMessage(Message msg) {
	    			// TODO Auto-generated method stub
	    			super.handleMessage(msg);
	    			switch (msg.what) {
	    			case GET_AP_LIST:
	    				ProgressDialogUtil.getInstance().cancleDialog();
	                	HashMap<String, wifiInfo> map = (HashMap<String, wifiInfo>) msg.obj;
	                	Intent apIntent = new Intent(mContext, AddDevicesApActivity.class);
	                	apIntent.putExtra("apmap", map);
	                	mContext.startActivity(apIntent);
	                    break;
	    			case CHEK_WIFI_CONNECTOR:
	    				ProgressDialogUtil.getInstance().cancleDialog();
	    				int wifiResult=msg.arg1;
	                    if (wifiResult== StaticConstant.RESULT_SUCESS){
							jumpToSetApActivityEx();
	                    }else{
	                        ToastUtils.show(mContext,R.string.devap_connect_fail);
	                    }
	                    break;
	    			case TIME_UP:
	                    ToastUtils.showShort(mContext, mContext.getResources()
                                .getString(R.string.actv_add_dev_searched_none));
	                    break;
	    			case SET_AP:
	                	 myWifiInfo = (wifiInfo) msg.obj;
	                	 doConnectorWifi(myWifiInfo.wifi_pass);
	                	break;
	                case FINISH:
	                    finish();
	                case ADD_DEVICE:
	                	String devId = (String)msg.obj;
	                	if(devId!=null&&!TextUtils.isEmpty(devId)){
	                	String camName = devId.split("-")[0];
	                	cam_name.setText(camName);
	                	dev_id1.setText(devId);
	                	view_pwd.setText("123456");
	                	}

	                	
	                	
	                    break;
	                case SEARCH_ADD:
	                	doSearch();
	                	break;
					default:
						break;
					}
	    		}
	    	};
		}
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	releaseConnector();
    	cancleCountTimer();
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	ppviewClient.setOnC2DCallback(onC2DCallbackListener);
    }
    
    private void initView(){
    	cam_name = (EditText)findViewById(R.id.cam_name);
    	dev_id1 = (EditText)findViewById(R.id.dev_id1);
    	view_pwd = (EditText)findViewById(R.id.view_pwd);
    	btn_advance = (Button)findViewById(R.id.btn_advance);
    	btn_search = (Button)findViewById(R.id.btn_search);
    	btn_cancel = (Button)findViewById(R.id.btn_cancel);
    	btn_save = (Button)findViewById(R.id.btn_save);
    	btn_scan_qrcode = (Button)findViewById(R.id.btn_scan_qrcode);
    	

    	btn_advance.setText(R.string.ap_mode);
    	btn_scan_qrcode.setText(R.string.qrcode_add);
    	btn_advance.setOnClickListener(onClickListener);
    	btn_search.setOnClickListener(onClickListener);
    	btn_cancel.setOnClickListener(onClickListener);
    	btn_save.setOnClickListener(onClickListener);
    	btn_scan_qrcode.setOnClickListener(onClickListener);


    }
    
    
    OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_search:
				doSearch();
				break;
			case R.id.btn_cancel:
				finish();
				break;
			case R.id.btn_save:
				addDevice();
				break;
			case R.id.btn_advance:
				doSearchDevAp();
				break;
			case R.id.btn_scan_qrcode:
				//二维码配置wifi
				Intent intent = new Intent(mContext, SetupQrcordActivity.class);
				startActivity(intent);
				break;

				
			default:
				break;
			}
		}
	};

	
	private void addDevice(){
		camName = cam_name.getText().toString().trim();
		devId = dev_id1.getText().toString().trim();
		devPass = view_pwd.getText().toString().trim();
		
		if(camName.length()<=0){
			Alert.showAlert(mContext,
					getText(R.string.tips), 
					getText(R.string.tips_input_camname), 
					getText(R.string.btn_ok));
			return;
		}
		
		if(devId.length()<=0){
			Alert.showAlert(mContext, 
					getText(R.string.tips), 
					getText(R.string.tips_input_devid),
					getText(R.string.btn_ok));
			return;
		}
		
		if(devPass.length()<=0){
			Alert.showAlert(mContext, 
					getText(R.string.tips), 
					getText(R.string.input_pwd),
					getText(R.string.btn_ok));
			return;
		}
		createConnector(devId, devPass);
		
	}
	
	
	
	
	/**
     * 搜索设备
     */
	
	enum SearchDeviceType{
        DEVICE,DEVICE_AP
    }

    private SearchDeviceType mSearchDeviceType= SearchDeviceType.DEVICE;
    
    ArrayList<c2s_searchDev.Dev> myList;
    
     synchronized private void doSearch() {
    	 mSearchDeviceType= SearchDeviceType.DEVICE;
    	 ProgressDialogUtil.getInstance().showDialog(
                 mContext,
                 mContext.getResources().getString(R.string.start_search), true);
    	 ppviewClient.vv_startsearch(mContext);
    }
     
     
     private ArrayList<c2s_searchDev.Dev> getJsonList(String json) {
         c2s_searchDev sd = null;
         Gson g = new GsonBuilder().create();
         sd = g.fromJson(json, c2s_searchDev.class);
         if (sd == null) {
             return null;
         }
         ArrayList<c2s_searchDev.Dev> devArray = sd.devices;
         if (devArray != null) {
             return devArray;
         }
         return null;
     }
     
     
     OnC2DCallbackListener onC2DCallbackListener = new OnC2DCallbackListener(){

		@Override
		public void on_bind_ip(int arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void on_create_connect_ip(long arg0, String arg1, String arg2,
				Object arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void on_get_devinfo_callback(final int nResuylt,final String sess,final String devinfo) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					releaseConnector();
					ProgressDialogUtil.getInstance().cancleDialog();
					if(nResuylt==200){
						Gson gson = new GsonBuilder().create();
						GetDevInfoBean getDevInfoBean = gson.fromJson(devinfo, GetDevInfoBean.class);
						getDevInfoBean.local_name = camName;
						getDevInfoBean.devpass = devPass;
						if(app.addVRDevice(getDevInfoBean)==0){
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
						
					}else if(nResuylt==203||nResuylt==414){
						Alert.showAlert(mContext, 
		     					getText(R.string.tips), 
		     					getText(R.string.info_connect_wrong_pwd), 
		     					getText(R.string.btn_ok));
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
		public void on_vv_search_dev_callback(final String arg0) {
			// TODO Auto-generated method stub
			//搜索到设备
			ProgressDialogUtil.getInstance().cancleDialog();
			ppviewClient.vv_stopsearch();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String mJson = arg0;
                    if (TextUtils.isEmpty(mJson)) {
                        ToastUtils.showShort(mContext, R.string.search_fail);
                    } else {
                        myList = getJsonList(mJson);
                        if (myList != null) {
                            int lSize = myList.size();
                            if (lSize > 0) {
                                if (mSearchDeviceType== SearchDeviceType.DEVICE) {
                                	//设备列表
                                	String[] stringName = new String[lSize];
                                	for(int i=0;i<lSize;i++){
                                		stringName[i] = myList.get(i).dev_id+"\nIP:"+myList.get(i).ip;
                                	}
                                	new AlertDialog.Builder(mContext)  
                        			.setTitle("选择设备")
                        			.setItems(stringName, new DialogInterface.OnClickListener() {
                        				
                        				@Override
                        				public void onClick(DialogInterface dialog, int which) {
                        					// TODO Auto-generated method stub
                        					if(myHandler!=null){
                        						Message msg = new Message();
                        						msg.what = ADD_DEVICE;
                        						msg.obj = myList.get(which).dev_id;
                        						myHandler.sendMessage(msg);
                        					}
                        					//dev_id1.setText(myList.get(which).dev_id);
                        				}
                        			})  
                        			.show();  
                                }else if(mSearchDeviceType== SearchDeviceType.DEVICE_AP){
                                	jumpToSetApActivity(myList.get(0).dev_id,myList.get(0).ip);

                                }
                            } else {
                                ToastUtils.showShort(mContext,R.string.search_fail);
                            }
                        }
                    }
				}
			});
		}
    	 
     };
     
     
     //***********链接相关
     private long devConnect = 0;
     /**
      * 创建connect
      */
     private void createConnector(String devid,String devpass){
         if(devConnect==0) {
             ppviewClient.setOndevConnectCallback(setDevConnectListener);
             ProgressDialogUtil.getInstance().showDialog(
                     mContext,
                     mContext.getResources().getString(R.string.start_connect), true);
             devConnect = ppviewClient.createConnect(devid, "admin",
            		 devpass);
         }
     }

     /**释放connect
      */
     private void releaseConnector() {
         if (devConnect != 0) {
        	 ppviewClient.releaseConnect(devConnect);
        	 devConnect = 0;
             ppviewClient.removeOndevConnectCallback(setDevConnectListener);
         }
     }




     PpviewClientInterface.OnDevConnectCallbackListener setDevConnectListener = new PpviewClientInterface.OnDevConnectCallbackListener() {
         @Override
         /**
          * 连接设备返回的回调
          * @param msgid
          *			设备交互编码，256为连接设备，其它的则不是
          * @param connector
          *        连接句柄
          * @param result
          * 连接成功：1
         p2p连接超时：0
         p2p未初始化：-1
         p2p连接失败:-2
         连接中断:-3
         设备不在线:-4
          */
         public void on_connect_callback(int msgid, final long connector, final int result) {
        	 ProgressDialogUtil.getInstance().cancleDialog();
        	 runOnUiThread(new Runnable() {
				public void run() {
					if (devConnect == connector) {
		                 if(result!=1){
		                	 Alert.showAlert(mContext, 
		         					getText(R.string.tips), 
		         					getText(R.string.connect_fail), 
		         					getText(R.string.btn_ok));
		                     releaseConnector();
		                 }else{
		                     //获取设备信息
		                	 ProgressDialogUtil.getInstance().showDialog(
		                             mContext,
		                             mContext.getResources().getString(R.string.start_getdevinfo), true);
		                     ppviewClient.c2d_get_devinfo_fun(devConnect, "admin", devPass, "getdevinfo", 0);
		                 }
		             }
				}
			});
             
         }
     };
     
     
     
     
     
     
     
     
     /**
      * 搜索设备ap
      */
     WifiTester2 wifiTester = null;
     private wifiInfo myWifiInfo = null;//连接的wifi信息
     private wifiInfo myConWifiInfo=null;//手机之前连接的wifi
     public static Handler myHandler;
     private void doSearchDevAp() {
         if (wifiTester == null) {
             wifiTester = new WifiTester2(mContext);
             wifiTester.setWifiCallback(searchWifiListCallback);
         }
         mSearchDeviceType= SearchDeviceType.DEVICE_AP;
         ProgressDialogUtil.getInstance().showDialog(mContext,mContext.getResources().getString(R.string.set_network),false);
         wifiTester.scanWifi(SearchType.AP);
     }
 	
 	 
 	 /**
      * 连接设备ap
      * @param wifiPass
      */
     private void doConnectorWifi(String wifiPass){
         wifiTester.doConnectWifi(myWifiInfo, wifiPass);
         startCheckWifi();
     }
     
     /**
      * 检查wifi是否连接线程
      */
     Thread myThread = null;
     int m_count = 0;
     static boolean ifChecking = false;

     synchronized private void startCheckWifi() {
         if (myThread == null) {
             ProgressDialogUtil.getInstance().showDialog(mContext,mContext.getResources().getString(R.string.connect_devap),false);
             ifChecking = true;
             myThread = new Thread(new Runnable() {
                 @Override
                 public void run() {
                     while (ifChecking) {
                         try {
                             Thread.sleep(3000);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                         boolean ifc =wifiTester.isWifiConnected();
                         if (ifc || m_count > 6) {
                             stopCheckWifi();
                             Message message=Message.obtain();
                             message.what=CHEK_WIFI_CONNECTOR;
                             if (wifiTester.isWifiConnected()){
                            	 if(myWifiInfo.ssid.equals(wifiTester.getMyWifiSsid())){
                            		 message.arg1= StaticConstant.RESULT_SUCESS;
                            	 }
                            	 else{
                            		 message.arg1=0;
                            	 }
                             }else{
                                 message.arg1=0;
                             }
                             myHandler.sendMessage(message);
                         }
                         m_count = m_count + 1;
                     }
                 }
             });
             myThread.start();
         }
     }

     private void stopCheckWifi() {
         ifChecking = false;
         m_count = 0;
         myThread = null;
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
             } else if (searchType == SearchType.AP) {
                 message.what = GET_AP_LIST;
             }
             message.obj = wifiMap;
             myHandler.sendMessage(message);
         }

         @Override
         public void getMyConnectorWifi(wifiInfo myWifi) {
             if (myConWifiInfo==null){
                 Log.e("DEBUG","getMyConnectorWifi    myWifi="+myWifi.toString());
                 myConWifiInfo=myWifi;
             }
         }
     };
     
     
     /**
      * 连上设备ap后搜索设备，若30秒内没有搜索到则为搜索失败
      */
     Timer searchTimer = null;

     private void startSearchTimer() {
         if (searchTimer != null) {
             return;
         }
         searchTimer = new Timer();
         searchTimer.schedule(new TimerTask() {
             public void run() {
                 searchTimer = null;
                 ppviewClient.vv_startsearch(mContext);
             }
         }, 3000);
     }

     private void cancleSearchTimer() {
         if (searchTimer != null) {
             searchTimer.cancel();
             searchTimer =null;
         }
     }

     Timer countTimer = null;

     private void startCountTimer() {
         if (countTimer != null) {
             return;
         }
         countTimer = new Timer();
         countTimer.schedule(new TimerTask() {

             @Override
             public void run() {
                 myHandler.sendEmptyMessage(TIME_UP);
                 cancleSearchTimer();
                 countTimer=null;
             }
         }, 30000);
     }

     private void cancleCountTimer() {
         if (countTimer != null) {
             countTimer.cancel();
             countTimer=null;
         }
     }
     private void jumpToSetApActivityEx(){
 		if (myWifiInfo!=null) {
 			Intent intent = new Intent();
 			intent.setClass(mContext, SetApActivityEx.class);
 			intent.putExtra(StaticConstant.ITEM, myWifiInfo);
 			intent.putExtra(StaticConstant.CONNECTOR_WIFI_INFO,myConWifiInfo);
 			mContext.startActivity(intent);
 			//myContext.startActivityForResult(intent,StaticConstant.ACTIVITY_ADD_TO_SETAP);
 		}
 	}
     private void jumpToSetApActivity(String devid,String devip){
         /*ProgressDialogUtil.getInstance().cancleDialog();
         cancleSearchTimer();
         cancleCountTimer();
         if (myWifiInfo!=null) {
             Intent intent = new Intent();
             intent.setClass(mContext, SetApActivity.class);
             intent.putExtra(StaticConstant.DEVICE_ID, devid);
             intent.putExtra(StaticConstant.DEVICE_IP, devip);
             intent.putExtra(StaticConstant.ITEM, myWifiInfo);
             intent.putExtra(StaticConstant.CONNECTOR_WIFI_INFO,myConWifiInfo);

             mContext.startActivity(intent);
             //mContext.startActivityForResult(intent,StaticConstant.ACTIVITY_ADD_TO_SETAP);
         }*/
     }
        
}