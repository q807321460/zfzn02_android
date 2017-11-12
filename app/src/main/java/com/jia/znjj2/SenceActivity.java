package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/10/25.
 */
public class SenceActivity extends Activity implements View.OnClickListener{
    private LinearLayout bottom_ll_main;
    private LinearLayout bottom_ll_area;
    private LinearLayout bottom_ll_scene;
    private LinearLayout bottom_ll_security;
    private TextView mTvScene;
    private ImageView ivBackMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        init();
    }

    private void init(){
        bottom_ll_main = (LinearLayout) findViewById(R.id.bottom_ll_main);
        bottom_ll_area = (LinearLayout) findViewById(R.id.bottom_ll_area);
        bottom_ll_scene = (LinearLayout) findViewById(R.id.bottom_ll_scene);
        bottom_ll_security = (LinearLayout) findViewById(R.id.bottom_ll_security);
        mTvScene = (TextView) findViewById(R.id.bottom_tv_scene);
        mTvScene.setTextColor(getResources().getColor(R.color.text_color_select));
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
                intent = new Intent(SenceActivity.this, MainPageActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_area:
                intent = new Intent(SenceActivity.this, AreaActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_ll_security:
                intent = new Intent(SenceActivity.this, SecurityActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


}
