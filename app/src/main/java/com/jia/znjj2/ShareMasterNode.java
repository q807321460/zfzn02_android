package com.jia.znjj2;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.UserData;

public class ShareMasterNode extends Activity {

    private DataControl mDC;
    private int position;
    private UserData.UserDataInfo userDataInfo;

    private EditText etAccountCode;
    private String accountCode;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1043:
                    Toast.makeText(ShareMasterNode.this, "分享失败", Toast.LENGTH_LONG).show();
                    break;
                case 0x1044:
                    Toast.makeText(ShareMasterNode.this, "分享成功", Toast.LENGTH_LONG).show();
                    break;
                case 0x1045:
                    Toast.makeText(ShareMasterNode.this, "已分享，不能重复分享", Toast.LENGTH_LONG).show();
                    break;
                case 0x1046:
                    Toast.makeText(ShareMasterNode.this, "不存在被分享的账户", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_master_node);
        mDC = DataControl.getInstance();
        position = getIntent().getIntExtra("user_sequ",0);
        userDataInfo = mDC.mUserList.get(position);
        etAccountCode = (EditText) findViewById(R.id.share_master_node_account_code);
    }


    public void shareMasterNodeBack(View view){
        finish();
    }
    public void shareMasterNodeSure(View view){
        accountCode = etAccountCode.getText().toString();
        if(accountCode == null || accountCode.equals("")){
            Toast.makeText(ShareMasterNode.this, "账户名不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.addSharedUser(accountCode,userDataInfo.getMasterCode(),
                        userDataInfo.getUserName(),userDataInfo.getUserIP());
                Message message = new Message();
                if(result.startsWith("-2")){
                    message.what = 0x1043;
                }else if(result.startsWith("1")){
                    message.what = 0x1044;
                }else if(result.startsWith("0")){
                    message.what = 0x1045;
                }else if(result.startsWith("-1")){
                    message.what = 0x1046;
                }
                handler.sendMessage(message);
            }
        }.start();
    }
}
