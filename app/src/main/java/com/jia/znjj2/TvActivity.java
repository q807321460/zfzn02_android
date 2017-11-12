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
import android.view.LayoutInflater;
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
import com.jia.ir.etclass.ETDeviceTV;
import com.jia.ir.etclass.ETKey;
import com.jia.ir.etclass.ETKeyEx;
import com.jia.ir.face.IBack;
import com.jia.ir.global.ETGlobal;
import com.jia.util.Util;

import et.song.device.DeviceType;
import et.song.remote.face.IRKeyExValue;
import et.song.remote.face.IRKeyValue;
import et.song.tool.ETTool;

public class TvActivity extends ElectricBase implements View.OnClickListener,
        View.OnLongClickListener, IBack, View.OnTouchListener, CompoundButton.OnCheckedChangeListener {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private EditText etElectricName;
    private int mGroupIndex;
    private int mDeviceIndex;
    private ETDeviceTV mDevice = null;
    private RecvReceiver mReceiver;
    private boolean mIsModity = false;
    private int mLongKey = 0;
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(TvActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(TvActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        initView();
    }
    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);

        initView();

    }
    @Override
    public void updateUI(){

    }

    public void tvActivityBack(View view){
        finish();
    }
    public void tvActivitySave(View view){
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
                    updateSceneElectricName(electric.getElectricIndex(),electricName);
                    mDC.mSceneElectricData.updateSceneElectric(electric.getElectricIndex(), electricName);
                }else {
                    msg.what = 0x1061;
                }
                handler1.sendMessage(msg);
            }
        }.start();
    }
    public void tvActivityEdit(View view){
        // 创建PopupMenu对象
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }


    private void initView(){
        tvTitleName = (TextView) findViewById(R.id.tv_activity_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.tv_activity_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.tv_activity_title_save);
        etElectricName = (EditText) findViewById(R.id.tv_name);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        try{

            int _type = DeviceType.DEVICE_REMOTE_TV;
            mDevice = (ETDeviceTV) ETDevice.Builder(_type);
            mDevice.setmMasterCode(electric.getMasterCode());
            mDevice.setmElectricIndex(electric.getElectricIndex());
            mDevice.SetName(electric.getElectricName());
            mDevice.SetType(_type);
            mDevice.SetRes(0);
            mDevice.Load(ETDB.getInstance(this));
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        TextView textViewPower = (TextView)findViewById(R.id.text_tv_power);
        textViewPower.setOnClickListener(this);
        textViewPower.setOnLongClickListener(this);
        textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
//        textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 8;
//        textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 8;

        TextView textViewHome = (TextView)findViewById(R.id.text_tv_home);
        textViewHome.setOnClickListener(this);
        textViewHome.setOnLongClickListener(this);
        textViewHome.setBackgroundResource(R.drawable.ic_home_selector);
//        textViewHome.getLayoutParams().width = (ETGlobal.W - 80) / 8;
//        textViewHome.getLayoutParams().height = (ETGlobal.W - 80) / 8;

        TextView textViewMute = (TextView)findViewById(R.id.text_tv_mute);
        textViewMute.setOnClickListener(this);
        textViewMute.setOnLongClickListener(this);
        textViewMute.setBackgroundResource(R.drawable.btn_style_white);
        textViewMute.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textViewMute.getLayoutParams().height = (ETGlobal.W - 160) * 3
                / (4 * 4);

        TextView textViewMenu = (TextView)findViewById(R.id.text_tv_menu);
        textViewMenu.setOnClickListener(this);
        textViewMenu.setOnLongClickListener(this);
        textViewMenu.setBackgroundResource(R.drawable.btn_style_white);
        textViewMenu.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textViewMenu.getLayoutParams().height = (ETGlobal.W - 160) * 3
                / (4 * 4);

        TextView textView123 = (TextView) findViewById(R.id.text_tv_123);
        textView123.setOnClickListener(this);
        textView123.setBackgroundResource(R.drawable.btn_style_white);
        textView123.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textView123.getLayoutParams().height = (ETGlobal.W - 160) * 3 / (4 * 4);

        TextView textViewBack = (TextView) findViewById(R.id.text_tv_back);
        textViewBack.setOnClickListener(this);
        textViewBack.setOnLongClickListener(this);
        textViewBack.setBackgroundResource(R.drawable.btn_style_white);
        textViewBack.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textViewBack.getLayoutParams().height = (ETGlobal.W - 160) * 3
                / (4 * 4);

        TextView textViewOk = (TextView) findViewById(R.id.text_tv_ok);
        textViewOk.setOnClickListener(this);
        textViewOk.setOnLongClickListener(this);
        textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);


        TextView textViewVolAdd = (TextView) findViewById(R.id.text_tv_vol_add);
        textViewVolAdd.setOnClickListener(this);
        textViewVolAdd.setOnTouchListener(this);
        textViewVolAdd.setOnLongClickListener(this);
        textViewVolAdd
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewVolAdd.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewChAdd = (TextView) findViewById(R.id.text_tv_ch_add);
        textViewChAdd.setOnClickListener(this);
        textViewChAdd.setOnTouchListener(this);
        textViewChAdd.setOnLongClickListener(this);
        textViewChAdd
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewChAdd.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewVolSub = (TextView) findViewById(R.id.text_tv_vol_sub);
        textViewVolSub.setOnClickListener(this);
        textViewVolSub.setOnTouchListener(this);
        textViewVolSub.setOnLongClickListener(this);
        textViewVolSub
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewVolSub.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewChSub = (TextView)findViewById(R.id.text_tv_ch_sub);
        textViewChSub.setOnClickListener(this);
        textViewChSub.setOnTouchListener(this);
        textViewChSub.setOnLongClickListener(this);
        textViewChSub
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewChSub.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewUp = (TextView) findViewById(R.id.text_tv_up);
        textViewUp.setOnClickListener(this);
        textViewUp.setOnLongClickListener(this);
        textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

        TextView textViewDown = (TextView) findViewById(R.id.text_tv_down);
        textViewDown.setOnClickListener(this);
        textViewDown.setOnLongClickListener(this);
        textViewDown.setBackgroundResource(R.drawable.ic_button_down_selector);

        TextView textViewLeft = (TextView) findViewById(R.id.text_tv_left);
        textViewLeft.setOnClickListener(this);
        textViewLeft.setOnLongClickListener(this);
        textViewLeft.setBackgroundResource(R.drawable.ic_button_left_selector);

        TextView textViewRight = (TextView) findViewById(R.id.text_tv_right);
        textViewRight.setOnClickListener(this);
        textViewRight.setOnLongClickListener(this);
        textViewRight
                .setBackgroundResource(R.drawable.ic_button_right_selector);

//        textViewOk.getLayoutParams().width = (ETGlobal.W - 80) / 5;
//        textViewOk.getLayoutParams().height = (ETGlobal.W - 80) / 5;
//        textViewUp.getLayoutParams().width = (ETGlobal.W - 80) / 3;
//        textViewUp.getLayoutParams().height = (int) (textViewUp
//                .getLayoutParams().width * (5 / 12.0));
//        textViewDown.getLayoutParams().width = (ETGlobal.W - 80) / 3;
//        textViewDown.getLayoutParams().height = (int) (textViewDown
//                .getLayoutParams().width * (5 / 12.0));

        textViewLeft.getLayoutParams().width = textViewDown.getLayoutParams().height;
        textViewLeft.getLayoutParams().height = textViewDown.getLayoutParams().width;
        textViewRight.getLayoutParams().width = textViewDown.getLayoutParams().height;
        textViewRight.getLayoutParams().height = textViewDown.getLayoutParams().width;
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



    @SuppressLint("InflateParams")
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        byte[] keyValue = null;
        int key = 0;
        mLongKey = 0;
        switch (v.getId()) {
            case R.id.text_tv_0:
                key = IRKeyValue.KEY_TV_KEY0;
                break;
            case R.id.text_tv_1:
                key = IRKeyValue.KEY_TV_KEY1;
                break;
            case R.id.text_tv_2:
                key = IRKeyValue.KEY_TV_KEY2;
                break;
            case R.id.text_tv_3:
                key = IRKeyValue.KEY_TV_KEY3;
                break;
            case R.id.text_tv_4:
                key = IRKeyValue.KEY_TV_KEY4;
                break;
            case R.id.text_tv_5:
                key = IRKeyValue.KEY_TV_KEY5;
                break;
            case R.id.text_tv_6:
                key = IRKeyValue.KEY_TV_KEY6;
                break;
            case R.id.text_tv_7:
                key = IRKeyValue.KEY_TV_KEY7;
                break;
            case R.id.text_tv_8:
                key = IRKeyValue.KEY_TV_KEY8;
                break;
            case R.id.text_tv_9:
                key = IRKeyValue.KEY_TV_KEY9;
                break;
            case R.id.text_tv_av:
                key = IRKeyValue.KEY_TV_AV_TV;
                break;
            case R.id.text_tv_select:
                key = IRKeyValue.KEY_TV_SELECT;
                break;
            case R.id.text_tv_up:
                key = IRKeyValue.KEY_TV_UP;
                break;
            case R.id.text_tv_down:
                key = IRKeyValue.KEY_TV_DOWN;
                break;
            case R.id.text_tv_left:
                key = IRKeyValue.KEY_TV_LEFT;
                break;
            case R.id.text_tv_right:
                key = IRKeyValue.KEY_TV_RIGHT;
                break;
            case R.id.text_tv_ch_add:
                key = IRKeyValue.KEY_TV_CHANNEL_IN;
                break;
            case R.id.text_tv_ch_sub:
                key = IRKeyValue.KEY_TV_CHANNEL_OUT;
                break;
            case R.id.text_tv_vol_add:
                key = IRKeyValue.KEY_TV_VOLUME_IN;
                break;
            case R.id.text_tv_vol_sub:
                key = IRKeyValue.KEY_TV_VOLUME_OUT;
                break;
            case R.id.text_tv_menu:
                key = IRKeyValue.KEY_TV_MENU;
                break;
            case R.id.text_tv_mute:
                key = IRKeyValue.KEY_TV_MUTE;
                break;
            case R.id.text_tv_ok:
                key = IRKeyValue.KEY_TV_OK;
                break;
            case R.id.text_tv_back:
                key = IRKeyValue.KEY_TV_BACK;
                break;
            case R.id.text_tv_power:
                key = IRKeyValue.KEY_TV_POWER;
                break;
            case R.id.text_tv_home:
                key = IRKeyExValue.KEYEX_TV_HOME;
                break;
            case R.id.text_tv_123:
                LayoutInflater mInflater = LayoutInflater.from(this);
                View view123 = mInflater.inflate(R.layout.fragment_tv_123, null);

                TextView textView1 = (TextView) view123
                        .findViewById(R.id.text_tv_1);
                textView1.setOnClickListener(this);
                textView1.setOnLongClickListener(this);
                textView1.setBackgroundResource(R.drawable.btn_style_white);
                // LinearLayout.LayoutParams viewParams =
                // (LinearLayout.LayoutParams)textView1.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;

                TextView textView2 = (TextView) view123
                        .findViewById(R.id.text_tv_2);
                textView2.setOnClickListener(this);
                textView2.setOnLongClickListener(this);
                textView2.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textView2.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                //
                TextView textView3 = (TextView) view123
                        .findViewById(R.id.text_tv_3);
                textView3.setOnClickListener(this);
                textView3.setOnLongClickListener(this);
                textView3.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textView3.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                TextView textView4 = (TextView) view123
                        .findViewById(R.id.text_tv_4);
                textView4.setOnClickListener(this);
                textView4.setOnLongClickListener(this);
                textView4.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textView4.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                TextView textView5 = (TextView) view123
                        .findViewById(R.id.text_tv_5);
                textView5.setOnClickListener(this);
                textView5.setBackgroundResource(R.drawable.btn_style_white);
                textView5.setOnLongClickListener(this);
                // viewParams =
                // (LinearLayout.LayoutParams)textView5.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                //
                TextView textView6 = (TextView) view123
                        .findViewById(R.id.text_tv_6);
                textView6.setOnClickListener(this);
                textView6.setBackgroundResource(R.drawable.btn_style_white);
                textView6.setOnLongClickListener(this);
                // viewParams =
                // (LinearLayout.LayoutParams)textView6.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;

                TextView textView7 = (TextView) view123
                        .findViewById(R.id.text_tv_7);
                textView7.setOnClickListener(this);
                textView7.setOnLongClickListener(this);
                textView7.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textView7.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;

                TextView textView8 = (TextView) view123
                        .findViewById(R.id.text_tv_8);
                textView8.setOnClickListener(this);
                textView8.setOnLongClickListener(this);
                textView8.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textView8.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                TextView textView9 = (TextView) view123
                        .findViewById(R.id.text_tv_9);
                textView9.setOnClickListener(this);
                textView9.setOnLongClickListener(this);
                textView9.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textView9.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                TextView textViewSelect = (TextView) view123
                        .findViewById(R.id.text_tv_select);
                textViewSelect.setOnClickListener(this);
                textViewSelect.setOnLongClickListener(this);
                textViewSelect.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textViewSelect.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                TextView textView0 = (TextView) view123
                        .findViewById(R.id.text_tv_0);
                textView0.setOnClickListener(this);
                textView0.setOnLongClickListener(this);
                textView0.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textView0.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                TextView textViewAV = (TextView) view123
                        .findViewById(R.id.text_tv_av);
                textViewAV.setOnClickListener(this);
                textViewAV.setOnLongClickListener(this);
                textViewAV.setBackgroundResource(R.drawable.btn_style_white);
                // viewParams =
                // (LinearLayout.LayoutParams)textViewAV.getLayoutParams();
                // viewParams.width = (ETGlobal.W - 80) / 4;
                // viewParams.height = viewParams.width * 2 / 3;
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher).setTitle(R.string.str_num)
                        .setView(view123).create();
                dialog.show();
                break;
        }
        boolean isSend = true;
        try {
            if (key == 0)
                return;
            keyValue = mDevice.GetKeyValueEx(key);
            System.out.println("TvActivity keyValue"+keyValue);
            //sendOrder(keyValue);
            if (keyValue == null)
            {
                keyValue = mDevice.GetKeyValue(key);
            }
            if (keyValue == null){
                Toast.makeText(this, R.string.str_error_no_key, Toast.LENGTH_SHORT).show();
                return;
            }
            sendOrder(keyValue);
            // Log.i("Len", String.valueOf(keyValue.length));
            // Log.i("Key", ETTool.BytesToHexString(keyValue));

            if (ETGlobal.mTg == null) {
                isSend = false;
            }
            int n = ETGlobal.mTg.write(keyValue, keyValue.length);
            if (n < 0) {
                isSend = false;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        int key = 0;
        switch (v.getId()) {
            case R.id.text_tv_0:
                key = IRKeyValue.KEY_TV_KEY0;
                break;
            case R.id.text_tv_1:
                key = IRKeyValue.KEY_TV_KEY1;
                break;
            case R.id.text_tv_2:
                key = IRKeyValue.KEY_TV_KEY2;
                break;
            case R.id.text_tv_3:
                key = IRKeyValue.KEY_TV_KEY3;
                break;
            case R.id.text_tv_4:
                key = IRKeyValue.KEY_TV_KEY4;
                break;
            case R.id.text_tv_5:
                key = IRKeyValue.KEY_TV_KEY5;
                break;
            case R.id.text_tv_6:
                key = IRKeyValue.KEY_TV_KEY6;
                break;
            case R.id.text_tv_7:
                key = IRKeyValue.KEY_TV_KEY7;
                break;
            case R.id.text_tv_8:
                key = IRKeyValue.KEY_TV_KEY8;
                break;
            case R.id.text_tv_9:
                key = IRKeyValue.KEY_TV_KEY9;
                break;
            case R.id.text_tv_av:
                key = IRKeyValue.KEY_TV_AV_TV;
                break;
            case R.id.text_tv_select:
                key = IRKeyValue.KEY_TV_SELECT;
                break;
            case R.id.text_tv_up:
                key = IRKeyValue.KEY_TV_UP;
                break;
            case R.id.text_tv_down:
                key = IRKeyValue.KEY_TV_DOWN;
                break;
            case R.id.text_tv_left:
                key = IRKeyValue.KEY_TV_LEFT;
                break;
            case R.id.text_tv_right:
                key = IRKeyValue.KEY_TV_RIGHT;
                break;
            case R.id.text_tv_ch_add:
                key = IRKeyValue.KEY_TV_CHANNEL_IN;
                break;
            case R.id.text_tv_ch_sub:
                key = IRKeyValue.KEY_TV_CHANNEL_OUT;
                break;
            case R.id.text_tv_vol_add:
                key = IRKeyValue.KEY_TV_VOLUME_IN;
                break;
            case R.id.text_tv_vol_sub:
                key = IRKeyValue.KEY_TV_VOLUME_OUT;
                break;
            case R.id.text_tv_menu:
                key = IRKeyValue.KEY_TV_MENU;
                break;
            case R.id.text_tv_mute:
                key = IRKeyValue.KEY_TV_MUTE;
                break;
            case R.id.text_tv_ok:
                key = IRKeyValue.KEY_TV_OK;
                break;
            case R.id.text_tv_back:
                key = IRKeyValue.KEY_TV_BACK;
                break;
            case R.id.text_tv_power:
                key = IRKeyValue.KEY_TV_POWER;
                break;
            case R.id.text_tv_home:
                key = IRKeyExValue.KEYEX_TV_HOME;
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
                                    TvStudyAsyncTask task = new TvStudyAsyncTask();
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
            if (key == IRKeyValue.KEY_TV_CHANNEL_IN) {
                mLongKey = key;
                handler.post(runnable);
            } else if (key == IRKeyValue.KEY_TV_CHANNEL_OUT) {
                mLongKey = key;
                handler.post(runnable);
            } else if (key == IRKeyValue.KEY_TV_VOLUME_IN) {
                mLongKey = key;
                handler.post(runnable);
            } else if (key == IRKeyValue.KEY_TV_VOLUME_OUT) {
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
                            k.Update(ETDB.getInstance(TvActivity.this));
                        }
                        else{
                            ETKeyEx keyEx =  mDevice.GetKeyByValueEx(key);
                            keyEx.SetValue(ETTool.HexStringToBytes(msg));
                            keyEx.Update(ETDB.getInstance(TvActivity.this));
                        }
                    } else if (select.equals("1")) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (action.equals(ETGlobal.BROADCAST_KEYCODE_VOLUME_DOWN)) {
                try {
                    byte[] keyValue = mDevice
                            .GetKeyValue(IRKeyValue.KEY_TV_VOLUME_OUT);
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
                            .GetKeyValue(IRKeyValue.KEY_TV_VOLUME_IN);
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
            case R.id.text_tv_ch_add:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mLongKey = 0;
                    handler.removeCallbacks(runnable);
                }
                break;
            case R.id.text_tv_ch_sub:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mLongKey = 0;
                    handler.removeCallbacks(runnable);
                }
                break;
            case R.id.text_tv_vol_add:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mLongKey = 0;
                    handler.removeCallbacks(runnable);
                }
                break;
            case R.id.text_tv_vol_sub:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mLongKey = 0;
                    handler.removeCallbacks(runnable);
                }
                break;
        }
        return false;
    }

    private class TvStudyAsyncTask extends AsyncTask<Integer,Void,String> {
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
                Toast.makeText(TvActivity.this,"学习失败，请重试",Toast.LENGTH_LONG).show();
            }else {
                System.out.println("红外学习结果："+s);
                if (select == 0) {
                    ETKey k = mDevice.GetKeyByValue(key);
                    if (k != null) {
                        k.SetState(ETKey.ETKEY_STATE_STUDY);
                        k.SetValue(Util.hex2byte(s.substring(13,15).toString()));
                        System.out.println("学习结果1：" + s.substring(13,15).getBytes());
                        System.out.println("学习结果2：" + Util.hex2byte(s.substring(13,15).toString()));
                        k.Update(ETDB.getInstance(TvActivity.this));
                        mDC.mWS.updateIRKeyValue(mDC.sMasterCode,electric.getElectricIndex(),key,s.substring(13,15).toString());
                    }
                } else if (select == 1) {

                }
            }
        }


    }
}
