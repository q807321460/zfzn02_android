package com.jia.znjj2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jia.data.DataControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212 on 2017/12/13.
 */

public class RoomList extends Activity {
    private DataControl mDC;
    private ImageView roomListbk;
    private ListView mvRoomList;
    private int electricIndex;
    private int electricSequ;
    private String result;
    private int a;
    private int[] Imageids = new int[]{
            R.drawable.area_hall,
            R.drawable.area_dinner,
            R.drawable.area_mainroom,
            R.drawable.area_secondroom,
            R.drawable.area_bookroom,
            R.drawable.area_kitchen,
            R.drawable.area_bathroom,
            R.drawable.area_balcony,
            R.drawable.area_childrenroom,
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1100:
                    Toast.makeText(RoomList.this, "移动电器失败，请检查网络", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x1101:
                    Toast.makeText(RoomList.this, "移动电器失败，稍候重试", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x1102:
                    Toast.makeText(RoomList.this, "移动电器成功", Toast.LENGTH_SHORT).show();
                    finish();
            }
        }
    };
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_room_list);
        initView();
        updateroomlist();
        addListener();
    }

    private void addListener() {
        electricIndex=AreaEdit.mvelectricindex;
        electricSequ= AreaEdit.mvelectricSequ;
        mvRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id){
                a = mDC.mAreaList.get(position).getmElectricInfoDataList().size();
                if(position == AreaEdit.mvroomposition) {
                    Toast.makeText(RoomList.this,"电器就在此区域，不用移动", Toast.LENGTH_LONG).show();
                }else{
                    wsMoveElectric(position);}
            }
        });
    }
    private void wsMoveElectric(final int position){
        new Thread(){
            public void run(){
                result= mDC.mWS.moveElectricToAnotherRoom(mDC.sMasterCode,electricIndex,position);
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1100;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1101;
                }else {
                    mDC.mElectricData.moveRoomIndex(mDC.sMasterCode,electricIndex,position);
                    mDC.mElectricData.updateElectricSequ(mDC.sMasterCode,electricSequ,AreaEdit.mvroomposition);
                    mDC.mElectricData.moveElectricSequ(mDC.sMasterCode,electricIndex,a);
                    mDC.mSceneElectricData.updateMoveElectricRoom(mDC.sMasterCode,electricIndex,position);
                    mDC.mAreaData.loadAreaList();
                    AreaEdit.areaElectricSize--;
                    msg.what = 0x1102;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    private void initView() {
        mDC = DataControl.getInstance();
        roomListbk = (ImageView) findViewById(R.id.move_room_title_back);
        mvRoomList = (ListView)findViewById(R.id.move_room_list);
    }
    public void roomlistBack(View v){
        finish();
    }
    public void updateroomlist(){
        List<Map<String,Object>> roomlistItems = new ArrayList<Map<String, Object>>();

        for(int i=0;i<mDC.mAreaList.size();i++){
            int j = mDC.mAreaList.get(i).getRoomImg();
            Map<String,Object> roomListItem = new HashMap<String,Object>();
            roomListItem.put("move_room_list_item_name",mDC.mAreaList.get(i).getRoomName());
            roomListItem.put("move_room_item_img", Imageids[j]);
            roomlistItems.add(roomListItem);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,roomlistItems,R.layout.move_room_list_demo,
                new String[]{"move_room_item_img","move_room_list_item_name"},
                new int[]{R.id.move_room_item_img,R.id.move_room_list_item_name});
        mvRoomList.setAdapter(simpleAdapter);
    }


}

