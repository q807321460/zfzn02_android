package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by 212 on 2017/10/31.
 */

public class Warnifo extends Activity{
    public ImageView Warninfobk;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warninfo);
        init();
    }
    public void init(){
        Warninfobk = (ImageView)findViewById(R.id.Warn_info_title_back);
    }
    public void warninfoBack(View v){
        finish();
    }
}
