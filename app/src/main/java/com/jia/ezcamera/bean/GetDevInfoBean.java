package com.jia.ezcamera.bean;

import java.io.Serializable;
import java.util.ArrayList;

import vv.tool.gsonclass.fisheye_params_item;

public class GetDevInfoBean {
	public int res;
	public String dev_id;
	public String model;
	public String firmware;
	public String vendor;
	public String local_name;
	public String devpass;
	public int status;
	public int p2pstatus;
	public ArrayList<cam_item_local> channels;
	
	
	public class cam_item_local{
		public String uid;
		public int chl_id;
		public String name;
		public int ptz;
		public int voicetalk_type;
		public int event_enabled;
		public int ext_sensor_cap;
		public int sensor_num;
		public int playback_cap;
		public int fisheyetype;
		public ArrayList<stream_item> streams;
		public int play_type;
	}
	
	public class stream_item{
		public int stream_id;
		public int width;
		public int height;
		public String stream_url;
		public int frame_rate;
		public fisheye_params_item fisheye_params;
	}
}
