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
public class AreaAdd extends Activity {
    private EditText etAreaName;
    private Spinner spAreaImage;
    private DataControl mDC;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x1018:
                    Toast.makeText(getApplicationContext(), "添加区域成功", Toast.LENGTH_SHORT).show();
                    mDC.mAreaData.loadAreaList();
                    finish();
                    break;
                case  0x1019:
                    Toast.makeText(getApplicationContext(), "本地添加房间失败", Toast.LENGTH_SHORT).show();
                    break;
                case  0x1020:
                    Toast.makeText(getApplicationContext(), "已存在房间", Toast.LENGTH_SHORT).show();
                    break;
                case  0x1021:
                    Toast.makeText(getApplicationContext(), "远程添加房间失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_add);
        init();
        updateSpinner();
    }
    private void init(){
        mDC = DataControl.getInstance();
        etAreaName = (EditText) findViewById(R.id.area_add_area_name);
        spAreaImage = (Spinner) findViewById(R.id.area_add_area_image);
    }

    private void updateSpinner()
    {
        BaseAdapter local1 = new BaseAdapter()
        {
            public int getCount()
            {
                return mDC.mAreaTypeImages.length();
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
                LinearLayout localLayout = new LinearLayout(AreaAdd.this);
                localLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView localImageView = new ImageView(AreaAdd.this);
                localImageView.setBackgroundResource(mDC.mAreaTypeImages.getResourceId(paramAnonymousInt, 0));
                localImageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
                localLayout.addView(localImageView);
                TextView localTextView = new TextView(AreaAdd.this);
                localTextView.setText(String.format("图片"+paramAnonymousInt));
                localTextView.setTextColor(2131230726);
                localTextView.setPadding(0, 35, 0, 0);
                localTextView.setTextSize(16.0F);
                localLayout.addView(localTextView);
                return localLayout;
            }
        };
        this.spAreaImage.setAdapter(local1);
        this.spAreaImage.setSelection(0);
    }

    public void areaAddBack(View view){
        finish();
    }

    public void areaAddSure(View view){
        if (this.etAreaName.getText().length() == 0)
        {
            Toast.makeText(getApplicationContext(),"区域名字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final String roomName = etAreaName.getText().toString();
        final int roomImg = spAreaImage.getSelectedItemPosition();
        new Thread(){
            @Override
            public void run() {
                int i= mDC.mAreaData.addArea(roomName, roomImg);
                Message message = new Message();
                if(i == 2){
                    message.what=0x1020;
                }else if (i == 1) {
                    message.what=0x1018;
                } else if (i == -1) {
                    message.what=0x1019;
                } else if( i== -2){
                    message.what = 0x1021;
                }
                handler.sendMessage(message);
            }
        }.start();
    }
}
