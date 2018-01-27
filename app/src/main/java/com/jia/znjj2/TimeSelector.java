package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.SceneData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212 on 2018/1/24.
 */

public class TimeSelector extends Activity {
    private DataControl mDC;
    private SceneData.SceneDataInfo sceneDataInfo;
    public TextView dataTime;
    public Button dataSelector;
    public Button weekSelector;
    public Button deleteTimeSelector;
    public ListView WeekTime1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x2201:
                    Toast.makeText(TimeSelector.this, "定时删除成功", Toast.LENGTH_SHORT).show();
                    dataTime.setText(null);
                    WeekTime1.setAdapter(null);
                    deleteTimeSelector.setVisibility(View.GONE);
                    break;
                case 0x2202:
                    Toast.makeText(TimeSelector.this, "定时删除失败，请检查主机联网", Toast.LENGTH_SHORT).show();
                    break;
                case 0x2203:
                    Toast.makeText(TimeSelector.this, "定时删除失败 ", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeselector);
        mDC = DataControl.getInstance();
        sceneDataInfo = mDC.mSceneList.get(SceneInfo.sceneNumber);
        initView();
        dataTimeshow();
        updateWeekTime();
    }

    protected void onResume() {
        super.onResume();
        dataTimeshow();
        updateWeekTime();
        if(sceneDataInfo.getDetailTiming() != null || sceneDataInfo.getWeeklyDays() != null){
            deleteTimeSelector.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        dataTime = (TextView) findViewById(R.id.OnceTime);
        dataSelector = (Button) findViewById(R.id.once_time_selector);
        weekSelector = (Button) findViewById(R.id.many_times_selector);
        WeekTime1 = (ListView) findViewById(R.id.move_room_list123);
        deleteTimeSelector = (Button)findViewById(R.id.delete_time_selector);
        if(sceneDataInfo.getDetailTiming()==null&&sceneDataInfo.getWeeklyDays()==null){
            deleteTimeSelector.setVisibility(View.GONE);
        }
    }

    private void dataTimeshow() {
            String abc=sceneDataInfo.getDetailTiming();
            dataTime.setText(abc);
    }

    public void timeSelectorBack(View view) {
        finish();
    }

    public void dateSelector(View view) {
        Intent intent = new Intent(TimeSelector.this, DataTimeSelector.class);
        startActivity(intent);
    }

    public void weekSelector(View view) {
        Intent intent = new Intent(TimeSelector.this, WeekTimeSelector.class);
        startActivity(intent);
    }

    public void updateWeekTime() {
        String def1 = sceneDataInfo.getDaliyTiming();
        if(def1!= null){
            List<Map<String, Object>> weeklistItems = new ArrayList<Map<String, Object>>();
            String WeeklyDays = sceneDataInfo.getWeeklyDays().substring(1, sceneDataInfo.getWeeklyDays().length() - 1);
            String[] abc = WeeklyDays.split(",");
            ArrayList list7 = new ArrayList();
            for (int i = 0; i < abc.length; i++) {
                if (abc[i].equals("1")) {
                    list7.add("星期一");
                } else if (abc[i].equals("2")) {
                    list7.add("星期二");
                } else if (abc[i].equals("3")) {
                    list7.add("星期三");
                } else if (abc[i].equals("4")) {
                    list7.add("星期四");
                } else if (abc[i].equals("5")) {
                    list7.add("星期五");
                } else if (abc[i].equals("6")) {
                    list7.add("星期六");
                } else if (abc[i].equals("7")) {
                    list7.add("星期日");
                }
            }
            for (int j = 0; j < list7.size(); j++) {
                Map<String, Object> weekListItem = new HashMap<String, Object>();
                weekListItem.put("week_list_item_name", list7.get(j));
                weekListItem.put("time_list_item_name", sceneDataInfo.getDaliyTiming());
                weeklistItems.add(weekListItem);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, weeklistItems, R.layout.week_time_list,
                    new String[]{"week_list_item_name", "time_list_item_name"},
                    new int[]{R.id.week_list_item_name1, R.id.time_list_item_name1});
            WeekTime1.setAdapter(simpleAdapter);
        }else{

            WeekTime1.setAdapter(null);
        }

    }
    public void deleteSelector(View view){
        new Thread(){
            Message msg = new Message();
            public void run(){
            String abc = mDC.mWS.deleteSceneTiming(SceneInfo.tmMastercode, SceneInfo.tmsceneindex);
                if (abc.equals("1")){
                    mDC.mSceneList.get(SceneInfo.sceneNumber).setWeeklyDays(null);
                    mDC.mSceneList.get(SceneInfo.sceneNumber).setDaliyTiming(null);
                    mDC.mSceneList.get(SceneInfo.sceneNumber).setDetailTiming(null);
                    mDC.mSceneData.updateSceneWeeklyDays(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, null);
                    mDC.mSceneData.updateSceneWeeklyTime(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, null);
                    mDC.mSceneData.updateSceneDetailTime(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, null);
                    msg.what = 0x2201;
                    handler.sendMessage(msg);
                }else if(abc.equals("0")){
                    msg.what = 0x2202;
                    handler.sendMessage(msg);
                }else{
                    msg.what = 0x2203;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

}







