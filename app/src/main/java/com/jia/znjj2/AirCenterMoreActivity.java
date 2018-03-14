package com.jia.znjj2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.util.NetworkUtil;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class AirCenterMoreActivity extends ElectricBase implements View.OnClickListener {
    public static int aircenterelectricIndex;
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ImageButton ibAddAircenter;
    private CheckBox cbCheckAll;
    String  strNumber=null;
    String order=null;
    String stAllCheck="FFFFFF";
    int i;
    private Button btOpen;
    private Button btClose;
    private Button btCold;
    private Button btHot;
    private Button btWind;
    private Button btHumit;
    private Button btHigh;
    private Button btMid;
    private Button btLow;
    private Button bt18;
    private Button bt22;
    private Button bt26;
    private Button bt30;
    private TreeMap<String,Integer> checkedStr;
    //操作取消一个时，全选取消，这个变量是是否是用户点击
    private boolean checkForAll=false;
    private String stOrder;
    private String stCount;
    private EditText etElectricName;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(AirCenterMoreActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(AirCenterMoreActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_center_more);
        initView();
        aircenterelectricIndex=electric.getElectricIndex();
    }

    @Override
    public void updateUI() {

    }
    public void AirMoreEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void AirMoreSave(View view){
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
    private void initView(){
        tvTitleName = (TextView)findViewById(R.id.air_center_more_name);
        tvTitleEdit = (TextView)findViewById(R.id.air_center_more_edit);
        tvTitleSave = (TextView)findViewById(R.id.air_center_more_save);
        ibAddAircenter = (ImageButton)findViewById(R.id.air_center_more_img_add);
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);

        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        etElectricName= (EditText) findViewById(R.id.air_more_name);
        cbCheckAll= (CheckBox) findViewById(R.id.air_center_more_selectall);
        cbCheckAll.setOnCheckedChangeListener(listener);
        checkedStr=new TreeMap<String, Integer>();
        btOpen= (Button) findViewById(R.id.rb_open);
        btOpen.setOnClickListener(this);
        btClose= (Button) findViewById(R.id.rb_close);
        btClose.setOnClickListener(this);
        btCold= (Button) findViewById(R.id.rb_cold);
        btCold.setOnClickListener(this);
        btHot= (Button) findViewById(R.id.rb_hot);
        btHot.setOnClickListener(this);
        btWind= (Button) findViewById(R.id.rb_wind);
        btWind.setOnClickListener(this);
        btHumit= (Button) findViewById(R.id.rb_humit);
        btHumit.setOnClickListener(this);
        btHigh= (Button) findViewById(R.id.rb_high);
        btHigh.setOnClickListener(this);
        btMid= (Button) findViewById(R.id.rb_mid);
        btMid.setOnClickListener(this);
        btLow= (Button) findViewById(R.id.rb_low);
        btLow.setOnClickListener(this);
        bt18= (Button) findViewById(R.id.rb_18);
        bt18.setOnClickListener(this);
        bt22= (Button) findViewById(R.id.rb_22);
        bt22.setOnClickListener(this);
        bt26= (Button) findViewById(R.id.rb_26);
        bt26.setOnClickListener(this);
        bt30= (Button) findViewById(R.id.rb_30);
        bt30.setOnClickListener(this);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());

    }
   public void addAirCenter(View view){
        Intent intent = new Intent(AirCenterMoreActivity.this,AddAirCenterMore.class);
        startActivity(intent);
   }
    public void AirMoreBack(View view){
        finish();
    }
    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
        {
            switch(buttonView.getId())
            {
                case R.id.air_center_more_selectall:
                    if(isChecked){
                       checkForAll=true;

                    }else {
                        checkForAll=false;
                    }

                    break;

            }
        }
    };
    private void onDeal(String str1,String firstCount){
        i = checkedStr.size();
        strNumber= String.valueOf(i);
        int checkSum = 0;
        int count=0;
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Integer>> entryseSet=checkedStr.entrySet();
        for (Map.Entry<String, Integer> entry:entryseSet) {
            sb.append(entry.getKey());
            checkSum = checkSum + entry.getValue();
        }
        count=checkSum+Integer.parseInt(strNumber,10)+Integer.parseInt(firstCount,16);
        int mod = count % 256;
        String hex = Integer.toHexString(mod);
        int len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        if(strNumber.length()<2){
            strNumber="0"+strNumber;
        }
        if(checkForAll==false) {
            order = str1 + strNumber + sb + hex;
        }else {
            order = str1 + stAllCheck + firstCount;
        }
        if(order.length()>13) {
            open(order);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb_open:
                stOrder="013101";
                if(checkForAll==false){
                    stCount="33";
                }else {
                    stCount="30";
                }

                onDeal(stOrder,stCount);
                break;
            case R.id.rb_close:
                stOrder="013102";
                if(checkForAll==false){
                    stCount="34";
                }else {
                    stCount="31";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_cold:
                stOrder="013301";
                if(checkForAll==false){
                    stCount="35";
                }else {
                    stCount="32";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_hot:
                stOrder="013308";
                if(checkForAll==false){
                    stCount="3C";
                }else {
                    stCount="39";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_wind:
                stOrder="013304";
                if(checkForAll==false){
                    stCount="38";
                }else {
                    stCount="35";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_humit:
                stOrder="013302";
                if(checkForAll==false){
                    stCount="33";
                }else {
                    stCount="33";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_high:
                stOrder="013401";
                if(checkForAll==false){
                    stCount="36";
                }else {
                    stCount="33";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_mid:
                stOrder="013402";
                if(checkForAll==false){
                    stCount="37";
                }else {
                    stCount="34";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_low:
                stOrder="013404";
                if(checkForAll==false){
                    stCount="39";
                }else {
                    stCount="36";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_18:
                stOrder="013212";
                if(checkForAll==false){
                    stCount="45";
                }else {
                    stCount="42";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_22:
                stOrder="013216";
                if(checkForAll==false){
                    stCount="49";
                }else {
                    stCount="46";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_26:
                stOrder="01321A";
                if(checkForAll==false){
                    stCount="4D";
                }else {
                    stCount="4A";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_30:
                stOrder="01321E";
                if(checkForAll==false){
                    stCount="51";
                }else {
                    stCount="4E";
                }
                onDeal(stOrder,stCount);
                break;
            default:

        }
    }
    public void open(final String order2){
        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+mDC.airSet,order2);
                    System.out.println("远程开电器：");
                }
            }.start();

        }else{
            //本地socket通信
            String order1 = "<" + electric.getElectricCode()+mDC.orderSign+mDC.airSet + order2 + "FF"+">";
            System.out.println("本地开电气： "+order1);
            NetworkUtil.out.println(order1);
        }
    }
}
