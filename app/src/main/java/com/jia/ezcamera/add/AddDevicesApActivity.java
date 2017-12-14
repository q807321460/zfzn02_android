package com.jia.ezcamera.add;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jia.ezcamera.utils.wifi.lan.wifitool.wifiInfo;
import com.jia.znjj2.R;

import java.util.ArrayList;
import java.util.HashMap;


public class AddDevicesApActivity extends Activity {

    private ImageButton btn_back;
    private ListView search_list;
    private Context mContext = this;
    private ArrayList<String> ap_list;
    private HashMap<String, wifiInfo> apMap;
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_return:
                    finish();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device_search_list);
        apMap = (HashMap<String, wifiInfo>) getIntent().getSerializableExtra("apmap");
        ap_list = new ArrayList<String>();
        for (String mStr : apMap.keySet()) {
        	ap_list.add(mStr);
        }
        init();
    }

    private void init() {
        btn_back = (ImageButton) findViewById(R.id.btn_return);
        search_list = (ListView) findViewById(R.id.search_list);
        btn_back.setOnClickListener(onClickListener);
        
        search_list.setAdapter(new ListAdapter());
        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(ActivityAdd.myHandler!=null){
					wifiInfo myWifiInfo = apMap.get(ap_list.get(position));
					Message msg = new Message();
					msg.what = ActivityAdd.SET_AP;
					msg.obj = myWifiInfo;
					ActivityAdd.myHandler.sendMessage(msg);
					finish();
				}
			}
        	
		});

    }
    
    private class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ap_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ap_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = LayoutInflater.from(mContext).inflate(R.layout.item_ap_list, null);
			TextView apText = (TextView)view.findViewById(R.id.devices_ap);
			apText.setText(ap_list.get(position));
			return view;
		}
    	
    }
    
}
