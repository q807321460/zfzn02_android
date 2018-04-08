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

import static com.jia.znjj2.AirCenterMoreActivity.aircenterNamelist8;
import static com.jia.znjj2.AirCenterMoreActivity.aircenterNumberlist1;
import static com.jia.znjj2.AirCenterMoreActivity.aircenterelectricIndex;
import static com.jia.znjj2.UpdateAirCenterName.airCenterClickPosition;

/**
 * Created by ShengYi on 2018/4/8.
 */

public class AirCenterSingleName extends Activity {
    private TextView aircenterName;
    private TextView aircenterNo;
    private EditText aircenterNewName;
    public DataControl mDC;
    private ProgressDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1001:
                    dialog.cancel();
                    Toast.makeText(AirCenterSingleName.this, "名称保存失败，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                case 0x1002:
                    mDC.mAreaData.loadAreaList();
                    dialog.cancel();
                    Toast.makeText(AirCenterSingleName.this, "名称保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x1003:
                    dialog.cancel();
                    Toast.makeText(AirCenterSingleName.this, "请先输入名称", Toast.LENGTH_SHORT).show();
                    break;}
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_name_air_center_single);
        initView();

    }
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        mDC = DataControl.getInstance();
        aircenterNo = (TextView)findViewById(R.id.air_center_address1);
        aircenterName = (TextView)findViewById(R.id.air_center_single_name1);
        aircenterNewName = (EditText)findViewById(R.id.air_center_update_name1);
        aircenterName.setText(aircenterNumberlist1.get(airCenterClickPosition));
        aircenterNo.setText(aircenterNamelist8.get(airCenterClickPosition));

    }
    public void airCenterSingleinfoBack(View view){finish();}
    public void AirCenterNameSave1(View view){
        final String NewName = aircenterNewName.getText().toString();
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在添加保存空调");
        dialog.show();

        new Thread(){
            public void run(){
                Message msg = new Message();
                if(NewName.equals("")){
                    msg.what=0x1003;
                    handler.sendMessage(msg);
                }else{
                String result = mDC.mWS.updateCentralAirName(mDC.sMasterCode,aircenterelectricIndex,aircenterNamelist8.get(airCenterClickPosition),NewName);
                mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),AirCenterSingleName.this);
                if (result.equals("-2")){
                    msg.what=0x1001;
                    handler.sendMessage(msg);
                }else{
                    msg.what=0x1002;
                    handler.sendMessage(msg);
                    aircenterNumberlist1.set(airCenterClickPosition,NewName);
                }
                }
            }
        }.start();
    }
}

