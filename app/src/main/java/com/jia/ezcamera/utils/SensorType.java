package com.jia.ezcamera.utils;

import android.content.Context;

import com.jia.znjj2.R;


public class SensorType {
	public static final int TYPE1 = 0x01;// 门磁
	public static final int TYPE2 = 0x02;// 红外
	public static final int TYPE3 = 0x03;// 烟感
	public static final int TYPE4 = 0x04;// 煤气
	public static final int TYPE5 = 0x05;// 水浸
	public static final int TYPE6 = 0x06;// 震动
	public static final int TYPE7 = 0x07;// 遥控器
	public static final int TYPE8 = 0x08;// 幕帘
	public static final int TYPE9 = 0x09;// SOS按钮
	public static final int TYPE10 = 0x0a;// 门铃
	public static final int TYPE241 = 0xF1;// 开关
	public static final int TYPE242 = 0xF2;// 插座
	public static final int TYPE249 = 0xF9;// 遥控窗帘

	public static String getTypeString(int type, Context mContext) {
		int id = R.string.control;
		switch (type) {
		case TYPE1:
			id = R.string.magnetic;
			break;
		case TYPE2:
			id = R.string.infrared;
			break;
		case TYPE3:
			id = R.string.smoke;
			break;
		case TYPE4:
			id = R.string.gas;
			break;
		case TYPE5:
			id = R.string.water;
			break;
		case TYPE6:
			id = R.string.snake;
			break;
		case TYPE7:
			id = R.string.control;
			break;
		case TYPE8:
			id = R.string.events_type22;
			break;
		case TYPE9:
			id = R.string.sos_button;
			break;
		case TYPE10:
			id = R.string.door_bell;
			break;
		
		case TYPE241:
			id = R.string.switch_s;
			break;
		case TYPE242:
			id = R.string.socket_s;
			break;
		case TYPE249:
			id = R.string.win_control;
			break;
		

		default:
			break;
		}
		return mContext.getResources().getString(id);
	}
	
	
	public static int getTypeImageId(int type, Context mContext) {
		int id = R.string.control;
		switch (type) {
		case TYPE1:
			//门磁图标
			id = R.drawable.png_sensor_dor;
			break;
		case TYPE2:
			//红外图标
			id = R.drawable.png_sensor_ir;
			break;
		case TYPE3:
			//烟感图标
			id = R.drawable.png_sensor_yanwu;
			break;
		case TYPE4:
			//煤气图标
			id = R.drawable.png_sensor_ranqi;
			break;
		case TYPE5:
			//水浸图标
			id = R.drawable.png_sensor_water;
			break;
		case TYPE6:
			//震动图标
			id = R.drawable.png_sensor_ir;
			break;
		case TYPE7:
			//遥控器图标
			id = R.drawable.png_sensor_ctrl;
			break;
		case TYPE8:
			//幕帘图标
			id = R.drawable.png_sensor_ir;
			break;

		case TYPE9:
			//警急按钮
			id = R.drawable.png_sensor_sos;
			break;
		case TYPE10:
			//门铃
			id = R.drawable.png_sensor_doorbell;
			break;
		case TYPE241:
			//开关图标
			id = R.drawable.png_sensor_ir;
			break;
		case TYPE242:
			//插座图标
			id = R.drawable.png_sensor_ir;
			break;
		case TYPE249:
			//遥控窗帘图标
			id = R.drawable.png_sensor_ctrl;
			break;
		default:
			id = R.drawable.png_sensor_ctrl;
			break;
		}
		return id;
	}

}
