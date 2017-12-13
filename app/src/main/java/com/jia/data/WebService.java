package com.jia.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jia.ir.db.DBProfile;
import com.jia.ir.db.ETDB;
import com.jia.model.ETAirLocal;
import com.jia.model.ETKeyLocal;
import com.jia.util.CreateImage;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jia on 2016/4/6.
 */
public class WebService {

    DataControl mDC;

    //命名空间
    private final static String SERVICE_NS = "http://ws.smarthome.zfznjj.com/";
    // EndPoint
   //private final static String SERVICE_URL = "http://192.168.1.133:8080/zfzn02/services/smarthome?wsdl=SmarthomeWs.wsdl";

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

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what)
            {
                case 0x123:
                    break;
            }
        }
    };

    public WebService()
    {
        ht = new HttpTransportSE(SERVICE_URL);  // ①
        ht.debug = true;
        mDC = DataControl.getInstance();
    }

    public void selectElectricForVoice(String masterCode){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "selectElectricForVoice";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    ArrayList<ElectricForVoice> list = new ArrayList<>();
                    int i;
                    for (i = 0; i < result.getPropertyCount()-1; i++) {
                        ElectricForVoice electricForVoice = new ElectricForVoice();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        electricForVoice.setElectricCode(obj.getProperty("electricCode").toString());
                        electricForVoice.setElectricName(obj.getProperty("electricName").toString());
                        electricForVoice.setRoomName(obj.getProperty("roomName").toString());
                        electricForVoice.setOrderInfo(obj.getProperty("orderInfo").toString());
                        list.add(electricForVoice);
                    }
                    //数据解析完成
                    for (ElectricForVoice e: list) {
                        System.out.println(e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }



    public synchronized String addAccount(String accountCode, String password, String accountName){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addAccount";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("password", password);
            soapObject.addProperty("accountName", accountName);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice checkUserPassword 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice checkUserPassword IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice checkUserPassword XmlPullParserException");
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String sendSms(String phoneNum, String msg){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "sendSms";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("phoneNum", phoneNum);
            soapObject.addProperty("msg", msg);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice sendSms 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice sendSms IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice sendSms XmlPullParserException");
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String signLeCheng(String accountCode){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "signLeCheng";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("accountCode", accountCode);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice signLeCheng 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice signLeCheng IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice signLeCheng XmlPullParserException");
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String updateElectric(String masterCode, String electricCode, int electricIndex, String electricName, int sceneIndex){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricCode", electricCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("electricName", electricName);
            soapObject.addProperty("sceneIndex", sceneIndex);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateElectric IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateElectric IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }
    public synchronized String updateElectric1(String masterCode, String electricCode, int electricIndex, String electricName, int sceneIndex, String electricOrder){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateElectric1";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricCode", electricCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("electricName", electricName);
            soapObject.addProperty("sceneIndex", sceneIndex);
            soapObject.addProperty("electricOrder", electricOrder);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateElectric IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateElectric IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }
    public synchronized String  updateElectricSequ(String masterCode,int electricIndex, int roomIndex,
                                      int oldElectricSequ, int newElectricSequ)
    {
        if(!mDC.bUseWeb)//不是远程
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateElectricSequ";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("roomIndex", roomIndex);
            soapObject.addProperty("oldElectricSequ",oldElectricSequ);
            soapObject.addProperty("newElectricSequ",newElectricSequ);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice deleteElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice deleteElectric IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice deleteElectric XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }


    public synchronized String updateSensorExtras(String masterCode, String electricCode, int electricIndex, String extras){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateSensorExtras";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricCode", electricCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("extras", extras);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateSensorExtras 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateSensorExtras IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateSensorExtras IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }


    public synchronized String updateIRKeyValue(String masterCode, int electricIndex, int keyKey, String keyValue){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateIRKeyValue";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("keyKey", keyKey);
            soapObject.addProperty("keyValue", keyValue);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateIRKeyValue 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateIRKeyValue IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateIRKeyValue IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String updateAccountPhoto(String path){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            byte[] photoBytes = null;
            try{
                FileInputStream fis = new FileInputStream(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count = 0;
                while((count = fis.read(buffer)) >= 0){
                    baos.write(buffer, 0, count);
                }
                photoBytes = baos.toByteArray();
            }catch(Exception e){
                e.printStackTrace();
                return "-1";
            }

            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateAccountPhoto";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("accountCode", mDC.sAccountCode);
            soapObject.addProperty("photo", photoBytes);
            new MarshalBase64().register(envelope);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateElectric IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateElectric IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String updateUserRoom(String masterCode, int roomIndex, String roomName, int roomImg){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateUserRoom";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("roomIndex", roomIndex);
            soapObject.addProperty("roomName", roomName);
            soapObject.addProperty("roomImg", roomImg);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateUserRoom 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateUserRoom IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateUserRoom IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }


    public synchronized String updateUserIP(String masterCode, String userIP){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateUserIP";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("userIP", userIP);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateUserIP 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateUserIP IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateUserIP IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }


    public synchronized String updateAccount(String accountCode, String accountName, String accountPhone, String accountAddress,
                                String accountEmail){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateAccount";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("accountName", accountName);
            soapObject.addProperty("accountPhone", accountPhone);
            soapObject.addProperty("accountAddress", accountAddress);
            soapObject.addProperty("accountEmail", accountEmail);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice checkUserPassword 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice checkUserPassword IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice checkUserPassword IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }
    /**
     * new
     * @param accountCode
     * @param password
     * @return
     */
    public synchronized String checkUserPassword(String accountCode, String password)
    {
        if(!mDC.bUseWeb)
        {
            return "1|0|0|0";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "validLogin";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("password", password);
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice checkUserPassword 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice checkUserPassword IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice checkUserPassword XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String updateAccountPassword(String accountCode, String oldPassword, String newPassword)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateAccountPassword";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("oldPassword", oldPassword);
            soapObject.addProperty("newPassword", newPassword);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateAccountPassword 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateAccountPassword IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateAccountPassword XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String deleteUser(String accountCode, String masterCode)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "deleteUser";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("masterCode", masterCode);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice deleteRoom 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice deleteRoom IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice deleteRoom XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -2 + "";
        }
    }



    public synchronized String deleteRoom(String masterCode, int roomIndex, int roomSequ)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "deleteRoom";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("roomIndex", roomIndex);
            soapObject.addProperty("roomSequ", roomSequ);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice deleteRoom 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice deleteRoom IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice deleteRoom XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }
    public synchronized String deleteElectric(String masterCode, String electricCode, int electricIndex, int electricSequ, int roomIndex)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "deleteElectric1";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricCode", electricCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("electricSequ", electricSequ);
            soapObject.addProperty("roomIndex", roomIndex);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice deleteElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice deleteElectric IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice deleteElectric XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String deleteScene(String masterCode, int sceneIndex ,int sceneSequ)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "deleteScene";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("sceneIndex", sceneIndex);
            soapObject.addProperty("sceneSequ", sceneSequ);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice deleteScene 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice deleteScene IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice deleteScene XmlPullParserException");
                e.printStackTrace();
            }
            return -2 + "";
        }
    }

    public synchronized String deleteSceneElectric(String masterCode, int electricIndex, int sceneIndex)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "deleteSceneElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("sceneIndex", sceneIndex);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice deleteSceneElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice deleteSceneElectric IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice deleteSceneElectric XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }


    public String adminSharedElectric(byte[] bytes)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "adminSharedElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("bytes", bytes);
            new MarshalBase64().register(envelope);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice adminSharedElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice adminSharedElectric IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice adminSharedElectric XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -2 + "";
        }
    }

    public synchronized String giveUpAdmin(String masterCode, String owner)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "giveUpAdmin";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("owner", owner);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice giveUpAdmin 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice giveUpAdmin IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice giveUpAdmin XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return "-2";
        }
    }
    public synchronized String updateUserName(String accountCode, String masterCode, String userName)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "updateUserName";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("userName", userName);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice giveUpAdmin 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice giveUpAdmin IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice giveUpAdmin XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return "-2";
        }
    }
    public synchronized String accessAdmin(String masterCode, String owner)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "accessAdmin";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("owner", owner);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice getAdmin 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice getAdmin IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice getAdmin XmlPullParserException");
                e.printStackTrace();
            }
            return "-2";
        }
    }
    public synchronized String getAdminAccountCode(String masterCode)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "getAdminAccountCode";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice getAdmin 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice getAdmin IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice getAdmin XmlPullParserException");
                e.printStackTrace();
            }
            return "-2";
        }
    }

    public synchronized String deleteSharedUser(String masterCode, String accountCode)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "deleteSharedUser";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("accountCode", accountCode);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice deleteSharedUser 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice deleteSharedUser IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice deleteSharedUser XmlPullParserException");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return "-2";
        }
    }

    public synchronized void loadAccountFromWs(String accountCode, String accountTime){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadAccountFromWs";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("accountTime", accountTime);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    result = (SoapObject)result.getProperty(0);
                    AccountData.AccountDataInfo accountDataInfo = new AccountData().new AccountDataInfo();
                    mDC.mAccount.setAccountCode(result.getProperty("accountCode").toString());
                    if(result.hasProperty("accountName")){
                        mDC.mAccount.setAccountName(result.getProperty("accountName").toString());
                    }
                    if(result.hasProperty("accountPhone")){
                        mDC.mAccount.setAccountPhone(result.getProperty("accountPhone").toString());
                    }
                    if(result.hasProperty("accountAddress")){
                        mDC.mAccount.setAccountAddress(result.getProperty("accountAddress").toString());
                    }
                    if(result.hasProperty("accountEmail")){
                        mDC.mAccount.setAccountEmail(result.getProperty("accountEmail").toString());
                    }
                    if(result.hasProperty("lePhone")){
                        mDC.mAccount.setLePhone(result.getProperty("lePhone").toString());
                    }
                    if(result.hasProperty("leSign")){
                        mDC.mAccount.setLeSign(Integer.parseInt(result.getProperty("leSign").toString()));
                    }
                    if (result.hasProperty("photo")){
                        byte[] bytes = Base64.decode(result.getProperty("photo").toString(),0);
//                        mDC.mAccount.setAccountPhoto(bytes);
                        CreateImage.createImage(mDC.sAccountCode,bytes,mDC.sAccountCode+".jpg");
                    }
                    mDC.mAccount.setAccountTime(result.getProperty("accountTime").toString());
                    //mDC.mAccount.setUserTime(result.getProperty("userTime").toString());
                    mDC.mAccountData.updateAccount(mDC.mAccount);
                    System.out.println(accountDataInfo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }

    public synchronized void loadAccountFromWsTest(String accountCode, String accountTime){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadAccountFromWs";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("accountTime", accountTime);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    result = (SoapObject)result.getProperty(0);
                    AccountData.AccountDataInfo accountDataInfo = new AccountData().new AccountDataInfo();
                    mDC.mAccount.setAccountCode(result.getProperty("accountCode").toString());
                    if(result.hasProperty("accountName")){
                        mDC.mAccount.setAccountName(result.getProperty("accountName").toString());
                    }
                    if(result.hasProperty("accountPhone")){
                        mDC.mAccount.setAccountPhone(result.getProperty("accountPhone").toString());
                    }
                    if(result.hasProperty("accountAddress")){
                        mDC.mAccount.setAccountAddress(result.getProperty("accountAddress").toString());
                    }
                    if(result.hasProperty("accountEmail")){
                        mDC.mAccount.setAccountEmail(result.getProperty("accountEmail").toString());
                    }
                    if(result.hasProperty("lePhone")){
                        mDC.mAccount.setLePhone(result.getProperty("lePhone").toString());
                    }
                    if(result.hasProperty("leSign")){
                        mDC.mAccount.setLeSign(Integer.parseInt(result.getProperty("leSign").toString()));
                    }
                    if (result.hasProperty("photo")){
                        mDC.mAccount.setAccountPhoto(
                                Base64.decode(result.getProperty("photo").toString(),0));
                    }
                    mDC.mAccount.setAccountTime(result.getProperty("accountTime").toString());
                    //mDC.mAccount.setUserTime(result.getProperty("userTime").toString());
                    mDC.mAccountData.updateAccount(mDC.mAccount);
                    System.out.println(accountDataInfo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }

    /**
     * 读取某一账户下的全部主节点用户
     * @param accountCode
     */
    public synchronized void loadUserFromWs(String accountCode, String userTime){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadUserFromWs";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("userTime", userTime);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    ArrayList<UserData.UserDataInfo> list = new ArrayList<>();
                    UserData userData = new UserData();
                    int i;
                    for (i = 0; i < result.getPropertyCount()-1; i++) {
                        UserData.UserDataInfo userDataInfo = userData.new UserDataInfo();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        userDataInfo.masterCode = obj.getProperty("masterCode").toString();
                        userDataInfo.accountCode = obj.getProperty("accountCode").toString();
                        userDataInfo.userName = obj.getProperty("userName").toString();
                        userDataInfo.userIP = obj.getProperty("userIp").toString();
                        userDataInfo.isAdmin = Integer.parseInt(obj.getProperty("isAdmin").toString());
                        list.add(userDataInfo);
                    }
                    //数据解析完成
                    //mDC.mAreaData.updateAreaDataFromWs(userName, list);   //将数据更新到本地DB
                    mDC.mUserData.loadUserFromWs(list);
                    SoapObject obj = (SoapObject) result.getProperty(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("user_time",obj.getProperty("extraTime").toString());
                    mDC.mAccountData.updateAccount(contentValues);


                    System.out.println("*************");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }

    /**
     * 读取该账户的所有分享的用户
     * @param masterCode
     */
    public synchronized void loadSharedAccount(String masterCode){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadSharedAccount";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    mDC.mSharedAccountList.clear();
                    for (int i = 0; i < result.getPropertyCount(); i++) {
                        AccountData.AccountDataInfo accountDataInfo = new AccountData().new AccountDataInfo();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        accountDataInfo.setAccountCode(obj.getProperty("accountCode").toString());
                        accountDataInfo.setAccountName(obj.getProperty("accountName").toString());
                        if(obj.hasProperty("accountPhone")){
                            accountDataInfo.setAccountPhone(obj.getProperty("accountPhone").toString());
                        }
                        if(obj.hasProperty("accountAddress")){
                            accountDataInfo.setAccountAddress(obj.getProperty("accountAddress").toString());
                        }
                        if(obj.hasProperty("accountEmail")){
                            accountDataInfo.setAccountEmail(obj.getProperty("accountEmail").toString());
                        }
                        if(result.hasProperty("lePhone")){
                            mDC.mAccount.setLePhone(result.getProperty("lePhone").toString());
                        }
                        if(result.hasProperty("leSign")){
                            mDC.mAccount.setLeSign(Integer.parseInt(result.getProperty("leSign").toString()));
                        }
                        if (obj.hasProperty("photo")){
                            byte[] bytes = Base64.decode(obj.getProperty("photo").toString(),0);
                            CreateImage.createImage(mDC.sAccountCode,bytes,accountDataInfo.getAccountCode()+".jpg");
                        }
                        mDC.mSharedAccountList.add(accountDataInfo);

                    }
                    mDC.mAccountData.loadSharedAccountFromWs(mDC.mSharedAccountList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }

    /**
     * 读取某一被分享的用户的全部电器
     * @param accountCode
     * @param masterCode
     */
    public void loadSharedElectric(String accountCode, String masterCode){

    }

    public synchronized void loadUserRoomFromWs(String masterCode, String areaTime){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadUserRoomFromWs";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", mDC.sAccountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("areaTime", areaTime);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    ArrayList<RoomData.RoomDataInfo> list = new ArrayList<>();
                    RoomData roomData = new RoomData();
                    int i;
                    for (i = 0; i < result.getPropertyCount()-1; i++) {
                        RoomData.RoomDataInfo roomDataInfo = roomData.new RoomDataInfo();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        roomDataInfo.masterCode = obj.getProperty("masterCode").toString();
                        roomDataInfo.roomIndex = Integer.parseInt(obj.getProperty("roomIndex").toString());
                        roomDataInfo.roomSequ = Integer.parseInt(obj.getProperty("roomSequ").toString());
                        roomDataInfo.roomImg = Integer.parseInt(obj.getProperty("roomImg").toString());
                        roomDataInfo.roomName = obj.getProperty("roomName").toString();
                        list.add(roomDataInfo);
                    }
                    mDC.mAreaData.loadUserFromWs(list);

                    SoapObject obj = (SoapObject) result.getProperty(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("area_time",obj.getProperty("extraTime").toString());
                    mDC.mUserData.updateUserData(contentValues);

                    System.out.println("*************");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }finally {
                resetParam();
            }
        }
    }

    public synchronized void loadElectricFromWs(String masterCode, String electricTime, Context context){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadElectricFromWs";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", mDC.sAccountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricTime", electricTime);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    ArrayList<ElectricInfoData> list = new ArrayList<>();
                    int i;
                    for (i = 0; i < result.getPropertyCount()-1; i++) {
                        ElectricInfoData electricInfoData = new ElectricInfoData();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        electricInfoData.masterCode = mDC.sMasterCode;
                        electricInfoData.roomIndex = Integer.parseInt(obj.getProperty("roomIndex").toString());
                        electricInfoData.electricIndex = Integer.parseInt(obj.getProperty("electricIndex").toString());
                        electricInfoData.electricName = obj.getProperty("electricName").toString();
                        electricInfoData.electricSequ = Integer.parseInt(obj.getProperty("electricSequ").toString());
                        electricInfoData.electricCode = obj.getProperty("electricCode").toString();
                        electricInfoData.electricType = Integer.parseInt(obj.getProperty("electricType").toString());
                        if(obj.hasProperty("extras")){
                            electricInfoData.extras = obj.getProperty("extras").toString();
                        }
                        if(obj.hasProperty("sceneIndex")){
                            electricInfoData.sceneIndex = Integer.parseInt(obj.getProperty("sceneIndex").toString());
                        }
                        if(obj.hasProperty("orderInfo")){
                            electricInfoData.orderInfo = obj.getProperty("orderInfo").toString();
                        }else {
                            electricInfoData.orderInfo = "**";
                        }

                        if(electricInfoData.getElectricCode().startsWith("09")){
                            loadKeyFromWs(electricInfoData.getMasterCode(),electricInfoData.getElectricIndex(),context);
                            if(electricInfoData.getElectricType() == 9||electricInfoData.getElectricType() == 21 ){
                                loadETAirByElectric(electricInfoData.getMasterCode(),electricInfoData.getElectricIndex(), context);
                            }
                        }
                        list.add(electricInfoData);
                    }
                    //数据解析完成
                    //mDC.mAreaData.updateAreaDataFromWs(userName, list);   //将数据更新到本地DB
                    mDC.mElectricData.loadElectricFromWs(list);

                    SoapObject obj = (SoapObject) result.getProperty(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("electric_time",obj.getProperty("extraTime").toString());
                    mDC.mUserData.updateUserData(contentValues);

                    System.out.println("*************");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }

    public synchronized void loadAllSharedElectric(String masterCode){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadAllSharedElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    ArrayList<ElectricSharedData.ElectricSharedLoacl> list = new ArrayList<>();
                    int i;
                    for (i = 0; i < result.getPropertyCount(); i++) {
                        ElectricSharedData.ElectricSharedLoacl electricShared = new ElectricSharedData().new ElectricSharedLoacl();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        electricShared.setMasterCode(obj.getProperty("masterCode").toString());
                        electricShared.setAccountCode(obj.getProperty("accountCode").toString());
                        electricShared.setElectricCode(obj.getProperty("electricCode").toString());
                        electricShared.setElectricIndex(Integer.parseInt(obj.getProperty("electricIndex").toString()));
                        electricShared.setElectricType(Integer.parseInt(obj.getProperty("electricType").toString()));
                        electricShared.setOrderInfo(obj.getProperty("orderInfo").toString());
                        electricShared.setRoomIndex(Integer.parseInt(obj.getProperty("roomIndex").toString()));
                        electricShared.setElectricName(obj.getProperty("electricName").toString());
                        electricShared.setIsShared(Integer.parseInt(obj.getProperty("isShared").toString()));
                        list.add(electricShared);
                    }
                    //数据解析完成
                    mDC.mElectricSharedData.loadSharedElectricFromWs(list);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void loadSceneFromWs(String masterCode,String sceneTime){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadSceneFromWs";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", mDC.sAccountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("sceneTime", sceneTime);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    ArrayList<SceneData.SceneDataInfo> list = new ArrayList<>();
                    SceneData sceneData = new SceneData();
                    int i;
                    for (i = 0; i < result.getPropertyCount()-1; i++) {
                        SceneData.SceneDataInfo sceneDataInfo = sceneData.new SceneDataInfo();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        sceneDataInfo.setMasterCode(obj.getProperty("masterCode").toString());
                        sceneDataInfo.setSceneName(obj.getProperty("sceneName").toString());
                        sceneDataInfo.setSceneIndex(Integer.parseInt(obj.getProperty("sceneIndex").toString()));
                        sceneDataInfo.setSceneSequ(Integer.parseInt(obj.getProperty("sceneSequ").toString()));
                        sceneDataInfo.setSceneImg(Integer.parseInt(obj.getProperty("sceneImg").toString()));
                        list.add(sceneDataInfo);
                    }
                    mDC.mSceneData.loadSceneFromWs(list);

                    SoapObject obj = (SoapObject) result.getProperty(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("scene_time",obj.getProperty("extraTime").toString());
                    mDC.mUserData.updateUserData(contentValues);

                    System.out.println("*************loadSceneFromWs");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }finally {
                resetParam();
            }
        }
    }

    public synchronized void loadSceneElectricFromWs(String masterCode,String sceneElectricTime){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadSceneElectricFromWs";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", mDC.sAccountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("sceneElectricTime", sceneElectricTime);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    //解析数据
                    ArrayList<SceneElectricData.SceneElectricInfo> list = new ArrayList<>();
                    SceneElectricData sceneElectricData = new SceneElectricData();
                    int i;
                    for (i = 0; i < result.getPropertyCount()-1; i++) {
                        SceneElectricData.SceneElectricInfo sceneElectricInfo = sceneElectricData.new SceneElectricInfo();
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        sceneElectricInfo.setMasterCode(obj.getProperty("masterCode").toString());
                        sceneElectricInfo.setElectricIndex(Integer.parseInt(obj.getProperty("electricIndex").toString()));
                        sceneElectricInfo.setElectricName(obj.getProperty("electricName").toString());
                        sceneElectricInfo.setElectricCode(obj.getProperty("electricCode").toString());
                        sceneElectricInfo.setElectricOrder(obj.getProperty("electricOrder").toString());
                        sceneElectricInfo.setRoomIndex(Integer.parseInt(obj.getProperty("roomIndex").toString()));
                        sceneElectricInfo.setElectricType(Integer.parseInt(obj.getProperty("electricType").toString()));
                        sceneElectricInfo.setSceneIndex(Integer.parseInt(obj.getProperty("sceneIndex").toString()));
                        if (obj.hasProperty("orderInfo")) {
                            sceneElectricInfo.setOrderInfo(obj.getProperty("orderInfo").toString());
                        }else {
                            sceneElectricInfo.setOrderInfo("**");
                        }
                        list.add(sceneElectricInfo);
                    }
                    mDC.mSceneElectricData.loadSceneElectricFromWs(list);

                    SoapObject obj = (SoapObject) result.getProperty(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("scene_electric_time",obj.getProperty("extraTime").toString());
                    mDC.mUserData.updateUserData(contentValues);

                    System.out.println("*************loadSceneElectricFromWs");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }



    public void getAppinfo()
    {
        if (!mDC.bUseWeb){
            return ;
        }else {
            methodName = "getAppInfo";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    SoapObject obj = (SoapObject) result.getProperty(0);
                    mDC.appInfo.setAppName(obj.getProperty("appName").toString());
                    mDC.appInfo.setAppVersion(obj.getProperty("appVersion").toString());
                    mDC.appInfo.setDownPath(obj.getProperty("downPath").toString());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    /**
     * 更新电器指令
     * @param masterCode
     * @param electricCode
     * @param electricOrder
     * @param orderInfo
     */
    public synchronized void updateElectricOrder(String masterCode,String electricCode, String electricOrder,String orderInfo)
    {
        if (!mDC.bUseWeb){
            return ;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "updateElectricOrder";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricCode", electricCode);
            soapObject.addProperty("order", electricOrder);
            soapObject.addProperty("orderInfo", orderInfo);
            try {
                ht.call(soapAction, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }


    /**
     * 添加user
     * @param accountCode
     * @param masterCode
     * @param userName
     * @param userIp
     */
    public synchronized String addUser(String accountCode, String masterCode, String userName, String userIp)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addUser";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("userName", userName);
            soapObject.addProperty("userIp", userIp);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addUser 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return "-2";
        }
    }

    public synchronized String addSharedUser(String accountCode, String masterCode, String userName, String userIp)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addSharedUser";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("userName", userName);
            soapObject.addProperty("userIp", userIp);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addSharedUser 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return "-2";
        }
    }


    /**
     * 添加房间
     * @param masterCode
     * @param roomIndex
     * @param roomName
     * @param roomSequ
     */
    public synchronized String addUserRoom(String masterCode,int roomIndex, String roomName,int roomSequ, int roomImg)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addUserRoom";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("roomIndex", roomIndex);
            soapObject.addProperty("roomName", roomName);
            soapObject.addProperty("roomSequ", roomSequ);
            soapObject.addProperty("roomImg", roomImg);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addUserRoom 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return "-2";
        }
    }
    public synchronized String IsExistElectric(String masterCode, String electricCode)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "isExistElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode",masterCode);
            soapObject.addProperty("electricCode", electricCode);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addScene 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
        return "-2";
    }

    public synchronized void addElectric(String masterCode, int electricIndex, String electricCode, int roomIndex,
                            String electricName, int electricSequ, int electricType, String extra, String orderInfo)
    {
        if(!mDC.bUseWeb)
        {
            return ;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode",masterCode);
            soapObject.addProperty("electricCode", electricCode);
            soapObject.addProperty("electricName", electricName);
            soapObject.addProperty("electricType", electricType);
            soapObject.addProperty("roomIndex", roomIndex);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("electricSequ", electricSequ);
            soapObject.addProperty("extra", extra);
            soapObject.addProperty("orderInfo", orderInfo);

            try {
                ht.call(soapAction, envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }

    /**
     * 添加情景模式
     * @param accountCode
     * @param masterCode
     * @param sceneName
     * @param sceneIndex
     * @param sceneSequ
     * @param sceneImg
     * @return
     */
    public synchronized String addScene(String accountCode, String masterCode, String sceneName, int sceneIndex,int sceneSequ, int sceneImg)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            methodName = "addScene";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("sceneName", sceneName);
            soapObject.addProperty("sceneIndex", sceneIndex);
            soapObject.addProperty("sceneSequ", sceneSequ);
            soapObject.addProperty("sceneImg", sceneImg);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addScene 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return "-2";
        }
    }

    public synchronized String addSceneElectric(String masterCode, String electricCode, String electricOrder, String accountCode,
                                   int sceneIndex, String orderInfo,int electricIndex,String electricName,
                                   int roomIndex, int electricType)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addSceneElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricCode", electricCode);
            soapObject.addProperty("sceneIndex", sceneIndex);
            soapObject.addProperty("electricOrder", electricOrder);
            soapObject.addProperty("orderInfo", orderInfo);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("electricName", electricName);
            soapObject.addProperty("roomIndex", roomIndex);
            soapObject.addProperty("electricType", electricType);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addSceneElectric 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return "-2";
        }
    }

    public synchronized String updateSceneElectricOrder(String masterCode, int electricIndex, int sceneIndex, String electricOrder)
    {
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "updateSceneElectricOrder";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("sceneIndex", sceneIndex);
            soapObject.addProperty("electricOrder", electricOrder);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息F;
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addSceneOrder 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return "-2";
        }
    }

    public synchronized void getElectricStateByUser(String accountCode,String masterCode)
    {
        if (!mDC.bUseWeb){
            return ;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "getElectricStateByUser";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("accountCode", accountCode);
            soapObject.addProperty("masterCode", masterCode);
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    for (int i = 0; i < result.getPropertyCount(); i++) {
                        SoapObject obj = (SoapObject) result.getProperty(i);
                        String electricCode = obj.getProperty("electricCode").toString();
                        String electricState = obj.getProperty("electricState").toString();
                        String stateInfo = obj.getProperty("stateInfo").toString();
                        String[] strings = {electricState,stateInfo};
                        System.out.println("webService: " + "electricCode"+electricCode + "electricState"+electricState);
                        //mDC.mDeviceData.updateElectricState(electricCode,electricState,stateInfo);
                        mDC.mElectricState.put(electricCode,strings);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            finally {
                resetParam();
            }
        }
    }

    public synchronized String getAppVersion(){
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "getAppVersion";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice checkUserPassword 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice checkUserPassword IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice checkUserPassword IOException2");
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized String addETKeys(List<ETKeyLocal> list){
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
//            String keyJsonString = JsonPluginsUtil.beanListToJson(list);
            String keyJsonString = new Gson().toJson(list);
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addETKeys";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("keyJsonString",keyJsonString);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addETKeys 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice addETKeys IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice addETKeys IOException2");
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public synchronized void loadKeyFromWs(String masterCode, int electricIndex, Context context){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadKeyByElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricIndex", electricIndex);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    String str = result.getProperty(0).toString();
                    List<ETKeyLocal> list = new Gson().fromJson(str, new TypeToken<List<ETKeyLocal>>() {}.getType());
                    saveOrUpdate(list, context);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }

    public synchronized void loadETAirByElectric(String masterCode, int electricIndex, Context context){
        if(!mDC.bUseWeb){
            return;
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "loadETAirByElectric";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricIndex", electricIndex);
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$");
                ht.call(soapAction, envelope);
                System.out.println("##############################");
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    String str = result.getProperty(0).toString();
                    ETAirLocal etAir = new Gson().fromJson(str,ETAirLocal.class);
                    saveOrUpdateETAir(etAir, context);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                resetParam();
            }
        }
    }
    private void saveOrUpdateETAir(ETAirLocal etAir, Context context){
        ETDB db = ETDB.getInstance(context);
        String sql = "SELECT * FROM ETAirDevice WHERE master_code = ? and electric_index = ?";
        ContentValues contentValues = new ContentValues();
        try{
            Cursor cursor = db.queryData2Cursor(sql,
                    new String[]{etAir.getMasterCode(),
                            String.valueOf(etAir.getElectricIndex())});
            if(cursor.getCount() > 0){
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_BRAND,etAir.getAirBrand());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_INDEX,etAir.getAirIndex());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_TEMP,etAir.getAirTemp());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_RATE,etAir.getAirRate());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_DIR,etAir.getAirDir());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_AUTO_DIR,etAir.getAirAutoDir());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_MODE,etAir.getAirMode());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_POWER,etAir.getAirPower());
                String where = "master_code = ? and electric_index = ?";
                db.updataData(DBProfile.AIRDEVICE_TABLE_NAME,contentValues,where,
                        new String[]{etAir.getMasterCode(),
                                String.valueOf(etAir.getElectricIndex())});
            }else {
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_MASTERCODE,etAir.getMasterCode());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_ELECTRICINDEX,etAir.getElectricIndex());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_BRAND,etAir.getAirBrand());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_INDEX,etAir.getAirIndex());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_TEMP,etAir.getAirTemp());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_RATE,etAir.getAirRate());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_DIR,etAir.getAirDir());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_AUTO_DIR,etAir.getAirAutoDir());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_MODE,etAir.getAirMode());
                contentValues.put(DBProfile.TABLE_AIRDEVICE_FIELD_POWER,etAir.getAirPower());
                db.insertData(DBProfile.AIRDEVICE_TABLE_NAME,contentValues);
            }
            contentValues.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveOrUpdate(List<ETKeyLocal> list, Context context){
        ETDB db = ETDB.getInstance(context);
        String sql = "SELECT * FROM ETKEY WHERE master_code = ? and electric_index = ? and key_key = ?";
        ContentValues contentValues = new ContentValues();
        for (ETKeyLocal keyLocal : list) {
            try{
                Cursor cursor = db.queryData2Cursor(sql,
                        new String[]{keyLocal.getMasterCode(),
                                String.valueOf(keyLocal.getElectricIndex()),
                                String.valueOf(keyLocal.getKeyKey())});
                if(cursor.getCount() > 0){
                    contentValues.put("key_value",keyLocal.getKeyValue());
                    String where = "master_code = ? and electric_index = ? and key_key = ?";
                    db.updataData(DBProfile.KEY_TABLE_NAME,contentValues,where,
                            new String[]{keyLocal.getMasterCode(),
                            String.valueOf(keyLocal.getElectricIndex()),
                            String.valueOf(keyLocal.getKeyKey())});
                }else {
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_MASTER_CODE,keyLocal.getMasterCode());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_ELECTRIC_INDEX,keyLocal.getElectricIndex());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_DEVICE_ID,keyLocal.getDid());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_NAME,keyLocal.getKeyName());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_RES,keyLocal.getKeyRes());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_X,keyLocal.getKeyX());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_Y,keyLocal.getKeyY());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_KEYVALUE,keyLocal.getKeyValue());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_KEY,keyLocal.getKeyKey());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_BRANDINDEX,keyLocal.getKeyBrandIndex());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_BRANDPOS,keyLocal.getKeyBrandPos());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_ROW,keyLocal.getKeyRow());
                    contentValues.put(DBProfile.TABLE_KEY_FIELD_STATE,keyLocal.getKeyState());
                    db.insertData(DBProfile.KEY_TABLE_NAME,contentValues);
                }
                contentValues.clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public synchronized String addETAirDevice(ETAirLocal etAirLocal){
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
//            String keyJsonString = JsonPluginsUtil.beanListToJson(list);
            String eTAirJsonString = new Gson().toJson(etAirLocal);
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;

            methodName = "addETAirDevice";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            soapObject.addProperty("eTAirJsonString",eTAirJsonString);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice addETAirDevice 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice addETAirDevice IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice addETAirDevice IOException2");
                e.printStackTrace();
            }finally {
                resetParam();
            }
            return -1 + "";
        }
    }
    public synchronized String loadDoorRecord(String masterCode,String electricCode) {
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        SoapSerializationEnvelope envelope;
        SoapObject soapObject;
        SoapObject result;
        methodName = "loadDoorRecord";
        soapAction = SERVICE_NS + methodName;
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
        // 实例化SoapObject对象
        soapObject = new SoapObject(SERVICE_NS, methodName); // ③
        // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
        envelope.bodyOut = soapObject;  // ⑤
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        soapObject.addProperty("masterCode", masterCode);
        soapObject.addProperty("electricCode", electricCode);
        try {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$");
            ht.call(soapAction, envelope);
            System.out.println("##############################");
            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息
                result = (SoapObject) envelope.bodyIn;
                String str = result.getProperty(0).toString();
                return str;
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

    public String loadAlarmRecord(String  masterCode){
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        SoapSerializationEnvelope envelope;
        SoapObject soapObject;
        SoapObject result;
        methodName = "loadAlarmRecord";
        soapAction = SERVICE_NS + methodName;
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
        // 实例化SoapObject对象
        soapObject = new SoapObject(SERVICE_NS, methodName); // ③
        // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
        envelope.bodyOut = soapObject;  // ⑤
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        soapObject.addProperty("masterCode", masterCode);
        try {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$");
            ht.call(soapAction, envelope);
            System.out.println("##############################");
            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息
                result = (SoapObject) envelope.bodyIn;
                String str = result.getProperty(0).toString();
                return str;
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
    public synchronized String updateSceneName(String masterCode, int sceneIndex, String sceneName, int sceneImg){
        if(!mDC.bUseWeb)
        {
            return "-1";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "updateSceneName";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);

            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("sceneIndex", sceneIndex);
            soapObject.addProperty("sceneName", sceneName);
            soapObject.addProperty("sceneImg", sceneImg);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateSceneName 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateSceneName IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateSceneName IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    public  String moveElectricToAnotherRoom(String masterCode, int electricIndex,  int roomIndex){
        if(!mDC.bUseWeb)
        {
            return "-2";
        }else {
            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL) ;
            ht.debug = true;
            SoapSerializationEnvelope envelope;
            SoapObject soapObject;
            SoapObject result;
            methodName = "moveElectricToAnotherRoom";
            soapAction = SERVICE_NS + methodName;

            // 使用SOAP1.1协议创建Envelop对象
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  // ②
            // 实例化SoapObject对象
            soapObject = new SoapObject(SERVICE_NS, methodName); // ③
            // 将soapObject对象设置为 SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = soapObject;  // ⑤
            envelope.dotNet = true;
            envelope.setOutputSoapObject(soapObject);
            StringBuffer str = new StringBuffer();
            soapObject.addProperty("masterCode", masterCode);
            soapObject.addProperty("electricIndex", electricIndex);
            soapObject.addProperty("roomIndex", roomIndex);

            try {
                ht.call(soapAction, envelope);
                if (envelope.getResponse() != null) {
                    // 获取服务器响应返回的SOAP消息
                    result = (SoapObject) envelope.bodyIn; // ⑦
                    // 接下来就是从SoapObject对象中解析响应数据的过程了
                    String flag = result.getProperty(0).toString();
                    System.out.println("*********Webservice updateSceneName 服务器返回值：" + flag);
                    return flag;
                }
            } catch (IOException e) {
                System.out.println("*********Webservice updateSceneName IOException1");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                System.out.println("*********Webservice updateSceneName IOException2");
                e.printStackTrace();
            } finally {
                resetParam();
            }
            return -1 + "";
        }
    }

    private void resetParam(){
        envelope = null;
        soapObject = null;
        result = null;
    }




}
