package com.jia.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jia.znjj2.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ShengYi on 2018/3/14.
 */

public class AirCenterAdapter  extends BaseAdapter {
        // 填充数据的list
        private ArrayList<String> list1;
        private ArrayList<String> list2;
        private ArrayList<String> list3;
        private ArrayList<String> list4;
        private ArrayList<String> list5;
        private ArrayList<String> list6;
        private ArrayList<String> list7;
        // 用来控制CheckBox的选中状况
        private static HashMap<Integer, Boolean> isSelected;
        // 上下文
        private Context context;
        // 用来导入布局
        private LayoutInflater inflater = null;
        // 构造器
        public AirCenterAdapter(ArrayList<String> list1,ArrayList<String> list2,ArrayList<String> list3,
                                ArrayList<String> list4,ArrayList<String> list5,ArrayList<String> list6,
                                ArrayList<String> list7,Context context) {
            this.context = context;
            this.list1 = list1;
            this.list2 = list2;
            this.list3 = list3;
            this.list4 = list4;
            this.list5 = list5;
            this.list6 = list6;
            this.list7 = list7;

            inflater = LayoutInflater.from(context);
            isSelected = new HashMap<Integer, Boolean>();
            // 初始化数据
            initDate();
        }
        // 初始化isSelected的数据
        private void initDate() {
            for (int i = 0; i < list1.size(); i++) {
                getIsSelected().put(i, false);
            }
            for (int i = 0; i < list2.size(); i++) {
                getIsSelected().put(i, false);
            }
            for (int i = 0; i < list3.size(); i++) {
                getIsSelected().put(i, false);
            }
            for (int i = 0; i < list4.size(); i++) {
                getIsSelected().put(i, false);
            } for (int i = 0; i < list1.size(); i++) {
                getIsSelected().put(i, false);
            }
            for (int i = 0; i < list5.size(); i++) {
                getIsSelected().put(i, false);
            }
            for (int i = 0; i < list6.size(); i++) {
                getIsSelected().put(i, false);
            }
            for (int i = 0; i < list6.size(); i++) {
                getIsSelected().put(i, false);
            }

        }
        @Override
        public int getCount() {
            return list1.size();
        }
        @Override
        public Object getItem(int position) {
            return list1.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                // 获得ViewHolder对象
                holder = new ViewHolder();
                // 导入布局并赋值给convertview
                convertView = inflater.inflate(R.layout.air_center_checkbox, null);
                holder.tvAirCenterNo = (TextView) convertView.findViewById(R.id.air_center_number);
                holder.tvAirCenterOpen = (TextView) convertView.findViewById(R.id.air_center_open);
                holder.tvAirCenterMode = (TextView) convertView.findViewById(R.id.air_center_mode);
                holder.tvAirCenterWind = (TextView) convertView.findViewById(R.id.air_center_wind);
                holder.tvAirCenterTp = (TextView) convertView.findViewById(R.id.air_center_number_temperature);
                holder.tvAirCentersnTp = (TextView) convertView.findViewById(R.id.air_center_number_innertemp);
                holder.tvAirCentererInfo = (TextView) convertView.findViewById(R.id.air_center_error_info);
                holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
                // 为view设置标签
                convertView.setTag(holder);
            } else {
                // 取出holder
                holder = (ViewHolder) convertView.getTag();
            }
            // 设置list中TextView的显示
            holder.tvAirCenterNo.setText(list1.get(position));
            holder.tvAirCenterOpen.setText(list2.get(position));
            holder.tvAirCenterMode.setText(list3.get(position));
            holder.tvAirCenterWind.setText(list4.get(position));
            holder.tvAirCenterTp.setText(list5.get(position));
            holder.tvAirCentersnTp.setText(list6.get(position));
            holder.tvAirCentererInfo.setText(list7.get(position));
            // 根据isSelected来设置checkbox的选中状况
            holder.cb.setChecked(getIsSelected().get(position));
            return convertView;
        }
        public static HashMap<Integer, Boolean> getIsSelected() {
            return isSelected;
        }
        public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
            AirCenterAdapter.isSelected = isSelected;
        }
        public static class ViewHolder {
            TextView tvAirCenterNo;
            TextView tvAirCenterOpen;
            TextView tvAirCenterMode;
            TextView tvAirCenterWind;
            TextView tvAirCenterTp;
            TextView tvAirCentersnTp;
            TextView tvAirCentererInfo;
            public CheckBox cb;
        }
    }

