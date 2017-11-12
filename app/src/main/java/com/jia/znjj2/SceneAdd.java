package com.jia.znjj2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;

/**
 * Created by Administrator on 2016/10/23.
 */
public class SceneAdd extends Activity {
    private DataControl mDC;
    private EditText etSceneName;
    private Spinner spSceneImg;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1150:
                    Toast.makeText(SceneAdd.this,"添加失败，检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1151:
                    Toast.makeText(SceneAdd.this,"添加失败，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1152:
                    Toast.makeText(SceneAdd.this,"添加成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_add);
        initView();
        initSpinner();
    }

    private void initView(){
        mDC = DataControl.getInstance();
        etSceneName = (EditText) findViewById(R.id.scene_add_scene_name);
        spSceneImg = (Spinner) findViewById(R.id.scene_add_scene_image);
    }

    private void initSpinner(){
        BaseAdapter local1 = new BaseAdapter()
        {
            public int getCount()
            {
                return mDC.mSceneTypeImages.length();
            }

            public Object getItem(int paramAnonymousInt)
            {
                return null;
            }

            public long getItemId(int paramAnonymousInt)
            {
                return 0L;
            }

            @SuppressLint({"NewApi", "ResourceAsColor"})
            public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
            {
                LinearLayout localLayout = new LinearLayout(SceneAdd.this);
                localLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView localImageView = new ImageView(SceneAdd.this);
                localImageView.setBackgroundResource(mDC.mSceneTypeImages.getResourceId(paramAnonymousInt, 0));
                localImageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
                localLayout.addView(localImageView);
                TextView localTextView = new TextView(SceneAdd.this);
                localTextView.setText(String.format("图片"+paramAnonymousInt));
                localTextView.setTextColor(2131230726);
                localTextView.setPadding(0, 35, 0, 0);
                localTextView.setTextSize(16.0F);
                localLayout.addView(localTextView);
                return localLayout;
            }
        };
        this.spSceneImg.setAdapter(local1);
        this.spSceneImg.setSelection(0);
    }

    public void sceneAddBack(View view) {
        finish();
    }

    public void sceneAddSure(View view){
        //由于主控存储的限制，最多只能添加10中情景模式
        if(mDC.mSceneList.size() >= 20){
            Toast.makeText(SceneAdd.this,"最多只能添加20中情景模式",Toast.LENGTH_LONG).show();
            return;
        }
        final String sceneName = etSceneName.getText().toString();

        if (sceneName == null || sceneName.equals("")) {
            Toast.makeText(SceneAdd.this,"情景名字不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        //将区域数据添加到本地SQLite数据库中
        //如果该用户已经有一个相同的情景模式名字，不应该添加
        if(mDC.mDB.sceneQueryByRoomName(sceneName,mDC.sMasterCode).getCount() != 0){
            Toast.makeText(SceneAdd.this,"情景名字不能重复",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                int sceneImg = spSceneImg.getSelectedItemPosition();
                int sceneIndex = mDC.mSceneData.getAddSceneIndex();
                int sceneSequ = mDC.mSceneData.getAddSceneSequ();
                String result = mDC.mWS.addScene(mDC.sAccountCode,mDC.sMasterCode,sceneName,sceneIndex,sceneSequ,sceneImg);
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1150;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1151;
                }else if(result.startsWith("1")){
                    msg.what = 0x1152;
                    mDC.mSceneData.addScene(sceneName, sceneImg, sceneIndex, sceneSequ);
                    mDC.mSceneData.loadSceneList();
                }
                handler.sendMessage(msg);

            }
        }.start();

    }
}
