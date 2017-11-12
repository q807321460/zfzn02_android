package com.jia.jdplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jia.znjj2.R;
import com.judian.support.jdplay.api.data.JdDeviceInfo;
import com.judian.support.jdplay.sdk.JdDeviceListContract;
import com.judian.support.jdplay.sdk.JdDeviceListPresenter;

import java.util.List;

public class JdDeviceListActivity extends Activity implements JdDeviceListContract.View {

    private static final String TAG = "JdDeviceListActivity";
    private JdDeviceListPresenter mPresenter;
    private JdDevicesAdapter mJdDevicesAdapter;
    private ListView mJdDevicesListView;
    private View mLoadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jdplay_device_list_view);
        mLoadView = findViewById(R.id.loading_view);
        mJdDevicesAdapter = new JdDevicesAdapter();
        mJdDevicesListView = (ListView) findViewById(R.id.device_listview);
        mJdDevicesListView.setAdapter(mJdDevicesAdapter);

        /*JdDeviceListPresenter的创建必须在设备的 listview 初始化后调用,
        因为在创建JdDeviceListPresenter的时候，可能会调用onJdDeviceInfoChange*/
        mPresenter = new JdDeviceListPresenter(this, this);
    }

    @Override
    public void onJdDeviceInfoChange(final List<JdDeviceInfo> infos) {
        JdDeviceListActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mLoadView.setVisibility(View.GONE);
                mJdDevicesAdapter.replaceData(infos);
            }
        });
    }

    private void startJdPlayResourceView() {
        Intent intent = new Intent();
        intent.setClass(JdDeviceListActivity.this, JdMusicResourceActivity.class);
        startActivity(intent);
    }

    private class ViewHolder {
        private final View contain;
        private final TextView deviceName, deviceStatus;
        private View deviceItem;
        private ImageView settingImg;
        private ImageView deviceSelect;

        public ViewHolder(View view) {
            contain = view;
            deviceName = (TextView) contain.findViewById(R.id.device_name);
            deviceStatus = (TextView) contain.findViewById(R.id.device_status);
            deviceSelect = (ImageView) contain.findViewById(R.id.device_select);
            deviceItem = contain.findViewById(R.id.device_item);
        }
    }

    class JdDevicesAdapter extends BaseAdapter {

        List<JdDeviceInfo> mJdDeviceInfoList;

        public void replaceData(List<JdDeviceInfo> info) {
            mJdDeviceInfoList = info;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return (mJdDeviceInfoList == null) ? 0 : mJdDeviceInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mJdDeviceInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView =
                        LayoutInflater.from(JdDeviceListActivity.this).inflate(
                                R.layout.jdplay_device_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final JdDeviceInfo jdDeviceInfo = mJdDeviceInfoList.get(position);
            holder.deviceName.setText(jdDeviceInfo.getName());

            if (jdDeviceInfo.getUuid() != null && jdDeviceInfo.getUuid().equals(mPresenter.getSelectedDeviceUuid())) {
                holder.deviceSelect.setImageResource(R.drawable.jdplay_checkbox_press);
            } else {
                holder.deviceSelect.setImageResource(R.drawable.jdplay_check_normal);
            }

            final boolean isOnline = jdDeviceInfo.getOnlineStatus() > 0;
            if (isOnline) {
                holder.deviceStatus.setSelected(true);
                holder.deviceStatus.setText(getString(R.string.online));
            } else {
                holder.deviceStatus.setSelected(false);
                holder.deviceStatus.setText(getString(R.string.offline));
            }

            View.OnClickListener selectDeviceListener = new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Log.d(TAG, "selectDevice uuid: " + jdDeviceInfo.getUuid());
                    mPresenter.selectDevice(jdDeviceInfo.getUuid());
                    notifyDataSetChanged();
                    startJdPlayResourceView();
                }
            };
            holder.deviceItem.setOnClickListener(selectDeviceListener);
            holder.deviceSelect.setOnClickListener(selectDeviceListener);

            return convertView;
        }
    }

}
