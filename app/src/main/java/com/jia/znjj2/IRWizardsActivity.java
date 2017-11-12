package com.jia.znjj2;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jia.ir.AdapterPYinItem;
import com.jia.ir.FragmentWizardsTwo;

public class IRWizardsActivity extends FragmentActivity {
    private int mType;
    private int mRoomSequ;
    private String electricCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_wizards);
        mType = getIntent().getIntExtra("electricType", 0);
        mRoomSequ = getIntent().getIntExtra("roomSequ", 0);
        electricCode = getIntent().getStringExtra("electricCode");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = null;
        fragment = new FragmentWizardsTwo();
        Bundle args = new Bundle();
        args.putInt("type", mType);
        args.putInt("group", mRoomSequ);
        args.putString("electricCode", electricCode);
        fragment.setArguments(args);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
