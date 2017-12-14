package com.jia.ezcamera.set;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.jia.znjj2.SecurityFragment;

/**
 * Created by Administrator on 2017/11/29.
 */
public class CameraSensorSetActivity extends FragmentActivity {
    private SecurityFragment mSecurityFragment;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(com.jia.znjj2.R.layout.activity_sensor_set);
        showSecurityFragment();

    }

    public void showSecurityFragment() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();//获取fragment管理器
        FragmentTransaction transaction = fm.beginTransaction();//打开事务
        if (mSecurityFragment == null) {
            mSecurityFragment = new SecurityFragment();
            transaction.add(com.jia.znjj2.R.id.sensor_set,mSecurityFragment);
        }else {
            transaction.show(mSecurityFragment);
        }
        transaction.commit();

    }

}
