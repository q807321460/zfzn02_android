package com.jia.ezcamera.set;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;



import vv.ppview.PpviewClientInterface;
import vv.tool.gsonclass.item_netif;
import vv.tool.gsonclass.item_wifi;
import vv.tool.gsonclass.item_wifi_info;
import vv.ppview.PpviewClientInterface.OnC2dSetNetifCallback;
import vv.ppview.PpviewClientInterface.OnC2dScanWifiCallback;

public class CameraNetOfWifiActivity extends Activity implements OnClickListener {
	private static final String TAG = CameraNetOfWifiActivity.class.getSimpleName();

	PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
	private ListView wifiList = null;
	private TextView cur_wifiSSID;
	private ImageView cur_wifiState;
	private ImageView cur_wifiSignal;
	private Timer refreshTimer = null;
	private Context mContext = this;
	private SetWifiAdapter adapter;
	private MainApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_cam_netofwifi);
		app = (MainApplication)getApplication();
		init();
		onvif_c2s.setOnC2DScanWifiCallback(onC2DScanWifiCallback);
		onvif_c2s.setOnC2dSetNetifCallback(onC2dSetNetifCallback);
		doGetWifiList(true);
	}


	private void init() {	
		findViewById(R.id.btn_return).setOnClickListener(this);
		cur_wifiSSID = (TextView)findViewById(R.id.net_wifi_curname);
		cur_wifiState = (ImageView)findViewById(R.id.net_wifi_curlock);
		cur_wifiSignal = (ImageView)findViewById(R.id.net_wifi_cursignal);
		
		cur_wifiState.setVisibility(View.GONE);
		cur_wifiSignal.setVisibility(View.GONE);
		cur_wifiSSID.setText(getString(R.string.net_wifi_noconnect));
		wifiList = (ListView) findViewById(R.id.net_wifi_list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 鑾峰彇WIFI鍒楄〃
	 */
	private void doGetWifiList(boolean isShowProgressDialog) {
		if(refreshTimer!=null){
			refreshTimer.cancel();
			refreshTimer = null;
		}
		if (app.SetCamConnector >0) {
			if(isShowProgressDialog){
				ProgressDialogUtil.getInstance().showDialog(
						mContext,
						mContext.getResources().getString(
								R.string.net_wifi_geting), true);
			}
			onvif_c2s.c2d_scanWifiList_fun(app.SetCamConnector);
		}
	}

	
	OnC2dScanWifiCallback onC2DScanWifiCallback = new OnC2dScanWifiCallback() {
		
		@Override
		public void on_c2d_scanWifiListCallBack(final int res, final item_wifi info) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProgressDialogUtil.getInstance().cancleDialog();
					Log.e("DEBUG", "on_c2d_scanWifiListCallBack  "+res);
					if(res==200&&info!=null&&info.wifi_list!=null){
						ArrayList<item_wifi_info> wifiInfo = new ArrayList<item_wifi_info>(info.wifi_list);
						Collections.sort(wifiInfo, new SortBySignel());
						for(int i = 0 ;i<wifiInfo.size();i++){
							Log.e("DEBUG", wifiInfo.get(i).ssid+"    "+wifiInfo.get(i).signel);
							if(wifiInfo.get(i).is_conn==1){
								item_wifi_info item = wifiInfo.remove(i);
								cur_wifiSSID.setText(item.ssid);
								if(item.is_secu==1){
									cur_wifiState.setVisibility(View.VISIBLE);
								}else{
									cur_wifiState.setVisibility(View.GONE);
								}
								int imageId=0;
								switch (item.signel/25) {
								case 0:
									imageId = R.drawable.png_wifi1;
									break;
								case 1:
									imageId = R.drawable.png_wifi2;
									break;
								case 2:
									imageId = R.drawable.png_wifi3;
									break;
								case 3:
									imageId = R.drawable.png_wifi4;
									break;

								default:
									imageId = R.drawable.png_wifi4;
									break;
								}
								cur_wifiSignal.setImageResource(imageId);
								cur_wifiSignal.setVisibility(View.VISIBLE);
								break;
							}
							
						}
						if(wifiInfo.size()==info.wifi_list.size()){
							cur_wifiSignal.setVisibility(View.INVISIBLE);
							cur_wifiSSID.setText(getString(R.string.net_wifi_noconnect));
							cur_wifiState.setVisibility(View.GONE);
						}
						adapter = new SetWifiAdapter(mContext, wifiInfo);
						wifiList.setAdapter(adapter);
						wifiList.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								item_wifi_info info = (item_wifi_info)adapter.getItem(position);
								if(info.is_secu==1){
									showPasswordDialog(info.ssid);
								}else{
									ProgressDialogUtil.getInstance().showDialog(
											mContext,
											mContext.getResources().getString(
													R.string.net_wifi_connecting));
									item_netif netif = new item_netif();
									netif.net_type =4;
									netif.ssid = info.ssid;
									onvif_c2s.c2d_setNetif_fun(app.SetCamConnector, netif);
								}
							}
						});
					}else{
						
					}
					//姣忛殧10绉掑埛鏂�
					if(refreshTimer==null){
						refreshTimer = new Timer();
						refreshTimer.schedule(new TimerTask() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								doGetWifiList(false);
							}
						}, 10000);
					}
				}
			});
			
		}
	};
	
	OnC2dSetNetifCallback onC2dSetNetifCallback = new OnC2dSetNetifCallback() {
		
		@Override
		public void on_c2d_setNetifCallBack(int res) {
			// TODO Auto-generated method stub
			ProgressDialogUtil.getInstance().cancleDialog();
			Log.e("DEBUG", "on_c2d_setNetifCallBack  ret"+res);
		}
	};
	
	
	class SortBySignel implements Comparator<item_wifi_info> {
		 public int compare(item_wifi_info o1, item_wifi_info o2) {
			 return o2.signel-o1.signel;
		 }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		onvif_c2s.setOnC2DScanWifiCallback(null);
		if(refreshTimer!=null){
			refreshTimer.cancel();
			refreshTimer = null;
		}
	}
	
	Dialog dlg = null;
	View view = null;
	EditText etInfo;
	private void showPasswordDialog(final String ssid){
		view = View.inflate(mContext, R.layout.dlg_rename, null);
		etInfo = (EditText) view.findViewById(R.id.dlg_rename_et);
		String titleString = getString(R.string.input_wifi_password)+"\n"+getString(R.string.input_wifi_connect)+ssid;
		((TextView) view.findViewById(R.id.dlg_rename_title)).setText(titleString);
		view.findViewById(R.id.dlg_rename_btn_ok).setOnClickListener(
		new OnClickListener() {
			@Override
			public void onClick(View v) {
				String strInfo = etInfo.getText().toString().trim();
				if (TextUtils.isEmpty(strInfo)) {
					ToastUtils.showShort(CameraNetOfWifiActivity.this, R.string.hint_pass_empty);
				} else {
					// 杈撳叆wifi瀵嗙爜
					ProgressDialogUtil.getInstance().showDialog(
							mContext,
							mContext.getResources().getString(
									R.string.net_wifi_connecting));
					item_netif netif = new item_netif();
					netif.net_type =4;
					netif.ssid = ssid;
					netif.secu_psk_pass  = strInfo;
					onvif_c2s.c2d_setNetif_fun(app.SetCamConnector, netif);
					cancleDialog();
				}
			}
		});
		view.findViewById(R.id.dlg_rename_btn_cancle).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						cancleDialog();
					}
				});
		dlg = new Dialog(mContext, R.style.style_dlg_groupnodes);
		dlg.setContentView(view);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setCancelable(false);
		dlg.show();
	}
	private void cancleDialog() {
		if (dlg != null) {
			dlg.cancel();
			dlg = null;
		}
	}
}
