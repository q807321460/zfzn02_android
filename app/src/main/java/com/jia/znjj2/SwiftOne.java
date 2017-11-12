package com.jia.znjj2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.widget.SlipButton;

public class SwiftOne extends ElectricBase {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ImageView ivElectricImg;
    private EditText etElectricName;
    private TextView tvRoomName;
    private SlipButton ibSwift;
    private View view ;
    private Bitmap bitmapOn;
    private Bitmap bitmapOff;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(SwiftOne.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(SwiftOne.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swift_one);
        init();
        initView();
        addListener();
    }

    private void initView(){
        view = new View(getBaseContext());
        tvTitleName = (TextView) findViewById(R.id.swift_one_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.swift_one_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.swift_one_title_save);
        ivElectricImg = (ImageView) findViewById(R.id.swift_one_img);
        etElectricName = (EditText) findViewById(R.id.swift_one_name);
        tvRoomName = (TextView) findViewById(R.id.swift_one_room);
        ibSwift = (SlipButton) findViewById(R.id.swift_one_swift);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());

        orderInfo = electric.getOrderInfo() + "********";
        if(electric.getElectricType() == 0){
            bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_socket_on1);
            bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_socket_close);
        }else if(electric.getElectricType() == 1){
            bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift1_on);
            bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift1);
        }else if(electric.getElectricType() == 2){
            if("01".equals(electric.getOrderInfo())){
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift2_left_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift2_left);
            } else if ("02".equals(electric.getOrderInfo())) {
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift2_right_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift2_right);
            }
        }else if(electric.getElectricType() == 3){
            if("01".equals(electric.getOrderInfo())){
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift3_left_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift3_left);
            } else if ("02".equals(electric.getOrderInfo())) {
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift3_center_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift3_center);
            }else if ("03".equals(electric.getOrderInfo())) {
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift3_right_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift3_right);
            }
        }else if(electric.getElectricType() == 4){
            if("01".equals(electric.getOrderInfo())){
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_left1_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_left1);
            } else if ("02".equals(electric.getOrderInfo())) {
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_left2_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_left2);
            }else if ("03".equals(electric.getOrderInfo())) {
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_right2_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_right2);
            }else if ("04".equals(electric.getOrderInfo())) {
                bitmapOn = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_right1_on);
                bitmapOff = BitmapFactory.decodeResource(getResources(), R.drawable.electric_type_swift4_right1);
            }
        }
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void addListener(){
        ibSwift.SetOnChangedListener(new SlipButton.OnChangedListener() {

            public void OnChanged(boolean CheckState) {
                if (CheckState) {
                    open(view);
                } else {
                    close(view);
                }
            }
        });
    }

    public void swiftOneEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }

    public void swiftOneSave(View view){
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

    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);

        initView();

    }

    public void swiftOneBack(View view){
        finish();
    }

    @Override
    public void updateUI() {
        electricState = mDC.mElectricState.get(electric.getElectricCode())[0];
        stateInfo = mDC.mElectricState.get(electric.getElectricCode())[1];
        int electricType = electric.getElectricType();
        if(electricType == 0){
            if(electricState.equals("ZV")){
                ivElectricImg.setImageBitmap(bitmapOn);
                ibSwift.setCheck(true);
            }else {
                ivElectricImg.setImageBitmap(bitmapOff);
                ibSwift.setCheck(false);
            }
        }else if(electricType == 1){
            if(electricState.equals("Z1")){
                ivElectricImg.setImageBitmap(bitmapOn);
                ibSwift.setCheck(true);
            }else {
                ivElectricImg.setImageResource(R.drawable.electric_type_swift1);
                ibSwift.setCheck(false);
            }
        }else if(electricType == 2){
            if(electric.getOrderInfo().equals("01")){
                if(electricState.equals("Z1") || electricState.equals("Z3")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else{
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }
            }else if(electric.getOrderInfo().equals("02")){
                if(electricState.equals("Z2") || electricState.equals("Z3")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else{
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }
            }

        }else if(electricType == 3){
            if(electric.getOrderInfo().equals("01")){
                if(electricState.equals("Z1") || electricState.equals("Z3")
                        || electricState.equals("Z5") || electricState.equals("Z7")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else {
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }
            }else if(electric.getOrderInfo().equals("02")){
                if(electricState.equals("Z2") || electricState.equals("Z3")
                        || electricState.equals("Z6") || electricState.equals("Z7")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else {
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }
            }else if (electric.getOrderInfo().equals("03")){
                if(electricState.equals("Z4") || electricState.equals("Z5")
                        || electricState.equals("Z6") || electricState.equals("Z7")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else{
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }
            }
        }else if (electricType == 4){
            if (electric.getOrderInfo().equals("01")){
                if(electricState.equals("Z1") || electricState.equals("Z3") || electricState.equals("Z5") || electricState.equals("Z7")
                        || electricState.equals("Z9") || electricState.equals("ZB") || electricState.equals("ZD") || electricState.equals("ZF")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else {
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }
            }else if(electric.getOrderInfo().equals("02")){
                if(electricState.equals("Z3") || electricState.equals("Z4") || electricState.equals("Z6") || electricState.equals("Z7")
                        || electricState.equals("ZA") || electricState.equals("ZB") || electricState.equals("ZE") || electricState.equals("ZF")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else {
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }

            }else if(electric.getOrderInfo().equals("03")){
                if(electricState.equals("Z4") || electricState.equals("Z5") || electricState.equals("Z6") || electricState.equals("Z7")
                        || electricState.equals("ZC") || electricState.equals("ZD") || electricState.equals("ZE") || electricState.equals("ZF")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else {
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }

            }else if(electric.getOrderInfo().equals("04")){
                if(electricState.equals("Z8") || electricState.equals("Z9") || electricState.equals("ZA") || electricState.equals("ZB")
                        || electricState.equals("ZC") || electricState.equals("ZD") || electricState.equals("ZE") || electricState.equals("ZF")){
                    ivElectricImg.setImageBitmap(bitmapOn);
                    ibSwift.setCheck(true);
                }else {
                    ivElectricImg.setImageBitmap(bitmapOff);
                    ibSwift.setCheck(false);
                }

            }
        }
    }
}
