package com.jia.ezcamera.play;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.CPlayParams;
import com.jia.ezcamera.utils.CalendarUtils;
import com.jia.ezcamera.utils.ErrorToastUtils;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.SrceensUtils;
import com.jia.ezcamera.utils.TimeAlgorithm;
import com.jia.ezcamera.utils.TimeAxis;
import com.jia.znjj2.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


import vv.android.params.PlaybackGetDateListItem;
import vv.android.params.PlaybackGetMinListItem;
import vv.playlib.CPlayerEx;
import vv.playlib.FishEyeInfo;
import vv.playlib.OnPlayerCallbackListener;
import vv.playlib.render.display_point;
import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dPlaybackGetDateListCallback;
import vv.ppview.PpviewClientInterface.OnC2dPlaybackGetMinListCallback;
import vv.ppview.PpviewClientInterface.OnDevConnectCallbackListener;

public class VideoPlayBackActivityEx extends Activity implements
        OnPlayerCallbackListener {
    private static final String TAG = VideoPlayBackActivityEx.class.getSimpleName();
    private static final int MAX_PROGRESS = 1440;
    private static final int CONNECTOR = 200;
    private static final int PLAYSTATUSCHANGED = 101;
    private static final int GETWIDTHANDHEIGHT = 102;
    private static final int ONAUDIOSSTATUSCHANGED = 103;
    private static final int CATCH_PICTURE = 201;
    private static final int CATCH_PICTURE_FAIL = 202;
    private static final int START_PLAYBACK = 999;
    private static final int CHECK_ALL = 0;
    private static final int CHECK_MOUTH = 1;
    private Activity mActivity = this;
    private MainApplication app;
    OnC2dPlaybackGetDateListCallback onC2dPlaybackGetDateListCallback = new OnC2dPlaybackGetDateListCallback() {

        @Override
        public void on_c2d_PlaybackGetDateList(int nResult,
                                               ArrayList<PlaybackGetDateListItem> list) {
            Log.i(TAG, "mouth-----on_c2d_PlaybackGetDateList    nResult=" + nResult);
            Message m = Message.obtain();
            m.what = CHECK_MOUTH;
            m.arg1 = nResult;
            m.obj = list;
            mHandler.sendMessage(m);
        }
    };
    private static final int CHECK_DAY = 2;
    OnC2dPlaybackGetMinListCallback checkDayCallback = new OnC2dPlaybackGetMinListCallback() {

        @Override
        public void on_c2d_PlaybackGetMinList(int nResult,
                                              ArrayList<PlaybackGetMinListItem> list) {
            // 200: 正确
            // 203: 无权限
            // 204: 设备未就绪
            // 500: 请求消息有错误
//            Log.i(TAG, "on_c2d_PlaybackGetMinList   list=" +
//                    list.size());
            Message m = Message.obtain();
            m.what = CHECK_DAY;
            m.arg1 = nResult;
            m.obj = list;
            mHandler.sendMessage(m);
        }
    };
    /**
     * 屏幕缩放------------------------------------
     */
    private static final int DOUBLECLICK = 2;
    private static final int ZOOMING = 3;
    private static final float MULTIPLE = 3;
    static boolean ifZoom = false;
    private static int DOING = 1;
    private static float m_Multiple = 1;// 倍数
    private static display_point m_Point;
    private static float m_x = 0;
    private static float m_y = 1;
    private static float sfv_width = 0;// surface的宽度
    private static float sfv_height = 0;// surface的高度
    private static float d_x = 0;
    private static float d_y = 0;
    private static float m_d_x = 0;
    private static float m_d_y = 0;
    private static float last_multiple = 0;
    OnScaleGestureListener listener = new OnScaleGestureListener() {

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
    private String alarmPlayTime = null;
    CJpgFileTool cft = CJpgFileTool.getInstance();// 截图
    int nHour = 0;
    int nMinute = 0;
    int nSecond = 0;
    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    // -----日历
    DialogCalendar dc;

    long zeroTime = 0;
    private ImageButton playback_btn_return;
    private TextView moveTimeText;
    private ImageView moveTimeImage;
    private RelativeLayout moveTimeRelativeLayout;
    private TextView playback_txt_title;
    private TimeAxis timeAxis;
    private RelativeLayout ll_sfv;// 装sfv的linearlayout
    private GLSurfaceView sfv = null;
    private boolean ifLand = false;// 是否横屏
    OnClickListener sfvClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (ifLand) {
                if (playback_ll_buttonId.getVisibility() == View.GONE) {
                    showMenu();
                } else {
                    goneMenu();
                }
            }
        }
    };
    private boolean ifFull = false;// 是否全屏
    // private StaffView staff;
    // private ruler_view m_ruler;
    private ProgressBar progressb;
    private TextView progressText;
    private TextView mTime_tv;// 显示日期
    private RelativeLayout playback_rl_top;
    private LinearLayout playback_ll_buttonId;// 底部
    private Bitmap bm = null;
    private Dialog importDialog = null;
    private View importView = null;
    private Context mContext;
    private DisplayMetrics dm = new DisplayMetrics();
    private int mWhith = 0;
    private int mHeight = 0;
    private float mDensity;// 密度
    private float hourGraduation = 0;
    private boolean ifChecked = false;// 是否查到回放录像
    private float fGraduation = 0;// mWidth/1440
    private ImageButton playback_ImageButton_sound;
    private ImageButton playback_ImageButton_camera;
    private ImageButton playback_imageButton_play;
    private ImageButton playback_ImageButton_screen;
    private boolean ifPlay = false;// 是否开始播放
    private boolean isSeekTouch = false;


    private ImageView select_time_img;
    private RelativeLayout select_time_layout;
    private TextView select_time_text;

    private String playTime;


    TimeAxis.OnValueChangeListener onValueChangeListener = new TimeAxis.OnValueChangeListener() {
        @Override
        public void onValueChange(TimeAlgorithm _value) {
            setTextTime();
        }

        @Override
        public void onStartValueChange(TimeAlgorithm _value) {
            mHandler.removeMessages(START_PLAYBACK);
            isSeekTouch = true;
        }

        @Override
        public void onStopValueChange(TimeAlgorithm _value) {
            mHandler.sendEmptyMessageDelayed(START_PLAYBACK, 1000);
        }
    };
    private Timer timeProgressTimer = null;
    private int firstUtcTime = 0;
    private boolean ifDoingPlay = false;// 是否正在启动播放
    /**
     * 自定义datepicker
     *
     * @author ls
     */
    // private class MyDatePickerDialog extends DatePickerDialog {
    //
    // public MyDatePickerDialog(Context context, OnDateSetListener listener,
    // int year, int monthOfYear, int dayOfMonth) {
    // super(context, listener, year, monthOfYear, dayOfMonth);
    // }
    //
    // @Override
    // protected void onStop() {
    // // super.onStop();
    // }
    //
    // }

    OnClickListener BtnOnclick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playback_btn_return:
                    VideoPlayBackActivityEx.this.finish();
                    break;
                case R.id.playback_ImageButton_sound:
                    doStartOrStopAudio();
                    break;
                case R.id.playback_ImageButton_camera:
                    doScreenshot();
                    break;
                case R.id.playback_imageButton_play:
                    if (!ifDoingPlay) {
                        if (ifPlay) {
                            doStopPlay();
                            playback_imageButton_play
                                    .setImageResource(R.drawable.png_playback_play);
                        } else {
                            doStartPlay(playTime);
                        }
                    }
                    break;
                case R.id.playback_ImageButton_screen:
                    //doSetScrreen();
                    break;
                case R.id.select_time_img:
                    //在鱼眼模式下，进入拖动界面
                    if(select_time_layout.getVisibility()==View.GONE){
                        select_time_layout.setVisibility(View.VISIBLE);
                    }else{
                        select_time_layout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
    private ImageButton mButton_datapicker;
    private Calendar m_calendar;
    // private String mDevid = null;
    // private String mDevuser = null;
    // private String mDevPass = null;
    // private int mChlId = 0;
    private CPlayParams cp = null;
    private int recType = 1;
    private long mConnector = 0;
    OnDevConnectCallbackListener onMessageCallback = new OnDevConnectCallbackListener() {

        @Override
        public void on_connect_callback(final int msgid, final long connector, final int result) {
            // Log.i(TAG, "result=" + result + "     connector=" + connector
            // + "     msgid=" + msgid);
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mConnector == connector) {
		                if (result == 1 && msgid == 256) {
		                    doCheckPlayBack(CHECK_DAY);
		                } else {
		                    Message m = Message.obtain();
		                    m.what = CONNECTOR;
		                    m.arg1 = result;
		                    m.arg2 = msgid;
		                    mHandler.sendMessage(m);
		                }
		            }
				}
			});
            
        }
    };
    private String myMonth = null;
    /**
     * 选择日期
     */
    OnClickListener BtnSetDateOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // showDatePicker();
            dc.showDialog(mContext, m_calendar);
            mYear = m_calendar.get(Calendar.YEAR);
            mMonth = m_calendar.get(Calendar.MONTH);
            myMonth = "" + format(mYear) + "" + format(mMonth + 1);
//            Log.i(TAG,"BtnSetDateOnClickListener    myMont="+myMonth);
            doCheckPlayBack(CHECK_MOUTH);
        }
    };

    private String myDay = null;
    /**
     * 日历操作回调
     */
    CalendarUtils caldenerCallback = new CalendarUtils() {

        @Override
        public void seletData(String year, String month, String day) {
            mYear = Integer.parseInt(year);
            mMonth = Integer.parseInt(month) - 1;
            mDay = Integer.parseInt(day);
            // Log.i(TAG, "seletData    mMonth=" + mMonth);
            m_calendar.set(Calendar.YEAR, mYear);
            m_calendar.set(Calendar.MONTH, mMonth);
            m_calendar.set(Calendar.DAY_OF_MONTH, mDay);
            //nHour = 0;
            //nMinute = 0;
            //nSecond = 0;
            initTextTime();
            myMonth = "" + format(mYear) + "" + format(mMonth + 1);
            myDay = "" + format(mYear) + "" + format(mMonth + 1) + ""
                    + format(mDay);
            doStopPlay();
            doCheckPlayBack(CHECK_DAY);
        }

        @Override
        public void doNext(String year, String month, String dat) {

            mYear = Integer.parseInt(year);
            mMonth = Integer.parseInt(month);
            // Log.i(TAG, "doNext    mMonth=" + mMonth + "     mYear=" + mYear);
            myMonth = "" + format(mYear) + "" + format(mMonth + 1);
            doCheckPlayBack(CHECK_MOUTH);
        }

        @Override
        public void doPrevious(String year, String month, String dat) {
            mYear = Integer.parseInt(year);
            mMonth = Integer.parseInt(month);
            // Log.i(TAG, "doPrevious    mMonth=" + mMonth + "     mYear=" +
            // mYear);
            myMonth = "" + format(mYear) + "" + format(mMonth + 1);
            doCheckPlayBack(CHECK_MOUTH);
        }

        /**
         * 获取下一天
         */
        @Override
        public void doGetNextDay(String year, String month, String day) {
            mYear = Integer.parseInt(year);
            mMonth = Integer.parseInt(month) - 1;
            mDay = Integer.parseInt(day);
            // Log.i(TAG, "seletData    mMonth=" + mMonth);
            m_calendar.set(Calendar.YEAR, mYear);
            m_calendar.set(Calendar.MONTH, mMonth);
            m_calendar.set(Calendar.DAY_OF_MONTH, mDay);
            initTextTime();
            myMonth = "" + format(mYear) + "" + format(mMonth + 1);
            myDay = "" + format(mYear) + "" + format(mMonth + 1) + ""
                    + format(mDay);
            doCheckPlayBack(CHECK_DAY);
        }
    };
    private ArrayList<PlaybackGetMinListItem> myDayList;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_DAY:
                	ProgressDialogUtil.getInstance().cancleDialog();
                    int mResult = msg.arg1;
                    if (mResult == 200) {
                        myDayList.clear();
                        myDayList = (ArrayList<PlaybackGetMinListItem>) msg.obj;
                        if (myDayList == null) {
                            return;
                        }
                        int mSize = myDayList.size();
                        if (mSize > 0) {
                            ifChecked = true;
                        } else {
                            ifChecked = false;
                        }
                    } else {
                        ifChecked = false;
                        showToast(mResult);
                    }
                    zeroTime =0;
                    timeAxis.setDate(format(mYear) + "-" + format(mMonth + 1) + "-"
                            + format(mDay));
                    timeAxis.setMinList(myDayList);
                    setTimeAxis();
                    setTextTime();


                    break;
                case CHECK_MOUTH:
                	ProgressDialogUtil.getInstance().cancleDialog();
                    int mouthResult = msg.arg1;
                    if (mouthResult == 200) {
                        ArrayList<PlaybackGetDateListItem> mList = (ArrayList<PlaybackGetDateListItem>) msg.obj;
                        if (mList == null) {
                            return;
                        }
                        if (mList != null && mList.size() > 0) {
//                            for (PlaybackGetDateListItem pd : mList) {
//                                Log.i(TAG, "-----start=" + pd.start_day + "     end=" + pd.end_day);
//                            }
                            if (dc.isNowMonth(format(mYear), format(mMonth + 1))) {
                                dc.setStartAndEnd(mList);
                                dc.doRefresh();
                            }
                        }
                    } else {
                        showToast(mouthResult);
                    }
                    break;
                case CONNECTOR:
                    showConnectMessage(msg.arg1);
                    break;
                case PLAYSTATUSCHANGED:
                    int mStatus = msg.arg1;
                    if (mStatus == 1) {
                    	progressb.setVisibility(View.GONE);
                        ifPlay = true;
                        if(cp.fishEyeInfo.fishType!=0){
                            select_time_img.setVisibility(View.VISIBLE);
                        }
                        playback_imageButton_play
                                .setImageResource(R.drawable.png_playback_stop);
                    } else if(mStatus == 2){
                    	//进入缓冲状态
                    	progressb.setVisibility(View.VISIBLE);
                    	progressText.setVisibility(View.VISIBLE);
                    	progressText.setText(msg.arg2+"%");
                    }else if(mStatus == 3){
                    	//退出缓冲状态
                    	progressb.setVisibility(View.GONE);
                    	progressText.setVisibility(View.GONE);
                    }else {
                    	progressb.setVisibility(View.GONE);
                        ifPlay = false;
                        if(cp.fishEyeInfo.fishType!=0){
                            select_time_img.setVisibility(View.GONE);
                        }
                        playback_imageButton_play
                                .setImageResource(R.drawable.png_playback_play);
                        showPLayToast(mStatus);
                    }
                    break;
                case GETWIDTHANDHEIGHT:
                    int mw = msg.arg1;
                    int mh = msg.arg2;
                    if (mw > 0 && mh > 0) {
                        cp.chl_width = mw;
                        cp.chl_height = mh;
                        if (ifLand) {
                            setSrceenHorizontal();
                            //setLand(ll_sfv, sfv);
                        } else {
                            //setPort(ll_sfv, sfv);
                            setSrceenVertical();
                        }
                    }
                    break;
                case ONAUDIOSSTATUSCHANGED:
                    doStartAudio(msg.arg1);
                    break;
                case CATCH_PICTURE:
                	ProgressDialogUtil.getInstance().cancleDialog();
                    SaveOrDeletePic((String) msg.obj);
                    break;
                case CATCH_PICTURE_FAIL:
                	ProgressDialogUtil.getInstance().cancleDialog();
                    cancleImportDialog();
                    showMessage(mContext.getResources().getString(
                            R.string.photot_fail));
                    break;
                case START_PLAYBACK:
                    if(myDayList.size()>0){
                        doStopPlay();
                        doStartPlay(timeAxis.getPlayDate());
                        isSeekTouch = false;
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private CPlayerEx cplayerEx = null;
    // 手势
    private GestureDetector gestureDetector;
    private boolean mIsFirst = true;// 是否是第一次触摸屏幕
    private ScaleGestureDetector myScaleGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayback_ex);
        app = (MainApplication)getApplication();
        mContext = this;
        Intent intent = getIntent();
        if (intent != null) {
            cp = (CPlayParams) intent.getSerializableExtra("item");
            alarmPlayTime = intent.getStringExtra("alarmtime");
        }
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mWhith = dm.widthPixels;
        mHeight = dm.heightPixels;
        hourGraduation = mWhith / 24;
        fGraduation = (float) mWhith / 1440;
        Log.i(TAG, "fGraduation=" + fGraduation);
        init();

        myDayList = new ArrayList<PlaybackGetMinListItem>();
        doCreateConnect();

        dc = DialogCalendar.getInstance();
        dc.setCallback(caldenerCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 是否阻止休眠
         */
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // -----开启屏幕旋转
//        int viewFlag = -1;
//        try {
//            viewFlag = Settings.System.getInt(this.getContentResolver(),
//                    Settings.System.ACCELEROMETER_ROTATION);
//        } catch (SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (viewFlag == 0) {
//            Settings.System.putInt(mContext.getContentResolver(),
//                    Settings.System.ACCELEROMETER_ROTATION, 1);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onvif_c2s
                .setOnC2dPlaybackGetDateListCallback(onC2dPlaybackGetDateListCallback);
        onvif_c2s.setOnC2dPlaybackGetMinListCallback(checkDayCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        doStopPlay();
        //releaseConnector();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseConnector();
        GetDevInfoBean info = app.getDevInfo(cp.dev_id);
		Intent intent = new Intent(mContext,PlayActivity.class);
		CPlayParams cPlayParams = new CPlayParams();
		cPlayParams.chl_id = 0;
		cPlayParams.dev_id = info.dev_id;
		cPlayParams.user = "admin";
		cPlayParams.pass = info.devpass;
		cPlayParams.uid = info.channels.get(0).uid;
		cPlayParams.cloudId = info.channels.get(0).ptz;
		cPlayParams.ifp2ptalk = info.channels.get(0).voicetalk_type;
		cPlayParams.play_type = info.channels.get(0).play_type;
		FishEyeInfo fishEyeInfo = new FishEyeInfo();
		fishEyeInfo.fishType = info.channels.get(0).fisheyetype;
		fishEyeInfo.main_x1_offsize = info.channels.get(0).streams.get(0).fisheye_params.l;
		fishEyeInfo.main_x2_offsize = info.channels.get(0).streams.get(0).fisheye_params.r;
		fishEyeInfo.main_y1_offsize = info.channels.get(0).streams.get(0).fisheye_params.t;
		fishEyeInfo.main_y2_offsize = info.channels.get(0).streams.get(0).fisheye_params.b;

	
		cPlayParams.fishEyeInfo = fishEyeInfo;
		intent.putExtra("PlayParams", cPlayParams);
		startActivity(intent);
    }

    private void init() {
    	moveTimeRelativeLayout = (RelativeLayout)findViewById(R.id.playback_move_layout);
    	moveTimeText = (TextView)findViewById(R.id.playback_move_time);
    	moveTimeImage = (ImageView)findViewById(R.id.playback_move_image);
        playback_rl_top = (RelativeLayout) findViewById(R.id.playback_rl_top);
        playback_ll_buttonId = (LinearLayout) findViewById(R.id.playback_ll_buttonId);
        progressb = (ProgressBar) findViewById(R.id.pb);
        progressb.setVisibility(View.GONE);
        progressText = (TextView) findViewById(R.id.pbtext);
        progressText.setVisibility(View.GONE);

        ll_sfv = (RelativeLayout) findViewById(R.id.ll_sfv);
        //toStandFull(ll_sfv);
        sfv = new GLSurfaceView(mContext);

        gestureDetector = new GestureDetector(this, new sfvGestrue());
        myScaleGesture = new ScaleGestureDetector(this, listener);
        StartZoom();
        sfv.setOnTouchListener(new sfvOntouch());

        RelativeLayout.LayoutParams sfv_lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        sfv_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        ll_sfv.addView(sfv, sfv_lp);
        setSrceenVertical();

        playback_btn_return = (ImageButton) findViewById(R.id.playback_btn_return);
        playback_btn_return.setOnClickListener(BtnOnclick);

        playback_txt_title = (TextView) findViewById(R.id.playback_txt_title);
        playback_txt_title.setText(cp.chl_name);

        playback_ImageButton_camera = (ImageButton) findViewById(R.id.playback_ImageButton_camera);
        playback_ImageButton_sound = (ImageButton) findViewById(R.id.playback_ImageButton_sound);
        playback_imageButton_play = (ImageButton) findViewById(R.id.playback_imageButton_play);
        playback_ImageButton_screen = (ImageButton) findViewById(R.id.playback_ImageButton_screen);
        playback_ImageButton_screen.setVisibility(View.GONE);

        playback_ImageButton_camera.setOnClickListener(BtnOnclick);
        playback_ImageButton_sound.setOnClickListener(BtnOnclick);
        playback_imageButton_play.setOnClickListener(BtnOnclick);
        playback_ImageButton_screen.setOnClickListener(BtnOnclick);

        mButton_datapicker = (ImageButton) findViewById(R.id.playback_ImageButton_calender);
        mButton_datapicker.setOnClickListener(BtnSetDateOnClickListener);
        m_calendar = Calendar.getInstance();

        mYear = m_calendar.get(Calendar.YEAR);
        mMonth = m_calendar.get(Calendar.MONTH);
        mDay = m_calendar.get(Calendar.DAY_OF_MONTH);

        myMonth = "" + format(mYear) + "" + format(mMonth + 1);
        myDay = "" + format(mYear) + "" + format(mMonth + 1) + ""
                + format(mDay);
        mTime_tv = (TextView) findViewById(R.id.playback_text_time);
        initTextTime();


        timeAxis = (TimeAxis)findViewById(R.id.time_axis);
        timeAxis.setOnValueChangeListener(onValueChangeListener);

        cplayerEx = new CPlayerEx(mActivity, 0, this,cp.fishEyeInfo);
        cplayerEx.setSurfaceview1(sfv);
        cplayerEx.setPlayMode(1);


        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("HHmmss");
        String date = sdFormatter.format(nowTime);
        nHour = Integer.parseInt(date.substring(0, 2));
        nMinute = Integer.parseInt(date.substring(2, 4));
        nSecond = Integer.parseInt(date.substring(4, 6));
        if(alarmPlayTime!=null){
        	//播放报警录像
        	myMonth = alarmPlayTime.substring(0, 6);
        	myDay = alarmPlayTime.substring(0, 8);
        	mYear = Integer.parseInt(alarmPlayTime.substring(0, 4));
        	mMonth = Integer.parseInt(alarmPlayTime.substring(4, 6))-1;
        	mDay = Integer.parseInt(alarmPlayTime.substring(6, 8));
        	nHour = Integer.parseInt(alarmPlayTime.substring(8, 10));
        	nMinute = Integer.parseInt(alarmPlayTime.substring(10, 12));
        	nSecond = Integer.parseInt(alarmPlayTime.substring(12, 14));
        	m_calendar.set(mYear, mMonth, mDay, nHour, nMinute, nSecond);
        	initTextTime();
        	doStartPlay(alarmPlayTime);
        }


        select_time_img = (ImageView)findViewById(R.id.select_time_img);
        select_time_img.setOnClickListener(BtnOnclick);
        select_time_layout = (RelativeLayout)findViewById(R.id.select_time_layout);
        select_time_layout.setOnTouchListener(new FishSelectTimeOntouch());
        select_time_text = (TextView)findViewById(R.id.select_time_text);
    }

    private void initTextTime() {
        String strtmp = format(mYear) + "-" + format(mMonth + 1) + "-"
                + format(mDay) + " " + format(nHour) + ":" + format(nMinute) + ":" + format(nSecond);
        mTime_tv.setText(strtmp);
    }

    // 播放
    private void doStartPlay(String playtime) {
        if (cp != null ) {
            ifDoingPlay = true;
            releaseTimeProgressTimer();
            progressb.setVisibility(View.VISIBLE);
            this.playTime = playtime;
            cplayerEx.startPlayBack(cp.dev_id, cp.user, cp.pass, 0, playTime);
            
            sfv.setVisibility(View.VISIBLE);
            startTimeProgressTimer();
        }
    }

    private long curTime = 0;
    private void startTimeProgressTimer() {
        releaseTimeProgressTimer();
        firstUtcTime = 0;
        timeProgressTimer = new Timer();
        timeProgressTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        long utctime = cplayerEx.getFrameLocalTime();
                        if(ifPlay&&utctime>0&&curTime!=utctime&&!isSeekTouch) {
                            getTime(utctime);
                            curTime = utctime;
                        }

                    }
                });
            }
        }, 0, 1000);
    }

    private void getTime(long mss) {
        long m_cur_utc_time = mss;
        long dayTime = m_cur_utc_time % (24 * 60 * 60);

        if (zeroTime == 0) {
            zeroTime = m_cur_utc_time - dayTime;
        }
        long theTime = mss - zeroTime;
//        Log.e(TAG, "86400-----theTime=" + theTime+"     dayTime="+dayTime+"     zeroTime="+zeroTime);
        if (theTime >= 1440 * 60) {
//            dc.showDialog(m_calendar);
            zeroTime = 0;
            m_calendar.add(Calendar.DAY_OF_MONTH, (int)theTime / (1440 * 60));
            mYear = m_calendar.get(Calendar.YEAR);
            mMonth = m_calendar.get(Calendar.MONTH);
            mDay = m_calendar.get(Calendar.DAY_OF_MONTH);
//            Log.i(TAG, "mYear=" + mYear + "    mMonth=" + mMonth + "     mDay=" + mDay);
            initTextTime();
            myMonth = "" + format(mYear) + "" + format(mMonth + 1);
            myDay = "" + format(mYear) + "" + format(mMonth + 1) + ""
                    + format(mDay);
            doCheckPlayBack(CHECK_DAY);

        }
        if (theTime < 0) {
            zeroTime = 0;
        }else{
            changeSeekbar((int)theTime);

        }

    }

    private void changeSeekbar(int theTime) {
        nHour = theTime / 3600;
        nMinute = ((theTime / 60) % 60);
        nSecond = (theTime % 60);
        timeAxis.setDate(format(mYear) + "-" + format(mMonth + 1) + "-"
                + format(mDay));
        setTimeAxis();

        //seekbar.setProgress(nHour * 60 + nMinute);
        setTextTime();
    }

    private void setTimeAxis(){
        timeAxis.setDate(format(mYear) + "-" + format(mMonth + 1) + "-"
                + format(mDay));
        String axisTime = "";
        if(nHour<10){
            axisTime = axisTime+"0"+nHour;
        }else{
            axisTime = axisTime+nHour;
        }
        axisTime = axisTime+":";
        if(nMinute<10){
            axisTime = axisTime+"0"+nMinute;
        }else{
            axisTime = axisTime+nMinute;
        }
        axisTime = axisTime+":";

        if(nSecond<10){
            axisTime = axisTime+"0"+nSecond;
        }else{
            axisTime = axisTime+nSecond;
        }
        TimeAlgorithm time = new TimeAlgorithm(axisTime);
        timeAxis.setValue(time);
    }


    private void setTextTime(){
        mTime_tv.setText(timeAxis.getDate3());
        if(!ifMoveTime)
            select_time_text.setText(timeAxis.getDate3());
    }


    private void releaseTimeProgressTimer() {
        if (timeProgressTimer != null) {
            timeProgressTimer.cancel();
            timeProgressTimer = null;
            firstUtcTime = 0;
        }
    }

    private void doStopPlay() {
        releaseTimeProgressTimer();
        doStopAudio();

        cplayerEx.stopPlay();
        ifDoingPlay = false;
        ifPlay = false;
        if(cp.fishEyeInfo.fishType!=0){
            select_time_img.setVisibility(View.GONE);
        }
        sfv.setVisibility(View.INVISIBLE);
        progressb.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);
    }

    /**
     * 开始音频
     */
    private void doStartOrStopAudio() {
        if (cp.ifAudio == 1) {
            if (cp.audioStatu == 0) {
                cp.audioStatu = 1;
                audioTospeaking();
                cplayerEx.setAudioStatus(cp.audioStatu);
            } else if (cp.audioStatu == 1) {
                cp.audioStatu = 0;
                audioToSpeak();
                cplayerEx.setAudioStatus(cp.audioStatu);
            }
        } else {
            audioToNospeak();
        }
    }

    private void doStartAudio(int mStatus) {
        if (mStatus == 1) {
            cp.ifAudio = 1;
            cp.audioStatu = 1;

            if (cplayerEx.setAudioStatus(cp.audioStatu) == 0) {
                audioTospeaking();
            }

        } else {
            audioToNospeak();
        }
    }

    private void doStopAudio() {
        if (cp.ifAudio == 1 && cp.audioStatu == 1) {
            cp.audioStatu = 0;
            cplayerEx.setAudioStatus(cp.audioStatu);
            audioToSpeak();
        }
    }

    private void audioToNospeak() {
        playback_ImageButton_sound.setImageResource(R.drawable.png_sound1);
    }

    private void audioToSpeak() {
        playback_ImageButton_sound.setImageResource(R.drawable.png_sound1);
    }

    private void audioTospeaking() {
        playback_ImageButton_sound
                .setImageResource(R.drawable.png_sound1_orange);
    }

    /**
     * 设置播放时间
     *
     * @param mMiunte
     * @return
     */
    private String getPlayTime(int mMiunte) {
        String strTime = null;
        if (mMiunte > 0 && mMiunte < MAX_PROGRESS) {
            int mHour = mMiunte / 60;
            int iMinute = mMiunte % 60;
            strTime = "" + format(mYear) + "" + format(mMonth + 1) + ""
                    + format(mDay) + format(mHour) + format(iMinute) + "00";
        }
        // Log.i(TAG, "getPlayTime     strTime=" + strTime);
        return strTime;
    }
    
    
    /**
     * 设置播放时间
     *
     * @param sec
     * @return
     */
    private String getPlayTimeToSec(int sec) {
        long ltime = (long)sec;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String time = simpleDateFormat.format(ltime*1000);
        return time.replace("-","").replace(" ","").replace(":","");
    }


    
    
    private void setMoveTextTime(int utcTime) {
        long ltime = (long)utcTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String time = simpleDateFormat.format(ltime*1000);
        moveTimeText.setText(time);
        select_time_text.setText(time);
    }

    public String format(int i) {
        String s = "" + i;
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * 设置满屏或者按比例缩放
     *//*
    private void doSetScrreen() {
        if (ifFull) {
            ifFull = false;
            playback_ImageButton_screen
                    .setImageResource(R.drawable.png_screen2);
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            ChangeSfvWidthAndHeight_land(sfv, dm.widthPixels, dm.heightPixels);
        } else {
            ifFull = true;
            playback_ImageButton_screen
                    .setImageResource(R.drawable.png_screen1);
            toLandFull(sfv);
        }
    }*/

    /**
     * 创建连接
     */
    private void doCreateConnect() {
        if (cp.dev_id != null) {
        	onvif_c2s.setOndevConnectCallback(onMessageCallback);
            mConnector = onvif_c2s.createConnect(cp.dev_id, cp.user, cp.pass);
        }
    }

    private void releaseConnector() {
        if (mConnector != 0) {
        	onvif_c2s.removeOndevConnectCallback(onMessageCallback);
            onvif_c2s.releaseConnect(mConnector);
        }
    }

    /**
     * 查询回放列表
     */
    private void doCheckPlayBack(int mData) {
        if (mConnector != 0) {
            //doStopPlay();
            switch (mData) {
                case CHECK_ALL:
                    onvif_c2s.c2d_playbackGetCap_fun(mConnector);
                    break;
                case CHECK_MOUTH:
                	ProgressDialogUtil.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.doing_search_mouth));
                    onvif_c2s.c2d_playbackGetDateList_fun(mConnector, cp.chl_id,
                            recType, myMonth);
                    break;
                case CHECK_DAY:
                	ProgressDialogUtil.getInstance().showDialog(mContext, mContext.getResources().getString(R.string.doing_search_mouth));
                    onvif_c2s.c2d_playbackGetMinList_fun(mConnector, cp.chl_id,
                            recType, myDay);
                    break;
            }
        }
    }

    private void showToast(int mResult) {
        String strInfo = mContext.getResources().getString(
                R.string.check_playback_fail);
        switch (mResult) {
            case 203:
                strInfo = strInfo
                        + mContext.getResources().getString(R.string.set_203);
                break;
            case 204:
                strInfo = strInfo
                        + mContext.getResources().getString(R.string.set_204);
                break;
            case 500:
                strInfo = strInfo
                        + mContext.getResources().getString(R.string.set_500);
                break;
            case 502:
                strInfo = strInfo
                        + mContext.getResources().getString(R.string.sdcard_not_use);
                break;
            default:
                strInfo = strInfo + mResult;
                break;
        }
        Toast.makeText(mContext, strInfo, Toast.LENGTH_SHORT).show();
    }

    private void showConnectMessage(int i) {
        if (i != 1) {
            // Log.i(TAG, "-----showConnectMessage    mResult=" + i);
            String strInfo = "";
            if (i == 0) {
                strInfo = mContext.getResources().getString(
                        R.string.content_info0);
            } else if (i == -1) {
                strInfo = mContext.getResources().getString(
                        R.string.content_info_minus1);
            } else if (i == -2) {
                strInfo = mContext.getResources().getString(
                        R.string.content_info_minus2);
            } else if (i == -4) {
                strInfo = mContext.getResources().getString(
                        R.string.content_info_minus4);
            } else {
                strInfo = "" + i;
            }
            Toast.makeText(mContext, strInfo, Toast.LENGTH_SHORT).show();
        }
    }

    private void showPLayToast(int i) {
        if (i != 1) {
            ErrorToastUtils.PlayBackFail(mContext, i);
        }
    }


    /**
     * 播放回调
     */
    @Override
    public void OnPlayStatusChanged(int index, int status, String tag,int progress) {
        // Log.i(TAG, "OnPlayStatusChanged      index=" + index + "    status="
        // + status);
        ifDoingPlay = false;
        Message m = Message.obtain();
        m.what = PLAYSTATUSCHANGED;
        m.arg1 = status;
        m.arg2 = progress;
        mHandler.sendMessage(m);
    }

    @Override
    public void GetWidthAndHeight(int i, int width, int height) {
        // Log.i(TAG, "GetWidthAndHeight      i=" + i + "    width=" + width
        // + "     height=" + height);
        Message m = Message.obtain();
        m.what = GETWIDTHANDHEIGHT;
        m.arg1 = width;
        m.arg2 = height;
        mHandler.sendMessage(m);
    }

    @Override
    public void OnAudiosStatusChanged(int index, int audio_status,
                                      int p2ptalk_status, int playHandler, int sendSize) {
        // Log.i(TAG, "OnAudiosStatusChanged      index=" + index
        // + "    audio_status=" + audio_status + "      p2ptalk_status="
        // + p2ptalk_status + "      playHandler=" + playHandler
        // + "       sendSize=" + sendSize);
        cp.ifAudio = audio_status;
        cp.playHandler = playHandler;
        Message m = Message.obtain();
        m.what = ONAUDIOSSTATUSCHANGED;
        m.arg1 = audio_status;
        mHandler.sendMessage(m);
    }

    @Override
    public void OnCaptureEnable(int index) {

    }

    /**
     * 横竖屏切换
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWhith = dm.widthPixels;
        hourGraduation = (float) mWhith / 24;
        fGraduation = (float) mWhith / 1440;
        // Log.e(TAG, "onConfigurationChanged      mWhith=" + mWhith
        // + "     hourGraduation=" + hourGraduation
        // + "      fGraduation=" + fGraduation);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            setFullScreen();
            ifLand = true;
            playback_ImageButton_screen.setVisibility(View.VISIBLE);
            setSrceenHorizontal();
            //setLand(ll_sfv, sfv);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            quitFullScreen();
            ifLand = false;
            ifFull = false;
            playback_ImageButton_screen
                    .setImageResource(R.drawable.png_screen2);
            playback_ImageButton_screen.setVisibility(View.GONE);
            //setPort(ll_sfv, sfv);
            setSrceenVertical();

        }
    }

    // 设置全屏
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        goneSeek();
    }

    // private float mOriginalLength;// 刚触摸时两个手指的距离
    // private float mCurrentLength;// 当前两个手指的距离

    // 退出全屏函数：
    private void quitFullScreen() {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        showSeek();
    }

    private void showSeek() {
        timeAxis.setVisibility(View.VISIBLE);
        mTime_tv.setVisibility(View.VISIBLE);
        viewToShow();
    }

    private void goneSeek() {
        timeAxis.setVisibility(View.GONE);
        mTime_tv.setVisibility(View.GONE);
        viewToHide();
    }

    // -----设置透明度
    private void viewToHide() {
        playback_ll_buttonId.getBackground().setAlpha(155);
        playback_rl_top.getBackground().setAlpha(155);
        timeAxis.getBackground().setAlpha(155);
        goneMenu();
    }

    private void viewToShow() {
        playback_ll_buttonId.getBackground().setAlpha(255);
        playback_rl_top.getBackground().setAlpha(255);
        timeAxis.getBackground().setAlpha(255);
        showMenu();
    }

    private void showMenu() {
        timeAxis.setVisibility(View.VISIBLE);
        playback_ll_buttonId.setVisibility(View.VISIBLE);
        playback_rl_top.setVisibility(View.VISIBLE);
    }

    private void goneMenu() {
        timeAxis.setVisibility(View.GONE);
        playback_ll_buttonId.setVisibility(View.GONE);
        playback_rl_top.setVisibility(View.GONE);
    }


    private void initDm(){
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mWhith = dm.widthPixels;
        mHeight = dm.heightPixels;
    }


    private void setSrceenVertical(){
        initDm();
        if(cp.fishEyeInfo==null||cp.fishEyeInfo.fishType==0) {
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(ll_sfv, cp.chl_width, cp.chl_height, mWhith, mHeight);
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(sfv, cp.chl_width, cp.chl_height, mWhith, mHeight);
        }else{
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(ll_sfv, 4, 4, mWhith, mHeight);
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(sfv, 4, 4, mWhith, mHeight);
        }
    }

    private void setSrceenHorizontal(){
        initDm();
        if(cp.fishEyeInfo==null||cp.fishEyeInfo.fishType==0) {
            SrceensUtils.setHorizontal(ll_sfv, mWhith, mHeight);
            SrceensUtils.setHorizontal(sfv, mWhith, mHeight);
        }else{
            SrceensUtils.setHorizontal(ll_sfv, mWhith, mWhith);
            SrceensUtils.setHorizontal(sfv, mWhith, mWhith);
        }
    }

    // 竖屏3：4
    /*private void toStandFull(View mView) {
        // RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
        // RelativeLayout.LayoutParams.MATCH_PARENT,
        // RelativeLayout.LayoutParams.MATCH_PARENT);
        // (LayoutParams) mView.getLayoutParams();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.width = mWhith;
        lp.height = (int) (lp.width / 2);
        mView.setLayoutParams(lp);
    }

    private void setPort(View v1, View v2) {
        // LayoutParams lp = (LayoutParams) v1.getLayoutParams();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.width = mWhith;
        lp.height = (int) (lp.width * (float) 3 / 4);
        // v1.setLayoutParams(lp);
        ChangeSfvWidthAndHeight_port(v2, lp.width, lp.height);
        ChangeSfvWidthAndHeight_port(v1, lp.width, lp.height);
    }

    // 横屏全屏
    private void toLandFull(View mView) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;
        lp.height = dm.heightPixels;
        mView.setLayoutParams(lp);
    }

    private void setLand(View mView, View v2) {
        // LayoutParams lp = (LayoutParams) mView.getLayoutParams();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;
        lp.height = dm.heightPixels;
        // lp.width = getWindowManager().getDefaultDisplay().getWidth();
        // lp.height = getWindowManager().getDefaultDisplay().getHeight();
        mView.setLayoutParams(lp);
        ChangeSfvWidthAndHeight_land(v2, lp.width, lp.height);
    }

    *//**
     * 设置屏幕按比率缩放－－横屏
     *
     * @param v
     * @param myWidth
     * @param myHeight
     *//*
    private void ChangeSfvWidthAndHeight_land(View v, int myWidth, int myHeight) {
        int pWidth = myWidth;
        int pHeight = myHeight;
        if (cp != null) {
            int sfvWidth = cp.chl_width;
            int sfvHeight = cp.chl_height;
            if (sfvWidth == 0 || sfvHeight == 0) {
                sfvWidth = myWidth;
                sfvHeight = myHeight;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (((float) pWidth / pHeight) < ((float) sfvWidth / sfvHeight)) {// 当分辨率的宽高比率大于屏幕的宽高比率时，应该将高度保持不变，缩小宽度
                float m_h = (sfvHeight * pWidth) / sfvWidth;
                lp.height = (int) (((float) sfvHeight * pWidth) / sfvWidth);
                lp.width = myWidth;
                v.setLayoutParams(lp);
            } else if (((float) pWidth / pHeight) > ((float) sfvWidth / sfvHeight)) {
                lp.height = myHeight;
                lp.width = (int) (sfvWidth * ((float) pHeight / sfvHeight));
                v.setLayoutParams(lp);
            } else {
                lp.width = myWidth;
                lp.height = myHeight;
                v.setLayoutParams(lp);
            }
        }
    }

    *//**
     * 设置屏幕按比率缩放－－竖屏
     *
     * @param v
     * @param myWidth
     * @param myHeight
     *//*
    private void ChangeSfvWidthAndHeight_port(View v, int myWidth, int myHeight) {
        if (cp != null) {
            int sfvWidth = cp.chl_width;
            int sfvHeight = cp.chl_height;
            if (sfvWidth == 0 || sfvHeight == 0) {
                sfvWidth = myWidth;
                sfvHeight = myHeight;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (((float) 4 / 3) < ((float) sfvWidth / sfvHeight)) {// 当分辨率的宽高比率大�?�?时，应该将高度保持不变，缩小宽度
                lp.width = myWidth;
                lp.height = (int) (lp.width / ((float) sfvWidth / sfvHeight));
                v.setLayoutParams(lp);
            } else if (((float) 4 / 3) > ((float) sfvWidth / sfvHeight)) {
                lp.width = myWidth;
                lp.height = (int) ((((float) myWidth / myHeight) / ((float) sfvWidth / sfvHeight)) * myHeight);
                v.setLayoutParams(lp);
            } else {
                lp.width = myWidth;
                lp.height = myHeight;
                v.setLayoutParams(lp);
            }
        }
    }*/

    /**
     * 抓图
     */
    private void doScreenshot() {
        if (cft.IsCanUseSdCard()) {
        	ProgressDialogUtil.getInstance().showDialog(mContext,
                    mContext.getResources().getString(R.string.get_picture));
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String strdate = new SimpleDateFormat("yyyy.MM.dd")
                            .format(new Date());
                    File file = new File(cft.getCatchPicturePath() + strdate
                            + "/");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String bitId = "" + System.currentTimeMillis();
//                    final String filePath = cft.getCatchPicturePath() + strdate
//                            + "/" + bitId + "_jpg.jpg";
                    final String filePath = cft.getCatchPicturePath() + strdate
                            + "/" + + cp.fishEyeInfo.fishType
                            +"_"+ cp.fishEyeInfo.main_x1_offsize +"_"+ cp.fishEyeInfo.main_x2_offsize +"_"+ cp.fishEyeInfo.main_y1_offsize +"_"+ cp.fishEyeInfo.main_y2_offsize
                            +"_"+bitId + "_jpg.jpg";
                    if (doGetBitmap(320, filePath)) {
                        Message m = Message.obtain();
                        m.what = CATCH_PICTURE;
                        m.obj = filePath;
                        mHandler.sendMessage(m);
                    } else {
                        mHandler.sendEmptyMessage(CATCH_PICTURE_FAIL);
                    }
                }
            }).start();
        } else {
            showMessage(this.getResources().getString(R.string.sdcard_not_use));
        }
    }

    private boolean doGetBitmap(int height, String fileName) {
        int i_cp = -1;
        i_cp = cplayerEx.getCaptureFile(fileName);
        if (i_cp != 0) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        int i = cp.chl_width / height;
        if (i > 1) {
        } else {
            i = 1;
        }
        options.inSampleSize = i;
        bm = BitmapFactory.decodeFile(fileName, options);
        if (bm == null) {
            return false;
        }
        return true;
    }

    private void SaveOrDeletePic(String filepath) {
        final String filePath = filepath;
        importDialog = new Dialog(this, R.style.style_dlg_groupnodes);
        importView = View.inflate(this, R.layout.dlg_photo, null);
        // -----
        ImageView dlg_photo_img = (ImageView) importView
                .findViewById(R.id.dlg_photo_img);
        dlg_photo_img.setImageBitmap(bm);
        Button dlg_rename_btn_ok = (Button) importView
                .findViewById(R.id.dlg_rename_btn_ok);
        dlg_rename_btn_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cancleImportDialog();
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(filePath));
                intent.setData(uri);
                mContext.sendBroadcast(intent);
                showMessage(mContext.getResources().getString(R.string.save_ok));
                // saveBitmap();
            }
        });
        Button dlg_rename_btn_cancle = (Button) importView
                .findViewById(R.id.dlg_rename_btn_cancle);
        dlg_rename_btn_cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                File my_file = new File(filePath);
                if (my_file.exists()) {
                    my_file.delete();
                }
                cancleImportDialog();
            }
        });
        importDialog.setContentView(importView);
        importDialog.setCanceledOnTouchOutside(false);
        importDialog.show();
    }

    private void cancleImportDialog() {
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
        if (importDialog != null) {
            importDialog.cancel();
        }
    }

    // -----两个手指捏合时的中心距离
    // private static float get_x = 0;
    // private static float get_y = 0;

    private void showMessage(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    private void getMaxWidthAndHeight() {
            m_x = cplayerEx.getMaxXoffset();
            m_y = 1;
            sfv_width = sfv.getWidth();
            sfv_height = sfv.getHeight();
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
        cplayerEx.DisplayChange(m_Point);
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
    private int moveSecTime = 0;
    private void setSeekBarTime(float f_x, float f_y){
    	float move_x = f_x - d_x;
    	if(move_x>0){
    		moveTimeImage.setImageResource(R.drawable.png_playback_forword);
    	}else{
    		moveTimeImage.setImageResource(R.drawable.png_playback_back);
    	}
        int move_time = (int) ((int)move_x+timeAxis.getSec());
        setMoveTextTime(move_time);
        moveSecTime = move_time;
        //Log.e("DEBUG", "move "+move_time);
        
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
            if ((x_change || y_chang)) {
                cplayerEx.DisplayChange(m_Point);
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
        cplayerEx.DisplayChange(m_Point);
    }

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
        cplayerEx.DisplayChange(m_Point);
        last_multiple = m_multiple;
        setM_d_x();
        setM_d_y();
    }


    
    private boolean ifMoveTime = false;

    class sfvOntouch implements OnTouchListener {

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
                if(m_Multiple == 1&&ifPlay&&ifMoveTime){
                	moveTimeRelativeLayout.setVisibility(View.GONE);
                	doStartPlay(getPlayTimeToSec(moveSecTime));
                	ifMoveTime = false;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE&&ifPlay) {
                if (event.getPointerCount() == 1 && DOING == 1
                        && m_Multiple != 1) {
                    setMultipleMove(event.getX(), event.getY());
                }else{
                	float move_x = event.getX() - d_x;
                	if(Math.abs(move_x)>80&&!ifMoveTime){
                		ifMoveTime = true;
                		moveTimeRelativeLayout.setVisibility(View.VISIBLE);
                	}
                	if(ifMoveTime)
                		setSeekBarTime(event.getX(), event.getY());
                }
            }
            return gestureDetector.onTouchEvent(event)
                    || myScaleGesture.onTouchEvent(event);
        }
    }

    class FishSelectTimeOntouch implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                getMaxWidthAndHeight();
                d_x = event.getX();
                d_y = event.getY();

            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if(ifPlay&&ifMoveTime){
                    doStartPlay(getPlayTimeToSec(moveSecTime));
                    ifMoveTime = false;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE&&ifPlay) {

                    float move_x = event.getX() - d_x;
                    if(Math.abs(move_x)>80&&!ifMoveTime){
                        ifMoveTime = true;
                    }
                    if(ifMoveTime)
                        setSeekBarTime(event.getX(), event.getY());

            }
            return true;
        }
    }

    class sfvGestrue extends SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
        	if(ifPlay){
	            DOING = DOUBLECLICK;
	            if (m_Multiple == (float) 1) {
	                screensToMultiple(e.getX(), e.getY());
	            } else if (m_Multiple != (float) 1) {
	                StopZoom();
	            }
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
            if (ifLand) {
                if (playback_ll_buttonId.getVisibility() == View.GONE) {
                    showMenu();
                } else {
                    goneMenu();
                }
            }
            return super.onSingleTapUp(e);
        }
    }
}
