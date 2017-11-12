package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jia.connection.MasterSocket;
import com.jia.connection.UdpChat;
import com.jia.data.DataControl;
import com.jia.util.Util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/24.
 */
public class UserAddActivity extends Activity {
    private EditText etUserName;
    private EditText etMasterCode;
    private EditText etUserIp;

    private String userName;
    private String masterCode;
    private String userIp;

    private ProgressDialog dialog;
    //搜索主节点IP所需参数
    ArrayList<String> sUserIPs = new ArrayList<>();
    ArrayList<String> sMasterCodes = new ArrayList<>();
    UdpChat udpChat;
    private String end_IP;
    private DataControl mDC;

    private int count = 0;//记录想服务器上传数据的次数

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0x1011:
                    Toast.makeText(UserAddActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1012:
                    dialog.dismiss();
                    if(mDC.bIsSearchMaster) {
                        if (sUserIPs.size() > 0) {
                            showSwitchDialog();
                        } else {
                            Toast.makeText(UserAddActivity.this, "搜索主节点失败", Toast.LENGTH_LONG).show();
                        }
                    }
                    mDC.bIsSearchMaster = true;
                    break;
                case 0x1013:
                    Toast.makeText(UserAddActivity.this, "非管理员不能添加用户", Toast.LENGTH_LONG).show();
                    break;
                case 0x1014:
                    if(++count < 5){
                        addUserToWs();
                    }else{
                        Toast.makeText(UserAddActivity.this, "远程添加失败，稍候请同步数据", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 0x1015:
                    Toast.makeText(UserAddActivity.this, "已存在该账户名，请核对", Toast.LENGTH_LONG).show();
                    break;
                case 0x1016:
                    Toast.makeText(UserAddActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                    finish();
                    //goToMainPage();
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
        setContentView(R.layout.activity_user_add);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDC.bIsSearchMaster = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    //获得输入数据
    private void init() {
        etUserName = (EditText) findViewById(R.id.user_add_user_name);
        etMasterCode = (EditText) findViewById(R.id.user_add_master_code);
        etUserIp = (EditText) findViewById(R.id.user_add_user_ip);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在搜索主节点");
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDC.bIsSearchMaster = false;
                    }
                });
        mDC = DataControl.getInstance();
        mDC.bIsSearchMaster = true;
    }

    public void addUserBack(View view){
        finish();
    }

    public void addUserSearch(View view){
        sUserIPs.clear();
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            end_IP = Util.intToEndIp(ipAddress);
        } catch (Exception e) {
            Message msg3 = new Message();
            msg3.what = 0x1011;
            handler.sendMessage(msg3);
        }
        dialog.show();
        new Thread(){
            @Override
            public void run() {
                try {

                    udpChat = new UdpChat();
                    ArrayList<String> receiveFromMasterNode=udpChat.init(end_IP);
                    //存储搜索到的主节点的IP 和 唯一编码
                    MasterSocket masterSocket = new MasterSocket();
                    for(int i = 0;i<receiveFromMasterNode.size();i++) {
                        System.out.println(receiveFromMasterNode.get(i));
                        String[] strings = receiveFromMasterNode.get(i).split(",");
                        sUserIPs.add(strings[0]);
                        String masterCode = masterSocket.getMasterNodeCode(strings[0]);
                        if(masterCode != null && !masterCode.equals("")){
                            masterCode = masterCode.substring(1,9);
                        }
                        sMasterCodes.add(masterCode);
                    }
                    Message msg = new Message();
                    msg.what = 0x1012;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void addUserSave(View view){
        userName = etUserName.getText().toString();
        masterCode = etMasterCode.getText().toString();
        userIp = etUserIp.getText().toString();
        if(userName==null || userName.equals("")){
            Toast.makeText(UserAddActivity.this, "主机名称不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(masterCode == null || masterCode.equals("")){
            Toast.makeText(UserAddActivity.this, "主机编码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(userIp == null || userIp.equals("")){
            Toast.makeText(UserAddActivity.this, "主机IP不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.addUser(mDC.sAccountCode,masterCode,userName,userIp);
                Message message = new Message();
                if(result.startsWith("-3")){
                    message.what=0x1015;
                }else if(result.startsWith("-2")){
                    message.what=0x1014;
                }else if (result.startsWith("0")){
                    message.what=0x1013;
                }else {
                    message.what = 0x1016;
                    mDC.mUserData.addUser(userName,masterCode,userIp);
                }
                handler.sendMessage(message);

//                mDC.mSceneData.addScene("回家", 0);
//                mDC.mSceneData.addScene("离家", 1);
//                mDC.mSceneData.addScene("起床", 2);
//                mDC.mSceneData.addScene("睡觉", 3);
            }
        }.start();
    }

    public void addUserToWs(){
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.addUser(mDC.sAccountCode,masterCode, userName, userIp);
                Message message = new Message();
                if(result.startsWith("-2")){
                    message.what = 0x1014;
                }else if(result.startsWith("1")){
                    message.what = 0x1016;
                    mDC.mUserData.addUser(userName,masterCode,userIp);
                }
                handler.sendMessage(message);
            }
        }.start();
    }


    private void showSwitchDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserAddActivity.this);
        builder.setTitle("选择一个主节点");
        int size = sUserIPs.size();
        final String[] strings = new String[size];
        for(int i=0;i< size;i++){
            strings[i] = "IP:"+sUserIPs.get(i)+"\n编号："+ sMasterCodes.get(i);
        }
        //final String[] strings = (String[])sUserIPs.toArray(new String[size]);
        System.out.println("前前前前前前前前前前前前前前前前前前前前前");
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etUserIp.setText(sUserIPs.get(which));
                etMasterCode.setText(sMasterCodes.get(which));
            }
        });
        System.out.println("后后后后后后后后后后后后后后后后后后后后后后后后后后");
        builder.show();
    }

    private void goToMainPage(){
        mDC.mSceneData.loadSceneList();
        Intent ourIntent = new Intent(UserAddActivity.this, MainActivity.class);
        startActivity(ourIntent);
        finish();
    }
}
