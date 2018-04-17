package com.jia.znjj2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;


/**
 * Created by ShengYi on 2018/3/13.
 */

public class AddAirCenterMore extends Activity {
    private TextView tvTitle;
    private EditText outerAddress;
    private EditText innerAddress;
    private EditText aircenterName;
    public DataControl mDC;
    public ElectricInfoData electric;
    public static String aircode;
    private ProgressDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1100:
                    dialog.cancel();
                    Toast.makeText(AddAirCenterMore.this, "空调保存失败，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                case 0x1101:
                    dialog.cancel();
                    Toast.makeText(AddAirCenterMore.this, "空调保存失败，稍候重试", Toast.LENGTH_SHORT).show();
                    break;
                case 0x1102:
                    mDC.mAreaData.loadAreaList();
                    dialog.cancel();
                    Toast.makeText(AddAirCenterMore.this, "空调保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x1103:
                    dialog.cancel();
                    Toast.makeText(AddAirCenterMore.this, "空调内地址或外地址不能为空值", Toast.LENGTH_SHORT).show();
                    break;
                case 0x1104:
                    dialog.cancel();
                    Toast.makeText(AddAirCenterMore.this, "空调地址格式错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_air_center_more);
        initView();
    }
    private void initView(){
        mDC = DataControl.getInstance();
        tvTitle = (TextView)findViewById(R.id.add_air_center);
        outerAddress = (EditText)findViewById(R.id.outer_address);
        innerAddress = (EditText)findViewById(R.id.inner_address);
        aircenterName = (EditText)findViewById(R.id.air_center_name);

    }
    public void airCenterinfoBack(View view){
       finish();
    }
    public void AirCenterSave(View view){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在添加保存空调");
        dialog.show();
        final String OuterAddress = outerAddress.getText().toString();
        final String InnerAddress = innerAddress.getText().toString();
        final String airName = aircenterName.getText().toString();
         aircode = OuterAddress+InnerAddress;
        final String msCode =  mDC.sMasterCode;
        final int ecIndex = AirCenterMoreActivity.aircenterelectricIndex;

        new Thread(){
            public void run(){
                Message msg = new Message();
                if(OuterAddress.equals("")||InnerAddress.equals("")){
                    msg.what = 0x1103;
                    handler.sendMessage(msg);
                } if(OuterAddress.length()!=2||InnerAddress.length()!=2){
                    msg.what = 0x1104;
                    handler.sendMessage(msg);
                }else{
                    String result = mDC.mWS.addCentralAir(msCode,ecIndex,aircode,airName);
                    mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),AddAirCenterMore.this);
                if(result.startsWith("-2")){
                    msg.what = 0x1100;
                    handler.sendMessage(msg);
                }else if(result.startsWith("-1")){
                    msg.what = 0x1101;
                    handler.sendMessage(msg);
                } else{
                    msg.what=0x1102;
                    handler.sendMessage(msg);
                }
            }
            }
        }.start();

    }

}
