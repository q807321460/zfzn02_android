package com.jia.ezcamera.set;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jia.ezcamera.play.CJpgFileTool;
import com.jia.ezcamera.play.PlayLocalVideoActivity;
import com.jia.ezcamera.play.StaticConstant;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.znjj2.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class PictureListActivity extends Activity implements
        MediaScannerConnectionClient {
    private static final String TAG = "PictureListActivity";
    private static final String FILE_TYPE = "image/*";
    private static String SCAN_PATH;
    public String[] allFiles;
    public ArrayList<myItem> myList;
    public ArrayList<myItem> deleteList;
    public Handler pictureListHandler = null;
    CJpgFileTool cft = CJpgFileTool.getInstance();
    DisplayMetrics dm = new DisplayMetrics();
    int mScreenW = 0; // �õ����
    int mScreenH = 0; // �õ��߶�
    int mPicWidth = 0;
    int mPicHeight = 0;
    OnClickListener BtnBackClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            PictureListActivity.this.finish();
        }
    };
    private Context myContext = null;
    private Button view_pic_btn_return;
    private GridView pic_gridview;
    private myImageAdapter adapter;
    private TextView view_tv_name;
    private String fileName = null;
    OnItemClickListener itemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
        	if(myList.get(arg2).fileName.endsWith("_jpg.jpg")){
	            SCAN_PATH = cft.getCatchPicturePath() + fileName + "/"
	                    + myList.get(arg2).fileName;
	            Log.i(TAG, "SCAN_PATH=" + SCAN_PATH);

                jumpToPicActivity(SCAN_PATH);
	            // startScan();
//	            File file = new File(SCAN_PATH);
//	            Intent it = new Intent(Intent.ACTION_VIEW);
//	            Uri mUri = Uri.parse("file://" + file.getPath());
//	            it.setDataAndType(mUri, FILE_TYPE);
//	            startActivity(it);
        	}else if(myList.get(arg2).fileName.endsWith("_video.jpg")){
        		SCAN_PATH = cft.getCatchPicturePath() + fileName + "/"
	                    + myList.get(arg2).fileName;
        		String playpath = SCAN_PATH.replace(".jpg", ".vveye");
        		Log.e("DEBUG", playpath);
        		jumpToPlayActivity(playpath);
        	}
        }
    };
    private Button pic_delete;
    // private Button pic_cancle;
    private Button pic_ok;
    private boolean m_state = false;
    OnClickListener BtnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.pic_delete:
                    if (m_state) {
                        deleteToVisible();
                        setState(0);
                    } else {
                        deleteToGone();
                        setState(1);
                    }
                    break;
                case R.id.pic_ok:
                    deletePng();
                    deleteToVisible();
                    setState(0);
                    break;
                // case R.id.pic_cancle:
                // deleteToVisible();
                // setState(0);
                // break;
                default:
                    break;
            }
        }
    };
    private MediaScannerConnection conn;

    public static Bitmap loadResBitmap(String path, int scalSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = scalSize;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
        myContext = this;
        Intent i = getIntent();
        if (i != null) {
            Bundle b = i.getExtras();
            if (b != null) {
                fileName = b.getString("fileName");
            }
        }
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenW = dm.widthPixels; // �õ����
        mScreenH = dm.heightPixels; // �õ��߶�
        if (mScreenW > 0) {
            mPicWidth = mScreenW / 3;
            mPicHeight = mPicWidth * 3 / 4;
        }
        Log.i(TAG, "mScreenW=" + mScreenW + "     mScreenH=" + mScreenH);
        init();
    }

    private void init() {
        view_pic_btn_return = (Button) findViewById(R.id.view_pic_btn_return);
        view_pic_btn_return.setOnClickListener(BtnBackClick);
        pic_gridview = (GridView) findViewById(R.id.pic_gridview);
        view_tv_name = (TextView) findViewById(R.id.view_tv_name);

        pic_delete = (Button) findViewById(R.id.pic_delete);
        pic_delete.setOnClickListener(BtnClick);
        pic_ok = (Button) findViewById(R.id.pic_ok);
        pic_ok.setOnClickListener(BtnClick);
        // pic_cancle = (Button) findViewById(R.id.pic_cancle);
        // pic_cancle.setOnClickListener(BtnClick);
        deleteToVisible();

        if (fileName == null || "".equals(fileName.trim())) {
            pic_delete.setVisibility(View.GONE);
            return;
        }
        view_tv_name.setText(fileName);
        deleteList = new ArrayList<myItem>();
        myList = new ArrayList<myItem>();
        adapter = new myImageAdapter(this);
        pic_gridview.setAdapter(adapter);
        pic_gridview.setOnItemClickListener(itemClick);

        pictureListHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                    	ProgressDialogUtil.getInstance()
                                .cancleDialog();
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        ProgressDialogUtil.getInstance().showDialog(myContext,
                PictureListActivity.this.getResources().getString(
                        R.string.doing_get_picture_folder_list));

    }

    private void deleteToVisible() {
        m_state = false;
        // pic_cancle.setVisibility(View.GONE);
        pic_ok.setVisibility(View.GONE);
        pic_delete.setBackgroundResource(R.drawable.png_delete);
    }

    private void deleteToGone() {
        m_state = true;
        // pic_cancle.setVisibility(View.VISIBLE);
        pic_ok.setVisibility(View.VISIBLE);
        pic_delete.setBackgroundResource(R.drawable.png_delete_red);
    }

    private void deletePng() {
        if (deleteList != null && deleteList.size() > 0) {
            for (int i = 0; i < deleteList.size(); i++) {
                myItem mi = deleteList.get(i);
               
                // Log.i("info", "delete   myFlie=" + cft.getPath() + fileName
                // + "/" + mi.fileName);
                myList.remove(mi);
                if(mi.isVideo){
                	File myFlie = new File(cft.getCatchPicturePath() + fileName
                             + "/" + mi.fileName.replace(".jpg", ".vveye"));
                	deleteFile(myFlie, cft.getCatchPicturePath() + fileName + "/"
                            + mi.fileName.replace(".jpg", ".vveye"));
                }
                File myFlie = new File(cft.getCatchPicturePath() + fileName
                        + "/" + mi.fileName);
                deleteFile(myFlie, cft.getCatchPicturePath() + fileName + "/"
                        + mi.fileName);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void deleteFile(File file, String filePath) {
        if (file.exists() && file.isFile()) {
            String where = "";
            Log.i("info", "path1=" + file.getAbsolutePath());
            String path = file.getAbsolutePath().replaceFirst(".*/?sdcard",
                    "/mnt/sdcard");
            Log.i("info", "path1=" + path);
            // where = MediaStore.Images.Media.DATA + " LIKE '" + path + "%'";
            where = MediaStore.Images.Media.DATA + "='" + path + "'";
            getContentResolver().delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, null);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
    }

    private boolean readFile() {
        File folder = new File(cft.getCatchPicturePath() + fileName + "/");// "/sdcard/Photo/"
        allFiles = folder.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				String name = filename;  
                if(name.endsWith(".jpg") || name.endsWith(".jpeg"))  
                    return true;  
                else  
                    return false;  
			}
		});
        if (folder.list().length < 1) {
            return false;
        }
        if(allFiles.length<1){
        	return false;
        }
        if (myList != null) {
            myList.clear();
        }
        SCAN_PATH = cft.getCatchPicturePath() + fileName + "/" + allFiles[0];
        for (int i = 0; i < allFiles.length; i++) {
            myItem mi = new myItem();
            mi.fileState = 0;
            mi.fileName = allFiles[i];
            if(mi.fileName.endsWith("_video.jpg")){
            	mi.isVideo = true;
            }else{
            	mi.isVideo = false;
            }
            if (!myList.contains(mi)) {
                myList.add(mi);
            }
        }
        return true;
    }

    private void setState(int i) {
        if (deleteList != null) {
            deleteList.clear();
        }
        if (myList.size() > 0) {
            for (int j = 0; j < myList.size(); j++) {
                myList.get(j).fileState = i;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                readFile();
                pictureListHandler.sendEmptyMessage(1);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void startScan() {
        if (conn != null) {
            conn.disconnect();
        }
        conn = new MediaScannerConnection(this, this);

        conn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        // TODO Auto-generated method stub
        conn.scanFile(SCAN_PATH, FILE_TYPE);
    }

    ;

    @Override
    public void onScanCompleted(String path, Uri uri) {
        // TODO Auto-generated method stub
        try {
            if (uri != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                // Log.i(TAG, "uri=" + uri);
                startActivity(intent);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            conn.disconnect();
            conn = null;
        }
    }

    private class myImageAdapter extends BaseAdapter {

        // allFiles/*GridView��������ͼƬ��ImageView*/

        private Context myContext;

        /* ���췽�� */
        public myImageAdapter(Context myContext) {
            // TODO Auto-generated constructor stub
            this.myContext = myContext;
            /* ����һ��Context�������д������GridViewTest */
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return myList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder vh = null;
            final myItem myitem = myList.get(position);
            // if (convertView == null) {
            convertView = LayoutInflater.from(myContext).inflate(
                    R.layout.item_file_picture, null);
            vh = new ViewHolder();
            vh.the_imageView = (ImageView) convertView
                    .findViewById(R.id.item_fl_img);
            vh.item_fl_textview = (TextView) convertView
                    .findViewById(R.id.item_fl_textview);
            vh.item_fl_cb = (CheckBox) convertView
                    .findViewById(R.id.item_fl_cb);
            vh.item_fl_textview.setVisibility(View.GONE);
            // vh.the_imageView.setImageURI(Uri.fromFile(new File(cft.getPath()
            // + fileName + "/" + myitem.fileName)));
            vh.the_imageView.setImageBitmap(loadResBitmap(
                    cft.getCatchPicturePath() + fileName + "/"
                            + myitem.fileName, 10));
            vh.play_img = (ImageView)convertView.findViewById(R.id.item_play_img);
            convertView.setTag(vh);
            // } else {
            // vh = (ViewHolder) convertView.getTag();
            // }
            if(myitem.isVideo){
            	vh.play_img.setVisibility(View.VISIBLE);
            }
            if (myitem.fileState == 0) {
                vh.item_fl_cb.setVisibility(View.GONE);
            } else if (myitem.fileState == 1) {
                vh.item_fl_cb.setVisibility(View.VISIBLE);
            }
            vh.item_fl_cb
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {
                                deleteList.add(myitem);
                            } else {
                                deleteList.remove(myitem);
                            }
                        }
                    });
            // /* ����һ��ImageView */
            // the_imageView = new ImageView(myContext);
            // // ����ͼ��Դ����ԴID��
            // the_imageView.setImageURI(Uri.fromFile(new File(cft.getPath()
            // + fileName + "/" + allFiles[position])));
			/* ʹImageView��߽���Ӧ */
            // vh.the_imageView.setAdjustViewBounds(true);
            // vh.the_imageView.setScaleType(ScaleType.FIT_XY);
			/* ���ñ���ͼƬ�ķ�� */
            vh.the_imageView
                    .setBackgroundResource(android.R.drawable.picture_frame);
            // /* ���ش��ж��ͼƬID��ImageView */
            vh.the_imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                    mPicWidth, mPicHeight));
            return convertView;
        }
    }

    private class ViewHolder {
        public ImageView the_imageView;
        public ImageView play_img;
        public CheckBox item_fl_cb;
        public TextView item_fl_textview;
    }

    private class myItem {
        public String fileName;
        public int fileState;// 0:����ѡ 1��ѡ��
        public boolean isVideo ; //false ͼƬ  true¼��
    }
    
    private void jumpToPlayActivity(String filePath){
        if (!TextUtils.isEmpty(filePath)) {
            Intent intent = new Intent();
            intent.setClass(myContext, PlayLocalVideoActivity.class);
            intent.putExtra(StaticConstant.FILE_PATH, filePath);
            myContext.startActivity(intent);
        }
    }

    private void jumpToPicActivity(String filePath){
        if (!TextUtils.isEmpty(filePath)) {
            Intent intent = new Intent();
            intent.setClass(myContext, FishEyePicActivity.class);
            intent.putExtra(StaticConstant.FILE_PATH, filePath);
            myContext.startActivity(intent);
        }
    }
}
