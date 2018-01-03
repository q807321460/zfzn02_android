package com.jia.znjj2;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AirCenterMoreActivity extends ElectricBase {
    private TextView tvTitleName;
    private TextView tvTitleEdit;
    private TextView tvTitleSave;

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

    }
}
