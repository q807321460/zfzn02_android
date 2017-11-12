package com.jia.znjj2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.jia.data.DataControl;
import com.jia.util.CreateImage;

import org.w3c.dom.Text;

public class SharedAccountInfo extends Activity {

    private ImageView ivAccountPhoto;
    private TextView tvAccountName;
    private TextView tvAccountCode;
    private TextView tvAccountEmail;
    private Switch switchAdmin;
    private ImageView ivElectrics;

    private DataControl mDC;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_account_info);
        initView();
    }

    private void initView() {
        mDC = DataControl.getInstance();
        position = getIntent().getIntExtra("position", -1);
        ivAccountPhoto = (ImageView) findViewById(R.id.shared_account_info_photo);
        tvAccountName = (TextView) findViewById(R.id.shared_account_info_name);
        tvAccountCode = (TextView) findViewById(R.id.shared_account_info_code);
        tvAccountEmail = (TextView) findViewById(R.id.shared_account_info_email);
        switchAdmin = (Switch) findViewById(R.id.shared_account_info_admin);
        ivElectrics = (ImageView) findViewById(R.id.shared_account_info_electric);

        if (position != -1) {
            Bitmap bitmap = CreateImage.getLoacalBitmap(mDC.sUrlDir + mDC.mSharedAccountList.get(position).getAccountCode() + ".jpg");
            ivAccountPhoto.setImageBitmap(bitmap);
            tvAccountName.setText(mDC.mSharedAccountList.get(position).getAccountName());
            tvAccountCode.setText(mDC.mSharedAccountList.get(position).getAccountCode());
            tvAccountEmail.setText(mDC.mSharedAccountList.get(position).getAccountEmail());
            switchAdmin.setChecked(true);
        }
    }

    public void sharedAccountInfoBack(View view){
        finish();
    }
    public void goToSharedElectrics(View view){
        Intent intent = new Intent(SharedAccountInfo.this, SharedElectric.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
