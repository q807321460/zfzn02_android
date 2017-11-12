package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.data.SceneElectricData;
import com.jia.util.NetworkUtil;
import com.jia.util.Util;

import java.net.Socket;

/**
 * Created by Administrator on 2016/8/21.
 */
public abstract class ElectricBase extends Activity {
    private static final String TAG = "ElectricBase";

    public ElectricInfoData electric;
    private SceneElectricData.SceneElectricInfo sceneElectric;
    public String orderInfo;
    public int electricSequ;
    public int roomSequ;
    public Intent mIntent;
    public DataControl mDC;
    public View viewBase;
    public String electricState;
    public String stateInfo;
    public BrdcstReceiver receiver;


    Socket socket = NetworkUtil.socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void init(){
        mDC = DataControl.getInstance();
        mIntent = getIntent();
        roomSequ = mIntent.getIntExtra("roomSequ", -1);
        electricSequ = mIntent.getIntExtra("electricSequ", -1);
        electric = mDC.mAreaList.get(roomSequ).getmElectricInfoDataList().get(electricSequ);
     //sceneElectric=mDC.mSceneList.get().getSceneElectricInfos().get()
        orderInfo="0000000000";
        /*设置接收后台的广播消息*/
        receiver = new BrdcstReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MY_RECEIVER");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //this.unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        finish();
    }

    public void back(View view){
        this.finish();
    }

    public void open(View view){
        System.out.println(mDC.sMasterCode + "开： " + electric.toString());
        Log.i(TAG, "open: " + mDC.sMasterCode + "开： "+electric.toString());
        //本地通信失败，选择远程连接
        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+mDC.orderOpen,orderInfo);
                    Log.i(TAG, "run: "+ mDC.sMasterCode+electric.getElectricCode()+mDC.orderSign+mDC.orderOpen+orderInfo);
                    System.out.println("远程开电器：");
                }
            }.start();

        }else{
            //本地socket通信
            String order = "<" + electric.getElectricCode()+mDC.orderSign+mDC.orderOpen + orderInfo+ "00" + ">";
            System.out.println("本地开电气： "+order);
            NetworkUtil.out.println(order);
        }


    }

    public void close(View view){
        System.out.println(mDC.sMasterCode + "关： "+electric.toString());
        Log.i(TAG, "close: " + mDC.sMasterCode + "关： " + electric.toString());
        //本地通信失败，选择远程连接
        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+mDC.orderClose,orderInfo);
                    Log.i(TAG, "run: " + mDC.sMasterCode + electric.getElectricCode() + mDC.orderSign + mDC.orderClose + orderInfo);
                    System.out.println("远程关电器：");
                }
            }.start();

        }else {
            //本地socket通信
            String order = "<" + electric.getElectricCode()+mDC.orderSign+mDC.orderClose + orderInfo+ "00" + ">";
            System.out.println("本地关电器："+order);
            NetworkUtil.out.println(order);
        }
    }

    public void stop(View view){
        System.out.println(mDC.sMasterCode + "停： " + electric.toString());
        //本地通信失败，选择远程连接
        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode, electric.getElectricCode(), "" + mDC.orderSign + mDC.orderStop, orderInfo);
                    Log.i(TAG, "run: " + mDC.sMasterCode + electric.getElectricCode() + mDC.orderSign + mDC.orderStop + orderInfo);
                }
            }.start();

        }else {
            //本地socket通信
            String order = "<" + electric.getElectricCode()+mDC.orderSign+mDC.orderStop+orderInfo + "00" + ">";
            System.out.println(order);
            NetworkUtil.out.println(order);
        }
    }
    private String bytes2Order(byte[] bytes){
        String irCode = Util.byte2hex(bytes);
        System.out.println("红外码："+irCode);
        return irCode;
    }
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    public void sendOrder1(byte[] bytes){
        String irCode1 = bytestoIrCode(bytes);
        final String irCode =hexStringToString(irCode1);
        String irOrder=null;
        String length = Integer.toHexString(irCode.length());
        String irCount = (length.length()<2)?"0"+length:length;
        System.out.println("红外指令长度："+Integer.toHexString(irCode.length()));
        if(length.equals("20")) {
            irOrder = "<" + electric.getElectricCode() + "XM" + irCount + irCode + "FF>";
        }else{
            irOrder = "<" + electric.getElectricCode() + "XM" + irCount + irCode +"000"+ "FF>";
        }
        System.out.println("红外命令："+irOrder);

        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+"XM",irCode);
                    Log.i(TAG, "run: "+ mDC.sMasterCode+electric.getElectricCode()+"XM"+irCode);
                }
            }.start();

        }else {
            //本地socket通信
            System.out.println("本地通信："+irOrder);
            NetworkUtil.out.println(irOrder);
        }
    }
    public void sendOrder(byte[] bytes){
        final String irCode = bytestoIrCode(bytes);
        String irOrder=null;
        String length = Integer.toHexString(irCode.length());
        String irCount = (length.length()<2)?"0"+length:length;
        System.out.println("红外指令长度："+Integer.toHexString(irCode.length()));
        if(length.equals("20")) {
           irOrder = "<" + electric.getElectricCode() + "XM" + irCount + irCode + "FF>";
        }else{
           irOrder = "<" + electric.getElectricCode() + "XM" + irCount + irCode +"000"+ "FF>";
        }
        System.out.println("红外命令："+irOrder);

        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+"XM",irCode);
                    Log.i(TAG, "run: "+ mDC.sMasterCode+electric.getElectricCode()+"XM"+irCode);
                }
            }.start();

        }else {
            //本地socket通信
            System.out.println("本地通信："+irOrder);
            NetworkUtil.out.println(irOrder);
        }
    }

    private String bytestoIrCode(byte[] bytes){
        String irCode = "";
        if(bytes.length == 4){
            irCode = String.valueOf(bytes);
        }else {
            irCode = bytes2Order(bytes);
        }
        return irCode;
    }
    public void updateSceneElectricName(int electricIndex,String electricName){
        for (int i=0;i<mDC.mSceneList.size();i++){
            for(int j=0;j<mDC.mSceneList.get(i).getSceneElectricInfos().size();j++){
                if(mDC.mSceneList.get(i).getSceneElectricInfos().get(j).getElectricIndex()==electricIndex){
                    mDC.mSceneList.get(i).getSceneElectricInfos().get(j).setElectricName(electricName);
                }
            }
        }
    }
    boolean checkNetConnection() {
        try{
            socket.sendUrgentData(0xFF);
            //return true;
        }catch(Exception ex){
            System.out.println("Socket通信异常");
            mDC.bIsRemote = true;
            /*启动后台Service服务，接受网络数据*/
            //new ServiceSocket().readFromWebService();
            return false;
        }
        boolean isConnected = socket.isConnected() && !socket.isClosed() && socket.isBound()&& !socket.isOutputShutdown()&& !socket.isInputShutdown();
        System.out.println("Socket状态"+isConnected);
        //判断网络是否连接上了，没有则提示，并返回
        if ((socket ==null) || !(socket.isConnected() && !socket.isClosed())){
            new AlertDialog.Builder(ElectricBase.this).setTitle("网络连接失败，请检查网络" )
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                {
                                }
                            }).show();      //警告对话框
            return true;
        }
        return false;
    }

    public abstract void updateUI();
    /**
     *广播：接受后台的service发送的广播
     */
    public class BrdcstReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //System.out.println("灯页面接收到电器状态更新的广播");
            updateUI();
        }
    }

}
