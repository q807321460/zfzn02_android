package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChangeAccount extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);
    }
    public void gotoLogin(View v){
        Intent intent = new Intent(ChangeAccount.this,LoginActivity.class);
        startActivity(intent);
    }
}
