package com.jia.util;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 存放Socket通讯相关的东西
 * Created by Jia on 2016/3/30.
 *
 */

public class NetworkUtil {
    public static Socket socket = null;
    public static PrintWriter out = null;
    public static BufferedReader br = null;

    public NetworkUtil(){}
}
