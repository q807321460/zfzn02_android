package com.jia.znjj2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.util.NetworkUtil;

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
    private SeekBar redSB;
    private SeekBar greenSB;
    private SeekBar blueSB;
    private SeekBar lightSB;
    private SeekBar timeSB;
    private TextView redTX;
    private TextView greenTX;
    private TextView blueTX;
    private TextView timeTX;
    private TextView lightTX;
    private ImageView imcolorview;
    private CheckBox glchange;
    private CheckBox jpchange;
    private String changeType;
    private String lightType;
    private String timeType;
    private List colorlist;
    private String beltorder;
    private static int redrgb;
    private static int greenrgb;
    private static int bluergb ;

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
        colorlist = new ArrayList();
        redSB = (SeekBar)findViewById(R.id.red_progress);
        greenSB = (SeekBar)findViewById(R.id.green_progress);
        blueSB = (SeekBar)findViewById(R.id.blue_progress);
        timeSB = (SeekBar)findViewById(R.id.sb_lamp_belt_time);
        lightSB = (SeekBar)findViewById(R.id.sb_lamp_belt_light);
        redTX = (TextView)findViewById(R.id.red_progress_text);
        greenTX = (TextView)findViewById(R.id.green_progress_text);
        blueTX = (TextView)findViewById(R.id.blue_progress_text);
        imcolorview = (ImageView)findViewById(R.id.color_image_view);
        timeTX = (TextView) findViewById(R.id.tx_lamp_belt_time);
        lightTX = (TextView)findViewById(R.id.tx_lamp_belt_light);
        glchange = (CheckBox)findViewById(R.id.gradual_change);
        jpchange = (CheckBox)findViewById(R.id.jump_change);
        glchange.setOnClickListener(this);
        jpchange.setOnClickListener(this);
        redrgb=0;
        greenrgb=0;
        bluergb=0;
        setColor();
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

    private void setColor() {
        redSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                redTX.setText("红色" + Integer.toString(progress));
                redrgb = progress;
                imcolorview.setBackgroundColor(Color.rgb(redrgb,greenrgb,bluergb));
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
        greenSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                greenTX.setText("绿色" + Integer.toString(progress));
                greenrgb = progress;
                imcolorview.setBackgroundColor(Color.rgb(redrgb,greenrgb,bluergb));
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
        blueSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                blueTX.setText("蓝色" + Integer.toString(progress));
                bluergb = progress;
                imcolorview.setBackgroundColor(Color.rgb(redrgb,greenrgb,bluergb));
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
    public void LampBeltColorList(View view){
        String red1 = Integer.toHexString(redrgb).toString().toUpperCase();
        if(red1.length()<2){
            String red = "0"+red1;
            red1 = red;
        }
        String green1 = Integer.toHexString(greenrgb).toString().toUpperCase();
        if(green1.length()<2){
            String green = "0"+green1;
            green1 = green;
        }
        String blue1 = Integer.toHexString(bluergb).toString().toUpperCase();
        if(blue1.length()<2){
            String blue = "0"+blue1;
            blue1 = blue;
        }
        String color = blue1+green1+red1;
        colorlist.add(color);
        Toast.makeText(LampbeltActivity.this,"第"+colorlist.size()+"组颜色已选中",Toast.LENGTH_LONG).show();
    }
    public void BeltLampOpen(View view){
        if((redrgb==0)&&(bluergb==0)&&(greenrgb==0)){
            Toast.makeText(LampbeltActivity.this,"请先设置颜色",Toast.LENGTH_LONG).show();
        }else{
        if(colorlist.size()<5) {
            beltorder = lightType + timeType + changeType;
            int n = colorlist.size();
            if(n<10){
                beltorder = beltorder+ "0"+Integer.toString(n);
            }else{
                beltorder = beltorder+Integer.toString(n);
            }
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
        redSB.setProgress(0);
        greenSB.setProgress(0);
        blueSB.setProgress(0);
        Toast.makeText(LampbeltActivity.this,"灯带颜色组合已经清除，请重新添加颜色组合",Toast.LENGTH_LONG).show();

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

