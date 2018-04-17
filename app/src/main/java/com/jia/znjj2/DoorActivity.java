package com.jia.znjj2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DoorActivity extends ElectricBase {

    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ImageView ivSceneImg;
    private EditText etElectricName;
    private TextView tvRoomName;
    private TextView tvSceneName;
    private TextView tvOpenScene;
    private TextView tvCloseScene;
    private ImageView ivOpenScene;
    private ImageView ivCloseScene;


    private LinearLayout llSelectScene;
    private LinearLayout llDoorAction;
    private LinearLayout llDoorShow;
    private Spinner spSelectScene;
    private Switch door_switch;
    private String electricOrder="SH";
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1140:
                    Toast.makeText(DoorActivity.this, "更改失败，检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1141:
                    Toast.makeText(DoorActivity.this,"更改失败，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1142:
                    Toast.makeText(DoorActivity.this,"更改成功",Toast.LENGTH_LONG).show();

                    changeToNormal();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);
        init();
        initView();
        updateSpinner();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //initView();
        //updateBindSence();
    }


    private void initView(){
        tvTitleName = (TextView) findViewById(R.id.sensor_door_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.sensor_door_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.sensor_door_title_save);
        ivSceneImg = (ImageView) findViewById(R.id.sensor_door_img);
        etElectricName = (EditText) findViewById(R.id.sensor_door_name);
        tvRoomName = (TextView) findViewById(R.id.sensor_door_room);
        tvSceneName = (TextView) findViewById(R.id.sensor_door_scene_name);
        tvOpenScene= (TextView) findViewById(R.id.open_scene);
        tvCloseScene= (TextView) findViewById(R.id.close_scene);
        ivOpenScene= (ImageView) findViewById(R.id.open_img);
        ivCloseScene= (ImageView) findViewById(R.id.close_img);
        llSelectScene = (LinearLayout) findViewById(R.id.sensor_door_ll);
        spSelectScene = (Spinner) findViewById(R.id.sensor_door_select_scene);
        llDoorAction= (LinearLayout) findViewById(R.id.sensor_door_action);
        llDoorShow= (LinearLayout) findViewById(R.id.ll_door_show);
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvSceneName.setVisibility(View.VISIBLE);
        llSelectScene.setVisibility(View.GONE);
        llDoorAction.setVisibility(View.GONE);
        llDoorShow.setVisibility(View.VISIBLE);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());
        door_switch= (Switch) findViewById(R.id.open);
        door_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    electricOrder = "SH";
                } else {
                    electricOrder = "SG";
                }

            }
        });

        updateBindSence();







    }

    private void updateBindSence(){
        JSONObject json= null;
        try {
            json = new JSONObject(electric.getExtras());
            if(json==null){
                tvOpenScene.setText(" ");
                ivOpenScene.setImageResource(0);
                tvCloseScene.setText(" ");
                ivCloseScene.setImageResource(0);
            }else {
                if(json.get("SH")==null){
                    tvOpenScene.setText(" ");
                    ivOpenScene.setImageResource(0);
                }else {
                    if(-1 != electric.getSceneIndex()){
                        for(int i = 0; i<mDC.mSceneList.size();i++){
                            int number  = mDC.mSceneList.get(i).getSceneIndex();
                            System.out.print(number);
                            if(Integer.parseInt((String) json.get("SH")) == number){
                                    tvOpenScene.setText(mDC.mSceneList.get(i).getSceneName());
                                    ivOpenScene.setImageResource(mDC.mSceneList.get(i).getSceneImg());
                            }
                        }
                    }

                }

                if(json.get("SG")==null){
                    tvCloseScene.setText(" ");
                    ivCloseScene.setImageResource(0);
                }else {
                    if(-1 != electric.getSceneIndex()){
                        for(int i = 0; i<mDC.mSceneList.size();i++){
                            int number  = mDC.mSceneList.get(i).getSceneIndex();
                            System.out.print(number);
                            if(Integer.parseInt((String) json.get("SG"))==number){
                                tvCloseScene.setText(mDC.mSceneList.get(i).getSceneName());
                                ivCloseScene.setImageResource(mDC.mSceneList.get(i).getSceneImg());
                            }
                        }
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateSpinner()
    {
        BaseAdapter localAdapter = new BaseAdapter()
        {
            public int getCount()
            {
                return mDC.mSceneList.size();
            }

            public Object getItem(int paramAnonymousInt)
            {
                return null;
            }

            public long getItemId(int paramAnonymousInt)
            {
                return 0L;
            }

            public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
            {
                DisplayMetrics loacalDisplayMetrics = new DisplayMetrics();
                DoorActivity.this.getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
                int i = loacalDisplayMetrics.heightPixels;
                LinearLayout linearLayout = new LinearLayout(DoorActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);
                ImageView imageView = new ImageView(DoorActivity.this);
                imageView.setBackgroundResource(mDC.mSceneTypeImages.getResourceId(mDC.mSceneList.get(paramAnonymousInt).getSceneImg(), 0));
                imageView.setLayoutParams(new ViewGroup.LayoutParams(i / 9, i / 9));
                linearLayout.addView(imageView);
                TextView textView = new TextView(DoorActivity.this);
                textView.setText(mDC.mSceneList.get(paramAnonymousInt).getSceneName());
                textView.setTextColor(Color.BLACK);
                if (i <= 800)
                {
                    textView.setPadding(35, 25, 0, 0);
                    textView.setTextSize(18.0F);
                }
                else
                {

                    textView.setPadding(35, 55, 0, 0);
                    textView.setTextSize(22.0F);
                }
                linearLayout.addView(textView);
                return linearLayout;
            }
        };
        spSelectScene.setAdapter(localAdapter);
        if(-1 != electric.getSceneIndex()){
            spSelectScene.setSelection(0);
        }

    }

    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvSceneName.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);
        llSelectScene.setVisibility(View.GONE);
        llDoorAction.setVisibility(View.GONE);
        llDoorShow.setVisibility(View.VISIBLE);
        updateBindSence();

        //initView();
    }

    public void sensorDoorBack(View view){
        finish();
    }
    public void sensorDoorEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        tvSceneName.setVisibility(View.GONE);
        llSelectScene.setVisibility(View.VISIBLE);
        llDoorAction.setVisibility(View.VISIBLE);
        llDoorShow.setVisibility(View.GONE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void sensorDoorSave(View view){
        final String electricName = etElectricName.getText().toString();
        new Thread(){
            @Override
            public void run() {

                String result=mDC.mWS.updateElectric1(mDC.sMasterCode, electric.getElectricCode(),
                        electric.getElectricIndex(), electricName, mDC.mSceneList.get(spSelectScene.getSelectedItemPosition()).getSceneIndex(), electricOrder);
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1140;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1141;
                }else if(result.startsWith("1")){
                    msg.what = 0x1142;

                    electric.setElectricName(electricName);
                    electric.setSceneIndex(mDC.mSceneList.get(spSelectScene.getSelectedItemPosition()).getSceneIndex());
                    Map<String,String> value = JSON.parseObject(electric.getExtras(),Map.class);
                    value.put(electricOrder, String.valueOf(mDC.mSceneList.get(spSelectScene.getSelectedItemPosition()).getSceneIndex()));//改变zzmm的值
                    electric.setExtras(JSON.toJSONString(value));//重新转成json字符串，｛“name”:"张三","age":"20","xb":“男”,"zzmm":"newValue"｝

//                    Map<String,String> extras = JSON.parseObject(electric.getExtras(), Map.class);
//                    if("SH".equals(electricOrder)){
//                        extras.put("SH", String.valueOf(mDC.mSceneList.get(spSelectScene.getSelectedItemPosition()).getSceneIndex()));//改变zzmm的值
//
//                    }else if("SG".equals(electricOrder)){
//                        extras.put("SG", String.valueOf(mDC.mSceneList.get(spSelectScene.getSelectedItemPosition()).getSceneIndex()));//改变zzmm的值
//
//
//
//                    }
//                    electric.setExtra(JSON.toJSONString(extras));
                    updateSceneElectricName(electric.getElectricIndex(),electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex()
                            , electricName, mDC.mSceneList.get(spSelectScene.getSelectedItemPosition()).getSceneIndex());
                    mDC.mSceneElectricData.updateSceneElectric(electric.getElectricIndex(), electricName);
                }
                handler.sendMessage(msg);
            }
        }.start();

    }

    @Override
    public void updateUI() {
      //  updateBindSence();
    }
}
