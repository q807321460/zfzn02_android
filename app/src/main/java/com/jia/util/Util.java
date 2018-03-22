package com.jia.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.jia.data.DataControl;
import com.jia.model.ETAirLocal;
import com.jia.model.ETKeyLocal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.jia.znjj2.AirCenterMoreActivity.airCenterInfo;

/**
 * Created by Jia on 2016/5/15.
 */
public class Util {

    private static DataControl mDC;
    static {
        mDC = DataControl.getInstance();
    }

    //字节码转换为16进制字符串
    public static String byte2hex(byte[] bytes)
    {
        String hs = "";
        String stmp = "";
        for(int i = 0;i<bytes.length;i++) {
            stmp = Integer.toHexString(bytes[i] & 0XFF);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
    //16进制字符串转换成字节码
    public static byte[] hex2byte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for(int i =0;i<bytes.length;i++) {
            bytes[i] = Integer.decode("#" + str.substring(2 * i, 2 * i + 2)).byteValue();
        }
        return bytes;
    }

    //获得发搜索主节点，送广播的ip
    public static String intToEndIp(int i) {
        return (i & 0xff) + "." + ((i >> 8) & 0xff) + "." + ((i >> 16) & 0xff) + "." + ((i >> 24) | 0xff);
    }

    /**
     * 处理单个单元的状态
     * 待处理的字符串：
     * 地址	功能码	数据	   校验码
     4字节	1字节	1字节	1字节
     * @param string
     */
    public static void analyseSingleStatus(String string){
        String electricCode = null;
        String electricState = null;
        String stateInfo = null;
        if(string.length()==22){
            electricCode = string.substring(0,8);
             electricState = string.substring(8,10);
             stateInfo = string.substring(10,20);
        }else if(string.length()==26){
            electricCode = string.substring(0,12);
            electricState = string.substring(12,14);
            stateInfo = string.substring(14,24);
        }else if(string.length()>26){
            electricCode = string.substring(0,12);
            electricState = string.substring(12,14);
            stateInfo = string.substring(14,string.length()-2);
            airCenterInfo = stateInfo;
        }

        String[] strings = {electricState,stateInfo};

        System.out.println("电器编码"+electricCode + "电器状态码： " + electricState);
        //若返回的是本用户已添加过的电器的状态，才保存到内存和数据库当中
        if(mDC.mElectricState.keySet().contains(electricCode)){
            mDC.mElectricState.put(electricCode,strings);
            mDC.mElectricData.updateElectricState(electricCode,electricState,stateInfo);
            System.out.println("fdddddddddddddddddddddddddddddddddd");
        }

    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static String convertToJson(ETKeyLocal keyLocal){
        Gson gson = new Gson();
        return gson.toJson(keyLocal);
    }

    public static String convertToJson(List<ETKeyLocal> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }


    public static String convertETAirtoJson(ETAirLocal airLocal){
        String jsonStr = "";
        JSONObject airLocalJsonObject = new JSONObject();
        try {
            airLocalJsonObject.put("id",airLocal.getId());
            airLocalJsonObject.put("masterCode",airLocal.getMasterCode());
            airLocalJsonObject.put("electricIndex",airLocal.getElectricIndex());
            airLocalJsonObject.put("airBrand",airLocal.getAirBrand());
            airLocalJsonObject.put("airIndex",airLocal.getAirIndex());
            airLocalJsonObject.put("airTemp",airLocal.getAirTemp());
            airLocalJsonObject.put("airRate",airLocal.getAirRate());
            airLocalJsonObject.put("airDir",airLocal.getAirDir());
            airLocalJsonObject.put("airAutoDir",airLocal.getAirAutoDir());
            airLocalJsonObject.put("ariMode",airLocal.getAirMode());
            airLocalJsonObject.put("airPower",airLocal.getAirPower());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonStr;
    }
}
