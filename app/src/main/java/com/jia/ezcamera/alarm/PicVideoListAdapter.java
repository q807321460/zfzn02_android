package com.jia.ezcamera.alarm;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


import vv.tool.gsonclass.item_c2devents;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.jia.ezcamera.utils.StringUtils;
import com.jia.znjj2.R;


public class PicVideoListAdapter extends BaseExpandableListAdapter {
	private Activity mContext;
	private EventsListUtil eventsListUtil;
	private  ArrayList<String> groupArray ;  
	private  ArrayList<item_c2devents> childArray ; 
	private  HashMap<String, Integer> childCountArray;
	private int mode;
	private DisplayMetrics dm = new DisplayMetrics();
    private float mDensity;
	public PicVideoListAdapter(Activity context ,ArrayList<item_c2devents> events , int mode) {
		// TODO Auto-generated constructor stub
		mContext = context;
		this.mode = mode;
		eventsListUtil = new EventsListUtil(mContext,events);
		if(mode==0){
			groupArray = eventsListUtil.getPicEventGroup();
			childArray = eventsListUtil.getPicEventChild();
			childCountArray = eventsListUtil.getPicEventChildCount();
		}else{
			groupArray = eventsListUtil.getVideoEventGroup();
			childArray = eventsListUtil.getVideoEventChild();
			childCountArray = eventsListUtil.getVideoEventChildCount();
		}
		mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
		mDensity = dm.density;

	}
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupArray.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		//Log.e("DEBUG", childCountArray.get(groupPosition)+"     childCountArray  ");
		return childCountArray.get(groupArray.get(groupPosition));
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupArray.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		int position=0;
		for(int i=0;i<groupPosition;i++){
			position = position+childCountArray.get(groupArray.get(i));
		}
		position= position+childPosition;
		item_c2devents ret = childArray.get(position);
		return ret;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		int position=0;
		for(int i=0;i<groupPosition;i++){
			position = position+childCountArray.get(groupArray.get(i));
		}
		position= position+childPosition;
		return position;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View textView = getGenericGroupView(getGroup(groupPosition).toString());
        return textView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		item_c2devents event = (item_c2devents) getChild(groupPosition, childPosition);
		SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat2.setTimeZone(TimeZone.getDefault());
		String time2 = simpleDateFormat2.format(event.event_time*1000);
		View  view = null;
		if(mode == 0)
			view= getGenericChildView(time2, StringUtils.getEventTypeString(mContext, event.event_type)+":"+event.name, event.snap+"��");
		else{
			view= getGenericChildView(time2, StringUtils.getEventTypeString(mContext, event.event_type)+":"+event.name, event.rec_sec+"��");
		}
        return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	
	
	public View getGenericGroupView(String time) {
		View view= View.inflate(mContext, R.layout.item_group_pic_video,null);
        TextView timeText = (TextView)view.findViewById(R.id.picvideo_date);
        timeText.setText(time);
        return view;
    }
	
	
	public View getGenericChildView(String time,String sensor, String page) {
		View view= View.inflate(mContext, R.layout.item_pic_video,null);		
        TextView timeText = (TextView)view.findViewById(R.id.picvideo_date);
        TextView sensorText = (TextView)view.findViewById(R.id.picvideo_sensor);
        TextView pageText = (TextView)view.findViewById(R.id.picvideo_page);
        timeText.setText(time);
        sensorText.setText(sensor);
        pageText.setText(page);
		return view;
    }
	
	
	
}
