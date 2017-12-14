package com.jia.znjj2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jia.ezcamera.MainApplication;
import com.jia.ezcamera.add.ActivityAdd;
import com.jia.ezcamera.alarm.PicVideoActivity;
import com.jia.ezcamera.bean.GetDevInfoBean;
import com.jia.ezcamera.play.ImageLoader_local;
import com.jia.ezcamera.play.PlayActivity;
import com.jia.ezcamera.set.CameraSetActivity;
import com.jia.ezcamera.utils.CPlayParams;

import vv.playlib.FishEyeInfo;

/**
 * Created by Administrator on 2017/11/13.
 */
public class CameraActivity extends Activity {
    public static Handler vrHandler;
    private Button img_add_device;
    private Button img_set_device;
    private XRecyclerView list_cams;
    private MyAdapter listAdapter;
    private MainApplication mainApp;
    private Activity mActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);
        mainApp = (MainApplication)getApplication();
        vrHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.arg1) {
                    case 0:
                        //添加摄像头成功，刷新
                        if(listAdapter!=null){
                            listAdapter.refreshDevicesList();
                        }
                        break;
                    case 1:
                        //刷新list
                        if(listAdapter!=null){
                            listAdapter.refreshDevicesList();
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        initView();
    }


    private void initView(){
        img_add_device = (Button)findViewById(R.id.img_add_device);
        img_set_device = (Button)findViewById(R.id.img_set_device);
        list_cams = (XRecyclerView)findViewById(R.id.list_cams);


        img_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CameraActivity.this, ActivityAdd.class);
                startActivity(intent);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list_cams.setLayoutManager(layoutManager);

        list_cams.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        list_cams.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        list_cams.setLoadingListener(loadingListener);

        listAdapter = new MyAdapter(this);
        list_cams.setAdapter(listAdapter);

        img_set_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listAdapter != null){
                    listAdapter.setIsShowSet(!listAdapter.getIsShowSet());
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mainApp.setPreConnect();
    }

    XRecyclerView.LoadingListener loadingListener = new XRecyclerView.LoadingListener() {
        @Override
        public void onRefresh() {
            mainApp.setPreConnect();
            list_cams.refreshComplete();
        }

        @Override
        public void onLoadMore() {

        }
    };
    private View.OnClickListener linerLayoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer idInt = (Integer) v.getTag();
            int index = idInt.intValue();
            if (index < 0)
                return;
            //播放VR
            GetDevInfoBean info = mainApp.getDevInfoList().get(index);
            Intent intent = new Intent(mActivity,PlayActivity.class);
            CPlayParams cPlayParams = new CPlayParams();
            cPlayParams.chl_id = 0;
            cPlayParams.dev_id = info.dev_id;
            cPlayParams.user = "admin";
            cPlayParams.pass = info.devpass;
            cPlayParams.uid = info.channels.get(0).uid;
            cPlayParams.cloudId = info.channels.get(0).ptz;
            cPlayParams.ifp2ptalk = info.channels.get(0).voicetalk_type;
            cPlayParams.play_type = info.channels.get(0).play_type;
            FishEyeInfo fishEyeInfo = new FishEyeInfo();
            fishEyeInfo.fishType = info.channels.get(0).fisheyetype;
            fishEyeInfo.main_x1_offsize = info.channels.get(0).streams.get(0).fisheye_params.l;
            fishEyeInfo.main_x2_offsize = info.channels.get(0).streams.get(0).fisheye_params.r;
            fishEyeInfo.main_y1_offsize = info.channels.get(0).streams.get(0).fisheye_params.t;
            fishEyeInfo.main_y2_offsize = info.channels.get(0).streams.get(0).fisheye_params.b;


            cPlayParams.fishEyeInfo = fishEyeInfo;
            intent.putExtra("PlayParams", cPlayParams);
            startActivity(intent);
        }
    };


    private View.OnClickListener imgEditOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer idInt = (Integer) v.getTag();
            int index = idInt.intValue();
            if (index < 0)
                return;
            //VR设置
            String devid = mainApp.getDevInfoList().get(index).dev_id;
            Intent setIntent = new Intent(CameraActivity.this,CameraSetActivity.class);
            setIntent.putExtra("devid", devid);
            startActivity(setIntent);
        }
    };



    private View.OnClickListener imgDelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer idInt = (Integer) v.getTag();
            final int index = idInt.intValue();
            if (index < 0)
                return;


            //VR删除
            new AlertDialog.Builder(CameraActivity.this)
                    .setTitle(getText(R.string.tips_del1))
                    .setMessage(getText(R.string.tips_del2)+mainApp.getDevInfoList().get(index).dev_id+"?")
                    .setPositiveButton(getText(R.string.btn_yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            mainApp.removeVRDevice(mainApp.getDevInfoList().get(index).dev_id);
                            listAdapter.refreshDevicesList();
                        }
                    })
                    .setNegativeButton(getText(R.string.btn_no), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    };



    private View.OnClickListener imgEventListOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer idInt = (Integer) v.getTag();
            final int index = idInt.intValue();
            if (index < 0)
                return;

            //VR回放
            GetDevInfoBean info = mainApp.getDevInfoList().get(index);
            CPlayParams cPlayParams = new CPlayParams();
            cPlayParams.chl_id = 0;
            cPlayParams.dev_id = info.dev_id;
            cPlayParams.user = "admin";
            cPlayParams.pass = info.devpass;
            cPlayParams.uid = info.channels.get(0).uid;
            cPlayParams.cloudId = info.channels.get(0).ptz;
            cPlayParams.ifp2ptalk = info.channels.get(0).voicetalk_type;
            cPlayParams.play_type = info.channels.get(0).play_type;
            FishEyeInfo fishEyeInfo = new FishEyeInfo();
            fishEyeInfo.fishType = info.channels.get(0).fisheyetype;
            fishEyeInfo.main_x1_offsize = info.channels.get(0).streams.get(0).fisheye_params.l;
            fishEyeInfo.main_x2_offsize = info.channels.get(0).streams.get(0).fisheye_params.r;
            fishEyeInfo.main_y1_offsize = info.channels.get(0).streams.get(0).fisheye_params.t;
            fishEyeInfo.main_y2_offsize = info.channels.get(0).streams.get(0).fisheye_params.b;


            cPlayParams.fishEyeInfo = fishEyeInfo;
            Intent intent_playback = new Intent();
            intent_playback.setClass(mActivity, PicVideoActivity.class);
            intent_playback.putExtra("item", cPlayParams);
            startActivity(intent_playback);
        }

    };

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private MainApplication mainApp;
        private Context mContext;

        private boolean isShowSet = false;
        private static final int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
        private static final int COLOR_TEXT_DISCONN = 0xFFFF0000;
        private static final int COLOR_TEXT_ONLINE = 0xFF00FF00;

        public MyAdapter(Context context) {
            mainApp = (MainApplication)getApplication();
            mContext= context;
        }
        public void setIsShowSet(boolean isShow){
            isShowSet = isShow;
            notifyDataSetChanged();
        }

        public boolean getIsShowSet(){
            return isShowSet;
        }


        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_devlist,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {


            //显示缩略图
            GetDevInfoBean devInfo = mainApp.getDevInfoList().get(position);
            viewHolder.ivScreenShot.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageLoader_local ll = ImageLoader_local.getInstance(mContext);
            ll.loadImage(devInfo.dev_id, viewHolder.ivScreenShot);

            //显示名称
            String camName = devInfo.local_name+"-"+devInfo.dev_id;
            viewHolder.cam_name.setText(camName);

            //显示状态
            if(devInfo.p2pstatus==1){
                viewHolder.cam_status.setTextColor(COLOR_TEXT_ONLINE);
                viewHolder.cam_status.setText(getText(R.string.info_connected));
            }else{
                viewHolder.cam_status.setTextColor(COLOR_TEXT_NORMAL);
                viewHolder.cam_status.setText(getText(R.string.info_connecting));
            }

            //layout显示状态
            if(isShowSet){
                viewHolder.set_layout.setVisibility(View.VISIBLE);
            }else{
                viewHolder.set_layout.setVisibility(View.GONE);
            }


            viewHolder.img_edit.setTag(Integer.valueOf(position));
            viewHolder.img_del.setTag(Integer.valueOf(position));
            viewHolder.img_eventlist.setTag(Integer.valueOf(position));
            viewHolder.ivScreenShot.setTag(Integer.valueOf(position));

            viewHolder.img_edit.setOnClickListener(imgEditOnClickListener);
            viewHolder.img_del.setOnClickListener(imgDelOnClickListener);
            viewHolder.img_eventlist.setOnClickListener(imgEventListOnClickListener);
            viewHolder.ivScreenShot.setOnClickListener(linerLayoutOnClickListener);
        }





        public void refreshDevicesList(){
            //刷新P2P连接状态
            mainApp.setPreConnect();
            notifyDataSetChanged();
        }


        //获取数据的数量
        @Override
        public int getItemCount() {
            return mainApp.getDevInfoList().size();
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivScreenShot;
            public TextView cam_name;
            public TextView cam_status;
            public ImageView img_edit;
            public ImageView img_del;
            public ImageView img_eventlist;
            public LinearLayout set_layout;
            public ViewHolder(View view){
                super(view);
                cam_name = (TextView) view.findViewById(R.id.cam_name);
                cam_status = (TextView) view.findViewById(R.id.cam_status);
                ivScreenShot = (ImageView) view.findViewById(R.id.ivScreenShot);
                img_edit = (ImageView) view.findViewById(R.id.img_edit);
                img_del = (ImageView) view.findViewById(R.id.img_del);
                img_eventlist = (ImageView) view.findViewById(R.id.img_eventlist);
                set_layout = (LinearLayout)  view.findViewById(R.id.set_layout);
            }
        }
    }
}
