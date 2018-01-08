package com.jia.znjj2;


import android.content.Context;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jia.data.DataControl;
import com.jia.widget.AlarmOpreation;
import com.jia.widget.AlarmsSetting;
import com.jia.widget.WeekGridAdpter;

import java.util.ArrayList;
import java.util.HashMap;


public class SetSceneTimeActivity extends AppCompatActivity {
    private ListView lvSetSceneTime;
    private DataControl mDC;
    private WeekGridAdpter inGridAdapter;
    private AlarmsSetting alarmsSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_scene_time);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    private void initView() {
        mDC = DataControl.getInstance();
        lvSetSceneTime = (ListView) findViewById(R.id.set_scene_time_list);
        alarmsSetting = new AlarmsSetting(this);
    }

    public void updateListView() {
        int i = mDC.mSceneList.size();
        ArrayList localList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            HashMap localHashMap = new HashMap();
            localHashMap.put("scene_name", mDC.mSceneList.get(j).getSceneName());
            localList.add(localHashMap);
        }
        SetSceneTimeActivity.lvButtonAdapter adapter = new SetSceneTimeActivity.lvButtonAdapter(localList, this,
                new String[]{"scene_name", "switch_in", "alarm_in_set","set_time","in_gridview", "set_scene_time_ll"},
                new int[]{R.id.set_scene_name, R.id.switch_in, R.id.set_in_time,R.id.alarm_in_set_time, R.id.in_gridview, R.id.set_scene_time_ll});
        lvSetSceneTime.setAdapter(adapter);
    }

    public class lvButtonAdapter extends BaseAdapter implements View.OnClickListener {
        private lvButtonAdapter.buttonViewHolder holder;
        private String[] keyString;
        private ArrayList<HashMap<String, Object>> mAppList;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] valueViewID;

        public lvButtonAdapter(ArrayList<HashMap<String, Object>> paramArrayList, Context context,
                               String[] paramArrayOfString, int[] paramArrayOfInt) {
            this.mAppList = paramArrayList;
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
            this.keyString = new String[paramArrayOfString.length];
            this.valueViewID = new int[paramArrayOfInt.length];
            System.arraycopy(paramArrayOfString, 0, this.keyString, 0, paramArrayOfString.
                    length);
            System.arraycopy(paramArrayOfInt, 0, this.valueViewID, 0, paramArrayOfInt.length);
        }

        public int getCount() {
            return this.mAppList.size();
        }

        public Object getItem(int paramInt) {
            return this.mAppList.get(paramInt);
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            paramView = this.mInflater.inflate(R.layout.activity_set_scene_time_item, null);
            this.holder = new lvButtonAdapter.buttonViewHolder();
            this.holder.sceneName = ((TextView) paramView.findViewById(this.valueViewID[0]));
            this.holder.sceneSwitch = ((TextView) paramView.findViewById(this.valueViewID[1]));
            this.holder.sceneSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.isSelected()) {
                        alarmsSetting.setInEnble(false);
                        v.setSelected(false);
                        AlarmOpreation.cancelAlert(SetSceneTimeActivity.this, AlarmsSetting.ALARM_SETTING_TYPE_IN);
                    }else {
                        alarmsSetting.setInEnble(true);
                        v.setSelected(true);
                        AlarmOpreation.enableAlert(SetSceneTimeActivity.this, AlarmsSetting.ALARM_SETTING_TYPE_IN, alarmsSetting);
                    }
                }
            });
            this.holder.sceneSwitch.setSelected(alarmsSetting.isInEnble()?true:false);
            this.holder.sceneTime= (RelativeLayout) paramView.findViewById(this.valueViewID[2]);
            this.holder.sceneTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerFragment  timePicker = new TimePickerFragment();
                    final int type=AlarmsSetting.ALARM_SETTING_TYPE_IN;
                    if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN) {
                        timePicker.setTime(alarmsSetting.getInHour(),alarmsSetting.getInMinutes());
                    }else{
                        timePicker.setTime(alarmsSetting.getOutHour(), alarmsSetting.getOutMinutes());
                    }
                    timePicker.show(getFragmentManager(),"timePicker" );
                    timePicker.setOnSelectListener(new TimePickerFragment.OnSelectListener() {
                        @Override
                        public void getValue(int hourOfDay, int minute) {
                            if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN) {
                                alarmsSetting.setInHour(hourOfDay);
                                alarmsSetting.setInMinutes(minute);
                            }else{
                                alarmsSetting.setOutHour(hourOfDay);
                                alarmsSetting.setOutMinutes(minute);
                            }
                            //this.holder.sceneSet.setText(hourOfDay+":"+minute);

                            AlarmOpreation.cancelAlert(SetSceneTimeActivity.this,type);
                            AlarmOpreation.enableAlert(SetSceneTimeActivity.this,type,alarmsSetting);
                        }
                    });
                }
            });

            this.holder.sceneSet = ((TextView) paramView.findViewById(this.valueViewID[2]));
            this.holder.sceneSet.setText(alarmsSetting.getInHour()+":"+alarmsSetting.getInMinutes());

            this.holder.gridSelect = ((GridView) paramView.findViewById(this.valueViewID[4]));
            inGridAdapter=new WeekGridAdpter(SetSceneTimeActivity.this,alarmsSetting,AlarmsSetting.ALARM_SETTING_TYPE_IN);
            this.holder.gridSelect.setAdapter(inGridAdapter);
            this.holder.listItem = ((LinearLayout) paramView.findViewById(this.valueViewID[5]));
            paramView.setTag(this.holder);
            HashMap localHashMap = (HashMap) this.mAppList.get(paramInt);
            if (localHashMap != null) {
                String str1 = (String) ((HashMap) localHashMap).get(this.keyString[0]);
                this.holder.sceneName.setText(str1);
//                if (paramInt == 0) {
//                    this.holder.sceneSwitch.setText("已连接");
//                } else {
//                    this.holder.sceneSwitch.setText("");
//                }

//                this.holder.userEdit.setOnClickListener(new MyMasterNode.lvButtonAdapter.UserEditListener(paramInt));
//                this.holder.userDelete.setOnClickListener(new MyMasterNode.lvButtonAdapter.UserDeleteListener(paramInt));
//                this.holder.listItem.setOnClickListener(new MyMasterNode.lvButtonAdapter.MasterNodeItemListener(paramInt));

                }

            return paramView;
        }
        @Override
        public void onClick(View v) {

        }
        public void showTimePickerDialog(final int type){
            TimePickerFragment  timePicker = new TimePickerFragment();
            if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN) {
                timePicker.setTime(alarmsSetting.getInHour(),alarmsSetting.getInMinutes());
            }else{
                timePicker.setTime(alarmsSetting.getOutHour(), alarmsSetting.getOutMinutes());
            }
            timePicker.show(getFragmentManager(),"timePicker" );
            timePicker.setOnSelectListener(new TimePickerFragment.OnSelectListener() {
                @Override
                public void getValue(int hourOfDay, int minute) {
                    if(type==AlarmsSetting.ALARM_SETTING_TYPE_IN) {
                        alarmsSetting.setInHour(hourOfDay);
                        alarmsSetting.setInMinutes(minute);
                    }else{
                        alarmsSetting.setOutHour(hourOfDay);
                        alarmsSetting.setOutMinutes(minute);
                    }
                 //   setTime(hourOfDay, minute, type);

                    AlarmOpreation.cancelAlert(SetSceneTimeActivity.this,type);
                    AlarmOpreation.enableAlert(SetSceneTimeActivity.this,type,alarmsSetting);
                }
            });
        }

        private class buttonViewHolder {
            TextView sceneName;
            TextView sceneSwitch;
            RelativeLayout sceneTime;
            TextView sceneSet;
            GridView gridSelect;
            LinearLayout listItem;

            private buttonViewHolder() {
            }
        }


    }
}

