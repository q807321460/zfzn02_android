package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jia.data.DataControl;

public class ForgetPasswordActivity extends Activity {
    private DataControl mDC;
    private TimeCount time;
    private Button btnGetcode;
    private EditText etPhone;
    private EditText etCheckword;
    private TextView tv_show;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0x1001:
                    Toast.makeText(ForgetPasswordActivity.this, "服务器连接失败，请检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1002:
                    Toast toast = Toast.makeText(ForgetPasswordActivity.this, "短信已发送", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    break;
                case 0x1003:
                    time.start();
                    break;
                case 0x1004:
                    Toast.makeText(ForgetPasswordActivity.this, "不存在该用户，请检查后重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1005:
                    tv_show.setText((CharSequence) msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
    Handler handler1 = new Handler()
    {
        @Override
        public void handleMessage(Message msg1) {
            switch (msg1.what)
            {case 0x1005:
                    tv_show.setText((CharSequence) msg1.obj);
                    break;
                default:
                    super.handleMessage(msg1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mDC=DataControl.getInstance();
        time = new TimeCount(60000, 1000);
        etPhone= (EditText) findViewById(R.id.et_phoneNumber);
        etCheckword= (EditText) findViewById(R.id.et_checkword);
        btnGetcode = (Button) findViewById(R.id.btn_check);
        tv_show= (TextView) findViewById(R.id.tv_show);
        btnGetcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String phoneNumber=etPhone.getText().toString();
                if(phoneNumber.length()!=11){
                    Toast.makeText(ForgetPasswordActivity.this,"请输入正确的手机号",Toast.LENGTH_LONG).show();
                }else{

                    new Thread(){
                    @Override
                    public void run() {
                      String flag=mDC.mWS.IsExistAccount(phoneNumber);
                        Message msg = new Message();
                        Message msg1 = new Message();
                        if (flag.equals("-1")) {       //远程连接失败
                            msg.what = 0x1001;
                        }else if (flag.equals("0")) {   //存在该用户，且密码正确
                            msg.what = 0x1003;
                             String result=mDC.mWS.SendSmsCode(phoneNumber);
                            JSONObject job = JSONObject.parseObject(result );
                            if(job.get("info")!=null){
                                msg1.what = 0x1005;
                                msg1.obj=job.get("info");
                            }
                        } else if (flag.equals("-2")) {
                            msg.what = 0x1004;
                        }
                        handler.sendMessage(msg);
                        handler1.sendMessage(msg1);
                    }
                    }.start();

                }


            }
        });
    }
    public void forgetPasswordBack(View v){
        finish();
    }
    public void onNextStep(View view){
        final String checkWord=etCheckword.getText().toString();
        final String phone=etPhone.getText().toString();

        new Thread(){
            @Override
            public void run() {
                String mCode=null;
                String res=mDC.mWS.CheckSmsCode(phone,checkWord);
                JSONObject job1 = JSONObject.parseObject(res);
                if(job1.get("info")!=null){
                    mCode= (String) job1.get("code");
                }
                if(mCode.equals("200")){
                    Intent intent = new Intent(ForgetPasswordActivity.this,ResetPassword.class);
                    intent.putExtra("phoneNumber", phone);
                    startActivity(intent);
                }
            }
        }.start();

    }
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnGetcode.setBackgroundColor(Color.parseColor("#B6B6D8"));
            btnGetcode.setClickable(false);
            btnGetcode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            btnGetcode.setText("重新获取验证码");
            btnGetcode.setClickable(true);
            btnGetcode.setBackgroundColor(Color.parseColor("#ffffff"));

        }
    }
}