package com.jia.crash;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by luojuan on 2017/11/9.
 */

public class CrashWebservice {


    //命名空间
    private final static String SERVICE_NS = "http://ws.smarthome.zfznjj.com/";
    // EndPoint
    //private final static String SERVICE_URL = "http://192.168.1.104:8080/zfzn02/services/smarthome?wsdl=SmarthomeWs.wsdl";

    //阿里云
    private final static String SERVICE_URL = "http://101.201.211.87:8080/zfzn02/services/smarthome?wsdl=SmarthomeWs.wsdl";
    //lv
    //private final static String SERVICE_URL = "http://192.168.0.104:8080/ZFZNJJ_WS/services/samrthome?wsdl=SmarthomeWs.wsdl";
    // SOAP Action
    private static String soapAction = "";
    // 调用的方法名称
    private static String methodName = "";
    private HttpTransportSE ht;
    private SoapSerializationEnvelope envelope;
    private SoapObject soapObject;
    private SoapObject result;

    /**
     * 用于向服务器上传崩溃日志
     *
     * @param logName
     *           崩溃日志的文件名称
     * @param logDetail
     *            崩溃日志的具体内容
     * @param appName
     *            app名称
     * @return 服务器的反馈信息
     */
    public String addCrashLog(String logName, byte[] logDetail, String appName) {
        // HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
        ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
		/*
		 * SoapSerializationEnvelope envelope; SoapObject soapObject; SoapObject
		 * result;
		 */

        methodName = "addCrashLog";
        soapAction = SERVICE_NS + methodName;// 通常为命名空间 + 调用的方法名称

        // 使用SOAP1.1协议创建Envelop对象
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11); // ②
        // 实例化SoapObject对象
        soapObject = new SoapObject(SERVICE_NS, methodName); // ③
        // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
        envelope.bodyOut = soapObject; // ⑤
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        soapObject.addProperty("logName", logName);
        soapObject.addProperty("logDetail", logDetail);
        soapObject.addProperty("appName", appName);
        try {
            // System.out.println("测试1");
            // 必须加上这句，否则报错
            new MarshalBase64().register(envelope);
            ht.call(soapAction, envelope);
            // System.out.println("测试2");
            // 根据测试发现，运行这行代码时有时会抛出空指针异常，使用加了一句进行处理
            if (envelope != null && envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息
                // System.out.println("测试3");
                result = (SoapObject) envelope.bodyIn; // ⑦
                // 接下来就是从SoapObject对象中解析响应数据的过程了
                // System.out.println("测试4");
                String flag = result.getProperty(0).toString();
                Log.e(TAG, "*********Webservice masterReadElecticOrder 服务器返回值："
                        + flag);
                return flag;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            resetParam();
        }
        return -1 + "";

    }
    private void resetParam(){
        envelope = null;
        soapObject = null;
        result = null;
    }


}
