package com.jia.znjj2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CurtainDetail extends ElectricBase {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private TextView tvTextClose;
    private TextView tvTextOpen;
    private ImageView ivCurtainImg;
    private EditText etCurtainName;
    private TextView tvRoomName;

    private TextView tvCurtainPercent;
    private SeekBar curtainSeekBar;
    private int percent = 0;
    private int electricImg;
    private int electricImgOn;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1180:
                    Toast.makeText(CurtainDetail.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
                case 0x1181:
                    Toast.makeText(CurtainDetail.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain_detail);
        init();
        initView();
    }
    public void curtainDetailBack(View view) {
        finish();
    }
    private void initView() {
        tvTitleName = (TextView) findViewById(R.id.curtain_detail_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.curtain_detail_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.curtain_detail_title_save);
        tvTextClose = (TextView) findViewById(R.id.curtain_detail_text_close);
        tvTextOpen = (TextView) findViewById(R.id.curtain_detail_text_open);
        ivCurtainImg = (ImageView) findViewById(R.id.curtain_detail_img);
        etCurtainName = (EditText) findViewById(R.id.curtain_detail_name);
        tvRoomName = (TextView) findViewById(R.id.curtain_detail_room);
        curtainSeekBar = (SeekBar) findViewById(R.id.curtain_detail_seekBar);
        tvCurtainPercent = (TextView) findViewById(R.id.curtain_detail_percent);
        tvTitleName.setText(electric.getElectricName());
        etCurtainName.setText(electric.getElectricName());
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());
        orderInfo = electric.getOrderInfo() + "********";
        if(electric.getElectricType() == 6){
            electricImg = R.drawable.electric_type_curtain;
            electricImgOn = R.drawable.electric_type_curtain_on;
            //ivCurtainImg.setImageResource(R.drawable.electric_type_curtain);
        }else if(electric.getElectricType() == 7){
            electricImg = R.drawable.electric_type_window;
            electricImgOn = R.drawable.electric_type_window_on;
           // ivCurtainImg.setImageResource(R.drawable.electric_type_window);
            tvTextClose.setVisibility(View.GONE);
            tvTextOpen.setVisibility(View.GONE);
            tvCurtainPercent.setVisibility(View.GONE);
            curtainSeekBar.setVisibility(View.GONE);
        }else if(electric.getElectricType() == 11){
            electricImg = R.drawable.electric_type_arm_close1;
            electricImgOn = R.drawable.electric_type_armon;
           // ivCurtainImg.setImageResource(R.drawable.electric_type_arm);
            tvTextClose.setVisibility(View.GONE);
            tvTextOpen.setVisibility(View.GONE);
            tvCurtainPercent.setVisibility(View.GONE);
            curtainSeekBar.setVisibility(View.GONE);
        }

        curtainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percent = progress;
                tvCurtainPercent.setText("比例："+progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                orderInfo =percentToOrderInfo(percentToString(seekBar.getProgress()));
                openpercent();
            }
        });
    }

    public void openpercent(){
        super.open(new View(this));
    }

    @Override
    public void open(View view){
        percent=100;
        orderInfo =percentToOrderInfo(percentToString(percent));
        super.open(view);
    }

    @Override
    public void close(View view) {
        percent=0;
        orderInfo = percentToOrderInfo(percentToString(percent));
        super.close(view);
    }

    @Override
    public void updateUI() {
        System.out.println("进入窗帘更新程序");
        String electricCode = mDC.mAreaList.get(roomSequ).getmElectricInfoDataList().get(electricSequ).getElectricCode();
//        System.out.println("更新页面： 电器编号---->" +electricCode + "电器状态：" +
//                mDC.mElectricState.get(electricCode)[0] + "  " +
//                mDC.mElectricState.get(electricCode)[1]);
        //int percent = stateInfoToPercent(mDC.mElectricState.get(electricCode)[1]);
        String electricState = mDC.mElectricState.get(electricCode)[0];
        String stateInfo = mDC.mElectricState.get(electricCode)[1];

        if(electric.getElectricType() == 11){
            if("ZV".equals(electricState)){
                ivCurtainImg.setImageResource(R.drawable.electric_type_arm_on);
            }else {
                ivCurtainImg.setImageResource(R.drawable.electric_type_arm);
            }
        }else if(electric.getElectricType() == 7){
            if("ZV".equals(electricState)){
                ivCurtainImg.setImageResource(R.drawable.electric_type_window_on);
            }else {
                ivCurtainImg.setImageResource(R.drawable.electric_type_window);
            }
        }else if(electric.getElectricType() == 6){
            int percent = stateInfoToPercent(stateInfo);
            if(percent > 10){
                ivCurtainImg.setImageResource(R.drawable.electric_type_curtain_on);
            }else {
                ivCurtainImg.setImageResource(R.drawable.electric_type_curtain);
            }
            tvCurtainPercent.setText("状态："+percent+"%");
            System.out.println("窗帘打开的比例："+percent);
            curtainSeekBar.setProgress(percent);
        }

//        if(percent > 0){
//            ivCurtainImg.setImageResource(electricImgOn);
//        }else {
//            ivCurtainImg.setImageResource(electricImg);
//        }

    }

    private String percentToString(int n){
        if(n<16){
            return 0+Integer.toHexString(n);
        }
        return Integer.toHexString(n);
    }

    private String percentToOrderInfo(String password){
        StringBuffer sb = new StringBuffer(password);
        while (sb.length()<10){
            sb.append('*');
        }
        return sb.toString();
    }

    private int stateInfoToPercent(String str){
        String str2="00";
        if(str.length()>=2){
            str2 = str.substring(0,2);
        }
        if(str2.equals("**"))
            return 0;
        return Integer.parseInt(str2,16);
    }

    public void curtainDetailEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etCurtainName.setVisibility(View.VISIBLE);
        etCurtainName.setFocusable(true);
        etCurtainName.setFocusableInTouchMode(true);
        etCurtainName.requestFocus();
    }

    public void curtainDetailSave(View view){
        final String electricName = etCurtainName.getText().toString();
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.updateElectric(mDC.sMasterCode,electric.getElectricCode()
                        ,electric.getElectricIndex(),electricName,electric.getSceneIndex());
                Message msg = new Message();
                if(result.startsWith("1")){
                    msg.what = 0x1181;
                    electric.setElectricName(electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex(), electricName);
                    updateSceneElectricName(electric.getElectricIndex(),electricName);
                    mDC.mSceneElectricData.updateSceneElectric(electric.getElectricIndex(), electricName);
                }else {
                    msg.what = 0x1180;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void changeToNormal(){
        etCurtainName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etCurtainName.setVisibility(View.GONE);

        initView();

    }
}
