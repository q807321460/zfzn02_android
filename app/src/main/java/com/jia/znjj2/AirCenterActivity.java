package com.jia.znjj2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.util.NetworkUtil;

public class AirCenterActivity extends ElectricBase implements View.OnClickListener {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private  TextView tv_temper;
    private EditText etElectricName;
    private LinearLayout ll_auto;
    private LinearLayout ll_cold;
    private LinearLayout ll_wind;
    private LinearLayout ll_heat;
    private LinearLayout ll_open;
    private LinearLayout ll_close;
    private LinearLayout ll_wind_low;
    private LinearLayout ll_wind_mid;
    private LinearLayout ll_wind_high;
    private TextView add;
    private TextView sub;
    String orderInfo="**********";
    char control;
    String temper="22";
    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(AirCenterActivity.this, "更改成功", Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(AirCenterActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_center);
        init();
        initView();

    }

private void initView(){
    tvTitleName = (TextView) findViewById(R.id.air_center_activity_title_name);
    tvTitleEdit = (TextView) findViewById(R.id.air_center_activity_title_edit);
    tvTitleSave = (TextView) findViewById(R.id.air_center_activity_title_save);
    etElectricName = (EditText) findViewById(R.id.air_center_name);
    tvTitleName.setText(electric.getElectricName());
    etElectricName.setText(electric.getElectricName());
    tvTitleEdit.setVisibility(View.VISIBLE);
    tvTitleSave.setVisibility(View.GONE);
    ll_auto= (LinearLayout) findViewById(R.id.left_ll_air_auto);
    ll_auto.setOnClickListener(this);
    ll_cold= (LinearLayout) findViewById(R.id.left_ll_air_cold);
    ll_cold.setOnClickListener(this);
    ll_wind= (LinearLayout) findViewById(R.id.left_ll_air_wind);
    ll_wind.setOnClickListener(this);
    ll_heat= (LinearLayout) findViewById(R.id.left_ll_air_heat);
    ll_heat.setOnClickListener(this);
    ll_open= (LinearLayout) findViewById(R.id.ll_air_open);
    ll_open.setOnClickListener(this);
    ll_wind_low= (LinearLayout) findViewById(R.id.ll_air_wind_low);
    ll_wind_low.setOnClickListener(this);
    ll_wind_mid= (LinearLayout) findViewById(R.id.ll_air_wind_mid);
    ll_wind_mid.setOnClickListener(this);
    ll_wind_high= (LinearLayout) findViewById(R.id.ll_air_wind_high);
    ll_wind_high.setOnClickListener(this);
    add= (TextView) findViewById(R.id.text_air_center_tempadd);
    add.setOnClickListener(this);
    add.setBackgroundResource(R.drawable.ic_button_round_selector);
    sub= (TextView) findViewById(R.id.text_air_center_tempsub);
    sub.setOnClickListener(this);
    sub.setBackgroundResource(R.drawable.ic_button_round_selector);
    tv_temper= (TextView) findViewById(R.id.tv_Temp_Text);
}
    @Override
    public void updateUI() {

    }

    public void airCenterActivityEdit(View view){
        // 创建PopupMenu对象
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void  airCenterOpen(View view){

    }
    public void airCenterActivitySave(View view){
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

    public void airCenterActivityBack(View view){
        finish();
    }
    private void changeToNormal() {
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);

        initView();
    }

        @Override
    public void onClick(View v) {

            switch (v.getId()){
                case R.id.left_ll_air_auto:
                    control=mDC.orderOpen;
                    break;
                case R.id.left_ll_air_cold:
                    control=mDC.airCold;
                    break;
                case R.id.left_ll_air_wind:
                    control=mDC.airWind;
                    break;
                case R.id.left_ll_air_heat:
                    control=mDC.airHeat;
                    break;
                case R.id.ll_air_open:
                    control=mDC.orderClose;
                    break;
                case R.id.ll_air_wind_low:
                    control=mDC.airWindLow;
                    break;
                case R.id.ll_air_wind_mid:
                    control=mDC.airWindMid;
                    break;
                case R.id.ll_air_wind_high:
                    control=mDC.airWindHigh;
                    break;
                case R.id.text_air_center_tempadd:
                    control=mDC.airSet;
                    if(Integer.parseInt(temper)<35) {
                        temper = String.valueOf(Integer.parseInt(temper) + 1);
                        tv_temper.setText(temper);
                    }else {
                        temper="35";
                    }
                    break;
                case R.id.text_air_center_tempsub:
                    control=mDC.airSet;
                    if(Integer.parseInt(temper)>16) {
                        temper = String.valueOf(Integer.parseInt(temper) - 1);
                        tv_temper.setText(temper);
                    }else {
                        temper="16";
                    }
                    break;

            }
            if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
                if(control==mDC.airSet){

                }else {
                   temper="**";
                }

                //if (true){
                new Thread(){
                    @Override
                    public void run() {
                        orderInfo=temper+ "********";
                        mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+control,orderInfo);
                        System.out.println("远程开电器：");
                    }
                }.start();

            }else{
                //本地socket通信
                orderInfo=temper+ "********";
                String order = "<" + electric.getElectricCode()+mDC.orderSign+control + orderInfo+ "00" + ">";
                System.out.println("本地开电气： "+order);
                NetworkUtil.out.println(order);
            }

    }
}
