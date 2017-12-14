package com.jia.ezcamera.set;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;


import com.jia.ezcamera.play.CJpgFileTool;
import com.jia.ezcamera.utils.ProgressDialogUtil;
import com.jia.znjj2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FileListActivity extends Activity {
    // private String[] allFiles;
    // private File[] childFile;
    public ArrayList<myItem> myList;
    public ArrayList<myItem> deleteList;
    public Handler fileList_handler = null;
    CJpgFileTool cft = CJpgFileTool.getInstance();
    OnItemClickListener itemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            // if (childFile[arg2].isDirectory()) {// ��Ŀ¼: true
            Intent i = new Intent();
            i.setClass(FileListActivity.this, PictureListActivity.class);
            
            Bundle b = new Bundle();
            b.putString("fileName", myList.get(arg2).fileName);
            i.putExtras(b);
            FileListActivity.this.startActivity(i);
            // }
        }
    };
    OnClickListener BtnBackClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            FileListActivity.this.finish();
        }
    };
    private Button view_pic_btn_return;
    private GridView pic_gridview;
    private myImageAdapter adapter;
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
                    deleteFloder();
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
    private Context myContext = null;

    // private myImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
        myContext = this;
        init();
    }

    private void init() {
        view_pic_btn_return = (Button) findViewById(R.id.view_pic_btn_return);
        view_pic_btn_return.setOnClickListener(BtnBackClick);
        pic_gridview = (GridView) findViewById(R.id.pic_gridview);

        // if (cft.IsCanUseSdCard()) {
        // File folder = new File(cft.getPath());// "/sdcard/Photo/"
        // Log.i("info", "fileActivity   path=" + cft.getPath());
        // childFile = folder.listFiles();
        // allFiles = folder.list();
        // if (allFiles.length < 1) {
        // return;
        // }
        //
        // ArrayList<HashMap<String, Object>> lstImageItem = new
        // ArrayList<HashMap<String, Object>>();
        // for (int i = 0; i < allFiles.length; i++) {
        // HashMap<String, Object> map = new HashMap<String, Object>();
        // if (childFile[i].isFile()) {
        // // map.put("ItemImage", R.drawable.png_file_orange);
        // } else {
        // map.put("ItemImage", R.drawable.png_file_blue);// ���ͼ����Դ��ID
        // map.put("ItemText", allFiles[i]);// �������ItemText
        // lstImageItem.add(map);
        // }
        // }
        // SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,//
        // ������Դ
        // R.layout.item_file_picture, new String[] { "ItemImage",
        // "ItemText" }, new int[] { R.id.item_fl_img,
        // R.id.item_fl_textview });
        // // ��Ӳ�����ʾ
        // pic_gridview.setAdapter(saImageItems);
        //
        // pic_gridview.setOnItemClickListener(itemClick);
        // }

        pic_delete = (Button) findViewById(R.id.pic_delete);
        pic_ok = (Button) findViewById(R.id.pic_ok);
        pic_ok.setOnClickListener(BtnClick);
        pic_delete.setOnClickListener(BtnClick);
        // pic_cancle = (Button) findViewById(R.id.pic_cancle);
        // pic_cancle.setOnClickListener(BtnClick);
        deleteToVisible();
        myList = new ArrayList<myItem>();
        deleteList = new ArrayList<myItem>();
        adapter = new myImageAdapter(this);
        pic_gridview.setAdapter(adapter);
        pic_gridview.setOnItemClickListener(itemClick);
        fileList_handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                    	ProgressDialogUtil.getInstance()
                                .cancleDialog();
                        if(myList!=null){
                            Comparator<myItem> comp = new SortComparator();
                            Collections.sort(myList,comp);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            ;
        };

        if (cft.IsCanUseSdCard()) {
        	ProgressDialogUtil.getInstance().showDialog(this,
                    FileListActivity.this.getResources().getString(
                            R.string.doing_get_picture_folder_list));
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    readFile();
                    fileList_handler.sendEmptyMessage(1);
                }
            }).start();
        } else {
            Toast.makeText(this,
                    this.getResources().getString(R.string.sdcard_not_use),
                    Toast.LENGTH_SHORT).show();
        }
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

    private void deleteFloder() {
        if (deleteList != null && deleteList.size() > 0) {
            for (int i = 0; i < deleteList.size(); i++) {
                myItem mi = deleteList.get(i);
                File myFlie = new File(cft.getCatchPicturePath() + mi.fileName);
                if (myFlie.isDirectory()) {
                    File files[] = myFlie.listFiles(); // ����Ŀ¼�����е��ļ� files[];
                    for (int j = 0; j < files.length; j++) { // ����Ŀ¼�����е��ļ�
                        deleteFile(files[j]); // ��ÿ���ļ� ������������е���
                    }
                    if (myFlie.delete()) {
                        myList.remove(mi);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void deleteFile(File file) {
        String where = "";
        String path = file.getAbsolutePath().replaceFirst(".*/?sdcard",
                "/mnt/sdcard");
        // where = MediaStore.Images.Media.DATA + " LIKE '" + path + "%'";
        where = MediaStore.Images.Media.DATA + "='" + path + "'";
        getContentResolver().delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, null);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    private boolean readFile() {
        File folder = new File(cft.getCatchPicturePath());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (folder.listFiles().length < 1) {
            return false;
        }
        if (myList != null) {
            myList.clear();
        }
        for (int i = 0; i < folder.list().length; i++) {
            if (folder.listFiles()[i].isDirectory()) {
                File f = folder.listFiles()[i];
                myItem mi = new myItem();
                mi.fileState = 0;
                mi.fileName = f.getName();
                mi.lastModified = f.lastModified();
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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
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
            vh.item_fl_textview.setText(myitem.fileName);
            vh.the_imageView.setImageResource(R.drawable.png_file_blue);
            convertView.setTag(vh);
            // } else {
            // vh = (ViewHolder) convertView.getTag();
            // }
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
            vh.the_imageView.setAdjustViewBounds(true);
            return convertView;
        }
    }

    private class ViewHolder {
        public ImageView the_imageView;
        public CheckBox item_fl_cb;
        public TextView item_fl_textview;
    }

    private class myItem {
        public String fileName;
        public int fileState;// 0:����ѡ 1��ѡ��
        long lastModified;
    }



    class SortComparator implements Comparator<myItem> {
        @Override
        public int compare(myItem lhs, myItem rhs) {
            myItem a = lhs;
            myItem b = rhs;

            if(a.fileName.equals("event")){
                return -1;
            }

            long diff = a.lastModified - b.lastModified;
            if (diff > 0)
                return -1;
            else if (diff == 0)
                return 0;
            else
                return 1;

        }

    }
}
