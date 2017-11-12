package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.AccountData;
import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.data.ElectricSharedData;
import com.jia.util.CreateImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SharedElectric extends Activity {

    private DataControl mDC;
    private int position;
    private AccountData.AccountDataInfo accountDataInfo;

    private TextView tvTitleText;
    private TextView tvTitleSave;
    private ListView lvElectricList;
    private List<ElectricSharedData.ElectricSharedLoacl> electricList = new ArrayList<>();
    SharedElectricListAdapter adapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1130:
                    Toast.makeText(SharedElectric.this, "分发失败，检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1131:
                    Toast.makeText(SharedElectric.this, "分发失败，稍候重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1132:
                    Toast.makeText(SharedElectric.this, "分发成功", Toast.LENGTH_LONG).show();
                    updateLocalShared();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_electric);
        initView();
        updateListView();
        addListener();
    }

    public void initView() {
        mDC = DataControl.getInstance();
        position = getIntent().getIntExtra("position", -1);
        if (-1 != position) {
            accountDataInfo = mDC.mSharedAccountList.get(position);
        }
        electricList = mDC.mElectricSharedData.loadSharedElectricByAccountCode(accountDataInfo.getAccountCode());

        tvTitleText = (TextView) findViewById(R.id.shared_electric_title_text);
        tvTitleSave = (TextView) findViewById(R.id.shared_electric_title_save);
        lvElectricList = (ListView) findViewById(R.id.shared_electric_list);

        tvTitleText.setText(accountDataInfo.getAccountName()+"的电器权限");
    }

    private void updateLocalShared(){
        for(int i = 0; i<electricList.size();i++ ){
            mDC.mElectricSharedData.updateLocalShared(
                    electricList.get(i).getAccountCode()
                    ,electricList.get(i).getMasterCode()
                    ,electricList.get(i).getElectricIndex()
                    ,electricList.get(i).getIsShared());
        }
    }

    private void addListener(){
        tvTitleSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuffer buffer = new StringBuffer();
                for(int i = 0; i<electricList.size();i++ ){
                    buffer.append(electricList.get(i).getAccountCode()).append('|');
                    buffer.append(electricList.get(i).getMasterCode()).append('|');
                    buffer.append(electricList.get(i).getElectricIndex()).append('|');
                    buffer.append(electricList.get(i).getIsShared()).append(';');
                }
                new Thread(){
                    @Override
                    public void run() {
                        String result = mDC.mWS.adminSharedElectric(buffer.toString().getBytes());
                        Message msg = new Message();
                        if(result.startsWith("-2")){
                            msg.what = 0x1130;
                        }else if(result.startsWith("-1")){
                            msg.what = 0x1131;
                        }else if(result.startsWith("1")){
                            msg.what = 0x1132;
                        }
                        handler.sendMessage(msg);
                        System.out.println("权限分发的返回结果： "+ result);
                    }
                }.start();


            }
        });
    }

    public void updateListView()
    {
        int i = electricList.size();
        ArrayList localList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            HashMap localHashMap = new HashMap();
            localHashMap.put("electric_name", electricList.get(j).getElectricName());
            localHashMap.put("room_name", getElectricRoomName(electricList.get(j).getRoomIndex()));
            localList.add(localHashMap);
        }
        adapter = new SharedElectricListAdapter(localList, this,
                new String[] {"electric_name", "room_name"},
                new int[] {R.id.shared_electric_item_name,R.id.shared_electric_item_area });
        lvElectricList.setAdapter(adapter);
    }

    public class SharedElectricListAdapter extends BaseAdapter
    {
        private ViewHolder holder;
        private String[] keyString;
        private ArrayList<HashMap<String, Object>> mAppList;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] valueViewID;

        public SharedElectricListAdapter(ArrayList<HashMap<String, Object>> paramArrayList, Context context,
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
            paramView = this.mInflater.inflate(R.layout.shared_electric_item, null);
            holder = new ViewHolder();
            holder.electricItem = (RelativeLayout) paramView.findViewById(R.id.shared_electric_item_rl);
            holder.electricImg = (ImageView) paramView.findViewById(R.id.shared_electric_item_img);
            holder.electricName = (TextView)paramView.findViewById(R.id.shared_electric_item_name);
            holder.electricRoom = (TextView)paramView.findViewById(R.id.shared_electric_item_area);
            holder.electricAdmin = (Switch) paramView.findViewById(R.id.shared_electric_item_admin);
            paramView.setTag(this.holder);
            HashMap localHashMap = (HashMap)this.mAppList.get(paramInt);
            if (localHashMap != null)
            {
                String electricName = (String)((HashMap)localHashMap).get(this.keyString[0]);
                String roomName = (String)((HashMap)localHashMap).get(this.keyString[1]);


                ElectricSharedData.ElectricSharedLoacl electricShared = electricList.get(paramInt);
                int electricType = electricList.get(paramInt).getElectricType();
                if (electricType == 2){
                    if (electricShared.getOrderInfo().equals("01")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_left);
                    }else if(electricShared.getOrderInfo().equals("02")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_right);
                    }
                }else if(electricType == 3){
                    if (electricShared.getOrderInfo().equals("01")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_left);
                    }else if(electricShared.getOrderInfo().equals("02")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_center);

                    }else if(electricShared.getOrderInfo().equals("03")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_right);
                    }
                }else if (electricType == 4){
                    if (electricShared.getOrderInfo().equals("01")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left1);
                    }else if(electricShared.getOrderInfo().equals("02")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left2);
                    }else if(electricShared.getOrderInfo().equals("03")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right2);
                    }else if(electricShared.getOrderInfo().equals("04")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right1);
                    }
                }else {
                    holder.electricImg.setBackgroundResource(mDC.mElectricTypeImages.getResourceId(electricType,0));
                }

                this.holder.electricName.setText(electricName);
                this.holder.electricRoom.setText(roomName);
                if(electricShared.getIsShared() == 0){
                    this.holder.electricAdmin.setChecked(false);
                }else if(electricShared.getIsShared() == 1){
                    this.holder.electricAdmin.setChecked(true);
                }
            }

//            holder.electricAdmin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(holder.electricAdmin.isChecked()){
//                        electricList.get(paramInt).setIsShared(1);
//                    }else {
//                        electricList.get(paramInt).setIsShared(0);
//                    }
//                }
//            });
            holder.electricAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        electricList.get(paramInt).setIsShared(1);
                    }else {
                        electricList.get(paramInt).setIsShared(0);
                    }
                }
            });
            return paramView;
        }

        public void removeItem(int paramInt)
        {
            this.mAppList.remove(paramInt);
            notifyDataSetChanged();
        }



    }

    class ViewHolder
    {
        RelativeLayout electricItem;
        ImageView electricImg;
        TextView electricName;
        TextView electricRoom;
        Switch electricAdmin;

        private ViewHolder() {}
    }


    private String getElectricRoomName(int roomIndex){
        for(int i=0;i<mDC.mAreaList.size();i++) {
            if(mDC.mAreaList.get(i).getRoomIndex() == roomIndex){
                return mDC.mAreaList.get(i).getRoomName();
            }
        }
        return null;
    }
    public void sharedElectricBack(View view) {
        finish();
    }
}
