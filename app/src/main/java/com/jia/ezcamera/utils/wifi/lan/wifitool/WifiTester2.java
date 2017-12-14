package com.jia.ezcamera.utils.wifi.lan.wifitool;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/*
 *信号强度是负数 wifiinfo.getRssi()这个方法决定的
 *得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，
 *-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线。 
 */
public class WifiTester2 {
	private static final String TAG = WifiTester2.class.getSimpleName();

	/** 定义WifiManager对象 */
	private WifiManager wifiManager;

	/** 扫描完毕接收器 */
	private WifiReceiver receiverWifi;

	/** 扫描出的网络连接列表 */
	private List<ScanResult> wifiList;

	private ProgressDialog dialog;

	private Context mContext;

	private WifiInfo myWifi;


    private SearchType mySearchType;//搜索的类型wifi或者ap

    private wifiInfo myConnectorWifi;

	public WifiTester2(Context context) {
		this.mContext = context;
		init();
	}

	private void init() {
		wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		registerReceiver();
	}

    /**
     * 获取手机正在使用的wifi ssid
     * @return
     */
	public String getMyWifiSsid() {
		if (ifOpenWlan()) {
			myWifi = wifiManager.getConnectionInfo();
			scanWifi2();
			String iw_ssid = myWifi.getSSID();
			if(iw_ssid == null){
				return "";
			}
			Log.i(TAG, "getMyWifi    myWifi=" + myWifi.toString());
			if (iw_ssid.startsWith("\"") && iw_ssid.endsWith("\"")) {
				iw_ssid = iw_ssid.substring(1, iw_ssid.length() - 1);
			}
			return iw_ssid;
		}
		return "";
	}

    /**
     * 判断wifi是否打开
     * @return
     */
	public boolean ifOpenWlan() {
		return wifiManager ==null?false: wifiManager.isWifiEnabled();
	}

    /**
     * 判断wifi是否连接上
     * @return
     */
    public boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isConnected();
        }
        return false;
    }

	public static boolean ifMySearch = false;

	/**
	 * 扫描WIFI 加载进度条
	 */
	public void scanWifi(SearchType searchType) {
		ifMySearch = true;
        mySearchType=searchType;
        registerReceiver();
		OpenWifi();
		wifiManager.startScan();
//		String strMessage = mContext.getResources().getString(
//				R.string.doing_search_wifi);
//		if (mySearchType==SearchType.AP) {
//			strMessage = mContext.getResources().getString(
//					R.string.doing_search_ap);
//		}
//		dialog = ProgressDialog.show(mContext, "", strMessage);
//		dialog.setCancelable(false);
	}

	private void scanWifi2() {
		OpenWifi();
		wifiManager.startScan();
	}

	/**
	 * 打开WIFI
	 */
	public void OpenWifi() {
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * 关闭WIFI
	 */
	public void CloseWifi() {
		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
	}

    /**
     * 注册广播
     */
    public void registerReceiver(){
        if (receiverWifi == null) {
            receiverWifi = new WifiReceiver();
            mContext.registerReceiver(receiverWifi, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));// 注册广播
        }
    }

    /**
     * 注销广播
     */
	public void unRegister() {
		if (receiverWifi != null) {
			mContext.unregisterReceiver(receiverWifi);// 注销广播
			receiverWifi = null;
		}
	}

	Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			unRegister();
		};
	};

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				if (wifiList != null) {
					wifiList.clear();
				}
				wifiList = wifiManager.getScanResults();
				createHashset(wifiList);
				m_handler.sendEmptyMessage(0);
				// Toast.makeText(context, "扫描完毕", Toast.LENGTH_LONG).show();
				// Intent in = new Intent();
				// in.setClass(WifiTester2.this, ListOk.class);
				// WifiTester2.this.startActivity(in);
			}

		}
	}

	public List<ScanResult> getWifiList() {
		return wifiList;
	}

	public WifiManager getWifiManager() {
		return wifiManager;
	}

//	public void setWifiManager(WifiManager wifiManager) {
//		this.wifiManager = wifiManager;
//	}

    /**
     * wifi和设备ap分开返回
     */
	private HashMap<String, wifiInfo> wifiMap = null;

    private void initWifiMap(){
        if (wifiMap == null) {
            wifiMap = new HashMap<String, wifiInfo>();
        } else {
            wifiMap.clear();
        }
    }

	private void createHashset(List<ScanResult> wifiList) {
		initWifiMap();
		for (ScanResult scanResult:wifiList) {
            Log.e(TAG, "createHashset     scanResult=" + scanResult.capabilities);
			wifiInfo iw = getIw(scanResult.SSID, scanResult.BSSID, scanResult.capabilities);
            getMyConnectorWifi(iw);
			if (mySearchType == SearchType.AP) {
//                Log.i(TAG, "createHashset     aSize=" + aSize);
//				for (int j = 0; j < aSize; j++) {
////                    Log.i(TAG,"createHashset    scanResult.SSID="+scanResult.SSID+"    adh.getDevapIndex(j)="+adh.getDevapIndex(j));
//					if (scanResult.SSID.startsWith(adh.getDevapIndex(j))) {
//						iw.wifi_pass = adh.getAppassIndex(j);
//						wifiMap.put(scanResult.SSID, iw);
//						break;
//					}
//				}
				//目前所有AP密码默认为12345678
                iw.wifi_pass = "88888888";
				wifiMap.put(scanResult.SSID, iw);
			} else if (mySearchType== SearchType.WIFI) {
				wifiMap.put(scanResult.SSID, iw);
			}
		}
		if (ifMySearch) {
            if (searchWifiListCallback!=null){
                searchWifiListCallback.getWifiList(mySearchType, wifiMap);
            }
            ifMySearch=false;
		}
	}

    /**
     * 返回所有的wifi
     * @param wifiList
     */
    private void getAllWifiInfo(List<ScanResult> wifiList){
        initWifiMap();
        for (ScanResult scanResult:wifiList) {
            wifiInfo iw = getIw(scanResult.SSID, scanResult.BSSID, scanResult.capabilities);
                wifiMap.put(scanResult.SSID, iw);
        }
        if (ifMySearch) {
            if (searchWifiListCallback!=null){
                searchWifiListCallback.getWifiList(mySearchType, wifiMap);
            }
            ifMySearch=false;
        }
    }

    /**
     * 返回当点连接的wifi信息
     * @param wifiinfo
     */
    private void getMyConnectorWifi(wifiInfo wifiinfo){
    	
        if (wifiinfo!=null){
			Log.e(TAG, "wifiInfowifiInfo  "+wifiinfo.ssid+"   secu_algo:"+wifiinfo.secu_algo);

            if (TextUtils.equals(getMyWifiSsid(),wifiinfo.ssid)&&searchWifiListCallback!=null){
            	Log.e(TAG, "getMyConnectorWifi  "+wifiinfo.ssid+"   secu_algo:"+wifiinfo.secu_algo);
            	searchWifiListCallback.getMyConnectorWifi(wifiinfo);
            }
        }
    }

	private wifiInfo getIw(String ssid, String bssid, String capab) {
		wifiInfo iw = new wifiInfo();
		iw.ssid = ssid;
		iw.bssid = bssid;
		iw.capabilitie = capab;
		iw.secu_algo = 0;
//        Log.i(TAG,"getIw     ssid="+ssid+"    bssid="+bssid+"    capa="+capab);
		String strwifi="";
		if (!TextUtils.isEmpty(capab)) {
			Pattern pattern = Pattern.compile("\\[([^\\[\\]]+)\\]");
			Matcher matcher = pattern.matcher(capab);
			while (matcher.find()) {
				strwifi = matcher.group(1);
				break;
			}
			if (!TextUtils.isEmpty(strwifi)) {
				if (strwifi.trim().equals("WEP")) {
                    iw.secu_mode = 1;
                    iw.secu_algo = 0;
				} else {
					String[] strWifi = strwifi.split("-");
					if (strWifi.length > 1) {
						if (strWifi[1].trim().equals("PSK")) {
							iw.secu_mode = 2;
						}
						if(strWifi.length>2){
							if (strWifi[2].trim().equals("CCMP")) {
								iw.secu_algo = 0;
							} else if (strWifi[2].trim().equals("TKIP")) {
								iw.secu_algo = 1;
							} else if (strWifi[2].trim().equals("TKIP+CCMP")||strWifi[2].trim().equals("CCMP+TKIP")) {
								iw.secu_algo = 2;
							}
						}
					}else{
                        iw.secu_mode =0;
                        iw.secu_algo = 0;
                    }
				}
			}
		} else {
			iw.secu_mode =0;
			iw.secu_algo = 0;
		}
		return iw;
	}

    private boolean doConnectorWifi(String wifiPass){
        OpenWifi();
        WifiType m_type = WifiType.WIFICIPHER_INVALID;
        if (myConnectorWifi != null) {
            if (TextUtils.isEmpty(myConnectorWifi.capabilitie)) {
                m_type =WifiType.WIFICIPHER_NOPASS;
            } else {
                String strc = myConnectorWifi.capabilitie;
                String strwifi = "";
                if (!TextUtils.isEmpty(strc)) {
                    Pattern pattern = Pattern.compile("\\[([^\\[\\]]+)\\]");
                    Matcher matcher = pattern.matcher(strc);
                    while (matcher.find()) {
                        strwifi = matcher.group(1);
                        break;
                    }
                    if (strwifi != null) {
                        Log.i(TAG, "doConnectorWifi    strwifi=" + strwifi);
                        if (strwifi.trim().contains("WEP")) {
                            m_type = WifiType.WIFICIPHER_WEP;
                        } else if(strwifi.trim().contains("WPA")){
                            m_type = WifiType.WIFICIPHER_WPA;
                        }else{
                            m_type=WifiType.WIFICIPHER_NOPASS;
                        }
                    }
                }
            }
        }
        Log.e(TAG, "myConnectorWifi.ssid  " + myConnectorWifi.ssid);
        Log.e(TAG, "myConnectorWifi.wifiPass  " + wifiPass);
        Log.e(TAG, "myConnectorWifi.m_type  " + m_type);
        return connectWifi(myConnectorWifi.ssid,wifiPass,m_type);
    }

    private boolean connectWifi(String ssid,String wifiPass,WifiType m_type){
        Log.e(TAG,"connectWifi       ssid="+ssid+"    wifiPass="+wifiPass+"     m_type="+m_type);
        WifiConnect wc = new WifiConnect(wifiManager);
        boolean bc=wc.Connect(ssid, wifiPass, m_type);
        Log.e(TAG, "connectWifi     bc=" + bc);
        return bc;
    }

    /**
     * 连接wifi
     * @param iw
     * @param wifiPass
     * @return
     */
    public boolean doConnectWifi(wifiInfo iw,String wifiPass){
        if (iw!=null) {
            this.myConnectorWifi = iw;
            return doConnectorWifi(wifiPass);
        }
        return false;
    }

    /**
     * 回调
     */
    private SearchWifiListCallback searchWifiListCallback=null;

    public void setWifiCallback(SearchWifiListCallback cb){
        searchWifiListCallback=cb;
    }

    public interface SearchWifiListCallback{
        public void getWifiList(SearchType searchType, HashMap<String, wifiInfo> wifiMap);
        public void getMyConnectorWifi(wifiInfo myWifi);
    }

}