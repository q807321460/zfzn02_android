package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.SceneData;
import com.jia.widget.CustomDataSelector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 212 on 2018/1/24.
 */

public class DataTimeSelector extends Activity implements View.OnClickListener {
    private DataControl mDC;
    private SceneData.SceneDataInfo sceneDataInfo;
    private RelativeLayout selectTime;
    private TextView appointTime,currentTime;
    private CustomDataSelector customDataSelector;
    public static String abc;
    public String dataSelctor;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x2201:
                Toast.makeText(DataTimeSelector.this, "单次定时，时间保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x2202:
                    Toast.makeText(DataTimeSelector.this, "单次定时失败，请检查网络和主机", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDC = DataControl.getInstance();
        sceneDataInfo = mDC.mSceneList.get(SceneInfo.sceneNumber);
        setContentView(R.layout.activity_date_selector);
        selectTime = (RelativeLayout) findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        currentTime = (TextView) findViewById(R.id.currentTime);
        appointTime = (TextView) findViewById(R.id.currentDate);
        initDatePicker();

    }
    protected void onResume(){
        super.onResume();
        appointTime.setText(abc);

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectTime:
                // 日期格式为yyyy-MM-dd HH:mm
                customDataSelector.show(currentTime.getText().toString());
                break;
        }
    }

    private void initDatePicker() {
        currentTime.setText("2018-01-01 00:00");
        SimpleDateFormat sdf = new SimpleDateFormat("2045-12-31 00:00", Locale.CHINA);
        String now = sdf.format(new Date());
        customDataSelector = new CustomDataSelector(this, new CustomDataSelector.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentTime.setText(time);
                abc = time+":00";
                appointTime.setText(abc);
            }
        }, "2018-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDataSelector.showSpecificTime(true); // 显示时和分
        customDataSelector.setIsLoop(false); // 允许循环滚动
    }
    public void dataSelectorSave(View view){
        dataSelctor = abc;
        new Thread() {
            public void run() {
                Message msg = new Message();
                mDC.mWS.deleteSceneTiming(SceneInfo.tmMastercode, SceneInfo.tmsceneindex);
                String wsanswer = mDC.mWS.updateSceneDetailTiming(SceneInfo.tmMastercode, SceneInfo.tmsceneindex,dataSelctor);
                if (wsanswer.equals("1")) {
                    mDC.mSceneData.updateSceneDetailTime(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, dataSelctor);
                    sceneDataInfo.setDetailTiming(dataSelctor);
                    mDC.mSceneList.get(SceneInfo.sceneNumber).setWeeklyDays(null);
                    mDC.mSceneList.get(SceneInfo.sceneNumber).setDaliyTiming(null);
                    mDC.mSceneData.updateSceneWeeklyDays(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, null);
                    mDC.mSceneData.updateSceneWeeklyTime(SceneInfo.tmMastercode, SceneInfo.tmsceneindex, null);
                    mDC.mSceneData.loadSceneList();
                    msg.what = 0x2201;
                    handler.sendMessage(msg);
                } else {
                    msg.what = 0x2202;
                    handler.sendMessage(msg);
                }

            }
        }.start();
    }
    public void dataSelectorBack(View view){
        finish();
    }
}
