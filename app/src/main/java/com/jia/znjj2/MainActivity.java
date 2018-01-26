package com.jia.znjj2;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jia.connection.MasterSocket;
import com.jia.connection.ServiceSocket;
import com.jia.connection.UdpChat;
import com.jia.data.DataControl;
import com.jia.connection.WebSocket;
import com.jia.util.NetworkUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

import com.jia.util.Util;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private LinearLayout mTabHome;
    private LinearLayout mTabArea;
    private LinearLayout mTabScene;
    private LinearLayout mTabSecurity;
    private ImageView mHomeImg;
    private ImageView mAreaImg;
    private ImageView mSceneImg;
    private ImageView mSecurityImg;
    private Fragment mHomeFragment;
    private Fragment mAreaFragment;
    private Fragment mSceneFragment;
    private Fragment mSecurityFragment;

//    private BrdcstReceiver receiver;
    private DataControl mDC;

    private volatile int currentAreaPage;

    private long exitTime = 0;

    public int getCurrentAreaPage() {
        return currentAreaPage;
    }

    public void setCurrentAreaPage(int currentAreaPage) {
        this.currentAreaPage = currentAreaPage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        setSelect(0);
        connectToServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //进入首页后更新IP
        new updateIPAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(receiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                mDC.mWST.CloseWebsocket();
                SysApplication.getInstance().exit();
                //finish();
//                final Intent intent = new Intent(); intent.setAction("com.jia.connection.ServiceSocket");
//                final Intent eintent = new Intent(createExplicitFromImplicitIntent(this,intent));
//                stopService(eintent);
               // System.exit(0);


            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
//        // Retrieve all services that can match the given intent
//        PackageManager pm = context.getPackageManager();
//        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
//
//        // Make sure only one match was found
//        if (resolveInfo == null || resolveInfo.size() != 1) {
//            return null;
//        }
//
//        // Get component info and create ComponentName
//        ResolveInfo serviceInfo = resolveInfo.get(0);
//        String packageName = serviceInfo.serviceInfo.packageName;
//        String className = serviceInfo.serviceInfo.name;
//        ComponentName component = new ComponentName(packageName, className);
//
//        // Create a new intent. Use the old one for extras and such reuse
//        Intent explicitIntent = new Intent(implicitIntent);
//
//        // Set the component to be explicit
//        explicitIntent.setComponent(component);
//
//        return explicitIntent;
//    }
    private void initEvent(){
        mDC = DataControl.getInstance();
//        receiver = new BrdcstReceiver();


        mTabHome.setOnClickListener(this);
        mTabArea.setOnClickListener(this);
        mTabScene.setOnClickListener(this);
        mTabSecurity.setOnClickListener(this);
    }

    /**
     * 连接服务器，开启一个多线程，避免卡死页面
     */
    private void connectToServer() {
        new Thread(){
            @Override
            public void run() {
                /*连接主节点*/

                if(!mDC.bIsRemote) {
                    try {
                        int port = mDC.iUserPort;
                        Socket socket = new Socket(mDC.sUserIP, port);
                        /**
                         * 设置网络，输入流，输出流
                         */
                        NetworkUtil.socket = socket;
                        NetworkUtil.out = new PrintWriter(socket.getOutputStream(), true);
                        NetworkUtil.br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*启动后台Service服务，接受网络数据*/
                MainActivity.this.startService(new Intent(MainActivity.this, ServiceSocket.class));

//                /*设置接收后台的广播消息*/
//                receiver = new BrdcstReceiver();
//                IntentFilter filter = new IntentFilter();
//                filter.addAction("android.intent.action.MY_RECEIVER");
//                registerReceiver(receiver, filter);

            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        resetImg();
        switch (v.getId()){
            case R.id.bottom_ll_main:
                setSelect(0);
                break;
            case R.id.bottom_ll_area:
                setSelect(1);
                break;
            case R.id.bottom_ll_scene:
                setSelect(2);
                break;
            case R.id.bottom_ll_security:
                setSelect(3);
                break;
            default:
                break;
        }
    }

    public void setSelect(int i){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        //把图片设置为亮的
        //设置内容区域
        switch (i){
            case 0:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.id_content,mHomeFragment);

                }else {
                    transaction.show(mHomeFragment);
                    ((HomeFragment)mHomeFragment).setTitleText();
                }
                mHomeImg.setImageResource(R.drawable.bottom_home_select);
                break;
            case 1:
                if (mAreaFragment == null) {
                    mAreaFragment = new AreaFragment();
                    transaction.add(R.id.id_content,mAreaFragment);
                }else {
                    //pager.setCurrentItem(((MainActivity)getActivity()).getCurrentAreaPage());
                    int m = getCurrentAreaPage();
                    ((AreaFragment)mAreaFragment).getPager().setCurrentItem(getCurrentAreaPage());
                    transaction.show(mAreaFragment);
                }
                mAreaImg.setImageResource(R.drawable.bottom_area_select);
                break;
            case 2:
                if (mSceneFragment == null) {
                    mSceneFragment = new SceneFragment();
                    transaction.add(R.id.id_content,mSceneFragment);
                }else {
                    transaction.show(mSceneFragment);
                }
                mSceneImg.setImageResource(R.drawable.bottom_scene_select);
                break;
            case 3:
                if (mSecurityFragment == null) {
                    mSecurityFragment = new SecurityFragment();
                    transaction.add(R.id.id_content,mSecurityFragment);
                }else {
                    transaction.show(mSecurityFragment);
                }
                mSecurityImg.setImageResource(R.drawable.bottom_security_select);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction){
        if(mHomeFragment != null){
            transaction.hide(mHomeFragment);
        }
        if(mAreaFragment != null && !mAreaFragment.isHidden()){
            ((AreaFragment)mAreaFragment).onPause();
            transaction.hide(mAreaFragment);
        }
        if(mSceneFragment != null){
            transaction.hide(mSceneFragment);
        }
        if(mSecurityFragment != null){
            transaction.hide(mSecurityFragment);
        }
    }

    private void initView(){
        //tabs
        mTabHome = (LinearLayout) findViewById(R.id.bottom_ll_main);
        mTabArea = (LinearLayout) findViewById(R.id.bottom_ll_area);
        mTabScene = (LinearLayout) findViewById(R.id.bottom_ll_scene);
        mTabSecurity = (LinearLayout) findViewById(R.id.bottom_ll_security);
        //ImageButton
        mHomeImg = (ImageView) findViewById(R.id.bottom_iv_main);
        mAreaImg = (ImageView) findViewById(R.id.bottom_iv_area);
        mSceneImg = (ImageView) findViewById(R.id.bottom_iv_scene);
        mSecurityImg = (ImageView) findViewById(R.id.bottom_iv_security);

    }

    protected void resetImg(){
        mHomeImg.setImageResource(R.drawable.bottom_home_normal);
        mAreaImg.setImageResource(R.drawable.bottom_area_normal);
        mSceneImg.setImageResource(R.drawable.bottom_scene_normal);
        mSecurityImg.setImageResource(R.drawable.bottom_security_normal);
    }


    class updateIPAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            //后台进行更新搜索IP的功能
            //搜索主节点IP所需参数
            ArrayList<String> sUserIPs = new ArrayList<>();
            ArrayList<String> sMasterCodes = new ArrayList<>();
            try {
                //获取wifi服务
                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                //判断wifi是否开启
//                if (!wifiManager.isWifiEnabled()) {
//                    wifiManager.setWifiEnabled(true);
//                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                String end_IP = Util.intToEndIp(ipAddress);
                UdpChat udpChat = new UdpChat();
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
                if(sMasterCodes.size() == sUserIPs.size()){
                    for(int i=0; i< sMasterCodes.size(); i++){
                        if(sMasterCodes.get(i).equals(mDC.sMasterCode) && !sUserIPs.get(i).equals(mDC.sUserIP)){
                            updateMasterCodeIP(sUserIPs.get(i));
                        }
                    }
                }
            } catch (Exception e) {

            }
            return null;
        }

        private void updateMasterCodeIP(String IP){
            mDC.sUserIP = IP;
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_ip", IP);
            mDC.mUserData.updateUserData(contentValues);
            mDC.mWS.updateUserIP(mDC.sMasterCode, IP);
        }

    }

//    /**
//     *广播：接受后台的service发送的广播
//     */
//    private  class BrdcstReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            updateUI();
//        }
//    }
//    private void updateUI(){
//        System.out.println("MainaPageActivity updateUI");
//    }
}
