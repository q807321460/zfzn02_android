package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jia.data.DataControl;

/**
 * Created by Administrator on 2016/8/21.
 */
public class ChangPassword extends Activity{
    private DataControl mDC;
    private EditText etPassOld;
    private EditText etPassNew;
    private EditText etPassSure;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0x1171:
                    Toast.makeText(ChangPassword.this, "更改密码成功", Toast.LENGTH_LONG).show();
                    break;
                case 0x1170:
                    Toast.makeText(ChangPassword.this, "更改密码失败，请检查网络并重试", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        mDC = DataControl.getInstance();
        etPassOld = (EditText) findViewById(R.id.change_password_old);
        etPassNew = (EditText) findViewById(R.id.change_password_new);
        etPassSure = (EditText) findViewById(R.id.change_password_new_sure);
    }

    public void changePasswordBack(View view){
        finish();
    }

    public void changePass(View view){
        final String oldPass = etPassOld.getText().toString();
        final String newPass = etPassNew.getText().toString();
        final String surePass = etPassSure.getText().toString();
        if(oldPass == null || oldPass.equals("")){
            Toast toast = Toast.makeText(ChangPassword.this, "原密码不能为空", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else if(newPass == null || newPass.equals("") ){
            Toast toast = Toast.makeText(ChangPassword.this, "原密码不能为空", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } else if(newPass!=null && !newPass.equals(surePass)){
            Toast toast = Toast.makeText(ChangPassword.this, "两次输入密码不一致", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } else if(newPass!=null && newPass.equals(surePass)){
            new Thread(){
                @Override
                public void run() {
                    System.out.println("accountCode: " + mDC.sAccountCode + " oldPassWord: " + oldPass + "newPassword: "+newPass);
                    String result = mDC.mWS.updateAccountPassword(mDC.sAccountCode,oldPass,newPass);
                    System.out.println("更改密码返回的结果："+ result);
                    Message message = new Message();
                    if(result.startsWith("1")){
                        message.what = 0x1171;
                    }else {
                        message.what = 0x1170;
                    }
                    handler.sendMessage(message);
                }
            }.start();

        }

    }
}
