package com.jia.znjj2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.connection.MasterSocket;
import com.jia.data.DataControl;
import com.jia.ir.db.ETDB;
import com.jia.ir.etclass.ETDevice;
import com.jia.ir.etclass.ETDeviceAIR;
import com.jia.ir.etclass.ETKey;
import com.jia.ir.face.IBack;
import com.jia.ir.global.ETGlobal;
import com.jia.util.Util;

import et.song.device.DeviceType;
import et.song.remote.face.IRKeyValue;
import et.song.tool.ETTool;

public class AirLearnActivity extends ElectricBase implements CompoundButton.OnCheckedChangeListener,View.OnLongClickListener,IBack, View.OnClickListener {
    private DataControl mDC;
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private EditText etElectricName;
    private ImageView iv_open;
    private ImageView iv_close;
    private Button bt_cold_16;
    private Button bt_cold_18;
    private Button bt_cold_20;
    private Button bt_cold_22;
    private Button bt_cold_24;
    private Button bt_cold_25;
    private Button bt_cold_26;
    private Button bt_cold_28;
    private Button bt_cold_30;
    private Button bt_hot_18;
    private Button bt_hot_20;
    private Button bt_hot_22;
    private Button bt_hot_24;
    private Button bt_hot_26;
    private Button bt_hot_28;


    CheckBox cb;
    private boolean mIsModity = false;
    private RecvReceiver mReceiver;
    private ETDeviceAIR mDevice = null;
    private int mLongKey = 0;
    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(AirLearnActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(AirLearnActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int key = mLongKey;
            try {
                if (key == 0)
                    return;
                byte[] keyValue = mDevice.GetKeyValue(key);
                if (keyValue == null)
                    return;
                ETGlobal.mTg.write(keyValue, keyValue.length);

            } catch (Exception e) {

            }
            handler.postDelayed(this, 300);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_learn);
        mDC=DataControl.getInstance();
        initView();

    }

    private void initView() {
        try{

            int _type = DeviceType.DEVICE_REMOTE_AIR;
            mDevice = (ETDeviceAIR) ETDevice.Builder(_type);
            mDevice.setmMasterCode(electric.getMasterCode());
            mDevice.setmElectricIndex(electric.getElectricIndex());
            mDevice.SetName(electric.getElectricName());
            mDevice.SetType(_type);
            mDevice.SetRes(7);
            mDevice.Load(ETDB.getInstance(this));
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tvTitleName = (TextView) findViewById(R.id.air1_activity_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.air1_activity_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.air1_activity_title_save);
        etElectricName = (EditText) findViewById(R.id.air1_name);
        iv_open= (ImageView) findViewById(R.id.air_learn_iv_open);
        iv_open.setOnClickListener(this);
        iv_open.setOnLongClickListener(this);
        iv_close= (ImageView) findViewById(R.id.air_learn_iv_close);
        iv_close.setOnClickListener(this);
        iv_close.setOnLongClickListener(this);
        cb = (CheckBox) findViewById(R.id.cb_check1);
        cb.setOnCheckedChangeListener(this);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        bt_cold_16= (Button) findViewById(R.id.button_cold_16);
        bt_cold_16.setOnLongClickListener(this);
        bt_cold_16.setOnClickListener(this);
        bt_cold_18= (Button) findViewById(R.id.button_cold_18);
        bt_cold_18.setOnLongClickListener(this);
        bt_cold_18.setOnClickListener(this);
        bt_cold_20= (Button) findViewById(R.id.button_cold_20);
        bt_cold_20.setOnLongClickListener(this);
        bt_cold_20.setOnClickListener(this);
        bt_cold_22= (Button) findViewById(R.id.button_cold_22);
        bt_cold_22.setOnLongClickListener(this);
        bt_cold_22.setOnClickListener(this);
        bt_cold_24= (Button) findViewById(R.id.button_cold_24);
        bt_cold_24.setOnLongClickListener(this);
        bt_cold_24.setOnClickListener(this);
        bt_cold_25= (Button) findViewById(R.id.button_cold_25);
        bt_cold_25.setOnLongClickListener(this);
        bt_cold_25.setOnClickListener(this);
        bt_cold_26= (Button) findViewById(R.id.button_cold_26);
        bt_cold_26.setOnLongClickListener(this);
        bt_cold_26.setOnClickListener(this);
        bt_cold_28= (Button) findViewById(R.id.button_cold_28);
        bt_cold_28.setOnLongClickListener(this);
        bt_cold_28.setOnClickListener(this);
        bt_cold_30= (Button) findViewById(R.id.button_cold_30);
        bt_cold_30.setOnLongClickListener(this);
        bt_cold_30.setOnClickListener(this);
        bt_hot_18= (Button) findViewById(R.id.button_hot_18);
        bt_hot_18.setOnLongClickListener(this);
        bt_hot_18.setOnClickListener(this);
        bt_hot_20= (Button) findViewById(R.id.button_hot_20);
        bt_hot_20.setOnLongClickListener(this);
        bt_hot_20.setOnClickListener(this);
        bt_hot_22= (Button) findViewById(R.id.button_hot_22);
        bt_hot_22.setOnLongClickListener(this);
        bt_hot_22.setOnClickListener(this);
        bt_hot_24= (Button) findViewById(R.id.button_hot_24);
        bt_hot_24.setOnLongClickListener(this);
        bt_hot_24.setOnClickListener(this);
        bt_hot_26= (Button) findViewById(R.id.button_hot_26);
        bt_hot_26.setOnLongClickListener(this);
        bt_hot_26.setOnClickListener(this);
        bt_hot_28= (Button) findViewById(R.id.button_hot_28);
        bt_hot_28.setOnLongClickListener(this);
        bt_hot_28.setOnClickListener(this);

    }
    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);

        initView();

    }
    @Override
    public void onStart() {
        super.onStart();
        mReceiver = new RecvReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ETGlobal.BROADCAST_PASS_LEARN);
        filter.addAction(ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN);
        filter.addAction(ETGlobal.BROADCAST_KEYCODE_VOLUME_UP);
        filter.addAction(ETGlobal.BROADCAST_APP_BACK);
        registerReceiver(mReceiver, filter);
    }
    public void air1ActivityEdit(View view){
        // 创建PopupMenu对象
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void air1ActivitySave(View view){
        final String electricName = etElectricName.getText().toString();
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.updateElectric(mDC.sMasterCode,electric.getElectricCode()
                        ,electric.getElectricIndex(),electricName,electric.getSceneIndex());
                Message msg = new Message();
                if(result.startsWith("1")){
                    msg.what = 0x1060;
                    electric.setElectricName(electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex(), electricName);
                }else {
                    msg.what = 0x1061;
                }
                handler2.sendMessage(msg);
            }
        }.start();
    }
    public void air1ActivityBack(View view){
        finish();
    }
    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void updateUI() {

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
public void onClick(View v){
    byte[] keyValue=null;
    int key = 0;
    switch (v.getId()){
        case R.id.air_learn_iv_open:
          key=mDC.KEY_AIR_POWER;
            break;
        case R.id.air_learn_iv_close:
            key=mDC.KEY_AIR_POWER_OFF;
            break;
        case R.id.button_cold_16:
            key=mDC.KEY_AIR_COLD_16;
            break;
        case R.id.button_cold_18:
            key=mDC.KEY_AIR_COLD_18;
            break;
        case R.id.button_cold_20:
            key=mDC.KEY_AIR_COLD_20;
            break;
        case R.id.button_cold_22:
            key=mDC.KEY_AIR_COLD_22;
            break;
        case R.id.button_cold_24:
            key=mDC.KEY_AIR_COLD_24;
            break;
        case R.id.button_cold_25:
            key=mDC.KEY_AIR_COLD_25;
            break;
        case R.id.button_cold_26:
            key=mDC.KEY_AIR_COLD_26;
            break;
        case R.id.button_cold_28:
            key=mDC.KEY_AIR_COLD_28;
            break;
        case R.id.button_cold_30:
            key=mDC.KEY_AIR_COLD_30;
            break;
        case R.id.button_hot_18:
            key=mDC.KEY_AIR_HOT_18;
            break;
        case R.id.button_hot_20:
            key=mDC.KEY_AIR_HOT_20;
            break;
        case R.id.button_hot_22:
            key=mDC.KEY_AIR_HOT_22;
            break;
        case R.id.button_hot_24:
            key=mDC.KEY_AIR_HOT_24;
            break;
        case R.id.button_hot_26:
            key=mDC.KEY_AIR_HOT_26;
            break;
        case R.id.button_hot_28:
            key=mDC.KEY_AIR_HOT_28;
            break;
    }
        ETKey k = mDevice.GetKeyByValue(key);
//    if (k.GetState() != ETKey.ETKEY_STATE_STUDY
//            && k.GetState() != ETKey.ETKEY_STATE_DIY) {
//        if (key != IRKeyValue.KEY_AIR_POWER && mDevice.GetPower() != 0x01) {
//            return;
//        }
//    }else if(k.GetState() == ETKey.ETKEY_STATE_STUDY){
        keyValue = k.GetValue();
    if(keyValue!=null){
        if(keyValue.length==1 ){
            sendOrder(keyValue);
        } else {
            sendOrder1(keyValue);

            return;
        }
    }else {
        Toast.makeText(AirLearnActivity.this,"请先对码，长按要学习的键，红外伴侣灯亮时，用遥控器调到对应状态",Toast.LENGTH_LONG).show();
    }


    boolean isSend = true;
//    try {
//        if (key == 0)
//            return;
//        keyValue = mDevice.GetKeyValue(key);
//        if(keyValue.length==1){
//            sendOrder(keyValue);}else {
//            sendOrder1(keyValue);
//        }
//        if (keyValue == null)
//            return;
//        if (ETGlobal.mTg == null) {
//            isSend = false;
//        }
//        int n = ETGlobal.mTg.write(keyValue, keyValue.length);
//        if (n < 0) {
//            isSend = false;
//        }
//
//    } catch (Exception e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//    }

}
    public boolean onLongClick(View v) {
        int key = 0;
        switch (v.getId()){
            case R.id.air_learn_iv_open:
                key=mDC.KEY_AIR_POWER;
                break;
            case R.id.air_learn_iv_close:
                key=mDC.KEY_AIR_POWER_OFF;
                break;
            case R.id.button_cold_16:
                key=mDC.KEY_AIR_COLD_16;
                break;
            case R.id.button_cold_18:
                key=mDC.KEY_AIR_COLD_18;
                break;
            case R.id.button_cold_20:
                key=mDC.KEY_AIR_COLD_20;
                break;
            case R.id.button_cold_22:
                key=mDC.KEY_AIR_COLD_22;
                break;
            case R.id.button_cold_24:
                key=mDC.KEY_AIR_COLD_24;
                break;
            case R.id.button_cold_25:
                key=mDC.KEY_AIR_COLD_25;
                break;
            case R.id.button_cold_26:
                key=mDC.KEY_AIR_COLD_26;
                break;
            case R.id.button_cold_28:
                key=mDC.KEY_AIR_COLD_28;
                break;
            case R.id.button_cold_30:
                key=mDC.KEY_AIR_COLD_30;
                break;
            case R.id.button_hot_18:
                key=mDC.KEY_AIR_HOT_18;
                break;
            case R.id.button_hot_20:
                key=mDC.KEY_AIR_HOT_20;
                break;
            case R.id.button_hot_22:
                key=mDC.KEY_AIR_HOT_22;
                break;
            case R.id.button_hot_24:
                key=mDC.KEY_AIR_HOT_24;
                break;
            case R.id.button_hot_26:
                key=mDC.KEY_AIR_HOT_26;
                break;
            case R.id.button_hot_28:
                key=mDC.KEY_AIR_HOT_28;
                break;
        }
        if (mIsModity) {
            final int k = key;
            Dialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setMessage(R.string.str_study_info_1)
                    .setPositiveButton(R.string.str_other_yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Intent intentStartLearn = new Intent(
                                            ETGlobal.BROADCAST_START_LEARN);
                                    intentStartLearn.putExtra("select", "0");
                                    intentStartLearn.putExtra("key", k);
                                    sendBroadcast(intentStartLearn);
                                    AirStudyAsyncTask task = new AirStudyAsyncTask();
                                    task.execute(0,k);
                                }
                            })
                    .setNegativeButton(R.string.str_other_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                }
                            }).create();
            dialog.setTitle(R.string.str_dialog_set_study);
            dialog.show();
        } else {
            if (key == IRKeyValue.KEY_AIR_TEMPERATURE_IN) {
                mLongKey = key;
                handler.post(runnable);
            } else if (key == IRKeyValue.KEY_AIR_TEMPERATURE_OUT) {
                mLongKey = key;
                handler.post(runnable);
            }
        }
        return false;
    }
    public class RecvReceiver extends BroadcastReceiver {
        @SuppressLint({"InlinedApi", "NewApi"})
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ETGlobal.BROADCAST_PASS_LEARN)) {
                try {
                    Log.i("Recv", "Recv");
                    String select = intent.getStringExtra("select");
                    int key = intent.getIntExtra("key", 0);
                    String msg = intent.getStringExtra("msg");
                    Log.i("Key",
                            String.valueOf(ETTool.HexStringToBytes(msg).length));
                    if (select.equals("0")) {
                        ETKey k = mDevice.GetKeyByValue(key);
                        if (k != null) {
                            k.SetState(ETKey.ETKEY_STATE_STUDY);
                            k.SetValue(ETTool.HexStringToBytes(msg));
                            k.Update(ETDB.getInstance(AirLearnActivity.this));
                        }
                    } else if (select.equals("1")) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN)) {
                try {
                    byte[] keyValue = mDevice
                            .GetKeyValue(IRKeyValue.KEY_AIR_TEMPERATURE_OUT);
                    if (keyValue == null)
                        return;
                    ETGlobal.mTg.write(keyValue, keyValue.length);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_UP)) {
                try {
                    byte[] keyValue = mDevice
                            .GetKeyValue(IRKeyValue.KEY_AIR_TEMPERATURE_IN);
                    if (keyValue == null)
                        return;
                    ETGlobal.mTg.write(keyValue, keyValue.length);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
                Back();
            }
        }
    }

    public void Back() {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            mIsModity = true;
        } else {
            mIsModity = false;
        }
    }

    private class AirStudyAsyncTask extends AsyncTask<Integer, Void, String> {
        private int key;
        private int select;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            select = params[0];
            key = params[1];
            StringBuffer sb = new StringBuffer();
            sb.append("<").append(electric.getElectricCode()).append("XS").append("**********FF>");
            System.out.println("学习指令：" + sb.toString());
            String result = new MasterSocket().getIrStudyFromMasterNode(sb.toString());
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null || "".equals(s)) {
                Toast.makeText(AirLearnActivity.this, "学习失败，请重试", Toast.LENGTH_LONG).show();
            } else {
                System.out.println("红外学习结果：" + s);
                if (select == 0) {
                    ETKey k = mDevice.GetKeyByValue(key);
                    if (k != null) {
                        k.SetState(ETKey.ETKEY_STATE_STUDY);
                        if(s.length()==28) {
                            k.SetValue(Util.hex2byte(s.substring(17, 19).toString()));
                            System.out.println("学习结果1：" + s.substring(17, 19).getBytes());
                            System.out.println("学习结果2：" + Util.hex2byte(s.substring(17, 19).toString()));
                            k.Update(ETDB.getInstance(AirLearnActivity.this));
                            mDC.mWS.updateIRKeyValue(mDC.sMasterCode, electric.getElectricIndex(), key, s.substring(17, 19).toString());
                        }else if(s.length()==24){
                            k.SetValue(Util.hex2byte(s.substring(13, 15).toString()));
                            System.out.println("学习结果1：" + s.substring(13, 15).getBytes());
                            System.out.println("学习结果2：" + Util.hex2byte(s.substring(13, 15).toString()));
                            k.Update(ETDB.getInstance(AirLearnActivity.this));
                            mDC.mWS.updateIRKeyValue(mDC.sMasterCode, electric.getElectricIndex(), key, s.substring(13, 15).toString());

                        }
                    }
                } else if (select == 1) {

                }
            }
        }

    }
}
