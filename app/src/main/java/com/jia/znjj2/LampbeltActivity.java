package com.jia.znjj2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.util.NetworkUtil;
import com.jia.widget.ColorPickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShengYi on 2018/5/3.
 */

public class LampbeltActivity extends ElectricBase implements View.OnClickListener {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private EditText etElectricName;
    private TextView tvRoomName;
    private ColorPickerView colorPickerView;
    private SeekBar lightSB;
    private SeekBar timeSB;
    private TextView timeTX;
    private TextView lightTX;
    private TextView tv;
    private CheckBox glchange;
    private CheckBox jpchange;
    private String changeType;
    private String lightType;
    private String timeType;
    private LinearLayout cpView;
    private List colorlist;
    private String beltorder;
    private String redcolor;
    private String greencolor;
    private String bluecolor;
    private String beltcolor;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1201:
                    Toast.makeText(LampbeltActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1200:
                    Toast.makeText(LampbeltActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_belt);
        initView();
        beltcolor = "";
        colorlist = new ArrayList();
        timeSB = (SeekBar)findViewById(R.id.sb_lamp_belt_time);
        lightSB = (SeekBar)findViewById(R.id.sb_lamp_belt_light);
        timeTX = (TextView) findViewById(R.id.tx_lamp_belt_time);
        lightTX = (TextView)findViewById(R.id.tx_lamp_belt_light);
        tv = (TextView)findViewById(R.id.txview_lamp_belt_color);
        glchange = (CheckBox)findViewById(R.id.gradual_change);
        jpchange = (CheckBox)findViewById(R.id.jump_change);
        glchange.setOnClickListener(this);
        jpchange.setOnClickListener(this);
        cpView = (LinearLayout) findViewById(R.id.color_picker_view);
        colorPickerView = new ColorPickerView(this);
        cpView.addView(colorPickerView);
        colorPickerView.setOnColorBackListener(new ColorPickerView.OnColorBackListener() {
            @Override
            public void onColorBack(int a, int r, int g, int b) {
                if(r==0){
                    redcolor = "00";
                }else if(r<16){
                    redcolor = "0"+Integer.toHexString(r).toUpperCase();
                }else if(r>=16){
                    redcolor = Integer.toHexString(r).toUpperCase();
                }
                if(g==0){
                    greencolor = "00";
                }else if(g<16){
                    greencolor = "0"+Integer.toHexString(g).toUpperCase();
                }else if(g>=16){
                    greencolor = Integer.toHexString(g).toUpperCase();
                }
                if(b==0){
                    bluecolor = "00";
                }else if(b<16){
                    bluecolor = "0"+Integer.toHexString(b).toUpperCase();
                }else if(b>=16){
                    bluecolor = Integer.toHexString(b).toUpperCase();
                }
                tv.setText("颜色（RGB):"+redcolor+greencolor+bluecolor);
                tv.setTextColor(Color.argb(a, r, g, b));
                beltcolor = redcolor+greencolor+bluecolor;
            }
        });
        setlightandtime();
    }


    private void setlightandtime() {
        timeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                if(Integer.toString(progress).length()<2){
                    timeType = "0"+Integer.toString(progress);
                }else {
                    timeType = Integer.toString(progress);
                }
                timeTX.setText("时间间隔:" + timeType);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "停止滑动！");
            }
        });
        lightSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                lightType = "0"+Integer.toString(progress+1);
                lightTX.setText("亮度等级:" + lightType);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "停止滑动！");
            }
        });
    }

    public void BeltLampOpen(View view){
        if(colorlist.size()==0||beltcolor == ""){
                Toast.makeText(LampbeltActivity.this,"请先设置一种颜色",Toast.LENGTH_LONG).show();
        }else{
        if(colorlist.size()<5) {
            beltorder = lightType + timeType + changeType;
            int n = colorlist.size();
            beltorder = beltorder+ "0"+Integer.toString(n);
            for(int i=0;i<n;i++){
                beltorder=beltorder+colorlist.get(i);
            }
            if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
                //if (true){
                new Thread(){
                    @Override
                    public void run() {
                        mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+mDC.airSet,beltorder);
                        System.out.println("远程关电器：");
                    }
                }.start();

            }else{
                //本地socket通信
                String order1 = "<" + electric.getElectricCode()+mDC.orderSign+mDC.airSet +beltorder + "FF"+">";
                System.out.println("本地开电气： "+order1);
                NetworkUtil.out.println(order1);
            }
        }else{
            Toast.makeText(LampbeltActivity.this,"设置颜色过多",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void BeltLampLast(View view){
        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+mDC.orderOpen,"**********");
                    System.out.println("远程开电器：");
                }
            }.start();

        }else{
            //本地socket通信
            String order1 = "<" + electric.getElectricCode()+mDC.orderSign+mDC.orderOpen +"**********" + "FF"+">";
            System.out.println("本地开电气： "+order1);
            NetworkUtil.out.println(order1);
        }
    }
    public void BeltLampClose(View view){
        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+mDC.orderClose,"**********");
                    System.out.println("远程关电器：");
                }
            }.start();

        }else{
            //本地socket通信
            String order1 = "<" + electric.getElectricCode()+mDC.orderSign+mDC.orderClose +"**********" + "FF"+">";
            System.out.println("本地关电气： "+order1);
            NetworkUtil.out.println(order1);
        }
    }
    public void BeltLampClear(View view){
        colorlist.clear();
        Toast.makeText(LampbeltActivity.this,"灯带颜色组合已经清除，请重新添加颜色组合",Toast.LENGTH_LONG).show();

    }

    public void BeltLampColorSure(View view){
        if(beltcolor == ""){
            Toast.makeText(LampbeltActivity.this,"请在色带处选择颜色",Toast.LENGTH_LONG).show();
        }else{
            colorlist.add(beltcolor);
            Toast.makeText(LampbeltActivity.this,"颜色选择成功",Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void updateUI() {

    }

    private void initView() {
        timeType = "00";
        lightType = "01";
        changeType = "00";
        tvTitleName = (TextView) findViewById(R.id.lamp_belt_name);
        tvTitleEdit = (TextView) findViewById(R.id.lamp_belt_edit);
        tvTitleSave = (TextView) findViewById(R.id.lamp_belt_save);
        tvRoomName = (TextView)findViewById(R.id.lamp_belt_room);
        etElectricName = (EditText) findViewById(R.id.ed_lamp_belt_name);
        tvTitleName.setText(electric.getElectricName());
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());
    }

    public void lampbeltBack(View view){
        finish();
    }
    public void LampBeltEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void LampBeltSave(View view){
        final String electricName = etElectricName.getText().toString();
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.updateElectric(mDC.sMasterCode,electric.getElectricCode()
                        ,electric.getElectricIndex(),electricName,electric.getSceneIndex());
                Message msg = new Message();
                if(result.startsWith("1")){
                    msg.what = 0x1201;
                    electric.setElectricName(electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex(), electricName);
                    updateSceneElectricName(electric.getElectricIndex(),electricName);
                    mDC.mSceneElectricData.updateSceneElectric(electric.getElectricIndex(), electricName);
                }else {
                    msg.what = 0x1200;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gradual_change:
                glchange.setChecked(true);
                jpchange.setChecked(false);
                changeType = "00";
                break;
            case R.id.jump_change:
                glchange.setChecked(false);
                jpchange.setChecked(true);
                changeType = "01";
                break;
            default:
                break;
        }
    }
}

