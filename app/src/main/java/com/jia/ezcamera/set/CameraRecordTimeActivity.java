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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.utils.StringUtils;
import com.jia.ezcamera.utils.ToastUtils;
import com.jia.znjj2.R;

import vv.tool.gsonclass.c2d_rec_schedules;
import vv.tool.gsonclass.item_rec_schedule;
import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dRectrlScheduleCallback;


public class CameraRecordTimeActivity extends Activity {
	private static final String TAG = CameraRecordTimeActivity.class.getSimpleName();
	private static final int IMAGEVIEWS_INDEX = 0;
    private static final int IMAGEVIEWS_INDEX1 = IMAGEVIEWS_INDEX + 1;
    private static final int IMAGEVIEWS_INDEX2 = IMAGEVIEWS_INDEX1 + 1;
    private static final int IMAGEVIEWS_INDEX3 = IMAGEVIEWS_INDEX2 + 1;
    private static final int IMAGEVIEWS_INDEX4 = IMAGEVIEWS_INDEX3 + 1;
    private static final int IMAGEVIEWS_INDEX5 = IMAGEVIEWS_INDEX4 + 1;
    private static final int IMAGEVIEWS_INDEX6 = IMAGEVIEWS_INDEX5 + 1;
    private ImageButton btn_back;
    private Context mContext = this;
    private TextView title,title2,startText,endText;
    private ImageView swichButton;
    private LinearLayout record_timing_layout;
    private int setType = 0; //0��ʱ¼��    1����¼��
    private c2d_rec_schedules mySchedules=null;
    private item_rec_schedule mySecurityItem=null;//��ȡ�Ķ�ʱ¼����Ϣ
    private item_rec_schedule setSecurityItem=null;//���õĶ�ʱ¼����Ϣ
    private String startTime="";
    private String endTime="";
    private List<TextView> dayList;
    public static final int START_TIMER_ACTIVITY=106;
    public static final String START_TIME="start_time";
    public static final String END_TIME="end_time";
    private MainApplication app;
    
    
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.record_set_time_swich:
                	if (mySecurityItem.enabled==0){
                        mySecurityItem.enabled=1;
                    }else{
                        mySecurityItem.enabled=0;
                    }
                	doSetInfo();
                    break;
                case R.id.record_set_time_startlayout:
                	doSetTimeActivity();
                    break;
                case R.id.record_set_time_endlayout:
                	doSetTimeActivity();
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
    private PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    private String devid;
	private GetDevInfoBean devInfo;
	private int chl_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cam_record_time);
        app = (MainApplication)getApplication();
        devid = getIntent().getStringExtra("devid");
		app = (MainApplication) getApplication();
		devInfo = app.getDevInfo(devid);
        setType = getIntent().getExtras().getInt("SetType", 0);
        init();
        onvif_c2s.setOnC2dRectrlScheduleCallback(onC2dRectrlScheduleCallback);
        if(app.checkSetCamConnect()){
        	onvif_c2s.c2d_getRectrlSchedule_fun(app.SetCamConnector,chl_id);
        }
    }

    private void init() {
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        title = (TextView)findViewById(R.id.record_set_time_title);
        title2 = (TextView)findViewById(R.id.record_set_time_title2);
        swichButton = (ImageView)findViewById(R.id.record_set_time_swich);
        startText = (TextView)findViewById(R.id.record_set_time_starttext);
        endText = (TextView)findViewById(R.id.record_set_time_endtext);
        record_timing_layout = (LinearLayout)findViewById(R.id.record_timing_layout);
        if(setType==1){
        	title.setText(R.string.record_alarm);
        	title2.setText(R.string.record_alarm);
        }
        
        btn_back.setOnClickListener(onClickListener);
        swichButton.setOnClickListener(onClickListener);
        findViewById(R.id.record_set_time_startlayout).setOnClickListener(onClickListener);
        findViewById(R.id.record_set_time_endlayout).setOnClickListener(onClickListener);
        findViewById(R.id.v_mon).setOnClickListener(onClickListener);
        findViewById(R.id.v_fri).setOnClickListener(onClickListener);
        findViewById(R.id.v_sat).setOnClickListener(onClickListener);
        findViewById(R.id.v_sun).setOnClickListener(onClickListener);
        findViewById(R.id.v_thu).setOnClickListener(onClickListener);
        findViewById(R.id.v_tue).setOnClickListener(onClickListener);
        findViewById(R.id.v_wed).setOnClickListener(onClickListener);
        
        dayList = new ArrayList<TextView>();
        dayList.add((TextView)findViewById(R.id.v_sun));
        dayList.add((TextView)findViewById(R.id.v_mon));
        dayList.add((TextView)findViewById(R.id.v_tue));
        dayList.add((TextView)findViewById(R.id.v_wed));
        dayList.add((TextView)findViewById(R.id.v_thu));
        dayList.add((TextView)findViewById(R.id.v_fri));
        dayList.add((TextView)findViewById(R.id.v_sat));
        
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i(TAG,"onActivityResult     requestCode="+requestCode+"     resultCode="+resultCode+"     Activity.RESULT_OK="+Activity.RESULT_OK);
        if (requestCode==START_TIMER_ACTIVITY&&data!=null){
            startTime= data.getStringExtra(START_TIME);
            endTime=data.getStringExtra(END_TIME);
            setSecurityItem.sun= StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.mon = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.thu = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.wed = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.tue = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.fri = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
            setSecurityItem.sat = StringUtils.formatAllTime(startTime)+"-"+StringUtils.formatAllTime(endTime);
//          Log.i(TAG,"onActivityResult      startTime="+startTime+"     endTime="+endTime+"      setSecurityItem.sun="+setSecurityItem.sun);
            doSetInfo();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void doSetTimeActivity(){
    	Intent intent = new Intent();
    	intent.setClass(mContext,CameraRecordSetTimerActivity.class);
        intent.putExtra(START_TIME,startTime);
        intent.putExtra(END_TIME,endTime);
        startActivityForResult(intent, START_TIMER_ACTIVITY);
    }
    /**
     * ���ö�ʱ¼��
     */
    private void doSetInfo(){
    	if(!app.checkSetCamConnect()){
    		return;
    	}
        if (devInfo!=null&&mySchedules!=null){
        	if(setType==0){
        		mySchedules.timing_schedule=mySecurityItem;
        	}else{
        		mySchedules.alert_schedule=mySecurityItem;
        	}
            onvif_c2s.c2d_setRectrlSchedule_fun(app.SetCamConnector, chl_id, mySchedules);
        }
    }

    private void replaceString(int index){
        String strRepeat=mySecurityItem.day_of_week_enabled;
        if (!TextUtils.isEmpty(strRepeat)) {
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
                    setSecurityItem.day_of_week_enabled=sb.toString();
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
    
    private void getOrSetResult(int result,Object item){
        mySchedules = (c2d_rec_schedules)item;
        if(setType==0){
        	mySecurityItem=mySchedules.timing_schedule;
    	}else{
    		mySecurityItem=mySchedules.alert_schedule;
    	}
        setSecurityItem=mySecurityItem;
        doRefreshActivity();
    }
    
    
    private void doRefreshActivity(){
        //-----
        if (mySecurityItem!=null) {
            if (mySecurityItem.enabled == 0) {
            	swichButton.setImageResource(R.drawable.switch_off);
            	record_timing_layout.setVisibility(View.GONE);
            } else if (mySecurityItem.enabled == 1) {
            	swichButton.setImageResource(R.drawable.switch_on);
            	record_timing_layout.setVisibility(View.VISIBLE);
            }
            //-----
            String times = mySecurityItem.sun;
            if (!TextUtils.isEmpty(times)) {
                String[] allTime = times.split(";");
                if (allTime != null && allTime.length > 0) {
                    String myTime = allTime[0];
                    String[] dayTimes = myTime.split("-");
                    if (dayTimes != null) {
                        startTime = dayTimes[0];
                        endTime = dayTimes[1];
                        startText.setText(StringUtils.formatAllTime(startTime));
                        endText.setText(StringUtils.formatAllTime(endTime));
                    }
                }
            }
            //-----
            String strRepeat = mySecurityItem.day_of_week_enabled;
            if (!TextUtils.isEmpty(strRepeat)) {
                int charSize = strRepeat.length();
                for (int i = 0; i < charSize; i++) {
                    setDays(i, Integer.parseInt(strRepeat.substring(i, i + 1)));
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
    
    
    /**
     * ��ȡ¼��ƻ��ص�
     */
	OnC2dRectrlScheduleCallback onC2dRectrlScheduleCallback = new OnC2dRectrlScheduleCallback() {
		
		@Override
		public void on_c2d_setRectrlScheduleCallBack(final int res,final c2d_rec_schedules schedule) {
			Log.i(TAG,"on_c2d_setRectrlScheduleCallBack    res="+res);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(res==200&&schedule!=null){
						getOrSetResult(res, schedule);
					}else{
						ToastUtils.show(mContext, R.string.record_getinfo_fail);
					}
				}
			});
		}
		
		@Override
		public void on_c2d_getRectrlScheduleCallBack(final int res,
				final c2d_rec_schedules schedule) {
            Log.i(TAG,"on_c2d_getRectrlScheduleCallBack     res="+res);
            runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(res==200&&schedule!=null){
						getOrSetResult(res, schedule);
					}else{
						ToastUtils.show(mContext, R.string.record_setinfo_fail);
					}
				}
			});
		}
	};

    
}
