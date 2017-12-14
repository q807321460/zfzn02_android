package com.jia.znjj2;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.data.SceneData;

import java.util.ArrayList;
import java.util.HashMap;

import static com.jia.znjj2.R.id.scene_info_edit_list;

public class SceneInfoEdit extends Activity {

    private DataControl mDC;
    private static final String TAG = "SceneInfoEdit";

    private SceneData.SceneDataInfo sceneDataInfo;
    private int scenePosition;
    private TextView edSceneName;
    private TextView svSceneName;
    private ImageView ivSceneImg;
    private ListView lvElectricEdit;
    private EditText upSceneName;
    private TextView upSceneName1;
    private Spinner spSceneSwift;
    private String electricOrder;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1100:
                    Toast.makeText(SceneInfoEdit.this, "情景电器删除失败，请检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1101:
                    Toast.makeText(SceneInfoEdit.this, "情景电器删除失败，稍候重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1102:
                    Toast.makeText(SceneInfoEdit.this, "情景电器删除成功", Toast.LENGTH_LONG).show();
                    updateListView();
                case 0x1105:
                    Toast.makeText(SceneInfoEdit.this, "保存失败，请检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1106:
                    Toast.makeText(SceneInfoEdit.this, "保存失败，稍候重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1107:
                    Toast.makeText(SceneInfoEdit.this, "保存成功", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0x1160:
                    Toast.makeText(SceneInfoEdit.this, "删除情景模式失败，检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1161:
                    Toast.makeText(SceneInfoEdit.this, "删除情景模式失败，稍候重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1162:
                    Toast.makeText(SceneInfoEdit.this, "情景模式删除成功", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0x1163:
                    Toast.makeText(SceneInfoEdit.this, "编辑情景名称失败，检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1164:
                    Toast.makeText(SceneInfoEdit.this, "编辑情景名称失败，请重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x1165:
                    Toast.makeText(SceneInfoEdit.this, "编辑情景名称成功", Toast.LENGTH_LONG).show();
                    edSceneName.setVisibility(View.VISIBLE);
                    svSceneName.setVisibility(View.GONE);
                    upSceneName1.setText(sceneDataInfo.getSceneName());
                    upSceneName1.setVisibility(View.VISIBLE);
                    upSceneName.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_info_edit);
        initView();
        updateSpinner();
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
        scenePosition = getIntent().getIntExtra("scenePosition", 0);
        sceneDataInfo = mDC.mSceneList.get(scenePosition);
        spSceneSwift = (Spinner) findViewById(R.id.scene_info_edit_scene_swift);
        svSceneName = (TextView)findViewById(R.id.scene_info_save_name);
        edSceneName = (TextView) findViewById(R.id.scene_info_edit_name);
        upSceneName1 =(TextView) findViewById(R.id.scene_info_name1);
        upSceneName =(EditText) findViewById(R.id.scene_info_name11);
        ivSceneImg = (ImageView) findViewById(R.id.scene_info_edit_img);
        lvElectricEdit = (ListView) findViewById(scene_info_edit_list);
        edSceneName.setVisibility(View.VISIBLE);
        svSceneName.setVisibility(View.GONE);
        upSceneName1.setVisibility(View.VISIBLE);
        upSceneName.setVisibility(View.GONE);
        upSceneName1.setText(sceneDataInfo.getSceneName());
        ivSceneImg.setImageResource(mDC.mSceneTypeImages.getResourceId(sceneDataInfo.getSceneImg(),0));
    }

    public void sceneInfoEditBack(View view){
        finish();
    }
    public void sceneInfoEditSave(View view){
        new AlertDialog.Builder(SceneInfoEdit.this).setTitle("确定修改电器状态？" )
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                            {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(int i=0; i < lvElectricEdit.getChildCount()-1;i++) {
                                            RelativeLayout rl = (RelativeLayout) lvElectricEdit.getChildAt(i);
                                            Switch localSwitch = (Switch) rl.findViewById(R.id.scene_info_electric_item_order);
                                            if (localSwitch.isChecked()) {
                                                electricOrder = "SH";
                                            } else {
                                                electricOrder = "SG";
                                            }


                                            mDC.mWS.updateSceneElectricOrder(mDC.sMasterCode, sceneDataInfo.getSceneElectricInfos().get(i).getElectricIndex(), sceneDataInfo.getSceneIndex(), electricOrder);
                                            sceneDataInfo.getSceneElectricInfos().get(i).setElectricOrder(electricOrder);
//                                          mDC.mSceneElectricData.deleteSceneElectric(mDC.sMasterCode
//                                                    , sceneDataInfo.getSceneElectricInfos().get(i).getElectricIndex()
//                                                    , sceneDataInfo.getSceneIndex());
                                        }
                                    }
                                }).start();
                            }
                        })
                .setNegativeButton("取消", null).show();

    }
    public void sceneInfoEditAddAction(View view){
        Intent intent = new Intent(SceneInfoEdit.this, SceneAddElectric.class);
        intent.putExtra("scenePosition", scenePosition);
        startActivityForResult(intent,0);
    }
    public void editSceneName(View view){
        edSceneName.setVisibility(View.GONE);
        svSceneName.setVisibility(View.VISIBLE);
        upSceneName1.setVisibility(View.GONE);
        upSceneName.setVisibility(View.VISIBLE);
        upSceneName.setText(sceneDataInfo.getSceneName());
        upSceneName.setSelection(sceneDataInfo.getSceneName().length());
        upSceneName.setFocusable(true);
        upSceneName.setFocusableInTouchMode(true);
        upSceneName.requestFocus();
    }
    public void saveSceneName(View view){
        new Thread(){
            public void run(){
                String  sceneName = upSceneName.getText().toString();
                int SceneIndex = sceneDataInfo.getSceneIndex();
                int SceneImg =sceneDataInfo.getSceneImg();
                String result = mDC.mWS.updateSceneName(mDC.sMasterCode,SceneIndex , sceneName, SceneImg );
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1163;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1164;
                }else {
                    //
                    mDC.mSceneData.updateSceneNewname(mDC.sMasterCode, sceneDataInfo.getSceneIndex(), sceneName);
                    sceneDataInfo.setSceneName(sceneName);
                    msg.what = 0x1165;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 0) {

        }
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
        SceneElectricEditAdapter adapter = new SceneElectricEditAdapter(localList, this,
                new String[] {"electric_delete", "electric_img", "electric_name","electric_order", "scene_ll" },
                new int[] {R.id.scene_info_electric_item_delete, R.id.scene_info_electric_item_img,
                        R.id.scene_info_electric_item_name,R.id.scene_info_electric_item_order,
                        R.id.scene_info_electric_item_rl });
        lvElectricEdit.setAdapter(adapter);
    }

    public void sceneInfoEditDeleteScene(View view){
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.deleteScene(mDC.sMasterCode
                        , mDC.mSceneList.get(scenePosition).getSceneIndex()
                        , mDC.mSceneList.get(scenePosition).getSceneSequ());
                Message msg = new Message();
                if(result.startsWith("-2")){
                    msg.what = 0x1160;
                }else if(result.startsWith("-1")){
                    msg.what = 0x1161;
                }else if(result.startsWith("1")){
                    msg.what = 0x1162;
                    mDC.mSceneData.deleteScene(mDC.sMasterCode
                            , mDC.mSceneList.get(scenePosition).getSceneIndex()
                            , mDC.mSceneList.get(scenePosition).getSceneSequ());
                    mDC.mSceneList.remove(scenePosition);
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void updateSpinner()
    {
        BaseAdapter localAdapter = new BaseAdapter()
        {
            public int getCount()
            {
                return mDC.mSceneSwiftList.size();
            }

            public Object getItem(int paramAnonymousInt)
            {
                return null;
            }

            public long getItemId(int paramAnonymousInt)
            {
                return 0L;
            }

            public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
            {
                DisplayMetrics loacalDisplayMetrics = new DisplayMetrics();
                SceneInfoEdit.this.getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
                int i = loacalDisplayMetrics.heightPixels;
                LinearLayout linearLayout = new LinearLayout(SceneInfoEdit.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);
                ImageView imageView = new ImageView(SceneInfoEdit.this);
                imageView.setBackgroundResource(mDC.mElectricTypeImages.getResourceId(mDC.mSceneSwiftList.get(paramAnonymousInt).getElectricType(), 0));
                imageView.setLayoutParams(new ViewGroup.LayoutParams(i / 9, i / 9));
                linearLayout.addView(imageView);
                TextView textView = new TextView(SceneInfoEdit.this);
                textView.setText(mDC.mSceneSwiftList.get(paramAnonymousInt).getElectricName());
                textView.setTextColor(Color.BLACK);
                if (i <= 800)
                {
                    textView.setPadding(35, 25, 0, 0);
                    textView.setTextSize(18.0F);
                }
                else
                {

                    textView.setPadding(35, 55, 0, 0);
                    textView.setTextSize(22.0F);
                }
                linearLayout.addView(textView);
                return linearLayout;
            }
        };
        spSceneSwift.setAdapter(localAdapter);
    }

    public class SceneElectricEditAdapter extends BaseAdapter
    {
        private SceneModeViewHolder holder;
        private String[] keyString;
        private ArrayList<HashMap<String, String>> mAppList;
        private Context mContext;
        private LayoutInflater mInflater;
        private int[] valueViewID;

        public SceneElectricEditAdapter(ArrayList<HashMap<String, String>> paramArrayList, Context context,
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
            paramView.setTag(this.holder);
            HashMap<String,String> localHashMap = (HashMap)this.mAppList.get(paramInt);
            if (localHashMap != null)
            {
                String str1 = (String)((HashMap)localHashMap).get(this.keyString[2]);
                String orderInfo = (String)((HashMap)localHashMap).get("order_info");
                this.holder.electricName.setText(str1);
                int resourceId = Integer.parseInt(localHashMap.get(keyString[1]));

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
            }
            holder.electricDelete.setOnClickListener(new SceneElectricDeleteListener(paramInt));
            return paramView;
        }

        /**
         * 账户删除按钮的事件监听类
         */
        class SceneElectricDeleteListener
                implements View.OnClickListener
        {
            private int position;

            SceneElectricDeleteListener(int paramInt)
            {
                this.position = paramInt;
            }

            public void onClick(View paramView)
            {
                new AlertDialog.Builder(SceneInfoEdit.this).setTitle("删除情景模式的电器：" )
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                    {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                String result = mDC.mWS.deleteSceneElectric(mDC.sMasterCode
                                                        , sceneDataInfo.getSceneElectricInfos().get(position).getElectricIndex()
                                                        , sceneDataInfo.getSceneIndex());
                                                Message msg = new Message();
                                                if(result.startsWith("-2")){
                                                    msg.what = 0x1100;
                                                }else if(result.startsWith("-1")) {
                                                    msg.what = 0x1101;
                                                }else if(result.startsWith("1")){
                                                    mDC.mSceneElectricData.deleteSceneElectric(mDC.sMasterCode
                                                            , sceneDataInfo.getSceneElectricInfos().get(position).getElectricIndex()
                                                            , sceneDataInfo.getSceneIndex());
                                                    sceneDataInfo.getSceneElectricInfos().remove(position);
                                                    msg.what = 0x1102;
                                                }
                                                handler.sendMessage(msg);
                                            }
                                        }.start();


                                    }
                                })
                        .setNegativeButton("取消", null).show();
            }
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
}
