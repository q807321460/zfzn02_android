package com.jia.znjj2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.data.DataControl;
import com.jia.util.CreateImage;
import com.jia.widget.ActionSheetDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class PersonAccount extends Activity{
    private final int START_ALBUM_REQUESTCODE = 1;
    private final int CAMERA_WITH_DATA = 2;
    private final int CROP_RESULT_CODE = 3;
    private final int PERSON_ACCOUNT_NAME_REQUEST = 4;
    private final int PERSON_ACCOUNT_PHONE_REQUEST = 5;
    private final int PERSON_ACCOUNT_ADDRESS_REQUEST = 6;
    private final int PERSON_ACCOUNT_EMAIL_REQUEST = 7;
    public static final String TMP_PATH = "clip_temp.jpg";

    private DataControl mDC;
    private RelativeLayout rlAccountName;
    private RelativeLayout rlAccountPhone;
    private RelativeLayout rlAccountAddress;
    private RelativeLayout rlAccountEmail;
    private RelativeLayout rlAccountPassword;
    private ImageView ivAccountPhoto;
    private TextView tvAccountSave;
    private TextView tvAccountName;
    private TextView tvAccountPhone;
    private TextView tvAccountAddress;
    private TextView tvAccountEmail;


    private Intent intent ;
    private ActionSheetDialogBuilder builder;



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x1051:
                    Toast.makeText(PersonAccount.this, "保存成功", Toast.LENGTH_LONG).show();
                    break;
                case 0x1052:
                    Toast.makeText(PersonAccount.this, "保存失败，请检查网络", Toast.LENGTH_LONG).show();
                    break;
                case 0x1053:
                    Toast.makeText(PersonAccount.this, "保存失败，请稍候再试", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_account);
        if(savedInstanceState != null){
            initView1(savedInstanceState);

        }else {
            initView();
        }


        addListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void initView1(Bundle savedInstanceState){
        mDC = DataControl.getInstance();
        intent = new Intent(PersonAccount.this, PersonAccountEdit.class);
        rlAccountName = (RelativeLayout) findViewById(R.id.person_account_name_rl);
        rlAccountPhone = (RelativeLayout) findViewById(R.id.person_account_phone_rl);
        rlAccountAddress = (RelativeLayout) findViewById(R.id.person_account_address_rl);
        rlAccountEmail = (RelativeLayout) findViewById(R.id.person_account_email_rl);
        rlAccountPassword = (RelativeLayout) findViewById(R.id.person_account_password_rl);
        ivAccountPhoto = (ImageView) findViewById(R.id.person_account_head_iv);
        tvAccountSave = (TextView) findViewById(R.id.person_account_title_save);
        tvAccountName = (TextView) findViewById(R.id.person_account_name_tv2);
        tvAccountPhone = (TextView) findViewById(R.id.person_account_phone_tv2);
        tvAccountAddress = (TextView) findViewById(R.id.person_account_address_tv2);
        tvAccountEmail = (TextView) findViewById(R.id.person_account_email_tv2);

        Bitmap bitmap = CreateImage.getLoacalBitmap(mDC.sUrlDir + mDC.sAccountCode+".jpg");
        ivAccountPhoto.setImageBitmap(bitmap);
        tvAccountName.setText(savedInstanceState.getString("account_name"));
        tvAccountPhone.setText(savedInstanceState.getString("account_phone"));
        tvAccountAddress.setText(savedInstanceState.getString("account_address"));
        tvAccountEmail.setText(savedInstanceState.getString("account_email"));
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("account_name",tvAccountName.getText().toString());
        savedInstanceState.putString("account_phone",tvAccountPhone.getText().toString());
        savedInstanceState.putString("account_email",tvAccountEmail.getText().toString());
        savedInstanceState.putString("account_address",tvAccountAddress.getText().toString());
        super.onSaveInstanceState(savedInstanceState); //实现父类方法 放在最后 防止拍照后无法返回当前activity

    }
    private void initView(){
        mDC = DataControl.getInstance();
        rlAccountName = (RelativeLayout) findViewById(R.id.person_account_name_rl);
        rlAccountPhone = (RelativeLayout) findViewById(R.id.person_account_phone_rl);
        rlAccountAddress = (RelativeLayout) findViewById(R.id.person_account_address_rl);
        rlAccountEmail = (RelativeLayout) findViewById(R.id.person_account_email_rl);
        rlAccountPassword = (RelativeLayout) findViewById(R.id.person_account_password_rl);
        ivAccountPhoto = (ImageView) findViewById(R.id.person_account_head_iv);
        tvAccountSave = (TextView) findViewById(R.id.person_account_title_save);
        tvAccountName = (TextView) findViewById(R.id.person_account_name_tv2);
        tvAccountPhone = (TextView) findViewById(R.id.person_account_phone_tv2);
        tvAccountAddress = (TextView) findViewById(R.id.person_account_address_tv2);
        tvAccountEmail = (TextView) findViewById(R.id.person_account_email_tv2);

        Bitmap bitmap = CreateImage.getLoacalBitmap(mDC.sUrlDir + mDC.sAccountCode+".jpg");
        ivAccountPhoto.setImageBitmap(bitmap);
        tvAccountName.setText(mDC.mAccount.getAccountName());
        tvAccountPhone.setText(mDC.mAccount.getAccountPhone());
        tvAccountAddress.setText(mDC.mAccount.getAccountAddress());
        tvAccountEmail.setText(mDC.mAccount.getAccountEmail());
    }
    private void addListener(){
        ivAccountPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder == null) {
                    builder = new ActionSheetDialogBuilder(PersonAccount.this);
                    builder.setTitleMessage(R.string.photo_hint);
                    builder.setButtons(R.string.take_photo, R.string.photo,
                            R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case ActionSheetDialogBuilder.BUTTON1:
                                            startCapture();
                                            break;
                                        case ActionSheetDialogBuilder.BUTTON2:
                                            startAlbum();
                                            break;
                                    }
                                }
                            });
                }

                builder.create().show();
            }
        });
        tvAccountSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personAccountSave();
            }
        });
        rlAccountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonAccount.this, PersonAccountEdit.class);
                intent.putExtra("paramKey", "姓名");
                intent.putExtra("paramValue", tvAccountName.getText().toString());
                startActivityForResult(intent,PERSON_ACCOUNT_NAME_REQUEST);
            }
        });
        rlAccountPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonAccount.this, PersonAccountEdit.class);
                intent.putExtra("paramKey", "手机");
                intent.putExtra("paramValue", tvAccountPhone.getText().toString());
                startActivityForResult(intent,PERSON_ACCOUNT_PHONE_REQUEST);
            }
        });
        rlAccountAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonAccount.this, PersonAccountEdit.class);
                intent.putExtra("paramKey", "地址");
                intent.putExtra("paramValue", tvAccountAddress.getText().toString());
                startActivityForResult(intent,PERSON_ACCOUNT_ADDRESS_REQUEST);
            }
        });
        rlAccountEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonAccount.this, PersonAccountEdit.class);
                intent.putExtra("paramKey", "邮箱");
                intent.putExtra("paramValue", tvAccountEmail.getText().toString());
                startActivityForResult(intent,PERSON_ACCOUNT_EMAIL_REQUEST);
            }
        });
        rlAccountPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonAccount.this, ChangPassword.class);
                startActivity(intent);
            }
        });
    }

    public void updatePhotoToWeb(String path){
        new MyAsynTask().execute(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = "";
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_RESULT_CODE:
                String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
                Bitmap photo = BitmapFactory.decodeFile(path);
                byte[] bytes = Bitmap2Bytes(photo);
                CreateImage.createImage(mDC.sAccountCode,bytes,mDC.sAccountCode+".jpg");
                ivAccountPhoto.setImageBitmap(photo);
                updatePhotoToWeb(path);
                break;
            case START_ALBUM_REQUESTCODE:
                startCropImageActivity(getFilePath(data.getData()));
                break;
            case CAMERA_WITH_DATA:
                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                startCropImageActivity(Environment.getExternalStorageDirectory()
                        + "/" + TMP_PATH);
                break;
            case PERSON_ACCOUNT_NAME_REQUEST:
                result = data.getExtras().getString("result");
                tvAccountName.setText(result);
                break;
            case PERSON_ACCOUNT_PHONE_REQUEST:
                result = data.getExtras().getString("result");
                tvAccountPhone.setText(result);
                break;
            case PERSON_ACCOUNT_ADDRESS_REQUEST:
                result = data.getExtras().getString("result");
                tvAccountAddress.setText(result);
                break;
            case PERSON_ACCOUNT_EMAIL_REQUEST:
                result = data.getExtras().getString("result");
                tvAccountEmail.setText(result);
                break;

        }


    }

    public void personAccountBack(View view){
        finish();
    }

    public void personAccountSave(){
        new Thread(){
            @Override
            public void run() {
                String retsult = mDC.mWS.updateAccount(mDC.sAccountCode, tvAccountName.getText().toString(),
                        tvAccountPhone.getText().toString(),
                        tvAccountAddress.getText().toString(), tvAccountEmail.getText().toString());
                Message msg = new Message();
                if(retsult.startsWith("1")){
                    msg.what = 0x1051;
                    mDC.mAccount.setAccountName(tvAccountName.getText().toString());
                    mDC.mAccount.setAccountEmail(tvAccountEmail.getText().toString());
                    mDC.mAccount.setAccountAddress(tvAccountAddress.getText().toString());
                    mDC.mAccount.setAccountPhone(tvAccountPhone.getText().toString());
                }else if(retsult.startsWith("-1")){
                    msg.what = 0x1052;
                }else {
                    msg.what = 0x1053;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }


    // 裁剪图片的Activity
    private void startCropImageActivity(String path) {
        ClipImageActivity.startActivity(this, path, CROP_RESULT_CODE);
    }

    private void startAlbum() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            startActivityForResult(intent, START_ALBUM_REQUESTCODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, START_ALBUM_REQUESTCODE);
            } catch (Exception e2) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    private void startCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(String.valueOf(Environment.getExternalStorageDirectory()));
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir, TMP_PATH);
        Uri uritext = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory(), TMP_PATH));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(dir, TMP_PATH)));
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    /**
     * 通过uri获取文件路径
     *
     * @param mUri
     * @return
     */
    public String getFilePath(Uri mUri) {
        try {
            if (mUri.getScheme().equals("file")) {
                return mUri.getPath();
            } else {
                return getFilePathByUri(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    // 获取文件路径通过url
    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
        Cursor cursor = getContentResolver()
                .query(mUri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    class MyAsynTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            return mDC.mWS.updateAccountPhoto(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if("-1".equals(s)){
                Toast.makeText(PersonAccount.this,"上传图片失败，请重试",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(PersonAccount.this,"上传图片成功",Toast.LENGTH_LONG).show();
            }
        }
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
         return baos.toByteArray();
     }

}
