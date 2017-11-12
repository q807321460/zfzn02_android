package com.jia.connection;

import android.util.Log;

import com.jia.data.DataControl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jia on 2016/5/26.
 */
public class MasterSocket {
    private static final String TAG = "MasterSocket";
    DataControl mDC;
    Socket socket=null;

    public MasterSocket() {
        this.mDC = DataControl.getInstance();
    }

    public String getMasterNodeCode() {
        if(!mDC.bUserHard){
            return null;
        }else {
            String str = "";
            //Socket socket = null;
            try {
                //Socket socket = new Socket(mDC.mAccountList.get(i).getsAccountUserIp(),mDC.mAccountList.get(i).getiAccountUserPort());

                socket = new Socket();
                //socket.connect(new InetSocketAddress("192.168.1.117", 3000), 5000);
                socket.connect(new InetSocketAddress(mDC.sUserIP,mDC.iUserPort),5000);
                socket.setSoTimeout(10000);//设置10秒后即认为超时
                final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final OutputStream os = socket.getOutputStream();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // task to run goes here
                        if(!socket.isClosed()){
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Timer timer = new Timer();
                long delay = 10000;
                // schedules the task to be run in an interval
                timer.schedule(task,delay);
                try {
                    os.write(("<00000000U0**********00>"+"\r\n").getBytes("utf-8"));
                    while(str.equals("") || !str.startsWith("#")){
                        str = br.readLine();
//                        br.read(bytes,0,64);
//                        str = String.copyValueOf(bytes);
//                        System.out.println("++++++++++++++++" + str);
                    }

                } catch (IOException e) {
                    System.out.println("+++++++AddAccount.class+++++++搜索主节点111");
                    Log.i(TAG, "getMasterNodeCode: " + "搜索主节点111");
                    e.printStackTrace();
                    //socket.close();
                }
            } catch (IOException e) {
                System.out.println("+++++++AddAccount.class+++++++搜索主节点222");
                Log.i(TAG, "getMasterNodeCode: "+"搜索主节点222");
                e.printStackTrace();

            }
            if (socket != null && socket.isConnected()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str;
        }
    }

    public String getMasterNodeCode(String userIP) {
        if(!mDC.bUserHard){
            return null;
        }else {
            String str = "";
            //Socket socket = null;
            try {
                //Socket socket = new Socket(mDC.mAccountList.get(i).getsAccountUserIp(),mDC.mAccountList.get(i).getiAccountUserPort());

                socket = new Socket();
                //socket.connect(new InetSocketAddress("192.168.1.117", 3000), 5000);
                socket.connect(new InetSocketAddress(userIP,8899),5000);
                socket.setSoTimeout(10000);//设置10秒后即认为超时
                final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final OutputStream os = socket.getOutputStream();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // task to run goes here
                        if(!socket.isClosed()){
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Timer timer = new Timer();
                long delay = 10000;
                // schedules the task to be run in an interval
                timer.schedule(task,delay);
                try {
                    os.write(("<00000000U0**********00>"+"\r\n").getBytes("utf-8"));
                    while(str.equals("") || !str.startsWith("#")){
                        str = br.readLine();
//                        br.read(bytes,0,64);
//                        str = String.copyValueOf(bytes);
//                        System.out.println("++++++++++++++++" + str);
                    }

                } catch (IOException e) {
                    System.out.println("+++++++AddAccount.class+++++++搜索主节点111");
                    Log.i(TAG, "getMasterNodeCode: " + "搜索主节点111");
                    e.printStackTrace();
                    //socket.close();
                }
            } catch (IOException e) {
                System.out.println("+++++++AddAccount.class+++++++搜索主节点222");
                Log.i(TAG, "getMasterNodeCode: "+"搜索主节点222");
                e.printStackTrace();

            }
            if (socket != null && socket.isConnected()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str;
        }
    }

    public String getInfoFromMasterNode(String string) {
        if(!mDC.bUserHard){
            return null;
        }else {
            String str = "";
            //Socket socket = null;
            try {
                //Socket socket = new Socket(mDC.mAccountList.get(i).getsAccountUserIp(),mDC.mAccountList.get(i).getiAccountUserPort());

                socket = new Socket();
                //socket.connect(new InetSocketAddress("192.168.1.117", 3000), 5000);
                socket.connect(new InetSocketAddress(mDC.sUserIP,mDC.iUserPort),5000);
                System.out.println("mDC.sUserIP: " + mDC.sUserIP + "mDC.iUserPort: "+ mDC.iUserPort);
                socket.setSoTimeout(10000);//设置10秒后即认为超时
                final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final OutputStream os = socket.getOutputStream();

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // task to run goes here
                        if(!socket.isClosed()){
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Timer timer = new Timer();
                long delay = 10000;
                // schedules the task to be run in an interval
                timer.schedule(task,delay);

                try {
                    os.write((string + "\r\n").getBytes("utf-8"));
                    while(str.equals("") || !str.startsWith("#"+ string.substring(1,3))){
                        str = br.readLine();
                        System.out.println("str------------->"+str);
//                        Log.i(TAG, "getInfoFromMasterNode: str" + str);
                    }

                } catch (IOException e) {
                    System.out.println("getInfoFromMasterNode: "+"添加电器异常1");
                    Log.i(TAG, "getInfoFromMasterNode: "+"添加电器异常1");
                    e.printStackTrace();
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("getInfoFromMasterNode: "+"添加电器异常2");
                Log.i(TAG, "getInfoFromMasterNode: "+"添加电器异常2");
                e.printStackTrace();

            }
            if (socket != null && socket.isConnected()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str;
        }
    }


    public String getIrStudyFromMasterNode(String string) {
        if(!mDC.bUserHard){
            return null;
        }else {
            String str = "";
            //Socket socket = null;
            try {
                //Socket socket = new Socket(mDC.mAccountList.get(i).getsAccountUserIp(),mDC.mAccountList.get(i).getiAccountUserPort());

                socket = new Socket();
                //socket.connect(new InetSocketAddress("192.168.1.117", 3000), 5000);
                socket.connect(new InetSocketAddress(mDC.sUserIP,mDC.iUserPort),5000);
                System.out.println("mDC.sUserIP: " + mDC.sUserIP + "mDC.iUserPort: "+ mDC.iUserPort);
                socket.setSoTimeout(10000);//设置10秒后即认为超时
                final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final OutputStream os = socket.getOutputStream();

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // task to run goes here
                        if(!socket.isClosed()){
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Timer timer = new Timer();
                long delay = 10000;
                // schedules the task to be run in an interval
                timer.schedule(task,delay);

                try {
                    os.write((string + "\r\n").getBytes("utf-8"));
                    while(str.equals("") || !str.startsWith("<09")){
                        str = br.readLine();
 //                       System.out.println("str------------->"+str);
//                        Log.i(TAG, "getInfoFromMasterNode: str" + str);
                    }

                } catch (IOException e) {
                    System.out.println("+++++++getIrStudyFromMasterNode+++++++1111");
                    Log.i(TAG, "getInfoFromMasterNode: "+"指令学习异常1");
                    e.printStackTrace();
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("+++++++getIrStudyFromMasterNode+++++++2222");
                Log.i(TAG, "getInfoFromMasterNode: "+"指令学习异常2");
                e.printStackTrace();

            }
            if (socket != null && socket.isConnected()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str;
        }
    }

    public String duiMa(String string,String electricCode) {
        if(!mDC.bUserHard){
            return null;
        }else {
            String str = "";
            //Socket socket = null;
            try {
                //Socket socket = new Socket(mDC.mAccountList.get(i).getsAccountUserIp(),mDC.mAccountList.get(i).getiAccountUserPort());

                socket = new Socket();
                //socket.connect(new InetSocketAddress("192.168.1.117", 3000), 5000);
                socket.connect(new InetSocketAddress(mDC.sUserIP,mDC.iUserPort),5000);
                System.out.println("mDC.sUserIP: " + mDC.sUserIP + "mDC.iUserPort: "+ mDC.iUserPort);
                socket.setSoTimeout(10000);//设置10秒后即认为超时
                final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final OutputStream os = socket.getOutputStream();

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // task to run goes here
                        if(!socket.isClosed()){
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Timer timer = new Timer();
                long delay = 10000;
                // schedules the task to be run in an interval
                timer.schedule(task,delay);

                try {
                    os.write((string + "\r\n").getBytes("utf-8"));
                    while(str.equals("") || !str.startsWith("#"+ string.substring(1,3))){
                        str = br.readLine();
//                        System.out.println("str------------->"+str);
//                        Log.i(TAG, "getInfoFromMasterNode: str" + str);
                    }

                } catch (IOException e) {
                    System.out.println("+++++++getInfoFromMasterNode+++++++添加电器异常1");
                    Log.i(TAG, "getInfoFromMasterNode: "+"添加电器异常1");
                    e.printStackTrace();
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("+++++++getInfoFromMasterNode+++++++添加电器异常2");
                Log.i(TAG, "getInfoFromMasterNode: "+"添加电器异常2");
                e.printStackTrace();

            }
            if (socket != null && socket.isConnected()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str;
        }
    }
}
