package com.jia.znjj2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;

/**
 * Created by ShengYi on 2018/5/9.
 */

public class DuplexSwiftActivity extends ElectricBase{
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ImageView ivElectricImg;
    private EditText etElectricName;
    private TextView tvRoomName;
    private TextView bdSwiftName;
    private ImageView bdSwiftImg;
    private DataControl mDC;
    private int rmIndex;
    private int ecIndex;
    private String ecCode;
    private int ecType;
    private String ecGetorder;
    private ProgressDialog dialog;
    private ProgressDialog dialog1;
    private String ecExtras;
    private int page;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1201:
                    Toast.makeText(DuplexSwiftActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1200:
                    Toast.makeText(DuplexSwiftActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
                case 0x1300:
                    Toast.makeText(DuplexSwiftActivity.this,"开关绑定失败，请检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1301:
                    Toast.makeText(DuplexSwiftActivity.this,"开关绑定失败，请稍后重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1302:
                    mDC.mAreaData.loadAreaList();
                    dialog.cancel();
                    Toast.makeText(DuplexSwiftActivity.this,"开关绑定成功",Toast.LENGTH_LONG).show();
                    break;
                case 0x1303:
                    Toast.makeText(DuplexSwiftActivity.this,"解除绑定失败，请检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1304:
                    Toast.makeText(DuplexSwiftActivity.this,"解除绑定失败，请稍后重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1305:
                    mDC.mAreaData.loadAreaList();
                    bdSwiftName.setText("无绑定电器,请添加绑定");
                    bdSwiftImg.setVisibility(View.GONE);
                    dialog1.cancel();
                    Toast.makeText(DuplexSwiftActivity.this,"解除绑定成功",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplex_swift);
        initView();
        BindSwiftShow(ecExtras);

    }
    protected void onResume() {
        super.onResume();
        initView();
        if(BingdingSwiftActivity.checkSwiftChange==1){
        updateBindingSwift();
        }
    }

    private void updateBindingSwift() {
        ecIndex = BingdingSwiftActivity.BindingSwiftIndex;
        rmIndex = BingdingSwiftActivity.SwiftRoomIndex;
        for(int i=0;i<mDC.mAreaList.get(rmIndex).getmElectricInfoDataList().size();i++){
            if(mDC.mAreaList.get(rmIndex).getmElectricInfoDataList().get(i).getElectricIndex()==ecIndex){
                ecCode = mDC.mAreaList.get(rmIndex).getmElectricInfoDataList().get(i).getElectricCode();
                ecGetorder = mDC.mAreaList.get(rmIndex).getmElectricInfoDataList().get(i).getOrderInfo();
                ecType = mDC.mAreaList.get(rmIndex).getmElectricInfoDataList().get(i).getElectricType();
                bdSwiftName.setText(mDC.mAreaList.get(rmIndex).getRoomName()+":"+mDC.mAreaList.get(rmIndex).getmElectricInfoDataList().get(i).getElectricName());
                bdSwiftImg.setVisibility(View.VISIBLE);
                if(ecType == 1){
                    bdSwiftImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift1));
                }else  if(ecType == 2){
                    bdSwiftImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift2));
                }else if(ecType == 3){
                    bdSwiftImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift3));
                    }
                }
            }
        }



    private void initView() {
        mDC = DataControl.getInstance();
        tvTitleName = (TextView)findViewById(R.id.duplex_swift_title_name);
        tvTitleEdit = (TextView)findViewById(R.id.duplex_swift_title_edit);
        tvTitleSave = (TextView)findViewById(R.id.duplex_swift_title_save);
        ivElectricImg = (ImageView)findViewById(R.id.duplex_swift_img);
        etElectricName = (EditText)findViewById(R.id.duplex_swift_name);
        tvRoomName = (TextView)findViewById(R.id.duplex_swift_room);
        bdSwiftName = (TextView)findViewById(R.id.binding_swift_electric_name);
        bdSwiftImg = (ImageView)findViewById(R.id.binding_swift_electric_img);
        if(electric.getOrderInfo().equals("01")){
            ivElectricImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift3_left));
        }else if(electric.getOrderInfo().equals("02")){
            ivElectricImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift3_center));
        }else if(electric.getOrderInfo().equals("03")){
            ivElectricImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift3_right));
        }
        ecExtras = electric.getExtras();

    }
    public void DuplexSwiftBack(View view){
        BingdingSwiftActivity.checkSwiftChange = 0;
        finish();
    }
    public void updateUI(){}
    public void DuplexSwiftEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void DuplexSwiftSave(View view){
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
    public void BindSwift(View view){
        electric = mDC.mAreaList.get(roomSequ).getmElectricInfoDataList().get(electricSequ);
        String abc = electric.getExtras();
        Intent intent = new Intent(DuplexSwiftActivity.this,BingdingSwiftActivity.class);
        startActivity(intent);
    }
    public void DeleteBindingSwift(View view){
        electric = mDC.mAreaList.get(roomSequ).getmElectricInfoDataList().get(electricSequ);
        String abc = electric.getExtras();
        if((abc==null)||(abc.equals("anyType{}"))||(abc=="")){
            Toast.makeText(DuplexSwiftActivity.this,"请先绑定电器",Toast.LENGTH_LONG).show();
        }else{
        dialog1 = new ProgressDialog(this);
        dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setTitle("提示");
        dialog1.setMessage("正在解除绑定");
        dialog1.show();
        new Thread(){
            public void run(){
                Message msg = new Message();
                String result = mDC.mWS.deleteDuplexSwift(mDC.sMasterCode,electric.getElectricIndex());
                mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),DuplexSwiftActivity.this);
                if(result.startsWith("-2")){
                    msg.what = 0x1303;
                    handler.sendMessage(msg);
                }else if(result.startsWith("-1")){
                    msg.what = 0x1304;
                    handler.sendMessage(msg);
                } else{
                    close();
                    msg.what=0x1305;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
    }
    public void BindSwiftSure(View view){
        electric = mDC.mAreaList.get(roomSequ).getmElectricInfoDataList().get(electricSequ);
        String abc = electric.getExtras();
        if(bdSwiftName.getText().toString().equals("无绑定电器,请添加绑定")){
            Toast.makeText(DuplexSwiftActivity.this,"请先绑定电器",Toast.LENGTH_LONG).show();
        }else{
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在添加绑定开关");
        dialog.show();
        new Thread(){
            public void run(){
                Message msg = new Message();
                String result = mDC.mWS.bindingDuplexSwift(mDC.sMasterCode,electric.getElectricIndex(),Integer.toString(rmIndex),Integer.toString(ecIndex));
                mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),DuplexSwiftActivity.this);
                if(result.startsWith("-2")){
                    msg.what = 0x1300;
                    handler.sendMessage(msg);
                }else if(result.startsWith("-1")){
                    msg.what = 0x1301;
                    handler.sendMessage(msg);
                } else{
                    open();
                    msg.what=0x1302;
                    handler.sendMessage(msg);
                    }
                }
            }.start();
        }
    }
    public void open(){
        new Thread(){
            @Override
            public void run() {
                mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.airSet+mDC.orderOpen,electric.getOrderInfo()+ecCode+mDC.orderSign +mDC.orderOpen+ecGetorder);
                System.out.println("远程开电器：");
            }
        }.start();
    }
    public void close(){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.airSet+mDC.deleteBinding,electric.getOrderInfo()+ecCode+mDC.orderSign +mDC.orderOpen+ecGetorder);
                    System.out.println("远程开电器：");
                }
            }.start();

    }
    public void BindSwiftShow(String extras ){
        if((extras==null)||(extras.equals("anyType{}"))||(extras=="")){
            bdSwiftName.setText("无绑定电器,请添加绑定");
            bdSwiftImg.setVisibility(View.GONE);
        }else{
           String[] abc = extras.split("-");
            int roomIndex =Integer.parseInt(abc[0]);
            int swiftIndex = Integer.parseInt(abc[1]);
            for(int i=0;i<mDC.mAreaList.get(roomIndex).getmElectricInfoDataList().size();i++){
                if(mDC.mAreaList.get(roomIndex).getmElectricInfoDataList().get(i).getElectricIndex()==swiftIndex){
                    bdSwiftName.setText(mDC.mAreaList.get(roomIndex).getRoomName()+":"+mDC.mAreaList.get(roomIndex).getmElectricInfoDataList().get(i).getElectricName());
                    bdSwiftImg.setVisibility(View.VISIBLE);
                    ecCode = mDC.mAreaList.get(roomIndex).getmElectricInfoDataList().get(i).getElectricCode();
                    ecGetorder = mDC.mAreaList.get(roomIndex).getmElectricInfoDataList().get(i).getOrderInfo();
                    ecType = mDC.mAreaList.get(roomIndex).getmElectricInfoDataList().get(i).getElectricType();
                    int a = mDC.mAreaList.get(roomIndex).getmElectricInfoDataList().get(i).getElectricType();
                    if(a == 1){
                        bdSwiftImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift1));
                    }else  if(a == 2){
                        bdSwiftImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift2));
                    }else if(a == 3){
                        bdSwiftImg.setBackground(getResources().getDrawable(R.drawable.electric_type_swift3));
                    }
                }
            }
        }

    }
    }


