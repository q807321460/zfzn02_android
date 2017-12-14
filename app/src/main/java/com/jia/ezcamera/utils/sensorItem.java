package com.jia.ezcamera.utils;

import java.io.Serializable;
import java.util.ArrayList;

import vv.tool.gsonclass.item_sub_chls;

public class sensorItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6610925041451606991L;
	public int index;
	public int chlId;
	public String sensorName;
	public int sensorType;
	public String sensorId;
	public int preset;
	public int isAlarm;
	public int status;//状态 0:关闭   1:打开
	public int lowPower=0;//"low_power":是否低电压，0=否，1=是
	public int sub_chl_count;
	public ArrayList<item_sub_chls> sub_chls;
    public int ifNew=0;//是否为新的对吗   0:否   1:是
	/**
	"index": 序号(数字),
	"chl_id": 对应的通道号（数字，-1表示没有对应关系),
	"name": "传感器名称(文本)",
	"type": 传感器类型（数字,参见宏定义）,:
						0x01		//门磁
						0x02		//红外
						0x03		//烟感
						0x04		//煤气
						0x07		//遥控器
						0x08		//水浸
						0x09		//老人关爱
	"id": "传感器设备序列号(文本)",
	"preset":对应的预置点位置(数字，0表示没有对应),
	"is_main":是否是主门磁（数字，只有在类型是门磁时才会出现，0=不是 1=是）
	"is_alarm":触发后是否需要报警0=不 1=是
	*/
}
