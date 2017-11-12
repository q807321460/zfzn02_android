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
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.connection.MasterSocket;
import com.jia.data.DataControl;
import com.jia.ir.db.ETDB;
import com.jia.ir.etclass.ETDevice;
import com.jia.ir.etclass.ETDeviceTV;
import com.jia.ir.etclass.ETKey;
import com.jia.ir.global.ETGlobal;
import com.jia.util.Util;

import et.song.device.DeviceType;
import et.song.remote.face.IRKeyValue;
import et.song.tool.ETTool;

public class TvLearnActivity extends ElectricBase implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private DataControl mDC;
    private TextView tvLearnTitleName;
    private TextView tvLearnTitleEdit;
    private TextView tvLearnTitleSave;
    private EditText etLearnElectricName;
    CheckBox cb;
    private boolean mIsModity = false;
    private RecvReceiver mReceiver;
    private ETDeviceTV mDevice = null;
    private int mLongKey = 0;
    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1060:
                    Toast.makeText(TvLearnActivity.this, "更改成功", Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(TvLearnActivity.this, "更改失败", Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_tv_learn);
        mDC = DataControl.getInstance();
        initView();
    }

    @Override
    public void updateUI() {

    }

    private void initView() {
        try {

            int _type = DeviceType.DEVICE_REMOTE_TV;
            mDevice = (ETDeviceTV) ETDevice.Builder(_type);
            mDevice.setmMasterCode(electric.getMasterCode());
            mDevice.setmElectricIndex(electric.getElectricIndex());
            mDevice.SetName(electric.getElectricName());
            mDevice.SetType(_type);
            mDevice.SetRes(7);
            mDevice.Load(ETDB.getInstance(this));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tvLearnTitleName = (TextView) findViewById(R.id.tv_learn_activity_title_name);
        tvLearnTitleEdit = (TextView) findViewById(R.id.tv_learn_activity_title_edit);
        tvLearnTitleSave = (TextView) findViewById(R.id.tv_learn_activity_title_save);
        etLearnElectricName = (EditText) findViewById(R.id.et_learn_name);
        cb = (CheckBox) findViewById(R.id.cb_check2);
        cb.setOnCheckedChangeListener(this);
        tvLearnTitleName.setText(electric.getElectricName());
        etLearnElectricName.setText(electric.getElectricName());
        tvLearnTitleEdit.setVisibility(View.VISIBLE);
        tvLearnTitleSave.setVisibility(View.GONE);
        TextView textViewPower = (TextView) findViewById(R.id.text_tv_learn_power);
        textViewPower.setOnClickListener(this);
        textViewPower.setOnLongClickListener(this);
        textViewPower.setBackgroundResource(R.drawable.ic_power_selector);
//        textViewPower.getLayoutParams().width = (ETGlobal.W - 80) / 8;
//        textViewPower.getLayoutParams().height = (ETGlobal.W - 80) / 8;

        TextView textViewHome = (TextView) findViewById(R.id.text_tv_learn_home);
        textViewHome.setOnClickListener(this);
        textViewHome.setOnLongClickListener(this);
        textViewHome.setBackgroundResource(R.drawable.ic_home_selector);
//        textViewHome.getLayoutParams().width = (ETGlobal.W - 80) / 8;
//        textViewHome.getLayoutParams().height = (ETGlobal.W - 80) / 8;

        TextView textViewMute = (TextView) findViewById(R.id.text_tv_learn_mute);
        textViewMute.setOnClickListener(this);
        textViewMute.setOnLongClickListener(this);
        textViewMute.setBackgroundResource(R.drawable.btn_style_white);
        textViewMute.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textViewMute.getLayoutParams().height = (ETGlobal.W - 160) * 3
                / (4 * 4);

        TextView textViewMenu = (TextView) findViewById(R.id.text_tv_learn_menu);
        textViewMenu.setOnClickListener(this);
        textViewMenu.setOnLongClickListener(this);
        textViewMenu.setBackgroundResource(R.drawable.btn_style_white);
        textViewMenu.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textViewMenu.getLayoutParams().height = (ETGlobal.W - 160) * 3
                / (4 * 4);

        TextView textView123 = (TextView) findViewById(R.id.text_tv_learn_tvav);
        textView123.setOnClickListener(this);
        textView123.setOnLongClickListener(this);
        textView123.setBackgroundResource(R.drawable.btn_style_white);
        textView123.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textView123.getLayoutParams().height = (ETGlobal.W - 160) * 3 / (4 * 4);

        TextView textViewBack = (TextView) findViewById(R.id.text_tv_learn_back);
        textViewBack.setOnClickListener(this);
        textViewBack.setOnLongClickListener(this);
        textViewBack.setBackgroundResource(R.drawable.btn_style_white);
        textViewBack.getLayoutParams().width = (ETGlobal.W - 160) / 4;
        textViewBack.getLayoutParams().height = (ETGlobal.W - 160) * 3
                / (4 * 4);

        TextView textViewOk = (TextView) findViewById(R.id.text_tv_learn_ok);
        textViewOk.setOnClickListener(this);
        textViewOk.setOnLongClickListener(this);
        textViewOk.setBackgroundResource(R.drawable.ic_button_ok_selector);


        TextView textViewVolAdd = (TextView) findViewById(R.id.text_tv_learn_vol_add);
        textViewVolAdd.setOnClickListener(this);
        textViewVolAdd.setOnTouchListener(this);
        textViewVolAdd.setOnLongClickListener(this);
        textViewVolAdd
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewVolAdd.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewChAdd = (TextView) findViewById(R.id.text_tv_learn_ch_add);
        textViewChAdd.setOnClickListener(this);
        textViewChAdd.setOnTouchListener(this);
        textViewChAdd.setOnLongClickListener(this);
        textViewChAdd
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewChAdd.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewVolSub = (TextView) findViewById(R.id.text_tv_learn_vol_sub);
        textViewVolSub.setOnClickListener(this);
        textViewVolSub.setOnTouchListener(this);
        textViewVolSub.setOnLongClickListener(this);
        textViewVolSub
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewVolSub.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewChSub = (TextView) findViewById(R.id.text_tv_learn_ch_sub);
        textViewChSub.setOnClickListener(this);
        textViewChSub.setOnTouchListener(this);
        textViewChSub.setOnLongClickListener(this);
        textViewChSub
                .setBackgroundResource(R.drawable.ic_button_round_selector);
        // viewParams =
        // (LinearLayout.LayoutParams)textViewChSub.getLayoutParams();
        // viewParams.width = (ETGlobal.W - 80) / 5;
        // viewParams.height = viewParams.width;

        TextView textViewUp = (TextView) findViewById(R.id.text_tv_learn_up);
        textViewUp.setOnClickListener(this);
        textViewUp.setOnLongClickListener(this);
        textViewUp.setBackgroundResource(R.drawable.ic_button_up_selector);

        TextView textViewDown = (TextView) findViewById(R.id.text_tv_learn_down);
        textViewDown.setOnClickListener(this);
        textViewDown.setOnLongClickListener(this);
        textViewDown.setBackgroundResource(R.drawable.ic_button_down_selector);

        TextView textViewLeft = (TextView) findViewById(R.id.text_tv_learn_left);
        textViewLeft.setOnClickListener(this);
        textViewLeft.setOnLongClickListener(this);
        textViewLeft.setBackgroundResource(R.drawable.ic_button_left_selector);

        TextView textViewRight = (TextView) findViewById(R.id.text_tv_learn_right);
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

    private void changeToNormal() {
        etLearnElectricName.setFocusable(false);
        tvLearnTitleSave.setVisibility(View.GONE);
        tvLearnTitleEdit.setVisibility(View.VISIBLE);
        etLearnElectricName.setVisibility(View.GONE);

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

    public void tvlearnActivityEdit(View view) {
        // 创建PopupMenu对象
        tvLearnTitleEdit.setVisibility(View.GONE);
        tvLearnTitleSave.setVisibility(View.VISIBLE);
        etLearnElectricName.setVisibility(View.VISIBLE);
        etLearnElectricName.setFocusable(true);
        etLearnElectricName.setFocusableInTouchMode(true);
        etLearnElectricName.requestFocus();
    }

    public void tvlearnActivitySave(View view) {
        final String electricName = etLearnElectricName.getText().toString();
        new Thread() {
            @Override
            public void run() {
                String result = mDC.mWS.updateElectric(mDC.sMasterCode, electric.getElectricCode()
                        , electric.getElectricIndex(), electricName, electric.getSceneIndex());
                Message msg = new Message();
                if (result.startsWith("1")) {
                    msg.what = 0x1060;
                    electric.setElectricName(electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex(), electricName);
                } else {
                    msg.what = 0x1061;
                }
                handler1.sendMessage(msg);
            }
        }.start();
    }

    public void tvlearnActivityBack(View view) {
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        byte[] keyValue = null;
        int key = 0;
        switch (v.getId()) {
            case R.id.text_tv_learn_power:
                key = mDC.KEY_TV_POWER;
                break;
            case R.id.text_tv_learn_home:
                key = mDC.KEY_TV_HOME;
                break;
            case R.id.text_tv_learn_tvav:
                key = mDC.KEY_TV_TVAV;
                break;
            case R.id.text_tv_learn_menu:
                key = mDC.KEY_TV_MENU;
                break;
            case R.id.text_tv_learn_back:
                key = mDC.KEY_TV_BACK;
                break;
            case R.id.text_tv_learn_mute:
                key = mDC.KEY_TV_MUTE;
                break;
            case R.id.text_tv_learn_vol_add:
                key = mDC.KEY_TV_VOLADD;
                break;
            case R.id.text_tv_learn_vol_sub:
                key = mDC.KEY_TV_VOLSUB;
                break;
            case R.id.text_tv_learn_up:
                key = mDC.KEY_TV_UP;
                break;
            case R.id.text_tv_learn_left:
                key = mDC.KEY_TV_LEFT;
                break;
            case R.id.text_tv_learn_ok:
                key = mDC.KEY_TV_OK;
                break;
            case R.id.text_tv_learn_right:
                key = mDC.KEY_TV_RIGHT;
                break;
            case R.id.text_tv_learn_down:
                key = mDC.KEY_TV_DOWN;
                break;
            case R.id.text_tv_learn_ch_sub:
                key = mDC.KEY_TV_CH_SUB;
                break;
            case R.id.text_tv_learn_ch_add:
                key = mDC.KEY_TV_CH_ADD;
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
        if (keyValue != null) {
            if (keyValue.length == 1) {
                sendOrder(keyValue);
            } else {
                sendOrder1(keyValue);

                return;
            }
        } else {
            Toast.makeText(TvLearnActivity.this, "请先对码，长按要学习的键，红外伴侣灯亮时，用遥控器调到对应状态", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int key = 0;
        switch (v.getId()) {
            case R.id.text_tv_learn_power:
                key = mDC.KEY_TV_POWER;
                break;
            case R.id.text_tv_learn_home:
                key = mDC.KEY_TV_HOME;
                break;
            case R.id.text_tv_learn_tvav:
                key = mDC.KEY_TV_TVAV;
                break;
            case R.id.text_tv_learn_menu:
                key = mDC.KEY_TV_MENU;
                break;
            case R.id.text_tv_learn_back:
                key = mDC.KEY_TV_BACK;
                break;
            case R.id.text_tv_learn_mute:
                key = mDC.KEY_TV_MUTE;
                break;
            case R.id.text_tv_learn_vol_add:
                key = mDC.KEY_TV_VOLADD;
                break;
            case R.id.text_tv_learn_vol_sub:
                key = mDC.KEY_TV_VOLSUB;
                break;
            case R.id.text_tv_learn_up:
                key = mDC.KEY_TV_UP;
                break;
            case R.id.text_tv_learn_left:
                key = mDC.KEY_TV_LEFT;
                break;
            case R.id.text_tv_learn_ok:
                key = mDC.KEY_TV_OK;
                break;
            case R.id.text_tv_learn_right:
                key = mDC.KEY_TV_RIGHT;
                break;
            case R.id.text_tv_learn_down:
                key = mDC.KEY_TV_DOWN;
                break;
            case R.id.text_tv_learn_ch_sub:
                key = mDC.KEY_TV_CH_SUB;
                break;
            case R.id.text_tv_learn_ch_add:
                key = mDC.KEY_TV_CH_ADD;
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
                                    TvLearnActivity.TvStudyAsyncTask task = new TvLearnActivity.TvStudyAsyncTask();
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
//            if (key == IRKeyValue.KEY_AIR_TEMPERATURE_IN) {
//                mLongKey = key;
//                handler.post(runnable);
//            } else if (key == IRKeyValue.KEY_AIR_TEMPERATURE_OUT) {
//                mLongKey = key;
//                handler.post(runnable);
//            }
        }
        return false;

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
                            k.Update(ETDB.getInstance(TvLearnActivity.this));
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

    private class TvStudyAsyncTask extends AsyncTask<Integer, Void, String> {
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
                Toast.makeText(TvLearnActivity.this, "学习失败，请重试", Toast.LENGTH_LONG).show();
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
                            k.Update(ETDB.getInstance(TvLearnActivity.this));

//                            ContentValues contentValues = new ContentValues();
//                            contentValues.put("master_code", mDC.sMasterCode);
//                            contentValues.put("electric_index",electric.getElectricIndex());
//                            contentValues.put("key_value", key);
//                            contentValues.put("key_key", s.substring(13, 15).toString());
//                            mDC.mDB.insertAirLearning(contentValues);
                            mDC.mWS.updateIRKeyValue(mDC.sMasterCode, electric.getElectricIndex(), key, s.substring(17, 19).toString());
                        }else if(s.length()==24){
                            k.SetValue(Util.hex2byte(s.substring(13, 15).toString()));
                            System.out.println("学习结果1：" + s.substring(13, 15).getBytes());
                            System.out.println("学习结果2：" + Util.hex2byte(s.substring(13, 15).toString()));
                            k.Update(ETDB.getInstance(TvLearnActivity.this));

//                            ContentValues contentValues = new ContentValues();
//                            contentValues.put("master_code", mDC.sMasterCode);
//                            contentValues.put("electric_index",electric.getElectricIndex());
//                            contentValues.put("key_value", key);
//                            contentValues.put("key_key", s.substring(13, 15).toString());
//                            mDC.mDB.insertAirLearning(contentValues);
                            mDC.mWS.updateIRKeyValue(mDC.sMasterCode, electric.getElectricIndex(), key, s.substring(13, 15).toString());

                        }
                    }
                } else if (select == 1) {

                }
            }
        }

    }
}
