package com.jia.znjj2;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.jia.util.NetworkUtil;


import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class AirCenterMoreActivity extends ElectricBase implements View.OnClickListener {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private CheckBox cbCheckAll;
    private CheckBox cb0100;
    private ImageView iv0100;
    private CheckBox cb0101;
    private CheckBox cb0102;
    private CheckBox cb0103;
    private CheckBox cb0104;
    private CheckBox cb0105;
    private CheckBox cb0106;
    private CheckBox cb0107;
    private CheckBox cb0108;
    private CheckBox cb0109;
    private CheckBox cb0110;
    private CheckBox cb0111;
    private CheckBox cb0112;
    private CheckBox cb0113;
    private CheckBox cb0114;
    private CheckBox cb0115;
    private CheckBox cb0200;
    private CheckBox cb0201;
    private CheckBox cb0202;
    private CheckBox cb0203;
    private CheckBox cb0204;
    private CheckBox cb0205;
    private CheckBox cb0206;
    private CheckBox cb0207;
    private CheckBox cb0208;
    private CheckBox cb0209;
    private CheckBox cb0210;
    private CheckBox cb0211;
    private CheckBox cb0212;
    private CheckBox cb0213;
    private CheckBox cb0214;
    private CheckBox cb0215;
    private CheckBox cb0300;
    private CheckBox cb0301;
    private CheckBox cb0302;
    private CheckBox cb0303;
    private CheckBox cb0304;
    private CheckBox cb0305;
    private CheckBox cb0306;
    private CheckBox cb0307;
    private CheckBox cb0308;
    private CheckBox cb0309;
    private CheckBox cb0310;
    private CheckBox cb0311;
    private CheckBox cb0312;
    private CheckBox cb0313;
    private CheckBox cb0314;
    private CheckBox cb0315;
    private CheckBox cb0400;
    private CheckBox cb0401;
    private CheckBox cb0402;
    private CheckBox cb0403;
    private CheckBox cb0404;
    private CheckBox cb0405;
    private CheckBox cb0406;
    private CheckBox cb0407;
    private CheckBox cb0408;
    private CheckBox cb0409;
    private CheckBox cb0410;
    private CheckBox cb0411;
    private CheckBox cb0412;
    private CheckBox cb0413;
    private CheckBox cb0414;
    private CheckBox cb0415;
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
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_center_more);
        initView();
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
        tvTitleName = (TextView)findViewById(R.id.air_center_more_name);
        tvTitleEdit = (TextView)findViewById(R.id.air_center_more_edit);
        tvTitleSave = (TextView)findViewById(R.id.air_center_more_save);
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);

        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        etElectricName= (EditText) findViewById(R.id.air_more_name);
        cbCheckAll= (CheckBox) findViewById(R.id.air_center_more_selectall);
        cbCheckAll.setOnCheckedChangeListener(listener);
        cb0100= (CheckBox) findViewById(R.id.cb_100);
        cb0100.setOnCheckedChangeListener(listener);
        iv0100= (ImageView) findViewById(R.id.open_100);
        cb0101= (CheckBox) findViewById(R.id.cb_101);
        cb0101.setOnCheckedChangeListener(listener);
        cb0102= (CheckBox) findViewById(R.id.cb_102);
        cb0102.setOnCheckedChangeListener(listener);
        cb0103= (CheckBox) findViewById(R.id.cb_103);
        cb0103.setOnCheckedChangeListener(listener);
        cb0104= (CheckBox) findViewById(R.id.cb_104);
        cb0104.setOnCheckedChangeListener(listener);
        cb0105= (CheckBox) findViewById(R.id.cb_105);
        cb0105.setOnCheckedChangeListener(listener);
        cb0106= (CheckBox) findViewById(R.id.cb_106);
        cb0106.setOnCheckedChangeListener(listener);
        cb0107= (CheckBox) findViewById(R.id.cb_107);
        cb0107.setOnCheckedChangeListener(listener);
        cb0108= (CheckBox) findViewById(R.id.cb_108);
        cb0108.setOnCheckedChangeListener(listener);
        cb0109= (CheckBox) findViewById(R.id.cb_109);
        cb0109.setOnCheckedChangeListener(listener);
        cb0110= (CheckBox) findViewById(R.id.cb_110);
        cb0110.setOnCheckedChangeListener(listener);
        cb0111= (CheckBox) findViewById(R.id.cb_111);
        cb0111.setOnCheckedChangeListener(listener);
        cb0112= (CheckBox) findViewById(R.id.cb_112);
        cb0112.setOnCheckedChangeListener(listener);
        cb0113= (CheckBox) findViewById(R.id.cb_113);
        cb0113.setOnCheckedChangeListener(listener);
        cb0114= (CheckBox) findViewById(R.id.cb_114);
        cb0114.setOnCheckedChangeListener(listener);
        cb0115= (CheckBox) findViewById(R.id.cb_115);
        cb0115.setOnCheckedChangeListener(listener);
        cb0200= (CheckBox) findViewById(R.id.cb_200);
        cb0200.setOnCheckedChangeListener(listener);
        cb0201= (CheckBox) findViewById(R.id.cb_201);
        cb0201.setOnCheckedChangeListener(listener);
        cb0202= (CheckBox) findViewById(R.id.cb_202);
        cb0202.setOnCheckedChangeListener(listener);
        cb0203= (CheckBox) findViewById(R.id.cb_203);
        cb0203.setOnCheckedChangeListener(listener);
        cb0204= (CheckBox) findViewById(R.id.cb_204);
        cb0204.setOnCheckedChangeListener(listener);
        cb0205= (CheckBox) findViewById(R.id.cb_205);
        cb0205.setOnCheckedChangeListener(listener);
        cb0206= (CheckBox) findViewById(R.id.cb_206);
        cb0206.setOnCheckedChangeListener(listener);
        cb0207= (CheckBox) findViewById(R.id.cb_207);
        cb0207.setOnCheckedChangeListener(listener);
        cb0208= (CheckBox) findViewById(R.id.cb_208);
        cb0208.setOnCheckedChangeListener(listener);
        cb0209= (CheckBox) findViewById(R.id.cb_209);
        cb0209.setOnCheckedChangeListener(listener);
        cb0210= (CheckBox) findViewById(R.id.cb_210);
        cb0210.setOnCheckedChangeListener(listener);
        cb0211= (CheckBox) findViewById(R.id.cb_211);
        cb0211.setOnCheckedChangeListener(listener);
        cb0212= (CheckBox) findViewById(R.id.cb_212);
        cb0212.setOnCheckedChangeListener(listener);
        cb0213= (CheckBox) findViewById(R.id.cb_213);
        cb0213.setOnCheckedChangeListener(listener);
        cb0214= (CheckBox) findViewById(R.id.cb_214);
        cb0214.setOnCheckedChangeListener(listener);
        cb0215= (CheckBox) findViewById(R.id.cb_215);
        cb0215.setOnCheckedChangeListener(listener);
        cb0300= (CheckBox) findViewById(R.id.cb_300);
        cb0300.setOnCheckedChangeListener(listener);
        cb0301= (CheckBox) findViewById(R.id.cb_301);
        cb0301.setOnCheckedChangeListener(listener);
        cb0302= (CheckBox) findViewById(R.id.cb_302);
        cb0302.setOnCheckedChangeListener(listener);
        cb0303= (CheckBox) findViewById(R.id.cb_303);
        cb0303.setOnCheckedChangeListener(listener);
        cb0304= (CheckBox) findViewById(R.id.cb_304);
        cb0304.setOnCheckedChangeListener(listener);
        cb0305= (CheckBox) findViewById(R.id.cb_305);
        cb0305.setOnCheckedChangeListener(listener);
        cb0306= (CheckBox) findViewById(R.id.cb_306);
        cb0306.setOnCheckedChangeListener(listener);
        cb0307= (CheckBox) findViewById(R.id.cb_307);
        cb0307.setOnCheckedChangeListener(listener);
        cb0308= (CheckBox) findViewById(R.id.cb_308);
        cb0308.setOnCheckedChangeListener(listener);
        cb0309= (CheckBox) findViewById(R.id.cb_309);
        cb0309.setOnCheckedChangeListener(listener);
        cb0310= (CheckBox) findViewById(R.id.cb_310);
        cb0310.setOnCheckedChangeListener(listener);
        cb0311= (CheckBox) findViewById(R.id.cb_311);
        cb0311.setOnCheckedChangeListener(listener);
        cb0312= (CheckBox) findViewById(R.id.cb_312);
        cb0312.setOnCheckedChangeListener(listener);
        cb0313= (CheckBox) findViewById(R.id.cb_313);
        cb0313.setOnCheckedChangeListener(listener);
        cb0314= (CheckBox) findViewById(R.id.cb_314);
        cb0314.setOnCheckedChangeListener(listener);
        cb0315= (CheckBox) findViewById(R.id.cb_315);
        cb0315.setOnCheckedChangeListener(listener);
        cb0400= (CheckBox) findViewById(R.id.cb_400);
        cb0400.setOnCheckedChangeListener(listener);
        cb0401= (CheckBox) findViewById(R.id.cb_401);
        cb0401.setOnCheckedChangeListener(listener);
        cb0402= (CheckBox) findViewById(R.id.cb_402);
        cb0402.setOnCheckedChangeListener(listener);
        cb0403= (CheckBox) findViewById(R.id.cb_403);
        cb0403.setOnCheckedChangeListener(listener);
        cb0404= (CheckBox) findViewById(R.id.cb_404);
        cb0404.setOnCheckedChangeListener(listener);
        cb0405= (CheckBox) findViewById(R.id.cb_405);
        cb0405.setOnCheckedChangeListener(listener);
        cb0406= (CheckBox) findViewById(R.id.cb_406);
        cb0406.setOnCheckedChangeListener(listener);
        cb0407= (CheckBox) findViewById(R.id.cb_407);
        cb0407.setOnCheckedChangeListener(listener);
        cb0408= (CheckBox) findViewById(R.id.cb_408);
        cb0408.setOnCheckedChangeListener(listener);
        cb0409= (CheckBox) findViewById(R.id.cb_409);
        cb0409.setOnCheckedChangeListener(listener);
        cb0410= (CheckBox) findViewById(R.id.cb_410);
        cb0410.setOnCheckedChangeListener(listener);
        cb0411= (CheckBox) findViewById(R.id.cb_411);
        cb0411.setOnCheckedChangeListener(listener);
        cb0412= (CheckBox) findViewById(R.id.cb_412);
        cb0412.setOnCheckedChangeListener(listener);
        cb0413= (CheckBox) findViewById(R.id.cb_413);
        cb0413.setOnCheckedChangeListener(listener);
        cb0414= (CheckBox) findViewById(R.id.cb_414);
        cb0414.setOnCheckedChangeListener(listener);
        cb0415= (CheckBox) findViewById(R.id.cb_415);
        cb0415.setOnCheckedChangeListener(listener);
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

    }
    public void AirMoreBack(View view){
        finish();
    }
    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
        {
            switch(buttonView.getId())
            {
                case R.id.air_center_more_selectall:
                    if(isChecked){
                       checkForAll=true;

                    }else {
                        checkForAll=false;
                    }

                    break;
                case R.id.cb_100:
                    if(isChecked){
                    checkedStr.put("0100",1);}
                    else {
                        checkedStr.remove("0100");
                    }
                    break;
                case R.id.cb_101:
                    if(isChecked){
                        checkedStr.put("0101",2);
                    }else {
                            checkedStr.remove("0101");
                    }
                    break;
                case R.id.cb_102:
                    if(isChecked){
                        checkedStr.put("0102",3);
                   }else {

                            checkedStr.remove("0102");


                    }
                    break;
                case R.id.cb_103:
                    if(isChecked) {
                        checkedStr.put("0103",4);
                    }else {
                            checkedStr.remove("0103");
                    }
                        break;
                case R.id.cb_104:
                    if(isChecked) {
                        checkedStr.put("0104",5);
                    }else {
                        checkedStr.remove("0104");
                    }
                        break;
                case R.id.cb_105:
                    if(isChecked) {
                        checkedStr.put("0105",6);
                    }else {
                        checkedStr.remove("0105");
                    }
                        break;
                case R.id.cb_106:
                    if(isChecked) {
                        checkedStr.put("0106",7);
                    }else {
                        checkedStr.remove("0106");
                    }
                        break;
                case R.id.cb_107:
                    if(isChecked) {
                        checkedStr.put("0107",8);
                    }else {
                        checkedStr.remove("0107");
                    }

                        break;
                case R.id.cb_108:
                    if(isChecked) {
                        checkedStr.put("0108",9);
                    }else {
                        checkedStr.remove("0108");
                    }

                        break;
                case R.id.cb_109:
                    if(isChecked) {
                        checkedStr.put("0109",10);
                    }else {
                        checkedStr.remove("0109");
                    }

                        break;
                case R.id.cb_110:
                    if(isChecked) {
                        checkedStr.put("0110",17);
                    }else {
                        checkedStr.remove("0110");
                    }

                        break;
                case R.id.cb_111:
                    if(isChecked) {
                        checkedStr.put("0111",18);
                    }else {
                        checkedStr.remove("0111");
                    }

                        break;
                case R.id.cb_112:
                    if(isChecked) {
                        checkedStr.put("0112",19);
                    }else {
                        checkedStr.remove("0112");
                    }
                        break;
                case R.id.cb_113:
                    if(isChecked) {
                        checkedStr.put("0113",20);
                    }else {
                        checkedStr.remove("0113");
                    }
                        break;
                case R.id.cb_114:
                    if(isChecked) {
                        checkedStr.put("0114",21);
                    }else {
                        checkedStr.remove("0114");
                    }
                        break;
                case R.id.cb_115:
                    if(isChecked) {
                        checkedStr.put("0115",22);
                    }else {
                        checkedStr.remove("0115");
                    }
                        break;
                case R.id.cb_200:
                    if(isChecked) {
                        checkedStr.put("0200",2);
                    }else {
                        checkedStr.remove("0200");
                    }

                        break;
                case R.id.cb_201:
                    if(isChecked) {
                        checkedStr.put("0201",3);
                    }else {
                        checkedStr.remove("0201");
                    }
                        break;
                case R.id.cb_202:
                    if(isChecked) {
                        checkedStr.put("0202",4);
                    }else {
                        checkedStr.remove("0202");
                    }

                        break;
                case R.id.cb_203:
                    if(isChecked) {
                        checkedStr.put("0203",5);
                    }else {
                        checkedStr.remove("0203");
                    }

                        break;
                case R.id.cb_204:
                    if(isChecked) {
                        checkedStr.put("0204",6);
                    }else {
                        checkedStr.remove("0204");
                    }

                        break;
                case R.id.cb_205:
                    if(isChecked) {
                        checkedStr.put("0205",7);
                    }else {
                        checkedStr.remove("0205");
                    }
                        break;
                case R.id.cb_206:
                    if(isChecked) {
                        checkedStr.put("0206",8);
                    }else {
                        checkedStr.remove("0206");
                    }

                        break;
                case R.id.cb_207:
                    if(isChecked) {
                        checkedStr.put("0207",9);
                    }else {
                        checkedStr.remove("0207");
                    }
                        break;
                case R.id.cb_208:
                    if(isChecked) {
                        checkedStr.put("0208",10);
                    }else {
                        checkedStr.remove("0208");
                    }
                        break;
                case R.id.cb_209:
                    if(isChecked) {
                        checkedStr.put("0209",11);
                    }else {
                        checkedStr.remove("0209");
                    }
                        break;
                case R.id.cb_210:
                    if(isChecked) {
                        checkedStr.put("0210",18);
                    }else {
                        checkedStr.remove("0210");
                    }
                        break;
                case R.id.cb_211:
                    if(isChecked) {
                        checkedStr.put("0211",19);
                    }else {
                        checkedStr.remove("0211");
                    }
                        break;
                case R.id.cb_212:
                    if(isChecked) {
                        checkedStr.put("0212",20);
                    }else {
                        checkedStr.remove("0212");
                    }
                        break;
                case R.id.cb_213:
                    if(isChecked) {
                        checkedStr.put("0213",21);
                    }else {
                        checkedStr.remove("0213");
                    }
                        break;
                case R.id.cb_214:
                    if(isChecked) {
                        checkedStr.put("0214",22);
                    }else {
                        checkedStr.remove("0214");
                    }

                        break;
                case R.id.cb_215:
                    if(isChecked) {
                        checkedStr.put("0215",23);
                    }else {
                        checkedStr.remove("0215");
                    }

                        break;
                case R.id.cb_300:
                    if(isChecked) {
                        checkedStr.put("0300",3);
                    }else {
                        checkedStr.remove("0300");
                    }
                        break;
                case R.id.cb_301:
                    if(isChecked) {
                        checkedStr.put("0301",4);
                    }else {
                        checkedStr.remove("0301");
                    }
                        break;
                case R.id.cb_302:
                    if(isChecked) {
                        checkedStr.put("0302",5);
                    }else {
                        checkedStr.remove("0302");
                    }

                        break;
                case R.id.cb_303:
                    if(isChecked) {
                        checkedStr.put("0303",6);
                    }else {
                        checkedStr.remove("0303");
                    }
                        break;
                case R.id.cb_304:
                    if(isChecked) {
                        checkedStr.put("0304",7);
                    }else {
                        checkedStr.remove("0304");
                    }
                        break;
                case R.id.cb_305:
                    if(isChecked) {
                        checkedStr.put("0305",8);
                    }else {
                        checkedStr.remove("0305");
                    }
                        break;
                case R.id.cb_306:
                    if(isChecked) {
                        checkedStr.put("0306",9);
                    }else {
                        checkedStr.remove("0306");
                    }
                        break;
                case R.id.cb_307:
                    if(isChecked) {
                        checkedStr.put("0307",10);
                    }else {
                        checkedStr.remove("0307");
                    }
                        break;
                case R.id.cb_308:
                    if(isChecked) {
                        checkedStr.put("0308",11);
                    }else {
                        checkedStr.remove("0308");
                    }
                        break;
                case R.id.cb_309:
                    if(isChecked) {
                        checkedStr.put("0309",12);
                    }else {
                        checkedStr.remove("0309");
                    }
                        break;
                case R.id.cb_310:
                    if(isChecked) {
                        checkedStr.put("0310",19);
                    }else {
                        checkedStr.remove("0310");
                    }
                        break;
                case R.id.cb_311:
                    if(isChecked) {
                        checkedStr.put("0311",20);
                    }else {
                        checkedStr.remove("0311");
                    }
                        break;
                case R.id.cb_312:
                    if(isChecked) {
                        checkedStr.put("0312",21);
                    }else {
                        checkedStr.remove("0312");
                    }
                        break;
                case R.id.cb_313:
                    if(isChecked) {
                        checkedStr.put("0313",22);
                    }else {
                        checkedStr.remove("0313");
                    }
                        break;
                case R.id.cb_314:
                    if(isChecked) {
                        checkedStr.put("0314",23);
                    }else {
                        checkedStr.remove("0314");
                    }
                        break;
                case R.id.cb_315:
                    if(isChecked) {
                        checkedStr.put("0315",24);
                    }else {
                        checkedStr.remove("0315");
                    }
                        break;
                case R.id.cb_400:
                    if(isChecked) {
                        checkedStr.put("0400",4);
                    }else {
                        checkedStr.remove("0400");
                    }
                        break;
                case R.id.cb_401:
                    if(isChecked) {
                        checkedStr.put("0401",5);
                    }else {
                        checkedStr.remove("0401");
                    }
                        break;
                case R.id.cb_402:
                    if(isChecked) {
                        checkedStr.put("0402",6);
                    }else {
                        checkedStr.remove("0402");
                    }
                        break;
                case R.id.cb_403:
                    if(isChecked) {
                        checkedStr.put("0403",7);
                    }else {
                        checkedStr.remove("0403");
                    }
                        break;
                case R.id.cb_404:
                    if(isChecked) {
                        checkedStr.put("0404",8);
                    }else {
                        checkedStr.remove("0404");
                    }
                        break;
                case R.id.cb_405:
                    if(isChecked) {
                        checkedStr.put("0405",9);
                    }else {
                        checkedStr.remove("0405");
                    }
                        break;
                case R.id.cb_406:
                    if(isChecked) {
                        checkedStr.put("0406",10);
                    }else {
                        checkedStr.remove("0406");
                    }
                        break;
                case R.id.cb_407:
                    if(isChecked) {
                        checkedStr.put("0407",11);
                    }else {
                        checkedStr.remove("0407");
                    }
                        break;
                case R.id.cb_408:
                    if(isChecked) {
                        checkedStr.put("0408",12);
                    }else {
                        checkedStr.remove("0408");
                    }
                        break;
                case R.id.cb_409:
                    if(isChecked) {
                        checkedStr.put("0409",13);
                    }else {
                        checkedStr.remove("0409");
                    }
                        break;
                case R.id.cb_410:
                    if(isChecked) {
                        checkedStr.put("0410",20);
                    }else {
                        checkedStr.remove("0410");
                    }
                        break;
                case R.id.cb_411:
                    if(isChecked) {
                        checkedStr.put("0411",21);
                    }else {
                        checkedStr.remove("0411");
                    }
                        break;
                case R.id.cb_412:
                    if(isChecked) {
                        checkedStr.put("0412",22);
                    }else {
                        checkedStr.remove("0412");
                    }
                        break;
                case R.id.cb_413:
                    if(isChecked) {
                        checkedStr.put("0413",23);
                    }else {
                        checkedStr.remove("0413");
                    }
                        break;
                case R.id.cb_414:
                    if(isChecked) {
                        checkedStr.put("0414",24);
                    }else {
                        checkedStr.remove("0414");
                    }
                        break;
                case R.id.cb_415:
                    if(isChecked) {
                        checkedStr.put("0415",25);
                    }else {
                        checkedStr.remove("0415");
                    }
                        break;


            }
        }
    };
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
}
