package com.jia.znjj2;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jia.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class AirCenterMoreActivity extends ElectricBase implements View.OnClickListener {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;
    private CheckBox cbCheckAll;
    private CheckBox cb0100;
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
    String  str=null;
    String  strNumber=null;
    String  strPlus=null;
    String order=null;
    int i=0;
    private Button btOpen;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_center_more);
        initView();
    }

    @Override
    public void updateUI() {

    }

    private void initView(){
        tvTitleName = (TextView)findViewById(R.id.air_center_more_name);
        tvTitleEdit = (TextView)findViewById(R.id.air_center_more_edit);
        tvTitleSave = (TextView)findViewById(R.id.air_center_more_save);
        tvTitleEdit.setVisibility(View.VISIBLE);
        tvTitleSave.setVisibility(View.GONE);
        tvTitleName.setText(electric.getElectricName());
        cbCheckAll= (CheckBox) findViewById(R.id.air_center_more_selectall);
        cbCheckAll.setOnCheckedChangeListener(listener);
        cb0100= (CheckBox) findViewById(R.id.cb_100);
        cb0101= (CheckBox) findViewById(R.id.cb_101);
        cb0102= (CheckBox) findViewById(R.id.cb_102);
        cb0103= (CheckBox) findViewById(R.id.cb_103);
        cb0104= (CheckBox) findViewById(R.id.cb_104);
        cb0105= (CheckBox) findViewById(R.id.cb_105);
        cb0106= (CheckBox) findViewById(R.id.cb_106);
        cb0107= (CheckBox) findViewById(R.id.cb_107);
        cb0108= (CheckBox) findViewById(R.id.cb_108);
        cb0109= (CheckBox) findViewById(R.id.cb_109);
        cb0110= (CheckBox) findViewById(R.id.cb_110);
        cb0111= (CheckBox) findViewById(R.id.cb_111);
        cb0112= (CheckBox) findViewById(R.id.cb_112);
        cb0113= (CheckBox) findViewById(R.id.cb_113);
        cb0114= (CheckBox) findViewById(R.id.cb_114);
        cb0115= (CheckBox) findViewById(R.id.cb_115);
        cb0200= (CheckBox) findViewById(R.id.cb_200);
        cb0201= (CheckBox) findViewById(R.id.cb_201);
        cb0202= (CheckBox) findViewById(R.id.cb_202);
        cb0203= (CheckBox) findViewById(R.id.cb_203);
        cb0204= (CheckBox) findViewById(R.id.cb_204);
        cb0205= (CheckBox) findViewById(R.id.cb_205);
        cb0206= (CheckBox) findViewById(R.id.cb_206);
        cb0207= (CheckBox) findViewById(R.id.cb_207);
        cb0208= (CheckBox) findViewById(R.id.cb_208);
        cb0209= (CheckBox) findViewById(R.id.cb_209);
        cb0210= (CheckBox) findViewById(R.id.cb_210);
        cb0211= (CheckBox) findViewById(R.id.cb_211);
        cb0212= (CheckBox) findViewById(R.id.cb_212);
        cb0213= (CheckBox) findViewById(R.id.cb_213);
        cb0214= (CheckBox) findViewById(R.id.cb_214);
        cb0215= (CheckBox) findViewById(R.id.cb_215);
        cb0300= (CheckBox) findViewById(R.id.cb_300);
        cb0301= (CheckBox) findViewById(R.id.cb_301);
        cb0302= (CheckBox) findViewById(R.id.cb_302);
        cb0303= (CheckBox) findViewById(R.id.cb_303);
        cb0304= (CheckBox) findViewById(R.id.cb_304);
        cb0305= (CheckBox) findViewById(R.id.cb_305);
        cb0306= (CheckBox) findViewById(R.id.cb_306);
        cb0307= (CheckBox) findViewById(R.id.cb_307);
        cb0308= (CheckBox) findViewById(R.id.cb_308);
        cb0309= (CheckBox) findViewById(R.id.cb_309);
        cb0310= (CheckBox) findViewById(R.id.cb_310);
        cb0311= (CheckBox) findViewById(R.id.cb_311);
        cb0312= (CheckBox) findViewById(R.id.cb_312);
        cb0313= (CheckBox) findViewById(R.id.cb_313);
        cb0314= (CheckBox) findViewById(R.id.cb_314);
        cb0315= (CheckBox) findViewById(R.id.cb_315);
        cb0400= (CheckBox) findViewById(R.id.cb_400);
        cb0401= (CheckBox) findViewById(R.id.cb_401);
        cb0402= (CheckBox) findViewById(R.id.cb_402);
        cb0403= (CheckBox) findViewById(R.id.cb_403);
        cb0404= (CheckBox) findViewById(R.id.cb_404);
        cb0405= (CheckBox) findViewById(R.id.cb_405);
        cb0406= (CheckBox) findViewById(R.id.cb_406);
        cb0407= (CheckBox) findViewById(R.id.cb_407);
        cb0408= (CheckBox) findViewById(R.id.cb_408);
        cb0409= (CheckBox) findViewById(R.id.cb_409);
        cb0410= (CheckBox) findViewById(R.id.cb_410);
        cb0411= (CheckBox) findViewById(R.id.cb_411);
        cb0412= (CheckBox) findViewById(R.id.cb_412);
        cb0413= (CheckBox) findViewById(R.id.cb_413);
        cb0414= (CheckBox) findViewById(R.id.cb_414);
        cb0415= (CheckBox) findViewById(R.id.cb_415);
        btOpen= (Button) findViewById(R.id.rb_open);
        btOpen.setOnClickListener(this);


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
                    if(isChecked)

                    break;
                case R.id.cb_100:
                    if(isChecked)
                          str=str+"0100";
                    strPlus=
                    i++;
                    break;
                case R.id.cb_101:
                    if(isChecked)
                        str=str+"0101";
                    i++;
                    break;
                case R.id.cb_102:
                    if(isChecked)
                        str=str+"0102";
                    i++;
                    break;
                case R.id.cb_103:
                    if(isChecked)
                        str=str+"0103";
                    i++;
                        break;
                case R.id.cb_104:
                    if(isChecked)
                        str=str+"0104";
                    i++;
                        break;
                case R.id.cb_105:
                    if(isChecked)
                        str=str+"0105";
                    i++;
                        break;
                case R.id.cb_106:
                    if(isChecked)

                        break;
                case R.id.cb_107:
                    if(isChecked)

                        break;
                case R.id.cb_108:
                    if(isChecked)

                        break;
                case R.id.cb_109:
                    if(isChecked)

                        break;
                case R.id.cb_110:
                    if(isChecked)

                        break;
                case R.id.cb_111:
                    if(isChecked)

                        break;
                case R.id.cb_112:
                    if(isChecked)

                        break;
                case R.id.cb_113:
                    if(isChecked)

                        break;
                case R.id.cb_114:
                    if(isChecked)

                        break;
                case R.id.cb_115:
                    if(isChecked)

                        break;
                case R.id.cb_200:
                    if(isChecked)

                        break;
                case R.id.cb_201:
                    if(isChecked)

                        break;
                case R.id.cb_202:
                    if(isChecked)

                        break;
                case R.id.cb_203:
                    if(isChecked)

                        break;
                case R.id.cb_204:
                    if(isChecked)

                        break;
                case R.id.cb_205:
                    if(isChecked)

                        break;
                case R.id.cb_206:
                    if(isChecked)

                        break;
                case R.id.cb_207:
                    if(isChecked)

                        break;
                case R.id.cb_208:
                    if(isChecked)

                        break;
                case R.id.cb_209:
                    if(isChecked)

                        break;
                case R.id.cb_210:
                    if(isChecked)

                        break;
                case R.id.cb_211:
                    if(isChecked)

                        break;
                case R.id.cb_212:
                    if(isChecked)

                        break;
                case R.id.cb_213:
                    if(isChecked)

                        break;
                case R.id.cb_214:
                    if(isChecked)

                        break;
                case R.id.cb_215:
                    if(isChecked)

                        break;
                case R.id.cb_300:
                    if(isChecked)

                        break;
                case R.id.cb_301:
                    if(isChecked)

                        break;
                case R.id.cb_302:
                    if(isChecked)

                        break;
                case R.id.cb_303:
                    if(isChecked)

                        break;
                case R.id.cb_304:
                    if(isChecked)

                        break;
                case R.id.cb_305:
                    if(isChecked)

                        break;
                case R.id.cb_306:
                    if(isChecked)

                        break;
                case R.id.cb_307:
                    if(isChecked)

                        break;
                case R.id.cb_308:
                    if(isChecked)

                        break;
                case R.id.cb_309:
                    if(isChecked)

                        break;
                case R.id.cb_310:
                    if(isChecked)

                        break;
                case R.id.cb_311:
                    if(isChecked)

                        break;
                case R.id.cb_312:
                    if(isChecked)

                        break;
                case R.id.cb_313:
                    if(isChecked)

                        break;
                case R.id.cb_314:
                    if(isChecked)

                        break;
                case R.id.cb_315:
                    if(isChecked)

                        break;
                case R.id.cb_400:
                    if(isChecked)

                        break;
                case R.id.cb_401:
                    if(isChecked)

                        break;
                case R.id.cb_402:
                    if(isChecked)

                        break;
                case R.id.cb_403:
                    if(isChecked)

                        break;
                case R.id.cb_404:
                    if(isChecked)

                        break;
                case R.id.cb_405:
                    if(isChecked)

                        break;
                case R.id.cb_406:
                    if(isChecked)

                        break;
                case R.id.cb_407:
                    if(isChecked)

                        break;
                case R.id.cb_408:
                    if(isChecked)

                        break;
                case R.id.cb_409:
                    if(isChecked)

                        break;
                case R.id.cb_410:
                    if(isChecked)

                        break;
                case R.id.cb_411:
                    if(isChecked)

                        break;
                case R.id.cb_412:
                    if(isChecked)

                        break;
                case R.id.cb_413:
                    if(isChecked)

                        break;
                case R.id.cb_414:
                    if(isChecked)

                        break;
                case R.id.cb_415:
                    if(isChecked)

                        break;


            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb_open:
                if(i<10){
                   strNumber= "0"+String.valueOf(i);
                }else {
                    strNumber=String.valueOf(i);
                }
               order="013101"+strNumber+str+

                break;
        }
    }
}
