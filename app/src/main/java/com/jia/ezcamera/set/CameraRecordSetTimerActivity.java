package com.jia.ezcamera.set;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jia.ezcamera.utils.StringUtils;
import com.jia.znjj2.R;


/**
 * Created by ls on 15/1/28.
 */
public class CameraRecordSetTimerActivity extends Activity {
    private static final String TAG = CameraRecordSetTimerActivity.class.getSimpleName();
    private TextView mTextStart;
    private RelativeLayout mRelativeStartTime;
    private TimePicker mTimePickerStart;
    private TextView mTextEnd;
    private RelativeLayout mRelativeEndTime;
    private TimePicker mTimePickerEnd;

    private Context myContext=null;
    private String startTime;
    private String endTime;
    private int startHour=0;
    private int startMinute=0;
    private int endHour=0;
    private int endMinute=0;


    
    OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_return:
				finish();
				break;
			case R.id.btn_sure:
				Intent intent = new Intent();
                intent.putExtra(CameraRecordTimeActivity.START_TIME, startTime);
                intent.putExtra(CameraRecordTimeActivity.END_TIME, endTime);
                CameraRecordSetTimerActivity.this.setResult(Activity.RESULT_OK, intent);
                CameraRecordSetTimerActivity.this.finish();
				break;

			default:
				break;
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cam_settimer);
        myContext=this;
        Intent intent=getIntent();
        if (intent!=null){
            startTime=intent.getStringExtra(CameraRecordTimeActivity.START_TIME);
            endTime=intent.getStringExtra(CameraRecordTimeActivity.END_TIME);
            if (!TextUtils.isEmpty(startTime)&&!TextUtils.isEmpty(endTime)) {
                String[] starts = startTime.split(":");
                if (starts.length==2){
                    startHour=Integer.parseInt(starts[0]);
                    startMinute=Integer.parseInt(starts[1]);
                }
                String[] ends = endTime.split(":");
                if (ends.length==2){
                    endHour=Integer.parseInt(ends[0]);
                    endMinute=Integer.parseInt(ends[1]);
                }
            }
//            Log.i(TAG, "onCreate     startTime=" + startTime + "    endTime=" + endTime);
        }
        init();
    }

    private void init(){
    	findViewById(R.id.btn_return).setOnClickListener(onClickListener);
    	findViewById(R.id.btn_sure).setOnClickListener(onClickListener);
    	mTextStart = (TextView)findViewById(R.id.text_start);
    	mRelativeStartTime = (RelativeLayout)findViewById(R.id.relative_start_time);
    	mTimePickerStart = (TimePicker)findViewById(R.id.timePicker_start);
    	mTextEnd = (TextView)findViewById(R.id.text_end);
    	mRelativeEndTime = (RelativeLayout)findViewById(R.id.relative_end_time);
    	mTimePickerEnd = (TimePicker)findViewById(R.id.timePicker_end);
    	mTimePickerStart.setIs24HourView(true);
    	mTimePickerEnd.setIs24HourView(true);
    	
        
        mTimePickerStart.setCurrentHour(startHour);
        mTimePickerStart.setCurrentMinute(startMinute);
        mTimePickerEnd.setCurrentHour(endHour);
        mTimePickerEnd.setCurrentMinute(endMinute);
        mTextStart.setText(StringUtils.formatAllTime(startTime));
        mTextEnd.setText(StringUtils.formatAllTime(endTime));
        mTimePickerStart.setOnTimeChangedListener(timeChangedListener);
        mTimePickerEnd.setOnTimeChangedListener(timeChangedListener);
    }

    TimePicker.OnTimeChangedListener timeChangedListener=new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            Log.i(TAG,"onTimeChanged     view="+view+"    hour="+hourOfDay+"     miune="+minute);
            switch (view.getId()){
                case R.id.timePicker_start:
                    startHour=hourOfDay;
                    startMinute=minute;
                    startTime=""+startHour+":"+startMinute;
                    mTextStart.setText(StringUtils.formatAllTime(startTime));
                    break;
                case R.id.timePicker_end:
                    endHour=hourOfDay;
                    endMinute=minute;
                    endTime=""+endHour+":"+endMinute;
                    mTextEnd.setText(StringUtils.formatAllTime(endTime));
                    break;
            }
        }
    };

//    private boolean checkStartEnd(){
//        return startHour<endHour?true:startMinute<endMinute?true:false;
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
