package com.jia.znjj2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.jia.data.DataControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PhotoTest extends AppCompatActivity {

    private static int   IMAGE_MAX_WIDTH  = 480;
    private static int   IMAGE_MAX_HEIGHT = 960;
    DataControl mDC;
    ImageView imageView;
    Bitmap bitmap = null;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            bitmap = getLoacalBitmap("data/data/com.jia.znjj2/databases/out.jpg");
            imageView.setImageBitmap(bitmap);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_test);

        mDC = DataControl.getInstance();
        mDC.init(this);
        imageView = (ImageView) findViewById(R.id.image_photo);
        new Thread(){
            @Override
            public void run() {
                mDC.mWS.loadAccountFromWsTest("15109833917",null);
                try {
                    File cacheDir = new File("data/data/com.jia.znjj2/databases/");//设置目录参数
                    cacheDir.mkdirs();//新建目录
                    Log.i("copySd2phone","新建data/data目录成功");
                    String filename;
                    //获得文件名的长度
                    filename = "out.jpg";
                    Log.i("createNewFile","filename= "+filename);
                    //文件名
                    File cacheFile = new File(cacheDir,filename);//设置参数
                    cacheFile.createNewFile();//生成文件
                    Log.i("createNewFile","生成文件成功"+cacheFile.getName());
                    OutputStream out = new FileOutputStream("data/data/com.jia.znjj2/databases/out.jpg");
                    out.write(mDC.mAccount.getAccountPhoto());
                    out.flush();
                    out.close();
                }catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }finally {
                    handler.sendMessage(new Message());
                }
            }
        }.start();
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = getImageScale(url);
            return BitmapFactory.decodeFile(url, option);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getImageScale(String imagePath) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        // set inJustDecodeBounds to true, allowing the caller to query the bitmap info without having to allocate the
        // memory for its pixels.
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        int scale = 1;
        while (option.outWidth / scale >= IMAGE_MAX_WIDTH || option.outHeight / scale >= IMAGE_MAX_HEIGHT) {
            scale *= 2;
        }
        return scale;
    }
}
