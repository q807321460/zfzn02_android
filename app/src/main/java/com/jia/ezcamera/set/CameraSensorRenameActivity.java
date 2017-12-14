package com.jia.ezcamera.set;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.utils.sensorItem;
import com.jia.znjj2.R;

import vv.ppview.PpviewClientInterface;
import vv.tool.gsonclass.item_sensorinfo;


public class CameraSensorRenameActivity extends Activity {

    private ImageButton btn_back;
    private TextView btn_sure;
    private EditText et_name;
    private sensorItem mitem = null;
    private Context mContext = this;
    private MainApplication app;
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.btn_sure:
                    String strName = et_name.getText().toString().trim();
                    if (strName != null && !"".equals(strName)) {
                    	doSetName(strName);
                        finish();
                    }
                    break;
                default:
                    break;
            }

        }
    };
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        app = (MainApplication) getApplication();
        setContentView(R.layout.activity_set_cam_rename);
        TextView titleText = (TextView)findViewById(R.id.title);
        titleText.setText(R.string.afs_sensor_change_name);
        Intent intent = getIntent();
		if (intent != null) {
			mitem = (sensorItem) intent.getSerializableExtra("item");
		}
        init();

    }

    private void init() {
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        btn_sure = (TextView) findViewById(R.id.btn_sure);
        et_name = (EditText) findViewById(R.id.cam_rename_et);

        btn_back.setOnClickListener(onClickListener);
        btn_sure.setOnClickListener(onClickListener);
        et_name.setText("" + mitem.sensorName);
    }
    
    
    private void doSetName(String name) {
		if (app.checkSetCamConnect()) {
//			ProgressDialogUtil.getInstance().showDialog(
//					(Activity) mContext,
//					mContext.getResources().getString(
//							R.string.afs_sensor_editing), false);
			item_sensorinfo item = new item_sensorinfo();
			item.index = mitem.index;
			item.chl_id = mitem.chlId;
			item.name = name;
			item.type = mitem.sensorType;
			item.sensor_id = mitem.sensorId;
			item.preset = mitem.preset;
//			item.is_main = mitem.isMain;
			item.is_alarm = mitem.isAlarm;
			onvif_c2s.c2d_extSensorSetInfo_fun(app.SetCamConnector, item);
		}
	}
}
