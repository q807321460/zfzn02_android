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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.widget.SlipButton;

public class SwiftScene extends ElectricBase {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ImageView ivSceneImg;
    private EditText etElectricName;
    private TextView tvRoomName;
    private TextView tvSceneName;
    private LinearLayout llSelectScene;
    private Spinner spSelectScene;
    private SlipButton ibSwift;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1140:
                    Toast.makeText(SwiftScene.this,"更改失败，检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1141:
                    Toast.makeText(SwiftScene.this,"更改失败，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1142:
                    Toast.makeText(SwiftScene.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swift_scene);
        init();
        initView();
        updateSpinner();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }


    private void initView(){
        tvTitleName = (TextView) findViewById(R.id.swift_scene_title_name);
        tvTitleEdit = (TextView) findViewById(R.id.swift_scene_title_edit);
        tvTitleSave = (TextView) findViewById(R.id.swift_scene_title_save);
        ivSceneImg = (ImageView) findViewById(R.id.swift_scene_img);
        etElectricName = (EditText) findViewById(R.id.swift_scene_name);
        tvRoomName = (TextView) findViewById(R.id.swift_scene_room);
        tvSceneName = (TextView) findViewById(R.id.swift_scene_scene_name);
        llSelectScene = (LinearLayout) findViewById(R.id.swift_scene_ll);
        spSelectScene = (Spinner) findViewById(R.id.swift_scene_select_scene);
        ibSwift = (SlipButton) findViewById(R.id.swift_scene_swift);

        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvSceneName.setVisibility(View.VISIBLE);
        llSelectScene.setVisibility(View.GONE);

        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        tvRoomName.setText(mDC.mAreaList.get(roomSequ).getRoomName());

        if(-1 != electric.getSceneIndex()){
            for(int i = 0; i<mDC.mSceneList.size();i++){
                if(mDC.mSceneList.get(i).getSceneIndex() == electric.getSceneIndex()){
                    tvSceneName.setText("关联情景："+mDC.mSceneList.get(i).getSceneName());
                }
            }
        }


        if (electric.getElectricType() == 13){
            ivSceneImg.setImageResource(R.drawable.electric_type_swift4_left1);
        }else if(electric.getOrderInfo().equals("02")){
            ivSceneImg.setImageResource(R.drawable.electric_type_swift4_left1);
        }else if(electric.getOrderInfo().equals("03")){
            ivSceneImg.setImageResource(R.drawable.electric_type_swift4_left1);
        }else if(electric.getOrderInfo().equals("04")){
            ivSceneImg.setImageResource(R.drawable.electric_type_swift4_left1);        }




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
                SwiftScene.this.getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
                int i = loacalDisplayMetrics.heightPixels;
                LinearLayout linearLayout = new LinearLayout(SwiftScene.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);
                ImageView imageView = new ImageView(SwiftScene.this);
                imageView.setBackgroundResource(mDC.mSceneTypeImages.getResourceId(mDC.mSceneList.get(paramAnonymousInt).getSceneImg(), 0));
                imageView.setLayoutParams(new ViewGroup.LayoutParams(i / 9, i / 9));
                linearLayout.addView(imageView);
                TextView textView = new TextView(SwiftScene.this);
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
            spSelectScene.setSelection(electric.getSceneIndex());
        }

    }

    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvSceneName.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);
        llSelectScene.setVisibility(View.GONE);
        ibSwift.setVisibility(View.VISIBLE);

        initView();
    }

    public void swiftSceneBack(View view){
        finish();
    }
    public void swiftSceneEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        tvSceneName.setVisibility(View.GONE);
        llSelectScene.setVisibility(View.VISIBLE);
        ibSwift.setVisibility(View.GONE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void swiftSceneSave(View view){
        final String electricName = etElectricName.getText().toString();
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.updateElectric(mDC.sMasterCode,electric.getElectricCode()
                        ,electric.getElectricIndex(),electricName, spSelectScene.getSelectedItemPosition());
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1140;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1141;
                }else if(result.startsWith("1")){
                    msg.what = 0x1142;

                    electric.setElectricName(electricName);
                    electric.setSceneIndex(spSelectScene.getSelectedItemPosition());
                    updateSceneElectricName(electric.getElectricIndex(),electricName);
                    mDC.mSceneElectricData.updateSceneElectric(electric.getElectricIndex(), electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex()
                            ,electricName, spSelectScene.getSelectedItemPosition());
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public void updateUI() {

    }
}
