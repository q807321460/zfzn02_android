package com.jia.ezcamera.utils.wifi.lan.wifitool;


public class item_wifi {
	public String ssid;
	public String bssid;
	public String capabilitie;
	public int secu_mode;// 0=无 1=WEP 2=WPA-PSK 3=Dot1x
	public int secu_algo;// 0=CCMP1=TKIP2=Any
	public String secu_psk_pass;
}
