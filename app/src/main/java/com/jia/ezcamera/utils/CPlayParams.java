package com.jia.ezcamera.utils;


import java.io.Serializable;

import vv.playlib.FishEyeInfo;

/**
 * ���Ų���
 */
public class CPlayParams implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1981637003230404611L;
	 public String cam_name;
	public String chl_name;
	public int chl_id;
	public String dev_id;
	public String user;
	public String pass;
	public boolean ifVideo = false;
	public String mainstream;
	public int main_id;
	public int main_frame_rate;
	public String substream;
	public int sub_id;
	public int sub_frame_rate;
	public int chl_width;
	public int chl_height;
	public int chl_port;
	public boolean shared;
	public long longConnect = 0;
	public int cloudId;
	public boolean ifCloud = false;
	public int ifAudio = 0;
	public int audioStatu = 0;
	public boolean ifPhoto = false;
	public String uid;

	public int ifp2ptalk = 0;
	public long longTalk = 0;
	public boolean ifTalk = false;
	public int playHandler = 0;
	public int sendSize = 0;

	public int frame_rate = 0;
	public int play_type = 0;

	public FishEyeInfo fishEyeInfo;
};