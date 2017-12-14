package com.jia.ezcamera.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.ezcamera.set.CameraSensorEditActivity;
import com.jia.znjj2.R;

import java.util.ArrayList;

import vv.ppview.PpviewClientInterface;

public class SensorAdapter extends BaseAdapter {
    private static final String TAG = SensorAdapter.class.getSimpleName();
    private ArrayList<sensorItem> mList = null;
    private Context myContext;
    private int editState = 0;// 0:非编辑状态 1:编辑状态
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();

    public SensorAdapter(Context context) {
        myContext = context;
        mList = new ArrayList<sensorItem>();
    }

    public void doRefresh() {
        if (mList != null) {
            mList.clear();
        }
        mList.addAll(SensorArray.getInstance().getList());
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
        Holder h = null;
        sensorItem item = mList.get(position);
        if (item == null) {
            return null;
        }

        final sensorItem mItem = item;

        convertView = View.inflate(myContext, R.layout.item_set_sensor,null);
        h = new Holder();
        h.sensorItem = (RelativeLayout) convertView
                .findViewById(R.id.sensor_rl);
        h.sensorName = (TextView) convertView
                .findViewById(R.id.sensor_name);
        h.sensorType = (TextView) convertView
                .findViewById(R.id.sensor_type);
        h.sensorBtn = (Button) convertView.findViewById(R.id.sensor_btn);
        h.sensorImg = (ImageView) convertView.findViewById(R.id.sensor_img);
        h.viewNew = convertView.findViewById(R.id.view_ifnew);
        convertView.setTag(h);

        // Log.i(TAG, "getView    item.sensorName=" + item.sensorName);
        if (TextUtils.isEmpty(item.sensorName)) {
            h.sensorName.setText(myContext.getResources().getString(
                    R.string.afs_sensor_name)
                    + " " + item.sensorId);
        } else {
            h.sensorName.setText(myContext.getResources().getString(
                    R.string.afs_sensor_name)
                    + " " + item.sensorName);
        }
        h.sensorImg.setImageResource(SensorType.getTypeImageId(item.sensorType, myContext));
        String strType = myContext.getResources().getString(
                R.string.afs_sensor_type)
                + " "
                + SensorType.getTypeString(item.sensorType, myContext);
        if (item.sensorType == SensorType.TYPE241) {
            strType = strType + item.sub_chl_count
                    + "路 (" + item.sensorId + ")";
        } else {
            strType = strType + "(" + item.sensorId + ")";
        }
        h.sensorType.setText(strType);
        h.sensorItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doClick(mItem);
            }
        });
        h.sensorBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doClick(mItem);
            }
        });
        if (item.ifNew == 0) {
            h.viewNew.setVisibility(View.GONE);
        } else if (item.ifNew == 1) {
            h.viewNew.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void doClick(sensorItem mItem) {
           jumpToEdit(mItem);
    }

    class Holder {
        public RelativeLayout sensorItem;
        public TextView sensorName;
        public TextView sensorType;
        public ImageView sensorImg;
        public Button sensorBtn;
        public View viewNew;
    }

    private void jumpToEdit(sensorItem item) {
        Intent intent = new Intent();
        intent.putExtra("item", item);
        intent.setClass(myContext, CameraSensorEditActivity.class);
        myContext.startActivity(intent);
    }


}
