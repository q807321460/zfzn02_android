package com.jia.znjj2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jia.camera.business.Business;
import com.jia.connection.MasterSocket;
import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;

import static com.jia.znjj2.LoginActivity.userlogin;

public class ChangeAccount extends Activity {
    private static final String TAG ="ChangeAccount" ;
    private DataControl mDC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);
        mDC=DataControl.getInstance();
    }
    public void gotoLogin(View v){
        finish();
        Intent intent = new Intent(ChangeAccount.this,LoginActivity.class);
        startActivity(intent);
    }

    public void changeAccountBack(View view){
        finish();
    }
}
