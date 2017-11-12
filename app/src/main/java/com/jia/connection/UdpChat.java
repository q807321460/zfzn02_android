package com.jia.connection;

import com.jia.data.DataControl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jia on 2016/4/14.
 */
public class UdpChat {
    DataControl mDC;
    //使用常量作为本程序多点广播的地址
    private static final String BROADCAST_IP = "230.0.0.255";
    private String end_IP;
    //使用常量作为本程序多点广播的端口
    public static final int BROADCAST_PORT = 48899;
    //定义每个数据报的大小最大为4KB
    private static final int DATA_LEN= 4096;
    //定义本程序的MulticastSocket实例
    private MulticastSocket socket = null;
    private InetAddress broadcastAddress = null;
    private Scanner scan = null;

    //定义接收网络数据的字节数组
    byte[] inBuff = new byte[DATA_LEN];
    //以指定字节数组创建准备接收数据的DatagramPacket对象
    private DatagramPacket inPacket = new DatagramPacket(inBuff,inBuff.length);
    private DatagramPacket outPacket = null;
    private ArrayList<String> strs = new ArrayList<>();

    public  UdpChat(){
        mDC = DataControl.getInstance();
    }
    public ArrayList<String> init(String IP) throws IOException
    {
        InetAddress address = InetAddress.getByName(IP);
        try//创建键盘输入流
        {
            //创建用于发送、接收数据的MulticastSocket对象
            //由于该MulticastSocket对象需要接收数据，所以有指定端口
            socket = new MulticastSocket(BROADCAST_PORT);
            broadcastAddress = InetAddress.getByName(BROADCAST_IP);
            //将该socket加入到指定的多点广播地址
            socket.joinGroup(broadcastAddress);
            //设置本MulticastSocket发送的数据报会发送到本身
            socket.setLoopbackMode(false);
            socket.setSoTimeout(10000);
            //初始化发送用的DatagramPacket，它包含一个长度为0的字节数组
            outPacket = new DatagramPacket(new byte[0],0,address,48899);

            //将发送的字符串转换成字节数组
            byte[] buff = "HF-A11ASSISTHREAD".getBytes();
            //设置发送用的DatagramPacket里的字节数据
            outPacket.setData(buff);
            //发送数据
            socket.send(outPacket);
            while (mDC.bIsSearchMaster) {
                //读取Socket中的数据，读到的数据放在inPacket所封装的字节数组中
                socket.receive(inPacket);
                String str= new String(inBuff, 0, inPacket.getLength());
                if(!str.equals("HF-A11ASSISTHREAD")) {
                    strs.add(str);
                }
                System.out.println("聊天信息：" + new String(inBuff, 0, inPacket.getLength()));
            }
            socket.close();

        }
        finally
        {
            if(!socket.isClosed())
                socket.close();
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            //删除strs的第一个字符串，"HF-A11ASSISTHREAD"
//            if (strs.size() > 0) {
//                strs.remove(0);
//            }
            return strs;
        }

    }

}
