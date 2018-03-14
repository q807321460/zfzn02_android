package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;


/**
 * Created by ShengYi on 2018/3/13.
 */

public class AddAirCenterMore extends Activity {
    private TextView tvTitle;
    private EditText outerAddress;
    private EditText innerAddress;
    public DataControl mDC;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1100:
                    Toast.makeText(AddAirCenterMore.this, "空调保存失败，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                case 0x1101:
                    Toast.makeText(AddAirCenterMore.this, "空调保存失败，稍候重试", Toast.LENGTH_SHORT).show();
                    break;
                case 0x1102:
                    Toast.makeText(AddAirCenterMore.this, "空调保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0x1103:
                    Toast.makeText(AddAirCenterMore.this, "空调内地址或外地址不能为空值", Toast.LENGTH_SHORT).show();
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
    }
    public void airCenterinfoBack(View view){
        finish();
    }
    public void AirCenterSave(View view){
        final String OuterAddress = outerAddress.getText().toString();
        final String InnerAddress = innerAddress.getText().toString();
        final String aircode = OuterAddress+InnerAddress;
        final String msCode =  mDC.sMasterCode;
        final int ecIndex = AirCenterMoreActivity.aircenterelectricIndex;

        new Thread(){
            public void run(){
                Message msg = new Message();
                if(OuterAddress.equals("")||InnerAddress.equals("")){
                    msg.what = 0x1103;
                    handler.sendMessage(msg);
                }else{
                String result = mDC.mWS.addCentralAir(msCode,ecIndex,aircode);
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
