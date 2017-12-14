package com.jia.ezcamera.add;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.jia.ezcamera.utils.BottomDialog;
import com.jia.ezcamera.zxing.encoding.EncodingHandler;
import com.jia.znjj2.R;


/**
 * Created by Administrator on 2016/5/17.
 */
public class SetupQrcordActivity2 extends Activity{
    private String codeString;
    private ImageView codeImg;
    private Bitmap bm = null;
    //public static Handler SetQrCodeHandler = null;
    // public static final int HANDLER_BIND_SUC = 0;
    // public static final int HANDLER_BIND_BY_ANOTHER = 1;
    private Context mContext = this;
    private Activity mActivity = this;
    //private BottomDialog hintDialog;
    private SensorManager sensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_qrcode2);
        //获得光照传感器
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor liaghtSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);//Sensor.TYPE_LIGHT 代表光照传感器
        sensorManager.registerListener(sensorEventListener,liaghtSensor, SensorManager.SENSOR_DELAY_NORMAL);
        codeString = getIntent().getStringExtra("QRCODESTRING");
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);

    }

    private BottomDialog tipsDialog;
    private void initView(){
        findViewById(R.id.btn_return).setOnClickListener(onClickListener);
        findViewById(R.id.btn_next).setOnClickListener(onClickListener);
        codeImg = (ImageView)findViewById(R.id.code_img);
        doInstance();
//        if(sp.isIfShowQrcodeDialog()) {
//            if (hintDialog == null) {
//                hintDialog = new BottomDialog(mActivity, getString(R.string.setup_qrcode_dialog_hint),
//                        getString(R.string.know2), getString(R.string.know3), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        hintDialog.dismiss();
//                    }
//                }, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        hintDialog.dismiss();
//                        sp.setIfShowQrcodeDialog(false);
//                    }
//                }, false);
//            }
//
//            hintDialog.show();
//            hintDialog.setHintImage(R.drawable.png_qrcode_dialog);
//        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.btn_next:
                    tipsDialog = new BottomDialog(mActivity,
                            mActivity.getString(R.string.qrcode_tips),
                            mActivity.getString(R.string.ok),
                            mActivity.getString(R.string.cancel),
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    if(ActivityAdd.myHandler!=null){
                                        ActivityAdd.myHandler.sendEmptyMessage(ActivityAdd.SEARCH_ADD);
                                    }
                                    tipsDialog.cancel();
                                    finish();
                                }
                            },
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    tipsDialog.dismiss();
                                }
                            },
                            false);
                    tipsDialog.show();

                    break;
            }
        }
    };


    private void doInstance() {
        if (!TextUtils.isEmpty(codeString)) {
            try {
                recycleBitmap();
                bm = EncodingHandler.createQRCode(codeString, 320);
                if (bm != null) {
                    codeImg.setImageBitmap(bm);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    private void recycleBitmap() {
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
    }


    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            //获取精度
            float acc = event.accuracy;
            //获取光线强度
            float lux = event.values[0];


            //设置当前activity的屏幕亮度
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            //0到1,调整亮度暗到全亮
            float light = lux/100;
            Log.e("DEBUG","acc:"+acc+";"+"lux："+lux+"    light:"+light);
            lp.screenBrightness = Float.valueOf(light);
            mActivity.getWindow().setAttributes(lp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
