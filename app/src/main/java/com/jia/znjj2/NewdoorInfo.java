package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by 212 on 2017/10/26.
 */

public class NewdoorInfo extends Activity {
    public String flag;
    public ListView list1;
    public ImageView doorinfobk;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newdoor_info_detail);
        init();
        doorrecordlist();
    }

    public void init(){
        list1 = (ListView) findViewById(R.id.door_info_list);
        doorinfobk=(ImageView) findViewById(R.id.Door_info_title_back);
    }
    public void doorinfoBack(View view) {
        finish();
    }
    public void doorrecordlist(){
        flag= Newdoor.record;
        List DoorRecord = new ArrayList();
        List<Map<String,String>> listObjectDoor = (List<Map<String,String>>) JSONArray.parse(flag);
        for(Map<String,String> doorinfomap : listObjectDoor){
            for (Map.Entry entry : doorinfomap.entrySet()){
                if(entry.getKey().equals("openTime")||entry.getKey().equals("byPerson")){
                    DoorRecord.add(entry.getValue());
                }
            }
        }
        Collections.reverse(DoorRecord);
            String[] arr = (String[])DoorRecord.toArray(new String[DoorRecord.size()]);
            ArrayAdapter<String> adapter1 =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);
            list1.setAdapter(adapter1);

    }
}


