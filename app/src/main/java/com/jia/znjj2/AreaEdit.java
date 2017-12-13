package com.jia.znjj2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.camera.business.Business;
import com.jia.camera.business.entity.ChannelInfo;
import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.data.RoomData;
import com.jia.widget.DragAdapterInterface;
import com.jia.widget.DragCallback;
import com.jia.widget.DragGridView;
import com.jia.widget.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AreaEdit extends Activity {
    private DragGridView dragGridView;
    private DataControl mDC;
    private int roomPosition;
    private EditText etAreaName;
    private Spinner spAreaImg;
    private TextView tvElectricNum;
   // private MyGridView mgvElectricList;
    private RoomData.RoomDataInfo roomDataInfo;
    private List<ChannelInfo> mChannelInfoList;
    private List<Integer> datas=new ArrayList<>();
    private MyAdapter adapter;
    private int fromPosition;
    private int toPosition;
    public static int mvelectricSequ;
    public static int mvelectricindex;
    public static int mvroomposition;
    public static int areaElectricSize;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1080:
                    Toast.makeText(AreaEdit.this,"删除房间失败，检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1081:
                    Toast.makeText(AreaEdit.this,"删除房间失败，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1082:
                    Toast.makeText(AreaEdit.this,"删除房间成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0x1085:
                    Toast.makeText(AreaEdit.this,"删除电器失败，检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1086:
                    Toast.makeText(AreaEdit.this,"删除电器失败，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1087:
                    Toast.makeText(AreaEdit.this,"删除电器成功",Toast.LENGTH_LONG).show();
                    updateView();
                    break;
                case 0x1090:
                    Toast.makeText(AreaEdit.this,"更改房间失败，检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1091:
                    Toast.makeText(AreaEdit.this,"更改房间失败，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1092:
                    Toast.makeText(AreaEdit.this,"更改房间成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0x1093:
                    Toast.makeText(AreaEdit.this,"电器位置更换失败，检查网络",Toast.LENGTH_LONG).show();
                    break;
                case 0x1094:
                    Toast.makeText(AreaEdit.this,"电器位置更换失败，稍候重试",Toast.LENGTH_LONG).show();
                    break;
                case 0x1095:
                    Toast.makeText(AreaEdit.this,"电器位置更换成功",Toast.LENGTH_LONG).show();
                    updateGridView();
                    break;


            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_edit);
        initView();
        updateSpinner();
        updateGridView();
    }

    public void onResume(){
        super.onResume();
        dragGridView.setAdapter(adapter);
        tvElectricNum.setText("设备（"+areaElectricSize+"）");
    }

    private void updateView(){

        tvElectricNum.setText("设备（"+areaElectricSize+"）");
        updateGridView();
    }

    private void initView(){
        mDC = DataControl.getInstance();
        roomPosition = getIntent().getIntExtra("roomPosition", -1);
        if(roomPosition != -1){
            roomDataInfo = mDC.mAreaList.get(roomPosition);
            mvroomposition = roomPosition;
        }
        etAreaName = (EditText) findViewById(R.id.area_edit_area_name);
        spAreaImg = (Spinner) findViewById(R.id.area_edit_area_image);
        tvElectricNum = (TextView) findViewById(R.id.area_edit_electric_num);
        //mgvElectricList = (MyGridView) findViewById(R.id.area_edit_electric_list);
        dragGridView= (DragGridView) findViewById(R.id.area_edit_electric_list);
        etAreaName.setText(roomDataInfo.getRoomName());
        spAreaImg.setSelection(roomDataInfo.getRoomImg());
        areaElectricSize = roomDataInfo.getmElectricInfoDataList().size();
        tvElectricNum.setText("设备（"+areaElectricSize+"）");
    }
    class MyAdapter extends BaseAdapter implements DragAdapterInterface
    {
        private List<Integer> datas=new ArrayList<>();
        private Context context;
        public MyAdapter(Context context)
        {
            this.context=context;
        }
        public void setDatas(List<Integer> datas)
        {
            this.datas.clear();
            this.datas.addAll(datas);
        }
        @Override
        public int getCount()
        {
            return mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size();
        }

        @Override
        public Object getItem(int position)
        {
            return mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            Holder holder;
            if(convertView==null)
            {
                holder=new Holder();
                convertView= LayoutInflater.from(context).inflate(R.layout.view_item,null);
                holder.deleteImg= (ImageView) convertView.findViewById(R.id.delete_img);
                holder.moveImg =(ImageView)convertView.findViewById(R.id.move_img);
                holder.iconImg= (ImageView) convertView.findViewById(R.id.icon_img);
                holder.nameTv= (TextView) convertView.findViewById(R.id.name_tv);
                holder.container=convertView.findViewById(R.id.item_container);

                convertView.setTag(holder);
            }
            else
            {
                holder= (Holder) convertView.getTag();
            }
            holder.moveImg.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v){
                    mvelectricSequ= mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricSequ();
                    mvelectricindex = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricIndex();
                    Intent intent = new Intent(AreaEdit.this,RoomList.class);
                    startActivity(intent);
                }
            });
            holder.deleteImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    new AlertDialog.Builder(AreaEdit.this).setTitle("确定删除电器？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                        {
                                            int eletricType = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricType();
                                            if(eletricType == 8){
                                                //先从乐橙后台删除摄像头，再从兆峰后台删除，最后本地删除
                                                loadChannelList(position);
                                            }else {
                                                //先从兆峰后台删除，后本地删除
                                                deleteElectric(position);
                                            }

                                        }
                                    })
                            .setNegativeButton("取消", null).show();
                  //  mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().remove(position);
                    notifyDataSetChanged();
                }
            });
            DisplayMetrics loacalDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
            System.out.println("$$$$$$$$$$$"+loacalDisplayMetrics);
            int i = (int)(loacalDisplayMetrics.heightPixels/loacalDisplayMetrics.density);
            holder.moveImg.setVisibility(View.GONE);
            holder.deleteImg.setVisibility(View.GONE);
            holder.container.setBackgroundColor(Color.WHITE);
            if (position < mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size()) {
                ElectricInfoData electricInfoData = new ElectricInfoData();
                for (ElectricInfoData ele : mDC.mAreaList.get(roomPosition).getmElectricInfoDataList()) {
                    if (ele.getElectricSequ()==position) {
                        electricInfoData = ele;
                        break;
                    }
                }
//                    ElectricInfoData electricInfoData = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position);//获取每一个电器
                int electricType = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricType();

                if (electricType == 2){
                    if (electricInfoData.getOrderInfo().equals("01")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift2_left);
                    }else if(electricInfoData.getOrderInfo().equals("02")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift2_right);
                    }
                }else if(electricType == 3){
                    if (electricInfoData.getOrderInfo().equals("01")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift3_left);
                    }else if(electricInfoData.getOrderInfo().equals("02")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift3_center);

                    }else if(electricInfoData.getOrderInfo().equals("03")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift3_right);

                    }
                }else if (electricType == 4 || electricType == 10){
                    if (electricInfoData.getOrderInfo().equals("01")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift4_left1);
                    }else if(electricInfoData.getOrderInfo().equals("02")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift4_left2);

                    }else if(electricInfoData.getOrderInfo().equals("03")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift4_right2);

                    }else if(electricInfoData.getOrderInfo().equals("04")){
                        holder.iconImg.setBackgroundResource(R.drawable.electric_type_swift4_right1);

                    }
                }else {
                    holder.iconImg.setBackgroundResource(mDC.mElectricTypeImages.getResourceId(electricType,0));
                }

                holder.nameTv.setText(mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricName());
            }
            //viewHolder.electricrl.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i / 3));
            holder.iconImg.setLayoutParams(new LinearLayout.LayoutParams(i / 6, i / 6));
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(i/15,i/15);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp2.setMargins(0,10,10,0);
            if (i <= 800)
            {
                holder.nameTv.setTextSize(18.0F);
            }
            else
            {
                holder.nameTv.setTextSize(22.0F);
            }
            holder.nameTv.setTag(position);
            holder.iconImg.setTag(convertView);
            return convertView;
        }


        class Holder
        {
            public ImageView deleteImg;
            public ImageView moveImg;
            public ImageView iconImg;
            public TextView nameTv;
            public View container;
        }
        @Override
        public void reOrder(int startPosition, int endPosition)
        {
//            if(endPosition<mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size())
//            {
//                Object object=mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().remove(startPosition);
//                mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().add(endPosition, (ElectricInfoData) object);
//                notifyDataSetChanged();
//
//            }
        }
    }

    public void updateGridView() {
        for (int i = 0; i < 46; i++) {
            datas.add(i);
        }
        adapter = new MyAdapter(this);
        adapter.setDatas(datas);
        dragGridView.setAdapter(adapter);


        dragGridView.setDragCallback(new DragCallback() {
            @Override
            public void startDrag(int position) {
                LogUtil.i("start drag at " + position);
                fromPosition = position;
            }

            @Override
            public void endDrag(int position) {
                LogUtil.i("end drag at " + position);
                toPosition = position;

                   // mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().set(toPosition, fromElectric);
                    new Thread(){
                        @Override
                        public void run() {
                            if (toPosition != fromPosition) {
                                // ElectricInfoData fromElectric = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(fromPosition);
                                if (fromPosition < toPosition) {
                                    for (int i = fromPosition; i < toPosition; i++) {
                                        Collections.swap(mDC.mAreaList.get(roomPosition).getmElectricInfoDataList(), i, i + 1);
                                    }
                                } else if (fromPosition > toPosition) {
                                    for (int i = fromPosition; i > toPosition; i--) {
                                        Collections.swap(mDC.mAreaList.get(roomPosition).getmElectricInfoDataList(), i, i - 1);
                                    }
                                }

                                for (ElectricInfoData ele : mDC.mAreaList.get(roomPosition).getmElectricInfoDataList()) {
                                    if (ele.getElectricSequ() == fromPosition) {
                                        String result = mDC.mWS.updateElectricSequ(mDC.sMasterCode,
                                                ele.getElectricIndex(),
                                                mDC.mAreaList.get(roomPosition).getRoomIndex(), fromPosition, toPosition);
                                        Message msg = new Message();
                                        if (result.startsWith("-2")) {
                                            msg.what = 0x1093;
                                        } else if (result.startsWith("-1")) {
                                            msg.what = 0x1094;
                                        } else if (result.startsWith("1")){
                                            mDC.mElectricData.updateElectricSequ1(mDC.sMasterCode,
                                                    ele.getElectricIndex(),
                                                    mDC.mAreaList.get(roomPosition).getRoomIndex(),
                                                    fromPosition, toPosition);
                                            mDC.mAreaData.loadAreaList();//更新区域
                                            msg.what = 0x1095;
                                        }
                                        handler.sendMessage(msg);
                                        break;
                                    }
                                }
                            }
                        }
                    }.start();


            }

        });
        dragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dragGridView.clicked(position);
            }
        });
        dragGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dragGridView.startDrag(position);
                return false;
            }
        });
    }
    private void updateSpinner()
    {
        BaseAdapter local1 = new BaseAdapter()
        {
            public int getCount()
            {
                return mDC.mAreaTypeImages.length();
            }

            public Object getItem(int paramAnonymousInt)
            {
                return null;
            }

            public long getItemId(int paramAnonymousInt)
            {
                return 0L;
            }

            @SuppressLint({"NewApi", "ResourceAsColor"})
            public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
            {
                LinearLayout localLayout = new LinearLayout(AreaEdit.this);
                localLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView localImageView = new ImageView(AreaEdit.this);
                localImageView.setBackgroundResource(mDC.mAreaTypeImages.getResourceId(paramAnonymousInt, 0));
                localImageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
                localLayout.addView(localImageView);
                TextView localTextView = new TextView(AreaEdit.this);
                localTextView.setText(String.format("图片"+paramAnonymousInt));
                localTextView.setTextColor(2131230726);
                localTextView.setPadding(0, 35, 0, 0);
                localTextView.setTextSize(16.0F);
                localLayout.addView(localTextView);
                return localLayout;
            }
        };
        spAreaImg.setAdapter(local1);
        spAreaImg.setSelection(roomDataInfo.getRoomImg());
    }

    public void areaEditBack(View view){
        finish();
    }

    public void areaEditSave(View view){
        new Thread(){
            @Override
            public void run() {
                String roomName = etAreaName.getText().toString();
                int roomImg = spAreaImg.getSelectedItemPosition();
                String result = mDC.mWS.updateUserRoom(mDC.sMasterCode, roomDataInfo.getRoomIndex(), roomName, roomImg);
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1090;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1091;
                }else {
                    mDC.mAreaData.updateRoomNameAndRoomImg(mDC.sMasterCode, roomDataInfo.getRoomIndex(), roomName, roomImg);
                    roomDataInfo.setRoomName(roomName);
                    roomDataInfo.setRoomImg(roomImg);
                    msg.what = 0x1092;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void areaEditDeleteArea(View view){
        if(mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size()>0){
            Toast.makeText(AreaEdit.this,"该区域存在电器，不能删除",Toast.LENGTH_LONG).show();
            return;
        }
        new AlertDialog.Builder(AreaEdit.this).setTitle("删除房间：" + roomDataInfo.getRoomName())
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                            {
                                new Thread(){
                                    @Override
                                    public void run() {
                                        String result = mDC.mWS.deleteRoom(mDC.sMasterCode, roomDataInfo.getRoomIndex(), roomDataInfo.getRoomSequ());
                                        Message msg = new Message();
                                        if(result.startsWith("-2")){
                                            msg.what = 0x1080;
                                        }else if(result.startsWith("-1")){
                                            msg.what = 0x1081;
                                        }else {
                                            mDC.mAreaData.deleteRoom(mDC.sMasterCode, roomDataInfo.getRoomIndex(), roomPosition);
                                            mDC.mAreaList.remove(roomPosition);
                                            msg.what = 0x1082;
                                        }
                                        handler.sendMessage(msg);
                                    }
                                }.start();

                            }
                        })
                .setNegativeButton("取消", null).show();

    }

    private void loadChannelList(final int position) {
        // 初始化数据
        Business.getInstance().getChannelList(new Handler()	{
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {
                Business.RetObject retObject = (Business.RetObject) msg.obj;
                if (msg.what == 0) {
                    mChannelInfoList = (List<ChannelInfo>) retObject.resp;
                    if(mChannelInfoList != null && mChannelInfoList.size() > 0){
                        unBindDevice(position);
                    } else{
                        Toast.makeText(AreaEdit.this, "没有设备", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AreaEdit.this, retObject.mMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void unBindDevice(final int position){
        final ChannelInfo info = getChannelInfo(position);
        Business.getInstance().unBindDevice(info.getDeviceCode(), new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Business.RetObject retObject = (Business.RetObject) msg.obj;
                if(msg.what == 0){
                    Toast.makeText(getApplicationContext(), "乐橙后台删除成功", Toast.LENGTH_SHORT).show();
                    //乐橙后台删除后，从兆峰后台删
                    deleteElectric(position);
                    mChannelInfoList.remove(info);
                }else{
                    Toast.makeText(getApplicationContext(), retObject.mMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private ChannelInfo getChannelInfo(int position){
        String electricCode = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricCode();
        for (ChannelInfo info: mChannelInfoList ) {
            if (info.getDeviceCode() != null && info.getDeviceCode().equals(electricCode)) {
                return info;
            }
        }
        return null;
    }

    private void deleteElectric(final int position){
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.deleteElectric(mDC.sMasterCode,
                        mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricCode(),
                        mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricIndex(),
                        mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricSequ(),
                        mDC.mAreaList.get(roomPosition).getRoomIndex());
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1085;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1086;
                }else {
                    mDC.mElectricData.deleteElectric(mDC.sMasterCode,
                            mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricIndex(),
                            mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricSequ(),
                            mDC.mAreaList.get(roomPosition).getRoomIndex());
                    mDC.mAreaData.loadAreaList();
                    //mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().remove(position);
                    msg.what = 0x1087;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private class ViewHolder{
        public RelativeLayout electricrl;
        public LinearLayout electricll;
        public ImageView electricImg;
        public TextView electricName;
        public ImageView delete;
        public void update() {
            // 精确计算GridView的item高度
            electricName.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            int position = (Integer) electricName.getTag();
                            // 这里是保证同一行的item高度是相同的！！也就是同一行是齐整的 height相等
                            if (position > 0 && position % 3 == 1) {
                                View v = (View) electricImg.getTag();
                                int height = v.getHeight();

                                View v1 = dragGridView.getChildAt(position - 1);
                                int height1 = v1.getHeight();
                                //System.out.println("height:"+height + "  height1:"+height1+"   height2"+height2);
                                int maxHeight = Math.max(height1,height);
                                // 得到同一行的最后一个item和前一个item想比较，把谁的height大，就把两者中
                                // height小的item的高度设定为height较大的item的高度一致，也就是保证同一
                                // 行高度相等即可
                                v.setLayoutParams(new GridView.LayoutParams(
                                        GridView.LayoutParams.MATCH_PARENT,
                                        maxHeight));
                                v1.setLayoutParams(new GridView.LayoutParams(
                                        GridView.LayoutParams.MATCH_PARENT,
                                        maxHeight));
                            }else if (position > 0 && position % 3 == 2) {
                                View v = (View) electricImg.getTag();
                                int height = v.getHeight();

                                View v1 = dragGridView.getChildAt(position - 1);
                                int height1 = v1.getHeight();
                                View v2 = dragGridView.getChildAt(position - 2);
                                int height2 = v2.getHeight();
                                //System.out.println("height:"+height + "  height1:"+height1+"   height2"+height2);
                                int maxHeight = Math.max(Math.max(height1,height2),height);
                                int minHegith = Math.min(Math.min(height1,height2),height);
                                // 得到同一行的最后一个item和前一个item想比较，把谁的height大，就把两者中
                                // height小的item的高度设定为height较大的item的高度一致，也就是保证同一
                                // 行高度相等即可
                                v.setLayoutParams(new GridView.LayoutParams(
                                        GridView.LayoutParams.MATCH_PARENT,
                                        maxHeight));
                                v1.setLayoutParams(new GridView.LayoutParams(
                                        GridView.LayoutParams.MATCH_PARENT,
                                        maxHeight));
                                v2.setLayoutParams(new GridView.LayoutParams(
                                        GridView.LayoutParams.MATCH_PARENT,
                                        maxHeight));

                            }
                        }
                    });
        }
    }
}
