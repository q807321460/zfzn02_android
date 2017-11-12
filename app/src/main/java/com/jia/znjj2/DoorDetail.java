package com.jia.znjj2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/24.
 */
public class DoorDetail extends ElectricBase {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private EditText etElectricName;
    private TextView tvRoomName;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ImageView doorImage;
    private EditText doorPassword;
    private CheckBox doorRePassword;
    private String password;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(DoorDetail.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(DoorDetail.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.door_detail);
        init();
        initView();

    }
    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);

        initView();

    }
    private void initView(){
        tvTitleName = (TextView) findViewById(R.id.door_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.door_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.door_title_save);
        etElectricName = (EditText) findViewById(R.id.door_name);
        tvRoomName = (TextView) findViewById(R.id.door_room);
        doorImage = (ImageView) findViewById(R.id.doorControl_image);
        doorPassword = (EditText) findViewById(R.id.door_password);
        doorRePassword = (CheckBox) findViewById(R.id.door_remember_password);
        preferences = getSharedPreferences("zfzn_door",MODE_PRIVATE);
        editor = preferences.edit();
        String string = preferences.getString(mDC.sAccountCode+electric.getElectricCode(),"");
        if(!string.equals("")){
            doorPassword.setText(string);
        }
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());
//        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
        finish();
    }

    @Override
    public void open(View view) {
        String electricState = mDC.mElectricState.get(electric.getElectricCode())[0];
        String stateInfo = mDC.mElectricState.get(electric.getElectricCode())[1];
        password = doorPassword.getText().toString().trim();
        if(password == null || password.equals("")){
            Toast.makeText(DoorDetail.this, "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        orderInfo = passswordToOrderInfo(password);
        new Thread(){
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = format.format(new Date());
                String msg = mDC.sAccountCode + "开锁，时间：" + time;
                mDC.mWS.sendSms(mDC.sAccountCode, msg);
            }
        }.start();
        rePassword();
        super.open(view);
    }

    @Override
    public void close(View view) {
        password = doorPassword.getText().toString().trim();
        if(password == null || password.equals("")){
            Toast.makeText(DoorDetail.this, "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        orderInfo = passswordToOrderInfo(password);
        rePassword();
        super.close(view);
    }

    private void rePassword(){
        if(doorRePassword.isChecked()){
            editor.putString(mDC.sAccountCode+electric.getElectricCode(),doorPassword.getText().toString().trim());
        }else {
            editor.remove(mDC.sAccountCode+electric.getElectricCode());
        }
        editor.commit();
    }

    @Override
    public void updateUI() {
//        String electricCode = mDC.mAreaList.get(iAreaIndex).getmElectricInfoDataList().get(iElectricType).getmCode();
        String electricCode = electric.getElectricCode();
        String electricState = mDC.mElectricState.get(electric.getElectricCode())[0];
        String stateInfo = mDC.mElectricState.get(electric.getElectricCode())[1];
        System.out.println("更新页面： 电器编号---->" + electricCode + "电器状态：" +
                electricState + "  " + stateInfo);
        if (mDC.mElectricState.get(electricCode)[0].equals("ZW")) {
            doorImage.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_lock_close));
        }else if (mDC.mElectricState.get(electricCode)[0].equals("ZV")) {
            doorImage.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_lock_open));
        }
    }

    private String passswordToOrderInfo(String password){
        StringBuffer sb = new StringBuffer(password);
        while (sb.length()<10){
            sb.append('*');
        }
        return sb.toString();
    }
    public void doorEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }

    public void doorSave(View view){
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
                handler.sendMessage(msg);
            }
        }.start();
    }



    public void doorBack(View view){
        finish();
    }

}
