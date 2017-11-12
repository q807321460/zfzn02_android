package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.data.SceneElectricData;
import com.jia.ir.db.ETDB;
import com.jia.ir.etclass.ETDevice;
import com.jia.ir.etclass.ETDeviceAIR;
import com.jia.ir.etclass.ETDeviceTV;
import com.jia.ir.etclass.ETKey;
import com.jia.util.Util;

import et.song.device.DeviceType;
import et.song.remote.face.IRKeyValue;

import static com.jia.znjj2.ElectricBase.hexStringToString;
import static et.song.remote.face.IRKeyValue.KEY_IPTV_POWER;

public class SceneAddElectricDetail extends Activity {

    private DataControl mDC;
    private int electricPosition;
    private int roomPosition;
    private int scenePosition;
    private ElectricInfoData electric;
    private String electricOrder;
    private String orderInfo;
    private ETDeviceAIR mDeviceAIR = null;
    private ETDeviceTV mDeviceTV = null;
    private TextView tvElectricName;
    private TextView tvRoomName;
    private Switch sElectricOrder;
    private String irCode;
    private String irCount;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1070:
                    Toast.makeText(SceneAddElectricDetail.this,"添加成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0x1071:
                    Toast.makeText(SceneAddElectricDetail.this,"已添加，请勿重复添加",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0x1072:
                    Toast.makeText(SceneAddElectricDetail.this,"请检查网络并重试",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_add_electric_detail);
        initView();
    }
    private void initView(){
        mDC = DataControl.getInstance();
        roomPosition = getIntent().getIntExtra("roomPosition", -1);
        electricPosition = getIntent().getIntExtra("electricPosition", -1);
        scenePosition = getIntent().getIntExtra("scenePosition", -1);
        electric = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(electricPosition);
        tvElectricName = (TextView) findViewById(R.id.scene_add_electric_detail_name);
        tvRoomName = (TextView) findViewById(R.id.scene_add_electric_detail_room);
        sElectricOrder = (Switch) findViewById(R.id.scene_add_electric_detail_switch);
        tvRoomName.setText(mDC.mAreaList.get(roomPosition).getRoomName());
        tvElectricName.setText(electric.getElectricName());
        try{

            int _type = DeviceType.DEVICE_REMOTE_AIR;
            mDeviceAIR = (ETDeviceAIR) ETDevice.Builder(_type);
            mDeviceAIR.setmMasterCode(electric.getMasterCode());
            mDeviceAIR.setmElectricIndex(electric.getElectricIndex());
            mDeviceAIR.SetName(electric.getElectricName());
            mDeviceAIR.SetType(_type);
            mDeviceAIR.SetRes(7);
            mDeviceAIR.Load(ETDB.getInstance(this));
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {

            int _type = DeviceType.DEVICE_REMOTE_TV;
            mDeviceTV = (ETDeviceTV) ETDevice.Builder(_type);
            mDeviceTV.setmMasterCode(electric.getMasterCode());
            mDeviceTV.setmElectricIndex(electric.getElectricIndex());
            mDeviceTV.SetName(electric.getElectricName());
            mDeviceTV.SetType(_type);
            mDeviceTV.SetRes(7);
            mDeviceTV.Load(ETDB.getInstance(this));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sceneAddElectricDetailBack(View view){
        finish();
    }

    public void sceneAddElectricDetailSave(View view){
        byte[] keyValue = null;
        int key = 0;
        ETKey k=null;
        System.out.println("添加情景电器");
        if(electric.getElectricCode().startsWith("09")){

            if(electric.getElectricType()==9){//空调码库

                if(sElectricOrder.isChecked()){//开
                   key= IRKeyValue.KEY_AIR_POWER;
                    mDeviceAIR.SetPower((byte) 0);
                }else {//关
                    key= IRKeyValue.KEY_AIR_POWER;
                    mDeviceAIR.SetPower((byte) 1);
                }
                try {
                    irCode = bytestoIrCode(mDeviceAIR.GetKeyValue(key));
                    //sendOrder(keyValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(electric.getElectricType()==21){//空调学习
                if(sElectricOrder.isChecked()){//开
                   key=mDC.KEY_AIR_POWER;
                }else {//关
                   key=mDC.KEY_AIR_POWER_OFF;
                }
                k = mDeviceAIR.GetKeyByValue(key);
                irCode=hexStringToString(bytestoIrCode(k.GetValue()));
            }else if(electric.getElectricType()==12){//电视机码库
                if(sElectricOrder.isChecked()){//开
                    key=IRKeyValue.KEY_TV_POWER;
                }else {//关
                    key=IRKeyValue.KEY_TV_POWER;
                }
                try {
                    irCode = bytestoIrCode(mDeviceTV.GetKeyValue(key));
                    //sendOrder(keyValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(electric.getElectricType()==24){//电视机学习
                if(sElectricOrder.isChecked()){//开
                    key=mDC.KEY_TV_POWER ;
                }else {//关
                    key=mDC.KEY_TV_POWER ;
                }
                k = mDeviceTV.GetKeyByValue(key);
                irCode=hexStringToString(bytestoIrCode(k.GetValue()));
                // irCode= String.valueOf(k.GetValue());

            }

            electricOrder="SM";
            if(irCode==null){
                Toast.makeText(this,"设备还未学习",Toast.LENGTH_LONG).show();
                return;
            }else {
                if(irCode.length()==2){
                    irCount="02";
                }else {
                  irCount= Integer.toHexString(irCode.length());
                }

                orderInfo =irCount+irCode;
            }
            if(orderInfo==null){
                Toast.makeText(this,"未学习，无法加入",Toast.LENGTH_LONG).show();
                return;
            }else {
                new Thread(){
                    @Override
                    public void run() {
                        String result = mDC.mWS.addSceneElectric(mDC.sMasterCode, electric.getElectricCode(), electricOrder, mDC.sAccountCode,
                                mDC.mSceneList.get(scenePosition).getSceneIndex(), orderInfo,electric.getElectricIndex(),
                                electric.getElectricName(),mDC.mAreaList.get(roomPosition).getRoomIndex(),electric.getElectricType());
                        SceneElectricData.SceneElectricInfo sceneElectricInfo = new SceneElectricData().new SceneElectricInfo();

                        Message msg = new Message();
                        if(result.startsWith("1")){
                            mDC.mSceneElectricData.addSceneElectric(mDC.sMasterCode, electric.getElectricCode(), electricOrder, mDC.sAccountCode,
                                    mDC.mSceneList.get(scenePosition).getSceneIndex(), orderInfo,electric.getElectricIndex(),
                                    electric.getElectricName(),mDC.mAreaList.get(roomPosition).getRoomIndex(),electric.getElectricType());
                            mDC.mSceneData.loadSceneList();
                            msg.what = 0x1070;
                        }else if(result.startsWith("0")){
                            msg.what = 0x1071;
                        }else if(result.startsWith("-2")){
                            msg.what = 0x1072;
                        }
                        handler.sendMessage(msg);
                    }
                }.start();
            }


        }else {
            if(sElectricOrder.isChecked()){
                electricOrder = "SH";
            }else {
                electricOrder = "SG";
            }
            orderInfo = electric.getOrderInfo();
            new Thread(){
                @Override
                public void run() {
                    String result = mDC.mWS.addSceneElectric(mDC.sMasterCode, electric.getElectricCode(), electricOrder, mDC.sAccountCode,
                            mDC.mSceneList.get(scenePosition).getSceneIndex(), orderInfo,electric.getElectricIndex(),
                            electric.getElectricName(),mDC.mAreaList.get(roomPosition).getRoomIndex(),electric.getElectricType());
                    SceneElectricData.SceneElectricInfo sceneElectricInfo = new SceneElectricData().new SceneElectricInfo();

                    Message msg = new Message();
                    if(result.startsWith("1")){
                        mDC.mSceneElectricData.addSceneElectric(mDC.sMasterCode, electric.getElectricCode(), electricOrder, mDC.sAccountCode,
                                mDC.mSceneList.get(scenePosition).getSceneIndex(), orderInfo,electric.getElectricIndex(),
                                electric.getElectricName(),mDC.mAreaList.get(roomPosition).getRoomIndex(),electric.getElectricType());
                        mDC.mSceneData.loadSceneList();
                        msg.what = 0x1070;
                    }else if(result.startsWith("0")){
                        msg.what = 0x1071;
                    }else if(result.startsWith("-2")){
                        msg.what = 0x1072;
                    }
                    handler.sendMessage(msg);
                }
            }.start();
        }

        }
    private String bytestoIrCode(byte[] bytes){
        String irCode = "";
        if(bytes==null){
            return null;

        }else {
            if(bytes.length == 4){
                irCode = String.valueOf(bytes);
            }else {
                irCode = bytes2Order(bytes);
            }
            return irCode;
        }

    }
    private String bytes2Order(byte[] bytes){
        String irCode = Util.byte2hex(bytes);
        System.out.println("红外码："+irCode);
        return irCode;
    }

}
