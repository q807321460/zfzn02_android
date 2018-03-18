package com.jia.znjj2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.util.NetworkUtil;
import com.jia.widget.AirCenterAdapter;
import com.jia.widget.AirCenterAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.jia.widget.AirCenterAdapter.getIsSelected;


public class AirCenterMoreActivity extends ElectricBase implements View.OnClickListener {
    public static int aircenterelectricIndex;
    public static String airCenterInfo;
    private ProgressDialog dialog;
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ListView aircenterList;
    private int checkNum;
    private ImageButton ibAddAircenter;
    private CheckBox cbCheckAll;
    private AirCenterAdapter adapter;
    public  ArrayList<String> list1;
    public  ArrayList<String> list2;
    public  ArrayList<String> list3;
    public  ArrayList<String> list4;
    public  ArrayList<String> list5;
    public  ArrayList<String> list6;
    public  ArrayList<String> list7;


    private TextView tvNo;
    String  strNumber=null;
    String order=null;
    String stAllCheck="FFFFFF";
    int i;
    private Button btOpen;
    private Button btClose;
    private Button btCold;
    private Button btHot;
    private Button btWind;
    private Button btHumit;
    private Button btHigh;
    private Button btMid;
    private Button btLow;
    private Button bt18;
    private Button bt22;
    private Button bt26;
    private Button bt30;
    private TreeMap<String,Integer> checkedStr;
    //操作取消一个时，全选取消，这个变量是是否是用户点击
    private boolean checkForAll=false;
    private String stOrder;
    private String stCount;
    private EditText etElectricName;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1060:
                    Toast.makeText(AirCenterMoreActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                    changeToNormal();
                    break;
                case 0x1061:
                    Toast.makeText(AirCenterMoreActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                    break;
                case 0x1062:
                    dialog.cancel();
                    Toast.makeText(AirCenterMoreActivity.this,"清先选择空调",Toast.LENGTH_LONG).show();
                    break;
                case 0x1063:
                    mDC.mAreaData.loadAreaList();
                    dialog.cancel();
                    Toast.makeText(AirCenterMoreActivity.this,"空调删除成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_center_more);
        initView();
        aircenterelectricIndex=electric.getElectricIndex();
        initDate();
        adapter = new AirCenterAdapter(list1,list2,list3,list4,list5,list6,list7,this);
        aircenterList.setAdapter(adapter);
        aircenterList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ViewHolder holder = (ViewHolder) arg1.getTag();
                holder.cb.toggle();
                getIsSelected().put(arg2, holder.cb.isChecked());
                if (holder.cb.isChecked() == true) {
                    checkNum++;
                } else {
                    checkNum--;
                }
                tvNo.setText("当前将选择" + checkNum + "台空调");
            }
        });
    }

    protected void onResume() {
        super.onResume();
        initView();
        checkNum=0;
        tvNo.setText("当前将选择" + checkNum + "台空调");
        electric = mDC.mAreaList.get(roomSequ).getmElectricInfoDataList().get(electricSequ);
        initDate();
        adapter = new AirCenterAdapter(list1,list2,list3,list4,list5,list6,list7,this);
        aircenterList.setAdapter(adapter);

    }

   public void initDate(){
       list1 = new ArrayList<String>();
       list2 = new ArrayList<String>();
       list3 = new ArrayList<String>();
       list4 = new ArrayList<String>();
       list5 = new ArrayList<String>();
       list6 = new ArrayList<String>();
       list7 = new ArrayList<String>();
        if(electric.getExtras().equals("[]")){
            list1.add("无空调记录");
            list2.add("开/关：" + " ");
            list3.add("模式" + " ");
            list4.add("风速" + " ");
            list5.add("温度" + " ");
            list6.add("室温" + " ");
            list7.add("错误信息" + " ");
        }else{
        String number = electric.getExtras().substring(1,electric.getExtras().length()-1);
        String No[] = number.split(",");
        for (int i = 0; i < No.length; i++) {
            String a = No[i].substring(1,3)+"-"+No[i].substring(3,5);
            list1.add(a);
        }
        for (int i = 0; i < list1.size(); i++) {
            list2.add("开/关：" + " ");
        }
        for (int i = 0; i < list1.size(); i++) {
            list3.add("模式" + " ");
        }
        for (int i = 0; i < list1.size(); i++) {
            list4.add("风速" + " ");
        }
        for (int i = 0; i < list1.size(); i++) {
            list5.add("温度" + " ");
        }
        for (int i = 0; i < list1.size(); i++) {
            list6.add("室温" + " ");
        }
        for (int i = 0; i < list1.size(); i++) {
            list7.add("错误信息" + " ");
        }
        }

    }
        @Override
    public void updateUI() {
    }
    public void AirMoreEdit(View view){
        tvTitleEdit.setVisibility(View.GONE);
        tvTitleSave.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.VISIBLE);
        etElectricName.setFocusable(true);
        etElectricName.setFocusableInTouchMode(true);
        etElectricName.requestFocus();
    }
    public void AirMoreSave(View view){
        final String electricName = etElectricName.getText().toString();
        new Thread(){
            @Override
            public void run() {
                String result = mDC.mWS.updateElectric(mDC.sMasterCode,electric.getElectricCode()
                        ,electric.getElectricIndex(),electricName,electric.getSceneIndex());
                Message msg = new Message();
                if(result.startsWith("1")){
                    msg.what = 0x1060;
                    electric.setElectricName(electricName);
                    mDC.mElectricData.updateElectric(electric.getElectricIndex(), electricName);
                    updateSceneElectricName(electric.getElectricIndex(),electricName);
                    mDC.mSceneElectricData.updateSceneElectric(electric.getElectricIndex(), electricName);
                }else {
                    msg.what = 0x1061;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    private void changeToNormal(){
        etElectricName.setFocusable(false);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleEdit.setVisibility(View.VISIBLE);
        etElectricName.setVisibility(View.GONE);
        initView();

    }
    private void initView(){
        mDC = DataControl.getInstance();
        tvTitleName = (TextView)findViewById(R.id.air_center_more_name);
        tvTitleEdit = (TextView)findViewById(R.id.air_center_more_edit);
        tvTitleSave = (TextView)findViewById(R.id.air_center_more_save);
        ibAddAircenter = (ImageButton)findViewById(R.id.air_center_more_img_add);
        tvNo = (TextView)findViewById(R.id.tv_air_center_number);
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);

        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        etElectricName= (EditText) findViewById(R.id.air_more_name);
        checkedStr=new TreeMap<String, Integer>();
        btOpen= (Button) findViewById(R.id.rb_open);
        btOpen.setOnClickListener(this);
        btClose= (Button) findViewById(R.id.rb_close);
        btClose.setOnClickListener(this);
        btCold= (Button) findViewById(R.id.rb_cold);
        btCold.setOnClickListener(this);
        btHot= (Button) findViewById(R.id.rb_hot);
        btHot.setOnClickListener(this);
        btWind= (Button) findViewById(R.id.rb_wind);
        btWind.setOnClickListener(this);
        btHumit= (Button) findViewById(R.id.rb_humit);
        btHumit.setOnClickListener(this);
        btHigh= (Button) findViewById(R.id.rb_high);
        btHigh.setOnClickListener(this);
        btMid= (Button) findViewById(R.id.rb_mid);
        btMid.setOnClickListener(this);
        btLow= (Button) findViewById(R.id.rb_low);
        btLow.setOnClickListener(this);
        bt18= (Button) findViewById(R.id.rb_18);
        bt18.setOnClickListener(this);
        bt22= (Button) findViewById(R.id.rb_22);
        bt22.setOnClickListener(this);
        bt26= (Button) findViewById(R.id.rb_26);
        bt26.setOnClickListener(this);
        bt30= (Button) findViewById(R.id.rb_30);
        bt30.setOnClickListener(this);
        tvTitleName.setText(electric.getElectricName());
        etElectricName.setText(electric.getElectricName());
        aircenterList = (ListView)findViewById(R.id.air_center_list);

    }
   public void addAirCenter(View view){
        Intent intent = new Intent(AirCenterMoreActivity.this,AddAirCenterMore.class);
        startActivity(intent);
   }
    public void AirMoreBack(View view){
        finish();
    }
    private void onDeal(String str1,String firstCount){
        i = checkedStr.size();
        strNumber= String.valueOf(i);
        int checkSum = 0;
        int count=0;
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Integer>> entryseSet=checkedStr.entrySet();
        for (Map.Entry<String, Integer> entry:entryseSet) {
            sb.append(entry.getKey());
            checkSum = checkSum + entry.getValue();
        }
        count=checkSum+Integer.parseInt(strNumber,10)+Integer.parseInt(firstCount,16);
        int mod = count % 256;
        String hex = Integer.toHexString(mod);
        int len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        if(strNumber.length()<2){
            strNumber="0"+strNumber;
        }
        if(checkForAll==false) {
            order = str1 + strNumber + sb + hex;
        }else {
            order = str1 + stAllCheck + firstCount;
        }
        if(order.length()>13) {
            open(order);
        }

    }
    @Override
    public void onClick(View v) {
        if (checkedStr.size()==0){
            Toast.makeText(AirCenterMoreActivity.this,"请先确认您的空调选择",Toast.LENGTH_LONG).show();
        }else{
        switch (v.getId()){
            case R.id.rb_open:
                stOrder="013101";
                if(checkForAll==false){
                    stCount="33";
                }else {
                    stCount="30";
                }

                onDeal(stOrder,stCount);
                break;
            case R.id.rb_close:
                stOrder="013102";
                if(checkForAll==false){
                    stCount="34";
                }else {
                    stCount="31";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_cold:
                stOrder="013301";
                if(checkForAll==false){
                    stCount="35";
                }else {
                    stCount="32";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_hot:
                stOrder="013308";
                if(checkForAll==false){
                    stCount="3C";
                }else {
                    stCount="39";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_wind:
                stOrder="013304";
                if(checkForAll==false){
                    stCount="38";
                }else {
                    stCount="35";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_humit:
                stOrder="013302";
                if(checkForAll==false){
                    stCount="33";
                }else {
                    stCount="33";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_high:
                stOrder="013401";
                if(checkForAll==false){
                    stCount="36";
                }else {
                    stCount="33";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_mid:
                stOrder="013402";
                if(checkForAll==false){
                    stCount="37";
                }else {
                    stCount="34";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_low:
                stOrder="013404";
                if(checkForAll==false){
                    stCount="39";
                }else {
                    stCount="36";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_18:
                stOrder="013212";
                if(checkForAll==false){
                    stCount="45";
                }else {
                    stCount="42";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_22:
                stOrder="013216";
                if(checkForAll==false){
                    stCount="49";
                }else {
                    stCount="46";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_26:
                stOrder="01321A";
                if(checkForAll==false){
                    stCount="4D";
                }else {
                    stCount="4A";
                }
                onDeal(stOrder,stCount);
                break;
            case R.id.rb_30:
                stOrder="01321E";
                if(checkForAll==false){
                    stCount="51";
                }else {
                    stCount="4E";
                }
                onDeal(stOrder,stCount);
                break;
            default:

        }
    }
    }
    public void open(final String order2){
        if (mDC.socketCrash || mDC.bIsRemote || checkNetConnection()){
            //if (true){
            new Thread(){
                @Override
                public void run() {
                    mDC.mWS.updateElectricOrder(mDC.sMasterCode,electric.getElectricCode(),""+mDC.orderSign+mDC.airSet,order2);
                    System.out.println("远程开电器：");
                }
            }.start();

        }else{
            //本地socket通信
            String order1 = "<" + electric.getElectricCode()+mDC.orderSign+mDC.airSet + order2 + "FF"+">";
            System.out.println("本地开电气： "+order1);
            NetworkUtil.out.println(order1);
        }
    }
    public void refresh(View view){
        airCenterInfo = null;
        open("0150FFFFFFFF4D");
        new Thread(){
            public void run(){
                try {
                    sleep(2000);
                    updateUI();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String abc = airCenterInfo;
                System.out.print(abc);

            }
        }.start();


    }
    public void aircentersure(View view){
        checkedStr.clear();
        ArrayList<String> choiceList = new ArrayList<String>();
        for (int i = 0; i < list1.size(); i++) {
            if (AirCenterAdapter.getIsSelected().get(i)) {
                String haschecked = list1.get(i);
                choiceList.add(haschecked.substring(0,2)+haschecked.substring(3,5));
            }
        }
        for(int i = 0;i<choiceList.size();i++) {
            String outer = choiceList.get(i).substring(0,2);
            String inner = choiceList.get(i).substring(2,4);
            int a = Integer.parseInt(outer,16);
            int b = Integer.parseInt(inner,16);
            int key = a+b;
            String value =choiceList.get(i);
            checkedStr.put(value,key);
        }

    }
    public void aircenterdelete(View view){
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("提示");
        dialog.setMessage("正在删除空调");
        dialog.show();
        new Thread(){
            public void run(){
                Message msg = new Message();
                ArrayList<String> deleteList = new ArrayList<String>();
                for (int i = 0; i < list1.size(); i++) {
                    if (AirCenterAdapter.getIsSelected().get(i)) {
                        String haschecked = list1.get(i);
                        deleteList.add(haschecked.substring(0,2)+haschecked.substring(3,5));
                    }
                }
                if(deleteList.size()==0){
                    msg.what=0x1062;
                    handler.sendMessage(msg);
                }
                for(int i = 0;i<deleteList.size();i++){
                    mDC.mWS.deleteCentralAir(electric.getMasterCode(),electric.getElectricIndex(),deleteList.get(i));
                    mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),AirCenterMoreActivity.this);
                    }
                    if(true){
                        msg.what=0x1063;
                        handler.sendMessage(msg);
                    }
            }
        }.start();
    }
}

