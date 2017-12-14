package com.jia.ezcamera.play;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;
import vv.android.params.CCStreamStatParams;
import vv.android.params.CCommonParams;
import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dLocalAlarmCallback;
import vv.playlib.*;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.CPlayParams;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

public class PlayActivity extends Activity{
    private Activity mContext = this;
    private DisplayMetrics dm;
    private float density;// 密度
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    private CPlayParams cPlayParams;
    private CJpgFileTool cft = CJpgFileTool.getInstance();
    private CViewPager play_viewpager_4;//多画面播放时的view pager
    private MyPagerAdapter m_pager_adapter1;//多屏播放viewpager 的adapter
    private MainApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_4);
		app = (MainApplication)getApplication();
		cPlayParams = (CPlayParams)getIntent().getSerializableExtra("PlayParams");
		initPlay();//初始化播放参数
        startFlowTimer();//打开速率线程
	}
	
	
	@Override
    protected void onStart() {
        super.onStart();
        //判断是否需要阻止休眠
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
	
	
	@Override
    protected void onResume() {
        super.onResume();
        //lay_wait2.setBackgroundResource(R.color.background_green);
        play_btn_select_preset.setVisibility(View.GONE);
        play_btn_select_video.setVisibility(View.GONE);
        play_btn_cruise.setVisibility(View.GONE);
        sfvStartPlay();
    }
	
	
	@Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancleFlowTimer();
		sfvStop();

	}

	@Override
    protected void onStop() {
        super.onStop();
        //移除P2P回调
        onvif_c2s.removeOndevConnectCallback(devConnectCallbackListener);
        //停止音频对讲
        VVAudio.VVTalkStop();
        //释放P2P连接
        if (cPlayParams.longConnect != 0 && cPlayParams.cloudId == 1 && cPlayParams.play_type == 0) {
            onvif_c2s.releaseConnect(cPlayParams.longConnect);
        }
        if (cPlayParams.longTalk != 0
                && (cPlayParams.ifp2ptalk == 2 || cPlayParams.ifp2ptalk == 1 || cPlayParams.ifp2ptalk == 4)) {
            onvif_c2s.releaseConnect(cPlayParams.longTalk);
        }
        
        if(alarm_connect>0){
        	onvif_c2s.releaseConnect(alarm_connect);
        }

        if (isSnap == true) {
            //如果已经拍照，那么更新缩略图
            
        }
    }
	
	/**
     * 开始预览
     */
    private void sfvStartPlay() {
        setSvfViewState(0);
        if (cPlayParams.play_type == 1) {
        	cplayerEx.startPlay(cPlayParams.dev_id, cPlayParams.user,
        			cPlayParams.pass, cPlayParams.chl_id, 0);
        	
        } else {
        	setSvfViewState(3);
        }
    }
    
    /**
     * 停止预览
     */
    private void sfvStop() {
        setSvfViewState(2);
        if (cPlayParams != null) {
            if (cPlayParams.ifVideo) {
                doStopVideo(cPlayParams);
            }
        }
        cplayerEx.stopPlay();
    }
    

    
    /**
     * 设置预览窗口的布局
     * @param state
     * 当前的播放状态，0连接中，1播放中，2停止播放状态，3播放失败状态
     *
     */
    private void setSvfViewState(int state){
        switch (state){
            case 0:
                //连接中
                btns2.setVisibility(View.GONE);//重连隐藏
                views2.setVisibility(View.INVISIBLE);//svf隐藏
                pbr_wait2.setVisibility(View.VISIBLE);//转圈显示
                break;
            case 1:
                //播放中
                btns2.setVisibility(View.GONE);//重连按钮隐藏
                views2.setVisibility(View.VISIBLE);//svf预览窗口显示
                pbr_wait2.setVisibility(View.GONE);//转圈隐藏
                break;
            case 2:
                //停止中
                btns2.setVisibility(View.GONE);//重连按钮隐藏
                views2.setVisibility(View.INVISIBLE);
                pbr_wait2.setVisibility(View.VISIBLE);
                break;
            case 3:
                //播放失败
                btns2.setVisibility(View.VISIBLE);
                views2.setVisibility(View.INVISIBLE);
                pbr_wait2.setVisibility(View.GONE);
                break;
            default:
                //播放失败
                btns2.setVisibility(View.VISIBLE);
                views2.setVisibility(View.INVISIBLE);
                pbr_wait2.setVisibility(View.GONE);
                break;
        }
    }
    
    
  //重新播放按键监听
    View.OnClickListener retryBtnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int i = (Integer) v.getTag();
            sfvStartPlay();
        }
    };
	
	 private boolean ifAgain;// 播放失败重新播放
	 private int ifPlayOk;// 记录每一个surfaceview是否播放成功 1:成功 -1：失败
	 private OneScreen  oneSc;
	 private GLSurfaceView  views2;
	 private RelativeLayout lay_wait2;
	 private ProgressBar pbr_wait2;
	 private Button btns2;
	 private SfvView sfvv2;
	 
	    private boolean isLand;//当前是否是横屏模式

	    
	    private RelativeLayout topButtonLayout;//上方的菜单栏
	    private LinearLayout bottomButtonLayout;//下方的菜单栏整个
	    private LinearLayout bottomButtonLayout2;//下方的菜单栏只包括按键
	    //------------------------顶部菜单栏按键-----------------------------
	    private ImageView play_btn_select_preset;//预置位按键
	    private ImageView play_btn_select_video;//码流选择按键
	    private ImageView play_btn_cruise;//云台左右巡航按键
	    private ImageButton btn_return;//退出预览按键
	    private Button btn_alarm;//布撤防按键
	    private long alarm_connect = 0;//布撤防connect
	    private int alarm_status = 0;


	    //------------------------底部菜单栏按键----------------------------
	    private ImageButton player_ImageButton_playback;//回放按键
	    private ImageButton player_ImageButton_sound;//声音开关
	    private ImageButton player_imageButton_speak;//对讲开关
	    private ImageButton player_ImageButton_camera;//截图按键
	    private ImageButton player_ImageButton_record;//录像按键
	    private TextView record_text;//录像提示文本


	    //------------------------速率窗口-----------------------------
	    private LinearLayout linearLayoutSpeed;//竖屏时速率窗口
	    private LinearLayout linearLayoutSpeed2;//横屏时速率窗口
	    private TextView textNowSpeed,textAllFlow;//竖屏时下载速度和总下载量
	    private TextView textNowSpeed2,textAllFlow2;//横屏时下载速度和总下载量

	    
	    private CPlayerEx cplayerEx = null;//VVSDK预览播放接口类
	    //------------------------vv 对讲接口-----------------------------
	    private VVAudio VVAudio;//vv对讲接口类
	    private int talkType;//当前设备的对讲类型  4：半双工 其他：全双工
	    private LinearLayout play_talk_ll;//对讲的控制布局
	    private LinearLayout play_talk_btn;//按下对讲说话的按键
	    private ImageView play_talk_gone;//退出对讲的按键
	    private TextView play_text_speak;//对讲的提示文本
	    private boolean ifTalk = false;//当前是否正在对讲

	 
	    private LinearLayout play_ll_viewId;//装点的布局
	    private TextView idText;

	    List<View> viewList = new ArrayList<View>();// 多屏集合


		private Button vr_changemode;//360VR下的修改模式按键
		private VRChangeModePopMenu popMenu = new VRChangeModePopMenu(this);



	private void initPlay(){
	        //初始化工具类
	        m_image_loader = ImageLoader_local.getInstance(this);
	        //p2p回调注册
	        onvif_c2s.setOndevConnectCallback(devConnectCallbackListener);
	        onvif_c2s.setOnC2dLocalAlarmCallback(new OnC2dLocalAlarmCallback() {
				
				@Override
				public void on_c2d_setLocalAlarmCallBack(final int res,final int status) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(res == 200){
								alarm_status = status;
								if(alarm_status==0){
									btn_alarm.setBackgroundResource(R.drawable.img_animate_disarm01_setting);
								}else{
									btn_alarm.setBackgroundResource(R.drawable.img_animate_disarm05_setting);
								}
							}else{
								ToastUtils.show(mContext, getString(R.string.set_alarm_faild));
							}
						}
					});
				}
				
				@Override
				public void on_c2d_getLocalAlarmCallBack(final int res) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(res==0||res==1){
								alarm_status = res;
								if(alarm_status==0){
									btn_alarm.setBackgroundResource(R.drawable.img_animate_disarm01_setting);
								}else{
									btn_alarm.setBackgroundResource(R.drawable.img_animate_disarm05_setting);
								}

							}else{
								ToastUtils.show(mContext, getString(R.string.get_alarm_faild));
							}
						}
					});
				}
			});
	        //布局初始化
	        topButtonLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
	        bottomButtonLayout = (LinearLayout) findViewById(R.id.linearLayout1);
	        bottomButtonLayout2 = (LinearLayout) findViewById(R.id.play_ll_buttonId);
	        play_ll_viewId = (LinearLayout) findViewById(R.id.play_ll_viewId);
	        play_ll_viewId.setVisibility(View.INVISIBLE);


	        //顶部菜单栏按键初始化
	        play_btn_select_preset = (ImageView)findViewById(R.id.play_btn_select_preset);
	        play_btn_select_video = (ImageView)findViewById(R.id.play_btn_select_video);
	        play_btn_cruise = (ImageView)findViewById(R.id.play_btn_cruise);
	        btn_return = (ImageButton) this.findViewById(R.id.btn_return);
	        btn_return.setOnClickListener(topButtonClickListener);
	        
	        btn_alarm = (Button)this.findViewById(R.id.button_alarm);
	        btn_alarm.setOnClickListener(topButtonClickListener);

	        play_btn_select_video.setOnClickListener(topButtonClickListener);
	        play_btn_cruise.setOnClickListener(topButtonClickListener);
	        play_btn_select_preset.setOnClickListener(topButtonClickListener);

	        //底部菜单栏按键初始化
	        player_ImageButton_playback = (ImageButton)findViewById(R.id.player_ImageButton_playback);
	        player_ImageButton_sound = (ImageButton) findViewById(R.id.player_ImageButton_sound);
	        player_imageButton_speak = (ImageButton) findViewById(R.id.player_imageButton_speak);
	        player_ImageButton_camera = (ImageButton) findViewById(R.id.player_ImageButton_camera);
	        player_ImageButton_record = (ImageButton) findViewById(R.id.player_ImageButton_record);
	        player_ImageButton_record.setOnClickListener(bottomButtonClickListener);
	        player_ImageButton_sound.setOnClickListener(bottomButtonClickListener);
	        player_imageButton_speak.setOnClickListener(bottomButtonClickListener);
	        player_ImageButton_camera.setOnClickListener(bottomButtonClickListener);
	        player_ImageButton_playback.setOnClickListener(bottomButtonClickListener);
	        record_text = (TextView) findViewById(R.id.record_text);
	        record_text.setVisibility(View.GONE);
	        idText = (TextView)findViewById(R.id.play_id_text);
	        idText.setVisibility(View.INVISIBLE);

	        //速率窗口初始化
	        linearLayoutSpeed = (LinearLayout) findViewById(R.id.linear_speed);

	        textNowSpeed = (TextView) findViewById(R.id.now_speed);
	        textAllFlow = (TextView) findViewById(R.id.all_flow);
	        textAllFlow.setOnTouchListener(new View.OnTouchListener() {
	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                switch (event.getAction()){
	                    case MotionEvent.ACTION_DOWN:
	                        idText.setVisibility(View.VISIBLE);
	                        break;
	                    case MotionEvent.ACTION_UP:
	                        idText.setVisibility(View.GONE);
	                        break;
	                }
	                return false;
	            }
	        });
	        linearLayoutSpeed2 = (LinearLayout) findViewById(R.id.linear_speed2);
	        textNowSpeed2 = (TextView) findViewById(R.id.now_speed2);
	        textAllFlow2 = (TextView) findViewById(R.id.all_flow2);

	        //对讲接口初始化
	        VVAudio = VVAudio.getInstance();
	        VVAudio.setVoiceTalkCallback(ctCallback);
	        play_talk_ll = (LinearLayout) findViewById(R.id.play_talk_ll);
	        play_talk_btn = (LinearLayout) findViewById(R.id.play_talk_btn);
	        play_talk_gone = (ImageView) findViewById(R.id.play_talk_gone);
	        play_text_speak = (TextView) findViewById(R.id.play_text_speak);
	        play_talk_gone.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	                talkLlToGone();
	            }
	        });

	        //预览窗口布局初始化
	        

	        // ---------------------单屏

	        oneSc = new OneScreen(this);
	        sfvv2 = new SfvView(this);
	        lay_wait2 = sfvv2.getRl();
	        views2 = sfvv2.getSfv();
	        pbr_wait2 = sfvv2.getPb();
	        btns2 = sfvv2.getBtn();
	        btns2.setOnClickListener(retryBtnClick);//播放失败重连的按键监听
	        
	        toStandFull(lay_wait2, views2);
	        oneSc.llAddView(sfvv2);
	        cplayerEx = new CPlayerEx(mContext, 0, onPlayerCallbackListener,cPlayParams.fishEyeInfo);
	        cplayerEx.setSurfaceview1(views2);

	        viewList.add(oneSc);
	        play_viewpager_4 = (CViewPager) findViewById(R.id.play_viewpager1);
	        //play_viewpager_4.setOnPageChangeListener(new MyPageListener());//多屏页切换监听
	        //play_viewpager_4.setfourscreenRightViewFlag(pager4_count - 1);
	       //play_viewpager_4.setonescreenleftViewFlag(pager4_count);
	       play_viewpager_4.setTouchIntercept(false);//防止svf和viewpager手势冲突
	        play_viewpager_4.setScrollble(false);//静止多屏页滑动

	        m_pager_adapter1 = new MyPagerAdapter(viewList);
	        play_viewpager_4.setAdapter(m_pager_adapter1);
	       // setCurPager1_Id(0);//设置当前页
	        
	        dm = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
	        density = dm.density;// 密度


			//VR模式切换菜单初始化
			vr_changemode = (Button)findViewById(R.id.vr_changemode);
			if(cPlayParams.fishEyeInfo.fishType==1) {
				vr_changemode.setVisibility(View.VISIBLE);
			}

			vr_changemode.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					popMenu.showStreamPopView(vr_changemode,cplayerEx);
				}
			});
	    }
	    
	    
	    private void toStandFullSquare(View v, View sfvV) {
	        ViewGroup.LayoutParams lp = v.getLayoutParams();
	        lp.width = (getWindowManager().getDefaultDisplay().getWidth());
	        lp.height = (getWindowManager().getDefaultDisplay().getWidth());
	        v.setLayoutParams(lp);
	        ChangeSfvWidthAndHeight_land(sfvV,lp.width, lp.height);
	    }
	    
	    private void toLandSquare(View v, View sfvV) {
	        ViewGroup.LayoutParams lp = v.getLayoutParams();
	        lp.width = (getWindowManager().getDefaultDisplay().getWidth());
	        lp.height = (getWindowManager().getDefaultDisplay().getHeight());
	        v.setLayoutParams(lp);
	        ChangeSfvWidthAndHeight_land(sfvV, lp.width, lp.width);
	    }

	    private void ChangeSfvWidthAndHeight_land(View v,int myWidth,
                int myHeight) {
			int pWidth = myWidth;
			int pHeight = myHeight;
			if (cPlayParams != null) {
				int sfvWidth = cPlayParams.chl_width;
				int sfvHeight = cPlayParams.chl_height;
				if (sfvWidth == 0 || sfvHeight == 0) {
					sfvWidth = myWidth;
					sfvHeight = myHeight;
				}
				ChangeSfvWidthAndHeight2(v, myWidth, myHeight);
			}
		}
	    
	    private void ChangeSfvWidthAndHeight2(View v, int myWidth, int myHeight) {
	        ViewGroup.LayoutParams lp = v.getLayoutParams();
	        lp.width = myWidth - 2;
	        lp.height = myHeight - 2;
	        v.setLayoutParams(lp);
	    }

	    
	    /**
	     * 单屏播放时竖屏模式
	     */
	    private void OneTohalf() {
	            //判断单屏时是否是鱼眼镜头
	            if(cPlayParams.fishEyeInfo.fishType==1||cPlayParams.fishEyeInfo.fishType==2) {
	                //180度鱼眼，始终正方形
	                toStandFullSquare(lay_wait2, views2);
	            }
	    }

	    /**
	     * 单屏播放时横屏模式
	     */
	    private void OneToFull() {
	            //判断单屏时是否是鱼眼镜头
	            if(cPlayParams.fishEyeInfo.fishType==1||cPlayParams.fishEyeInfo.fishType==2) {
	                //180度鱼眼，始终正方形
	                toLandSquare(lay_wait2, views2);
	            }
	        
	        
	    }
	  //预览窗口回调
	    OnPlayerCallbackListener onPlayerCallbackListener = new OnPlayerCallbackListener() {
	        @Override
	        public void OnPlayStatusChanged(final int index, final int status, final String tag, final int progress) {
	            switch (status){
	                case 1:
	                    //播放成功
	                    runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                            ifPlayOk = status;
	                            setSvfViewState(1);

	                            if ((cPlayParams.ifp2ptalk == 2 || cPlayParams.ifp2ptalk == 4 || cPlayParams.ifp2ptalk == 1)
	                                    && !cPlayParams.ifTalk) {
	                                //如果当前播放的参数支持对讲，先连接上对讲P2P
	                            	cPlayParams.longTalk = onvif_c2s.createConnect(cPlayParams.dev_id, cPlayParams.user, cPlayParams.pass);
	                            }
	                        }
	                    });
	                    break;
	                case -99:
	                    //播放过程中被断开
	                    runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                            //重新播放
	                            sfvStop();
	                            sfvStartPlay();
	                        }
	                    });
	                    break;
	                case 203:
	                case 414:
	                	//播放时密码错误
	                	runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                        	new AlertDialog.Builder(mContext)
		                		.setTitle(getString(R.string.tips))
		                		.setMessage(getString(R.string.info_connect_wrong_pwd))
		                		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                				
	                				@Override
	                				public void onClick(DialogInterface dialog, int which) {
	                					// TODO Auto-generated method stub
	                					finish();
	                				}
	                			})
		                		.show();
	                        }
	                    });
	                	
	                	break;
	                default:
	                    //播放失败
	                    runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                            if(ifAgain){
	                                //如果需要重连，那么重连
	                                ifAgain= false;
	                                if (ifPlayOk!=1) {
	                                    sfvStartPlay();
	                                }
	                            }
	                        }
	                    });
	                    break;
	            }

	        }

	        @Override
	        public void GetWidthAndHeight(final  int index, final  int width,final int height) {
	            //收到视屏的宽高回调
	        	cPlayParams.chl_width = width;
	        	cPlayParams.chl_height = height;
	            if (cPlayParams.cloudId == 1 && cPlayParams.longConnect <= 0 && cPlayParams.play_type == 0) {
	                //连接云台connect，老版本
	            	cPlayParams.longConnect = onvif_c2s.createConnect(cPlayParams.dev_id,
	            			cPlayParams.user, cPlayParams.pass);
	            } else if (cPlayParams.cloudId == 1 && cPlayParams.play_type == 1) {
	                //新版本，不用再单独连接connect
	            	cPlayParams.ifCloud = true;
	            }
	            if(alarm_connect<=0){
	            	alarm_connect = onvif_c2s.createConnect(cPlayParams.dev_id,
	            			cPlayParams.user, cPlayParams.pass);
	            }
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                	if (isLand) {
                            //当前在横屏模式
                            OneToFull();
                        } else {
                            //当前在竖屏模式
                            OneTohalf();
                        }
	                }
	            });

	        }

	        @Override
	        public void OnAudiosStatusChanged(final int index, int audio_status, int p2ptalk_status, int playHandler, int sendSize) {

	            if (cPlayParams == null)
	                return;
	            cPlayParams.ifAudio = audio_status;
	            cPlayParams.playHandler = playHandler;
	            cPlayParams.sendSize = sendSize;

	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                        //如果单屏播放,关闭当前播放的音频
	                        if (cPlayParams.ifAudio == 1) {
	                        	cPlayParams.audioStatu = 0;
	                        	cplayerEx.setAudioStatus(cPlayParams.audioStatu);
	                            setSoundImage(0);
	                        } else {
	                        	cPlayParams.audioStatu = 0;
	                            setSoundImage(0);
	                        }
	                    
	                }
	            });
	        }

	        @Override
	        public void OnCaptureEnable(final int index) {
	            //判断是否已经抓过图，如果没有抓过，那么先抓图作为封面
	            if (cPlayParams.ifPhoto == false) {
	                Timer get_picTimer = new Timer();
	                get_picTimer.schedule(new TimerTask() {
	                    @Override
	                    public void run() {
	                        get_cam_pic(cPlayParams.dev_id);//抓图作为封面
	                       // m_list_cam_manager.setCamPicState(cPlayParams.uid, true);//修改摄像头封面flag
	                        //params.ifPhoto = true;
	                    }
	                },1000);

	            }
	        }
	    };
	    
	    /**
	     * 单屏播放竖屏时显示的预览窗口大小
	     * @param v
	     * 包含sfv view的layout 例如lay_wait
	     * @param sfvV
	     * sfv view
	     */
	    private void toStandFull(View v, View sfvV) {
	        ViewGroup.LayoutParams lp = v.getLayoutParams();
	        lp.width = (getWindowManager().getDefaultDisplay().getWidth());
	        lp.height = (int) (lp.width * (float) 3 / 4);
	        v.setLayoutParams(lp);
	        ChangeSfvWidthAndHeight_port(sfvV,lp.width, lp.height);
	    }
	    
	    private void toStandFullLl_add_height(View v, int m_wifth, int m_height) {
	        ViewGroup.LayoutParams lp = v.getLayoutParams();
	        lp.width = m_wifth;
	        lp.height = m_height;
	        v.setLayoutParams(lp);
	    }
	    private void ChangeSfvWidthAndHeight_port(View v, int myWidth,
                int myHeight) {
			CPlayParams params = cPlayParams;
			if (params != null) {
				int sfvWidth = params.chl_width;
				int sfvHeight = params.chl_height;
				if (sfvWidth == 0 || sfvHeight == 0) {
				sfvWidth = myWidth;
				sfvHeight = myHeight;
			}
			if (((float) 4 / 3) < ((float) sfvWidth / sfvHeight)) {// 当分辨率的宽高比率大�?�?时，应该将高度保持不变，缩小宽度
			ViewGroup.LayoutParams lp = v.getLayoutParams();
			lp.width = myWidth - 2;
			lp.height = (int) (lp.width / ((float) sfvWidth / sfvHeight));
			toStandFullLl_add_height(lay_wait2, lp.width + 2, lp.height + 2);
			v.setLayoutParams(lp);
			} else if (((float) 4 / 3) > ((float) sfvWidth / sfvHeight)) {
			ViewGroup.LayoutParams lp = v.getLayoutParams();
			lp.width = myWidth - 2;
			lp.height = (int) ((((float) myWidth / myHeight) / ((float) sfvWidth / sfvHeight)) * myHeight);
			toStandFullLl_add_height(lay_wait2, lp.width + 2, lp.height + 2);
			v.setLayoutParams(lp);
			} else {
			ViewGroup.LayoutParams lp = v.getLayoutParams();
			lp.width = myWidth - 2;
			lp.height = myHeight - 2;
			v.setLayoutParams(lp);
			}
			}
			}
	    
	  //-------------------------------顶部底部按键-----------------------------
	    private View.OnClickListener topButtonClickListener = new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            switch (v.getId()){
	                case R.id.btn_return:
	                    finish();
	                    break;
	                case R.id.play_btn_select_preset:
	                   
	                    break;
	                case R.id.play_btn_select_video:
	                    
	                    break;
	                case R.id.play_btn_cruise:
	                   
	                    break;
	                
	                case R.id.button_alarm:
	                	//本地 布撤防
	                	if(alarm_connect>0){
	                		if(alarm_status==0){
	                			onvif_c2s.c2d_setLocalAlarm_fun(alarm_connect, 0, 1);
	                		}else{
	                			onvif_c2s.c2d_setLocalAlarm_fun(alarm_connect, 0, 0);
	                		}
	                	}else{
	                		ToastUtils.show(mContext, getString(R.string.cam_no_connecting));
	                	}
	                	break;
	            }
	        }
	    };
	    
	    
	    private View.OnClickListener bottomButtonClickListener = new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            switch (v.getId()){
	                case R.id.player_ImageButton_sound:
	                    //预览声音开关按键
	                    if (cPlayParams != null) {
	                        if (cPlayParams.ifAudio == 1) {
	                            if (cPlayParams.audioStatu == 1) {
	                            	cPlayParams.audioStatu = 0;
	                                setAudioStatus(0);
	                                //stopAudio(getViewId(), params.audioStatu);
	                            } else {
	                            	cPlayParams.audioStatu = 1;
	                                setAudioStatus(1);
	                                //startAudio(getViewId(), params.audioStatu);
	                            }
	                        } else {
	                            setSoundImage(0);
	                            //audioToNospeak();
	                        }
	                    }
	                    break;
	                case R.id.player_imageButton_speak:
	                    //对讲按键
	                    talkLlToVisible();
	                    break;
	                case R.id.player_ImageButton_camera:
	                    //拍照按键
	                    doScreenshot();
	                    break;
	                case R.id.player_ImageButton_record:
	                    //录像按键
	                    if (cPlayParams != null) {
	                        if (cPlayParams.ifVideo) {
	                            doStopVideo(cPlayParams);
	                        } else {
	                            doStartVideo(cPlayParams);
	                        }
	                    }
	                    break;
	                case R.id.player_ImageButton_playback:
	                	GetDevInfoBean info = app.getDevInfo(cPlayParams.dev_id);
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
						Intent intent_playback = new Intent();
		                intent_playback.setClass(mContext, VideoPlayBackActivityEx.class);
		                intent_playback.putExtra("item", cPlayParams);
		                startActivity(intent_playback);
	                	finish();
	                	break;
	            }
	        }
	    };

	    
	    
	    /**
	     * 录像命名规则fishtype_x1_x2_y1_y2_datetime_video.vveye
	     * 录像缩略图命名规则datetime_video.jpg
	     * @param cpp
	     */
	    //----------------------------录像相关-------------------------------
	    private void doStartVideo(CPlayParams cpp) {
	        if (cpp != null) {
	            String time = new SimpleDateFormat("yyyy.MM.dd")
	                    .format(new java.util.Date());
	            String videoPath = cft.getCatchPicturePath() + time + "/";
	            if (FileUtils.makeFolders(videoPath)) {
	            } else {

	            }
	            long strdate = System.currentTimeMillis();
	            String rec_fileName;
	            String rec_snapshotName;
	                rec_fileName = videoPath +
	                        ("" + cpp.fishEyeInfo.fishType
	                +"_"+ cpp.fishEyeInfo.main_x1_offsize +"_"+ cpp.fishEyeInfo.main_x2_offsize +"_"+ cpp.fishEyeInfo.main_y1_offsize +"_"+ cpp.fishEyeInfo.main_y2_offsize
	                +"_"+strdate + StaticConstant.VIDEO_SUFFIX);

	            rec_snapshotName = videoPath +
	                    ("" + cpp.fishEyeInfo.fishType
	                            +"_"+ cpp.fishEyeInfo.main_x1_offsize +"_"+ cpp.fishEyeInfo.main_x2_offsize +"_"+ cpp.fishEyeInfo.main_y1_offsize +"_"+ cpp.fishEyeInfo.main_y2_offsize
	                            +"_"+strdate + StaticConstant.VIDEOPIC_SUFFIX);
	            int startResult = onvif_c2s.playerStartRec(cpp.dev_id, cpp.chl_id,
	                    rec_fileName, rec_snapshotName,0);
	            if (startResult == 0) {
	                cpp.ifVideo = true;
	                setBtnDoVideo();
	            } else {
	                ToastUtils.show(mContext, getString(R.string.video_record_start_faild));
	            }
	        }
	    }

	    private void doStopVideo(CPlayParams cpp) {
	        if (cpp != null) {
	            if (getVideoState(cpp) == 1) {
	                int stopResult = onvif_c2s.playerStopRec(cpp.dev_id, cpp.chl_id);
	                if (stopResult == 0) {
	                    cpp.ifVideo = false;
	                    setBtnUnVideo();
	                } else {
	                    ToastUtils.show(mContext, getString(R.string.video_record_stop_faild));
	                }
	            } else {
	                cpp.ifVideo = false;
	            }
	        }
	    }

	    /**
	     * 获取录像状态
	     *
	     * @param cpp
	     * @return 0:没有在录像   1:在录像
	     */
	    private int getVideoState(CPlayParams cpp) {
	        return cpp == null ? -1 : onvif_c2s.playerIsRecording(cpp.dev_id, cpp.chl_id);
	    }

	    private void setBtnUnVideo() {
	        player_ImageButton_record.setImageResource(R.drawable.png_cam_white);
	        record_text.clearAnimation();
	        record_text.setVisibility(View.GONE);
	    }

	    private void setBtnDoVideo() {
	        player_ImageButton_record.setImageResource(R.drawable.png_playback_stop2);
	        Animation animation = AnimationUtils.loadAnimation(this, R.anim.blin_action);
	        record_text.startAnimation(animation);
	        record_text.setVisibility(View.VISIBLE);
	    }
	    //----------------------------录像相关-------------------------------
	    

	    
	  //-------------------------------速率相关-------------------------------
	    private void visiblePortSpeed() {
	        linearLayoutSpeed.setVisibility(View.VISIBLE);
	        linearLayoutSpeed2.setVisibility(View.GONE);
	    }

	    private void visibleLandSpeed() {
	        linearLayoutSpeed.setVisibility(View.INVISIBLE);
	        linearLayoutSpeed2.setVisibility(View.VISIBLE);
	    }


	    private void upddateSpeed(String nowSpeed, String allFlow) {
	        if (!isLand) {
	            textNowSpeed.setText(getResources().getString(R.string.now_speed) + nowSpeed + " KB/S");
	            textAllFlow.setText(getResources().getString(R.string.all_flow) + allFlow + " MB");
	        }else{
	            textNowSpeed2.setText(getResources().getString(R.string.now_speed) + nowSpeed + " KB/S");
	            textAllFlow2.setText(getResources().getString(R.string.all_flow) + allFlow + " MB");
	        }
	    }

	    Timer mFlowTimer = null;
	    private boolean ifGetFlow = true;

	    private void startFlowTimer() {
	        if (mFlowTimer != null || !ifGetFlow) {
	            return;
	        }
	        mFlowTimer = new Timer();
	        mFlowTimer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {
	                            //单屏模式下，设置鱼眼摄像头OSD
	                            if(cPlayParams.fishEyeInfo.fishType!=0&&ifPlayOk==1) {
	                                //设置OSD
	                                long dateTime = cplayerEx.getFrameLocalTime();
	                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
	                                        "yyyy-MM-dd HH:mm:ss");
	                                Log.e("DEBUG","dateTime  "+dateTime);
	                                String time = simpleDateFormat.format(dateTime * 1000- TimeZone.getDefault().getRawOffset());
	                                sfvv2.getFishOSD().setText(time);
	                                sfvv2.getFishOSD().setVisibility(View.VISIBLE);
	                        }
	                        if (cPlayParams != null) {
	                            CCStreamStatParams paramter = onvif_c2s.playerGetStreamStat(onvif_c2s.getPlayHandler(cPlayParams.dev_id, cPlayParams.chl_id));
	                            if (paramter != null) {
	                                upddateSpeed(String.format("%.2f", (double) paramter.avg / 1024.0), String.format("%.2f", (double) paramter.total / 1024.0 / 1024.0));
	                            }
	                        }
	                        cancleFlowTimer();
	                        if (ifGetFlow) {
	                            startFlowTimer();
	                        }
	                    }
	                });
	            }
	        }, 1000);
	    }

	    private void cancleFlowTimer() {
	        if (mFlowTimer != null) {
	            mFlowTimer.cancel();
	            mFlowTimer = null;
	        }
	    }
	    //-------------------------------速率相关-------------------------------
	    
	  //----------------------------P2P连接相关-------------------------------
	    PpviewClientInterface.OnDevConnectCallbackListener devConnectCallbackListener = new PpviewClientInterface.OnDevConnectCallbackListener() {

	        @Override
	        public void on_connect_callback(final int msgid,final long connector,final int result) {
	            if (result == 1 && msgid == 256) {
	                runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {

	                            if (cPlayParams.longConnect == connector) {
	                            	cPlayParams.ifCloud = true;
	                            }
	                            if (cPlayParams.longTalk == connector && !cPlayParams.ifTalk) {
	                            	cPlayParams.ifTalk = true;
                                    Log.e("DEBUG","Connect UUID "+cPlayParams.dev_id+"    TalkType "+cPlayParams.ifp2ptalk+"   CanTalk "+cPlayParams.ifTalk);
                                    setTalkStatus(cPlayParams.ifp2ptalk,cPlayParams.ifTalk);
	                            }
	                            
	                            
	                            if(alarm_connect == connector){
	                            	onvif_c2s.c2d_getLocalAlarm_fun(alarm_connect, 0);
	                            }

	                    }
	                });

	            }
	        }
	    };
	    //----------------------------P2P连接相关-------------------------------

	    
	    
		  //----------------------------拍照截图相关-------------------------------
	    private Bitmap snapBmp = null;
	    private ImageLoader_local m_image_loader = null;
	    private boolean isSnap = false;
	    private ImageLoader m_imageLoader = null;
	    private Dialog importDialog = null;
	    private View importView = null;

	    private void get_cam_pic(String camid) {
	        final CCommonParams cp = cplayerEx.getCaptureData();
	        if (cp == null) {
	            return;
	        }

	        BitmapFactory.Options options = new BitmapFactory.Options();
	        int i = cPlayParams.chl_width / 128;
	        if (i > 1) {
	        } else {
	            i = 1;
	        }
	        options.inSampleSize = i;
	        snapBmp = BitmapFactory.decodeByteArray(cp.data, 0, cp.len, options);
	        m_image_loader.saveBitmap(snapBmp, camid);
	        isSnap = true;
	    }


	    private boolean doGetBitmap(int height, String fileName) {
	        // cp = screens.getCaptureData(getViewId());
	    	Log.e("DEBUG", "get bitmap "+fileName);
	        int i_cp = cplayerEx.getCaptureFile(fileName);
	        if (i_cp != 0) {
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                    ProgressDialogUtil.getInstance().cancleDialog();
	                    cancleImportDialog();
	                    ToastUtils.show(mContext,getString(R.string.photot_fail));
	                }
	            });
	            return false;
	        }
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        int i = cPlayParams.chl_width / height;
	        if (i > 1) {
	        } else {
	            i = 1;
	        }
	        options.inSampleSize = i;
	        snapBmp = BitmapFactory.decodeFile(fileName, options);
	        // bm = BitmapFactory.decodeByteArray(cp.data, 0, cp.len, options);
	        if (snapBmp == null) {
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                    ProgressDialogUtil.getInstance().cancleDialog();
	                    cancleImportDialog();
	                    ToastUtils.show(mContext,getString(R.string.photot_fail));
	                }
	            });
	            return false;
	        }
	        return true;
	    }

	    private void doScreenshot() {
	        if (cft.IsCanUseSdCard()) {
	            ProgressDialogUtil.getInstance().showDialog(mContext,getString(R.string.get_picture));
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
	                    String bitId = "" + System.currentTimeMillis();
	                    CPlayParams params = cPlayParams;
	                    final String filePath = cft.getCatchPicturePath() + strdate
	                            + "/" + + params.fishEyeInfo.fishType
	                            +"_"+ params.fishEyeInfo.main_x1_offsize +"_"+ params.fishEyeInfo.main_x2_offsize +"_"+ params.fishEyeInfo.main_y1_offsize +"_"+ params.fishEyeInfo.main_y2_offsize
	                    +"_"+bitId + "_jpg.jpg";
	                    if (doGetBitmap(320, filePath)) {
	                        runOnUiThread(new Runnable() {
	                            @Override
	                            public void run() {
	                                ProgressDialogUtil.getInstance().cancleDialog();
	                                SaveOrDeletePic(filePath);
	                            }
	                        });
	                    }
	                }
	            }).start();
	        } else {
	            ToastUtils.show(mContext,getString(R.string.sdcard_not_use));
	            //showMessage(this.getResources().getString(R.string.sdcard_not_use));
	        }
	    }

	    private boolean isSaveImage = false;
	    private void SaveOrDeletePic(String filepath) {
	        final String filePath = filepath;
	        importDialog = new Dialog(this, R.style.style_dlg_groupnodes);
	        isSaveImage = false;
	        importDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
	            @Override
	            public void onDismiss(DialogInterface dialog) {
	                if(isSaveImage){
	                    Intent intent = new Intent(
	                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	                    Uri uri = Uri.fromFile(new File(filePath));
	                    intent.setData(uri);
	                    PlayActivity.this.sendBroadcast(intent);
	                    ToastUtils.show(mContext,getString(R.string.save_ok));
	                }else{
	                    File my_file = new File(filePath);
	                    if (my_file.exists()) {
	                        my_file.delete();
	                    }
	                }
	            }
	        });
	        importView = View.inflate(this, R.layout.dlg_photo, null);
	        // -----
	        ImageView dlg_photo_img = (ImageView) importView
	                .findViewById(R.id.dlg_photo_img);
	        dlg_photo_img.setImageBitmap(snapBmp);
	        Button dlg_rename_btn_ok = (Button) importView
	                .findViewById(R.id.dlg_rename_btn_ok);
	        dlg_rename_btn_ok.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                // v.getTag();
	                isSaveImage = true;
	                cancleImportDialog();

	            }
	        });
	        Button dlg_rename_btn_cancle = (Button) importView
	                .findViewById(R.id.dlg_rename_btn_cancle);
	        dlg_rename_btn_cancle.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                isSaveImage = false;
	                cancleImportDialog();
	            }
	        });

	        importDialog.setContentView(importView);
	        importDialog.setCanceledOnTouchOutside(false);
	        importDialog.show();
	    }

	    private void cancleImportDialog() {
	        if (snapBmp != null && !snapBmp.isRecycled()) {
	            snapBmp.recycle();
	        }
	        if (importDialog != null) {
	            importDialog.cancel();
	        }
	    }
	    //----------------------------拍照截图相关-------------------------------
	    
	    
	    
	  //----------------------------音频对讲控制相关-------------------------------

	    /**
	     * 音频按键图表设置
	     * @param state
	     * 0关闭音频播放 1打开音频播放
	     */
	    private void setSoundImage(int state){
	        switch (state){
	            case 0:
	                player_ImageButton_sound.setImageResource(R.drawable.png_sound1);
	                break;
	            case 1:
	                player_ImageButton_sound.setImageResource(R.drawable.png_sound1_orange);
	                break;
	        }
	    }


	    View.OnTouchListener BottomTouch = new View.OnTouchListener() {

	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                    if (talkType == 4) {
	                        play_talk_btn.setBackgroundResource(R.color.talk_green);
	                        VVAudio.setTalkMode(VVAudio.MODE_STARTREC);
	                        play_text_speak.setText(mContext
	                                .getResources().getString(R.string.stop_talk));
	                    }
	                    break;
	                case MotionEvent.ACTION_UP:
	                    if (talkType == 4) {
	                        play_talk_btn.setBackgroundResource(R.color.talk_gray);
	                        VVAudio.setTalkMode(VVAudio.MODE_STARTPLAY);
	                        play_text_speak.setText(mContext
	                                .getResources().getString(R.string.press_to_talk));
	                    }
	                    break;
	            }
	            return true;
	        }
	    };
	    // -----对讲回调,用于获取对讲状态
	    OnVoiceTalkCallbackListener ctCallback = new OnVoiceTalkCallbackListener() {

	        @Override
	        public void OnVoiceTalkCallback(final int ct_status) {
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                    if (ct_status == 1) {
	                        if (talkType == 4) {
	                            play_text_speak.setText(mContext
	                                    .getResources().getString(R.string.press_to_talk));
	                            play_talk_btn.setOnTouchListener(BottomTouch);
	                        } else {
	                            play_text_speak.setText(mContext
	                                    .getResources().getString(R.string.talking));
	                            play_talk_btn.setOnTouchListener(null);
	                        }
	                    } else {
	                        if (talkType == 4) {
	                            VVAudio.setTalkMode(VVAudio.MODE_RECANDPLAY);
	                        }
	                        play_text_speak.setText(mContext
	                                .getResources().getString(R.string.talkback_fail)
	                                + ":" + ct_status);
	                    }
	                }
	            });
	        }

	    };
	    /**
	     * APP端开始允许对讲，停止放音
	     */
	    private void startToTalk(CPlayParams params) {
	        if (ifTalk ) {
	            if (params.ifp2ptalk == 2 || params.ifp2ptalk == 4) {
	                if (params.ifTalk && params.longTalk > 0) {
	                    talkType = params.ifp2ptalk;
	                    if (params.ifp2ptalk == 4) {
	                        VVAudio.setTalkMode(VVAudio.MODE_STARTPLAY);
	                    }
	                    VVAudio.VVTalkStart(params.longTalk, params.chl_id,
	                            params.user, params.pass);
	                } else if (!params.ifTalk || params.longTalk == 0) {
	                    play_text_speak.setText(mContext
	                            .getString(R.string.talkback_fail));
	                    params.longTalk = onvif_c2s.createConnect(params.dev_id,
	                            params.user, params.pass);
	                }
	            }
	        }
	    }

	    /**
	     * APP端暂停对讲，可以放音
	     */
	    private void stopToTalk(CPlayParams params) {
	        if (ifTalk) {
	            if (params.ifp2ptalk == 2 || params.ifp2ptalk == 4) {
	                if (params.ifTalk) {
	                    VVAudio.VVTalkStop();
	                    if (params.ifp2ptalk == 4) {
	                        VVAudio.setTalkMode(VVAudio.MODE_RECANDPLAY);
	                        play_talk_btn.setOnTouchListener(null);
	                    }
	                }
	            }
	        }
	    }

	    private void talkLlToVisible() {
	        if (cPlayParams.ifp2ptalk == 0 || !cPlayParams.ifTalk) {
	            return;
	        }
	        ifTalk = true;
	        play_talk_ll.setVisibility(View.VISIBLE);
	        bottomButtonLayout2.setVisibility(View.GONE);
	        play_text_speak.setText(mContext.getResources().getString(
	                R.string.ctalk_connecting));
	        startToTalk(cPlayParams);
	    }

	    private void talkLlToGone() {
	        stopToTalk(cPlayParams);
	        ifTalk = false;
	        play_talk_ll.setVisibility(View.GONE);
	        bottomButtonLayout2.setVisibility(View.VISIBLE);
	    }

	    /**
	     * 修改对讲图标
	     * @param talk_type
	     * 0不能对讲
	     * @param ifCanTalk
	     * false 不能对讲 true可以对讲
	     */
	    private void setTalkStatus(int talk_type, boolean ifCanTalk) {
	        if (talk_type == 0 || !ifCanTalk) {
	            player_imageButton_speak.setImageResource(R.drawable.png_nospeak);
	        } else {
	            player_imageButton_speak.setImageResource(R.drawable.png_speak);
	        }
	    }


	    /**
	     * 设置音频监听的状态,打开或关闭
	     * @param status
	     * 状态,0:关闭 1：打开
	     */
	    private void setAudioStatus(int status){
	        cplayerEx.setAudioStatus(status);
	        setSoundImage(status);
	    }

	    /**
	     * 修改音频监听状态，打开或关闭，此功能必须要设置音频状态为打开后执行
	     * @param status
	     * 状态,0:关闭 1：打开
	     */
	    private void changeAudioStatus( int status){
	        if (cPlayParams.ifAudio == 1) {
	            if (cPlayParams.audioStatu == 1) {
	                setAudioStatus(1);
	            }
	        }
	    }

	    //----------------------------音频对讲控制相关-------------------------------

}
