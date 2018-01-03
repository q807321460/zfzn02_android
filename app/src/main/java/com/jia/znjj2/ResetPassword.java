package com.jia.znjj2;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jia.data.DataControl;

public class ResetPassword extends Activity {
    private String phoneNumber;
    private EditText resetPassword;
    private EditText resetSurePassword;
    private DataControl mDC;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0x1001:
                    Toast.makeText(ResetPassword.this, "密码修改失败", Toast.LENGTH_LONG).show();
                    break;
                case 0x1003:
                    Toast.makeText(ResetPassword.this, "密码修改失败", Toast.LENGTH_LONG).show();
                    break;
                case 0x1004:
                    Toast.makeText(ResetPassword.this, "密码修改成功", Toast.LENGTH_LONG).show();
                    break;
                case 0x1005:
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        resetPassword= (EditText) findViewById(R.id.reset_password);
        resetSurePassword= (EditText) findViewById(R.id.reset_password_sure);
        mDC=DataControl.getInstance();

    }
    public void resetPasswordBack(View view){
        finish();
    }
    public void resetSave(View view){
        final String passwordReset=resetPassword.getText().toString();
        String passwordSureReset=resetSurePassword.getText().toString();
        if(passwordReset==null || passwordReset.equals("")){
            Toast.makeText(ResetPassword.this,"密码不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(!passwordReset.equals(passwordSureReset)){
            Toast.makeText(ResetPassword.this, "两次密码不一致，请核对", Toast.LENGTH_LONG).show();
            return;
        }else {
            new Thread() {
                @Override
                public void run() {
                    String flag = mDC.mWS.ResetAccountPassword(phoneNumber, passwordReset);
                    Message msg = new Message();
                    if (flag.equals("-1")) {       //修改失败
                        msg.what = 0x1001;
                    }else if (flag.equals("0")) {//修改失败
                        msg.what = 0x1003;
                    } else if (flag.equals("1")) {//密码修改成功
                        msg.what = 0x1004;
                    }
                    handler.sendMessage(msg);
                }
            }.start();
        }

    }
}
