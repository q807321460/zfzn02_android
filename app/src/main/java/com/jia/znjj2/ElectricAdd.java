package com.jia.znjj2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.camera.BindUserActivity;
import com.jia.camera.business.Business;
import com.jia.camera.manager.DeviceAddActivity;
import com.jia.connection.MasterSocket;
import com.jia.data.DataControl;
import com.jia.data.RoomData;
import com.jia.ir.db.ETDB;
import com.jia.ir.etclass.ETDevice;
import com.jia.ir.etclass.ETDeviceAIR;
import com.jia.ir.etclass.ETDeviceTV;
import com.jia.model.ETKeyLocal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import et.song.device.DeviceType;
import et.song.jni.ir.ETIR;
import et.song.remote.face.IR;
import et.song.remote.instance.AIR;

/**
 * Created by Administrator on 2016/10/23.
 */
public class ElectricAdd extends Activity {

    private LinearLayout mLLElectricAdd1;
    private LinearLayout mLLElectricAdd2;
    private LinearLayout mLLElectricName1;
    private LinearLayout mLLElectricName2;
    private LinearLayout mLLElectricName3;
    private LinearLayout mLLElectricName4;
    private GridView mGvElectricAdd1;
    private ImageView mIvElectricTypeImg;
    private TextView mTvElectricTypeName;
    private Spinner mSpElectricAdd2;
    private EditText mEtElectricName1;
    private EditText mEtElectricName2;
    private EditText mEtElectricName3;
    private EditText mEtElectricName4;
    private int electricIndex;
    private DataControl mDC;
    private ProgressDialog dialog;
    private IR mIR = null;
    private int iAreaSequ;
    private int iElectricType;
    private String str2;
    String[] mDevTypeNames;
    TypedArray mDevTyeImages;
    private TypedArray mAreaTypeImages;
    List<Map<String,Object>> listItems;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0x2221:
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(ElectricAdd.this, "添加电器成功", Toast.LENGTH_LONG).show();
                    mDC.mAreaData.loadAreaList();   //添加电器成功后重新读取本地数据库电器数据，刷新情景控制开关
                    break;
                case 0x2222:
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(ElectricAdd.this, "不存在待添加的电器", Toast.LENGTH_LONG).show();
                    break;
                case 0x2223:
                    gotoIrWizards((String) msg.obj);
                    break;
                case 0x2224:
                    gotoTvWizards((String) msg.obj);
                    break;
                case 0x2225:
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(ElectricAdd.this, "添加电器成功", Toast.LENGTH_LONG).show();
                    gotoIrLearnWizards((String) msg.obj);
                    break;
                case 0x2226:
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(ElectricAdd.this, "添加电器成功", Toast.LENGTH_LONG).show();
                    gotoTvLearnWizards((String) msg.obj);
                    break;
                case 0x2227:
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(ElectricAdd.this, "删除电器失败", Toast.LENGTH_LONG).show();
                    break;
                case 0x2228:
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(ElectricAdd.this, "删除电器失败，请联网", Toast.LENGTH_LONG).show();
                    break;
                case 0x2229:
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(ElectricAdd.this, "联网失败，请重试", Toast.LENGTH_LONG).show();
                    break;
                case 0x2230:
                    new AlertDialog.Builder(ElectricAdd.this).setTitle("电器已存在，请确认是否删除该电器重新添加？" )
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                        {
                                            for(int i=0;i<mDC.mAreaList.size();i++){
                                                for (int j=0;j<mDC.mAreaList.get(i).getmElectricInfoDataList().size();j++){
                                                    if (mDC.mAreaList.get(i).getmElectricInfoDataList().get(j).getElectricCode().equals(str2)) {
                                                        String result = mDC.mWS.deleteElectric(mDC.sMasterCode,
                                                                mDC.mAreaList.get(i).getmElectricInfoDataList().get(j).getElectricCode(),
                                                                mDC.mAreaList.get(i).getmElectricInfoDataList().get(j).getElectricIndex(),
                                                                mDC.mAreaList.get(i).getmElectricInfoDataList().get(j).getElectricSequ(),
                                                                mDC.mAreaList.get(i).getRoomIndex());

                                                        if(result.startsWith("-2")){
                                                            return;
                                                        }else if(result.startsWith("-1")){
                                                            return;
                                                        }else {
                                                            mDC.mElectricData.deleteElectric(mDC.sMasterCode,
                                                                    mDC.mAreaList.get(i).getmElectricInfoDataList().get(j).getElectricIndex(),
                                                                    mDC.mAreaList.get(i).getmElectricInfoDataList().get(j).getElectricSequ(),
                                                                    mDC.mAreaList.get(i).getRoomIndex());


                                                        }
                                                        //handler.sendMessage(msg3);
                                                    }

                                                }
                                                mDC.mAreaData.loadAreaList();

                                            }
                                            addElectric(str2);
                                        }
                                    })
                            .setNegativeButton("取消", null).show();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_add);
        initView();
        updateSpinner();
        addListener();
    }
    private void initView(){
        mDC = DataControl.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在添加电器");
        mIR = ETIR.Builder(DeviceType.DEVICE_REMOTE_AIR);
        mLLElectricAdd1 = (LinearLayout) findViewById(R.id.electric_add1_ll);
        mLLElectricAdd2 = (LinearLayout) findViewById(R.id.electric_add2_ll);
        mLLElectricName1 = (LinearLayout) findViewById(R.id.electric_add2_electric_name1_ll);
        mLLElectricName2 = (LinearLayout) findViewById(R.id.electric_add2_electric_name2_ll);
        mLLElectricName3 = (LinearLayout) findViewById(R.id.electric_add2_electric_name3_ll);
        mLLElectricName4 = (LinearLayout) findViewById(R.id.electric_add2_electric_name4_ll);
        mGvElectricAdd1 = (GridView) findViewById(R.id.electric_add1_type);
        mIvElectricTypeImg = (ImageView) findViewById(R.id.electric_add2_type_img);
        mTvElectricTypeName = (TextView) findViewById(R.id.electric_add2_type_text);
        mSpElectricAdd2 = (Spinner) findViewById(R.id.electric_add2_area_belong);
        mEtElectricName1 = (EditText) findViewById(R.id.electric_add2_electric_name1);
        mEtElectricName2 = (EditText) findViewById(R.id.electric_add2_electric_name2);
        mEtElectricName3 = (EditText) findViewById(R.id.electric_add2_electric_name3);
        mEtElectricName4 = (EditText) findViewById(R.id.electric_add2_electric_name4);
        mLLElectricAdd1.setVisibility(View.VISIBLE);
        mLLElectricAdd2.setVisibility(View.GONE);

        Intent intent = getIntent();
        iAreaSequ = intent.getIntExtra("roomSequ",0);

        mDevTypeNames = getResources().getStringArray(R.array.dev_type_names);
        mDevTyeImages = getResources().obtainTypedArray(R.array.dev_type_images);
        mAreaTypeImages = getResources().obtainTypedArray(R.array.area_type_press_images);
        //创建一个List对象，List对象的元素是Map
        listItems = new ArrayList<>();
        for (int i = 0;i<mDevTypeNames.length;i++)
        {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("image", mDevTyeImages.getResourceId(i,0));
            listItem.put("name", mDevTypeNames[i]);
            listItems.add(listItem);
        }
        //创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.cell_electric_type, new String[]{"image","name"},
                new int[]{R.id.electric_add_image,R.id.electric_add_name});
        mGvElectricAdd1.setAdapter(simpleAdapter);

    }

    private void updateSpinner()
    {
        BaseAdapter localAdapter = new BaseAdapter()
        {
            public int getCount()
            {
                return mDC.mAreaData.getAreaSize();
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
                ElectricAdd.this.getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
                int i = loacalDisplayMetrics.heightPixels;
                LinearLayout linearLayout = new LinearLayout(ElectricAdd.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);
                ImageView imageView = new ImageView(ElectricAdd.this);
                imageView.setBackgroundResource(mAreaTypeImages.getResourceId(mDC.mAreaList.get(paramAnonymousInt).getRoomImg(), 0));
                imageView.setLayoutParams(new ViewGroup.LayoutParams(i / 9, i / 9));
                linearLayout.addView(imageView);
                TextView textView = new TextView(ElectricAdd.this);
                textView.setText(mDC.mAreaList.get(paramAnonymousInt).getRoomName());
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
        mSpElectricAdd2.setAdapter(localAdapter);
        /*设置默认的房间为添加电器的入口房间*/
        if(iAreaSequ<localAdapter.getCount()){
            mSpElectricAdd2.setSelection(iAreaSequ);
        }
    }

    private void addListener() {
        //添加列表项被选中的监听器
        mGvElectricAdd1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iElectricType = position;
                if (iElectricType != 8) {
                    mLLElectricAdd1.setVisibility(View.GONE);
                    mLLElectricAdd2.setVisibility(View.VISIBLE);
                    if (position == 2) {
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.VISIBLE);
                        mLLElectricName3.setVisibility(View.GONE);
                        mLLElectricName4.setVisibility(View.GONE);
                    } else if (position == 3) {
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.VISIBLE);
                        mLLElectricName3.setVisibility(View.VISIBLE);
                        mLLElectricName4.setVisibility(View.GONE);
                    } else if (position == 4 || position == 10) {
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.VISIBLE);
                        mLLElectricName3.setVisibility(View.VISIBLE);
                        mLLElectricName4.setVisibility(View.VISIBLE);
                    } else {
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.GONE);
                        mLLElectricName3.setVisibility(View.GONE);
                        mLLElectricName4.setVisibility(View.GONE);
                    }

                    mIvElectricTypeImg.setImageResource(mDevTyeImages.getResourceId(iElectricType, 0));
                    mTvElectricTypeName.setText(mDevTypeNames[iElectricType]);
                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //添加列表项被单击的监听器
        mGvElectricAdd1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iElectricType = position;
                if (iElectricType != 8) {
                    mLLElectricAdd1.setVisibility(View.GONE);
                    mLLElectricAdd2.setVisibility(View.VISIBLE);
                    if(position == 2){
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.VISIBLE);
                        mLLElectricName3.setVisibility(View.GONE);
                        mLLElectricName4.setVisibility(View.GONE);
                    }else if(position == 3){
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.VISIBLE);
                        mLLElectricName3.setVisibility(View.VISIBLE);
                        mLLElectricName4.setVisibility(View.GONE);
                    }else if(position == 4 || position == 10){
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.VISIBLE);
                        mLLElectricName3.setVisibility(View.VISIBLE);
                        mLLElectricName4.setVisibility(View.VISIBLE);
                    }else if(position == 9 || position == 12){
                        mLLElectricName1.setVisibility(View.GONE);
                        mLLElectricName2.setVisibility(View.GONE);
                        mLLElectricName3.setVisibility(View.GONE);
                        mLLElectricName4.setVisibility(View.GONE);
                    }else  {
                        mLLElectricName1.setVisibility(View.VISIBLE);
                        mLLElectricName2.setVisibility(View.GONE);
                        mLLElectricName3.setVisibility(View.GONE);
                        mLLElectricName4.setVisibility(View.GONE);
                    }

                    mIvElectricTypeImg.setImageResource(mDevTyeImages.getResourceId(iElectricType,0));
                    mTvElectricTypeName.setText(mDevTypeNames[iElectricType]);
                }else {
                    Business.getInstance().userlogin(mDC.sLePhoneNumber,new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if(0 == msg.what){
                                //该手机号与账户已经绑定
                                LoginActivity.userlogin(mDC.sLePhoneNumber);
                                Intent intent = new Intent(ElectricAdd.this, DeviceAddActivity.class);
                                intent.putExtra("roomSequ",iAreaSequ);
                                startActivity(intent);
                            }else {
                                //该手机号与账户没有绑定
                                new AlertDialog.Builder(ElectricAdd.this).setTitle("添加摄像头需先注册乐橙账号")
                                        .setPositiveButton("确定",
                                                new DialogInterface.OnClickListener()
                                                {
                                                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                                                    {
                                                        Intent intent = new Intent(ElectricAdd.this, BindUserActivity.class);
                                                        intent.putExtra("phoneNumber",mDC.mAccount.getLePhone());
                                                        startActivity(intent);
                                                    }
                                                })
                                        .setNegativeButton("取消", null).show();
                            }
                        }
                    });

                }
            }
        });

    }

    /**
     * 用于判断是否已经注册乐橙账号
     * @return
     */
    private boolean checkLeChengSign(){
        if(mDC.mAccount.getLeSign() == 1){
            return true;
        }
        return false;
    }

    public void electricAddBack(View view){
        if(mLLElectricAdd1.getVisibility()==View.VISIBLE){
            finish();
        }else if(mLLElectricAdd2.getVisibility() == View.VISIBLE){
            mLLElectricAdd1.setVisibility(View.VISIBLE);
            mLLElectricAdd2.setVisibility(View.GONE);
        }
    }

    public void electricAdd2Back(View view){
        mLLElectricAdd1.setVisibility(View.VISIBLE);
        mLLElectricAdd2.setVisibility(View.GONE);
    }

    public void electricAdd2Sure(View view){
        dialog.show();

          //  IsExistElectric(masterCode: gDC.mUserInfo.m_sMasterCode, electricCode: electricCode)


        String jiaoyan = "00";
        StringBuffer sb = new StringBuffer();
        sb.append('<').append(mDC.electricTypeCode[iElectricType]);

        if(mDC.electricTypeCode[iElectricType].length()==4){
            sb.append("0000");
        }else {  
            sb.append(mDC.addleft);
        }
        final String str1 = sb.append(mDC.addSign).append('0').append("**********").append(jiaoyan).append('>').toString();
        //final String str1 = "<00000000Y0**********00>";
        System.out.println(str1);
        new Thread(){
            @Override
            public void run() {
                 str2= new MasterSocket().getInfoFromMasterNode(str1);
                System.out.println("添加电器返回str2： " +str2);
                if (str2 != null &&str2.startsWith("#")&&str2.length() ==24){
                    str2 = str2.substring(1,9);
                }else if(str2 != null&&str2.startsWith("#")&& str2.length() ==28){
                    str2 = str2.substring(1,13);
                }else {
                    str2=null;
                }

                System.out.println("str2**********： " +str2);
                String res=mDC.mWS.IsExistElectric(mDC.sMasterCode,str2);
                if(res.startsWith("1")){
                    Message msg4=new Message();
                    msg4.what=0x2230;
                    handler.sendMessage(msg4);



                }else if(res.startsWith("0")){
                   addElectric(str2);

                }else if(res.startsWith("-2")){
                    Message msg = new Message();
                    msg.what=0x2229;
                    handler.sendMessage(msg);

                }
                //str2="0900FF00";  //本地测试

                //finish();
            }
        }.start();

    }
   private void addElectric(String str){
       Message msg = new Message();
       int signSize = mDC.electricTypeCode[iElectricType].length();
       if(str!= null  && str.substring(0,signSize).equals(mDC.electricTypeCode[iElectricType])){
           String extra = null;
           if(iElectricType == 2){
               extra = mEtElectricName1.getText().toString()+ "|" + mEtElectricName2.getText().toString();
           }else if(iElectricType == 3){
               extra = mEtElectricName1.getText().toString()+ "|" + mEtElectricName2.getText().toString()+"|"
                       + mEtElectricName3.getText().toString();
           }else if(iElectricType == 4 || iElectricType == 10){
               extra = mEtElectricName1.getText().toString()+ "|" + mEtElectricName2.getText().toString()+"|"
                       + mEtElectricName3.getText().toString()+ "|" + mEtElectricName4.getText().toString();
           }else if(iElectricType == 9){
               Message msg2= new Message();
               msg2.what = 0x2223;
               msg2.obj = str;
               handler.sendMessage(msg2);
               return;
           }else if(iElectricType == 12){
               Message msg2= new Message();
               msg2.what = 0x2224;
               msg2.obj = str;
               handler.sendMessage(msg2);
               return;
           }
           else if(iElectricType==21){
               Message msg2= new Message();
               msg2.what = 0x2225;
               msg2.obj = str;
               handler.sendMessage(msg2);
               return;
           }else if(iElectricType==24){
               Message msg2= new Message();
               msg2.what = 0x2226;
               msg2.obj = str;
               handler.sendMessage(msg2);
               return;
           }
           mDC.mElectricData.addElectric(iElectricType, str2, mEtElectricName1.getText().toString(),
                   mDC.mAreaList.get(mSpElectricAdd2.getSelectedItemPosition()).getRoomIndex(),
                   mSpElectricAdd2.getSelectedItemPosition(),extra);
           msg.what = 0x2221;
           handler.sendMessage(msg);
           finish();
       }else {
           System.out.println("不存在待添加的电器");
           msg.what = 0x2222;
           handler.sendMessage(msg);
           //Toast.makeText(ElectricAddDetail.this,"不存在待添加的电器",Toast.LENGTH_LONG).show();
       }
     

   }
    private void gotoIrWizards(String electricCode) {

        Intent intent = new Intent(ElectricAdd.this, IRWizardsActivity.class);
        intent.putExtra("electricCode", electricCode);
        intent.putExtra("roomSequ", iAreaSequ);
        if(iElectricType == 9){
            intent.putExtra("electricType", DeviceType.DEVICE_REMOTE_AIR);
        }
        startActivity(intent);
        if(dialog.isShowing()){
            dialog.cancel();
        }
    }
private void gotoIrLearnWizards(String electricCode){
    RoomData.RoomDataInfo roomDataInfo = mDC.mAreaList.get(iAreaSequ);
    ETDevice device = null;
    device = new ETDeviceAIR(-1);//mRow, mIndex
    device.SetName(mEtElectricName1.getText().toString());
    device.SetType(21);
    device.SetRes(7);
    device.SetGID(roomDataInfo.getRoomIndex());
    device.setmElectricCode(electricCode);
    electricIndex = mDC.mElectricData.getMaxElectricIndex(mDC.sAccountCode,mDC.sMasterCode)+1;
    device.setmElectricIndex(electricIndex);
    device.setmMasterCode(mDC.sMasterCode);
    device.setmRoomIndex(roomDataInfo.getRoomIndex());
    device.setmRoomSequ(iAreaSequ);
    ((ETDeviceAIR)device).SetTemp(((AIR) mIR).GetTemp());
    ((ETDeviceAIR)device).SetMode(((AIR) mIR).GetMode());
    ((ETDeviceAIR)device).SetPower(((AIR) mIR).GetPower());
    ((ETDeviceAIR)device).SetWindRate(((AIR) mIR).GetWindRate());
    ((ETDeviceAIR)device).SetWindDir(((AIR) mIR).GetWindDir());
    ((ETDeviceAIR)device).SetAutoWindDir(((AIR) mIR).GetAutoWindDir());
  ((ETDeviceAIR)device).Inster(ETDB.getInstance(ElectricAdd.this));
    new AddKeyAsyncTask().execute();
}
    private void gotoTvLearnWizards(String electricCode){
        RoomData.RoomDataInfo roomDataInfo = mDC.mAreaList.get(iAreaSequ);
        ETDevice device = null;
        device = new ETDeviceTV(-1);//mRow, mIndex
        device.SetName(mEtElectricName1.getText().toString());
        device.SetType(24);
        device.SetRes(7);
        device.SetGID(roomDataInfo.getRoomIndex());
        device.setmElectricCode(electricCode);
        electricIndex = mDC.mElectricData.getMaxElectricIndex(mDC.sAccountCode,mDC.sMasterCode)+1;
        device.setmElectricIndex(electricIndex);
        device.setmMasterCode(mDC.sMasterCode);
        device.setmRoomIndex(roomDataInfo.getRoomIndex());
        device.setmRoomSequ(iAreaSequ);

        ((ETDevice)device).Inster(ETDB.getInstance(ElectricAdd.this));
        new AddKeyAsyncTask().execute();
    }
    class AddKeyAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String sql = "SELECT * FROM ETKEY WHERE master_code = ? and electric_index = ?";
            try{
                Cursor cursor = ETDB.getInstance(ElectricAdd.this).queryData2Cursor(sql, new String[]{mDC.sMasterCode,""+electricIndex});
                int j = cursor.getCount();
                ArrayList<ETKeyLocal> locallist = new ArrayList<>();
                for (int i = 0; i < j; i++) {
                    cursor.moveToPosition(i);
                    ETKeyLocal etKeyLocal = new ETKeyLocal();
                    //etKeyLocal.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    etKeyLocal.setMasterCode(cursor.getString(cursor.getColumnIndex("master_code")));
                    etKeyLocal.setElectricIndex(cursor.getInt(cursor.getColumnIndex("electric_index")));
                    etKeyLocal.setDid(cursor.getInt(cursor.getColumnIndex("did")));
                    etKeyLocal.setKeyName(cursor.getString(cursor.getColumnIndex("key_name")));
                    etKeyLocal.setKeyRes(cursor.getInt(cursor.getColumnIndex("key_res")));
                    etKeyLocal.setKeyX(cursor.getFloat(cursor.getColumnIndex("key_x")));
                    etKeyLocal.setKeyY(cursor.getFloat(cursor.getColumnIndex("key_y")));
                    etKeyLocal.setKeyValue(cursor.getString(cursor.getColumnIndex("key_value")));
                    etKeyLocal.setKeyKey(cursor.getInt(cursor.getColumnIndex("key_key")));
                    etKeyLocal.setKeyBrandIndex(cursor.getInt(cursor.getColumnIndex("key_brandindex")));
                    etKeyLocal.setKeyBrandPos(cursor.getInt(cursor.getColumnIndex("key_brandpos")));
                    etKeyLocal.setKeyRow(cursor.getInt(cursor.getColumnIndex("key_row")));
                    etKeyLocal.setKeyState(cursor.getInt(cursor.getColumnIndex("key_state")));
                    locallist.add(etKeyLocal);
                }
                mDC.mWS.addETKeys(locallist);
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    private void gotoTvWizards(String electricCode){
        Intent intent = new Intent(ElectricAdd.this, IRWizardsActivity.class);
        intent.putExtra("electricCode", electricCode);
        intent.putExtra("roomSequ", iAreaSequ);
        if(iElectricType == 12){
            intent.putExtra("electricType", DeviceType.DEVICE_REMOTE_TV);
        }
        startActivity(intent);
        if(dialog.isShowing()){
            dialog.cancel();
        }
    }
}
