package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jia.znjj2.AirCenterMoreActivity.aircenterNamelist8;
import static com.jia.znjj2.AirCenterMoreActivity.aircenterNumberlist1;

/**
 * Created by ShengYi on 2018/4/3.
 */

public class UpdateAirCenterName  extends Activity {
    private ArrayList<String> list1;
    private ArrayList<String> list2;
    public static int airCenterClickPosition;
    public ListView airCenterNameList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_air_center_name);
        initdata();
        airCenterNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                System.out.println(position);
                airCenterClickPosition = position;
                Intent intent = new Intent(UpdateAirCenterName.this,AirCenterSingleName.class);
                startActivity(intent);
            }
        });
    }
    protected void onResume() {
        super.onResume();
        initdata();
        airCenterNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                System.out.println(position);
                airCenterClickPosition = position;
                Intent intent = new Intent(UpdateAirCenterName.this,AirCenterSingleName.class);
                startActivity(intent);
            }
        });
    }


    public void updateAirCentralNameBack(View view) {
        finish();
    }


    public void initdata(){
        list1=aircenterNumberlist1;
        list2=aircenterNamelist8;
        List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i=0;i<list1.size();i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("air_center_single_no","空调编号："+list2.get(i));
            listItem.put("air_center_single_name","空调房间名："+list1.get(i));
            listItems.add(listItem);
            SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItems,R.layout.air_center_name_list,
                    new String[]{"air_center_single_no","air_center_single_name"},
                    new int[]{R.id.air_center_single_no,R.id.air_center_single_name});

            airCenterNameList = (ListView) findViewById(R.id.air_center_detail_list);
            airCenterNameList.setAdapter(simpleAdapter);
        }
    }
}
