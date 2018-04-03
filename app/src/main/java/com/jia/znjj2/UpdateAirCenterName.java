package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by ShengYi on 2018/4/3.
 */

public class UpdateAirCenterName  extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_air_center_name);
    }
    public void updateAirCentralNameBack(View view){
        finish();
    }
}
