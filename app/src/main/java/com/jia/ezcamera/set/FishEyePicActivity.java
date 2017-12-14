package com.jia.ezcamera.set;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jia.ezcamera.play.StaticConstant;
import com.jia.ezcamera.utils.SrceensUtils;
import com.jia.znjj2.R;

import java.io.File;

import vv.ppview.PpviewClientInterface;
import vv.playlib.FishEyeInfo;
import vv.playlib.FishEyePicEx;

/**
 * Created by Administrator on 2016/7/11.
 */

public class FishEyePicActivity extends Activity{
    //-----play
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    private Context mContext = this;
    private Activity mActivity = this;
    private GLSurfaceView sfv = null;

    //-----srceens
    private DisplayMetrics dm = new DisplayMetrics();
    private float mDensity;// �ܶ�
    private int mWhith = 0;
    private int mHeight = 0;

    private String myFilePath="";
    private FishEyeInfo fishEyeInfo;

    private RelativeLayout mRelativeSfv;
    private RelativeLayout topLayout;
    private View mViewPlay;

    private FishEyePicEx fishEyePicEx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_local);
        Intent intent = getIntent();
        if (intent != null) {
            fishEyeInfo = new FishEyeInfo();
            myFilePath=intent.getStringExtra(StaticConstant.FILE_PATH);
            String filenames[] = myFilePath.split("/");
            String filename = filenames[filenames.length-1];
            String fishInfos[] = filename.split("_");
            if(fishInfos.length<7){
                //�ϱ�����������
                fishEyeInfo.fishType = 0;
            }else{
                fishEyeInfo.fishType = Integer.parseInt(fishInfos[0]);
                fishEyeInfo.main_x1_offsize = Float.parseFloat(fishInfos[1]);
                fishEyeInfo.main_x2_offsize = Float.parseFloat(fishInfos[2]);
                fishEyeInfo.main_y1_offsize = Float.parseFloat(fishInfos[3]);
                fishEyeInfo.main_y2_offsize = Float.parseFloat(fishInfos[4]);
            }
        }
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int viewFlag = -1;
        try {
            viewFlag = Settings.System.getInt(this.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (viewFlag == 0) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean ifLand = false;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ifLand = true;
            setSrceenHorizontal();
        } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ifLand = false;
            setSrceenVertical();
        }
    }


    private void setSrceenVertical(){
        initDm();
        SrceensUtils.ChangeSfvWidthAndHeight_Vertical(mRelativeSfv, 4, 4, mWhith, mHeight);
        SrceensUtils.ChangeSfvWidthAndHeight_Vertical(sfv, 4, 4, mWhith, mHeight);
        topLayout.setVisibility(View.VISIBLE);
    }

    private void setSrceenHorizontal(){
        initDm();
        SrceensUtils.setHorizontal(mRelativeSfv,mWhith,mHeight);
        if(fishEyeInfo.fishType==1) {
            SrceensUtils.setHorizontal(sfv, mWhith, mHeight);
        }else{
            SrceensUtils.setHorizontal(sfv, mWhith, mWhith);
        }
        topLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(fishEyeInfo.fishType==0){
            File file = new File(myFilePath);
            Intent it = new Intent(Intent.ACTION_VIEW);
            Uri mUri = Uri.parse("file://" + file.getPath());
            it.setDataAndType(mUri, "image/*");
            startActivity(it);
            finish();
        }else {
            setSrceenVertical();
            fishEyePicEx.startPreviewPic();
        }
    }

    private void init(){
        initDm();
        TextView titleText = (TextView)findViewById(R.id.head_title);
        titleText.setText(R.string.fisheye_pic_preview);
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.linearLayout_info).setVisibility(View.GONE);
        findViewById(R.id.progressbar).setVisibility(View.GONE);
        findViewById(R.id.relative_bottom).setVisibility(View.GONE);

        mRelativeSfv = (RelativeLayout)findViewById(R.id.relative_sfv);
        topLayout = (RelativeLayout)findViewById(R.id.include_top);
        mViewPlay = findViewById(R.id.view_play);

        SrceensUtils.setVertical_2_1(mRelativeSfv, mWhith);
        sfv = new GLSurfaceView(mContext);
        RelativeLayout.LayoutParams sfv_lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sfv_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRelativeSfv.addView(sfv, sfv_lp);

        fishEyePicEx = new FishEyePicEx(mActivity,fishEyeInfo,myFilePath);
        fishEyePicEx.setSurfaceview(sfv);
    }




    private void initDm(){
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mWhith = dm.widthPixels;
        mHeight = dm.heightPixels;
    }
}
