package com.jia.ezcamera.alarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.ezcamera.play.CJpgFileTool;
import com.jia.ezcamera.utils.CPlayParams;
import com.jia.ezcamera.utils.SrceensUtils;
import com.jia.ezcamera.utils.StringUtils;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;



import vv.ppview.PpviewClientInterface;
import vv.playlib.CPlayerEx;
import vv.playlib.OnPlayerCallbackListener;

/**
 * Created by ls on 15/1/28.
 */
public class PlayAlarmActivity extends Activity {
    private static final String TAG = PlayAlarmActivity.class.getSimpleName();
    private static final int PLAYSTATUSCHANGED = 101;
    private static final int GETWIDTHANDHEIGHT = 102;
    private static final int ONAUDIOSSTATUSCHANGED = 103;
    private static final int CATCH_PICTURE = 201;
    private static final int CATCH_PICTURE_FAIL = 202;
    private Activity mActivity = this;
    private long playTime;
    RelativeLayout mRelativeSfv;
    View mViewPlay;
    SeekBar mSeekbar;
    ProgressBar progressb;
    TextView progressText;
    ImageButton mPlayerImageButtonCamera;
    ImageButton mPlayerImageButtonPlay;
    ImageButton mPlayerImageButtonSound;
    List<View> menuList;
    RelativeLayout mRelativeBottom;
    private TextView start_time,end_time;
    
    private int myWidth=0;
    private int myHeight=0;
    private int myAllSize=0;
    OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_return:
				finish();
				break;
			case R.id.player_ImageButton_sound:
				if(ifAudio){
		    		cplayerEx.setAudioStatus(0);
		    		mPlayerImageButtonSound.setImageResource(R.drawable.png_sound_grey);
		    	}else{
		    		cplayerEx.setAudioStatus(1);
		    		mPlayerImageButtonSound.setImageResource(R.drawable.png_sound_white);
		    	}
		    	ifAudio = !ifAudio;
				break;
			case R.id.player_ImageButton_play:
				if(ifPlay){
					doStopPlay();
				}else{
					doStartPlay(playTime+mSeekbar.getProgress());
				}
				break;
			case R.id.player_ImageButton_camera:
				doScreenshot();
				break;
			default:
				break;
			}
		}
	};

    //-----play
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    private CPlayerEx cplayerEx = null;
    private Context myContext = null;
    private GLSurfaceView sfv = null;

    private CPlayParams cp = null;

    //-----srceens
    private DisplayMetrics dm = new DisplayMetrics();
    private float mDensity;
    private int mWhith = 0;
    private int mHeight = 0;

    //-----status
    private boolean ifPlaying = false;
    private boolean ifPlay = false;
    private boolean ifLand=false;
    private boolean ifAudio=true;
    
    private long stopTime = 0;
    private Timer stopTimer = null;
    private int alarmTime = 0;
    
    private boolean isMoveSeekBar = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_local);
        myContext=this;
        Intent intent = getIntent();
        if (intent != null) {
            cp = (CPlayParams) getIntent().getSerializableExtra("CPlayParams");
            playTime = getIntent().getLongExtra("PlayTime", 0)-3;
            alarmTime = getIntent().getIntExtra("AlarmTime", 0)+3;
            Log.e(TAG, getPlayTime(playTime,TIME_TYPE1)+"   playtime   "+playTime);
        }
        init();
        initPlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int viewFlag = -1;
        /**
         * �Ƿ���ֹ����
         */
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        try {
//            viewFlag = Settings.System.getInt(this.getContentResolver(),
//                    Settings.System.ACCELEROMETER_ROTATION);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (viewFlag == 0) {
//            Settings.System.putInt(myContext.getContentResolver(),
//                    Settings.System.ACCELEROMETER_ROTATION, 1);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doStartPlay(playTime);
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

    
    private void initDm(){
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mWhith = dm.widthPixels;
        mHeight = dm.heightPixels;
    }
    private void init() {
    	initDm();
        findViewById(R.id.btn_return).setOnClickListener(onClickListener);
        TextView titleText = (TextView)findViewById(R.id.head_title);
        titleText.setText(R.string.alarm_video);
        mRelativeSfv = (RelativeLayout)findViewById(R.id.relative_sfv);
        mViewPlay = findViewById(R.id.view_play);
        mPlayerImageButtonPlay = (ImageButton)findViewById(R.id.player_ImageButton_play);
        mPlayerImageButtonSound = (ImageButton)findViewById(R.id.player_ImageButton_sound);
        mPlayerImageButtonCamera = (ImageButton)findViewById(R.id.player_ImageButton_camera);
        mPlayerImageButtonCamera.setOnClickListener(onClickListener);
        mPlayerImageButtonSound.setOnClickListener(onClickListener);
        mPlayerImageButtonPlay.setOnClickListener(onClickListener);
        start_time = (TextView)findViewById(R.id.start_time);
        end_time = (TextView)findViewById(R.id.end_time);
        setStartText(0);
    	setEndText(alarmTime);
        mSeekbar = (SeekBar)findViewById(R.id.seekbar);
        mSeekbar.setMax(alarmTime);
        mSeekbar.setProgress(0);
        progressb = (ProgressBar)findViewById(R.id.progressbar);
        progressText = (TextView)findViewById(R.id.pbtext);
        progressb.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);
        findViewById(R.id.linearLayout_info).setVisibility(View.GONE);
        SrceensUtils.setVertical_2_1(mRelativeSfv, mWhith);
        sfv = new GLSurfaceView(myContext);
        RelativeLayout.LayoutParams sfv_lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sfv_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRelativeSfv.addView(sfv, sfv_lp);
        menuList = new ArrayList<View>();
        menuList.add(findViewById(R.id.include_top));
        menuList.add(findViewById(R.id.include_bottom));

        mRelativeBottom = (RelativeLayout)findViewById(R.id.relative_bottom);
        sfv.setOnClickListener(sfvClick);
        
        mSeekbar.setOnSeekBarChangeListener(seekBarListener);
       // mTextTime.setText(myContext.getResources().getString(R.string.time_of_cam) + getPlayTime(playTime,TIME_TYPE2));
    }

    private void initPlay() {
        cplayerEx = new CPlayerEx(mActivity, 0, playerCallbackListener,cp.fishEyeInfo);
        cplayerEx.setSurfaceview1(sfv);
        cplayerEx.setPlayMode(1);

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
                    if (mStatus == 1) {
                    	progressb.setVisibility(View.GONE);
                        ifPlay = true;
                        mPlayerImageButtonPlay
                                .setImageResource(R.drawable.png_playback_stop);
                        if(ifAudio){
                        	//����Ƶ
            	    		cplayerEx.setAudioStatus(1);
            	    		mPlayerImageButtonSound.setImageResource(R.drawable.png_sound_white);
            	    		
            	    	}else{
            	    		//�ر���Ƶ
            	    		cplayerEx.setAudioStatus(0);
            	    		mPlayerImageButtonSound.setImageResource(R.drawable.png_sound_grey);
            	    	}
                    } else if(mStatus==2){
                    	//��ʼ����
                    	progressb.setVisibility(View.VISIBLE);
                    	progressText.setVisibility(View.VISIBLE);
                    	progressText.setText(msg.arg2+"%");
                    }else if(mStatus==3){
                    	//ֹͣ����
                    	progressb.setVisibility(View.GONE);
                    	progressText.setVisibility(View.GONE);
                    }else {
                    	progressb.setVisibility(View.GONE);
                        ifPlay = false;
                        mPlayerImageButtonPlay
                                .setImageResource(R.drawable.png_playback_play);
                        showPLayToast(mStatus);
                    }
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
                    break;
                case CATCH_PICTURE:
                	cancleWaitDialog();
                    SaveOrDeletePic((String) msg.obj);
                    break;
                case CATCH_PICTURE_FAIL:
                	cancleWaitDialog();
                    cancleImportDialog();
                    showMessage(PlayAlarmActivity.this.getResources().getString(
                            R.string.photot_fail));
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * ��ʼ����
     */
    private void doStartPlay(long startPlayTime) {
    	if(startPlayTime>=playTime+alarmTime){
    		return;
    	}
        if (cp != null) {
            ifPlaying = true;
            progressb.setVisibility(View.VISIBLE);
            Log.e("DEBUG","getPlayTime "+getPlayTime(startPlayTime,TIME_TYPE1)+"   "+startPlayTime);
            
            cplayerEx.startPlayBack(cp.dev_id, cp.user, cp.pass, 0,getPlayTime(startPlayTime,TIME_TYPE1));
            
            
            if(stopTimer==null){
            	stopTimer = new Timer();
            	stopTimer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub

								int curTime = (int) (alarmTime-(stopTime-cplayerEx.getFrameLocalTime()));
                                Log.e("DEBUG","getFrameLocalTime "+cplayerEx.getFrameLocalTime()+"    "+stopTime);
                                Log.e("DEBUG","curTime "+curTime);
								if(!isMoveSeekBar&&curTime>=0&&curTime<=alarmTime){
									mSeekbar.setProgress(curTime);
									setStartText(curTime);
								}
								//Log.e("DEBUG", "UTCTIME  "+cplayerEx.getFrameLocalTime()+"     STOPTIME  "+stopTime+"     "+curTime);
								if(cplayerEx.getFrameLocalTime()!=0&&stopTime==0){
									stopTime = cplayerEx.getFrameLocalTime()+alarmTime-1;
								}
								if(cplayerEx.getFrameLocalTime()!=0&&(cplayerEx.getFrameLocalTime()>=stopTime)){
                                    mSeekbar.setProgress(alarmTime);
                                    setStartText(alarmTime);
                                    doStopPlay();
								}
								
							}
						});
						
						
					}
				}, 500, 500);
            }
        }
    }
    
    private void setStartText(int startTime){
    	start_time.setText(StringUtils.format(startTime/60)+":"+StringUtils.format(startTime%60));
    }

    private void setEndText(int endTime){
        end_time.setText(StringUtils.format(endTime/60)+":"+StringUtils.format(endTime%60));
    }

    private void doStopPlay() {

        cplayerEx.stopPlay();

        progressb.setVisibility(View.GONE);
        ifPlaying = false;
        ifPlay = false;
  
        mPlayerImageButtonPlay.setImageResource(R.drawable.png_playback_play);
        progressb.setVisibility(View.GONE);
        if(stopTimer!=null){
        	stopTimer.cancel();
        	stopTimer = null;
        }
    }
    
    
    private void doMovePlay(long movePlayTime){
    	doStopPlay();
    	doStartPlay(movePlayTime);
    }

    private void showPLayToast(int i) {
        if (i != 1) {
        	ToastUtils.show(myContext, getString(R.string.alarm_playback_faild)+i);
        }
    }

    private void doStartOrStopAudio() {
        if (cp.ifAudio == 1) {
            if (cp.audioStatu == 0) {
                cp.audioStatu = 1;
                cplayerEx.setAudioStatus(cp.audioStatu);
            } else if (cp.audioStatu == 1) {
                cp.audioStatu = 0;
                cplayerEx.setAudioStatus(cp.audioStatu);
            }
        }
    }
    
    
    OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			isMoveSeekBar = false;
			doMovePlay(playTime+seekBar.getProgress());
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			isMoveSeekBar = true;
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			setStartText(progress);
		}
	};

    /**
     * ���Żص�
     */
    OnPlayerCallbackListener playerCallbackListener = new OnPlayerCallbackListener() {
        @Override
        public void OnPlayStatusChanged(int index, int status, String tag,int progress) {
            ifPlaying = false;
            Message m = Message.obtain();
            m.what = PLAYSTATUSCHANGED;
            m.arg1 = status;
            m.arg2 = progress;
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
    };


    
    
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
    
    private void setSrceenVertical(){
        initDm();
        if(cp.fishEyeInfo==null||cp.fishEyeInfo.fishType==0) {
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(mRelativeSfv, myWidth, myHeight, mWhith, mHeight);
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(sfv, myWidth, myHeight, mWhith, mHeight);
        }else{
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(mRelativeSfv, 4, 4, mWhith, mHeight);
            SrceensUtils.ChangeSfvWidthAndHeight_Vertical(sfv, 4, 4, mWhith, mHeight);
        }
        setSrceenAlPha(255);
        setWidgetVisible(View.VISIBLE);
    }

    private void setSrceenHorizontal(){
        initDm();
        SrceensUtils.setHorizontal(mRelativeSfv,mWhith,mHeight);
        SrceensUtils.setHorizontal(sfv,mWhith,mHeight);
        setSrceenAlPha(155);
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
    
    private Bitmap bm = null;
    private Dialog importDialog = null;
    private View importView = null;
    // private CCommonParams cp = null;
    private String bitId;
    public ProgressDialog m_LoginWaitDialog = null;
    private void showMessage(String str) {
        Toast.makeText(PlayAlarmActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void cancleImportDialog() {
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
            // bm = null;
        }
        if (importDialog != null) {
            importDialog.cancel();
        }
    }
    

    private void showWaitDialog() {
        if (m_LoginWaitDialog == null) {
            m_LoginWaitDialog = new ProgressDialog(this);
            String message = PlayAlarmActivity.this.getResources().getString(
                    R.string.get_picture);
            m_LoginWaitDialog = new ProgressDialog(PlayAlarmActivity.this);// ProgressDialog.show(this,
            m_LoginWaitDialog.setMessage(message);
        }
        m_LoginWaitDialog.show();
    }

    private void cancleWaitDialog() {
        if (m_LoginWaitDialog != null) {
            m_LoginWaitDialog.cancel();
        }
    }
    
    CJpgFileTool cft = CJpgFileTool.getInstance();
    
    private boolean doGetBitmap(int height, String fileName) {
        // cp = screens.getCaptureData(getViewId());
        int i_cp = cplayerEx.getCaptureFile(fileName);
        Log.i(TAG, "doGetBitmap     height=" + height + "      fileName=" + fileName+ "    i_cp=" + i_cp);
        if (i_cp != 0) {
        	mHandler.sendEmptyMessage(CATCH_PICTURE_FAIL);
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
        // bm = BitmapFactory.decodeByteArray(cp.data, 0, cp.len, options);
        if (bm == null) {
        	mHandler.sendEmptyMessage(CATCH_PICTURE_FAIL);
            return false;
        }
        return true;
    }
    
    private void doScreenshot() {
        if (cft.IsCanUseSdCard()) {
            showWaitDialog();
            new Thread(new Runnable() {

                @Override
                public void run() {
                	String strdate = new SimpleDateFormat("yyyy.MM.dd")
                    .format(new java.util.Date());
		            File file = new File(cft.getCatchPicturePath() + strdate
		                    + "/");
		            if (!file.exists()) {
		                file.mkdirs();
		            }
		            bitId = "" + System.currentTimeMillis();
//		            final String filePath = cft.getCatchPicturePath() + strdate
//		                    + "/" + bitId + "_jpg.jpg";

                    final String filePath = cft.getCatchPicturePath() + strdate
                            + "/" +  cp.fishEyeInfo.fishType
                            +"_"+ cp.fishEyeInfo.main_x1_offsize +"_"+ cp.fishEyeInfo.main_x2_offsize +"_"+ cp.fishEyeInfo.main_y1_offsize +"_"+ cp.fishEyeInfo.main_y2_offsize
                            +"_"+bitId + "_jpg.jpg";
//                    Log.i(TAG,"doScreenshot    filePath="+filePath);
                    if (doGetBitmap(320, filePath)) {
                        Message m = new Message();
                        m.what = CATCH_PICTURE;
                        m.obj = filePath;
                        mHandler.sendMessage(m);
                    }
                }
            }).start();
        } else {
            showMessage(this.getResources().getString(R.string.sdcard_not_use));
        }
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
                // v.getTag();
                cancleImportDialog();
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(filePath));
                intent.setData(uri);
                PlayAlarmActivity.this.sendBroadcast(intent);
                showMessage(PlayAlarmActivity.this.getResources().getString(
                        R.string.save_ok));
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
}
