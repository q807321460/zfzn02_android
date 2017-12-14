package com.jia.ezcamera.alarm;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import vv.tool.gsonclass.item_c2devents;


import android.content.Context;
import android.util.Log;




public class EventsListUtil {
	private ArrayList<item_c2devents> events;
	private ArrayList<String> picgroup;
	private ArrayList<item_c2devents> picchild;
	private HashMap<String, Integer> picchild_count;
	//private ArrayList<Integer> picchild_count;
	private ArrayList<String> videogroup;
	private ArrayList<item_c2devents> videochild;
	private HashMap<String, Integer> videochild_count;
	private Context mContext;
	//private List<String> childItem  = new ArrayList<String>();
	
	public EventsListUtil(Context mContext,ArrayList<item_c2devents> events) {
		// TODO Auto-generated constructor stub
		this.events = events;
		this.mContext = mContext;
		//����������
		Collections.sort(events,new sortClass());  
		picgroup = new ArrayList<String>();
		picchild = new ArrayList<item_c2devents>();
		videogroup = new ArrayList<String>();
		videochild = new ArrayList<item_c2devents>();
		picchild_count = new HashMap<String, Integer>();
		videochild_count = new HashMap<String, Integer>();
		for(int i=0;i<events.size();i++){
			if(events.get(i).snap>0){
				SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
				simpleDateFormat.setTimeZone(TimeZone.getDefault());
				String time = simpleDateFormat.format(events.get(i).event_time*1000);
				picgroup.add(time);
				
				/*SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				simpleDateFormat2.setTimeZone(TimeZone.getDefault());
				String time2 = simpleDateFormat2.format(events.get(i).event_time*1000);
				String title = time2+"  "+getEventTypeString(events.get(i).event_type)+":"+events.get(i).name+"  "+events.get(i).snap;*/
				picchild.add(events.get(i));
			}
			if(events.get(i).rec_sec>0){
				SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
				simpleDateFormat.setTimeZone(TimeZone.getDefault());
				String time = simpleDateFormat.format(events.get(i).event_time*1000);
				videogroup.add(time);
				
				/*SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				simpleDateFormat2.setTimeZone(TimeZone.getDefault());
				String time2 = simpleDateFormat2.format(events.get(i).event_time*1000);
				String title = time2+"  "+getEventTypeString(events.get(i).event_type)+":"+events.get(i).name+"  "+events.get(i).rec_sec;*/
				videochild.add(events.get(i));
			}
		}
		getPicChildCount();
		getVideoChildCount();
		picgroup = singleElement(picgroup);
		videogroup = singleElement(videogroup);
		
	}
	
	private void getPicChildCount(){
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String item: picgroup){
			if(map.containsKey(item)){
				map.put(item, map.get(item).intValue() + 1);
			}else{
				map.put(item, new Integer(1));
			}
		}
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			//Log.e("DEBUG",key + ":" + map.get(key).intValue() + ", ");
			picchild_count.put(key, map.get(key).intValue());
			//picchild_count.add(map.get(key).intValue());
		}
	}
	
	private void getVideoChildCount(){
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String item: videogroup){
			if(map.containsKey(item)){
				map.put(item, map.get(item).intValue() + 1);
			}else{
				map.put(item, new Integer(1));
			}
		}
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			//System.out.print(key + ":" + map.get(key).intValue() + ", ");
			//videochild_count.add(map.get(key).intValue());
			videochild_count.put(key, map.get(key).intValue());
		}
	}
	
	
	public ArrayList<String> getPicEventGroup(){
		return picgroup;
	}
	
	public ArrayList<item_c2devents> getPicEventChild(){
		return picchild;
	}
	public HashMap<String, Integer> getPicEventChildCount(){
		return picchild_count;
	}
	
	public ArrayList<String> getVideoEventGroup(){
		return videogroup;
	}
	
	public ArrayList<item_c2devents> getVideoEventChild(){
		return videochild;
	}
	
	public HashMap<String, Integer> getVideoEventChildCount(){
		return videochild_count;
	}
	
	
	
	
	private ArrayList singleElement(ArrayList al)  
    {  
        ArrayList newAl = new ArrayList();  
  
        for(Iterator it = al.iterator(); it.hasNext();)  
        {  
            Object obj = it.next();  
            if(!newAl.contains(obj))  
            {  
                newAl.add(obj);  
            }  
        }  
        return newAl;  
    }  
	
	
	public class sortClass implements Comparator{  
	    public int compare(Object event1,Object event2){  
	    	item_c2devents event_1 = (item_c2devents) event1;  
	    	item_c2devents event_2 = (item_c2devents) event2;  
	        if(event_1.event_time<event_2.event_time){
	        	return 1;
	        }else{
	        	return -1;
	        }
	    }  
	}  
	
}
