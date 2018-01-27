package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jia.data.DataControl;
import com.jia.data.SceneData;
import com.jia.data.SceneElectricData;
import com.jia.util.NetworkUtil;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SceneInfo extends Activity {
    private static final String TAG = "SceneInfo";
    private DataControl mDC;

    private int scenePosition;
    private ImageView ivSceneImg;
    private TextView tvSceneName;
    private ListView lvSceneElectricList;
    private SceneData.SceneDataInfo sceneDataInfo;
    public static String tmMastercode;
    public static int tmsceneindex;
    public static int sceneNumber;

    Socket socket = NetworkUtil.socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_info);
        initView();
        updateListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        updateListView();
    }

    private void initView(){
        mDC = DataControl.getInstance();
        scenePosition = getIntent().getIntExtra("scenePosition",-1);
        if(scenePosition != -1 && scenePosition < mDC.mSceneList.size()){
            sceneNumber=scenePosition;
            sceneDataInfo = mDC.mSceneList.get(scenePosition);
        }
        ivSceneImg = (ImageView) findViewById(R.id.scene_info_img);
        tvSceneName = (TextView) findViewById(R.id.scene_info_name);
        lvSceneElectricList = (ListView) findViewById(R.id.scene_info_list);
        tvSceneName.setText(sceneDataInfo.getSceneName());
        ivSceneImg.setImageResource(mDC.mSceneTypeImages.getResourceId(sceneDataInfo.getSceneImg(),0));
        tmMastercode = mDC.sMasterCode;
        tmsceneindex = sceneDataInfo.getSceneIndex();
    }

    public void updateListView()
    {
        int i = sceneDataInfo.getSceneElectricInfos().size();
        ArrayList<HashMap<String,String>> localList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            HashMap<String,String> localHashMap = new HashMap<>();
            localHashMap.put("electric_img", ""+sceneDataInfo.getSceneElectricInfos().get(j).getElectricType());
            localHashMap.put("electric_name", sceneDataInfo.getSceneElectricInfos().get(j).getElectricName());
            localHashMap.put("electric_order", sceneDataInfo.getSceneElectricInfos().get(j).getElectricOrder());
            localHashMap.put("order_info", sceneDataInfo.getSceneElectricInfos().get(j).getOrderInfo());
            localList.add(localHashMap);
        }
        SceneElectricAdapter adapter = new SceneElectricAdapter(localList, this,
                new String[] {"electric_delete", "electric_img", "electric_name","electric_order", "scene_ll" },
                new int[] {R.id.scene_info_electric_item_delete, R.id.scene_info_electric_item_img,
                        R.id.scene_info_electric_item_name,R.id.scene_info_electric_item_order,
                        R.id.scene_info_electric_item_rl });
        lvSceneElectricList.setAdapter(adapter);
    }

    public class SceneElectricAdapter extends BaseAdapter
    {
        private SceneModeViewHolder holder;
        private String[] keyString;
        private ArrayList<HashMap<String, String>> mAppList;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] valueViewID;

        public SceneElectricAdapter(ArrayList<HashMap<String, String>> paramArrayList, Context context,
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

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            paramView = this.mInflater.inflate(R.layout.scene_info_electric_item, null);
            holder = new SceneModeViewHolder();
            holder.electricDelete = ((ImageView) paramView.findViewById(this.valueViewID[0]));
            holder.electricImg = ((ImageView) paramView.findViewById(this.valueViewID[1]));
            holder.electricName = ((TextView)paramView.findViewById(this.valueViewID[2]));
            holder.electricOrder = ((Switch) paramView.findViewById(this.valueViewID[3]));
            holder.listItem = ((RelativeLayout) paramView.findViewById(this.valueViewID[4]));
            holder.electricDelete.setVisibility(View.GONE);
            paramView.setTag(this.holder);
            HashMap<String,String> localHashMap = (HashMap)this.mAppList.get(paramInt);
            if (localHashMap != null)
            {
                String str1 = (String)((HashMap)localHashMap).get(this.keyString[2]);
                String orderInfo = (String)((HashMap)localHashMap).get("order_info");
                this.holder.electricName.setText(str1);
                int resourceId = Integer.parseInt(localHashMap.get(keyString[1]));


                //this.holder.electricImg.setImageResource(mDC.mElectricTypeImages.getResourceId(resourceId,0));

                if (resourceId == 2){
                    if (orderInfo.equals("01")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_left);
                    }else if(orderInfo.equals("02")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_right);
                    }
                }else if(resourceId == 3){
                    if (orderInfo.equals("01")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_left);
                    }else if(orderInfo.equals("02")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_center);

                    }else if(orderInfo.equals("03")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_right);
                    }
                }else if (resourceId == 4){
                    if (orderInfo.equals("01")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left1);
                    }else if(orderInfo.equals("02")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left2);
                    }else if(orderInfo.equals("03")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right2);
                    }else if(orderInfo.equals("04")){
                        holder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right1);
                    }
                }else {
                    holder.electricImg.setImageResource(mDC.mElectricTypeImages.getResourceId(resourceId,0));
                }



                if(localHashMap.get(keyString[3]).equals("XG") || localHashMap.get(keyString[3]).equals("SG")){
                    holder.electricOrder.setChecked(false);
                }else if(localHashMap.get(keyString[3]).equals("XH") || localHashMap.get(keyString[3]).equals("SH")){
                    holder.electricOrder.setChecked(true);
                }
                holder.electricOrder.setClickable(false);
            }
            return paramView;
        }

        private class SceneModeViewHolder
        {
            ImageView electricDelete;
            TextView electricName;
            ImageView electricImg;
            Switch electricOrder;
            RelativeLayout listItem;

            private SceneModeViewHolder() {}
        }

    }


    public void sceneInfoBack(View view){
        finish();
    }
    public void sceneInfoEdit(View view){
        Log.e(TAG, "sceneInfoEdit: ", null);
        Intent intent = new Intent(SceneInfo.this, SceneInfoEdit.class);
        intent.putExtra("scenePosition",scenePosition);
        startActivity(intent);
    }
    public void sceneInfoExecute(View view) throws InterruptedException {
        final List<SceneElectricData.SceneElectricInfo> sceneElectricInfos = mDC.mSceneList.get(scenePosition).getSceneElectricInfos();
        if (NetworkUtil.out == null || mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,"********","TH","**"+mDC.mSceneList.get(scenePosition).getSceneIndex()+"*******");
                    System.out.println("远程控制电器：<"+mDC.sMasterCode+"********"+"TH"+"**"+mDC.mSceneList.get(scenePosition).getSceneIndex()+"*******"+"00>");
                }
            }.start();

        }else{
            //本地socket通信
//            for(int i = 0; i < sceneElectricInfos.size();i++) {
//                System.out.println(sceneElectricInfos.get(i));
//                String order = "<" + sceneElectricInfos.get(i).getElectricCode()+sceneElectricInfos.get(i).getElectricOrder() + sceneElectricInfos.get(i).getOrderInfo()+ "********00" + ">";
//                System.out.println("本地控制电气： "+order);
//                NetworkUtil.out.println(order);
//                NetworkUtil.out.flush();
//                Thread.sleep(20);
//            }
            String order = "<" + "********"+"TH"+ "**"+mDC.mSceneList.get(scenePosition).getSceneIndex()+"*******00" + ">";
            System.out.println("本地控制电气： "+order);
            NetworkUtil.out.println(order);
            NetworkUtil.out.flush();

        }
    }
    public void sceneTimeSelector(View view){
        Intent intent = new Intent(SceneInfo.this, TimeSelector.class);
        startActivity(intent);
    }
    private boolean checkNetConnection() {
        try{
            socket.sendUrgentData(0xFF);
        }catch(Exception ex){
            System.out.println("Socket通信异常");
            mDC.bIsRemote = true;
            /*启动后台Service服务，接受网络数据*/
            return false;
        }
        boolean isConnected = socket.isConnected() && !socket.isClosed() && socket.isBound()&& !socket.isOutputShutdown()&& !socket.isInputShutdown();
        System.out.println("Socket状态"+isConnected);
        //判断网络是否连接上了，没有则提示，并返回
        if ((socket ==null) || !(socket.isConnected() && !socket.isClosed())){
            new AlertDialog.Builder(SceneInfo.this).setTitle("网络连接失败，请检查网络" )
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                {
                                }
                            }).show();      //警告对话框
            return true;
        }
        return false;
    }
}
