package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.jia.widget.PickerView;
import com.jia.widget.PickerView.onSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212 on 2018/1/24.
 */

public class WeekTime extends Activity {
    PickerView whour_pv;
    PickerView wminute_pv;
    public String wkhour;
    public String wkminute;
    public static String wkTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_time_selector);
        whour_pv = (PickerView) findViewById(R.id.whour_pv);
        wminute_pv = (PickerView) findViewById(R.id.wminute_pv);
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        wkhour = "0";
        wkminute = "0";
        for (int i = 0; i <23; i++)
        {
            data.add("" + i);}
        for (int i = 0; i < 60; i++)
        {
            seconds.add(i < 23 ? "" + i : "" + i);
        }
        whour_pv.setData(data);
        whour_pv.setSelected(0);
        whour_pv.setOnSelectListener(new onSelectListener()
        {

            @Override
            public void onSelect(String text)
            {
                        wkhour = text;
            }
        });
        wminute_pv.setData(seconds);
        wminute_pv.setSelected(0);
        wminute_pv.setOnSelectListener(new onSelectListener()
        {

            @Override
            public void onSelect(String text)
            {
                        wkminute = text;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void wkTimeSave(View view){
        Integer hour = Integer.valueOf(wkhour);
        if(hour<10){
            wkhour="0"+wkhour;
        }
        Integer minute = Integer.valueOf(wkminute);
        if(minute<10){
            wkminute="0"+wkminute;
        }
        wkTime = wkhour+":"+wkminute+":00";
        Toast.makeText(WeekTime.this, "选择了"+wkTime+"，保存成功",
                Toast.LENGTH_LONG).show();
        finish();
    }

}

