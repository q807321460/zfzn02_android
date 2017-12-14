package com.jia.ezcamera.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;


import com.jia.ezcamera.utils.CPlayParams;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.znjj2.R;

import java.util.ArrayList;


import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dEventCallback;
import vv.ppview.PpviewClientInterface.OnDevConnectCallbackListener;
import vv.tool.gsonclass.item_c2devents;


public class PicVideoActivity extends Activity {
	private static final String TAG = PicVideoActivity.class.getSimpleName();
	private final int PICTURE = 0;
	private final int VIDEO = 1;
	private int curMode = PICTURE;
	private Activity mContext;
	PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
//	private mycamItem mi = null;
    private CPlayParams mi=null;
	private long myConnector = 0;
	private ExpandableListView list;
	private PicVideoListAdapter adapter;
	private TextView title1;
	private TextView title2;
	private TextView nodata;
	private ArrayList<item_c2devents> eventsList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picvideo);
		mContext = this;
		Intent intent = getIntent();
		if (intent != null) {
			mi = (CPlayParams) intent.getSerializableExtra("item");
		}
		init();
		onvif_c2s.setOnC2dEventCallback(onC2dEventCallback);
		if (mi != null) {
			ProgressDialogUtil.getInstance().showDialog(mContext,
					mContext.getResources().getString(R.string.start_connect),true);
			onvif_c2s.setOndevConnectCallback(onMessageCallback);
			myConnector = onvif_c2s.createConnect(mi.dev_id, "admin",
					mi.pass);
			Log.e("DEBUG", "createConnect---------1   myConnector "+myConnector);
		}
	}
	
	void init(){
		list = (ExpandableListView)findViewById(R.id.picvideo_list);
		list.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//int position = (int) adapter.getChildId(groupPosition, childPosition);
				if(curMode==VIDEO){
					startAlarmVideoActivity((item_c2devents) adapter.getChild(groupPosition, childPosition));
				}
				else{
                    startActivity((item_c2devents) adapter.getChild(groupPosition, childPosition),ScanPicActivity.class);
				}
				return false;
			}
		});
		title1 = (TextView)findViewById(R.id.picvideo_title1);
		title2 = (TextView)findViewById(R.id.picvideo_title2);
		nodata = (TextView)findViewById(R.id.nodata);
		findViewById(R.id.btn_return).setOnClickListener(onClickListener);
		title2.setOnClickListener(onClickListener);

		title1.setText(R.string.alarm_pic);
		title2.setText(R.string.alarm_video);
	}

	private void startAlarmVideoActivity(item_c2devents ic){
		Intent intent = new Intent(mContext, PlayAlarmActivity.class);
        intent.putExtra("CPlayParams", mi);
        intent.putExtra("PlayTime", ic.event_time);
        intent.putExtra("AlarmTime", ic.rec_sec);
        mContext.startActivity(intent);
	}
    private void startActivity(item_c2devents ic,Class<?> clazz){
        if (ic!=null) {
            playbackItem pi=new playbackItem();
            pi.eventId=ic.event_id;
            pi.event_time=ic.event_time;
            pi.event_type=ic.event_type;
            pi.rec_sec=ic.rec_sec;
            pi.cam=mi;
            pi.snap=ic.snap;
            Intent intent = new Intent();
            intent.setClass(mContext, clazz);
            intent.putExtra("item", pi);
            intent.putExtra("connector",myConnector);
            mContext.startActivity(intent);
        }
    }

	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_return:
				finish();
				break;
			case R.id.picvideo_title2:
				if(curMode == PICTURE){
                    curMode = VIDEO;
                    title2.setText(R.string.alarm_pic);
					title1.setText(R.string.alarm_video);
					if(eventsList!=null){
						adapter = new PicVideoListAdapter(mContext, eventsList, VIDEO);
						list.setAdapter(adapter);
						if(adapter.getGroupCount()==0){
							nodata.setVisibility(View.VISIBLE);
						}else{
							nodata.setVisibility(View.GONE);
						}
					}
				}else{
                    curMode = PICTURE;
                    title1.setText(R.string.alarm_pic);
					title2.setText(R.string.alarm_video);
					if(eventsList!=null){
						adapter = new PicVideoListAdapter(mContext, eventsList, PICTURE);
						list.setAdapter(adapter);
						if(adapter.getGroupCount()==0){
							nodata.setVisibility(View.VISIBLE);
						}else{
							nodata.setVisibility(View.GONE);
						}
					}
				}
				break;

			default:
				break;
			}
			
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myConnector != 0) {
			onvif_c2s.releaseConnect(myConnector);
		}
		onvif_c2s.removeOndevConnectCallback(onMessageCallback);
	}


	OnDevConnectCallbackListener onMessageCallback = new OnDevConnectCallbackListener() {

		@Override
		public void on_connect_callback(int msgid, long connector, int result) {
			Log.i(TAG, "on_connect_callback    connector=" + connector
					+ "     msgid=" + msgid + "     result=" + result+"    myConnector"+myConnector);
			if (myConnector == connector) {
				if (result == 1 && msgid == 256) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.e("DEBUG", "createConnect---------2");
					onvif_c2s.c2d_getEventLog_fun(connector,mi.chl_id, 1);
				} else {
					//myConnector = 0;
				}
			}
		}
	};
	
	OnC2dEventCallback onC2dEventCallback = new OnC2dEventCallback() {
		@Override
		public void on_c2d_getEventLogCallBack(int res,
				final ArrayList<item_c2devents> events) {
			//Log.e(TAG,"  on_c2d_getEventLogCallBack "+res+"    events size "+events.size());
			//Log.e("DEBUG", "createConnect---------3");
			ProgressDialogUtil.getInstance().cancleDialog();
			eventsList = events;
			if(events!=null){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//Log.e(TAG, events.get(0).event_time+"");
						adapter = new PicVideoListAdapter(mContext, eventsList, PICTURE);
						list.setAdapter(adapter);
						if(adapter.getGroupCount()==0){
							nodata.setVisibility(View.VISIBLE);
						}else{
							nodata.setVisibility(View.GONE);
						}
						/*if (myConnector != 0) {
							onvif_c2s.releaseConnect(myConnector);
						}
						onvif_c2s.removeOndevConnectCallback(onMessageCallback);*/
					}
				});
				
			}
		}

        @Override
        public void on_c2d_getEventPicCallBack(int i, String s, int i2) {

        }


    };

}
