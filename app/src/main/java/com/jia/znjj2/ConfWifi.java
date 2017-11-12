package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ConfWifi extends Activity {

    private EditText etSSID;
    private EditText etPass;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_wifi);
        etSSID = (EditText) findViewById(R.id.conf_wifi_ssid);
        etPass = (EditText) findViewById(R.id.conf_wifi_pass);
    }

    public void confWifiBack(View view){
        finish();
    }

    public void confWifiSearch(View view){
        WifiManager manager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        manager.startScan();
        // 得到扫描结果
        mWifiList = manager.getScanResults();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择要连接的WIFI");
        int size = mWifiList.size();
        final String[] strings = new String[size];
        for(int i=0;i< size;i++){
            strings[i] = mWifiList.get(i).SSID;
        }
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etSSID.setText(strings[which]);
            }
        });
        builder.show();
    }

    public void confWifiSure(View view){
        if(etSSID.getText().toString()==null || etPass.getText().toString() == null){
            Toast.makeText(ConfWifi.this,"SSID或密码不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                Socket socket;
                String str = "";
                String order = "<<"+etSSID.getText().toString()+","+etPass.getText().toString()+">>";
                System.out.println(order);
                try {
                    //Socket socket = new Socket(mDC.mAccountList.get(i).getsAccountUserIp(),mDC.mAccountList.get(i).getiAccountUserPort());

                    socket = new Socket();
                    //socket.connect(new InetSocketAddress("192.168.1.117", 3000), 5000);
                    socket.connect(new InetSocketAddress("192.168.4.1",8899),5000);
                    socket.setSoTimeout(10000);//设置10秒后即认为超时
                    final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final OutputStream os = socket.getOutputStream();
                    try {
                        os.write((order+"\r\n").getBytes("utf-8"));
                        while(str.equals("") || !str.startsWith("<<")){
                            str = br.readLine();
                        }

                    } catch (IOException e) {
                        System.out.println("WIFI设置1111111111111");
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    System.out.println("WIFI设置2222222222222222222");
                    e.printStackTrace();

                }
            }
        }.start();
    }
}
