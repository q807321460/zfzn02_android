package com.jia.znjj2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 212 on 2017/9/21.
 */

public class Newdoor extends ElectricBase {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ImageView newDoorImg;
    private EditText etElectricName;
    private TextView tvRoomName;
    private TextView newDoorinfo;
    public Button doorInfobn;
    public Intent intent;
    public static String record;
    boolean mCliked=true;
    boolean isOpen=false;
    Timer timer=new Timer();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(Newdoor.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(Newdoor.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
                case 0x120:
                    Toast.makeText(Newdoor.this,"无门锁记录",Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdoor);
        init();
        initView();

    }

    private void initView() {
        tvTitleName = (TextView) findViewById(R.id.newdoor_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.newdoor_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.newdoor_title_save);
        newDoorImg = (ImageView) findViewById(R.id.newdoor_close_img);
        etElectricName = (EditText) findViewById(R.id.newdoor_name);
        tvRoomName = (TextView) findViewById(R.id.newdoor_room);
        newDoorinfo=(TextView)findViewById(R.id.newdoor_room_info);
        doorInfobn=(Button)findViewById(R.id.doorinfobn);
        doorInfobn.getBackground().setAlpha(150);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());


    }
    public void updateUI(){
        final String electricCode = electric.getElectricCode();
        String electricState = mDC.mElectricState.get(electric.getElectricCode())[0];
        String stateInfo = mDC.mElectricState.get(electric.getElectricCode())[1];
        System.out.println("更新页面： 电器编号---->" + electricCode + "电器状态：" +
                electricState + "  " + stateInfo);
        String flag=stateInfo.substring(0,1);
//        if (mDC.mElectricState.get(electricCode)[0].equals("ZW")) {
//            newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
//        }else if (mDC.mElectricState.get(electricCode)[0].equals("ZV")) {
//            newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_open));
//        }
        int i=Integer.parseInt(flag);
        if(flag.equals("A")){
            newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
            newDoorinfo.setText("非法用户，卡");

        }else if(flag.equals("B")){
            newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
            newDoorinfo.setText("门被撬");
        }else if(flag.equals("C")){
            newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
            newDoorinfo.setText("锁被撬");
        }else{
            switch (i) {
                case 1:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
                    newDoorinfo.setText("关锁成功");
                    break;
                case 2:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_open));
                    newDoorinfo.setText("电量足，开锁成功");
                    isOpen=true;
                    break;
                case 3:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
                    newDoorinfo.setText("电量足，关锁成功");
                    break;
                case 4:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_open));
                    newDoorinfo.setText("电量不足，开锁成功");
                    isOpen=true;
                    break;
                case 5:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
                    newDoorinfo.setText("电量不足，关锁成功");
                    break;
                case 6:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
                    newDoorinfo.setText("删除用户成功");
                    break;
                case 7:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
                    newDoorinfo.setText("门铃响");
                    break;
                case 8:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
                    newDoorinfo.setText("非法用户，指纹");
                    break;
                case 9:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
                    newDoorinfo.setText("非法用户，密码");
                    break;
                default:
                    newDoorImg.setImageDrawable(getResources().getDrawable(R.drawable.electric_type_newdoor_close));
            }
        }
        if(isOpen==true&& mCliked==true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.UpdateDoorOpenPerson(electricCode,mDC.sAccountCode);
                }
            }.start();
        }
    }

    public void newdoorOpen(View view){
        orderInfo = "01********";
        super.open(view);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mCliked=false;
                }
            },5000);


    }
    public void newdoorClose(View view){
        orderInfo = "02********";
        super.close(view);
    }
    public void newdoorBack(View view){finish();}
    public void newdoorSave(View view){
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
    public void doorRecord(View Button){
        new Thread(){
            public void run(){
                record = mDC.mWS.loadDoorRecord(electric.getMasterCode(),electric.getElectricCode());
                Message msg = new Message();
                if(record.equals("[]")){
                    msg.what=0x120;
                }else{
                    Intent intent= new Intent(Newdoor.this,NewdoorInfo.class);
                    startActivity(intent);
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    public void newdoorEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);
        initView();

    }
}
