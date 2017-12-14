package com.jia.ezcamera.set;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.jia.znjj2.R;

import java.util.ArrayList;


import vv.tool.gsonclass.item_wifi_info;

public class SetWifiAdapter extends BaseAdapter {
	private static final String TAG = SetWifiAdapter.class.getSimpleName();
	private ArrayList<item_wifi_info> mList = null;
	private Context myContext;

	public SetWifiAdapter(Context context,ArrayList<item_wifi_info> wifiList) {
		myContext = context;
		mList = wifiList;
	}

	public void doRefresh(ArrayList<item_wifi_info> wifiList) {
		if (mList != null) {
			mList.clear();
		}
		mList = wifiList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mList == null) {
			return null;
		}
		item_wifi_info item = mList.get(position);
		if (item == null) {
			return null;
		}
		Holder h = null;
		// final sensorItem mItem = item;
		if (convertView == null) {
			convertView = View.inflate(myContext, R.layout.item_set_wifi,
					null);
			h = new Holder();
			h.wifiName = (TextView) convertView
					.findViewById(R.id.net_wifi_name);
			h.wifiLock = (ImageView) convertView
					.findViewById(R.id.net_wifi_lock);
            h.wifiSignal= (ImageView) convertView.findViewById(R.id.net_wifi_signal);
			convertView.setTag(h);
		} else {
			h = (Holder) convertView.getTag();
		}
		// Log.i(TAG, "getView    item.sensorName=" + item.sensorName);
		h.wifiName.setText(item.ssid);
		if(item.is_secu!=0){
			h.wifiLock.setVisibility(View.VISIBLE);
		}else{
			h.wifiLock.setVisibility(View.INVISIBLE);
		}
		int imageId=0;
		switch (item.signel/25) {
		case 0:
			imageId = R.drawable.png_wifi1;
			break;
		case 1:
			imageId = R.drawable.png_wifi2;
			break;
		case 2:
			imageId = R.drawable.png_wifi3;
			break;
		case 3:
			imageId = R.drawable.png_wifi4;
			break;

		default:
			imageId = R.drawable.png_wifi4;
			break;
		}
		h.wifiSignal.setImageResource(imageId);
		return convertView;
	}

	class Holder {
		public TextView wifiName;
		public ImageView wifiLock;
        public ImageView wifiSignal;
	}

}
