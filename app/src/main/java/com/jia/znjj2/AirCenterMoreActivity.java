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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jia.data.DataControl;
import com.jia.util.NetworkUtil;
import com.jia.widget.AirCenterAdapter;
import com.jia.widget.AirCenterAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.jia.widget.AirCenterAdapter.getIsSelected;
import static java.lang.Integer.parseInt;


public class AirCenterMoreActivity extends ElectricBase implements View.OnClickListener {
    public static int aircenterelectricIndex;
    public static ArrayList<String>  airCenterInfoList;
    public static String aircenterinfoback;
    private ProgressDialog dialog;
    private ProgressDialog dialog1;
    private ProgressDialog dialog2;
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private ListView aircenterList;
    private int checkNum;
    private ImageButton ibAddAircenter;
    private AirCenterAdapter adapter;
    public  static ArrayList<String> aircenterNumberlist1;
    public  ArrayList<String> list2;
    public  ArrayList<String> list3;
    public  ArrayList<String> list4;
    public  ArrayList<String> list5;
    public  ArrayList<String> list6;
    public  ArrayList<String> list7;
    public static ArrayList<String> aircenterNamelist8;
    public  ArrayList<String> list9;
    private ArrayList<String> check_list;


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

    public Handler handler = new Handler(){
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
                case 0x1234:
                    adapter = new AirCenterAdapter(aircenterNumberlist1,list2,list3,list4,list5,list6,list7,AirCenterMoreActivity.this);
                    aircenterList.setAdapter(adapter);
                    for(int i=0;i<check_list.size();i++){
                        AirCenterAdapter.getIsSelected().put(Integer.parseInt(check_list.get(i)),true);
                    }
                    dialog1.cancel();
                    Toast.makeText(AirCenterMoreActivity.this,"空调状态刷新成功",Toast.LENGTH_LONG).show();
                    break;
                case 0x1235:
                    dialog1.cancel();
                    Toast.makeText(AirCenterMoreActivity.this,"空调状态刷新失败，请检查主机",Toast.LENGTH_LONG).show();
                    break;
                case 0x123:
                    Toast.makeText(AirCenterMoreActivity.this,"空调状态自动刷新失败，请手动刷新",Toast.LENGTH_LONG).show();
                    break;
                case 0x122:
                    adapter = new AirCenterAdapter(aircenterNumberlist1,list2,list3,list4,list5,list6,list7,AirCenterMoreActivity.this);
                    aircenterList.setAdapter(adapter);
                    Toast.makeText(AirCenterMoreActivity.this,"已经获取空调状态，请进行操作",Toast.LENGTH_LONG).show();
                    break;
                case 0x0001:
                    adapter = new AirCenterAdapter(aircenterNumberlist1,list2,list3,list4,list5,list6,list7,AirCenterMoreActivity.this);
                    aircenterList.setAdapter(adapter);
                    for(int i=0;i<check_list.size();i++){
                        AirCenterAdapter.getIsSelected().put(Integer.parseInt(check_list.get(i)),true);
                    }
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
        adapter = new AirCenterAdapter(aircenterNumberlist1,list2,list3,list4,list5,list6,list7,AirCenterMoreActivity.this);
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

//        final String order = dealRefresh(list8);
//        if(order != "1")
//        {Toast.makeText(AirCenterMoreActivity.this,"正在为您获取空调信息刷新界面，请您耐心等待几秒",Toast.LENGTH_LONG).show();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                    new Thread(){
//                        Message msg =new Message();
//                        public void run(){
//                            airCenterInfoList=new ArrayList<>();
//                            try {
//                                String order = dealRefresh(list8);
//                                open(order);
//                                sleep(2800+list1.size()*500);
//                                removeRepeat(airCenterInfoList);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            if(airCenterInfoList.size()==0){
//                                msg.what=0x123;
//                                handler.sendMessage(msg);
//                            }else{
//                                for(int i=0;i<list1.size();i++){
//                                    String airCenterInfo = airCenterInfoList.get(i);
//                                    String abc1 = null;
//                                    if(airCenterInfo.substring(0,2).equals("Z0")){
//                                        abc1 = airCenterInfo.substring(10,airCenterInfo.length()-2);
//                                    }else {
//                                        abc1 = airCenterInfo.substring(8,airCenterInfo.length()-2);
//                                    }
//                                    ArrayList<String> list = new ArrayList<String>();
//                                    list = null;
//                                    list = dealRefreshData(abc1);
//                                    int no =list.size();
//                                    list2.set(i, list.get(no-6));
//                                    list3.set(i, list.get(no-5));
//                                    list4.set(i, list.get(no-4));
//                                    list5.set(i, list.get(no-3));
//                                    list6.set(i, list.get(no-2));
//                                    list7.set(i, list.get(no-1));
//                                }
//                                msg.what=0x122;
//                                handler.sendMessage(msg);
//                            }
//                        }
//                    }.start();
//            }
//            };
//        Timer timer = new Timer();
//        timer.schedule(task, 1);
//    }
        }


    protected void onResume() {
        super.onResume();
        checkNum=0;
        tvNo.setText("当前将选择" + checkNum + "台空调");
        electric = mDC.mAreaList.get(roomSequ).getmElectricInfoDataList().get(electricSequ);
        initDate();
        adapter = new AirCenterAdapter(aircenterNumberlist1,list2,list3,list4,list5,list6,list7,this);
        aircenterList.setAdapter(adapter);

    }

   public void initDate(){
       aircenterNumberlist1 = new ArrayList<>();
       list2 = new ArrayList<>();
       list3 = new ArrayList<>();
       list4 = new ArrayList<>();
       list5 = new ArrayList<>();
       list6 = new ArrayList<>();
       list7 = new ArrayList<>();
       list9 = new ArrayList<>();
       aircenterNamelist8 = new ArrayList<>();

       if(electric.getExtras()==null||electric.getExtras().equals("anyType{}")||electric.getExtras().equals("{}")){
            aircenterNumberlist1.add("无空调记录");
            list2.add("开/关：" + " ");
            list3.add("模式" + " ");
            list4.add("风速" + " ");
            list5.add("温度" + " ");
            list6.add("室温" + " ");
            list7.add("错误信息" + " ");
        }else{
        String extras = electric.getExtras();
            Map maps = (Map) JSON.parse(extras);
            List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
            for (Object map : maps.entrySet()){
                list9.add(((Map.Entry)map).getKey().toString());
              }
            aircenterNamelist8 = new ArrayList<>(list9);
            Collections.sort(aircenterNamelist8);
            for(int i=0;i<aircenterNamelist8.size();i++){
                aircenterNumberlist1.add((String) maps.get(aircenterNamelist8.get(i)));
            }
            for (int i = 0; i < aircenterNumberlist1.size(); i++) {
                list2.add("开/关");
                       }
            for (int i = 0; i < aircenterNumberlist1.size(); i++) {
                list3.add("温度");
                       }
            for (int i = 0; i < aircenterNumberlist1.size(); i++) {
                list4.add("模式");
                       }
            for (int i = 0; i < aircenterNumberlist1.size(); i++) {
                list5.add("风速" );
                       }
            for (int i = 0; i < aircenterNumberlist1.size(); i++) {
                list6.add("室内温度");
                       }
            for (int i = 0; i < aircenterNumberlist1.size(); i++) {
                list7.add("错误信息" );
                       }
            for (int i = 0; i < aircenterNumberlist1.size(); i++) {
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
        strNumber= Integer.toHexString(i);
        int checkSum = 0;
        int count=0;
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Integer>> entryseSet=checkedStr.entrySet();
        for (Map.Entry<String, Integer> entry:entryseSet) {
            sb.append(entry.getKey());
            checkSum = checkSum + entry.getValue();
        }
        count=checkSum+ parseInt(strNumber,16)+ parseInt(firstCount,16);
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
            order = str1 + strNumber + sb + hex;
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
                aircenterinfoback = "";
                stOrder="013101";
                if(checkForAll==false){
                    stCount="33";
                }else {
                    stCount="30";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list2.set(Integer.parseInt(check_list.get(i)),"开机");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_close:
                aircenterinfoback = "";
                stOrder="013102";
                if(checkForAll==false){
                    stCount="34";
                }else {
                    stCount="31";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list2.set(Integer.parseInt(check_list.get(i)),"关机");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_cold:
                aircenterinfoback = "";
                stOrder="013301";
                if(checkForAll==false){
                    stCount="35";
                }else {
                    stCount="32";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list4.set(Integer.parseInt(check_list.get(i)),"制冷");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_hot:
                aircenterinfoback = "";
                stOrder="013308";
                if(checkForAll==false){
                    stCount="3C";
                }else {
                    stCount="39";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list4.set(Integer.parseInt(check_list.get(i)),"制热");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_wind:
                aircenterinfoback = "";
                stOrder="013304";
                if(checkForAll==false){
                    stCount="38";
                }else {
                    stCount="35";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list4.set(Integer.parseInt(check_list.get(i)),"送风");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_humit:
                aircenterinfoback = "";
                stOrder="013302";
                if(checkForAll==false){
                    stCount="36";
                }else {
                    stCount="33";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list4.set(Integer.parseInt(check_list.get(i)),"除湿");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_high:
                aircenterinfoback = "";
                stOrder="013401";
                if(checkForAll==false){
                    stCount="36";
                }else {
                    stCount="33";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list5.set(Integer.parseInt(check_list.get(i)),"风速高");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_mid:
                aircenterinfoback = "";
                stOrder="013402";
                if(checkForAll==false){
                    stCount="37";
                }else {
                    stCount="34";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list5.set(Integer.parseInt(check_list.get(i)),"风速中");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_low:
                aircenterinfoback = "";
                stOrder="013404";
                if(checkForAll==false){
                    stCount="39";
                }else {
                    stCount="36";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list5.set(Integer.parseInt(check_list.get(i)),"风速低");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_18:
                aircenterinfoback = "";
                stOrder="013212";
                if(checkForAll==false){
                    stCount="45";
                }else {
                    stCount="42";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list3.set(Integer.parseInt(check_list.get(i)),"温度:18℃");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_22:
                aircenterinfoback = "";
                stOrder="013216";
                if(checkForAll==false){
                    stCount="49";
                }else {
                    stCount="46";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list3.set(Integer.parseInt(check_list.get(i)),"温度:22℃");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_26:
                aircenterinfoback = "";
                stOrder="01321A";
                if(checkForAll==false){
                    stCount="4D";
                }else {
                    stCount="4A";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list3.set(Integer.parseInt(check_list.get(i)),"温度:26℃");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                break;
            case R.id.rb_30:
                aircenterinfoback = "";
                stOrder="01321E";
                if(checkForAll==false){
                    stCount="51";
                }else {
                    stCount="4E";
                }
                keepcheck();
                new Thread(){
                    Message msg = new Message();
                    public void run(){
                        try{
                            onDeal(stOrder,stCount);
                            sleep(2500);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(aircenterinfoback!=""){
                            for(int i=0;i<check_list.size();i++){
                                list3.set(Integer.parseInt(check_list.get(i)),"温度:30℃");
                            }
                            msg.what=0x0001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
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
        keepcheck();
        dialog1 = new ProgressDialog(this);
        dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setTitle("提示");
        dialog1.setMessage("正在查询空调状态，请稍候");
        dialog1.show();
        airCenterInfoList=new ArrayList<>();
        final String order = dealRefresh(aircenterNamelist8);
        if(order != "1"){
        new Thread(){
            Message msg =new Message();
            public void run(){
                try {
                    open(order);
                    sleep(4300+aircenterNumberlist1.size()*500);
                    removeRepeat(airCenterInfoList);
                    System.out.print(airCenterInfoList);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(airCenterInfoList.size()==0||airCenterInfoList.size()!=aircenterNamelist8.size()){
                    msg.what=0x1235;
                    handler.sendMessage(msg);
                }else if(airCenterInfoList.size()==aircenterNamelist8.size()){
                    for(int i=0;i<aircenterNumberlist1.size();i++){
                        String airCenterInfo = airCenterInfoList.get(i);
                        String abc1 = null;
                if(airCenterInfo.substring(0,2).equals("Z0")){
                     abc1 = airCenterInfo.substring(10,airCenterInfo.length()-2);
                }else {
                     abc1 = airCenterInfo.substring(8,airCenterInfo.length()-2);
                }
                    ArrayList<String> list = new ArrayList<String>();
                    list = null;
                    list = dealRefreshData(abc1);
                    int no =list.size();
                    list2.set(i, list.get(no-6));
                    list3.set(i, list.get(no-5));
                    list4.set(i, list.get(no-4));
                    list5.set(i, list.get(no-3));
                    list6.set(i, list.get(no-2));
                    list7.set(i, list.get(no-1));
                }
                    msg.what=0x1234;
                    handler.sendMessage(msg);
                }
            }
        }.start();}
        else{
            dialog1.cancel();
            Toast.makeText(AirCenterMoreActivity.this,"请先添加空调空调",Toast.LENGTH_LONG).show();
        }
    }
    public void aircentersure(View view){
        checkedStr.clear();
        ArrayList<String> choiceList = new ArrayList<String>();
        for (int i = 0; i < aircenterNamelist8.size(); i++) {
            if (AirCenterAdapter.getIsSelected().get(i)) {
                String haschecked = aircenterNamelist8.get(i);
                choiceList.add(haschecked);
            }
        }
        for(int i = 0;i<choiceList.size();i++) {
            String outer = choiceList.get(i).substring(0,2);
            String inner = choiceList.get(i).substring(2,4);
            int a = parseInt(outer,16);
            int b = parseInt(inner,16);
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
                if(aircenterNumberlist1.size()==0){
                    msg.what=0x1062;
                    handler.sendMessage(msg);
                }
                for(int i = 0;i<aircenterNumberlist1.size();i++){
                        if (AirCenterAdapter.getIsSelected().get(i)) {
                            mDC.mWS.deleteCentralAir(electric.getMasterCode(),electric.getElectricIndex(),aircenterNamelist8.get(i),aircenterNumberlist1.get(i));
                        }
                    }
                    if(true){
                        msg.what=0x1063;
                        mDC.mWS.loadElectricFromWs(mDC.sMasterCode,mDC.mUserList.get(0).getElectricTime(),AirCenterMoreActivity.this);
                        handler.sendMessage(msg);
                    }
            }
        }.start();
    }
    public String dealRefresh(ArrayList<String> list){
        String refreshorder = null;
        String check_sum = null;
        String air_code = "";
        int air_code_no=0;
        String str1 = "01500101";
        String str2 = "01500F";
        if(list.size()==0||list.get(0)=="无空调记录"){
            refreshorder ="1";
        }else if(list.size()==1){
            air_code = list.get(0);
            int a = parseInt(list.get(0).substring(0,2),16)+ parseInt(list.get(0).substring(2,4),16);
            int b = 83+a;
            if(b<256){
                check_sum= Integer.toHexString(b);
                refreshorder = str1+air_code+check_sum;
            }else{
                check_sum= "FF";
                refreshorder = str1+air_code+check_sum;
            }
        }else if(list.size()<16){
            String check_NO = Integer.toHexString(list.size());
            for(int i=0;i<list.size();i++){
                String a =list.get(i);
                int b = parseInt(list.get(i).substring(0,2),16)+ parseInt(list.get(i).substring(2,4),16);
                air_code_no=air_code_no+b;
                air_code = air_code+a;
            }
                int  b = air_code_no+96+list.size();
            if(b<256){
                check_sum= Integer.toHexString(b);
                refreshorder = str2+"0"+check_NO+air_code+check_sum;
            }else{
                check_sum = "FF";
                refreshorder = str2+"0"+check_NO+air_code+check_sum;
            }
        }else if(list.size()>=16){
            String check_NO = Integer.toHexString(list.size());
            for(int i=0;i<list.size();i++){
                String a =list.get(i);
                int b = parseInt(list.get(i).substring(0,2),16)+ parseInt(list.get(i).substring(2,4),16);
                air_code_no=b;
                air_code = air_code+a;
            }
            int  b = air_code_no+96+list.size();
            if(b<256){
                check_sum= Integer.toHexString(b);
                refreshorder = str2+check_NO+air_code+check_sum;
            }else{
                check_sum = "FF";
                refreshorder = str2+check_NO+air_code+check_sum;
            }
        }
        return refreshorder;
        }


    public void selectAllairCenter(View view){
        for (int i = 0; i < aircenterNumberlist1.size(); i++) {
            AirCenterAdapter.getIsSelected().put(i, true);
        }
        checkNum = aircenterNumberlist1.size();
        dataChanged();
    }
    public void deleteSelectAll(View view){
        for (int i = 0; i < aircenterNumberlist1.size(); i++) {
            AirCenterAdapter.getIsSelected().put(i, false);
        }
        checkNum = 0;
        dataChanged();
    }


    private void dataChanged() {
        adapter.notifyDataSetChanged();
        tvNo.setText("当前将选择" + checkNum + "台空调");
    }
    public ArrayList<String> dealRefreshData(String str){
        ArrayList<String> list = new ArrayList<String>();
        if(str.substring(4,6).equals("00")){
            list.add("关机");
        }else{
            list.add("开机");
        }

        int a = Integer.parseInt(str.substring(6,8),16);
        list.add("温度:"+String.valueOf(a)+"℃");


        if(str.substring(8,10).equals("01")){
            list.add("制冷");
        }else if(str.substring(8,10).equals("02")){
            list.add("除湿");
        }else if(str.substring(8,10).equals("08")){
            list.add("制热");
        }else if(str.substring(8,10).equals("04")){
            list.add("送风");
        }

        if(str.substring(10,12).equals("01")){
            list.add("风速高");
        }else if(str.substring(10,12).equals("02")){
            list.add("风速中");
        }else {
            list.add("风速低");
        }

        int b = Integer.parseInt(str.substring(12,14),16);
        list.add("室温:"+String.valueOf(b)+"℃");

        if(str.substring(14,16).equals("00")){
            list.add("无错误信息");
        }else{
            list.add("出现错误");
        }

        return list;
    }
 public void keepcheck(){
     check_list = new ArrayList<String>();
     check_list.clear();
     for(int i = 0; i<aircenterNumberlist1.size();i++){
         if(AirCenterAdapter.getIsSelected().get(i)){
             String a = String.valueOf(i);
             check_list.add(a);
         }
     }
 }
    public  void removeRepeat(List list) {
        for (int i=0;i<list.size();i++) {
            for (int j=list.size()-1;j>i;j--){
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
    }
    public void updateAirCentralName(View view){
        if(aircenterNumberlist1.get(0)!="无空调记录"){
        Intent intent = new Intent(AirCenterMoreActivity.this,UpdateAirCenterName.class);
        startActivity(intent);}else{
            Toast.makeText(AirCenterMoreActivity.this,"请先添加空调",Toast.LENGTH_LONG).show();
        }

    }
}