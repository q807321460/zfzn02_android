package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.SceneData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212 on 2018/1/24.
 */

public class WeekTimeSelector extends Activity implements View.OnClickListener {
    private DataControl mDC;
    private SceneData.SceneDataInfo sceneDataInfo;
    public TextView selctTime;
    private List<CheckBox> checkBoxList=new ArrayList<CheckBox>();
    private LinearLayout wk_checkBoxList;
    private Button btn_sure;
    public  String appointWeekTime;
    public  String selectweek;
    public String weeks;
    public String localweekdays;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x2201:
                    Toast.makeText(WeekTimeSelector.this, "循环定时时间保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x2202:
                    Toast.makeText(WeekTimeSelector.this, "循环定时失败，请检查主机联网", Toast.LENGTH_SHORT).show();
                    break;
                case 0x2203:
                    Toast.makeText(WeekTimeSelector.this, "循环定时失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x2204:
                    Toast.makeText(WeekTimeSelector.this, "请设定时间", Toast.LENGTH_SHORT).show();
                    break;
                case 0x2205:
                    Toast.makeText(WeekTimeSelector.this, "请设定工作日", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_selector);
        initView();
        weekcheck();
        refreshWeektime();
    }

    private void weekcheck() {
        String[] strArr={"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
        for(String str:strArr){
            CheckBox checkBox=(CheckBox) View.inflate(this, R.layout.week_checkbox, null);
            checkBox.setText(str);
            wk_checkBoxList.addView(checkBox);
            checkBoxList.add(checkBox);
        }
        btn_sure.setOnClickListener(this);
    }

    protected void onResume(){
        super.onResume();
        refreshWeektime();
    }
    public void initView(){
        mDC = DataControl.getInstance();
        sceneDataInfo = mDC.mSceneList.get(SceneInfo.sceneNumber);
        selctTime = (TextView)findViewById(R.id.week8);
        wk_checkBoxList =(LinearLayout) findViewById(R.id.wk_CheckBoxList);
        btn_sure =(Button)findViewById(R.id.btn_ok);
   }
   public void refreshWeektime(){
       appointWeekTime= WeekTime.wkTime;
       selctTime.setText(appointWeekTime);

   }
    public void weekSelectorBack(View view){
        finish();
    }


    public void onClick(View v) {
        new Thread() {
            public void run() {
                Message msg = new Message();
                if (appointWeekTime != null) {
                    String str = "";
                    for (CheckBox checkBox : checkBoxList) {
                        if (checkBox.isChecked()) {
                            str += checkBox.getText().toString() + "，";
                        }
                    }
                    if(str==""){
                        msg.what =0x2205;
                        handler.sendMessage(msg);
                    }else if(str != ""){
                    selectweek = str + "时间保存成功";
                    String[] week = selectweek.split("，");
                    ArrayList listWeek = new ArrayList();
                    for (int i = 0; i < week.length-1; i++) {
                        listWeek.add(week[i]);}
                    ArrayList list123 = new ArrayList();
                    for(int i=0;i<listWeek.size();i++){
                        if(listWeek.get(i).equals("星期一")){
                            list123.add(1);
                        }else if(listWeek.get(i).equals("星期二")){
                            list123.add(2);
                        }else if(listWeek.get(i).equals("星期三")){
                            list123.add(3);
                        }else if(listWeek.get(i).equals("星期四")){
                            list123.add(4);
                        }else if(listWeek.get(i).equals("星期五")){
                            list123.add(5);
                        }else if(listWeek.get(i).equals("星期六")){
                            list123.add(6);
                        }else if(listWeek.get(i).equals("星期日")){
                            list123.add(7);
                        }
                    }
                    weeks="[";
                    for(int i=0;i<list123.size()-1;i++){
                        weeks+=list123.get(i)+",";
                    }
                    localweekdays=weeks+list123.get(list123.size()-1)+"]";
                    mDC.mWS.deleteSceneTiming(SceneInfo.tmMastercode, SceneInfo.tmsceneindex);
                    String flag = mDC.mWS.updateSceneDaliyTiming(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, localweekdays,appointWeekTime);
                    if(flag.equals("1")) {
                        mDC.mSceneList.get(SceneInfo.sceneNumber).setDaliyTiming(appointWeekTime);
                        mDC.mSceneData.updateSceneWeeklyTime(SceneInfo.tmMastercode, SceneInfo.tmsceneindex,appointWeekTime);
                        mDC.mSceneList.get(SceneInfo.sceneNumber).setWeeklyDays(localweekdays);
                        mDC.mSceneData.updateSceneWeeklyDays(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, localweekdays);
                        mDC.mSceneList.get(SceneInfo.sceneNumber).setDetailTiming(null);
                        mDC.mSceneData.updateSceneDetailTime(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, null);
                        msg.what = 0x2201;
                        handler.sendMessage(msg);
                    }else if(flag.equals("0")){
                        msg.what = 0x2202;
                        handler.sendMessage(msg);
                    }else{
                        msg.what = 0x2203;
                        handler.sendMessage(msg);
                        }
                    }
                } else {
                    msg.what =0x2204;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    public void select_Time(View view){
        Intent intent = new Intent(WeekTimeSelector.this,WeekTime.class);
        startActivity(intent);
    }
//     public String  weektoWS(String abc){
//        String[] week = abc.split(",");
//         ArrayList listWeek = new ArrayList();
//        for (int i = 0; i < week.length; i++) {
//            listWeek.add(week[i]);}
//        for(int i=0;i<listWeek.size()-1;i++){
//            if(listWeek.get(i)=="星期一"){
//              a[i]=1;
//            }else if(listWeek.get(i)=="星期二"){
//                a[i]=2;
//            }else if(listWeek.get(i)=="星期三"){
//                a[i]=3;
//            }else if(listWeek.get(i)=="星期四"){
//                a[i]=4;
//            }else if(listWeek.get(i)=="星期五"){
//                a[i]=5;
//            }else if(listWeek.get(i)=="星期六"){
//                a[i]=6;
//            }else if(listWeek.get(i)=="星期日"){
//                a[i]=7;
//            }
//        }
//        for(int i=0;i<a.length-1;i++){
//             weeks+=a[i]+",";
//        }
//        localweekdays="["+weeks+","+a[a.length]+"]";
//        return localweekdays;
//    }
}
