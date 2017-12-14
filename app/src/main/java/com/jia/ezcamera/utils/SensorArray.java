package com.jia.ezcamera.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import vv.tool.gsonclass.item_sub_chls;

public class SensorArray {
	private static final String TAG = SensorArray.class.getSimpleName();
	private ArrayList<sensorItem> mList = null;
	private static SensorArray instance = null;

	private SensorArray() {
		if (mList == null) {
			mList = new ArrayList<sensorItem>();
			mList.clear();
		}
	}

	public static SensorArray getInstance() {
		if (instance == null) {
			instance = new SensorArray();
		}
		return instance;
	}

	public ArrayList<sensorItem> getList() {
		if (mList != null) {
			return mList;
		}
		return null;
	}

	public int getCount() {
		if (mList != null) {
			return mList.size();
		}
		return 0;
	}

	public void clearList() {
		if (mList != null) {
			mList.clear();
		}
	}

	public void addItem(int index, int chlId, String sensorName,
                        int sensorType, String sensorId, int preset,
                        int isAlarm, int status, int power, int chls_count, ArrayList<item_sub_chls> chls, int ifNew) {
		sensorItem item = new sensorItem();
		item.index = index;
		item.chlId = chlId;
		item.sensorName = sensorName;
		item.sensorType = sensorType;
		item.sensorId = sensorId;
		item.preset = preset;
		item.isAlarm = isAlarm;
		item.status = status;
        item.lowPower=power;
        item.sub_chl_count = chls_count;
        item.sub_chls = chls;
        item.ifNew=ifNew;
		if (mList != null) {
			// Log.i(TAG, "addItem    item=" + item.sensorId);
			mList.add(item);
		}
	}

	public boolean ifContainItem(int index, int chlId, String sensorName,
                                 int sensorType, String sensorId, int preset,
                                 int isAlarm, int status, int power, int chls_count, ArrayList<item_sub_chls> chls, int ifNew) {
		if (TextUtils.isEmpty(sensorId)) {
			return false;
		}
		if (mList != null) {
			int mSize = getCount();
			if (mSize > 0) {
				for (int i = 0; i < mSize; i++) {
					sensorItem si = mList.get(i);
					if (TextUtils.equals(si.sensorId, sensorId)) {
						return false;
					}
				}
			}
		}
		addItem(index, chlId, sensorName, sensorType, sensorId, preset, 
				isAlarm, status,power,chls_count,chls,ifNew);
		return true;
	}

	public boolean removeItem(String sensorId) {
		if (TextUtils.isEmpty(sensorId)) {
			return false;
		}
		if (getCount() > 0) {
			int mCount = getCount();
			for (int i = 0; i < mCount; i++) {
				sensorItem si = mList.get(i);
				if (TextUtils.equals(si.sensorId, sensorId)) {
					mList.remove(si);
					return true;
				}
			}
		}
		return false;
	}

	public boolean updateItem(int index, int chlId, String sensorName,
			int sensorType, String sensorId, int preset, 
			int isAlarm, int status,int power,int chls_count,ArrayList<item_sub_chls> chls) {
		if (!TextUtils.isEmpty(sensorId) && getCount() > 0) {
			int mCount = getCount();
			for (int i = 0; i < mCount; i++) {
				sensorItem item = mList.get(i);
				if (!TextUtils.isEmpty(item.sensorId)
						&& item.sensorId.equals(sensorId)) {
					item.index = index;
					item.chlId = chlId;
					item.sensorName = sensorName;
					item.sensorType = sensorType;
					item.sensorId = sensorId;
					item.preset = preset;
					item.isAlarm = isAlarm;
					item.status = status;
                    item.lowPower=power;
                    item.sub_chl_count = chls_count;
                    item.sub_chls = chls;
				}
			}
			return true;
		}
		return false;
	}
	
	public ArrayList<swichItem> getSwichList(){
		ArrayList<swichItem> list = new ArrayList<swichItem>();
		if (getCount() > 0) {
			int mCount = getCount();
			for (int i = 0; i < mCount; i++) {
				sensorItem si = mList.get(i);
				if(si.sensorType==0xF1){
					if(si.sub_chl_count>0){
						swichItem item1 = new swichItem();//空白
						swichItem item2 = new swichItem();//名称
						item1.swichStatus = -1;
						item2.swichSensorId =si.sensorId;
						item2.swichSensorName = si.sensorName;
						item2.swichStatus = -2;
						list.add(item1);
						list.add(item2);
					}
					for(int j=0;j<si.sub_chl_count;j++){
						swichItem item = new swichItem();
						item_sub_chls chls = si.sub_chls.get(j);
						item.swichName = chls.name;
						item.swichChl = j;
						item.swichSensorId =si.sensorId;
						item.swichSensorName = si.sensorName;
						item.swichAlarm_linkage = chls.alarm_linkage;
						item.swichStatus = chls.status;
						item.cam_chl_id = si.chlId;
						list.add(item);
					}
				}
			}
		}
		//Log.e("DEBUG", "getSwichList"+list.size());
		swichItem item = new swichItem();//空白
		item.swichStatus = -1;
		list.add(item);
		return list;
	}

    public void updateItemStatus(String sensorid,int status){
        if (!TextUtils.isEmpty(sensorid)&&mList!=null){
            for (sensorItem si:mList){
                if (TextUtils.equals(sensorid,si.sensorId)){
                    si.status=status;
                    break;
                }
            }
        }
    }
    
    public void updateItemSubStatus(String sensorid,int sub_chl_id,int status){
    	Log.e("DEBUG", "chl_id "+sub_chl_id +"    status:"+status);
        if (!TextUtils.isEmpty(sensorid)&&mList!=null){
            for (sensorItem si:mList){
                if (TextUtils.equals(sensorid,si.sensorId)&&si.sub_chl_count>0){
                	if(sub_chl_id<=si.sub_chl_count)
                		si.sub_chls.get(sub_chl_id).status = status;
                    break;
                }
            }
        }
    }
    
    
    public void updateItemSubNames(String sensorid,int sub_chl_id,String name){
        if (!TextUtils.isEmpty(sensorid)&&mList!=null){
            for (sensorItem si:mList){
                if (TextUtils.equals(sensorid,si.sensorId)&&si.sub_chl_count>0){
                	if(sub_chl_id<=si.sub_chl_count)
                		si.sub_chls.get(sub_chl_id).name = name;
                    break;
                }
            }
        }
    }
    
    
}
