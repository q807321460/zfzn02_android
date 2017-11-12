package com.jia.znjj2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.ir.global.ETGlobal;

public class ClothesHanger extends ElectricBase {

    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ImageView ivElectricImg;
    private EditText etElectricName;
    private TextView tvRoomName;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1191:
                    Toast.makeText(ClothesHanger.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1190:
                    Toast.makeText(ClothesHanger.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger);
        init();
        initView();
    }

    private void initView(){
        tvTitleName = (TextView) findViewById(R.id.clothes_hanger_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.clothes_hanger_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.clothes_hanger_title_save);
        ivElectricImg = (ImageView) findViewById(R.id.clothes_hanger_img);
        etElectricName = (EditText) findViewById(R.id.clothes_hanger_name);
        tvRoomName = (TextView) findViewById(R.id.clothes_hanger_room);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());
    }

    @Override
    public void updateUI() {

    }

    public void clothesHangerUp(View view){
        orderInfo = "01********";
        super.open(view);
    }
    public void clothesHangerStop(View view){
        orderInfo = "02********";
        super.open(view);
    }
    public void clothesHangerDown(View view){
        orderInfo = "03********";
        super.open(view);
    }
    public void clothesHangerLight(View view){
        orderInfo = "04********";
        super.open(view);
    }
    public void clothesHangerDisinfect(View view){
        orderInfo = "05********";
        super.open(view);
    }
    public void clothesHangerKilnDry(View view){
        orderInfo = "06********";
        super.open(view);
    }
    public void clothesHangerAirDry(View view){
        orderInfo = "07********";
        super.open(view);
    }
    public void clothesHangerClose(View view){
        orderInfo = "08********";
        super.open(view);
    }

    public void clothesHangerMatchCode(final View view){
        Dialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(R.string.str_study_info_2)
                .setPositiveButton(R.string.str_other_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                orderInfo = "00********";
                                 open(view);

                            }
                        })
                .setNegativeButton(R.string.str_other_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        }).create();
        dialog.setTitle(R.string.str_dialog_set_duima);
        dialog.show();


    }

    public void clothesHangerBack(View view){
        finish();
    }
    public void clothesHangerEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void clothesHangerSave(View view){
        final String electricName = etElectricName.getText().toString();
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.updateElectric(mDC.sMasterCode,electric.getElectricCode()
                        ,electric.getElectricIndex(),electricName,electric.getSceneIndex());
                Message msg = new Message();
                if(result.startsWith("1")){
                    msg.what = 0x1191;
                    electric.setElectricName(electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex(), electricName);
                    updateSceneElectricName(electric.getElectricIndex(),electricName);
                    mDC.mSceneElectricData.updateSceneElectric(electric.getElectricIndex(), electricName);
                }else {
                    msg.what = 0x1190;
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
}
