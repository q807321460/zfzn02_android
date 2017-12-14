package com.jia.ezcamera.set;



import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.SelectDialog;
import com.jia.ezcamera.utils.StringUtils;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import vv.ppview.PpviewClientInterface;
import vv.tool.gsonclass.item_alarm_cfg;


public class CameraAfsActivity extends Activity {
	public static final String TAG = CameraAfsActivity.class.getSimpleName();
	private static final int IMAGEVIEWS_INDEX = 0;
    private static final int IMAGEVIEWS_INDEX1 = IMAGEVIEWS_INDEX + 1;
    private static final int IMAGEVIEWS_INDEX2 = IMAGEVIEWS_INDEX1 + 1;
    private static final int IMAGEVIEWS_INDEX3 = IMAGEVIEWS_INDEX2 + 1;
    private static final int IMAGEVIEWS_INDEX4 = IMAGEVIEWS_INDEX3 + 1;
    private static final int IMAGEVIEWS_INDEX5 = IMAGEVIEWS_INDEX4 + 1;
    private static final int IMAGEVIEWS_INDEX6 = IMAGEVIEWS_INDEX5 + 1;

    private static final int ALARM_PIC1 = 1;
    private static final int ALARM_PIC2 = ALARM_PIC1 + 1;
    private static final int ALARM_PIC3 = ALARM_PIC2 + 1;

    private static final int ATC0 = 0;
    private static final int ATC1 = ATC0 + 1;
    private static final int ATC2 = ATC1 + 1;
    private static final int ATC3 = ATC2 + 1;

    private static final int TIME0 = 0;
    private static final int TIME15 = 15;
    private static final int TIME30 = TIME15 * 2;
    private static final int TIME45 = TIME15 * 3;
    private static final int TIME60 = TIME30 * 2;
    private static final int TIME120 = TIME60 * 2;
    private static final int TIME180 = TIME60 * 3;
    public static final int START_TIMER_ACTIVITY=105;
    public static final String START_TIME="start_time";
    public static final String END_TIME="end_time";
    
    private String startTime="";
    private String endTime="";

    private ImageButton btn_back;
    private Context mContext = this;
    private ImageView afs_timing_alarm,afs_motion_detection,afs_alert_sound;
    private TextView afs_set_alarm_catch_text;
    private TextView afs_set_alarm_time_text;
    private TextView afs_alarm_linkage_text;
    private TextView afs_set_time_starttext;
    private TextView afs_set_time_endtext;
    
    private LinearLayout afs_timing_layout;
    
    private RelativeLayout afs_set_time_startlayout;
    private RelativeLayout afs_set_time_endlayout;
    private RelativeLayout afs_set_alarm_catch;
    private RelativeLayout afs_set_alarm_time;
    private RelativeLayout afs_alarm_linkage;
    private RelativeLayout alarm_sensor_set;
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    private item_alarm_cfg mySecurityItem;
    private item_alarm_cfg setSecurityItem;
    
    private List<TextView> dayList;
    private MainApplication app;
    private String devid;
   	private GetDevInfoBean devInfo;
   	private int chl_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cam_afs);
        devid = getIntent().getStringExtra("devid");
		app = (MainApplication) getApplication();
		devInfo = app.getDevInfo(devid);
        init();
        onvif_c2s.setOnC2dAlarmCfgCallback(onC2dAlarmCfgCallback);
        if(app.checkSetCamConnect()){
        	onvif_c2s.c2d_getAlarmCfg_fun(app.SetCamConnector, chl_id);
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i(TAG,"onActivityResult     requestCode="+requestCode+"     resultCode="+resultCode+"     Activity.RESULT_OK="+Activity.RESULT_OK);
        if (requestCode==START_TIMER_ACTIVITY&&data!=null){
            startTime=data.getStringExtra(START_TIME);
            endTime=data.getStringExtra(END_TIME);
            setSecurityItem.sun= StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.mon = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.thu = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.wed = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.tue = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.fri = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.sat = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            doSetInfo();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        afs_timing_alarm = (ImageView)findViewById(R.id.afs_timing_alarm);
        afs_motion_detection = (ImageView)findViewById(R.id.afs_motion_detection);
        afs_alert_sound = (ImageView)findViewById(R.id.afs_alert_sound);
        afs_set_alarm_catch_text = (TextView)findViewById(R.id.afs_set_alarm_catch_text);
        afs_set_alarm_time_text = (TextView)findViewById(R.id.afs_set_alarm_time_text);
        afs_alarm_linkage_text = (TextView)findViewById(R.id.afs_alarm_linkage_text);
        afs_set_time_starttext = (TextView)findViewById(R.id.afs_set_time_starttext);
        afs_set_time_endtext = (TextView)findViewById(R.id.afs_set_time_endtext);
        
        afs_timing_layout = (LinearLayout)findViewById(R.id.afs_timing_layout);
        
        afs_set_time_startlayout = (RelativeLayout)findViewById(R.id.afs_set_time_startlayout);
        afs_set_time_endlayout = (RelativeLayout)findViewById(R.id.afs_set_time_endlayout);
        afs_set_alarm_catch = (RelativeLayout)findViewById(R.id.afs_set_alarm_catch);
        afs_set_alarm_time = (RelativeLayout)findViewById(R.id.afs_set_alarm_time);
        afs_alarm_linkage = (RelativeLayout)findViewById(R.id.afs_alarm_linkage);
       // if(cam_item.m_ptz==0){
            afs_alarm_linkage.setVisibility(View.GONE);
     //   }
        alarm_sensor_set = (RelativeLayout)findViewById(R.id.alarm_sensor_set);
        
        btn_back.setOnClickListener(otherOnClickListener);
        afs_timing_alarm.setOnClickListener(setOnClickListener);
        afs_motion_detection.setOnClickListener(setOnClickListener);
        afs_alert_sound.setOnClickListener(setOnClickListener);
        afs_set_time_startlayout.setOnClickListener(setOnClickListener);
        afs_set_time_endlayout.setOnClickListener(setOnClickListener);
        afs_set_alarm_catch.setOnClickListener(setOnClickListener);
        afs_set_alarm_time.setOnClickListener(setOnClickListener);
        afs_alarm_linkage.setOnClickListener(setOnClickListener);
        alarm_sensor_set.setOnClickListener(otherOnClickListener);
        
        findViewById(R.id.v_mon).setOnClickListener(setOnClickListener);
        findViewById(R.id.v_fri).setOnClickListener(setOnClickListener);
        findViewById(R.id.v_sat).setOnClickListener(setOnClickListener);
        findViewById(R.id.v_sun).setOnClickListener(setOnClickListener);
        findViewById(R.id.v_thu).setOnClickListener(setOnClickListener);
        findViewById(R.id.v_tue).setOnClickListener(setOnClickListener);
        findViewById(R.id.v_wed).setOnClickListener(setOnClickListener);
        
        dayList = new ArrayList<TextView>();
        dayList.add((TextView)findViewById(R.id.v_sun));
        dayList.add((TextView)findViewById(R.id.v_mon));
        dayList.add((TextView)findViewById(R.id.v_tue));
        dayList.add((TextView)findViewById(R.id.v_wed));
        dayList.add((TextView)findViewById(R.id.v_thu));
        dayList.add((TextView)findViewById(R.id.v_fri));
        dayList.add((TextView)findViewById(R.id.v_sat));
        
    }
    OnClickListener otherOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_return:
				finish();
				break;
				
			case R.id.alarm_sensor_set:
            	Intent intent = new Intent(mContext, CameraSensorActivity.class);
                intent.putExtra("devid", devid);
            	mContext.startActivity(intent);
                break;

			default:
				break;
			}
		}
    	
    };
    
    OnClickListener setOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
        	if(!checkInfo()){
        		return;
        	}
            switch (v.getId()) {
                case R.id.afs_timing_alarm:
                	if (mySecurityItem.timing_arm_enabled==0){
                        setSecurityItem.timing_arm_enabled=1;
                    }else{
                        setSecurityItem.timing_arm_enabled=0;
                    }
                	doSetInfo();
                    break;
                case R.id.afs_motion_detection:
                	if (mySecurityItem.motion_enabled==0){
                        setSecurityItem.motion_enabled=1;
                    }else{
                        setSecurityItem.motion_enabled=0;
                    }
                	doSetInfo();
                    break;
                case R.id.afs_alert_sound:
                    if (mySecurityItem.alert_sound==0||mySecurityItem.alert_sound==1){
                        setSecurityItem.alert_sound=2;
                    }else{
                        setSecurityItem.alert_sound=1;
                    }
                    doSetInfo();
                    break;
                case R.id.afs_set_time_startlayout:
                	doStartSetTimerActivity();
                    break;
                case R.id.afs_set_time_endlayout:
                	doStartSetTimerActivity();
                    break;
                case R.id.afs_set_alarm_catch:
                	showCatchPageDialog();
                    break;
                case R.id.afs_set_alarm_time:
                	showAlarmTimeDialog();
                    break;
                case R.id.afs_alarm_linkage:
                	showLinkageDialog();
                    break;
                case R.id.v_sun:
                    replaceString(IMAGEVIEWS_INDEX);
                    doSetInfo();
                    break;
                case R.id.v_mon:
                    replaceString(IMAGEVIEWS_INDEX1);
                    doSetInfo();
                    break;
                case R.id.v_tue:
                    replaceString(IMAGEVIEWS_INDEX2);
                    doSetInfo();
                    break;
                case R.id.v_wed:
                    replaceString(IMAGEVIEWS_INDEX3);
                    doSetInfo();
                    break;
                case R.id.v_thu:
                    replaceString(IMAGEVIEWS_INDEX4);
                    doSetInfo();
                    break;
                case R.id.v_fri:
                    replaceString(IMAGEVIEWS_INDEX5);
                    doSetInfo();
                    break;
                case R.id.v_sat:
                    replaceString(IMAGEVIEWS_INDEX6);
                    doSetInfo();
                    break;
                default:
                    break;
            }

        }
    };
    
    
    private boolean checkInfo(){
        if (mySecurityItem!=null){
             return true;
        }
        ToastUtils.show(mContext,R.string.afs_get_info_faild);
        return false;
    }

    private void doSetInfo(){
    	if(app.checkSetCamConnect()&&checkInfo()){
	        if (devInfo!=null&&setSecurityItem!=null)
	            onvif_c2s.c2d_setAlarmCfg_fun(app.SetCamConnector,chl_id,setSecurityItem);
    	}
    }
    
    private void getOrSetResult(int result,item_alarm_cfg item){
        if (result == 200) {
            mySecurityItem = item;
            setSecurityItem=mySecurityItem;
            refreshActivity();
        }
    }

    private void refreshActivity() {
        if (mySecurityItem != null) {
            //-----
            if (mySecurityItem.timing_arm_enabled == 0) {
            	afs_timing_alarm.setImageResource(R.drawable.switch_off);
            	afs_timing_layout.setVisibility(View.GONE);
            } else if (mySecurityItem.timing_arm_enabled == 1) {
            	afs_timing_alarm.setImageResource(R.drawable.switch_on);
            	afs_timing_layout.setVisibility(View.GONE);
            }
            
            if (mySecurityItem.motion_enabled == 0) {
            	afs_motion_detection.setImageResource(R.drawable.switch_off);
            } else if (mySecurityItem.motion_enabled == 1) {
            	afs_motion_detection.setImageResource(R.drawable.switch_on);
            }

            if(mySecurityItem.alert_sound == 2 ){
                afs_alert_sound.setImageResource(R.drawable.switch_off);
            }else{
                afs_alert_sound.setImageResource(R.drawable.switch_on);
            }
            //-----报警图片
            int armSize=mySecurityItem.arm_snap;
            String armString = armSize+getString(R.string.page);
            afs_set_alarm_catch_text.setText(armString);
            //-----云台方式
            int controlIndex = R.string.none;
            int actSize=mySecurityItem.alert_act;
            if (actSize==ATC1) {
                controlIndex = R.string.afs_cruise_up_down;
            }
            else if (actSize==ATC2) {
                controlIndex = R.string.afs_cruise_left_right;
            }
            else if (actSize==ATC3) {
                controlIndex = R.string.afs_cruise_preset;
            }
            afs_alarm_linkage_text.setText(getString(controlIndex));
            //-----录像持续时间
            int alartTime=mySecurityItem.alert_time;
            String alartTimeString = alartTime+getString(R.string.sec);
            afs_set_alarm_time_text.setText(alartTimeString);
            
            String times=mySecurityItem.sun;
            if (!TextUtils.isEmpty(times)){
                String[] allTime=times.split(";");
                if (allTime!=null&&allTime.length>0){
                    String myTime=allTime[0];
                    String[] dayTimes=myTime.split("-");
                    if (dayTimes!=null&&dayTimes.length==2) {
                        startTime=dayTimes[0];
                        endTime=dayTimes[1];
                        afs_set_time_starttext.setText(StringUtils.formatAllTime(startTime));
                        afs_set_time_endtext.setText(StringUtils.formatAllTime(endTime));
                    }
                }
            }
            //-----
            String strRepeat=mySecurityItem.timing_arm;
            if (!TextUtils.isEmpty(strRepeat)&&StringUtils.isNumeric(strRepeat)){
                int charSize=strRepeat.length();
                for (int i=0;i<charSize;i++){
                    setDays(i,Integer.parseInt(strRepeat.substring(i, i + 1)));
                }
            }
        }
    }
    
    

    private void setDays(int index,int status){
        int daySize=dayList.size();
        if (index>=0&&index<daySize){
            if (status==1){
                dayList.get(index).setBackgroundResource(R.color.blue_green);
            }else{
                dayList.get(index).setBackgroundResource(R.drawable.week_bc);
            }
        }
    }

    private void replaceString(int index){
        String strRepeat=mySecurityItem.timing_arm;
        if (!TextUtils.isEmpty(strRepeat)) {
        	if(!StringUtils.isNumeric(strRepeat)){
        		strRepeat = "0000000";
        	}
            char[] strChar=strRepeat.toCharArray();
            if (strChar!=null){
                int charSize=strChar.length;
                if (charSize>0&&index>=0&&index<charSize){
                    StringBuffer sb = new StringBuffer();
                    for (int i=0;i<charSize;i++){
                        int resut=Integer.parseInt(String.valueOf(strChar[i]));
                        if (index==i){
                            resut=(resut==0?1:0);
                        }
                        sb.append(resut);
                    }
                    setSecurityItem.timing_arm=sb.toString();
                    setSecurityItem.sun= mySecurityItem.sun;
                    setSecurityItem.mon = mySecurityItem.sun;
                    setSecurityItem.thu = mySecurityItem.sun;
                    setSecurityItem.wed = mySecurityItem.sun;
                    setSecurityItem.tue = mySecurityItem.sun;
                    setSecurityItem.fri = mySecurityItem.sun;
                    setSecurityItem.sat = mySecurityItem.sun;
                }
            }
        }
    }
    
    void doStartSetTimerActivity(){
        Intent intent=new Intent();
        intent.setClass(mContext,CameraRecordSetTimerActivity.class);
        intent.putExtra(START_TIME,startTime);
        intent.putExtra(END_TIME,endTime);
        startActivityForResult(intent, START_TIMER_ACTIVITY);
    }
    

    private SelectDialog selectDialog;
    private void showCatchPageDialog(){
    	ArrayList<String> list = new ArrayList<String>();
    	list.add(1+getString(R.string.page));
    	list.add(2+getString(R.string.page));
    	list.add(3+getString(R.string.page));
    	selectDialog = new SelectDialog(mContext, getString(R.string.afs_alarm_catch_count), 
    			list, mySecurityItem.arm_snap-1,new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,
    				long id) {
    			// TODO Auto-generated method stub
    			setSecurityItem.arm_snap = position+1;
    			doSetInfo();
    			selectDialog.dismiss();
    		}
    	});
    	selectDialog.show();
    }
    
    
    private void showAlarmTimeDialog(){
    	ArrayList<String> list = new ArrayList<String>();
    	list.add(15+getString(R.string.sec));
    	list.add(30+getString(R.string.sec));
    	list.add(45+getString(R.string.sec));
    	list.add(60+getString(R.string.sec));
    	list.add(120+getString(R.string.sec));
        list.add(180+getString(R.string.sec));
    	int curPositon = 0;
    	switch (mySecurityItem.alert_time) {
		case 15:
			curPositon = 0;
			break;
		case 30:
			curPositon = 1;
			break;
		case 45:
			curPositon = 2;
			break;
		case 60:
			curPositon = 3;
			break;
		case 120:
			curPositon = 4;
			break;
        case 180:
            curPositon = 5;
            break;

		default:
			curPositon = 0;
			break;
		}
    	selectDialog = new SelectDialog(mContext, getString(R.string.afs_alarm_record_time), 
    			list, curPositon , new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,
    				long id) {
    			// TODO Auto-generated method stub
    			int time = 0;
    			switch (position) {
				case 0:
					time = 15;
					break;
				case 1:
					time = 30;
					break;
				case 2:
					time = 45;
					break;
				case 3:
					time = 60;
					break;
				case 4:
					time = 120;
					break;
                case 5:
                    time = 180;
                    break;
					

				default:
					break;
				}
    			setSecurityItem.alert_time = time;
    			doSetInfo();
    			selectDialog.dismiss();
    		}
    	});
    	selectDialog.show();
    }
    
    private void showLinkageDialog(){
    	ArrayList<String> list = new ArrayList<String>();
    	list.add(getString(R.string.none));
    	list.add(getString(R.string.afs_cruise_up_down));
    	list.add(getString(R.string.afs_cruise_left_right));
    	list.add(getString(R.string.afs_cruise_preset));
    	
    	selectDialog = new SelectDialog(mContext, getString(R.string.afs_alarm_linkage),
    			list, mySecurityItem.alert_act,new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,
    				long id) {
    			// TODO Auto-generated method stub
    			setSecurityItem.alert_act = position;
    			doSetInfo();
    			selectDialog.dismiss();
    		}
    	});
    	selectDialog.show();
    }
    
    PpviewClientInterface.OnC2dAlarmCfgCallback onC2dAlarmCfgCallback = new PpviewClientInterface.OnC2dAlarmCfgCallback() {
        @Override
        public void on_c2d_getAlarmCfgCallBack(final int res, final item_alarm_cfg item_alarm_cfg) {
            Log.i(TAG, "on_c2d_getAlarmCfgCallBack    res=" + res);
            runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(res==200){
						getOrSetResult(res, item_alarm_cfg);
					}else{
						ToastUtils.show(mContext, getString(R.string.afs_get_info_faild));
					}
				}
			});
        }

        @Override
        public void on_c2d_setAlarmCfgCallBack(final int res, final item_alarm_cfg item_alarm_cfg) {
            Log.i(TAG, "on_c2d_setAlarmCfgCallBack    res=" + res);
            runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(res==200){
						getOrSetResult(res, item_alarm_cfg);
					}else{
						ToastUtils.show(mContext, getString(R.string.afs_set_info_faild));
					}
				}
			});
        }
    };
}
