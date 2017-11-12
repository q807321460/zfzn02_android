package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/10/25.
 */
public class SecurityActivity extends Activity implements View.OnClickListener{
    private LinearLayout bottom_ll_main;
    private LinearLayout bottom_ll_area;
    private LinearLayout bottom_ll_scene;
    private LinearLayout bottom_ll_security;
    private TextView mTvSecurity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        init();
    }
    private void init(){
        bottom_ll_main = (LinearLayout) findViewById(R.id.bottom_ll_main);
        bottom_ll_area = (LinearLayout) findViewById(R.id.bottom_ll_area);
        bottom_ll_scene = (LinearLayout) findViewById(R.id.bottom_ll_scene);
        bottom_ll_security = (LinearLayout) findViewById(R.id.bottom_ll_security);
        mTvSecurity = (TextView) findViewById(R.id.bottom_tv_security);
        mTvSecurity.setTextColor(getResources().getColor(R.color.text_color_select));
        bottom_ll_main.setOnClickListener(this);
        bottom_ll_area.setOnClickListener(this);
        bottom_ll_scene.setOnClickListener(this);
        bottom_ll_security.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.bottom_ll_main:
                intent = new Intent(SecurityActivity.this, MainPageActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_area:
                intent = new Intent(SecurityActivity.this, AreaActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_scene:
                intent = new Intent(SecurityActivity.this, SenceActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
