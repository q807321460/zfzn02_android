package com.jia.znjj2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jia.connection.UdpChat;
import com.jia.data.DataControl;

import java.util.ArrayList;

/**
 * Created by Jia on 2016/4/8.
 */
public class SignActivity extends Activity {
    private EditText mEtSignAccountCode;
    private EditText mEtSignAccountName;
    private EditText mEtSignPassword;
    private EditText mEtSignSuerPassword;

    private String sAccountCode;        //用户名
    private String sAccountName;
    private String sAccountPassword;
    private String sAccountSurePassword;

    private Button mBtnSign_search;
    private Button mBtnSign_save;
    private ProgressDialog dialog;


    //搜索主节点IP所需参数
    ArrayList<String> sUserIPs = new ArrayList<>();
    ArrayList<String> sMasterCodes = new ArrayList<>();
    UdpChat udpChat;
    private String end_IP;

    private DataControl mDC;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0x1005:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(SignActivity.this, "注册失败，请检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1006:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(SignActivity.this, "注册失败，请重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1007:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(SignActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0x1008:
                    if(dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(SignActivity.this, "该账户已被注册，请直接登录", Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_sign);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDC.bIsSearchMaster = true;
    }

    //获得输入数据
    private void init() {
        mDC = DataControl.getInstance();
        mDC.bIsSearchMaster = true;

        mEtSignAccountCode = (EditText) findViewById(R.id.sign_account_code);
        mEtSignAccountName = (EditText) findViewById(R.id.sign_account_name);
        mEtSignPassword = (EditText) findViewById(R.id.sign_password);
        mEtSignSuerPassword = (EditText) findViewById(R.id.sign_password_sure);
        mBtnSign_search = (Button) findViewById(R.id.sign_search);
        mBtnSign_save = (Button) findViewById(R.id.sign_sign);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在注册，请稍候");

    }

    public void signBack(View view) {
        finish();
    }

    public void signSave(View v)
    {
        sAccountCode = mEtSignAccountCode.getText().toString();
        sAccountName = mEtSignAccountName.getText().toString();
        sAccountPassword = mEtSignPassword.getText().toString();
        sAccountSurePassword = mEtSignSuerPassword.getText().toString();
        System.out.println("注册账户");
        if(sAccountCode==null || sAccountCode.equals(""))
        {
            Toast.makeText(SignActivity.this, "账号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(sAccountName==null || sAccountName.equals(""))
        {
            Toast.makeText(SignActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(sAccountPassword==null || sAccountPassword.equals("")){
            Toast.makeText(SignActivity.this, "用密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(!sAccountPassword.equals(sAccountSurePassword)){
            Toast.makeText(SignActivity.this, "两次密码不一致，请核对", Toast.LENGTH_LONG).show();
            return;
        }
        //dialog.show();

        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.addAccount(sAccountCode,sAccountPassword,sAccountName);
                System.out.println("注册账户result:"+result);
                Message msg = new Message();
                if(result.startsWith("-1")){
                    msg.what = 0x1005;
                }else if(result.startsWith("0")){
                    msg.what = 0x1006;
                }else if(result.startsWith("1")){
                    msg.what = 0x1007;
                }else if(result.startsWith("2")){
                    msg.what = 0x1008;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }


}
