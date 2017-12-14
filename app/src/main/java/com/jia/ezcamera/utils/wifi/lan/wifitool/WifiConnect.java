package com.jia.ezcamera.utils.wifi.lan.wifitool;


import java.util.List;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiConnect {
    private static final String TAG=WifiConnect.class.getSimpleName();
	WifiManager wifiManager;



	// 构造函数
	public WifiConnect(WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}

	// 打开wifi功能
	private boolean OpenWifi() {
		boolean bRet = true;
		if (!wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	// 提供一个外部接口，传入要连接的无线网
	public boolean Connect(String SSID, String Password, WifiType Type) {
		Log.e(TAG, "Connect     ssid=" + SSID + "    password="
				+ Password + "     type=" + Type);
		if (!this.OpenWifi()) {
			Log.e("DEBUG", "wifiConfig NOTOPEN");
			return false;
		}
		while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
//            wifi的消息一共有五种：
//            WifiManager.WIFI_STATE_DISABLED: //wifi不可用
//            WifiManager.WIFI_STATE_DISABLING://wifi 正在关闭或者断开
//            WifiManager.WIFI_STATE_ENABLED://wifi可用
//            WifiManager.WIFI_STATE_ENABLING://wifi正在打开或者连接
//            WifiManager.WIFI_STATE_UNKNOWN://未知消息
			try {
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}


		WifiConfiguration wifiConfig = this
				.CreateWifiInfo(SSID, Password, Type);
		if (wifiConfig == null) {
			Log.e("DEBUG", "wifiConfig NULL");
			return false;
		}
		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
//			Log.e("DEBUG", "removeNetwork  "+tempConfig.SSID);
//			if(wifiManager.removeNetwork(tempConfig.networkId)){
//				Log.e("DEBUG", "removeNetwork  suc");
//			}else{
//				Log.e("DEBUG", "removeNetwork  faild");
//			}
			//发现指定WiFi，并且这个WiFi以前连接成功过
			wifiConfig = tempConfig;
			boolean b = wifiManager.enableNetwork(wifiConfig.networkId, true);
			return b;
		}
		int netID = wifiManager.addNetwork(wifiConfig);
		Log.e("DEBUG", "addNetwork  "+wifiConfig.SSID+"   netID "+netID);
        boolean bRet=wifiManager.enableNetwork(netID, true);
        
//        wifiManager.saveConfiguration();
//        boolean bRet=wifiManager.reconnect();
		return bRet;
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration CreateWifiInfo(String SSID, String Password,
			WifiType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// WifiConfiguration tempConfig = this.IsExsits(SSID);
		// if (tempConfig != null) {
		// wifiManager.removeNetwork(tempConfig.networkId);
		// }
		if (Type == WifiType.WIFICIPHER_NOPASS) {
			//config.wepKeys[0] = "";
			config.hiddenSSID = true;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			//config.wepTxKeyIndex = 0;
		}
		else if (Type == WifiType.WIFICIPHER_WEP) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		else if (Type == WifiType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		} else {
			return null;
		}
		return config;
	}

}
