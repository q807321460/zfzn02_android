package com.jia.ezcamera.set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.utils.BottomDialog;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.SensorArray;
import com.jia.ezcamera.utils.SensorType;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.ezcamera.utils.sensorItem;
import com.jia.znjj2.R;

import java.util.ArrayList;

import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dSensorCallback;
import vv.tool.gsonclass.item_sensorinfo;

public class CameraSensorEditActivity extends Activity implements OnClickListener {
	private static final String TAG = CameraSensorEditActivity.class.getSimpleName();
	private Context myContext;
	private sensorItem mitem = null;
	PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();

	private TextView etSensorName;
	private ImageView cbAlarm;
	private MainApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_cam_afs_sensor_set);
		app = (MainApplication)getApplication();
		myContext = this;
		Intent intent = getIntent();
		if (intent != null) {
			mitem = (sensorItem) intent.getSerializableExtra("item");
		}
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		onvif_c2s.setOnC2dSensorCallback(sensorCallback);
	}

	private void init() {
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.afs_set_sensor_name).setOnClickListener(this);
		findViewById(R.id.afs_sensor_del).setOnClickListener(this);
		etSensorName = (TextView) findViewById(R.id.afs_set_sensor_name_text);
		cbAlarm = (ImageView) findViewById(R.id.afs_sensor_isalarm);
		updateView();
		cbAlarm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mitem.isAlarm==1){
					mitem.isAlarm=0;
				}else{
					mitem.isAlarm=1;
				}
				doSetInfo();
			}
		});
	}

	private void updateView() {
		if (mitem != null) {
			((TextView) findViewById(R.id.afs_set_sensor_id_text))
					.setText(mitem.sensorId);
			((TextView) findViewById(R.id.afs_set_sensor_type_text))
					.setText(SensorType.getTypeString(mitem.sensorType,
							myContext));
			etSensorName.setText(mitem.sensorName);
			
			if (mitem.isAlarm == 1) {
				cbAlarm.setImageResource(R.drawable.switch_on);
			} else {
				cbAlarm.setImageResource(R.drawable.switch_off);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.afs_set_sensor_name:
			//修改传感器名称
			doRename();
			break;
		case R.id.afs_sensor_del:
			//删除传感器
			delSensor();
			break;
		
		default:
			break;
		}
	}
	
	private void doRename(){
		Intent intent = new Intent(myContext, CameraSensorRenameActivity.class);
		 intent.putExtra("item", mitem);
		 myContext.startActivity(intent);
	}
	
	BottomDialog delDialog;
	private void delSensor(){
		delDialog = new BottomDialog(myContext, getString(R.string.afs_sensor_del_hint), 
				getString(R.string.afs_sensor_del), 
				getString(R.string.cancel), 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ProgressDialogUtil.getInstance().showDialog(
                                myContext,myContext.getResources().getString(R.string.afs_sensor_deling),
                                false);
						onvif_c2s.c2d_extSensorRemove_fun(app.SetCamConnector, mitem.chlId,mitem.sensorId);
						delDialog.dismiss();
					}
				}, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						delDialog.dismiss();
					}
				});
		delDialog.show();
	}

	private void doSetInfo() {
		if (app.checkSetCamConnect()) {
			ProgressDialogUtil.getInstance().showDialog(
					(Activity) myContext,
					myContext.getResources().getString(
							R.string.afs_sensor_editing), false);
			item_sensorinfo item = new item_sensorinfo();
			item.index = mitem.index;
			item.chl_id = mitem.chlId;
			item.name = mitem.sensorName;
			item.type = mitem.sensorType;
			item.sensor_id = mitem.sensorId;
			item.preset = mitem.preset;
//			item.is_main = mitem.isMain;
			item.is_alarm = mitem.isAlarm;
			onvif_c2s.c2d_extSensorSetInfo_fun(app.SetCamConnector, item);
		}
	}

	// -----回调
	OnC2dSensorCallback sensorCallback = new OnC2dSensorCallback() {

		@Override
		public void on_c2d_extSensorMatch(int arg0,
				ArrayList<item_sensorinfo> arg1) {
			Log.i(TAG, "on_c2d_extSensorMatch    result=" + arg0 + "   arg1="+ arg1);
		}

		@Override
		public void on_c2d_extSensorGetList(int arg0,
				ArrayList<item_sensorinfo> arg1) {
			Log.i(TAG, "on_c2d_extSensorGetList     result=" + arg0+ "   arg1=" + arg1);
		}

		@Override
		public void on_c2d_extSensorRemove(final int result,final int chlId,
				String sebsorId) {
			Log.i(TAG, "on_c2d_extSensorRemove    result=" + result+ "   chlId=" + chlId + "    sebsorId=" + sebsorId);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProgressDialogUtil.getInstance().cancleDialog();
					if(result==200){
						finish();
					}else{
						ToastUtils.show(myContext, getString(R.string.afs_sensor_delfaild));
					}
				}
			});
		}

		@Override
		public void on_c2d_extSensorSetInfo(final int result,final item_sensorinfo item) {
			Log.i(TAG, "on_c2d_extSensorSetInfo    result=" + result + "   arg1="
					+ item.name);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProgressDialogUtil.getInstance().cancleDialog();
					if (result == 200) {
						if (item != null
								&& SensorArray.getInstance().updateItem(item.index,
										item.chl_id, item.name, item.type,
										item.sensor_id, item.preset, 
										item.is_alarm, item.status,item.low_power,item.sub_chl_count,item.sub_chls)
								&& CameraSensorActivity.myHandler != null) {
							mitem.sensorName = item.name;
							updateView();
							CameraSensorActivity.myHandler
									.sendEmptyMessage(CameraSensorActivity.UPDATE);
						}
					} else {
						ToastUtils.show(myContext, getString(R.string.afs_sensor_edit_faild));
					}
				}
			});
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

	
}
