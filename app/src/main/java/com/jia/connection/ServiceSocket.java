package com.jia.connection;//package com.example.jia.smarthone;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jia.data.DataControl;
import com.jia.util.NetworkUtil;
import com.jia.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Jia on 2016/3/30.
 */
public class ServiceSocket extends Service {
    private static final String TAG = "ServiceSocket";
    private boolean threadDisable = false;//标识线程是否结束
    //Util util = new Util();
    Handler handler = null;
    private int i_time_out = 3000;
    private Socket mSocketClient = null;
    private PrintWriter out = null;
    private DataControl mDC;
    Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("ServiceSocket", "ServiceSocket is start.........");
        System.out.println("ServiceSocket is start.........");
        mDC = DataControl.getInstance();

        if(!mDC.bIsRemote && NetworkUtil.socket!=null && NetworkUtil.socket.isConnected()){
            serviceSocket();
        }else {
           // readFromWebService();//显示具体界面里的电器状态
             webSocket();
        }

        handler = new Handler();
        handler.postDelayed(runnable, i_time_out);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
        Log.v("CountService", "stopService on destroy.");
        System.out.println("stopService on destroy.");
        try {
            if((mSocketClient != null) && (mSocketClient.isConnected()))
            {
                mSocketClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.cancel();
        this.threadDisable = true;

    }

    //启动服务器数据处理线程，在后台处理从服务器接收数据，并通知更新界面
    void serviceSocket()
    {
        new Thread(new Runnable() {
            //该批注指令的作用是给编辑器一条指令，告诉它对被
            @SuppressWarnings("static-access")
            @Override
            public void run() {
                BufferedReader mBufferedReaderClient = null;
                //设置intent,让其能够通知上层调用
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MY_RECEIVER");
                try {
                    //等待连接上。由于此处一直在等待，所致直到连接上才会跳出while，
                    // 故下方的判断网络的都是多余的
                    while(NetworkUtil.socket == null || !checkNetConnection()) {
                        mDC.bIsRemote = true;
                       //readFromWebService();
                        webSocket();

                    }
                    //取得输入输出流
                    mBufferedReaderClient = new BufferedReader(new InputStreamReader(NetworkUtil.socket.getInputStream()));
                    out = NetworkUtil.out;
                    //NetworkUtil.out.println("230000000003000023");
                    System.out.println("进入主页面后查询全部电器状态的指令");
                    //一直接收网络数据
                    while(!threadDisable)
                    {
                        if((NetworkUtil.socket!=null)&&(NetworkUtil.socket.isConnected()))
                        {
                            String strRecvMsg = mBufferedReaderClient.readLine();
                            System.out.println("收到状态：" + strRecvMsg);
                            Log.i(TAG, "run: strRecvMsg-->"+strRecvMsg);
                            String strState = null;
                            Log.v("CountService","read:"+strRecvMsg);
                            //只处理以23开头并且以23结尾的字符串
                            if (strRecvMsg.startsWith("<"))  //处理返回状态
                            {
                                strState = strRecvMsg.substring(strRecvMsg.indexOf("<")+"<".length(), strRecvMsg.lastIndexOf(">"));
                                System.out.println("socket通信返回值： "+ strState);
                                Log.v("strState", strState);
                                //处理单元状态
                                Util.analyseSingleStatus(strState);
                                System.out.println("待处理的状态： " + strState);
                                sendBroadcast(intent);      //通知所有注册过的上层的应用程序
                            }

                            //清空buffer
                            /*for(int i=0;i<buffer.length;i++) {
                                buffer[i] = '\0';
                            }*/

                        }

                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("本地切换远程");
                    mDC.socketCrash = true;
                    mDC.bIsRemote = true;
                    webSocket();
                }
            }
        }).start();
    }
    void webSocket(){
        new Thread(){
            @Override
            public void run() {
                //设置intent,让其能够通知上层调用
                final Intent intent = new Intent();
                intent.setAction("android.intent.action.MY_RECEIVER");
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        new Thread(){
                            @Override
                            public void run() {
                                sendBroadcast(intent);
                            }
                        }.start();

                    }
                };
             timer.schedule(timerTask,500,500);


            }
        }.start();

    }
//
//    public void readFromWebService(){
//        new Thread(){
//            @Override
//            public void run() {
//                //设置intent,让其能够通知上层调用
//                final Intent intent = new Intent();
//                intent.setAction("android.intent.action.MY_RECEIVER");
//
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        new Thread(){
//                            @Override
//                            public void run() {
//                                System.out.println("开始从服务器读取数据");
//                                mDC.mWS.getElectricStateByUser(mDC.sAccountCode, mDC.sMasterCode);
//                                //System.out.println("结束从服务器读取数据");
//                                sendBroadcast(intent);
//                            }
//                        }.start();
//
//                    }
//                };
//                timer.schedule(timerTask,500,5000);
//
//
//            }
//        }.start();
//    }


    //定时器响应函数
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //要做的事情
            if((mSocketClient != null) && (mSocketClient.isConnected())) {
                Log.v("timer", "timer send");
                NetworkUtil.out.println("230000000003000023");
                System.out.println("进入主页面后查询全部电器状态的指令");
            }
            else {
                Log.v("timer", "mSocketClient is not Connected()");
                handler.postDelayed(this, i_time_out);
            }

        }
    };




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean checkNetConnection() {
        try{
            NetworkUtil.socket.sendUrgentData(0xFF);
            return true;
        }catch(Exception ex){
            System.out.println("Socket通信异常");
            mDC.bIsRemote = true;
            /*启动后台Service服务，接受网络数据*/
            return false;
        }
    }
}
