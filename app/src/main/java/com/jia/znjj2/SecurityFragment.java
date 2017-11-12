package com.jia.znjj2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/11.
 */
public class SecurityFragment extends Fragment {
    private View view;
    private ListView lvSensor;
    private DataControl mDC;
    private BrdcstReceiver receiver;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x1210:
                    Toast.makeText(getContext(),"更改失败,请检查网络，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1211:
                    Toast.makeText(getContext(),"更改成功",Toast.LENGTH_LONG).show();
                    updateUI();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_security, container, false);
        lvSensor = (ListView) view.findViewById(R.id.security_sensor_list);
        mDC = DataControl.getInstance();
        updateListView();

        /*设置接收后台的广播消息*/
        receiver = new BrdcstReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MY_RECEIVER");
        getActivity().registerReceiver(receiver, filter);
        return view;
    }



    public void updateListView()
    {
        int i = mDC.mSensorList.size();
        ArrayList<HashMap<String,String>> localList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            HashMap<String,String> localHashMap = new HashMap<>();
            localHashMap.put("electric_img", ""+mDC.mSensorList.get(j).getElectricType());
            localHashMap.put("electric_name", mDC.mSensorList.get(j).getElectricName());
            localHashMap.put("extras", mDC.mSensorList.get(j).getExtras());

//            localHashMap.put("electric_order", mDC.mSensorList.get(j).getElectricOrder());
//            localHashMap.put("order_info", mDC.mSensorList.get(j).getOrderInfo());
            localList.add(localHashMap);
        }
        SensorElectricAdapter adapter = new SensorElectricAdapter(localList, getContext(),
                new String[] {"electric_img", "electric_name","electric_state","extras" },
                new int[] {R.id.sensor_electric_item_img,
                        R.id.sensor_electric_item_name,
                        R.id.sensor_electric_item_state,
                        R.id.sensor_electric_item_extra
                });
        lvSensor.setAdapter(adapter);
    }

    /**
     *广播：接受后台的service发送的广播
     */
    private  class BrdcstReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    }
    private void updateUI(){
        //System.out.println("AreaElectricFragment updateUI");
        updateListView();
    }

    public class SensorElectricAdapter extends BaseAdapter
    {
        private SceneModeViewHolder holder;
        private String[] keyString;
        private ArrayList<HashMap<String, String>> mAppList;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] valueViewID;

        public SensorElectricAdapter(ArrayList<HashMap<String, String>> paramArrayList, Context context,
                                    String[] paramArrayOfString, int[] paramArrayOfInt)
        {
            this.mAppList = paramArrayList;
            this.mContext = context;
            this.mInflater = LayoutInflater.from(this.mContext);
            this.keyString = new String[paramArrayOfString.length];
            this.valueViewID = new int[paramArrayOfInt.length];
            System.arraycopy(paramArrayOfString, 0, this.keyString, 0, paramArrayOfString.length);
            System.arraycopy(paramArrayOfInt, 0, this.valueViewID, 0, paramArrayOfInt.length);
        }

        public int getCount()
        {
            return this.mAppList.size();
        }

        public Object getItem(int paramInt)
        {
            return this.mAppList.get(paramInt);
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            paramView = this.mInflater.inflate(R.layout.sensor_electric_item, null);
            holder = new SceneModeViewHolder();
            holder.sensorImg = ((ImageView) paramView.findViewById(this.valueViewID[0]));
            holder.sensorName = ((TextView)paramView.findViewById(this.valueViewID[1]));
            holder.sensorState = ((TextView) paramView.findViewById(this.valueViewID[2]));
            holder.sensorExtra = ((Switch) paramView.findViewById(this.valueViewID[3]));
            paramView.setTag(this.holder);
            HashMap<String,String> localHashMap = (HashMap)this.mAppList.get(paramInt);
            if (localHashMap != null)
            {
                String name = (String)((HashMap)localHashMap).get(this.keyString[1]);
                String extra = mDC.mSensorList.get(paramInt).getExtras();
                System.out.println(mDC.mSensorList.get(paramInt));
                this.holder.sensorName.setText(name);
                final int resourceId = Integer.parseInt(localHashMap.get(keyString[0]));
                this.holder.sensorImg.setImageResource(mDC.mElectricTypeImages.getResourceId(resourceId,0));
                if(extra.equals("0")){
                    this.holder.sensorExtra.setChecked(false);
                }else {
                    this.holder.sensorExtra.setChecked(true);
                }
                final String electricCode = mDC.mSensorList.get(paramInt).getElectricCode();
                final String sensorState = mDC.mElectricState.get(electricCode)[0];
                String sensorStateInfo = mDC.mElectricState.get(electricCode)[1];
                if(sensorStateInfo.equals("00")){
                    holder.sensorState.setText("正常");
                } else if(sensorStateInfo.startsWith("01")){
                    holder.sensorState.setText("出现报警");
                } else if(sensorStateInfo.startsWith("02")){
                    holder.sensorState.setText("安装异常");
                } else if(sensorStateInfo.startsWith("03")){
                    holder.sensorState.setText("出现报警，安装异常");
                } else if(sensorStateInfo.startsWith("04")){
                    holder.sensorState.setText("电量低");
                } else if(sensorStateInfo.startsWith("05")){
                    holder.sensorState.setText("出现报警，电量低");
                } else if(sensorStateInfo.startsWith("06")){
                    holder.sensorState.setText("电量低，安装异常");
                } else if(sensorStateInfo.startsWith("07")){
                    holder.sensorState.setText("出现报警，电量低，安装异常");
                }

                holder.sensorExtra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String extras;
                        if(isChecked){
                            extras = "1";
                        }else {
                            extras = "0";
                        }
                        String result = mDC.mWS.updateSensorExtras(mDC.sMasterCode,electricCode,mDC.mSensorList.get(paramInt).getElectricIndex(),extras);
                        Message msg = new Message();
                        if(result.equals("1")){
                            msg.what = 0x1211;
                            mDC.mElectricData.updateElectricExtras(mDC.mSensorList.get(paramInt).getElectricIndex(),extras);
                            mDC.mAreaData.loadAreaList();
                        }else if(result.equals("-1")){
                            msg.what = 0x1210;
                        }
                        handler.sendMessage(msg);
                    }
                });
            }
            return paramView;
        }

        private class SceneModeViewHolder
        {
            TextView sensorName;
            ImageView sensorImg;
            TextView sensorState;
            Switch sensorExtra;

            private SceneModeViewHolder() {}
        }

    }
}
