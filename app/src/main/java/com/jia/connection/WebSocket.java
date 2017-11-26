package com.jia.connection;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import com.jia.data.DataControl;
import com.jia.util.Util;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by luojuan on 2017/11/22.
 */

public class WebSocket{
    DataControl mDC;
    private static final String url1 = "ws://101.201.211.87:8080/zfzn02/websocket_app/";
    private static final String TAG = "WebSocket ";
    boolean mConnected=false;
    boolean mPolling=false;
    private Handler mHandler = new Handler();
    public WebSocketConnection mConnect = new WebSocketConnection();

    /**
     * websocket连接，接收服务器消息
     */
    public WebSocket(){
       mDC=DataControl.getInstance();
    }

    @Nullable
    public void ConnectToWebSocket(final String mastercode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "ws connect....");
                mConnected=true;
                String url=url1+mastercode;

                try {
                    mConnect.connect(url, new WebSocketHandler() {
                        @Override
                        public void onOpen() {
                            Log.i(TAG, "Status:Connect to ");

                        }

                        @Override
                        public void onTextMessage(String payload) {

                            Log.i(TAG, payload);
                            if (mConnect.isConnected()==true){

                                if (payload.startsWith("<"))  //处理返回状态
                                {
                                    String strState = null;
                                    strState = payload.substring(payload.indexOf("<")+"<".length(), payload.lastIndexOf(">"));
                                    System.out.println("socket通信返回值： "+ strState);
                                    Log.v("strState", strState);
                                    //处理单元状态
                                    Util.analyseSingleStatus(strState);
                                }

                            }



                        }

                        @Override
                        public void onClose(int code, String reason) {
                            Log.i(TAG, "Connection lost..");
                        }
                    });
                } catch (WebSocketException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }
    public void CloseWebsocket(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mConnected==true){
                    mConnect.disconnect();
                    mConnected=false;
                }
            }
        }).start();


    }
    public void SendText(String msm){
        if (mConnect.isConnected()){
            mConnect.sendTextMessage(msm);
        }else {
            Log.i(TAG, "no connection!!");
        }
    }
    public void OpenPolling(){
        if (mPolling==true){
            return;
        }
        mHandler.postDelayed(mReconnectTask,5000);
        mPolling=true;

    }
    public void RunPolling(){
        if (mDC.bIsRemote){
            ConnectToWebSocket(mDC.sMasterCode);
        }
    }
    public void StopPolling(){
        if (mPolling==false){
            return;
        }
        mPolling=false;
        mHandler.removeCallbacks(mReconnectTask);
    }
    public void webSocketDidOpen(){
        StopPolling();
        //重连后，从服务器调用最新的所有电器的状态
        mDC.mWS.getElectricStateByUser(mDC.sAccountCode, mDC.sMasterCode);
        //发送广播
    }
    private Runnable mReconnectTask = new Runnable() {

        @Override
        public void run() {
            RunPolling();
        }
    };
}
