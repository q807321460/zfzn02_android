package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212 on 2017/10/31.
 */

public class Warnifo extends Activity{
    public ImageView Warninfobk;
    public String flag;
    public String a;
    public String b;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warninfo);
        init();
        updatewarnInfo();
    }
    public void init(){
        Warninfobk = (ImageView)findViewById(R.id.Warn_info_title_back);
    }
    public void warninfoBack(View v){
        finish();
    }
    public void updatewarnInfo(){
        flag=HomeFragment.warnInfo;
        List electricType = new ArrayList();
        List WarnelectricName = new ArrayList();
        List WarnroomName = new ArrayList();
        List StateInfoCode = new ArrayList();
        List StateInfo = new ArrayList();
        List AlarmTime =new ArrayList();
        List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
        List<Map<String,String>> listObjectWarninfo = (List<Map<String,String>>) JSONArray.parse(flag);
        for(Map<String,String> doorinfomap : listObjectWarninfo){
            for (Map.Entry entry : doorinfomap.entrySet()){
                if(entry.getKey().equals("electricName")){
                    WarnelectricName.add(entry.getValue());
                }if (entry.getKey().equals("roomName")){
                    WarnroomName.add(entry.getValue());
                }if(entry.getKey().equals("stateInfo")){
                    StateInfoCode.add(entry.getValue());
                }if(entry.getKey().equals("alarmTime")){
                    AlarmTime.add(entry.getValue());
                }if(entry.getKey().equals("electricType")){
                    electricType.add(entry.getValue());
                }
            }
        }
        Collections.reverse(WarnelectricName);
        Collections.reverse(WarnroomName);
        Collections.reverse(StateInfoCode);
        Collections.reverse(AlarmTime);
        Collections.reverse(electricType );
        for (int i=0; i<StateInfoCode.size();i++){
            a = (String) StateInfoCode.get(i);
            b = a.substring(0,2);
            if(b.equals("00")){
                StateInfo.add("普通");
            }else if(b.equals("01")){
                StateInfo.add("报警");
            }else if(b.equals("02")){
                StateInfo.add("防拆");
            }else if(b.equals("03")){
                StateInfo.add("报警+防拆");
            }else if(b.equals("04")){
                StateInfo.add("电量低");
            }else if(b.equals("05")){
                StateInfo.add("报警+电量低");
            }else if(b.equals("06")){
                StateInfo.add("防拆+电量低");
            }else if(b.equals("07")){
                StateInfo.add("防拆+电量低+报警");
            }
        }
        int[] imageIds= new int[]{
                R.drawable.electric_socket_close,
                R.drawable.electric_type_swift1, R.drawable.electric_type_swift2,
                R.drawable.electric_type_swift3, R.drawable.electric_type_swift4,
                R.drawable.electric_type_lock, R.drawable.electric_type_curtain,
                R.drawable.electric_type_window, R.drawable.camera,
                R.drawable.electric_type_aircondition, R.drawable.electric_type_scene,
                R.drawable.electric_type_arm_close1, R.drawable.electric_type_tv,
                R.drawable.electric_type_temp, R.drawable.electric_type_water,
                R.drawable.electric_type_door, R.drawable.electric_type_gas,
                R.drawable.electric_type_wall_ir, R.drawable.electric_type_horn,
                R.drawable.electric_type_smoke, R.drawable.electric_type_clothes,
                R.drawable.electric_type_aircondition, R.drawable.center_air,
                R.drawable.electric_type_newdoor, R.drawable.electric_type_tv,};

        for(int i=0;i<AlarmTime.size();i++){
            Map<String,Object> listItem = new HashMap<String,Object>();
            int j= (int) electricType.get(i);
            listItem.put("warn_info_item_img",imageIds[j]);
            listItem.put("warnelectricname",WarnelectricName.get(i));
            listItem.put("warnroomName",WarnroomName.get(i));
            listItem.put("stateInfo",StateInfo.get(i));
            listItem.put("alarmTime",AlarmTime.get(i));
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItems,R.layout.warninfo_item,
                new String[]{"warn_info_item_img","warnelectricname","warnroomName","stateInfo","alarmTime"},
                new int[]{R.id.warn_info_item_img,R.id.warnelectricName,R.id.wanroomName,R.id.stateinfo,R.id.alarmTime});

        ListView list = (ListView) findViewById(R.id.warn_info_list);
        list.setAdapter(simpleAdapter);
    }

}
