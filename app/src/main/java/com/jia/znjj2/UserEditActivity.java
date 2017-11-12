package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.UserData;

public class UserEditActivity extends Activity {
    public String result3;
    private static final String TAG = "UserEditActivity";
    private DataControl mDC;
    private Intent intent;
    private EditText etUserName;
    private EditText etMasterCode;
    private EditText etUserIp;
    private Button btnSave;
    private Button btnGiveUpAdmin;
    private Button btnShareMasterNode;
    private Button btnGetAdmin;
    private Button btnAquireAdmin;
    private int position;
    private UserData.UserDataInfo userDataInfo;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1027:
                    Toast.makeText(UserEditActivity.this,"成功放弃管理员权限，重启生效",Toast.LENGTH_LONG).show();
                    break;
                case 0x1028:
                    Toast.makeText(UserEditActivity.this,"成功放弃管理员权限",Toast.LENGTH_LONG).show();
                    initView();
                    break;
                case 0x1029:
                    Toast.makeText(UserEditActivity.this,"放弃管理员权限失败，请重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1030:
                    Toast.makeText(UserEditActivity.this,"成功获得管理员权限，重启生效",Toast.LENGTH_LONG).show();
                    break;
                case 0x1031:
                    Toast.makeText(UserEditActivity.this,"成功获得管理员权限",Toast.LENGTH_LONG).show();
                    initView();
                    break;
                case 0x1032:
                    Toast.makeText(UserEditActivity.this,"获得管理员权限失败，请重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1033:
                    goToSharedUser();
                    break;
                case 0x1034:
                    goToAquireAdmin();
                    break;
                case 0x1035:
                    Toast.makeText(UserEditActivity.this,"主机名称更改成功，重启生效",Toast.LENGTH_LONG).show();
                    break;
                case 0x1036:
                    Toast.makeText(UserEditActivity.this,"主机名称更改成功",Toast.LENGTH_LONG).show();
                    break;
                case 0x1037:
                    Toast.makeText(UserEditActivity.this,"主机名称更改失败",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        initView();
        addListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView(){
        mDC = DataControl.getInstance();
        intent = getIntent();
        position = intent.getIntExtra("user_sequ", 0);
        etUserName = (EditText) findViewById(R.id.user_edit_user_name);
        etUserIp = (EditText) findViewById(R.id.user_edit_user_ip);
        etMasterCode = (EditText) findViewById(R.id.user_edit_master_code);
        btnSave = (Button) findViewById(R.id.user_edit_btn_save);
        btnGiveUpAdmin = (Button) findViewById(R.id.user_edit_btn_giveup);
        btnGetAdmin = (Button) findViewById(R.id.user_edit_btn_get);
        btnAquireAdmin= (Button) findViewById(R.id.user_edit_btn_admin_inquire);
        btnShareMasterNode = (Button) findViewById(R.id.user_edit_btn_share);

        userDataInfo = mDC.mUserList.get(position);
        etUserName.setText(userDataInfo.getUserName());
        etMasterCode.setText(userDataInfo.getMasterCode());
        etUserIp.setText(userDataInfo.getUserIP());
        if(userDataInfo.getIsAdmin() == 1){     //管理员
            etUserIp.setFocusable(true);
            etMasterCode.setFocusable(true);
            etUserName.setFocusable(true);
            btnGiveUpAdmin.setVisibility(View.VISIBLE);
            btnShareMasterNode.setVisibility(View.VISIBLE);
            btnGetAdmin.setVisibility(View.GONE);
        }else if(userDataInfo.getIsAdmin() == 0){   //非管理员
            etUserIp.setFocusable(false);
            etMasterCode.setFocusable(false);
            etUserName.setFocusable(false);
            btnGiveUpAdmin.setVisibility(View.GONE);
            btnShareMasterNode.setVisibility(View.GONE);
            btnGetAdmin.setVisibility(View.VISIBLE);
            btnAquireAdmin.setVisibility(View.VISIBLE);
        }
    }

    private void addListener(){
        etUserIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userDataInfo.getIsAdmin() == 0){
                    Toast.makeText(UserEditActivity.this, "非管理员，不能编辑", Toast.LENGTH_LONG).show();
                }
            }
        });
        etMasterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userDataInfo.getIsAdmin() == 0){
                    Toast.makeText(UserEditActivity.this, "非管理员，不能编辑", Toast.LENGTH_LONG).show();
                }
            }
        });
        etUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDataInfo.getIsAdmin() == 0) {
                    Toast.makeText(UserEditActivity.this, "非管理员，不能编辑", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void editUserBack(View view){
        finish();
    }

    public void editUserSave(View view){
        final String userName = etUserName.getText().toString();
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.updateUserName(userDataInfo.getAccountCode(),userDataInfo.getMasterCode(), userName);
                Message message = new Message();
                if(result.startsWith("1")){ //远程更改成功
                    userDataInfo.setUserName(userName);
                    int result3 = mDC.mUserData.updateUserName(userDataInfo.getAccountCode(),userDataInfo.getMasterCode(), userName);
                    if(result3 == 0){
                        message.what = 0x1035;
                    }else {
                        message.what = 0x1036;
                    }

                }else{  //远程更改失败
                    message.what = 0x1037;
                }
                handler.sendMessage(message);
            }
        }.start();

    }

    public void giveUpAdmin(View view){
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.giveUpAdmin(userDataInfo.getMasterCode(), userDataInfo.getAccountCode());
                Message message = new Message();
                if(result.startsWith("1")){ //远程更改成功
                    userDataInfo.setIsAdmin(0);
                    int result2 = mDC.mUserData.giveUpAdmin(userDataInfo.getMasterCode(), userDataInfo.getAccountCode());
                    if(result2 == 0){
                        message.what = 0x1027;
                    }else {
                        message.what = 0x1028;
                    }

                }else{  //远程更改失败
                    message.what = 0x1029;
                }
                handler.sendMessage(message);
            }
        }.start();
    }

    public void shareMasterNode(View view){
        Intent intent = new Intent(UserEditActivity.this, ShareMasterNode.class);
        intent.putExtra("user_sequ", position);
        startActivity(intent);
    }

    public void accessAdmin(View view){
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.accessAdmin(userDataInfo.getMasterCode(), userDataInfo.getAccountCode());
                Message message = new Message();
                if(result.startsWith("1")){ //远程更改成功
                    userDataInfo.setIsAdmin(1);
                    int result2 = mDC.mUserData.getAdmin(userDataInfo.getMasterCode(), userDataInfo.getAccountCode());
                    if(result2 == 0){
                        message.what = 0x1030;
                    }else {
                        message.what = 0x1031;
                    }

                }else{  //远程更改失败
                    message.what = 0x1032;
                }
                handler.sendMessage(message);
            }
        }.start();
    }
    public void adminMasterNode(View view){
        new Thread(){
            @Override
            public void run() {
                result3=mDC.mWS.getAdminAccountCode(userDataInfo.getMasterCode());
                if(result3.startsWith("1")){
                    Message message = new Message();
                    message.what = 0x1034;
                    handler.sendMessage(message);
                }


            }

        }.start();

    }
    public void editUserShared(View view){
        new Thread(){
            @Override
            public void run() {
                mDC.mWS.loadSharedAccount(userDataInfo.getMasterCode());
                mDC.mWS.loadAllSharedElectric(userDataInfo.getMasterCode());

                Message message = new Message();
                message.what = 0x1033;
                handler.sendMessage(message);
            }
        }.start();

    }

    private void goToSharedUser(){
        Intent intent = new Intent(UserEditActivity.this, SharedAccount.class);
        startActivity(intent);
    }
    private void goToAquireAdmin(){
       new AlertDialog.Builder(this)
                .setTitle("管理员账号")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(result3)
                .setPositiveButton("确定",null)
                .show();


    }
}
