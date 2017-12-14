package com.jia.ezcamera.play;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.jia.ezcamera.utils.SrceensUtils;
import com.jia.ezcamera.utils.StringUtils;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


import vv.playlib.OnRecPlayerCallbackListen;
import vv.ppview.PpviewClientInterface;
import vv.playlib.FishEyeInfo;
import vv.playlib.OnPlayerCallbackListener;
import vv.playlib.RecPlayerEx;
import vv.playlib.render.display_point;

/**
 * Created by ls on 15/1/28.
 */
public class PlayLocalVideoActivity extends Activity {
    private static final String TAG = PlayLocalVideoActivity.class.getSimpleName();
    private static final int PLAYSTATUSCHANGED = 101;
    private static final int GETWIDTHANDHEIGHT = PLAYSTATUSCHANGED+1;
    private static final int ONAUDIOSSTATUSCHANGED = GETWIDTHANDHEIGHT+1;
    private static final int CATCH_PICTURE = ONAUDIOSSTATUSCHANGED+1;
    private static final int CATCH_PICTURE_FAIL = CATCH_PICTURE+1;
    private static final int SEEKBAR_MOVE=CATCH_PICTURE_FAIL+1;
    private static final int SECOND=60;
    private static final int TRANSLUCENT_155=155;
    private static final int TRANSLUCENT_255=255;

    RelativeLayout mRelativeSfv;
    View mViewPlay;
    ProgressBar pb;
    Button mBtnRight;
    TextView mText;
    ImageButton mPlayerImageButtonPlay;
    ImageButton mPlayerImageButtonSound;
    SeekBar mSeekbar;
    TextView textStart;
    TextView textEnd;

    List<View> menuList;

    RelativeLayout mRelativeBottom;


    //-----play
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    private Context myContext = this;
    private Activity mActivity = this;
    private GLSurfaceView sfv = null;
    //-----srceens
    private DisplayMetrics dm = new DisplayMetrics();
    private float mDensity;// 密度
    private int mWhith = 0;
    private int mHeight = 0;
    String phoneInfo = Build.VERSION.RELEASE;// 版本号
    int in = Integer.parseInt(phoneInfo.substring(0, 1));
    private int playAudioStatus = 0;
    //-----status
    private boolean ifPlaying = false;//是否正在播放
    private boolean ifPlay = false;//是否播放成功
    private boolean ifLand=false;//是否横屏
    private boolean ifPause=false;//是否暂停
    private boolean ifAudio=false;//是否有声音

    private RecPlayerEx rpe;
    private String myFilePath="";
    private int myWidth=0;
    private int myHeight=0;
    private int myAllSize=0;
    private FishEyeInfo fishEyeInfo;

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_return:
                    PlayLocalVideoActivity.this.finish();
                    break;
                case R.id.player_ImageButton_sound:
                    setAudioStatus(!ifAudio);
                    ifAudio = !ifAudio;
                    break;
                case R.id.player_ImageButton_play:
                    doPauseVideo();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化
     */
    private void init() {
        initDm();
        findViewById(R.id.btn_return).setOnClickListener(onClickListener);
        mRelativeSfv = (RelativeLayout)findViewById(R.id.relative_sfv);
        mViewPlay = findViewById(R.id.view_play);
        pb = (ProgressBar)findViewById(R.id.progressbar);
        mPlayerImageButtonPlay = (ImageButton)findViewById(R.id.player_ImageButton_play);
        mPlayerImageButtonSound = (ImageButton)findViewById(R.id.player_ImageButton_sound);
        mPlayerImageButtonSound.setOnClickListener(onClickListener);
        mPlayerImageButtonPlay.setOnClickListener(onClickListener);
        mSeekbar = (SeekBar)findViewById(R.id.seekbar);
        textStart = (TextView)findViewById(R.id.start_time);
        textEnd = (TextView)findViewById(R.id.end_time);

        menuList = new ArrayList<View>();
        menuList.add(findViewById(R.id.include_top));
        menuList.add(findViewById(R.id.include_bottom));

        mRelativeBottom = (RelativeLayout)findViewById(R.id.relative_bottom);
        findViewById(R.id.linearLayout_info).setVisibility(View.GONE);
        findViewById(R.id.player_ImageButton_camera).setVisibility(View.GONE);
        SrceensUtils.setVertical_2_1(mRelativeSfv, mWhith);
        sfv = new GLSurfaceView(myContext);
        RelativeLayout.LayoutParams sfv_lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sfv_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRelativeSfv.addView(sfv, sfv_lp);
        sfv.setOnClickListener(sfvClick);
        mSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_local);
        myContext=this;
        Intent intent = getIntent();
        if (intent != null) {
            fishEyeInfo = new FishEyeInfo();
            myFilePath=intent.getStringExtra(StaticConstant.FILE_PATH);
            String filenames[] = myFilePath.split("/");
            String filename = filenames[filenames.length-1];
            String fishInfos[] = filename.split("_");
            if(fishInfos.length<7){
                //老本本，无鱼眼
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
        initPlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 是否阻止休眠
         */
//        if (sp.isIfWakeLock()) {
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
//        // -----开启屏幕旋转
        int viewFlag = -1;
        try {
            viewFlag = Settings.System.getInt(this.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (viewFlag == 0) {
            Settings.System.putInt(myContext.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doStartPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doStopPlay();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initPlay() {
        if (in < 4) {
        } else {
            rpe=new RecPlayerEx(mActivity,recPlayerCallback,playerCallbackListener,fishEyeInfo);
            rpe.setSurfaceview(sfv);
            gestureDetector = new GestureDetector(this, new sfvGestrue());
            myScaleGesture = new ScaleGestureDetector(this, listener);
            StartZoom();
            sfv.setOnTouchListener(new sfvOntouch());
        }
    }

    private  static final String TIME_TYPE1="yyyyMMddHHmmss";
    private  static final String TIME_TYPE2="HH:mm:ss";
    private String getPlayTime(Long time,String type) {
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(type);
        simpleDateFormat2.setTimeZone(TimeZone.getDefault());
        String myTime = simpleDateFormat2.format(time * 1000);
        return myTime;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAYSTATUSCHANGED:
                    int mStatus = msg.arg1;
                    pb.setVisibility(View.GONE);
                    if (mStatus >= 0) {
                        mPlayerImageButtonPlay
                                .setImageResource(R.drawable.png_playback_stop);
                    }
//                    else {
//                        if (mStatus==-2) {
//                            mPlayerImageButtonPlay
//                                    .setImageResource(R.drawable.png_playback_play);
//                            doStopPlay();
//                        }else{
//                            ErrorToastUtils.StartPlayLocalVideoError(myContext,mStatus);
//                        }
//
//                    }
                    break;
                case GETWIDTHANDHEIGHT:
                    int mw = msg.arg1;
                    int mh = msg.arg2;
                    if (mw > 0 && mh > 0) {
                        myWidth = mw;
                        myHeight= mh;
                        if (ifLand) {
                            setSrceenHorizontal();
                        } else {
                            setSrceenVertical();
                        }
                    }
                    break;
                case ONAUDIOSSTATUSCHANGED:
                    doStartAudio(msg.arg1);
                    break;
                case CATCH_PICTURE:
//                    WaitingDialog.getInstance(mContext).dismissDialog();
//                    SaveOrDeletePic((String) msg.obj);
                    break;
                case CATCH_PICTURE_FAIL:
//                    WaitingDialog.getInstance(mContext).dismissDialog();
//                    cancleImportDialog();
//                    showMessage(mContext.getResources().getString(
//                            R.string.photot_fail));
                    break;
                case SEEKBAR_MOVE:
                    int moveResult=msg.arg1;
                    if (moveResult==-2){
                        doStopPlay();
                    }else {
                        if (moveResult>=0) {
                            moveSeekbar(msg.arg1);
                        }else{
                            //播放错误
                            ToastUtils.show(myContext, getString(R.string.video_local_play_faild));
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 开始播放
     */
    private void doStartPlay() {
        if (!TextUtils.isEmpty(myFilePath)) {
            ifPlaying = true;
            pb.setVisibility(View.VISIBLE);
            if (in < 4) {
            } else {
                myAllSize=rpe.startRecPlayer(myFilePath);
                setAudioStatus(false);
                Log.i(TAG,"-----start");
                if (myAllSize>=0) {
                    mSeekbar.setMax(myAllSize);
                    setEndText(myAllSize);
                }else{
                    //播放错误
                    ToastUtils.show(myContext, getString(R.string.video_local_play_faild));
                }
            }
        }
    }

    /**
     * 停止播放
     */
    private void doStopPlay() {
        doStopAudio();
        if (in < 4) {
        } else {
            if (ifPlaying) {
                rpe.stopRecplayer();
                Log.i(TAG,"-----stop");
                myAllSize=0;
                ifPlaying = false;
            }
        }
        pb.setVisibility(View.GONE);
    }

    private void moveSeekbar(int move){
        if (move>=0&&move<=myAllSize){
            mSeekbar.setProgress(move);
            setStartText(move);
        }
    }

    private void doPauseVideo(){
        if (ifPause){
            ifPause=false;
            mPlayerImageButtonPlay
                    .setImageResource(R.drawable.png_playback_stop);
        }else{
            ifPause=true;
            mPlayerImageButtonPlay
                    .setImageResource(R.drawable.png_playback_play);
        }
        rpe.setSuspend(ifPause);
    }

    private void setStartText(int startTime){
        textStart.setText(StringUtils.format(startTime/SECOND)+":"+StringUtils.format(startTime%SECOND));
    }

    private void setEndText(int endTime){
        textEnd.setText(StringUtils.format(endTime/SECOND)+":"+StringUtils.format(endTime%SECOND));
    }

    private void doStartAudio(int mStatus) {
//        if (mStatus == 1) {
//            cp.ifAudio = 1;
//            cp.audioStatu = 1;
//            if (in < 4) {
//                if (cplayer.setAudioStatus(cp.audioStatu) == 0) {
//                    audioTospeaking();
//                }
//            } else {
//                if (cplayerEx.setAudioStatus(cp.audioStatu) == 0) {
//                    audioTospeaking();
//                }
//            }
//        } else {
//            audioToNospeak();
//        }
    }

    private void doStopAudio() {
//        if (cp.ifAudio == 1 && cp.audioStatu == 1) {
//            cp.audioStatu = 0;
//            if (in < 4)
//                cplayer.setAudioStatus(cp.audioStatu);
//            else
//                cplayerEx.setAudioStatus(cp.audioStatu);
//            audioToSpeak();
//        }
    }

    private void audioToNospeak() {
//        playback_ImageButton_sound.setImageResource(R.drawable.png_nosound);
    }

    private void audioToSpeak() {
//        playback_ImageButton_sound.setImageResource(R.drawable.png_sound1);
    }

    private void audioTospeaking() {
//        playback_ImageButton_sound
//                .setImageResource(R.drawable.png_sound1_orange);
    }

    OnClickListener sfvClick=new OnClickListener() {
        @Override
        public void onClick(View v) {
            vislbleOrGoneView();
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ifLand = true;
            setSrceenHorizontal();
        } else if (myContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ifLand = false;
            setSrceenVertical();
        }
    }

    private void initDm(){
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mWhith = dm.widthPixels;
        mHeight = dm.heightPixels;
    }


    private void setSrceenVertical(){
        initDm();
        if(fishEyeInfo.fishType==0) {
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(mRelativeSfv, myWidth, myHeight, mWhith, mHeight);
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(sfv, myWidth, myHeight, mWhith, mHeight);
        }else{
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(mRelativeSfv, 4, 4, mWhith, mHeight);
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(sfv, 4, 4, mWhith, mHeight);
        }
        setSrceenAlPha(TRANSLUCENT_255);
        setWidgetVisible(View.VISIBLE);
    }

    private void setSrceenHorizontal(){
        initDm();
        if(fishEyeInfo.fishType==0) {
            SrceensUtils.setHorizontal(mRelativeSfv, mWhith, mHeight);
            SrceensUtils.setHorizontal(sfv, mWhith, mHeight);
        }else{
            SrceensUtils.setHorizontal(mRelativeSfv, mWhith, mHeight);
            SrceensUtils.setHorizontal(sfv, mWhith, mWhith);
        }
        setSrceenAlPha(TRANSLUCENT_155);
        setWidgetVisible(View.GONE);
    }

    private void setSrceenAlPha(int alPha){
        int menuSize=menuList.size();
        for (View rl:menuList){
            rl.getBackground().setAlpha(alPha);
        }
    }

    private void setWidgetVisible(int visible){
        for (View rl:menuList){
            rl.setVisibility(visible);
        }
        mRelativeBottom.setVisibility(visible);
    }

    private void vislbleOrGoneView(){
        if (ifLand){
            if (mRelativeBottom.getVisibility()==View.VISIBLE){
                setWidgetVisible(View.GONE);
            }else{
                setWidgetVisible(View.VISIBLE);
            }
        }
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int flag=seekBar.getProgress();
            setStartText(flag);
            ifPause=false;
            mPlayerImageButtonPlay
                    .setImageResource(R.drawable.png_playback_stop);
            rpe.setSuspend(ifPause);
            rpe.seekRecplayer(flag);
        }
    };

    // 手势
    private GestureDetector gestureDetector;
    private boolean mIsFirst = true;// 是否是第一次触摸屏幕

    // private float mOriginalLength;// 刚触摸时两个手指的距离
    // private float mCurrentLength;// 当前两个手指的距离

    class sfvOntouch implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                getMaxWidthAndHeight();
                d_x = event.getX();
                d_y = event.getY();
                DOING = 1;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!mIsFirst) {
                    mIsFirst = true;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (event.getPointerCount() == 1 && DOING == 1
                        && m_Multiple != 1) {
                    setMultipleMove(event.getX(), event.getY());
                }
            }
            return gestureDetector.onTouchEvent(event)
                    || myScaleGesture.onTouchEvent(event);
        }
    }

    class sfvGestrue extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            DOING = DOUBLECLICK;
            if (m_Multiple == (float) 1) {
                screensToMultiple(e.getX(), e.getY());
            } else if (m_Multiple != (float) 1) {
                StopZoom();
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            vislbleOrGoneView();
            return super.onSingleTapUp(e);
        }
    }

    /**
     * 屏幕缩放------------------------------------
     */
    private static final int DOUBLECLICK = 2;
    private static final int ZOOMING = 3;
    private static int DOING = 1;

    private static final float MULTIPLE = 3;
    private static float m_Multiple = 1;// 倍数
    private static display_point m_Point;
    private static float m_x = 0;
    private static float m_y = 1;
    private static float sfv_width = 0;// surface的宽度
    private static float sfv_height = 0;// surface的高度
    static boolean ifZoom = false;

    private static float d_x = 0;
    private static float d_y = 0;

    private static float m_d_x = 0;
    private static float m_d_y = 0;

    // -----两个手指捏合时的中心距离
    // private static float get_x = 0;
    // private static float get_y = 0;

    private void getMaxWidthAndHeight() {
        if (in >= 4) {
            m_x = rpe.getMaxXoffset();
            m_y = 1;
            sfv_width = sfv.getWidth();
            sfv_height = sfv.getHeight();
        }
    }

    private void StartZoom() {
        ifZoom = true;
        if (m_Point == null) {
            m_Point = new display_point();
        }
    }

    private void StopZoom() {
        screensToRestore();
    }

    private void screensToMultiple(float f_x, float f_y) {
        m_d_x = f_x;
        m_d_y = f_y;
        m_Multiple = MULTIPLE;
        setM(m_d_x, m_d_y);
        if (in >= 4) {
            rpe.DisplayChange(m_Point);
        }
        setM_d_x();
        setM_d_y();
    }

    private void setM_d_x() {
        if (m_d_x < (sfv_width / (2 * m_Multiple))) {
            m_d_x = (sfv_width / (2 * m_Multiple));
        } else if (m_d_x > (sfv_width - (sfv_width / (2 * m_Multiple)))) {
            m_d_x = (sfv_width - (sfv_width / (2 * m_Multiple)));
        }
    }

    private void setM_d_y() {
        if (m_d_y < (sfv_height / (2 * m_Multiple))) {
            m_d_y = (sfv_height / (2 * m_Multiple));
        } else if (m_d_y > (sfv_height - (sfv_height / (2 * m_Multiple)))) {
            m_d_y = (sfv_height - (sfv_height / (2 * m_Multiple)));
        }
    }

    private void setM(float f_x, float f_y) {
        float tmp_width = sfv_width / (2 * m_Multiple);
        m_Point.point0_x = (f_x - tmp_width) / sfv_width;
        m_Point.point1_x = m_Point.point0_x;
        m_Point.point2_x = (f_x + tmp_width) / sfv_width;
        m_Point.point3_x = m_Point.point2_x;
        setPointX0();
        setPointX1();
        setPointX2();
        setPointX3();

        float tmp_height = sfv_height / (2 * m_Multiple);
        m_Point.point0_y = (f_y + tmp_height) / sfv_height;
        m_Point.point1_y = (f_y - tmp_height) / sfv_height;
        m_Point.point2_y = m_Point.point1_y;
        m_Point.point3_y = m_Point.point0_y;
        setPointY0();
        setPointY1();
        setPointY2();
        setPointY3();
    }

    private void setMultipleMove(float f_x, float f_y) {
        float move_x = f_x - d_x;
        float move_y = f_y - d_y;
        if (Math.abs(move_x) > 5 || Math.abs(move_y) > 5) {
            float m_m_x = m_d_x - move_x;
            boolean x_change = false;
            if (m_m_x < (sfv_width / (2 * m_Multiple))
                    || m_m_x > (sfv_width - (sfv_width / (2 * m_Multiple)))) {

            } else {
                x_change = true;
                m_d_x = m_d_x - move_x;
            }
            float m_m_y = m_d_y - move_y;
            boolean y_chang = false;
            if (m_m_y < (sfv_height / (2 * m_Multiple))
                    || m_m_y > (sfv_height - (sfv_height / (2 * m_Multiple)))) {

            } else {
                y_chang = true;
                m_d_y = m_d_y - move_y;
            }
            setM(m_d_x, m_d_y);
            d_x = f_x;
            d_y = f_y;
            if ((x_change || y_chang) && in >= 4) {
                rpe.DisplayChange(m_Point);
            }
            setM_d_x();
            setM_d_y();
        }
    }

    private void setPointX0() {
        if (m_Point.point0_x < 0) {
            m_Point.point0_x = 0;
        } else if (m_Point.point0_x > m_x - m_x / m_Multiple) {
            m_Point.point0_x = m_x - m_x / m_Multiple;
        }
    }

    private void setPointX1() {
        if (m_Point.point1_x < 0) {
            m_Point.point1_x = 0;
        } else if (m_Point.point1_x > m_x - m_x / m_Multiple) {
            m_Point.point1_x = m_x - m_x / m_Multiple;
        }
    }

    private void setPointX2() {
        if (m_Point.point2_x < m_x / m_Multiple) {
            m_Point.point2_x = m_x / m_Multiple;
        } else if (m_Point.point2_x > m_x) {
            m_Point.point2_x = m_x;
        }
    }

    private void setPointX3() {
        if (m_Point.point3_x < m_x / m_Multiple) {
            m_Point.point3_x = m_x / m_Multiple;
        } else if (m_Point.point3_x > m_x) {
            m_Point.point3_x = m_x;
        }
    }

    private void setPointY0() {
        if (m_Point.point0_y < m_y / m_Multiple) {
            m_Point.point0_y = m_y / m_Multiple;
        } else if (m_Point.point0_y > m_y) {
            m_Point.point0_y = m_y;
        }
    }

    private void setPointY1() {
        if (m_Point.point1_y < 0) {
            m_Point.point1_y = 0;
        } else if (m_Point.point1_y > m_y - m_y / m_Multiple) {
            m_Point.point1_y = m_y - m_y / m_Multiple;
        }
    }

    private void setPointY2() {
        if (m_Point.point2_y < 0) {
            m_Point.point2_y = 0;
        } else if (m_Point.point2_y > m_y - m_y / m_Multiple) {
            m_Point.point2_y = m_y - m_y / m_Multiple;
        }
    }

    private void setPointY3() {
        if (m_Point.point3_y < m_y / m_Multiple) {
            m_Point.point3_y = m_y / m_Multiple;
        } else if (m_Point.point3_y > m_y) {
            m_Point.point3_y = m_y;
        }
    }

    private void screensToRestore() {
        // Log.i("info", TAG + "      stopzoom");
        m_Multiple = 1;
        m_Point.point0_x = 0;
        m_Point.point1_x = 0;
        m_Point.point2_x = m_x;
        m_Point.point3_x = m_x;
        m_Point.point0_y = 1;
        m_Point.point1_y = 0;
        m_Point.point2_y = 0;
        m_Point.point3_y = m_y;
        if (in >= 4) {
            rpe.DisplayChange(m_Point);
        }
    }

    private ScaleGestureDetector myScaleGesture;

    ScaleGestureDetector.OnScaleGestureListener listener = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            DOING = ZOOMING;
            last_multiple = detector.getScaleFactor();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float sf = detector.getScaleFactor();
            setMultiple2(sf);
            return false;
        }
    };

    private static float last_multiple = 0;

    private void setMultiple2(float m_multiple) {
        if (m_multiple > 1 && m_Multiple == 1) {
            m_d_x = d_x;
            m_d_y = d_y;
        }
        m_Multiple = m_Multiple + (m_multiple - last_multiple);
        if (m_Multiple < 1) {
            m_Multiple = 1;
        } else if (m_Multiple > MULTIPLE) {
            m_Multiple = MULTIPLE;
        }
        setM(m_d_x, m_d_y);
        if (in >= 4) {
            rpe.DisplayChange(m_Point);
        }
        last_multiple = m_multiple;
        setM_d_x();
        setM_d_y();
    }

    private void setAudioStatus(boolean status){
        rpe.setIsPlayAudio(status);
        if(status == false){
            mPlayerImageButtonSound.setImageResource(R.drawable.png_sound_grey);
        }else{
            mPlayerImageButtonSound.setImageResource(R.drawable.png_sound_white);
        }
    }


    /**
     * 播放回调
     */
    OnPlayerCallbackListener playerCallbackListener = new OnPlayerCallbackListener() {
        @Override
        public void OnPlayStatusChanged(int index, int status, String tag,int progress) {
//            Log.i(TAG,"OnPlayStatusChanged    status="+status+"     index="+index);
            Message m = Message.obtain();
            m.what = PLAYSTATUSCHANGED;
            m.arg1 = status;
            mHandler.sendMessage(m);
        }

        @Override
        public void GetWidthAndHeight(int i, int width, int height) {
            Message m = Message.obtain();
            m.what = GETWIDTHANDHEIGHT;
            m.arg1 = width;
            m.arg2 = height;
            mHandler.sendMessage(m);
        }

        @Override
        public void OnAudiosStatusChanged(int index, int audio_status,
                                          int p2ptalk_status, int playHandler, int sendSize) {
            Message m = Message.obtain();
            m.what = ONAUDIOSSTATUSCHANGED;
            m.arg1 = audio_status;
            mHandler.sendMessage(m);
        }

        @Override
        public void OnCaptureEnable(int index) {

        }
    };

    private int frameIndex=-1;
    OnRecPlayerCallbackListen recPlayerCallback=new OnRecPlayerCallbackListen() {
        @Override
        public void onFrameTimeCallback(int i) {
            if (frameIndex!=i) {
//                Log.i(TAG,"onFrameTimeCallback     i="+i);
                frameIndex=i;
                Message message = Message.obtain();
                message.what = SEEKBAR_MOVE;
                message.arg1 = frameIndex;
                mHandler.sendMessage(message);

                Log.e("DEBUG",""+rpe.getFrameUtcTime());
                Log.e("DEBUG",""+rpe.getFrameLocalTime());
                Log.e("DEBUG",""+TimeZone.getDefault().getRawOffset());

            }
        }
    };

}
