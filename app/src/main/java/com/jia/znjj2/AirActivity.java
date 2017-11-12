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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.connection.MasterSocket;
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

public class AirActivity extends ElectricBase implements View.OnClickListener,
        View.OnLongClickListener, IBack, View.OnTouchListener, CompoundButton.OnCheckedChangeListener {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private EditText etElectricName;
    private ETDeviceAIR mDevice = null;
    private TextView mTextViewTitleEdit;
    private TextView mTextViewTemp;
    private TextView mTextViewModeAuto;
    private TextView mTextViewModeCool;
    private TextView mTextViewModeDrying;
    private TextView mTextViewModeWind;
    private TextView mTextViewModeWarm;
    private TextView mTextViewRate;
    private TextView mTextViewDir;
    private RecvReceiver mReceiver;
    private boolean mIsModity = false;
    private int mLongKey = 0;
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(AirActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(AirActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);
        initView();
        //initPopupMenu();
    }

    private void initView(){

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
        tvTitleName = (TextView) findViewById(R.id.air_activity_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.air_activity_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.air_activity_title_save);
        etElectricName = (EditText) findViewById(R.id.air_name);
        TextView textViewPower = (TextView)findViewById(R.id.text_air_power);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        textViewPower.setOnClickListener(this);
        textViewPower.setOnLongClickListener(this);
        textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
        mTextViewTitleEdit = (TextView) findViewById(R.id.air_activity_title_edit);
        //registerForContextMenu(mTextViewTitleEdit);

        mTextViewModeAuto = (TextView)findViewById(R.id.text_air_mode_auto);
        mTextViewModeCool = (TextView)findViewById(R.id.text_air_mode_cool);
        mTextViewModeDrying = (TextView)findViewById(R.id.text_air_mode_drying);
        mTextViewModeWind = (TextView)findViewById(R.id.text_air_mode_wind);
        mTextViewModeWarm = (TextView)findViewById(R.id.text_air_mode_warm);

        mTextViewRate = (TextView)findViewById(R.id.text_air_rate);

        mTextViewDir = (TextView)findViewById(R.id.text_air_dir);

        mTextViewTemp = (TextView)findViewById(R.id.text_air_temp);
        mTextViewTemp.setText("");

        TextView textViewMode = (TextView)findViewById(R.id.text_air_mode);
        textViewMode.setOnClickListener(this);
        textViewMode.setOnLongClickListener(this);
        textViewMode.setBackgroundResource(R.drawable.ic_button_up_bg_selector);

        TextView textViewSpeed = (TextView)findViewById(R.id.text_air_speed);
        textViewSpeed.setOnClickListener(this);
        textViewSpeed.setOnLongClickListener(this);
        textViewSpeed
                .setBackgroundResource(R.drawable.ic_button_down_bg_selector);

        TextView textViewWindHand = (TextView)findViewById(R.id.text_air_hand);
        textViewWindHand.setOnClickListener(this);
        textViewWindHand.setOnLongClickListener(this);
        textViewWindHand
                .setBackgroundResource(R.drawable.ic_button_round_selector);

        TextView textViewWindAuto = (TextView)findViewById(R.id.text_air_auto);
        textViewWindAuto.setOnClickListener(this);
        textViewWindAuto.setOnLongClickListener(this);
        textViewWindAuto
                .setBackgroundResource(R.drawable.ic_button_round_selector);

        TextView textViewTempAdd = (TextView)findViewById(R.id.text_air_tempadd);
        textViewTempAdd.setOnClickListener(this);
        textViewTempAdd.setOnLongClickListener(this);
        textViewTempAdd
                .setBackgroundResource(R.drawable.ic_button_round_selector);

        TextView textViewTempSub = (TextView)findViewById(R.id.text_air_tempsub);
        textViewTempSub.setOnClickListener(this);
        textViewTempSub.setOnLongClickListener(this);
        textViewTempSub
                .setBackgroundResource(R.drawable.ic_button_round_selector);

//        textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 4;
//        textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 4;
//        textViewMode.getLayoutParams().width = (ETGlobal.W - 80) / 2;
//        textViewMode.getLayoutParams().height = (int) (textViewMode
//                .getLayoutParams().width * (5 / 12.0));
//        textViewSpeed.getLayoutParams().width = (ETGlobal.W - 80) / 2;
//        textViewSpeed.getLayoutParams().height = (int) (textViewSpeed
//                .getLayoutParams().width * (5 / 12.0));
        F5();
    }

    public void F5() {
        if (mDevice.GetPower() == 0x01) {
            switch (mDevice.GetMode()) {
                case 1:
                    mTextViewModeAuto
                            .setBackgroundResource(R.drawable.ic_air_mode_auto_2);
                    mTextViewModeCool
                            .setBackgroundResource(R.drawable.ic_air_mode_cold_1);
                    mTextViewModeDrying
                            .setBackgroundResource(R.drawable.ic_air_mode_drying_1);
                    mTextViewModeWind
                            .setBackgroundResource(R.drawable.ic_air_mode_wind_1);
                    mTextViewModeWarm
                            .setBackgroundResource(R.drawable.ic_air_mode_warm_1);
                    break;
                case 2:

                    mTextViewModeAuto
                            .setBackgroundResource(R.drawable.ic_air_mode_auto_1);
                    mTextViewModeCool
                            .setBackgroundResource(R.drawable.ic_air_mode_cold_2);
                    mTextViewModeDrying
                            .setBackgroundResource(R.drawable.ic_air_mode_drying_1);
                    mTextViewModeWind
                            .setBackgroundResource(R.drawable.ic_air_mode_wind_1);
                    mTextViewModeWarm
                            .setBackgroundResource(R.drawable.ic_air_mode_warm_1);

                    break;
                case 3:

                    mTextViewModeAuto
                            .setBackgroundResource(R.drawable.ic_air_mode_auto_1);
                    mTextViewModeCool
                            .setBackgroundResource(R.drawable.ic_air_mode_cold_1);
                    mTextViewModeDrying
                            .setBackgroundResource(R.drawable.ic_air_mode_drying_2);
                    mTextViewModeWind
                            .setBackgroundResource(R.drawable.ic_air_mode_wind_1);
                    mTextViewModeWarm
                            .setBackgroundResource(R.drawable.ic_air_mode_warm_1);
                    break;
                case 4:

                    mTextViewModeAuto
                            .setBackgroundResource(R.drawable.ic_air_mode_auto_1);
                    mTextViewModeCool
                            .setBackgroundResource(R.drawable.ic_air_mode_cold_1);
                    mTextViewModeDrying
                            .setBackgroundResource(R.drawable.ic_air_mode_drying_1);
                    mTextViewModeWind
                            .setBackgroundResource(R.drawable.ic_air_mode_wind_2);
                    mTextViewModeWarm
                            .setBackgroundResource(R.drawable.ic_air_mode_warm_1);
                    break;
                case 5:
                    mTextViewModeAuto
                            .setBackgroundResource(R.drawable.ic_air_mode_auto_1);
                    mTextViewModeCool
                            .setBackgroundResource(R.drawable.ic_air_mode_cold_1);
                    mTextViewModeDrying
                            .setBackgroundResource(R.drawable.ic_air_mode_drying_1);
                    mTextViewModeWind
                            .setBackgroundResource(R.drawable.ic_air_mode_wind_1);
                    mTextViewModeWarm
                            .setBackgroundResource(R.drawable.ic_air_mode_warm_2);
                    break;
            }

            switch (mDevice.GetWindRate()) {
                case 1:
                    mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_1);

                    break;
                case 2:
                    mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_2);

                    break;
                case 3:
                    mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_3);

                    break;
                case 4:
                    mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_4);

                    break;
            }

            if (mDevice.GetAutoWindDir() == 0x01) {
                mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_1);
            } else {
                switch (mDevice.GetWindDir()) {
                    case 1:
                        mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_2);
                        break;
                    case 2:
                        mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_3);
                        break;
                    case 3:
                        mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_4);
                        break;
                }
            }
            if (mDevice.GetMode() == 2 || mDevice.GetMode() == 5) {
                mTextViewTemp.setText(Byte.valueOf(mDevice.GetTemp())
                        .toString());
            } else {
                mTextViewTemp.setText("");
            }
        } else {

            mTextViewModeAuto
                    .setBackgroundResource(R.drawable.ic_air_mode_auto_1);
            mTextViewModeCool
                    .setBackgroundResource(R.drawable.ic_air_mode_cold_1);
            mTextViewModeDrying
                    .setBackgroundResource(R.drawable.ic_air_mode_drying_1);
            mTextViewModeWind
                    .setBackgroundResource(R.drawable.ic_air_mode_wind_1);
            mTextViewModeWarm
                    .setBackgroundResource(R.drawable.ic_air_mode_warm_1);
            mTextViewRate.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
            mTextViewDir.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
            mTextViewTemp.setText("");
        }
        mDevice.Update(ETDB.getInstance(this));
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

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Nothing to see here.
        menu.clear();
        getMenuInflater().inflate(R.menu.ir_menu_edit, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                    | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.clear();
        //getMenuInflater().inflate(R.menu.ir_menu_edit,menu);
        menu.add(0, 1, 0, "红色背景");
        menu.add(0, 2, 0, "绿色背景");
        menu.add(1, 3, 0, "白色背景");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.i("Home", "Home");
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Back();
//                return true;
//            case R.id.menu_look:
//                break;
//            case R.id.ir_menu_edit:
//                if (item.isChecked()) {
//                    item.setChecked(false);
//                    mIsModity = false;
//                } else {
//                    item.setChecked(true);
//                    mIsModity = true;
//                    // ETTool.MessageBox(getActivity(), 0.5f,
//                    // getString(R.string.str_study_start_info_2), true);
//                }
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        byte[] keyValue = null;
        int key = 0;
        switch (v.getId()) {
            case R.id.text_air_power:
                key = IRKeyValue.KEY_AIR_POWER;

                break;
            case R.id.text_air_mode:
                key = IRKeyValue.KEY_AIR_MODE;

                break;
            case R.id.text_air_hand:
                key = IRKeyValue.KEY_AIR_WIND_DIRECTION;

                break;
            case R.id.text_air_auto:
                key = IRKeyValue.KEY_AIR_AUTOMATIC_WIND_DIRECTION;

                break;
            case R.id.text_air_speed:
                key = IRKeyValue.KEY_AIR_WIND_RATE;

                break;
            case R.id.text_air_tempadd:
                key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;

                break;
            case R.id.text_air_tempsub:
                key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;

                break;
        }
        ETKey k = mDevice.GetKeyByValue(key);
        if (k.GetState() != ETKey.ETKEY_STATE_STUDY
                && k.GetState() != ETKey.ETKEY_STATE_DIY) {
            if (key != IRKeyValue.KEY_AIR_POWER && mDevice.GetPower() != 0x01) {
                return;
            }
        }else if(k.GetState() == ETKey.ETKEY_STATE_STUDY){
            keyValue = k.GetValue();
            sendOrder(keyValue);
            return;
        }

        boolean isSend = true;
        try {
            if (key == 0)
                return;
            keyValue = mDevice.GetKeyValue(key);
            sendOrder(keyValue);
            if (keyValue == null)
                return;
            if (ETGlobal.mTg == null) {
                isSend = false;
            }
            int n = ETGlobal.mTg.write(keyValue, keyValue.length);
            if (n < 0) {
                isSend = false;
            }
            F5();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        F5();

    }

    @Override
    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        int key = 0;
        switch (v.getId()) {
            case R.id.text_air_power:
                key = IRKeyValue.KEY_AIR_POWER;

                break;
            case R.id.text_air_mode:
                key = IRKeyValue.KEY_AIR_MODE;

                break;
            case R.id.text_air_hand:
                key = IRKeyValue.KEY_AIR_WIND_DIRECTION;

                break;
            case R.id.text_air_auto:
                key = IRKeyValue.KEY_AIR_AUTOMATIC_WIND_DIRECTION;

                break;
            case R.id.text_air_speed:
                key = IRKeyValue.KEY_AIR_WIND_RATE;

                break;
            case R.id.text_air_tempadd:
                key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;

                break;
            case R.id.text_air_tempsub:
                key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;

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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            mIsModity = true;
        }else {
            mIsModity = false;
        }

    }

    public class RecvReceiver extends BroadcastReceiver {
        @SuppressLint({ "InlinedApi", "NewApi" })
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
                            k.Update(ETDB.getInstance(AirActivity.this));
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
                    F5();
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
                    F5();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
                Back();
            }
        }
    }

    @Override
    public void Back() {
        finish();
    }

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
                F5();
            } catch (Exception e) {

            }
            handler.postDelayed(this, 300);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.text_air_tempadd:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mLongKey = 0;
                    handler.removeCallbacks(runnable);
                }
                break;
            case R.id.text_air_tempsub:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mLongKey = 0;
                    handler.removeCallbacks(runnable);
                }
                break;
        }
        return false;
    }

    @Override
    public void updateUI() {

    }

    public void airActivityEdit(View view){
        // 创建PopupMenu对象
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void airActivitySave(View view){
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
                handler1.sendMessage(msg);
            }
        }.start();
    }

    public void airActivityBack(View view){
        finish();
    }
    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);

        initView();

    }

    private class AirStudyAsyncTask extends AsyncTask<Integer,Void,String>{
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
            if(s == null || "".equals(s)){
                Toast.makeText(AirActivity.this,"学习失败，请重试",Toast.LENGTH_LONG).show();
            }else {
                System.out.println("红外学习结果："+s);
                if (select == 0) {
                    ETKey k = mDevice.GetKeyByValue(key);
                    if (k != null) {
                        k.SetState(ETKey.ETKEY_STATE_STUDY);
                        k.SetValue(Util.hex2byte(s.substring(13,15).toString()));
                        System.out.println("学习结果1：" + s.substring(13,15).getBytes());
                        System.out.println("学习结果2：" + Util.hex2byte(s.substring(13,15).toString()));
                        k.Update(ETDB.getInstance(AirActivity.this));

                        mDC.mWS.updateIRKeyValue(mDC.sMasterCode,electric.getElectricIndex(),key,s.substring(13,15).toString());
                    }
                } else if (select == 1) {

                }
            }
        }


    }


}
