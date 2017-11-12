package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jia.data.DataControl;

/**
 *  功能：
 *   显示兆峰的广告图片，测试同步
 *   初始化DataControl
 */
public class AdvertiseActivity extends Activity {
    private DataControl mDC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);
        Thread timer = new Thread()
        {
            public void run()
            {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent openLogin = new Intent(AdvertiseActivity.this,LoginActivity.class);
                    startActivity(openLogin);
                }
            }
        };
        
        timer.start();      //延时开始，1500ms后跳转到LoginActivity
        mDC = DataControl.getInstance();
        mDC.init(this);     //初始化DataControl
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
